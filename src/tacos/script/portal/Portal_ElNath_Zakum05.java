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

import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : Zakum05 (JMS186), lua の処理を利用
// テストサーバー (serverType == 2) の分岐とチャンネルの確認は削除
public class Portal_ElNath_Zakum05 extends PortalScriptMan {

    private final int item_eye_of_fire = 4001017; // 火の目
    private final int quest_zakum = 7002; // ジャクムの事前クエストの進行状況
    private final int quest_enter_time = 7003; // 最後に入場した時刻
    private final int quest_enter_count = 7004; // 入場回数
    private final int map_altar_entrance = 211042400; // エルナス : ジャクムの祭壇入口
    private final int minutes_per_day = 1440;

    @Override
    public boolean enter() {
        String val = getQuestData(this.quest_zakum);
        if (!(val.equals("end") || val.equals("s2")) || getItemCount(this.item_eye_of_fire) < 1) {
            // 翻訳
            message("ここに入れるのは、レベル3をクリアした後だけです。火の目も持っている必要があります。");
            return false;
        }
        TacosFieldSet field_set = getFieldSet("ZakumBoss");
        if (field_set == null) {
            return false;
        }
        if (field_set.getVar("boss").equals("yes")) {
            // 翻訳
            message("ボスとの戦いはすでに始まっているので、中に入ることはできません。");
            return false;
        }
        String count = getQuestData(this.quest_enter_count);
        // 24時間後
        boolean passed = this.minutes_per_day <= compareTime(currentTime(), getQuestData(this.quest_enter_time));
        if (passed || count.isEmpty() || count.equals("1")) {
            if (val.equals("s2")) {
                setQuestData(this.quest_zakum, "end");
            }
            return transferField(this.map_altar_entrance, "west00");
        }
        // 翻訳
        message("ジャクムの祭壇には1日2回しか入れません。");
        return false;
    }
}
