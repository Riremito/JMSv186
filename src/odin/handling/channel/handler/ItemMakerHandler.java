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
package odin.handling.channel.handler;

import java.util.List;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import java.util.ArrayList;
import java.util.Map;
import odin.client.inventory.Item;
import odin.client.inventory.Equip;
import odin.client.SkillFactory;
import tacos.client.TacosClient;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.ResCUserRemote;
import odin.server.ItemMakerFactory;
import odin.server.ItemMakerFactory.GemCreateEntry;
import odin.server.ItemMakerFactory.ItemMakerCreateEntry;
import odin.server.Randomizer;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleInventoryManipulator;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.builder.PB_UserEffect;

public class ItemMakerHandler {

    enum RecipeClass {
        RECIPE_CLASS_START(0),
        RECIPE_CLASS_NORMAL(1),
        RECIPE_CLASS_HIDDEN(2),
        RECIPE_CLASS_MONSTER_CRYSTAL(3),
        RECIPE_CLASS_EQUIP_DISASSEMBLE(4),
        RECIPE_CLASS_END(5),
        UNKNOWN;

        private int value;

        RecipeClass(int value) {
            this.value = value;
        }

        RecipeClass() {
            this.value = -1;
        }

        public int get() {
            return this.value;
        }

        public void set(int value) {
            this.value = value;
        }

        public static RecipeClass find(int value) {
            for (RecipeClass ops : RecipeClass.values()) {
                if (ops.get() == value) {
                    return ops;
                }
            }
            return UNKNOWN;
        }
    }

    public enum ItemMakerResult {
        ITEM_MAKER_RESULT_SUCCESS(0),
        ITEM_MAKER_RESULT_DESTROYED(1),
        ITEM_MAKER_ERR_UNKNOWN(2),
        ITEM_MAKER_ERR_EMPTYSLOT(3),
        ITEM_MAKER_ERR_EMPTYSLOT_EQUIP(4),
        ITEM_MAKER_ERR_EMPTYSLOT_COMSUME(5),
        ITEM_MAKER_ERR_EMPTYSLOT_INSTALL(6),
        ITEM_MAKER_ERR_EMPTYSLOT_ETC(7),
        UNKNOWN;

        private int value;

        ItemMakerResult(int value) {
            this.value = value;
        }

        ItemMakerResult() {
            value = -1;
        }

        public int get() {
            return this.value;
        }

        public void set(int value) {
            this.value = value;
        }

        public static ItemMakerResult find(int value) {
            for (ItemMakerResult ops : ItemMakerResult.values()) {
                if (ops.get() == value) {
                    return ops;
                }
            }
            return UNKNOWN;
        }
    }

