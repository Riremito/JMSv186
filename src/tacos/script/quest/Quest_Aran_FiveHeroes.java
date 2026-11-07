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
// Quest.wz startscript : q21100s (JMS186), 5人の英雄, 旧 js (scripts_JMS) を参考に実装
public class Quest_Aran_FiveHeroes extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 5) {
            // 原文ママ
            // TODO : 文章 (js の英文を翻訳)
            say("あら、5匹では物足りませんか？もっと修行が必要だと思うなら、それ以上倒しても構いませんよ。全部倒してしまっても、心は痛みますが見なかったことにしましょう。立派な目的のための犠牲ですから…", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(8, "暗黒の魔法使いと戦った英雄たちの記録はほとんど残っていません。予言書にも、英雄が5人いたということしか書かれていないのです。誰だったのか、どんな姿だったのかもわかりません。何か覚えていることはありませんか？どんなことでもいいのです。", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "何も覚えていない…", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(8, "やはりそうですか。暗黒の魔法使いの呪いは、記憶をすべて消してしまうほど強力だったのですね。それでも、あなたが英雄の一人だとはっきりした今、いつか過去が明らかになるはずです。戦いの中で鎧と武器をなくしてしまったようですが…ああ、そうでした。忘れるところでした！あなたの#b武器#kです！", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "私の武器？", true, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(8, "少し前に氷の塊を掘っていたら、すごい武器を見つけたのです。英雄の武器だと思って村に持ち帰り、村の中央に置いておきました。見かけませんでしたか？#bあの#p1201001##kです…\r\r#i4032372#\r\rこんな形をしています…", true, true);
                return true;
            }
            case 5 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "そういえば、村で#p1201001#を見かけた。", true, true);
                return true;
            }
            case 6 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("そう、それです。記録によると、英雄の武器は本当の持ち主を見分けるそうです。あなたが#p1201001#を使っていた英雄なら、#p1201001#をつかんだときに反応するはずです。#b#p1201001#を探してクリックしてみてください。#k");
                return true;
            }
            case 7 -> {
                if (getQuestStatus(getQuestId()) == 0) {
                    forceCompleteQuest();
                    questEndEffect();
                }
                // js の sendOkS は type が反映されない
                // TODO : 文章 (js の英文を翻訳)
                say(8, "#p1201001#があなたに反応すれば、あなたが#p1201001#を振るっていた英雄、#bアラン#kだとわかります。", false, false);
                showReservedEffect("Effect/Direction1.img/aranTutorial/ClickPoleArm");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
