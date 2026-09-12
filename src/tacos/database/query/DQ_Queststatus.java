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
import java.util.LinkedHashMap;
import java.util.Map;
import odin.client.MapleCharacter;
import odin.client.MapleQuestStatus;
import odin.server.quest.MapleQuest;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Queststatus {

    public static final String DB_TABLE_NAME = "queststatus";

    /**
     * NOTE: unlike {@code add}, load/save here declare
     * {@code throws SQLException} instead of catching it internally, since
     * these are called from MapleCharacter.loadCharFromDB/saveToDB, which
     * manage their own outer try/catch and (for saveToDB) an explicit
     * transaction that must see any failure in order to roll back
     * correctly.
     */
    public static Map<MapleQuest, MapleQuestStatus> loadAll(int characterId) throws SQLException {
        Map<MapleQuest, MapleQuestStatus> ret = new LinkedHashMap<>();
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final MapleQuest q = MapleQuest.getInstance(rs.getInt("quest"));
                    final MapleQuestStatus status = new MapleQuestStatus(q, rs.getByte("status"));
                    final long cTime = rs.getLong("time");
                    if (cTime > -1) {
                        status.setCompletionTime(cTime * 1000);
                    }
                    status.setForfeited(rs.getInt("forfeited"));
                    status.setCustomData(rs.getString("customData"));
                    for (Map.Entry<Integer, Integer> mobKill : DQ_Queststatusmobs.loadAll(rs.getInt("queststatusid")).entrySet()) {
                        status.setMobKills(mobKill.getKey(), mobKill.getValue());
                    }
                    ret.put(q, status);
                }
            }
        }
        return ret;
    }

    public static void deleteAndSaveAll(Connection con, int characterId, java.util.Collection<MapleQuestStatus> quests) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
            ps.setInt(1, characterId);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)", DatabaseConnection.RETURN_GENERATED_KEYS); PreparedStatement pse = con.prepareStatement("INSERT INTO " + DQ_Queststatusmobs.DB_TABLE_NAME + " VALUES (DEFAULT, ?, ?, ?)")) {
            ps.setInt(1, characterId);
            for (final MapleQuestStatus q : quests) {
                ps.setInt(2, q.getQuest().getId());
                ps.setInt(3, q.getStatus());
                ps.setInt(4, (int) (q.getCompletionTime() / 1000));
                ps.setInt(5, q.getForfeited());
                ps.setString(6, q.getCustomData());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();

                    if (q.hasMobKills()) {
                        for (int mob : q.getMobKills().keySet()) {
                            pse.setInt(1, rs.getInt(1));
                            pse.setInt(2, mob);
                            pse.setInt(3, q.getMobKills(mob));
                            pse.executeUpdate();
                        }
                    }
                }
            }
        }
    }

    public static boolean add(MapleCharacter chr) {
        if (!DatabaseConnection.setManual()) {
            return false;
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)", DatabaseConnection.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, chr.getId());
                for (MapleQuestStatus q : chr.getQuest_Map().values()) {
                    ps.setInt(2, q.getQuest().getId());
                    ps.setInt(3, q.getStatus());
                    ps.setInt(4, (int) (q.getCompletionTime() / 1000));
                    ps.setInt(5, q.getForfeited());
                    ps.setString(6, q.getCustomData());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        int queststatusid = rs.getInt(1);

                        if (!DQ_Queststatusmobs.add(queststatusid, q.getMobKills())) {
                            return false;
                        }
                    }
                }
                return true;
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
}
