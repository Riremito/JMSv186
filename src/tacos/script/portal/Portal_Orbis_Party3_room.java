/*
 * Copyright (C) 2026 Riremito
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
package tacos.script.portal;

import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : party3_room1, party3_room2, party3_room3, party3_room4, party3_room5, party3_room6 (JMS186), lua の処理を利用
// 中央塔から各部屋へ移動, パーティーリーダーが入った部屋にのみ他のメンバーも入れる
public class Portal_Orbis_Party3_room extends PortalScriptMan {

    private final int map_walkway = 920010200; // 女神の塔 : 散歩路 (party3_room1 = 920010200, party3_room2 = 920010300, ...)
    private final int map_lounge = 920010600; // 女神の塔 : ラウンジ
    private final String[] room_names = {"<遊歩道>", "<倉庫>", "<ロビー>", "<封印された部屋>", "<休憩室>", "<上への出口>"};

    @Override
    public boolean enter() {
        int index = integer(substring(getScriptName(), 11, 1));
        int map_room = this.map_walkway + (index - 1) * 100;
        TacosFieldSet field_set = getFieldSet("Party5");
        if (field_set == null) {
            return false;
        }
        if (field_set.getVar("stage" + index + "_clear").equals("1")) {
            if (index == 1) {
                // 翻訳
                message("この部屋ではもうすることはないようです。");
            } else {
                // 翻訳
                message("この部屋ではもうすることはありません。");
            }
            return false;
        }
        if (index == 6 && !field_set.getVar("room6_ans").equals("1")) {
            // 正解 (stage6_ans1, stage6_ans2) と不正解 (stage6_wans1 ~ stage6_wans3) の番号を決める
            String answers = shuffle("11000");
            int t = 0;
            int k = 0;
            for (int i = 0; i < answers.length(); i++) {
                if (answers.charAt(i) == '1') {
                    field_set.setVar("stage6_ans" + (++t), String.valueOf(i + 1));
                } else {
                    field_set.setVar("stage6_wans" + (++k), String.valueOf(i + 1));
                }
            }
            field_set.setVar("room6_ans", "1");
        }
        if (!isPartyBoss()) {
            int count = getUserCount(map_room);
            if (map_room == this.map_lounge) {
                for (int i = 1; i <= 4; i++) {
                    count += getUserCount(map_room + i);
                }
            }
            if (count <= 0) {
                // 翻訳
                message("パーティーリーダーがいる場所にしか入れません。");
                return false;
            }
            return transferField(map_room, "st00");
        }
        // 翻訳
        field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "パーティーリーダーが" + this.room_names[index - 1] + "に入りました。");
        return transferField(map_room, "st00");
    }
}
