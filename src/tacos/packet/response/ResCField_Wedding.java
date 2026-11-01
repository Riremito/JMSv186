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
public class ResCField_Wedding {

    // CField_Wedding::OnWeddingProgress, not tested.
    public static ServerPacket WeddingProgress(int nStep, int m_dwGroomID, int m_dwBrideID) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_WeddingProgress);

        sp.Encode1(nStep);
        sp.Encode4(m_dwGroomID); // m_dwGroomID
        sp.Encode4(m_dwBrideID); // m_dwBrideID
        return sp;
    }

    // CField_Wedding::OnWeddingCeremonyEnd, not tested.
    public static ServerPacket WeddingCremonyEnd() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_WeddingCremonyEnd);

        return sp;
    }
}
