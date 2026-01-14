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
import java.util.List;
import tacos.property.Property_World;
import tacos.debug.DebugLogger;
import odin.handling.channel.PlayerStorage;
import odin.server.maps.MapleMapFactory;
import tacos.config.Region;
import tacos.constants.TacosConstants;
import tacos.network.MaplePacket;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.network.PacketHandler_Game;
import tacos.property.Property_Dummy_World;

/**
 *
 * @author Riremito
 */
public class Server_Game extends TacosServer {

    private TacosWorld world = null;
    private int channel;
    private int language = 0;
    private MapleMapFactory mapFactory = null;
    private PlayerStorage players;
    private String serverMessage;
    private int expRate;
    private int mesoRate;
    private int dropRate;

    public Server_Game(String server_name, int channel, int language) {
        super(server_name);
        setType(TacosServerType.GAME_SERVER);
        this.channel = channel; // from 1.
        this.language = language; // EMS
        this.mapFactory = new MapleMapFactory();
    }

    @Override
    public void shutdown() {
        broadcastPacket(ResWrapper.BroadCastMsgNoticeOld("This channel will now shut down."));
        DebugLogger.InfoLog("Channel " + this.channel + ", Saving hired merchants...");
        ServerOdinGame.getInstance(this.channel).closeAllMerchant();
        DebugLogger.InfoLog("Channel " + this.channel + ", Saving characters...");
        getPlayerStorage().disconnectAll();
        DebugLogger.InfoLog("Channel " + this.channel + ", Unbinding...");
        ServerOdinGame.getInstances().remove(this.channel);
        super.shutdown();
    }

    public void broadcastPacket(MaplePacket packet) {
        getPlayerStorage().broadcastPacket(packet);
    }

    public void broadcastMegaphonePacket(MaplePacket packet) {
        getPlayerStorage().broadcastSmegaPacket(packet);
    }

    public TacosWorld getWorld() {
        return this.world;
    }

    public void setWorld(TacosWorld world) {
        this.world = world;
    }

    public int getChannel() {
        return this.channel;
    }

    public int getLanguage() {
        return this.language;
    }

    public MapleMapFactory getMapFactory() {
        return this.mapFactory;
    }

    public PlayerStorage getPlayerStorage() {
        return this.players;
    }

    public String getServerMessage() {
        return this.serverMessage;
    }

    public void setServerMessage(String newMessage) {
        this.serverMessage = newMessage;
    }

    public final int getExpRate() {
        return this.expRate;
    }

    public final int getMesoRate() {
        return this.mesoRate;
    }

    public final int getDropRate() {
        return this.dropRate;
    }

    public static List<Server_Game> init() {
        List<Server_Game> game_servers = new ArrayList<>();
        TacosWorld world = new TacosWorld(0, Property_World.getName(), Property_World.getFlags(), Property_World.getEvent());
        TacosWorld.add(world);

        for (int i = 0; i < Property_World.getChannels(); i++) {
            int channel = i + 1;
            int channel_port = Property_World.getPort() + i;
            String channel_name = Property_World.getName() + "-" + channel;
            ServerOdinGame odin_game = ServerOdinGame.newInstance(channel);
            int language = Region.check(Region.EMS) ? i % Property_World.getLanguages() : 0;
            Server_Game server = new Server_Game(channel_name, channel, language);
            server.mapFactory.setChannel(channel);
            server.players = new PlayerStorage(channel);
            TacosServer.add(server);
            server.setGlobalIP(TacosConstants.SERVER_GLOBAL_IP);
            server.run(TacosConstants.SERVER_LOCAL_IP, channel_port, new PacketHandler_Game(server, channel));
            odin_game.set(server);
            odin_game.run_startup_configurations(channel_port);
            game_servers.add(server);
            server.setWorld(world);
            world.addChannel(server);
            // property
            server.serverMessage = Property_World.getMessage();
            server.expRate = Property_World.getRateExp();
            server.mesoRate = Property_World.getRateMeso();
            server.dropRate = Property_World.getRateDrop();
        }

        for (int i = 1; i <= 1; i++) {
            createDummyWorld(i);
        }
        return game_servers;
    }

    public static void createDummyWorld(int dummy_world_id) {
        TacosWorld dummy_world = new TacosWorld(dummy_world_id, Property_Dummy_World.getName(), Property_Dummy_World.getFlags(), Property_Dummy_World.getEvent());
        TacosWorld.add(dummy_world);

        for (int i = 0; i < Property_Dummy_World.getChannels(); i++) {
            int channel = i + 1;
            String channel_name = Property_Dummy_World.getName() + "-" + channel;
            int language = Region.check(Region.EMS) ? i % Property_Dummy_World.getLanguages() : 0;
            Server_Game server = new Server_Game(channel_name, channel, language);
            server.setGlobalIP(TacosConstants.SERVER_GLOBAL_IP);
            server.setWorld(dummy_world);
            dummy_world.addChannel(server);
        }
    }

}
