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
package tacos.client;

import java.util.TreeMap;
import tacos.packet.ops.OpsSecondaryStat;

/**
 *
 * @author Riremito
 */
public class TacosTemporaryStat {

    public static class Buff {

        OpsSecondaryStat ops;
        public int buff_effect;
        public int buff_id; // skill id,  negative value is item id.
        public int buff_time;
    }

    private TreeMap<Integer, Buff> buffs = new TreeMap<>();

    public TacosTemporaryStat() {
    }

    public byte[] getBuffMask() {
        return null;
    }
}
