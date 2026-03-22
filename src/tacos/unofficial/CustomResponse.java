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
package tacos.unofficial;

import odin.client.MapleClient;
import tacos.property.Property_Login;
import tacos.network.MaplePacket;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class CustomResponse {

    public static boolean Test(MapleClient client) {
        if (Property_Login.getAntiCheat()) {
            client.SendPacket(CustomResponse.GetWzHash("Skill.wz"));
            client.SendPacket(CustomResponse.GetMemoryHash(0x008625B5, 3)); // damage hack check
            byte mem[] = {(byte) 0x90, (byte) 0x90, (byte) 0x90};
            client.SendPacket(CustomResponse.SetPatch(0x00BCCA45, mem));
        }
        return true;
    }

    public static MaplePacket GetWzHash(String wz_name) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CUSTOM_WZ_HASH);

        sp.EncodeStr(wz_name);
        return sp.get();
    }

    public static MaplePacket GetMemoryHash(int address, int size) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CUSTOM_MEMORY_SCAN);

        sp.Encode4(address);
        sp.Encode4(size);
        return sp.get();
    }

    public static MaplePacket SetPatch(int address, byte[] memory) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CUSTOM_CLIENT_PATCH);

        sp.Encode4(address);
        sp.Encode4(memory.length);
        sp.EncodeBuffer(memory);
        return sp.get();
    }

}
