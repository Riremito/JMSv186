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
public class Npc_Victoria_RegularCab extends NpcScriptMan {

    private final int[] map_ids = {
        104000000, // リス : 港口
        100000000, // ヘネシス : ヘネシス
        102000000, // ぺリオン : ぺリオン
        101000000, // エリニア : エリニア
        103000000, // カニングシティー : カニングシティー
        120000000 // ノーチラス船着場 : ノーチラス
    };
    // 料金 (lua の taxi1 - taxi4), [出発地][目的地] で map_ids と同じ並び, 初心者は 1/10
    // TODO : リス港口とノーチラスの料金 (lua に記載なし)
    private final int[][] costs = {
        // リス : 港口 (lua に記載なし)
        {0, 0, 0, 0, 0, 0},
        // ヘネシス (taxi2)
        {800, 0, 1000, 1000, 1200, 0},
        // ぺリオン (taxi1)
        {1200, 1000, 0, 1000, 800, 0},
        // エリニア (taxi4)
        {1200, 1000, 1000, 0, 1200, 0},
        // カニングシティー (taxi3)
        {1000, 1000, 800, 1200, 0, 0},
        // ノーチラス (lua に記載なし)
        {0, 0, 0, 0, 0, 0}
    };
    private int to_map_index = -1;

    @Override
    public boolean action(int status, int answer) {
        boolean beginner = getJob() == 0;
        int from_map_index = -1;
        for (int i = 0; i < this.map_ids.length; i++) {
            if (getMapId() == this.map_ids[i]) {
                from_map_index = i;
            }
        }
        // いいえを選択した
        if (getLastAction() == 0 && status == 1) {
            // 翻訳
            say("この町にも見どころはたくさんありますよ。別の場所へ行きたくなったら、また声をかけてください。");
            return false;
        }
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("こんにちは！　メイプル運輸大型タクシーでございます。他の村への安全で迅速な移動をお望みですか？でしたら我がタクシーをご利用ください。安い値段でお望みの場所まで親切にご案内しております。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                if (beginner) {
                    // 翻訳
                    nt.add("初心者の方には90%の特別割引があります。");
                }
                nt.add("目的地をお選びください。村事に料金が異なります。");
                for (int i = 0; i < this.map_ids.length; i++) {
                    // 現在のマップはスキップ
                    if (getMapId() == this.map_ids[i]) {
                        continue;
                    }
                    int cost = (from_map_index < 0) ? 0 : this.costs[from_map_index][i];
                    if (beginner) {
                        cost /= 10;
                    }
                    nt.addMenu(i, getMapName(this.map_ids[i]) + "(" + cost + "メル)");
                }
                askMenu(nt);
                return true;
            }
            case 2 -> {
                if (answer < 0 || this.map_ids.length <= answer || getMapId() == this.map_ids[answer]) {
                    return false;
                }
                this.to_map_index = answer;
                int cost = (from_map_index < 0) ? 0 : this.costs[from_map_index][this.to_map_index];
                if (beginner) {
                    cost /= 10;
                }
                NpcScriptTag nt = new NpcScriptTag();
                // 翻訳
                nt.add("ここでの用事はもう済みましたよね？#b#m" + this.map_ids[this.to_map_index] + "##kへ行きますか？料金は#b" + cost + " mesos#k.");
                askYesNo(nt);
                return true;
            }
            case 3 -> {
                int cost = (from_map_index < 0) ? 0 : this.costs[from_map_index][this.to_map_index];
                if (beginner) {
                    cost /= 10;
                }
                if (!payMoney(cost)) {
                    // 翻訳
                    say("お金が足りませんね。残念ですが、十分なお金がないとタクシーには乗れませんよ。");
                    return false;
                }
                warp(this.map_ids[this.to_map_index]);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
