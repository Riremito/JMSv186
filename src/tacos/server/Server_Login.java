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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import tacos.debug.DebugLogger;
import tacos.property.Property_Login;

/**
 *
 * @author Riremito
 */
public class Server_Login extends TacosServer {

    private ArrayList<Server_Game> game_servers = new ArrayList<>();
    private Map<Integer, ArrayList<Server_Game>> game_worlds = new HashMap<>();

    public Server_Login(String server_name) {
        super(server_name);
        setType(TacosServerType.LOGIN_SERVER);
    }

    public void addGameServer(Server_Game game_server) {
        this.game_servers.add(game_server);
        int world_id = game_server.getWorld().getId();
        ArrayList<Server_Game> game_world = this.game_worlds.get(world_id);
        if (game_world == null) {
            game_world = new ArrayList<>();
            this.game_worlds.put(world_id, game_world);
        }
        game_world.add(game_server);
    }

    public int getNumberOfWorlds() {
        return this.game_worlds.size();
    }

    public ArrayList<Server_Game> getWorld(int world_id) {
        return this.game_worlds.get(world_id);
    }

    public int getWolrdStatus(int world) {
        // test
        if (2 <= world) {
            if (world == 2) {
                return 1;
            }
            return 2;
        }
        int online_users = 0;
        for (Server_Game game_server : this.game_servers) {
            if (game_server.getWorld().getId() == world) {
                online_users += game_server.getNumberOfSessions();
            }
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

}
