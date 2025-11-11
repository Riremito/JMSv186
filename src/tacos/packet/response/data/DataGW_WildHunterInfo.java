/*
 * Copyright (C) 2023 Riremito
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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package tacos.packet.response.data;

import tacos.packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class DataGW_WildHunterInfo {

    // GW_WildHunterInfo::Decode
    public static byte[] Encode() {
        ServerPacket p = new ServerPacket();

        p.Encode1(0);
        for (int i = 0; i < 5; i++) {
            p.Encode4(0);
        }
        return p.get().getBytes();
    }
}
