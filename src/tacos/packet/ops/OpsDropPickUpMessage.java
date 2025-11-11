/*
 * Copyright (C) 2025 Riremito
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
package tacos.packet.ops;

/**
 *
 * @author Riremito
 */
public enum OpsDropPickUpMessage {
    PICKUP_ITEM(0),
    PICKUP_MESO(1),
    PICKUP_MONSTER_CARD(2),
    PICKUP_INVENTORY_FULL(-1), // any value
    PICKUP_UNAVAILABLE(-2),
    PICKUP_BROKEN(-3),
    UNKNOWN(-1);

    private int value;

    OpsDropPickUpMessage(int flag) {
        value = flag;
    }

    OpsDropPickUpMessage() {
        value = -1;
    }

    public boolean set(int flag) {
        value = flag;
        return true;
    }

    public int get() {
        return value;
    }
}
