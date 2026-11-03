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
public class NpcEurekTheAlchemist extends NpcScriptMan {

    private final int[] item_ids = {
        4006000, // 魔法の石
        4006001 // 召喚の石
    };

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("モグラの舌とフクロウの嘴を1:1の比率で混ぜ合わせて…あれ！ほのかな光の粉を入れることを忘れてた。大変～！あれ？いつからそこにいました？あ…僕があまりにも仕事に夢中になっていて気づいていなかったのか。これは失敬。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("ご覧の通り、僕は風来坊の錬金術師です。まだ未熟者ですが、あなたが必要なものを作れるかもしれません。一度見てみますか？");
                for (int i = 0; i < this.item_ids.length; i++) {
                    nt.addMenu(i, "#t" + this.item_ids[i] + "#作り");
                }
                askMenu(nt);
                return true;
            }
            case 2 -> {
                // TODO : 魔法の石, 召喚の石の作成 (材料とメルが不明), 元のスクリプトはデバッグモードで材料なしに100個渡していた
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
