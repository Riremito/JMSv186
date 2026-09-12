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
import java.util.Map;
import odin.client.ISkill;
import odin.client.SkillEntry;
import odin.constants.GameConstants;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 * NOTE: load/save here declare {@code throws SQLException} instead of
 * catching it internally, matching the original MapleCharacter behavior
 * (see DQ_Questinfo for the same rationale).
 *
 * @author Riremito
 */
public class DQ_Skills {

    public static final String DB_TABLE_NAME = "skills";

    public static List<SkillRow> loadAll(int characterId) throws SQLException {
        List<SkillRow> ret = new ArrayList<>();
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT skillid, skilllevel, masterlevel, expiration FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ret.add(new SkillRow(rs.getInt("skillid"), rs.getByte("skilllevel"), rs.getByte("masterlevel"), rs.getLong("expiration")));
                }
            }
        }
        return ret;
    }

    public static void deleteAndSaveAll(Connection con, int characterId, Map<ISkill, SkillEntry> skills) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
            ps.setInt(1, characterId);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (characterid, skillid, skilllevel, masterlevel, expiration) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, characterId);
            for (Map.Entry<ISkill, SkillEntry> skill : skills.entrySet()) {
                if (GameConstants.isApplicableSkill(skill.getKey().getId())) { //do not save additional skills
                    ps.setInt(2, skill.getKey().getId());
                    ps.setByte(3, skill.getValue().skillevel);
                    ps.setByte(4, skill.getValue().masterlevel);
                    ps.setLong(5, skill.getValue().expiration);
                    ps.execute();
                } else {
                    DebugLogger.ErrorLog("ApplicableSkill : error = " + skill.getKey().getId());
                }
            }
        }
    }

    public static final class SkillRow {

        public final int skillId;
        public final byte skillLevel;
        public final byte masterLevel;
        public final long expiration;

        public SkillRow(int skillId, byte skillLevel, byte masterLevel, long expiration) {
            this.skillId = skillId;
            this.skillLevel = skillLevel;
            this.masterLevel = masterLevel;
            this.expiration = expiration;
        }
    }

}
