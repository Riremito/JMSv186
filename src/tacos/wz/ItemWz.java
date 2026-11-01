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
import lombok.Getter;

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

    private MapleData getItemImg(int item_sub_type) {
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

    private MapleData getItemData_Pet(int id) {
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

    private MapleData getItemOption() {
        return getData("ItemOption.img");
    }

    private boolean loadItemOpton() {
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

    public PetCommand getPetCommand(int petId, int skillId) {
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

    private int getHunger(int petId) {
        if (map_petHunger == null) {
            map_petHunger = new HashMap<>();
        }
        Integer found = map_petHunger.get(petId);
        if (found != null) {
            return found;
        }

        Integer ret = getData("Pet/" + petId + ".img").getInt("info/hungry", 1);
        map_petHunger.put(petId, ret);
        return ret;
    }

    // item ids of one item sub type (the children of Item.wz/xxxx/xxxx.img)
    public List<Integer> getItemIds(int item_sub_type) {
        List<Integer> ids = new ArrayList<>();
        MapleData md_item_sub_type = getItemImg(item_sub_type);
        if (md_item_sub_type != null) {
            for (MapleData md_item : md_item_sub_type.getChildren()) {
                ids.add(Integer.valueOf(md_item.getName()));
            }
        }
        return ids;
    }

    private MapleData getResolvedItemData(int id) {
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

    // Consume/0238.img, [card item id, info/mob]
    public LinkedHashMap<Integer, Integer> getMonsterCardMobIds() {
        MapleData md_cards = getItemImg(238);
        if (md_cards == null) {
            return null;
        }
        LinkedHashMap<Integer, Integer> ret = new LinkedHashMap<>();
        for (MapleData md_card : md_cards.getChildren()) {
            ret.put(Integer.parseInt(md_card.getName()), md_card.getInt("info/mob"));
        }
        return ret;
    }

    // info/spec
    public class ItemSpecInfo extends StatEffectData {
    }

    public ItemSpecInfo getItemSpec(int itemId) {
        MapleData item = getResolvedItemData(itemId);
        if (item == null) {
            return null;
        }
        return loadItemSpec(item.getChildByPath("spec"));
    }

    private ItemSpecInfo loadItemSpec(MapleData source) {
        if (source == null) {
            return null;
        }
        ItemSpecInfo ret = new ItemSpecInfo();
        ret.time = source.getInt("time", -1);
        ret.hp = source.getInt("hp");
        ret.hpR = source.getInt("hpR");
        ret.mp = source.getInt("mp");
        ret.mpR = source.getInt("mpR");
        ret.mhpR = source.getInt("mhpR");
        ret.mmpR = source.getInt("mmpR");
        ret.mpCon = source.getInt("mpCon");
        ret.hpCon = source.getInt("hpCon");
        ret.prop = source.getInt("prop", 100);
        ret.cooltime = source.getInt("cooltime");
        ret.expinc = source.getInt("expinc");
        ret.morph = source.getInt("morph");
        ret.cp = source.getInt("cp");
        ret.nuffSkill = source.getInt("nuffSkill");
        ret.mobCount = source.getInt("mobCount", 1);
        ret.exp = source.getInt("exp");
        ret.mastery = source.getInt("mastery");
        ret.pad = source.getInt("pad");
        ret.pdd = source.getInt("pdd");
        ret.mad = source.getInt("mad");
        ret.mdd = source.getInt("mdd");
        ret.emhp = source.getInt("emhp");
        ret.emmp = source.getInt("emmp");
        ret.epad = source.getInt("epad");
        ret.epdd = source.getInt("epdd");
        ret.emdd = source.getInt("emdd");
        ret.acc = source.getInt("acc");
        ret.eva = source.getInt("eva");
        ret.speed = source.getInt("speed");
        ret.jump = source.getInt("jump");
        ret.expBuff = source.getInt("expBuff");
        ret.cashBuff = source.getInt("cashBuff");
        ret.itemupbyitem = source.getInt("itemupbyitem");
        ret.mesoupbyitem = source.getInt("mesoupbyitem");
        ret.berserk = source.getInt("berserk");
        ret.berserk2 = source.getInt("berserk2");
        ret.illusion = source.getInt("illusion");
        if (source.getChildByPath("lt") != null) {
            ret.lt = source.getPoint("lt");
            ret.rb = source.getPoint("rb");
        }
        ret.x = source.getInt("x");
        ret.y = source.getInt("y");
        ret.z = source.getInt("z");
        ret.damage = source.getInt("damage");
        ret.attackCount = source.getInt("attackCount", 1);
        ret.bulletCount = source.getInt("bulletCount", 1);
        ret.bulletConsume = source.getInt("bulletConsume");
        ret.moneyCon = source.getInt("moneyCon");
        ret.itemCon = source.getInt("itemCon");
        ret.itemConNo = source.getInt("itemConNo");
        ret.moveTo = source.getInt("moveTo", -1);
        return ret;
    }

    public Map<Integer, Map<String, Integer>> loadEquipIncrements(int itemId) {
        Map<Integer, Map<String, Integer>> ret = new LinkedHashMap<>();
        MapleData item = getResolvedItemData(itemId);
        if (item == null) {
            return null;
        }
        MapleData info = item.getChildByPath("info/level/info");
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
            ret.put(Integer.valueOf(dat.getName()), incs);
        }
        return ret;
    }

    public Map<Integer, List<Integer>> loadEquipSkills(int itemId) {
        Map<Integer, List<Integer>> ret = new LinkedHashMap<>();
        MapleData item = getResolvedItemData(itemId);
        if (item == null) {
            return null;
        }
        MapleData info = item.getChildByPath("info/level/case");
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

    @Getter
    public class ScrollData {

        private int success;
        private int incSTR;
        private int incDEX;
        private int incINT;
        private int incLUK;
        private int incMaxHP;
        private int incMaxMP;
        private int incPAD;
        private int incMAD;
        private int incPDD;
        private int incMDD;
        private int incACC;
        private int incEVA;
        private int incCraft;
        private int incSpeed;
        private int incJump;
        private int preventslip;
        private int warmsupport;
        private int cursed;
        private int recover;
        private int randstat;
        private int incRandVol;
        private int noNegative;
        private int forceUpgrade;
        private int reset;
        private int notExtend;
        private HashMap<Integer, Integer> successRates;
        private HashMap<Integer, Integer> cursedRates;
        private HashMap<Integer, Integer> req;
        private int incattackSpeed;
    }

    private final HashMap<Integer, ScrollData> scroll_data_list = new HashMap<>();

    public ScrollData getScrollData(int item_id) {
        ScrollData ret = this.scroll_data_list.get(item_id);
        if (ret == null) {
            MapleData md = getItemData(item_id);
            if (md != null) {
                MapleData md_info = md.getChildByPath("info");
                if (md_info != null) {
                    ret = new ScrollData();
                    ret.success = md_info.getInt("success");
                    ret.incSTR = md_info.getInt("incSTR");
                    ret.incDEX = md_info.getInt("incDEX");
                    ret.incINT = md_info.getInt("incINT");
                    ret.incLUK = md_info.getInt("incLUK");
                    ret.incMaxHP = md_info.getInt("incMaxHP");
                    ret.incMaxMP = md_info.getInt("incMaxMP");
                    ret.incPAD = md_info.getInt("incPAD");
                    ret.incMAD = md_info.getInt("incMAD");
                    ret.incPDD = md_info.getInt("incPDD");
                    ret.incMDD = md_info.getInt("incMDD");
                    ret.incACC = md_info.getInt("incACC");
                    ret.incEVA = md_info.getInt("incEVA");
                    ret.incCraft = md_info.getInt("incCraft");
                    ret.incSpeed = md_info.getInt("incSpeed");
                    ret.incJump = md_info.getInt("incJump");
                    ret.preventslip = md_info.getInt("preventslip"); // 2040727, スパイクの書
                    ret.warmsupport = md_info.getInt("warmsupport"); // 2041058, 防寒の書
                    ret.cursed = md_info.getInt("cursed");
                    ret.recover = md_info.getInt("recover"); // 2049000, 白の書
                    ret.randstat = md_info.getInt("randstat"); // 2049100, 混沌の書
                    ret.incRandVol = md_info.getInt("incRandVol"); // 2049116, 驚きの混沌の書
                    ret.noNegative = md_info.getInt("noNegative"); // 2049122, 肯定の混沌の書
                    ret.forceUpgrade = md_info.getInt("forceUpgrade"); // 2049304, 3星装備強化の書
                    ret.reset = md_info.getInt("reset"); // 2049600
                    ret.notExtend = md_info.getInt("notExtend"); // 2049402
                    ret.successRates = new HashMap<>();
                    MapleData md_successRates = md_info.getChildByPath("successRates"); // 2047000
                    if (md_successRates != null) {
                        for (MapleData data : md_successRates.getChildren()) {
                            ret.successRates.put(Integer.valueOf(data.getName()), (Integer) data.getData());
                        }
                    }
                    ret.cursedRates = new HashMap<>();
                    MapleData md_cursedRates = md_info.getChildByPath("cursedRates"); // 2047000
                    if (md_cursedRates != null) {
                        for (MapleData data : md_cursedRates.getChildren()) {
                            ret.cursedRates.put(Integer.valueOf(data.getName()), (Integer) data.getData());
                        }
                    }
                    ret.req = new HashMap<>();
                    MapleData md_req = md_info.getChildByPath("req"); // 2044909
                    if (md_req != null) {
                        for (MapleData data : md_req.getChildren()) {
                            ret.req.put(Integer.valueOf(data.getName()), (Integer) data.getData());
                        }
                    }
                    ret.incattackSpeed = md_info.getInt("incattackSpeed");// 2044709, 攻撃速度の書
                }
            }
        }
        this.scroll_data_list.put(item_id, ret);
        return ret;
    }

    @Getter
    public class ItemInfo {

        private int slotMax;
        private int price;
        private double unitPrice;
        private int cash;
        private int only;
        private int quest;
        private int tradeBlock;
        private int accountSharable;
        private int pickUpBlock;
        private int notSale;
        private int consumeOnPickup;
        private int stateChangeItem;
        private int meso;
        private int mesomax;
        private int mesomin;
        private int mesostdev;
        private int dama;
        private int inc;
        private int lv;
        private int mob;
        private int incPAD;
        private int incMAD;
        private int incACC;
        private int incEVA;
        private int incSpeed;
        private int incJump;
        private int incMaxHP;
        private int incMaxMP;
        private int incSTR;
        private int incINT;
        private int incLUK;
        private int incDEX;
        private int randOption;
        private int randStat;
        private int masterLevel;
        private int reqSkillLevel;
        private int success;
        private ArrayList<Integer> skill;
        private int questId;
        private ArrayList<Integer> consumeItem;
        private ArrayList<Integer> req;
        private ArrayList<SimpleImmutableEntry<Integer, Integer>> summonMob;
    }

    private final HashMap<Integer, ItemInfo> item_info_list = new HashMap<>();

    public ItemInfo getItemInfo(int item_id) {
        if (this.item_info_list.containsKey(item_id)) {
            return this.item_info_list.get(item_id);
        }
        ItemInfo ret = null;
        MapleData md = getItemData(item_id);
        if (md != null) {
            ret = new ItemInfo();
            ret.slotMax = md.getInt("info/slotMax", 100);
            ret.price = md.getInt("info/price", -1);
            if (md.getChildByPath("info/unitPrice") != null) {
                ret.unitPrice = md.getDouble("info/unitPrice", 1.0);
            } else {
                ret.unitPrice = ret.price;
            }
            if (item_id == 2070019 || item_id == 2330007) {
                ret.unitPrice = 1.0;
            }
            ret.cash = md.getInt("info/cash");
            ret.only = md.getInt("info/only");
            ret.quest = md.getInt("info/quest");
            ret.tradeBlock = md.getInt("info/tradeBlock");
            ret.accountSharable = md.getInt("info/accountSharable");
            ret.pickUpBlock = md.getInt("info/pickUpBlock");
            ret.notSale = md.getInt("info/notSale");
            // 0 = not, 1 = consume on pickup, 2 = consume + party
            ret.consumeOnPickup = md.getInt("spec/consumeOnPickup");
            if (ret.consumeOnPickup == 0) {
                ret.consumeOnPickup = md.getInt("specEx/consumeOnPickup");
            }
            if (ret.consumeOnPickup == 1 && md.getInt("spec/party") > 0) {
                ret.consumeOnPickup = 2;
            }
            ret.stateChangeItem = md.getInt("info/stateChangeItem");
            ret.meso = md.getInt("info/meso"); // 5200000, メル袋
            ret.mesomax = md.getInt("info/mesomax"); // 5202000, ランダムメル袋
            ret.mesomin = md.getInt("info/mesomin");
            ret.mesostdev = md.getInt("info/mesostdev");
            ret.dama = md.getInt("info/dama"); // 5201000, パチンコ玉
            ret.inc = md.getInt("spec/inc"); // pet food
            ret.lv = md.getInt("info/lv"); // 400xxxx, monster crystal level
            ret.mob = md.getInt("info/mob"); // 238xxxx, monster card
            // 425xxxx, item maker gem
            ret.incPAD = md.getInt("info/incPAD");
            ret.incMAD = md.getInt("info/incMAD");
            ret.incACC = md.getInt("info/incACC");
            ret.incEVA = md.getInt("info/incEVA");
            ret.incSpeed = md.getInt("info/incSpeed");
            ret.incJump = md.getInt("info/incJump");
            ret.incMaxHP = md.getInt("info/incMaxHP");
            ret.incMaxMP = md.getInt("info/incMaxMP");
            ret.incSTR = md.getInt("info/incSTR");
            ret.incINT = md.getInt("info/incINT");
            ret.incLUK = md.getInt("info/incLUK");
            ret.incDEX = md.getInt("info/incDEX");
            ret.randOption = md.getInt("info/randOption"); // Black Crystal Wa/MA
            ret.randStat = md.getInt("info/randStat"); // Dark Crystal - Str/Dex/int/Luk
            // 228xxxx, 229xxxx, 562xxxx, skill book and mastery book
            ret.masterLevel = md.getInt("info/masterLevel");
            ret.reqSkillLevel = md.getInt("info/reqSkillLevel");
            ret.success = md.getInt("info/success");
            ret.skill = new ArrayList<>();
            MapleData md_skill = md.getChildByPath("info/skill");
            if (md_skill != null) {
                for (int i = 0; i < md_skill.getChildren().size(); i++) {
                    ret.skill.add(md_skill.getInt(Integer.toString(i)));
                }
            }
            // 422xxxx, quest item
            ret.questId = md.getInt("info/questId");
            ret.consumeItem = new ArrayList<>();
            MapleData md_consume_item = md.getChildByPath("info/consumeItem");
            if (md_consume_item != null) {
                for (MapleData consume : md_consume_item) {
                    ret.consumeItem.add(md_consume_item.getInt(consume.getName()));
                }
            }
            // equip ids the scroll can be used on
            ret.req = new ArrayList<>();
            MapleData md_req = md.getChildByPath("req");
            if (md_req != null) {
                for (MapleData data : md_req.getChildren()) {
                    ret.req.add(md_req.getInt(data.getName()));
                }
            }
            // 210xxxx, summon sack, [mob id, prob]
            ret.summonMob = new ArrayList<>();
            MapleData md_mob = md.getChildByPath("mob");
            if (md_mob != null) {
                for (MapleData child : md_mob.getChildren()) {
                    ret.summonMob.add(new SimpleImmutableEntry<>(child.getInt("id"), child.getInt("prob")));
                }
            }
        }
        this.item_info_list.put(item_id, ret);
        return ret;
    }

    /**
     *
     * @author Danny (Leifde)
     */
    public static class PetCommand {

        private int petId;
        private int skillId;
        private int prob;
        private int inc;

        public PetCommand(int petId, int skillId, int prob, int inc) {
            this.petId = petId;
            this.skillId = skillId;
            this.prob = prob;
            this.inc = inc;
        }

        public int getSkillId() {
            return skillId;
        }

        public int getProbability() {
            return prob;
        }

        public int getIncrease() {
            return inc;
        }
    }
}
