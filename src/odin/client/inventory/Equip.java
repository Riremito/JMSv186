/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.client.inventory;

import tacos.packet.ops.OpsGrade;
import tacos.server.TacosEnhancement;
import tacos.wz.CharacterWz.EquipInfo;
import tacos.wz.WzXML;

public class Equip extends Item {

    // 変動可能数能力値
    private int nRUC = 0;
    private int nCUC = 0;
    private int nSTR = 0;
    private int nDEX = 0;
    private int nINT = 0;
    private int nLUK = 0;
    private int nMaxHP = 0;
    private int nMaxMP = 0;
    private int nPAD = 0;
    private int nMAD = 0;
    private int nPDD = 0;
    private int nMDD = 0;
    private int nACC = 0;
    private int nEVA = 0;
    private int nCraft = 0;
    private int nSpeed = 0;
    private int nJump = 0;
    // owner
    // nAttribute (flag)
    private int nLevelUpType = 0; // 成長装備
    private int nLevel = 0; // 成長装備
    private int nEXP = 0; // 成長装備
    private int nDurability = -1; // 耐久装備
    private int nIUC = 0; // ビシャスのハンマー
    private int nGrade = 0; // 潜在
    private int hidden = 0; // 潜在
    private int nCHUC = 0; // 装備強化
    private int nOption1 = 0; // 潜在能力1
    private int nOption2 = 0; // 潜在能力2
    private int nOption3 = 0; // 潜在能力3
    private int nSocket1 = 0; // 潜在能力4
    private int nSocket2 = 0; // 潜在能力5 or カナトコ (共用)
    // 固定能力値
    private int nMaxHPr = 0;
    private int nMaxMPr = 0;
    // test.
    private int itemEXP = 0;
    private int nAttackSpeed = 0; // 攻撃速度

    public Equip(int id, short position, int uniqueid, byte flag) {
        super(id, position, (short) 1, flag, uniqueid);
    }

    public TacosEnhancement getEnhancement() {
        return new TacosEnhancement(this);
    }

    public EquipInfo getInfo() {
        return WzXML.CHARACTER.getEquipInfo(getItemId());
    }

    // get.
    public int getRUC() {
        return this.nRUC;
    }

    public int getCUC() {
        return this.nCUC;
    }

    public int getSTR() {
        return this.nSTR;
    }

    public int getDEX() {
        return this.nDEX;
    }

    public int getINT() {
        return this.nINT;
    }

    public int getLUK() {
        return this.nLUK;
    }

    public int getMaxHP() {
        return this.nMaxHP;
    }

    public int getMaxMP() {
        return this.nMaxMP;
    }

    public int getPAD() {
        return this.nPAD;
    }

    public int getMAD() {
        return this.nMAD;
    }

    public int getPDD() {
        return this.nPDD;
    }

    public int getMDD() {
        return this.nMDD;
    }

    public int getACC() {
        return this.nACC;
    }

    public int getEVA() {
        return this.nEVA;
    }

    public int getCraft() {
        return this.nCraft;
    }

    public int getSpeed() {
        return this.nSpeed;
    }

    public int getJump() {
        return this.nJump;
    }

    // owner.
    // nAttribute
    public int getLevelUpType() {
        return this.nLevelUpType;
    }

    public int getLevelLevel() {
        return this.nLevel;

    }

    public int getLevelEXP() {
        return this.nEXP;
    }

    public int getDurability() {
        return this.nDurability;
    }

    public int getIUC() {
        return this.nIUC;
    }

    public int getGrade() {
        return this.nGrade;
    }

    public OpsGrade getOpsGrade() {
        if (this.hidden != 0) {
            return OpsGrade.HIDDEN;
        }
        switch (this.nGrade) {

            case 1 -> {
                return OpsGrade.RARE;
            }
            case 2 -> {
                return OpsGrade.EPIC;
            }
            case 3 -> {
                return OpsGrade.UNIQUE;
            }
            case 4 -> {
                return OpsGrade.LEGENDARY;
            }
            default -> {
                return OpsGrade.NONE;
            }
        }
    }

