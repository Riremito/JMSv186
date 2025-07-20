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

import provider.MapleData;

/**
 *
 * @author Riremito
 */
public class DW_Quest {

    private static DataWz wz = null;

    private static DataWz getWz() {
        if (wz == null) {
            wz = new DataWz("Quest.wz");
        }
        return wz;
    }

    private static MapleData actions = null;
    private static MapleData requirements = null;
    private static MapleData info = null;
    private static MapleData pinfo = null;

    public static MapleData getActions() {
        if (actions == null) {
            actions = getWz().loadData("Act.img");
        }
        return actions;
    }

    public static MapleData getRequirements() {
        if (requirements == null) {
            requirements = getWz().loadData("Check.img");
        }
        return requirements;
    }

    public static MapleData getInfo() {
        if (info == null) {
            info = getWz().loadData("QuestInfo.img");
        }
        return info;
    }

    public static MapleData getPinfo() {
        if (pinfo == null) {
            pinfo = getWz().loadData("PQuest.img");
        }
        return pinfo;
    }

}
