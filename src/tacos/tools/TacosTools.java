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
package tacos.tools;

import java.net.InetAddress;
import java.net.UnknownHostException;
import tacos.config.Region;
import tacos.config.Version;
import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class TacosTools {

    public static String DatatoString(byte[] hex) {
        String data = "";
        for (byte b : hex) {
            data += BYTEtoString(b);
        }
        return data;
    }

    public static String BYTEtoString(byte b) {
        byte high = (byte) ((b >> 4) & 15);
        byte low = (byte) (b & 15);
        high += (high <= 9) ? 48 : 55;
        low += (low <= 9) ? 48 : 55;
        return new String(new byte[]{high, low});
    }

    public static int bytesToInt(byte[] data) {
        // length check is needed?
        return data[0] | (data[1] << 8) | (data[2] << 16) | (data[3] << 24);
    }

    public static int getIPAddressValue(String ip_address) {
        int ip_address_value = 0;

        try {
            byte[] ip_bytes = InetAddress.getByName(ip_address).getAddress();
            ip_address_value = bytesToInt(ip_bytes);
        } catch (UnknownHostException ex) {
            DebugLogger.ErrorLog("getIPAddressValue, return default ip.");
            ip_address_value = TacosConstants.SERVER_GLOBAL_IP_VALUE;
        }

        return ip_address_value;
    }

    private static int GAME_SERVER_IP_VALUE_CACHE = 0;

    public static int getGameServerIP(String server_ip) {
        if (GAME_SERVER_IP_VALUE_CACHE != 0) {
            return GAME_SERVER_IP_VALUE_CACHE;
        }
        // client has to receive correct server ip if client still has ip checks in it.
        if (Version.GreaterOrEqual(Region.GMS, 116)) {
            // 8.31.98.52, CClientSocket::Connect
            GAME_SERVER_IP_VALUE_CACHE = getIPAddressValue(TacosConstants.FAKE_GLOBAL_IP_GMS116);
            // error
            if (GAME_SERVER_IP_VALUE_CACHE == TacosConstants.SERVER_GLOBAL_IP_VALUE) {
                GAME_SERVER_IP_VALUE_CACHE = TacosConstants.FAKE_GLOBAL_IP_GMS116_VALUE;
                DebugLogger.ErrorLog("getGameServerIP. GMS116.");
            }
            return GAME_SERVER_IP_VALUE_CACHE;
        }
        // default mode.
        GAME_SERVER_IP_VALUE_CACHE = getIPAddressValue(server_ip);
        return GAME_SERVER_IP_VALUE_CACHE;
    }

}
