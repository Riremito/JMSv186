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

import debug.Debug;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import server.life.MapleMonster;
import server.life.MobAttackInfo;
import tools.Pair;
import tools.StringUtil;

/**
 *
 * @author Riremito
 */
public class DW_Mob {

    private static DataWz wz = null;

    private static DataWz getWz() {
        if (wz == null) {
            wz = new DataWz("Mob.wz");
        }
        return wz;
    }

    public static MapleDataProvider getWzRoot() {
        return getWz().getWzRoot();
    }

    private static Map<Pair<Integer, Integer>, MobAttackInfo> map_mobAttacks = null;

    public static MobAttackInfo getMobAttackInfo(MapleMonster mob, int attack) {
        if (map_mobAttacks == null) {
            map_mobAttacks = new HashMap<>();
        }
        MobAttackInfo mai_found = map_mobAttacks.get(new Pair<>(mob.getId(), attack));
        if (mai_found != null) {
            return mai_found;
        }

        MobAttackInfo ret = new MobAttackInfo();
        MapleData mobData = getWzRoot().getData(StringUtil.getLeftPaddedStr(Integer.toString(mob.getId()) + ".img", '0', 11));
        if (mobData != null) {
            MapleData infoData = mobData.getChildByPath("info/link");
            if (infoData != null) {
                String linkedmob = MapleDataTool.getString("info/link", mobData);
                mobData = getWzRoot().getData(StringUtil.getLeftPaddedStr(linkedmob + ".img", '0', 11));
            }
            final MapleData attackData = mobData.getChildByPath("attack" + (attack + 1) + "/info");
            if (attackData != null) {
                ret.setDeadlyAttack(attackData.getChildByPath("deadlyAttack") != null);
                ret.setMpBurn(MapleDataTool.getInt("mpBurn", attackData, 0));
                ret.setDiseaseSkill(MapleDataTool.getInt("disease", attackData, 0));
                ret.setDiseaseLevel(MapleDataTool.getInt("level", attackData, 0));
                ret.setMpCon(MapleDataTool.getInt("conMP", attackData, 0));
            }
        }
        map_mobAttacks.put(new Pair<>(mob.getId(), attack), ret);
        return ret;
    }

    private static Map<Integer, List<Integer>> map_QuestCountGroup = null;

    public static Map<Integer, List<Integer>> getQuestCountGroup() {
        if (map_QuestCountGroup != null) {
            return map_QuestCountGroup;
        }
        map_QuestCountGroup = new HashMap<>();
        for (MapleDataDirectoryEntry mapz : getWzRoot().getRoot().getSubdirectories()) {
            if (mapz.getName().equals("QuestCountGroup")) {
                for (MapleDataFileEntry entry : mapz.getFiles()) {
                    final int id = Integer.parseInt(entry.getName().substring(0, entry.getName().length() - 4));
                    MapleData dat = getWzRoot().getData("QuestCountGroup/" + entry.getName());
                    if (dat != null && dat.getChildByPath("info") != null) {
                        List<Integer> z = new ArrayList<Integer>();
                        for (MapleData da : dat.getChildByPath("info")) {
                            z.add(MapleDataTool.getInt(da, 0));
                        }
                        map_QuestCountGroup.put(id, z);
                    } else {
                        Debug.ErrorLog("null questcountgroup");
                    }
                }
            }
        }

        return map_QuestCountGroup;
    }
}
