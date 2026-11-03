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
public class NpcKonpeiBodyguard extends NpcScriptMan {

    private final int item_comb = 4000138; // 姐御の櫛
    private final int map_boss = 801040100; // ジパング : 悪夢の果て

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 姐御の櫛 (item_comb) の確認
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("ややっ！お前さんが持っているのはここの姐御の櫛じゃないか！？やっぱりオイラが見込んだとおりだった！お前さんなら大親分のやつを倒せるかもしれねぇ。本丸に攻め込むのか？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("本当に入室するんだな？この部屋に長居はできないぜ！それとやつらは部屋の奥の宝箱の上に姐御の櫛を置くとすっ飛んで来るから気をつけろ。");
                askYesNo(nt);
                return true;
            }
            case 2 -> {
                TacosMap map = getPlayer().findMap(this.map_boss);
                if (map != null && map.getCharactersSize() == 0) {
                    map.resetFully();
                }
                warp(this.map_boss);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
