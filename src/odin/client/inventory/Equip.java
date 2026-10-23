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

import tacos.config.Region;
import odin.constants.GameConstants;
import tacos.wz.ids.DWI_Random;
import odin.server.Randomizer;
import tacos.config.Config;

public class Equip extends Item {

    public enum ScrollResult {

        SUCCESS, FAIL, CURSE
    }
    public static final int ARMOR_RATIO = 350000;
    public static final int WEAPON_RATIO = 700000;

    private int upgradeSlots = 0;
    private int level = 0;
    private int vicioushammer = 0;
    private int enhance = 0;
    private int str = 0;
    private int dex = 0;
    private int _int = 0;
    private int luk = 0;
    private int hp = 0;
    private int mp = 0;
    private int watk = 0;
    private int matk = 0;
    private int wdef = 0;
    private int mdef = 0;
    private int acc = 0;
    private int avoid = 0;
    private int hands = 0;
    private int speed = 0;
    private int jump = 0;
    private int hpR = 0;
    private int mpR = 0;
    private int rank = 0;
    private int hidden = 0;
    private int potential1 = 0;
    private int potential2 = 0;
    private int potential3 = 0;
    private int itemEXP = 0;
    private int durability = -1;
    private int incattackSpeed = 0; // 攻撃速度の書

    public Equip(int id, short position, byte flag) {
        super(id, position, (short) 1, flag);
    }

    public Equip(int id, short position, int uniqueid, byte flag) {
        super(id, position, (short) 1, flag, uniqueid);
    }

    public int getRUC() {
        return this.upgradeSlots;
    }

    public int getCUC() {
        return this.level;
    }

    public int getIncSTR() {
        return this.str;
    }

    public int getIncDEX() {
        return this.dex;
    }

    public int getIncINT() {
        return this._int;
    }

    public int getIncLUK() {
        return this.luk;
    }

    public int getIncMaxHP() {
        return this.hp;
    }

    public int getIncMaxMP() {
        return this.mp;
    }

    public int getIncPAD() {
        return this.watk;
    }

    public int getIncMAD() {
        return this.matk;
    }

    public int getIncPDD() {
        return this.wdef;
    }

    public int getIncMDD() {
        return this.mdef;
    }

    public int getIncACC() {
        return this.acc;
    }

    public int getIncEVA() {
        return this.avoid;
    }

    public int getIncCraft() {
        return this.hands;
    }

    public int getIncSpeed() {
        return this.speed;
    }

    public int getIncJump() {
        return this.jump;
    }

