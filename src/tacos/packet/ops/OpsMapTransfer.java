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
public enum OpsMapTransfer implements IPacketOps {
    MapTransferReq_DeleteList(0),
    MapTransferReq_RegisterList(1),
    MapTransferRes_DeleteList(2),
    MapTransferRes_RegisterList(3),
    MapTransferRes_Use(4),
    MapTransferRes_Unknown(5),
    MapTransferRes_TargetNotExist(6),
    MapTransferRes_TargetDied(7),
    MapTransferRes_NotAllowed(8),
    MapTransferRes_AlreadyInMap(9),
    MapTransferRes_RegisterFail(10),
    MapTransferRes_LevelLimit(11),
    UNKNOWN;

    private int value;

    private OpsMapTransfer(int value) {
        this.value = value;
    }

    private OpsMapTransfer() {
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

    public static OpsMapTransfer find(int value) {
        for (OpsMapTransfer ops : values()) {
            if (ops.get() == value) {
                if (value != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }
}
