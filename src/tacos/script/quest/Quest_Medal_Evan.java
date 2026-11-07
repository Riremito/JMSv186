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
// Quest.wz startscript : q29934s - q29940s (JMS186), いいひと, ぺリオン警備兵, カニングシティー名誉市民, 秘密組織秘密メンバー, ドラゴンマスター, 英雄の後継者 (エヴァン), 旧 js (scripts_JMS) を参考に実装
// js の 29934 - 29938 は演算子の優先順位の誤り ((job / 100) | 0 == 22) でエヴァン以外も対象になるが、29939, 29940 と同じエヴァンの条件にする
public class Quest_Medal_Evan extends QuestScriptMan {

    private final int quest_dragon_master = 29938; // ドラゴンマスター
    private final int quest_dragon_master_2 = 29939; // ドラゴンマスター (2)
    private final int item_medal_dragon_master = 1142156; // ドラゴンマスター
    private final int item_medal_dragon_master_2 = 1142157; // ドラゴンマスター (2)
    private final int required_level = 10;
    private final int job_evan = 22; // エヴァン (2200 - 2218)

    @Override
    public boolean questStart(int status, int answer) {
        if (getLevel() < this.required_level || getJob() / 100 != this.job_evan) {
            return false;
        }
        int item_medal = 0;
        if (getQuestId() == this.quest_dragon_master) {
            item_medal = this.item_medal_dragon_master;
        } else if (getQuestId() == this.quest_dragon_master_2) {
            item_medal = this.item_medal_dragon_master_2;
        }
        if (item_medal != 0 && canHold(item_medal, 1) && !haveItem(item_medal, 1)) {
            gainItem(item_medal, 1);
        }
        forceStartQuest();
        forceCompleteQuest();
        return false;
    }
}
