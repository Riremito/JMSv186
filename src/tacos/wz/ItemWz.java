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

                pod.optionType = dat.getInt("info/optionType");
                pod.reqLevel = dat.getInt("info/reqLevel");
                pod.face = level.getString("face");
                pod.boss = level.getInt("boss") > 0;
                pod.potentialID = Integer.parseInt(dat.getName());
                pod.attackType = (short) level.getInt("attackType");
                pod.incMHP = (short) level.getInt("incMHP");
                pod.incMMP = (short) level.getInt("incMMP");
                pod.incSTR = (byte) level.getInt("incSTR");
                pod.incDEX = (byte) level.getInt("incDEX");
                pod.incINT = (byte) level.getInt("incINT");
                pod.incLUK = (byte) level.getInt("incLUK");
                pod.incACC = (byte) level.getInt("incACC");
                pod.incEVA = (byte) level.getInt("incEVA");
                pod.incSpeed = (byte) level.getInt("incSpeed");
                pod.incJump = (byte) level.getInt("incJump");
                pod.incPAD = (byte) level.getInt("incPAD");
                pod.incMAD = (byte) level.getInt("incMAD");
                pod.incPDD = (byte) level.getInt("incPDD");
                pod.incMDD = (byte) level.getInt("incMDD");
                pod.prop = (byte) level.getInt("prop");
                pod.time = (byte) level.getInt("time");
                pod.incSTRr = (byte) level.getInt("incSTRr");
                pod.incDEXr = (byte) level.getInt("incDEXr");
                pod.incINTr = (byte) level.getInt("incINTr");
                pod.incLUKr = (byte) level.getInt("incLUKr");
                pod.incMHPr = (byte) level.getInt("incMHPr");
                pod.incMMPr = (byte) level.getInt("incMMPr");
                pod.incACCr = (byte) level.getInt("incACCr");
                pod.incEVAr = (byte) level.getInt("incEVAr");
                pod.incPADr = (byte) level.getInt("incPADr");
                pod.incMADr = (byte) level.getInt("incMADr");
                pod.incPDDr = (byte) level.getInt("incPDDr");
                pod.incMDDr = (byte) level.getInt("incMDDr");
                pod.incCr = (byte) level.getInt("incCr");
                pod.incDAMr = (byte) level.getInt("incDAMr");
                pod.RecoveryHP = (byte) level.getInt("RecoveryHP");
                pod.RecoveryMP = (byte) level.getInt("RecoveryMP");
                pod.HP = (byte) level.getInt("HP");
                pod.MP = (byte) level.getInt("MP");
                pod.level = (byte) level.getInt("level");
                pod.ignoreTargetDEF = (byte) level.getInt("ignoreTargetDEF");
                pod.ignoreDAM = (byte) level.getInt("ignoreDAM");
                pod.DAMreflect = (byte) level.getInt("DAMreflect");
                pod.mpconReduce = (byte) level.getInt("mpconReduce");
                pod.mpRestore = (byte) level.getInt("mpRestore");
                pod.incMesoProp = (byte) level.getInt("incMesoProp");
                pod.incRewardProp = (byte) level.getInt("incRewardProp");
                pod.incAllskill = (byte) level.getInt("incAllskill");
                pod.ignoreDAMr = (byte) level.getInt("ignoreDAMr");
                pod.RecoveryUP = (byte) level.getInt("RecoveryUP");

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
            prob = skillData.getInt("interact/" + skillId + "/prob");
            inc = skillData.getInt("interact/" + skillId + "/inc");
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
        ret.put("incPAD", (byte) info.getInt("incPAD")); // WATK
        ret.put("incMAD", (byte) info.getInt("incMAD")); // MATK
        ret.put("incACC", (byte) info.getInt("incACC")); // ACC
        ret.put("incEVA", (byte) info.getInt("incEVA")); // AVOID
        ret.put("incSpeed", (byte) info.getInt("incSpeed")); // SPEED
        ret.put("incJump", (byte) info.getInt("incJump")); // JUMP
        ret.put("incMaxHP", (byte) info.getInt("incMaxHP")); // HP
        ret.put("incMaxMP", (byte) info.getInt("incMaxMP")); // MP
        ret.put("incSTR", (byte) info.getInt("incSTR")); // STR
        ret.put("incINT", (byte) info.getInt("incINT")); // INT
        ret.put("incLUK", (byte) info.getInt("incLUK")); // LUK
        ret.put("incDEX", (byte) info.getInt("incDEX")); // DEX
