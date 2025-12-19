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

import odin.client.MapleCharacter;
import odin.client.inventory.MaplePet;
import tacos.packet.ops.OpsChangeStat;

/**
 *
 * @author Riremito
 */
public class TacosLastStat {

    private int statmask; // for updating client side stat
    private int skin;
    private int face;
    private int hair;
    private long pet1;
    private int level;
    private int job;
    private int stat_str;
    private int stat_dex;
    private int stat_int;
    private int stat_luk;
    private int stat_hp;
    private int stat_maxhp;
    private int stat_mp;
    private int stat_maxmp;
    private int ap;
    private int sp;
    private int exp;
    private int fame;
    private int meso;
    private long pet2;
    private long pet3;
    private int gasha_exp;

    public TacosLastStat(MapleCharacter chr) {
        this.statmask = 0;
        this.skin = chr.getSkinColor();
        this.face = chr.getFace();
        this.hair = chr.getHair();
        this.pet1 = 0;
        this.level = chr.getLevel();
        this.job = chr.getJob();
        this.stat_str = chr.getStat().getStr();
        this.stat_dex = chr.getStat().getDex();
        this.stat_int = chr.getStat().getInt();
        this.stat_luk = chr.getStat().getLuk();
        this.stat_hp = chr.getStat().getHp();
        this.stat_maxhp = chr.getStat().getMaxHp();
        this.stat_mp = chr.getStat().getMp();
        this.stat_maxmp = chr.getStat().getMaxMp();
        this.ap = chr.getRemainingAp();
        this.sp = chr.getRemainingSp();
        this.exp = chr.getExp();
        this.fame = chr.getFame();
        this.meso = chr.getMeso();
        this.pet2 = 0;
        this.pet3 = 0;
        this.gasha_exp = chr.getGashaEXP();

        // pet
        MaplePet pet = chr.getPet(0);
        if (pet != null && pet.getSummoned()) {
            this.pet1 = pet.getUniqueId();
        }
        pet = chr.getPet(1);
        if (pet != null && pet.getSummoned()) {
            this.pet2 = pet.getUniqueId();
        }
        pet = chr.getPet(2);
        if (pet != null && pet.getSummoned()) {
            this.pet3 = pet.getUniqueId();
        }
    }

    public void update(MapleCharacter chr) {
        if (this.skin != chr.getSkinColor()) {
            this.skin = chr.getSkinColor();
            this.statmask |= OpsChangeStat.CS_SKIN.get();
        }
        if (this.face != chr.getFace()) {
            this.face = chr.getFace();
            this.statmask |= OpsChangeStat.CS_FACE.get();
        }
        if (this.hair != chr.getHair()) {
            this.hair = chr.getHair();
            this.statmask |= OpsChangeStat.CS_HAIR.get();
        }
        if (this.level != chr.getLevel()) {
            this.level = chr.getLevel();
            this.statmask |= OpsChangeStat.CS_LEV.get();
        }
        if (this.job != chr.getJob()) {
            this.job = chr.getJob();
            this.statmask |= OpsChangeStat.CS_JOB.get();
        }
        if (this.stat_str != chr.getStat().getStr()) {
            this.stat_str = chr.getStat().getStr();
            this.statmask |= OpsChangeStat.CS_STR.get();
        }
        if (this.stat_dex != chr.getStat().getDex()) {
            this.stat_dex = chr.getStat().getDex();
            this.statmask |= OpsChangeStat.CS_DEX.get();
        }
        if (this.stat_int != chr.getStat().getInt()) {
            this.stat_int = chr.getStat().getInt();
            this.statmask |= OpsChangeStat.CS_INT.get();
        }
        if (this.stat_luk != chr.getStat().getLuk()) {
            this.stat_luk = chr.getStat().getLuk();
            this.statmask |= OpsChangeStat.CS_LUK.get();
        }
        if (this.stat_hp != chr.getStat().getHp()) {
            this.stat_hp = chr.getStat().getHp();
            this.statmask |= OpsChangeStat.CS_HP.get();
        }
        if (this.stat_maxhp != chr.getStat().getMaxHp()) {
            this.stat_maxhp = chr.getStat().getMaxHp();
            this.statmask |= OpsChangeStat.CS_MHP.get();
        }
        if (this.stat_mp != chr.getStat().getMp()) {
            this.stat_mp = chr.getStat().getMp();
            this.statmask |= OpsChangeStat.CS_MP.get();
        }
        if (this.stat_maxmp != chr.getStat().getMaxMp()) {
            this.stat_maxmp = chr.getStat().getMaxMp();
            this.statmask |= OpsChangeStat.CS_MMP.get();
        }
        if (this.ap != chr.getRemainingAp()) {
            this.ap = chr.getRemainingAp();
            this.statmask |= OpsChangeStat.CS_AP.get();
        }
        if (this.sp != chr.getRemainingSp()) {
            this.sp = chr.getRemainingSp();
            this.statmask |= OpsChangeStat.CS_SP.get();
        }
        if (this.exp != chr.getExp()) {
            this.exp = chr.getExp();
            this.statmask |= OpsChangeStat.CS_EXP.get();
        }
        if (this.fame != chr.getFame()) {
            this.fame = chr.getFame();
            this.statmask |= OpsChangeStat.CS_POP.get();
        }
        if (this.meso != chr.getMeso()) {
            this.meso = chr.getMeso();
            this.statmask |= OpsChangeStat.CS_MONEY.get();
        }
        // v188 ここから+1
        //this.pet2;
        //this.pet3;
        if (this.gasha_exp != chr.getGashaEXP()) {
            this.gasha_exp = chr.getGashaEXP();
            this.statmask |= OpsChangeStat.CS_TEMPEXP.get();
        }
        // pet
        MaplePet new_pet1 = chr.getPet(0);
        MaplePet new_pet2 = chr.getPet(1);
        MaplePet new_pet3 = chr.getPet(2);
        long new_pet1_val = (new_pet1 != null && new_pet1.getSummoned()) ? new_pet1.getUniqueId() : 0;
        long new_pet2_val = (new_pet2 != null && new_pet2.getSummoned()) ? new_pet2.getUniqueId() : 0;
        long new_pet3_val = (new_pet3 != null && new_pet3.getSummoned()) ? new_pet3.getUniqueId() : 0;

        if (this.pet1 != new_pet1_val) {
            this.pet1 = new_pet1_val;
            this.statmask |= OpsChangeStat.CS_PETSN.get();
        }
        if (this.pet2 != new_pet2_val) {
            this.pet2 = new_pet2_val;
            this.statmask |= OpsChangeStat.CS_PETSN2.get();
        }
        if (this.pet3 != new_pet3_val) {
            this.pet3 = new_pet3_val;
            this.statmask |= OpsChangeStat.CS_PETSN3.get();
        }
    }

    public int getStatMask() {
        return this.statmask;
    }

    public void clearStatMask() {
        this.statmask = 0;
    }

}
