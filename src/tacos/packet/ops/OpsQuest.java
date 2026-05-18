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
package tacos.packet.ops;

import tacos.config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsQuest implements IPacketOps {
    QuestReq_LostItem(0),
    QuestReq_AcceptQuest(1),
    QuestReq_CompleteQuest(2),
    QuestReq_ResignQuest(3),
    QuestReq_OpeningScript(4),
    QuestReq_CompleteScript(5),
    QuestRes_Start_QuestTimer(6),
    QuestRes_End_QuestTimer(7),
    QuestRes_Start_TimeKeepQuestTimer(8),
    QuestRes_End_TimeKeepQuestTimer(9),
    QuestRes_Act_Success(10),
    QuestRes_Act_Failed_Unknown(11),
    QuestRes_Act_Failed_Inventory(12),
    QuestRes_Act_Failed_Meso(13),
    QuestRes_Act_Failed_Pet(14),
    QuestRes_Act_Failed_Equipped(15),
    QuestRes_Act_Failed_OnlyItem(16),
    QuestRes_Act_Failed_TimeOver(17),
    QuestRes_Act_Reset_QuestTimer(18),
    UNKNOWN;

    private int value;

    OpsQuest(int val) {
        this.value = val;
    }

    OpsQuest() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int val) {
        this.value = val;
    }

    public static OpsQuest find(int val) {
        for (OpsQuest ops : values()) {
            if (ops.get() == val) {
                if (val != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }

    public static void clear() {
        for (OpsQuest ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        if (Version.PostBB()) {
            return;
        }
        // JMS186
        clear();
        QuestRes_Act_Success.set(8);
        /*
        QuestReq_LostItem.set(0);
        QuestReq_AcceptQuest.set(1);
        QuestReq_CompleteQuest.set(2);
        QuestReq_ResignQuest.set(3);
        QuestReq_OpeningScript.set(4);
        QuestReq_CompleteScript.set(5);
        QuestRes_Start_QuestTimer.set(6);
        QuestRes_End_QuestTimer.set(7);
        QuestRes_Start_TimeKeepQuestTimer.set(8);
        QuestRes_End_TimeKeepQuestTimer.set(9);
        QuestRes_Act_Success.set(10);
        QuestRes_Act_Failed_Unknown.set(11);
        QuestRes_Act_Failed_Inventory.set(12);
        QuestRes_Act_Failed_Meso.set(13);
        QuestRes_Act_Failed_Pet.set(14);
        QuestRes_Act_Failed_Equipped.set(15);
        QuestRes_Act_Failed_OnlyItem.set(16);
        QuestRes_Act_Failed_TimeOver.set(17);
        QuestRes_Act_Reset_QuestTimer.set(18);
         */
    }
}
