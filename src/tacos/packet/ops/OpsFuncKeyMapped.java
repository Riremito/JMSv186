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
public enum OpsFuncKeyMapped implements IPacketOps {
    FuncKeyMapped_KeyModified(0),
    FuncKeyMapped_PetConsumeHPItemModified(1), // HP, rename
    FuncKeyMapped_PetConsumeMPItemModified(2), // MP
    FuncKeyMapped_JMS_PetConsumeCureItemModified(3), // Cure (JMS)
    UNKNOWN(-1);

    private int value;

    OpsFuncKeyMapped(int val) {
        this.value = val;
    }

    OpsFuncKeyMapped() {
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

    public static OpsFuncKeyMapped find(int val) {
        for (final OpsFuncKeyMapped ops : values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        // unchanged.
    }

}
