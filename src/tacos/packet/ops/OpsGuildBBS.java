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

    private OpsGuildBBS(int value) {
        this.value = value;
    }

    private OpsGuildBBS() {
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

    public static OpsGuildBBS find(int value) {
        for (OpsGuildBBS ops : values()) {
            if (ops.get() == value) {
                if (value != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }
}
