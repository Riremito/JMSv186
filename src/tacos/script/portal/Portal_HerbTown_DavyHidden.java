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
// Map.wz portal script : davy2_hd1, davy3_hd1 (JMS186), lua の処理を利用
// 海賊王の手下の隠しマップへの入場, 最初に入場してから 50 秒間は他のキャラクターも入場できる
public class Portal_HerbTown_DavyHidden extends PortalScriptMan {

    private final int map_hidden1 = 925100202; // デイビーゾーンの手下1
    private final int map_hidden2 = 925100302; // デイビーゾーンの手下2
    private final int time_open = 50;

    @Override
    public boolean enter() {
        boolean stage3 = getScriptName().equals("davy2_hd1");
        TacosFieldSet field_set = getFieldSet(stage3 ? "DavyJohn3" : "DavyJohn4");
        if (field_set == null) {
            return false;
        }
        String key_clear = stage3 ? "clear_3_hd" : "clear_4_hd";
        String key_time = stage3 ? "s3_eTime" : "s4_eTime";
        // ゲートオープン
        if (!field_set.getVar(key_clear).equals("1")) {
            // 時間記録
            field_set.setVar(key_time, String.valueOf(field_set.getQuestTime()));
            // 入場処理
            TacosFieldSet field_set_next = getFieldSet(stage3 ? "DavyJohn3_hd" : "DavyJohn4_hd");
            int res = field_set_next == null ? TacosFieldSet.ENTER_ERROR : field_set_next.enter(getPlayer(), 0);
            if (res == TacosFieldSet.ENTER_ERROR) {
                // 翻訳
                message("現在このポータルは使用できません。しばらくしてからもう一度お試しください。");
                return false;
            }
            if (res != TacosFieldSet.ENTER_SUCCESS) {
                return false;
            }
            field_set.setVar(key_clear, "1");
            // 翻訳
            field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, getPlayer().getName() + "が海賊王の手下" + (stage3 ? "I" : "II") + "に入場しました。今から50秒間、このマップに入場できます。");
            return true;
        }
        int time = field_set.getQuestTime() - integer(field_set.getVar(key_time));
        if (time < this.time_open) {
            return transferField(stage3 ? this.map_hidden1 : this.map_hidden2, "");
        }
        // 翻訳
        message("ここで時間をかけすぎたため、このポータルは閉じられました。");
        return false;
    }
}
