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
import java.util.Map;
import odin.client.MapleCharacter;
import tacos.client.TacosCharacter;
import tacos.config.Config;
import tacos.config.Region;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsCommodity;
import tacos.packet.response.ResCCashShop;
import tacos.server.TacosITC;
import tacos.shared.SharedDate;
import tacos.wz.EtcWz;
import tacos.wz.EtcWz.CS_COMMODITY;

/**
 *
 * @author Riremito
 */
public class RD_CStage {

    // CClientOptMan::DecodeOpt
    public static byte[] ClientOptMan_EncodeOpt() {
        ServerPacket data = new ServerPacket();

        data.Encode2(0); // not 0, Encode4, Encode4
        return data.getBytes();
    }

    // CWvsContext::OnSetLogoutGiftConfig
    public static byte[] CWvsContext_OnSetLogoutGiftConfig() {
        ServerPacket data = new ServerPacket();

        data.Encode4(0); // something
        if (Config.GreaterOrEqual(Region.GMS, 126)) {
            // 0 = no data.
            return data.getBytes();
        }
        if (Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.EMS, 76)) {
            data.Encode4(0);
        }

        data.Encode4(0); // item1?
        data.Encode4(0); // item2?
        data.Encode4(0); // item3?
        return data.getBytes();
    }

    // GW_MonsterBookCode::Decode
    public static byte[] GW_MonsterBookCode_Encode(TacosCharacter chr) {
        ServerPacket data = new ServerPacket();

        data.Encode1(0); // shrink or not.
        data.Encode2(chr.getMonsterBook().getCards().size());
        for (Map.Entry<Integer, Integer> card : chr.getMonsterBook().getCards().entrySet()) {
            int card_id_short = card.getKey() % 10000; // item id to card id.
            data.Encode2(card_id_short);
            data.Encode1(card.getValue());
        }

        return data.getBytes();
    }

    // GW_WildHunterInfo::Decode
    public static byte[] GW_WildHunterInfo_Encode() {
        ServerPacket data = new ServerPacket();

        data.Encode1(0);
        for (int i = 0; i < 5; i++) {
            data.Encode4(0);
        }

        return data.getBytes();
    }

    // GW_MonsterBookCode::Decode, unused.
    public static byte[] GW_MonsterBookCode_Encode(TacosCharacter chr, boolean data_shrink) {
        ServerPacket data = new ServerPacket();

        data.Encode1(data_shrink ? 1 : 0);
        if (!data_shrink) {
            Map<Integer, Integer> cards = chr.getMonsterBook().getCards();

            data.Encode2(cards.size());
            for (Map.Entry<Integer, Integer> card : cards.entrySet()) {
                int card_id_short = card.getKey() % 10000; // item id to card id.
                data.Encode2(card_id_short);
                data.Encode1(card.getValue());
            }

            return data.getBytes();
        }

        // unknown format, not coded.
        int card_count = 7; // unk
        int buffer_size_1 = 1;
        int buffer_size_2 = 5;

        data.Encode2(card_count);
        data.Encode1(buffer_size_1); // buffer size 1
        data.EncodeBuffer(new byte[]{1 | 2 | 4 | 8 | 16}); // buffer, card id mask, some card can be put in 1 byte.
        data.Encode1(buffer_size_2); // buffer size 2
        data.EncodeBuffer(new byte[]{4, 3, 2, 1, 5}); // buffer, nCardCount?
        return data.getBytes();
    }

    // CITC::CITC
    public static byte[] CITC_CITC(MapleCharacter chr) {
        return CITC_LoadData(chr);
    }

    // CITC::LoadData
    public static byte[] CITC_LoadData(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        data.EncodeStr(chr.getClient().getMapleId()); // m_sNexonClubID
        data.Encode4(TacosITC.m_nRegisterFeeMeso); // m_nRegisterFeeMeso
        data.Encode4(TacosITC.m_nCommissionRate); // m_nCommissionRate
        data.Encode4(TacosITC.m_nCommissionBase); // m_nCommissionBase
        data.Encode4(TacosITC.m_nAuctionDurationMin); // m_nAuctionDurationMin
        data.Encode4(TacosITC.m_nAuctionDurationMax); // m_nAuctionDurationMax
        data.Encode8(SharedDate.getTimestamp(), Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 146) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 74) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 61) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 55) || Config.GreaterOrEqual(Region.BMS, 24) || Config.GreaterOrEqual(Region.VMS, 35)); // ftServer
        return data.getBytes();
    }

    // CCashShop::CCashShop
    public static byte[] CCashShop_CCashShop(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        data.EncodeBuffer(RD_CStage.CCashShop_LoadData(chr));
        data.Encode1(0); // m_bEventOn
        data.Encode4(0, Config.GreaterOrEqual(Region.GMS, 62)); // m_nHighestCharacterLevelInThisAccount
        data.Encode1(0, Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.IMS, 1));
        return data.getBytes();
    }

    // CCashShop::LoadData
    public static byte[] CCashShop_LoadData(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        data.Encode1(1, Region.GMS.check() || Region.GMST.check() || Region.EMS.check() || Region.BMS.check()); // m_bCashShopAuthorized
        data.EncodeStr(chr.getClient().getMapleId(), !(Region.MSEA.check() || Region.THMS.check() || Region.VMS.check())); // not asia soft.
        data.Encode1(0, Config.GreaterOrEqual(Region.EMS, 55));
        data.EncodeBuffer(CWvsContext_SetSaleInfo());
        data.EncodeBuffer(ResCCashShop.getBestItems(), 1080);
        data.Encode2(0); // CCashShop::DecodeStock
        data.Encode2(0); // CCashShop::DecodeLimitGoods
        data.Encode2(0, Config.GreaterOrEqual(Region.GMS, 72)); // CCashShop::DecodeZeroGoods
        return data.getBytes();
    }

    // CWvsContext::SetSaleInfo
    public static byte[] CWvsContext_SetSaleInfo() {
        ServerPacket data = new ServerPacket();

        data.Encode4(0, Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.GMS, 61) || Config.Between(Region.EMS, 55, 70) || Config.GreaterOrEqual(Region.BMS, 24)); // NotSaleCount
        data.EncodeBuffer(getCommodities(EtcWz.getOnSale())); // 2 bytes.
        data.Encode2(0, Config.GreaterOrEqual(Region.KMS, 92) || Config.GreaterOrEqual(Region.KMST, 330) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70) || Config.GreaterOrEqual(Region.IMS, 1)); // non 0, Decode4, DecodeStr
        data.EncodeBuffer(getDiscountRates(0, 0, 99)); // 1 byte.
        data.Encode4(0, Config.GreaterOrEqual(Region.EMS, 89));
        return data.getBytes();
    }

    public static byte[] getCommodities(ArrayList<CS_COMMODITY> onsales) {
        ServerPacket data = new ServerPacket();

        data.Encode2(onsales.size()); // count
        for (CS_COMMODITY onsale : onsales) {
            data.Encode4(onsale.nSN);
            data.EncodeBuffer(CS_COMMODITY_EncodeModifiedData(onsale));
        }

        return data.getBytes();
    }

    // CS_COMMODITY::DecodeModifiedData
    public static byte[] CS_COMMODITY_EncodeModifiedData(CS_COMMODITY ccm) {
        ServerPacket data = new ServerPacket();

        boolean mask4 = Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54) || Region.VMS.check() || Region.BMS.check() || Config.GreaterOrEqual(Region.GMS, 84);

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
            return data.getBytes();
        }

        // 0x10000
        if ((ccm.dwModifiedFlag & OpsCommodity.CM_ITEMID.get()) != 0) {
            data.Encode1(ccm.aPackageSN.size()); // loop count
            for (int p_aPackageSN : ccm.aPackageSN) {
                data.Encode4(p_aPackageSN); // p_aPackageSN
            }
        }

        return data.getBytes();
    }

    public static byte[] getDiscountRates(int categories, int sub_categories, int discount_rate) {
        ServerPacket data = new ServerPacket();

        // // count max 9*30, ただし1 byteなので全ては利用不可
        data.Encode1(categories * sub_categories);
        for (int category = 2; category < (categories + 2); category++) {
            for (int sub_category = 0; sub_category < sub_categories; sub_category++) {
                data.Encode1(category); // category
                data.Encode1(sub_category); // sub category
                data.Encode1(discount_rate); // discount rate
            }
        }

        return data.getBytes();
    }
}
