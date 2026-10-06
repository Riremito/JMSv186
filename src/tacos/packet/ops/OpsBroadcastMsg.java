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
package tacos.packet.ops;

import tacos.config.Config;
import tacos.config.Region;

/**
 *
 * @author Riremito
 */
public enum OpsBroadcastMsg {
    BM_ALL,
    BM_CLONE,
    BM_MAP,
    BM_NOTICE(0), // 青文字, [告知事項]
    BM_ALERT(1),
    BM_SPEAKERCHANNEL(2),
    BM_SPEAKERWORLD(3),
    BM_SLIDE(4),
    BM_EVENT(5),
    BM_NOTICEWITHOUTPREFIX(6),
    BM_UTILDLGEX(7),
    BM_ITEMSPEAKER(8),
    BM_ARTSPEAKERWORLD(9),
    MEGAPHONE_TRIPLE(10),
    UNKNOWN_0B(11),
    BM_HEARTSPEAKER(12),
    BM_SKULLSPEAKER(13),
    BM_GACHAPONANNOUNCE(14),
    UNKNOWN_0F(15), // 青文字, 名前:アイテム名(xxxx個))
    BM_CASHSHOPAD(16), // 体験用アバター獲得
    UNKNOWN_11(17), // 青文字, アイテム表示
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
        if (Config.GreaterOrEqual(Region.JMS, 302)) {
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
