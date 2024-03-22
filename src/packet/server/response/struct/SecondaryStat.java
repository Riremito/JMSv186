/*
 * Copyright (C) 2023 Riremito
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
package packet.server.response.struct;

import client.ISkill;
import client.MapleCharacter;
import client.SkillFactory;
import config.ServerConfig;
import packet.server.ServerPacket;
import server.MapleStatEffect;

/**
 *
 * @author Riremito
 */
public class SecondaryStat {

    public enum Flag {
        // v186
        // 0x00000001
        UNK(0),
        UNKNOWN;

        private int value;

        Flag(int flag) {
            value = flag;
        }

        Flag() {
            value = -1;
        }

        public boolean set(int flag) {
            value = flag;
            return true;
        }

        public int get() {
            return value;
        }
    }

    public static void Init() {
        if (194 <= ServerConfig.version) {
        }
    }

    // SecondaryStat::DecodeForLocal
    public static byte[] EncodeForLocal(MapleCharacter chr, int skill_id) {
        ServerPacket p = new ServerPacket();

        int buff_level = 0;
        int buff_time = 0;
        int buff_mask1 = 0;
        int buff_speed = 0;
        int buff_jump = 0;

        final ISkill buff_skill = SkillFactory.getSkill(skill_id);

        if (buff_skill != null) {
            buff_level = chr.getSkillLevel(buff_skill);
            if (buff_level > 0) {
                MapleStatEffect mse = buff_skill.getEffect(buff_level);

                buff_time = mse.getDuration();
                buff_speed = mse.getSpeed();
                if (0 < buff_speed) {
                    buff_mask1 |= 1;
                }
                buff_jump = mse.getJump();
                if (0 < buff_jump) {
                    buff_mask1 |= 2;
                }
            }
        }

        p.Encode4(0); // mask1
        p.Encode4(0); // mask2
        p.Encode4(0); // mask3
        p.Encode4(0); // mask4

        if (194 <= ServerConfig.version) {
            p.Encode4(buff_mask1); // mask5
        }

        //SPEED(0x8000000000L)
        if ((buff_mask1 & 1) > 0) {
            p.Encode2(buff_speed);
            p.Encode4(skill_id);
            p.Encode4(buff_time);
        }

        //JUMP(0x10000000000L)
        if ((buff_mask1 & 2) > 0) {
            p.Encode2(buff_jump);
            p.Encode4(skill_id);
            p.Encode4(buff_time);
        }

        p.Encode1(0);
        p.Encode1(0);
        return p.Get().getBytes();
    }
    // SecondaryStat::DecodeForRemote

    public static byte[] EncodeForRemote(MapleCharacter chr, int statmask) {
        ServerPacket p = new ServerPacket();
        return p.Get().getBytes();
    }
}
