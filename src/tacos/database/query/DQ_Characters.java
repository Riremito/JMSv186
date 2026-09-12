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
import tacos.client.TacosClient;
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
import odin.client.PlayerStats;
import tacos.client.TacosCharacter;
import tacos.client.TacosClient;
import tacos.config.DeveloperMode;
import tacos.server.TacosFriend;

/**
 *
 * @author Riremito
 */
public class DQ_Characters {

    public static final String DB_TABLE_NAME = "characters";

    public static boolean add(TacosCharacter chr) {
        if (!DatabaseConnection.setManual()) {
            return false;
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            PlayerStats stat = chr.getStat();

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (level, fame, str, dex, luk, `int`, exp, hp, mp, maxhp, maxmp, sp, ap, gm, skincolor, gender, job, hair, face, map, meso, hpApUsed, spawnpoint, party, buddyCapacity, monsterbookcover, dojo_pts, dojoRecord, pets, subcategory, marriageId, currentrep, totalrep, accountid, name, world, tama) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", DatabaseConnection.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, 1); // Level
                ps.setShort(2, (short) 0); // Fame
                ps.setInt(3, stat.getStr()); // Str
                ps.setInt(4, stat.getDex()); // Dex
                ps.setInt(5, stat.getInt()); // Int
                ps.setInt(6, stat.getLuk()); // Luk
                ps.setInt(7, 0); // EXP
                ps.setInt(8, stat.getHp()); // HP
                ps.setInt(9, stat.getMp());
                ps.setInt(10, stat.getMaxHp()); // MP
                ps.setInt(11, stat.getMaxMp());
                ps.setString(12, "0,0,0,0,0,0,0,0,0,0"); // Remaining SP
                ps.setShort(13, (short) 0); // Remaining AP
                ps.setByte(14, (byte) 0); // GM Level
                ps.setByte(15, (byte) chr.getSkinColor());
                ps.setByte(16, (byte) chr.getGender());
                ps.setInt(17, chr.getJob());
                ps.setInt(18, chr.getHair());
                ps.setInt(19, chr.getFace());
                ps.setInt(20, DeveloperMode.DM_FIRST_MAP_ID.getInt()); // 0, 130030000, 900090000, 914000000
                ps.setInt(21, chr.getMeso()); // Meso
                ps.setShort(22, (short) 0); // HP ap used
                ps.setByte(23, (byte) 0); // Spawnpoint
                ps.setInt(24, -1); // Party
                ps.setByte(25, (byte) chr.getBuddyCapacity()); // Buddylist
                ps.setInt(26, 0); // Monster book cover
                ps.setInt(27, 0); // Dojo
                ps.setInt(28, 0); // Dojo record
                ps.setString(29, "-1,-1,-1");
                ps.setInt(30, chr.getSubcategory()); // dual blade
                ps.setInt(31, 0); //marriage ID
                ps.setInt(32, 0); //current reps
                ps.setInt(33, 0); //total reps
                ps.setInt(34, chr.getAccountId());
                ps.setString(35, chr.getName());
                ps.setByte(36, (byte) chr.getClient().getSelectedWorld());
                ps.setInt(37, chr.getTama());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int character_id = rs.getInt(1);
                        chr.setId(character_id);
                        return true;
                    }
                }

                // ?_? rollback?
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

