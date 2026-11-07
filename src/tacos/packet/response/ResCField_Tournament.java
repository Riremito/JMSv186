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
public class ResCField_Tournament {

    // CField_Tournament::OnTournament, not tested.
    public static ServerPacket Tournament(int flag, int value1, int value2) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Tournament);

        sp.Encode1(flag);
        sp.Encode1(value1);
        sp.Encode1(value2);
        return sp;
    }

    // CField_Tournament::OnTournamentMatchTable, not tested.
    public static ServerPacket TournamentMatchTable(byte[] m_aaMatch, int m_nState) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TournamentMatchTable);

        sp.EncodeBuffer(m_aaMatch, 0x300); // m_aaMatch
        sp.Encode1(m_nState); // m_nState
        return sp;
    }

    // CField_Tournament::OnTournamentSetPrize, not tested.
    public static ServerPacket TournamentSetPrize(int type, boolean has_prize, int prize1, int prize2) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TournamentSetPrize);

        sp.Encode1(type);
        sp.Encode1(has_prize ? 1 : 0);
        sp.Encode4(prize1, has_prize);
        sp.Encode4(prize2, has_prize);
        return sp;
    }

    // CField_Tournament::OnTournamentUEW, not tested.
    public static ServerPacket TournamentNoticeUEW(int round) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TournamentNoticeUEW);

        sp.Encode1(round);
        return sp;
    }

    // CField_Tournament::OnTournamentAvatarInfo, not tested.
    public static ServerPacket TournamentAvatarInfo() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TournamentAvatarInfo);

        return sp;
    }
}
