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
public class Npc_Amoria_ThomasSwift extends NpcScriptMan {

    private final int map_wedding_town = 680000000; // ウェディングタウン : ウェディングタウン
    private final int map_henesys = 100000000; // ヘネシス : ヘネシス
    // ウェディングタウン : 出口
    private final int[] map_exits = {
        680000500,
        680000501,
        680000502
    };
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                if (this.asked) {
                    // 翻訳
                    say("わかりました、行く準備ができるまでゆっくりしていってください！");
                }
                return false;
            }
            case 0 -> {
                if (getMapId() == this.map_wedding_town) {
                    this.asked = true;
                    // 翻訳
                    askYesNo("元いた場所へお連れできます。行く準備はできましたか？");
                    return true;
                }
                for (int map_exit : this.map_exits) {
                    if (getMapId() == map_exit) {
                        // TODO : luaに記載なし (jsではクエスト記録160002を"0"に設定してウェディングタウンへ移動)
                        warp(this.map_wedding_town);
                        return false;
                    }
                }
                return false;
            }
            case 1 -> {
                // 翻訳
                say("楽しい時間を過ごせたならいいのですが！また会いましょう！");
                return true;
            }
            default -> {
                // TODO : 移動先のポータル (jsでは5番)
                warp(this.map_henesys);
                return false;
            }
        }
    }
}
