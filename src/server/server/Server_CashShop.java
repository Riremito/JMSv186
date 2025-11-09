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
package server.server;

import config.property.Property_Shop;
import handling.channel.PlayerStorage;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoServiceConfig;
import server.MTSStorage;
import server.network.PacketHandler;
import server.network.PacketHandler_CashShop;

/**
 *
 * @author Riremito
 */
public class Server_CashShop extends Server {
    
    private PlayerStorage players;
    private PlayerStorage playersMTS;
    
    public Server_CashShop(String server_name, String server_ip, int server_port, IoHandler ih, IoServiceConfig isc) {
        super(server_name, server_ip, server_port, ih, isc);
        
        this.players = new PlayerStorage(-10);
        this.playersMTS = new PlayerStorage(-20);
    }
    
    @Override
    public void shutdown() {
        players.disconnectAll();
        playersMTS.disconnectAll();
        MTSStorage.getInstance().saveBuyNow(true);
        super.shutdown();
    }
    
    public PlayerStorage getPlayerStorage() {
        return this.players;
    }
    
    public PlayerStorage getPlayerStorageMTS() {
        return this.playersMTS;
    }
    
    public static boolean init() {
        Server_CashShop server = new Server_CashShop("CashShop", "127.0.0.1", Property_Shop.getPort(), new PacketHandler_CashShop(), PacketHandler.getSocketAcceptorConfig());
        Server.add(server);
        server.run();
        ServerOdinCashShop.set(server);
        return true;
    }
}
