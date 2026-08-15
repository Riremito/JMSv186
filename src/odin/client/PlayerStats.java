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
package odin.client;

import odin.constants.GameConstants;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.IItem;
import odin.client.inventory.Equip;
import odin.client.inventory.IEquip;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.wrapper.ResWrapper;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.builder.PB_UserEffect;

public class PlayerStats {

    private WeakReference<MapleCharacter> chr;
    private Map<Integer, Integer> setHandling = new HashMap<>();
    private List<Equip> durabilityHandling = new ArrayList<>(), equipLevelHandling = new ArrayList<>();
    private float shouldHealHP, shouldHealMP;
    public int str, dex, luk, int_, hp, maxhp, mp, maxmp;
    private short localmaxhp, localmaxmp;
    private byte passive_mastery = 0;
    private int localstr, localdex, localluk, localint_;
    private int magic, watk, hands, accuracy;
    public boolean equippedWelcomeBackRing, equippedFairy, hasMeso, hasItem, hasVac, hasClone, hasPartyBonus, Berserk = false, isRecalc = false;
    public int equipmentBonusExp, expMod, dropMod, cashMod, levelBonus;
    public double expBuff, dropBuff, mesoBuff, cashBuff;
    //restore/recovery are separate variables because i dont know jack shit what it even does
    //same with incMesoProp/incRewardProp for now
    public double dam_r, bossdam_r;
    public int recoverHP, recoverMP, mpconReduce, incMesoProp, incRewardProp, DAMreflect, DAMreflect_rate, mpRestore,
            hpRecover, hpRecoverProp, mpRecover, mpRecoverProp, RecoveryUP, incAllskill;
    private float speedMod, jumpMod;
    // Elemental properties
    public int def, element_ice, element_fire, element_light, element_psn;
    public ReentrantLock lock = new ReentrantLock(); //we're getting concurrentmodificationexceptions, but would this slow things down?

    public PlayerStats(final MapleCharacter chr) {
        // TODO, move str/dex/int etc here -_-
        this.chr = new WeakReference<>(chr);
    }

    //POTENTIALS:
    //incMesoProp, incRewardProp
    public final void init() {
        recalcLocalStats();
        relocHeal();
    }

    public final int getStr() {
        return str;
    }

    public final int getDex() {
        return dex;
    }

    public final int getLuk() {
        return luk;
    }

    public final int getInt() {
        return int_;
    }

    public final void setStr(final int str) {
        this.str = str;
        recalcLocalStats();
    }

    public final void setDex(final int dex) {
        this.dex = dex;
        recalcLocalStats();
    }

    public final void setLuk(final int luk) {
        this.luk = luk;
        recalcLocalStats();
    }

    public final void setInt(final int int_) {
        this.int_ = int_;
        recalcLocalStats();
    }

    public final boolean setHp(final int newhp) {
        return setHp(newhp, false);
    }

    public final boolean setHp(int newhp, boolean silent) {
        final int oldHp = hp;
        int thp = newhp;
        if (thp < 0) {
            thp = 0;
        }
        if (thp > localmaxhp) {
            thp = localmaxhp;
        }
        this.hp = (short) thp;

        final MapleCharacter chra = chr.get();
        if (chra != null) {
            if (!silent) {
                chra.updatePartyMemberHP();
            }
            if (oldHp > hp && !chra.isAlive()) {
                chra.playerDead();
            }
        }
        return hp != oldHp;
    }

    public final boolean setMp(final int newmp) {
        final int oldMp = mp;
        int tmp = newmp;
        if (tmp < 0) {
            tmp = 0;
        }
        if (tmp > localmaxmp) {
            tmp = localmaxmp;
        }
        this.mp = (short) tmp;
        return mp != oldMp;
    }

    public final void setMaxHp(final int hp) {
        this.maxhp = hp;
        recalcLocalStats();
    }

    public final void setMaxMp(final int mp) {
        this.maxmp = mp;
        recalcLocalStats();
    }

    public final int getHp() {
        return hp;
    }

    public final int getMaxHp() {
        return maxhp;
    }

    public final int getMp() {
        return mp;
    }

    public final int getMaxMp() {
        return maxmp;
    }

