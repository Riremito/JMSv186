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
// Quest.wz startscript : q22507s (JMS186), ドラゴンマスターとは？, 旧 js (scripts_JMS) を参考に実装
public class Quest_Evan_WhatIsADragonMaster extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 13) {
            // TODO : 文章 (js の英文を翻訳)
            say("え、冗談だろ？指がすべったって言ってくれよ！さあ、クエストを受けてくれ。", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("やっぱりな！俺たちはつながってるって思ってたんだ、ご主人様！お前が強くなれば俺も強くなる。そして俺が強くなれば、お前は俺の力を使える！それが俺たちの契約さ。いいご主人様を選んだって思ってたよ！", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#bなるほど。でも、そもそもどうして契約することになったの？#k", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("わからない。俺はただの卵だったからな。よく覚えてないんだ…でも、霧の深い森でご主人様が俺のほうへ歩いてきたのはぼんやり覚えてる。俺を見て驚いていたのも覚えてるよ。そして、俺もお前に呼びかけていたんだ。", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（待って！それってあの夢とそっくりだ…夢の中で出会ったってこと？夢で見たあの巨大なドラゴンは…ミルだったのかな？#k", true, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ご主人様、お前と俺は魂がひとつなんだ。お前を見た瞬間にわかったよ。だから、ほかの誰でもなくお前と契約したかったんだ。もちろん、俺が決めた代価は払ってもらったけどな。", true, true);
                return true;
            }
            case 5 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b僕が代価を払ったの？#k", true, true);
                return true;
            }
            case 6 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("覚えてないのか？お前が俺に気づいて、俺に触れたときのことだよ。それが俺の決めたたったひとつの条件だったんだ。お前が俺の卵に触れた瞬間、お前と俺の魂はひとつになったのさ。", true, true);
                return true;
            }
            case 7 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b魂が…ひとつ？", true, true);
                return true;
            }
            case 8 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("そう！魂の契約だ！お前と俺は体は別々だけど、ひとつの魂を分け合ってるんだ。だから俺が強くなればお前も強くなるし、その逆もそうなんだ！すごいだろ？少なくとも俺はそう思うね。", true, true);
                return true;
            }
            case 9 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b何を言ってるのかよくわからないけど、すごいことみたいだね。#k", true, true);
                return true;
            }
            case 10 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("すごいことに決まってるだろ、ばかなご主人様！もうモンスターの心配なんてしなくていいんだ。これからは俺がお前を守ってやるからな！試してみろよ。いや、今すぐ行こうぜ！", true, true);
                return true;
            }
            case 11 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#bでも、ここは平和だよ。危ないモンスターなんていないし。#k", true, true);
                return true;
            }
            case 12 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("なんだって？！そんなのつまらないよ！ご主人様は冒険が好きじゃないのか？みんなのためにモンスターと戦ったり、悪を倒したり、罪のない人を助けたりとかさ？そういうのに興味ないのか？", true, true);
                return true;
            }
            case 13 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b僕の将来設計にはないなあ。冗談だけど、本当に僕は農家の息子だし…#k", true, true);
                return true;
            }
            case 14 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("ちぇっ、じゃあ教えてやるよ。ドラゴンマスターが平和に暮らすなんて無理なんだ。俺の力を見せる機会はいくらでもあるさ。俺を信じろ、俺たちの人生はひとつの大冒険になるんだ。ずっと一緒にいるって約束してくれよ、いいな？");
                return true;
            }
            case 15 -> {
                forceStartQuest();
                // TODO : 文章 (js の英文を翻訳)
                say(1, "へへへ、よし、ご主人様。さあ、始めようぜ！", false, true);
                return true;
            }
            case 16 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#b（少し混乱しているが、これからはドラゴンのミルと一緒に旅をすることになった。ミルの言うとおり、一緒に冒険することになるのかもしれない。）#k", true, true);
                return true;
            }
            case 17 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（まだお使いが残っている。お父さんが話があるそうなので、会いに行こう。）#k", true, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
