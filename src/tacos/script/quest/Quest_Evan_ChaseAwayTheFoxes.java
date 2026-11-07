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
// Quest.wz startscript : q22008s, endscript : q22008e (JMS186), 裏庭の狐の追放, 旧 js (scripts_JMS) を参考に実装
public class Quest_Evan_ChaseAwayTheFoxes extends QuestScriptMan {

    private final int item_wand = 1372043; // 初歩魔法使いのワンド
    private final int item_milk = 2022621; // 美味しい牛乳
    private final int item_juice = 2022622; // 美味しいジュース
    private final int reward_exp = 910;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == -1) {
            // TODO : 文章 (js の英文を翻訳)
            say("#o9300385#が怖いのか？俺の家族だなんて誰にも言うなよ。恥ずかしいからな。", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("変だな。ニワトリの様子がおかしいんだ。前はもっとたくさん#t4032451#を産んでいたのに。狐のせいだと思うか？だとしたら、急いで何とかしないとな。");
                return true;
            }
            case 1 -> {
                forceStartQuest();
                // TODO : 文章 (js の英文を翻訳)
                say("だろ？一緒にあの狐たちを倒しに行こう。まずはお前が先に行って、#b#m100030103##kで#r#o9300385#を10匹#k倒してくれ。俺は後からついて行って、残りを片付けるから。さあ、急いで#m100030103#へ行け！", false, true);
                return true;
            }
            case 2 -> {
                evanTutorial("UI/tutorial/evan/10/0");
                return false;
            }
            default -> {
            }
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ずる賢い狐は倒したか？", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#b残りの狐を片付けるって話はどうなったの？", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ああ、あれか？はは。いちおう追いかけたんだけどさ、お前に追いつかれないようにしたかったんだよ。お前が#o9300385#に食べられでもしたら大変だろ。だから放っておいたんだ。", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#b本当は狐が怖くて隠れてたんじゃないの？", true, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("何だって？まさか！ふん、俺に怖いものなんてないさ！", true, true);
                return true;
            }
            case 5 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#b危ない！すぐ後ろに#o9300385#がいるよ！", true, true);
                return true;
            }
            case 6 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ひいいっ！ママー！", true, true);
                return true;
            }
            case 7 -> {
                say(3, "#b...", true, true);
                return true;
            }
            case 8 -> {
                say("...", true, true);
                return true;
            }
            case 9 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("このガキ！俺はお前の兄ちゃんだぞ。からかうな！兄ちゃんは心臓が弱いんだからな。そんなふうに驚かすなよ！", true, true);
                return true;
            }
            case 10 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#b（だから兄ちゃんって呼びたくないんだよ…）", true, true);
                return true;
            }
            case 11 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ふん！とにかく、#o9300385#を倒してくれてよかったよ。お礼に、昔ある冒険家にもらったものをやるよ。ほら。 \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i" + this.item_wand + "# 1 #t" + this.item_wand + "# \r\n#i" + this.item_milk + "# 25 #t" + this.item_milk + "# \r\n#i" + this.item_juice + "# 25 #t" + this.item_juice + "#s \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " 経験値", true, true);
                return true;
            }
            case 12 -> {
                if (getQuestStatus(getQuestId()) == 1) {
                    forceCompleteQuest();
                    questEndEffect();
                    gainItem(this.item_wand, 1);
                    gainItem(this.item_milk, 25);
                    gainItem(this.item_juice, 25);
                    gainExp(this.reward_exp);
                }
                // TODO : 文章 (js の英文を翻訳)
                say("#bこれは魔法使いが使う武器、ワンドだ#k。お前には必要ないだろうけど、持ち歩けば偉そうに見えるぞ。はははははは。", true, true);
                return true;
            }
            case 13 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("それにしても、狐が増えてるよな？おかしくないか？どうして日に日に増えてるんだろう？ちゃんと調べて、原因を突き止めないとな。", true, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