    /**
     * NOTE: this declares {@code throws SQLException} instead of catching
     * it internally, since it is called from MapleCharacter.saveToDB, which
     * manages its own outer transaction and must see any failure in order
     * to roll back correctly.
     */
    public static boolean updateStat(Connection con, CharacterSaveRow row) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET level = ?, fame = ?, str = ?, dex = ?, luk = ?, `int` = ?, exp = ?, hp = ?, mp = ?, maxhp = ?, maxmp = ?, sp = ?, ap = ?, gm = ?, skincolor = ?, gender = ?, job = ?, hair = ?, face = ?, map = ?, meso = ?, hpApUsed = ?, spawnpoint = ?, party = ?, buddyCapacity = ?, monsterbookcover = ?, dojo_pts = ?, dojoRecord = ?, pets = ?, subcategory = ?, marriageId = ?, currentrep = ?, totalrep = ?, name = ?, tama = ? WHERE id = ?", DatabaseConnection.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, row.level);
            ps.setInt(2, row.fame);
            ps.setInt(3, row.str);
            ps.setInt(4, row.dex);
            ps.setInt(5, row.luk);
            ps.setInt(6, row.intel);
            ps.setInt(7, row.exp);
            ps.setInt(8, row.hp);
            ps.setInt(9, row.mp);
            ps.setInt(10, row.maxhp);
            ps.setInt(11, row.maxmp);
            ps.setString(12, row.sp);
            ps.setInt(13, row.ap);
            ps.setByte(14, row.gm);
            ps.setByte(15, row.skinColor);
            ps.setByte(16, row.gender);
            ps.setInt(17, row.job);
            ps.setInt(18, row.hair);
            ps.setInt(19, row.face);
            ps.setInt(20, row.map);
            ps.setInt(21, row.meso);
            ps.setInt(22, row.hpApUsed);
            ps.setByte(23, row.spawnpoint);
            ps.setInt(24, row.party);
            ps.setShort(25, row.buddyCapacity);
            ps.setInt(26, row.monsterbookcover);
            ps.setInt(27, row.dojo);
            ps.setInt(28, row.dojoRecord);
            ps.setString(29, row.pets);
            ps.setInt(30, row.subcategory);
            ps.setInt(31, row.marriageId);
            ps.setInt(32, row.currentrep);
            ps.setInt(33, row.totalrep);
            ps.setString(34, row.name);
            ps.setInt(35, row.tama);
            ps.setInt(36, row.id);

