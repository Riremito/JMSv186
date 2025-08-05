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
package packet.response;

import server.network.MaplePacket;
import packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class ResCField_Coconut {

    // not checked.
    public static MaplePacket CoconutHit(int nTarget, int nDelay, int nState) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CoconutHit);
        sp.Encode2(nTarget); // -1 = ALL
        sp.Encode2(nDelay); // nDelay
        sp.Encode1(nState); // 1 = spawn, 3 = destroy
        return sp.get();
    }

    public static MaplePacket CoconutScore(int[] coconutscore) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CoconutScore);
        sp.Encode2(coconutscore[0]); // maple team score
        sp.Encode2(coconutscore[1]); // story team score
        return sp.get();
    }

}
