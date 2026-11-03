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
public class NpcChamberlainEak extends NpcScriptMan {

    private final int map_exit = 920011200; // 出口
    private final int map_tower = 200080101; // 見知らぬ塔

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getMapId() == this.map_exit) {
                    // TODO : 女神の像のかけら (4001044 - 4001063) の回収
                    warp(this.map_tower); // TODO : portal name.
                    return false;
                }
                // TODO : グループクエスト (OrbisPQ) の各ステージの処理, グループ長の確認 (中国語の文章が文字化けしているため未移行)
                // 920010000 入口 : グループを中央塔の portal 2 に移動
                // 920010100 中央塔 : ステージ6をクリアして女神の像のかけら(4001055)がなければ庭園(920010800)に移動
                //                    Please, save Minerva! Gather the six pieces of her statue and talk to me to retrieve the final piece!
                // 920010200 散歩路 : 像のかけら(4001050) 30個で 1つ目のかけら(4001044) とグループ経験値 7500
                //                    Gather the 30 Statue Pieces from the monsters in this stage, and please bring them to me so I can put them together!
                // 920010300 倉庫 : 像のかけら(4001051) 15個で 2つ目のかけら(4001045) とグループ経験値 7500
                //                    Gather the 15 Statue Pieces from the monsters in this stage, and please bring them to me so I can put them together!
                // 920010400 休憩室 : 曜日のLP (4001056 - 4001062) をプレーヤーに置くとリアクターを起動
                //                    Please, find the LP for the current day of week and place it on the music player.
                // 920010500 封印された部屋 : 3つのエリアの人数が正しければリアクターを起動, グループ経験値 7500
                //                    The portal is opened! Go!
                // 920010600 ラウンジ : 像のかけら(4001052) 40個で 5つ目のかけら(4001048) とグループ経験値 7500
                //                    Gather the 40 Statue Pieces from the monsters in this stage, and please bring them to me so I can put them together!
                // 920010700 登り道 : 5つのレバーのうち正しい2つが押されていればリアクターを起動, グループ経験値 7500
                //                    There needs to be 2 levers at the top of the map pushed on.
                // 920010800 庭園 : グループを中央塔(920010100)に移動
                // 920010900 罪人の部屋, 920011000 暗黒の部屋 : 説明
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
