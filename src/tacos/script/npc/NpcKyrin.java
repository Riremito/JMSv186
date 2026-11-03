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

/**
 *
 * @author Riremito
 */
public class NpcKyrin extends NpcScriptMan {

    private final int npc_3rd_job = 2020013; // ペドロ
    private final int item_black_charm = 4031059; // 黒い護符
    private final int item_necklace_of_strength = 4031057; // 強靭のネックレス

    @Override
    public boolean action(int status, int answer) {
        // TODO : 1次転職, 2次転職 (インファイター, ガンスリンガー), 3次転職 (レベル70), 海賊の説明, カイリンの訓練場 (912010200) のクエスト (6330, 6370) (コメントアウトされた旧スクリプト)
        switch (status) {
            case 0 -> {
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("何か用？");
                nt.addMenu(0, "海賊について知りたいです。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer != 0) {
                    return false;
                }
                // 3次転職 力のテスト
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                if (checkItem(this.item_black_charm)) {
                    nt.add("あら…私の分身を倒して#b#z" + this.item_black_charm + "##kを持ってきたのね。ふふっ…やるじゃない。これであなたの実力は十分に証明されたわ。力だけなら、3次転職をするのに申し分ないくらい備わっているわね。じゃあ、約束通り、あなたに#b#z" + this.item_necklace_of_strength + "##kをあげるわ。このネックレスを持って、オシリアの#b" + getNpcName(this.npc_3rd_job) + "#kさんのところに行けば、2番目のテストを受けることができると思うわ。じゃあ、あなたが無事に3次転職できるよう、祈ってるわ。");
                } else {
                    // TODO : 他の次元に通じる亀裂への入場許可
                    nt.add("あなたのことを待ってたわ。この間、オシリアの#b" + getNpcName(this.npc_3rd_job) + "#kさんからあなたのことを聞いたのよ。ふふっ…いいわ。あなたの実力、私が試してみましょう。エビルアイの巣って知ってる？　そのどこかに、他の次元に通じる亀裂があるはずよ。普通の人は入ることができないんだけれど、あなただけは入れるようにしておくから。亀裂の中に入ると、私の分身がいると思うわ。その分身を倒して、#b#z" + this.item_black_charm + "##kを入手して、私の所まで戻ってきてごらんなさい。");
                }
                say(nt, false, true);
                return true;
            }
            case 2 -> {
                if (checkItem(this.item_black_charm)) {
                    // TODO : アイテム欄の空き確認
                    MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_black_charm, 1, true, false);
                    MapleInventoryManipulator.addById(getPlayer(), this.item_necklace_of_strength, 1); // TODO : item effect.
                    return false;
                }
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("私の分身の強さは想像もつかないでしょう？　そうね…色々な高レベルのスキルを使ってくるのは確実よ。そんな相手にあなたは1人で戦わないといけないから、相当大変だと思うわ。それに、戦う場所はこの世界とは異なる次元の世界だから、普通の人間が長居するのはあまり良くないの。できるだけ素早く倒すのよ？　だから、挑戦するなら　事前に万全の準備を整えてからね。絶対よ。じゃあ、あなたが#b#z" + this.item_black_charm + "##kを持って、無事に戻ってくるのを待っているわ。");
                say(nt, true, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
