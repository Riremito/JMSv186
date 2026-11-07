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
package tacos.script.quest;

/**
 *
 * @author Riremito
 */
// Quest.wz endscript : q3314e (JMS186), 生命錬金術と、失踪した錬金術師, lua の処理を利用
// TODO : 状態異常 (checkCondition) が未実装のため、現状では完了できない
public class Quest_Magatia_LifeAlchemyAndTheMissingAlchemist extends QuestScriptMan {

    private final char condition_required = '6'; // lua の target.checkCondition に 6 が含まれる
    private final int item_all_cure_potion = 2050004; // 万病治療薬
    private final int item_russellon_pill_rare = 2022199; // ラセルロンの薬
    private final int item_russellon_pill_1 = 2022224; // ラセルロンの薬
    private final int item_russellon_pill_2 = 2022225; // ラセルロンの薬
    private final int item_russellon_pill_3 = 2022226; // ラセルロンの薬
    private final int item_russellon_pill_4 = 2022227; // ラセルロンの薬

    @Override
    public boolean questEnd(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (checkCondition().indexOf(this.condition_required) < 0) {
                    // 翻訳
                    say("…まだ薬を飲んでいないようだな。このラッセロンが信用できないというのか？アルカドノの先輩として、君には手本だけを示してきたつもりだったのに…");
                    return false;
                }
                // 翻訳
                askMenu("ふふふふふ…顔色が青白くなったのを見ると、やはり効果があるな。今回の実験は成功だ！うわっはっは！やはりロイドを倒せるほど丈夫な奴なら使っても大丈夫だな！\r\n#L0##b（やっぱり人体実験だったのか！）#k#l");
                return true;
            }
            case 1 -> {
                // 翻訳
                askMenu("ずいぶん驚いた顔だな？そんなに心配することはない。それほど危険な薬じゃない…いや、危険な薬だが、解毒剤はあるからな…ふふふふ…\r\n#L0##b（病気にしてから薬をくれても意味がない！）#k#l");
                return true;
            }
            case 2 -> {
                // 翻訳
                askMenu("これで人体の状態を自由に変えられるようになった。これで生命錬金がもっと楽になるだろう。これで、あいつの願いをかなえてやれるかもしれないな…\r\n#L0##bあいつ？#k#l");
                return true;
            }
            case 3 -> {
                // lua では 40300 経験値と表示されるが、実際は 12500
                // 翻訳
                say("ああ…あいつだ。生命錬金の分野では最高の奴だった。誰よりも優れた腕を持っていたのに…あいつさえいれば、こんな研究はすぐに片付いただろう。だが仕方ない…あいつはもう失踪してしまったのだから…\r\n\r\n#fUI/UIWindow.img/QuestIcon/5/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 40300 経験値", false, true);
                return true;
            }
            case 4 -> {
                int r = random(0, 20);
                int item_id;
                if (r == 0) {
                    item_id = this.item_russellon_pill_rare;
                } else if (r < 6) {
                    item_id = this.item_russellon_pill_1;
                } else if (r < 11) {
                    item_id = this.item_russellon_pill_2;
                } else if (r < 16) {
                    item_id = this.item_russellon_pill_3;
                } else {
                    item_id = this.item_russellon_pill_4;
                }
                if (!exchange(0, this.item_all_cure_potion, 10, item_id, 20)) {
                    // 翻訳
                    say("消費インベントリがいっぱいなのではないか？確認してみたまえ。");
                    return false;
                }
                gainExp(12500);
                forceCompleteQuest();
                questEndEffect();
                // 翻訳
                say("あいつがなぜ失踪したのかは誰も知らない。いつからか、あいつは焦り始めて、人目を盗んで得体の知れない研究を始めたんだ。いくら聞いても、どんな研究なのか教えてくれなかった。あいつは半分狂ったようだった。研究、研究、研究…休みなく研究ばかりしていた。生命錬金に関する…そしてついに、#bあの事件#kが起きたんだ…", false, true);
                return true;
            }
            case 5 -> {
                // 翻訳
                say("錬金術師の村と呼ばれるマガティアでも、あれほどの大爆発は一度もなかった…あいつがどんな実験をしたのか、見当もつかない。一体どんな恐ろしいものを研究していたのだろう…あいつの家を調べたのだから協会長は何か知っているはずなのに、何も教えてくれない…", false, true);
                return true;
            }
            case 6 -> {
                // 翻訳
                say("この研究も最初はあいつとの共同研究だったんだ。だが、あいつは消えてしまい、これ以上研究を進めるのは難しくなった。薬には自信がある方だが、それでもやはり簡単ではない。あいつがやっていたことだから続けてはいるが…あいつは一体なぜ人体の状態を変える研究をしたのだろう…？", false, true);
                return true;
            }
            case 7 -> {
                // 翻訳
                say("あいつはまだ生きているはずだ。あいつには、そうでなければならない理由があるのだから。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
