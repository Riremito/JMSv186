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
import odin.server.life.MonsterDropEntry;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_DropData {

    public static final String DB_TABLE_NAME = "drop_data";

    public static ArrayList<MonsterDropEntry> getDropByMobId(int mob_id) {
        ArrayList<MonsterDropEntry> ret = new ArrayList<>();

        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE dropperid = ?")) {
            ps.setInt(1, mob_id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int itemid = rs.getInt("itemid");
                    int chance = rs.getInt("chance");
                    ret.add(new MonsterDropEntry(
                            itemid,
                            chance,
                            rs.getInt("minimum_quantity"),
                            rs.getInt("maximum_quantity"),
                            rs.getShort("questid")));
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getDrops");
        }

        return ret;
    }
}
