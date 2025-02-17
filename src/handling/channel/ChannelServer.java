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
package handling.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.MapleCharacter;
import config.ServerConfig;
import constants.ServerConstants;
import debug.Debug;
import handling.ByteArrayMaplePacket;
import handling.MaplePacket;
import handling.MapleServerHandler;
import handling.login.LoginServer;
import handling.mina.MapleCodecFactory;
import handling.world.CheaterData;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import scripting.EventScriptManager;
import server.MapleSquad;
import server.maps.MapleMapFactory;
import server.shops.HiredMerchant;
import server.life.PlayerNPC;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import packet.response.wrapper.ResWrapper;
import server.events.MapleCoconut;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.events.MapleFitness;
import server.events.MapleOla;
import server.events.MapleOxQuiz;
import server.events.MapleSnowball;
import tools.CollectionUtil;

public class ChannelServer implements Serializable {

    public static long serverStartTime;
    private int expRate, mesoRate, dropRate, cashRate;
    private short port = 8585;
    private int channel, running_MerchantID = 0, flags = 0;
    private String serverMessage, key, serverName;
    private boolean shutdown = false, finishedShutdown = false, MegaphoneMuteState = false, adminOnly = false;
    private PlayerStorage players;
    private MapleServerHandler serverHandler;
    private IoAcceptor acceptor;
    private final MapleMapFactory mapFactory;
    private EventScriptManager eventSM;
    private static final Map<Integer, ChannelServer> instances = new HashMap<Integer, ChannelServer>();
    private final Map<String, MapleSquad> mapleSquads = new HashMap<String, MapleSquad>();
    private final Map<Integer, HiredMerchant> merchants = new HashMap<Integer, HiredMerchant>();
    private final Map<Integer, PlayerNPC> playerNPCs = new HashMap<Integer, PlayerNPC>();
    private final ReentrantReadWriteLock merchLock = new ReentrantReadWriteLock(); //merchant
    private final ReentrantReadWriteLock squadLock = new ReentrantReadWriteLock(); //squad
    private int eventmap = -1;
    private final Map<MapleEventType, MapleEvent> events = new EnumMap<MapleEventType, MapleEvent>(MapleEventType.class);

    private ChannelServer(final String key, final int channel) {
        this.key = key;
        this.channel = channel;
        mapFactory = new MapleMapFactory();
        mapFactory.setChannel(channel);
    }

    public static Set<Integer> getAllInstance() {
        return new HashSet<Integer>(instances.keySet());
    }

    public static int getChannels() {
        return ServerConfig.game_server_channels;
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
        return ServerConfig.game_server_custom;
    }

    public final void run_startup_configurations() {
        setChannel(channel); //instances.put
        try {

            expRate = ServerConfig.game_server_expRate;
            mesoRate = ServerConfig.game_server_mesoRate;
            dropRate = ServerConfig.game_server_dropRate;
            serverMessage = ServerConfig.game_server_serverMessage;
            serverName = ServerConfig.game_server_serverName;
            flags = ServerConfig.game_server_flags;
            adminOnly = ServerConfig.game_server_adminOnly;

            // 壊れている可能性あり
            eventSM = new EventScriptManager(this, ServerConfig.game_server_events.split(","));

            port = (short) (ServerConfig.game_server_DEFAULT_PORT + channel - 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        acceptor = new SocketAcceptor();
        final SocketAcceptorConfig acceptor_config = new SocketAcceptorConfig();
        acceptor_config.getSessionConfig().setTcpNoDelay(true);
        acceptor_config.setDisconnectOnUnbind(true);
        acceptor_config.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));
        players = new PlayerStorage(channel);
        loadEvents();

        try {
            this.serverHandler = new MapleServerHandler(channel, MapleServerHandler.ServerType.GameServer);
            acceptor.bind(new InetSocketAddress(port), serverHandler, acceptor_config);
            Debug.InfoLog("Channel " + channel + " Port = " + port);
            eventSM.init();
        } catch (IOException e) {
            Debug.InfoLog("Binding to port " + port + " failed (ch: " + getChannel() + ")" + e);
        }
    }

