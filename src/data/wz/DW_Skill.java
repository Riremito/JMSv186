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
import java.util.HashMap;
import java.util.Map;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import server.MapleCarnivalFactory;

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
