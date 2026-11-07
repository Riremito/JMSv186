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

import tacos.client.TacosCharacter;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsBattlefieldTeam;

/**
 *
 * @author Riremito
 */
public class ResCField_Battlefield {

    // CField_Battlefield::OnScoreUpdate, not tested.
    public static ServerPacket BattlefieldScore(int score1, int score2) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_BattlefieldScore);

        sp.Encode1(score1);
        sp.Encode1(score2);
        return sp;
    }

    // CField_Battlefield::OnTeamChanged, not tested.
    public static ServerPacket BattlefieldTeamChanged(TacosCharacter chr, OpsBattlefieldTeam nTeam) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_BattlefieldTeamChanged);

        sp.Encode4(chr.getId());
        sp.Encode1(nTeam.get());
        return sp;
    }
}
