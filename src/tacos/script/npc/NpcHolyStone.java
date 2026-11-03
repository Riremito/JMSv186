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
package tacos.script.npc;

import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class NpcHolyStone extends NpcScriptMan {

    private final int item_necklace_of_wisdom = 4031058; // 知恵のネックレス
    private final int question_count = 5;
    // TODO : 残りの問題
    private final String[] questions = {
        "次の中でメイプルストーリーを始めると一番最初に会えるNPCは誰なのか？",
        "レベル1からレベル2になるために、必要な必要経験値はいくつなのか？",
        "次の中でオシリア大陸　エルナスで会えないNPCは誰なのか？"
    };
    private final String[][] choices = {
        {"ローザー", "リナ", "ニナ", "セン", "ヒナ"},
        {"10", "15", "20", "25", "30"},
        {"ボゲン", "フォックス曹長", "メイドエルマ", "ゼイド", "スカドル"}
    };
    private final int[] answers = {
        4,
        1,
        2
    };
    private int answer_index = -1;

    @Override
    public boolean action(int status, int answer) {
        // TODO : 3次転職のクエストの確認
        if (2 <= status && status <= this.question_count + 1) {
            if (answer != this.answer_index) {
                say("間違い");
                return false;
            }
        }
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("よかろう。これから汝の知恵をテストしよう。全ての質問に正しい答えを選ぶと合格だ。ただし途中で1つでも間違うと最初からやり直さねばならん。では始めるぞ。");
                say(nt, false, true);
                return true;
            }
            case 1, 2, 3, 4, 5 -> {
                int question_num = (int) (Math.random() * this.questions.length);
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(status + "つ目の問題だ。" + this.questions[question_num]);
                for (int i = 0; i < this.choices[question_num].length; i++) {
                    nt.addMenu(i, this.choices[question_num][i]);
                }
                this.answer_index = this.answers[question_num];
                askMenu(nt);
                return true;
            }
            case 6 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("汝の答えに嘘がないのを認めよう。");
                nt.add("これで汝の知恵も証明された。");
                nt.add("このネックレスを持って帰るがいい。");
                MapleInventoryManipulator.addById(getPlayer(), this.item_necklace_of_wisdom, 1); // TODO : item effect.
                say(nt);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
