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
package tacos.packet.response;

import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCMapLoadable {

    // CMapLoadable::OnSetBackEffect, not tested.
    public static ServerPacket SetBackgroundEffect(int nEffect, int nFieldID, int nPageID, int tDuration) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetBackgroundEffect);

        sp.Encode1(nEffect); // nEffect
        sp.Encode4(nFieldID); // nFieldID
        sp.Encode1(nPageID); // nPageID
        sp.Encode4(tDuration); // tDuration
        return sp;
    }

    // CMapLoadable::OnSetMapObjectVisible, not tested.
    public static ServerPacket SetMapObjectVisible(String name, boolean visible) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetMapObjectVisible);

        sp.Encode1(1); // count
        sp.EncodeStr(name);
        sp.Encode1(visible ? 1 : 0);
        return sp;
    }

    // CMapLoadable::OnClearBackEffect
    public static ServerPacket ClearBackgroundEffect() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ClearBackgroundEffect);
        return sp;
    }
}
