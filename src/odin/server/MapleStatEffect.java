package odin.server;

import odin.client.Skill;
import tacos.wz.WzXML;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import tacos.server.map.object.TacosMonster;
import tacos.server.map.object.TacosMist;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.EnumMap;
import tacos.client.TacosCharacter.PlayerStats;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventory;
import odin.client.inventory.MapleInventoryType;
import tacos.packet.ops.OpsMobStat;
import tacos.config.ContentState;
import odin.constants.GameConstants;
import tacos.packet.ops.OpsSecondaryStat;
import tacos.packet.ops.OpsSkill_BB;
import tacos.packet.ops.OpsUserEffect;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.wz.StatEffectData;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.builder.PB_UserEffect;

public class MapleStatEffect {

    private byte mobCount;
    private byte level;
    private short hp;
    private short mp;
    private short watk;
    private short matk;
    private short wdef;
    private short mdef;
    private short acc;
    private short avoid;
    private short hands;
    private short speed;
    private short jump;
    private short mpCon;
    private short hpCon;
    private short damage;
    private short prop;
    private double hpR;
    private double mpR;
    private int duration;
    private int sourceid;
    private int moveTo;
    private int x;
    private int y;
    private int itemCon;
    private int itemConNo;
    private int cooldown;
    private int morphId = 0;
    private int expinc;
    private int cp;
    private int exp; // gashaEXP, consume 237
    private boolean overTime;
    private boolean skill;
    private final Map<OpsMobStat, Integer> monsterStatus = new EnumMap<>(OpsMobStat.class);
    private Point lt;
    private Point rb;
    private ArrayList<SimpleImmutableEntry<OpsSecondaryStat, Integer>> oss = new ArrayList<>();

    public static MapleStatEffect loadSkillEffectFromData(StatEffectData source, int skillid, boolean overtime, byte level) {
        return loadFromData(source, skillid, true, overtime, level);
    }

    public static MapleStatEffect loadItemEffectFromData(StatEffectData source, int itemid) {
        return loadFromData(source, itemid, false, false, (byte) 1);
    }

    private void addStat(OpsSecondaryStat stat, int value) {
        oss.add(new SimpleImmutableEntry<>(stat, value));
    }

