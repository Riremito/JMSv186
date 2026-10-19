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
package tacos.wz;

import odin.client.Skill;
import odin.client.MapleCharacter;
import tacos.config.Content;
import tacos.debug.DebugLogger;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import odin.server.MapleCarnivalFactory;
import odin.server.life.MobSkill;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.LinkedHashMap;
import odin.client.SkillFactory;
import odin.server.MapleStatEffect;
import odin.server.life.Element;
import tacos.client.TacosDoorSkill;
import tacos.client.TacosSkill;
import tacos.client.TacosSummonSkill;
import tacos.config.Config;

/**
 *
 * @author Riremito
 */
public class SkillWz extends WzXML {

    public SkillWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Skill" : "Skill.wz");
    }

    public MapleData getImg(int job_id) {
        return getData(job_id + ".img");
    }

    // job_id, skill_id, skill_level
    private final LinkedHashMap<Integer, LinkedHashMap<Integer, LinkedHashMap<Integer, TacosSummonSkill>>> job_list_summon = new LinkedHashMap<>();

    public TacosSummonSkill getSummonSkill(int target_skill_id, int target_skill_level) {
        int job_id = target_skill_id / 10000;

        LinkedHashMap<Integer, LinkedHashMap<Integer, TacosSummonSkill>> skill_list = this.job_list_summon.get(job_id);
        if (skill_list != null) {
            LinkedHashMap<Integer, TacosSummonSkill> level_list = skill_list.get(target_skill_id);
            if (level_list != null) {
                return level_list.get(target_skill_level);
            }
            return null;
        }

        skill_list = new LinkedHashMap<>();

        MapleData job_img = getImg(job_id);
        if (job_img != null) {
            MapleData md_skill = job_img.getChildByPath("skill");
            if (md_skill != null) {
                for (MapleData md_skill_data : md_skill.getChildren()) {
                    MapleData md_summon = md_skill_data.getChildByPath("summon");
                    if (md_summon != null) {
                        int skill_id = Integer.parseInt(md_skill_data.getName());
                        // postBB style, but not all skills are updated in BIGBANG.
                        if (Config.PostBB()) {
                            MapleData md_common = md_skill_data.getChildByPath("common");
                            if (md_common != null) {
                                LinkedHashMap<Integer, TacosSummonSkill> level_list = new LinkedHashMap<>();
                                int maxLevel = md_common.getInt("maxLevel");
                                for (int skill_level = 1; skill_level <= maxLevel; skill_level++) {
                                    TacosSummonSkill tss = new TacosSummonSkill(skill_id, skill_level);
                                    tss.setItemCon(md_common.getIntExpression("itemCon", skill_level));
                                    tss.setItemConNo(md_common.getIntExpression("itemConNo", skill_level));
                                    tss.setMobCount(md_common.getIntExpression("mobCount", skill_level));
                                    tss.setMpCon(md_common.getIntExpression("mpCon", skill_level));
                                    tss.setProp(md_common.getIntExpression("prop", skill_level));
                                    tss.setTime(md_common.getIntExpression("time", skill_level));
                                    tss.setX(md_common.getIntExpression("x", skill_level, 1));
                                    level_list.put(skill_level, tss);
                                }
                                skill_list.put(Integer.valueOf(md_skill_data.getName()), level_list);
                                continue;
                            }
                        }
                        // preBB style.
                        MapleData md_level = md_skill_data.getChildByPath("level");
                        if (md_level != null) {
                            LinkedHashMap<Integer, TacosSummonSkill> level_list = new LinkedHashMap<>();
                            for (MapleData md_level_data : md_level.getChildren()) {
                                int skill_level = Integer.parseInt(md_level_data.getName());
                                TacosSummonSkill tss = new TacosSummonSkill(skill_id, skill_level);
                                tss.setHs(md_level_data.getString("hs"));
                                tss.setItemCon(md_level_data.getInt("itemCon"));
                                tss.setItemConNo(md_level_data.getInt("itemConNo"));
                                tss.setMad(md_level_data.getInt("mad"));
                                tss.setMastery(md_level_data.getInt("mastery"));
                                tss.setMobCount(md_level_data.getInt("mobCount"));
                                tss.setMpCon(md_level_data.getInt("mpCon"));
                                tss.setPad(md_level_data.getInt("pad"));
                                tss.setProp(md_level_data.getInt("prop"));
                                tss.setTime(md_level_data.getInt("time"));
                                tss.setX(md_level_data.getInt("x", 1));
                                level_list.put(skill_level, tss);
                            }
                            skill_list.put(Integer.valueOf(md_skill_data.getName()), level_list);
                        }
                    }
                }
            }
        }

        this.job_list_summon.put(job_id, skill_list);

        LinkedHashMap<Integer, TacosSummonSkill> level_list = skill_list.get(target_skill_id);
        if (level_list != null) {
            return level_list.get(target_skill_level);
        }
        return null;
    }

    // door.
    private final LinkedHashMap<Integer, LinkedHashMap<Integer, LinkedHashMap<Integer, TacosDoorSkill>>> job_list_door = new LinkedHashMap<>();

    public TacosDoorSkill getDoorSkill(int target_skill_id, int target_skill_level) {
        int job_id = target_skill_id / 10000;

        LinkedHashMap<Integer, LinkedHashMap<Integer, TacosDoorSkill>> skill_list = this.job_list_door.get(job_id);
        if (skill_list != null) {
            LinkedHashMap<Integer, TacosDoorSkill> level_list = skill_list.get(target_skill_id);
            if (level_list != null) {
                return level_list.get(target_skill_level);
            }
            return null;
        }

        skill_list = new LinkedHashMap<>();

        MapleData job_img = getImg(job_id);
        if (job_img != null) {
            MapleData md_skill = job_img.getChildByPath("skill");
            if (md_skill != null) {
                for (MapleData md_skill_data : md_skill.getChildren()) {
                    MapleData md_frame = md_skill_data.getChildByPath("Frame");
                    if (md_frame != null) {
                        int skill_id = Integer.parseInt(md_skill_data.getName());
                        // postBB style, but not all skills are updated in BIGBANG.
                        if (Config.PostBB()) {
                            MapleData md_common = md_skill_data.getChildByPath("common");
                            if (md_common != null) {
                                LinkedHashMap<Integer, TacosDoorSkill> level_list = new LinkedHashMap<>();
                                int maxLevel = md_common.getInt("maxLevel");
                                for (int skill_level = 1; skill_level <= maxLevel; skill_level++) {
                                    TacosDoorSkill tds = new TacosDoorSkill(skill_id, skill_level);
                                    tds.setItemCon(md_common.getIntExpression("itemCon", skill_level));
                                    tds.setItemConNo(md_common.getIntExpression("itemConNo", skill_level));
                                    tds.setMpCon(md_common.getIntExpression("mpCon", skill_level));
                                    tds.setTime(md_common.getIntExpression("time", skill_level));
                                    level_list.put(skill_level, tds);
                                }
                                skill_list.put(Integer.valueOf(md_skill_data.getName()), level_list);
                                continue;
                            }
                        }
                        // preBB style.
                        MapleData md_level = md_skill_data.getChildByPath("level");
                        if (md_level != null) {
                            LinkedHashMap<Integer, TacosDoorSkill> level_list = new LinkedHashMap<>();
                            for (MapleData md_level_data : md_level.getChildren()) {
                                int skill_level = Integer.parseInt(md_level_data.getName());
                                TacosDoorSkill tds = new TacosDoorSkill(skill_id, skill_level);
                                tds.setItemCon(md_level_data.getInt("itemCon"));
                                tds.setItemConNo(md_level_data.getInt("itemConNo"));
                                tds.setMpCon(md_level_data.getInt("mpCon"));
                                tds.setTime(md_level_data.getInt("time"));
                                level_list.put(skill_level, tds);
                            }
                            skill_list.put(Integer.valueOf(md_skill_data.getName()), level_list);
                        }
                    }
                }
            }
        }

        this.job_list_door.put(job_id, skill_list);

        LinkedHashMap<Integer, TacosDoorSkill> level_list = skill_list.get(target_skill_id);
        if (level_list != null) {
            return level_list.get(target_skill_level);
        }
        return null;
    }

    // skill.
    private final LinkedHashMap<Integer, LinkedHashMap<Integer, LinkedHashMap<Integer, TacosSkill>>> job_list = new LinkedHashMap<>();

    public TacosSkill getSkill(int target_skill_id, int target_skill_level) {
        int job_id = target_skill_id / 10000;

        LinkedHashMap<Integer, LinkedHashMap<Integer, TacosSkill>> skill_list = this.job_list.get(job_id);
        if (skill_list != null) {
            LinkedHashMap<Integer, TacosSkill> level_list = skill_list.get(target_skill_id);
            if (level_list != null) {
                return level_list.get(target_skill_level);
            }
            return null;
        }

        skill_list = new LinkedHashMap<>();

        MapleData job_img = getImg(job_id);
        if (job_img != null) {
            MapleData md_skill = job_img.getChildByPath("skill");
            if (md_skill != null) {
                for (MapleData md_skill_data : md_skill.getChildren()) {
                    MapleData md_hit_0 = md_skill_data.getChildByPath("hit/0");
                    if (md_hit_0 != null) {
                        int hitAfter = md_hit_0.getInt("hitAfter");
                        int skill_id = Integer.parseInt(md_skill_data.getName());
                        // postBB style, but not all skills are updated in BIGBANG.
                        if (Config.PostBB()) {
                            MapleData md_common = md_skill_data.getChildByPath("common");
                            if (md_common != null) {
                                LinkedHashMap<Integer, TacosSkill> level_list = new LinkedHashMap<>();
                                int maxLevel = md_common.getInt("maxLevel");
                                for (int skill_level = 1; skill_level <= maxLevel; skill_level++) {
                                    TacosSkill ts = new TacosSkill(skill_id, skill_level);
                                    ts.setHitAfter(hitAfter);
                                    level_list.put(skill_level, ts);
                                }
                                skill_list.put(Integer.valueOf(md_skill_data.getName()), level_list);
                                continue;
                            }
                        }
                        // preBB style.
                        MapleData md_level = md_skill_data.getChildByPath("level");
                        if (md_level != null) {
                            LinkedHashMap<Integer, TacosSkill> level_list = new LinkedHashMap<>();
                            for (MapleData md_level_data : md_level.getChildren()) {
                                int skill_level = Integer.parseInt(md_level_data.getName());
                                TacosSkill ts = new TacosSkill(skill_id, skill_level);
                                ts.setHitAfter(hitAfter);
                                level_list.put(skill_level, ts);
                            }
                            skill_list.put(Integer.valueOf(md_skill_data.getName()), level_list);
                        }
                    }
                }
            }
        }

        this.job_list.put(job_id, skill_list);

        LinkedHashMap<Integer, TacosSkill> level_list = skill_list.get(target_skill_id);
        if (level_list != null) {
            return level_list.get(target_skill_level);
        }
        return null;
    }

    private Map<Integer, Skill> map_Skill = null;
    private Map<Integer, List<Integer>> map_SkillsByJob = null;

    public Map<Integer, List<Integer>> getSkillsByJob() {
        if (map_SkillsByJob == null) {
            getSkill();
        }
        return map_SkillsByJob;
    }

    public Map<Integer, Skill> getSkill() {
        if (map_Skill != null) {
            return map_Skill;
        }
        map_Skill = new HashMap<>();
        map_SkillsByJob = new HashMap<>();

        int skillid;
        for (MapleDataEntity topDir : getRootDirectory().getFiles()) { // Loop thru jobs
            if (topDir.getName().length() <= 8) {
                for (MapleData data : getData(topDir.getName())) { // Loop thru each jobs
                    if (data.getName().equals("skill")) {
                        for (MapleData data2 : data) { // Loop thru each jobs
                            if (data2 != null) {
                                skillid = Integer.parseInt(data2.getName());

                                Skill skil = parseSkill(skillid, data2);
                                List<Integer> job = map_SkillsByJob.get(skillid / 10000);
                                if (job == null) {
                                    job = new ArrayList<>();
                                    map_SkillsByJob.put(skillid / 10000, job);
                                }
                                job.add(skillid);

                                // THMS meme
                                String skill_name = "";
                                try {
                                    skill_name = SkillFactory.getName(skillid);
                                } catch (RuntimeException e) {
                                    DebugLogger.ErrorLog("" + skillid);
                                }
                                skil.setName(skill_name);
                                map_Skill.put(skillid, skil);
                            }
                        }
                    }
                }
            }
        }
        return map_Skill;
    }

    private Skill parseSkill(final int id, final MapleData data) {
        Skill ret = new Skill(id);

        boolean isBuff = false;
        final int skillType = data.getInt("skillType", -1);
        final String elem = data.getString("elemAttr", null);
        if (elem != null) {
            ret.setElement(Element.getFromChar(elem.charAt(0)));
        } else {
            ret.setElement(Element.NEUTRAL);
        }
        ret.setInvisible(data.getInt("invisible") > 0);
        ret.setTimeLimited(data.getInt("timeLimited") > 0);
        ret.setMasterLevel(data.getInt("masterLevel"));
        final MapleData effect = data.getChildByPath("effect");
        if (skillType != -1) {
            if (skillType == 2) {
                isBuff = true;
            }
        } else {
            final MapleData action_ = data.getChildByPath("action");
            final MapleData hit = data.getChildByPath("hit");
            final MapleData ball = data.getChildByPath("ball");

            boolean action = false;
            if (action_ == null) {
                if (data.getChildByPath("prepare/action") != null) {
                    action = true;
                } else {
                    switch (id) {
                        case 5201001:
                        case 5221009:
                        case 4221001:
                        case 4321001:
                        case 4321000:
                        case 4331001: //o_o
                        case 3101005: //or is this really hack
                            action = true;
                            break;
                    }
                }
            } else {
                action = true;
            }
            ret.setAction(action);
            isBuff = effect != null && hit == null && ball == null;
            isBuff |= action_ != null && action_.getString("0").equals("alert2");
            switch (id) {
                case 2301002: // heal is alert2 but not overtime...
                case 2111003: // poison mist
                case 12111005: // Flame Gear
                case 2111002: // explosion
                case 4211001: // chakra
                case 2121001: // Big bang
                case 2221001: // Big bang
                case 2321001: // Big bang
                    isBuff = false;
                    break;
                case 1004: // monster riding
                case 10001004:
                case 20001004:
                case 20011004:
                case 30001004:
                case 1026: //Soaring
                case 10001026:
                case 20001026:
                case 20011026:
                case 30001026:
                case 9101004: // hide is a buff -.- atleast for us o.o"
                case 1111002: // combo
                case 4211003: // pickpocket
                case 4111001: // mesoup
                case 15111002: // Super Transformation
                case 5111005: // Transformation
                case 5121003: // Super Transformation
                case 13111005: // Alabtross
                case 21000000: // Aran Combo
                case 21101003: // Body Pressure
                case 5211001: // Pirate octopus summon
                case 5211002:
                case 5220002: // wrath of the octopi
                case 5001005: //dash
                case 15001003:
                case 5211006: //homing beacon
                case 5220011: //bullseye
                case 5110001: //energy charge
                case 15100004:
                case 5121009: //speed infusion
                case 15111005:

                case 22121001: //element reset
                case 22131001: //magic shield
                case 22141002: //magic booster
                case 22151002: //killer wing
                case 22151003: //magic resist
                case 22171000: //maple warrior
                case 22171004: //hero will
                case 22181000: //onyx blessing
                case 22181003: //soul stone
                //case 22121000:
                //case 22141003:
                //case 22151001:
                //case 22161002:
                case 4331003: //owl spirit
                case 15101006: //spark
                case 15111006: //spark
                case 4321000: //tornado spin
                case 1320009: //beholder's buff.. passive
                case 35120000:
                case 35001002: //TEMP. mech
                case 9001004: // hide
                case 4341002:

                case 32001003: //dark aura
                case 32120000:
                case 32101002: //blue aura
                case 32110000:
                case 32101003: //yellow aura
                case 32120001:
                case 35101007: //perfect armor
                case 35121006: //satellite safety
                case 35001001: //flame
                case 35101009:
                case 35111007: //TEMP
                case 35121005: //missile
                case 35121013:
                //case 35111004: //siege
                case 35101002: //TEMP
                case 33111003: //puppet ?
                case 1211009:
                case 1111007:
                case 1311007: //magic,armor,atk crash
                    isBuff = true;
                    break;
            }
        }
        ret.setChargeSkill(data.getChildByPath("keydown") != null);

        if (Config.PreBB()) {
            for (final MapleData level : data.getChildByPath("level")) {
                ret.addEffect(MapleStatEffect.loadSkillEffectFromData(loadSkillEffect(level, 0), id, isBuff, Byte.parseByte(level.getName())));
            }
        } else {
            // v188+
            MapleData common = data.getChildByPath("common");
            if (common != null) {
                // after bigbang updates
                int max_level = common.getInt("maxLevel", -1);
                for (int level = 1; level <= max_level; level++) {
                    ret.addEffect(MapleStatEffect.loadSkillEffectFromData(loadSkillEffect(common, level), id, isBuff, (byte) level)); // 変数
                }
            } else {
                // old skills
                for (final MapleData level : data.getChildByPath("level")) {
                    ret.addEffect(MapleStatEffect.loadSkillEffectFromData(loadSkillEffect(level, 0), id, isBuff, Byte.parseByte(level.getName())));
                }
            }
        }

        final MapleData reqDataRoot = data.getChildByPath("req");
        if (reqDataRoot != null) {
            for (final MapleData reqData : reqDataRoot.getChildren()) {
                ret.setRequiredSkill(Integer.parseInt(reqData.getName()));
                ret.setLevel((byte) reqDataRoot.getInt(reqData.getName(), 1));
            }
        }
        int animationTime = 0;
        if (effect != null) {
            for (final MapleData effectEntry : effect) {
                animationTime += effectEntry.getInt("delay");
            }
        }
        ret.setAnimationTime(animationTime);
        return ret;
    }

    // test
    public ArrayList<Integer> getBasicSkill(MapleCharacter chr, String job_img_name) {
        ArrayList<Integer> list = new ArrayList<>();

        MapleData md_job = getData(job_img_name);
        if (md_job == null) {
            return list;
        }
        MapleData md_skill_dir = md_job.getChildByPath("skill");
        if (md_skill_dir == null) {
            return list;
        }
        for (MapleData md_skill : md_skill_dir.getChildren()) {
            int skill_id = Integer.parseInt(md_skill.getName());
            if (skill_id == 0) {
                continue;
            }
            list.add(skill_id);
        }
        return list;
    }
    // Mob
    private Map<SimpleImmutableEntry<Integer, Integer>, MobSkill> map_mobSkills = null;

    private MapleData getMobSkill() {
        return getData("MobSkill.img");
    }

    public MobSkill getMobSkillData(int skillId, int level) {
        if (map_mobSkills == null) {
            map_mobSkills = new HashMap<>();
        }

        MobSkill ms_found = map_mobSkills.get(new SimpleImmutableEntry<>(skillId, level));
        if (ms_found != null) {
            return ms_found;
        }

        if (getMobSkill() == null || getMobSkill().getChildren() == null || getMobSkill().getChildByPath(String.valueOf(skillId)) == null || getMobSkill().getChildByPath(String.valueOf(skillId)).getChildren() == null || getMobSkill().getChildByPath(String.valueOf(skillId)).getChildByPath("level") == null) {
            return null;
        }

        final MapleData skillData = getMobSkill().getChildByPath(skillId + "/level/" + level);

        if (skillData == null) {
            return null;
        }

        if (skillData.getChildren() == null) {
            return null;
        }

        List<Integer> toSummon = new ArrayList<>();
        for (int i = 0; i > -1; i++) {
            if (skillData.getChildByPath(String.valueOf(i)) == null) {
                break;
            }
            toSummon.add(skillData.getInt(String.valueOf(i)));
        }

        final MapleData ltd = skillData.getChildByPath("lt");
        Point lt = null;
        Point rb = null;
        if (ltd != null) {
            lt = skillData.getPoint("lt");
            rb = skillData.getPoint("rb");
        }

        MobSkill ret = new MobSkill(skillId, level);
        ret.addSummons(toSummon);
        ret.setCoolTime(skillData.getInt("interval") * 1000);
        ret.setDuration(skillData.getInt("time", 1) * 1000);
        ret.setHp(skillData.getInt("hp", 100));
        ret.setMpCon(skillData.getInt("mpCon"));
        ret.setSpawnEffect(skillData.getInt("summonEffect"));
        ret.setX(skillData.getInt("x", 1));
        ret.setY(skillData.getInt("y", 1));
        ret.setProp(skillData.getInt("prop", 100) / 100f);
        ret.setLimit((short) skillData.getInt("limit"));
        ret.setLtRb(lt, rb);

        map_mobSkills.put(new SimpleImmutableEntry<>(skillId, level), ret);

        return ret;
    }

    // Monster Carnival
    private Map<Integer, MapleCarnivalFactory.MCSkill> map_MCSkill = null;
    private Map<Integer, MapleCarnivalFactory.MCSkill> map_MCGuardian = null;

    public Map<Integer, MapleCarnivalFactory.MCSkill> getMCSkill() {
        if (map_MCSkill != null) {
            return map_MCSkill;
        }

        map_MCSkill = new HashMap<>();
        for (MapleData md : getData("MCSkill.img")) {
            // THMS meme
            int mobSkillID = 0;
            try {
                mobSkillID = md.getInt("mobSkillID");
            } catch (NumberFormatException e) {
                // MCSkill.img/4/mobSkillID
                DebugLogger.ErrorLog("MCSkill.img/" + md.getName() + "/mobSkillID");
                continue;
            }
            map_MCSkill.put(Integer.valueOf(md.getName()), new MapleCarnivalFactory.MCSkill(md.getInt("spendCP"), mobSkillID, md.getInt("level"), md.getInt("target", 1) > 1));
        }
        return map_MCSkill;
    }

    public Map<Integer, MapleCarnivalFactory.MCSkill> getMCGuardian() {
        if (map_MCGuardian != null) {
            return map_MCGuardian;
        }

        map_MCGuardian = new HashMap<>();
        for (MapleData md : getData("MCGuardian.img")) {
            map_MCGuardian.put(Integer.valueOf(md.getName()), new MapleCarnivalFactory.MCSkill(md.getInt("spendCP"), md.getInt("mobSkillID"), md.getInt("level"), true));
        }
        return map_MCGuardian;
    }

    // level, common
    public class SkillEffectInfo extends StatEffectData {
    }

    // common_level : 0 = not an expression (pre-bb).
    public SkillEffectInfo loadSkillEffect(MapleData source, int common_level) {
        if (source == null) {
            return null;
        }
        SkillEffectInfo ret = new SkillEffectInfo();
        ret.time = source.getIntExpression("time", common_level, -1);
        ret.hp = source.getIntExpression("hp", common_level);
        ret.hpR = source.getIntExpression("hpR", common_level);
        ret.mp = source.getIntExpression("mp", common_level);
        ret.mpR = source.getIntExpression("mpR", common_level);
        ret.mhpR = source.getIntExpression("mhpR", common_level);
        ret.mmpR = source.getIntExpression("mmpR", common_level);
        ret.mpCon = source.getIntExpression("mpCon", common_level);
        ret.hpCon = source.getIntExpression("hpCon", common_level);
        ret.prop = source.getIntExpression("prop", common_level, 100);
        ret.cooltime = source.getIntExpression("cooltime", common_level);
        ret.expinc = source.getIntExpression("expinc", common_level);
        ret.morph = source.getIntExpression("morph", common_level);
        ret.cp = source.getIntExpression("cp", common_level);
        ret.nuffSkill = source.getIntExpression("nuffSkill", common_level);
        ret.mobCount = source.getIntExpression("mobCount", common_level, 1);
        ret.exp = source.getIntExpression("exp", common_level);
        ret.mastery = source.getIntExpression("mastery", common_level);
        ret.pad = source.getIntExpression("pad", common_level);
        ret.pdd = source.getIntExpression("pdd", common_level);
        ret.mad = source.getIntExpression("mad", common_level);
        ret.mdd = source.getIntExpression("mdd", common_level);
        ret.emhp = source.getIntExpression("emhp", common_level);
        ret.emmp = source.getIntExpression("emmp", common_level);
        ret.epad = source.getIntExpression("epad", common_level);
        ret.epdd = source.getIntExpression("epdd", common_level);
        ret.emdd = source.getIntExpression("emdd", common_level);
        ret.acc = source.getIntExpression("acc", common_level);
        ret.eva = source.getIntExpression("eva", common_level);
        ret.speed = source.getIntExpression("speed", common_level);
        ret.jump = source.getIntExpression("jump", common_level);
        ret.expBuff = source.getIntExpression("expBuff", common_level);
        ret.cashBuff = source.getIntExpression("cashBuff", common_level);
        ret.itemupbyitem = source.getIntExpression("itemupbyitem", common_level);
        ret.mesoupbyitem = source.getIntExpression("mesoupbyitem", common_level);
        ret.berserk = source.getIntExpression("berserk", common_level);
        ret.berserk2 = source.getIntExpression("berserk2", common_level);
        ret.illusion = source.getIntExpression("illusion", common_level);
        if (source.getChildByPath("lt") != null) {
            ret.lt = source.getPoint("lt");
            ret.rb = source.getPoint("rb");
        }
        ret.x = source.getIntExpression("x", common_level);
        ret.y = source.getIntExpression("y", common_level);
        ret.z = source.getIntExpression("z", common_level);
        ret.damage = source.getIntExpression("damage", common_level);
        ret.attackCount = source.getIntExpression("attackCount", common_level, 1);
        ret.bulletCount = source.getIntExpression("bulletCount", common_level, 1);
        ret.bulletConsume = source.getIntExpression("bulletConsume", common_level);
        ret.moneyCon = source.getIntExpression("moneyCon", common_level);
        ret.itemCon = source.getInt("itemCon");
        ret.itemConNo = source.getInt("itemConNo");
        ret.moveTo = source.getInt("moveTo", -1);
        return ret;
    }
}
