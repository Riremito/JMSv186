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
import tacos.client.TacosCharacter;
import tacos.config.Config;
import tacos.config.Region;
import tacos.packet.ServerPacket;

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
}
