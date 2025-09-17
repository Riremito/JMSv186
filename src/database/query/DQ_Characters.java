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
package database.query;

import client.MapleCharacter;
import client.MapleClient;
import database.DatabaseConnection;
import debug.DebugLogger;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Riremito
 */
public class DQ_Characters {

    public static final String DB_TABLE_NAME = "characters";

    public static List<Integer> getCharatcerIds(MapleClient c) {
        List<Integer> character_ids = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id, name FROM " + DB_TABLE_NAME + " WHERE accountid = ? AND world = ?");
            ps.setInt(1, c.getId());
            ps.setInt(2, c.getWorld());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int character_id = rs.getInt("id");
                character_ids.add(character_id);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getCharatcerIds");
        }
        return character_ids;
    }

    public static int getIdByName(String name) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id FROM " + DB_TABLE_NAME + " WHERE name = ?");
            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }

            int id = rs.getInt("id");
            rs.close();
            ps.close();
            return id;
        } catch (SQLException e) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getIdByName");
        }

        return -1;
    }

    public static boolean deleteCharacter(MapleClient c, int character_id) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT guildid, guildrank, familyid, name FROM " + DB_TABLE_NAME + " WHERE id = ? AND accountid = ?");
            ps.setInt(1, character_id);
            ps.setInt(2, c.getId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                DebugLogger.ErrorLog("deleteCharacter : 1");
                return true;
            }
            if (rs.getInt("guildid") > 0) { // is in a guild when deleted
                if (rs.getInt("guildrank") == 1) { //cant delete when leader
                    rs.close();
                    ps.close();
                    DebugLogger.ErrorLog("deleteCharacter : 2");
                    return false;
                }
                World.Guild.deleteGuildCharacter(rs.getInt("guildid"), character_id);
            }
            if (rs.getInt("familyid") > 0) {
                World.Family.getFamily(rs.getInt("familyid")).leaveFamily(character_id);
            }
            rs.close();
            ps.close();

            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM " + DB_TABLE_NAME + " WHERE id = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM monsterbook WHERE charid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM hiredmerch WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mts_cart WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mts_items WHERE characterid = ?", character_id);
            //MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM cheatlog WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mountdata WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM inventoryitems WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM famelog WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM famelog WHERE characterid_to = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM dueypackages WHERE RecieverId = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM wishlist WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM buddies WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM buddies WHERE buddyid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM keymap WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM skills WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mountdata WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM skillmacros WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM queststatus WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM inventoryslot WHERE characterid = ?", character_id);
            return true;
        } catch (Exception e) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "deleteCharacter");
        }
        return false;
    }

}
