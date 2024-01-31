/*
 * Copyright (C) 2024 Riremito
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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package debug;

import client.MapleCharacter;
import client.PlayerStats;

/**
 *
 * @author Riremito
 */
public class DebugJob {

    private static boolean ResetStat(MapleCharacter chr) {

        PlayerStats stat = chr.getStat();
        stat.setHp(50);
        stat.setMaxHp(50);
        stat.setMp(50);
        stat.setMaxMp(50);
        stat.setStr(12);
        stat.setDex(5);
        stat.setInt(4);
        stat.setLuk(4);

        chr.UpdateStat(true);
        return true;
    }
}
