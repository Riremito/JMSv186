/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.server.quest;

import java.util.HashMap;
import tacos.client.TacosCharacter;
import java.util.Map;
import odin.client.Skill;
import odin.constants.GameConstants;
import odin.client.inventory.MapleInventoryType;
import odin.client.MapleQuestStatus;
import odin.client.SkillFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.ops.OpsMessage;
import tacos.packet.response.builder.PB_Message;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.Randomizer;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.builder.PB_UserEffect;
import tacos.wz.QuestWz;

public class MapleQuestAction {

    private MapleQuestActionType type;
    private QuestWz.QuestActionInfo info;
    private MapleQuest quest;

    public MapleQuestAction(QuestWz.QuestActionInfo info, MapleQuest quest) {
        this.type = info.getType();
        this.info = info;
        this.quest = quest;
    }

    private static boolean canGetItem(QuestWz.QuestActionItemInfo item, TacosCharacter client) {
        int gender = item.getGender();
        if (gender != 2 && gender != client.getGender()) {
            return false;
        }
        if (item.isHasJob()) {
            List<Integer> code = getJobBy5ByteEncoding(item.getJob());
            boolean jobFound = false;
            for (int codec : code) {
                if (codec / 100 == client.getJob() / 100) {
                    jobFound = true;
                    break;
                }
            }
            if (!jobFound) {
                List<Integer> codeEx = getJobBy5ByteEncoding(item.getJobEx());
                for (int codec : codeEx) {
                    if (codec / 100 == client.getJob() / 100) {
                        jobFound = true;
                        break;
                    }
                }
            }
            return jobFound;
        }
        return true;
    }

    // first check for randomness in item selection
    private static int selectRandomItem(List<QuestWz.QuestActionItemInfo> items, TacosCharacter chr) {
        Map<Integer, Integer> props = new HashMap<>();
        for (QuestWz.QuestActionItemInfo item : items) {
            if (item.isHasProp() && item.getProp() != -1 && canGetItem(item, chr)) {
                for (int i = 0; i < item.getProp(); i++) {
                    props.put(props.size(), item.getId());
                }
            }
        }
        int selection = 0;
        if (!props.isEmpty()) {
            selection = props.get(Randomizer.nextInt(props.size()));
        }
        return selection;
    }

