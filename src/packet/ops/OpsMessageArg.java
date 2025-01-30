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
public class OpsMessageArg {

    public OpsMessage mt = OpsMessage.UNKNOWN;
    public OpsDropPickUpMessage dt;

    public String str = "";
    public int ItemID = 0;
    public int Inc_ItemCount = 0;
    public short QuestID = 0;
    public OpsQuestRecordMessage qt;
    public int Inc_EXP_TextColor = 0; // white yellow
    public int Inc_EXP = 0;
    public int InChat = 0; // chat or not
    public int Inc_EXP_MobEventBonusPercentage = 0;
    public int Inc_EXP_PlayTimeHour = 0;
    public int Inc_EXP_EventBonus = 0;
    public int Inc_EXP_WeddingBonus = 0;
    public int Inc_EXP_PartyBonus = 0;
    public int Inc_EXP_EquipmentBonus = 0;
    public int Inc_EXP_PremiumBonus = 0;
    public int Inc_EXP_RainbowWeekBonus = 0;
    public int Inc_EXP_ClassBonus = 0;
    public short JobID = 0;
    public byte Inc_SP = 0;
    public int Inc_Fame = 0;
    public int Inc_Meso = 0;
    public int Inc_GP = 0;
    public int Inc_Tama = 0;
}
