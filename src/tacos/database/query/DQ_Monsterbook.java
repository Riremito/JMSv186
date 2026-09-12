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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import tacos.client.TacosCharacter;
import tacos.client.TacosMonsterBook;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Monsterbook {

    public static final String DB_TABLE_NAME = "monsterbook";

    public static boolean load(TacosCharacter chr) {
        LinkedHashMap<Integer, Integer> cards = chr.getMonsterBook().getCards();

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE charid = ? ORDER BY cardid ASC")) {
            ps.setInt(1, chr.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cards.put(rs.getInt("cardid"), rs.getInt("level"));
                }
            }
            chr.getMonsterBook().update();
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "load");
        }

        return false;
    }

    public static boolean save(TacosCharacter chr) {
        TacosMonsterBook monster_book = chr.getMonsterBook();
        if (monster_book.getModifiedCount() == 0) {
            return true;
        }
        monster_book.resetModifiedCount();
        if (monster_book.getCards().isEmpty()) {
            return true;
        }

        if (!DatabaseConnection.setManual()) {
            return false;
        }

        String card_data = "";
        for (Map.Entry<Integer, Integer> card : monster_book.getCards().entrySet()) {
            if (!card_data.isEmpty()) {
                card_data += ", ";
            }
            card_data += "(" + chr.getId() + ", " + card.getKey() + ", " + card.getValue() + ")";
        }

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (charid, cardid, level) VALUES " + card_data + " AS data ON DUPLICATE KEY UPDATE level = data.level")) {
            ps.execute();
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "save");
            DatabaseConnection.rollback();
        } finally {
            DatabaseConnection.commit();
            DatabaseConnection.setAuto();
        }

        return false;
    }
}
