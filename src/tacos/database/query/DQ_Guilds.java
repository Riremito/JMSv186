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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import odin.handling.channel.MapleGuildRanking.GuildRankingInfo;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Guilds {

    public static final String DB_TABLE_NAME = "guilds";

    public static GuildRow load(int guildid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE guildid = ?")) {
                ps.setInt(1, guildid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    String[] rankTitles = new String[]{
                        rs.getString("rank1title"), rs.getString("rank2title"), rs.getString("rank3title"),
                        rs.getString("rank4title"), rs.getString("rank5title")
                    };
                    return new GuildRow(rs.getString("name"), rs.getInt("GP"), rs.getInt("logo"), rs.getInt("logoColor"),
                            rs.getInt("logoBG"), rs.getInt("logoBGColor"), rs.getInt("capacity"), rankTitles,
                            rs.getInt("leader"), rs.getString("notice"), rs.getInt("signature"), rs.getInt("alliance"));
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "load");
        }

        return null;
    }

    public static List<Integer> getAllGuildIds() {
        List<Integer> ret = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT guildid FROM " + DB_TABLE_NAME)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ret.add(rs.getInt("guildid"));
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getAllGuildIds");
        }

        return ret;
    }

    public static boolean update(int guildid, int gp, int logo, int logoColor, int logoBG, int logoBGColor,
            String[] rankTitles, int capacity, String notice, int allianceId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            StringBuilder buf = new StringBuilder("UPDATE " + DB_TABLE_NAME + " SET GP = ?, logo = ?, logoColor = ?, logoBG = ?, logoBGColor = ?, ");
            for (int i = 1; i < 6; i++) {
                buf.append("rank" + i + "title = ?, ");
            }
            buf.append("capacity = ?, " + "notice = ?, alliance = ? WHERE guildid = ?");

            try (PreparedStatement ps = con.prepareStatement(buf.toString())) {
                ps.setInt(1, gp);
                ps.setInt(2, logo);
                ps.setInt(3, logoColor);
                ps.setInt(4, logoBG);
                ps.setInt(5, logoBGColor);
                ps.setString(6, rankTitles[0]);
                ps.setString(7, rankTitles[1]);
                ps.setString(8, rankTitles[2]);
                ps.setString(9, rankTitles[3]);
                ps.setString(10, rankTitles[4]);
                ps.setInt(11, capacity);
                ps.setString(12, notice);
                ps.setInt(13, allianceId);
                ps.setInt(14, guildid);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "update");
        }

        return false;
    }

    public static boolean delete(int guildid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE guildid = ?")) {
                ps.setInt(1, guildid);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "delete");
        }

        return false;
    }

    public static boolean updateAlliance(int guildid, int allianceId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET alliance = ? WHERE guildid = ?")) {
                ps.setInt(1, allianceId);
                ps.setInt(2, guildid);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "updateAlliance");
        }

        return false;
    }

    /**
     * Returns the guildid of the guild with this name, or -1 if there is no
     * such guild (or the lookup failed).
     */
    public static int findIdByName(String name) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT guildid FROM " + DB_TABLE_NAME + " WHERE name = ?")) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("guildid");
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "findIdByName");
        }

        return -1;
    }

    /**
     * Creates a new guild row. Returns the generated guildid, or 0 on
     * failure.
     */
    public static int create(int leaderId, String name, int signature) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (`leader`, `name`, `signature`, `alliance`) VALUES (?, ?, ?, 0)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, leaderId);
                ps.setString(2, name);
                ps.setInt(3, signature);
                ps.execute();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "create");
        }

        return 0;
    }

    public static boolean updateEmblem(int guildid, int logo, int logoColor, int logoBG, int logoBGColor) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET logo = ?, logoColor = ?, logoBG = ?, logoBGColor = ? WHERE guildid = ?")) {
                ps.setInt(1, logo);
                ps.setInt(2, logoColor);
                ps.setInt(3, logoBG);
                ps.setInt(4, logoBGColor);
                ps.setInt(5, guildid);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "updateEmblem");
        }

        return false;
    }

    public static boolean updateCapacity(int guildid, int capacity) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET capacity = ? WHERE guildid = ?")) {
                ps.setInt(1, capacity);
                ps.setInt(2, guildid);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "updateCapacity");
        }

        return false;
    }

    public static List<GuildRankingInfo> getTopByGP(int limit) {
        List<GuildRankingInfo> ret = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " ORDER BY `GP` DESC LIMIT ?")) {
                ps.setInt(1, limit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ret.add(new GuildRankingInfo(rs.getString("name"), rs.getInt("GP"), rs.getInt("logo"),
                                rs.getInt("logoColor"), rs.getInt("logoBG"), rs.getInt("logoBGColor")));
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getTopByGP");
        }

        return ret;
    }

    public static final class GuildRow {

        public final String name;
        public final int gp, logo, logoColor, logoBG, logoBGColor, capacity;
        public final String[] rankTitles;
        public final int leader;
        public final String notice;
        public final int signature;
        public final int alliance;

        public GuildRow(String name, int gp, int logo, int logoColor, int logoBG, int logoBGColor, int capacity,
                String[] rankTitles, int leader, String notice, int signature, int alliance) {
            this.name = name;
            this.gp = gp;
            this.logo = logo;
            this.logoColor = logoColor;
            this.logoBG = logoBG;
            this.logoBGColor = logoBGColor;
            this.capacity = capacity;
            this.rankTitles = rankTitles;
            this.leader = leader;
            this.notice = notice;
            this.signature = signature;
            this.alliance = alliance;
        }
    }

}
