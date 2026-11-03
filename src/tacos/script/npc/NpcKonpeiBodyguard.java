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

import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;
import tacos.server.map.TacosMap;

/**
 *
 * @author Riremito
 */
public class NpcKonpeiBodyguard extends NpcScriptMan {

    private final int item_comb = 4000138; // 姐御の櫛
    private final int item_lantern = 4000141; // 大親分の提灯
    private final int map_boss = 801040100; // ジパング : 悪夢の果て
    private final int max_users = 20;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1, 0 -> {
                if (status == -1 || answer < 0) {
                    // 翻訳
                    say("そうか？じゃあ、気が変わったら教えてくれ。");
                    return false;
                }
                // 大親分の提灯の回収
                int lantern = getPlayer().getItemQuantity(this.item_lantern, false);
                if (0 < lantern) {
                    MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_lantern, lantern, true, false);
                }
                if (!checkItem(this.item_comb)) {
                    // 翻訳
                    say("ここまで来たか。悪くない。これからボスに挑んでもらう！お前の力で強大なボスに立ち向かえるのか心配だが…誤解しないでくれ、うちのボスも彼女には歯が立たなかったんだ。もしボスを倒して彼女の櫛を持ち帰ってくれたら、次のステージへ連れて行ってやる。");
                    return false;
                }
                // TODO : 1日1回の入場制限 (クエスト 8001), 戦闘中の場合は "悪いが、戦いはもう始まっている。安全のため、今は外で待っていてもらわないと。" (翻訳)
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
                if (map != null && this.max_users <= map.getCharactersSize()) {
                    // 翻訳
                    say("今、勇敢な戦士たちがたくさん中で、俺たちのボスに取りついた悪霊と戦っている。部屋には一度に" + this.max_users + "人しか入れないんだ。今は順番を待ってくれ。");
                    return false;
                }
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
