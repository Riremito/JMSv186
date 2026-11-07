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
public class ResCField_GuildBoss {

    // CField_GuildBoss::OnHealerMove, not tested.
    public static ServerPacket HealerMove(int y) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_HealerMove);

        sp.Encode2(y);
        return sp;
    }

    // CField_GuildBoss::OnPulleyStateChange, not tested.
    public static ServerPacket PulleyStateChange(int m_nState) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PulleyStateChange);

        sp.Encode1(m_nState); // m_nState
        return sp;
    }
}
