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
public enum OpsRecipe implements IOpsFixed {
    RECIPE_CLASS_START(0),
    RECIPE_CLASS_NORMAL(1),
    RECIPE_CLASS_HIDDEN(2),
    RECIPE_CLASS_MONSTER_CRYSTAL(3),
    RECIPE_CLASS_EQUIP_DISASSEMBLE(4),
    RECIPE_CLASS_END(5),
    UNKNOWN;

    private final int value;

    private OpsRecipe(int value) {
        this.value = value;
    }

    private OpsRecipe() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    public static OpsRecipe find(int value) {
        for (OpsRecipe ops : values()) {
            if (ops.get() == value) {
                return ops;
            }
        }
        return UNKNOWN;
    }
}
