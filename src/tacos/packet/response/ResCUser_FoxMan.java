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
package tacos.packet.response;

import odin.client.MapleCharacter;
import tacos.network.MaplePacket;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.request.parse.ParseCMovePath;

/**
 *
 * @author Riremito
 */
public class ResCUser_FoxMan {

    public static MaplePacket FoxManEnterField(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FoxManEnterField);

        sp.Encode4(chr.getId()); // m_dwCharacterID
        sp.EncodeBuffer(Create());
        return sp.get();
    }

    // CFoxMan::OnCreated
    private static byte[] Create() {
        ServerPacket data = new ServerPacket();

        data.Encode2(0); // m_ptPos.x
        data.Encode2(0); // m_ptPos.y
        data.Encode1(0); // m_nMoveAction
        data.Encode2(0); // FH, dwSN
        data.Encode1(0); // m_nUpgrade
        data.Encode4(0); // m_anFoxManEquip[0]
        return data.get().getBytes();
    }

    // CFoxMan::OnMove
    public static MaplePacket FoxManMove(MapleCharacter chr, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FoxManMove);

        sp.Encode4(chr.getId()); // m_dwCharacterID
        sp.EncodeBuffer(data.get());
        return sp.get();
    }

    // CFoxMan::OnExclResult
    public static MaplePacket FoxManExclResult(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FoxManExclResult);

        sp.Encode4(chr.getId()); // m_dwCharacterID
        return sp.get();
    }

    // CFoxMan::OnShowChangeEffect
    public static MaplePacket FoxManShowChangeEffect(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FoxManShowChangeEffect);

        sp.Encode4(chr.getId()); // m_dwCharacterID
        return sp.get();
    }

    // CFoxMan::OnModified
    public static MaplePacket FoxManModified(MapleCharacter chr, boolean is_change_equip) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FoxManModified);

        sp.Encode4(chr.getId()); // m_dwCharacterID
        sp.Encode1(is_change_equip ? 1 : 0); // change equip
        if (is_change_equip) {
            sp.Encode4(0); // m_anFoxManEquip[0]
        }
        // for local
        sp.Encode1(1); // for SetExclRequestSent
        return sp.get();
    }

    public static MaplePacket FoxManLeaveField(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FoxManLeaveField);

        sp.Encode4(chr.getId()); // m_dwCharacterID
        return sp.get();
    }
}
