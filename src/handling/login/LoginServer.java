/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.login;

import config.ServerConfig;
import debug.Debug;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import handling.MapleServerHandler;
import handling.mina.MapleCodecFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.common.IoAcceptor;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketAcceptor;

public class LoginServer {

    private static InetSocketAddress InetSocketadd;
    private static IoAcceptor acceptor;
    private static Map<Integer, Integer> load = new HashMap<Integer, Integer>();
    private static int usersOn = 0;
    private static boolean finishedShutdown = true, adminOnly = false;

    private static final int WolrdLimit = 20;
    public static int NumberOfWorld = 0;
    public static final int WorldPort[] = new int[WolrdLimit];
    public static final String WorldEvent[] = new String[WolrdLimit];
    public static final String WorldMessage[] = new String[WolrdLimit];
    public static final String WorldName[] = new String[WolrdLimit];
    public static final int WorldFlag[] = new int[WolrdLimit];
    public static final int WorldChannels[] = new int[WolrdLimit];

    public static final void SetWorldConfig() {
        // Kaede
        WorldChannels[NumberOfWorld] = ServerConfig.game_server_channels;
        WorldPort[NumberOfWorld] = ServerConfig.game_server_DEFAULT_PORT;
        WorldName[NumberOfWorld] = ServerConfig.game_server_serverName;
        WorldEvent[NumberOfWorld] = ServerConfig.game_server_event;
        WorldMessage[NumberOfWorld] = ServerConfig.game_server_serverMessage;
        WorldFlag[NumberOfWorld] = ServerConfig.game_server_flags;
        NumberOfWorld++;
        // Momiji, not used
        WorldChannels[NumberOfWorld] = ServerConfig.test_game_server_channels;
        WorldPort[NumberOfWorld] = ServerConfig.test_game_server_DEFAULT_PORT;
        WorldName[NumberOfWorld] = ServerConfig.test_game_server_serverName;
        WorldEvent[NumberOfWorld] = ServerConfig.test_game_server_event;
        WorldMessage[NumberOfWorld] = ServerConfig.test_game_server_serverMessage;
        WorldFlag[NumberOfWorld] = ServerConfig.test_game_server_flags;
        NumberOfWorld++;
    }

    public static final void addChannel(final int channel) {
        load.put(channel, 0);
    }

    public static final void removeChannel(final int channel) {
        load.remove(channel);
    }

    public static final void run_startup_configurations() {

        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        acceptor = new SocketAcceptor();
        final SocketAcceptorConfig cfg = new SocketAcceptorConfig();
        cfg.getSessionConfig().setTcpNoDelay(true);
        cfg.setDisconnectOnUnbind(true);
        cfg.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));

        try {
            InetSocketadd = new InetSocketAddress(ServerConfig.login_server_port);
            acceptor.bind(InetSocketadd, new MapleServerHandler(-1, MapleServerHandler.ServerType.LoginServer), cfg);
            Debug.InfoLog("Port = " + ServerConfig.login_server_port);
        } catch (IOException e) {
            Debug.ErrorLog("Binding to port " + ServerConfig.login_server_port + " failed" + e);
        }
    }

    public static final void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("Shutting down login...");
        acceptor.unbindAll();
        finishedShutdown = true; //nothing. lol
    }

    public static final Map<Integer, Integer> getLoad() {
        return load;
    }

    public static void setLoad(final Map<Integer, Integer> load_, final int usersOn_) {
        load = load_;
        usersOn = usersOn_;
    }

    public static final int getUserLimit() {
        return ServerConfig.login_server_userlimit;
    }

    public static final int getUsersOn() {
        return usersOn;
    }

    public static final int getNumberOfSessions() {
        return acceptor.getManagedSessions(InetSocketadd).size();
    }

    public static final boolean isAdminOnly() {
        return adminOnly;
    }

    public static final boolean isShutdown() {
        return finishedShutdown;
    }

    public static final void setOn() {
        finishedShutdown = false;
    }
}
