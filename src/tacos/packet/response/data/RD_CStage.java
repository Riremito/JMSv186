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

import java.util.Map;
import odin.client.MapleCharacter;
import tacos.client.TacosCharacter;
import tacos.config.Config;
import tacos.config.Region;
import tacos.packet.ServerPacket;
import tacos.packet.response.ResCCashShop;

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

    // CCashShop::CCashShop
    public static byte[] CCashShop_CCashShop(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        data.EncodeBuffer(RD_CStage.CCashShop_LoadData(chr));
        data.Encode1(0); // m_bEventOn
        if (Config.GreaterOrEqual(Region.GMS, 62)) {
            data.Encode4(0); // m_nHighestCharacterLevelInThisAccount
        }
        if (Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.IMS, 1)) {
            data.Encode1(0);
        }

        return data.getBytes();
    }

    // CCashShop::LoadData
    public static byte[] CCashShop_LoadData(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        if (Region.GMS.check() || Region.EMS.check() || Region.BMS.check()) {
            data.Encode1(1); // m_bCashShopAuthorized
        }
        // not asia soft.
        if (!(Region.MSEA.check() || Region.THMS.check() || Region.VMS.check())) {
            data.EncodeStr(chr.getClient().getMapleId());
        }
        if (Config.GreaterOrEqual(Region.EMS, 55)) {
            data.Encode1(0);
        }
        data.EncodeBuffer(CWvsContext_SetSaleInfo());
        data.EncodeBuffer(ResCCashShop.getBestItems(), 1080);
        data.Encode2(0); // CCashShop::DecodeStock
        data.Encode2(0); // CCashShop::DecodeLimitGoods
        if (Config.GreaterOrEqual(Region.GMS, 72)) {
            data.Encode2(0); // CCashShop::DecodeZeroGoods
        }

        return data.getBytes();
    }

    // CWvsContext::SetSaleInfo
    public static byte[] CWvsContext_SetSaleInfo() {
        ServerPacket data = new ServerPacket();

        if (Config.GreaterOrEqual(Region.JMS, 187) || Config.GreaterOrEqual(Region.CMS, 88) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.GMS, 61) || Config.Between(Region.EMS, 70, 76) || Config.GreaterOrEqual(Region.BMS, 24)) {
            data.Encode4(0); // NotSaleCount
        }
        data.EncodeBuffer(RD_CS_COMMODITY.CWvsContext_SetSaleInfo()); // 2 bytes.
        if (Config.GreaterOrEqual(Region.KMS, 92) || Config.GreaterOrEqual(Region.KMST, 330) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70) || Config.GreaterOrEqual(Region.IMS, 1)) {
            data.Encode2(0); // non 0, Decode4, DecodeStr
        }
        data.EncodeBuffer(ResCCashShop.getDiscountRates()); // 1 byte.
        if (Config.GreaterOrEqual(Region.EMS, 89)) {
            data.Encode4(0);
        }

        return data.getBytes();
    }
}
