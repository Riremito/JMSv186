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
public enum OpsViewAllChar implements IPacketOps {
    VAC_ResCode_Success(0),
    VAC_ResCode_CountRelatedSvrs(1),
    VAC_ResCode_AlreadyConnected(2),
    VAC_ResCode_TimedOut(3),
    VAC_ResCode_HasNoCharacterInAllWorld(4),
    VAC_ResCode_HasNoCharacterInOneWorld(5),
    VAC_ResCode_DBError(6),
    VAC_ResCode_VADDlgAlreadyOn(7),
    UNKNOWN;

    private int value;

    OpsViewAllChar(int val) {
        this.value = val;
    }

    OpsViewAllChar() {
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

    public static OpsViewAllChar find(int val) {
        for (OpsViewAllChar ops : values()) {
            if (ops.get() == val) {
                if (val != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }
}
