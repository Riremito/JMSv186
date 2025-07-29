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
package data.wz;

import client.ISkill;
import client.MapleCharacter;
import client.Skill;
import static client.SkillFactory.getName;
import client.SummonSkillEntry;
import debug.Debug;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import provider.MapleData;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import server.MapleCarnivalFactory;
import server.life.MobSkill;
import tools.Pair;

/**
 *
 * @author Riremito
 */
public class DW_Skill {

    private static DataWz wz = null;

    private static DataWz getWz() {
        if (wz == null) {
            wz = new DataWz("Skill.wz");
        }
        return wz;
    }

    public static MapleDataProvider getWzRoot() {
        return getWz().getWzRoot();
    }

    private static Map<Integer, ISkill> map_Skill = null;
    private static Map<Integer, List<Integer>> map_SkillsByJob = null;
    private static Map<Integer, SummonSkillEntry> map_SummonSkillInformation = null;

    public static Map<Integer, List<Integer>> getSkillsByJob() {
        if (map_SkillsByJob == null) {
            getSkill();
        }
        return map_SkillsByJob;
    }

    public static Map<Integer, SummonSkillEntry> getSummonSkillInformation() {
        if (map_SummonSkillInformation == null) {
            getSkill();
        }
        return map_SummonSkillInformation;
    }

    public static Map<Integer, ISkill> getSkill() {
        if (map_Skill != null) {
            return map_Skill;
        }
        map_Skill = new HashMap<>();
        map_SkillsByJob = new HashMap<>();
        map_SummonSkillInformation = new HashMap<>();

        int skillid;
        MapleData summon_data;
        SummonSkillEntry sse;
        for (MapleDataFileEntry topDir : getWzRoot().getRoot().getFiles()) { // Loop thru jobs
            if (topDir.getName().length() <= 8) {
                for (MapleData data : getWzRoot().getData(topDir.getName())) { // Loop thru each jobs
                    if (data.getName().equals("skill")) {
                        for (MapleData data2 : data) { // Loop thru each jobs
                            if (data2 != null) {
                                skillid = Integer.parseInt(data2.getName());

                                Skill skil = Skill.loadFromData(skillid, data2);
                                List<Integer> job = map_SkillsByJob.get(skillid / 10000);
                                if (job == null) {
                                    job = new ArrayList<Integer>();
                                    map_SkillsByJob.put(skillid / 10000, job);
                                }
                                job.add(skillid);

                                // THMS meme
                                String skill_name = "";
                                try {
                                    skill_name = getName(skillid);
                                } catch (RuntimeException e) {
                                    Debug.ErrorLog("" + skillid);
                                }
                                skil.setName(skill_name);
                                map_Skill.put(skillid, skil);

                                summon_data = data2.getChildByPath("summon/attack1/info");
                                if (summon_data != null) {
                                    sse = new SummonSkillEntry();
                                    sse.attackAfter = (short) MapleDataTool.getInt("attackAfter", summon_data, 999999);
                                    sse.type = (byte) MapleDataTool.getInt("type", summon_data, 0);
                                    sse.mobCount = (byte) MapleDataTool.getInt("mobCount", summon_data, 1);
                                    map_SummonSkillInformation.put(skillid, sse);
                                }
                            }
                        }
                    }
                }
            }
        }
        return map_Skill;
    }

    // test
    public static ArrayList<Integer> getBasicSkill(MapleCharacter chr, String job_img_name) {
        ArrayList<Integer> list = new ArrayList<Integer>();

        MapleData md_job = getWzRoot().getData(job_img_name);
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
    private static Map<Pair<Integer, Integer>, MobSkill> map_mobSkills = null;
    private static MapleData img_MobSkill = null;

    private static MapleData getMobSkill() {
        if (img_MobSkill == null) {
            img_MobSkill = getWz().loadData("MobSkill.img");
        }
        return img_MobSkill;
    }

    public static MobSkill getMobSkillData(int skillId, int level) {
        if (map_mobSkills == null) {
            map_mobSkills = new HashMap<>();
        }

        MobSkill ms_found = map_mobSkills.get(new Pair<>(skillId, level));
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
            toSummon.add(MapleDataTool.getInt(skillData.getChildByPath(String.valueOf(i)), 0));
        }

        final MapleData ltd = skillData.getChildByPath("lt");
        Point lt = null;
        Point rb = null;
        if (ltd != null) {
            lt = (Point) ltd.getData();
            rb = (Point) skillData.getChildByPath("rb").getData();
        }

        MobSkill ret = new MobSkill(skillId, level);
        ret.addSummons(toSummon);
        ret.setCoolTime(MapleDataTool.getInt("interval", skillData, 0) * 1000);
        ret.setDuration(MapleDataTool.getInt("time", skillData, 1) * 1000);
        ret.setHp(MapleDataTool.getInt("hp", skillData, 100));
        ret.setMpCon(MapleDataTool.getInt(skillData.getChildByPath("mpCon"), 0));
        ret.setSpawnEffect(MapleDataTool.getInt("summonEffect", skillData, 0));
        ret.setX(MapleDataTool.getInt("x", skillData, 1));
        ret.setY(MapleDataTool.getInt("y", skillData, 1));
        ret.setProp(MapleDataTool.getInt("prop", skillData, 100) / 100f);
        ret.setLimit((short) MapleDataTool.getInt("limit", skillData, 0));
        ret.setLtRb(lt, rb);

        map_mobSkills.put(new Pair<>(skillId, level), ret);

        return ret;
    }

    // Monster Carnival
    private static Map<Integer, MapleCarnivalFactory.MCSkill> map_MCSkill = null;
    private static Map<Integer, MapleCarnivalFactory.MCSkill> map_MCGuardian = null;

    public static Map<Integer, MapleCarnivalFactory.MCSkill> getMCSkill() {
        if (map_MCSkill != null) {
            return map_MCSkill;
        }

        map_MCSkill = new HashMap<>();
        for (MapleData md : getWz().loadData("MCSkill.img")) {
            // THMS meme
            int mobSkillID = 0;
            try {
                mobSkillID = MapleDataTool.getInt("mobSkillID", md, 0);
            } catch (NumberFormatException e) {
                // MCSkill.img/4/mobSkillID
                Debug.ErrorLog("MCSkill.img/" + md.getName() + "/mobSkillID");
                continue;
            }
            map_MCSkill.put(Integer.parseInt(md.getName()), new MapleCarnivalFactory.MCSkill(MapleDataTool.getInt("spendCP", md, 0), mobSkillID, MapleDataTool.getInt("level", md, 0), MapleDataTool.getInt("target", md, 1) > 1));
        }
        return map_MCSkill;
    }

    public static Map<Integer, MapleCarnivalFactory.MCSkill> getMCGuardian() {
        if (map_MCGuardian != null) {
            return map_MCGuardian;
        }

        map_MCGuardian = new HashMap<>();
        for (MapleData md : getWz().loadData("MCGuardian.img")) {
            map_MCGuardian.put(Integer.parseInt(md.getName()), new MapleCarnivalFactory.MCSkill(MapleDataTool.getInt("spendCP", md, 0), MapleDataTool.getInt("mobSkillID", md, 0), MapleDataTool.getInt("level", md, 0), true));
        }
        return map_MCGuardian;
    }

}
