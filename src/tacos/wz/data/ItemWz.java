/*
 * Copyright (C) 2025 Riremito
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
package tacos.wz.data;

import tacos.wz.Wz;
import odin.client.inventory.PetCommand;
import tacos.config.Content;
import tacos.debug.DebugLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import odin.provider.MapleData;
import odin.provider.MapleDataDirectoryEntry;
import odin.provider.MapleDataFileEntry;
import odin.provider.MapleDataProvider;
import odin.provider.MapleDataTool;
import odin.server.StructPotentialItem;
import odin.tools.Pair;

/**
 *
 * @author Riremito
 */
public class ItemWz {

    private static Wz wz = null;

    private static Wz getWz() {
        if (wz == null) {
            wz = new Wz(Content.Wz_SingleFile.get() ? "Data.wz/Item" : "Item.wz");
        }
        return wz;
    }

    public static MapleDataProvider getWzRoot() {
        return getWz().getWzRoot();
    }

    private static final int item_sub_type_pet = 500;

    public static MapleData getItemData(int id) {
        int item_type = id / 1000000;
        if (item_type <= 1) {
            return null;
        }
        int item_sub_type = id / 10000;

        // Pet
        if (item_sub_type == item_sub_type_pet) {
            return getItemData_Pet(id);
        }

        String target_img_name = String.format("%04d.img", item_sub_type);
        String target_dir_name = String.format("%08d", id);

        for (MapleDataDirectoryEntry mdde : getWzRoot().getRoot().getSubdirectories()) {
            for (MapleDataFileEntry mdfe : mdde.getFiles()) {
                if (mdfe.getName().equals(target_img_name)) {
                    MapleData md_item_sub_type = getWz().loadData(mdde.getName() + "/" + mdfe.getName());
                    if (md_item_sub_type == null) {
                        DebugLogger.ErrorLog("getItemData : Invalid item type = " + item_sub_type);
                        return null;
                    }
                    MapleData md_item = md_item_sub_type.getChildByPath(target_dir_name);
                    if (md_item == null) {
                        DebugLogger.ErrorLog("getItemData : Invalid item id = " + id);
                        return null;
                    }
                    return md_item;
                }
            }
        }

        DebugLogger.ErrorLog("getItemData : err item id " + id);
        return null;
    }

    public static MapleData getItemImg(int item_sub_type) {
        if (item_sub_type < 200) {
            return null;
        }

        // Pet
        if (item_sub_type == item_sub_type_pet) {
            return null;
        }

        String target_img_name = String.format("%04d.img", item_sub_type);

        for (MapleDataDirectoryEntry mdde : getWzRoot().getRoot().getSubdirectories()) {
            for (MapleDataFileEntry mdfe : mdde.getFiles()) {
                if (mdfe.getName().equals(target_img_name)) {
                    MapleData md_item_sub_type = getWz().loadData(mdde.getName() + "/" + mdfe.getName());
                    if (md_item_sub_type == null) {
                        DebugLogger.ErrorLog("getItemImg : Invalid item type = " + item_sub_type);
                        return null;
                    }
                    return md_item_sub_type;
                }
            }
        }

        DebugLogger.ErrorLog("getItemImg : err item_sub_type " + item_sub_type);
        return null;
    }

    public static MapleData getItemData_Pet(int id) {
        int item_sub_type = id / 10000;
        if (item_sub_type != item_sub_type_pet) {
            return null;
        }
        String target_img_name = String.format("%d.img", id);

        for (MapleDataDirectoryEntry mdde : getWzRoot().getRoot().getSubdirectories()) {
            if (mdde.getName().equals("Pet")) {
                for (MapleDataFileEntry mdfe : mdde.getFiles()) {
                    if (mdfe.getName().equals(target_img_name)) {
                        MapleData md_pet = getWz().loadData(mdde.getName() + "/" + mdfe.getName());
                        if (md_pet == null) {
                            DebugLogger.ErrorLog("getItemData_Pet : Invalid pet id 1 = " + id);
                            return null;
                        }
                        return md_pet;
                    }
                }
                DebugLogger.ErrorLog("getItemData_Pet : Invalid pet id 2 = " + id);
                return null;
            }
        }
        DebugLogger.ErrorLog("getItemData_Pet : Invalid pet id 3 = " + id);
        return null;
    }

    private static MapleData img_ItemOption = null;
    private static Map<Integer, List<StructPotentialItem>> map_ItemOption = null;
    private static ArrayList<Integer> list_RarePotential = null;
    private static ArrayList<Integer> list_EpicPotential = null;
    private static ArrayList<Integer> list_UniquePotential = null;
    private static ArrayList<Integer> list_LegendaryPotential = null;

