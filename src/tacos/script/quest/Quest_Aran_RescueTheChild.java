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
// Quest.wz startscript : q21000s, q21001s, endscript : q21001e (JMS186), 子供を助けてください1, 子供を助けてください2, 旧 js (scripts_JMS) を参考に実装
public class Quest_Aran_RescueTheChild extends QuestScriptMan {

    private final int quest_rescue_1 = 21000; // 子供を助けてください1
    private final int item_child = 4001271; // はぐれた子供
    private final int map_forest = 914000300; // ブラックロード : 森の最奥
    private final int map_movie = 914090010; // アランの動画 (マップ名なし)
    private final int map_ice_cave = 140090000; // スノーアイランド : 氷洞窟

    @Override
    public boolean questStart(int status, int answer) {
        if (getQuestId() == this.quest_rescue_1) {
            // 受諾しなかった
            if (getLastAction() == 0 && status == -1) {
                // TODO : 文章 (js の英文を翻訳)
                say("だめよ、アラン…あの子をひとりここに残していくのなら、逃げる意味がないわ。無理なお願いなのはわかっているけど…どうか考え直して！", false, true);
                return false;
            }
            switch (status) {
                case 0 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    askAccept("待って、あの子はどこ？大変、森の中に取り残されているんだわ！方舟が出る前にあの子を連れ戻さないと！アラン…森に入ってあの子を見つけてきて！怪我をしているのに無理なお願いなのはわかっているけど…あなたしか頼れないの！");
                    return true;
                }
                case 1 -> {
                    // TODO : js ではクエストのデータに文字化けした値 ("..w?PGﾃ・・") を設定している
                    forceStartQuest();
                    // js では status 1 が重複しており "The most important thing right now is not to panic, Aran. ..." は表示されない
                    // TODO : 文章 (js の英文を翻訳)
                    say("#bあの子はきっと森の奥のどこかにいるわ#k！暗黒の魔法使いに見つかる前にすぐに出発しないといけないから、急いで！", false, true);
                    return true;
                }
                case 2 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    say("アラン、森からあの子を助け出して！暗黒の魔法使いのせいで、これ以上犠牲者を出すわけにはいかないわ！", true, true);
                    return true;
                }
                case 3 -> {
                    avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1");
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        // 受諾しなかった
        if (getLastAction() == 0 && status == -1) {
            // TODO : 文章 (js の英文を翻訳)
            say("いやだー！アランがぼくをここに置いていこうとしてる！！..", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("う…（震えながら）ここ…すごく…こわ…かった…。#p1209000#のところに連れていって！");
                return true;
            }
            case 1 -> {
                if (getQuestStatus(getQuestId()) == 0) {
                    gainItem(this.item_child, 1);
                    forceStartQuest();
                }
                warp(this.map_forest);
                return false;
            }
            default -> {
            }
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        // いいえを選択した
        if (getLastAction() == 0 && status == -1) {
            // TODO : 文章 (js の英文を翻訳)
            say("あの子はどこ？あの子を連れてきたのなら、私に預けて！", false, true);
            return false;
        }
        // 動画を見る
        if (getLastAction() == 0 && status == 7) {
            if (haveItem(this.item_child, 1)) {
                removeItem(this.item_child, 1);
            }
            setStandAloneMode(true);
            setDirectionMode(true);
            forceCompleteQuest();
            questEndEffect();
            warp(this.map_movie);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                askYesNo("ああ、無事だったのね！あの子は？あの子はどこ？");
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("..ああ、よかった…本当によかった..", false, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "今すぐ乗って！時間がないわ！", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ええ、そうね。そんなことをしている時間はないわ。暗黒の魔法使いの力がすぐそこまで迫っているのを感じるの。方舟の場所も見つかってしまったみたい！今出発しないと襲われてしまうわ！", true, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "今すぐ出発するんだ！", true, true);
                return true;
            }
            case 5 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("アラン！今すぐ乗って！一緒に戦いたい気持ちはわかるけど…もう遅いわ！暗黒の魔法使いは仲間たちに任せて、あなたは今すぐ乗ってビクトリアアイランドへ逃げて！", true, true);
                return true;
            }
            case 6 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "いや、そうはいかない！", true, true);
                return true;
            }
            case 7 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#p1209000#、みんなを連れてビクトリアアイランドへ向かってくれ。約束する、俺は死なない。すぐに島で会おう。俺は仲間を助けて、暗黒の魔法使いと決着をつけてくる！", true, true);
                return true;
            }
            case 8 -> {
                // TODO : 文章 (js の英文を翻訳)
                askYesNo("映像をスキップしますか？スキップしてもゲームの進行には影響ありません。");
                return true;
            }
            case 9 -> {
                // 動画を見ない
                if (haveItem(this.item_child, 1)) {
                    removeItem(this.item_child, 1);
                }
                forceCompleteQuest();
                questEndEffect();
                warp(this.map_ice_cave);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
