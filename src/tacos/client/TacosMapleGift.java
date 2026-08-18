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
package tacos.client;

import java.util.ArrayList;

/**
 *
 * @author Riremito
 */
public class TacosMapleGift {

    public static class MapleGiftData {

        public int unk1;
        public int item_id;
        public int id; // id?
        public String name;
    }

    private final ArrayList<MapleGiftData> maple_gift_list;

    public TacosMapleGift() {
        this.maple_gift_list = new ArrayList<>();
    }

    public ArrayList<MapleGiftData> get() {
        return this.maple_gift_list;
    }

    public MapleGiftData find(int id) {
        for (MapleGiftData maple_gift_data : this.maple_gift_list) {
            if (maple_gift_data.id == id) {
                return maple_gift_data;
            }
        }
        return null;
    }

    public void add(MapleGiftData maple_gift_data) {
        this.maple_gift_list.add(maple_gift_data);
    }

    public void remove(int id) {
        MapleGiftData maple_gift_data = find(id);
        if (maple_gift_data != null) {
            this.maple_gift_list.remove(maple_gift_data);
        }
    }

    public void clear() {
        this.maple_gift_list.clear();
    }
}
