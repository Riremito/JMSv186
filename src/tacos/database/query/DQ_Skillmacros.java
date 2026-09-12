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
import odin.client.SkillMacro;
import tacos.database.DatabaseConnection;

/**
 * NOTE: load/save here declare {@code throws SQLException} instead of
 * catching it internally, matching the original MapleCharacter behavior
 * (see DQ_Questinfo for the same rationale).
 *
 * @author Riremito
 */
public class DQ_Skillmacros {

    public static final String DB_TABLE_NAME = "skillmacros";
    public static final int SLOT_COUNT = 5;

    public static SkillMacro[] loadAll(int characterId) throws SQLException {
        SkillMacro[] ret = new SkillMacro[SLOT_COUNT];
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int position = rs.getInt("position");
                    ret[position] = new SkillMacro(rs.getInt("skill1"), rs.getInt("skill2"), rs.getInt("skill3"), rs.getString("name"), rs.getInt("shout"), position);
                }
            }
        }
        return ret;
    }

    public static void deleteAndSaveAll(Connection con, int characterId, SkillMacro[] skillMacros) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
            ps.setInt(1, characterId);
            ps.executeUpdate();
        }

        for (int i = 0; i < SLOT_COUNT; i++) {
            final SkillMacro macro = skillMacros[i];
            if (macro != null) {
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (characterid, skill1, skill2, skill3, name, shout, position) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1, characterId);
                    ps.setInt(2, macro.getSkill1());
                    ps.setInt(3, macro.getSkill2());
                    ps.setInt(4, macro.getSkill3());
                    ps.setString(5, macro.getName());
                    ps.setInt(6, macro.getShout());
                    ps.setInt(7, i);
                    ps.execute();
                }
            }
        }
    }

}
