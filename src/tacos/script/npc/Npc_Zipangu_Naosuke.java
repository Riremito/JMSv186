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
public class Npc_Zipangu_Naosuke extends NpcScriptMan {

    private final int map_dream_room = 800040300; // ジパング : 楓城 夢幻の間
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (this.asked) {
                    // 移動確認でいいえを選択した
                    // TODO : 文章 (適当)
                    say("…ほらな？この先は危険な道だ。足を踏み入れた者はみな痛い目にあっている。俺なら、命があるうちに引き返すね。");
                    return false;
                }
                // TODO : 文章 (適当)
                say("うわっ！誰だお前は！？", false, true);
                return true;
            }
            case 1 -> {
                this.asked = true;
                // TODO : 文章 (適当)
                askYesNo("何？ここから先へ進みたいだと？この先に何があるのか分かって言っているのか？");
                return true;
            }
            case 2 -> {
                // TODO : 文章 (適当)
                say("…分かった。この先に何があるか知った上で行くのなら、止めはしない。無事に天守閣までたどり着いて…あいつらを倒してくれることを願っているよ！", false, true);
                return true;
            }
            case 3 -> {
                warp(this.map_dream_room);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
