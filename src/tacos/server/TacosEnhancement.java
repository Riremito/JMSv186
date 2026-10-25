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
import odin.server.Randomizer;
import tacos.config.Config;
import tacos.config.Region;
import tacos.wz.ids.DWI_Random;

/**
 *
 * @author Riremito
 */
public class TacosEnhancement {

    // currently rate is x20.
    private static final int RANK_UP_RARE_TO_EPIC = 80;
    private static final int RANK_UP_EPIC_TO_UNIQUE = 40;
    private static final int RANK_UP_UNIQUE_TO_LEGENDARY = 20;
    private static final int OPTION_3LINES = 50;
    private static final int OPTION2_HIGH_RANK = 10;
    private static final int OPTION3_HIGH_RANK = 10;

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
        if (equip.getHidden() != 0 || equip.getGrade() != 0 || (equip.getRUC() == 0 && equip.getCUC() == 0)) {
            return false;
        }
        int rand = Randomizer.nextInt(100);
        int grade = 1; //rate
        // epic, 4%
        if (rand < RANK_UP_RARE_TO_EPIC) {
            grade++; // epic
        }
        // unique, 2%
        if (rand < RANK_UP_EPIC_TO_UNIQUE) {
            grade++; // unique
        }
        equip.setGrade(grade);
        equip.setOption1(DWI_Random.getRandomPotential(grade));
        equip.setPOption2(DWI_Random.getRandomPotential(Math.max((Randomizer.nextInt(100)) < 10 ? grade : grade - 1, 1))); // 10%
        // 3 options, 30%
        if (Randomizer.nextInt(100) < OPTION_3LINES) {
            equip.setOption3(DWI_Random.getRandomPotential(Math.max((Randomizer.nextInt(100)) < 10 ? grade : grade - 1, 1))); // 10%
        }
        equip.setHidden(1);
        return true;
    }

    public boolean useMiracleCube() {
        int grade = equip.getGrade();
        switch (grade) {
            case 1 -> {
                // rare to epic, 4%
                if (Randomizer.nextInt(100) < RANK_UP_RARE_TO_EPIC) {
                    grade++;
                }
            }
            case 2 -> {
                // epic to unique, 2%
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
                // rare to epic, 8%
                if (Randomizer.nextInt(100) < 80) {
                    grade++;
                }
            }
            case 2 -> {
                // epic to unique, 4%
                if (Randomizer.nextInt(100) < 40) {
                    grade++;
                }
            }
            case 3 -> {
                // unique to legendary, 2%
                if (Randomizer.nextInt(100) < 20) {
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
        equip.setPOption2(DWI_Random.getRandomPotential(Math.max((Randomizer.nextInt(100)) < OPTION2_HIGH_RANK ? grade : grade - 1, 1)));
        if (equip.getOption3() != 0) {
            equip.setOption3(DWI_Random.getRandomPotential(Math.max((Randomizer.nextInt(100)) < OPTION3_HIGH_RANK ? grade : grade - 1, 1)));
        }
        equip.setHidden(1);
        return true;
    }

    public boolean useHyperMasterMiracleCube() {
        if (equip.getOption3() == 0) {
            if (Randomizer.nextInt(100) < OPTION_3LINES) {
                equip.setOption3(DWI_Random.getRandomPotential(Math.max((Randomizer.nextInt(100)) < OPTION3_HIGH_RANK ? equip.getGrade() : equip.getGrade() - 1, 1)));
            }
        }
        return useMasterMiracleCube();
    }
}
