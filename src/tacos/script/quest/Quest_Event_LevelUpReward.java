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
// Quest.wz endscript : q9800e (JMS186), 4周年記念のレベルアップイベント
public class Quest_Event_LevelUpReward extends QuestScriptMan {

    private final int item_maple_leaf = 4001126; // モミジ

    @Override
    public boolean questEnd(int status, int answer) {
        int level = getLevel();
        int reward = 0;
        if (2 <= level && level <= 31) {
            reward = level + 8;
        } else if (32 <= level && level <= 41) {
            reward = level + 9 + (level - 32);
        } else if (42 <= level && level <= 61) {
            reward = level + 20 + (2 * (level - 42));
        } else if (62 <= level && level <= 76) {
            reward = level + 62 + (4 * (level - 62));
        } else if (77 <= level && level <= 91) {
            reward = level + 123 + (5 * (level - 77));
        } else if (92 <= level && level <= 106) {
            reward = level + 199 + (6 * (level - 92));
        } else if (107 <= level && level <= 121) {
            reward = level + 290 + (7 * (level - 107));
        } else if (122 <= level && level <= 136) {
            reward = level + 396 + (8 * (level - 122));
        } else if (137 <= level && level <= 151) {
            reward = level + 517 + (9 * (level - 137));
        } else if (152 <= level && level <= 166) {
            reward = level + 653 + (10 * (level - 152));
        } else if (167 <= level && level <= 181) {
            reward = level + 804 + (11 * (level - 167));
        } else if (182 <= level && level <= 196) {
            reward = level + 970 + (12 * (level - 182));
        } else if (197 <= level && level <= 200) {
            reward = level + 1151 + (13 * (level - 197));
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                say(level + "レベル到達おめでとうございます。レベルアップのお祝いとして#t" + this.item_maple_leaf + "#をお配りしています。今日も楽しい#r#eメイプルストーリー#n#kをお楽しみください～\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v" + this.item_maple_leaf + "# #t" + this.item_maple_leaf + "#" + reward + "個", false, true);
                return true;
            }
            case 1 -> {
                if (!exchange(0, this.item_maple_leaf, reward)) {
                    // 翻訳
                    say("ETCインベントリに空きがあるか確認してください。");
                    return false;
                }
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
