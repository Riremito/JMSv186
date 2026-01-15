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
import odin.server.maps.MapleMapFactory;

public class ServerOdinGame {

    private TacosChannel server_game = null;
    private static final Map<Integer, ServerOdinGame> instances = new HashMap<Integer, ServerOdinGame>();

    public void set(TacosChannel server) {
        this.server_game = server;
    }

    public boolean isShutdown() {
        return server_game.isShutdown();
    }

    public MapleMapFactory getMapFactory() {
        return this.server_game.getMapFactory();
    }

    public PlayerStorage getPlayerStorage() {
        return this.server_game.getPlayerStorage();
    }

    private int channel;

    private ServerOdinGame(int channel) {
        this.channel = channel;
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

    public void setChannel(int channel) {
        instances.put(channel, this);
    }

    public static Collection<ServerOdinGame> getAllInstances() {
        return Collections.unmodifiableCollection(instances.values());
    }

}
