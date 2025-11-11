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
package server.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import odin.client.MapleCharacter;
import config.ContentCustom;
import config.property.Property_World;
import odin.handling.channel.PlayerStorage;
import server.network.ByteArrayMaplePacket;
import server.network.MaplePacket;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import odin.scripting.EventScriptManager;
import odin.server.MapleSquad;
import odin.server.maps.MapleMapFactory;
import odin.server.shops.HiredMerchant;
import odin.server.life.PlayerNPC;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import packet.response.wrapper.ResWrapper;
import odin.server.events.MapleCoconut;
import odin.server.events.MapleEvent;
import odin.server.events.MapleEventType;
import odin.server.events.MapleFitness;
import odin.server.events.MapleOla;
import odin.server.events.MapleOxQuiz;
import odin.server.events.MapleSnowball;

public class ServerOdinGame {

    private Server_Game server_game = null;
    private static final Map<Integer, ServerOdinGame> instances = new HashMap<Integer, ServerOdinGame>();

    public void set(Server_Game server) {
        this.server_game = server;
    }

    public boolean isAdminOnly() {
        return this.server_game.isAdminOnly();
    }

    public boolean isShutdown() {
        return server_game.isShutdown();
    }

    public static Map<Integer, ServerOdinGame> getInstances() {
        return instances;
    }

    public PlayerStorage getPlayerStorage() {
        return this.server_game.getPlayerStorage();
    }

    private int expRate, mesoRate, dropRate, cashRate;
    private short port = 8585;
    private int channel, running_MerchantID = 0, flags = 0;
    private String serverMessage, serverName;
    private boolean MegaphoneMuteState = false;
    private final MapleMapFactory mapFactory;
    private EventScriptManager eventSM;
    private final Map<String, MapleSquad> mapleSquads = new HashMap<String, MapleSquad>();
    private final Map<Integer, HiredMerchant> merchants = new HashMap<Integer, HiredMerchant>();
    private final Map<Integer, PlayerNPC> playerNPCs = new HashMap<Integer, PlayerNPC>();
    private final ReentrantReadWriteLock merchLock = new ReentrantReadWriteLock(); //merchant
    private final ReentrantReadWriteLock squadLock = new ReentrantReadWriteLock(); //squad
    private int eventmap = -1;
    private final Map<MapleEventType, MapleEvent> events = new EnumMap<MapleEventType, MapleEvent>(MapleEventType.class);

    private ServerOdinGame(int channel) {
        this.channel = channel;
        mapFactory = new MapleMapFactory();
        mapFactory.setChannel(channel);
    }

    public static Set<Integer> getAllInstance() {
        return new HashSet<Integer>(instances.keySet());
    }

    public static int getChannels() {
        return Property_World.getChannels();
    }

    public final void loadEvents() {
        if (events.size() != 0) {
            return;
        }
        events.put(MapleEventType.Coconut, new MapleCoconut(channel, MapleEventType.Coconut.mapids));
        events.put(MapleEventType.Fitness, new MapleFitness(channel, MapleEventType.Fitness.mapids));
        events.put(MapleEventType.OlaOla, new MapleOla(channel, MapleEventType.OlaOla.mapids));
        events.put(MapleEventType.OxQuiz, new MapleOxQuiz(channel, MapleEventType.OxQuiz.mapids));
        events.put(MapleEventType.Snowball, new MapleSnowball(channel, MapleEventType.Snowball.mapids));
    }

    // 独自仕様かどうか
    public static boolean IsCustom() {
        return ContentCustom.CC_WZ_MAP_ADDED.get();
    }

