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

import tacos.server.map.TacosMap;

/**
 *
 * @author Riremito
 */
public class Npc_Event_Genta extends NpcScriptMan {

    // 海外ボスモンスターレイド
    private final int[] mob_ids = {
        9400337, // パパシュ
        9400449, // アエムロボット
        9400339, // ビックアイズフィッシュ
        9400340, // スカイエレファント
        9400341, // パープルエレファント
        // 休憩1
        9400342, // 大王ムカデ
        9400343, // ウッドスペクター
        9400344, // クレーンゲーム機
        9400346, // 屋台
        9400450, // フロッグ
        // 休憩2
        9400349, // ファイアスピード
        9400350, // ライオンマスク
        9400351, // 白銀巨人
        9400352, // 黄金巨人
        9400353, // 武林妖僧
        // 休憩3
        9400354, // 大蛇
        9400357, // ホワイトベア
        9400361, // ドライウッドスペクター
        9400452, // ブラックキトン
        9400454, // 腐ったスケルトン
        // 休憩4
        9400367, // シティジャングルゴーレム
        9400368, // ホワイトバルログ
        9400369, // ガイフォックス
        9400455, // デッドスケアクロウ
        9400456 // 従者リリム
    };
    private final int[] map_ids = {
        889210100, // 隠れ道 : ステージ1
        889210200, // 隠れ道 : ステージ2
        889210300, // 隠れ道 : ステージ3
        889210400, // 隠れ道 : ステージ4
        889210500, // 隠れ道 : ステージ5
        889210600, // 隠れ道 : ステージ6
        889210700, // 隠れ道 : ステージ7
        889210800, // 隠れ道 : ステージ8
        889210900, // 隠れ道 : ステージ9
        889211000, // 隠れ道 : ステージ10
        889211100, // 隠れ道 : ステージ11
        889211200, // 隠れ道 : ステージ12
        889211300, // 隠れ道 : ステージ13
        889211400, // 隠れ道 : ステージ14
        889211500, // 隠れ道 : ステージ15
        889211600, // 隠れ道 : ステージ16
        889211700, // 隠れ道 : ステージ17
        889211800, // 隠れ道 : ステージ18
        889211900, // 隠れ道 : ステージ19
        889212000, // 隠れ道 : ステージ20
        889212100, // 隠れ道 : ステージ21
        889212200, // 隠れ道 : ステージ22
        889212300, // 隠れ道 : ステージ23
        889212400, // 隠れ道 : ステージ24
        889212500 // 隠れ道 : ステージ25
    };

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 文章が不明 (元のスクリプトの仮の文章), 本来はステージ1から順番に進む (コメントアウトされた旧スクリプト)
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("海外ボスモンスターレイド v180");
                for (int i = 0; i < this.map_ids.length; i++) {
                    nt.addMenu(i, "#o" + this.mob_ids[i] + "# (#m" + this.map_ids[i] + "#)");
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer < 0 || this.map_ids.length <= answer) {
                    return false;
                }
                TacosMap map = findMap(this.map_ids[answer]);
                if (map != null && map.getCharactersSize() == 0) {
                    map.resetFully();
                }
                warp(this.map_ids[answer], "sp");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
