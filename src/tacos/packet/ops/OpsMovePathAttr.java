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

import tacos.config.Region;
import tacos.config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsMovePathAttr {
    MPA_NORMAL(0),
    MPA_JUMP,
    MPA_IMPACT,
    MPA_IMMEDIATE,
    MPA_TELEPORT,
    MPA_HANGONBACK,
    MPA_ASSAULTER,
    MPA_ASSASSINATION,
    MPA_RUSH,
    MPA_STATCHANGE,
    MPA_SITDOWN,
    MPA_STARTFALLDOWN,
    MPA_FALLDOWN(12), // required
    MPA_STARTWINGS,
    MPA_WINGS,
    MPA_ARAN_ADJUST,
    MPA_MOB_TOSS,
    MPA_FLYING_BLOCK,
    MPA_DASH_SLIDE,
    MPA_BMAGE_ADJUST,
    MPA_FLASHJUMP,
    MPA_ROCKET_BOOSTER,
    MPA_BACKSTEP_SHOT,
    MPA_MOBPOWERKNOCKBACK,
    MPA_VERTICALJUMP,
    MPA_CUSTOMIMPACT,
    MPA_COMBATSTEP,
    MPA_HIT,
    MPA_TIMEBOMBATTACK,
    MPA_SNOWBALLTOUCH,
    MPA_BUFFZONEEFFECT,
    MPA_MOB_LADDER,
    MPA_MOB_RIGHTANGLE,
    MPA_MOB_STOPNODE_START,
    MPA_MOB_BEFORE_NODE,
    MPA_MOB_ATTACK_RUSH,
    MPA_MOB_ATTACK_RUSH_STOP,
    UNKNOWN(0);

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

    public static void init() {
        if (Version.LessOrEqual(Region.KMS, 65)) {
            MPA_FALLDOWN.set(15);
            return;
        }
        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
            MPA_FALLDOWN.set(14);
            return;
        }
        if (Version.GreaterOrEqual(Region.JMS, 186)) {
            MPA_FALLDOWN.set(12);
            return;
        }
        if (Version.GreaterOrEqual(Region.JMS, 147)) {
            MPA_FALLDOWN.set(15);
            return;
        }
        if (Version.GreaterOrEqual(Region.GMS, 83)) {
            MPA_FALLDOWN.set(15);
            return;
        }

        MPA_FALLDOWN.set(12);
        return;
    }
}
