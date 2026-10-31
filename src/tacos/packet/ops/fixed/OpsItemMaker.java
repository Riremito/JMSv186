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
package tacos.packet.ops.fixed;

/**
 *
 * @author Riremito
 */
public enum OpsItemMaker implements IOpsFixed {
    ITEM_MAKER_RESULT_SUCCESS(0),
    ITEM_MAKER_RESULT_DESTROYED(1),
    ITEM_MAKER_ERR_UNKNOWN(2),
    ITEM_MAKER_ERR_EMPTYSLOT(3),
    ITEM_MAKER_ERR_EMPTYSLOT_EQUIP(4),
    ITEM_MAKER_ERR_EMPTYSLOT_COMSUME(5),
    ITEM_MAKER_ERR_EMPTYSLOT_INSTALL(6),
    ITEM_MAKER_ERR_EMPTYSLOT_ETC(7),
    UNKNOWN;

    private final int value;

    private OpsItemMaker(int value) {
        this.value = value;
    }

    private OpsItemMaker() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }
}
