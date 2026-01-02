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

import tacos.debug.DebugLogger;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import tacos.network.PacketHandler;

/**
 *
 * @author Riremito
 */
public class TacosServer {

    protected static ArrayList<TacosServer> server_list = new ArrayList<>();
    private static IoAcceptor server_io_acceptor = null;

    public static ArrayList<TacosServer> get() {
        return server_list;
    }

    public static void add(TacosServer server) {
        server_list.add(server);
    }

    public static IoAcceptor getIoAcceptor() {
        if (server_io_acceptor == null) {
            server_io_acceptor = new SocketAcceptor();
        }
        return server_io_acceptor;
    }

    private String server_name;
    private String server_ip;
    private int server_port;
    private IoHandler server_io_handler;
    private IoServiceConfig server_service_config;
    private InetSocketAddress server_inet_socket_address = null;
    //private IoAcceptor server_io_acceptor = null;
    private boolean finishedShutdown = true;
    private boolean adminOnly = false;
    private boolean server_status = false; // online or not.

    public TacosServer(String server_name) {
        this.server_name = server_name;
    }

    public boolean run(String server_ip, int server_port, IoHandler ih) {
        this.server_ip = server_ip;
        this.server_port = server_port;
        this.server_io_handler = ih;
        this.server_service_config = PacketHandler.getSocketAcceptorConfig();
        this.server_inet_socket_address = new InetSocketAddress(this.server_ip, this.server_port);

        try {
            getIoAcceptor().bind(this.server_inet_socket_address, this.server_io_handler, this.server_service_config);
        } catch (IOException e) {
            DebugLogger.ErrorLog("bind = " + this.server_ip + ":" + this.server_port + " (" + this.server_name + ")");
            DebugLogger.ErrorLog(e.toString());
            return false;
        }

        DebugLogger.InfoLog("bind = " + this.server_inet_socket_address.getHostName() + ":" + this.server_inet_socket_address.getPort() + " (" + this.server_name + ")");
        this.server_status = true;
        return true;
    }

    public void shutdown() {
        DebugLogger.InfoLog("unbind = " + this.server_inet_socket_address.getHostName() + ":" + this.server_inet_socket_address.getPort() + " (" + this.server_name + ")");
        if (this.server_status) {
            getIoAcceptor().unbind(this.server_inet_socket_address);
        }
        this.server_status = false;
    }

    public String getName() {
        return this.server_name;
    }

    public int getNumberOfSessions() {
        return getIoAcceptor().getManagedSessions(this.server_inet_socket_address).size();
    }

    public boolean isShutdown() {
        return !this.server_status;
    }

    public boolean isAdminOnly() {
        return this.adminOnly;
    }

}
