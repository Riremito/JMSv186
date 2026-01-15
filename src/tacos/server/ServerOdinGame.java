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
package tacos.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import odin.handling.channel.PlayerStorage;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import odin.server.MapleSquad;
import odin.server.maps.MapleMapFactory;
import odin.server.shops.HiredMerchant;
import odin.server.life.PlayerNPC;
import java.util.HashSet;
import java.util.Set;

public class ServerOdinGame {

    private Server_Game server_game = null;
    private static final Map<Integer, ServerOdinGame> instances = new HashMap<Integer, ServerOdinGame>();

    public void set(Server_Game server) {
        this.server_game = server;
    }

    public boolean isShutdown() {
        return server_game.isShutdown();
    }

    public static Map<Integer, ServerOdinGame> getInstances() {
        return instances;
    }

    public MapleMapFactory getMapFactory() {
        return this.server_game.getMapFactory();
    }

    public PlayerStorage getPlayerStorage() {
        return this.server_game.getPlayerStorage();
    }

    private int channel, running_MerchantID = 0;
    private final Map<String, MapleSquad> mapleSquads = new HashMap<>();
    private final Map<Integer, HiredMerchant> merchants = new HashMap<>();
    private final Map<Integer, PlayerNPC> playerNPCs = new HashMap<>();
    private final ReentrantReadWriteLock merchLock = new ReentrantReadWriteLock(); //merchant
    private final ReentrantReadWriteLock squadLock = new ReentrantReadWriteLock(); //squad
    private int eventmap = -1;

    private ServerOdinGame(int channel) {
        this.channel = channel;
    }

    public static Set<Integer> getAllInstance() {
        return new HashSet<>(instances.keySet());
    }

    public final void run_startup_configurations(int port) {
        setChannel(channel);
    }

    public static final ServerOdinGame newInstance(int channel) {
        return new ServerOdinGame(channel);
    }

    public static final ServerOdinGame getInstance(final int channel) {
        return instances.get(channel);
    }

    public final void removePlayer(final int idz, final String namez) {
        getPlayerStorage().deregisterPlayer(idz, namez);

    }

    public final int getChannel() {
        return channel;
    }

    public final void setChannel(final int channel) {
        instances.put(channel, this);
    }

    public static final Collection<ServerOdinGame> getAllInstances() {
        return Collections.unmodifiableCollection(instances.values());
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
        final List<HiredMerchant> list = new LinkedList<>();
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

}