    public boolean RestoreLostItem(TacosCharacter client, int item_id) {
        if (type == MapleQuestActionType.item) {
            for (QuestWz.QuestActionItemInfo item : info.getItems()) {
                int retitem = item.getId();
                if (retitem == item_id) {
                    if (!client.haveItem(retitem, 1, true, false)) {
                        MapleInventoryManipulator.addById(client.getClient(), retitem, (short) 1);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // items of this action for the character : job/gender filter, random selection (prop) and the selection of the user (prop == -1).
    private List<QuestWz.QuestActionItemInfo> selectItems(TacosCharacter chr, Integer extSelection) {
        int selection = selectRandomItem(info.getItems(), chr);
        int extNum = 0;
        List<QuestWz.QuestActionItemInfo> ret = new ArrayList<>();
        for (QuestWz.QuestActionItemInfo item : info.getItems()) {
            if (!canGetItem(item, chr)) {
                continue;
            }
            if (item.isHasProp()) {
                if (item.getProp() == -1) {
                    if (extSelection != extNum++) {
                        continue;
                    }
                } else if (item.getId() != selection) {
                    continue;
                }
            }
            ret.add(item);
        }
        return ret;
    }

    private void giveItems(TacosCharacter chr, Integer extSelection) {
        for (QuestWz.QuestActionItemInfo item : selectItems(chr, extSelection)) {
            int id = item.getId();
            short count = (short) item.getCount();
            if (count < 0) { // remove items
                MapleInventoryManipulator.removeById(chr.getClient(), GameConstants.getInventoryType(id), id, (count * -1), true, false);
            } else { // add items
                int period = item.getPeriod() / 1440; //im guessing.
                String name = MapleItemInformationProvider.getInstance().getName(id);
                if (id / 10000 == 114 && name != null && name.length() > 0) { //medal
                    String msg = "You have attained title <" + name + ">";
                    chr.dropMessage(-1, msg);
                    chr.dropMessage(5, msg);
                }
                MapleInventoryManipulator.addById(chr.getClient(), id, count, "", null, period);
            }
            PB_UserEffect pb = PB_UserEffect.builder()
                    .item_id(id)
                    .item_quantity(count)
                    .build();
            chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_Quest, pb));
        }
    }

    private static final MapleInventoryType[] REWARD_INVENTORY_TYPES = {MapleInventoryType.EQUIP, MapleInventoryType.USE, MapleInventoryType.SETUP, MapleInventoryType.ETC, MapleInventoryType.CASH};
    private static final String[] REWARD_INVENTORY_NAMES = {"Equip", "Use", "Setup", "Etc", "Cash"};

    private boolean checkItems(TacosCharacter chr, Integer extSelection) {
        Map<MapleInventoryType, Integer> needed = new HashMap<>();
        for (QuestWz.QuestActionItemInfo item : selectItems(chr, extSelection)) {
            int id = item.getId();
            short count = (short) item.getCount();
            if (count < 0) { // remove items
                if (!chr.haveItem(id, count, false, true)) {
                    chr.dropMessage(1, "You are short of some item to complete quest.");
                    return false;
                }
                continue;
            }
            // add items
            if (MapleItemInformationProvider.getInstance().isPickupRestricted(id) && chr.haveItem(id, 1, true, false)) {
                chr.dropMessage(1, "You have this item already: " + MapleItemInformationProvider.getInstance().getName(id));
                return false;
            }
            needed.merge(GameConstants.getInventoryType(id), 1, Integer::sum);
        }
        for (int i = 0; i < REWARD_INVENTORY_TYPES.length; i++) {
            MapleInventoryType type = REWARD_INVENTORY_TYPES[i];
            if (chr.getInventory(type).getNumFreeSlot() < needed.getOrDefault(type, 0)) {
                chr.dropMessage(1, "Please make space for your " + REWARD_INVENTORY_NAMES[i] + " inventory.");
                return false;
            }
        }
        return true;
    }

    private void updateQuests(TacosCharacter chr) {
        for (SimpleImmutableEntry<Integer, Integer> q : info.getQuests()) {
            chr.updateQuest(new MapleQuestStatus(MapleQuest.getInstance(q.getKey()), (byte) (int) q.getValue()));
        }
    }

    private void changeSkills(TacosCharacter chr) {
        for (QuestWz.QuestActionSkillInfo s : info.getSkills()) {
            Skill skillObject = SkillFactory.getSkill(s.getId());

            for (int applicableJob : s.getJob()) {
                if (skillObject.isBeginnerSkill() || chr.getJob() == applicableJob) {
                    chr.changeSkillLevel(skillObject,
                            (byte) Math.max(s.getSkillLevel(), chr.getSkillLevel(skillObject)),
                            (byte) Math.max(s.getMasterLevel(), chr.getMasterLevel(skillObject)));
                    break;
                }
            }
        }
    }

    private void gainFame(TacosCharacter chr) {
        int fameGain = info.getValue();
        chr.addFame(fameGain);
        chr.sendStatChanged();
        chr.SendPacket(ResCWvsContext.Message(OpsMessage.MS_IncPOPMessage, PB_Message.builder().Inc_Fame(fameGain).build()));
    }

    private void applyBuffItem(TacosCharacter chr) {
        int tobuff = info.getValue();
        if (tobuff == -1) {
            return;
        }
        MapleItemInformationProvider.getInstance().getItemEffect(tobuff).applyTo(chr);
    }

    private static int getFinalJob(TacosCharacter chr, List<Integer> jobs) {
        int finalJob = 0;
        for (int job_val : jobs) {
            if (chr.getJob() >= job_val && job_val > finalJob) {
                finalJob = job_val;
            }
        }
        return finalJob;
    }

    public void runStart(TacosCharacter chr, Integer extSelection) {
        MapleQuestStatus status;
        switch (type) {
            case exp:
                status = chr.getQuest(quest);
                if (status.getForfeited() > 0) {
                    break;
                }
                chr.gainExp(info.getValue() * GameConstants.getExpRate_Quest(chr.getLevel()), true, true, true);
                break;
            case item:
                giveItems(chr, extSelection);
                break;
            case nextQuest:
                status = chr.getQuest(quest);
                if (status.getForfeited() > 0) {
                    break;
                }
                chr.SendPacket(ResCUserLocal.UserQuestResult(quest.getId(), status.getNpc(), info.getValue()));
                break;
            case money:
                status = chr.getQuest(quest);
                if (status.getForfeited() > 0) {
                    break;
                }
                chr.gainMeso(info.getValue(), true, false, true);
                break;
            case quest:
                updateQuests(chr);
                break;
            case skill:
                //TODO needs gain/lost message?
                changeSkills(chr);
                break;
            case pop:
                status = chr.getQuest(quest);
                if (status.getForfeited() > 0) {
                    break;
                }
                gainFame(chr);
                break;
            case buffItemID:
                status = chr.getQuest(quest);
                if (status.getForfeited() > 0) {
                    break;
                }
                applyBuffItem(chr);
                break;
            case infoNumber: {
//		System.out.println("quest : "+MapleDataTool.getInt(data, 0)+"");
//		MapleQuest.getInstance(MapleDataTool.getInt(data, 0)).forceComplete(c, 0);
                break;
            }
            case sp: {
                status = chr.getQuest(quest);
                if (status.getForfeited() > 0) {
                    break;
                }
                for (QuestWz.QuestActionSpInfo sp : info.getSps()) {
                    int sp_val = sp.getSpValue();
                    if (sp.getJob() != null) {
                        int finalJob = getFinalJob(chr, sp.getJob());
                        if (finalJob == 0) {
                            chr.gainSP(sp_val);
                        } else {
                            chr.gainSP(sp_val, GameConstants.getSkillBook(finalJob));
                        }
                    } else {
                        chr.gainSP(sp_val);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    public boolean checkEnd(TacosCharacter chr, Integer extSelection) {
        switch (type) {
            case item: {
                return checkItems(chr, extSelection);
            }
            case money: {
                int meso = info.getValue();
                if (chr.getMeso() + meso < 0) { // Giving, overflow
                    chr.dropMessage(1, "Meso exceed the max amount, 2147483647.");
                    return false;
                }
                if (meso < 0 && chr.getMeso() < Math.abs(meso)) { //remove meso
                    chr.dropMessage(1, "Insufficient meso.");
                    return false;
                }
                return true;
            }
        }
        return true;
    }

    public void runEnd(TacosCharacter chr, Integer extSelection) {
        switch (type) {
            case exp: {
                chr.gainExp(info.getValue() * GameConstants.getExpRate_Quest(chr.getLevel()), true, true, true);
                break;
            }
            case item: {
                giveItems(chr, extSelection);
                break;
            }
            case nextQuest: {
                chr.SendPacket(ResCUserLocal.UserQuestResult(quest.getId(), chr.getQuest(quest).getNpc(), info.getValue()));
                break;
            }
            case money: {
                chr.gainMeso(info.getValue(), true, false, true);
                break;
            }
            case quest: {
                updateQuests(chr);
                break;
            }
            case skill: {
                changeSkills(chr);
                break;
            }
            case pop: {
                gainFame(chr);
                break;
            }
            case buffItemID: {
                applyBuffItem(chr);
                break;
            }
            case infoNumber: {
//		System.out.println("quest : "+MapleDataTool.getInt(data, 0)+"");
//		MapleQuest.getInstance(MapleDataTool.getInt(data, 0)).forceComplete(c, 0);
                break;
            }
            case sp: {
                for (QuestWz.QuestActionSpInfo sp : info.getSps()) {
                    int sp_val = sp.getSpValue();
                    if (sp.getJob() != null) {
                        chr.gainSP(sp_val, GameConstants.getSkillBook(getFinalJob(chr, sp.getJob())));
                    } else {
                        chr.gainSP(sp_val);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    private static List<Integer> getJobBy5ByteEncoding(int encoded) {
        List<Integer> ret = new ArrayList<>();
        if ((encoded & 0x1) != 0) {
            ret.add(0);
        }
        if ((encoded & 0x2) != 0) {
            ret.add(100);
        }
        if ((encoded & 0x4) != 0) {
            ret.add(200);
        }
        if ((encoded & 0x8) != 0) {
            ret.add(300);
        }
        if ((encoded & 0x10) != 0) {
            ret.add(400);
        }
        if ((encoded & 0x20) != 0) {
            ret.add(500);
        }
        if ((encoded & 0x400) != 0) {
            ret.add(1000);
        }
        if ((encoded & 0x800) != 0) {
            ret.add(1100);
        }
        if ((encoded & 0x1000) != 0) {
            ret.add(1200);
        }
        if ((encoded & 0x2000) != 0) {
            ret.add(1300);
        }
        if ((encoded & 0x4000) != 0) {
            ret.add(1400);
        }
        if ((encoded & 0x8000) != 0) {
            ret.add(1500);
        }
        if ((encoded & 0x20000) != 0) {
            ret.add(2001); //im not sure of this one
            ret.add(2200);
        }
        if ((encoded & 0x100000) != 0) {
            ret.add(2000);
            ret.add(2001); //?
        }
        if ((encoded & 0x200000) != 0) {
            ret.add(2100);
        }
        if ((encoded & 0x400000) != 0) {
            ret.add(2001); //?
            ret.add(2200);
        }

        if ((encoded & 0x40000000) != 0) { //i haven't seen any higher than this o.o
            ret.add(3000);
            ret.add(3200);
            ret.add(3300);
            ret.add(3500);
        }
        return ret;
    }

    public enum MapleQuestActionType {

        UNDEFINED(-1),
        exp(0),
        item(1),
        nextQuest(2),
        money(3),
        quest(4),
        skill(5),
        pop(6),
        buffItemID(7),
        infoNumber(8),
        yes(9),
        no(10),
        sp(11);
        final byte type;

        private MapleQuestActionType(int type) {
            this.type = (byte) type;
        }

        public static MapleQuestActionType getByWZName(String name) {
            try {
                return valueOf(name);
            } catch (IllegalArgumentException ex) {
                return UNDEFINED;
            }
        }
    }
}