    // the secondary stats this effect sets
    private void initSecondaryStats() {
        if (watk != 0) {
            addStat(OpsSecondaryStat.CTS_PAD, watk);
        }
        if (wdef != 0) {
            addStat(OpsSecondaryStat.CTS_PDD, wdef);
        }
        if (matk != 0) {
            addStat(OpsSecondaryStat.CTS_MAD, matk);
        }
        if (mdef != 0) {
            addStat(OpsSecondaryStat.CTS_MDD, mdef);
        }
        if (acc != 0) {
            addStat(OpsSecondaryStat.CTS_ACC, acc);
        }
        if (avoid != 0) {
            addStat(OpsSecondaryStat.CTS_EVA, avoid);
        }
        if (hands != 0) {
            addStat(OpsSecondaryStat.CTS_Craft, hands); // not coded
        }
        if (speed != 0) {
            addStat(OpsSecondaryStat.CTS_Speed, speed);
        }
        if (jump != 0) {
            addStat(OpsSecondaryStat.CTS_Jump, jump);
        }

        switch (OpsSkill_BB.find(sourceid)) {
            case MAGICIAN_MAGIC_GUARD:
            case FLAMEWIZARD_MAGIC_GUARD:
            case EVAN_MAGIC_GUARD: {
                addStat(OpsSecondaryStat.CTS_MagicGuard, x);
                return;
            }
            case ROGUE_DARK_SIGHT:
            case DUAL4_ADVANCED_DARK_SIGHT:
            case NIGHTWALKER_DARK_SIGHT: {
                addStat(OpsSecondaryStat.CTS_DarkSight, x);
                return;
            }
            case FIGHTER_WEAPON_BOOSTER:
            case PAGE_WEAPON_BOOSTER:
            case SPEARMAN_WEAPON_BOOSTER:
            case MAGE1_MAGIC_BOOSTER:
            case MAGE2_MAGIC_BOOSTER:
            case HUNTER_BOW_BOOSTER:
            case CROSSBOWMAN_CROSSBOW_BOOSTER:
            case ASSASSIN_JAVELIN_BOOSTER:
            case THIEF_DAGGER_BOOSTER:
            case DUAL1_DUAL_BOOSTER:
            case INFIGHTER_KNUCKLE_BOOSTER:
            case GUNSLINGER_GUN_BOOSTER:
            case STRIKER_KNUCKLE_BOOSTER:
            case SOULMASTER_SWORD_BOOSTER:
            case FLAMEWIZARD_MAGIC_BOOSTER:
            case WINDBREAKER_BOW_BOOSTER:
            case NIGHTWALKER_JAVELIN_BOOSTER:
            case ARAN_POLEARM_BOOSTER:
            case EVAN_MAGIC_BOOSTER:
            case BMAGE_STAFF_BOOSTER:
            case WILDHUNTER_CROSSBOW_BOOSTER:
            case MECHANIC_BOOSTER: {
                addStat(OpsSecondaryStat.CTS_Booster, x);
                return;
            }
            case FIGHTER_POWER_GUARD:
            case PAGE_POWER_GUARD: {
                addStat(OpsSecondaryStat.CTS_PowerGuard, x);
                return;
            }
            case SPEARMAN_HYPER_BODY: {
                addStat(OpsSecondaryStat.CTS_MaxHP, x);
                addStat(OpsSecondaryStat.CTS_MaxMP, y);
                return;
            }
            case HUNTER_SOUL_ARROW_BOW:
            case CROSSBOWMAN_SOUL_ARROW_CROSSBOW:
            case WINDBREAKER_SOUL_ARROW_BOW: {
                addStat(OpsSecondaryStat.CTS_SoulArrow, x);
                return;
            }
            case HERMIT_SHADOW_PARTNER:
            case THIEFMASTER_SHADOW_PARTNER:
            case NIGHTWALKER_SHADOW_PARTNER: {
                addStat(OpsSecondaryStat.CTS_ShadowPartner, x);
                return;
            }
            case BOWMASTER_SHARP_EYES:
            case CROSSBOWMASTER_SHARP_EYES:
            case WILDHUNTER_SHARP_EYES:
            case NOVICE_SHARP_EYES:
            case NOBLESSE_SHARP_EYES:
            case EVANJR_SHARP_EYES:
            case CITIZEN_SHARP_EYES: {
                addStat(OpsSecondaryStat.CTS_SharpEyes, (x << 8) | y);
                return;
            }
            case HERO_MAPLE_HERO:
            case PALADIN_MAPLE_HERO:
            case DARKKNIGHT_MAPLE_HERO:
            case ARCHMAGE1_MAPLE_HERO:
            case ARCHMAGE2_MAPLE_HERO:
            case BISHOP_MAPLE_HERO:
            case BOWMASTER_MAPLE_HERO:
            case CROSSBOWMASTER_MAPLE_HERO:
            case NIGHTLORD_MAPLE_HERO:
            case SHADOWER_MAPLE_HERO:
            case DUAL5_MAPLE_HERO:
            case VIPER_MAPLE_HERO:
            case CAPTAIN_MAPLE_HERO:
            case ARAN_MAPLE_HERO:
            case EVAN_MAPLE_HERO:
            case BMAGE_MAPLE_HERO:
            case WILDHUNTER_MAPLE_HERO:
            case MECHANIC_MAPLE_HERO: {
                addStat(OpsSecondaryStat.CTS_BasicStatUp, x);
                return;
            }
            case BOWMASTER_HAMSTRING: {
                addStat(OpsSecondaryStat.CTS_HamString, x);
                return;
            }
            case BOWMASTER_CONCENTRATION: {
                addStat(OpsSecondaryStat.CTS_Concentration, x);
                return;
            }
            case HERMIT_MESO_UP: {
                addStat(OpsSecondaryStat.CTS_MesoUp, x);
                return;
            }
            case NIGHTLORD_SPIRIT_JAVELIN: {
                addStat(OpsSecondaryStat.CTS_SpiritJavelin, 0);
                return;
            }
            case BMAGE_AURA_DARK: {
                addStat(OpsSecondaryStat.CTS_DarkAura, x);
                return;
            }
            case BMAGE_AURA_BLUE: {
                addStat(OpsSecondaryStat.CTS_BlueAura, x);
                return;
            }
            case BMAGE_AURA_YELLOW: {
                addStat(OpsSecondaryStat.CTS_YellowAura, x);
                return;
            }
            case BMAGE_CYCLONE: {
                addStat(OpsSecondaryStat.CTS_Cyclone, x);
                return;
            }
            case NOVICE_MONSTER_RIDING: {
                addStat(OpsSecondaryStat.CTS_RideVehicle, 1);
                return;
            }
            default: {
                break;
            }
        }
    }

