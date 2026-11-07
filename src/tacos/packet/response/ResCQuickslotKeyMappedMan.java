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
public class ResCQuickslotKeyMappedMan {

    // CQuickslotKeyMappedMan::OnInit, not tested.
    public static ServerPacket QuickslotMappedInit(byte[] m_aQuickslotKeyMapped) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_QuickslotMappedInit);

        sp.Encode1(1);
        sp.EncodeBuffer(m_aQuickslotKeyMapped, 0x20); // m_aQuickslotKeyMapped
        return sp;
    }
}