    public final void run_startup_configurations(int port) {
        setChannel(channel);
        this.port = (short) port;
        expRate = Property_World.getRateExp();
        mesoRate = Property_World.getRateMeso();
        dropRate = Property_World.getRateDrop();
        serverMessage = Property_World.getMessage();
        serverName = Property_World.getName();
        flags = Property_World.getFlags();
        //adminOnly = Property_World.getAdminOnly();

        // 読み込み遅い
        try {
            // 壊れている可能性あり
            eventSM = new EventScriptManager(this, Property_World.getEvents().split(","));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        loadEvents();
        eventSM.init();
    }

    public final MapleMapFactory getMapFactory() {
        return mapFactory;
    }

    public static final ServerOdinGame newInstance(int channel) {
        return new ServerOdinGame(channel);
    }

    public static final ServerOdinGame getInstance(final int channel) {
        return instances.get(channel);
    }

    // 接続人数
    public static int getPopulation(int channel) {
        ServerOdinGame ch = instances.get(channel);
        if (ch == null) {
            return 1000;
        }
        return ch.getPlayerStorage().getAllCharacters().size();
    }

    public final void addPlayer(final MapleCharacter chr) {
        getPlayerStorage().registerPlayer(chr);
    }

    public final void removePlayer(final MapleCharacter chr) {
        getPlayerStorage().deregisterPlayer(chr);

    }

    public final void removePlayer(final int idz, final String namez) {
        getPlayerStorage().deregisterPlayer(idz, namez);

    }

    public final String getServerMessage() {
        return serverMessage;
    }

    public final void setServerMessage(final String newMessage) {
        serverMessage = newMessage;
        broadcastPacket(ResWrapper.BroadCastMsgSlide(serverMessage));
    }

    public final void broadcastPacket(final MaplePacket data) {
        getPlayerStorage().broadcastPacket(data);
    }

    public final void broadcastSmegaPacket(final MaplePacket data) {
        getPlayerStorage().broadcastSmegaPacket(data);
    }

    public final void broadcastGMPacket(final MaplePacket data) {
        getPlayerStorage().broadcastGMPacket(data);
    }

    public final int getExpRate() {
        return expRate;
    }

    public final void setExpRate(final int expRate) {
        this.expRate = expRate;
    }

    public final int getChannel() {
        return channel;
    }

    public final void setChannel(final int channel) {
        instances.put(channel, this);
        ServerOdinLogin.addChannel(channel);
    }

    public static final Collection<ServerOdinGame> getAllInstances() {
        return Collections.unmodifiableCollection(instances.values());
    }

    public final int getPort() {
        return port;
    }

    public final int getLoadedMaps() {
        return mapFactory.getLoadedMaps();
    }

    public final EventScriptManager getEventSM() {
        return eventSM;
    }

    public final void reloadEvents() {
        eventSM.cancel();
        eventSM = new EventScriptManager(this, Property_World.getEvents().split(","));
        eventSM.init();
    }

    public final int getMesoRate() {
        return mesoRate;
    }

    public final void setMesoRate(final int mesoRate) {
        this.mesoRate = mesoRate;
    }

    public final int getDropRate() {
        return dropRate;
    }

    public final void setDropRate(final int dropRate) {
        this.dropRate = dropRate;
    }

    public Map<String, MapleSquad> getAllSquads() {
        squadLock.readLock().lock();
        try {
            return Collections.unmodifiableMap(mapleSquads);
        } finally {
            squadLock.readLock().unlock();
        }
    }

    public final MapleSquad getMapleSquad(final String type) {
        squadLock.readLock().lock();
        try {
            return mapleSquads.get(type.toLowerCase());
        } finally {
            squadLock.readLock().unlock();
        }
    }

    public final boolean addMapleSquad(final MapleSquad squad, final String type) {
        squadLock.writeLock().lock();
        try {
            if (!mapleSquads.containsKey(type.toLowerCase())) {
                mapleSquads.put(type.toLowerCase(), squad);
                return true;
            }
        } finally {
            squadLock.writeLock().unlock();
        }
        return false;
    }

    public final boolean removeMapleSquad(final String type) {
        squadLock.writeLock().lock();
        try {
            if (mapleSquads.containsKey(type.toLowerCase())) {
                mapleSquads.remove(type.toLowerCase());
                return true;
            }
        } finally {
            squadLock.writeLock().unlock();
        }
        return false;
    }

    public final void closeAllMerchant() {
        merchLock.writeLock().lock();
        try {
            final Iterator<HiredMerchant> merchants_ = merchants.values().iterator();
            while (merchants_.hasNext()) {
                merchants_.next().closeShop(true, false, 0);
                merchants_.remove();
            }
        } finally {
            merchLock.writeLock().unlock();
        }
    }

    public final int addMerchant(final HiredMerchant hMerchant) {
        merchLock.writeLock().lock();

        int runningmer = 0;
        try {
            runningmer = running_MerchantID;
            merchants.put(running_MerchantID, hMerchant);
            running_MerchantID++;
        } finally {
            merchLock.writeLock().unlock();
        }
        return runningmer;
    }

    public final void removeMerchant(final HiredMerchant hMerchant) {
        merchLock.writeLock().lock();

        try {
            merchants.remove(hMerchant.getStoreId());
        } finally {
            merchLock.writeLock().unlock();
        }
    }

    public final boolean containsMerchant(final int accid) {
        boolean contains = false;

        merchLock.readLock().lock();
        try {
            final Iterator itr = merchants.values().iterator();

            while (itr.hasNext()) {
                if (((HiredMerchant) itr.next()).getOwnerAccId() == accid) {
                    contains = true;
                    break;
                }
            }
        } finally {
            merchLock.readLock().unlock();
        }
        return contains;
    }

    public final List<HiredMerchant> searchMerchant(final int itemSearch) {
        final List<HiredMerchant> list = new LinkedList<HiredMerchant>();
        merchLock.readLock().lock();
        try {
            final Iterator itr = merchants.values().iterator();

            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if (hm.searchItem(itemSearch).size() > 0) {
                    list.add(hm);
                }
            }
        } finally {
            merchLock.readLock().unlock();
        }
        return list;
    }

