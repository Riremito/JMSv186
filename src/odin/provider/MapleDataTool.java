/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.provider;

import java.awt.Point;
import java.awt.image.BufferedImage;

import odin.provider.WzXML.MapleDataType;
import odin.CaltechEval;

public class MapleDataTool {

    public static String getString(MapleData data) {
        return ((String) data.getData());
    }

    public static String getString(MapleData data, String def) {
        if (data == null || data.getData() == null) {
            return def;
        } else {
            return ((String) data.getData());
        }
    }

    public static String getString(String path, MapleData data) {
        return getString(data.getChildByPath(path));
    }

    public static String getString(String path, MapleData data, String def) {
        return getString(data.getChildByPath(path), def);
    }

    public static double getDouble(MapleData data) {
        return ((Double) data.getData()).doubleValue();
    }

    public static float getFloat(MapleData data) {
        return ((Float) data.getData()).floatValue();
    }

    public static float getFloat(MapleData data, float def) {
        if (data == null || data.getData() == null) {
            return def;
        } else {
            return ((Float) data.getData()).floatValue();
        }
    }

    public static int getInt(MapleData data) {
        return ((Integer) data.getData()).intValue();
    }

    public static int getInt(MapleData data, int def) {
        if (data == null || data.getData() == null) {
            return def;
        } else {
            if (data.getType() == MapleDataType.STRING) {
                return (int) Long.parseLong(getString(data));
            } else if (data.getType() == MapleDataType.SHORT) {
                return Integer.valueOf(((Short) data.getData()).shortValue());
            } else {
                return ((Integer) data.getData()).intValue();
            }
        }
    }

    public static int getInt(String path, MapleData data) {
        return getInt(data.getChildByPath(path));
    }

    public static int getIntConvert(MapleData data) {
        if (data.getType() == MapleDataType.STRING) {
            return Integer.parseInt(getString(data));
        } else {
            return getInt(data);
        }
    }

    public static int getIntConvert(String path, MapleData data) {
        MapleData d = data.getChildByPath(path);
        if (d.getType() == MapleDataType.STRING) {
            return Integer.parseInt(getString(d));
        } else {
            return getInt(d);
        }
    }

    public static int getInt(String path, MapleData data, int def) {
        return getInt(data.getChildByPath(path), def);
    }

    public static int getIntConvert(String path, MapleData data, int def) {
        if (data == null) {
            return def;
        }
        MapleData d = data.getChildByPath(path);
        if (d == null) {
            return def;
        }
        if (d.getType() == MapleDataType.STRING) {
            try {
                return Integer.parseInt(getString(d));
            } catch (NumberFormatException nfe) {
                return def;
            }
        } else {
            return getInt(d, def);
        }
    }

    // parseEval
    public static int getInt(String path, MapleData source, int def, int common_level) {
        // level dir OK
        if (common_level == 0) {
            return getInt(path, source, def);
        }
        // common dir
        final MapleData data = source.getChildByPath(path);
        if (data == null) {
            return def;
        }
        if (data.getType() != MapleDataType.STRING) {
            return MapleDataTool.getIntConvert(path, source, def);
        }
        String d = MapleDataTool.getString(data).toLowerCase();
        if (d.contains("\\r\\n")) {
            d = d.replace("\\r\\n", "");
        }
        if (d.endsWith("u") || d.endsWith("y")) {
            d = d.substring(0, d.length() - 1) + "x";
        } else if (d.endsWith("%")) {
            d = d.substring(0, d.length() - 1);
        }
        d = d.replace("x", String.valueOf(common_level));
        if (d.substring(0, 1).equals("-")) { // -30+3*x
            if (d.substring(1, 2).equals("u") || d.substring(1, 2).equals("d")) { //  -u(x/2)
                d = "n(" + d.substring(1, d.length()) + ")"; // n(u(x/2))
            } else {
                d = "n" + d.substring(1, d.length()); // n30+3*x
            }
        } else if (d.substring(0, 1).equals("=")) { // lol nexon and their mistakes
            d = d.substring(1, d.length());
        }
        return (int) (new CaltechEval(d).evaluate());
    }

    public static BufferedImage getImage(MapleData data) {
        return ((MapleCanvas) data.getData()).getImage();
    }

    public static Point getPoint(MapleData data) {
        return ((Point) data.getData());
    }

    public static Point getPoint(String path, MapleData data) {
        return getPoint(data.getChildByPath(path));
    }

    public static Point getPoint(String path, MapleData data, Point def) {
        final MapleData pointData = data.getChildByPath(path);
        if (pointData == null) {
            return def;
        }
        return getPoint(pointData);
    }

    public static String getFullDataPath(MapleData data) {
        String path = "";
        MapleDataEntity myData = data;
        while (myData != null) {
            path = myData.getName() + "/" + path;
            myData = myData.getParent();
        }
        return path.substring(0, path.length() - 1);
    }
}