    public final int getTotalDex() {
        return localdex;
    }

    public final int getTotalInt() {
        return localint_;
    }

    public final int getTotalStr() {
        return localstr;
    }

    public final int getTotalLuk() {
        return localluk;
    }

    public final int getTotalMagic() {
        return magic;
    }

    public final double getSpeedMod() {
        return speedMod;
    }

    public final double getJumpMod() {
        return jumpMod;
    }

    public final int getTotalWatk() {
        return watk;
    }

    public final short getCurrentMaxHp() {
        return localmaxhp;
    }

    public final short getCurrentMaxMp() {
        return localmaxmp;
    }

    public final int getHands() {
        return hands;
    }

    public void recalcLocalStats() {
        recalcLocalStats(false);
    }

    public void recalcLocalStats(boolean first_login) {
        final MapleCharacter chra = chr.get();
        if (chra == null) {
            return;
        }
        lock.lock();
        try {
            if (isRecalc) {
                return;
            }
            isRecalc = true;
        } finally {
            lock.unlock();
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int oldmaxhp = localmaxhp;
        int localmaxhp_ = getMaxHp();
        int localmaxmp_ = getMaxMp();
        localdex = getDex();
        localint_ = getInt();
        localstr = getStr();
        localluk = getLuk();
        int speed = 100;
        int jump = 100;
        int percent_hp = 0, percent_mp = 0, percent_str = 0, percent_dex = 0, percent_int = 0, percent_luk = 0, percent_acc = 0, percent_atk = 0, percent_matk = 0;
        int added_sharpeye_rate = 0, added_sharpeye_dmg = 0;
        magic = localint_;
        watk = 0;
        if (chra.getJob() == 500 || (chra.getJob() >= 520 && chra.getJob() <= 522)) {
            watk = 20; //bullet
        } else if (chra.getJob() == 400 || (chra.getJob() >= 410 && chra.getJob() <= 412) || (chra.getJob() >= 1400 && chra.getJob() <= 1412)) {
            watk = 30; //stars
        }
        dam_r = 0.0;
        bossdam_r = 0.0;
        expBuff = 100.0;
        cashBuff = 100.0;
        dropBuff = 100.0;
        mesoBuff = 100.0;
        recoverHP = 0;
        recoverMP = 0;
        mpconReduce = 0;
        incMesoProp = 0;
        incRewardProp = 0;
        DAMreflect = 0;
        DAMreflect_rate = 0;
        hpRecover = 0;
        hpRecoverProp = 0;
        mpRecover = 0;
        mpRecoverProp = 0;
        mpRestore = 0;
        equippedWelcomeBackRing = false;
        equippedFairy = false;
        hasMeso = false;
        hasItem = false;
        hasPartyBonus = false;
        hasVac = false;
        hasClone = false;
        final boolean canEquipLevel = chra.getLevel() >= 120 && !GameConstants.isKOC(chra.getJob());
        equipmentBonusExp = 0;
        RecoveryUP = 0;
        dropMod = 1;
        expMod = 1;
        cashMod = 1;
        levelBonus = 0;
        incAllskill = 0;
        durabilityHandling.clear();
        equipLevelHandling.clear();
        setHandling.clear();
        element_fire = 100;
        element_ice = 100;
        element_light = 100;
        element_psn = 100;
        def = 100;

        for (IItem item : chra.getInventory(MapleInventoryType.EQUIPPED)) {
            final IEquip equip = (IEquip) item;

            if (equip.getPosition() == -11) {
                if (GameConstants.isMagicWeapon(equip.getItemId())) {
                    final Map<String, Integer> eqstat = MapleItemInformationProvider.getInstance().getEquipStats(equip.getItemId());

                    element_fire = eqstat.get("incRMAF");
                    element_ice = eqstat.get("incRMAI");
                    element_light = eqstat.get("incRMAL");
                    element_psn = eqstat.get("incRMAS");
                    def = eqstat.get("elemDefault");
                }
            }
            accuracy += equip.getAcc();
            localmaxhp_ += equip.getHp();
            localmaxmp_ += equip.getMp();
            localdex += equip.getDex();
            localint_ += equip.getInt();
            localstr += equip.getStr();
            localluk += equip.getLuk();
            magic += equip.getMatk() + equip.getInt();
            watk += equip.getWatk();
            speed += equip.getSpeed();
            jump += equip.getJump();
            switch (equip.getItemId()) {
                case 1112427: //cruel, gives crit + OHKO
                    added_sharpeye_rate += 5;
                    added_sharpeye_dmg += 20;
                    break;
                case 1112428: //critical, gives crit + OHKO
                    added_sharpeye_rate += 10;
                    added_sharpeye_dmg += 10;
                    break;
                case 1112429: //magical, gives crit + STUN
                    added_sharpeye_rate += 5;
                    added_sharpeye_dmg += 20;
                    break;
                case 1112127:
                    equippedWelcomeBackRing = true;
                    break;
                case 1122017:
                    equippedFairy = true;
                    break;
                case 1812000:
                    hasMeso = true;
                    break;
                case 1812001:
                    hasItem = true;
                    break;
                default:
                    for (int eb_bonus : GameConstants.Equipments_Bonus) {
                        if (equip.getItemId() == eb_bonus) {
                            equipmentBonusExp += GameConstants.Equipment_Bonus_EXP(eb_bonus);
                            break;
                        }
                    }
                    break;
            } //slow, poison, darkness, seal, freeze
            percent_hp += equip.getHpR();
            percent_mp += equip.getMpR();
            int set = ii.getSetItemID(equip.getItemId());
            if (set > 0) {
                int value = 1;
                if (setHandling.get(set) != null) {
                    value += setHandling.get(set);
                }
                setHandling.put(set, value); //id of Set, number of items to go with the set
            }
            if (equip.getHidden() > 1) {
                if (equip.getDurability() > 0) {
                    durabilityHandling.add((Equip) equip);
                }
                if (canEquipLevel && GameConstants.getMaxLevel(equip.getItemId()) > 0 && (GameConstants.getStatFromWeapon(equip.getItemId()) == null ? (equip.getEquipLevel() <= GameConstants.getMaxLevel(equip.getItemId())) : (equip.getEquipLevel() < GameConstants.getMaxLevel(equip.getItemId())))) {
                    equipLevelHandling.add((Equip) equip);
                }
            }
        }
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        for (IItem item : chra.getInventory(MapleInventoryType.CASH)) {
            if (expMod < 3 && (item.getItemId() == 5211060 || item.getItemId() == 5211050 || item.getItemId() == 5211051 || item.getItemId() == 5211052 || item.getItemId() == 5211053 || item.getItemId() == 5211054)) {
                expMod = 3;//overwrite
            } else if (expMod == 1 && (item.getItemId() == 5210000 || item.getItemId() == 5210001 || item.getItemId() == 5210002 || item.getItemId() == 5210003 || item.getItemId() == 5210004 || item.getItemId() == 5210005 || item.getItemId() == 5211061 || item.getItemId() == 5211000 || item.getItemId() == 5211001 || item.getItemId() == 5211002 || item.getItemId() == 5211003 || item.getItemId() == 5211046 || item.getItemId() == 5211047 || item.getItemId() == 5211048 || item.getItemId() == 5211049)) {
                expMod = 2;
            } else if (expMod == 1 && item.getItemId() == 5210006 && (hour >= 22 || hour <= 2)) {
                expMod = 2;
            } else if (expMod == 1 && item.getItemId() == 5210007 && hour >= 2 && hour <= 6) {
                expMod = 2;
            } else if (expMod == 1 && item.getItemId() == 5210008 && hour >= 6 && hour <= 10) {
                expMod = 2;
            } else if (expMod == 1 && item.getItemId() == 5210009 && hour >= 10 && hour <= 14) {
                expMod = 2;
            } else if (expMod == 1 && item.getItemId() == 5210010 && hour >= 14 && hour <= 18) {
                expMod = 2;
            } else if (expMod == 1 && item.getItemId() == 5210011 && hour >= 18 && hour <= 22) {
                expMod = 2;
            }
            if (dropMod == 1) {
                if (item.getItemId() == 5360009 || item.getItemId() == 5360010 || item.getItemId() == 5360011 || item.getItemId() == 5360012 || item.getItemId() == 5360013 || item.getItemId() == 5360014 || item.getItemId() == 5360017 || item.getItemId() == 5360050 || item.getItemId() == 5360053 || item.getItemId() == 5360042 || item.getItemId() == 5360052) {
                    dropMod = 2;
                } else if (item.getItemId() == 5360000 && hour >= 0 && hour <= 6) {
                    dropMod = 2;
                } else if (item.getItemId() == 5360001 && hour >= 6 && hour <= 12) {
                    dropMod = 2;
                } else if (item.getItemId() == 5360002 && hour >= 12 && hour <= 18) {
                    dropMod = 2;
                } else if (item.getItemId() == 5360003 && hour >= 18 && hour <= 24) {
                    dropMod = 2;
                }
            }
            if (item.getItemId() == 5650000) {
                hasPartyBonus = true;
            } else if (item.getItemId() == 5590001) {
                levelBonus = 10;
            } else if (levelBonus == 0 && item.getItemId() == 5590000) {
                levelBonus = 5;
            }
        }
        for (IItem item : chra.getInventory(MapleInventoryType.ETC)) { //omfg;
            switch (item.getItemId()) {
                case 4030003:
                    hasVac = true;
                    break;
                case 4030004:
                    hasClone = true;
                    break;
                case 4030005:
                    cashMod = 2;
                    break;
            }
        }
        magic += chra.getSkillLevel(SkillFactory.getSkill(22000000));
        //dam_r += (chra.getJob() >= 430 && chra.getJob() <= 434 ? 70 : 0); //leniency on upper stab
        this.localstr += (percent_str * localstr) / 100f;
        this.localdex += (percent_dex * localdex) / 100f;
        final int before_ = localint_;
        this.localint_ += (percent_int * localint_) / 100f;
        this.magic += localint_ - before_;
        this.localluk += (percent_luk * localluk) / 100f;
        this.accuracy += (percent_acc * accuracy) / 100f;
        this.watk += (percent_atk * watk) / 100f;
        this.magic += (percent_matk * magic) / 100f; //or should this go before
        localmaxhp_ += (percent_hp * localmaxhp_) / 100f;
        localmaxmp_ += (percent_mp * localmaxmp_) / 100f;
        magic = Math.min(magic, 1999); //buffs can make it higher

        switch (chra.getJob()) {
            case 322: { // Crossbowman
                final ISkill expert = SkillFactory.getSkill(3220004);
                final int boostLevel = chra.getSkillLevel(expert);
                if (boostLevel > 0) {
                    watk += expert.getEffect(boostLevel).getX();
                }
                break;
            }
            case 312: { // Bowmaster
                final ISkill expert = SkillFactory.getSkill(3120005);
                final int boostLevel = chra.getSkillLevel(expert);
                if (boostLevel > 0) {
                    watk += expert.getEffect(boostLevel).getX();
                }
                break;
            }
            case 211:
            case 212: { // IL
                final ISkill amp = SkillFactory.getSkill(2110001);
                final int level = chra.getSkillLevel(amp);
                if (level > 0) {
                    dam_r *= amp.getEffect(level).getY() / 100.0;
                    bossdam_r *= amp.getEffect(level).getY() / 100.0;
                }
                break;
            }
            case 221:
            case 222: { // IL
                final ISkill amp = SkillFactory.getSkill(2210001);
                final int level = chra.getSkillLevel(amp);
                if (level > 0) {
                    dam_r *= amp.getEffect(level).getY() / 100.0;
                    bossdam_r *= amp.getEffect(level).getY() / 100.0;
                }
                break;
            }
            case 1211:
            case 1212: { // flame
                final ISkill amp = SkillFactory.getSkill(12110001);
                final int level = chra.getSkillLevel(amp);
                if (level > 0) {
                    dam_r *= amp.getEffect(level).getY() / 100.0;
                    bossdam_r *= amp.getEffect(level).getY() / 100.0;
                }
                break;
            }
            case 2215:
            case 2216:
            case 2217:
            case 2218: {
                final ISkill amp = SkillFactory.getSkill(22150000);
                final int level = chra.getSkillLevel(amp);
                if (level > 0) {
                    dam_r *= amp.getEffect(level).getY() / 100.0;
                    bossdam_r *= amp.getEffect(level).getY() / 100.0;
                }
                break;
            }
            case 2112: { // Aran
                final ISkill expert = SkillFactory.getSkill(21120001);
                final int boostLevel = chra.getSkillLevel(expert);
                if (boostLevel > 0) {
                    watk += expert.getEffect(boostLevel).getX();
                }
                break;
            }
        }
        final ISkill blessoffairy = SkillFactory.getSkill(GameConstants.getBOF_ForJob(chra.getJob()));
        final int boflevel = chra.getSkillLevel(blessoffairy);
        if (boflevel > 0) {
            watk += blessoffairy.getEffect(boflevel).getX();
            magic += blessoffairy.getEffect(boflevel).getY();
            accuracy += blessoffairy.getEffect(boflevel).getX();
        }
        final ISkill bx = SkillFactory.getSkill(1320006);
        if (chra.getSkillLevel(bx) > 0) {
            dam_r *= bx.getEffect(chra.getSkillLevel(bx)).getDamage() / 100.0;
            bossdam_r *= bx.getEffect(chra.getSkillLevel(bx)).getDamage() / 100.0;
        }
        if (speed > 140) {
            speed = 140;
        }
        if (jump > 123) {
            jump = 123;
        }
        speedMod = speed / 100.0f;
        jumpMod = jump / 100.0f;
        hands = this.localdex + this.localint_ + this.localluk;

        localmaxhp = (short) Math.min(30000, Math.abs(Math.max(-30000, localmaxhp_)));
        localmaxmp = (short) Math.min(30000, Math.abs(Math.max(-30000, localmaxmp_)));

        if (first_login) {
            chra.silentEnforceMaxHpMp();
        } else {
            chra.enforceMaxHpMp();
        }

        if (oldmaxhp != 0 && oldmaxhp != localmaxhp) {
            chra.updatePartyMemberHP();
        }
        lock.lock();
        try {
            isRecalc = false;
        } finally {
            lock.unlock();
        }
    }

    public boolean checkEquipLevels(final MapleCharacter chr, int gain) {
        boolean changed = false;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        List<Equip> all = new ArrayList<>(equipLevelHandling);
        for (Equip eq : all) {
            int lvlz = eq.getEquipLevel();
            eq.setItemEXP(eq.getItemEXP() + gain);

            if (eq.getEquipLevel() > lvlz) { //lvlup
                for (int i = eq.getEquipLevel() - lvlz; i > 0; i--) {
                    //now for the equipment increments...
                    final Map<Integer, Map<String, Integer>> inc = ii.getEquipIncrements(eq.getItemId());
                    if (inc != null && inc.containsKey(lvlz + i)) { //flair = 1
                        eq = ii.levelUpEquip(eq, inc.get(lvlz + i));
                    }
                    //UGH, skillz
                    if (GameConstants.getStatFromWeapon(eq.getItemId()) == null) {
                        final Map<Integer, List<Integer>> ins = ii.getEquipSkills(eq.getItemId());
                        if (ins != null && ins.containsKey(lvlz + i)) {
                            for (Integer z : ins.get(lvlz + i)) {
                                if (Math.random() < 0.1) { //10% chance dood
                                    final ISkill skil = SkillFactory.getSkill(z);
                                    if (skil != null && skil.canBeLearnedBy(chr.getJob()) && chr.getSkillLevel(skil) < chr.getMasterLevel(skil)) { //dont go over masterlevel :D
                                        chr.changeSkillLevel(skil, (byte) (chr.getSkillLevel(skil) + 1), chr.getMasterLevel(skil));
                                    }
                                }
                            }
                        }
                    }
                }
                changed = true;
            }
            chr.SendPacket(ResWrapper.addInventorySlot(MapleInventoryType.EQUIPPED, eq.copy()));
        }
        if (changed) {
            chr.equipChanged();

            PB_UserEffect pb = PB_UserEffect.builder()
                    .player(chr)
                    .build();
            chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_ItemLevelUp));
            chr.getMap().broadcastMessage(chr, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_ItemLevelUp, pb), false);
        }
        return changed;
    }

    public boolean checkEquipDurabilitys(final MapleCharacter chr, int gain) {
        for (Equip item : durabilityHandling) {
            item.setDurability(item.getDurability() + gain);
            if (item.getDurability() < 0) { //shouldnt be less than 0
                item.setDurability(0);
            }
        }
        List<Equip> all = new ArrayList<>(durabilityHandling);
        for (Equip eqq : all) {
            if (eqq.getDurability() == 0) { //> 0 went to negative
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    chr.updateInv();
                    chr.getClient().getSession().write(ResWrapper.getShowInventoryFull());
                    return false;
                }
                durabilityHandling.remove(eqq);
                final short pos = chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot();
                MapleInventoryManipulator.unequip(chr.getClient(), eqq.getPosition(), pos);
                chr.SendPacket(ResWrapper.addInventorySlot(MapleInventoryType.EQUIP, eqq));
            } else {
                chr.SendPacket(ResWrapper.addInventorySlot(MapleInventoryType.EQUIPPED, eqq.copy()));
            }
        }
        return true;
    }

    public final byte passive_mastery() {
        return passive_mastery; //* 5 + 10 for mastery %
    }

    public final void relocHeal() {
        final MapleCharacter chra = chr.get();
        if (chra == null) {
            return;
        }
        final int playerjob = chra.getJob();

        shouldHealHP = 10 + recoverHP; // Reset
        shouldHealMP = 3 + mpRestore + recoverMP; // i think

        if (GameConstants.isJobFamily(200, playerjob)) { // Improving MP recovery
            shouldHealMP += ((float) ((float) chra.getSkillLevel(SkillFactory.getSkill(2000000)) / 10) * chra.getLevel());

        } else if (GameConstants.isJobFamily(111, playerjob)) {
            final ISkill effect = SkillFactory.getSkill(1110000); // Improving MP Recovery
            final int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealMP += effect.getEffect(lvl).getMp();
            }

        } else if (GameConstants.isJobFamily(121, playerjob)) {
            final ISkill effect = SkillFactory.getSkill(1210000); // Improving MP Recovery
            final int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealMP += effect.getEffect(lvl).getMp();
            }

        } else if (GameConstants.isJobFamily(1111, playerjob)) {
            final ISkill effect = SkillFactory.getSkill(11110000); // Improving MP Recovery
            final int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealMP += effect.getEffect(lvl).getMp();
            }

        } else if (GameConstants.isJobFamily(410, playerjob)) {
            final ISkill effect = SkillFactory.getSkill(4100002); // Endure
            final int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealHP += effect.getEffect(lvl).getHp();
                shouldHealMP += effect.getEffect(lvl).getMp();
            }

        } else if (GameConstants.isJobFamily(420, playerjob)) {
            final ISkill effect = SkillFactory.getSkill(4200001); // Endure
            final int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealHP += effect.getEffect(lvl).getHp();
                shouldHealMP += effect.getEffect(lvl).getMp();
            }
        }
        if (chra.isGM()) {
            shouldHealHP += 1000;
            shouldHealMP += 1000;
        }
        if (chra.getChair() != 0) { // Is sitting on a chair.
            shouldHealHP += 99; // Until the values of Chair heal has been fixed,
            shouldHealMP += 99; // MP is different here, if chair data MP = 0, heal + 1.5
        } else { // Because Heal isn't multipled when there's a chair :)
            final float recvRate = chra.getMap().getRecoveryRate();
            shouldHealHP *= recvRate;
            shouldHealMP *= recvRate;
        }
        shouldHealHP *= 2; // To avoid any problem with bathrobe / Sauna >.<
        shouldHealMP *= 2; // 1.5
    }

    public final int getSkillByJob(final int skillID, final int job) {
        if (GameConstants.isKOC(job)) {
            return skillID + 10000000;
        } else if (GameConstants.isAran(job)) {
            return skillID + 20000000;
        } else if (GameConstants.isEvan(job)) {
            return skillID + 20010000;
        } else if (GameConstants.isResist(job)) {
            return skillID + 30000000;
        }
        return skillID;
    }

}
