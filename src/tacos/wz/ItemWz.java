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
package tacos.wz;

import odin.client.inventory.PetCommand;
import tacos.config.Content;
import tacos.debug.DebugLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import tacos.odin.OdinPair;
import odin.provider.IMapleData;
import odin.provider.IMapleDataDirectoryEntry;
import odin.provider.IMapleDataEntity;
import tacos.unofficial.PotentialOptimization;
import tacos.unofficial.PotentialOptimization.PotentialOptionData;

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

    ArrayList<Integer> list_RarePotential = null;
    ArrayList<Integer> list_EpicPotential = null;
    ArrayList<Integer> list_UniquePotential = null;
    ArrayList<Integer> list_LegendaryPotential = null;

    public IMapleData getItemOption() {
        return getData("ItemOption.img");
    }

    public boolean loadItemOpton() {
        if (this.list_RarePotential != null) {
            return true;
        }

        this.list_RarePotential = new ArrayList<>();
        this.list_EpicPotential = new ArrayList<>();
        this.list_UniquePotential = new ArrayList<>();
        this.list_LegendaryPotential = new ArrayList<>();

        if (getItemOption() == null) {
            return false;
        }

        for (IMapleData dat : getItemOption()) {
            ArrayList<PotentialOptionData> pods = new ArrayList<>();
            for (IMapleData level : dat.getChildByPath("level")) {
                PotentialOptionData pod = new PotentialOptionData();

                pod.optionType = WzDataTool.getIntPath("info/optionType", dat, 0);
                pod.reqLevel = WzDataTool.getIntPath("info/reqLevel", dat, 0);
                pod.face = WzDataTool.getStringPath("face", level, "");
                pod.boss = WzDataTool.getIntPath("boss", level, 0) > 0;
                pod.potentialID = Integer.parseInt(dat.getName());
                pod.attackType = (short) WzDataTool.getIntPath("attackType", level, 0);
                pod.incMHP = (short) WzDataTool.getIntPath("incMHP", level, 0);
                pod.incMMP = (short) WzDataTool.getIntPath("incMMP", level, 0);
                pod.incSTR = (byte) WzDataTool.getIntPath("incSTR", level, 0);
                pod.incDEX = (byte) WzDataTool.getIntPath("incDEX", level, 0);
                pod.incINT = (byte) WzDataTool.getIntPath("incINT", level, 0);
                pod.incLUK = (byte) WzDataTool.getIntPath("incLUK", level, 0);
                pod.incACC = (byte) WzDataTool.getIntPath("incACC", level, 0);
                pod.incEVA = (byte) WzDataTool.getIntPath("incEVA", level, 0);
                pod.incSpeed = (byte) WzDataTool.getIntPath("incSpeed", level, 0);
                pod.incJump = (byte) WzDataTool.getIntPath("incJump", level, 0);
                pod.incPAD = (byte) WzDataTool.getIntPath("incPAD", level, 0);
                pod.incMAD = (byte) WzDataTool.getIntPath("incMAD", level, 0);
                pod.incPDD = (byte) WzDataTool.getIntPath("incPDD", level, 0);
                pod.incMDD = (byte) WzDataTool.getIntPath("incMDD", level, 0);
                pod.prop = (byte) WzDataTool.getIntPath("prop", level, 0);
                pod.time = (byte) WzDataTool.getIntPath("time", level, 0);
                pod.incSTRr = (byte) WzDataTool.getIntPath("incSTRr", level, 0);
                pod.incDEXr = (byte) WzDataTool.getIntPath("incDEXr", level, 0);
                pod.incINTr = (byte) WzDataTool.getIntPath("incINTr", level, 0);
                pod.incLUKr = (byte) WzDataTool.getIntPath("incLUKr", level, 0);
                pod.incMHPr = (byte) WzDataTool.getIntPath("incMHPr", level, 0);
                pod.incMMPr = (byte) WzDataTool.getIntPath("incMMPr", level, 0);
                pod.incACCr = (byte) WzDataTool.getIntPath("incACCr", level, 0);
                pod.incEVAr = (byte) WzDataTool.getIntPath("incEVAr", level, 0);
                pod.incPADr = (byte) WzDataTool.getIntPath("incPADr", level, 0);
                pod.incMADr = (byte) WzDataTool.getIntPath("incMADr", level, 0);
                pod.incPDDr = (byte) WzDataTool.getIntPath("incPDDr", level, 0);
                pod.incMDDr = (byte) WzDataTool.getIntPath("incMDDr", level, 0);
                pod.incCr = (byte) WzDataTool.getIntPath("incCr", level, 0);
                pod.incDAMr = (byte) WzDataTool.getIntPath("incDAMr", level, 0);
                pod.RecoveryHP = (byte) WzDataTool.getIntPath("RecoveryHP", level, 0);
                pod.RecoveryMP = (byte) WzDataTool.getIntPath("RecoveryMP", level, 0);
                pod.HP = (byte) WzDataTool.getIntPath("HP", level, 0);
                pod.MP = (byte) WzDataTool.getIntPath("MP", level, 0);
                pod.level = (byte) WzDataTool.getIntPath("level", level, 0);
                pod.ignoreTargetDEF = (byte) WzDataTool.getIntPath("ignoreTargetDEF", level, 0);
                pod.ignoreDAM = (byte) WzDataTool.getIntPath("ignoreDAM", level, 0);
                pod.DAMreflect = (byte) WzDataTool.getIntPath("DAMreflect", level, 0);
                pod.mpconReduce = (byte) WzDataTool.getIntPath("mpconReduce", level, 0);
                pod.mpRestore = (byte) WzDataTool.getIntPath("mpRestore", level, 0);
                pod.incMesoProp = (byte) WzDataTool.getIntPath("incMesoProp", level, 0);
                pod.incRewardProp = (byte) WzDataTool.getIntPath("incRewardProp", level, 0);
                pod.incAllskill = (byte) WzDataTool.getIntPath("incAllskill", level, 0);
                pod.ignoreDAMr = (byte) WzDataTool.getIntPath("ignoreDAMr", level, 0);
                pod.RecoveryUP = (byte) WzDataTool.getIntPath("RecoveryUP", level, 0);

                switch (pod.potentialID) {
                    case 31001:
                    case 31002:
                    case 31003:
                    case 31004: {
                        pod.skillID = pod.potentialID - 23001;
                        break;
                    }
                    default: {
                        pod.skillID = 0;
                        break;
                    }
                }

                pods.add(pod);
            }

            // block adding weak potential options to potential list.
            if (PotentialOptimization.ignore(pods.get(0))) {
                continue;
            }

            int potential_id = Integer.parseInt(dat.getName());
            switch (potential_id / 10000) {
                case 1: {
                    this.list_RarePotential.add(potential_id);
                    break;
                }
                case 2: {
                    this.list_EpicPotential.add(potential_id);
                    break;
                }
                case 3: {
                    this.list_UniquePotential.add(potential_id);
                    break;
                }
                case 4: {
                    this.list_LegendaryPotential.add(potential_id);
                    break;
                }
                default: {
                    break;
                }
            }
        }

        return true;
    }

    public ArrayList<Integer> getRarePotential() {
        loadItemOpton();
        return this.list_RarePotential;
    }

    public ArrayList<Integer> getEpicPotential() {
        loadItemOpton();
        return this.list_EpicPotential;
    }

    public ArrayList<Integer> getUniquePotential() {
        loadItemOpton();
        return this.list_UniquePotential;
    }

    public ArrayList<Integer> getLegendaryPotential() {
        loadItemOpton();
        return this.list_LegendaryPotential;
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
            prob = WzDataTool.getIntPath("interact/" + skillId + "/prob", skillData, 0);
            inc = WzDataTool.getIntPath("interact/" + skillId + "/inc", skillData, 0);
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
        Integer ret = WzDataTool.getInt(hungerData, 1);
        map_petHunger.put(petId, ret);
        return ret;
    }
}
