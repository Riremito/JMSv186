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

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Map;
import odin.client.inventory.Equip;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.ItemMakerFactory;
import odin.server.ItemMakerFactory.GemCreateEntry;
import odin.server.ItemMakerFactory.ItemMakerCreateEntry;
import odin.server.ItemMakerFactory.RandomReward;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.Randomizer;
import tacos.client.TacosCharacter;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsItemMakerResult;
import tacos.packet.ops.OpsRecipeClass;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.builder.PB_UserEffect;
import tacos.wz.WzXML;

public class ItemMakerHandler {

    private static final int MONSTER_CRYSTAL_COST = 100; // monster etc items for a crystal

    // CUIItemMaker::RequestItemMake
    public static boolean OnItemMakeRequest(ClientPacket cp, TacosCharacter chr) {
        int recipe_class = cp.Decode4();
        OpsItemMakerResult result;
        switch (OpsRecipeClass.find(recipe_class)) {
            case RECIPE_CLASS_NORMAL:
            case RECIPE_CLASS_HIDDEN: {
                int target_item_id = cp.Decode4(); // m_nTargetItem
                if (GameConstants.isGem(target_item_id)) {
                    result = makeGem(chr, target_item_id);
                } else if (GameConstants.isOtherGem(target_item_id)) {
                    result = makeOtherGem(chr, target_item_id);
                } else {
                    result = makeEquip(cp, chr, target_item_id);
                }
                break;
            }
            case RECIPE_CLASS_MONSTER_CRYSTAL: {
                int etc_item_id = cp.Decode4();
                result = makeMonsterCrystal(chr, etc_item_id);
                break;
            }
            case RECIPE_CLASS_EQUIP_DISASSEMBLE: {
                int item_id = cp.Decode4();
                int inventory_type = cp.Decode4(); // m_nTI_DisassbleItem
                int slot = cp.Decode4();
                result = disassembleEquip(chr, item_id, slot);
                break;
            }
            default: {
                DebugLogger.ErrorLog("OnItemMakeRequest : not coded " + recipe_class);
                result = OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
                break;
            }
        }
        sendMakeResult(chr, result);
        return result == OpsItemMakerResult.ITEM_MAKER_RESULT_SUCCESS;
    }

