package odin.server;

import odin.client.ISkill;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import odin.client.MapleCharacter;
import odin.client.PlayerStats;
import odin.client.SkillFactory;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventory;
import odin.client.inventory.MapleInventoryType;
import odin.client.status.MonsterStatus;
import odin.client.status.MonsterStatusEffect;
import tacos.config.ContentState;
import odin.constants.GameConstants;
import java.util.Arrays;
import odin.server.maps.MapleMapObject;
import tacos.packet.ops.OpsSecondaryStat;
import tacos.packet.ops.OpsSkill;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.ResCTownPortalPool;
import odin.server.life.MapleMonster;
import odin.server.maps.MapleDoor;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapObjectType;
import odin.server.maps.MapleMist;
import odin.server.maps.MapleSummon;
import tacos.odin.OdinPair;
import odin.provider.IMapleData;
import tacos.packet.ops.OpsMoveAbility;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.builder.PB_UserEffect;
import tacos.wz.WzDataTool;

public class MapleStatEffect {

    private byte mastery;
    private byte mhpR;
    private byte mmpR;
    private byte mobCount;
    private byte attackCount;
    private byte bulletCount;
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
    private short ehp;
    private short emp;
    private short ewatk;
    private short ewdef;
    private short emdef;
    private double hpR;
    private double mpR;
    private int duration;
    private int sourceid;
    private int moveTo;
    private int x;
    private int y;
    private int z;
    private int itemCon;
    private int itemConNo;
    private int bulletConsume;
    private int moneyCon;
    private int cooldown;
    private int morphId = 0;
    private int expinc;
    private int expBuff;
    private int itemup;
    private int mesoup;
    private int cashup;
    private int berserk;
    private int illusion;
    private int booster;
    private int berserk2;
    private int cp;
    private int nuffSkill;
    private int exp; // gashaEXP, consume 237
    private boolean overTime;
    private boolean skill;
    private boolean partyBuff = true;
    private Map<MonsterStatus, Integer> monsterStatus;
    private Point lt;
    private Point rb;
    private ArrayList<OdinPair<OpsSecondaryStat, Integer>> oss = new ArrayList<>();

    public static final MapleStatEffect loadSkillEffectFromData(final IMapleData source, final int skillid, final boolean overtime, final byte level) {
        return loadFromData(source, skillid, true, overtime, level, 0);
    }

    // after bigbang
    public static final MapleStatEffect loadSkillEffectFromData(final IMapleData source, final int skillid, final boolean overtime, final byte level, int common_level) {
        return loadFromData(source, skillid, true, overtime, level, common_level);
    }

    public static final MapleStatEffect loadItemEffectFromData(final IMapleData source, final int itemid) {
        return loadFromData(source, itemid, false, false, (byte) 1, 0);
    }

