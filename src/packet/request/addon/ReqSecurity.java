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
package packet.request.addon;

import client.MapleClient;
import config.Region;
import config.Version;
import config.property.Property_Login;
import debug.Debug;
import packet.ClientPacket;

/**
 *
 * @author Riremito
 */
public class ReqSecurity {

    public static boolean OnPacket(ClientPacket.Header header, ClientPacket cp, MapleClient c) {
        if (Property_Login.getAntiCheat()) {
            return false;
        }

        switch (header) {
            // 独自実装
            case CP_CUSTOM_WZ_HASH: {
                if (Hash(cp)) {
                    Debug.DebugLog("MapleID:" + c.getAccountName() + ", wz OK");
                } else {
                    Debug.DebugLog("MapleID:" + c.getAccountName() + ", wz NG");
                }
                return true;
            }
            case CP_CUSTOM_MEMORY_SCAN: {
                if (Scan(cp)) {
                    Debug.DebugLog("MapleID:" + c.getAccountName() + ", memory OK");
                } else {
                    Debug.DebugLog("MapleID:" + c.getAccountName() + ", memory NG");
                }
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean Scan(ClientPacket p) {
        // v186以外は無視
        if (!(Region.IsJMS() && Version.getVersion() == 186)) {
            return true;
        }
        int scan_address = p.Decode4();
        byte[] scan_result = p.DecodeBuffer();
        // v186 damage hack
        if (scan_address == (int) 8791477 && scan_result[0] == (byte) 139 && scan_result[1] == (byte) 69 && scan_result[2] == (byte) 24) {
            return true;
        }
        return false;
    }

    public static boolean Hash(ClientPacket p) {
        // v186以外は無視
        if (!(Region.IsJMS() && Version.getVersion() == 186)) {
            return true;
        }
        final String wz_hash = p.DecodeStr();
        Debug.DebugLog(wz_hash);
        // v186 Skill.wz
        return wz_hash.startsWith("2e6008284345bbf5552b45ba206464404e474cbe8d8ba31bd61d0b4733422948");
    }

}
