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
 * @author Bubbling
 */
public enum OpsUserEffect {
    // GMS v95
    UserEffect_LevelUp(0x0),
    UserEffect_SkillUse(0x1),
    UserEffect_SkillAffected(0x2),
    UserEffect_SkillAffected_Select(0x3),
    UserEffect_SkillSpecialAffected(0x4),
    UserEffect_Quest(0x5),
    UserEffect_Pet(0x6),
    UserEffect_SkillSpecial(0x7),
    UserEffect_Resist(-1),
    UserEffect_ProtectOnDieItemUse(0x8),
    UserEffect_PlayPortalSE(0x9),
    UserEffect_JobChanged(0xA),
    UserEffect_QuestComplete(0xB),
    UserEffect_IncDecHPEffect(0xC),
    UserEffect_BuffItemEffect(0xD),
    UserEffect_SquibEffect(0xE),
    UserEffect_MonsterBookCardGet(0xF),
    UserEffect_LotteryUse(0x10),
    UserEffect_ItemLevelUp(0x11),
    UserEffect_ItemMaker(0x12),
    UserEffect_ExpItemConsumed(0x13),
    UserEffect_ReservedEffect(0x14),
    UserEffect_Buff(0x15),
    UserEffect_ConsumeEffect(0x16),
    UserEffect_UpgradeTombItemUse(0x17),
    UserEffect_BattlefieldItemUse(0x18),
    UserEffect_AvatarOriented(0x19),
    UserEffect_IncubatorUse(0x1A),
    UserEffect_PlaySoundWithMuteBGM(0x1B),
    UserEffect_SoulStoneUse(0x1C),
    UserEffect_IncDecHPEffect_EX(0x1D),
    UserEffect_IncDecHPRegenEffect(-1),
    UserEffect_DeliveryQuestItemUse(0x1E),
    UserEffect_RepeatEffectRemove(0x1F),
    UserEffect_EvolRing(0x20),
    UserEffect_PvPRage(-1),
    UserEffect_PvPChampion(-1),
    UserEffect_PvPGradeUp(-1),
    UserEffect_PvPRevive(-1),
    // For THMS v87
    UserEffect_UNK_1(-1),
    UNKNOWN(-1);

    private int value;

    OpsUserEffect(int flag) {
        value = flag;
    }

    OpsUserEffect() {
        value = -1;
    }

    public int get() {
        return value;
    }

    public void set(int val) {
        value = val;
    }

