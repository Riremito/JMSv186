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

import handling.MaplePacket;
import packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class ResCUIVega {

    private enum Action {
        // 成功
        SUCCESS((byte) 0x3B),
        // 不明
        START_NOT_USED((byte) 0x3C),
        // 開始
        START((byte) 0x3E),
        // 失敗
        FAILURE((byte) 0x40),
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

    // ベガの呪文書の結果
    public static MaplePacket Result(boolean isSuccess) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_VegaResult);
        // 成功可否
        p.Encode1((byte) (isSuccess ? Action.SUCCESS.Get() : Action.FAILURE.Get()));
        return p.Get();
    }

    // ベガの呪文書開始
    public static MaplePacket Start() {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_VegaResult);
        // 0x3E or 0x40
        p.Encode1(Action.START.Get());
        return p.Get();
    }

}
