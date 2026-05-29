/*
 * Copyright (C) 2026 Riremito
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

import java.awt.Point;
import odin.provider.IMapleData;
import odin.provider.WzXML.MapleDataType;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class WzDataTool {

    // no default value.
    public static int getInt(IMapleData data) {
        return (Integer) data.getData();
    }

    public static String getString(IMapleData data) {
        return (String) data.getData();
    }

    // with path.
    public static int getIntPath(String path, IMapleData data, int def) {
        return getInt(data.getChildByPath(path), def);
    }

    public static Float getFloatPath(String path, IMapleData data, float def) {
        return getFloat(data.getChildByPath(path), def);
    }

    public static String getStringPath(String path, IMapleData data, String def) {
        return getString(data.getChildByPath(path), def);
    }

    // PostBB Skill.wz
    public static int getIntExpression(String path, IMapleData source, int def, int common_level) {
        if (common_level == 0) {
            return getIntPath(path, source, def);
        }
        IMapleData data = source.getChildByPath(path);
        if (data == null) {
            //DebugLogger.XmlDataLog(null, "getIntExpression");
            return def;
        }
        if (data.getType() != MapleDataType.STRING) {
            return getIntPath(path, source, def);
        }
        // post bb
        return WzDataExpression.getInt(getString(data), common_level);
    }

    // get data.
    public static int getInt(IMapleData data, int def) {
        if (data == null) {
            //DebugLogger.XmlDataLog(null, "getInt");
            return def;
        }
        Object ret = data.getData();
        if (ret == null) {
            DebugLogger.XmlDataLog(data, "getInt def");
            return def;
        }

        switch (data.getType()) {
            case SHORT:
                DebugLogger.XmlDataLog(data, "getInt = short");
                return (Short) ret;
            case INT: {
                return (Integer) ret;
            }
            case FLOAT: {
                return ((Float) ret).intValue();
            }
            case DOUBLE: {
                return ((Double) ret).intValue();
            }
            case STRING: {
                DebugLogger.XmlDataLog(data, "getInt = string");
                return Integer.parseInt((String) ret);
            }
            default: {
                DebugLogger.XmlDataLog(data, "getInt = others");
                break;
            }
        }

        return (Integer) ret;
    }

    // for Map.wz, 749050100.img, info/decHP, pinkbean cake map
    public static long getLong(IMapleData data, long def) {
        if (data == null) {
            DebugLogger.XmlDataLog(null, "getLong");
            return def;
        }
        if (data.getType() != MapleDataType.STRING) {
            return (long) getInt(data, (int) def);
        }
        String ret = (String) data.getData();
        if (ret == null) {
            DebugLogger.XmlDataLog(data, "getLong def");
            return def;
        }
        return Long.parseLong(ret);
    }

    // for Map.wz, info/recovery
    public static Float getFloat(IMapleData data, float def) {
        if (data == null) {
            DebugLogger.XmlDataLog(null, "getFloat");
            return def;
        }
        Float ret = (Float) data.getData();
        if (ret == null) {
            DebugLogger.XmlDataLog(data, "getFloat def");
            return def;
        }
        return (Float) data.getData();
    }

    // for Item.wz, unitPrice star/bullet
    public static Double getDouble(IMapleData data, double def) {
        if (data == null) {
            DebugLogger.XmlDataLog(null, "getDouble");
            return def;
        }
        Double ret = (Double) data.getData();
        if (ret == null) {
            DebugLogger.XmlDataLog(data, "getDouble def");
            return def;
        }
        return ret;
    }

    public static String getString(IMapleData data, String def) {
        if (data == null) {
            //DebugLogger.XmlDataLog(null, "getString");
            return def;
        }
        String ret = (String) data.getData();
        if (ret == null) {
            DebugLogger.XmlDataLog(data, "getString def");
            return def;
        }
        return ret;
    }

    // for Reactor.wz, lt, rb
    public static Point getPoint(IMapleData data) {
        if (data == null) {
            DebugLogger.XmlDataLog(null, "getPoint");
            return null;
        }
        Point ret = (Point) data.getData();
        if (ret == null) {
            DebugLogger.XmlDataLog(data, "getPoint def");
            return null;
        }
        return ret;
    }
}
