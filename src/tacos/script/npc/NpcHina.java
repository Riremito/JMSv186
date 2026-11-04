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
// Npc.wz script : begin_jp3 (JMS131-176), 会話文は lua_script_jp (begin_jp3) を利用
public class NpcHina extends NpcScriptMan {

    private final int map_training_exit = 3; // メイプルアイランド : 冒険者講習所出口

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // 翻訳
                askYesNo("訓練は終わりましたか？よければ、この訓練場から出してあげますよ。");
                return true;
            }
            case 1 -> {
                // 翻訳
                say("では、ここから外へお送りします。お疲れさまでした。", false, true);
                return true;
            }
            case 2 -> {
                warp(this.map_training_exit, "st00");
                return false;
            }
            default -> {
                // 翻訳
                say("まだ訓練プログラムを終えていないのですか？ここから出たくなったら、遠慮なく言ってくださいね。");
                return false;
            }
        }
    }
}
