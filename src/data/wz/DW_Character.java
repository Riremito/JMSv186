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

import config.Content;
import debug.Debug;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;

/**
 *
 * @author Riremito
 */
public class DW_Character {

    private static DataWz wz = null;

    private static DataWz getWz() {
        if (wz == null) {
            wz = new DataWz(Content.Wz_SingleFile.get() ? "Data.wz/Character" : "Character.wz");
        }
        return wz;
    }

    private static MapleDataProvider getWzRoot() {
        return getWz().getWzRoot();
    }

    public static MapleData getItemData(int id) {
        int item_type = id / 1000000;
        if (2 <= item_type) {
            return null;
        }

        String target_img_name = String.format("%08d.img", id);
        for (MapleDataDirectoryEntry mdde : getWzRoot().getRoot().getSubdirectories()) {
            for (MapleDataFileEntry mdfe : mdde.getFiles()) {
                if (mdfe.getName().equals(target_img_name)) {
                    MapleData md_equip = getWz().loadData(mdde.getName() + "/" + mdfe.getName());
                    if (md_equip == null) {
                        Debug.ErrorLog("getItemData : Invalid equip id = " + id);
                        return null;
                    }
                    return md_equip;
                }
            }
        }

        Debug.ErrorLog("getItemData : err equip id " + id);
        return null;
    }
}