            return ps.executeUpdate() >= 1;
        }
    }

    public static boolean loadStat(MapleCharacter ret) {
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE id = ?")) {
            ps.setInt(1, ret.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                PlayerStats stat = ret.getStat();

                String name = rs.getString("name");
                ret.setName(name);
                int gender = rs.getByte("gender");
                ret.setGender(gender);
                int subcategory = rs.getInt("subcategory");
                ret.setSubcategory(subcategory);

                int stat_skin = rs.getByte("skincolor");
                int stat_face = rs.getInt("face");
                int stat_hair = rs.getInt("hair");
                int stat_level = rs.getShort("level");
                int stat_job = rs.getShort("job");
                int stat_str = rs.getShort("str");
                int stat_dex = rs.getShort("dex");
                int stat_int = rs.getShort("int");
                int stat_luk = rs.getShort("luk");
                int stat_hp = rs.getShort("hp");
                int stat_mhp = rs.getShort("maxhp");
                int stat_mp = rs.getShort("mp");
                int stat_mmp = rs.getShort("maxmp");
                int stat_remainingAp = rs.getShort("ap");
                String stat_remainingSp = rs.getString("sp");
                int stat_exp = rs.getInt("exp");
                int stat_pop = rs.getShort("fame");
                int stat_meso = rs.getInt("meso");
                ret.setSkinColor(stat_skin);
                ret.setFace(stat_face);
                ret.setHair(stat_hair);
                ret.setLevel(stat_level);
                ret.setJob(stat_job);
                stat.str = stat_str;
                stat.dex = stat_dex;
                stat.int_ = stat_int;
                stat.luk = stat_luk;
                stat.hp = stat_hp;
                stat.maxhp = stat_mhp;
                stat.mp = stat_mp;
                stat.maxmp = stat_mmp;
                ret.setRemainingAp(stat_remainingAp);
                ret.setRemainingSps(stat_remainingSp);
                ret.setExp(stat_exp);
                ret.setFame(stat_pop);
                ret.setMeso(stat_meso);

                int money_tama = rs.getInt("tama");
                ret.setTama(money_tama);

                int hpApUsed = rs.getShort("hpApUsed");
                ret.setHpApUsed(hpApUsed);

                int gmLevel = rs.getByte("gm");
                ret.setGM(gmLevel);

                int accountid = rs.getInt("accountid");
                ret.setAccountId(accountid);

                int dwPosMap = rs.getInt("map");
                int nPortal = rs.getByte("spawnpoint");
                ret.setPosMapAndPortal(dwPosMap, nPortal);

                int world_id = rs.getByte("world");
                ret.setWorldId(world_id);
                // guild
                int guildid = rs.getInt("guildid");
                int guildrank = rs.getByte("guildrank");
                int allianceRank = rs.getByte("allianceRank");
                ret.setGuildId(guildid);
                ret.setGuildRank(guildrank);
                ret.setAllianceRank(allianceRank);
                // family
                int currentrep = rs.getInt("currentrep");
                int totalrep = rs.getInt("totalrep");
                ret.setCurrentRep(currentrep);
                ret.setTotalRep(totalrep);
                ret.makeMFC(rs.getInt("familyid"), rs.getInt("seniorid"), rs.getInt("junior1"), rs.getInt("junior2"));
                // friend
                int buddy_capacity = rs.getByte("buddyCapacity");
                ret.setBuddylist(buddy_capacity);
                // ranking
                int rank = rs.getInt("rank");
                int rankMove = rs.getInt("rankMove");
                int jobRank = rs.getInt("jobRank");
                int jobRankMove = rs.getInt("jobRankMove");
                ret.setRank(rank, rankMove, jobRank, jobRankMove);
                // marriage
                int marriageId = rs.getInt("marriageId");
                ret.setMarriageId(marriageId);
                // mount?
                ret.setMount();
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "loadStat");
        }

        return true;
    }

    /**
     * NOTE: this declares {@code throws SQLException} instead of catching
     * it internally (unlike most methods in this class), matching the
     * original MapleCharacter.loadCharFromDB behavior, which relies on the
     * exception (and the not-found RuntimeException below) propagating to
     * its own outer catch block.
     */
    public static ExtrasRow loadExtras(int characterId) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE id = ?")) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("Loading the Char Failed (char not found)");
                }
                return new ExtrasRow(rs.getInt("party"), rs.getInt("monsterbookcover"), rs.getInt("dojo_pts"), rs.getByte("dojoRecord"), rs.getString("pets"));
            }
        }
    }

    /**
     * NOTE: this declares {@code throws SQLException} for the same reason
     * as {@code loadExtras} above.
     */
    public static List<BlessOfFairyRow> loadOtherCharactersForBlessOfFairy(int accountId) throws SQLException {
        List<BlessOfFairyRow> ret = new ArrayList<>();
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE accountid = ? ORDER BY level DESC")) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ret.add(new BlessOfFairyRow(rs.getInt("id"), rs.getShort("level"), rs.getString("name")));
                }
            }
        }
        return ret;
    }

    public static boolean updateFamilyStatus(int characterId, int familyId, int seniorId, int junior1, int junior2) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET familyid = ?, seniorid = ?, junior1 = ?, junior2 = ? WHERE id = ?")) {
                ps.setInt(1, familyId);
                ps.setInt(2, seniorId);
                ps.setInt(3, junior1);
                ps.setInt(4, junior2);
                ps.setInt(5, characterId);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "updateFamilyStatus");
        }

        return false;
    }

    public static List<Integer> getCharatcerIds(TacosClient client) {
        List<Integer> character_ids = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT id, name FROM " + DB_TABLE_NAME + " WHERE accountid = ? AND world = ?")) {
                ps.setInt(1, client.getId());
                ps.setInt(2, client.getSelectedWorld());

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

    public static boolean deleteCharacter(TacosClient client, int character_id) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT guildid, guildrank, familyid, name FROM " + DB_TABLE_NAME + " WHERE id = ? AND accountid = ?")) {
                ps.setInt(1, character_id);
                ps.setInt(2, client.getId());
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
            query += "ORDER BY c.level DESC , c.exp DESC , c.fame DESC , c.meso DESC , c.rank ASC;";

            // MYSQL 8, rank should be escaped.
            try (PreparedStatement charSelect = con.prepareStatement(query); ResultSet rs = charSelect.executeQuery(); PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET jobRank = ?, jobRankMove = ?, `rank` = ?, rankMove = ? WHERE id = ?;")) {
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
            System.getLogger(DatabaseConnection.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        return false;
    }

    public static boolean resetAllianceRankForGuild(int guildId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET alliancerank = 5 WHERE guildid = ?")) {
                ps.setInt(1, guildId);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "resetAllianceRankForGuild");
        }

        return false;
    }

    public static List<FamilyMemberRow> getFamilyMembers(int familyId) {
        List<FamilyMemberRow> ret = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT id, name, level, job, seniorid, junior1, junior2, currentrep, totalrep FROM " + DB_TABLE_NAME + " WHERE familyid = ?")) {
                ps.setInt(1, familyId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ret.add(new FamilyMemberRow(rs.getInt("id"), rs.getShort("level"), rs.getString("name"), rs.getInt("job"),
                                rs.getInt("seniorid"), rs.getInt("junior1"), rs.getInt("junior2"), rs.getInt("currentrep"), rs.getInt("totalrep")));
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getFamilyMembers");
        }

        return ret;
    }

    public static boolean resetFamilyForMembers(int familyId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET familyid = 0, junior1 = 0, junior2 = 0, seniorid = 0 WHERE familyid = ?")) {
                ps.setInt(1, familyId);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "resetFamilyForMembers");
        }

        return false;
    }

    public static boolean setOfflineFamilyStatus(int characterId, int familyId, int seniorId, int junior1, int junior2, int currentRep, int totalRep) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET familyid = ?, seniorid = ?, junior1 = ?, junior2 = ?, currentrep = ?, totalrep = ? WHERE id = ?")) {
                ps.setInt(1, familyId);
                ps.setInt(2, seniorId);
                ps.setInt(3, junior1);
                ps.setInt(4, junior2);
                ps.setInt(5, currentRep);
                ps.setInt(6, totalRep);
                ps.setInt(7, characterId);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "setOfflineFamilyStatus");
        }

        return false;
    }

    public static int getNextRunningPartyId() {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT MAX(party)+2 FROM " + DB_TABLE_NAME)) {
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getNextRunningPartyId");
        }

        return 0;
    }

    public static List<GuildMemberRow> getGuildMembers(int guildId) {
        List<GuildMemberRow> ret = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT id, name, level, job, guildrank, alliancerank FROM " + DB_TABLE_NAME + " WHERE guildid = ? ORDER BY guildrank ASC, name ASC")) {
                ps.setInt(1, guildId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ret.add(new GuildMemberRow(rs.getInt("id"), rs.getShort("level"), rs.getString("name"), rs.getInt("job"), rs.getByte("guildrank"), rs.getByte("alliancerank")));
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getGuildMembers");
        }

        return ret;
    }

    public static boolean resetGuildForMembers(int guildId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET guildid = 0, guildrank = 5, alliancerank = 5 WHERE guildid = ?")) {
                ps.setInt(1, guildId);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "resetGuildForMembers");
        }

        return false;
    }

    public static boolean setOfflineGuildStatus(int characterId, int guildId, int guildRank, int allianceRank) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET guildid = ?, guildrank = ?, alliancerank = ? WHERE id = ?")) {
                ps.setInt(1, guildId);
                ps.setInt(2, guildRank);
                ps.setInt(3, allianceRank);
                ps.setInt(4, characterId);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "setOfflineGuildStatus");
        }

        return false;
    }

    public static final class ExtrasRow {
        public final int party;
        public final int monsterbookcover;
        public final int dojo;
        public final byte dojoRecord;
        public final String pets;
        public ExtrasRow(int party, int monsterbookcover, int dojo, byte dojoRecord, String pets) {
            this.party = party;
            this.monsterbookcover = monsterbookcover;
            this.dojo = dojo;
            this.dojoRecord = dojoRecord;
            this.pets = pets;
        }
    }

    public static final class BlessOfFairyRow {
        public final int id;
        public final int level;
        public final String name;
        public BlessOfFairyRow(int id, int level, String name) {
            this.id = id;
            this.level = level;
            this.name = name;
        }
    }

    public static final class CharacterSaveRow {
        public final int id;
        public final int level;
        public final int fame;
        public final int str, dex, luk, intel;
        public final int exp;
        public final int hp, mp, maxhp, maxmp;
        public final String sp;
        public final int ap;
        public final byte gm;
        public final byte skinColor;
        public final byte gender;
        public final int job;
        public final int hair, face;
        public final int map;
        public final int meso;
        public final int hpApUsed;
        public final byte spawnpoint;
        public final int party;
        public final short buddyCapacity;
        public final int monsterbookcover;
        public final int dojo;
        public final int dojoRecord;
        public final String pets;
        public final int subcategory;
        public final int marriageId;
        public final int currentrep, totalrep;
        public final String name;
        public final int tama;

        public CharacterSaveRow(int id, int level, int fame, int str, int dex, int luk, int intel, int exp,
                int hp, int mp, int maxhp, int maxmp, String sp, int ap, byte gm, byte skinColor, byte gender,
                int job, int hair, int face, int map, int meso, int hpApUsed, byte spawnpoint, int party,
                short buddyCapacity, int monsterbookcover, int dojo, int dojoRecord, String pets, int subcategory,
                int marriageId, int currentrep, int totalrep, String name, int tama) {
            this.id = id;
            this.level = level;
            this.fame = fame;
            this.str = str;
            this.dex = dex;
            this.luk = luk;
            this.intel = intel;
            this.exp = exp;
            this.hp = hp;
            this.mp = mp;
            this.maxhp = maxhp;
            this.maxmp = maxmp;
            this.sp = sp;
            this.ap = ap;
            this.gm = gm;
            this.skinColor = skinColor;
            this.gender = gender;
            this.job = job;
            this.hair = hair;
            this.face = face;
            this.map = map;
            this.meso = meso;
            this.hpApUsed = hpApUsed;
            this.spawnpoint = spawnpoint;
            this.party = party;
            this.buddyCapacity = buddyCapacity;
            this.monsterbookcover = monsterbookcover;
            this.dojo = dojo;
            this.dojoRecord = dojoRecord;
            this.pets = pets;
            this.subcategory = subcategory;
            this.marriageId = marriageId;
            this.currentrep = currentrep;
            this.totalrep = totalrep;
            this.name = name;
            this.tama = tama;
        }
    }

    public static final class FamilyMemberRow {
        public final int id;
        public final int level;
        public final String name;
        public final int job;
        public final int seniorId;
        public final int junior1;
        public final int junior2;
        public final int currentRep;
        public final int totalRep;
        public FamilyMemberRow(int id, int level, String name, int job, int seniorId, int junior1, int junior2, int currentRep, int totalRep) {
            this.id = id; this.level = level; this.name = name; this.job = job;
            this.seniorId = seniorId; this.junior1 = junior1; this.junior2 = junior2;
            this.currentRep = currentRep; this.totalRep = totalRep;
        }
    }

    public static final class GuildMemberRow {

        public final int id;
        public final int level;
        public final String name;
        public final int job;
        public final int guildRank;
        public final int allianceRank;

        public GuildMemberRow(int id, int level, String name, int job, int guildRank, int allianceRank) {
            this.id = id;
            this.level = level;
            this.name = name;
            this.job = job;
            this.guildRank = guildRank;
            this.allianceRank = allianceRank;
        }
    }
}
