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

/**
 *
 * @author Riremito
 */
public enum OpsQuestRecordMessage {
    QUEST_START(0),
    QUEST_UPDATE(1),
    QUEST_COMPLETE(2),
    UNKNOWN(-1);

    private int value;

    OpsQuestRecordMessage(int flag) {
        value = flag;
    }

    OpsQuestRecordMessage() {
        value = -1;
    }

    public boolean set(int flag) {
        value = flag;
        return true;
    }

    public int get() {
        return value;
    }

    public static OpsQuestRecordMessage get(int v) {
        for (final OpsQuestRecordMessage f : OpsQuestRecordMessage.values()) {
            if (f.get() == v) {
                return f;
            }
        }
        return OpsQuestRecordMessage.UNKNOWN;
    }
}
