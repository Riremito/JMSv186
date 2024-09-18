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
package packet.request.struct;

import config.ServerConfig;
import java.awt.Point;
import packet.ClientPacket;

/**
 *
 * @author Riremito
 */
public class CMovePath {

    private byte data[] = null;
    Point move_start;
    Point move_end;
    int action;
    short foothold_id;

    private static byte JUMP_DOWN_ACTION = 0x0C; // v186

    public static boolean setJumpDown() {
        if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion())) {
            JUMP_DOWN_ACTION = 0x0C;
            return true;
        }
        if ((ServerConfig.IsJMS() && 164 <= ServerConfig.GetVersion())) {
            JUMP_DOWN_ACTION = 0x0F;
            return true;
        }
        return false;
    }

    public CMovePath(ClientPacket cp) {
        int ignore_bytes = 0; // 末尾検索

        switch (cp.GetOpcode()) {
            case CP_UserMove: {
                ignore_bytes = 1 + 1 * 9 + 2 * 4; // v164-v186 OK
                break;
            }
            case CP_DragonMove:
            case CP_SummonedMove:
            case CP_PetMove: {
                ignore_bytes = 1 + 2 * 4; // v186
                break;
            }
            case CP_MobMove: {
                ignore_bytes = 1 + 2 * 4;
                if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion()) || ServerConfig.IsKMS()) {
                    ignore_bytes += 1 * 4 + 4;
                }

                if (ServerConfig.IsPostBB()) {
                    ignore_bytes += 8; // 25 bytes
                }
                break;
            }
            default: {
                break;
            }
        }

        data = cp.DecodeMovePath();

        // offset
        int offset_start_x = 0;
        int offset_start_y = 2;
        // JMS under v165 / JMS v186 or later and post BB, very early version's offset is 13
        int offset_end_x = (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 165) ? (data.length - 13 - ignore_bytes) : (data.length - 17 - ignore_bytes);
        int offset_end_y = (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 165) ? (data.length - 11 - ignore_bytes) : (data.length - 15 - ignore_bytes);
        int offset_end_fh = (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 165) ? (data.length - 5 - ignore_bytes) : (data.length - 9 - ignore_bytes);
        int offset_action = data.length - 3 - ignore_bytes;
        // data for updating coordinates
        action = data[offset_action];
        move_start = new Point(ShortToInt(offset_start_x), ShortToInt(offset_start_y));
        move_end = new Point(ShortToInt(offset_end_x), ShortToInt(offset_end_y));
        foothold_id = readShort(offset_end_fh);

        // bandaid fix
        //Debug.DebugLog("XY = " + move_end);
        if (data[offset_end_x - 3] == JUMP_DOWN_ACTION && ShortToInt(offset_end_y + 4) == 0) {
            Point move_end_test = new Point(ShortToInt(offset_end_x - 2), ShortToInt(offset_end_y - 2));
            int vector_0 = (move_end_test.x - move_start.x) * (move_end_test.x - move_start.x) + (move_end_test.y - move_start.y) * (move_end_test.y - move_start.y);
            int vector_1 = (move_end.x - move_start.x) * (move_end.x - move_start.x) + (move_end.y - move_start.y) * (move_end.y - move_start.y);
            if (vector_0 < vector_1) {
                //Debug.DebugLog("XY = " + move_end_test + ", JUMPDOWN DETECTED!");
                move_end = move_end_test;
                foothold_id = readShort(offset_end_fh - 2);
            }
        }
    }

    private short readShort(int offset) {
        return (short) (((short) data[offset] & 0xFF) | (((short) data[offset + 1] & 0xFF) << 8));
    }

    private int ShortToInt(int offset) {
        return readShort(offset);
    }

    public byte[] get() {
        return data;
    }

    public Point getStart() {
        return move_start;
    }

    public Point getEnd() {
        return move_end;
    }

    public int getAction() {
        return action;
    }

    public short getFootHoldId() {
        return foothold_id;
    }

    // CMovePath::Decode
    public static CMovePath Decode(ClientPacket cp) {
        return new CMovePath(cp);
    }
}
