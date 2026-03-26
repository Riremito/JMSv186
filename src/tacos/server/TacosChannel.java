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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import odin.client.MapleCharacter;
import tacos.property.Property_World;
import tacos.debug.DebugLogger;
import odin.server.MapleSquad;
import odin.server.maps.MapleMapFactory;
import odin.server.shops.HiredMerchant;
import tacos.config.Region;
import tacos.network.MaplePacket;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.network.PacketHandler_Game;
import tacos.property.Property_Dummy_World;

/**
 *
 * @author Riremito
 */
public class TacosChannel extends TacosServer {

    private TacosWorld world = null;
    private int channel;
    private int language = 0;
    private MapleMapFactory mapFactory = null;
    private OnlinePlayers onlines;
    private String serverMessage;
    private int expRate;
    private int mesoRate;
    private int dropRate;

    public TacosChannel(String server_name, int channel, int language) {
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
        closeAllMerchant();
        DebugLogger.InfoLog("Channel " + this.channel + ", Saving characters...");
        getOnlinePlayers().disconnectAll();
        DebugLogger.InfoLog("Channel " + this.channel + ", Unbinding...");
        super.shutdown();
    }

    public void broadcastPacket(MaplePacket packet) {
        for (MapleCharacter player : getOnlinePlayers().get()) {
            player.SendPacket(packet);
        }
    }

    public void broadcastMegaphonePacket(MaplePacket packet) {
        for (MapleCharacter player : getOnlinePlayers().get()) {
            if (!player.getSmega()) {
                continue;
            }
            player.SendPacket(packet);
        }
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

    public OnlinePlayers getOnlinePlayers() {
        return this.onlines;
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

    // merch
    private int running_MerchantID = 0;
    private Map<Integer, HiredMerchant> merchants = new HashMap<>();

    public void closeAllMerchant() {
        for (HiredMerchant hm : this.merchants.values()) {
            hm.closeShop(true, false, 0);
        }
        this.merchants.clear();
    }

    public int addMerchant(HiredMerchant hMerchant) {
        int shop_id = this.running_MerchantID;
        this.merchants.put(shop_id, hMerchant);
        this.running_MerchantID++;
        return shop_id;
    }

    public void removeMerchant(HiredMerchant hMerchant) {
        this.merchants.remove(hMerchant.getStoreId());
    }

    public boolean containsMerchant(int character_id) {
        for (HiredMerchant hm : this.merchants.values()) {
            if (hm.getOwnerAccId() == character_id) {
                return true;
            }
        }
        return false;
    }

    public List<HiredMerchant> searchMerchant(int item_id) {
        List<HiredMerchant> list = new LinkedList<>();
        for (HiredMerchant hm : this.merchants.values()) {
            if (hm.searchItem(item_id).size() > 0) {
                list.add(hm);
            }
        }
        return list;
    }

    // 遠征隊
    private Map<String, MapleSquad> mapleSquads = new HashMap<>();

    public MapleSquad getMapleSquad(String type) {
        return this.mapleSquads.get(type.toLowerCase());
    }

    public boolean addMapleSquad(MapleSquad squad, String type) {
        if (this.mapleSquads.containsKey(type.toLowerCase())) {
            return false;
        }
        this.mapleSquads.put(type.toLowerCase(), squad);
        return true;
    }

    public boolean removeMapleSquad(String type) {
        if (this.mapleSquads.containsKey(type.toLowerCase())) {
            this.mapleSquads.remove(type.toLowerCase());
            return true;
        }
        return false;
    }

    // init
    public static void init() {
        TacosWorld world = TacosWorld.find(0);

        for (int i = 0; i < Property_World.getChannels(); i++) {
            int channel = i + 1;
            int channel_port = Property_World.getPort() + i;
            String channel_name = Property_World.getName() + "-" + channel;
            int language = Region.check(Region.EMS) ? i % Property_World.getLanguages() : 0;
            TacosChannel server = new TacosChannel(channel_name, channel, language);
            server.mapFactory.setChannel(channel);
            server.onlines = new OnlinePlayers();
            TacosServer.add(server);
            server.run(Property_World.getIP(), channel_port, new PacketHandler_Game(server, channel));
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
    }

    public static void createDummyWorld(int dummy_world_id) {
        TacosWorld dummy_world = new TacosWorld(dummy_world_id, Property_Dummy_World.getName(), Property_Dummy_World.getFlags(), Property_Dummy_World.getEvent());
        TacosWorld.add(dummy_world);

        for (int i = 0; i < Property_Dummy_World.getChannels(); i++) {
            int channel = i + 1;
            String channel_name = Property_Dummy_World.getName() + "-" + channel;
            int language = Region.check(Region.EMS) ? i % Property_Dummy_World.getLanguages() : 0;
            TacosChannel server = new TacosChannel(channel_name, channel, language);
            server.setWorld(dummy_world);
            dummy_world.addChannel(server);
            server.onlines = new OnlinePlayers();
        }
    }

}
