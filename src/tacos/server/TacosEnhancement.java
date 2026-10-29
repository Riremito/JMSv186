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
package tacos.server;

import odin.client.inventory.Equip;
import odin.client.inventory.Item.ItemFlag;
import odin.constants.GameConstants;
import odin.server.Randomizer;
import tacos.config.Config;
import tacos.config.ContentCustom;
import tacos.config.Region;
import tacos.constants.TacosConstants;
import tacos.shared.TacosShared;
import tacos.wz.CharacterWz.EquipInfo;
import tacos.wz.ItemWz.ScrollData;
import tacos.wz.WzXML;
import tacos.wz.ids.DWI_Random;

/**
 *
 * @author Riremito
 */
public class TacosEnhancement {

    // currently rate is set for test.
    private static final int RANK_UP_RARE_TO_EPIC = 80; // 多分4%
    private static final int RANK_UP_EPIC_TO_UNIQUE = 40; // 多分2%
    private static final int RANK_UP_UNIQUE_TO_LEGENDARY = 10; // 多分2% (初期のマスターミラクルキューブ), 通常ミラクルキューブで昇級するようになった後は1%くらいかも
    private static final int OPTION_3LINES = 50; // 多分10%
    private static final int OPTION2_HIGH_RANK = 10; // 10%
    private static final int OPTION3_HIGH_RANK = 1; // 1%

    private final Equip equip;

    public TacosEnhancement(Equip equip) {
        this.equip = equip;
    }

