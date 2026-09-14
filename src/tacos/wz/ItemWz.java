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
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.unofficial.PotentialOptimization;
import tacos.unofficial.PotentialOptimization.PotentialOptionData;
import java.util.LinkedHashMap;
import java.util.List;
import odin.constants.GameConstants;
import odin.client.inventory.MapleInventoryType;
import odin.server.StructRewardItem;

/**
 *
 * @author Riremito
 */
public class ItemWz extends WzXML {

    public ItemWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Item" : "Item.wz");
    }

    private static final int item_sub_type_pet = 500;

    public MapleData getItemData(int id) {
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

        for (MapleDataDirectoryEntry mdde : getRootDirectory().getSubDirectories()) {
            for (MapleDataEntity mdfe : mdde.getFiles()) {
                if (mdfe.getName().equals(target_img_name)) {
                    MapleData md_item_sub_type = getData(mdde.getName() + "/" + mdfe.getName());
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

    public MapleData getItemImg(int item_sub_type) {
        if (item_sub_type < 200) {
            return null;
        }

        // Pet
        if (item_sub_type == item_sub_type_pet) {
            return null;
        }

        String target_img_name = String.format("%04d.img", item_sub_type);

        for (MapleDataDirectoryEntry mdde : getRootDirectory().getSubDirectories()) {
            for (MapleDataEntity mdfe : mdde.getFiles()) {
                if (mdfe.getName().equals(target_img_name)) {
                    MapleData md_item_sub_type = getData(mdde.getName() + "/" + mdfe.getName());
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

    public MapleData getItemData_Pet(int id) {
        int item_sub_type = id / 10000;
        if (item_sub_type != item_sub_type_pet) {
            return null;
        }
        String target_img_name = String.format("%d.img", id);

        for (MapleDataDirectoryEntry mdde : getRootDirectory().getSubDirectories()) {
            if (mdde.getName().equals("Pet")) {
                for (MapleDataEntity mdfe : mdde.getFiles()) {
                    if (mdfe.getName().equals(target_img_name)) {
                        MapleData md_pet = getData(mdde.getName() + "/" + mdfe.getName());
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

    public MapleData getItemOption() {
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

        for (MapleData dat : getItemOption()) {
            ArrayList<PotentialOptionData> pods = new ArrayList<>();
            for (MapleData level : dat.getChildByPath("level")) {
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
    private Map<SimpleImmutableEntry<Integer, Integer>, PetCommand> map_petCommands = null;
    private Map<Integer, Integer> map_petHunger = null;

    public PetCommand getPetCommand(final int petId, final int skillId) {
        if (map_petCommands == null) {
            map_petCommands = new HashMap<>();
        }
        PetCommand pc_found = map_petCommands.get(new SimpleImmutableEntry<>(petId, skillId));
        if (pc_found != null) {
            return pc_found;
        }

        MapleData skillData = getData("Pet/" + petId + ".img");
        int prob = 0;
        int inc = 0;
        if (skillData != null) {
            prob = WzDataTool.getIntPath("interact/" + skillId + "/prob", skillData, 0);
            inc = WzDataTool.getIntPath("interact/" + skillId + "/inc", skillData, 0);
        }
        PetCommand ret = new PetCommand(petId, skillId, prob, inc);
        map_petCommands.put(new SimpleImmutableEntry<>(petId, skillId), ret);
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

        MapleData hungerData = getData("Pet/" + petId + ".img").getChildByPath("info/hungry");
        Integer ret = WzDataTool.getInt(hungerData, 1);
        map_petHunger.put(petId, ret);
        return ret;
    }

    public MapleData getResolvedItemData(int id) {
        MapleData md_character = WzXML.CHARACTER.getItemData(id);
        if (md_character != null) {
            return md_character;
        }

        MapleData md_item = getItemData(id);
        if (md_item != null) {
            return md_item;
        }

        DebugLogger.ErrorLog("getItemData : " + id);
        return null;
    }

    public short loadSlotMax(final int itemId) {
        short ret = 0;
        final MapleData item = getResolvedItemData(itemId);
        if (item != null) {
            final MapleData smEntry = item.getChildByPath("info/slotMax");
            if (smEntry == null) {
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    ret = 1;
                } else {
                    ret = 100;
                }
            } else {
                ret = (short) WzDataTool.getInt(smEntry);
            }
        }
        return ret;
    }

    public int loadWholePrice(final int itemId) {
        final MapleData item = getResolvedItemData(itemId);
        if (item == null) {
            return -1;
        }
        final MapleData pData = item.getChildByPath("info/price");
        if (pData == null) {
            return -1;
        }
        return WzDataTool.getInt(pData);
    }

    public double loadPrice(final int itemId) {
        final MapleData item = getResolvedItemData(itemId);
        if (item == null) {
            return -1;
        }
        Double pEntry;
        MapleData pData = item.getChildByPath("info/unitPrice");
        if (pData != null) {
            pEntry = WzDataTool.getDouble(pData, 1.0);
        } else {
            pData = item.getChildByPath("info/price");
            if (pData == null) {
                return -1;
            }
            pEntry = (double) WzDataTool.getInt(pData, 1);
        }
        if (itemId == 2070019 || itemId == 2330007) {
            pEntry = 1.0;
        }
        return pEntry;
    }

    public Map<String, Byte> loadItemMakeStats(final int itemId) {
        if (itemId / 10000 != 425) {
            return null;
        }
        final Map<String, Byte> ret = new LinkedHashMap<>();
        final MapleData item = getResolvedItemData(itemId);
        if (item == null) {
            return null;
        }
        final MapleData info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }
        ret.put("incPAD", (byte) WzDataTool.getIntPath("incPAD", info, 0)); // WATK
        ret.put("incMAD", (byte) WzDataTool.getIntPath("incMAD", info, 0)); // MATK
        ret.put("incACC", (byte) WzDataTool.getIntPath("incACC", info, 0)); // ACC
        ret.put("incEVA", (byte) WzDataTool.getIntPath("incEVA", info, 0)); // AVOID
        ret.put("incSpeed", (byte) WzDataTool.getIntPath("incSpeed", info, 0)); // SPEED
        ret.put("incJump", (byte) WzDataTool.getIntPath("incJump", info, 0)); // JUMP
        ret.put("incMaxHP", (byte) WzDataTool.getIntPath("incMaxHP", info, 0)); // HP
        ret.put("incMaxMP", (byte) WzDataTool.getIntPath("incMaxMP", info, 0)); // MP
        ret.put("incSTR", (byte) WzDataTool.getIntPath("incSTR", info, 0)); // STR
        ret.put("incINT", (byte) WzDataTool.getIntPath("incINT", info, 0)); // INT
        ret.put("incLUK", (byte) WzDataTool.getIntPath("incLUK", info, 0)); // LUK
        ret.put("incDEX", (byte) WzDataTool.getIntPath("incDEX", info, 0)); // DEX
//	ret.put("incReqLevel", MapleDataTool.getInt("incReqLevel", info, 0)); // IDK!
        ret.put("randOption", (byte) WzDataTool.getIntPath("randOption", info, 0)); // Black Crystal Wa/MA
        ret.put("randStat", (byte) WzDataTool.getIntPath("randStat", info, 0)); // Dark Crystal - Str/Dex/int/Luk

        return ret;
    }

    public Map<Integer, Map<String, Integer>> loadEquipIncrements(final int itemId) {
        final Map<Integer, Map<String, Integer>> ret = new LinkedHashMap<>();
        final MapleData item = getResolvedItemData(itemId);
        if (item == null) {
            return null;
        }
        final MapleData info = item.getChildByPath("info/level/info");
        if (info == null) {
            return null;
        }
        for (MapleData dat : info.getChildren()) {
            Map<String, Integer> incs = new HashMap<>();
            for (MapleData data : dat.getChildren()) { //why we have to do this? check if number has skills or not
                if (data.getName().length() > 3) {
                    incs.put(data.getName().substring(3), WzDataTool.getIntPath(data.getName(), dat, 0));
                }
            }
            ret.put(Integer.parseInt(dat.getName()), incs);
        }
        return ret;
    }

    public Map<Integer, List<Integer>> loadEquipSkills(final int itemId) {
        final Map<Integer, List<Integer>> ret = new LinkedHashMap<>();
        final MapleData item = getResolvedItemData(itemId);
        if (item == null) {
            return null;
        }
        final MapleData info = item.getChildByPath("info/level/case");
        if (info == null) {
            return null;
        }
        for (MapleData dat : info.getChildren()) {
            for (MapleData data : dat.getChildren()) { //why we have to do this? check if number has skills or not
                if (data.getName().length() == 1) { //the numbers all them are one digit. everything else isnt so we're lucky here..
                    List<Integer> adds = new ArrayList<>();
                    for (MapleData skil : data.getChildByPath("Skill").getChildren()) {
                        adds.add(WzDataTool.getIntPath("id", skil, 0));
                    }
                    ret.put(Integer.valueOf(data.getName()), adds);
                }
            }
        }
        return ret;
    }

    public Map<String, Integer> loadEquipStats(final int itemId) {
        final Map<String, Integer> ret = new LinkedHashMap<>();
        final MapleData item = getResolvedItemData(itemId);
        if (item == null) {
            return null;
        }
        final MapleData info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }
        for (final MapleData data : info.getChildren()) {
            if (data.getName().startsWith("inc")) {
                ret.put(data.getName().substring(3), WzDataTool.getInt(data, 0));
            }
        }
        ret.put("tuc", WzDataTool.getIntPath("tuc", info, 0));
        ret.put("reqLevel", WzDataTool.getIntPath("reqLevel", info, 0));
        ret.put("reqJob", WzDataTool.getIntPath("reqJob", info, 0));
        ret.put("reqSTR", WzDataTool.getIntPath("reqSTR", info, 0));
        ret.put("reqDEX", WzDataTool.getIntPath("reqDEX", info, 0));
        ret.put("reqINT", WzDataTool.getIntPath("reqINT", info, 0));
        ret.put("reqLUK", WzDataTool.getIntPath("reqLUK", info, 0));
        ret.put("reqPOP", WzDataTool.getIntPath("reqPOP", info, 0));
        ret.put("cash", WzDataTool.getIntPath("cash", info, 0));
        ret.put("canLevel", info.getChildByPath("level") == null ? 0 : 1);
        ret.put("cursed", WzDataTool.getIntPath("cursed", info, 0));
        ret.put("success", WzDataTool.getIntPath("success", info, 0));
        ret.put("setItemID", WzDataTool.getIntPath("setItemID", info, 0));
        ret.put("equipTradeBlock", WzDataTool.getIntPath("equipTradeBlock", info, 0));
        ret.put("durability", WzDataTool.getIntPath("durability", info, -1));

        if (GameConstants.isMagicWeapon(itemId)) {
            ret.put("elemDefault", WzDataTool.getIntPath("elemDefault", info, 100));
            ret.put("incRMAS", WzDataTool.getIntPath("incRMAS", info, 100)); // Poison
            ret.put("incRMAF", WzDataTool.getIntPath("incRMAF", info, 100)); // Fire
            ret.put("incRMAL", WzDataTool.getIntPath("incRMAL", info, 100)); // Lightning
            ret.put("incRMAI", WzDataTool.getIntPath("incRMAI", info, 100)); // Ice
        }

        return ret;
    }

    public List<Integer> loadScrollReqs(final int itemId) {
        final List<Integer> ret = new ArrayList<>();
        final MapleData data = getResolvedItemData(itemId).getChildByPath("req");

        if (data == null) {
            return ret;
        }
        for (final MapleData req : data.getChildren()) {
            ret.add(WzDataTool.getInt(req));
        }
        return ret;
    }

    public List<SimpleImmutableEntry<Integer, Integer>> loadSummonMobs(final int itemId) {
        if (!GameConstants.isSummonSack(itemId)) {
            return null;
        }
        final MapleData data = getResolvedItemData(itemId).getChildByPath("mob");
        if (data == null) {
            return null;
        }
        final List<SimpleImmutableEntry<Integer, Integer>> mobPairs = new ArrayList<>();

        for (final MapleData child : data.getChildren()) {
            mobPairs.add(new SimpleImmutableEntry<>(
                    WzDataTool.getIntPath("id", child, 0),
                    WzDataTool.getIntPath("prob", child, 0)));
        }
        return mobPairs;
    }

    public int loadCardMobId(final int id) {
        MapleData data = getResolvedItemData(id);
        return WzDataTool.getIntPath("info/mob", data, 0);
    }

    public short loadItemMakeLevel(final int itemId) {
        if (itemId / 10000 != 400) {
            return 0;
        }
        return (short) WzDataTool.getIntPath("info/lv", getResolvedItemData(itemId), 0);
    }

    public byte loadConsumeOnPickup(final int itemId) {
        // 0 = not, 1 = consume on pickup, 2 = consume + party
        final MapleData data = getResolvedItemData(itemId);
        byte consume = (byte) WzDataTool.getIntPath("spec/consumeOnPickup", data, 0);
        if (consume == 0) {
            consume = (byte) WzDataTool.getIntPath("specEx/consumeOnPickup", data, 0);
        }
        if (consume == 1) {
            if (WzDataTool.getIntPath("spec/party", getResolvedItemData(itemId), 0) > 0) {
                consume = 2;
            }
        }
        return consume;
    }

    public boolean loadDropRestricted(final int itemId) {
        final MapleData data = getResolvedItemData(itemId);

        return WzDataTool.getIntPath("info/tradeBlock", data, 0) == 1 || WzDataTool.getIntPath("info/quest", data, 0) == 1;
    }

    public boolean loadPickupRestricted(final int itemId) {
        return WzDataTool.getIntPath("info/only", getResolvedItemData(itemId), 0) == 1;
    }

    public boolean loadAccountShared(final int itemId) {
        return WzDataTool.getIntPath("info/accountSharable", getResolvedItemData(itemId), 0) == 1;
    }

    public int loadStateChangeItem(final int itemId) {
        return WzDataTool.getIntPath("info/stateChangeItem", getResolvedItemData(itemId), 0);
    }

    public int loadMeso(final int itemId) {
        return WzDataTool.getIntPath("info/meso", getResolvedItemData(itemId), 0);
    }

    // info/damaとか
    public int loadIntField(final int itemId, final String text) {
        return WzDataTool.getIntPath(text, getResolvedItemData(itemId), 0);
    }

    public boolean loadPickupBlocked(final int itemId) {
        return WzDataTool.getIntPath("info/pickUpBlock", getResolvedItemData(itemId), 0) == 1;
    }

    public boolean loadCantSell(final int itemId) { //true = cant sell, false = can sell
        return WzDataTool.getIntPath("info/notSale", getResolvedItemData(itemId), 0) == 1;
    }

    public SimpleImmutableEntry<Integer, List<StructRewardItem>> loadRewardItem(final int itemid) {
        final MapleData data = getResolvedItemData(itemid);
        if (data == null) {
            return null;
        }
        final MapleData rewards = data.getChildByPath("reward");
        if (rewards == null) {
            return null;
        }
        int totalprob = 0; // As there are some rewards with prob above 2000, we can't assume it's always 100
        List<StructRewardItem> all = new ArrayList<>();

        for (final MapleData reward : rewards) {
            StructRewardItem struct = new StructRewardItem();

            struct.itemid = WzDataTool.getIntPath("item", reward, 0);
            struct.prob = (byte) WzDataTool.getIntPath("prob", reward, 0);
            struct.quantity = (short) WzDataTool.getIntPath("count", reward, 0);
            struct.effect = WzDataTool.getStringPath("Effect", reward, "");
            struct.worldmsg = WzDataTool.getStringPath("worldMsg", reward, null);
            struct.period = WzDataTool.getIntPath("period", reward, -1);

            totalprob += struct.prob;

            all.add(struct);
        }
        return new SimpleImmutableEntry<>(totalprob, all);
    }

    public Map<String, Integer> loadSkillStats(final int itemId) {
        if (!(itemId / 10000 == 228 || itemId / 10000 == 229 || itemId / 10000 == 562)) { // Skillbook and mastery book
            return null;
        }
        final MapleData item = getResolvedItemData(itemId);
        if (item == null) {
            return null;
        }
        final MapleData info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }
        final Map<String, Integer> ret = new LinkedHashMap<>();
        for (final MapleData data : info.getChildren()) {
            if (data.getName().startsWith("inc")) {
                ret.put(data.getName().substring(3), WzDataTool.getInt(data, 0));
            }
        }
        ret.put("masterLevel", WzDataTool.getIntPath("masterLevel", info, 0));
        ret.put("reqSkillLevel", WzDataTool.getIntPath("reqSkillLevel", info, 0));
        ret.put("success", WzDataTool.getIntPath("success", info, 0));

        final MapleData skill = info.getChildByPath("skill");

        for (int i = 0; i < skill.getChildren().size(); i++) { // List of allowed skillIds
            ret.put("skillid" + i, WzDataTool.getIntPath(Integer.toString(i), skill, 0));
        }
        return ret;
    }

    public SimpleImmutableEntry<Integer, List<Integer>> loadQuestItemInfo(final int itemId) {
        if (itemId / 10000 != 422 || getResolvedItemData(itemId) == null) {
            return null;
        }
        final MapleData itemD = getResolvedItemData(itemId).getChildByPath("info");
        if (itemD == null || itemD.getChildByPath("consumeItem") == null) {
            return null;
        }
        final List<Integer> consumeItems = new ArrayList<>();
        for (MapleData consume : itemD.getChildByPath("consumeItem")) {
            consumeItems.add(WzDataTool.getInt(consume, 0));
        }
        return new SimpleImmutableEntry<>(WzDataTool.getIntPath("questId", itemD, 0), consumeItems);
    }

}
