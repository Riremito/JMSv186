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

import tacos.wz.WzXML;
import odin.client.inventory.PetCommand;
import tacos.config.Content;
import tacos.debug.DebugLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import odin.server.StructPotentialItem;
import tacos.odin.OdinPair;
import odin.provider.IMapleData;
import odin.provider.IMapleDataDirectoryEntry;
import odin.provider.IMapleDataEntity;
import tacos.wz.TacosWzDataTool;

/**
 *
 * @author Riremito
 */
public class ItemWz extends WzXML {

    public ItemWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Item" : "Item.wz");
    }

    private static final int item_sub_type_pet = 500;

    public IMapleData getItemData(int id) {
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

        for (IMapleDataDirectoryEntry mdde : getRootDirectory().getSubDirectories()) {
            for (IMapleDataEntity mdfe : mdde.getFiles()) {
                if (mdfe.getName().equals(target_img_name)) {
                    IMapleData md_item_sub_type = getData(mdde.getName() + "/" + mdfe.getName());
                    if (md_item_sub_type == null) {
                        DebugLogger.ErrorLog("getItemData : Invalid item type = " + item_sub_type);
                        return null;
                    }
                    IMapleData md_item = md_item_sub_type.getChildByPath(target_dir_name);
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

    public IMapleData getItemImg(int item_sub_type) {
        if (item_sub_type < 200) {
            return null;
        }

        // Pet
        if (item_sub_type == item_sub_type_pet) {
            return null;
        }

        String target_img_name = String.format("%04d.img", item_sub_type);

        for (IMapleDataDirectoryEntry mdde : getRootDirectory().getSubDirectories()) {
            for (IMapleDataEntity mdfe : mdde.getFiles()) {
                if (mdfe.getName().equals(target_img_name)) {
                    IMapleData md_item_sub_type = getData(mdde.getName() + "/" + mdfe.getName());
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

    public IMapleData getItemData_Pet(int id) {
        int item_sub_type = id / 10000;
        if (item_sub_type != item_sub_type_pet) {
            return null;
        }
        String target_img_name = String.format("%d.img", id);

        for (IMapleDataDirectoryEntry mdde : getRootDirectory().getSubDirectories()) {
            if (mdde.getName().equals("Pet")) {
                for (IMapleDataEntity mdfe : mdde.getFiles()) {
                    if (mdfe.getName().equals(target_img_name)) {
                        IMapleData md_pet = getData(mdde.getName() + "/" + mdfe.getName());
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

    Map<Integer, List<StructPotentialItem>> map_ItemOption = null;
    ArrayList<Integer> list_RarePotential = null;
    ArrayList<Integer> list_EpicPotential = null;
    ArrayList<Integer> list_UniquePotential = null;
    ArrayList<Integer> list_LegendaryPotential = null;

    public IMapleData getItemOption() {
        return getData("ItemOption.img");
    }

    public Map<Integer, List<StructPotentialItem>> getItemOptionList() {
        if (map_ItemOption != null) {
            return map_ItemOption;
        }
        map_ItemOption = new HashMap<>();
        list_RarePotential = new ArrayList<>();
        list_EpicPotential = new ArrayList<>();
        list_UniquePotential = new ArrayList<>();
        list_LegendaryPotential = new ArrayList<>();

        if (getItemOption() == null) {
            return map_ItemOption;
        }

        for (IMapleData dat : getItemOption()) {
            List<StructPotentialItem> items = new LinkedList<>();
            for (IMapleData level : dat.getChildByPath("level")) {
                StructPotentialItem item = new StructPotentialItem();
                item.optionType = TacosWzDataTool.getIntPath("info/optionType", dat, 0);
                item.reqLevel = TacosWzDataTool.getIntPath("info/reqLevel", dat, 0);
                item.face = TacosWzDataTool.getStringPath("face", level, "");
                item.boss = TacosWzDataTool.getIntPath("boss", level, 0) > 0;
                item.potentialID = Integer.parseInt(dat.getName());
                item.attackType = (short) TacosWzDataTool.getIntPath("attackType", level, 0);
                item.incMHP = (short) TacosWzDataTool.getIntPath("incMHP", level, 0);
                item.incMMP = (short) TacosWzDataTool.getIntPath("incMMP", level, 0);

                item.incSTR = (byte) TacosWzDataTool.getIntPath("incSTR", level, 0);
                item.incDEX = (byte) TacosWzDataTool.getIntPath("incDEX", level, 0);
                item.incINT = (byte) TacosWzDataTool.getIntPath("incINT", level, 0);
                item.incLUK = (byte) TacosWzDataTool.getIntPath("incLUK", level, 0);
                item.incACC = (byte) TacosWzDataTool.getIntPath("incACC", level, 0);
                item.incEVA = (byte) TacosWzDataTool.getIntPath("incEVA", level, 0);
                item.incSpeed = (byte) TacosWzDataTool.getIntPath("incSpeed", level, 0);
                item.incJump = (byte) TacosWzDataTool.getIntPath("incJump", level, 0);
                item.incPAD = (byte) TacosWzDataTool.getIntPath("incPAD", level, 0);
                item.incMAD = (byte) TacosWzDataTool.getIntPath("incMAD", level, 0);
                item.incPDD = (byte) TacosWzDataTool.getIntPath("incPDD", level, 0);
                item.incMDD = (byte) TacosWzDataTool.getIntPath("incMDD", level, 0);
                item.prop = (byte) TacosWzDataTool.getIntPath("prop", level, 0);
                item.time = (byte) TacosWzDataTool.getIntPath("time", level, 0);
                item.incSTRr = (byte) TacosWzDataTool.getIntPath("incSTRr", level, 0);
                item.incDEXr = (byte) TacosWzDataTool.getIntPath("incDEXr", level, 0);
                item.incINTr = (byte) TacosWzDataTool.getIntPath("incINTr", level, 0);
                item.incLUKr = (byte) TacosWzDataTool.getIntPath("incLUKr", level, 0);
                item.incMHPr = (byte) TacosWzDataTool.getIntPath("incMHPr", level, 0);
                item.incMMPr = (byte) TacosWzDataTool.getIntPath("incMMPr", level, 0);
                item.incACCr = (byte) TacosWzDataTool.getIntPath("incACCr", level, 0);
                item.incEVAr = (byte) TacosWzDataTool.getIntPath("incEVAr", level, 0);
                item.incPADr = (byte) TacosWzDataTool.getIntPath("incPADr", level, 0);
                item.incMADr = (byte) TacosWzDataTool.getIntPath("incMADr", level, 0);
                item.incPDDr = (byte) TacosWzDataTool.getIntPath("incPDDr", level, 0);
                item.incMDDr = (byte) TacosWzDataTool.getIntPath("incMDDr", level, 0);
                item.incCr = (byte) TacosWzDataTool.getIntPath("incCr", level, 0);
                item.incDAMr = (byte) TacosWzDataTool.getIntPath("incDAMr", level, 0);
                item.RecoveryHP = (byte) TacosWzDataTool.getIntPath("RecoveryHP", level, 0);
                item.RecoveryMP = (byte) TacosWzDataTool.getIntPath("RecoveryMP", level, 0);
                item.HP = (byte) TacosWzDataTool.getIntPath("HP", level, 0);
                item.MP = (byte) TacosWzDataTool.getIntPath("MP", level, 0);
                item.level = (byte) TacosWzDataTool.getIntPath("level", level, 0);
                item.ignoreTargetDEF = (byte) TacosWzDataTool.getIntPath("ignoreTargetDEF", level, 0);
                item.ignoreDAM = (byte) TacosWzDataTool.getIntPath("ignoreDAM", level, 0);
                item.DAMreflect = (byte) TacosWzDataTool.getIntPath("DAMreflect", level, 0);
                item.mpconReduce = (byte) TacosWzDataTool.getIntPath("mpconReduce", level, 0);
                item.mpRestore = (byte) TacosWzDataTool.getIntPath("mpRestore", level, 0);
                item.incMesoProp = (byte) TacosWzDataTool.getIntPath("incMesoProp", level, 0);
                item.incRewardProp = (byte) TacosWzDataTool.getIntPath("incRewardProp", level, 0);
                item.incAllskill = (byte) TacosWzDataTool.getIntPath("incAllskill", level, 0);
                item.ignoreDAMr = (byte) TacosWzDataTool.getIntPath("ignoreDAMr", level, 0);
                item.RecoveryUP = (byte) TacosWzDataTool.getIntPath("RecoveryUP", level, 0);
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

    public ArrayList<Integer> getRarePotential() {
        if (list_RarePotential != null) {
            return list_RarePotential;
        }
        getItemOptionList();
        return list_RarePotential;
    }

    public ArrayList<Integer> getEpicPotential() {
        if (list_EpicPotential != null) {
            return list_EpicPotential;
        }
        getItemOptionList();
        return list_EpicPotential;
    }

    public ArrayList<Integer> getUniquePotential() {
        if (list_UniquePotential != null) {
            return list_UniquePotential;
        }
        getItemOptionList();
        return list_UniquePotential;
    }

    public ArrayList<Integer> getLegendaryPotential() {
        if (list_LegendaryPotential != null) {
            return list_LegendaryPotential;
        }
        getItemOptionList();
        return list_LegendaryPotential;
    }

    // Pet
    private Map<OdinPair<Integer, Integer>, PetCommand> map_petCommands = null;
    private Map<Integer, Integer> map_petHunger = null;

    public PetCommand getPetCommand(final int petId, final int skillId) {
        if (map_petCommands == null) {
            map_petCommands = new HashMap<>();
        }
        PetCommand pc_found = map_petCommands.get(new OdinPair<>(petId, skillId));
        if (pc_found != null) {
            return pc_found;
        }

        IMapleData skillData = getData("Pet/" + petId + ".img");
        int prob = 0;
        int inc = 0;
        if (skillData != null) {
            prob = TacosWzDataTool.getIntPath("interact/" + skillId + "/prob", skillData, 0);
            inc = TacosWzDataTool.getIntPath("interact/" + skillId + "/inc", skillData, 0);
        }
        PetCommand ret = new PetCommand(petId, skillId, prob, inc);
        map_petCommands.put(new OdinPair<>(petId, skillId), ret);
        return ret;
    }

    public int getHunger(final int petId) {
        if (map_petHunger == null) {
            map_petHunger = new HashMap<>();
        }
        Integer found = map_petHunger.get(petId);
        if (found != null) {
            return found;
        }

        IMapleData hungerData = getData("Pet/" + petId + ".img").getChildByPath("info/hungry");
        Integer ret = TacosWzDataTool.getInt(hungerData, 1);
        map_petHunger.put(petId, ret);
        return ret;
    }

}
