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
import tacos.client.TacosCharacter;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_MonsterBook {

    public static final String DB_TABLE_NAME = "monsterbook";

    public static boolean load(TacosCharacter chr) {
        LinkedHashMap<Integer, Integer> cards = chr.getMonsterBook().getCards();

        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE charid = ? ORDER BY cardid ASC")) {
            ps.setInt(1, chr.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cards.put(rs.getInt("cardid"), rs.getInt("level"));
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "load");
            return false;
        }

        return true;
    }

    public static boolean save(TacosCharacter chr) {
        if (!DatabaseConnection.setManual()) {
            return false;
        }

        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM monsterbook WHERE charid = ?")) {
            ps.setInt(1, chr.getId());
            ps.execute();
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "save 1");
            DatabaseConnection.rollback();
            return false;
        } finally {
            DatabaseConnection.commit();
            DatabaseConnection.setAuto();
        }

        boolean first = true;
        StringBuilder query = new StringBuilder();

        for (Map.Entry<Integer, Integer> all : chr.getMonsterBook().getCards().entrySet()) {
            if (first) {
                first = false;
                query.append("INSERT INTO monsterbook VALUES (DEFAULT,");
            } else {
                query.append(",(DEFAULT,");
            }
            query.append(chr.getId());
            query.append(",");
            query.append(all.getKey()); // Card ID
            query.append(",");
            query.append(all.getValue()); // Card level
            query.append(")");
        }

        try (PreparedStatement ps1 = con.prepareStatement(query.toString())) {
            ps1.execute();
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "save 2");
        }

        return true;
    }
}
