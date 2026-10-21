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
package tacos.packet.ops;

/**
 *
 * @author Riremito
 */
public enum OpsItemMakerResult implements IPacketOps {
    // GMS v95
    ITEM_MAKER_RESULT_SUCCESS(0),
    ITEM_MAKER_RESULT_DESTROYED(1),
    ITEM_MAKER_ERR_UNKNOWN(2),
    ITEM_MAKER_ERR_EMPTYSLOT(3),
    ITEM_MAKER_ERR_EMPTYSLOT_EQUIP(4),
    ITEM_MAKER_ERR_EMPTYSLOT_COMSUME(5),
    ITEM_MAKER_ERR_EMPTYSLOT_INSTALL(6),
    ITEM_MAKER_ERR_EMPTYSLOT_ETC(7),
    UNKNOWN;

    private int value;

    OpsItemMakerResult(int val) {
        this.value = val;
    }

    OpsItemMakerResult() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int val) {
        this.value = val;
    }

    public static OpsItemMakerResult find(int val) {
        for (OpsItemMakerResult ops : values()) {
            if (ops.get() == val && val != UNKNOWN.get()) {
                return ops;
            }
        }
        return UNKNOWN;
    }
}
