/*
 * Copyright (C) 2026 Riremito
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */
package tacos.packet.request;

import java.util.AbstractMap;
import java.util.List;
import odin.client.inventory.Equip;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.ItemMakerFactory;
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
import tacos.wz.CharacterWz.EquipInfo;
import tacos.wz.ItemWz.ItemInfo;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class ReqCUser_ItemMaker {

    private static final int MONSTER_CRYSTAL_COST = 100; // monster etc items for a crystal

    // CUIItemMaker::RequestItemMake
    public static boolean OnUserItemMakeRequest(ClientPacket cp, TacosCharacter chr) {
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
                .player(chr)
                .maker(result)
                .build();
        chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_ItemMaker, pb));
        if (result == OpsItemMakerResult.ITEM_MAKER_RESULT_SUCCESS || result == OpsItemMakerResult.ITEM_MAKER_RESULT_DESTROYED) {
            chr.getMap().splitSendPacket(chr, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_ItemMaker, pb), chr.getId());
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
        int crystal_id = getCrystal(getMonsterCrystalLevel(etc_item_id));
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
        Equip to_use = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem((short) slot);
        if (to_use == null || to_use.getItemId() != item_id || to_use.getQuantity() < 1) {
            DebugLogger.ErrorLog("RECIPE_CLASS_EQUIP_DISASSEMBLE");
            return OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isDropRestricted(item_id) || ii.isAccountShared(item_id)) {
            return OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
        }
        EquipInfo ed = to_use.getInfo();
        int crystal_id = getCrystal(ed.getReqLevel());
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
        ItemMakerFactory.GemCreateEntry gem = ItemMakerFactory.getInstance().getGemInfo(target_item_id);
        if (gem == null || !hasSkill(chr, gem.getReqSkillLevel()) || chr.getMeso() < gem.getCost()) {
            DebugLogger.ErrorLog("makeGem : " + target_item_id);
            return OpsItemMakerResult.ITEM_MAKER_ERR_UNKNOWN;
        }
        ItemMakerFactory.RandomReward reward = getRandomReward(gem.getRandomReward());
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
        ItemMakerFactory.GemCreateEntry gem = ItemMakerFactory.getInstance().getGemInfo(target_item_id);
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
            MapleInventoryManipulator.addbyItem(chr, MapleItemInformationProvider.getInstance().getDefaultEquipById(target_item_id));
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

        ItemMakerFactory.ItemMakerCreateEntry create = ItemMakerFactory.getInstance().getCreateInfo(target_item_id);
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
        Equip to_give = ii.getDefaultEquipById(target_item_id);
        // the stimulator is also used when enchanters are used.
        if ((catalyst || gems.length > 0) && chr.haveItem(create.getStimulator(), 1, false, true)) {
            to_give.getEnhancement().randomize();
            MapleInventoryManipulator.removeById(chr, MapleInventoryType.ETC, create.getStimulator(), 1, false, false);
        }
        for (int gem_id : gems) {
            if (!chr.haveItem(gem_id, 1, false, true)) {
                continue;
            }
            ItemInfo stats = WzXML.ITEM.getItemInfo(gem_id);
            if (stats == null || gem_id / 10000 != 425) {
                continue;
            }
            addEnchantStats(stats, to_give);
            MapleInventoryManipulator.removeById(chr, MapleInventoryType.ETC, gem_id, 1, false, false);
        }
        MapleInventoryManipulator.addbyItem(chr, to_give);
        return OpsItemMakerResult.ITEM_MAKER_RESULT_SUCCESS;
    }

    // level of the monster drop item (400xxxx), 0 = not a monster drop item.
    private static int getMonsterCrystalLevel(int etc_item_id) {
        if (etc_item_id / 10000 != 400) {
            return 0;
        }
        ItemInfo info = WzXML.ITEM.getItemInfo(etc_item_id);
        return (info == null) ? 0 : info.getLv();
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

    private static void addEnchantStats(ItemInfo stats, Equip item) {
        item.setPAD((short) (item.getPAD() + stats.getIncPAD()));
        item.setMAD((short) (item.getMAD() + stats.getIncMAD()));
        item.setACC((short) (item.getACC() + stats.getIncACC()));
        item.setEVA((short) (item.getEVA() + stats.getIncEVA()));
        item.setSpeed((short) (item.getSpeed() + stats.getIncSpeed()));
        item.setJump((short) (item.getJump() + stats.getIncJump()));
        item.setMaxHP((short) (item.getMaxHP() + stats.getIncMaxHP()));
        item.setMaxMP((short) (item.getMaxMP() + stats.getIncMaxMP()));
        item.setSTR((short) (item.getSTR() + stats.getIncSTR()));
        item.setDEX((short) (item.getDEX() + stats.getIncDEX()));
        item.setINT((short) (item.getINT() + stats.getIncINT()));
        item.setLUK((short) (item.getLUK() + stats.getIncLUK()));
        int rand_option = stats.getRandOption();
        if (rand_option > 0) {
            int delta = Randomizer.nextBoolean() ? rand_option : -rand_option;
            if (item.getPAD() > 0) {
                item.setPAD((short) (item.getPAD() + delta));
            }
            if (item.getMAD() > 0) {
                item.setMAD((short) (item.getMAD() + delta));
            }
        }
        int rand_stat = stats.getRandStat();
        if (rand_stat > 0) {
            int delta = Randomizer.nextBoolean() ? rand_stat : -rand_stat;
            if (item.getSTR() > 0) {
                item.setSTR((short) (item.getSTR() + delta));
            }
            if (item.getDEX() > 0) {
                item.setDEX((short) (item.getDEX() + delta));
            }
            if (item.getINT() > 0) {
                item.setINT((short) (item.getINT() + delta));
            }
            if (item.getLUK() > 0) {
                item.setLUK((short) (item.getLUK() + delta));
            }
        }
    }

    private static ItemMakerFactory.RandomReward getRandomReward(List<ItemMakerFactory.RandomReward> rewards) {
        int total = 0;
        for (ItemMakerFactory.RandomReward reward : rewards) {
            total += reward.prob;
        }
        if (total <= 0) {
            return null;
        }
        int rand = Randomizer.nextInt(total);
        for (ItemMakerFactory.RandomReward reward : rewards) {
            rand -= reward.prob;
            if (rand < 0) {
                return reward;
            }
        }
        return null;
    }

    private static boolean checkRequiredNRemove(TacosCharacter chr, List<AbstractMap.SimpleImmutableEntry<Integer, Integer>> recipe) {
        for (AbstractMap.SimpleImmutableEntry<Integer, Integer> p : recipe) {
            if (!chr.haveItem(p.getKey(), p.getValue(), false, true)) {
                return false;
            }
        }
        for (AbstractMap.SimpleImmutableEntry<Integer, Integer> p : recipe) {
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
