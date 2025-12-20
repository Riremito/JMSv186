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
package tacos.client;

import tacos.packet.ops.OpsForcedStat;

/**
 *
 * @author Riremito
 */
public class TacosForcedStat {

    private int nSTR = 0;
    private int nDEX = 0;
    private int nINT = 0;
    private int nLUK = 0;
    private int nPAD = 0;
    private int nPDD = 0;
    private int nMAD = 0;
    private int nMDD = 0;
    private int nACC = 0;
    private int nEVA = 0;
    private int nSpeed = 0;
    private int nJump = 0;
    private int nSpeedMax = 0;

    public TacosForcedStat() {

    }

    public void reset() {
        this.nSTR = 0;
        this.nDEX = 0;
        this.nINT = 0;
        this.nLUK = 0;
        this.nPAD = 0;
        this.nPDD = 0;
        this.nMAD = 0;
        this.nMDD = 0;
        this.nACC = 0;
        this.nEVA = 0;
        this.nSpeed = 0;
        this.nJump = 0;
        this.nSpeedMax = 0;
    }

    public int getMask() {
        int mask = 0;
        if (this.nSTR != 0) {
            mask |= OpsForcedStat.STR.get();
        }
        if (this.nDEX != 0) {
            mask |= OpsForcedStat.DEX.get();
        }
        if (this.nINT != 0) {
            mask |= OpsForcedStat.INT.get();
        }
        if (this.nLUK != 0) {
            mask |= OpsForcedStat.LUK.get();
        }
        if (this.nPAD != 0) {
            mask |= OpsForcedStat.PAD.get();
        }
        if (this.nPDD != 0) {
            mask |= OpsForcedStat.PDD.get();
        }
        if (this.nMAD != 0) {
            mask |= OpsForcedStat.MAD.get();
        }
        if (this.nMDD != 0) {
            mask |= OpsForcedStat.MDD.get();
        }
        if (this.nACC != 0) {
            mask |= OpsForcedStat.ACC.get();
        }
        if (this.nEVA != 0) {
            mask |= OpsForcedStat.EVA.get();
        }
        if (this.nSpeed != 0) {
            mask |= OpsForcedStat.SPEED.get();
        }
        if (this.nJump != 0) {
            mask |= OpsForcedStat.JUMP.get();
        }
        if (this.nSpeedMax != 0) {
            mask |= OpsForcedStat.SPEEDMAX.get();
        }
        return mask;
    }

    public int getSTR() {
        return this.nSTR;
    }

    public void setSTR(int nSTR) {
        this.nSTR = nSTR;
    }

    public int getDEX() {
        return this.nDEX;
    }

    public void setDEX(int nDEX) {
        this.nDEX = nDEX;
    }

    public int getINT() {
        return this.nINT;
    }

    public void setINT(int nINT) {
        this.nINT = nINT;
    }

    public int getLUK() {
        return this.nLUK;
    }

    public void setLUK(int nLUK) {
        this.nLUK = nLUK;
    }

    public int getPAD() {
        return this.nPAD;
    }

    public void setPAD(int nPAD) {
        this.nPAD = nPAD;
    }

    public int getPDD() {
        return this.nPDD;
    }

    public void setPDD(int nPDD) {
        this.nPDD = nPDD;
    }

    public int getMAD() {
        return this.nMAD;
    }

    public void setMAD(int nMAD) {
        this.nMAD = nMAD;
    }

    public int getMDD() {
        return this.nMDD;
    }

    public void setMDD(int nMDD) {
        this.nMDD = nMDD;
    }

    public int getACC() {
        return this.nACC;
    }

    public void setACC(int nACC) {
        this.nACC = nACC;
    }

    public int getEVA() {
        return this.nEVA;
    }

    public void setEVA(int nEVA) {
        this.nEVA = nEVA;
    }

    public int getSpeed() {
        return this.nSpeed;
    }

    public void setSpeed(int nSpeed) {
        this.nSpeed = nSpeed;
    }

    public int getJump() {
        return this.nJump;
    }

    public void setJump(int nJump) {
        this.nJump = nJump;
    }

    public int getSpeedMax() {
        return this.nSpeedMax;
    }

    public void setSpeedMax(int nSpeedMax) {
        this.nSpeedMax = nSpeedMax;
    }

}
