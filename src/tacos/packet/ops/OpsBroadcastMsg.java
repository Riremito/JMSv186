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
    // GMS v95
    BM_NOTICE(0), // 青文字, [告知事項]
    BM_ALERT(1),
    BM_SPEAKERCHANNEL(2),
    BM_SPEAKERWORLD(3),
    BM_SLIDE(4),
    BM_EVENT(5),
    BM_NOTICEWITHOUTPREFIX(6),
    BM_UTILDLGEX(7),
    BM_ITEMSPEAKER(8),
    BM_SPEAKERBRIDGE(9),
    BM_ARTSPEAKERWORLD(10), // 5077000, 三連拡声器
    BM_BLOWWEATHER(11),
    BM_GACHAPONANNOUNCE(12),
    BM_GACHAPONANNOUNCE_OPEN(13),
    BM_GACHAPONANNOUNCE_COPY(14),
    BM_ULISTCLIP(15),
    BM_FREEMARKETCLIP(16),
    BM_DESTROYSHOP(17),
    BM_CASHSHOPAD(18), // 体験用アバター獲得
    BM_HEARTSPEAKER(19),
    BM_SKULLSPEAKER(20),
    // added for JMS
    BM_JMS_UNKNOWN_0B, // JMS 11
    BM_JMS_UNKNOWN_0F, // JMS 15, 青文字, 名前:アイテム名(xxxx個))
    BM_JMS_UNKNOWN_11, // JMS 17, 青文字, アイテム表示
    UNKNOWN;

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
        for (OpsBroadcastMsg o : OpsBroadcastMsg.values()) {
            if (o.get() == b) {
                return o;
            }
        }

        return UNKNOWN;
    }

    public static void clear() {
        for (OpsBroadcastMsg ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        if (Region.JMS.check()) {
            clear();
            BM_NOTICE.set(0);
            BM_ALERT.set(1);
            BM_SPEAKERCHANNEL.set(2);
            BM_SPEAKERWORLD.set(3);
            BM_SLIDE.set(4);
            BM_EVENT.set(5);
            BM_NOTICEWITHOUTPREFIX.set(6);
            BM_UTILDLGEX.set(7);
            BM_ITEMSPEAKER.set(8);
            // 9 : ?
            BM_ARTSPEAKERWORLD.set(10);
            BM_JMS_UNKNOWN_0B.set(11);
            BM_HEARTSPEAKER.set(12);
            BM_SKULLSPEAKER.set(13);
            BM_GACHAPONANNOUNCE.set(14);
            BM_JMS_UNKNOWN_0F.set(15);
            BM_CASHSHOPAD.set(16);
            BM_JMS_UNKNOWN_11.set(17);
            if (Config.GreaterOrEqual(Region.JMS, 302)) {
                // 9 world (test)
                BM_HEARTSPEAKER.set(15);
                BM_SKULLSPEAKER.set(16);
                BM_GACHAPONANNOUNCE.set(17);
                BM_CASHSHOPAD.set(19);
                BM_JMS_UNKNOWN_0F.set(-1);
                BM_JMS_UNKNOWN_11.set(-1);
                // 21 cake
                // 22 yello crash
            }
            return;
        }
    }
}
