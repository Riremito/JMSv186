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
import tacos.debug.DebugLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import odin.server.life.MapleMonster;
import odin.server.life.MobAttackInfo;
import tacos.odin.OdinPair;
import odin.provider.IMapleData;
import odin.provider.IMapleDataDirectoryEntry;
import odin.provider.IMapleDataEntity;

/**
 *
 * @author Riremito
 */
public class MobWz extends WzXML {

    public MobWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Mob" : "Mob.wz");
    }

    public IMapleData getImg(int mob_id) {
        String target_img_path = String.format("%07d.img", mob_id);
        return getData(target_img_path);
    }

    private Map<OdinPair<Integer, Integer>, MobAttackInfo> map_mobAttacks = null;

    public MobAttackInfo getMobAttackInfo(MapleMonster mob, int attack) {
        if (map_mobAttacks == null) {
            map_mobAttacks = new HashMap<>();
        }
        MobAttackInfo mai_found = map_mobAttacks.get(new OdinPair<>(mob.getId(), attack));
        if (mai_found != null) {
            return mai_found;
        }

        MobAttackInfo ret = new MobAttackInfo();
        IMapleData mobData = getImg(mob.getId());
        if (mobData != null) {
            IMapleData infoData = mobData.getChildByPath("info/link");
            if (infoData != null) {
                int link_id = WzDataTool.getIntPath("info/link", mobData, 0);
                mobData = getImg(link_id);
            }
            IMapleData attackData = mobData.getChildByPath("attack" + (attack + 1) + "/info");
            if (attackData != null) {
                ret.setDeadlyAttack(attackData.getChildByPath("deadlyAttack") != null);
                ret.setMpBurn(WzDataTool.getIntPath("mpBurn", attackData, 0));
                ret.setDiseaseSkill(WzDataTool.getIntPath("disease", attackData, 0));
                ret.setDiseaseLevel(WzDataTool.getIntPath("level", attackData, 0));
                ret.setMpCon(WzDataTool.getIntPath("conMP", attackData, 0));
            }
        }
        map_mobAttacks.put(new OdinPair<>(mob.getId(), attack), ret);
        return ret;
    }

    private Map<Integer, List<Integer>> map_QuestCountGroup = null;

    public Map<Integer, List<Integer>> getQuestCountGroup() {
        if (map_QuestCountGroup != null) {
            return map_QuestCountGroup;
        }
        map_QuestCountGroup = new HashMap<>();
        for (IMapleDataDirectoryEntry mapz : getRootDirectory().getSubDirectories()) {
            if (mapz.getName().equals("QuestCountGroup")) {
                for (IMapleDataEntity entry : mapz.getFiles()) {
                    final int id = Integer.parseInt(entry.getName().substring(0, entry.getName().length() - 4));
                    IMapleData dat = getData("QuestCountGroup/" + entry.getName());
                    if (dat != null && dat.getChildByPath("info") != null) {
                        List<Integer> z = new ArrayList<>();
                        for (IMapleData da : dat.getChildByPath("info")) {
                            z.add(WzDataTool.getInt(da, 0));
                        }
                        map_QuestCountGroup.put(id, z);
                    } else {
                        DebugLogger.ErrorLog("null questcountgroup");
                    }
                }
            }
        }

        return map_QuestCountGroup;
    }
}
