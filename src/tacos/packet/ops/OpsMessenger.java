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
public enum OpsMessenger implements IPacketOps {
    MSMP_Enter(0),
    MSMP_SelfEnterResult(1),
    MSMP_Leave(2),
    MSMP_Invite(3),
    MSMP_InviteResult(4),
    MSMP_Blocked(5),
    MSMP_Chat(6),
    MSMP_Avatar(7),
    MSMP_Migrated(8),
    UNKNOWN;

    private int value;

    OpsMessenger(int val) {
        this.value = val;
    }

    OpsMessenger() {
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

    public static OpsMessenger find(int val) {
        for (OpsMessenger ops : values()) {
            if (ops.get() == val) {
                if (val != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }
}
