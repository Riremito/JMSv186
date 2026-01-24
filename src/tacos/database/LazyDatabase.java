/*
 * Copyright (C) 2024 Riremito
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
package tacos.database;

import java.sql.Connection;
import odin.client.MapleCharacter;
import tacos.debug.DebugLogger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Riremito
 */
public class LazyDatabase {

    private static final String DB_TABLE_NAME = "__root";

    public static boolean loadData(MapleCharacter chr) {
        ArrayList<LazyData> lazy_data_list = chr.getLazyDataList();
        lazy_data_list.clear();
        for (LazyDataNames ldn : LazyDataNames.values()) {
            if (ldn.getType() == LazyDataTypes.UNKNOWN) {
                continue;
            }
            LazyData ld = new LazyData(ldn);
            if (get(chr, ld)) {
                ld.setOk(true);
            }
            lazy_data_list.add(ld);
        }

        for (LazyData ld : lazy_data_list) {
            if (!ld.getOk()) {
                continue;
            }
            DebugLogger.DebugLog("LazyDB load : " + ld.getDataName().name() + " = " + ld.getInt() + ", \"" + ld.getStr() + "\"");
            switch (ld.getDataName()) {
                case PET_ITEM_HP: {
                    chr.setPetAutoHPItem(ld.getInt());
                    break;
                }
                case PET_ITEM_MP: {
                    chr.setPetAutoMPItem(ld.getInt());
                    break;
                }
                case PET_ITEM_CURE: {
                    chr.setPetAutoCureItem(ld.getInt());
                    break;
                }
                case RETURN_MAP_FREEMARKET: {
                    chr.getFreeMarketPortal().setReturnMapId(ld.getInt());
                    break;
                }
                case RETURN_PORTAL_FREEMARKET: {
                    chr.getFreeMarketPortal().setReturnPortalName(ld.getStr());
                    break;
                }
                case RETURN_MAP_ARDENTMILL: {
                    chr.getArdentmillPortal().setReturnMapId(ld.getInt());
                    break;
                }
                case RETURN_PORTAL_ARDENTMILL: {
                    chr.getArdentmillPortal().setReturnPortalName(ld.getStr());
                    break;
                }
                default: {
                    break;
                }
            }
        }

        return true;
    }

    public static boolean saveData(MapleCharacter chr) {
        ArrayList<LazyData> lazy_data_list = chr.getLazyDataList();

        for (LazyData ld : lazy_data_list) {
            int value_int = 0;
            String value_str = "";
            switch (ld.getDataName()) {
                case PET_ITEM_HP: {
                    value_int = chr.getPetAutoHPItem();
                    break;
                }
                case PET_ITEM_MP: {
                    value_int = chr.getPetAutoMPItem();
                    break;
                }
                case PET_ITEM_CURE: {
                    value_int = chr.getPetAutoCureItem();
                    break;
                }
                case RETURN_MAP_FREEMARKET: {
                    value_int = chr.getFreeMarketPortal().getReturnMapId();
                    break;
                }
                case RETURN_PORTAL_FREEMARKET: {
                    value_str = chr.getFreeMarketPortal().getReturnPortalName();
                    break;
                }
                case RETURN_MAP_ARDENTMILL: {
                    value_int = chr.getArdentmillPortal().getReturnMapId();
                    break;
                }
                case RETURN_PORTAL_ARDENTMILL: {
                    value_str = chr.getArdentmillPortal().getReturnPortalName();
                    break;
                }
                default: {
                    break;
                }
            }
            switch (ld.getDataName().getType()) {
                case TYPE_INT: {
                    if (ld.getInt() != value_int) {
                        ld.setInt(value_int);
                        DebugLogger.DebugLog("LazyDB save : " + ld.getDataName().name() + " = " + ld.getInt() + ", \"" + ld.getStr() + "\"");
                        if (ld.getOk()) {
                            updateInt(chr, ld);
                        } else {
                            setInt(chr, ld);
                            ld.setOk(true);
                        }
                    }
                    break;
                }
                case TYPE_STR: {
                    if (!ld.getStr().equals(value_str)) {
                        ld.setStr(value_str);
                        DebugLogger.DebugLog("LazyDB save : " + ld.getDataName().name() + " = " + ld.getInt() + ", \"" + ld.getStr() + "\"");
                        if (ld.getOk()) {
                            updateStr(chr, ld);
                        } else {
                            setStr(chr, ld);
                            ld.setOk(true);
                        }
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }

        return true;
    }

    public static boolean get(MapleCharacter chr, LazyData ld) {
        LazyDataNames ldn = ld.getDataName();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT `value_int`, `value_str` from " + DB_TABLE_NAME + " where maple_id = ? AND character_id = ? AND data_name = ?;")) {
                ps.setInt(1, chr.getAccountID());
                ps.setInt(2, chr.getId());
                ps.setString(3, ldn.getName());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (ldn.getType() == LazyDataTypes.TYPE_INT) {
                            int value_int = rs.getInt("value_int");
                            ld.setInt(value_int);
                            return true;
                        }
                        if (ldn.getType() == LazyDataTypes.TYPE_STR) {
                            String value_str = rs.getString("value_str");
                            ld.setStr(value_str);
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "get");
        }

        return false;
    }

    public static boolean setInt(MapleCharacter chr, LazyData ld) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (maple_id, character_id, data_name, value_int) VALUES (?, ?, ?, ?);")) {
                ps.setInt(1, chr.getAccountID());
                ps.setInt(2, chr.getId());
                ps.setString(3, ld.getDataName().getName());
                ps.setInt(4, ld.getInt());
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "setInt");
        }

        return false;
    }

    public static boolean setStr(MapleCharacter chr, LazyData ld) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (maple_id, character_id, data_name, value_str) VALUES (?, ?, ?, ?);")) {
                ps.setInt(1, chr.getAccountID());
                ps.setInt(2, chr.getId());
                ps.setString(3, ld.getDataName().getName());
                ps.setString(4, ld.getStr());
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "setStr");
        }

        return false;
    }

    private static boolean updateInt(MapleCharacter chr, LazyData ld) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET `value_int` = ? WHERE maple_id = ? AND character_id = ? AND data_name = ?;")) {
                ps.setInt(1, ld.getInt());
                ps.setInt(2, chr.getAccountID());
                ps.setInt(3, chr.getId());
                ps.setString(4, ld.getDataName().getName());
                ps.execute();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "updateInt");
        }

        return false;
    }

    private static boolean updateStr(MapleCharacter chr, LazyData ld) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET `value_str` = ? WHERE maple_id = ? AND character_id = ? AND data_name = ?;")) {
                ps.setString(1, ld.getStr());
                ps.setInt(2, chr.getAccountID());
                ps.setInt(3, chr.getId());
                ps.setString(4, ld.getDataName().getName());
                ps.execute();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "updateStr");
        }

        return false;
    }

}
