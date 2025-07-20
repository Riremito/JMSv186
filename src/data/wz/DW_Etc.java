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

import config.property.Property_Java;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;

/**
 *
 * @author Riremito
 */
public class DW_Etc {

    private static DW_Etc instance = null;

    private static DW_Etc getInstance() {
        if (instance == null) {
            instance = new DW_Etc();
        }
        return instance;
    }

    private MapleDataProvider wz_root = null;

    DW_Etc() {
        this.wz_root = MapleDataProviderFactory.getDataProvider(new File(Property_Java.getDir_WzXml() + "/Etc.wz"));
    }

    private List<String> ForbiddenName = null;

    public List<String> getForbiddenName() {
        if (ForbiddenName != null) {
            return ForbiddenName;
        }

        ForbiddenName = new ArrayList<String>();
        final MapleData nameData = this.wz_root.getData("ForbiddenName.img");
        for (final MapleData data : nameData.getChildren()) {
            ForbiddenName.add(MapleDataTool.getString(data));
        }

        return ForbiddenName;
    }

    public static boolean isForbiddenName(String character_name) {
        for (final String forbidden_name : getInstance().getForbiddenName()) {
            if (character_name.contains(forbidden_name)) {
                return true;
            }
        }
        return false;
    }

}
