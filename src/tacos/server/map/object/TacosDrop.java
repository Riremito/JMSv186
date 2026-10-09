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
package tacos.server.map.object;

import odin.client.inventory.Item;

/**
 *
 * @author Riremito
 */
public class TacosDrop extends TacosMapObject {

    private Item item = null;
    private int quest_id = 0;
    private int meso = 0;
    private boolean player_drop = false;
    private int delay = 0;

    public TacosDrop(Item item, int quest_id, int meso) {
        this.item = item;
        this.quest_id = quest_id;
        this.meso = meso;
    }

    public Item getItem() {
        return this.item;
    }

    public int getItemId() {
        if (getMeso() > 0) {
            return this.meso;
        }
        return this.item.getItemId();
    }

    public int getMeso() {
        return this.meso;
    }

    public int getQuestId() {
        return this.quest_id;
    }

    public boolean isPlayerDrop() {
        return this.player_drop;
    }

    public void setPlayerDrop(boolean player_drop) {
        this.player_drop = player_drop;
    }

    public int getDelay() {
        return this.delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    private DropEnterType enter_type = DropEnterType.NORMAL;
    private DropLeaveType leave_type = DropLeaveType.EXPIRED;

    public DropEnterType getET() {
        return this.enter_type;
    }

    public void setET(DropEnterType enter_type) {
        this.enter_type = enter_type;
    }

    public DropLeaveType getLT() {
        return this.leave_type;
    }

    public void setLT(DropLeaveType leave_type) {
        this.leave_type = leave_type;
    }

    public enum DropEnterType {
        UPDATE(0),
        NORMAL(1),
        SILENT(2),
        SPAWN(3),
        NO_ROTATE(4),
        UNKNOWN;

        private int value;

        private DropEnterType(int value) {
            this.value = value;
        }

        private DropEnterType() {
            this.value = -1;
        }

        public int get() {
            return this.value;
        }
    }

    public enum DropLeaveType {
        EXPIRED(0),
        REMOVE(1),
        NORMAL(2),
        SILENT(3),
        EXPLOSION(4),
        PET(5),
        UNKNOWN;

        private int value;

        private DropLeaveType(int value) {
            this.value = value;
        }

        private DropLeaveType() {
            this.value = -1;
        }

        public int get() {
            return this.value;
        }
    }
}
