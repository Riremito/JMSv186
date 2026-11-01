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
public enum OpsMapleTV implements IPacketOps {
    MapleTVResCode_Fail(-1),
    MapleTVResCode_Success(0),
    MapleTVResCode_IsNotGM(1),
    MapleTVResCode_WrongUser(2),
    MapleTVResCode_TimeOut(3),
    UNKNOWN;

    private int value;

    private OpsMapleTV(int value) {
        this.value = value;
    }

    private OpsMapleTV() {
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
