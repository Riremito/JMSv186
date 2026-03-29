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
package tacos.wz.data;

import tacos.wz.TacosWz;
import tacos.config.Content;
import tacos.debug.DebugLogger;
import odin.provider.IMapleData;
import odin.provider.IMapleDataDirectoryEntry;
import odin.provider.IMapleDataEntity;

/**
 *
 * @author Riremito
 */
public class CharacterWz extends TacosWz {

    private static CharacterWz wz = null;

    public static CharacterWz get() {
        if (wz == null) {
            wz = new CharacterWz(Content.Wz_SingleFile.get() ? "Data.wz/Character" : "Character.wz");
        }

        return wz;
    }

    public CharacterWz(String path) {
        super(path);
    }

    public IMapleData getItemData(int id) {
        int item_type = id / 1000000;
        if (2 <= item_type) {
            return null;
        }

        String target_img_name = String.format("%08d.img", id);
        for (IMapleDataDirectoryEntry mdde : rootDirectory.getSubDirectories()) {
            for (IMapleDataEntity mdfe : mdde.getFiles()) {
                if (mdfe.getName().equals(target_img_name)) {
                    IMapleData md_equip = getData(mdde.getName() + "/" + mdfe.getName());
                    if (md_equip == null) {
                        DebugLogger.XmlLog("getItemData : invalid equip data, " + id);
                        return null;
                    }
                    return md_equip;
                }
            }
        }

        DebugLogger.XmlLog("getItemData : not found, " + id);
        return null;
    }
}
