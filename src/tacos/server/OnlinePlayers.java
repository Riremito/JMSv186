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
public class OnlinePlayers {

    private ArrayList<MapleCharacter> players = new ArrayList<>();

    public ArrayList<MapleCharacter> get() {
        return this.players;
    }

    public boolean add(MapleCharacter player) {
        if (this.players.contains(player)) {
            DebugLogger.ErrorLog("OnlinePlayers : add");
            return false;
        }

        this.players.add(player);
        return true;
    }

    public boolean remove(MapleCharacter player) {
        if (!this.players.contains(player)) {
            DebugLogger.ErrorLog("OnlinePlayers : remove");
            return false;
        }

        this.players.remove(player);
        return true;
    }

    public MapleCharacter findByName(String character_name) {
        for (MapleCharacter player : this.players) {
            if (player.getName().equals(character_name)) {
                return player;
            }
        }

        return null;
    }

    public MapleCharacter findById(int character_id) {
        for (MapleCharacter player : this.players) {
            if (player.getId() == character_id) {
                return player;
            }
        }

        return null;
    }

    public void disconnectAll() {
        DebugLogger.InfoLog("disconnectAll dc.");

        for (MapleCharacter player : this.players) {
            player.getClient().disconnect(false, false, true);
            player.getClient().getSession().close();
        }

        this.players.clear();
    }

}
