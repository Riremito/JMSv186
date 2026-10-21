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
public enum OpsRecipeClass implements IPacketOps {
    // GMS v95
    RECIPE_CLASS_START(0),
    RECIPE_CLASS_NORMAL(1),
    RECIPE_CLASS_HIDDEN(2),
    RECIPE_CLASS_MONSTER_CRYSTAL(3),
    RECIPE_CLASS_EQUIP_DISASSEMBLE(4),
    RECIPE_CLASS_END(5),
    UNKNOWN;

    private int value;

    OpsRecipeClass(int val) {
        this.value = val;
    }

    OpsRecipeClass() {
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

    public static OpsRecipeClass find(int val) {
        for (OpsRecipeClass ops : values()) {
            if (ops.get() == val && val != UNKNOWN.get()) {
                return ops;
            }
        }
        return UNKNOWN;
    }
}