    public int getCHUC() {
        return this.nCHUC;
    }

    public int getOption1() {
        return this.nOption1;
    }

    public int getOption2() {
        return this.nOption2;
    }

    public int getOption3() {
        return this.nOption3;
    }

    public int getSocket1() {
        return this.nSocket1;
    }

    public int getSocket2() {
        return this.nSocket2;
    }

    // set.
    public void setRUC(int nRUC) {
        this.nRUC = nRUC;
    }

    public void setCUC(int nCUC) {
        this.nCUC = nCUC;
    }

    public void setSTR(int nSTR) {
        this.nSTR = checkShort(nSTR);
    }

    public void setDEX(int nDEX) {
        this.nDEX = checkShort(nDEX);
    }

    public void setINT(int nINT) {
        this.nINT = checkShort(nINT);
    }

    public void setLUK(int nLUK) {
        this.nLUK = checkShort(nLUK);
    }

    public void setMaxHP(int nMaxHP) {
        this.nMaxHP = checkShort(nMaxHP);
    }

    public void setMaxMP(int nMaxMP) {
        this.nMaxMP = checkShort(nMaxMP);
    }

    public void setPAD(int nPAD) {
        this.nPAD = checkShort(nPAD);
    }

    public void setMAD(int nMAD) {
        this.nMAD = checkShort(nMAD);
    }

    public void setPDD(int nPDD) {
        this.nPDD = checkShort(nPDD);
    }

    public void setMDD(int nMDD) {
        this.nMDD = checkShort(nMDD);
    }

    public void setACC(int nACC) {
        this.nACC = checkShort(nACC);
    }

    public void setEVA(int nEVA) {
        this.nEVA = checkShort(nEVA);
    }

    public void setCraft(int nCraft) {
        this.nCraft = checkShort(nCraft);
    }

    public void setSpeed(int nSpeed) {
        this.nSpeed = checkShort(nSpeed);
    }

    public void setJump(int nJump) {
        this.nJump = checkShort(nJump);
    }

    public void setLevelUpType(int nLevelUpType) {
        this.nLevelUpType = nLevelUpType;
    }

    public void setLevelLevel(int nLevel) {
        this.nLevel = nLevel;

    }

    public void setLevelEXP(int nEXP) {
        this.nEXP = nEXP;
    }

    public void setIUC(int nIUC) {
        this.nIUC = nIUC;
    }

    public void setDurability(int nDurability) {
        this.nDurability = nDurability;
    }

    public void setGrade(int nGrade) {
        this.nGrade = nGrade;
    }

    public void setCHUC(int nCHUC) {
        this.nCHUC = nCHUC;
    }

    public void setOption1(int nOption1) {
        this.nOption1 = nOption1;
    }

    public void setPOption2(int nOption2) {
        this.nOption2 = nOption2;
    }

    public void setOption3(int nOption3) {
        this.nOption3 = nOption3;
    }

    // inc.
    private int checkByte(int value) {
        if (Byte.MAX_VALUE < value) {
            return Byte.MAX_VALUE;
        }
        if (value < 0) {
            return 0;
        }
        return value;
    }

    private int checkShort(int value) {
        if (Short.MAX_VALUE < value) {
            return Short.MAX_VALUE;
        }
        if (value < 0) {
            return 0;
        }
        return value;
    }

    public void incRUC(int value) {
        this.nRUC = checkByte(this.nRUC + value);
    }

    public void incCUC(int value) {
        this.nCUC = checkByte(this.nCUC + value);
    }

    public void incSTR(int value) {
        this.nSTR = checkShort(this.nSTR + value);
    }

    public void incDEX(int value) {
        this.nDEX = checkShort(this.nDEX + value);
    }

