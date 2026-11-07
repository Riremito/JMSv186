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
// Quest.wz startscript : q22003s (JMS186), お弁当配達, 旧 js (scripts_JMS) を参考に実装
// js では status 2 の処理が抜けていて会話が進まないため、status 3, 4 の処理を status 2, 3 で行う
public class Quest_Evan_LunchBoxDelivery extends QuestScriptMan {

    private final int item_lunch_box = 4032448; // 愛が込められたお弁当

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == -1) {
            // TODO : 文章 (js の英文を翻訳)
            say("いい子はお母さんの言うことを聞くものよ。さあ、エヴァン、いい子だからもう一度話しかけて。", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("#bお父さん#kが今朝農場へ出かけるときに、お弁当を忘れていったの。#b#m100030300##kにいるお父さんに#bこのお弁当を届けて#kくれる？");
                return true;
            }
            case 1 -> {
                forceStartQuest();
                // TODO : 文章 (js の英文を翻訳)
                say("ふふ、うちのエヴァンは本当にいい子ね！#b家を出たら左へ#k行ってね。急いでお父さんのところへ。きっとお腹を空かせているわ。", false, true);
                if (!haveItem(this.item_lunch_box, 1)) {
                    gainItem(this.item_lunch_box, 1);
                }
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("もしお弁当をなくしたら戻ってきてね。もう一度作ってあげるから。", true, true);
                return true;
            }
            case 3 -> {
                evanTutorial("UI/tutorial/evan/5/0");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
