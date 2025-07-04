/*
 * Copyright (C) 2025 Riremito
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
package packet.ops;

import config.Region;
import config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsQuest {
    QuestReq_LostItem,
    QuestReq_AcceptQuest,
    QuestReq_CompleteQuest,
    QuestReq_ResignQuest,
    QuestReq_OpeningScript,
    QuestReq_CompleteScript,
    QuestRes_Start_QuestTimer,
    QuestRes_End_QuestTimer,
    QuestRes_Start_TimeKeepQuestTimer,
    QuestRes_End_TimeKeepQuestTimer,
    QuestRes_Act_Success,
    QuestRes_Act_Failed_Unknown,
    QuestRes_Act_Failed_Inventory,
    QuestRes_Act_Failed_Meso,
    QuestRes_Act_Failed_Pet,
    QuestRes_Act_Failed_Equipped,
    QuestRes_Act_Failed_OnlyItem,
    QuestRes_Act_Failed_TimeOver,
    QuestRes_Act_Reset_QuestTimer,
    UNKNOWN(-1);

    private int value;

    OpsQuest(int v) {
        value = v;
    }

    OpsQuest() {
        value = -1;
    }

    public int get() {
        return value;
    }

    public void set(int v) {
        this.value = v;
    }

    public static OpsQuest find(int v) {
        for (final OpsQuest o : OpsQuest.values()) {
            if (o.get() == v) {
                return o;
            }
        }

        return UNKNOWN;
    }

    public static void init() {
        // JMS186
        QuestRes_Act_Success.set(8);
        if (Version.GreaterOrEqual(Region.JMS, 194)) {
            QuestRes_Act_Success.set(10);
        }
    }
}