    public final void toggleMegaphoneMuteState() {
        this.MegaphoneMuteState = !this.MegaphoneMuteState;
    }

    public final boolean getMegaphoneMuteState() {
        return MegaphoneMuteState;
    }

    public int getEvent() {
        return eventmap;
    }

    public final void setEvent(final int ze) {
        this.eventmap = ze;
    }

    public MapleEvent getEvent(final MapleEventType t) {
        return events.get(t);
    }

    public final Collection<PlayerNPC> getAllPlayerNPC() {
        return playerNPCs.values();
    }

    public final PlayerNPC getPlayerNPC(final int id) {
        return playerNPCs.get(id);
    }

    public final void addPlayerNPC(final PlayerNPC npc) {
        if (playerNPCs.containsKey(npc.getId())) {
            removePlayerNPC(npc);
        }
        playerNPCs.put(npc.getId(), npc);
        getMapFactory().getMap(npc.getMapId()).addMapObject(npc);
    }

    public final void removePlayerNPC(final PlayerNPC npc) {
        if (playerNPCs.containsKey(npc.getId())) {
            playerNPCs.remove(npc.getId());
            getMapFactory().getMap(npc.getMapId()).removeMapObject(npc);
        }
    }

    public final String getServerName() {
        return serverName;
    }

    public final void setServerName(final String sn) {
        this.serverName = sn;
    }

    public static final Set<Integer> getChannelServer() {
        return new HashSet<Integer>(instances.keySet());
    }

    public final static int getChannelCount() {
        return instances.size();
    }

    public final int getTempFlag() {
        return flags;
    }

    public static Map<Integer, Integer> getChannelLoad() {
        Map<Integer, Integer> ret = new HashMap<Integer, Integer>();
        for (ServerOdinGame cs : instances.values()) {
            ret.put(cs.getChannel(), cs.getConnectedClients());
        }
        return ret;
    }

    public int getConnectedClients() {
        return getPlayerStorage().getConnectedClients();
    }

    public void broadcastMessage(byte[] message) {
        broadcastPacket(new ByteArrayMaplePacket(message));
    }

    public void broadcastSmega(byte[] message) {
        broadcastSmegaPacket(new ByteArrayMaplePacket(message));
    }

    public void broadcastGMMessage(byte[] message) {
        broadcastGMPacket(new ByteArrayMaplePacket(message));
    }
}