    public void incINT(int value) {
        this.nINT = checkShort(this.nINT + value);
    }

    public void incLUK(int value) {
        this.nLUK = checkShort(this.nLUK + value);
    }

    public void incMaxHP(int value) {
        this.nMaxHP = checkShort(this.nMaxHP + value);
    }

    public void incMaxMP(int value) {
        this.nMaxMP = checkShort(this.nMaxMP + value);
    }

    public void incPAD(int value) {
        this.nPAD = checkShort(this.nPAD + value);
    }

    public void incMAD(int value) {
        this.nMAD = checkShort(this.nMAD + value);
    }

    public void incPDD(int value) {
        this.nPDD = checkShort(this.nPDD + value);
    }

    public void incMDD(int value) {
        this.nMDD = checkShort(this.nMDD + value);
    }

    public void incACC(int value) {
        this.nACC = checkShort(this.nACC + value);
    }

    public void incEVA(int value) {
        this.nEVA = checkShort(this.nEVA + value);
    }

    public void incCraft(int value) {
        this.nCraft = checkShort(this.nCraft + value);
    }

    public void incSpeed(int value) {
        this.nSpeed = checkShort(this.nSpeed + value);
    }

    public void incJump(int value) {
        this.nJump = checkShort(this.nJump + value);
    }

    public void incCHUC(int value) {
        this.nCHUC = checkByte(this.nCHUC + value);
    }

    public void incAttackSpeed(int value) {
        this.nAttackSpeed = checkByte(this.nAttackSpeed + value);
    }

    // nSocket1.
    // nSocket2.
    @Override
    public Item copy() {
        Equip ret = new Equip(getItemId(), getPosition(), getUniqueId(), getFlag());
        ret.nSTR = nSTR;
        ret.nDEX = nDEX;
        ret.nINT = nINT;
        ret.nLUK = nLUK;
        ret.nMaxHP = nMaxHP;
        ret.nMaxMP = nMaxMP;
        ret.nMAD = nMAD;
        ret.nMDD = nMDD;
        ret.nPAD = nPAD;
        ret.nPDD = nPDD;
        ret.nACC = nACC;
        ret.nEVA = nEVA;
        ret.nCraft = nCraft;
        ret.nSpeed = nSpeed;
        ret.nJump = nJump;
        ret.nCHUC = nCHUC;
        ret.nRUC = nRUC;
        ret.nCUC = nCUC;
        ret.itemEXP = itemEXP;
        ret.nDurability = nDurability;
        ret.nIUC = nIUC;
        ret.nGrade = nGrade;
        ret.hidden = hidden;
        ret.nOption1 = nOption1;
        ret.nOption2 = nOption2;
        ret.nOption3 = nOption3;
        ret.nMaxHPr = nMaxHPr;
        ret.nMaxMPr = nMaxMPr;
        ret.nAttackSpeed = nAttackSpeed;
        ret.setGiftFrom(getGiftFrom());
        ret.setOwner(getOwner());
        ret.setQuantity(getQuantity());
        ret.setExpiration(getExpiration());
        return ret;
    }

    @Override
    public byte getType() {
        return 1;
    }

    @Override
    public void setQuantity(short quantity) {
        super.setQuantity((short) 1);
    }

    public int getHidden() {
        return hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }

    public int getMaxHPr() {
        return this.nMaxHPr;
    }

    public void setMaxHPr(int nMaxHPr) {
        this.nMaxHPr = nMaxHPr;
    }

    public int getMaxMPr() {
        return this.nMaxMPr;
    }

    public void setMaxMPr(int nMaxMPr) {
        this.nMaxMPr = nMaxMPr;
    }

    public int getAttackSpeed() {
        return nAttackSpeed;
    }

    public void setAttackSpeed(int nAttackSpeed) {
        this.nAttackSpeed = nAttackSpeed;
    }

    public enum ScrollResult {

        SUCCESS, FAIL, CURSE
    }
}
