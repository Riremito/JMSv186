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
package tacos.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import tacos.client.TacosCharacter;

/**
 *
 * @author Riremito
 */
public class TableMonsterBook extends TacosDB {

    public TableMonsterBook() {
        super("monsterbook");
    }

    public boolean load(TacosCharacter chr) {
        LinkedHashMap<Integer, Integer> cards = chr.getMonsterBook().getCards();

        try (PreparedStatement ps = prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE charid = ? ORDER BY cardid ASC")) {
            ps.setInt(1, chr.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cards.put(rs.getInt("cardid"), rs.getInt("level"));
                }
            }
            chr.getMonsterBook().update();
            return true;
        } catch (SQLException ex) {
            error();
        }

        return false;
    }

    public boolean save(TacosCharacter chr) {
        if (!setManual()) {
            return false;
        }

        try (PreparedStatement ps = prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE charid = ?")) {
            ps.setInt(1, chr.getId());
            ps.execute();
        } catch (SQLException ex) {
            error();
            rollback();
            return false;
        } finally {
            commit();
            setAuto();
        }

        if (chr.getMonsterBook().getCards().isEmpty()) {
            return true;
        }

        boolean first = true;
        StringBuilder query = new StringBuilder();

        for (Map.Entry<Integer, Integer> all : chr.getMonsterBook().getCards().entrySet()) {
            if (first) {
                first = false;
                query.append("INSERT INTO ").append(DB_TABLE_NAME).append(" VALUES (DEFAULT,");
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

        try (PreparedStatement ps1 = prepareStatement(query.toString())) {
            ps1.execute();
            return true;
        } catch (SQLException ex) {
            error();
        }

        return false;
    }
}
