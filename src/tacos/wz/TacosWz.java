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

import tacos.property.Property_Java;
import tacos.debug.DebugLogger;
import java.io.File;
import odin.provider.WzXML.XMLWZFile;
import odin.provider.IMapleData;
import odin.provider.IMapleDataProvider;

/**
 *
 * @author Riremito
 */
public class TacosWz {

    private IMapleDataProvider wz_root = null;
    private String name = null;

    public TacosWz(String name) {
        setWzRoot(name);
    }

    public IMapleDataProvider getWzRoot() {
        return this.wz_root;
    }

    private void setWzRoot(String name) {
        DebugLogger.XmlLog("setWzRoot = " + name);
        this.name = name;
        File dir = new File(Property_Java.getDir_WzXml() + "/" + name);
        this.wz_root = dir.exists() ? new XMLWZFile(dir) : null;
        if (this.wz_root == null) {
            DebugLogger.XmlLog("setWzRoot : wz_root is null, " + name);
        } else {
            DebugLogger.XmlLog("setWzRoot : wz_root = " + name);
        }
    }

    public IMapleData loadData(String path) {
        DebugLogger.XmlLog("loadData = " + this.name + "/" + path);
        return this.wz_root.getData(path);
    }

}
