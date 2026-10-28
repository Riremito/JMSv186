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
package tacos.database.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import odin.client.inventory.Equip;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Inventoryequipment {

    public static final String DB_TABLE_NAME = "inventoryequipment";

    public static boolean add(int unique_id, Equip equip) {
        /*
        if (!DatabaseConnection.setManual()) {
            return false;
        }
         */

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
                ps.setInt(1, unique_id);
                ps.setInt(2, equip.getRUC());
                ps.setInt(3, equip.getCUC());
                ps.setInt(4, equip.getSTR());
                ps.setInt(5, equip.getDEX());
                ps.setInt(6, equip.getINT());
                ps.setInt(7, equip.getLUK());
                ps.setInt(8, equip.getMaxHP());
                ps.setInt(9, equip.getMaxMP());
                ps.setInt(10, equip.getPAD());
                ps.setInt(11, equip.getMAD());
                ps.setInt(12, equip.getPDD());
                ps.setInt(13, equip.getMDD());
                ps.setInt(14, equip.getACC());
                ps.setInt(15, equip.getEVA());
                ps.setInt(16, equip.getCraft());
                ps.setInt(17, equip.getSpeed());
                ps.setInt(18, equip.getJump());
                ps.setInt(19, equip.getIUC());
                ps.setInt(20, equip.getLevelEXP());
                ps.setInt(21, equip.getDurability());
                ps.setInt(22, equip.getCHUC());
                ps.setInt(23, equip.getGrade());
                ps.setInt(24, equip.getHidden());
                ps.setInt(25, equip.getOption1());
                ps.setInt(26, equip.getOption2());
                ps.setInt(27, equip.getOption3());
                ps.setInt(28, equip.getMaxHPr());
                ps.setInt(29, equip.getMaxMPr());
                ps.setInt(30, equip.getAttackSpeed());
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "add");
            //DatabaseConnection.rollback();
        } finally {
            //DatabaseConnection.commit();
            //DatabaseConnection.setAuto();
        }

        return false;
    }

    public static boolean load(int inventory_item_uid, Equip equip) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE inventoryitemid = ?;")) {
                ps.setInt(1, inventory_item_uid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        DebugLogger.DBErrorLog(DB_TABLE_NAME, "load, inventoryitemid is not found, id = " + inventory_item_uid);
                        return false;
                    }
                    equip.setRUC(rs.getInt("upgradeslots"));
                    equip.setCUC(rs.getByte("level"));
                    equip.setSTR(rs.getInt("str"));
                    equip.setDEX(rs.getInt("dex"));
                    equip.setINT(rs.getInt("int"));
                    equip.setLUK(rs.getInt("luk"));
                    equip.setMaxHP(rs.getInt("hp"));
                    equip.setMaxMP(rs.getInt("mp"));
                    equip.setPAD(rs.getInt("watk"));
                    equip.setMAD(rs.getInt("matk"));
                    equip.setPDD(rs.getInt("wdef"));
                    equip.setMDD(rs.getInt("mdef"));
                    equip.setACC(rs.getInt("acc"));
                    equip.setEVA(rs.getInt("avoid"));
                    equip.setCraft(rs.getInt("hands"));
                    equip.setSpeed(rs.getInt("speed"));
                    equip.setJump(rs.getInt("jump"));
                    equip.setIUC(rs.getInt("ViciousHammer"));
                    equip.setLevelEXP(rs.getInt("itemEXP"));
                    equip.setDurability(rs.getInt("durability"));
                    equip.setCHUC(rs.getInt("enhance"));
                    equip.setGrade(rs.getInt("rank"));
                    equip.setHidden(rs.getInt("hidden"));
                    equip.setOption1(rs.getInt("potential1"));
                    equip.setPOption2(rs.getInt("potential2"));
                    equip.setOption3(rs.getInt("potential3"));
                    equip.setMaxHPr(rs.getInt("hpR"));
                    equip.setMaxMPr(rs.getInt("mpR"));
                    equip.setAttackSpeed(rs.getInt("incattackSpeed"));
                    if (rs.next()) {
                        DebugLogger.DBErrorLog(DB_TABLE_NAME, "load, inventoryitemid is duplicated , id = " + inventory_item_uid);
                        return false;
                    }
                }
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "load");
        }

        return false;
    }

}
