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
public enum OpsMovePathAttr {
    // GMS v95
    MPA_NORMAL(0),
    MPA_JUMP(1),
    MPA_IMPACT(2),
    MPA_IMMEDIATE(3),
    MPA_TELEPORT(4),
    MPA_HANGONBACK(5),
    MPA_ASSAULTER(6),
    MPA_ASSASSINATION(7),
    MPA_RUSH(8),
    MPA_STATCHANGE(9),
    MPA_SITDOWN(10),
    MPA_STARTFALLDOWN(11),
    MPA_FALLDOWN(12), // required
    MPA_STARTWINGS(13),
    MPA_WINGS(14),
    MPA_ARAN_ADJUST(15),
    MPA_MOB_TOSS(16),
    MPA_FLYING_BLOCK(17),
    MPA_DASH_SLIDE(18),
    MPA_BMAGE_ADJUST(19),
    MPA_FLASHJUMP(20),
    MPA_ROCKET_BOOSTER(21),
    MPA_BACKSTEP_SHOT(22),
    MPA_MOBPOWERKNOCKBACK(23),
    MPA_VERTICALJUMP(24),
    MPA_CUSTOMIMPACT(25),
    MPA_COMBATSTEP(26),
    MPA_HIT(27),
    MPA_TIMEBOMBATTACK(28),
    MPA_SNOWBALLTOUCH(29),
    MPA_BUFFZONEEFFECT(30),
    MPA_MOB_LADDER(31),
    MPA_MOB_RIGHTANGLE(32),
    MPA_MOB_STOPNODE_START(33),
    MPA_MOB_BEFORE_NODE(34),
    MPA_MOB_ATTACK_RUSH(35),
    MPA_MOB_ATTACK_RUSH_STOP(36),
    UNKNOWN;

    int value;

    OpsMovePathAttr(int val) {
        value = val;
    }

    OpsMovePathAttr() {
        value = -1;
    }

    public int get() {
        return value;
    }

    public void set(int val) {
        value = val;
    }

    public static void clear() {
        for (OpsMovePathAttr ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        // JMS302
        if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148)) {
            clear();
            MPA_NORMAL.set(0);
            MPA_TELEPORT.set(4);
            MPA_FALLDOWN.set(14);
            return;
        }
        // KMS112, GMS95
        if (Config.PostBB()) {
            return;
        }
        // JMS186
        if (Config.GreaterOrEqual(Region.JMS, 186)) {
            clear();
            MPA_NORMAL.set(0);
            MPA_TELEPORT.set(4);
            MPA_FALLDOWN.set(12);
            return;
        }
        // KMS55-95, JMS147-180, CMS73
        clear();
        MPA_NORMAL.set(0);
        MPA_TELEPORT.set(4);
        MPA_FALLDOWN.set(15);
    }
}
