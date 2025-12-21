/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.client;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import tacos.database.DatabaseConnection;
import odin.tools.Pair;

public class MapleKeyLayout {

    private boolean changed = false;
    private Map<Integer, Pair<Byte, Integer>> keymap;

    public MapleKeyLayout() {
        keymap = new HashMap<>();
    }

    public MapleKeyLayout(Map<Integer, Pair<Byte, Integer>> keys) {
        keymap = keys;
    }

    public Map<Integer, Pair<Byte, Integer>> Layout() {
        changed = true;
        return keymap;
    }

    public Map<Integer, Pair<Byte, Integer>> get() {
        return keymap;
    }

    public void saveKeys(int charid) throws SQLException {
        if (!changed || keymap.isEmpty()) {
            return;
        }
        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = con.prepareStatement("DELETE FROM keymap WHERE characterid = ?");
        ps.setInt(1, charid);
        ps.execute();
        ps.close();

        boolean first = true;
        StringBuilder query = new StringBuilder();

        for (Entry<Integer, Pair<Byte, Integer>> keybinding : keymap.entrySet()) {
            if (first) {
                first = false;
                query.append("INSERT INTO keymap VALUES (");
            } else {
                query.append(",(");
            }
            query.append("DEFAULT,");
            query.append(charid).append(",");
            query.append(keybinding.getKey().intValue()).append(",");
            query.append(keybinding.getValue().getLeft().byteValue()).append(",");
            query.append(keybinding.getValue().getRight().intValue()).append(")");
        }
        ps = con.prepareStatement(query.toString());
        ps.execute();
        ps.close();
    }
}
