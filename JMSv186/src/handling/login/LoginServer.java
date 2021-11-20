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

import debug.Debug;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import handling.MapleServerHandler;
import handling.mina.MapleCodecFactory;
import java.util.Properties;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.common.IoAcceptor;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import server.ServerProperties;

public class LoginServer {

    private static InetSocketAddress InetSocketadd;
    private static IoAcceptor acceptor;
    private static Map<Integer, Integer> load = new HashMap<Integer, Integer>();
    private static int userLimit, usersOn = 0;
    private static boolean finishedShutdown = true, adminOnly = false;
    private static int PORT;

    private static final int WolrdLimit = 20;
    public static int NumberOfWorld = 0;
    public static final short WorldPort[] = new short[WolrdLimit];
    public static final String WorldEvent[] = new String[WolrdLimit];
    public static final String WorldMessage[] = new String[WolrdLimit];
    public static final String WorldName[] = new String[WolrdLimit];
    public static final int WorldFlag[] = new int[WolrdLimit];
    public static final int WorldChannels[] = new int[WolrdLimit];

    public static final void LoadConfig() {
        Properties p = ServerProperties.LoadConfig("properties/login.properties");
        PORT = Integer.parseInt(p.getProperty("server.port"));
        userLimit = Integer.parseInt(p.getProperty("server.userlimit"));
    }

    public static final Properties LoadWorldConfig(final String server) {
        Properties p = ServerProperties.LoadConfig("properties/" + server + ".properties");
        return p;
    }

    public static final void SetWorldConfig() {
        final String worldlist[] = {"kaede", "momiji"};

        for (String wolrd : worldlist) {
            Properties p = LoadWorldConfig(wolrd);
            WorldChannels[NumberOfWorld] = Integer.parseInt(p.getProperty("server.channels"));
            WorldPort[NumberOfWorld] = Short.parseShort(p.getProperty("server.port"));
            WorldName[NumberOfWorld] = p.getProperty("server.name");
            WorldEvent[NumberOfWorld] = p.getProperty("server.event");
            WorldMessage[NumberOfWorld] = p.getProperty("server.message");
            WorldFlag[NumberOfWorld] = Integer.parseInt(p.getProperty("server.flags"));

            NumberOfWorld++;
        }
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
            InetSocketadd = new InetSocketAddress(PORT);
            acceptor.bind(InetSocketadd, new MapleServerHandler(-1, MapleServerHandler.ServerType.LoginServer), cfg);
            Debug.InfoLog("Login Server Port = " + PORT);
        } catch (IOException e) {
            Debug.InfoLog("Binding to port " + PORT + " failed" + e);
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
        return userLimit;
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
