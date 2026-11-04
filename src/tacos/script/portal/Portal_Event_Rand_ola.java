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
// Map.wz portal script : rand_ola (JMS186), lua の処理を利用
// のぼれ～のぼれ～ のポータル, 正解は NPC (event_master) が Event1 の ola_ans1 ~ ola_ans3 にランダムに設定する
// ola_ans の各文字 : 0, 1 = 正解 (次の段階へ), スタート地点などへ戻る, 非アクティブ (何もしない)
public class Portal_Event_Rand_ola extends PortalScriptMan {

    private final int map_stage1 = 109030001; // 隠れ道 : のぼれ～のぼれ～＜１段階＞
    private final int map_stage2 = 109030002; // 隠れ道 : のぼれ～のぼれ～＜２段階＞
    private final int map_stage3 = 109030003; // 隠れ道 : のぼれ～のぼれ～＜３段階＞
    private final int map_exchange = 109050000; // 隠れ道 : 商品交換所

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("Event1");
        if (field_set == null) {
            return false;
        }
        int id = getPortalId();
        if (getMapId() == this.map_stage1 && 19 <= id && id <= 23) {
            // lua の ola_answer1
            String c = substring(field_set.getVar("ola_ans1"), id - 19, 1);
            if (c.equals("0") || c.equals("1")) {
                return transferField(this.map_stage2, "start00");
            }
            if (c.equals("2") || c.equals("3")) {
                return transferField(-1, "np00");
            }
            return false;
        }
        if (getMapId() == this.map_stage2 && 9 <= id && id <= 16) {
            // lua の ola_answer2
            String c = substring(field_set.getVar("ola_ans2"), id - 9, 1);
            if (c.equals("0") || c.equals("1") || c.equals("2")) {
                return transferField(this.map_stage3, "start00");
            }
            if (c.equals("3") || c.equals("4")) {
                return transferField(-1, "np01");
            }
            if (c.equals("5")) {
                return transferField(-1, "np02");
            }
            return false;
        }
        if (getMapId() == this.map_stage3 && 11 <= id && id <= 26) {
            // lua の ola_answer3
            String c = substring(field_set.getVar("ola_ans3"), id - 11, 1);
            switch (c) {
                case "0", "1" -> {
                    return transferField(this.map_exchange, "start00");
                }
                case "2", "3", "4", "5", "6" -> {
                    return transferField(-1, "np03");
                }
                case "7" -> {
                    return transferField(-1, "np04");
                }
                case "8" -> {
                    return transferField(-1, "np05");
                }
                case "9" -> {
                    return transferField(-1, "np06");
                }
                default -> {
                    return false;
                }
            }
        }
        return false;
    }
}
