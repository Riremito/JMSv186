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
package handling.cashshop;

import config.property.Property_Shop;
import debug.DebugLogger;
import java.net.InetSocketAddress;
import handling.channel.PlayerStorage;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import server.MTSStorage;
import server.network.PacketHandler_CashShop;
import server.network.PacketHandler;

public class CashShopServer {

    private static InetSocketAddress InetSocketadd;
    private static IoAcceptor acceptor;
    private static PlayerStorage players, playersMTS;
    private static boolean finishedShutdown = false;

    public static final void run_startup_configurations() {
        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        acceptor = new SocketAcceptor();
        players = new PlayerStorage(-10);
        playersMTS = new PlayerStorage(-20);

        try {
            InetSocketadd = new InetSocketAddress(Property_Shop.getPort());
            acceptor.bind(InetSocketadd, new PacketHandler_CashShop(), PacketHandler.getSocketAcceptorConfig());
            DebugLogger.InfoLog("Port = " + Property_Shop.getPort());
        } catch (final Exception e) {
            DebugLogger.ErrorLog("Binding to port " + Property_Shop.getPort() + " failed");
            e.printStackTrace();
            throw new RuntimeException("Binding failed.", e);
        }
    }

    public static final PlayerStorage getPlayerStorage() {
        return players;
    }

    public static final PlayerStorage getPlayerStorageMTS() {
        return playersMTS;
    }

    public static final void shutdown() {
        if (finishedShutdown) {
            return;
        }
        DebugLogger.InfoLog("Saving all connected clients (CS)...");
        players.disconnectAll();
        playersMTS.disconnectAll();
        MTSStorage.getInstance().saveBuyNow(true);
        DebugLogger.InfoLog("Shutting down CS...");
        acceptor.unbindAll();
        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }
}
