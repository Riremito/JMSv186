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
package tacos.packet.response.data;

import java.util.ArrayList;
import odin.provider.IMapleData;
import odin.provider.MapleDataTool;
import tacos.config.ContentCustom;
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import tacos.debug.DebugLogger;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsCommodity;
import tacos.wz.data.EtcWz;

/**
 *
 * @author Riremito
 */
public class DataCS_COMMODITY {

    // CWvsContext::SetSaleInfo
    public static byte[] SetSaleInfo() {
        ServerPacket data = new ServerPacket();

        ArrayList<CS_COMMODITY> onsales = getOnSale();
        data.Encode2(onsales.size()); // count
        for (CS_COMMODITY onsale : onsales) {
            data.Encode4(onsale.nSN);
            data.EncodeBuffer(EncodeModifiedData(onsale));
        }

        return data.get().getBytes();
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
        for (IMapleData field : EtcWz.get().getCommodity().getChildren()) {
            int nItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            int nSN = MapleDataTool.getIntConvert("SN", field, 0);
            int bOnSale = MapleDataTool.getIntConvert("OnSale", field, 0);
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

    // CS_COMMODITY::DecodeModifiedData
    public static byte[] EncodeModifiedData(CS_COMMODITY ccm) {
        ServerPacket data = new ServerPacket();

        boolean mask4 = ServerConfig.JMS164orLater() || Region.IsVMS() || Region.IsBMS() || Version.GreaterOrEqual(Region.GMS, 84);

        if (mask4) {
            data.Encode4(ccm.dwModifiedFlag);
        } else {
            data.Encode2(ccm.dwModifiedFlag); // GMS83, old cashshop masks
        }

        // 0x01
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_ITEMID.get()) != 0) {
            data.Encode4(ccm.nItemId); // nItemId
        }
        // 0x02
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_COUNT.get()) != 0) {
            data.Encode2(ccm.nCount); // nCount
        }
        // 0x10, weird order
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_PRIORITY.get()) != 0) {
            data.Encode1(ccm.nPriority); // nPriority
        }
        // 0x04
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_PRICE.get()) != 0) {
            data.Encode4(ccm.nPrice); // nPrice
        }
        // 0x08
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_BONUS.get()) != 0) {
            data.Encode1(ccm.bBonus); // bBonus
        }
        // 0x20
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_PERIOD.get()) != 0) {
            data.Encode2(ccm.nPeriod); // nPeriod
        }
        if (mask4) {
            // 0x20000, weird order
            if ((ccm.dwModifiedFlag & OpsCommodity.CM_REQPOP.get()) != 0) {
                data.Encode2(ccm.nReqPOP); // nReqPOP
            }
            // 0x40000, weird order
            if ((ccm.dwModifiedFlag & OpsCommodity.CM_REQLEV.get()) != 0) {
                data.Encode2(ccm.nReqLEV); // nReqLEV
            }
        }
        // 0x40
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_MAPLEPOINT.get()) != 0) {
            data.Encode4(ccm.nMaplePoint); // nMaplePoint
        }
        // 0x80
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_MESO.get()) != 0) {
            data.Encode4(ccm.nMeso); // nMeso
        }
        // 0x100
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_FORPREMIUMUSER.get()) != 0) {
            data.Encode1(ccm.bForPremiumUser); // bForPremiumUser
        }
        // 0x200
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_COMMODITYGENDER.get()) != 0) {
            data.Encode1(ccm.nCommodityGender); // nCommodityGender
        }
        // 0x400
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_ONSALE.get()) != 0) {
            data.Encode1(ccm.bOnSale); // bOnSale
        }
        // 0x800
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_CLASS.get()) != 0) {
            data.Encode1(ccm.nClass); // nClass
        }
        // 0x1000
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_LIMIT.get()) != 0) {
            data.Encode1(ccm.nLimit); // nLimit
        }
        // 0x2000
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_PBCASH.get()) != 0) {
            data.Encode2(ccm.nPbCash); // nPbCash
        }
        // 0x4000
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_PBPOINT.get()) != 0) {
            data.Encode2(ccm.nPbPoint); // nPbPoint
        }
        // 0x8000
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_PBGIFT.get()) != 0) {
            data.Encode2(ccm.nPbGift); // nPbGift
        }

        if (!mask4) {
            return data.get().getBytes();
        }

        // 0x10000
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_ITEMID.get()) != 0) {
            data.Encode1(ccm.aPackageSN.size()); // loop count
            for (int p_aPackageSN : ccm.aPackageSN) {
                data.Encode4(p_aPackageSN); // p_aPackageSN
            }
        }

        return data.get().getBytes();
    }

}
