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
// Quest.wz endscript : q2127e (JMS186), 砂漠へ…, lua の処理を利用 (アリアントへ 5 回送る)
public class Quest_Ariant_ToTheDesert extends QuestScriptMan {

    private final int quest_count = 7060; // lua の qr.get( 7060 ), 送った回数
    private final int map_ariant = 260000200; // アリアント村 : アリアント集落地
    private final String[] texts = {
        // 翻訳
        "出発の準備はできたかね？初めて行くから緊張しているようだな。心配いらない。君ならうまくやれるさ。私の力で、これから5回はそこへ送ってあげられる。今移動するかね？",
        // 翻訳
        "また会ったね。前回行ったときはどうだった？また行くということは、あそこでやっていることがあるのだろう？君にはあと4回のチャンスが残っている。今移動するかね？",
        // 翻訳
        "よく会うね。とても忙しいようだな？君にはあと3回のチャンスが残っている。今移動するかね？",
        // 翻訳
        "砂漠は魅力的な場所なのだろうな？そういえば、顔も日に焼けたようだ。君にはあと2回のチャンスが残っている。今移動するかね？",
        // 翻訳
        "今日もあそこへ行くのかね？名前は何といったかな？アリアントだったか？君を見ていると、私も一度くらい行ってみたくなるよ。君にはあと1回のチャンスが残っている。今移動するかね？"
    };
    private int count = 0;

    @Override
    public boolean questEnd(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == -1) {
            // 翻訳
            say("準備ができたらまた来なさい。");
            return false;
        }
        switch (status) {
            case 0 -> {
                String info = getQuestData(this.quest_count);
                if (info.isEmpty()) {
                    setQuestData(this.quest_count, "0");
                    info = getQuestData(this.quest_count);
                }
                this.count = -1;
                for (int i = 0; i < this.texts.length; i++) {
                    if (info.equals(String.valueOf(i))) {
                        this.count = i;
                    }
                }
                if (this.count < 0) {
                    return false;
                }
                askAccept(this.texts[this.count]);
                return true;
            }
            case 1 -> {
                // 翻訳
                say("気をつけて行ってきなさい。");
                return true;
            }
            case 2 -> {
                setQuestData(this.quest_count, String.valueOf(this.count + 1));
                if (this.count == this.texts.length - 1) {
                    forceCompleteQuest();
                    questEndEffect();
                }
                warp(this.map_ariant);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
