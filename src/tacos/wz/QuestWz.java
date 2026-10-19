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

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import odin.server.quest.MapleQuestActionType;
import odin.server.quest.MapleQuestRequirementType;
import tacos.config.Config;
import tacos.config.Content;
import tacos.config.Region;

/**
 *
 * @author Riremito
 */
public class QuestWz extends WzXML {

    public QuestWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Quest" : "Quest.wz");
    }

    private MapleData getActImg() {
        return getData("Act.img");
    }

    private MapleData getCheckImg() {
        return getData("Check.img");
    }

    private MapleData getQuestInfoImg() {
        return getData("QuestInfo.img");
    }

    private MapleData getPQuestImg() {
        return getData("PQuest.img");
    }

    @Getter
    public class QuestInfo {

        private int id;
        private String name = "";
        private boolean autoStart;
        private boolean autoPreComplete;
        private int viewMedalItem;
        private int selectedSkillID;
        private boolean repeatable;
        private boolean customend;
        private final List<QuestRequirementInfo> startReqs = new LinkedList<>();
        private final List<QuestRequirementInfo> completeReqs = new LinkedList<>();
        private final List<QuestActionInfo> startActs = new LinkedList<>();
        private final List<QuestActionInfo> completeActs = new LinkedList<>();
        private final Map<Integer, Integer> relevantMobs = new LinkedHashMap<>();
        private final Map<String, List<SimpleImmutableEntry<String, SimpleImmutableEntry<String, Integer>>>> partyQuestInfo = new LinkedHashMap<>(); //[rank, [more/less/equal, [property, value]]]
    }

    @Getter
    public class QuestRequirementInfo {

        private MapleQuestRequirementType type;
        private int intStore;
        private String stringStore;
        private List<SimpleImmutableEntry<Integer, Integer>> dataStore;
    }

    @Getter
    public class QuestActionInfo {

        private MapleQuestActionType type;
        private int value; // exp, nextQuest, money, pop, buffItemID
        private final List<QuestActionItemInfo> items = new ArrayList<>();
        private final List<SimpleImmutableEntry<Integer, Integer>> quests = new ArrayList<>(); // [id, state]
        private final List<QuestActionSkillInfo> skills = new ArrayList<>();
        private final List<QuestActionSpInfo> sps = new ArrayList<>();
    }

    @Getter
    public class QuestActionItemInfo {

        private int id;
        private int count;
        private int period;
        private boolean hasProp;
        private int prop;
        private int gender;
        private boolean hasJob;
        private int job;
        private int jobEx;
    }

    @Getter
    public class QuestActionSkillInfo {

        private int id;
        private int skillLevel;
        private int masterLevel;
        private final List<Integer> job = new ArrayList<>();
    }

    @Getter
    public class QuestActionSpInfo {

        private int spValue;
        private List<Integer> job; // null : no job
    }

    private final HashMap<Integer, QuestInfo> quest_info_list = new HashMap<>();

    public QuestInfo getQuestInfo(int quest_id) {
        QuestInfo ret = this.quest_info_list.get(quest_id);
        if (ret != null) {
            return ret;
        }

        MapleData md_check_img = getCheckImg();
        MapleData md_act_img = getActImg();
        // KMS1
        if (md_check_img == null || md_act_img == null) {
            return null;
        }

        MapleData md_check = md_check_img.getChildByPath(String.valueOf(quest_id));
        MapleData md_act = md_act_img.getChildByPath(String.valueOf(quest_id));
        if (md_check == null || md_act == null) {
            return null;
        }

        ret = new QuestInfo();
        ret.id = quest_id;

        MapleData md_start_reqs = md_check.getChildByPath("0");
        if (md_start_reqs != null) {
            for (MapleData md_req : md_start_reqs) {
                QuestRequirementInfo req = parseRequirement(md_start_reqs, md_req);
                if (req.type == MapleQuestRequirementType.interval) {
                    ret.repeatable = true;
                } else if (req.type == MapleQuestRequirementType.mob) {
                    parseRelevantMobs(md_req, ret.relevantMobs);
                }
                ret.startReqs.add(req);
            }
        }

        MapleData md_complete_reqs = md_check.getChildByPath("1");
        if (md_complete_reqs != null) {
            for (MapleData md_req : md_complete_reqs) {
                QuestRequirementInfo req = parseRequirement(md_complete_reqs, md_req);
                if (req.type == MapleQuestRequirementType.mob) {
                    parseRelevantMobs(md_req, ret.relevantMobs);
                } else if (req.type == MapleQuestRequirementType.endscript) {
                    ret.customend = true;
                }
                ret.completeReqs.add(req);
            }
        }

        MapleData md_start_acts = md_act.getChildByPath("0");
        if (md_start_acts != null) {
            for (MapleData md_act_data : md_start_acts) {
                ret.startActs.add(parseAction(md_start_acts, md_act_data));
            }
        }

        MapleData md_complete_acts = md_act.getChildByPath("1");
        if (md_complete_acts != null) {
            for (MapleData md_act_data : md_complete_acts) {
                ret.completeActs.add(parseAction(md_complete_acts, md_act_data));
            }
        }

        MapleData md_quest_info_img = getQuestInfoImg();
        MapleData md_quest_info = (md_quest_info_img != null) ? md_quest_info_img.getChildByPath(String.valueOf(quest_id)) : null;
        if (md_quest_info != null) {
            ret.name = md_quest_info.getString("name");
            ret.autoStart = md_quest_info.getInt("autoStart") == 1;
            ret.autoPreComplete = md_quest_info.getInt("autoPreComplete") == 1;
            ret.viewMedalItem = md_quest_info.getInt("viewMedalItem");
            ret.selectedSkillID = md_quest_info.getInt("selectedSkillID");
        }

        // not in KMS55
        if (Config.GreaterOrEqual(Region.KMS, 65)) {
            MapleData md_pquest_info = getPQuestImg().getChildByPath(String.valueOf(quest_id));
            if (md_pquest_info != null) {
                for (MapleData d : md_pquest_info.getChildByPath("rank")) {
                    List<SimpleImmutableEntry<String, SimpleImmutableEntry<String, Integer>>> pInfo = new ArrayList<>();
                    for (MapleData c : d) {
                        for (MapleData b : c) {
                            pInfo.add(new SimpleImmutableEntry<>(c.getName(), new SimpleImmutableEntry<>(b.getName(), c.getInt(b.getName()))));
                        }
                    }
                    ret.partyQuestInfo.put(d.getName(), pInfo);
                }
            }
        }

        this.quest_info_list.put(quest_id, ret);
        return ret;
    }

    private void parseRelevantMobs(MapleData md_req, Map<Integer, Integer> relevantMobs) {
        for (MapleData md_mob : md_req) {
            relevantMobs.put(md_mob.getInt("id"), md_mob.getInt("count"));
        }
    }

    private QuestRequirementInfo parseRequirement(MapleData md_reqs, MapleData data) {
        QuestRequirementInfo ret = new QuestRequirementInfo();
        ret.type = MapleQuestRequirementType.getByWZName(data.getName());

        switch (ret.type) {
            case job: {
                List<MapleData> child = data.getChildren();
                ret.dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    ret.dataStore.add(new SimpleImmutableEntry<>(i, data.getInt(child.get(i).getName(), -1)));
                }
                break;
            }
            case skill: {
                List<MapleData> child = data.getChildren();
                ret.dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    MapleData childdata = child.get(i);
                    ret.dataStore.add(new SimpleImmutableEntry<>(childdata.getInt("id"), childdata.getInt("acquire")));
                }
                break;
            }
            case quest: {
                List<MapleData> child = data.getChildren();
                ret.dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    MapleData childdata = child.get(i);
                    ret.dataStore.add(new SimpleImmutableEntry<>(childdata.getInt("id"), childdata.getInt("state")));
                }
                break;
            }
            case item: {
                List<MapleData> child = data.getChildren();
                ret.dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    MapleData childdata = child.get(i);
                    ret.dataStore.add(new SimpleImmutableEntry<>(childdata.getInt("id"), childdata.getInt("count")));
                }
                break;
            }
            case pettamenessmin:
            case npc:
            case questComplete:
            case pop:
            case interval:
            case mbmin:
            case lvmax:
            case lvmin: {
                ret.intStore = md_reqs.getInt(data.getName(), -1);
                break;
            }
            case end: {
                ret.stringStore = md_reqs.getString(data.getName(), null);
                break;
            }
            case mob: {
                List<MapleData> child = data.getChildren();
                ret.dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    MapleData childdata = child.get(i);
                    ret.dataStore.add(new SimpleImmutableEntry<>(childdata.getInt("id"), childdata.getInt("count")));
                }
                break;
            }
            case fieldEnter: {
                ret.intStore = data.getInt("0", -1);
                break;
            }
            case mbcard: {
                List<MapleData> child = data.getChildren();
                ret.dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    MapleData childdata = child.get(i);
                    ret.dataStore.add(new SimpleImmutableEntry<>(childdata.getInt("id"), childdata.getInt("min")));
                }
                break;
            }
            case pet: {
                ret.dataStore = new LinkedList<>();

                for (MapleData child : data) {
                    ret.dataStore.add(new SimpleImmutableEntry<>(-1, child.getInt("id")));
                }
                break;
            }
            default: {
                break;
            }
        }
        return ret;
    }

    private QuestActionInfo parseAction(MapleData md_acts, MapleData data) {
        QuestActionInfo ret = new QuestActionInfo();
        ret.type = MapleQuestActionType.getByWZName(data.getName());

        switch (ret.type) {
            case exp:
            case nextQuest:
            case money:
            case pop: {
                ret.value = md_acts.getInt(data.getName());
                break;
            }
            case buffItemID: {
                ret.value = md_acts.getInt(data.getName(), -1);
                break;
            }
            case item: {
                for (MapleData md_item : data) {
                    QuestActionItemInfo item = new QuestActionItemInfo();
                    item.id = md_item.getInt("id", -1);
                    item.count = md_item.getInt("count", 1);
                    item.period = md_item.getInt("period");
                    item.hasProp = md_item.getChildByPath("prop") != null;
                    item.prop = md_item.getInt("prop");
                    item.gender = md_item.getInt("gender", 2);
                    item.hasJob = md_item.getChildByPath("job") != null;
                    item.job = md_item.getInt("job");
                    item.jobEx = md_item.getInt("jobEx");
                    ret.items.add(item);
                }
                break;
            }
            case quest: {
                for (MapleData md_quest : data) {
                    ret.quests.add(new SimpleImmutableEntry<>(md_quest.getInt("id"), md_quest.getInt("state")));
                }
                break;
            }
            case skill: {
                for (MapleData md_skill : data) {
                    QuestActionSkillInfo skill = new QuestActionSkillInfo();
                    skill.id = md_skill.getInt("id");
                    skill.skillLevel = md_skill.getInt("skillLevel");
                    skill.masterLevel = md_skill.getInt("masterLevel");
                    MapleData md_job = md_skill.getChildByPath("job");
                    if (md_job != null) {
                        for (MapleData md_job_id : md_job) {
                            skill.job.add(md_job.getInt(md_job_id.getName()));
                        }
                    }
                    ret.skills.add(skill);
                }
                break;
            }
            case sp: {
                for (MapleData md_sp : data) {
                    QuestActionSpInfo sp = new QuestActionSpInfo();
                    sp.spValue = md_sp.getInt("sp_value");
                    MapleData md_job = md_sp.getChildByPath("job");
                    if (md_job != null) {
                        sp.job = new ArrayList<>();
                        for (MapleData md_job_id : md_job) {
                            sp.job.add(md_job.getInt(md_job_id.getName()));
                        }
                    }
                    ret.sps.add(sp);
                }
                break;
            }
            default: {
                break;
            }
        }
        return ret;
    }
}
