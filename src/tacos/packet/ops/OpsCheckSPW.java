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
public enum OpsCheckSPW implements IPacketOps {
    CHECK_SPW_NONE(0),
    CHECK_SPW_ENABLED(1),
    CHECK_SPW_DISABLED(2),
    CHECK_SPW_DONOTCHECK(3),
    CHECK_SPW_UNINITIALIZED(-1),
    UNKNOWN;

    private int value;

    private OpsCheckSPW(int value) {
        this.value = value;
    }

    private OpsCheckSPW() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int value) {
        this.value = value;
    }
}
