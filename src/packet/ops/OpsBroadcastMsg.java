/*
 * Copyright (C) 2025 Riremito
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
package packet.ops;

/**
 *
 * @author Riremito
 */
public enum OpsBroadcastMsg {
    // 青文字, [告知事項]
    BM_NOTICE((byte) 0x00),
    // ダイアログ
    BM_ALERT((byte) 0x01),
    // メガホン
    BM_SPEAKERCHANNEL((byte) 0x02),
    // 拡声器
    BM_SPEAKERWORLD((byte) 0x03),
    // 画面上部
    BM_SLIDE((byte) 0x04),
    // ピンク文字
    BM_EVENT((byte) 0x05),
    // 青文字
    BM_NOTICEWITHOUTPREFIX((byte) 0x06),
    // 用途不明, 0x00B93F3F[0x07] = 00B93E27
    BM_UTILDLGEX((byte) 0x07),
    // アイテム拡声器
    BM_ITEMSPEAKER((byte) 0x08),
    // ワールド拡声器, 未実装
    BM_ARTSPEAKERWORLD((byte) 0x09),
    // 三連拡声器
    MEGAPHONE_TRIPLE((byte) 0x0A),
    // 用途不明, 0x00B93F3F[0x0B] = 00B93ECA
    UNKNOWN_0B((byte) 0x0B),
    // ハート拡声器
    BM_HEARTSPEAKER((byte) 0x0C),// v131 -> 0x08
    // ドクロ拡声器
    BM_SKULLSPEAKER((byte) 0x0D), // v131 -> 0x09
    // ガシャポン
    BM_GACHAPONANNOUNCE((byte) 0x0E),
    // 青文字, 名前:アイテム名(xxxx個))
    UNKNOWN_0F((byte) 0x0F),
    // 体験用アバター獲得
    BM_CASHSHOPAD((byte) 0x10),
    // 青文字, アイテム表示
    UNKNOWN_11((byte) 0x11),
    UNKNOWN((byte) -1);

    public static OpsBroadcastMsg find(byte b) {
        for (final OpsBroadcastMsg o : OpsBroadcastMsg.values()) {
            if (o.get() == b) {
                return o;
            }
        }

        return UNKNOWN;
    }

    private byte value;

    OpsBroadcastMsg(byte b) {
        value = b;
    }

    OpsBroadcastMsg() {
        value = -1;
    }

    public byte get() {
        return value;
    }

}
