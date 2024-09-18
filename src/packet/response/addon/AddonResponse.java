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

import handling.MaplePacket;
import packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class AddonResponse {

    public static MaplePacket Scan(int address, short size) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_CUSTOM_MEMORY_SCAN);
        p.Encode4(address);
        p.Encode2(size);
        return p.Get();
    }

    public static MaplePacket Hash() {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_CUSTOM_WZ_HASH);
        p.EncodeStr("Skill.wz");
        return p.Get();
    }

    public static MaplePacket Patch(int address, byte[] memory) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_CUSTOM_CLIENT_PATCH);
        p.Encode4(address);
        p.Encode2((short) memory.length);
        p.EncodeBuffer(memory);
        return p.Get();
    }

}
