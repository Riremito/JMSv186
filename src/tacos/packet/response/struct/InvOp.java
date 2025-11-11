/*
 * Copyright (C) 2024 Riremito
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
package tacos.packet.response.struct;

import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import java.util.ArrayList;

/**
 *
 * @author Riremito
 */
public class InvOp {

    private ArrayList<InvData> idv = new ArrayList<>();

    public void add(MapleInventoryType type, IItem item) {
        idv.add(new InvData(0, type, item));
    }

    public void update(MapleInventoryType type, IItem item) {
        idv.add(new InvData(1, type, item));
    }

    public void move(MapleInventoryType type, int src, int dst) {
        idv.add(new InvData(2, type, src, dst));
    }

    public void remove(MapleInventoryType type, int src) {
        idv.add(new InvData(3, type, src));
    }

    public ArrayList<InvData> get() {
        return idv;
    }

    public class InvData {

        public int mode;
        public MapleInventoryType type;
        public IItem item;
        public int src, dst;

        public InvData(int mode, MapleInventoryType type, IItem item) {
            this.mode = mode;
            this.type = type;
            this.item = item;
        }

        public InvData(int mode, MapleInventoryType type, int src, int dst) {
            this.mode = mode;
            this.type = type;
            this.src = src;
            this.dst = dst;
        }

        public InvData(int mode, MapleInventoryType type, int src) {
            this.mode = mode;
            this.type = type;
            this.src = src;
        }
    }
}
