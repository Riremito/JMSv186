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

    private Map<Integer, Skill> map_Skill = null;
    private Map<Integer, List<Integer>> map_SkillsByJob = null;
    private LinkedHashMap<Integer, TacosSummonSkill> summon_skills = null;

    public Map<Integer, List<Integer>> getSkillsByJob() {
        if (map_SkillsByJob == null) {
            getSkill();
        }
        return map_SkillsByJob;
    }

    public LinkedHashMap<Integer, TacosSummonSkill> getSummonSkills() {
        if (this.summon_skills == null) {
            this.summon_skills = new LinkedHashMap<>();
            getSkill();
        }
        return this.summon_skills;
    }

    public boolean isSummonSkill(int skill_id) {
        if (getSummonSkills().get(skill_id) != null) {
            return true;
        }
        return false;
    }

    public Map<Integer, Skill> getSkill() {
        if (map_Skill != null) {
            return map_Skill;
        }
        map_Skill = new HashMap<>();
        map_SkillsByJob = new HashMap<>();

        int skillid;
        MapleData summon_data;
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

                                summon_data = data2.getChildByPath("summon");
                                if (summon_data != null) {
                                    TacosSummonSkill tss = new TacosSummonSkill(skillid);
                                    getSummonSkills().put(skillid, tss);
                                }
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
        final int skillType = WzDataTool.getIntPath("skillType", data, -1);
        final String elem = WzDataTool.getStringPath("elemAttr", data, null);
        if (elem != null) {
            ret.setElement(Element.getFromChar(elem.charAt(0)));
        } else {
            ret.setElement(Element.NEUTRAL);
        }
        ret.setInvisible(WzDataTool.getIntPath("invisible", data, 0) > 0);
        ret.setTimeLimited(WzDataTool.getIntPath("timeLimited", data, 0) > 0);
        ret.setMasterLevel(WzDataTool.getIntPath("masterLevel", data, 0));
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
            isBuff |= action_ != null && WzDataTool.getStringPath("0", action_, "").equals("alert2");
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
                ret.addEffect(MapleStatEffect.loadSkillEffectFromData(level, id, isBuff, Byte.parseByte(level.getName())));
            }
        } else {
            // v188+
            MapleData common = data.getChildByPath("common");
            if (common != null) {
                // after bigbang updates
                int max_level = WzDataTool.getIntPath("maxLevel", common, -1);
                for (int level = 1; level <= max_level; level++) {
                    ret.addEffect(MapleStatEffect.loadSkillEffectFromData(common, id, isBuff, (byte) level, level)); // 変数
                }
            } else {
                // old skills
                for (final MapleData level : data.getChildByPath("level")) {
                    ret.addEffect(MapleStatEffect.loadSkillEffectFromData(level, id, isBuff, Byte.parseByte(level.getName())));
                }
            }
        }

        final MapleData reqDataRoot = data.getChildByPath("req");
        if (reqDataRoot != null) {
            for (final MapleData reqData : reqDataRoot.getChildren()) {
                ret.setRequiredSkill(Integer.parseInt(reqData.getName()));
                ret.setLevel((byte) WzDataTool.getInt(reqData, 1));
            }
        }
        int animationTime = 0;
        if (effect != null) {
            for (final MapleData effectEntry : effect) {
                animationTime += WzDataTool.getIntPath("delay", effectEntry, 0);
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
            toSummon.add(WzDataTool.getInt(skillData.getChildByPath(String.valueOf(i)), 0));
        }

        final MapleData ltd = skillData.getChildByPath("lt");
        Point lt = null;
        Point rb = null;
        if (ltd != null) {
            lt = WzDataTool.getPoint(skillData.getChildByPath("lt"));
            rb = WzDataTool.getPoint(skillData.getChildByPath("rb"));
        }

        MobSkill ret = new MobSkill(skillId, level);
        ret.addSummons(toSummon);
        ret.setCoolTime(WzDataTool.getIntPath("interval", skillData, 0) * 1000);
        ret.setDuration(WzDataTool.getIntPath("time", skillData, 1) * 1000);
        ret.setHp(WzDataTool.getIntPath("hp", skillData, 100));
        ret.setMpCon(WzDataTool.getInt(skillData.getChildByPath("mpCon"), 0));
        ret.setSpawnEffect(WzDataTool.getIntPath("summonEffect", skillData, 0));
        ret.setX(WzDataTool.getIntPath("x", skillData, 1));
        ret.setY(WzDataTool.getIntPath("y", skillData, 1));
        ret.setProp(WzDataTool.getIntPath("prop", skillData, 100) / 100f);
        ret.setLimit((short) WzDataTool.getIntPath("limit", skillData, 0));
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
                mobSkillID = WzDataTool.getIntPath("mobSkillID", md, 0);
            } catch (NumberFormatException e) {
                // MCSkill.img/4/mobSkillID
                DebugLogger.ErrorLog("MCSkill.img/" + md.getName() + "/mobSkillID");
                continue;
            }
            map_MCSkill.put(Integer.parseInt(md.getName()), new MapleCarnivalFactory.MCSkill(WzDataTool.getIntPath("spendCP", md, 0), mobSkillID, WzDataTool.getIntPath("level", md, 0), WzDataTool.getIntPath("target", md, 1) > 1));
        }
        return map_MCSkill;
    }

    public Map<Integer, MapleCarnivalFactory.MCSkill> getMCGuardian() {
        if (map_MCGuardian != null) {
            return map_MCGuardian;
        }

        map_MCGuardian = new HashMap<>();
        for (MapleData md : getData("MCGuardian.img")) {
            map_MCGuardian.put(Integer.parseInt(md.getName()), new MapleCarnivalFactory.MCSkill(WzDataTool.getIntPath("spendCP", md, 0), WzDataTool.getIntPath("mobSkillID", md, 0), WzDataTool.getIntPath("level", md, 0), true));
        }
        return map_MCGuardian;
    }

}
