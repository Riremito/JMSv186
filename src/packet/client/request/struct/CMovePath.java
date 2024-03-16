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
package packet.client.request.struct;

import config.ServerConfig;
import java.awt.Point;
import packet.client.ClientPacket;

/**
 *
 * @author Riremito
 */
public class CMovePath {

    private byte data[] = null;
    Point move_start;
    Point move_end;
    int action;

    public CMovePath(ClientPacket cp) {
        //this.action = cp.getMoveAction();
        data = cp.DecodeMovePath();

        if (cp.GetOpcode() == ClientPacket.Header.CP_UserMove) {
            // m_aKeyPadState is enabled
            cp.Decode1();
            for (int i = 0; i < 9; i++) {
                // m_aSendBuff
                cp.Decode1();
            }
        }

        // offset
        int offset_start_x = 0;
        int offset_start_y = 2;
        // JMS under v165 / JMS v186 or later and post BB, very early version's offset is 13
        int offset_end_x = (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 165) ? (data.length - 13) : (data.length - 17);
        int offset_end_y = (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 165) ? (data.length - 11) : (data.length - 15);
        int offset_action = data.length - 3;
        // data for updating coordinates
        action = data[offset_action];
        move_start = new Point(ShortToInt(offset_start_x), ShortToInt(offset_start_y));
        move_end = new Point(ShortToInt(offset_end_x), ShortToInt(offset_end_y));
    }

    private int ShortToInt(int offset) {
        return (short) (((short) data[offset] & 0xFF) | (((short) data[offset + 1] & 0xFF) << 8));
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

    // CMovePath::Decode
    public static CMovePath Decode(ClientPacket cp) {
        return new CMovePath(cp);
    }
}
