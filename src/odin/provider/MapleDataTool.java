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
import tacos.wz.TacosWzExpression;

public class MapleDataTool {

    public static String getString(IMapleData data) {
        return ((String) data.getData());
    }

    public static String getString(IMapleData data, String def) {
        if (data == null || data.getData() == null) {
            return def;
        } else {
            return ((String) data.getData());
        }
    }

    public static String getString(String path, IMapleData data) {
        return getString(data.getChildByPath(path));
    }

    public static String getString(String path, IMapleData data, String def) {
        return getString(data.getChildByPath(path), def);
    }

    public static double getDouble(IMapleData data) {
        return ((Double) data.getData());
    }

    public static float getFloat(IMapleData data) {
        return ((Float) data.getData());
    }

    public static float getFloat(IMapleData data, float def) {
        if (data == null || data.getData() == null) {
            return def;
        } else {
            return ((Float) data.getData());
        }
    }

    public static int getInt(IMapleData data) {
        return ((Integer) data.getData());
    }

    public static int getInt(IMapleData data, int def) {
        if (data == null || data.getData() == null) {
            return def;
        } else {
            if (null == data.getType()) {
                return ((Integer) data.getData());
            } else {
                switch (data.getType()) {
                    case STRING:
                        return (int) Long.parseLong(getString(data));
                    case SHORT:
                        return Integer.valueOf(((Short) data.getData()));
                    default:
                        return ((Integer) data.getData());
                }
            }
        }
    }

    public static int getInt(String path, IMapleData data) {
        return getInt(data.getChildByPath(path));
    }

    public static int getIntConvert(IMapleData data) {
        if (data.getType() == MapleDataType.STRING) {
            return Integer.parseInt(getString(data));
        } else {
            return getInt(data);
        }
    }

    public static int getIntConvert(String path, IMapleData data) {
        IMapleData d = data.getChildByPath(path);
        if (d.getType() == MapleDataType.STRING) {
            return Integer.parseInt(getString(d));
        } else {
            return getInt(d);
        }
    }

    public static int getInt(String path, IMapleData data, int def) {
        return getInt(data.getChildByPath(path), def);
    }

    public static int getIntConvert(String path, IMapleData data, int def) {
        if (data == null) {
            return def;
        }
        IMapleData d = data.getChildByPath(path);
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

    public static int getInt(String path, IMapleData source, int def, int common_level) {
        if (common_level == 0) {
            return getInt(path, source, def);
        }
        IMapleData data = source.getChildByPath(path);
        if (data == null) {
            return def;
        }
        if (data.getType() != MapleDataType.STRING) {
            return MapleDataTool.getIntConvert(path, source, def);
        }
        // post bb
        return TacosWzExpression.getInt(MapleDataTool.getString(data), common_level);
    }

    public static BufferedImage getImage(IMapleData data) {
        return ((IMapleCanvas) data.getData()).getImage();
    }

    public static Point getPoint(IMapleData data) {
        return ((Point) data.getData());
    }

    public static Point getPoint(String path, IMapleData data) {
        return getPoint(data.getChildByPath(path));
    }

    public static Point getPoint(String path, IMapleData data, Point def) {
        final IMapleData pointData = data.getChildByPath(path);
        if (pointData == null) {
            return def;
        }
        return getPoint(pointData);
    }

    public static String getFullDataPath(IMapleData data) {
        String path = "";
        IMapleDataEntity myData = data;
        while (myData != null) {
            path = myData.getName() + "/" + path;
            myData = myData.getParent();
        }
        return path.substring(0, path.length() - 1);
    }
}
