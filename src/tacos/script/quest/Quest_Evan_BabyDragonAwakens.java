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
// Quest.wz startscript : q22500s (JMS186), ちびドラゴン目覚める, 旧 js (scripts_JMS) を参考に実装
public class Quest_Evan_BabyDragonAwakens extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 11) {
            // TODO : 文章 (js の英文を翻訳)
            say("信じないのか？ぐるるる、怒るぞ！", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ついに外に出られた！*すうっ* ああ、これが空気ってやつか。それに、あれは太陽だな！あれは木！あれは草！あれは花！うはははは！すごいぞ！卵の中に閉じ込められていたときに想像していた世界よりずっといい。それで、お前…お前が俺のご主人様か？ふーん、思ってたのと違うな。", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#bうわあああ、しゃべった！", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("変なご主人様だな。でも契約しちゃったから、今さらどうしようもないか。*はあ* まあ、よろしくな。これから長い付き合いになるぞ。", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#bえ？どういうこと？長い付き合いって？契約って何？", true, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("どういうことってどういうことだよ！お前が俺を卵から起こしたんだろ。お前は俺のご主人様だ！だから俺の面倒を見て、鍛えて、強いドラゴンにするのはお前の役目に決まってるだろ！", true, true);
                return true;
            }
            case 5 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#bええっ？ドラゴン？君がドラゴンなの？！よくわからないよ…どうして僕が君のご主人様なの？何の話をしてるの？", true, true);
                return true;
            }
            case 6 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("お前こそ何言ってるんだよ？お前の魂が俺の魂と契約したんだ！俺たちはもうほとんど同じ存在なんだよ。いちいち説明しなきゃだめか？その結果、お前は俺のご主人様になったんだ。俺たちは契約で結ばれてる。今さら気は変えられないぞ…契約は破れないんだ。", true, true);
                return true;
            }
            case 7 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#bちょっと待って、待って。整理させて。つまり、僕は君を助けるしかないってこと？", true, true);
                return true;
            }
            case 8 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("そのとおり！おーい…！なんだその顔は？お前…俺のご主人様になりたくないのか？", true, true);
                return true;
            }
            case 9 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#bいや…そうじゃなくて…ペットを飼う心の準備ができてるかわからなくて。", true, true);
                return true;
            }
            case 10 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ペ、ペ、ペット？！今、俺のことをペットって言ったか？！よくも…俺はドラゴンだぞ！世界で一番強い生き物なんだぞ！", true, true);
                return true;
            }
            case 11 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#b…#b（疑わしげに見つめた。トカゲにしか見えない。しかも、ちっぽけな。）#k", true, true);
                return true;
            }
            case 12 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("なんでそんな目で見るんだよ！見てろよ！俺の力でどんなことができるか見せてやる。いいか？");
                return true;
            }
            case 13 -> {
                forceStartQuest();
                // TODO : 文章 (js の英文を翻訳)
                say("#r#o1210100##kを倒せと命令してくれ！今すぐだ！ドラゴンがどれだけ速く#o1210100#を倒せるか見せてやる！いっけえ、突撃だ！", false, true);
                return true;
            }
            case 14 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ちょっと待て！APは振ったか？俺はご主人様の#bINTとLUK#kに大きく影響されるんだ！俺の力を本当に見たいなら、スキルを使う前にAPを振って、#b魔法使いの装備を身につけろ#k！", true, true);
                return true;
            }
            case 15 -> {
                evanTutorial("UI/tutorial/evan/11/0");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
