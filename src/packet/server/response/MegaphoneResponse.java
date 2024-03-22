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
package packet.server.response;

import client.inventory.IItem;
import handling.MaplePacket;
import java.util.List;
import packet.server.ServerPacket;
import packet.server.response.struct.GW_ItemSlotBase;

/**
 *
 * @author Riremito
 */
public class MegaphoneResponse {

    private enum Action {
        // 青文字, [告知事項]
        UNKNOWN_00((byte) 0x00),
        // ダイアログ
        UNKNOWN_01((byte) 0x01),
        // メガホン
        MEGAPHONE_BLUE((byte) 0x02),
        // 拡声器
        MEGAPHONE((byte) 0x03),
        // 画面上部
        UNKNOWN_04((byte) 0x04),
        // ピンク文字
        UNKNOWN_05((byte) 0x05),
        // 青文字
        UNKNOWN_06((byte) 0x06),
        // 用途不明, 0x00B93F3F[0x07] = 00B93E27
        UNKNOWN_07((byte) 0x07),
        // アイテム拡声器
        MEGAPHONE_ITEM((byte) 0x08),
        // ワールド拡声器, 未実装
        MEGAPHONE_GREEN((byte) 0x09),
        // 三連拡声器
        MEGAPHONE_TRIPLE((byte) 0x0A),
        // 用途不明, 0x00B93F3F[0x0B] = 00B93ECA
        UNKNOWN_0B((byte) 0x0B),
        // ハート拡声器
        MEGAPHONE_HEART((byte) 0x0C),// v131 -> 0x08
        // ドクロ拡声器
        MEGAPHONE_SKULL((byte) 0x0D), // v131 -> 0x09
        // ガシャポン
        MEGAPHONE_GASHAPON((byte) 0x0E),
        // 青文字, 名前:アイテム名(xxxx個))
        UNKNOWN_0F((byte) 0x0F),
        // 体験用アバター獲得
        MEGAPHONE_AVATAR((byte) 0x10),
        // 青文字, アイテム表示
        UNKNOWN_11((byte) 0x11),
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

    // ドクロ拡声器
    public static MaplePacket MegaphoneSkull(String text, byte channel, byte ear) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(Action.MEGAPHONE_SKULL.Get());
        p.EncodeStr(text);
        p.Encode1((byte) (channel - 1));
        p.Encode1(ear);
        return p.Get();
    }

    // 拡声器
    public static MaplePacket Megaphone(String text, byte channel, byte ear) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(Action.MEGAPHONE.Get());
        p.EncodeStr(text);
        p.Encode1((byte) (channel - 1));
        p.Encode1(ear);
        return p.Get();
    }

    // ハート拡声器
    public static MaplePacket MegaphoneHeart(String text, byte channel, byte ear) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(Action.MEGAPHONE_HEART.Get());
        p.EncodeStr(text);
        p.Encode1((byte) (channel - 1));
        p.Encode1(ear);
        return p.Get();
    }

    // アイテム拡声器
    public static MaplePacket MegaphoneItem(String text, byte channel, byte ear, byte showitem, IItem item) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(Action.MEGAPHONE_ITEM.Get());
        p.EncodeStr(text);
        p.Encode1((byte) (channel - 1));
        p.Encode1(ear);
        p.Encode1(showitem);
        if (showitem != 0) {
            p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
        }
        return p.Get();
    }

    // 三連拡声器
    public static MaplePacket MegaphoneTriple(List<String> text, byte channel, byte ear) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(Action.MEGAPHONE_TRIPLE.Get());
        // 1行目
        p.EncodeStr(text.get(0));
        p.Encode1((byte) text.size());
        for (int i = 1; i < text.size(); i++) {
            p.EncodeStr(text.get(i));
        }
        p.Encode1((byte) (channel - 1));
        p.Encode1(ear);
        return p.Get();
    }

    // メガホン
    public static MaplePacket MegaphoneBlue(String text) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(Action.MEGAPHONE_BLUE.Get());
        p.EncodeStr(text);
        return p.Get();
    }

}
