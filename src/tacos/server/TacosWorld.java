/*
 * Copyright (C) 2026 Riremito
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
import odin.client.MapleCharacter;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class TacosWorld {

    private static ArrayList<TacosWorld> worlds = new ArrayList<>();

    public static ArrayList<TacosWorld> getWorlds() {
        return worlds;
    }

    public static TacosWorld find(int id) {
        for (TacosWorld world : worlds) {
            if (world.getId() == id) {
                return world;
            }
        }
        return null;
    }

    public static void add(TacosWorld world) {
        worlds.add(world);
    }

    private int id;
    private String name;
    private int flag;
    private String event_desc;
    private ArrayList<Server_Game> channels = new ArrayList<>();
    private Server_CashShop cashshop = null;
    private ArrayList<MapleCharacter> player_migrating = new ArrayList<>();

    public TacosWorld(int id, String name, int flag, String event_desc) {
        this.id = id;
        this.name = name;
        this.flag = flag; // world flag icon.
        this.event_desc = event_desc; // BMS24 does not support this.
    }

    public TacosWorld() {
        // to do remove.
    }

    public int getId() {
        return this.id;
    }

    public int getFlag() {
        return this.flag;
    }

    public String getName() {
        return this.name;
    }

    public String getEvent() {
        return this.event_desc;
    }

    public void addChannel(Server_Game channel) {
        this.channels.add(channel);
    }

    public ArrayList<Server_Game> getChannels() {
        return this.channels;
    }

    // from 1.
    public Server_Game getChannelServer(int channel) {
        for (Server_Game ch_server : this.channels) {
            if (ch_server.getChannel() == channel) {
                return ch_server;
            }
        }
        return null;
    }

    public void setCashShop(Server_CashShop cashshop) {
        this.cashshop = cashshop;
    }

    public Server_CashShop getCashShop() {
        return this.cashshop;
    }

    public boolean addMigratingPlayer(MapleCharacter player) {
        if (this.player_migrating.contains(player)) {
            DebugLogger.DebugLog("addMigratingPlayer : NG.");
            return false;
        }
        this.player_migrating.add(player);
        return true;
    }

    public MapleCharacter findMigratingPlayer(int character_id) {
        for (MapleCharacter chr_mig : this.player_migrating) {
            if (chr_mig.getId() == character_id) {
                return chr_mig;
            }
        }
        DebugLogger.DebugLog("findMigratingPlayer : NG.");
        return null;
    }

    public boolean removeMigratingPlayer(MapleCharacter player) {
        MapleCharacter chr_mig = findMigratingPlayer(player.getId());
        if (chr_mig == null) {
            DebugLogger.DebugLog("removeMigratingPlayer : NG.");
            return false;
        }
        this.player_migrating.remove(chr_mig);
        return true;
    }

}
