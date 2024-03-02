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
package packet.client.handling;

import client.MapleClient;
import packet.client.ClientPacket;
import packet.server.response.DueyResponse;

/**
 *
 * @author Riremito
 */
public class DueyPacket {

    private enum Action {
        SEND((byte) 0x03),
        CLOSE((byte) 0x08),
        UNKNOWN((byte) -1);

        public static Action Find(byte b) {
            for (final Action o : Action.values()) {
                if (o.Get() == b) {
                    return o;
                }
            }

            return UNKNOWN;
        }

        private byte value;

        Action(byte b) {
            value = b;
        }

        Action() {
            value = -1;
        }

        public byte Get() {
            return value;
        }

    };

    // 宅配
    public static boolean Accept(MapleClient c, ClientPacket p) {
        // 処理内容
        byte action = p.Decode1();
        switch (Action.Find(action)) {
            // 配送
            case SEND: {
                c.ProcessPacket(DueyResponse.Send());
                return true;
            }
            // 閉じる
            case CLOSE: {
                // 厳密に宅配可能状態か判断する場合は宅配UIを開いた時に何らかのフラグをサーバー側で保持して閉じるパケットが送信されたフラグをオフにする必要がある
                return true;
            }
            default:
                break;
        }
        return false;
    }

}
