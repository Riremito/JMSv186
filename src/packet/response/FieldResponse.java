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

import client.MapleCharacter;
import debug.Debug;
import handling.MaplePacket;
import packet.ServerPacket;
import server.Randomizer;
import server.life.MapleMonster;

/**
 *
 * @author Riremito
 */
public class FieldResponse {

    public enum Flag_FieldEffect {
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

        Flag_FieldEffect(int flag) {
            value = flag;
        }

        Flag_FieldEffect() {
            value = -1;
        }

        public int get() {
            return value;
        }

        public static Flag_FieldEffect find(int val) {
            for (final Flag_FieldEffect o : Flag_FieldEffect.values()) {
                if (o.get() == val) {
                    return o;
                }
            }
            return UNKNOWN;
        }
    }

    public static class FieldEffectStruct {

        Flag_FieldEffect flag;
        String wz_path;
        MapleMonster monster;
        int type, delay;

        public FieldEffectStruct(Flag_FieldEffect flag, String wz_path) {
            this.flag = flag;
            this.wz_path = wz_path;
        }

        public FieldEffectStruct(Flag_FieldEffect flag, MapleMonster monster) {
            this.flag = flag;
            this.monster = monster;
        }

        public FieldEffectStruct(Flag_FieldEffect flag, int type, int delay) {
            this.flag = flag;
            this.type = type;
            this.delay = delay;
        }

    }

    public static MaplePacket playSound(String sound) {
        return FieldEffect(new FieldEffectStruct(Flag_FieldEffect.FieldEffect_Sound, sound));
    }

    public static MaplePacket musicChange(String song) {
        return FieldEffect(new FieldEffectStruct(Flag_FieldEffect.FieldEffect_ChangeBGM, song));
    }

    public static MaplePacket showEffect(String effect) {
        return FieldEffect(new FieldEffectStruct(Flag_FieldEffect.FieldEffect_Screen, effect));
    }

    public static MaplePacket environmentChange(String env, int mode) {
        return FieldEffect(new FieldEffectStruct(Flag_FieldEffect.find(mode), env));
    }

    public static final MaplePacket MapNameDisplay(final int mapid) {
        return FieldEffect(new FieldEffectStruct(Flag_FieldEffect.FieldEffect_Screen, "maplemap/enter/" + mapid));
    }

    // environmentChange, musicChange, showEffect, playSound
    // ShowBossHP, trembleEffect
    public static MaplePacket FieldEffect(FieldEffectStruct st) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FieldEffect);

        sp.Encode1(st.flag.get());
        switch (st.flag) {
            case FieldEffect_Summon: {
                sp.Encode1(0);
                sp.Encode4(0);
                sp.Encode4(0);
                break;
            }
            // 道場
            case FieldEffect_Tremble: {
                sp.Encode1((byte) st.type);
                sp.Encode4(st.delay);
                break;
            }
            case FieldEffect_Object: {
                sp.EncodeStr(st.wz_path);
                break;
            }
            case FieldEffect_Screen: {
                sp.EncodeStr(st.wz_path);
                break;
            }
            case FieldEffect_Sound: {
                sp.EncodeStr(st.wz_path);
                break;
            }
            case FieldEffect_MobHPTag: {
                sp.Encode4(st.monster.getId());

                if (st.monster.getHp() > Integer.MAX_VALUE) {
                    sp.Encode4((int) (((double) st.monster.getHp() / st.monster.getMobMaxHp()) * Integer.MAX_VALUE));
                } else {
                    sp.Encode4((int) st.monster.getHp());
                }

                if (st.monster.getMobMaxHp() > Integer.MAX_VALUE) {
                    sp.Encode4(Integer.MAX_VALUE);
                } else {
                    sp.Encode4((int) st.monster.getMobMaxHp());
                }
                sp.Encode1(st.monster.getStats().getTagColor());
                sp.Encode1(st.monster.getStats().getTagBgColor());
                break;
            }
            case FieldEffect_ChangeBGM: {
                sp.EncodeStr(st.wz_path);
                break;
            }
            case FieldEffect_RewordRullet: {
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode4(0);
                break;
            }
            default: {
                Debug.ErrorLog("FieldEffect not coded : " + st.flag);
                break;
            }
        }
        return sp.Get();
    }

    // test
    public static void MiroSlot(MapleCharacter chr) {
        chr.SendPacket(showEffect("miro/frame"));
        chr.SendPacket(showEffect("miro/RR1/" + Randomizer.nextInt(4)));
        chr.SendPacket(showEffect("miro/RR2/" + Randomizer.nextInt(4)));
        chr.SendPacket(showEffect("miro/RR3/" + Randomizer.nextInt(5)));
        chr.SendPacket(playSound("quest2288/" + Randomizer.nextInt(9))); // test bgm
    }
}
