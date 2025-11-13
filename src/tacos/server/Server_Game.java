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

import tacos.property.Property_World;
import tacos.debug.DebugLogger;
import odin.handling.channel.PlayerStorage;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoServiceConfig;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.network.PacketHandler;
import tacos.network.PacketHandler_Game;

/**
 *
 * @author Riremito
 */
public class Server_Game extends Server {

    private int world;
    private int channel;
    private PlayerStorage players;

    public Server_Game(String server_name, String server_ip, int server_port, IoHandler ih, IoServiceConfig isc) {
        super(server_name, server_ip, server_port, ih, isc);
    }

    @Override
    public void shutdown() {
        getPlayerStorage().broadcastPacket(ResWrapper.BroadCastMsgNoticeOld("This channel will now shut down."));
        DebugLogger.InfoLog("Channel " + channel + ", Saving hired merchants...");
        ServerOdinGame.getInstance(channel).closeAllMerchant();
        DebugLogger.InfoLog("Channel " + channel + ", Saving characters...");
        getPlayerStorage().disconnectAll();
        DebugLogger.InfoLog("Channel " + channel + ", Unbinding...");
        ServerOdinLogin.removeChannel(channel);
        ServerOdinGame.getInstances().remove(channel);
        super.shutdown();
    }

    public PlayerStorage getPlayerStorage() {
        return this.players;
    }

    public static boolean init() {
        for (int i = 0; i < Property_World.getChannels(); i++) {
            int channel = i + 1;
            int channel_port = Property_World.getPort() + i;
            String channel_name = Property_World.getName() + "-" + channel;
            ServerOdinGame odin_game = ServerOdinGame.newInstance(channel);
            Server_Game server = new Server_Game(channel_name, "127.0.0.1", channel_port, new PacketHandler_Game(channel), PacketHandler.getSocketAcceptorConfig());
            server.world = 0;
            server.channel = channel;
            server.players = new PlayerStorage(channel);
            Server.add(server);
            server.run();
            odin_game.set(server);
            odin_game.run_startup_configurations(channel_port);
        }
        return true;
    }
}
