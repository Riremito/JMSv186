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
// Npc.wz script : begin4 (JMS131-165), 会話文は lua_script_jp (begin4) を利用
public class Npc_MapleIsland_Peter extends NpcScriptMan {

    private final int map_lith_harbor = 104000000; // リス : 港口
    private final int map_small_forest = 40000; // メイプルロード : 小さな森

    @Override
    public boolean action(int status, int answer) {
        if (getPlayer().getJob() != 0) {
            if (status == 0) {
                // 翻訳
                say("ここは見習いのためのクエストエリアだよ。君は見習いじゃないよね？", false, true);
                return true;
            }
            warp(this.map_lith_harbor);
            return false;
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                say("こんなところまで来るなんて…すごいね！もう今すぐ旅を始められるよ！よし、次の場所まで連れて行ってあげよう。", false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                say("でも、一つ忠告しておくよ。ここを出たら君は自由だけど、モンスターがたくさんいる場所で、戻る方法もないんだ。じゃあ、またね！", true, true);
                return true;
            }
            case 2 -> {
                getPlayer().gainExp(3, true, true, true);
                warp(this.map_small_forest);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
