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

import debug.DebugLogger;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.transport.socket.nio.SocketAcceptor;

/**
 *
 * @author Riremito
 */
public class Server {

    protected static ArrayList<Server> server_list = new ArrayList<>();
    private static IoAcceptor server_io_acceptor = null;

    public static ArrayList<Server> get() {
        return server_list;
    }

    public static Server add(String server_name, String server_ip, int server_port, IoHandler ih, IoServiceConfig isc) {
        Server server = new Server(server_name, server_ip, server_port, ih, isc);
        server_list.add(server);
        return server;
    }

    public static void add(Server server) {
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

    public Server(String server_name, String server_ip, int server_port, IoHandler ih, IoServiceConfig isc) {
        this.server_name = server_name;
        this.server_ip = server_ip;
        this.server_port = server_port;
        this.server_io_handler = ih;
        this.server_service_config = isc;
    }

    public boolean run() {
        this.server_inet_socket_address = new InetSocketAddress(this.server_ip, this.server_port);

        try {
            getIoAcceptor().bind(this.server_inet_socket_address, this.server_io_handler, this.server_service_config);
        } catch (IOException e) {
            DebugLogger.ErrorLog(this.server_ip + ":" + this.server_port + ", failed : " + e);
            return false;
        }

        DebugLogger.InfoLog(this.server_inet_socket_address.getHostName() + ":" + this.server_inet_socket_address.getPort());
        this.server_status = true;
        return true;
    }

    public void shutdown() {
        getIoAcceptor().unbind(this.server_inet_socket_address);
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
