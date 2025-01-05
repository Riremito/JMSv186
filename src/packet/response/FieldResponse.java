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
 */
package packet.response;

import server.life.MapleMonster;

/**
 *
 * @author Riremito
 */
public class FieldResponse {

    public enum OpsFieldEffect {
        FieldEffect_Summon(0),
        FieldEffect_Tremble(1),
        FieldEffect_Object(2),
        FieldEffect_Screen(3),
        FieldEffect_Sound(4),
        FieldEffect_MobHPTag(5),
        FieldEffect_ChangeBGM(6),
        FieldEffect_RewordRullet(7),
        UNKNOWN(-1);

        private int value;

        OpsFieldEffect(int flag) {
            value = flag;
        }

        OpsFieldEffect() {
            value = -1;
        }

        public int get() {
            return value;
        }

        public static OpsFieldEffect find(int val) {
            for (final OpsFieldEffect o : OpsFieldEffect.values()) {
                if (o.get() == val) {
                    return o;
                }
            }
            return UNKNOWN;
        }
    }

    public static class FieldEffectStruct {

        OpsFieldEffect flag;
        String wz_path;
        MapleMonster monster;
        int type, delay;

        public FieldEffectStruct(OpsFieldEffect flag, String wz_path) {
            this.flag = flag;
            this.wz_path = wz_path;
        }

        public FieldEffectStruct(OpsFieldEffect flag, MapleMonster monster) {
            this.flag = flag;
            this.monster = monster;
        }

        public FieldEffectStruct(OpsFieldEffect flag, int type, int delay) {
            this.flag = flag;
            this.type = type;
            this.delay = delay;
        }

    }

}
