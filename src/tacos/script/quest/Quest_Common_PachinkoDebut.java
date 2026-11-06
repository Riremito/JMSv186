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
// Quest.wz startscript : q50042s, endscript : q50042e (JMS186), パチンコデビュー, 旧 js (scripts_JMS) を参考に実装
// TODO : js では sendSimple で表示しており、ボタンの種類は js のコメント (prev next) を参考にした
public class Quest_Common_PachinkoDebut extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        switch (status) {
            case 0 -> {
                // 原文ママ
                say("ごきげんよう。今日はどんより曇り空ね。雨でも降るのかしら。", false, true);
                return true;
            }
            case 1 -> {
                // 原文ママ
                say("こんな日には冒険するよりも、快適な室内で気分転換もいいんじゃないかしら。", true, true);
                return true;
            }
            case 2 -> {
                // 原文ママ
                say("気分転換といえばパチンコね。早速だけど、パチンコはやったことあるかしら？気軽に楽しめるわよ。丁度今回新台も入荷したところだから、一度やってみるといいわ。", true, true);
                return true;
            }
            case 3 -> {
                // 原文ママ
                say("パチンコ屋まで来てくださればパチンコ玉を少しあげるわ。近くの町からすぐにでも来れるはずだわ。", true, true);
                return true;
            }
            case 4 -> {
                forceStartQuest();
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
                // 原文ママ
                say("あら、来てくれたのね。それじゃあパチンコ玉をちょっとあげるから、これでパチンコデビューしてごらんなさい。", false, true);
                return true;
            }
            case 1 -> {
                // 原文ママ
                say("今日の結果がイマイチでも、明日またちょっと玉を分けてあげるあｋら心配なく楽しんで来てね。", true, true);
                return true;
            }
            case 2 -> {
                // 原文ママ
                say("もし玉が足りなければ、そっちにある#bパチンコ玉交換機を利用するといいわよ。#k", true, true);
                return true;
            }
            case 3 -> {
                // TODO : パチンコ玉を 100 玉追加する処理
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
