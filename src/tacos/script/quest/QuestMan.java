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

import odin.server.quest.MapleQuest;
import tacos.client.TacosCharacter;

/**
 *
 * @author Riremito
 */
public class QuestMan {

    // quest_start : true = startscript (開始時), false = endscript (完了時)
    // check_complete : endscript の実行前に完了条件を確認する
    public static boolean start(TacosCharacter chr, int npc_id, int quest_id, boolean quest_start, boolean check_complete) {
        QuestScriptMan quest_script = create(quest_id);
        if (quest_script == null) {
            return false;
        }
        if (!quest_start && check_complete && !MapleQuest.getInstance(quest_id).canComplete(chr, null)) {
            // Java で処理するクエストなので js は実行しない
            return true;
        }
        quest_script.setNpcId(npc_id);
        quest_script.setQuest(quest_id, quest_start);
        chr.DebugMsg("QuestMan : " + quest_id + (quest_start ? " (start)" : " (end)"));
        return quest_script.start(chr);
    }

    private static QuestScriptMan create(int quest_id) {
        switch (quest_id) {
            case 4615, 4616, 4617, 4618, 9682, 9683, 9684, 9685 -> {
                // 4615 - 4618, カサンドラのスペシャル占い
                // 9682 - 9685, 毎日毎日イベント (3月 5日, 12日, 19日, 26日)
                return new Quest_Event_ScrollChange();
            }
            case 8185, 8189 -> {
                // 8185, ペットの進化　2
                // 8189, ペットの再進化
                return new Quest_Common_PetEvolution();
            }
            case 9680, 9810, 9811, 9812, 9813 -> {
                // 9680, 驚愕の経験値ガッツリイベント
                // 9810 - 9813, 毎日毎日イベント (4月 27日, 4月 29日, 5月 4日, 5月 6日)
                return new Quest_Event_SelectedMob();
            }
            case 9800 -> {
                // 9800, 4周年記念のレベルアップイベント
                return new Quest_Event_LevelUpReward();
            }
            case 9809 -> {
                // 9809, カサンドラの占い
                return new Quest_Event_CassandraFortune();
            }
            default -> {
            }
        }
        return null;
    }
}
