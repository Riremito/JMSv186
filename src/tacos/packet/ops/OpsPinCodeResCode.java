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
public enum OpsPinCodeResCode implements IPacketOps {
    PinCodeResCode_Success(0),
    PinCodeResCode_NotAssigned(1),
    PinCodeResCode_Incorrect(2),
    PinCodeResCode_Assigned(4),
    PinCodeResCode_DBFail(3),
    PinCodeResCode_AlreadyConnected(7),
    UNKNOWN;

    private int value;

    OpsPinCodeResCode(int val) {
        this.value = val;
    }

    OpsPinCodeResCode() {
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

    public static OpsPinCodeResCode find(int val) {
        for (OpsPinCodeResCode ops : values()) {
            if (ops.get() == val) {
                if (val != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }
}
