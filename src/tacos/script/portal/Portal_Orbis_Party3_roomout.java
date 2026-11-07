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
// Map.wz portal script : party3_roomout (JMS186), lua の処理を利用
// 各部屋から中央塔へパーティーを戻す
public class Portal_Orbis_Party3_roomout extends PortalScriptMan {

    private final int map_center_tower = 920010100; // 女神の塔 : 中央塔
    private final int map_walkway = 920010200; // 女神の塔 : 散歩路
    private final int map_lounge = 920010600; // 女神の塔 : ラウンジ
    private final int map_climb = 920010700; // 女神の塔 : 登り道
    private final int map_dark_room = 920011000; // 女神の塔 : 暗黒の部屋
    // TODO : 文章 (lua の部屋の名前は英語とポルトガル語のため、party3_room の文章に合わせた)
    private final String[] room_names = {"<遊歩道>", "<倉庫>", "<ロビー>", "<封印された部屋>", "<休憩室>", "<上への出口>", "<闇の部屋>"};

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("Party5");
        if (field_set == null) {
            return false;
        }
        if (!isPartyBoss()) {
            // 翻訳
            message("この部屋から出るかどうかを決められるのはパーティーリーダーだけです。");
            return false;
        }
        int index;
        if (getMapId() == this.map_dark_room) {
            index = 6;
        } else if (this.map_walkway <= getMapId() && getMapId() <= this.map_climb && getMapId() % 100 == 0) {
            index = (getMapId() - this.map_walkway) / 100;
        } else {
            return false;
        }
        if (getMapId() == this.map_lounge) {
            for (int i = 1; i <= 4; i++) {
                if (getUserCount(this.map_lounge + i) != 0) {
                    // 翻訳
                    message("パーティーの誰かがまだ部屋にいるので、出られません。");
                    return false;
                }
            }
        }
        // 翻訳
        field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "パーティーリーダーが次の場所から出ました:" + this.room_names[index] + ".");
        return transferFieldAll(this.map_center_tower, String.format("in%02d", index));
    }
}
