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
package tacos.database.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import tacos.client.TacosCharacter;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;
import tacos.odin.OdinPair;

/**
 *
 * @author Riremito
 */
public class DQ_KeyMap {

    public static final String DB_TABLE_NAME = "keymap";
    private static int[] array1 = {2, 3, 4, 5, 6, 7, 16, 17, 18, 19, 23, 25, 31, 34, 37, 38, 44, 45, 46, 50, 59, 60, 61, 62, 63, 64, 65, 8, 9, 24, 30, 10, 11, 12, 20, 33, 35, 39, 40, 47, 48, 49};
    private static int[] array2 = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 4, 6, 6, 6, 6, 6, 6, 6, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4};
    private static int[] array3 = {10, 12, 13, 18, 23, 28, 8, 5, 0, 4, 1, 9, 2, 17, 3, 20, 50, 52, 53, 7, 100, 101, 102, 103, 104, 105, 106, 19, 14, 24, 51, 15, 16, 22, 27, 25, 11, 26, 16, 54, 21, 6};
    private static int[] array1_th = {2, 3, 4, 5, 6, 7, 16, 17, 18, 19, 20, 23, 24, 25, 26, 27, 29, 31, 33, 34, 35, 37, 38, 39, 40, 41, 43, 44, 45, 46, 50, 56, 57, 59, 60, 61, 62, 63, 64, 65};
    private static int[] array2_th = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6};
    private static int[] array3_th = {10, 12, 13, 18, 22, 27, 8, 5, 0, 4, 26, 1, 23, 19, 14, 15, 52, 2, 24, 17, 11, 3, 20, 25, 16, 21, 9, 50, 51, 6, 7, 53, 54, 100, 101, 102, 103, 104, 105, 106};

    public static boolean add(TacosCharacter chr) {
        if (!DatabaseConnection.setManual()) {
            return false;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (characterid, `key`, `type`, `action`) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, chr.getId());
                for (int i = 0; i < array1.length; i++) {
                    ps.setInt(2, array1[i]);
                    ps.setInt(3, array2[i]);
                    ps.setInt(4, array3[i]);
                    ps.execute();
                }
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "add");
            DatabaseConnection.rollback();
        } finally {
            DatabaseConnection.commit();
            DatabaseConnection.setAuto();
        }

        return false;
    }

    public static boolean loadKeyMap(TacosCharacter chr) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT `key`,`type`,`action` FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
                ps.setInt(1, chr.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    Map<Integer, OdinPair<Byte, Integer>> keymap = chr.getKeyLayout().get();
                    while (rs.next()) {
                        keymap.put(rs.getInt("key"), new OdinPair<>(rs.getByte("type"), rs.getInt("action")));
                    }
                    return true;
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "loadKeyMap");
        }

        return false;
    }

    public static boolean saveKeys(TacosCharacter chr) {
        if (!chr.getKeyLayout().isChanged()) {
            return false;
        }

        Map<Integer, OdinPair<Byte, Integer>> keymap = chr.getKeyLayout().get();
        if (keymap.isEmpty()) {
            return false;
        }

        try {
            Connection con = DatabaseConnection.getConnection();

            PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE characterid = ?");
            ps.setInt(1, chr.getId());
            ps.execute();
            ps.close();

            boolean first = true;
            StringBuilder query = new StringBuilder();

            for (Map.Entry<Integer, OdinPair<Byte, Integer>> keybinding : keymap.entrySet()) {
                if (first) {
                    first = false;
                    query.append("INSERT INTO " + DB_TABLE_NAME + " VALUES (");
                } else {
                    query.append(",(");
                }
                query.append("DEFAULT,");
                query.append(chr.getId()).append(",");
                query.append(keybinding.getKey().intValue()).append(",");
                query.append(keybinding.getValue().getKey()).append(",");
                query.append(keybinding.getValue().getValue()).append(")");
            }
            ps = con.prepareStatement(query.toString());
            ps.execute();
            ps.close();
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "saveKeys");
        }

        return false;
    }

}
