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

import tacos.network.MaplePacket;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCRPSGameDlg {

    public static MaplePacket getRPSMode(byte mode, int mesos, int selection, int answer) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_RPSGame);

        sp.Encode1(mode);
        switch (mode) {
            case 6: {
                //not enough mesos
                if (mesos != -1) {
                    sp.Encode4(mesos);
                }
                break;
            }
            case 8: {
                //open (npc)
                sp.Encode4(9000019);
                break;
            }
            case 11: {
                //selection vs answer
                sp.Encode1(selection);
                sp.Encode1(answer); // FF = lose, or if selection = answer then lose ???
                break;
            }
        }
        return sp.get();
    }

}
