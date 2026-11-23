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

import odin.server.shops.MapleMiniRoom;
import tacos.client.TacosCharacter;
import tacos.packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class RD_Structure {

    // addAnnounceBox, the mini room shown over the character.
    public static byte[] AnnounceBox(TacosCharacter chr) {
        ServerPacket data = new ServerPacket();

        MapleMiniRoom room = chr.getBalloonRoom();
        if (room == null) {
            data.Encode1(0); // m_nMiniRoomType
            return data.getBytes();
        }
        data.Encode1(room.getMiniRoomType().get()); // m_nMiniRoomType
        data.Encode4(room.getObjectId()); // m_dwMiniRoomSN
        data.EncodeStr(room.getDescription()); // m_sMiniRoomTitle
        data.Encode1(room.isPrivate() ? 1 : 0); // m_bPrivate
        data.Encode1(room.getItemId() % 10); // m_nGameKind
        data.Encode1(room.getUserCount()); // m_nCurUsers
        data.Encode1(room.getMaxUsers()); // m_nMaxUsers
        data.Encode1(room.isGameOn() ? 1 : 0); // m_bGameOn
        return data.getBytes();
    }
}
