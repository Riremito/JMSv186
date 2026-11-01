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
import tacos.packet.ops.OpsQuest;
import tacos.wz.WzXML;
import tacos.client.TacosCharacter;
import java.util.Map;
import odin.client.Skill;
import odin.constants.GameConstants;
import odin.client.inventory.MapleInventoryType;
import odin.client.MapleQuestStatus;
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
import tacos.wz.WzNameStorage;

public class MapleQuestAction {

    private final MapleQuestActionType type;
    private final QuestWz.QuestActionInfo info;
    private final MapleQuest quest;

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
            return hasJob(item.getJob(), client.getJob()) || hasJob(item.getJobEx(), client.getJob());
        }
        return true;
    }

    private static boolean hasJob(int encoded, int job) {
        for (int code : getJobBy5ByteEncoding(encoded)) {
            if (code / 100 == job / 100) {
                return true;
            }
        }
        return false;
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
                        MapleInventoryManipulator.addById(client, retitem, (short) 1);
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
            int item_id = item.getId();
            short count = (short) item.getCount();
            if (count < 0) { // remove items
                MapleInventoryManipulator.removeById(chr, GameConstants.getInventoryType(item_id), item_id, (count * -1), true, false);
            } else { // add items
                int period = item.getPeriod() / 1440; //im guessing.
                String name = WzNameStorage.ITEM.get(item_id).getName();
                if (item_id / 10000 == 114 && name != null && name.length() > 0) { //medal
                    String msg = "You have attained title <" + name + ">";
                    chr.dropMessage(-1, msg);
                    chr.Notice(msg);
                }
                MapleInventoryManipulator.addById(chr, item_id, count, "", null, period);
            }
            PB_UserEffect pb = PB_UserEffect.builder()
                    .item_id(item_id)
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
            int item_id = item.getId();
            short count = (short) item.getCount();
            if (count < 0) { // remove items
                if (!chr.haveItem(item_id, count, false, true)) {
                    chr.Alert("You are short of some item to complete quest.");
                    return false;
                }
                continue;
            }
            // add items
            if (MapleItemInformationProvider.getInstance().isPickupRestricted(item_id) && chr.haveItem(item_id, 1, true, false)) {
                chr.Alert("You have this item already: " + WzNameStorage.ITEM.get(item_id).getName());
                return false;
            }
            needed.merge(GameConstants.getInventoryType(item_id), 1, Integer::sum);
        }
        for (int i = 0; i < REWARD_INVENTORY_TYPES.length; i++) {
            MapleInventoryType type = REWARD_INVENTORY_TYPES[i];
            if (chr.getInventory(type).getNumFreeSlot() < needed.getOrDefault(type, 0)) {
                chr.Alert("Please make space for your " + REWARD_INVENTORY_NAMES[i] + " inventory.");
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
            Skill skillObject = WzXML.SKILL.getSkill().get(s.getId());

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

    // rewards that are not given again after the quest was forfeited
    private boolean isForfeitedReward(TacosCharacter chr) {
        switch (type) {
            case exp:
            case nextQuest:
            case money:
            case pop:
            case buffItemID:
            case sp: {
                return chr.getQuest(quest).getForfeited() > 0;
            }
            default: {
                return false;
            }
        }
    }

    private void gainSP(TacosCharacter chr) {
        for (QuestWz.QuestActionSpInfo sp : info.getSps()) {
            int sp_val = sp.getSpValue();
            int finalJob = (sp.getJob() != null) ? getFinalJob(chr, sp.getJob()) : 0;
            if (finalJob == 0) {
                chr.gainSP(sp_val);
            } else {
                chr.gainSP(sp_val, GameConstants.getSkillBook(finalJob));
            }
        }
    }

    private void run(TacosCharacter chr, Integer extSelection) {
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
                chr.SendPacket(ResCUserLocal.UserQuestResult(OpsQuest.QuestRes_Act_Success, quest.getId(), chr.getQuest(quest).getNpc(), info.getValue()));
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
            case sp: {
                gainSP(chr);
                break;
            }
            default: {
                break;
            }
        }
    }

    public void runStart(TacosCharacter chr, Integer extSelection) {
        if (isForfeitedReward(chr)) {
            return;
        }
        run(chr, extSelection);
    }

    public void runEnd(TacosCharacter chr, Integer extSelection) {
        run(chr, extSelection);
    }

    public boolean checkEnd(TacosCharacter chr, Integer extSelection) {
        switch (type) {
            case item: {
                return checkItems(chr, extSelection);
            }
            case money: {
                int meso = info.getValue();
                if (chr.getMeso() + meso < 0) { // Giving, overflow
                    chr.Alert("Meso exceed the max amount, 2147483647.");
                    return false;
                }
                if (meso < 0 && chr.getMeso() < Math.abs(meso)) { //remove meso
                    chr.Alert("Insufficient meso.");
                    return false;
                }
                return true;
            }
            default: {
                return true;
            }
        }
    }

    // job bit -> job ids
    private static final int[][] JOB_BITS = {
        {0x1, 0},
        {0x2, 100},
        {0x4, 200},
        {0x8, 300},
        {0x10, 400},
        {0x20, 500},
        {0x400, 1000},
        {0x800, 1100},
        {0x1000, 1200},
        {0x2000, 1300},
        {0x4000, 1400},
        {0x8000, 1500},
        {0x20000, 2001, 2200}, // im not sure of this one
        {0x100000, 2000, 2001},
        {0x200000, 2100},
        {0x400000, 2001, 2200},
        {0x40000000, 3000, 3200, 3300, 3500}, // i haven't seen any higher than this o.o
    };

    private static List<Integer> getJobBy5ByteEncoding(int encoded) {
        List<Integer> ret = new ArrayList<>();
        for (int[] bit : JOB_BITS) {
            if ((encoded & bit[0]) != 0) {
                for (int i = 1; i < bit.length; i++) {
                    ret.add(bit[i]);
                }
            }
        }
        return ret;
    }

    public enum MapleQuestActionType {
        UNDEFINED,
        exp,
        item,
        nextQuest,
        money,
        quest,
        skill,
        pop,
        buffItemID,
        infoNumber,
        yes,
        no,
        sp;

        public static MapleQuestActionType getByWZName(String name) {
            try {
                return valueOf(name);
            } catch (IllegalArgumentException ex) {
                return UNDEFINED;
            }
        }
    }
}
