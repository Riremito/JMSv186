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

import tacos.config.Config;
import tacos.config.Region;

/**
 *
 * @author Riremito
 */
public enum OpsGrade implements IPacketOps {
    NONE(0),
    HIDDEN(1),
    RARE(5),
    EPIC(6),
    UNIQUE(7),
    LEGENDARY,
    UNKNOWN;

    private int value;

    private OpsGrade(int val) {
        this.value = val;
    }

    private OpsGrade() {
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

    public static OpsGrade find(int val) {
        for (OpsGrade ops : values()) {
            if (ops.get() == val && val != UNKNOWN.get()) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void clear() {
        for (OpsGrade ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.EMS, 89)) {
            clear();
            NONE.set(0);
            HIDDEN.set(1);
            RARE.set(17);
            EPIC.set(18);
            UNIQUE.set(19);
            LEGENDARY.set(20);
            return;
        }
    }
}
