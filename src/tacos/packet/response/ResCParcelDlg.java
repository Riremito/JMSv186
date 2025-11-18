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
package tacos.packet.response;

import tacos.network.MaplePacket;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCParcelDlg {

    private enum Action {
        // 不明
        ERROR_09((byte) 0x09),
        // NPCドイから宅配UIを開く
        OPEN((byte) 0x0A),
        // メルが足りません。
        ERROR_0C((byte) 0x0C),
        // 間違った要請です。
        ERROR_0D((byte) 0x0D),
        // 宛先の名前を再確認してください。
        ERROR_0E((byte) 0x0E),
        // 同じID内のキャラクターには送れません。
        ERROR_0F((byte) 0x0F),
        // 宛先の宅配保管箱に空きがありません。
        ERROR_10((byte) 0x10),
        // 宅配を受け取ることができないキャラクターです。
        ERROR_11((byte) 0x11),
        // 1個しか持てないアイテムが宛先の宅配保管箱にあります。
        ERROR_12((byte) 0x12),
        // 宅配物を発送しました。
        SEND((byte) 0x13),
        // 原因不明のエラーが発生しました。
        ERROR_14((byte) 0x14),
        // 空きがあるか確認してください。
        ERROR_16((byte) 0x16),
        // 1個しか持てないアイテムがありメルとアイテムを取り出すことができませんでした。
        ERROR_17((byte) 0x17),
        // 宅配物を受け取りました。
        RECV((byte) 0x18),
        // クラッシュ
        ERROR_19((byte) 0x19),
        // 宅配物到着! 通知
        BALLOON_1A((byte) 0x1A),
        // 速達UI
        OPEN_EXPRESS((byte) 0x1B),
        // 宅配物到着! 通知
        BALLOON_1C((byte) 0x1C),
        // 原因不明のエラーが発生しました。
        ERROR_1D((byte) 0x1D),
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
    public static MaplePacket Open(boolean isQuick, boolean isNPC) {
        ServerPacket p = new ServerPacket(ServerPacketHeader.LP_Parcel);
        // 0x3B or 0x40
        if (isQuick) {
            // 速達のUI
            p.Encode1(Action.OPEN_EXPRESS.Get());
            return p.get();
        }
        // 通常のUI
        p.Encode1((byte) Action.OPEN.Get());
        // NPC会話 or 速達の通知から開いたかの判定
        p.Encode1((byte) (isNPC ? 0 : 1));
        p.Encode1((byte) 0);
        p.Encode1((byte) 0);
        return p.get();
    }

    public static MaplePacket Send() {
        ServerPacket p = new ServerPacket(ServerPacketHeader.LP_Parcel);
        p.Encode1((byte) Action.SEND.Get());
        return p.get();
    }

}
