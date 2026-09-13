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

import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Riremito
 */
public class InvOp {

    private final List<InvData> idv;

    private InvOp(List<InvData> idv) {
        this.idv = idv;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<InvData> get() {
        return idv;
    }

    public static class Builder {

        private final List<InvData> idv = new LinkedList<>();

        public Builder add(MapleInventoryType type, Item item) {
            idv.add(new InvData(0, type, item));
            return this;
        }

        public Builder update(MapleInventoryType type, Item item) {
            idv.add(new InvData(1, type, item));
            return this;
        }

        public Builder move(MapleInventoryType type, int src, int dst) {
            idv.add(new InvData(2, type, src, dst));
            return this;
        }

        public Builder remove(MapleInventoryType type, int src) {
            idv.add(new InvData(3, type, src));
            return this;
        }

        public InvOp build() {
            return new InvOp(Collections.unmodifiableList(idv));
        }
    }

    public static class InvData {

        public int mode;
        public MapleInventoryType type;
        public Item item;
        public int src, dst;

        public InvData(int mode, MapleInventoryType type, Item item) {
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
