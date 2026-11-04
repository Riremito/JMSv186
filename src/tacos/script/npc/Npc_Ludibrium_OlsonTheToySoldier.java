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

import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
public class Npc_Ludibrium_OlsonTheToySoldier extends NpcScriptMan {

    private final int quest_dollhouse = 3230; // 人形の家
    private final int npc_right = 2040001; // ライトくん
    private final int npc_mark = 2040028; // マークくん
    private final int item_walnut = 4031093; // おいしいクルミ
    private final int item_pendulum = 4031094; // 時計錘
    private final int map_ludibrium = 220000000; // ルディブリアム : ルディブリアム
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        int quest_status = getPlayer().getQuestStatus(this.quest_dollhouse);
        if (quest_status == 2) {
            // 翻訳
            say("#h #のおかげで、#b#t" + this.item_pendulum + "##kを取り戻し、別の次元のモンスターを倒すことができた。ありがたいことに、それ以来あんなモンスターは見つかっていない。手伝ってくれて、どうお礼を言えばいいかわからないよ。" + getMapName(this.map_ludibrium) + "での滞在を楽しんでくれ！");
            return false;
        }
        if (quest_status != 1) {
            NpcScriptTag nt = new NpcScriptTag();
            nt.add("僕達はこの部屋に誰でも入らないように警備をしています。なぜ中にはいれないか、中に何があるかはまだ言えません。僕に用がないんでしたら、もう仕事に戻りますんで、では、さよなら。");
            say(nt);
            return false;
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                askYesNo("ふむ…#b" + getNpcName(this.npc_right) + "#kから君のことはよく聞いているよ。彼が仕事の退屈から逃れられるように、#b#t" + this.item_walnut + "##kをたくさん集めてくれたそうだね。さて…本題だ。中にはとても、とても危険なモンスターがいる。そのモンスターを見つけるのを手伝ってほしいんだ。手を貸してくれるかい？");
                return true;
            }
            case 1 -> {
                if (this.asked) {
                    // 翻訳
                    say("なるほど。この任務を引き受ける準備ができたら話しかけてくれ。あまり時間をかけないことをすすめるよ。モンスターがまったく別のものに化けてしまうかもしれないからね。私たちは何も知らないふりをしなければならないんだ。");
                    return false;
                }
                // 翻訳
                say("本当にありがとう。実は、#b" + getNpcName(this.npc_right) + "#kが君に#b#t" + this.item_walnut + "##kを集めさせたのは、君の能力を試して、この件を任せられるか確かめるためだったんだ。だから、突然の頼みだとは思わないでくれ。君のような人なら、この問題に立ち向かえると思う。", false, true);
                return true;
            }
            case 2 -> {
                this.asked = true;
                // 翻訳
                askYesNo("少し前に、別の次元のモンスターが次元の裂け目を通ってここにやってきて、時計の振り子を盗んでいったんだ。そいつはあの部屋に隠れて、ドールハウスに化けている。私にはどれも同じに見えるから、見つけようがないんだ。そいつを見つけるのを手伝ってくれないか？");
                return true;
            }
            case 3 -> {
                // 翻訳
                say("よし！ドールハウスがたくさん置かれた部屋へ連れて行こう。その中に、ほかと少し違って見えるものが1つあるはずだ。君の仕事は、それを見つけて扉を壊すことだ。見つけられれば、#b#t" + this.item_pendulum + "##kが見つかるだろう。本物のドールハウスを壊してしまうと、ここに送り返されてしまうから、よく注意してくれ。", false, true);
                return true;
            }
            case 4 -> {
                // 翻訳
                say("中にはモンスターもいるが、別の次元のモンスターのせいで強くなりすぎていて、倒すことはできないだろう。時間切れになる前に#b#t" + this.item_pendulum + "##kを見つけて、中にいるはずの#b" + getNpcName(this.npc_mark) + "#kに知らせてくれ。さあ、始めよう！", true, true);
                return true;
            }
            case 5 -> {
                int count_pendulum = getPlayer().getItemQuantity(this.item_pendulum, false);
                if (0 < count_pendulum) {
                    exchange(0, this.item_pendulum, -count_pendulum);
                }
                if (enterFieldSet("Ludi023", 0) != TacosFieldSet.ENTER_SUCCESS) {
                    // 翻訳
                    say("ほかの人が中でドールハウスを探しているようだ。残念ながら一度に1人しか入れられないから、順番を待ってくれ。");
                }
                return false;
            }
            case -1 -> {
                // 翻訳
                say("なるほど。とても危険なモンスターと戦うことになるんだから、無理もない。もし気が変わったら、また話しかけてくれ。君の助けがあれば、きっととても心強いはずだ。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