    // the statuses this skill puts on monsters (applyMonsterBuff or on hit)
    private void initMonsterStatus() {
        switch (sourceid) {
            case 4001002: // disorder
            case 14001002: // cygnus disorder
            case 1201006: { // threaten
                monsterStatus.put(OpsMobStat.MS_PAD, x);
                monsterStatus.put(OpsMobStat.MS_PDR, y);
                break;
            }
            case 1211002: // charged blow
            case 1111008: // shout
            case 4211002: // assaulter
            case 3101005: // arrow bomb
            case 1111005: // coma: sword
            case 1111006: // coma: axe
            case 4221007: // boomerang step
            case 5101002: // Backspin Blow
            case 5101003: // Double Uppercut
            case 5121004: // Demolition
            case 5121005: // Snatch
            case 5121007: // Barrage
            case 5201004: // pirate blank shot
            case 4121008: // Ninja Storm
            case 22151001:
            case 4201004: // steal
            case 33101001:
            case 33101002:
            case 32111010:
            case 32121004:
            case 33111002:
            case 33121002:
            case 35101003:
            case 35111015:
            case 5111002: // energy blast
            case 15101005:
            case 4331005:
            case 3211005: // golden eagle
            case 3111005: // golden hawk
            case 33111005:
            case 35111002: {
                monsterStatus.put(OpsMobStat.MS_Stun, 1);
                break;
            }
            case 4321002: {
                monsterStatus.put(OpsMobStat.MS_Darkness, 1);
                break;
            }
            case 4221003:
            case 4121003:
            case 33121005: { // taunt
                monsterStatus.put(OpsMobStat.MS_Showdown, x);
                monsterStatus.put(OpsMobStat.MS_MDR, x);
                monsterStatus.put(OpsMobStat.MS_PDR, x);
                break;
            }
            case 2201004: // cold beam
            case 2211002: // ice strike
            case 3211003: // blizzard
            case 2211006: // il elemental compo
            case 2221007: // Blizzard
            case 5211005: // Ice Splitter
            case 2121006: // Paralyze
            case 21120006: // Tempest
            case 22121000:
            case 3221005: // frostprey
            case 2121005: { // elquines
                monsterStatus.put(OpsMobStat.MS_Freeze, 1);
                break;
            }
            case 2101003: // fp slow
            case 2201003: // il slow
            case 12101001:
            case 22141003: // Slow
            case 3121007: { // Hamstring
                monsterStatus.put(OpsMobStat.MS_Speed, x);
                break;
            }
            case 2101005: // poison breath
            case 2111006: // fp elemental compo
            case 2121003: // ice demon
            case 2221003: // fire demon
            case 3111003: // inferno
            case 22161002: { // phantom imprint
                monsterStatus.put(OpsMobStat.MS_Poison, 1);
                break;
            }
            case 4121004: // Ninja ambush
            case 4221004: {
                monsterStatus.put(OpsMobStat.MS_Ambush, (int) damage);
                break;
            }
            case 2311005: { // doom
                monsterStatus.put(OpsMobStat.MS_Doom, 1);
                break;
            }
            case 2211004: // il seal
            case 2111004: // fp seal
            case 12111002: { // cygnus seal
                monsterStatus.put(OpsMobStat.MS_Seal, 1);
                break;
            }
            case 4111003: // shadow web
            case 14111001: {
                monsterStatus.put(OpsMobStat.MS_Web, 1);
                break;
            }
            case 3221006: // Blind
            case 33111004: {
                monsterStatus.put(OpsMobStat.MS_ACC, x);
                break;
            }
            default: {
                break;
            }
        }
    }

