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
package client.inventory;

import config.ServerConfig;
import constants.GameConstants;
import java.io.Serializable;
import server.Randomizer;
import wz.LoadData;

public class Equip extends Item implements IEquip, Serializable {

    private int upgradeSlots = 0, level = 0, vicioushammer = 0, enhance = 0;
    private int str = 0, dex = 0, _int = 0, luk = 0, hp = 0, mp = 0, watk = 0, matk = 0, wdef = 0, mdef = 0, acc = 0, avoid = 0, hands = 0, speed = 0, jump = 0, hpR = 0, mpR = 0;
    private int rank = 0, hidden = 0, potential1 = 0, potential2 = 0, potential3 = 0;
    private int itemEXP = 0, durability = -1;
    private int incattackSpeed = 0; // 攻撃速度の書

    public Equip(int id, short position, byte flag) {
        super(id, position, (short) 1, flag);
    }

    public Equip(int id, short position, int uniqueid, byte flag) {
        super(id, position, (short) 1, flag, uniqueid);
    }

    @Override
    public IItem copy() {
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

    @Override
    public int getUpgradeSlots() {
        return upgradeSlots;
    }

    @Override
    public int getStr() {
        return str;
    }

    @Override
    public int getDex() {
        return dex;
    }

    @Override
    public int getInt() {
        return _int;
    }

    @Override
    public int getLuk() {
        return luk;
    }

    @Override
    public int getHp() {
        return hp;
    }

    @Override
    public int getMp() {
        return mp;
    }

    @Override
    public int getWatk() {
        return watk;
    }

    @Override
    public int getMatk() {
        return matk;
    }

    @Override
    public int getWdef() {
        return wdef;
    }

    @Override
    public int getMdef() {
        return mdef;
    }

    @Override
    public int getAcc() {
        return acc;
    }

    @Override
    public int getAvoid() {
        return avoid;
    }

    @Override
    public int getHands() {
        return hands;
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public int getJump() {
        return jump;
    }

    public void setStr(int str) {
        if (str < 0) {
            str = 0;
        }
        this.str = str;
    }

    public void setDex(int dex) {
        if (dex < 0) {
            dex = 0;
        }
        this.dex = dex;
    }

    public void setInt(int _int) {
        if (_int < 0) {
            _int = 0;
        }
        this._int = _int;
    }

    public void setLuk(int luk) {
        if (luk < 0) {
            luk = 0;
        }
        this.luk = luk;
    }

    public void setHp(int hp) {
        if (hp < 0) {
            hp = 0;
        }
        this.hp = hp;
    }

    public void setMp(int mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    public void setWatk(int watk) {
        if (watk < 0) {
            watk = 0;
        }
        this.watk = watk;
    }

    public void setMatk(int matk) {
        if (matk < 0) {
            matk = 0;
        }
        this.matk = matk;
    }

    public void setWdef(int wdef) {
        if (wdef < 0) {
            wdef = 0;
        }
        this.wdef = wdef;
    }

    public void setMdef(int mdef) {
        if (mdef < 0) {
            mdef = 0;
        }
        this.mdef = mdef;
    }

    public void setAcc(int acc) {
        if (acc < 0) {
            acc = 0;
        }
        this.acc = acc;
    }

    public void setAvoid(int avoid) {
        if (avoid < 0) {
            avoid = 0;
        }
        this.avoid = avoid;
    }

    public void setHands(int hands) {
        if (hands < 0) {
            hands = 0;
        }
        this.hands = hands;
    }

    public void setSpeed(int speed) {
        if (speed < 0) {
            speed = 0;
        }
        this.speed = speed;
    }

    public void setJump(int jump) {
        if (jump < 0) {
            jump = 0;
        }
        this.jump = jump;
    }

    public void setUpgradeSlots(int upgradeSlots) {
        this.upgradeSlots = upgradeSlots;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getViciousHammer() {
        return vicioushammer;
    }

    public void setViciousHammer(int ham) {
        vicioushammer = ham;
    }

    @Override
    public int getItemEXP() {
        return itemEXP;
    }

    public void setItemEXP(int itemEXP) {
        if (itemEXP < 0) {
            itemEXP = 0;
        }
        this.itemEXP = itemEXP;
    }

    @Override
    public int getEquipExp() {
        if (itemEXP <= 0) {
            return 0;
        }
        //aproximate value
        if (GameConstants.isWeapon(getItemId())) {
            return itemEXP / IEquip.WEAPON_RATIO;
        } else {
            return itemEXP / IEquip.ARMOR_RATIO;
        }
    }

    @Override
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

    @Override
    public int getExpPercentage() {
        if (getEquipLevel() < getBaseLevel() || getEquipLevel() > GameConstants.getMaxLevel(getItemId()) || GameConstants.getExpForLevel(getEquipLevel(), getItemId()) <= 0) {
            return 0;
        }
        return getEquipExpForLevel() * 100 / GameConstants.getExpForLevel(getEquipLevel(), getItemId());
    }

    @Override
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

    @Override
    public int getBaseLevel() {
        return (GameConstants.getStatFromWeapon(getItemId()) == null ? 1 : 0);
    }

    @Override
    public void setQuantity(short quantity) {
        if (quantity < 0 || quantity > 1) {
            throw new RuntimeException("Setting the quantity to " + quantity + " on an equip (itemid: " + getItemId() + ")");
        }
        super.setQuantity(quantity);
    }

    @Override
    public int getDurability() {
        return durability;
    }

    public void setDurability(final int dur) {
        this.durability = dur;
    }

    @Override
    public int getEnhance() {
        return enhance;
    }

    public void setEnhance(int en) {
        this.enhance = en;
    }

    @Override
    public int getPotential1() {
        return potential1;
    }

    public void setPotential1(int en) {
        this.potential1 = en;
    }

    @Override
    public int getPotential2() {
        return potential2;
    }

    public void setPotential2(int en) {
        this.potential2 = en;
    }

    @Override
    public int getPotential3() {
        return potential3;
    }

    public void setPotential3(int en) {
        this.potential3 = en;
    }

    @Override
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public int getHidden() {
        return hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }

    public int getNewLines() {
        // 3行
        if (getPotential3() != 0) {
            return 3;
        }
        // 初回抽選
        if (getPotential1() == 0) {
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
        if (2 <= getRank() || 50 <= Randomizer.nextInt(100)) {
            // ユニーク抽選
            if (3 <= getRank() || 50 <= Randomizer.nextInt(100)) {
                // レジェンダリー抽選
                if (master) {
                    if (ServerConfig.JMS302orLater() || ServerConfig.EMS89orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
                        if (4 <= getRank() || 50 <= Randomizer.nextInt(100)) {
                            return 4;
                        }
                    }
                }
                return 3;
            }
            return 2;
        }
        return 1;
    }

    public boolean resetPotential(boolean hyper, boolean master) {
        int lines = getNewLines();
        int newrank = getNewRank(master);
        if (getHidden() != 0) {
            return false;
        }
        setHidden(1); // 未確認状態
        setRank(newrank); // 等級

        if (hyper) {
            if (50 <= Randomizer.nextInt(100)) {
                lines = 3;
            }
        }

        setPotential1(LoadData.getRandomPotential(newrank));
        setPotential2(LoadData.getRandomPotential((2 <= newrank) ? ((50 <= Randomizer.nextInt(100)) ? newrank : newrank - 1) : 1));
        if (lines == 3) {
            setPotential3(LoadData.getRandomPotential((2 <= newrank) ? ((50 <= Randomizer.nextInt(100)) ? newrank : newrank - 1) : 1));
        }
        return true;
    }

    @Override
    public int getHpR() {
        return hpR;
    }

    public void setHpR(int hp) {
        this.hpR = hp;
    }

    @Override
    public int getMpR() {
        return mpR;
    }

    public void setMpR(int mp) {
        this.mpR = mp;
    }

    // 攻撃速度の書
    @Override
    public int getIncAttackSpeed() {
        return incattackSpeed;
    }

    public void setIncAttackSpeed(int incattackSpeed) {
        this.incattackSpeed = incattackSpeed;
    }
}
