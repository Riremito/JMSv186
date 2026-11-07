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
// Quest.wz endscript : q3301e, q3303e (JMS186), ジェニミスト協会長のテスト, アルカドノ協会長のテスト, lua の処理を利用
public class Quest_Magatia_AssociationPresidentsTest extends QuestScriptMan {

    private final int quest_zenumist = 3301; // ジェニミスト協会長のテスト
    private final int[] items_ore = {
        4020000, // ガーネットの原石
        4020001, // 紫水晶の原石
        4020002, // アクアマリンの原石
        4020003, // エメラルドの原石
        4020004, // オパールの原石
        4020005, // サファイアの原石
        4020006, // トパーズの原石
        4020007, // ダイヤモンドの原石
        4020008, // 黒水晶の原石
        4010004, // 銀の原料
        4010006 // 金の原料
    };
    private final int ore_count = 2;

    @Override
    public boolean questEnd(int status, int answer) {
        // lua の文章の違いは学派名のみ
        String group = (getQuestId() == this.quest_zenumist) ? "ジェナミスト" : "アルカドノ";
        switch (status) {
            case 0 -> {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < this.items_ore.length; i++) {
                    if (this.ore_count <= getItemCount(this.items_ore[i])) {
                        sb.append("\r\n#L").append(i).append("##b #t").append(this.items_ore[i]).append("##k#l");
                    }
                }
                if (sb.length() == 0) {
                    // 翻訳
                    say("何だ、代わりに渡せる原石を持っていないじゃないか？代価がなければ取引もなしだ。");
                    return false;
                }
                // 翻訳
                askMenu("ほう…その顔を見ると、取引する準備ができたようだな。そこまでして" + group + "に入りたいなんて…理解できないが、まあいい。代価に何をくれる？\r\n" + sb.toString());
                return true;
            }
            case 1 -> {
                if (answer < 0 || this.items_ore.length <= answer) {
                    return false;
                }
                int item_id = this.items_ore[answer];
                if (!exchange(0, item_id, -this.ore_count)) {
                    // 翻訳
                    say("何だ！#t" + item_id + "#を2個用意できていないじゃないか？おい。取引したいならちゃんと準備しないとな！");
                    return false;
                }
                forceCompleteQuest();
                questEndEffect();
                // 翻訳
                say("じゃあ、少し待っていてくれ。君が" + group + "協会長の試験に合格できるようにする品を、手に入れておいてやるから。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
