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
// Quest.wz endscript : q2124e, q2126e (JMS186), 砂絵団の補給品, 砂絵団の補給品！, lua の処理を利用
// q2124e (レベル 18 以上 29 以下) は lua では交換に失敗しても完了する
public class Quest_Ariant_SandBanditsSupplies extends QuestScriptMan {

    private final int quest_low_level = 2124; // 砂絵団の補給品
    private final int quest_sand_bandits = 3937; // lua の qr.getState( 3937 )
    private final int item_box_low_level = 4031619; // ボンちゃんの小さい箱 (2124)
    private final int item_box = 4031624; // ボンちゃんの小さい箱 (2126)
    private final int item_return_scroll = 2030000; // 帰還の書
    private final int item_desert_dew = 2022155; // 砂漠の露
    private boolean completed_3937 = false;

    @Override
    public boolean questEnd(int status, int answer) {
        boolean low_level = getQuestId() == this.quest_low_level;
        switch (status) {
            case 0 -> {
                this.completed_3937 = getQuestStatus(this.quest_sand_bandits) == 2;
                if (this.completed_3937) {
                    // 翻訳
                    say("ああ！誰かと思えば久しぶりだね。今回は補給品の運搬を任されたのかい？かなり重要な任務だったのに。お疲れさま。\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v" + this.item_return_scroll + "# #t" + this.item_return_scroll + "# 5個\r\n#v" + this.item_desert_dew + "# #t" + this.item_desert_dew + "# 5個\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0#\r\n2000 経験値", false, true);
                    return true;
                }
                if (low_level) {
                    // lua では 帰還の書 5個, 砂漠の露 5個 と表示されるが、実際は 帰還の書 10個
                    // 翻訳
                    say("お疲れさまでした。人目が多いので、もう行ってください。\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v" + this.item_return_scroll + "# #t" + this.item_return_scroll + "# 5個\r\n#v" + this.item_desert_dew + "# #t" + this.item_desert_dew + "# 5個\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0#\r\n2000 経験値", false, true);
                    return true;
                }
                // 翻訳
                say("お疲れさまでした。人目が多いので、もう行ってください。\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v" + this.item_return_scroll + "# #t" + this.item_return_scroll + "# 10個\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0#\r\n2000 経験値", false, true);
                return true;
            }
            case 1 -> {
                int item_box_id = low_level ? this.item_box_low_level : this.item_box;
                boolean result;
                if (this.completed_3937) {
                    result = exchange(0, item_box_id, -1, this.item_return_scroll, 5, this.item_desert_dew, 5);
                } else {
                    result = exchange(0, item_box_id, -1, this.item_return_scroll, 10);
                }
                if (!result) {
                    // 翻訳
                    say("消費インベントリに空きがあるか確認してください。");
                    // lua のまま (q2124e は交換に失敗しても完了する)
                    if (!low_level) {
                        return false;
                    }
                }
                gainExp(2000);
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
