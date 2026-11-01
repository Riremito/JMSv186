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
public enum OpsGuildBBS implements IPacketOps {
    // GMS v95
    GuildBBS_Register(0),
    GuildBBS_Delete(1),
    GuildBBS_LoadListRequest(2),
    GuildBBS_ViewEntryRequest(3),
    GuildBBS_RegisterComment(4),
    GuildBBS_DeleteComment(5),
    GuildBBS_LoadListResult(6),
    GuildBBS_ViewEntryResult(7),
    GuildBBS_EntryNotFound(8),
    UNKNOWN;

    private int value;

    OpsGuildBBS(int val) {
        this.value = val;
    }

    OpsGuildBBS() {
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

    public static OpsGuildBBS find(int val) {
        for (OpsGuildBBS ops : values()) {
            if (ops.get() == val && val != UNKNOWN.get()) {
                return ops;
            }
        }
        return UNKNOWN;
    }
}
