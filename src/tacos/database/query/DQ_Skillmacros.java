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
import tacos.client.TacosCharacter;
import tacos.client.TacosSingleMacro;
import tacos.database.DatabaseConnection;

/**
 * NOTE: load/save here declare {@code throws SQLException} instead of catching
 * it internally, matching the original MapleCharacter behavior (see
 * DQ_Questinfo for the same rationale).
 *
 * @author Riremito
 */
public class DQ_Skillmacros {

    public static final String DB_TABLE_NAME = "skillmacros";

    public static void loadAll(TacosCharacter chr) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
            ps.setInt(1, chr.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int position = rs.getInt("position");
                    int skill_id_1 = rs.getInt("skill1");
                    int skill_id_2 = rs.getInt("skill2");
                    int skill_id_3 = rs.getInt("skill3");
                    String name = rs.getString("name");
                    int shout = rs.getInt("shout");

                    TacosSingleMacro tsm = new TacosSingleMacro();
                    tsm.setName(name);
                    tsm.setMute((shout != 0));
                    tsm.setSkill1(skill_id_1);
                    tsm.setSkill2(skill_id_2);
                    tsm.setSkill3(skill_id_3);

                    chr.getMacros().put(position, tsm);
                }
            }
        }
    }

    public static void deleteAndSaveAll(Connection con, TacosCharacter chr) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
            ps.setInt(1, chr.getId());
            ps.executeUpdate();
        }

        for (int index = 0; index < chr.getMacros().size(); index++) {
            TacosSingleMacro macro = chr.getMacros().get(index);
            if (macro != null) {
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (characterid, skill1, skill2, skill3, name, shout, position) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1, chr.getId());
                    ps.setInt(2, macro.getSkill1());
                    ps.setInt(3, macro.getSkill2());
                    ps.setInt(4, macro.getSkill3());
                    ps.setString(5, macro.getName());
                    ps.setInt(6, macro.isMute() ? 1 : 0);
                    ps.setInt(7, index);
                    ps.execute();
                }
            }
        }
    }
}
