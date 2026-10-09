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

import tacos.config.Content;
import tacos.config.Config;
import tacos.config.Region;
import odin.server.quest.MapleQuestRequirementType;
import java.util.List;
import java.util.LinkedList;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.ArrayList;

/**
 *
 * @author Riremito
 */
public class QuestWz extends WzXML {

    public QuestWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Quest" : "Quest.wz");
    }

    public MapleData getAct() {
        return getData("Act.img");
    }

    public MapleData getCheck() {
        return getData("Check.img");
    }

    public MapleData getQuestInfo() {
        return getData("QuestInfo.img");
    }

    public MapleData getPQuest() {
        return getData("PQuest.img");
    }

    public static class QuestDefinition {

        public MapleData basedata1;
        public MapleData basedata2;
        public MapleData questInfo;
        public MapleData pquestInfo;
    }

    public QuestDefinition getQuestDefinition(int id) {
        MapleData check_img = getCheck();
        MapleData act_img = getAct();
        // KMS1
        if (check_img == null || act_img == null) {
            return null;
        }

        MapleData basedata1 = check_img.getChildByPath(String.valueOf(id));
        MapleData basedata2 = act_img.getChildByPath(String.valueOf(id));

        if (basedata1 == null || basedata2 == null) {
            return null;
        }

        QuestDefinition def = new QuestDefinition();
        def.basedata1 = basedata1;
        def.basedata2 = basedata2;
        def.questInfo = getQuestInfo().getChildByPath(String.valueOf(id));

        // not in KMS55
        if (Config.GreaterOrEqual(Region.KMS, 65)) {
            def.pquestInfo = getPQuest().getChildByPath(String.valueOf(id));
        }

        return def;
    }

    public static class QuestInfoData {

        public String name = "";
        public boolean autoStart;
        public boolean autoPreComplete;
        public int viewMedalItem;
        public int selectedSkillID;
    }

    public QuestInfoData parseQuestInfo(MapleData questInfo) {
        QuestInfoData ret = new QuestInfoData();
        if (questInfo != null) {
            ret.name = WzDataTool.getStringPath("name", questInfo, "");
            ret.autoStart = WzDataTool.getIntPath("autoStart", questInfo, 0) == 1;
            ret.autoPreComplete = WzDataTool.getIntPath("autoPreComplete", questInfo, 0) == 1;
            ret.viewMedalItem = WzDataTool.getIntPath("viewMedalItem", questInfo, 0);
            ret.selectedSkillID = WzDataTool.getIntPath("selectedSkillID", questInfo, 0);
        }
        return ret;
    }

    public void parsePartyQuestInfo(MapleData pquestInfo, Map<String, List<SimpleImmutableEntry<String, SimpleImmutableEntry<String, Integer>>>> partyQuestInfo) {
        if (pquestInfo != null) {
            for (MapleData d : pquestInfo.getChildByPath("rank")) {
                List<SimpleImmutableEntry<String, SimpleImmutableEntry<String, Integer>>> pInfo = new ArrayList<>();
                //LinkedHashMap<String, List<Pair<String, Pair<String, Integer>>>>
                for (MapleData c : d) {
                    for (MapleData b : c) {
                        pInfo.add(new SimpleImmutableEntry<>(c.getName(), new SimpleImmutableEntry<>(b.getName(), WzDataTool.getInt(b, 0))));
                    }
                }
                partyQuestInfo.put(d.getName(), pInfo);
            }
        }
    }

    public SimpleImmutableEntry<Integer, Integer> parseMobRequirementEntry(MapleData mob) {
        return new SimpleImmutableEntry<>(WzDataTool.getInt(mob.getChildByPath("id")), WzDataTool.getInt(mob.getChildByPath("count"), 0));
    }

    public static class QuestRequirementData {

        public int intStore;
        public String stringStore;
        public List<SimpleImmutableEntry<Integer, Integer>> dataStore;
    }

    public QuestRequirementData parseQuestRequirement(MapleQuestRequirementType type, MapleData data) {
        QuestRequirementData ret = new QuestRequirementData();

        switch (type) {
            case job: {
                final List<MapleData> child = data.getChildren();
                ret.dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    ret.dataStore.add(new SimpleImmutableEntry<>(i, WzDataTool.getInt(child.get(i), -1)));
                }
                break;
            }
            case skill: {
                final List<MapleData> child = data.getChildren();
                ret.dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    final MapleData childdata = child.get(i);
                    ret.dataStore.add(new SimpleImmutableEntry<>(WzDataTool.getInt(childdata.getChildByPath("id"), 0),
                            WzDataTool.getInt(childdata.getChildByPath("acquire"), 0)));
                }
                break;
            }
            case quest: {
                final List<MapleData> child = data.getChildren();
                ret.dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    final MapleData childdata = child.get(i);
                    ret.dataStore.add(new SimpleImmutableEntry<>(WzDataTool.getInt(childdata.getChildByPath("id")),
                            WzDataTool.getInt(childdata.getChildByPath("state"), 0)));
                }
                break;
            }
            case item: {
                final List<MapleData> child = data.getChildren();
                ret.dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    final MapleData childdata = child.get(i);
                    ret.dataStore.add(new SimpleImmutableEntry<>(WzDataTool.getInt(childdata.getChildByPath("id")),
                            WzDataTool.getInt(childdata.getChildByPath("count"), 0)));
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
                ret.intStore = WzDataTool.getInt(data, -1);
                break;
            }
            case end: {
                ret.stringStore = WzDataTool.getString(data, null);
                break;
            }
            case mob: {
                final List<MapleData> child = data.getChildren();
                ret.dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    final MapleData childdata = child.get(i);
                    ret.dataStore.add(new SimpleImmutableEntry<>(WzDataTool.getInt(childdata.getChildByPath("id"), 0),
                            WzDataTool.getInt(childdata.getChildByPath("count"), 0)));
                }
                break;
            }
            case fieldEnter: {
                final MapleData zeroField = data.getChildByPath("0");
                if (zeroField != null) {
                    ret.intStore = WzDataTool.getInt(zeroField);
                } else {
                    ret.intStore = -1;
                }
                break;
            }
            case mbcard: {
                final List<MapleData> child = data.getChildren();
                ret.dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    final MapleData childdata = child.get(i);
                    ret.dataStore.add(new SimpleImmutableEntry<>(WzDataTool.getInt(childdata.getChildByPath("id"), 0),
                            WzDataTool.getInt(childdata.getChildByPath("min"), 0)));
                }
                break;
            }
            case pet: {
                ret.dataStore = new LinkedList<>();

                for (MapleData child : data) {
                    ret.dataStore.add(new SimpleImmutableEntry<>(-1, WzDataTool.getIntPath("id", child, 0)));
                }
                break;
            }
        }
        return ret;
    }
}
