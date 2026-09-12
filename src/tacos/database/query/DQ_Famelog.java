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
import java.util.ArrayList;
import java.util.List;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 * NOTE: {@code loadRecent} declares {@code throws SQLException} instead of
 * catching it internally, matching the original MapleCharacter.loadCharFromDB
 * behavior (see DQ_Questinfo for the same rationale). {@code insert} instead
 * catches internally (matching the original MapleCharacter.hasGivenFame,
 * which also swallowed the exception).
 *
 * @author Riremito
 */
public class DQ_Famelog {

    public static final String DB_TABLE_NAME = "famelog";

    public static RecentFame loadRecent(int characterId) throws SQLException {
        long lastfametime = 0;
        List<Integer> lastmonthfameids = new ArrayList<>(31);
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT `characterid_to`,`when` FROM " + DB_TABLE_NAME + " WHERE characterid = ? AND DATEDIFF(NOW(),`when`) < 30")) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lastfametime = Math.max(lastfametime, rs.getTimestamp("when").getTime());
                    lastmonthfameids.add(Integer.valueOf(rs.getInt("characterid_to")));
                }
            }
        }
        return new RecentFame(lastfametime, lastmonthfameids);
    }

    public static boolean insert(int characterId, int characterIdTo) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (characterid, characterid_to) VALUES (?, ?)")) {
                ps.setInt(1, characterId);
                ps.setInt(2, characterIdTo);
                ps.execute();
                return true;
            }
        } catch (SQLException e) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "insert");
        }

        return false;
    }

    public static final class RecentFame {

        public final long lastFameTime;
        public final List<Integer> lastMonthFameIds;

        public RecentFame(long lastFameTime, List<Integer> lastMonthFameIds) {
            this.lastFameTime = lastFameTime;
            this.lastMonthFameIds = lastMonthFameIds;
        }
    }

}