    public final void shutdown(Object threadToNotify) {
        if (finishedShutdown) {
            return;
        }
        broadcastPacket(ResWrapper.serverNotice(0, "This channel will now shut down."));
        // dc all clients by hand so we get sessionClosed...
        shutdown = true;

        Debug.InfoLog("Channel " + channel + ", Saving hired merchants...");

        closeAllMerchant();

        Debug.InfoLog("Channel " + channel + ", Saving characters...");

        getPlayerStorage().disconnectAll();

        Debug.InfoLog("Channel " + channel + ", Unbinding...");

        acceptor.unbindAll();
        acceptor = null;

        //temporary while we dont have !addchannel
        instances.remove(channel);
        LoginServer.removeChannel(channel);
        setFinishShutdown();
//        if (threadToNotify != null) {
//            synchronized (threadToNotify) {
//                threadToNotify.notify();
//            }
//        }
    }

    public final void unbind() {
        acceptor.unbindAll();
    }

    public final boolean hasFinishedShutdown() {
        return finishedShutdown;
    }

    public final MapleMapFactory getMapFactory() {
        return mapFactory;
    }

    public static final ChannelServer newInstance(final String key, final int channel) {
        return new ChannelServer(key, channel);
    }

    public static final ChannelServer getInstance(final int channel) {
        return instances.get(channel);
    }

    // 接続人数
    public static int getPopulation(int channel) {
        ChannelServer ch = instances.get(channel);
        if (ch == null) {
            return 1000;
        }
        return ch.getPlayerStorage().getAllCharacters().size();
    }

    public final void addPlayer(final MapleCharacter chr) {
        getPlayerStorage().registerPlayer(chr);
        chr.getClient().getSession().write(ResWrapper.serverMessage(serverMessage));
    }

    public final PlayerStorage getPlayerStorage() {
        if (players == null) { //wth
            players = new PlayerStorage(channel); //wthhhh
        }
        return players;
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
        broadcastPacket(ResWrapper.serverMessage(serverMessage));
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
        LoginServer.addChannel(channel);
    }

    public static final Collection<ChannelServer> getAllInstances() {
        return Collections.unmodifiableCollection(instances.values());
    }

    public final int getPort() {
        return port;
    }

    public final boolean isShutdown() {
        return shutdown;
    }

    public final int getLoadedMaps() {
        return mapFactory.getLoadedMaps();
    }

    public final EventScriptManager getEventSM() {
        return eventSM;
    }

    public final void reloadEvents() {
        eventSM.cancel();
        eventSM = new EventScriptManager(this, ServerConfig.game_server_events.split(","));
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

    public static final void startChannel_Main() {
        serverStartTime = System.currentTimeMillis();

        for (int i = 0; i < ServerConfig.game_server_channels; i++) {
            newInstance(ServerConstants.Channel_Key[i], i + 1).run_startup_configurations();
        }
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

    public final void setShutdown() {
        this.shutdown = true;
        Debug.InfoLog("Channel " + channel + " has set to shutdown.");
    }

    public final void setFinishShutdown() {
        this.finishedShutdown = true;
        Debug.InfoLog("Channel " + channel + " has finished shutdown.");
    }

    public final boolean isAdminOnly() {
        return adminOnly;
    }

    public final static int getChannelCount() {
        return instances.size();
    }

    public final MapleServerHandler getServerHandler() {
        return serverHandler;
    }

    public final int getTempFlag() {
        return flags;
    }

    public static Map<Integer, Integer> getChannelLoad() {
        Map<Integer, Integer> ret = new HashMap<Integer, Integer>();
        for (ChannelServer cs : instances.values()) {
            ret.put(cs.getChannel(), cs.getConnectedClients());
        }
        return ret;
    }

    public int getConnectedClients() {
        return getPlayerStorage().getConnectedClients();
    }

    public List<CheaterData> getCheaters() {
        List<CheaterData> cheaters = getPlayerStorage().getCheaters();

        Collections.sort(cheaters);
        return CollectionUtil.copyFirst(cheaters, 20);
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
