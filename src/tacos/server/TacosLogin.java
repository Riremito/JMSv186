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
package tacos.server;

import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;
import tacos.network.PacketHandler_Login;
import tacos.property.Property_Login;

/**
 *
 * @author Riremito
 */
public class TacosLogin extends TacosServer {

    public TacosLogin(String server_name) {
        super(server_name);
        setType(TacosServerType.LOGIN_SERVER);
    }

    public int getWolrdStatus(int world_id) {
        // test
        if (2 <= world_id) {
            if (world_id == 2) {
                return 1;
            }
            return 2;
        }
        TacosWorld world = TacosWorld.find(world_id);
        if (world == null) {
            DebugLogger.ErrorLog("getWolrdStatus : invalid world.");
            return 2;
        }
        int online_users = 0;

        for (TacosChannel channel : world.getChannels()) {
            online_users += channel.getOnlinePlayers().get().size();
        }

        int world_max_users = Property_Login.getUserLimit();
        if (world_max_users <= online_users) {
            DebugLogger.ErrorLog("getWolrdStatus : max users limit.");
            return 2;
        }

        if ((world_max_users / 2) <= online_users) {
            DebugLogger.ErrorLog("getWolrdStatus : too many users.");
            return 1;
        }

        // OK
        return 0;
    }

    public static void init() {
        TacosLogin login_server = new TacosLogin("Login");
        TacosServer.add(login_server);
        login_server.setGlobalIP(TacosConstants.SERVER_GLOBAL_IP);
        login_server.run(TacosConstants.SERVER_LOCAL_IP, Property_Login.getPort(), new PacketHandler_Login(login_server));
    }

}
