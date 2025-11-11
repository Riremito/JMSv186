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
import tacos.packet.ops.OpsContiMove;

/**
 *
 * @author Riremito
 */
public class ResCField_ContiMove {

    public static MaplePacket ContiMove(OpsContiMove ops1, OpsContiMove ops2) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CONTIMOVE);
        sp.Encode1(ops1.get());
        sp.Encode1(ops2.get());
        return sp.get();
    }

    public static MaplePacket ContiState(OpsContiMove ops) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CONTISTATE);
        sp.Encode1(ops.get());
        sp.Encode1(0); // 0 or 1, CShip::AppearShip
        return sp.get();
    }

}