    private static MapleStatEffect loadFromData(StatEffectData source, int sourceid, boolean skill, boolean overTime, byte level) {
        MapleStatEffect ret = new MapleStatEffect();
        ret.sourceid = sourceid;
        ret.skill = skill;
        ret.level = level;
        if (source == null) {
            return ret;
        }
        ret.duration = source.getTime();
        ret.hp = (short) source.getHp();
        ret.hpR = source.getHpR() / 100.0;
        ret.mp = (short) source.getMp();
        ret.mpR = source.getMpR() / 100.0;
        ret.mpCon = (short) source.getMpCon();
        ret.hpCon = (short) source.getHpCon();
        ret.prop = (short) source.getProp();
        ret.cooldown = source.getCooltime();
        ret.expinc = source.getExpinc();
        ret.morphId = source.getMorph();
        ret.cp = source.getCp();
        ret.mobCount = (byte) source.getMobCount();
        ret.exp = source.getExp();

        if (skill) {
            switch (sourceid) {
                case 1100002:
                case 1100003:
                case 1200002:
                case 1200003:
                case 1300002:
                case 1300003:
                case 3100001:
                case 3200001:
                case 11101002:
                case 13101002:
                    ret.mobCount = 6;
                    break;
            }
        }

        if (!ret.skill && ret.duration > -1) {
            ret.overTime = true;
        } else {
            ret.duration *= 1000; // items have their times stored in ms, of course
            ret.overTime = overTime || ret.isMorph() || ret.isPirateMorph() || ret.isFinalAttack();
        }
        ret.watk = (short) source.getPad();
        ret.wdef = (short) source.getPdd();
        ret.matk = (short) source.getMad();
        ret.mdef = (short) source.getMdd();
        ret.acc = (short) source.getAcc();
        ret.avoid = (short) source.getEva();
        ret.speed = (short) source.getSpeed();
        ret.jump = (short) source.getJump();

        ret.lt = source.getLt();
        ret.rb = source.getRb();

        ret.x = source.getX();
        ret.y = source.getY();
        ret.damage = (short) source.getDamage();
        ret.itemCon = source.getItemCon();
        ret.itemConNo = source.getItemConNo();
        ret.moveTo = source.getMoveTo();

        ret.initSecondaryStats();
        if (skill) {
            ret.initMonsterStatus();
        }
        return ret;
    }

    public void applyPassive(TacosCharacter applyto, Object obj) {
        if (makeChanceResult()) {
            switch (sourceid) { // MP eater
                case 2100000:
                case 2200000:
                case 2300000:
                    if (!(obj instanceof TacosMonster)) {
                        return;
                    }
                    TacosMonster mob = (TacosMonster) obj; // x is absorb percentage
                    if (!mob.getStats().isBoss()) {
                        int absorbMp = Math.min((int) (mob.getMobMaxMp() * (getX() / 100.0)), mob.getMp());
                        if (absorbMp > 0) {
                            mob.setMp(mob.getMp() - absorbMp);
                            applyto.getStat().setMp((short) (applyto.getStat().getMp() + absorbMp));
                            //applyto.SendPacket(WrapCUserLocal.EffectLocal(OpsUserEffect.UserEffect_SkillUse, sourceid));
                            //applyto.getMap().broadcastMessage(applyto, WrapCUserRemote.EffectRemote(OpsUserEffect.UserEffect_SkillUse, applyto, sourceid), false);
                        }
                    }
                    break;
            }
        }
    }

    public boolean applyTo(TacosCharacter chr) {
        return applyTo(chr, chr, true, null, duration);
    }

    public boolean applyTo(TacosCharacter chr, Point pos) {
        return applyTo(chr, chr, true, pos, duration);
    }

    private boolean applyTo(TacosCharacter applyfrom, TacosCharacter applyto, boolean primary, Point pos) {
        return applyTo(applyfrom, applyto, primary, pos, duration);
    }

