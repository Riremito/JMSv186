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
// Quest.wz startscript : q22001s (JMS186), エゾオオカミに餌をあげる, 旧 js (scripts_JMS) を参考に実装, endscript なし (js の end は未使用)
public class Quest_Evan_FeedTheBulldog extends QuestScriptMan {

    private final int item_food = 4032447; // オオカミの餌

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 1) {
            // TODO : 文章 (js の英文を翻訳)
            say("怠けるなよ。兄ちゃんが犬にかまれるところを見たいのか？早くしろ！もう一度話しかけて、クエストを受けてくれ！", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("はは。大笑いしたよ。ははは。でも、冗談はそれくらいにして。#p1013102#に餌をやってくれないか？", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#bえ？それは#p1013101#の仕事でしょ！#k", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("このガキ！兄ちゃんと呼べって言っただろ！#p1013102#が俺のことをどれだけ嫌ってるか知ってるだろ。近づいたらかまれちゃうよ。お前が餌をやってくれ。あいつはお前のことが好きだからな。");
                return true;
            }
            case 3 -> {
                if (getQuestStatus(getQuestId()) == 0) {
                    gainItem(this.item_food, 1);
                    forceStartQuest();
                }
                // TODO : 文章 (js の英文を翻訳)
                say("早く#b左#kへ行って、#b#p1013102##kに餌をやってくれ。朝からずっと餌をくれって吠えてるんだ。", false, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("#p1013102#に餌をやったら、また俺のところに来てくれ。", true, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
