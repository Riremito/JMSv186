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
public class NpcPhil extends NpcScriptMan {

    // 原文通りの並び
    private final int[] map_ids = {
        101000000, // エリニア : エリニア
        102000000, // ぺリオン : ぺリオン
        104000000, // リス : 港口
        100000000, // ヘネシス : ヘネシス
        103000000, // カニングシティー : カニングシティー
        120000000 // ノーチラス船着場 : ノーチラス
    };
    // TODO : 料金
    private final int[] costs = {
        0,
        0,
        0,
        0,
        0,
        0
    };
    private int to_map_index = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 初心者の場合の文章と料金
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("君は初心者ではないな？なら料金は規定どおりにいただくぜ？さあ、どの村へ行きたいんだい？");
                for (int i = 0; i < this.map_ids.length; i++) {
                    // 現在のマップはスキップ
                    if (getMapId() == this.map_ids[i]) {
                        continue;
                    }
                    nt.addMenu(i, getMapName(this.map_ids[i]) + "(" + this.costs[i] + "メル)");
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer < 0 || this.map_ids.length <= answer || getMapId() == this.map_ids[answer]) {
                    return false;
                }
                this.to_map_index = answer;
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("ここではもう用事がないようですね。本当に" + getMapName(this.map_ids[this.to_map_index]) + "へ移動しますか？");
                askYesNo(nt);
                return true;
            }
            case 2 -> {
                // TODO : 料金の支払い
                warp(this.map_ids[this.to_map_index]);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
