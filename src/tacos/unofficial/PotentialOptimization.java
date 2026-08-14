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
package tacos.unofficial;

/**
 *
 * @author Riremito
 */
public class PotentialOptimization {

    public static class PotentialOptionData {

        public int incSTR;
        public int incDEX;
        public int incINT;
        public int incLUK;
        public int incACC;
        public int incEVA;
        public int incSpeed;
        public int incJump;
        public int incPAD;
        public int incMAD;
        public int incPDD;
        public int incMDD;
        public int prop;
        public int time;
        public int incSTRr;
        public int incDEXr;
        public int incINTr;
        public int incLUKr;
        public int incMHPr;
        public int incMMPr;
        public int incACCr;
        public int incEVAr;
        public int incPADr;
        public int incMADr;
        public int incPDDr;
        public int incMDDr;
        public int incCr;
        public int incDAMr;
        public int RecoveryHP;
        public int RecoveryMP;
        public int HP;
        public int MP;
        public int level;
        public int ignoreTargetDEF;
        public int ignoreDAM;
        public int DAMreflect;
        public int mpconReduce;
        public int mpRestore;
        public int incMesoProp;
        public int incRewardProp;
        public int incAllskill;
        public int ignoreDAMr;
        public int RecoveryUP;
        public int incMHP;
        public int incMMP;
        public int attackType;
        public int potentialID;
        public int skillID;
        public int optionType;
        public int reqLevel;
        public String face;
        public boolean boss;
    }

    public static boolean ignore(PotentialOptionData pod) {
        if (pod == null) {
            return true;
        }

        if (!pod.face.isEmpty()
                || pod.incMHP != 0
                || pod.incMMP != 0
                || pod.incSTR != 0
                || pod.incDEX != 0
                || pod.incINT != 0
                || pod.incLUK != 0
                || pod.incACC != 0
                || pod.incEVA != 0
                || pod.incSpeed != 0
                || pod.incJump != 0
                || pod.incPAD != 0
                || pod.incMAD != 0
                || pod.incPDD != 0
                || pod.incMDD != 0
                || pod.prop != 0
                || pod.time != 0
                || pod.incACCr != 0
                || pod.incEVAr != 0
                || pod.incPDDr != 0
                || pod.incMDDr != 0
                || pod.RecoveryHP != 0
                || pod.RecoveryMP != 0
                || pod.HP != 0
                || pod.MP != 0
                || pod.level != 0
                || pod.DAMreflect != 0
                || pod.mpconReduce != 0
                || pod.mpRestore != 0
                || pod.incMesoProp != 0
                || pod.incRewardProp != 0
                || pod.incAllskill != 0
                || pod.RecoveryUP != 0) {
            return true;
        }

        return false;
    }
}
