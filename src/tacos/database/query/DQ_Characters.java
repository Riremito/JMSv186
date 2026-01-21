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

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;
import odin.handling.world.OdinWorld;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tacos.server.TacosFriend;

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
            try (PreparedStatement ps = con.prepareStatement("SELECT id, name FROM " + DB_TABLE_NAME + " WHERE accountid = ? AND world = ?")) {
                ps.setInt(1, c.getId());
                ps.setInt(2, c.getSelectedWorld());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int character_id = rs.getInt("id");
                        character_ids.add(character_id);
                    }
                }
            }
        } catch (SQLException e) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getCharatcerIds");
        }
        return character_ids;
    }

    public static int getIdByName(String name) {
        try {
            Connection con = DatabaseConnection.getConnection();
            int id;
            try (PreparedStatement ps = con.prepareStatement("SELECT id FROM " + DB_TABLE_NAME + " WHERE name = ?")) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        rs.close();
                        ps.close();
                        return -1;
                    }
                    id = rs.getInt("id");
                }
            }
            return id;
        } catch (SQLException e) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getIdByName");
        }

        return -1;
    }

    public static boolean deleteCharacter(MapleClient c, int character_id) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT guildid, guildrank, familyid, name FROM " + DB_TABLE_NAME + " WHERE id = ? AND accountid = ?")) {
                ps.setInt(1, character_id);
                ps.setInt(2, c.getId());
                try (ResultSet rs = ps.executeQuery()) {
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
                        OdinWorld.Guild.deleteGuildCharacter(rs.getInt("guildid"), character_id);
                    }
                    if (rs.getInt("familyid") > 0) {
                        OdinWorld.Family.getFamily(rs.getInt("familyid")).leaveFamily(character_id);
                    }
                }
            }

            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM " + DB_TABLE_NAME + " WHERE id = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM monsterbook WHERE charid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM hiredmerch WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mts_cart WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mts_items WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mountdata WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM inventoryitems WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM famelog WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM famelog WHERE characterid_to = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM dueypackages WHERE RecieverId = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM wishlist WHERE characterid = ?", character_id);
            DQ_Buddies.removeByCharacterId(character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM keymap WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM skills WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mountdata WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM skillmacros WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM queststatus WHERE characterid = ?", character_id);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM inventoryslot WHERE characterid = ?", character_id);
            return true;
        } catch (SQLException e) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "deleteCharacter");
        }
        return false;
    }

    public static TacosFriend findOfflineFriend(int world_id, String name) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE world = ? AND name = ?")) {
                ps.setInt(1, world_id);
                ps.setString(2, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        TacosFriend friend = new TacosFriend();
                        int friend_id = rs.getInt("id");
                        String friend_name = rs.getString("name");
                        int friend_level = rs.getInt("level");
                        int friend_job = rs.getInt("job");
                        int friend_limit = rs.getInt("buddyCapacity");

                        friend.setId(friend_id);
                        friend.setName(friend_name);
                        friend.setLevel(friend_level);
                        friend.setJob(friend_job);
                        return friend;
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "findOfflineFriend");
        }

        return null;
    }

    public static boolean updateRanking() {
        try {
            Connection con = DatabaseConnection.getConnection();
            String query = "SELECT c.id, c.job, c.exp, c.level, c.name, c.jobRank, c.jobRankMove, c.rank, c.rankMove";
            query += ", a.lastlogin AS lastlogin, a.loggedin FROM " + DB_TABLE_NAME + " AS c LEFT JOIN accounts AS a ON c.accountid = a.id WHERE c.gm = 0 AND a.banned = 0 ";
            query += "ORDER BY c.level DESC , c.exp DESC , c.fame DESC , c.meso DESC , c.rank ASC";

            try (PreparedStatement charSelect = con.prepareStatement(query); ResultSet rs = charSelect.executeQuery(); PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET jobRank = ?, jobRankMove = ?, rank = ?, rankMove = ? WHERE id = ?")) {
                int rank = 0;
                Map<Integer, Integer> rankMap = new LinkedHashMap<>();
                while (rs.next()) {
                    int job = rs.getInt("job");
                    int job_category = job / 100;

                    if (!rankMap.containsKey(job_category)) {
                        rankMap.put(job_category, 0);
                    }

                    int jobRank = rankMap.get(job_category) + 1;
                    rankMap.put(job_category, jobRank);
                    rank++;
                    ps.setInt(1, jobRank);
                    ps.setInt(2, rs.getInt("jobRank") - jobRank);
                    ps.setInt(3, rank);
                    ps.setInt(4, rs.getInt("rank") - rank);
                    ps.setInt(5, rs.getInt("id"));
                    ps.addBatch();
                }
                ps.executeBatch();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "updateRanking");
        }

        return false;
    }

}
