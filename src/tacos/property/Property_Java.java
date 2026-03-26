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
package tacos.property;

import java.io.File;
import tacos.config.Region;
import tacos.config.Version;

/**
 *
 * @author Riremito
 */
public class Property_Java {

    private static String path_wz_xml;
    private static String path_scripts;

    public static boolean setPath() {
        // cmd line arguments
        path_wz_xml = System.getProperty("user.dir") + "/wz_xml/xml_" + Region.GetRegionName() + "_v" + Version.getVersion() + "/";
        path_scripts = System.getProperty("user.dir") + "/scripts/scripts_" + Region.GetRegionName() + "/";

        if (!(new File(path_scripts)).isDirectory()) {
            path_scripts = System.getProperty("user.dir") + "/scripts/scripts_JMS/";
        }
        return true;
    }

    public static String getDir_WzXml() {
        return path_wz_xml;
    }

    public static String getDir_Scripts() {
        return path_scripts;
    }

}
