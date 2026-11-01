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

/**
 *
 * @author Riremito
 */
public enum OpsQuestRecordMessage {
    QUEST_START(0),
    QUEST_UPDATE(1),
    QUEST_COMPLETE(2),
    UNKNOWN;

    private int value;

    private OpsQuestRecordMessage(int value) {
        this.value = value;
    }

    private OpsQuestRecordMessage() {
        this.value = -1;
    }

    public boolean set(int value) {
        this.value = value;
        return true;
    }

    public int get() {
        return this.value;
    }

    public static OpsQuestRecordMessage find(int value) {
        for (OpsQuestRecordMessage ops : values()) {
            if (ops.get() == value) {
                if (value != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }
}
