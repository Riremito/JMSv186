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
// Map.wz portal script : Zakum03 (JMS186), lua の処理を利用
public class Portal_ElNath_Zakum03 extends PortalScriptMan {

    private final int item_fire_ore = 4031061; // 火の原石かけら
    private final int item_return_scroll = 2030007; // 閉鉱帰還の書
    private final int quest_zakum = 7000; // ジャクムの事前クエストの進行状況
    private final int quest_party_count = 7006; // パーティーリーダーとして挑戦した回数と日付
    private final int map_exit = 280090000; // 閉鉱 : 悲恋の部屋
    private final int exp_clear = 20000;
    private final int exp_clear_no = 12000;

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("Party2");
        if (field_set == null) {
            return false;
        }
        String result = field_set.getVar("zakum");
        String clear_key = getPlayer().getName() + "_";
        String clear = field_set.getVar(clear_key);
        boolean finished = result.equals("yes") || result.equals("no");
        if (finished && !field_set.getVar("tcheck").equals("1")) {
            if (2 < compareTime(currentTime(), field_set.getVar("ctime"))) {
                field_set.setVar("tresult", "1");
            }
            field_set.setVar("tcheck", "1");
        }
        if (!field_set.getVar("tresult").equals("1")) {
            if (finished) {
                // 翻訳
                message("このクエストをあまりにも早く完了したため、努力に対する報酬は与えられません。");
                return transferField(this.map_exit, "west00");
            }
            // 翻訳
            message("ポータルは今閉じています。先に進むには、まずレベル1をクリアする必要があります。");
            return false;
        }
        if (result.equals("yes")) {
            if (!exchange(0, this.item_fire_ore, 1, this.item_return_scroll, 5)) {
                // 翻訳
                message("ETCインベントリか消費インベントリがいっぱいなので、次のマップへ進めません。");
                return false;
            }
        } else if (result.equals("no")) {
            if (!exchange(0, this.item_fire_ore, 1)) {
                // 翻訳
                message("ETCインベントリがいっぱいなので、次のマップへ進めません。");
                return false;
            }
        } else {
            // 翻訳
            message("ポータルは今閉じています。先に進むには、まずレベル1をクリアする必要があります。");
            return false;
        }
        // 1日に3回
        if (isPartyBoss()) {
            String val = getQuestData(this.quest_party_count);
            if (val.isEmpty()) {
                setQuestData(this.quest_party_count, "1" + substring(currentTime(), 0, 8));
            } else {
                setQuestData(this.quest_party_count, (integer(substring(val, 0, 1)) + 1) + substring(val, 1, 8));
            }
        }
        setQuestData(this.quest_zakum, "end");
        if (clear.isEmpty()) {
            incExp(result.equals("yes") ? this.exp_clear : this.exp_clear_no);
            field_set.setVar(clear_key, "clear");
        }
        return transferField(this.map_exit, "west00");
    }
}
