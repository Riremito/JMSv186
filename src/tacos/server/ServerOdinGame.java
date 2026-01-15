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
import java.util.Map;
import odin.handling.channel.PlayerStorage;
import odin.server.MapleSquad;
import odin.server.maps.MapleMapFactory;
import java.util.HashSet;
import java.util.Set;

public class ServerOdinGame {

    private TacosChannel server_game = null;
    private static final Map<Integer, ServerOdinGame> instances = new HashMap<Integer, ServerOdinGame>();

    public void set(TacosChannel server) {
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

    private int channel;
    private final Map<String, MapleSquad> mapleSquads = new HashMap<>();

    private ServerOdinGame(int channel) {
        this.channel = channel;
    }

    public static Set<Integer> getAllInstance() {
        return new HashSet<>(instances.keySet());
    }

    public void run_startup_configurations(int port) {
        setChannel(channel);
    }

    public static ServerOdinGame newInstance(int channel) {
        return new ServerOdinGame(channel);
    }

    public static ServerOdinGame getInstance(int channel) {
        return instances.get(channel);
    }

    public void removePlayer(int idz, String namez) {
        getPlayerStorage().deregisterPlayer(idz, namez);

    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        instances.put(channel, this);
    }

    public static Collection<ServerOdinGame> getAllInstances() {
        return Collections.unmodifiableCollection(instances.values());
    }

    public Map<String, MapleSquad> getAllSquads() {
        return Collections.unmodifiableMap(mapleSquads);
    }

    public MapleSquad getMapleSquad(String type) {
        return mapleSquads.get(type.toLowerCase());
    }

    public boolean addMapleSquad(MapleSquad squad, String type) {
        if (!mapleSquads.containsKey(type.toLowerCase())) {
            mapleSquads.put(type.toLowerCase(), squad);
            return true;
        }
        return false;
    }

    public boolean removeMapleSquad(String type) {
        if (mapleSquads.containsKey(type.toLowerCase())) {
            mapleSquads.remove(type.toLowerCase());
            return true;
        }
        return false;
    }

}