    private boolean checkPotentialSystem() {
        if (Config.PostBB()) {
            return true;
        }
        if (Config.GreaterOrEqual(Region.KMS, 98) || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)) {
            return true;
        }
        return false;
    }

    // 装備アイテムのステータスの範囲, 2012年以降は範囲が変動している可能性あり
    public boolean randomize() {
        if (ContentCustom.CC_EQUIP_STAT_RANDOMIZER.get()) {
            int rand = Randomizer.nextInt(5);
            switch (rand) {
                case 0 -> {
                    return randomizeTaco(0, 100, 1000);
                }
                case 1 -> {
                    return randomizeTaco(0, 20, 100);
                }
                case 2 -> {
                    return randomizeTaco(1, 10, 20);
                }
                case 3 -> {
                    return randomizeTaco(1, 0, 10);
                }
                default -> {
                    return randomize(-5, 5);
                }
            }
        }
        return randomize(-5, 5);
    }

    public boolean randomize(int rand_min, int rand_max) {
        if (1 <= equip.getSTR()) {
            equip.incSTR(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (1 <= equip.getDEX()) {
            equip.incDEX(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (1 <= equip.getINT()) {
            equip.incINT(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (1 <= equip.getLUK()) {
            equip.incLUK(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (1 <= equip.getMaxHP()) {
            equip.incMaxHP(Randomizer.nextInt(rand_max * 2 - rand_min * 2 + 1) + rand_min * 2);
        }
        if (1 <= equip.getMaxMP()) {
            equip.incMaxMP(Randomizer.nextInt(rand_max * 2 - rand_min * 2 + 1) + rand_min * 2);
        }
        if (1 <= equip.getPAD()) {
            equip.incPAD(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (1 <= equip.getMAD()) {
            equip.incMAD(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (1 <= equip.getPDD()) {
            equip.incPDD(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (1 <= equip.getMDD()) {
            equip.incMDD(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (1 <= equip.getEVA()) {
            equip.incEVA(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (1 <= equip.getCraft()) {
            equip.incCraft(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (1 <= equip.getSpeed()) {
            equip.incSpeed(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (1 <= equip.getJump()) {
            equip.incJump(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        return true;
    }

    public boolean randomizeTaco(int stat_min, int rand_min, int rand_max) {
        if (stat_min <= equip.getSTR()) {
            equip.incSTR(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (stat_min <= equip.getDEX()) {
            equip.incDEX(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (stat_min <= equip.getINT()) {
            equip.incINT(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (stat_min <= equip.getLUK()) {
            equip.incLUK(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (stat_min <= equip.getMaxHP()) {
            equip.incMaxHP(Randomizer.nextInt(rand_max * 2 - rand_min * 2 + 1) + rand_min * 2);
        }
        if (stat_min <= equip.getMaxMP()) {
            equip.incMaxMP(Randomizer.nextInt(rand_max * 2 - rand_min * 2 + 1) + rand_min * 2);
        }
        if (stat_min <= equip.getPAD()) {
            equip.incPAD(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (stat_min <= equip.getMAD()) {
            equip.incMAD(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (stat_min <= equip.getPDD()) {
            equip.incPDD(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (stat_min <= equip.getMDD()) {
            equip.incMDD(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (stat_min <= equip.getEVA()) {
            equip.incEVA(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (stat_min <= equip.getCraft()) {
            equip.incCraft(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (stat_min <= equip.getSpeed()) {
            equip.incSpeed(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        if (stat_min <= equip.getJump()) {
            equip.incJump(Randomizer.nextInt(rand_max - rand_min + 1) + rand_min);
        }
        return true;
    }

    public static enum EnhancementResult {
        SUCCESS,
        FAILURE,
        CURSED,
        UNKNOWN;
    }

    public EnhancementResult useScroll(int item_id) {
        ScrollData sd = WzXML.ITEM.getScrollData(item_id);
        if (sd == null) {
            return EnhancementResult.UNKNOWN;
        }
        EquipInfo ed = equip.getInfo();
        if (ed == null) {
            return EnhancementResult.UNKNOWN;
        }
        // 強化不可
        if (ed.getTuc() == 0) {
            return EnhancementResult.UNKNOWN;
        }
        // TODO : ビシャスのハンマーでアップグレード回数増加時の判定
        // 白の書
        boolean is_recover_scroll = sd.getRecover() != 0;
        if (is_recover_scroll) {
            return useRecoverScroll(sd, ed);
        }
        // 混沌の書
        boolean is_chaos = sd.getRandstat() != 0;
        if (is_chaos) {
            return useChaosScroll(sd);
        }
        // スパイクの書
        boolean is_prevent_slip = sd.getPreventslip() != 0;
        if (is_prevent_slip) {
            return usePreventSlipScroll(sd);
        }
        // 防寒の書
        boolean is_warm_support = sd.getWarmsupport() != 0;
        if (is_warm_support) {
            return useWarmSupportScroll(sd);
        }
        // 黄金つち (直接使用)
        if (TacosConstants.is_gold_hammer(item_id)) {
            if (useViciousHammer()) {
                return EnhancementResult.SUCCESS;
            }
            return EnhancementResult.UNKNOWN;
        }
        boolean is_upgrade_possible = equip.getRUC() != 0;
        if (!is_upgrade_possible) {
            return EnhancementResult.UNKNOWN;
        }
        equip.incRUC(-1);
        if (Randomizer.nextInt(100) < sd.getSuccess()) {
            equip.incCUC(1);
            equip.incSTR(sd.getIncSTR());
            equip.incDEX(sd.getIncDEX());
            equip.incINT(sd.getIncINT());
            equip.incLUK(sd.getIncLUK());
            equip.incMaxHP(sd.getIncMaxHP());
            equip.incMaxMP(sd.getIncMaxMP());
            equip.incPAD(sd.getIncPAD());
            equip.incMAD(sd.getIncMAD());
            equip.incPDD(sd.getIncPDD());
            equip.incMDD(sd.getIncMDD());
            equip.incACC(sd.getIncACC());
            equip.incEVA(sd.getIncEVA());
            equip.incCraft(sd.getIncCraft());
            equip.incSpeed(sd.getIncSpeed());
            equip.incJump(sd.getIncJump());
            equip.incJump(sd.getIncJump());
            equip.incAttackSpeed(sd.getIncattackSpeed());
            return EnhancementResult.SUCCESS;
        } else {
            if (Randomizer.nextInt(100) < sd.getCursed()) {
                return EnhancementResult.CURSED;
            }
        }
        return EnhancementResult.FAILURE;
    }

    // スパイクの書
    private EnhancementResult usePreventSlipScroll(ScrollData sd) {
        if ((equip.getFlag() & ItemFlag.SPIKES.getValue()) != 0) {
            return EnhancementResult.UNKNOWN;
        }
        if (Randomizer.nextInt(100) < sd.getSuccess()) {
            equip.setFlag((byte) (equip.getFlag() | ItemFlag.SPIKES.getValue()));
            return EnhancementResult.SUCCESS;
        }
        return EnhancementResult.FAILURE;
    }

    // 防寒の書
    private EnhancementResult useWarmSupportScroll(ScrollData sd) {
        if ((equip.getFlag() & ItemFlag.COLD.getValue()) != 0) {
            return EnhancementResult.UNKNOWN;
        }
        if (Randomizer.nextInt(100) < sd.getSuccess()) {
            equip.setFlag((byte) (equip.getFlag() | ItemFlag.COLD.getValue()));
            return EnhancementResult.SUCCESS;
        }
        return EnhancementResult.FAILURE;
    }

    // 白の書
    private EnhancementResult useRecoverScroll(ScrollData sd, EquipInfo ed) {
        boolean is_recovery_possible = equip.getRUC() + equip.getCUC() + sd.getRecover() <= ed.getTuc();
        if (!is_recovery_possible) {
            return EnhancementResult.UNKNOWN;
        }
        if (Randomizer.nextInt(100) < sd.getSuccess()) {
            equip.incRUC(sd.getRecover());
            return EnhancementResult.SUCCESS;
        } else {
            if (Randomizer.nextInt(100) < sd.getCursed()) {
                return EnhancementResult.CURSED;
            }
        }
        return EnhancementResult.FAILURE;
    }

    // 混沌の書
    private EnhancementResult useChaosScroll(ScrollData sd) {
        boolean is_upgrade_possible = equip.getRUC() != 0;
        if (!is_upgrade_possible) {
            return EnhancementResult.UNKNOWN;
        }
        boolean is_incredible = sd.getIncRandVol() != 0; // 驚きの混沌の書
        boolean is_no_negative = sd.getNoNegative() != 0; // 肯定の混沌の書
        int rand_range = is_incredible ? 6 : 5; // 驚きの混沌の書, 5 -> 6
        int rand_base = rand_range * 2 + 1;
        if (is_no_negative) {
            rand_range = 0;
            rand_base /= 2;
        }
        equip.incRUC(-1);
        if (Randomizer.nextInt(100) < sd.getSuccess()) {
            equip.incCUC(1);
            if (1 <= equip.getSTR()) {
                equip.incSTR(Randomizer.nextInt(rand_base) - rand_range);
            }
            if (1 <= equip.getDEX()) {
                equip.incDEX(Randomizer.nextInt(rand_base) - rand_range);
            }
            if (1 <= equip.getINT()) {
                equip.incINT(Randomizer.nextInt(rand_base) - rand_range);
            }
            if (1 <= equip.getLUK()) {
                equip.incLUK(Randomizer.nextInt(rand_base) - rand_range);
            }
            if (1 <= equip.getMaxHP()) {
                equip.incMaxHP((Randomizer.nextInt(rand_base) - rand_range) * 10);
            }
            if (1 <= equip.getMaxMP()) {
                equip.incMaxMP((Randomizer.nextInt(rand_base) - rand_range) * 10);
            }
            if (1 <= equip.getPAD()) {
                equip.incPAD(Randomizer.nextInt(rand_base) - rand_range);
            }
            if (1 <= equip.getMAD()) {
                equip.incMAD(Randomizer.nextInt(rand_base) - rand_range);
            }
            if (1 <= equip.getPDD()) {
                equip.incPDD(Randomizer.nextInt(rand_base) - rand_range);
            }
            if (1 <= equip.getMDD()) {
                equip.incMDD(Randomizer.nextInt(rand_base) - rand_range);
            }
            if (1 <= equip.getEVA()) {
                equip.incEVA(Randomizer.nextInt(rand_base) - rand_range);
            }
            if (1 <= equip.getCraft()) {
                equip.incCraft(Randomizer.nextInt(rand_base) - rand_range);
            }
            if (1 <= equip.getSpeed()) {
                equip.incSpeed(Randomizer.nextInt(rand_base) - rand_range);
            }
            if (1 <= equip.getJump()) {
                equip.incJump(Randomizer.nextInt(rand_base) - rand_range);
            }
            return EnhancementResult.SUCCESS;
        } else {
            if (Randomizer.nextInt(100) < sd.getCursed()) {
                return EnhancementResult.CURSED;
            }
        }
        return EnhancementResult.FAILURE;
    }

    public boolean useViciousHammer() {
        EquipInfo ed = equip.getInfo();
        if (ed == null || ed.getTuc() == 0) {
            return false;
        }
        if (TacosShared.isHorntailNecklace(equip.getItemId())) {
            return false;
        }
        if (Region.KMS.check() || Region.KMST.check()) {
            if (1 <= equip.getIUC()) {
                return false;
            }
        }
        // TODO : 3回以上ビシャスのハンマー使用可能な場合の対応 (JMS)
        if (2 <= equip.getIUC()) {
            return false;
        }
        equip.setIUC(equip.getIUC() + 1);
        equip.setRUC(equip.getRUC() + 1);
        return true;
    }

    public boolean useEquipEnhancementScroll() {
        if (!checkPotentialSystem()) {
            // TODO : 装備強化の書と潜在能力の実装時期に差がある場合は変更が必要
            return false;
        }
        // アップグレード回数が残っているため強化不可
        if (equip.getRUC() != 0) {
            return false;
        }
        EquipInfo ed = equip.getInfo();
        if (ed == null) {
            return false;
        }
        // 強化不可
        if (ed.getTuc() == 0) {
            return false;
        }
        boolean weapon = GameConstants.isWeapon(equip.getItemId());
        equip.incCHUC(1);
        // TODO : 武器以外にも手袋は攻撃力などが確定ではないが上昇したはずなので要確認
        if (weapon) {
            if (equip.getPAD() != 0) {
                equip.incPAD(equip.getPAD() / 50 + 1);
            }
            if (equip.getMAD() != 0) {
                equip.incMAD(equip.getMAD() / 50 + 1);
            }
        }
        // TODO : バージョンによって新規ステータス付与の仕様が異なる可能性があり, 2013年以降は付与されない可能性がある
        // TODO : 追加ステータス付与確率と能力値の上昇の数値の仕様を要確認
        if (1 <= equip.getSTR() || Randomizer.nextInt(100) < 10) {
            equip.incSTR(Randomizer.nextInt(6));
        }
        if (1 <= equip.getDEX() || Randomizer.nextInt(100) < 10) {
            equip.incDEX(Randomizer.nextInt(6));
        }
        if (1 <= equip.getINT() || Randomizer.nextInt(100) < 10) {
            equip.incINT(Randomizer.nextInt(6));
        }
        if (1 <= equip.getLUK() || Randomizer.nextInt(100) < 10) {
            equip.incLUK(Randomizer.nextInt(6));
        }
        if (!weapon) {
            if (1 <= equip.getMaxHP() || Randomizer.nextInt(100) < 10) {
                equip.incMaxHP(Randomizer.nextInt(51));
            }
            if (1 <= equip.getMaxMP() || Randomizer.nextInt(100) < 10) {
                equip.incMaxMP(Randomizer.nextInt(51));
            }
            if (1 <= equip.getPDD() || Randomizer.nextInt(100) < 10) {
                equip.incPDD(Randomizer.nextInt(6));
            }
            if (1 <= equip.getMDD() || Randomizer.nextInt(100) < 10) {
                equip.incMDD(Randomizer.nextInt(6));
            }
            if (1 <= equip.getACC() || Randomizer.nextInt(100) < 10) {
                equip.incACC(Randomizer.nextInt(6));
            }
            if (1 <= equip.getEVA() || Randomizer.nextInt(100) < 10) {
                equip.incEVA(Randomizer.nextInt(6));
            }
            if (1 <= equip.getCraft() || Randomizer.nextInt(100) < 10) {
                equip.incCraft(Randomizer.nextInt(6));
            }
            if (1 <= equip.getSpeed() || Randomizer.nextInt(100) < 10) {
                equip.incSpeed(Randomizer.nextInt(6));
            }
            if (1 <= equip.getJump() || Randomizer.nextInt(100) < 10) {
                equip.incJump(Randomizer.nextInt(6));
            }
        }
        return true;
    }

    public boolean useMagnify() {
        if (equip.getHidden() == 0) {
            return false;
        }
        equip.setHidden(0);
        return true;
    }

    public boolean usePotentialScroll() {
        if (!checkPotentialSystem()) {
            return false;
        }
        if (equip.getHidden() != 0 || equip.getGrade() != 0) {
            return false;
        }
        EquipInfo ed = equip.getInfo();
        if (ed == null) {
            return false;
        }
        // 強化不可
        if (ed.getTuc() == 0) {
            return false;
        }
        int rand = Randomizer.nextInt(100);
        int grade = 1; //rate
        if (rand < RANK_UP_RARE_TO_EPIC) {
            grade++; // epic
        }
        if (rand < RANK_UP_EPIC_TO_UNIQUE) {
            grade++; // unique
        }
        // 初期レジェンダリーは出現したことがないのでユニークまでだと思われる
        equip.setGrade(grade);
        equip.setOption1(DWI_Random.getRandomPotential(grade));
        equip.setPOption2(DWI_Random.getRandomPotential(Math.max((Randomizer.nextInt(100)) < OPTION2_HIGH_RANK ? grade : grade - 1, 1)));
        if (Randomizer.nextInt(100) < OPTION_3LINES) {
            equip.setOption3(DWI_Random.getRandomPotential(Math.max((Randomizer.nextInt(100)) < OPTION3_HIGH_RANK ? grade : grade - 1, 1)));
        }
        equip.setHidden(1);
        return true;
    }

    public boolean useMiracleCube() {
        int grade = equip.getGrade();
        switch (grade) {
            case 1 -> {
                if (Randomizer.nextInt(100) < RANK_UP_RARE_TO_EPIC) {
                    grade++;
                }
            }
            case 2 -> {
                if (Randomizer.nextInt(100) < RANK_UP_EPIC_TO_UNIQUE) {
                    grade++;
                }
            }
            case 3 -> {
                // unique
            }
            default -> {
                return false;
            }
        }
        equip.setGrade(grade);
        equip.setOption1(DWI_Random.getRandomPotential(grade));
        equip.setPOption2(DWI_Random.getRandomPotential(Math.max((Randomizer.nextInt(100)) < OPTION2_HIGH_RANK ? grade : grade - 1, 1)));
        if (equip.getOption3() != 0) {
            equip.setOption3(DWI_Random.getRandomPotential(Math.max((Randomizer.nextInt(100)) < OPTION3_HIGH_RANK ? grade : grade - 1, 1)));
        }
        equip.setHidden(1);
        return true;
    }

    public boolean useHyperMiracleCube() {
        if (equip.getOption3() == 0) {
            if (Randomizer.nextInt(100) < OPTION_3LINES) {
                equip.setOption3(DWI_Random.getRandomPotential(Math.max((Randomizer.nextInt(100)) < OPTION3_HIGH_RANK ? equip.getGrade() : equip.getGrade() - 1, 1)));
            }
        }
        return useMiracleCube();
    }

    public boolean useMasterMiracleCube() {
        int grade = equip.getGrade();
        switch (grade) {
            case 1 -> {
                if (Randomizer.nextInt(100) < RANK_UP_RARE_TO_EPIC * 2) {
                    grade++;
                }
            }
            case 2 -> {
                if (Randomizer.nextInt(100) < RANK_UP_EPIC_TO_UNIQUE * 2) {
                    grade++;
                }
            }
            case 3 -> {
                if (Randomizer.nextInt(100) < RANK_UP_UNIQUE_TO_LEGENDARY * 2) {
                    grade++;
                }
            }
            case 4 -> {
                // lengedary
            }
            default -> {
                return false;
            }
        }
        equip.setGrade(grade);
        equip.setOption1(DWI_Random.getRandomPotential(grade));
        equip.setPOption2(DWI_Random.getRandomPotential(Math.max((Randomizer.nextInt(100)) < OPTION2_HIGH_RANK * 2 ? grade : grade - 1, 1)));
        if (equip.getOption3() != 0) {
            equip.setOption3(DWI_Random.getRandomPotential(Math.max((Randomizer.nextInt(100)) < OPTION3_HIGH_RANK * 2 ? grade : grade - 1, 1)));
        }
        equip.setHidden(1);
        return true;
    }

    public boolean useHyperMasterMiracleCube() {
        if (equip.getOption3() == 0) {
            if (Randomizer.nextInt(100) < OPTION_3LINES) {
                equip.setOption3(DWI_Random.getRandomPotential(Math.max((Randomizer.nextInt(100)) < OPTION3_HIGH_RANK * 2 ? equip.getGrade() : equip.getGrade() - 1, 1)));
            }
        }
        return useMasterMiracleCube();
    }
}
