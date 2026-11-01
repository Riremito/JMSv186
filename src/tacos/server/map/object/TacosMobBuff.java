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
package tacos.server.map.object;

import java.util.ArrayList;
import tacos.config.Config;
import tacos.config.Region;
import tacos.packet.ops.OpsMobStat;

/**
 *
 * @author Riremito
 */
public class TacosMobBuff {

    public static int[] getBuffBuffer() {
        if (Config.GreaterOrEqual(Region.KMS, 197)) {
            return new int[12]; // 48
        }
        if (Config.GreaterOrEqual(Region.EMS, 89)) {
            return new int[9]; //36
        }
        if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148)) {
            return new int[8]; // 32
        }
        // post bb ex.
        if (Config.Equal(Region.KMST, 330) || Config.Equal(Region.GMS, 95) || Config.Equal(Region.THMS, 96) || Config.Equal(Region.IMS, 1)) {
            return new int[4]; // 16
        }
        // JMS187-194 (Post-BB)
        if (Config.PostBB()) {
            return new int[5]; // 20
        }
        // KMS84, JMS180-186 (Pre-BB)
        if (Config.GreaterOrEqual(Region.KMS, 84) || Config.GreaterOrEqual(Region.JMS, 180) || Config.Equal(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 83)) {
            return new int[4]; // 16
        }
        // KMS1 = none or 4.
        // JMS131-165, KMS1-71, BMS24
        return new int[1]; // 4
    }

    public static class Buff {

        public OpsMobStat ops;
        public int buff_effect;
        public int buff_id; // skill id or mob skill id.
        public int buff_time;
        public long server_time;
    }

    private final ArrayList<Buff> buffs;

    public TacosMobBuff() {
        this.buffs = new ArrayList<>();

        for (OpsMobStat ops : OpsMobStat.values()) {
            Buff buff = new Buff();
            buff.ops = ops;
            buff.buff_effect = 0;
            buff.buff_id = 0;
            buff.buff_time = 0;
            buff.server_time = 0;
            buffs.add(buff);
        }
    }

    public boolean update(OpsMobStat ops, int buff_id, int buff_effect, int buff_time) {
        // disabled in this region.
        if (ops.get() < 0 || getBuffBuffer().length <= ops.getNl()) {
            return false;
        }
        long time = System.currentTimeMillis();
        for (Buff buff : buffs) {
            if (buff.ops == ops) {
                buff.buff_effect = buff_effect;
                buff.buff_id = buff_id;
                buff.buff_time = buff_time;
                buff.server_time = time;
                return true;
            }
        }
        return false;
    }

    public ArrayList<Buff> getMS(int buff_id) {
        ArrayList<Buff> ret = new ArrayList<>();
        if (buff_id == 0) {
            return ret;
        }
        for (Buff buff : buffs) {
            if (buff.buff_id == buff_id) {
                ret.add(buff);
            }
        }
        return ret;
    }

    public ArrayList<Buff> getActive() {
        ArrayList<Buff> ret = new ArrayList<>();
        for (Buff buff : buffs) {
            if (buff.buff_id != 0) {
                ret.add(buff);
            }
        }
        return ret;
    }

    public ArrayList<Buff> getAll() {
        return this.buffs;
    }

    public boolean remove(int buff_id) {
        boolean removed = false;
        for (Buff buff : buffs) {
            if (buff.buff_id == buff_id) {
                buff.buff_effect = 0;
                buff.buff_id = 0;
                buff.buff_time = 0;
                buff.server_time = 0;
                removed = true;
            }
        }
        return removed;
    }

    public ArrayList<Integer> getTimeoutIds(long time) {
        ArrayList<Integer> ret = new ArrayList<>();
        for (Buff buff : buffs) {
            if (buff.server_time != 0 && (buff.server_time + buff.buff_time) < time && !ret.contains(buff.buff_id)) {
                ret.add(buff.buff_id);
            }
        }
        return ret;
    }
}
