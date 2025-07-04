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

import config.Region;
import config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsBroadcastMsg {
    BM_ALL,
    BM_CLONE,
    BM_MAP,
    // 青文字, [告知事項]
    BM_NOTICE(0),
    // ダイアログ
    BM_ALERT(1),
    // メガホン
    BM_SPEAKERCHANNEL(2),
    // 拡声器
    BM_SPEAKERWORLD(3),
    // 画面上部
    BM_SLIDE(4),
    // ピンク文字
    BM_EVENT(5),
    // 青文字
    BM_NOTICEWITHOUTPREFIX(6),
    // 用途不明, 0x00B93F3F[0x07] = 00B93E27
    BM_UTILDLGEX(7),
    // アイテム拡声器
    BM_ITEMSPEAKER(8),
    // ワールド拡声器, 未実装
    BM_ARTSPEAKERWORLD(9),
    // 三連拡声器
    MEGAPHONE_TRIPLE(10),
    // 用途不明, 0x00B93F3F[0x0B] = 00B93ECA
    UNKNOWN_0B(11),
    // ハート拡声器
    BM_HEARTSPEAKER(12),// v131 -> 0x08
    // ドクロ拡声器
    BM_SKULLSPEAKER(13), // v131 -> 0x09
    // ガシャポン
    BM_GACHAPONANNOUNCE(14),
    // 青文字, 名前:アイテム名(xxxx個))
    UNKNOWN_0F(15),
    // 体験用アバター獲得
    BM_CASHSHOPAD(16),
    // 青文字, アイテム表示
    UNKNOWN_11(17),
    UNKNOWN(-1);

    private int value;

    OpsBroadcastMsg(int v) {
        value = v;
    }

    OpsBroadcastMsg() {
        value = -1;
    }

    public int get() {
        return value;
    }

    public void set(int v) {
        value = v;
    }

    public static OpsBroadcastMsg find(byte b) {
        for (final OpsBroadcastMsg o : OpsBroadcastMsg.values()) {
            if (o.get() == b) {
                return o;
            }
        }

        return UNKNOWN;
    }

    public static void init() {
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            // 9 world (test)
            BM_HEARTSPEAKER.set(15);
            BM_SKULLSPEAKER.set(16);
            BM_GACHAPONANNOUNCE.set(17);
            BM_CASHSHOPAD.set(19);
            // 21 cake
            // 22 yello crash
        }
    }

}