    public static boolean OnItemMakeRequest(ClientPacket cp, TacosCharacter chr) {
        TacosClient client = chr.getClient();
        int type = cp.Decode4();

        switch (RecipeClass.find(type)) {
            case RECIPE_CLASS_NORMAL: {
                int toCreate = cp.Decode4();
                if (GameConstants.isGem(toCreate)) {
                    return makeGem(chr, toCreate);
                }
                if (GameConstants.isOtherGem(toCreate)) {
                    return makeOtherGem(chr, toCreate);
                }
                return makeEquip(cp, chr, toCreate);
            }
            case RECIPE_CLASS_MONSTER_CRYSTAL: {
                int etc = cp.Decode4();

                if (!chr.haveItem(etc, 100, false, true)) {
                    DebugLogger.ErrorLog("RECIPE_CLASS_MONSTER_CRYSTAL");
                    return false;
                }
                MapleInventoryManipulator.addById(client.getPlayer(), getCreateCrystal(etc), (short) 1);
                MapleInventoryManipulator.removeById(client.getPlayer(), MapleInventoryType.ETC, etc, 100, false, false);
                sendMakeSuccess(chr);
                return true;
            }
            case RECIPE_CLASS_EQUIP_DISASSEMBLE: {
                int itemId = cp.Decode4();
                int unk1 = cp.Decode4();
                int slot = cp.Decode4();

                Item toUse = chr.getInventory(MapleInventoryType.EQUIP).getItem((short) slot);
                if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
                    DebugLogger.ErrorLog("RECIPE_CLASS_EQUIP_DISASSEMBLE");
                    return false;
                }

                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (!ii.isDropRestricted(itemId) && !ii.isAccountShared(itemId)) {
                    int[] toGive = getCrystal(itemId, ii.getReqLevel(itemId));
                    MapleInventoryManipulator.addById(client.getPlayer(), toGive[0], (byte) toGive[1]);
                    MapleInventoryManipulator.removeFromSlot(client.getPlayer(), MapleInventoryType.EQUIP, (short) slot, (byte) 1, false);
                }
                sendMakeSuccess(chr);
                return true;
            }
            default: {
                DebugLogger.ErrorLog("OnItemMakeRequest : not coded.");
                return false;
            }
        }
    }

    private static void sendMakeSuccess(TacosCharacter chr) {
        PB_UserEffect pb = PB_UserEffect.builder()
                .maker(ItemMakerResult.ITEM_MAKER_RESULT_SUCCESS)
                .build();
        chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_ItemMaker, pb));
        chr.getMap().splitSendPacket(chr, ResCUserRemote.ItemMakerResultTo(chr, true), chr.getId());
    }

    private static boolean makeGem(TacosCharacter chr, int toCreate) {
        TacosClient client = chr.getClient();
        GemCreateEntry gem = ItemMakerFactory.getInstance().getGemInfo(toCreate);
        if (gem == null) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 1");
            return false;
        }
        if (!hasSkill(client.getPlayer(), gem.getReqSkillLevel())) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 2");
            return false;
        }
        if (chr.getMeso() < gem.getCost()) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 3");
            return false;
        }
        int randGemGiven = getRandomGem(gem.getRandomReward());
        if (chr.getInventory(GameConstants.getInventoryType(randGemGiven)).isFull()) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 4");
            return false;
        }
        int taken = checkRequiredNRemove(client.getPlayer(), gem.getReqRecipes());
        if (taken == 0) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 5");
            return false;
        }

        chr.gainMeso(-gem.getCost(), false);
        MapleInventoryManipulator.addById(client.getPlayer(), randGemGiven, (byte) (taken == randGemGiven ? 9 : 1)); // Gem is always 1
        sendMakeSuccess(chr);
        return true;
    }

    private static boolean makeOtherGem(TacosCharacter chr, int toCreate) {
        TacosClient client = chr.getClient();
        GemCreateEntry gem = ItemMakerFactory.getInstance().getGemInfo(toCreate);
        if (gem == null) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 6");
            return false;
        }
        if (!hasSkill(client.getPlayer(), gem.getReqSkillLevel())) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 7");
            return false;
        }
        if (chr.getMeso() < gem.getCost()) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 8");
            return false;
        }
        if (chr.getInventory(GameConstants.getInventoryType(toCreate)).isFull()) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 9");
            return false;
        }
        if (checkRequiredNRemove(client.getPlayer(), gem.getReqRecipes()) == 0) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 10");
            return false;
        }

        chr.gainMeso(-gem.getCost(), false);
        if (GameConstants.getInventoryType(toCreate) == MapleInventoryType.EQUIP) {
            MapleInventoryManipulator.addbyItem(client.getPlayer(), MapleItemInformationProvider.getInstance().getEquipById(toCreate));
        } else {
            MapleInventoryManipulator.addById(client.getPlayer(), toCreate, (byte) 1); // Gem is always 1
        }
        sendMakeSuccess(chr);
        return true;
    }

    private static boolean makeEquip(ClientPacket cp, TacosCharacter chr, int toCreate) {
        TacosClient client = chr.getClient();
        boolean stimulator = cp.Decode1() > 0;
        int numEnchanter = cp.Decode4();

        ItemMakerCreateEntry create = ItemMakerFactory.getInstance().getCreateInfo(toCreate);
        if (create == null) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 11");
            return false;
        }
        if (numEnchanter > create.getTUC()) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 12");
            return false;
        }
        if (!hasSkill(client.getPlayer(), create.getReqSkillLevel())) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 13");
            return false;
        }
        if (chr.getMeso() < create.getCost()) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 14");
            return false;
        }
        if (chr.getInventory(GameConstants.getInventoryType(toCreate)).isFull()) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 15");
            return false;
        }
        if (checkRequiredNRemove(client.getPlayer(), create.getReqItems()) == 0) {
            DebugLogger.ErrorLog("RECIPE_CLASS_NORMAL : 16");
            return false;
        }

        chr.gainMeso(-create.getCost(), false);

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Equip toGive = (Equip) ii.getEquipById(toCreate);

        // the stimulator is also used when enchanters are used.
        if ((stimulator || numEnchanter > 0) && chr.haveItem(create.getStimulator(), 1, false, true)) {
            ii.randomizeStats(toGive);
            MapleInventoryManipulator.removeById(client.getPlayer(), MapleInventoryType.ETC, create.getStimulator(), 1, false, false);
        }
        for (int i = 0; i < numEnchanter; i++) {
            int enchant = cp.Decode4();
            if (!chr.haveItem(enchant, 1, false, true)) {
                continue;
            }
            Map<String, Byte> stats = ii.getItemMakeStats(enchant);
            if (stats == null) {
                continue;
            }
            addEnchantStats(stats, toGive);
            MapleInventoryManipulator.removeById(client.getPlayer(), MapleInventoryType.ETC, enchant, 1, false, false);
        }

        MapleInventoryManipulator.addbyItem(client.getPlayer(), toGive);
        sendMakeSuccess(chr);
        return true;
    }

    private static int getCreateCrystal(int etc) {
        int itemid;
        short level = MapleItemInformationProvider.getInstance().getItemMakeLevel(etc);

        if (level >= 31 && level <= 50) {
            itemid = 4260000;
        } else if (level >= 51 && level <= 60) {
            itemid = 4260001;
        } else if (level >= 61 && level <= 70) {
            itemid = 4260002;
        } else if (level >= 71 && level <= 80) {
            itemid = 4260003;
        } else if (level >= 81 && level <= 90) {
            itemid = 4260004;
        } else if (level >= 91 && level <= 100) {
            itemid = 4260005;
        } else if (level >= 101 && level <= 110) {
            itemid = 4260006;
        } else if (level >= 111 && level <= 120) {
            itemid = 4260007;
        } else if (level >= 121) {
            itemid = 4260008;
        } else {
            throw new RuntimeException("Invalid Item Maker id");
        }
        return itemid;
    }

    private static int[] getCrystal(int itemid, int level) {
        int[] all = new int[2];
        all[0] = -1;
        if (level >= 31 && level <= 50) {
            all[0] = 4260000;
        } else if (level >= 51 && level <= 60) {
            all[0] = 4260001;
        } else if (level >= 61 && level <= 70) {
            all[0] = 4260002;
        } else if (level >= 71 && level <= 80) {
            all[0] = 4260003;
        } else if (level >= 81 && level <= 90) {
            all[0] = 4260004;
        } else if (level >= 91 && level <= 100) {
            all[0] = 4260005;
        } else if (level >= 101 && level <= 110) {
            all[0] = 4260006;
        } else if (level >= 111 && level <= 120) {
            all[0] = 4260007;
        } else if (level >= 121 && level <= 200) {
            all[0] = 4260008;
        } else {
            throw new RuntimeException("Invalid Item Maker type" + level);
        }
        if (GameConstants.isWeapon(itemid) || GameConstants.isOverall(itemid)) {
            all[1] = Randomizer.rand(5, 11);
        } else {
            all[1] = Randomizer.rand(3, 7);
        }
        return all;
    }

    private static void addEnchantStats(Map<String, Byte> stats, Equip item) {
        short s = stats.get("incPAD");
        if (s != 0) {
            item.setWatk((short) (item.getWatk() + s));
        }
        s = stats.get("incMAD");
        if (s != 0) {
            item.setMatk((short) (item.getMatk() + s));
        }
        s = stats.get("incACC");
        if (s != 0) {
            item.setAcc((short) (item.getAcc() + s));
        }
        s = stats.get("incEVA");
        if (s != 0) {
            item.setAvoid((short) (item.getAvoid() + s));
        }
        s = stats.get("incSpeed");
        if (s != 0) {
            item.setSpeed((short) (item.getSpeed() + s));
        }
        s = stats.get("incJump");
        if (s != 0) {
            item.setJump((short) (item.getJump() + s));
        }
        s = stats.get("incMaxHP");
        if (s != 0) {
            item.setHp((short) (item.getHp() + s));
        }
        s = stats.get("incMaxMP");
        if (s != 0) {
            item.setMp((short) (item.getMp() + s));
        }
        s = stats.get("incSTR");
        if (s != 0) {
            item.setStr((short) (item.getStr() + s));
        }
        s = stats.get("incDEX");
        if (s != 0) {
            item.setDex((short) (item.getDex() + s));
        }
        s = stats.get("incINT");
        if (s != 0) {
            item.setInt((short) (item.getInt() + s));
        }
        s = stats.get("incLUK");
        if (s != 0) {
            item.setLuk((short) (item.getLuk() + s));
        }
        s = stats.get("randOption");
        if (s > 0) {
            boolean success = Randomizer.nextBoolean();
            int ma = item.getMatk(), wa = item.getWatk();
            if (wa > 0) {
                item.setWatk((short) (success ? (wa + s) : (wa - s)));
            }
            if (ma > 0) {
                item.setMatk((short) (success ? (ma + s) : (ma - s)));
            }
        }
        s = stats.get("randStat");
        if (s > 0) {
            boolean success = Randomizer.nextBoolean();
            int str = item.getStr(), dex = item.getDex(), luk = item.getLuk(), int_ = item.getInt();
            if (str > 0) {
                item.setStr((short) (success ? (str + s) : (str - s)));
            }
            if (dex > 0) {
                item.setDex((short) (success ? (dex + s) : (dex - s)));
            }
            if (int_ > 0) {
                item.setInt((short) (success ? (int_ + s) : (int_ - s)));
            }
            if (luk > 0) {
                item.setLuk((short) (success ? (luk + s) : (luk - s)));
            }
        }
    }

    private static int getRandomGem(List<SimpleImmutableEntry<Integer, Integer>> rewards) {
        int itemid;
        List<Integer> items = new ArrayList<>();

        for (SimpleImmutableEntry p : rewards) {
            itemid = (Integer) p.getKey();
            for (int i = 0; i < (Integer) p.getValue(); i++) {
                items.add(itemid);
            }
        }
        return items.get(Randomizer.nextInt(items.size()));
    }

    private static int checkRequiredNRemove(TacosCharacter chr, List<SimpleImmutableEntry<Integer, Integer>> recipe) {
        int itemid = 0;
        for (SimpleImmutableEntry<Integer, Integer> p : recipe) {
            if (!chr.haveItem(p.getKey(), p.getValue(), false, true)) {
                return 0;
            }
        }
        for (SimpleImmutableEntry<Integer, Integer> p : recipe) {
            itemid = p.getKey();
            MapleInventoryManipulator.removeById(chr, GameConstants.getInventoryType(itemid), itemid, p.getValue(), false, false);
        }
        return itemid;
    }

    private static boolean hasSkill(TacosCharacter chr, int reqlvl) {
        if (GameConstants.isKOC(chr.getJob())) { // KoC Maker skill.
            return chr.getSkillLevel(SkillFactory.getSkill(10001007)) >= reqlvl;
        } else if (GameConstants.isAran(chr.getJob())) { // KoC Maker skill.
            return chr.getSkillLevel(SkillFactory.getSkill(20001007)) >= reqlvl;
        } else if (GameConstants.isEvan(chr.getJob())) { // KoC Maker skill.
            return chr.getSkillLevel(SkillFactory.getSkill(20011007)) >= reqlvl;
        } else if (GameConstants.isResist(chr.getJob())) { // KoC Maker skill.
            return chr.getSkillLevel(SkillFactory.getSkill(30001007)) >= reqlvl;
        } else {
            return chr.getSkillLevel(SkillFactory.getSkill(1007)) >= reqlvl;
        }
    }
}
