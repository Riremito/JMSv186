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
import odin.client.BuddylistEntry;
import tacos.client.TacosCharacter;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Buddies {

    public static final String DB_TABLE_NAME = "buddies";

    public static ArrayList<BuddylistEntry> load(TacosCharacter chr) {
        ArrayList<BuddylistEntry> friend_list = new ArrayList<>();
        int character_id = chr.getId();

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT b.buddyid, b.pending, c.name as buddyname, c.job as buddyjob, c.level as buddylevel, b.groupname FROM " + DB_TABLE_NAME + " as b, " + DQ_Characters.DB_TABLE_NAME + " as c WHERE c.id = b.buddyid AND b.characterid = ?")) {
                ps.setInt(1, character_id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String friend_name = rs.getString("buddyname");
                        int friend_id = rs.getInt("buddyid");
                        String friend_tag = rs.getString("groupname");
                        int friend_level = rs.getInt("buddylevel");
                        int friend_job = rs.getInt("buddyjob");
                        int friend_pending = rs.getInt("pending");

                        BuddylistEntry ble = new BuddylistEntry(friend_name, friend_id, friend_tag, -1, true, friend_level, friend_job);
                        friend_list.add(ble);
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE pending = 1 AND characterid = ?")) {
                ps.setInt(1, character_id);
                ps.executeUpdate();
            }

            return friend_list;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "loadFromDb");
        }

        return null;
    }

}
