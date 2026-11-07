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

import tacos.config.Config;
import tacos.config.Region;

/**
 *
 * @author Riremito
 */
public enum OpsChatGroup implements IPacketOps {
    CG_Friend(0),
    CG_Party(1),
    CG_Guild(2),
    CG_Alliance(3),
    CG_Couple(4),
    CG_ToCouple(5),
    CG_Expedition(6),
    UNKNOWN;

    private int value;

    private OpsChatGroup(int value) {
        this.value = value;
    }

    private OpsChatGroup() {
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

    public static OpsChatGroup find(int value) {
        for (OpsChatGroup ops : values()) {
            if (ops.get() == value) {
                if (value != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }

    public static void clear() {
        for (OpsChatGroup ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        if (Region.JMS.check()) {
            clear();
            CG_Friend.set(0);
            CG_Party.set(1);
            CG_Guild.set(2);
            CG_Alliance.set(3); // JMS147 OK
            if (Config.GreaterOrEqual(Region.JMS, 186)) {
                CG_Expedition.set(4); // JMS186-302 OK
            }
            return;
        }
    }
}
