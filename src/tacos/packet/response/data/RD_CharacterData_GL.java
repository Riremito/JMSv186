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

import odin.client.MapleCharacter;
import tacos.config.Config;
import tacos.config.Region;
import tacos.packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class RD_CharacterData_GL {

    public static boolean Encode_TeleportRock_Below(MapleCharacter chr, long datamask, ServerPacket data) {
        // GMS126-131
        if (Config.GreaterOrEqual(Region.GMS, 126)) {
            if ((datamask & 0x20000L) != 0) {
                data.Encode4(0);
            }
            if ((datamask & 0x10000L) != 0) {
                data.Encode1(0);
                data.Encode2(0);
            }
            if ((datamask & 0x100000000000L) != 0) {
                data.Encode4(0);
            }
            if ((datamask & 0x200000000000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x4000000000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x40000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x200000L) != 0 && (chr.getJob() / 100 == 33)) {
                data.EncodeBuffer(RD_CStage.GW_WildHunterInfo_Encode());
            }
            if ((datamask & 0x400000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x4000000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x20000000L) != 0) {
                int unk_loop_cnt = (4 + 4 + 3 + 2);
                for (int i = 0; i < unk_loop_cnt; i++) {
                    data.Encode4(0);
                }
            }
            if ((datamask & 0x10000000L) != 0) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
            }
            if ((datamask & 0x80000000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x100000000L) != 0) {
                data.Encode4(0);
                data.Encode4(0);
            }
            if ((datamask & 0x200000000L) != 0) {
                data.Encode1(0);
                data.Encode2(0);
            }
            if ((datamask & 0x400000000L) != 0) {
                data.Encode1(0);
            }
            if ((datamask & 0x800000000L) != 0) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
                data.Encode1(0);
            }
            if ((datamask & 0x2000000000L) != 0) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode8(0);
            }
            if ((datamask & 0x1000000000000L) != 0) {
                data.EncodeZeroBytes(84);
                data.Encode1(0);
            }
            if ((datamask & 0x8000000000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x2000000000000L) != 0) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
                data.EncodeZeroBytes(32);
            }
            return true;
        }
        // GMS116-117
        if (Config.GreaterOrEqual(Region.GMS, 116)) {
            if ((datamask & 0x20000L) != 0) {
                data.Encode4(0);
            }
            if ((datamask & 0x10000L) != 0) {
                data.Encode1(0);
                data.Encode2(0);
            }
            if ((datamask & 0x80000000L) != 0) {
                data.Encode4(0);
            }
            if ((datamask & 0x100000000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x40000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x80000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x400000L) != 0 && (chr.getJob() / 100 == 33)) {
                data.EncodeBuffer(RD_CStage.GW_WildHunterInfo_Encode());
            }
            if ((datamask & 0x80000000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x4000000000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x20000000000L) != 0) {
                for (int i = 0; i < (4 + 4 + 3 + 2); i++) {
                    data.Encode4(0);
                }
            }
            if ((datamask & 0x10000000000L) != 0) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
            }
            if ((datamask & 0x80000000000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x100000000000L) != 0) {
                data.Encode4(0);
                data.Encode4(0);
            }
            if ((datamask & 0x200000000000L) != 0) {
                data.EncodeZeroBytes(84);
                data.Encode1(0);
            }
            if ((datamask & 0x1000000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x400000000000L) != 0) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
                data.EncodeZeroBytes(32);
            }
            return true;
        }
        // GMS111
        if (Config.GreaterOrEqual(Region.GMS, 111)) {
            if ((datamask & 0x20000L) != 0) {
                data.Encode4(0);
            }
            if ((datamask & 0x10000L) != 0) {
                data.Encode1(0);
                data.Encode2(0);
            }
            if ((datamask & 0x80000000L) != 0) {
                data.Encode4(0);
            }
            if ((datamask & 0x100000000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x40000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x80000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x400000L) != 0 && (chr.getJob() / 100 == 33)) {
                data.EncodeBuffer(RD_CStage.GW_WildHunterInfo_Encode());
            }
            if ((datamask & 0x80000000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x4000000000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x20000000000L) != 0) {
                for (int i = 0; i < (6 + 5 + 4 + 3); i++) {
                    data.Encode4(0);
                }
            }
            if ((datamask & 0x10000000000L) != 0) {
                data.Encode1(0);
                data.Encode2(0);
                data.Encode2(0);
            }
            if ((datamask & 0x01000000) != 0) {
                data.Encode2(0);
            }
            return true;
        }
        // GMS95, GMST2
        if ((datamask & 0x40000L) != 0) {
            data.Encode2(0); // NewYearCardRecord
        }
        if ((datamask & 0x80000L) != 0) {
            data.Encode2(0); // InitQuestExFromRawStr
        }
        if ((datamask & 0x200000L) != 0 && (chr.getJob() / 100 == 33)) {
            data.EncodeBuffer(RD_CStage.GW_WildHunterInfo_Encode());
        }
        if ((datamask & 0x400000L) != 0) {
            data.Encode2(0); // QuestCompleteOld
        }
        if ((datamask & 0x800000L) != 0) {
            data.Encode2(0); // VisitorQuestLog
        }

        return true;
    }
}
