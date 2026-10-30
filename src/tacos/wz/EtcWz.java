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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import tacos.config.ContentCustom;
import tacos.debug.DebugLogger;
import tacos.packet.ops.OpsCommodity;
import odin.server.CashItemInfo;
import odin.server.ItemMakerFactory.GemCreateEntry;
import odin.server.ItemMakerFactory.ItemMakerCreateEntry;
import java.util.Map;
import lombok.Getter;

/**
 *
 * @author Riremito
 */
public class EtcWz extends WzXML {

    public EtcWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Etc" : "Etc.wz");
    }

    private MapleData getForbiddenName() {
        return getData("ForbiddenName.img");
    }

    private MapleData getNpcLocation() {
        return getData("NpcLocation.img");
    }

    // NpcLocation.img/npc_id
    public List<Integer> getNpcLocationMapIds(int npc_id) {
        MapleData md_npc_location = getNpcLocation();
        if (md_npc_location == null) {
            return null;
        }
        MapleData md_npc = md_npc_location.getChildByPath(Integer.toString(npc_id));
        if (md_npc == null) {
            return null;
        }
        List<Integer> ret = new ArrayList<>();
        for (MapleData md_map : md_npc) {
            ret.add(md_npc.getInt(md_map.getName()));
        }
        return ret;
    }

    private MapleData getItemMake() {
        return getData("ItemMake.img");
    }

    public ArrayList<Integer> getMakerItems(int maker_type) {
        ArrayList<Integer> ret = new ArrayList<>();
        // ItemMake.img
        MapleData item_make_img = getItemMake();
        if (item_make_img == null) {
            return ret;
        }
        // ItemMake.img/0 1, 2, 4, 8, 16
        MapleData md_maker_job = item_make_img.getChildByPath(String.valueOf(maker_type));
        if (md_maker_job == null) {
            return ret;
        }
        for (MapleData data : md_maker_job.getChildren()) {
            ret.add(Integer.valueOf(data.getName()));
        }
        return ret;
    }

    @Getter
    public class MakerItemInfo {

        private int reqLevel; // character level.
        private int reqSkillLevel; // maker skill level.
        private int reqItem; // quest item.
        private int meso;
        private ArrayList<MakerRecipe> recipe;
        private int tuc; // number of upgrade items you can add.
        private int catalyst;
        private int itemNum;
        private ArrayList<MakerRandomReward> randomReward;
    }

    @Getter
    public class MakerRecipe {

        private int count;
        private int item; // item id.
    }

    @Getter
    public class MakerRandomReward {

        private int item;
        private int itemNum;
        private int prob;
    }

    private final HashMap<Integer, MakerItemInfo> maker_item_info_list = new HashMap<>();

    public MakerItemInfo getMakerItem(int maker_type, int item_id) {
        MakerItemInfo ret = this.maker_item_info_list.get(item_id);
        if (ret != null) {
            return ret;
        }
        MapleData item_make_img = getItemMake();
        if (item_make_img == null) {
            return ret;
        }
        MapleData md_item = item_make_img.getChildByPath(String.format("%d/%08d", maker_type, item_id));
        if (md_item == null) {
            return ret;
        }
        ret = new MakerItemInfo();
        ret.reqLevel = md_item.getInt("reqLevel");
        ret.reqSkillLevel = md_item.getInt("reqSkillLevel");
        ret.reqItem = md_item.getInt("reqItem");
        ret.meso = md_item.getInt("meso");
        ret.recipe = new ArrayList<>();
        MapleData md_recipe = md_item.getChildByPath("recipe");
        if (md_recipe != null) {
            for (MapleData data : md_recipe.getChildren()) {
                MakerRecipe mr = new MakerRecipe();
                mr.count = data.getInt("count");
                mr.item = data.getInt("item");
                ret.recipe.add(mr);
            }
        }
        ret.tuc = md_item.getInt("tuc");
        ret.catalyst = md_item.getInt("catalyst");
        ret.itemNum = md_item.getInt("itemNum");
        ret.randomReward = new ArrayList<>();
        MapleData md_randomReward = md_item.getChildByPath("randomReward");
        if (md_randomReward != null) {
            for (MapleData data : md_randomReward.getChildren()) {
                MakerRandomReward mrr = new MakerRandomReward();
                mrr.item = data.getInt("item");
                mrr.itemNum = data.getInt("itemNum");
                mrr.prob = data.getInt("prob");
                ret.randomReward.add(mrr);
            }
        }

        this.maker_item_info_list.put(item_id, ret);
        return ret;
    }

    public void loadItemMake(Map<Integer, GemCreateEntry> gemCache, Map<Integer, ItemMakerCreateEntry> createCache) {
        // 0 = Item upgrade crystals
        // 1 / 2/ 4/ 8 = Item creation

        if (getItemMake() == null) {
            return;
        }

        byte totalupgrades, reqMakerLevel;
        int reqLevel, cost, quantity, stimulator;
        GemCreateEntry ret;
        ItemMakerCreateEntry imt;

        for (MapleData dataType : getItemMake().getChildren()) {
            int type = Integer.parseInt(dataType.getName());
            switch (type) {
                case 0: { // Caching of gem
                    for (MapleData itemFolder : dataType.getChildren()) {
                        reqLevel = itemFolder.getInt("reqLevel");
                        reqMakerLevel = (byte) itemFolder.getInt("reqSkillLevel");
                        cost = itemFolder.getInt("meso");
                        quantity = itemFolder.getInt("itemNum");
//			totalupgrades = MapleDataTool.getInt("tuc", itemFolder, 0); // Gem is always 0

                        ret = new GemCreateEntry(cost, reqLevel, reqMakerLevel, quantity);

                        for (MapleData rewardNRecipe : itemFolder.getChildren()) {
                            for (MapleData ind : rewardNRecipe.getChildren()) {
                                if (rewardNRecipe.getName().equals("randomReward")) {
                                    ret.addRandomReward(ind.getInt("item"), ind.getInt("itemNum", 1), ind.getInt("prob"));
                                } else if (rewardNRecipe.getName().equals("recipe")) {
                                    ret.addReqRecipe(ind.getInt("item"), ind.getInt("count"));
                                }
                            }
                        }
                        gemCache.put(Integer.valueOf(itemFolder.getName()), ret);
                    }
                    break;
                }
                case 1: // Warrior
                case 2: // Magician
                case 4: // Bowman
                case 8: // Thief
                case 16: { // Pirate
                    for (MapleData itemFolder : dataType.getChildren()) {
                        reqLevel = itemFolder.getInt("reqLevel");
                        reqMakerLevel = (byte) itemFolder.getInt("reqSkillLevel");
                        cost = itemFolder.getInt("meso");
                        quantity = itemFolder.getInt("itemNum");
                        totalupgrades = (byte) itemFolder.getInt("tuc");
                        stimulator = itemFolder.getInt("catalyst");

                        imt = new ItemMakerCreateEntry(cost, reqLevel, reqMakerLevel, quantity, totalupgrades, stimulator);

                        for (MapleData Recipe : itemFolder.getChildren()) {
                            for (MapleData ind : Recipe.getChildren()) {
                                if (Recipe.getName().equals("recipe")) {
                                    imt.addReqItem(ind.getInt("item"), ind.getInt("count"));
                                }
                            }
                        }
                        createCache.put(Integer.valueOf(itemFolder.getName()), imt);
                    }
                    break;
                }
            }
        }
    }

    private MapleData getCommodity() {
        return getData("Commodity.img");
    }

    private CashItemInfo parseCommodityField(MapleData field) {
        int SN = field.getInt("SN");
        int ItemId = field.getInt("ItemId");

        return new CashItemInfo(ItemId,
                field.getInt("Count", 1),
                field.getInt("Price"),
                SN,
                field.getInt("Period"),
                field.getInt("Gender", 2),
                field.getInt("OnSale") > 0);
    }

    public CashItemInfo findCommodityBySN(int item_SN) {
        for (MapleData field : getCommodity().getChildren()) {
            int SN = field.getInt("SN");
            if (SN <= 0 || item_SN != SN) {
                continue;
            }
            return parseCommodityField(field);
        }
        return null;
    }

    public CashItemInfo findCommodityByItemId(int itemid) {
        for (MapleData field : getCommodity().getChildren()) {
            int ItemId = field.getInt("ItemId");
            if (ItemId != itemid) {
                continue;
            }
            return parseCommodityField(field);
        }
        return null;
    }

    private MapleData getCashPackage() {
        return getData("CashPackage.img");
    }

    private MapleData getSetItemInfo() {
        return getData("SetItemInfo.img");
    }

    private List<String> list_fn = null;

    private List<String> getFN() {
        if (list_fn != null) {
            return list_fn;
        }

        list_fn = new ArrayList<>();
        MapleData md_forbidden_name = getForbiddenName();
        for (MapleData data : md_forbidden_name.getChildren()) {
            list_fn.add(md_forbidden_name.getString(data.getName()));
        }

        return list_fn;
    }

    public boolean isForbiddenName(String character_name) {
        for (String forbidden_name : getFN()) {
            if (character_name.contains(forbidden_name)) {
                return true;
            }
        }
        return false;
    }

    public static class CS_COMMODITY {

        public int nSN = 0;
        public int nItemId = 0;
        public int nCount = 0;
        public int nPrice = 0;
        public int bBonus = 0;
        public int nPriority = 0;
        public int nPeriod = 0;
        public int nReqPOP = 0;
        public int nReqLEV = 0;
        public int nMaplePoint = 0;
        public int nMeso = 0;
        public int bForPremiumUser = 0;
        public int nReqLev = 0;
        public int nCommodityGender = 0;
        public int bOnSale = 0;
        public int nClass = 0;
        public int nLimit = 0;
        public int nPbCash = 0;
        public int nPbPoint = 0;
        public int nPbGift = 0;
        public int nDiscountRate = 0;
        public ArrayList<Integer> aPackageSN = new ArrayList<>();
        public int aOriginalSN = 0;
        public int nOriginalPrice = 0;
        public int dwModifiedFlag = 0;
    }

    private static ArrayList<CS_COMMODITY> ONSALE_LIST = null;

    public static ArrayList<CS_COMMODITY> getOnSale() {
        if (ONSALE_LIST != null) {
            return ONSALE_LIST;
        }

        ONSALE_LIST = new ArrayList<>();
        if (!ContentCustom.CC_REMOVE_ALL_CASHITEM.get()) {
            return ONSALE_LIST;
        }
        // remove all onsale items.
        for (MapleData field : WzXML.ETC.getCommodity().getChildren()) {
            int nItemId = field.getInt("ItemId");
            int nSN = field.getInt("SN");
            int bOnSale = field.getInt("OnSale");
            /*
            if (nItemId / 1000000 == 1) {
                continue;
            }
             */
            if (bOnSale != 0) {
                CS_COMMODITY onsale = new CS_COMMODITY();
                onsale.nSN = nSN;
                onsale.nItemId = nItemId;
                onsale.bOnSale = bOnSale;
                // overwrite test.
                onsale.bOnSale = 0;
                onsale.dwModifiedFlag = OpsCommodity.CM_ONSALE.get();
                ONSALE_LIST.add(onsale);
            }
        }

        DebugLogger.DebugLog("CS_COMMODITY : getOnSale = " + ONSALE_LIST.size());
        return ONSALE_LIST;
    }
}
