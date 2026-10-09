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

import java.util.LinkedHashMap;
import odin.client.MapleCharacter;
import tacos.server.map.object.TacosMerchant;

/**
 *
 * @author Riremito
 */
public class TacosRoom {

    private static int ROOM_ID = 1000;
    private int id;
    private LinkedHashMap<Integer, MapleCharacter> players = new LinkedHashMap<>();
    private TacosMerchant merchant = null;

    public TacosRoom() {
        this.id = ROOM_ID++;
    }

    public int getId() {
        return this.id;
    }

    public void addPlayer(MapleCharacter player) {
        this.players.put(player.getId(), player);
    }

    public void removePlayer(MapleCharacter player) {
        this.players.remove(player.getId());
    }

    public boolean findPlayer(MapleCharacter player) {
        return this.players.get(player.getId()) != null;
    }

    public boolean findPlayerById(int player_id) {
        return this.players.get(player_id) != null;
    }

    public TacosMerchant getMerchant() {
        return this.merchant;
    }

    public void setMerchant(TacosMerchant merchant) {
        this.merchant = merchant;
    }
}