    // CUIItemMaker::OnItemMakeResult
    private static void sendMakeResult(TacosCharacter chr, OpsItemMakerResult result) {
        PB_UserEffect pb = PB_UserEffect.builder()
                .maker(result)
                .build();
        chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_ItemMaker, pb));
        if (result == OpsItemMakerResult.ITEM_MAKER_RESULT_SUCCESS || result == OpsItemMakerResult.ITEM_MAKER_RESULT_DESTROYED) {
            chr.getMap().splitSendPacket(chr, ResCUserRemote.ItemMakerResultTo(chr, result), chr.getId());
        }
    }

    // the result for a full inventory.
    private static OpsItemMakerResult getEmptySlotError(MapleInventoryType type) {
        switch (type) {
            case EQUIP: {
                return OpsItemMakerResult.ITEM_MAKER_ERR_EMPTYSLOT_EQUIP;
            }
            case USE: {
                return OpsItemMakerResult.ITEM_MAKER_ERR_EMPTYSLOT_COMSUME;
            }
            case SETUP: {
                return OpsItemMakerResult.ITEM_MAKER_ERR_EMPTYSLOT_INSTALL;
            }
            case ETC: {
                return OpsItemMakerResult.ITEM_MAKER_ERR_EMPTYSLOT_ETC;
            }
            default: {
                return OpsItemMakerResult.ITEM_MAKER_ERR_EMPTYSLOT;
            }
        }
    }

    private static boolean isInventoryFull(TacosCharacter chr, int item_id) {
        return chr.getInventory(GameConstants.getInventoryType(item_id)).isFull();
    }

    private static OpsItemMakerResult makeMonsterCrystal(TacosCharacter chr, int etc_item_id) {
        if (!chr.haveItem(etc_item_id, MONSTER_CRYSTAL_COST, false, true)) {
            DebugLogger.ErrorLog("RECIPE_CLASS_MONSTER_CRYSTAL : item");
            return OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
        }
        int crystal_id = getCrystal(MapleItemInformationProvider.getInstance().getItemMakeLevel(etc_item_id));
        if (crystal_id <= 0) {
            DebugLogger.ErrorLog("RECIPE_CLASS_MONSTER_CRYSTAL : level");
            return OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
        }
        if (isInventoryFull(chr, crystal_id)) {
            return getEmptySlotError(GameConstants.getInventoryType(crystal_id));
        }
        MapleInventoryManipulator.addById(chr, crystal_id, (short) 1);
        MapleInventoryManipulator.removeById(chr, MapleInventoryType.ETC, etc_item_id, MONSTER_CRYSTAL_COST, false, false);
        return OpsItemMakerResult.ITEM_MAKER_RESULT_SUCCESS;
    }

    private static OpsItemMakerResult disassembleEquip(TacosCharacter chr, int item_id, int slot) {
        Item to_use = chr.getInventory(MapleInventoryType.EQUIP).getItem((short) slot);
        if (to_use == null || to_use.getItemId() != item_id || to_use.getQuantity() < 1) {
            DebugLogger.ErrorLog("RECIPE_CLASS_EQUIP_DISASSEMBLE");
            return OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isDropRestricted(item_id) || ii.isAccountShared(item_id)) {
            return OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
        }
        int crystal_id = getCrystal(ii.getReqLevel(item_id));
        if (crystal_id <= 0) {
            return OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
        }
        if (isInventoryFull(chr, crystal_id)) {
            return getEmptySlotError(GameConstants.getInventoryType(crystal_id));
        }
        int count = (GameConstants.isWeapon(item_id) || GameConstants.isOverall(item_id)) ? Randomizer.rand(5, 11) : Randomizer.rand(3, 7);
        MapleInventoryManipulator.addById(chr, crystal_id, (short) count);
        MapleInventoryManipulator.removeFromSlot(chr, MapleInventoryType.EQUIP, (short) slot, (short) 1, false);
        return OpsItemMakerResult.ITEM_MAKER_RESULT_SUCCESS;
    }

    private static OpsItemMakerResult makeGem(TacosCharacter chr, int target_item_id) {
        GemCreateEntry gem = ItemMakerFactory.getInstance().getGemInfo(target_item_id);
        if (gem == null || !hasSkill(chr, gem.getReqSkillLevel()) || chr.getMeso() < gem.getCost()) {
            DebugLogger.ErrorLog("makeGem : " + target_item_id);
            return OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
        }
        RandomReward reward = getRandomReward(gem.getRandomReward());
        if (reward == null) {
            return OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
        }
        if (isInventoryFull(chr, reward.itemId)) {
            return getEmptySlotError(GameConstants.getInventoryType(reward.itemId));
        }
        if (!checkRequiredNRemove(chr, gem.getReqRecipes())) {
            return OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
        }
        chr.gainMeso(-gem.getCost(), false);
        MapleInventoryManipulator.addById(chr, reward.itemId, (short) reward.count);
        return OpsItemMakerResult.ITEM_MAKER_RESULT_SUCCESS;
    }

    private static OpsItemMakerResult makeOtherGem(TacosCharacter chr, int target_item_id) {
        GemCreateEntry gem = ItemMakerFactory.getInstance().getGemInfo(target_item_id);
        if (gem == null || !hasSkill(chr, gem.getReqSkillLevel()) || chr.getMeso() < gem.getCost()) {
            DebugLogger.ErrorLog("makeOtherGem : " + target_item_id);
            return OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
        }
        if (isInventoryFull(chr, target_item_id)) {
            return getEmptySlotError(GameConstants.getInventoryType(target_item_id));
        }
        if (!checkRequiredNRemove(chr, gem.getReqRecipes())) {
            return OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
        }
        chr.gainMeso(-gem.getCost(), false);
        if (GameConstants.getInventoryType(target_item_id) == MapleInventoryType.EQUIP) {
            MapleInventoryManipulator.addbyItem(chr, MapleItemInformationProvider.getInstance().getEquipById(target_item_id));
        } else {
            MapleInventoryManipulator.addById(chr, target_item_id, (short) Math.max(1, gem.getQuantity()));
        }
        return OpsItemMakerResult.ITEM_MAKER_RESULT_SUCCESS;
    }

    private static OpsItemMakerResult makeEquip(ClientPacket cp, TacosCharacter chr, int target_item_id) {
        boolean catalyst = cp.Decode1() != 0; // m_CatalystSlot.bMounted
        int num_gem = cp.Decode4(); // m_nNumGem_Mounted
        int[] gems = new int[Math.max(0, num_gem)];
        for (int i = 0; i < gems.length; i++) {
            gems[i] = cp.Decode4();
        }

        ItemMakerCreateEntry create = ItemMakerFactory.getInstance().getCreateInfo(target_item_id);
        if (create == null || num_gem > create.getTUC() || !hasSkill(chr, create.getReqSkillLevel()) || chr.getMeso() < create.getCost()) {
            DebugLogger.ErrorLog("makeEquip : " + target_item_id);
            return OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
        }
        if (isInventoryFull(chr, target_item_id)) {
            return getEmptySlotError(GameConstants.getInventoryType(target_item_id));
        }
        if (!checkRequiredNRemove(chr, create.getReqItems())) {
            return OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
        }
        chr.gainMeso(-create.getCost(), false);

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Equip to_give = (Equip) ii.getEquipById(target_item_id);
        // the stimulator is also used when enchanters are used.
        if ((catalyst || gems.length > 0) && chr.haveItem(create.getStimulator(), 1, false, true)) {
            ii.randomizeStats(to_give);
            MapleInventoryManipulator.removeById(chr, MapleInventoryType.ETC, create.getStimulator(), 1, false, false);
        }
        for (int gem_id : gems) {
            if (!chr.haveItem(gem_id, 1, false, true)) {
                continue;
            }
            Map<String, Byte> stats = ii.getItemMakeStats(gem_id);
            if (stats == null) {
                continue;
            }
            addEnchantStats(stats, to_give);
            MapleInventoryManipulator.removeById(chr, MapleInventoryType.ETC, gem_id, 1, false, false);
        }
        MapleInventoryManipulator.addbyItem(chr, to_give);
        return OpsItemMakerResult.ITEM_MAKER_RESULT_SUCCESS;
    }

    // monster crystal by the level, -1 = invalid level.
    private static int getCrystal(int level) {
        if (level < 31 || 200 < level) {
            return -1;
        }
        if (level <= 50) {
            return 4260000;
        }
        if (level >= 121) {
            return 4260008;
        }
        // 51-60 : 4260001, ..., 111-120 : 4260007
        return 4260001 + (level - 51) / 10;
    }

    private static short addStat(int base, Map<String, Byte> stats, String key) {
        Byte value = stats.get(key);
        return (short) ((value == null) ? base : base + value);
    }

    private static void addEnchantStats(Map<String, Byte> stats, Equip item) {
        item.setWatk(addStat(item.getWatk(), stats, "incPAD"));
        item.setMatk(addStat(item.getMatk(), stats, "incMAD"));
        item.setAcc(addStat(item.getAcc(), stats, "incACC"));
        item.setAvoid(addStat(item.getAvoid(), stats, "incEVA"));
        item.setSpeed(addStat(item.getSpeed(), stats, "incSpeed"));
        item.setJump(addStat(item.getJump(), stats, "incJump"));
        item.setHp(addStat(item.getHp(), stats, "incMaxHP"));
        item.setMp(addStat(item.getMp(), stats, "incMaxMP"));
        item.setStr(addStat(item.getStr(), stats, "incSTR"));
        item.setDex(addStat(item.getDex(), stats, "incDEX"));
        item.setInt(addStat(item.getInt(), stats, "incINT"));
        item.setLuk(addStat(item.getLuk(), stats, "incLUK"));
        Byte rand_option = stats.get("randOption");
        if (rand_option != null && rand_option > 0) {
            int delta = Randomizer.nextBoolean() ? rand_option : -rand_option;
            if (item.getWatk() > 0) {
                item.setWatk((short) (item.getWatk() + delta));
            }
            if (item.getMatk() > 0) {
                item.setMatk((short) (item.getMatk() + delta));
            }
        }
        Byte rand_stat = stats.get("randStat");
        if (rand_stat != null && rand_stat > 0) {
            int delta = Randomizer.nextBoolean() ? rand_stat : -rand_stat;
            if (item.getStr() > 0) {
                item.setStr((short) (item.getStr() + delta));
            }
            if (item.getDex() > 0) {
                item.setDex((short) (item.getDex() + delta));
            }
            if (item.getInt() > 0) {
                item.setInt((short) (item.getInt() + delta));
            }
            if (item.getLuk() > 0) {
                item.setLuk((short) (item.getLuk() + delta));
            }
        }
    }

    private static RandomReward getRandomReward(List<RandomReward> rewards) {
        int total = 0;
        for (RandomReward reward : rewards) {
            total += reward.prob;
        }
        if (total <= 0) {
            return null;
        }
        int rand = Randomizer.nextInt(total);
        for (RandomReward reward : rewards) {
            rand -= reward.prob;
            if (rand < 0) {
                return reward;
            }
        }
        return null;
    }

    private static boolean checkRequiredNRemove(TacosCharacter chr, List<SimpleImmutableEntry<Integer, Integer>> recipe) {
        for (SimpleImmutableEntry<Integer, Integer> p : recipe) {
            if (!chr.haveItem(p.getKey(), p.getValue(), false, true)) {
                return false;
            }
        }
        for (SimpleImmutableEntry<Integer, Integer> p : recipe) {
            int item_id = p.getKey();
            MapleInventoryManipulator.removeById(chr, GameConstants.getInventoryType(item_id), item_id, p.getValue(), false, false);
        }
        return true;
    }

    // Maker skill of each beginner job.
    private static boolean hasSkill(TacosCharacter chr, int reqlvl) {
        int skill_id;
        if (GameConstants.isKOC(chr.getJob())) {
            skill_id = 10001007;
        } else if (GameConstants.isAran(chr.getJob())) {
            skill_id = 20001007;
        } else if (GameConstants.isEvan(chr.getJob())) {
            skill_id = 20011007;
        } else if (GameConstants.isResist(chr.getJob())) {
            skill_id = 30001007;
        } else {
            skill_id = 1007;
        }
        return chr.getSkillLevel(WzXML.SKILL.getSkill().get(skill_id)) >= reqlvl;
    }
}
