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
public enum OpsMemo implements IPacketOps {

    MemoReq_Send(0),
    MemoReq_Delete(1),
    MemoReq_Load(2),
    MemoRes_Load(3),
    MemoRes_Send_Succeed(4),
    MemoRes_Send_Warning(5),
    MemoRes_Send_ConfirmOnline(6),
    MemoNotify_Receive(7),
    UNKNOWN;

    private int value;

    OpsMemo(int val) {
        this.value = val;
    }

    OpsMemo() {
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

    public static OpsMemo find(int val) {
        for (final OpsMemo ops : values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }
}