    private boolean checkData() {
        if (watk != 0) {
            oss.add(new OdinPair<>(OpsSecondaryStat.CTS_PAD, (int) watk));
        }
        if (wdef != 0) {
            oss.add(new OdinPair<>(OpsSecondaryStat.CTS_PDD, (int) wdef));
        }
        if (matk != 0) {
            oss.add(new OdinPair<>(OpsSecondaryStat.CTS_MAD, (int) matk));
        }
        if (mdef != 0) {
            oss.add(new OdinPair<>(OpsSecondaryStat.CTS_MDD, (int) mdef));
        }
        if (acc != 0) {
            oss.add(new OdinPair<>(OpsSecondaryStat.CTS_ACC, (int) acc));
        }
        if (avoid != 0) {
            oss.add(new OdinPair<>(OpsSecondaryStat.CTS_EVA, (int) avoid));
        }
        if (hands != 0) {
            oss.add(new OdinPair<>(OpsSecondaryStat.CTS_Craft, (int) hands)); // not coded
        }
        if (speed != 0) {
            oss.add(new OdinPair<>(OpsSecondaryStat.CTS_Speed, (int) speed));
        }
        if (jump != 0) {
            oss.add(new OdinPair<>(OpsSecondaryStat.CTS_Jump, (int) jump));
        }

        switch (OpsSkill.find(sourceid)) {
            case MAGICIAN_MAGIC_GUARD:
            case FLAMEWIZARD_MAGIC_GUARD:
            case EVAN_MAGIC_GUARD: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_MagicGuard, (int) x));
                return true;
            }
            case ROGUE_DARK_SIGHT:
            case DUAL4_ADVANCED_DARK_SIGHT:
            case NIGHTWALKER_DARK_SIGHT: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_DarkSight, (int) x));
                return true;
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
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_Booster, (int) x));
                return true;
            }
            case FIGHTER_POWER_GUARD:
            case PAGE_POWER_GUARD: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_PowerGuard, x));
                return true;
            }
            case SPEARMAN_HYPER_BODY: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_MaxHP, x));
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_MaxMP, y));
                return true;
            }
            case HUNTER_SOUL_ARROW_BOW:
            case CROSSBOWMAN_SOUL_ARROW_CROSSBOW:
            case WINDBREAKER_SOUL_ARROW_BOW: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_SoulArrow, x));
                return true;
            }
            case HERMIT_SHADOW_PARTNER:
            case THIEFMASTER_SHADOW_PARTNER:
            case NIGHTWALKER_SHADOW_PARTNER: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_ShadowPartner, x));
                return true;
            }
            case BOWMASTER_SHARP_EYES:
            case CROSSBOWMASTER_SHARP_EYES:
            case WILDHUNTER_SHARP_EYES:
            case NOVICE_SHARP_EYES:
            case NOBLESSE_SHARP_EYES:
            case EVANJR_SHARP_EYES:
            case CITIZEN_SHARP_EYES: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_SharpEyes, (x << 8) | y));
                return true;
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
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_BasicStatUp, x));
                return true;
            }
            case BOWMASTER_HAMSTRING: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_HamString, x));
                return true;
            }
            case BOWMASTER_CONCENTRATION: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_Concentration, x));
                return true;
            }
            case HERMIT_MESO_UP: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_MesoUp, x));
                return true;
            }
            case NIGHTLORD_SPIRIT_JAVELIN: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_SpiritJavelin, 0));
                return true;
            }
            case BMAGE_AURA_DARK: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_DarkAura, x));
                return true;
            }
            case BMAGE_AURA_BLUE: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_BlueAura, x));
                return true;
            }
            case BMAGE_AURA_YELLOW: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_YellowAura, x));
                return true;
            }
            case BMAGE_CYCLONE: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_Cyclone, x));
                return true;
            }
            case NOVICE_MONSTER_RIDING: {
                oss.add(new OdinPair<>(OpsSecondaryStat.CTS_RideVehicle, 1));
                return true;
            }
            default: {
                break;
            }
        }

        return true;
    }

    private static MapleStatEffect loadFromData(final IMapleData source, final int sourceid, final boolean skill, final boolean overTime, final byte level, int common_level) {
        final MapleStatEffect ret = new MapleStatEffect();
        ret.sourceid = sourceid;
        ret.skill = skill;
        ret.level = level;
        if (source == null) {
            return ret;
        }
        ret.duration = WzDataTool.getIntExpression("time", source, -1, common_level);
        ret.hp = (short) WzDataTool.getIntExpression("hp", source, 0, common_level);
        ret.hpR = WzDataTool.getIntExpression("hpR", source, 0, common_level) / 100.0;
        ret.mp = (short) WzDataTool.getIntExpression("mp", source, 0, common_level);
        ret.mpR = WzDataTool.getIntExpression("mpR", source, 0, common_level) / 100.0;
        ret.mhpR = (byte) WzDataTool.getIntExpression("mhpR", source, 0, common_level);
        ret.mmpR = (byte) WzDataTool.getIntExpression("mmpR", source, 0, common_level);
        ret.mpCon = (short) WzDataTool.getIntExpression("mpCon", source, 0, common_level);
        ret.hpCon = (short) WzDataTool.getIntExpression("hpCon", source, 0, common_level);
        ret.prop = (short) WzDataTool.getIntExpression("prop", source, 100, common_level);
        ret.cooldown = WzDataTool.getIntExpression("cooltime", source, 0, common_level);
        ret.expinc = WzDataTool.getIntExpression("expinc", source, 0, common_level);
        ret.morphId = WzDataTool.getIntExpression("morph", source, 0, common_level);
        ret.cp = WzDataTool.getIntExpression("cp", source, 0, common_level);
        ret.nuffSkill = WzDataTool.getIntExpression("nuffSkill", source, 0, common_level);
        ret.mobCount = (byte) WzDataTool.getIntExpression("mobCount", source, 1, common_level);
        ret.exp = WzDataTool.getIntExpression("exp", source, 0, common_level);

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
        ret.mastery = (byte) WzDataTool.getIntExpression("mastery", source, 0, common_level);
        ret.watk = (short) WzDataTool.getIntExpression("pad", source, 0, common_level);
        ret.wdef = (short) WzDataTool.getIntExpression("pdd", source, 0, common_level);
        ret.matk = (short) WzDataTool.getIntExpression("mad", source, 0, common_level);
        ret.mdef = (short) WzDataTool.getIntExpression("mdd", source, 0, common_level);
        ret.ehp = (short) WzDataTool.getIntExpression("emhp", source, 0, common_level);
        ret.emp = (short) WzDataTool.getIntExpression("emmp", source, 0, common_level);
        ret.ewatk = (short) WzDataTool.getIntExpression("epad", source, 0, common_level);
        ret.ewdef = (short) WzDataTool.getIntExpression("epdd", source, 0, common_level);
        ret.emdef = (short) WzDataTool.getIntExpression("emdd", source, 0, common_level);
        ret.acc = (short) WzDataTool.getIntExpression("acc", source, 0, common_level);
        ret.avoid = (short) WzDataTool.getIntExpression("eva", source, 0, common_level);
        ret.speed = (short) WzDataTool.getIntExpression("speed", source, 0, common_level);
        ret.jump = (short) WzDataTool.getIntExpression("jump", source, 0, common_level);
        ret.expBuff = WzDataTool.getIntExpression("expBuff", source, 0, common_level);
        ret.cashup = WzDataTool.getIntExpression("cashBuff", source, 0, common_level);
        ret.itemup = WzDataTool.getIntExpression("itemupbyitem", source, 0, common_level);
        ret.mesoup = WzDataTool.getIntExpression("mesoupbyitem", source, 0, common_level);
        ret.berserk = WzDataTool.getIntExpression("berserk", source, 0, common_level);
        ret.berserk2 = WzDataTool.getIntExpression("berserk2", source, 0, common_level);
        ret.booster = 0;
        ret.illusion = WzDataTool.getIntExpression("illusion", source, 0, common_level);

        final IMapleData ltd = source.getChildByPath("lt");
        if (ltd != null) {
            ret.lt = WzDataTool.getPoint(source.getChildByPath("lt"));
            ret.rb = WzDataTool.getPoint(source.getChildByPath("rb"));
        }

        ret.x = WzDataTool.getIntExpression("x", source, 0, common_level);
        ret.y = WzDataTool.getIntExpression("y", source, 0, common_level);
        ret.z = WzDataTool.getIntExpression("z", source, 0, common_level);
        ret.damage = (short) WzDataTool.getIntExpression("damage", source, 0, common_level);
        ret.attackCount = (byte) WzDataTool.getIntExpression("attackCount", source, 1, common_level);
        ret.bulletCount = (byte) WzDataTool.getIntExpression("bulletCount", source, 1, common_level);
        ret.bulletConsume = WzDataTool.getIntExpression("bulletConsume", source, 0, common_level);
        ret.moneyCon = WzDataTool.getIntExpression("moneyCon", source, 0, common_level);
        ret.itemCon = WzDataTool.getIntPath("itemCon", source, 0);
        ret.itemConNo = WzDataTool.getIntPath("itemConNo", source, 0);
        ret.moveTo = WzDataTool.getIntPath("moveTo", source, -1);

        ret.checkData();
        return ret;
    }

    public void applyPassive(MapleCharacter applyto, MapleMapObject obj) {
        if (makeChanceResult()) {
            switch (sourceid) { // MP eater
                case 2100000:
                case 2200000:
                case 2300000:
                    if (obj == null || obj.getType() != MapleMapObjectType.MONSTER) {
                        return;
                    }
                    MapleMonster mob = (MapleMonster) obj; // x is absorb percentage
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

    public final boolean applyTo(MapleCharacter chr) {
        return applyTo(chr, chr, true, null, duration);
    }

    public final boolean applyTo(MapleCharacter chr, Point pos) {
        return applyTo(chr, chr, true, pos, duration);
    }

    private final boolean applyTo(final MapleCharacter applyfrom, final MapleCharacter applyto, final boolean primary, final Point pos) {
        return applyTo(applyfrom, applyto, primary, pos, duration);
    }

    public final boolean applyTo(final MapleCharacter applyfrom, final MapleCharacter applyto, final boolean primary, final Point pos, int newDuration) {
        if (isHeal() && (applyfrom.getMapId() == 749040100 || applyto.getMapId() == 749040100)) {
            return false;
        } else if (sourceid == 33101004 && applyfrom.getMap().isTown()) {
            applyfrom.dropMessage(5, "You may not use this skill in towns.");
            applyfrom.updateStat();
            return false; //not supposed to
        }
        int hpchange = calcHPChange(applyfrom, primary);
        int mpchange = calcMPChange(applyfrom, primary);

        final PlayerStats stat = applyto.getStat();
        if (primary) {
            if (itemConNo != 0) {
                if (ContentState.CS_LOCK_LOSING_STONE.get()) {
                    // do nothing
                } else {
                    MapleInventoryManipulator.removeById(applyto.getClient(), GameConstants.getInventoryType(itemCon), itemCon, itemConNo, false, true);
                }
            }
        } else if (!primary && isResurrection()) {
            hpchange = stat.getMaxHp();
            applyto.setStance(0); //TODO fix death bug, player doesnt spawn on other screen
        }
        if (isMPRecovery()) {
            final int toDecreaseHP = ((stat.getMaxHp() / 100) * 10);
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
            IItem item;
            for (int i = 0; i < use.getSlotLimit(); i++) { // impose order...
                item = use.getItem((byte) i);
                if (item != null) {
                    if (GameConstants.isThrowingStar(item.getItemId()) && item.getQuantity() >= 200) {
                        if (ContentState.CS_LOCK_LOSING_THRWOING.get()) {
                            // do nothing
                        } else {
                            MapleInventoryManipulator.removeById(applyto.getClient(), MapleInventoryType.USE, item.getItemId(), 200, false, true);
                        }
                        break;
                    }
                }
            }
        } else if (cp != 0 && applyto.getCarnivalParty() != null) {
            applyto.getCarnivalParty().addCP(applyto, cp);
            applyto.CPUpdate(false, applyto.getAvailableCP(), applyto.getTotalCP(), 0);
            for (MapleCharacter chr : applyto.getMap().getCharacters()) {
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
        OpsMoveAbility summonMovementType = getSummonMovementType();
        if (summonMovementType != null) {
            final MapleSummon tosummon = new MapleSummon(applyfrom, this, new Point(pos == null ? applyfrom.getPosition() : pos), summonMovementType);
            if (!tosummon.isPuppet()) {
            }
            applyfrom.getMap().spawnSummon(tosummon);
            applyfrom.getSummons().put(sourceid, tosummon);
            tosummon.addHP((short) x);
            if (isBeholder()) {
                tosummon.addHP((short) 1);
            }
        } else if (isMagicDoor()) { // Magic Door
            if (!applyto.getDoors().isEmpty()) {
                applyto.removeDoor();
                applyto.silentPartyUpdate();
            }
            MapleDoor door = new MapleDoor(applyto, new Point(applyto.getPosition()), sourceid); // Current Map door
            if (door.getTownPortal() != null) {
                MapleDoor townDoor = new MapleDoor(door); // Town door
                door.setLink(townDoor);
                door.getTown().spawnDoor(townDoor);
                townDoor.setLink(door);

                applyto.getMap().spawnDoor(door);
                applyto.addDoor(door);
                applyto.addDoor(townDoor);
                //applyto.SendPacket(MysticDoorResponse.setMysticDoorInfo(door));

                if (applyto.getParty() != null) { // update town doors
                    //applyto.silentPartyUpdate();
                }

                applyto.SendPacket(ResCTownPortalPool.TownPortalCreated(door, false));

            } else {
                applyto.dropMessage(5, "You may not spawn a door because all doors in the town are taken.");
            }

        } else if (isMist()) {
            final Rectangle bounds = calculateBoundingBox(pos != null ? pos : new Point(applyfrom.getPosition()), applyfrom.isFacingLeft());
            final MapleMist mist = new MapleMist(bounds, applyfrom, this);
            applyfrom.getMap().spawnMist(mist, getDuration(), false);

        } else if (isTimeLeap()) {
            applyto.getCoolTime().timeLeap();
        }

        return true;
    }

    public final boolean applyReturnScroll(final MapleCharacter applyto) {
        if (moveTo != -1) {
            MapleMap target;
            if (moveTo == 999999999) {
                target = applyto.getMap().getReturnMap();
            } else {
                target = applyto.findMap(moveTo);
            }
            applyto.changeMap(target, target.getPortal(0));
            return true;
        }
        return false;
    }

    private final boolean isSoulStone() {
        return skill && sourceid == 22181003;
    }

    private final void applyBuff(final MapleCharacter applyfrom, int newDuration) {
        if (isSoulStone()) {
            if (applyfrom.getParty() != null) {
                int membrs = 0;
                for (MapleCharacter chr : applyfrom.getMap().getCharacters()) {
                    if (chr.getParty() != null && chr.getParty().equals(applyfrom.getParty()) && chr.isAlive()) {
                        membrs++;
                    }
                }
                List<MapleCharacter> awarded = new ArrayList<>();
                while (awarded.size() < Math.min(membrs, y)) {
                    for (MapleCharacter chr : applyfrom.getMap().getCharacters()) {
                        if (chr.isAlive() && chr.getParty().equals(applyfrom.getParty()) && !awarded.contains(chr) && Randomizer.nextInt(y) == 0) {
                            awarded.add(chr);
                        }
                    }
                }
                for (MapleCharacter chr : awarded) {
                    applyTo(applyfrom, chr, false, null, newDuration);
                    PB_UserEffect pb = PB_UserEffect.builder()
                            .player(chr)
                            .skill_id(sourceid)
                            .build();
                    chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_SkillAffected, pb));
                    chr.getMap().broadcastMessage(chr, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_SkillAffected, pb), false);
                }
            }
        } else if (isPartyBuff() && (applyfrom.getParty() != null || isGmBuff())) {
            final Rectangle bounds = calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
            final List<MapleMapObject> affecteds = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.PLAYER));

            for (final MapleMapObject affectedmo : affecteds) {
                final MapleCharacter affected = (MapleCharacter) affectedmo;

                if (affected != applyfrom && (isGmBuff() || applyfrom.getParty().equals(affected.getParty()))) {
                    if ((isResurrection() && !affected.isAlive()) || (!isResurrection() && affected.isAlive())) {
                        applyTo(applyfrom, affected, false, null, newDuration);

                        PB_UserEffect pb = PB_UserEffect.builder()
                                .player(affected)
                                .skill_id(sourceid)
                                .build();
                        affected.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_SkillAffected, pb));
                        affected.getMap().broadcastMessage(affected, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_SkillAffected, pb), false);
                    }
                    if (isTimeLeap()) {
                        affected.getCoolTime().timeLeap();
                    }
                }
            }
        }
    }

    private final void applyMonsterBuff(final MapleCharacter applyfrom) {
        final Rectangle bounds = calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
        final List<MapleMapObject> affected = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.MONSTER));
        int i = 0;

        for (final MapleMapObject mo : affected) {
            if (makeChanceResult()) {
                for (Map.Entry<MonsterStatus, Integer> stat : getMonsterStati().entrySet()) {
                    ((MapleMonster) mo).applyStatus(applyfrom, new MonsterStatusEffect(stat.getKey(), stat.getValue(), sourceid, null, false), isPoison(), getDuration(), false);
                }
            }
            i++;
            if (i >= mobCount) {
                break;
            }
        }
    }

    private final Rectangle calculateBoundingBox(final Point posFrom, final boolean facingLeft) {
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

    public final void setDuration(int d) {
        this.duration = d;
    }

    private final int calcHPChange(final MapleCharacter applyfrom, final boolean primary) {
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
        if (primary) {
            if (hpCon != 0) {
                hpchange -= hpCon;
            }
        }
        switch (this.sourceid) {
            case 4211001: // Chakra
                final PlayerStats stat = applyfrom.getStat();
                int v42 = getY() + 100;
                int v38 = Randomizer.rand(1, 100) + 100;
                hpchange = (int) ((v38 * stat.getLuk() * 0.033 + stat.getDex()) * v42 * 0.002);
                hpchange += makeHealHP(getY() / 100.0, applyfrom.getStat().getTotalLuk(), 2.3, 3.5);
                break;
        }
        return hpchange;
    }

    private static final int makeHealHP(double rate, double stat, double lowerfactor, double upperfactor) {
        return (int) ((Math.random() * ((int) (stat * upperfactor * rate) - (int) (stat * lowerfactor * rate) + 1)) + (int) (stat * lowerfactor * rate));
    }

    private static final int getElementalAmp(final int job) {
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

    private final int calcMPChange(final MapleCharacter applyfrom, final boolean primary) {
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
        if (primary) {
            if (mpCon != 0) {
                double mod = 1.0;

                final int ElemSkillId = getElementalAmp(applyfrom.getJob());
                if (ElemSkillId != -1) {
                    final ISkill amp = SkillFactory.getSkill(ElemSkillId);
                    final int ampLevel = applyfrom.getSkillLevel(amp);
                    if (ampLevel > 0) {
                        MapleStatEffect ampStat = amp.getEffect(ampLevel);
                        mod = ampStat.getX() / 100.0;
                    }
                }
                mpchange -= mpCon * mod;
            }
        }
        return mpchange;
    }

    private final int alchemistModifyVal(final MapleCharacter chr, final int val, final boolean withX) {
        if (!skill) {
            int offset = chr.getStat().RecoveryUP;
            final MapleStatEffect alchemistEffect = getAlchemistEffect(chr);
            if (alchemistEffect != null) {
                offset += (withX ? alchemistEffect.getX() : alchemistEffect.getY());
            } else {
                offset += 100;
            }
            return (val * offset / 100);
        }
        return val;
    }

    private final MapleStatEffect getAlchemistEffect(final MapleCharacter chr) {
        ISkill al;
        switch (chr.getJob()) {
            case 411:
            case 412:
                al = SkillFactory.getSkill(4110000);
                if (chr.getSkillLevel(al) <= 0) {
                    return null;
                }
                return al.getEffect(chr.getSkillLevel(al));
            case 1411:
            case 1412:
                al = SkillFactory.getSkill(14110003);
                if (chr.getSkillLevel(al) <= 0) {
                    return null;
                }
                return al.getEffect(chr.getSkillLevel(al));
        }
        if (GameConstants.isResist(chr.getJob())) {
            al = SkillFactory.getSkill(30000002);
            if (chr.getSkillLevel(al) <= 0) {
                return null;
            }
            return al.getEffect(chr.getSkillLevel(al));
        }
        return null;
    }

    public final void setSourceId(final int newid) {
        sourceid = newid;
    }

    private final boolean isGmBuff() {
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

    private final boolean isEnergyCharge() {
        return skill && (sourceid == 5110001 || sourceid == 15100004);
    }

    private final boolean isMonsterBuff() {
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

    private final boolean isPartyBuff() {
        if (lt == null || rb == null || !partyBuff) {
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

    public final boolean isHeal() {
        return sourceid == 2301002 || sourceid == 9101000;
    }

    public final boolean isResurrection() {
        return sourceid == 9001005 || sourceid == 2321006;
    }

    public final boolean isTimeLeap() {
        return sourceid == 5121010;
    }

    public final short getHp() {
        return hp;
    }

    public final short getMp() {
        return mp;
    }

    public final short getWatk() {
        return watk;
    }

    public final short getMatk() {
        return matk;
    }

    public final short getWdef() {
        return wdef;
    }

    public final short getMdef() {
        return mdef;
    }

    public final short getAcc() {
        return acc;
    }

    public final short getAvoid() {
        return avoid;
    }

    public final short getHands() {
        return hands;
    }

    public final short getSpeed() {
        return speed;
    }

    public final short getJump() {
        return jump;
    }

    public ArrayList<OdinPair<OpsSecondaryStat, Integer>> getOss() {
        return oss;
    }

    public final int getDuration() {
        return duration;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final short getDamage() {
        return damage;
    }

    public final byte getMobCount() {
        return mobCount;
    }

    public int getCooldown() {
        return cooldown;
    }

    public final Map<MonsterStatus, Integer> getMonsterStati() {
        return monsterStatus;
    }

    public final boolean isBeholder() {
        return skill && sourceid == 1321007;
    }

    public final boolean isMPRecovery() {
        return skill && sourceid == 5101005;
    }

    public final boolean isMonsterRiding_() {
        return skill && (sourceid == 1004 || sourceid == 10001004 || sourceid == 20001004 || sourceid == 20011004 || sourceid == 30001004);
    }

    public final boolean isMagicDoor() {
        return skill && (sourceid == 2311002 || sourceid == 8001 || sourceid == 10008001 || sourceid == 20008001 || sourceid == 20018001 || sourceid == 30008001);
    }

    public final boolean isPoison() {
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

    private final boolean isMist() {
        return skill && (sourceid == 2111003 || sourceid == 4221006 || sourceid == 12111005 || sourceid == 14111006 || sourceid == 22161003); // poison mist, smokescreen and flame gear, recovery aura
    }

    private final boolean isSpiritClaw() {
        return skill && sourceid == 4121006;
    }

    public final boolean isPirateMorph() {
        switch (sourceid) {
            case 15111002:
            case 5111005:
            case 5121003:
                return skill;
        }
        return false;
    }

    public final boolean isMorph() {
        return morphId > 0;
    }

    public final int getMorph() {
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

    public final int getMorph(final MapleCharacter chr) {
        final int morph = getMorph();
        switch (morph) {
            case 1000:
            case 1001:
            case 1003:
                return morph + (chr.getGender() == 1 ? 100 : 0);
        }
        return morph;
    }

    public final byte getLevel() {
        return level;
    }

    public OpsMoveAbility getSummonMovementType() {
        if (!skill) {
            return null;
        }
        switch (sourceid) {
            case 3211002: // puppet sniper
            case 3111002: // puppet ranger
            case 33111003:
            case 13111004: // puppet cygnus
            case 5211001: // octopus - pirate
            case 5220002: // advanced octopus - pirate
            case 4341006:
            case 35111002:
            case 35111005: //TEMP
            case 35111004: //TEMP
            //case 35111011: //TEMP
            case 35121009:
            //case 35121010: //TEMP
            case 35121011:
                //case 4111007: //TEMP
                return OpsMoveAbility.MOVEABILITY_STOP;
            case 3211005: // golden eagle
            case 3111005: // golden hawk
            case 33111005:
            case 2311006: // summon dragon
            case 3221005: // frostprey
            case 3121006: // phoenix
                return OpsMoveAbility.MOVEABILITY_FLY;
            case 5211002: // bird - pirate
                return OpsMoveAbility.MOVEABILITY_FLY_RANDOM;
            case 32111006: //reaper
                return OpsMoveAbility.MOVEABILITY_WALK_RANDOM;
            case 1321007: // beholder
            case 2121005: // elquines
            case 2221005: // ifrit
            case 2321003: // bahamut
            case 12111004: // Ifrit
            case 11001004: // soul
            case 12001004: // flame
            case 13001004: // storm
            case 14001005: // darkness
            case 15001004: // lightning
            case 35111001:
            case 35111010:
            case 35111009:
                return OpsMoveAbility.MOVEABILITY_WALK;
        }
        return null;
    }

    public final int getSourceId() {
        return sourceid;
    }

    public final boolean isFinalAttack() {
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
    public final boolean makeChanceResult() {
        return prop == 100 || Randomizer.nextInt(99) < prop;
    }

    public final short getProb() {
        return prop;
    }

    public final int getExp() {
        return exp;
    }
}
