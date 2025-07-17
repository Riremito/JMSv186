/*
 * Copyright (C) 2024 Riremito
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
package packet.response.addon;

import client.MapleClient;
import config.property.Property_Login;
import server.network.MaplePacket;
import packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class ResSecurity {

    public static boolean Test(MapleClient c) {
        if (Property_Login.getAntiCheat()) {
            c.SendPacket(ResSecurity.Hash());
            c.SendPacket(ResSecurity.Scan(0x008625B5, (short) 3)); // damage hack check
            byte mem[] = {(byte) 0x90, (byte) 0x90, (byte) 0x90};
            c.SendPacket(ResSecurity.Patch(0x00BCCA45, mem));
        }
        return true;
    }

    public static MaplePacket Scan(int address, short size) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CUSTOM_MEMORY_SCAN);
        sp.Encode4(address);
        sp.Encode2(size);
        return sp.get();
    }

    public static MaplePacket Hash() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CUSTOM_WZ_HASH);
        sp.EncodeStr("Skill.wz");
        return sp.get();
    }

    public static MaplePacket Patch(int address, byte[] memory) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CUSTOM_CLIENT_PATCH);
        sp.Encode4(address);
        sp.Encode2((short) memory.length);
        sp.EncodeBuffer(memory);
        return sp.get();
    }

}