    public static OpsUserEffect find(int val) {
        for (final OpsUserEffect o : OpsUserEffect.values()) {
            if (o.get() == val) {
                return o;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            UserEffect_LevelUp.set(0);
            UserEffect_SkillUse.set(1);
            UserEffect_SkillAffected.set(2);
            UserEffect_SkillAffected_Select.set(3);
            UserEffect_SkillSpecialAffected.set(4);
            UserEffect_Quest.set(5);
            UserEffect_Pet.set(6);
            UserEffect_SkillSpecial.set(7);
            UserEffect_Resist.set(8);
            UserEffect_ProtectOnDieItemUse.set(9); // 翡翠のお守り
            // 10 : 経験値お守り (兵法書系統) JMS
            UserEffect_PlayPortalSE.set(11);
            UserEffect_JobChanged.set(12);
            UserEffect_QuestComplete.set(13);
            UserEffect_IncDecHPEffect.set(14);
            UserEffect_BuffItemEffect.set(15);
            UserEffect_SquibEffect.set(16);
            UserEffect_MonsterBookCardGet.set(17);
            UserEffect_LotteryUse.set(18);
            UserEffect_ItemLevelUp.set(19);
            UserEffect_ItemMaker.set(20);
            UserEffect_ExpItemConsumed.set(21);
            UserEffect_ReservedEffect.set(22);
            // 23 : 運命の車輪 JMS
            UserEffect_Buff.set(-1);
            UserEffect_BattlefieldItemUse.set(24); // ?_?
            UserEffect_ConsumeEffect.set(25);
            UserEffect_UpgradeTombItemUse.set(26); // 4-4-1
            UserEffect_AvatarOriented.set(27);
            UserEffect_IncubatorUse.set(28);
            UserEffect_PlaySoundWithMuteBGM.set(29);
            UserEffect_SoulStoneUse.set(30); // 霊魂石
            UserEffect_IncDecHPEffect_EX.set(31);
            // 32 :
            UserEffect_IncDecHPRegenEffect.set(33);
            UserEffect_DeliveryQuestItemUse.set(-1);
            UserEffect_RepeatEffectRemove.set(-1);
            UserEffect_PvPRage.set(35);
            UserEffect_PvPChampion.set(36);
            UserEffect_PvPGradeUp.set(37); // GRADE UP
            UserEffect_PvPRevive.set(38); // 点滅
            // 43 : Aswan/DefenceFail
            // 44 : cards?
            // 45 :
            // 46 : ファミリア
            // 47 : アイテム合成
            // 48 : かえでのエフェクト
            // 49 : 48と同じ
            // 50 : 紫の謎エフェクト
            return;
        }

        if (Version.LessOrEqual(Region.JMS, 131)) {
            UserEffect_LevelUp.set(0);
            UserEffect_SkillUse.set(1);
            UserEffect_SkillAffected.set(2);
            UserEffect_Quest.set(3);
            UserEffect_Pet.set(4);
            UserEffect_SkillSpecial.set(5);
            UserEffect_ProtectOnDieItemUse.set(6);
            UserEffect_PlayPortalSE.set(7);
            UserEffect_JobChanged.set(8);
            UserEffect_QuestComplete.set(9);
            UserEffect_IncDecHPEffect.set(10);
            UserEffect_BuffItemEffect.set(11);
            return;
        }
        if (Version.GreaterOrEqual(Region.JMS, 186)) {
            UserEffect_LevelUp.set(0);
            UserEffect_SkillUse.set(1);
            UserEffect_SkillAffected.set(2);
            UserEffect_Quest.set(3);
            UserEffect_Pet.set(4);
            UserEffect_SkillSpecial.set(5);
            UserEffect_PlayPortalSE.set(8);
            UserEffect_JobChanged.set(9);
            UserEffect_QuestComplete.set(10);
            UserEffect_BuffItemEffect.set(12);
            UserEffect_MonsterBookCardGet.set(14);
            return;
        }
        if (Version.Equal(Region.THMS, 87)) {
            UserEffect_LevelUp.set(0x0);
            UserEffect_SkillUse.set(0x1);
            UserEffect_SkillAffected.set(0x2);
            UserEffect_SkillAffected_Select.set(0x2);
            UserEffect_SkillSpecialAffected.set(0x2);
            UserEffect_Quest.set(0x3);
            UserEffect_Pet.set(0x4);
            UserEffect_SkillSpecial.set(0x5);
            UserEffect_ProtectOnDieItemUse.set(0x6);
            UserEffect_PlayPortalSE.set(0x7);
            UserEffect_JobChanged.set(0x8);
            UserEffect_QuestComplete.set(0x9);
            UserEffect_IncDecHPEffect.set(0xA);
            UserEffect_BuffItemEffect.set(0xB);
            UserEffect_UNK_1.set(0xC); // UserEffect_UNK_1 is UserEffect (Effect/ItemEff.img/%d/0 and Effect/ItemEff.img/%d/1)
            UserEffect_SquibEffect.set(0xD);
            UserEffect_MonsterBookCardGet.set(0xE);
            UserEffect_LotteryUse.set(0xF);
            UserEffect_ItemLevelUp.set(0x10);
            UserEffect_ItemMaker.set(0x11);
            UserEffect_ExpItemConsumed.set(0x12);
            UserEffect_ReservedEffect.set(0x13);
            UserEffect_Buff.set(-1); // Not Exist in THMS v87
            UserEffect_ConsumeEffect.set(-1); // Not Exist in THMS v87
            UserEffect_UpgradeTombItemUse.set(0x14);
            UserEffect_BattlefieldItemUse.set(0x15);
            UserEffect_AvatarOriented.set(0x16);
            UserEffect_IncubatorUse.set(0x17);
            UserEffect_PlaySoundWithMuteBGM.set(0x18);
            UserEffect_SoulStoneUse.set(0x19);
            UserEffect_IncDecHPEffect_EX.set(0x1A);
            UserEffect_DeliveryQuestItemUse.set(-1); // Not Exist in THMS v87
            UserEffect_RepeatEffectRemove.set(-1); // Not Exist in THMS v87
            UserEffect_EvolRing.set(-1); // Not Exist in THMS v87
            return;
        }
    }
}
