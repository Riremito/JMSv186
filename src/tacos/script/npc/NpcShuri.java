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

import odin.server.maps.SavedLocationType;

/**
 *
 * @author Riremito
 */
public class NpcShuri extends NpcScriptMan {

    private final int item_free_pass = 4031134; // フリーパスカード
    private final int map_beach = 110000000; // フロリナ : ビーチ
    private final int cost = 2000;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 文章
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("港口から少し離れたところに#bビーチ#kという幻想的な海岸があると聞いたことはあるか？#b" + this.cost + "メル#kを払うか#bフリーパスカード#kを持っているなら、いつでも俺がそこに運んでやるぜ。");
                nt.addMenu(0, this.cost + "メル#k払う");
                nt.addMenu(1, "フリーパスカード#kを持っている。");
                nt.addMenu(2, "フリーパスカード#kとは？");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                // TODO : フリーパスカード (item_free_pass) を持っている, フリーパスカードとは？
                if (answer != 0) {
                    return false;
                }
                // TODO : 文章
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#b" + this.cost + "メル#kを払ってビーチに行くんだな？でも、そこにもモンスターがいるらしいから油断するよ！では、早速出向準備をするが…今すぐビーチに行くのか？");
                askYesNo(nt);
                return true;
            }
            case 2 -> {
                // TODO : 2000 メルの支払い
                getPlayer().saveLocation(SavedLocationType.FLORINA);
                warp(this.map_beach); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
