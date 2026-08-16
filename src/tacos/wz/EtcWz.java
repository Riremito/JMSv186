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
import odin.provider.IMapleData;
import tacos.config.ContentCustom;
import tacos.debug.DebugLogger;
import tacos.packet.ops.OpsCommodity;

/**
 *
 * @author Riremito
 */
public class EtcWz extends WzXML {

    public EtcWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Etc" : "Etc.wz");
    }

    public IMapleData getForbiddenName() {
        return getData("ForbiddenName.img");
    }

    public IMapleData getNpcLocation() {
        return getData("NpcLocation.img");
    }

    public IMapleData getItemMake() {
        return getData("ItemMake.img");
    }

    public IMapleData getCommodity() {
        return getData("Commodity.img");
    }

    public IMapleData getCashPackage() {
        return getData("CashPackage.img");
    }

    public IMapleData getSetItemInfo() {
        return getData("SetItemInfo.img");
    }

    private List<String> list_fn = null;

    private List<String> getFN() {
        if (list_fn != null) {
            return list_fn;
        }

        list_fn = new ArrayList<>();
        for (final IMapleData data : getForbiddenName().getChildren()) {
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
        for (IMapleData field : WzXML.ETC.getCommodity().getChildren()) {
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
