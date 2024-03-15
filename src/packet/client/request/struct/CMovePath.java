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
        this.action = cp.getMoveAction();
        this.data = cp.DecodeMovePath();
        if (cp.GetOpcode() == ClientPacket.Header.CP_UserMove) {
            // m_aKeyPadState is enabled
            cp.Decode1();
            for (int i = 0; i < 9; i++) {
                // m_aSendBuff
                cp.Decode1();
            }
        }
        int move_start_x = (int) cp.Decode2();
        int move_start_y = (int) cp.Decode2();
        int move_end_x = (int) cp.Decode2();
        int move_end_y = (int) cp.Decode2();
        move_start = new Point(move_start_x, move_start_y);
        move_end = new Point(move_end_x, move_end_y);
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
