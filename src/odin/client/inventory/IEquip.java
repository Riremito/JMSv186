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

public interface IEquip extends IItem {

    public static enum ScrollResult {

        SUCCESS, FAIL, CURSE
    }
    public static final int ARMOR_RATIO = 350000;
    public static final int WEAPON_RATIO = 700000;

    int getUpgradeSlots();

    int getLevel();

    public int getViciousHammer();

    public int getItemEXP();

    public int getExpPercentage();

    public int getEquipLevel();

    public int getEquipExp();

    public int getEquipExpForLevel();

    public int getBaseLevel();

    public int getStr();

    public int getDex();

    public int getInt();

    public int getLuk();

    public int getHp();

    public int getMp();

    public int getWatk();

    public int getMatk();

    public int getWdef();

    public int getMdef();

    public int getAcc();

    public int getAvoid();

    public int getHands();

    public int getSpeed();

    public int getJump();

    public int getDurability();

    public int getEnhance();

    public int getRank();

    public int getHidden();

    public int getPotential1();

    public int getPotential2();

    public int getPotential3();

    public int getHpR();

    public int getMpR();

    public int getIncAttackSpeed();
}
