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
package packet.request.parse;

import client.inventory.MaplePet;
import config.Region;
import config.Version;
import debug.DebugLogger;
import java.awt.Point;
import packet.ClientPacket;
import packet.ops.OpsMovePathAttr;
import server.life.AbstractLoadedMapleLife;
import server.maps.AnimatedMapleMapObject;

/**
 *
 * @author Riremito
 */
public class ParseCMovePath {

    private byte data[] = null;
    private Point move_start = null;
    private Point move_end = null;
    private int move_end_action = 0;
    private short move_end_foothold_id = 0;

    public ParseCMovePath() {

    }

    // mob, player
    public void update(AbstractLoadedMapleLife life) {
        life.setStance(move_end_action);
        life.setPosition(move_end);
        life.setFh(move_end_foothold_id);
    }

    // pet
    public void update(MaplePet life) {
        life.setStance(move_end_action);
        life.setPosition(move_end);
        life.setFh(move_end_foothold_id);
    }

    // dragon, summon
    public void update(AnimatedMapleMapObject life) {
        life.setStance(move_end_action);
        life.setPosition(move_end);
        life.setFH(move_end_foothold_id);
    }

    private int getTailDataSize(ClientPacket cp) {
        // ignore bytes : last Encode1 + unknown bytes (Post-BB)
        switch (cp.GetOpcode()) {
            case CP_UserMove: {
                if (Version.LessOrEqual(Region.KMS, 31)) {
                    return (1 + 9);
                }
                return (1 + 17); // KMS65, JMS164-302
            }
            case CP_NpcMove:
            case CP_DragonMove:
            case CP_SummonedMove:
            case CP_PetMove: {
                return (1 + 2 * 4); // JMS186-194
            }
            case CP_MobMove: {
                if (Version.LessOrEqual(Region.KMS, 31)) {
                    return 1;
                }
                if (Version.GreaterOrEqual(Region.KMS, 114)) {
                    return 17;
                }
                if (Version.GreaterOrEqual(Region.JMS, 302)) {
                    return (1 + 54);
                }
                if (Version.PostBB()) {
                    return (1 + 24); // JMS187+
                }
                if (Version.GreaterOrEqual(Region.KMS, 95) || Version.GreaterOrEqual(Region.JMS, 180)) {
                    return (1 + 2 * 4 + 1 * 4 + 4); // KMS95, JMS180-186
                }
                return (1 + 2 * 4); // KMS65, JMS131-165
            }
            default: {
                DebugLogger.ErrorLog("ParseCMovePath : invalid header.");
                break;
            }
        }

        return 0;
    }

    // CMovePath::Decode
    public boolean Decode(ClientPacket cp) {
        if (cp.getRemainingSize() == 0) {
            return false;
        }
        data = cp.DecodeAll();
        // offset
        final int offset_start_x = 0;
        final int offset_start_y = 2;
        int offset_end_x = 0;
        int offset_end_y = 0;
        int offset_end_fh = 0;
        int offset_end_action = 0;
        final int tail_data_size = getTailDataSize(cp); // for only mob.

        if (Version.LessOrEqual(Region.KMS, 65) || Version.LessOrEqual(Region.JMS, 165) || Version.LessOrEqual(Region.GMS, 83)) {
            // JMS131-165
            offset_end_x = data.length - 13 - tail_data_size;
            offset_end_fh = data.length - 5 - tail_data_size;
        } else {
            // JMS180+
            offset_end_x = data.length - 17 - tail_data_size;
            offset_end_fh = data.length - 9 - tail_data_size;
        }
        offset_end_y = offset_end_x + 2;
        offset_end_action = data.length - 3 - tail_data_size;

        move_start = new Point(Decode2At_int(offset_start_x), Decode2At_int(offset_start_y));
        move_end = new Point(Decode2At_int(offset_end_x), Decode2At_int(offset_end_y));
        move_end_foothold_id = Decode2At(offset_end_fh);
        move_end_action = data[offset_end_action];

        // TODO : FIX, auto detect jump down, last action is JD or not.
        if (data[offset_end_x - 3] == OpsMovePathAttr.MPA_FALLDOWN.get()) {
            if (Decode2At_int(offset_end_y + 4) == 0) {
                Point move_end_test = new Point(Decode2At_int(offset_end_x - 2), Decode2At_int(offset_end_y - 2));
                int vector_0 = (move_end_test.x - move_start.x) * (move_end_test.x - move_start.x) + (move_end_test.y - move_start.y) * (move_end_test.y - move_start.y); // JD distance
                int vector_1 = (move_end.x - move_start.x) * (move_end.x - move_start.x) + (move_end.y - move_start.y) * (move_end.y - move_start.y); // normal distance
                if (vector_0 < vector_1) {
                    move_end = move_end_test;
                    move_end_foothold_id = Decode2At(offset_end_fh - 2);
                }
            }
        }

        return true;
    }

    private short Decode2At(int offset) {
        return (short) (((short) data[offset] & 0xFF) | (((short) data[offset + 1] & 0xFF) << 8));
    }

    private int Decode2At_int(int offset) {
        return Decode2At(offset);
    }

    public byte[] get() {
        return data;
    }

}