    public static MapleData getItemOption() {
        if (img_ItemOption == null) {
            img_ItemOption = getWz().loadData("ItemOption.img");
        }
        return img_ItemOption;
    }

    public static Map<Integer, List<StructPotentialItem>> getItemOptionList() {
        if (map_ItemOption != null) {
            return map_ItemOption;
        }
        map_ItemOption = new HashMap<>();
        list_RarePotential = new ArrayList<Integer>();
        list_EpicPotential = new ArrayList<Integer>();
        list_UniquePotential = new ArrayList<Integer>();
        list_LegendaryPotential = new ArrayList<Integer>();

        if (getItemOption() == null) {
            return map_ItemOption;
        }

        for (MapleData dat : getItemOption()) {
            List<StructPotentialItem> items = new LinkedList<StructPotentialItem>();
            for (MapleData level : dat.getChildByPath("level")) {
                StructPotentialItem item = new StructPotentialItem();
                item.optionType = MapleDataTool.getIntConvert("info/optionType", dat, 0);
                item.reqLevel = MapleDataTool.getIntConvert("info/reqLevel", dat, 0);
                item.face = MapleDataTool.getString("face", level, "");
                item.boss = MapleDataTool.getIntConvert("boss", level, 0) > 0;
                item.potentialID = Integer.parseInt(dat.getName());
                item.attackType = (short) MapleDataTool.getIntConvert("attackType", level, 0);
                item.incMHP = (short) MapleDataTool.getIntConvert("incMHP", level, 0);
                item.incMMP = (short) MapleDataTool.getIntConvert("incMMP", level, 0);

                item.incSTR = (byte) MapleDataTool.getIntConvert("incSTR", level, 0);
                item.incDEX = (byte) MapleDataTool.getIntConvert("incDEX", level, 0);
                item.incINT = (byte) MapleDataTool.getIntConvert("incINT", level, 0);
                item.incLUK = (byte) MapleDataTool.getIntConvert("incLUK", level, 0);
                item.incACC = (byte) MapleDataTool.getIntConvert("incACC", level, 0);
                item.incEVA = (byte) MapleDataTool.getIntConvert("incEVA", level, 0);
                item.incSpeed = (byte) MapleDataTool.getIntConvert("incSpeed", level, 0);
                item.incJump = (byte) MapleDataTool.getIntConvert("incJump", level, 0);
                item.incPAD = (byte) MapleDataTool.getIntConvert("incPAD", level, 0);
                item.incMAD = (byte) MapleDataTool.getIntConvert("incMAD", level, 0);
                item.incPDD = (byte) MapleDataTool.getIntConvert("incPDD", level, 0);
                item.incMDD = (byte) MapleDataTool.getIntConvert("incMDD", level, 0);
                item.prop = (byte) MapleDataTool.getIntConvert("prop", level, 0);
                item.time = (byte) MapleDataTool.getIntConvert("time", level, 0);
                item.incSTRr = (byte) MapleDataTool.getIntConvert("incSTRr", level, 0);
                item.incDEXr = (byte) MapleDataTool.getIntConvert("incDEXr", level, 0);
                item.incINTr = (byte) MapleDataTool.getIntConvert("incINTr", level, 0);
                item.incLUKr = (byte) MapleDataTool.getIntConvert("incLUKr", level, 0);
                item.incMHPr = (byte) MapleDataTool.getIntConvert("incMHPr", level, 0);
                item.incMMPr = (byte) MapleDataTool.getIntConvert("incMMPr", level, 0);
                item.incACCr = (byte) MapleDataTool.getIntConvert("incACCr", level, 0);
                item.incEVAr = (byte) MapleDataTool.getIntConvert("incEVAr", level, 0);
                item.incPADr = (byte) MapleDataTool.getIntConvert("incPADr", level, 0);
                item.incMADr = (byte) MapleDataTool.getIntConvert("incMADr", level, 0);
                item.incPDDr = (byte) MapleDataTool.getIntConvert("incPDDr", level, 0);
                item.incMDDr = (byte) MapleDataTool.getIntConvert("incMDDr", level, 0);
                item.incCr = (byte) MapleDataTool.getIntConvert("incCr", level, 0);
                item.incDAMr = (byte) MapleDataTool.getIntConvert("incDAMr", level, 0);
                item.RecoveryHP = (byte) MapleDataTool.getIntConvert("RecoveryHP", level, 0);
                item.RecoveryMP = (byte) MapleDataTool.getIntConvert("RecoveryMP", level, 0);
                item.HP = (byte) MapleDataTool.getIntConvert("HP", level, 0);
                item.MP = (byte) MapleDataTool.getIntConvert("MP", level, 0);
                item.level = (byte) MapleDataTool.getIntConvert("level", level, 0);
                item.ignoreTargetDEF = (byte) MapleDataTool.getIntConvert("ignoreTargetDEF", level, 0);
                item.ignoreDAM = (byte) MapleDataTool.getIntConvert("ignoreDAM", level, 0);
                item.DAMreflect = (byte) MapleDataTool.getIntConvert("DAMreflect", level, 0);
                item.mpconReduce = (byte) MapleDataTool.getIntConvert("mpconReduce", level, 0);
                item.mpRestore = (byte) MapleDataTool.getIntConvert("mpRestore", level, 0);
                item.incMesoProp = (byte) MapleDataTool.getIntConvert("incMesoProp", level, 0);
                item.incRewardProp = (byte) MapleDataTool.getIntConvert("incRewardProp", level, 0);
                item.incAllskill = (byte) MapleDataTool.getIntConvert("incAllskill", level, 0);
                item.ignoreDAMr = (byte) MapleDataTool.getIntConvert("ignoreDAMr", level, 0);
                item.RecoveryUP = (byte) MapleDataTool.getIntConvert("RecoveryUP", level, 0);
                switch (item.potentialID) {
                    case 31001:
                    case 31002:
                    case 31003:
                    case 31004:
                        item.skillID = item.potentialID - 23001;
                        break;
                    default:
                        item.skillID = 0;
                        break;
                }
                items.add(item);
            }
            map_ItemOption.put(Integer.parseInt(dat.getName()), items);

            // 不要な潜在削除
            if (/*Version.GreaterOrEqual(Region.JMS, 302)*/true) {
                StructPotentialItem ci = items.get(0);
                if (ci.incSTRr == 0 && ci.incDEXr == 0 && ci.incINTr == 0 && ci.incLUKr == 0
                        && ci.incMHPr == 0 && ci.incMMPr == 0
                        && ci.incPADr == 0 && ci.incMADr == 0) {
                    continue;
                }
            }

            int potential_id = Integer.parseInt(dat.getName());
            switch (potential_id / 10000) {
                case 1: {
                    list_RarePotential.add(potential_id);
                    break;
                }
                case 2: {
                    list_EpicPotential.add(potential_id);
                    break;
                }
                case 3: {
                    list_UniquePotential.add(potential_id);
                    break;
                }
                case 4: {
                    list_LegendaryPotential.add(potential_id);
                    break;
                }
                default: {
                    //Debug.ErrorLog("invalid rank potential : " + potential_id);
                    break;
                }
            }
        }

        return map_ItemOption;
    }

