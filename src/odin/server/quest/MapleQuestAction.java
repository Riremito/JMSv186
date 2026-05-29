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
import java.util.Map;
import java.io.Serializable;

import odin.client.ISkill;
import odin.constants.GameConstants;
import odin.client.inventory.InventoryException;
import odin.client.MapleCharacter;
import odin.client.inventory.MapleInventoryType;
import odin.client.MapleQuestStatus;
import odin.client.SkillFactory;
import java.util.ArrayList;
import java.util.List;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.packet.response.wrapper.WrapCUserLocal;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.Randomizer;
import odin.provider.IMapleData;
import tacos.wz.WzDataTool;

public class MapleQuestAction implements Serializable {

    private static final long serialVersionUID = 9179541993413738569L;
    private MapleQuestActionType type;
    private IMapleData data;
    private MapleQuest quest;

    /**
     * Creates a new instance of MapleQuestAction
     */
    public MapleQuestAction(MapleQuestActionType type, IMapleData data, MapleQuest quest) {
        this.type = type;
        this.data = data;
        this.quest = quest;
    }

    private static boolean canGetItem(IMapleData item, MapleCharacter c) {
        if (item.getChildByPath("gender") != null) {
            final int gender = WzDataTool.getInt(item.getChildByPath("gender"));
            if (gender != 2 && gender != c.getGender()) {
                return false;
            }
        }
        if (item.getChildByPath("job") != null) {
            final int job = WzDataTool.getInt(item.getChildByPath("job"));
            final List<Integer> code = getJobBy5ByteEncoding(job);
            boolean jobFound = false;
            for (int codec : code) {
                if (codec / 100 == c.getJob() / 100) {
                    jobFound = true;
                    break;
                }
            }
            if (!jobFound && item.getChildByPath("jobEx") != null) {
                final int jobEx = WzDataTool.getInt(item.getChildByPath("jobEx"));
                final List<Integer> codeEx = getJobBy5ByteEncoding(jobEx);
                for (int codec : codeEx) {
                    if (codec / 100 == c.getJob() / 100) {
                        jobFound = true;
                        break;
                    }
                }
            }
            return jobFound;
        }
        return true;
    }

