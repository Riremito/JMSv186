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
package database;

import client.MapleCharacter;
import debug.DebugLogger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Riremito
 */
public class ExtraDB {

    // data
    public static final String DATA_PET_HP = "pet_hp";
    public static final String DATA_PET_MP = "pet_mp";
    public static final String DATA_PET_CURE = "pet_cure";

    public static void loadData(MapleCharacter chr) {
        DebugLogger.DebugLog("ExtraDB : Load");
        ExtraDBData pet_hp = get(chr, DATA_PET_HP);
        if (pet_hp.getOk()) {
            chr.setPetAutoHPItem(pet_hp.getInt());
        }
        ExtraDBData pet_mp = get(chr, DATA_PET_MP);
        if (pet_mp.getOk()) {
            chr.setPetAutoMPItem(pet_mp.getInt());
        }
        ExtraDBData pet_cure = get(chr, DATA_PET_CURE);
        if (pet_cure.getOk()) {
            chr.setPetAutoCureItem(pet_cure.getInt());
        }
    }

    public static void saveData(MapleCharacter chr) {
        DebugLogger.DebugLog("ExtraDB : Save");
        setInt(chr, DATA_PET_HP, chr.getPetAutoHPItem());
        setInt(chr, DATA_PET_MP, chr.getPetAutoMPItem());
        setInt(chr, DATA_PET_CURE, chr.getPetAutoCureItem());
    }

    public static void copyData(MapleCharacter src, MapleCharacter dst) {
        //dst.setPetAutoHPItem(src.getPetAutoHPItem());
        //dst.setPetAutoMPItem(src.getPetAutoMPItem());
        //dst.setPetAutoCureItem(src.getPetAutoCureItem());
    }

    private static final String table_name = "__root";

    public static boolean setInt(MapleCharacter chr, String data_name, int value) {
        ExtraDBData edd = get(chr, data_name);
        if (edd.getOk()) {
            // 更新不要
            if (edd.getInt() == value) {
                return true;
            }

            updateInt(chr, data_name, value);
            return true;
        }

        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO " + table_name + " (maple_id, character_id, data_name, value_int) VALUES (?, ?, ?, ?);");
            ps.setInt(1, chr.getAccountID());
            ps.setInt(2, chr.getId());
            ps.setString(3, data_name);
            ps.setInt(4, value);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ExtraDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public static boolean setStr(MapleCharacter chr, String data_name, String value) {
        ExtraDBData edd = get(chr, data_name);
        if (edd.getOk()) {
            // 更新不要
            if (edd.getStr().equals(value)) {
                return true;
            }

            updateStr(chr, data_name, value);
            return true;
        }
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO " + table_name + " (maple_id, character_id, data_name, value_str) VALUES (?, ?, ?, ?);");
            ps.setInt(1, chr.getAccountID());
            ps.setInt(2, chr.getId());
            ps.setString(3, data_name);
            ps.setString(4, value);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ExtraDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    private static boolean updateInt(MapleCharacter chr, String data_name, int value) {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE " + table_name + " SET `value_int` = ? WHERE maple_id = ? AND character_id = ? AND data_name = ?;");
            ps.setInt(1, value);
            ps.setInt(2, chr.getAccountID());
            ps.setInt(3, chr.getId());
            ps.setString(4, data_name);
            ps.execute();
            ps.close();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ExtraDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private static boolean updateStr(MapleCharacter chr, String data_name, String value) {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE " + table_name + " SET `value_str` = ? WHERE maple_id = ? AND character_id = ? AND data_name = ?;");
            ps.setString(1, value);
            ps.setInt(2, chr.getAccountID());
            ps.setInt(3, chr.getId());
            ps.setString(4, data_name);
            ps.execute();
            ps.close();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ExtraDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static ExtraDBData get(MapleCharacter chr, String data_name) {
        ExtraDBData edd = new ExtraDBData();

        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT `value_int`, `value_str` from " + table_name + " where maple_id = ? AND character_id = ? AND data_name = ?;");
            ps.setInt(1, chr.getAccountID());
            ps.setInt(2, chr.getId());
            ps.setString(3, data_name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                edd.setInt(rs.getInt("value_int"));
                edd.setStr(rs.getString("value_str"));
                edd.setOk(true);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ExtraDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return edd;
    }

    public static class ExtraDBData {

        private boolean ok = false;
        private int value_int = 0;
        private String value_str = "";

        public void setOk(boolean ok) {
            this.ok = ok;
        }

        public void setInt(int value_int) {
            this.value_int = value_int;
        }

        public void setStr(String value_str) {
            this.value_str = value_str;
        }

        public int getInt() {
            return this.value_int;
        }

        public String getStr() {
            return this.value_str;
        }

        public boolean getOk() {
            return this.ok;
        }

    }
}