    public static ArrayList<Integer> getRarePotential() {
        if (list_RarePotential != null) {
            return list_RarePotential;
        }
        getItemOptionList();
        return list_RarePotential;
    }

    public static ArrayList<Integer> getEpicPotential() {
        if (list_EpicPotential != null) {
            return list_EpicPotential;
        }
        getItemOptionList();
        return list_EpicPotential;
    }

    public static ArrayList<Integer> getUniquePotential() {
        if (list_UniquePotential != null) {
            return list_UniquePotential;
        }
        getItemOptionList();
        return list_UniquePotential;
    }

    public static ArrayList<Integer> getLegendaryPotential() {
        if (list_LegendaryPotential != null) {
            return list_LegendaryPotential;
        }
        getItemOptionList();
        return list_LegendaryPotential;
    }

    // Pet
    private static Map<Pair<Integer, Integer>, PetCommand> map_petCommands = null;
    private static Map<Integer, Integer> map_petHunger = null;

    public static PetCommand getPetCommand(final int petId, final int skillId) {
        if (map_petCommands == null) {
            map_petCommands = new HashMap<>();
        }
        PetCommand pc_found = map_petCommands.get(new Pair<>(petId, skillId));
        if (pc_found != null) {
            return pc_found;
        }

        MapleData skillData = getWz().loadData("Pet/" + petId + ".img");
        int prob = 0;
        int inc = 0;
        if (skillData != null) {
            prob = MapleDataTool.getInt("interact/" + skillId + "/prob", skillData, 0);
            inc = MapleDataTool.getInt("interact/" + skillId + "/inc", skillData, 0);
        }
        PetCommand ret = new PetCommand(petId, skillId, prob, inc);
        map_petCommands.put(new Pair<>(petId, skillId), ret);
        return ret;
    }

    public static int getHunger(final int petId) {
        if (map_petHunger == null) {
            map_petHunger = new HashMap<>();
        }
        Integer found = map_petHunger.get(petId);
        if (found != null) {
            return found;
        }

        MapleData hungerData = getWz().loadData("Pet/" + petId + ".img").getChildByPath("info/hungry");
        Integer ret = MapleDataTool.getInt(hungerData, 1);
        map_petHunger.put(petId, ret);
        return ret;
    }

}
