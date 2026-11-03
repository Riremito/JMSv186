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
public class NpcMysteriousStatue extends NpcScriptMan {

    // 忍耐の森
    private final int[] map_ids = {
        105040310, // 隠れ道 : 忍耐の森「1段階」
        105040311, // 隠れ道 : 忍耐の森「2段階」
        105040312, // 隠れ道 : 忍耐の森「3段階」
        105040313, // 隠れ道 : 忍耐の森「4段階」
        105040314, // 隠れ道 : 忍耐の森「5段階」
        105040315, // 隠れ道 : 忍耐の森「6段階」
        105040316 // 隠れ道 : 忍耐の森「7段階」
    };

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : クエスト (2052, 2053, 2054) の確認, 元のスクリプトはデバッグモードの選択肢で移動先を選択していた
                // クエスト 2052 : 忍耐の森「1段階」, 「2段階」
                // クエスト 2053 : 忍耐の森「3段階」, 「4段階」
                // クエスト 2054 : 忍耐の森「5段階」, 「6段階」, 「7段階」
                // クエスト中でない場合 : 石像に手を出してみても何も起こりませんでした。
                askYesNo("石像に手を出してみると不思議な光に全身を包まれ、中に吸い込まれるような感覚に襲われました。このまま移動しますか？");
                return true;
            }
            case 1 -> {
                warp(this.map_ids[0]); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
