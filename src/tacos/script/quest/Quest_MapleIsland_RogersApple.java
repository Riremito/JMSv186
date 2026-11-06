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
// Quest.wz startscript : q1021s, endscript : q1021e (JMS186), ローザーとリンゴ, lua の処理を利用
public class Quest_MapleIsland_RogersApple extends QuestScriptMan {

    private final int item_rogers_apple = 2010007; // ローザーのリンゴ
    private final int item_apple = 2010000; // リンゴ
    private final int item_green_apple = 2010009; // 緑リンゴ
    private boolean had_apple = false;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 1) {
            // 翻訳
            say("僕みたいな親切な人を断るなんて信じられないよ！");
            return false;
        }
        switch (status) {
            case 0 -> {
                if (getGender() == 0) {
                    // 翻訳
                    say("よう、相棒～元気かい？はは！僕は#p2000#、新米の旅人のみんなにいろいろな情報を教えているんだ。", false, true);
                } else {
                    // 翻訳
                    say("やあ、みんな～僕は#p2000#、メイプルに来たばかりのみんなにいろいろな情報を教えているんだ。", false, true);
                }
                return true;
            }
            case 1 -> {
                if (getGender() == 0) {
                    // 翻訳
                    say("誰にやらされているのかって？ははは！僕自身さ！新米の旅人のみんなに親切にしたくて、やりたくてやっているんだよ。", true, true);
                } else {
                    // 翻訳
                    say("#G忙しい:忙しい#のはわかってるよ！ちょっとだけ付き合ってよ～すごく役に立つ情報を教えてあげられるよ！ははは！", true, true);
                }
                return true;
            }
            case 2 -> {
                // 翻訳
                askAccept("それじゃあ…ちょっと盛り上げようか！アブラカダブラ～！");
                return true;
            }
            case 3 -> {
                this.had_apple = getItemCount(this.item_rogers_apple) >= 1;
                if (!this.had_apple && !exchange(0, this.item_rogers_apple, 1)) {
                    // 翻訳
                    say("ねえ、荷物を持ちすぎだよ。");
                    return false;
                }
                addHp(-(getHp() / 2));
                forceStartQuest();
                // 翻訳
                say("#G驚いた:驚いた#？HPが0になると困ったことになるよ。今から#r#t" + this.item_rogers_apple + "##kをあげるから、受け取って。元気が出るよ。アイテムウィンドウを開いて、ダブルクリックして使ってね。  ねえ、アイテムウィンドウを開くのはとっても簡単だよ。キーボードの#bI#kを押すだけさ。#I", false, true);
                return true;
            }
            case 4 -> {
                if (this.had_apple) {
                    // 翻訳
                    say("あげた#t" + this.item_rogers_apple + "#を全部使ってね。HPバーが増えていくのが見えるよ。HPが100%回復したら、また話しかけてね#I");
                } else {
                    // 翻訳
                    say("あげた#t" + this.item_rogers_apple + "#をひとつずつ使ってね。HPバーが増えていくのが見えるよ。HPが100%回復したら、また話しかけてね#I");
                }
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
                if (getItemCount(this.item_rogers_apple) != 0) {
                    // 翻訳
                    say("ほら…あげた#r#t" + this.item_rogers_apple + "##kをひとつずつ使ってって言ったでしょ。アイテムウィンドウを開いて、#b「消費」タブ#bをクリックしてね。そこに#t" + this.item_rogers_apple + "#があるから、ダブルクリックして使うんだ。");
                    return false;
                }
                if (getHp() != getMaxHp()) {
                    // 翻訳
                    say("ねえ、まだHPが完全に回復していないよ。あげた#t" + this.item_rogers_apple + "#をひとつずつ使った？確かかい？");
                    return false;
                }
                // 翻訳
                say("アイテムを使うのは簡単かって？簡単だろう？右下のスロットに#bショートカット#kを設定できるんだ。はは、知らなかっただろう？ああ、それと#G:#見習いのうちは、時間が経つとHPが自動的に回復するよ。まあ、時間はかかるけど、見習いのための作戦のひとつさ。", false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                say("よし！たくさん学んだから、プレゼントをあげよう。メイプルワールドを旅するには必要なものだから、感謝してよね！緊急のときに使ってね！", true, true);
                return true;
            }
            case 2 -> {
                // 翻訳
                say("よし、僕が教えられるのはこれで全部だ。寂しいけど、お別れの時間だよ。じゃあ、元気でね。幸運を祈るよ、#G:##G友よ:友よ#！\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v" + this.item_apple + "# 3 #t" + this.item_apple + "#\r\n#v" + this.item_green_apple + "# 3 #t" + this.item_green_apple + "#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 10 経験値", true, true);
                return true;
            }
            case 3 -> {
                if (!exchange(0, this.item_apple, 3, this.item_green_apple, 3)) {
                    // 翻訳
                    say("うわあ！インベントリにアイテムがいっぱいだね…..");
                    return false;
                }
                gainExp(10);
                forceCompleteQuest();
                questEndEffect();
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
