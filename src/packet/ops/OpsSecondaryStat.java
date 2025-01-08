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
package packet.ops;

import config.ServerConfig;

/**
 *
 * @author Riremito
 */
public enum OpsSecondaryStat {
    // JMS v186
    UNK(0),
    // 1, 0x000000800
    CTS_Booster(11),
    // 1, 0x000010000
    CTS_SoulArrow(16),
    // 2, 0x000000020
    CTS_SharpEyes(5),
    UNKNOWN(-1);

    private int value;

    OpsSecondaryStat(int flag) {
        value = flag;
    }

    OpsSecondaryStat() {
        value = -1;
    }

    public boolean set(int flag) {
        value = flag;
        return true;
    }

    public int get() {
        return value;
    }

    public static void Init() {
        if ((ServerConfig.JMS194orLater())) {
            // fix
            return;
        }
        
        if ((ServerConfig.JMS131orEarlier())) {
            // fixs
            return;
        }
    }
}
