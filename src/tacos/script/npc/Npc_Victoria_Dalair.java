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
public class Npc_Victoria_Dalair extends NpcScriptMan {

    private final int[] item_ids = {
        1142005, // レジェンドハンターの勲章
        1142006, // メイプルアイドルスターの勲章
        1142007, // ホーンテイルスレイヤーの勲章
        1142008, // ピンクビーンスレイヤーの勲章
        1142030 // リス港口寄付王の勲章
    };
    private final String[] medal_names = {
        "伝説的な狩人",
        "メイプルアイドルスター",
        "ホーンテイルスレイヤー",
        "ピンクビーン スレイヤー",
        "街の寄付王"
    };

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("どの勲章のランキングを見たいのだ？");
                for (int i = 0; i < this.item_ids.length; i++) {
                    nt.addMenu(i, this.medal_names[i]);
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                // TODO : 勲章のランキングの表示 (元のスクリプトはデバッグ用に勲章 (item_ids) を渡して称号獲得メッセージを流していた)
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
