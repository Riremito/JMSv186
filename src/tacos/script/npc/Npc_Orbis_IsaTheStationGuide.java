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
public class Npc_Orbis_IsaTheStationGuide extends NpcScriptMan {

    private final int[] map_ids = {
        200000110, // オルビス : ステーション通路<エリニア行き>
        200000120, // オルビス : ステーション通路<ルディブリアム行き>
        200000130, // オルビス : ステーション通路<リプレ行き>
        200000140, // オルビス : ステーション<武陵行き>
        200000150, // オルビス : ステーション通路<アリアント行き>
        200000160 // オルビス : ステーション通路<エレヴ行き>
    };
    private final String[] map_names = {
        "エリニア行きの船に乗る昇降場",
        "ルディブリアム行きの船に乗る昇降場",
        "リプレ行きの船に乗る昇降場",
        "武陵行きのツルに乗る昇降場",
        "アリアント行きのジニに乗る昇降場",
        "エレヴ行きの船に乗る昇降場"
    };
    private int to_index = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (0 <= this.to_index) {
                    // 翻訳
                    say("行き先をよく確かめてから、私を通して乗り場へ向かってください。出発時刻は決まっていますので、乗り遅れないように！");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("オルビスステーションには多くの昇降場があります。目的地によって正しい昇降場へ行かなければなりません。どの方面に向かう船の昇降場に行くんですか？");
                for (int i = 0; i < this.map_ids.length; i++) {
                    nt.addMenu(i, this.map_names[i]);
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer < 0 || this.map_ids.length <= answer) {
                    return false;
                }
                this.to_index = answer;
                // 翻訳
                askYesNo("間違った乗り場に行っても、ポータルを使ってここに戻ってこられます。#b" + this.map_names[this.to_index] + "#kへ行きますか？");
                return true;
            }
            case 2 -> {
                warp(this.map_ids[this.to_index], "west00");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
