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
package tacos.packet.response.builder;

import lombok.Builder;
import tacos.packet.ops.OpsDropPickUpMessage;
import tacos.packet.ops.OpsQuestRecordMessage;

/**
 *
 * @author Riremito
 */
@Builder
public class PB_Message {

    public OpsDropPickUpMessage dt;
    @Builder.Default
    public String str = "";
    public int ItemID;
    public int Inc_ItemCount;
    public short QuestID;
    public OpsQuestRecordMessage qt;
    public int Inc_EXP_TextColor;
    public int Inc_EXP;
    public int InChat;
    public int Inc_EXP_MobEventBonusPercentage;
    public int Inc_EXP_PlayTimeHour;
    public int Inc_EXP_EventBonus;
    public int Inc_EXP_WeddingBonus;
    public int Inc_EXP_PartyBonus;
    public int Inc_EXP_EquipmentBonus;
    public int Inc_EXP_PremiumBonus;
    public int Inc_EXP_RainbowWeekBonus;
    public int Inc_EXP_ClassBonus;
    public short JobID;
    public byte Inc_SP;
    public int Inc_Fame;
    public int Inc_Meso;
    public int Inc_GP;
    public int Inc_Tama;
}
