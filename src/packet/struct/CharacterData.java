/*
 * Copyright (C) 2023 Riremito
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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package packet.struct;

import client.MapleCharacter;
import config.ServerConfig;
import packet.ServerPacket;
import packet.Structure;

/**
 *
 * @author Riremito
 */
public class CharacterData {

    // CharacterData::Decode
    // CharacterInfo
    public static byte[] Encode(MapleCharacter chr) {
        ServerPacket p = new ServerPacket();

        if (ServerConfig.version <= 131) {
            p.Encode2(-1); // statmask
        } else {
            p.Encode8(-1); // statmask
        }

        if (186 <= ServerConfig.version) {
            p.Encode1(0);
        }

        if (187 <= ServerConfig.version) {
            p.Encode1(0);
        }

        // 0x1
        // キャラクター情報
        p.EncodeBuffer(GW_CharacterStat.Encode(chr));
        // 友達リストの上限
        p.Encode1(chr.getBuddylist().getCapacity());

        // 精霊の祝福 v165, v186
        if (165 <= ServerConfig.version) {
            if (chr.getBlessOfFairyOrigin() != null) {
                p.Encode1(1);
                p.EncodeStr(chr.getBlessOfFairyOrigin());
            } else {
                p.Encode1(0);
            }
        }

        // 祝福系統
        if (194 <= ServerConfig.version) {
            p.Encode1(0); // not 0, EncodeStr
            p.Encode1(0); // not 0, EncodeStr
        }

        // 0x2 (<< 1) v165-v194
        p.EncodeBuffer(GW_CharacterStat.EncodeMoney(chr));
        p.EncodeBuffer(GW_CharacterStat.EncodePachinko(chr));
        // 0x4 (<< 2), 0x100000, 0x4 [addInventoryInfo]
        p.EncodeBuffer(Structure.InventoryInfo(chr));
        // 0x100 [addSkillInfo] v165 changed v186-v194
        p.EncodeBuffer(Structure.addSkillInfo(chr));
        // 0x8000 [addCoolDownInfo] v165-v194
        p.EncodeBuffer(Structure.addCoolDownInfo(chr));
        // 0x200 [addQuestInfo] changed v165,v186,v188,v194
        p.EncodeBuffer(Structure.addQuestInfo(chr));
        // 0x4000 QuestComplete v165-v194
        p.EncodeBuffer(Structure.addQuestComplete(chr));
        // 0x400 MiniGameRecord v165-v194
        p.Encode2(0); // not 0 -> Encode4 x5
        // 0x800 [addRingInfo] v165-v194
        p.EncodeBuffer(Structure.addRingInfo(chr));
        // 0x1000 [addRocksInfo] v165-v188 changed v194
        p.EncodeBuffer(Structure.addRocksInfo(chr));
        // 0x7C JMS, Present v165-v194
        p.Encode2(0); // not 0 -> Encode4, Encode4, Encode2, EncodeStr

        if (164 <= ServerConfig.version) {
            // 0x20000 JMS v165-v194
            p.Encode4(chr.getMonsterBookCover());
            // 0x10000 JMS [addMonsterBookInfo] v165-v194
            p.EncodeBuffer(Structure.addMonsterBookInfo(chr));

            if (194 <= ServerConfig.version) {
                // 0x10000000
                p.Encode4(0);
                // 0x20000000
                p.Encode2(0); // not 0, Encode2
            }

            // 0x40000 (GMS 0x80000) [QuestInfoPacket] v165-v194
            p.EncodeBuffer(Structure.QuestInfoPacket(chr));

            if (ServerConfig.version <= 186) {
                // 0x80000 JMS v165, v186, not in v188
                p.Encode2(0);// not 0 -> Encode4, Encode2
            }

            if (186 == ServerConfig.version) {
                // 0x200000 VisitorQuestLog (GMS 0x800000)
                p.Encode2(0); // not 0 -> Encode2, Encode2
            }

            // v188-v194
            if (188 <= ServerConfig.version) {
                // 0x200000
                p.EncodeBuffer(GW_WildHunterInfo.Encode());
                // 0x400000 QuestCompleteOld
                p.Encode2(0); // not 0, Encode2, EncodeBuffer8
                // 0x800000
                p.Encode2(0); // not 0, Encode2, Encode2
            }
        }

        return p.Get().getBytes();
    }
}
