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
import debug.Debug;
import java.io.File;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;

/**
 *
 * @author Riremito
 */
public class DataWz {

    private MapleDataProvider wz_root = null;
    private String name = null;

    DataWz(String name) {
        setWzRoot(name);
    }

    public final void setWzRoot(String name) {
        Debug.XmlLog("setWzRoot = " + name);
        this.name = name;
        this.wz_root = MapleDataProviderFactory.getDataProvider(new File(Property_Java.getDir_WzXml() + "/" + name));
    }

    public final MapleData loadData(String path) {
        Debug.XmlLog("loadData = " + this.name + "/" + path);
        return this.wz_root.getData(path);
    }
}
