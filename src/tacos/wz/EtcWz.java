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
import java.util.List;
import tacos.config.ContentCustom;
import tacos.debug.DebugLogger;
import tacos.packet.ops.OpsCommodity;
import odin.server.CashItemInfo;
import odin.server.ItemMakerFactory.GemCreateEntry;
import odin.server.ItemMakerFactory.ItemMakerCreateEntry;
import java.util.Map;

/**
 *
 * @author Riremito
 */
public class EtcWz extends WzXML {

    public EtcWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Etc" : "Etc.wz");
    }

    public MapleData getForbiddenName() {
        return getData("ForbiddenName.img");
    }

    public MapleData getNpcLocation() {
        return getData("NpcLocation.img");
    }

    public MapleData getItemMake() {
        return getData("ItemMake.img");
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
                        reqLevel = WzDataTool.getIntPath("reqLevel", itemFolder, 0);
                        reqMakerLevel = (byte) WzDataTool.getIntPath("reqSkillLevel", itemFolder, 0);
                        cost = WzDataTool.getIntPath("meso", itemFolder, 0);
                        quantity = WzDataTool.getIntPath("itemNum", itemFolder, 0);
//			totalupgrades = MapleDataTool.getInt("tuc", itemFolder, 0); // Gem is always 0

                        ret = new GemCreateEntry(cost, reqLevel, reqMakerLevel, quantity);

                        for (MapleData rewardNRecipe : itemFolder.getChildren()) {
                            for (MapleData ind : rewardNRecipe.getChildren()) {
                                if (rewardNRecipe.getName().equals("randomReward")) {
                                    ret.addRandomReward(WzDataTool.getIntPath("item", ind, 0), WzDataTool.getIntPath("prob", ind, 0));
// MapleDataTool.getInt("itemNum", ind, 0)
                                } else if (rewardNRecipe.getName().equals("recipe")) {
                                    ret.addReqRecipe(WzDataTool.getIntPath("item", ind, 0), WzDataTool.getIntPath("count", ind, 0));
                                }
                            }
                        }
                        gemCache.put(Integer.parseInt(itemFolder.getName()), ret);
                    }
                    break;
                }
                case 1: // Warrior
                case 2: // Magician
                case 4: // Bowman
                case 8: // Thief
                case 16: { // Pirate
                    for (MapleData itemFolder : dataType.getChildren()) {
                        reqLevel = WzDataTool.getIntPath("reqLevel", itemFolder, 0);
                        reqMakerLevel = (byte) WzDataTool.getIntPath("reqSkillLevel", itemFolder, 0);
                        cost = WzDataTool.getIntPath("meso", itemFolder, 0);
                        quantity = WzDataTool.getIntPath("itemNum", itemFolder, 0);
                        totalupgrades = (byte) WzDataTool.getIntPath("tuc", itemFolder, 0);
                        stimulator = WzDataTool.getIntPath("catalyst", itemFolder, 0);

                        imt = new ItemMakerCreateEntry(cost, reqLevel, reqMakerLevel, quantity, totalupgrades, stimulator);

                        for (MapleData Recipe : itemFolder.getChildren()) {
                            for (MapleData ind : Recipe.getChildren()) {
                                if (Recipe.getName().equals("recipe")) {
                                    imt.addReqItem(WzDataTool.getIntPath("item", ind, 0), WzDataTool.getIntPath("count", ind, 0));
                                }
                            }
                        }
                        createCache.put(Integer.parseInt(itemFolder.getName()), imt);
                    }
                    break;
                }
            }
        }
    }

    public MapleData getCommodity() {
        return getData("Commodity.img");
    }

    private CashItemInfo parseCommodityField(MapleData field) {
        int SN = WzDataTool.getIntPath("SN", field, 0);
        int ItemId = WzDataTool.getIntPath("ItemId", field, 0);

        return new CashItemInfo(ItemId,
                WzDataTool.getIntPath("Count", field, 1),
                WzDataTool.getIntPath("Price", field, 0),
                SN,
                WzDataTool.getIntPath("Period", field, 0),
                WzDataTool.getIntPath("Gender", field, 2),
                WzDataTool.getIntPath("OnSale", field, 0) > 0);
    }

    public CashItemInfo findCommodityBySN(int item_SN) {
        for (MapleData field : getCommodity().getChildren()) {
            int SN = WzDataTool.getIntPath("SN", field, 0);
            if (SN <= 0 || item_SN != SN) {
                continue;
            }
            return parseCommodityField(field);
        }
        return null;
    }

    public CashItemInfo findCommodityByItemId(int itemid) {
        for (MapleData field : getCommodity().getChildren()) {
            int ItemId = WzDataTool.getIntPath("ItemId", field, 0);
            if (ItemId != itemid) {
                continue;
            }
            return parseCommodityField(field);
        }
        return null;
    }

    public MapleData getCashPackage() {
        return getData("CashPackage.img");
    }

    public MapleData getSetItemInfo() {
        return getData("SetItemInfo.img");
    }

    private List<String> list_fn = null;

    private List<String> getFN() {
        if (list_fn != null) {
            return list_fn;
        }

        list_fn = new ArrayList<>();
        for (final MapleData data : getForbiddenName().getChildren()) {
            list_fn.add(WzDataTool.getString(data));
        }

        return list_fn;
    }

    public boolean isForbiddenName(String character_name) {
        for (final String forbidden_name : getFN()) {
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
            int nItemId = WzDataTool.getIntPath("ItemId", field, 0);
            int nSN = WzDataTool.getIntPath("SN", field, 0);
            int bOnSale = WzDataTool.getIntPath("OnSale", field, 0);
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
