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
import java.util.Collection;
import java.util.List;
import odin.handling.world.guild.MapleBBSThread.MapleBBSReply;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_BbsReplies {

    public static final String DB_TABLE_NAME = "bbs_replies";

    public static List<MapleBBSReply> loadByThreadId(int threadId) {
        List<MapleBBSReply> ret = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE threadid = ?")) {
                ps.setInt(1, threadId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ret.add(new MapleBBSReply(ret.size(), rs.getInt("postercid"), rs.getString("content"), rs.getLong("timestamp")));
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "loadByThreadId");
        }

        return ret;
    }

    public static boolean deleteByGuildId(int guildId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE guildid = ?")) {
                ps.setInt(1, guildId);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "deleteByGuildId");
        }

        return false;
    }

    public static boolean saveAll(int threadId, int guildId, Collection<MapleBBSReply> replies) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (`threadid`, `postercid`, `timestamp`, `content`, `guildid`) VALUES (?, ?, ?, ?, ?)")) {
                ps.setInt(5, guildId);
                for (MapleBBSReply r : replies) {
                    ps.setInt(1, threadId);
                    ps.setInt(2, r.ownerID);
                    ps.setLong(3, r.timestamp);
                    ps.setString(4, r.content);
                    ps.execute();
                }
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "saveAll");
        }

        return false;
    }

}
