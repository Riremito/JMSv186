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
import odin.constants.GameConstants;
import odin.server.Randomizer;
import tacos.config.Config;
import tacos.config.Region;
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

    private boolean isUpgradeable() {
        if (equip.getRUC() == 0 && equip.getCUC() == 0) {
            return false;
        }
        return true;
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

    public boolean useEquipEnhancementScroll() {
        if (!checkPotentialSystem()) {
            // TODO : 装備強化の書と潜在能力の実装時期に差がある場合は変更が必要
            return false;
        }
        if (equip.getRUC() != 0 || !isUpgradeable()) {
            return false;
        }
        boolean weapon = GameConstants.isWeapon(equip.getItemId());
        equip.incCHUC(1);
        // TODO : 武器以外にも手袋は攻撃力などが確定ではないが上昇したはずなので要確認
        if (weapon) {
            if (equip.getIncPAD() != 0) {
                equip.incPAD(equip.getIncPAD() / 50 + 1);
            }
            if (equip.getIncMAD() != 0) {
                // TODO : 魔力の上昇設定
            }
        }
        // TODO : バージョンによって新規ステータス付与の仕様が異なる可能性があり, 2013年以降は付与されない可能性がある
        // TODO : 追加ステータス付与確率と能力値の上昇の数値の仕様を要確認
        if (equip.getIncDEX() != 0 || Randomizer.nextInt(100) < 10) {
            equip.incDEX(Randomizer.nextInt(6));
        }
        if (equip.getIncLUK() != 0 || Randomizer.nextInt(100) < 10) {
            equip.incLUK(Randomizer.nextInt(6));
        }
        // TODO : 他のステータス値の処理
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
        if (equip.getHidden() != 0 || equip.getGrade() != 0 || !isUpgradeable()) {
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