    public final boolean RestoreLostItem(final MapleCharacter c, final int itemid) {
        if (type == MapleQuestActionType.item) {
            int retitem;

            for (final IMapleData iEntry : data.getChildren()) {
                retitem = WzDataTool.getInt(iEntry.getChildByPath("id"), -1);
                if (retitem == itemid) {
                    if (!c.haveItem(retitem, 1, true, false)) {
                        MapleInventoryManipulator.addById(c.getClient(), retitem, (short) 1);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void runStart(MapleCharacter c, Integer extSelection) {
        MapleQuestStatus status;
        switch (type) {
            case exp:
                status = c.getQuest(quest);
                if (status.getForfeited() > 0) {
                    break;
                }
                c.gainExp(WzDataTool.getInt(data, 0) * GameConstants.getExpRate_Quest(c.getLevel()), true, true, true);
                break;
            case item:
                // first check for randomness in item selection
                Map<Integer, Integer> props = new HashMap<Integer, Integer>();
                IMapleData prop;
                for (IMapleData iEntry : data.getChildren()) {
                    prop = iEntry.getChildByPath("prop");
                    if (prop != null && WzDataTool.getInt(prop) != -1 && canGetItem(iEntry, c)) {
                        for (int i = 0; i < WzDataTool.getInt(iEntry.getChildByPath("prop")); i++) {
                            props.put(props.size(), WzDataTool.getInt(iEntry.getChildByPath("id")));
                        }
                    }
                }
                int selection = 0;
                int extNum = 0;
                if (props.size() > 0) {
                    selection = props.get(Randomizer.nextInt(props.size()));
                }
                for (IMapleData iEntry : data.getChildren()) {
                    if (!canGetItem(iEntry, c)) {
                        continue;
                    }
                    final int id = WzDataTool.getInt(iEntry.getChildByPath("id"), -1);
                    if (iEntry.getChildByPath("prop") != null) {
                        if (WzDataTool.getInt(iEntry.getChildByPath("prop")) == -1) {
                            if (extSelection != extNum++) {
                                continue;
                            }
                        } else if (id != selection) {
                            continue;
                        }
                    }
                    final short count = (short) WzDataTool.getInt(iEntry.getChildByPath("count"), 1);
                    if (count < 0) { // remove items
                        try {
                            MapleInventoryManipulator.removeById(c.getClient(), GameConstants.getInventoryType(id), id, (count * -1), true, false);
                        } catch (InventoryException ie) {
                            // it's better to catch this here so we'll atleast try to remove the other items
                            System.err.println("[h4x] Completing a quest without meeting the requirements" + ie);
                        }
                        c.getClient().getSession().write(WrapCUserLocal.getShowItemGain(id, count, true));
                    } else { // add items
                        final int period = WzDataTool.getInt(iEntry.getChildByPath("period"), 0) / 1440; //im guessing.
                        final String name = MapleItemInformationProvider.getInstance().getName(id);
                        if (id / 10000 == 114 && name != null && name.length() > 0) { //medal
                            final String msg = "You have attained title <" + name + ">";
                            c.dropMessage(-1, msg);
                            c.dropMessage(5, msg);
                        }
                        MapleInventoryManipulator.addById(c.getClient(), id, count, "", null, period);
                        c.getClient().getSession().write(WrapCUserLocal.getShowItemGain(id, count, true));
                    }
                }
                break;
            case nextQuest:
                status = c.getQuest(quest);
                if (status.getForfeited() > 0) {
                    break;
                }
                c.getClient().getSession().write(ResCUserLocal.UserQuestResult(quest.getId(), status.getNpc(), WzDataTool.getInt(data)));
                break;
            case money:
                status = c.getQuest(quest);
                if (status.getForfeited() > 0) {
                    break;
                }
                c.gainMeso(WzDataTool.getInt(data, 0), true, false, true);
                break;
            case quest:
                for (IMapleData qEntry : data) {
                    c.updateQuest(new MapleQuestStatus(MapleQuest.getInstance(WzDataTool.getInt(qEntry.getChildByPath("id"))),
                                    (byte) WzDataTool.getInt(qEntry.getChildByPath("state"), 0)));
                }
                break;
            case skill:
                //TODO needs gain/lost message?
                for (IMapleData sEntry : data) {
                    final int skillid = WzDataTool.getInt(sEntry.getChildByPath("id"));
                    int skillLevel = WzDataTool.getInt(sEntry.getChildByPath("skillLevel"), 0);
                    int masterLevel = WzDataTool.getInt(sEntry.getChildByPath("masterLevel"), 0);
                    final ISkill skillObject = SkillFactory.getSkill(skillid);

                    for (IMapleData applicableJob : sEntry.getChildByPath("job")) {
                        if (skillObject.isBeginnerSkill() || c.getJob() == WzDataTool.getInt(applicableJob)) {
                            c.changeSkillLevel(skillObject,
                                    (byte) Math.max(skillLevel, c.getSkillLevel(skillObject)),
                                    (byte) Math.max(masterLevel, c.getMasterLevel(skillObject)));
                            break;
                        }
                    }
                }
                break;
            case pop:
                status = c.getQuest(quest);
                if (status.getForfeited() > 0) {
                    break;
                }
                final int fameGain = WzDataTool.getInt(data, 0);
                c.addFame(fameGain);
                c.sendStatChanged();
                c.SendPacket(ResWrapper.getShowFameGain(fameGain));
                break;
            case buffItemID:
                status = c.getQuest(quest);
                if (status.getForfeited() > 0) {
                    break;
                }
                final int tobuff = WzDataTool.getInt(data, -1);
                if (tobuff == -1) {
                    break;
                }
                MapleItemInformationProvider.getInstance().getItemEffect(tobuff).applyTo(c);
                break;
            case infoNumber: {
//		System.out.println("quest : "+MapleDataTool.getInt(data, 0)+"");
//		MapleQuest.getInstance(MapleDataTool.getInt(data, 0)).forceComplete(c, 0);
                break;
            }
            case sp: {
                status = c.getQuest(quest);
                if (status.getForfeited() > 0) {
                    break;
                }
                for (IMapleData iEntry : data.getChildren()) {
                    final int sp_val = WzDataTool.getInt(iEntry.getChildByPath("sp_value"), 0);
                    if (iEntry.getChildByPath("job") != null) {
                        int finalJob = 0;
                        for (IMapleData jEntry : iEntry.getChildByPath("job").getChildren()) {
                            final int job_val = WzDataTool.getInt(jEntry, 0);
                            if (c.getJob() >= job_val && job_val > finalJob) {
                                finalJob = job_val;
                            }
                        }
                        if (finalJob == 0) {
                            c.gainSP(sp_val);
                        } else {
                            c.gainSP(sp_val, GameConstants.getSkillBook(finalJob));
                        }
                    } else {
                        c.gainSP(sp_val);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    public boolean checkEnd(MapleCharacter chr, Integer extSelection) {
        switch (type) {
            case item: {
                // first check for randomness in item selection
                final Map<Integer, Integer> props = new HashMap<>();

                for (IMapleData iEntry : data.getChildren()) {
                    final IMapleData prop = iEntry.getChildByPath("prop");
                    if (prop != null && WzDataTool.getInt(prop) != -1 && canGetItem(iEntry, chr)) {
                        for (int i = 0; i < WzDataTool.getInt(iEntry.getChildByPath("prop")); i++) {
                            props.put(props.size(), WzDataTool.getInt(iEntry.getChildByPath("id")));
                        }
                    }
                }
                int selection = 0;
                int extNum = 0;
                if (props.size() > 0) {
                    selection = props.get(Randomizer.nextInt(props.size()));
                }
                byte eq = 0, use = 0, setup = 0, etc = 0, cash = 0;

                for (IMapleData iEntry : data.getChildren()) {
                    if (!canGetItem(iEntry, chr)) {
                        continue;
                    }
                    final int id = WzDataTool.getInt(iEntry.getChildByPath("id"), -1);
                    if (iEntry.getChildByPath("prop") != null) {
                        if (WzDataTool.getInt(iEntry.getChildByPath("prop")) == -1) {
                            if (extSelection != extNum++) {
                                continue;
                            }
                        } else if (id != selection) {
                            continue;
                        }
                    }
                    final short count = (short) WzDataTool.getInt(iEntry.getChildByPath("count"), 1);
                    if (count < 0) { // remove items
                        if (!chr.haveItem(id, count, false, true)) {
                            chr.dropMessage(1, "You are short of some item to complete quest.");
                            return false;
                        }
                    } else { // add items
                        if (MapleItemInformationProvider.getInstance().isPickupRestricted(id) && chr.haveItem(id, 1, true, false)) {
                            chr.dropMessage(1, "You have this item already: " + MapleItemInformationProvider.getInstance().getName(id));
                            return false;
                        }
                        switch (GameConstants.getInventoryType(id)) {
                            case EQUIP:
                                eq++;
                                break;
                            case USE:
                                use++;
                                break;
                            case SETUP:
                                setup++;
                                break;
                            case ETC:
                                etc++;
                                break;
                            case CASH:
                                cash++;
                                break;
                        }
                    }
                }
                if (chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq) {
                    chr.dropMessage(1, "Please make space for your Equip inventory.");
                    return false;
                } else if (chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() < use) {
                    chr.dropMessage(1, "Please make space for your Use inventory.");
                    return false;
                } else if (chr.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup) {
                    chr.dropMessage(1, "Please make space for your Setup inventory.");
                    return false;
                } else if (chr.getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc) {
                    chr.dropMessage(1, "Please make space for your Etc inventory.");
                    return false;
                } else if (chr.getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash) {
                    chr.dropMessage(1, "Please make space for your Cash inventory.");
                    return false;
                }
                return true;
            }
            case money: {
                final int meso = WzDataTool.getInt(data, 0);
                if (chr.getMeso() + meso < 0) { // Giving, overflow
                    chr.dropMessage(1, "Meso exceed the max amount, 2147483647.");
                    return false;
                } else if (meso < 0 && chr.getMeso() < Math.abs(meso)) { //remove meso
                    chr.dropMessage(1, "Insufficient meso.");
                    return false;
                }
                return true;
            }
        }
        return true;
    }

    public void runEnd(MapleCharacter chr, Integer extSelection) {
        switch (type) {
            case exp: {
                chr.gainExp(WzDataTool.getInt(data, 0) * GameConstants.getExpRate_Quest(chr.getLevel()), true, true, true);
                break;
            }
            case item: {
                // first check for randomness in item selection
                Map<Integer, Integer> props = new HashMap<Integer, Integer>();

                for (IMapleData iEntry : data.getChildren()) {
                    final IMapleData prop = iEntry.getChildByPath("prop");
                    if (prop != null && WzDataTool.getInt(prop) != -1 && canGetItem(iEntry, chr)) {
                        for (int i = 0; i < WzDataTool.getInt(iEntry.getChildByPath("prop")); i++) {
                            props.put(props.size(), WzDataTool.getInt(iEntry.getChildByPath("id")));
                        }
                    }
                }
                int selection = 0;
                int extNum = 0;
                if (props.size() > 0) {
                    selection = props.get(Randomizer.nextInt(props.size()));
                }
                for (IMapleData iEntry : data.getChildren()) {
                    if (!canGetItem(iEntry, chr)) {
                        continue;
                    }
                    final int id = WzDataTool.getInt(iEntry.getChildByPath("id"), -1);
                    if (iEntry.getChildByPath("prop") != null) {
                        if (WzDataTool.getInt(iEntry.getChildByPath("prop")) == -1) {
                            if (extSelection != extNum++) {
                                continue;
                            }
                        } else if (id != selection) {
                            continue;
                        }
                    }
                    final short count = (short) WzDataTool.getInt(iEntry.getChildByPath("count"), 1);
                    if (count < 0) { // remove items
                        MapleInventoryManipulator.removeById(chr.getClient(), GameConstants.getInventoryType(id), id, (count * -1), true, false);
                        chr.getClient().getSession().write(WrapCUserLocal.getShowItemGain(id, count, true));
                    } else { // add items
                        final int period = WzDataTool.getInt(iEntry.getChildByPath("period"), 0) / 1440;
                        final String name = MapleItemInformationProvider.getInstance().getName(id);
                        if (id / 10000 == 114 && name != null && name.length() > 0) { //medal
                            final String msg = "You have attained title <" + name + ">";
                            chr.dropMessage(-1, msg);
                            chr.dropMessage(5, msg);
                        }
                        MapleInventoryManipulator.addById(chr.getClient(), id, count, "", null, period);
                        chr.getClient().getSession().write(WrapCUserLocal.getShowItemGain(id, count, true));
                    }
                }
                break;
            }
            case nextQuest: {
                chr.getClient().getSession().write(ResCUserLocal.UserQuestResult(quest.getId(), chr.getQuest(quest).getNpc(), WzDataTool.getInt(data)));
                break;
            }
            case money: {
                chr.gainMeso(WzDataTool.getInt(data, 0), true, false, true);
                break;
            }
            case quest: {
                for (IMapleData qEntry : data) {
                    chr.updateQuest(new MapleQuestStatus(MapleQuest.getInstance(WzDataTool.getInt(qEntry.getChildByPath("id"))),
                                    (byte) WzDataTool.getInt(qEntry.getChildByPath("state"), 0)));
                }
                break;
            }
            case skill: {
                for (IMapleData sEntry : data) {
                    final int skillid = WzDataTool.getInt(sEntry.getChildByPath("id"));
                    int skillLevel = WzDataTool.getInt(sEntry.getChildByPath("skillLevel"), 0);
                    int masterLevel = WzDataTool.getInt(sEntry.getChildByPath("masterLevel"), 0);
                    final ISkill skillObject = SkillFactory.getSkill(skillid);

                    for (IMapleData applicableJob : sEntry.getChildByPath("job")) {
                        if (skillObject.isBeginnerSkill() || chr.getJob() == WzDataTool.getInt(applicableJob)) {
                            chr.changeSkillLevel(skillObject,
                                    (byte) Math.max(skillLevel, chr.getSkillLevel(skillObject)),
                                    (byte) Math.max(masterLevel, chr.getMasterLevel(skillObject)));
                            break;
                        }
                    }
                }
                break;
            }
            case pop: {
                final int fameGain = WzDataTool.getInt(data, 0);
                chr.addFame(fameGain);
                chr.sendStatChanged();
                chr.SendPacket(ResWrapper.getShowFameGain(fameGain));
                break;
            }
            case buffItemID: {
                final int tobuff = WzDataTool.getInt(data, -1);
                if (tobuff == -1) {
                    break;
                }
                MapleItemInformationProvider.getInstance().getItemEffect(tobuff).applyTo(chr);
                break;
            }
            case infoNumber: {
//		System.out.println("quest : "+MapleDataTool.getInt(data, 0)+"");
//		MapleQuest.getInstance(MapleDataTool.getInt(data, 0)).forceComplete(c, 0);
                break;
            }
            case sp: {
                for (IMapleData iEntry : data.getChildren()) {
                    final int sp_val = WzDataTool.getInt(iEntry.getChildByPath("sp_value"), 0);
                    if (iEntry.getChildByPath("job") != null) {
                        int finalJob = 0;
                        for (IMapleData jEntry : iEntry.getChildByPath("job").getChildren()) {
                            final int job_val = WzDataTool.getInt(jEntry, 0);
                            if (chr.getJob() >= job_val && job_val > finalJob) {
                                finalJob = job_val;
                            }
                        }
                        chr.gainSP(sp_val, GameConstants.getSkillBook(finalJob));
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
        List<Integer> ret = new ArrayList<Integer>();
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

    public MapleQuestActionType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type + ": " + data;
    }
}