    // owner.
    // nAttribute.
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
        return this.durability;
    }

    public int getIUC() {
        return this.vicioushammer;
    }

    public int getGrade() {
        return this.rank;
    }

    public int getCHUC() {
        return this.enhance;
    }

    public int getOption1() {
        return this.potential1;
    }

    public int getOption2() {
        return this.potential2;
    }

    public int getOption3() {
        return this.potential3;
    }

    // nSocket1.
    // nSocket2.
    @Override
    public Item copy() {
        Equip ret = new Equip(getItemId(), getPosition(), getUniqueId(), getFlag());
        ret.str = str;
        ret.dex = dex;
        ret._int = _int;
        ret.luk = luk;
        ret.hp = hp;
        ret.mp = mp;
        ret.matk = matk;
        ret.mdef = mdef;
        ret.watk = watk;
        ret.wdef = wdef;
        ret.acc = acc;
        ret.avoid = avoid;
        ret.hands = hands;
        ret.speed = speed;
        ret.jump = jump;
        ret.enhance = enhance;
        ret.upgradeSlots = upgradeSlots;
        ret.level = level;
        ret.itemEXP = itemEXP;
        ret.durability = durability;
        ret.vicioushammer = vicioushammer;
        ret.rank = rank;
        ret.hidden = hidden;
        ret.potential1 = potential1;
        ret.potential2 = potential2;
        ret.potential3 = potential3;
        ret.hpR = hpR;
        ret.mpR = mpR;
        ret.incattackSpeed = incattackSpeed;
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

    public void setStr(int str) {
        this.str = Math.max(0, str);
    }

    public void setDex(int dex) {
        this.dex = Math.max(0, dex);
    }

    public void setInt(int _int) {
        this._int = Math.max(0, _int);
    }

    public void setLuk(int luk) {
        this.luk = Math.max(0, luk);
    }

    public void setHp(int hp) {
        this.hp = Math.max(0, hp);
    }

    public void setMp(int mp) {
        this.mp = Math.max(0, mp);
    }

    public void setWatk(int watk) {
        this.watk = Math.max(0, watk);
    }

    public void setMatk(int matk) {
        this.matk = Math.max(0, matk);
    }

    public void setWdef(int wdef) {
        this.wdef = Math.max(0, wdef);
    }

    public void setMdef(int mdef) {
        this.mdef = Math.max(0, mdef);
    }

    public void setAcc(int acc) {
        this.acc = Math.max(0, acc);
    }

    public void setAvoid(int avoid) {
        this.avoid = Math.max(0, avoid);
    }

    public void setHands(int hands) {
        this.hands = Math.max(0, hands);
    }

    public void setSpeed(int speed) {
        this.speed = Math.max(0, speed);
    }

    public void setJump(int jump) {
        this.jump = Math.max(0, jump);
    }

    public void setUpgradeSlots(int upgradeSlots) {
        this.upgradeSlots = upgradeSlots;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setViciousHammer(int ham) {
        vicioushammer = ham;
    }

    public int getItemEXP() {
        return itemEXP;
    }

    public void setItemEXP(int itemEXP) {
        this.itemEXP = Math.max(0, itemEXP);
    }

    public int getEquipExp() {
        if (itemEXP <= 0) {
            return 0;
        }
        //aproximate value
        if (GameConstants.isWeapon(getItemId())) {
            return itemEXP / Equip.WEAPON_RATIO;
        }
        return itemEXP / Equip.ARMOR_RATIO;
    }

    public int getEquipExpForLevel() {
        if (getEquipExp() <= 0) {
            return 0;
        }
        int expz = getEquipExp();
        for (int i = getBaseLevel(); i <= GameConstants.getMaxLevel(getItemId()); i++) {
            if (expz >= GameConstants.getExpForLevel(i, getItemId())) {
                expz -= GameConstants.getExpForLevel(i, getItemId());
            } else { //for 0, dont continue;
                break;
            }
        }
        return expz;
    }

    public int getExpPercentage() {
        if (getEquipLevel() < getBaseLevel() || getEquipLevel() > GameConstants.getMaxLevel(getItemId()) || GameConstants.getExpForLevel(getEquipLevel(), getItemId()) <= 0) {
            return 0;
        }
        return getEquipExpForLevel() * 100 / GameConstants.getExpForLevel(getEquipLevel(), getItemId());
    }

    public int getEquipLevel() {
        if (GameConstants.getMaxLevel(getItemId()) <= 0) {
            return 0;
        } else if (getEquipExp() <= 0) {
            return getBaseLevel();
        }
        int levelz = getBaseLevel();
        int expz = getEquipExp();
        for (int i = levelz; (GameConstants.getStatFromWeapon(getItemId()) == null ? (i <= GameConstants.getMaxLevel(getItemId())) : (i < GameConstants.getMaxLevel(getItemId()))); i++) {
            if (expz >= GameConstants.getExpForLevel(i, getItemId())) {
                levelz++;
                expz -= GameConstants.getExpForLevel(i, getItemId());
            } else { //for 0, dont continue;
                break;
            }
        }
        return levelz;
    }

    public int getBaseLevel() {
        return (GameConstants.getStatFromWeapon(getItemId()) == null ? 1 : 0);
    }

    private int nLevelUpType = 0; // nLevelUpType
    private int nLevel = 0; // nLevel
    private int nEXP = 0; // nEXP

    public void setItemLevel(int nLevelUpType, int nLevel, int nEXP) {
        this.nLevelUpType = nLevelUpType;
        this.nLevel = nLevel;
        this.nEXP = nEXP;
    }

    @Override
    public void setQuantity(short quantity) {
        if (quantity < 0 || quantity > 1) {
            throw new RuntimeException("Setting the quantity to " + quantity + " on an equip (itemid: " + getItemId() + ")");
        }
        super.setQuantity(quantity);
    }

    public void setDurability(int dur) {
        this.durability = dur;
    }

    public void setEnhance(int en) {
        this.enhance = en;
    }

    public void setPotential1(int en) {
        this.potential1 = en;
    }

    public void setPotential2(int en) {
        this.potential2 = en;
    }

    public void setPotential3(int en) {
        this.potential3 = en;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getHidden() {
        return hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }

    public int getNewLines() {
        // 3行
        if (getOption3() != 0) {
            return 3;
        }
        // 初回抽選
        if (getOption1() == 0) {
            // 初回は50%で3行 (テスト)
            if (50 <= Randomizer.nextInt(100)) {
                return 3;
            }
        }
        // 2行
        return 2;
    }

    public int getNewRank(boolean master) {
        // エピック抽選
        if (2 <= getGrade() || 50 <= Randomizer.nextInt(100)) {
            // ユニーク抽選
            if (3 <= getGrade() || 50 <= Randomizer.nextInt(100)) {
                // レジェンダリー抽選
                if (master && (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.EMS, 89)) && (4 <= getGrade() || 50 <= Randomizer.nextInt(100))) {
                    return 4;
                }
                return 3;
            }
            return 2;
        }
        return 1;
    }

    // the second and third lines are the same rank or one rank lower
    private static int getSubRank(int rank) {
        if (rank < 2) {
            return 1;
        }
        return (50 <= Randomizer.nextInt(100)) ? rank : rank - 1;
    }

    public boolean resetPotential(boolean hyper, boolean master) {
        if (getHidden() != 0) {
            return false;
        }
        int lines = getNewLines();
        int newrank = getNewRank(master);
        setHidden(1); // 未確認状態
        setRank(newrank); // 等級

        if (hyper && 50 <= Randomizer.nextInt(100)) {
            lines = 3;
        }

        setPotential1(DWI_Random.getRandomPotential(newrank));
        setPotential2(DWI_Random.getRandomPotential(getSubRank(newrank)));
        if (lines == 3) {
            setPotential3(DWI_Random.getRandomPotential(getSubRank(newrank)));
        }
        return true;
    }

    public int getHpR() {
        return hpR;
    }

    public void setHpR(int hp) {
        this.hpR = hp;
    }

    public int getMpR() {
        return mpR;
    }

    public void setMpR(int mp) {
        this.mpR = mp;
    }

    // 攻撃速度の書
    public int getIncAttackSpeed() {
        return incattackSpeed;
    }

    public void setIncAttackSpeed(int incattackSpeed) {
        this.incattackSpeed = incattackSpeed;
    }
}
