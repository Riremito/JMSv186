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
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;

/**
 *
 * @author Riremito
 */
public class CustomRequest {

    public static boolean OnPacket(ClientPacketHeader header, ClientPacket cp, MapleClient client) {
        if (Property_Login.getAntiCheat()) {
            return false;
        }

        switch (header) {
            // 独自実装
            case CP_CUSTOM_WZ_HASH: {
                if (OnWzHash(cp)) {
                    DebugLogger.DebugLog("MapleID:" + client.getMapleId() + ", wz OK");
                } else {
                    DebugLogger.DebugLog("MapleID:" + client.getMapleId() + ", wz NG");
                }
                return true;
            }
            case CP_CUSTOM_MEMORY_SCAN: {
                if (OnMemoryHash(cp)) {
                    DebugLogger.DebugLog("MapleID:" + client.getMapleId() + ", memory OK");
                } else {
                    DebugLogger.DebugLog("MapleID:" + client.getMapleId() + ", memory NG");
                }
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnWzHash(ClientPacket cp) {
        String wz_hash = cp.DecodeStr();
        // JMS186 Skill.wz
        return wz_hash.startsWith("2e6008284345bbf5552b45ba206464404e474cbe8d8ba31bd61d0b4733422948");
    }

    public static boolean OnMemoryHash(ClientPacket cp) {
        String memory_hash = cp.DecodeStr();

        return memory_hash.startsWith("");
    }

}
