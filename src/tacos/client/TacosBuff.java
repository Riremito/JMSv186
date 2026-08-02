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

import java.util.ArrayList;
import odin.client.ISkill;
import odin.client.SkillFactory;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleStatEffect;
import odin.server.maps.MapleMap;
import tacos.config.Region;
import tacos.config.Version;
import tacos.debug.DebugLogger;
import tacos.odin.OdinPair;
import tacos.packet.ops.OpsSecondaryStat;
import tacos.packet.ops.OpsSkill;
import static tacos.packet.ops.OpsSkill.CITIZEN_MYSTIC_DOOR;
import static tacos.packet.ops.OpsSkill.EVANJR_MYSTIC_DOOR;
import static tacos.packet.ops.OpsSkill.LEGEND_MYSTIC_DOOR;
import static tacos.packet.ops.OpsSkill.NOBLESSE_MYSTIC_DOOR;
import static tacos.packet.ops.OpsSkill.NOVICE_MYSTIC_DOOR;
import static tacos.packet.ops.OpsSkill.PRIEST_MYSTIC_DOOR;
import tacos.wz.opt.FieldOpt;

/**
 *
 * @author Riremito
 */
public class TacosBuff {

    public static int[] getBuffBuffer() {
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            return new int[12]; // 48
        }
        if (Version.GreaterOrEqual(Region.EMS, 89)) {
            return new int[9]; //36
        }
        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
            return new int[8]; // 32
        }
        // post bb ex.
        if (Version.Equal(Region.KMST, 330) || Version.Equal(Region.GMS, 95) || Version.Equal(Region.THMS, 96) || Version.Equal(Region.IMS, 1)) {
            return new int[4]; // 16
        }
        // JMS187, CMS88, EMS76
        if (Version.PostBB()) {
            return new int[5]; // 20
        }
        // JMS147, TWMS77, THMS87, BMS24
        if (Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 47) || Version.GreaterOrEqual(Region.JMS, 146) || Version.GreaterOrEqual(Region.CMS, 62) || Version.GreaterOrEqual(Region.TWMS, 73) || Version.GreaterOrEqual(Region.THMS, 0) || Version.GreaterOrEqual(Region.GMS, 61) || Version.GreaterOrEqual(Region.MSEA, 0) || Version.GreaterOrEqual(Region.EMS, 0) || Version.GreaterOrEqual(Region.BMS, 24) || Version.GreaterOrEqual(Region.VMS, 35)) {
            return new int[4]; // 16
        }
        if (Version.Equal(Region.KMS, 1)) {
            return new int[1]; // 4
        }
        // JMS131, reverse order.
        return new int[2]; // 8
    }

    public static int[] getMobBuffBuffer() {
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            return new int[12]; // 48
        }
        if (Version.GreaterOrEqual(Region.EMS, 89)) {
            return new int[9]; //36
        }
        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
            return new int[8]; // 32
        }
        // post bb ex.
        if (Version.Equal(Region.KMST, 330) || Version.Equal(Region.GMS, 95) || Version.Equal(Region.THMS, 96) || Version.Equal(Region.IMS, 1)) {
            return new int[4]; // 16
        }
        // JMS187-194 (Post-BB)
        if (Version.PostBB()) {
            return new int[5]; // 20
        }
        // JMS180-186 (Pre-BB)
        if (Version.GreaterOrEqual(Region.JMS, 180) || Version.GreaterOrEqual(Region.KMS, 95) || Version.GreaterOrEqual(Region.GMS, 83) || Version.Equal(Region.THMS, 87)) {
            return new int[4]; // 16
        }
        // KMS1 = none or 4.
        // JMS131-164, KMS1-65, BMS24
        return new int[1]; // 4
    }

    public static class Buff {

        public OpsSecondaryStat ops;
        public int buff_effect;
        public int buff_effect_2;
        public int buff_id; // skill id,  negative value is item id.
        public int buff_time;
        public long server_time;
    }

    private final TacosCharacter chr;
    private final ArrayList<Buff> buffs;

    public TacosBuff(TacosCharacter chr) {
        this.chr = chr;
        this.buffs = new ArrayList<>();

        for (OpsSecondaryStat ops : OpsSecondaryStat.values()) {
            Buff buff = new Buff();
            buff.ops = ops;
            buff.buff_effect = 0;
            buff.buff_id = 0;
            buff.buff_time = 0;
            buff.server_time = 0;
            buffs.add(buff);
        }
    }

    public boolean update(int buff_id) {
        MapleMap map = this.chr.getMap();
        MapleStatEffect effect = null;
        // skill or item.
        if (0 < buff_id) {
            OpsSkill ops_skill = OpsSkill.find(buff_id);
            ISkill skill = SkillFactory.getSkill(buff_id);
            if (skill == null) {
                DebugLogger.ErrorLog("TacosBuff - update : skill.");
                return false;
            }

            effect = skill.getEffect(this.chr.getSkillLevel(ops_skill));
            if (effect == null) {
                DebugLogger.ErrorLog("TacosBuff - update : effect.");
                return false;
            }

            if (0 < effect.getCooldown()) {
                if (this.chr.getCoolTime().check(ops_skill)) {
                    return false;
                }
                if (ops_skill != OpsSkill.CAPTAIN_BATTLESHIP) {
                    this.chr.getCoolTime().add(ops_skill, effect.getCooldown());
                }
            }

            switch (ops_skill) {
                case HERO_MONSTER_MAGNET:
                case DARKKNIGHT_MONSTER_MAGNET: {
                    return false;
                }
                case PRIEST_MYSTIC_DOOR:
                case NOVICE_MYSTIC_DOOR:
                case NOBLESSE_MYSTIC_DOOR:
                case LEGEND_MYSTIC_DOOR:
                case EVANJR_MYSTIC_DOOR:
                case CITIZEN_MYSTIC_DOOR: {
                    if (FieldOpt.FIELDOPT_MYSTICDOORLIMIT.check(map.getFieldLimit())) {
                        return false;
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        } else {
            int item_id = -buff_id;
            effect = MapleItemInformationProvider.getInstance().getItemEffect(item_id);
            if (effect == null) {
                DebugLogger.ErrorLog("TacosBuff - update : effect (item).");
                return false;
            }
            if (effect.getOss().isEmpty()) {
                return false;
            }
        }

        long time = System.currentTimeMillis();
        int count = 0;
        for (OdinPair<OpsSecondaryStat, Integer> oi : effect.getOss()) {
            for (Buff buff : buffs) {
                if (buff.ops == oi.getLeft()) {
                    buff.buff_effect = oi.getRight();
                    buff.buff_id = buff_id;
                    buff.buff_time = effect.getDuration();
                    buff.server_time = time;
                    count++;
                }
            }
        }

        if (count != 0) {
            return true;
        }

        return false;
    }

    public boolean updateTest(int index, int buff_id, int buff_effect, int buff_time) {
        return updateTest(index, buff_id, buff_effect, buff_time, 0);
    }

    public boolean updateTest(int index, int buff_id, int buff_effect, int buff_time, int buff_effect_2) {
        long time = System.currentTimeMillis();
        OpsSecondaryStat ops = OpsSecondaryStat.find(index);
        for (Buff buff : buffs) {
            if (buff.ops == ops) {
                buff.buff_effect = buff_effect;
                buff.buff_effect_2 = buff_effect_2;
                buff.buff_id = buff_id;
                buff.buff_time = buff_time;
                buff.server_time = time;
                return true;
            }
        }
        return false;
    }

    public ArrayList<Buff> getCTS(int buff_id) {
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

    public ArrayList<Buff> getAll() {
        return this.buffs;
    }

    public boolean remove(int buff_id) {
        for (Buff buff : buffs) {
            if (buff.buff_id == buff_id) {
                buff.buff_effect = 0;
                buff.buff_id = 0;
                buff.buff_time = 0;
                buff.server_time = 0;
            }
        }
        return false;
    }

    public ArrayList<Buff> getCTSTimeout(long time) {
        ArrayList<Buff> ret = new ArrayList<>();
        for (Buff buff : buffs) {
            if (buff.server_time != 0 && (buff.server_time + buff.buff_time) < time) {
                ret.add(buff);
            }
        }
        return ret;
    }

    public boolean removeTimeout(long time) {
        for (Buff buff : buffs) {
            if (buff.server_time != 0 && (buff.server_time + buff.buff_time) < time) {
                buff.buff_effect = 0;
                buff.buff_id = 0;
                buff.buff_time = 0;
                buff.server_time = 0;
                return true;
            }
        }
        return false;
    }
}
