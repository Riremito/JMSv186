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

import tacos.property.Property_Login;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoServiceConfig;
import tacos.network.PacketHandler;
import tacos.network.PacketHandler_Login;

/**
 *
 * @author Riremito
 */
public class Server_Login extends Server {

    public Server_Login(String server_name, String server_ip, int server_port, IoHandler ih, IoServiceConfig isc) {
        super(server_name, server_ip, server_port, ih, isc);
    }

    public static boolean init() {
        Server_Login server = new Server_Login("Login", "127.0.0.1", Property_Login.getPort(), new PacketHandler_Login(), PacketHandler.getSocketAcceptorConfig());
        Server.add(server);
        server.run();
        ServerOdinLogin.set(server);
        return true;
    }
}
