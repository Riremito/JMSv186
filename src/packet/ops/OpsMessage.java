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
import config.ServerConfig;
import config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsMessage {
    // v186
    MS_DropPickUpMessage(0),
    MS_QuestRecordMessage(1),
    MS_CashItemExpireMessage(2),
    MS_IncEXPMessage(3),
    MS_IncSPMessage(4),
    MS_IncPOPMessage(5),
    MS_IncMoneyMessage(6),
    MS_IncGPMessage(7),
    MS_IncCommitmentMessage(-1),
    MS_GiveBuffMessage(8),
    MS_GeneralItemExpireMessage(9),
    MS_SystemMessage(10),
    MS_QuestRecordExMessage(11),
    MS_ItemProtectExpireMessage(12),
    MS_ItemExpireReplaceMessage(13),
    MS_SkillExpireMessage(14),
    MS_JMS_Pachinko(15),
    UNKNOWN(-1);

    public static void init() {
        if (Version.LessOrEqual(Region.KMS, 84)) {
            OpsMessage.MS_DropPickUpMessage.set(0);
            OpsMessage.MS_QuestRecordMessage.set(1);
            OpsMessage.MS_CashItemExpireMessage.set(2);
            OpsMessage.MS_IncEXPMessage.set(3);
            OpsMessage.MS_IncSPMessage.set(4);
            OpsMessage.MS_IncPOPMessage.set(5);
            OpsMessage.MS_IncMoneyMessage.set(6);
            OpsMessage.MS_GiveBuffMessage.set(7);
            OpsMessage.MS_GeneralItemExpireMessage.set(8);
            OpsMessage.MS_SystemMessage.set(9);
            OpsMessage.MS_QuestRecordExMessage.set(10);
            OpsMessage.MS_ItemProtectExpireMessage.set(11);
            OpsMessage.MS_ItemExpireReplaceMessage.set(12);
            OpsMessage.MS_SkillExpireMessage.set(13);
            OpsMessage.MS_IncGPMessage.set(-1);
            OpsMessage.MS_JMS_Pachinko.set(-1);
            return;
        }
        if (ServerConfig.JMS194orLater() || Version.GreaterOrEqual(Region.GMS, 111)) {
            OpsMessage.MS_DropPickUpMessage.set(0);
            OpsMessage.MS_QuestRecordMessage.set(1);
            OpsMessage.MS_CashItemExpireMessage.set(2);
            OpsMessage.MS_IncEXPMessage.set(3);
            OpsMessage.MS_IncSPMessage.set(4);
            OpsMessage.MS_IncPOPMessage.set(5);
            OpsMessage.MS_IncMoneyMessage.set(6);
            OpsMessage.MS_IncGPMessage.set(7);
            OpsMessage.MS_IncCommitmentMessage.set(8); // post-bb
            OpsMessage.MS_GiveBuffMessage.set(9);
            OpsMessage.MS_GeneralItemExpireMessage.set(10);
            OpsMessage.MS_SystemMessage.set(11);
            OpsMessage.MS_QuestRecordExMessage.set(12);
            OpsMessage.MS_ItemProtectExpireMessage.set(13);
            OpsMessage.MS_ItemExpireReplaceMessage.set(14);
            OpsMessage.MS_SkillExpireMessage.set(15);
            OpsMessage.MS_JMS_Pachinko.set(16);
            return;
        }
    }

    private int value;

    OpsMessage(int flag) {
        value = flag;
    }

    OpsMessage() {
        value = -1;
    }

    public boolean set(int flag) {
        value = flag;
        return true;
    }

    public int get() {
        return value;
    }
}
