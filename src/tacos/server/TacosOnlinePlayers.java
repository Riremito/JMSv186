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
import tacos.client.TacosCharacter;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class TacosOnlinePlayers {

    private ArrayList<TacosCharacter> players = new ArrayList<>();

    public ArrayList<TacosCharacter> get() {
        return this.players;
    }

    public boolean add(TacosCharacter player) {
        if (this.players.contains(player)) {
            DebugLogger.ErrorLog("OnlinePlayers : add");
            return false;
        }

        this.players.add(player);
        return true;
    }

    public boolean remove(TacosCharacter player) {
        if (!this.players.contains(player)) {
            DebugLogger.ErrorLog("OnlinePlayers : remove");
            return false;
        }

        this.players.remove(player);
        return true;
    }

    public TacosCharacter findByName(String character_name) {
        for (TacosCharacter player : this.players) {
            if (player.getName().equals(character_name)) {
                return player;
            }
        }

        return null;
    }

    public TacosCharacter findById(int character_id) {
        for (TacosCharacter player : this.players) {
            if (player.getId() == character_id) {
                return player;
            }
        }

        return null;
    }

    public void disconnectAll() {
        DebugLogger.InfoLog("disconnectAll dc.");

        for (TacosCharacter player : this.players) {
            player.getClient().disconnect(false, true);
            player.getClient().getSession().close();
        }

        this.players.clear();
    }
}
