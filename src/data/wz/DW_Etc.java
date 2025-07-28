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

import java.util.ArrayList;
import java.util.List;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataTool;

/**
 *
 * @author Riremito
 */
public class DW_Etc {

    private static DataWz wz = null;

    private static DataWz getWz() {
        if (wz == null) {
            wz = new DataWz("Etc.wz");
        }
        return wz;
    }

    public static MapleDataProvider getWzRoot() {
        return getWz().getWzRoot();
    }

    private static MapleData img_ForbiddenName = null;
    private static MapleData img_NpcLocation = null;
    private static MapleData img_ItemMake = null;

    public static MapleData getForbiddenName() {
        if (img_ForbiddenName == null) {
            img_ForbiddenName = getWz().loadData("ForbiddenName.img");
        }
        return img_ForbiddenName;
    }

    public static MapleData getNpcLocation() {
        if (img_NpcLocation == null) {
            img_NpcLocation = getWz().loadData("NpcLocation.img");
        }
        return img_NpcLocation;
    }

    public static MapleData getItemMake() {
        if (img_ItemMake == null) {
            img_ItemMake = getWz().loadData("ItemMake.img");
        }
        return img_ItemMake;
    }

    private static List<String> list_fn = null;

    private static List<String> getFN() {
        if (list_fn != null) {
            return list_fn;
        }

        list_fn = new ArrayList<String>();
        for (final MapleData data : getForbiddenName().getChildren()) {
            list_fn.add(MapleDataTool.getString(data));
        }

        return list_fn;
    }

    public static boolean isForbiddenName(String character_name) {
        for (final String forbidden_name : getFN()) {
            if (character_name.contains(forbidden_name)) {
                return true;
            }
        }
        return false;
    }

}