//	ret.put("incReqLevel", MapleDataTool.getInt("incReqLevel", info, 0)); // IDK!
        ret.put("randOption", (byte) info.getInt("randOption")); // Black Crystal Wa/MA
        ret.put("randStat", (byte) info.getInt("randStat")); // Dark Crystal - Str/Dex/int/Luk

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
                    incs.put(data.getName().substring(3), dat.getInt(data.getName()));
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
                        adds.add(skil.getInt("id"));
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
        ret.put("tuc", info.getInt("tuc"));
        ret.put("reqLevel", info.getInt("reqLevel"));
        ret.put("reqJob", info.getInt("reqJob"));
        ret.put("reqSTR", info.getInt("reqSTR"));
        ret.put("reqDEX", info.getInt("reqDEX"));
        ret.put("reqINT", info.getInt("reqINT"));
        ret.put("reqLUK", info.getInt("reqLUK"));
        ret.put("reqPOP", info.getInt("reqPOP"));
        ret.put("cash", info.getInt("cash"));
        ret.put("canLevel", info.getChildByPath("level") == null ? 0 : 1);
        ret.put("cursed", info.getInt("cursed"));
        ret.put("success", info.getInt("success"));
        ret.put("setItemID", info.getInt("setItemID"));
        ret.put("equipTradeBlock", info.getInt("equipTradeBlock"));
        ret.put("durability", info.getInt("durability", -1));

        if (GameConstants.isMagicWeapon(itemId)) {
            ret.put("elemDefault", info.getInt("elemDefault", 100));
            ret.put("incRMAS", info.getInt("incRMAS", 100)); // Poison
            ret.put("incRMAF", info.getInt("incRMAF", 100)); // Fire
            ret.put("incRMAL", info.getInt("incRMAL", 100)); // Lightning
            ret.put("incRMAI", info.getInt("incRMAI", 100)); // Ice
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
                    child.getInt("id"),
                    child.getInt("prob")));
        }
        return mobPairs;
    }

    public int loadCardMobId(final int id) {
        MapleData data = getResolvedItemData(id);
        return data.getInt("info/mob");
    }

    public short loadItemMakeLevel(final int itemId) {
        if (itemId / 10000 != 400) {
            return 0;
        }
        return (short) getResolvedItemData(itemId).getInt("info/lv");
    }

    public byte loadConsumeOnPickup(final int itemId) {
        // 0 = not, 1 = consume on pickup, 2 = consume + party
        final MapleData data = getResolvedItemData(itemId);
        byte consume = (byte) data.getInt("spec/consumeOnPickup");
        if (consume == 0) {
            consume = (byte) data.getInt("specEx/consumeOnPickup");
        }
        if (consume == 1) {
            if (getResolvedItemData(itemId).getInt("spec/party") > 0) {
                consume = 2;
            }
        }
        return consume;
    }

    public boolean loadDropRestricted(final int itemId) {
        final MapleData data = getResolvedItemData(itemId);

        return data.getInt("info/tradeBlock") == 1 || data.getInt("info/quest") == 1;
    }

    public boolean loadPickupRestricted(final int itemId) {
        return getResolvedItemData(itemId).getInt("info/only") == 1;
    }

    public boolean loadAccountShared(final int itemId) {
        return getResolvedItemData(itemId).getInt("info/accountSharable") == 1;
    }

    public int loadStateChangeItem(final int itemId) {
        return getResolvedItemData(itemId).getInt("info/stateChangeItem");
    }

    public int loadMeso(final int itemId) {
        return getResolvedItemData(itemId).getInt("info/meso");
    }

    // info/damaとか
    public int loadIntField(final int itemId, final String text) {
        return getResolvedItemData(itemId).getInt(text);
    }

    public boolean loadPickupBlocked(final int itemId) {
        return getResolvedItemData(itemId).getInt("info/pickUpBlock") == 1;
    }

    public boolean loadCantSell(final int itemId) { //true = cant sell, false = can sell
        return getResolvedItemData(itemId).getInt("info/notSale") == 1;
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

            struct.itemid = reward.getInt("item");
            struct.prob = (byte) reward.getInt("prob");
            struct.quantity = (short) reward.getInt("count");
            struct.effect = reward.getString("Effect");
            struct.worldmsg = reward.getString("worldMsg", null);
            struct.period = reward.getInt("period", -1);

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
        ret.put("masterLevel", info.getInt("masterLevel"));
        ret.put("reqSkillLevel", info.getInt("reqSkillLevel"));
        ret.put("success", info.getInt("success"));

        final MapleData skill = info.getChildByPath("skill");

        for (int i = 0; i < skill.getChildren().size(); i++) { // List of allowed skillIds
            ret.put("skillid" + i, skill.getInt(Integer.toString(i)));
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
        return new SimpleImmutableEntry<>(itemD.getInt("questId"), consumeItems);
    }

}
