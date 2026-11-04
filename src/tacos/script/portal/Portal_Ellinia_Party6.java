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

/**
 *
 * @author Riremito
 */
// Map.wz portal script : party6_stage, party6_out (JMS186), js の処理を利用
// エリン森 (毒の森) のパーティークエスト
public class Portal_Ellinia_Party6 extends PortalScriptMan {

    private final int map_forest_front = 930000000; // エリン森 : 森の前
    private final int map_forest_edge = 930000100; // エリン森 : 森の端
    private final int map_altered_forest = 930000200; // エリン森 : 変質した森
    private final int map_foggy_forest = 930000300; // エリン森 : 霧の森
    private final int map_forest_exit = 930000800; // エリン森 : 森出口
    private final int item_altaire_fragment = 4001198; // アルテアの欠片
    private final int mob_boss_check = 9300183; // ボスモンスター討伐チェック用透明モンスター

    @Override
    public boolean enter() {
        switch (getScriptName()) {
            case "party6_stage" -> {
                if (getMapId() == this.map_forest_front) {
                    return warp(this.map_forest_edge, 0);
                }
                if (getMapId() == this.map_forest_edge) {
                    if (getMonsterCount() != 0) {
                        // TODO : 文章 (js の英文を翻訳, 原文 : Eliminate all the monsters.)
                        playerMessage(5, "全てのモンスターを倒してください。");
                        return false;
                    }
                    return warp(this.map_altered_forest, 0);
                }
                if (getMapId() == this.map_altered_forest) {
                    int state = getReactorState("spine");
                    if (state != -1 && state < 4) {
                        // TODO : 文章 (js の英文を翻訳, 原文 : The spine blocks the way.)
                        playerMessage(5, "トゲが道を塞いでいる。");
                        return false;
                    }
                    return warp(this.map_foggy_forest, 0);
                }
                return false;
            }
            case "party6_out" -> {
                int state = getReactorState("");
                boolean cleared = getMonsterCount() == 0 || getMap().getMonsterById(this.mob_boss_check) != null;
                if (!canHold(this.item_altaire_fragment) || !cleared || (state != -1 && state != 1)) {
                    // TODO : 文章 (js の英文を翻訳, 原文 : Please eliminate the Poison Golem and have room)
                    playerMessage(5, "ポイズンゴーレムを倒し、インベントリに空きを作ってください。");
                    return false;
                }
                gainExpR(52000);
                endPartyQuest(1206);
                // TODO : js では gainNX(2000)
                gainItem(this.item_altaire_fragment, 1);
                return warp(this.map_forest_exit, 0);
            }
            default -> {
                return false;
            }
        }
    }
}
