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
// Quest.wz endscript : q6700e (JMS186), 궁수의 길 (lua のコメントでは 矢の支給), lua の処理を利用
public class Quest_Victoria_ArrowSupply extends QuestScriptMan {

    private final int item_arrow_bow = 2060000; // 弓専用の矢
    private final int item_arrow_crossbow = 2061000; // 弩専用の矢
    private final int arrow_count = 6000;

    @Override
    public boolean questEnd(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getQuestStatus(getQuestId()) != 1) {
                    return false;
                }
                // 翻訳
                askMenu("あなたにプレゼントを用意しておきました。旅の役に立つはずです。ほしい矢を選んでください。\r\n#fUI/UIWindow.img/QuestIcon/4/0\r\n\r\n#b#L0# #v" + this.item_arrow_bow + "##t" + this.item_arrow_bow + "# 6000本#l\r\n#L1# #v" + this.item_arrow_crossbow + "##t" + this.item_arrow_crossbow + "# 6000本#l");
                return true;
            }
            case 1 -> {
                int item_id = (answer == 0) ? this.item_arrow_bow : this.item_arrow_crossbow;
                if (!exchange(0, item_id, this.arrow_count)) {
                    // 翻訳
                    say("消費インベントリに少なくとも3つ空きスロットが必要です。");
                    return false;
                }
                forceCompleteQuest();
                questEndEffect();
                // 翻訳
                say("#b#t" + item_id + "##kを6000本渡しました。さあ、メイプルワールド一の弓使いになれるよう修行してください。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
