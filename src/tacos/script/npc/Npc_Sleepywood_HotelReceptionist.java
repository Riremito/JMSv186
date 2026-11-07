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
package tacos.script.npc;

/**
 *
 * @author Riremito
 */
public class Npc_Sleepywood_HotelReceptionist extends NpcScriptMan {

    private final int[] map_ids = {
        105040401, // 中央ダンジョン : 回復サウナ室＜一般＞
        105040402 // 中央ダンジョン : 回復サウナ室＜高級＞
    };
    private final int[] costs = {
        499,
        999
    };
    private int room_index = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("いらっしゃいませ。スリーピーウッドホテルでございます。当ホテルは皆様に最高のサービスを提供するようにいつも心がけています。戦いに疲れたなら当ホテルを利用してはいかがでしょうか？");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                if (0 <= this.room_index) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("当ホテルは別のサービスもございますので、ゆっくりお考えになってからご利用下さい。");
                    say(nt);
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("当ホテルには2つのルームがございます。どちらのルームを利用しますか？");
                nt.addMenu(0, "一般サウナ室(1回：" + this.costs[0] + "メル)");
                nt.addMenu(1, "高級サウナ室 (1回：" + this.costs[1] + "メル)");
                askMenu(nt);
                return true;
            }
            case 2 -> {
                if (answer < 0 || this.map_ids.length <= answer) {
                    return false;
                }
                this.room_index = answer;
                NpcScriptTag nt = new NpcScriptTag();
                if (this.room_index == 0) {
                    nt.add("一般サウナ室をご利用ですね。HPとMPの回復ができて、色々なアイテムの売買も行えます。よろしいでしょうか？");
                } else {
                    nt.add("高級サウナ室をご利用ですね。一般サウナ室よりも速くHPとMPの回復ができて、中では特別なアイテムを買うこともできます。よろしいでしょうか？");
                }
                askYesNo(nt);
                return true;
            }
            case 3 -> {
                if (!payMoney(this.costs[this.room_index])) {
                    // 翻訳
                    say("申し訳ありません。お金が足りないようです。当ホテルのご利用には最低" + this.costs[this.room_index] + "メルかかります。");
                    return false;
                }
                warp(this.map_ids[this.room_index]);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