    public boolean applyTo(TacosCharacter applyfrom, TacosCharacter applyto, boolean primary, Point pos, int newDuration) {
        TacosMap map = applyfrom.getMap();
        if (isHeal() && (applyfrom.getMapId() == 749040100 || applyto.getMapId() == 749040100)) {
            return false;
        } else if (sourceid == 33101004 && map.isTown()) {
            applyfrom.Notice("You may not use this skill in towns.");
            applyfrom.updateStat();
            return false; //not supposed to
        }
        int hpchange = calcHPChange(applyfrom, primary);
        int mpchange = calcMPChange(applyfrom, primary);

        PlayerStats stat = applyto.getStat();
        if (primary) {
            if (itemConNo != 0) {
                if (ContentState.CS_LOCK_LOSING_STONE.get()) {
                    // do nothing
                } else {
                    MapleInventoryManipulator.removeById(applyto, GameConstants.getInventoryType(itemCon), itemCon, itemConNo, false, true);
                }
            }
        } else if (!primary && isResurrection()) {
            hpchange = stat.getMaxHp();
            applyto.setMoveAction(0); //TODO fix death bug, player doesnt spawn on other screen
        }
        if (isMPRecovery()) {
            int toDecreaseHP = ((stat.getMaxHp() / 100) * 10);
            if (stat.getHp() > toDecreaseHP) {
                hpchange += -toDecreaseHP; // -10% of max HP
            } else {
                hpchange = stat.getHp() == 1 ? 0 : stat.getHp() - 1;
            }
            mpchange += ((toDecreaseHP / 100) * getY());
        }
        if (hpchange != 0) {
            if (hpchange < 0 && (-hpchange) > stat.getHp()) {
                return false;
            }
            stat.setHp(stat.getHp() + hpchange);
        }
        if (mpchange != 0) {
            if (mpchange < 0 && (-mpchange) > stat.getMp()) {
                return false;
            }
            //short converting needs math.min cuz of overflow
            stat.setMp(stat.getMp() + mpchange);
        }

        applyto.sendStatChanged(true);

        if (expinc != 0) {
            applyto.gainExp(expinc, true, true, false);
            applyto.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_ItemLevelUp));
        } else if (isSpiritClaw()) {
            MapleInventory use = applyto.getInventory(MapleInventoryType.USE);
            Item item;
            for (int i = 0; i < use.getSlotLimit(); i++) { // impose order...
                item = use.getItem((byte) i);
                if (item != null && GameConstants.isThrowingStar(item.getItemId()) && item.getQuantity() >= 200) {
                    if (ContentState.CS_LOCK_LOSING_THRWOING.get()) {
                        // do nothing
                    } else {
                        MapleInventoryManipulator.removeById(applyto, MapleInventoryType.USE, item.getItemId(), 200, false, true);
                    }
                    break;
                }
            }
        } else if (cp != 0 && applyto.getCarnivalParty() != null) {
            applyto.getCarnivalParty().addCP(applyto, cp);
            applyto.CPUpdate(false, applyto.getAvailableCP(), applyto.getTotalCP(), 0);
            for (TacosCharacter chr : applyto.getMap().getAllPlayers()) {
                chr.CPUpdate(true, applyto.getCarnivalParty().getAvailableCP(), applyto.getCarnivalParty().getTotalCP(), applyto.getCarnivalParty().getTeam());
            }
        }
        if (primary) {
            if ((overTime || isHeal()) && !isEnergyCharge()) {
                applyBuff(applyfrom, newDuration);
            }
            if (isMonsterBuff()) {
                applyMonsterBuff(applyfrom);
            }
        }
        if (isMist()) {
            Rectangle bounds = calculateBoundingBox(pos != null ? pos : new Point(applyfrom.getPosition()), applyfrom.isFacingLeft());
            TacosMist mist = new TacosMist(bounds, applyfrom.getId(), applyfrom.getSkillLevel(WzXML.SKILL.getSkill().get(getSourceId())), getDuration(), null, this);
            map.addMist(mist);

        } else if (isTimeLeap()) {
            applyto.getCoolTime().timeLeap();
        }

        return true;
    }

    public boolean applyReturnScroll(TacosCharacter applyto) {
        if (moveTo != -1) {
            TacosMap target;
            if (moveTo == 999999999) {
                target = applyto.getMap().getReturnMap();
            } else {
                target = applyto.findMap(moveTo);
            }
            applyto.changeMapPortal(target, target.getPortal(0));
            return true;
        }
        return false;
    }

    private boolean isSoulStone() {
        return skill && sourceid == 22181003;
    }

    private void applyBuff(TacosCharacter applyfrom, int newDuration) {
        TacosMap map = applyfrom.getMap();
        if (isSoulStone()) {
            if (applyfrom.getParty() != null) {
                int membrs = 0;
                for (TacosCharacter chr : map.getAllPlayers()) {
                    if (chr.getParty() != null && chr.getParty().equals(applyfrom.getParty()) && chr.isAlive()) {
                        membrs++;
                    }
                }
                List<TacosCharacter> awarded = new ArrayList<>();
                while (awarded.size() < Math.min(membrs, y)) {
                    for (TacosCharacter chr : map.getAllPlayers()) {
                        if (chr.isAlive() && chr.getParty().equals(applyfrom.getParty()) && !awarded.contains(chr) && Randomizer.nextInt(y) == 0) {
                            awarded.add(chr);
                        }
                    }
                }
                for (TacosCharacter chr : awarded) {
                    applyTo(applyfrom, chr, false, null, newDuration);
                    PB_UserEffect pb = PB_UserEffect.builder()
                            .player(chr)
                            .skill_id(sourceid)
                            .build();
                    chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_SkillAffected, pb));
                    chr.getMap().splitSendPacket(chr, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_SkillAffected, pb), chr.getId());
                }
            }
        } else if (isPartyBuff() && (applyfrom.getParty() != null || isGmBuff())) {
            Rectangle bounds = calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
            List<TacosCharacter> affecteds = map.getPlayersInRect(bounds);

            for (TacosCharacter affected : affecteds) {
                if (affected != applyfrom && (isGmBuff() || applyfrom.getParty().equals(affected.getParty()))) {
                    if ((isResurrection() && !affected.isAlive()) || (!isResurrection() && affected.isAlive())) {
                        applyTo(applyfrom, affected, false, null, newDuration);

                        PB_UserEffect pb = PB_UserEffect.builder()
                                .player(affected)
                                .skill_id(sourceid)
                                .build();
                        affected.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_SkillAffected, pb));
                        affected.getMap().splitSendPacket(affected, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_SkillAffected, pb), affected.getId());
                    }
                    if (isTimeLeap()) {
                        affected.getCoolTime().timeLeap();
                    }
                }
            }
        }
    }

    private void applyMonsterBuff(TacosCharacter applyfrom) {
        Rectangle bounds = calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
        int i = 0;

        for (TacosMonster monster : applyfrom.getMap().getMonstersInRect(bounds)) {
            if (!monsterStatus.isEmpty() && makeChanceResult()) {
                monster.applyStatus(applyfrom, monsterStatus, sourceid, getDuration());
            }
            i++;
            if (i >= mobCount) {
                break;
            }
        }
    }

    private Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
        if (lt == null || rb == null) {
            return new Rectangle(posFrom.x, posFrom.y, facingLeft ? 1 : -1, 1);
        }
        Point mylt;
        Point myrb;
        if (facingLeft) {
            mylt = new Point(lt.x + posFrom.x, lt.y + posFrom.y);
            myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
        } else {
            myrb = new Point(lt.x * -1 + posFrom.x, rb.y + posFrom.y);
            mylt = new Point(rb.x * -1 + posFrom.x, lt.y + posFrom.y);
        }
        return new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
    }

    public void setDuration(int d) {
        this.duration = d;
    }

    private int calcHPChange(TacosCharacter applyfrom, boolean primary) {
        int hpchange = 0;
        if (hp != 0) {
            if (!skill) {
                if (primary) {
                    hpchange += alchemistModifyVal(applyfrom, hp, true);
                } else {
                    hpchange += hp;
                }
            } else { // assumption: this is heal
                hpchange += makeHealHP(hp / 100.0, applyfrom.getStat().getTotalMagic(), 3, 5);
            }
        }
        if (hpR != 0) {
            hpchange += (int) (applyfrom.getStat().getCurrentMaxHp() * hpR);
        }
        // actually receivers probably never get any hp when it's not heal but whatever
        if (primary && hpCon != 0) {
            hpchange -= hpCon;
        }
        switch (this.sourceid) {
            case 4211001: // Chakra
                PlayerStats stat = applyfrom.getStat();
                int v42 = getY() + 100;
                int v38 = Randomizer.rand(1, 100) + 100;
                hpchange = (int) ((v38 * stat.getLuk() * 0.033 + stat.getDex()) * v42 * 0.002);
                hpchange += makeHealHP(getY() / 100.0, applyfrom.getStat().getTotalLuk(), 2.3, 3.5);
                break;
        }
        return hpchange;
    }

    private static int makeHealHP(double rate, double stat, double lowerfactor, double upperfactor) {
        return (int) ((Math.random() * ((int) (stat * upperfactor * rate) - (int) (stat * lowerfactor * rate) + 1)) + (int) (stat * lowerfactor * rate));
    }

    private static int getElementalAmp(int job) {
        switch (job) {
            case 211:
            case 212:
                return 2110001;
            case 221:
            case 222:
                return 2210001;
            case 1211:
            case 1212:
                return 12110001;
            case 2215:
            case 2216:
            case 2217:
            case 2218:
                return 22150000;
        }
        return -1;
    }

    private int calcMPChange(TacosCharacter applyfrom, boolean primary) {
        int mpchange = 0;
        if (mp != 0) {
            if (primary) {
                mpchange += alchemistModifyVal(applyfrom, mp, true);
            } else {
                mpchange += mp;
            }
        }
        if (mpR != 0) {
            mpchange += (int) (applyfrom.getStat().getCurrentMaxMp() * mpR);
        }
        if (primary && mpCon != 0) {
            double mod = 1.0;

            int ElemSkillId = getElementalAmp(applyfrom.getJob());
            if (ElemSkillId != -1) {
                Skill amp = WzXML.SKILL.getSkill().get(ElemSkillId);
                int ampLevel = applyfrom.getSkillLevel(amp);
                if (ampLevel > 0) {
                    MapleStatEffect ampStat = amp.getEffect(ampLevel);
                    mod = ampStat.getX() / 100.0;
                }
            }
            mpchange -= mpCon * mod;
        }
        return mpchange;
    }

    private int alchemistModifyVal(TacosCharacter chr, int val, boolean withX) {
        if (!skill) {
            MapleStatEffect alchemistEffect = getAlchemistEffect(chr);
            int offset = 100;
            if (alchemistEffect != null) {
                offset = withX ? alchemistEffect.getX() : alchemistEffect.getY();
            }
            return (val * offset / 100);
        }
        return val;
    }

    private MapleStatEffect getAlchemistEffect(TacosCharacter chr) {
        Skill al;
        switch (chr.getJob()) {
            case 411:
            case 412:
                al = WzXML.SKILL.getSkill().get(4110000);
                if (chr.getSkillLevel(al) <= 0) {
                    return null;
                }
                return al.getEffect(chr.getSkillLevel(al));
            case 1411:
            case 1412:
                al = WzXML.SKILL.getSkill().get(14110003);
                if (chr.getSkillLevel(al) <= 0) {
                    return null;
                }
                return al.getEffect(chr.getSkillLevel(al));
        }
        if (GameConstants.isResist(chr.getJob())) {
            al = WzXML.SKILL.getSkill().get(30000002);
            if (chr.getSkillLevel(al) <= 0) {
                return null;
            }
            return al.getEffect(chr.getSkillLevel(al));
        }
        return null;
    }

    private boolean isGmBuff() {
        switch (sourceid) {
            case 1005: // echo of hero acts like a gm buff
            case 10001005: // cygnus Echo
            case 20001005: // Echo
            case 20011005:
            case 30001005:
            case 9001000: // GM dispel
            case 9001001: // GM haste
            case 9001002: // GM Holy Symbol
            case 9001003: // GM Bless
            case 9001005: // GM resurrection
            case 9001008: // GM Hyper body
                return true;
            default:
                return false;
        }
    }

    private boolean isEnergyCharge() {
        return skill && (sourceid == 5110001 || sourceid == 15100004);
    }

    public boolean isMonsterBuff() {
        switch (sourceid) {
            case 1201006: // threaten
            case 2101003: // fp slow
            case 2201003: // il slow
            case 12101001: // cygnus slow
            case 2211004: // il seal
            case 2111004: // fp seal
            case 12111002: // cygnus seal
            case 2311005: // doom
            case 4111003: // shadow web
            case 14111001: // cygnus web
            case 4121004: // Ninja ambush
            case 4221004: // Ninja ambush
            case 22151001:
            case 22141003:
            case 22121000:
            case 22161002:
            case 4321002:
                return skill;
        }
        return false;
    }

    private boolean isPartyBuff() {
        if (lt == null || rb == null) {
            return isSoulStone();
        }
        switch (sourceid) {
            case 1211003:
            case 1211004:
            case 1211005:
            case 1211006:
            case 1211007:
            case 1211008:
            case 1221003:
            case 1221004:
            case 11111007:
            case 12101005:
            case 4311001:
                return false;
        }
        return true;
    }

    public boolean isHeal() {
        return sourceid == 2301002 || sourceid == 9101000;
    }

    public boolean isResurrection() {
        return sourceid == 9001005 || sourceid == 2321006;
    }

    public boolean isTimeLeap() {
        return sourceid == 5121010;
    }

    public short getHp() {
        return hp;
    }

    public short getMp() {
        return mp;
    }

    public short getWatk() {
        return watk;
    }

    public short getMatk() {
        return matk;
    }

    public short getWdef() {
        return wdef;
    }

    public short getMdef() {
        return mdef;
    }

    public short getAcc() {
        return acc;
    }

    public short getAvoid() {
        return avoid;
    }

    public short getHands() {
        return hands;
    }

    public short getSpeed() {
        return speed;
    }

    public short getJump() {
        return jump;
    }

    public ArrayList<SimpleImmutableEntry<OpsSecondaryStat, Integer>> getOss() {
        return oss;
    }

    public int getDuration() {
        return duration;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public short getDamage() {
        return damage;
    }

    public byte getMobCount() {
        return mobCount;
    }

    public int getCooldown() {
        return cooldown;
    }

    public Map<OpsMobStat, Integer> getMonsterStati() {
        return monsterStatus;
    }

    public boolean isMPRecovery() {
        return skill && sourceid == 5101005;
    }

    public boolean isPoison() {
        switch (sourceid) {
            case 2111003:
            case 2101005:
            case 2111006:
            case 2121003:
            case 2221003:
            case 12111005: // Flame gear
            case 3111003: //inferno, new
            case 22161002: //phantom imprint
                return skill;
        }
        return false;
    }

    private boolean isMist() {
        return skill && (sourceid == 2111003 || sourceid == 4221006 || sourceid == 12111005 || sourceid == 14111006 || sourceid == 22161003); // poison mist, smokescreen and flame gear, recovery aura
    }

    private boolean isSpiritClaw() {
        return skill && sourceid == 4121006;
    }

    public boolean isPirateMorph() {
        switch (sourceid) {
            case 15111002:
            case 5111005:
            case 5121003:
                return skill;
        }
        return false;
    }

    public boolean isMorph() {
        return morphId > 0;
    }

    public int getMorph() {
        switch (sourceid) {
            case 15111002:
            case 5111005:
                return 1000;
            case 5121003:
                return 1001;
            case 5101007:
                return 1002;
            case 13111005:
                return 1003;
        }
        return morphId;
    }

    public int getMorph(TacosCharacter chr) {
        int morph = getMorph();
        switch (morph) {
            case 1000:
            case 1001:
            case 1003:
                return morph + (chr.getGender() == 1 ? 100 : 0);
        }
        return morph;
    }

    public byte getLevel() {
        return level;
    }

    public int getSourceId() {
        return sourceid;
    }

    public boolean isFinalAttack() {
        switch (sourceid) {
            case 13101002:
            case 11101002:
                return skill;
        }
        return false;
    }

    /**
     *
     * @return true if the effect should happen based on it's probablity, false
     * otherwise
     */
    public boolean makeChanceResult() {
        return prop == 100 || Randomizer.nextInt(99) < prop;
    }

    public short getProb() {
        return prop;
    }

    public int getExp() {
        return exp;
    }

    public int getMpCon() {
        return this.mpCon;
    }

    public int getHpCon() {
        return this.hpCon;
    }
}
