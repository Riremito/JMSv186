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
import odin.client.inventory.IEquip;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Inventoryequipment {

    public static final String DB_TABLE_NAME = "inventoryequipment";

    public static boolean add(int unique_id, IEquip equip) {
        /*
        if (!DatabaseConnection.setManual()) {
            return false;
        }
         */

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
                ps.setInt(1, unique_id);
                ps.setInt(2, equip.getUpgradeSlots());
                ps.setInt(3, equip.getLevel());
                ps.setInt(4, equip.getStr());
                ps.setInt(5, equip.getDex());
                ps.setInt(6, equip.getInt());
                ps.setInt(7, equip.getLuk());
                ps.setInt(8, equip.getHp());
                ps.setInt(9, equip.getMp());
                ps.setInt(10, equip.getWatk());
                ps.setInt(11, equip.getMatk());
                ps.setInt(12, equip.getWdef());
                ps.setInt(13, equip.getMdef());
                ps.setInt(14, equip.getAcc());
                ps.setInt(15, equip.getAvoid());
                ps.setInt(16, equip.getHands());
                ps.setInt(17, equip.getSpeed());
                ps.setInt(18, equip.getJump());
                ps.setInt(19, equip.getViciousHammer());
                ps.setInt(20, equip.getItemEXP());
                ps.setInt(21, equip.getDurability());
                ps.setInt(22, equip.getEnhance());
                ps.setInt(23, equip.getRank());
                ps.setInt(24, equip.getHidden());
                ps.setInt(25, equip.getPotential1());
                ps.setInt(26, equip.getPotential2());
                ps.setInt(27, equip.getPotential3());
                ps.setInt(28, equip.getHpR());
                ps.setInt(29, equip.getMpR());
                ps.setInt(30, equip.getIncAttackSpeed());
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
                    equip.setUpgradeSlots(rs.getInt("upgradeslots"));
                    equip.setLevel(rs.getByte("level"));
                    equip.setStr(rs.getInt("str"));
                    equip.setDex(rs.getInt("dex"));
                    equip.setInt(rs.getInt("int"));
                    equip.setLuk(rs.getInt("luk"));
                    equip.setHp(rs.getInt("hp"));
                    equip.setMp(rs.getInt("mp"));
                    equip.setWatk(rs.getInt("watk"));
                    equip.setMatk(rs.getInt("matk"));
                    equip.setWdef(rs.getInt("wdef"));
                    equip.setMdef(rs.getInt("mdef"));
                    equip.setAcc(rs.getInt("acc"));
                    equip.setAvoid(rs.getInt("avoid"));
                    equip.setHands(rs.getInt("hands"));
                    equip.setSpeed(rs.getInt("speed"));
                    equip.setJump(rs.getInt("jump"));
                    equip.setViciousHammer(rs.getInt("ViciousHammer"));
                    equip.setItemEXP(rs.getInt("itemEXP"));
                    equip.setDurability(rs.getInt("durability"));
                    equip.setEnhance(rs.getInt("enhance"));
                    equip.setRank(rs.getInt("rank"));
                    equip.setHidden(rs.getInt("hidden"));
                    equip.setPotential1(rs.getInt("potential1"));
                    equip.setPotential2(rs.getInt("potential2"));
                    equip.setPotential3(rs.getInt("potential3"));
                    equip.setHpR(rs.getInt("hpR"));
                    equip.setMpR(rs.getInt("mpR"));
                    equip.setIncAttackSpeed(rs.getInt("incattackSpeed"));
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
