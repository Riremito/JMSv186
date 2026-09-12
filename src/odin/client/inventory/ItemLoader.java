/*
This file is part of the ZeroFusion MapleStory Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>
ZeroFusion organized by "RMZero213" <RMZero213@hotmail.com>

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
package odin.client.inventory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import tacos.database.DatabaseConnection;
import tacos.database.query.DQ_Csitems;
import tacos.database.query.DQ_Dueyitems;
import tacos.database.query.DQ_Hiredmerchitems;
import tacos.database.query.DQ_MtsStorageItems;
import tacos.database.query.DQ_MtsTransferItems;
import tacos.odin.OdinPair;

public enum ItemLoader {

    CASHSHOP_EXPLORER("csitems", "csequipment", 2, "accountid"),
    CASHSHOP_CYGNUS("csitems", "csequipment", 3, "accountid"),
    CASHSHOP_ARAN("csitems", "csequipment", 4, "accountid"),
    HIRED_MERCHANT("hiredmerchitems", "hiredmerchequipment", 5, "packageid", "accountid", "characterid"),
    DUEY("dueyitems", "dueyequipment", 6, "packageid"),
    CASHSHOP_EVAN("csitems", "csequipment", 7, "accountid"),
    MTS("mtsitems", "mtsequipment", 8, "packageid"),
    MTS_TRANSFER("mtstransfer", "mtstransferequipment", 9, "characterid"),
    CASHSHOP_DB("csitems", "csequipment", 10, "accountid"),
    CASHSHOP_RESIST("csitems", "csequipment", 11, "accountid");
    private int value;
    private String table, table_equip;
    private List<String> arg;

    private ItemLoader(String table, String table_equip, int value, String... arg) {
        this.table = table;
        this.table_equip = table_equip;
        this.value = value;
        this.arg = Arrays.asList(arg);
    }

    public Map<Integer, OdinPair<IItem, MapleInventoryType>> loadItems(boolean login, Integer... id) throws SQLException {
        switch (this) {
            case CASHSHOP_EXPLORER:
            case CASHSHOP_CYGNUS:
            case CASHSHOP_ARAN:
            case CASHSHOP_EVAN:
            case CASHSHOP_DB:
            case CASHSHOP_RESIST:
                return DQ_Csitems.load(value, arg, login, id);
            case HIRED_MERCHANT:
                return DQ_Hiredmerchitems.load(value, arg, login, id);
            case DUEY:
                return DQ_Dueyitems.load(value, arg, login, id);
            case MTS:
                return DQ_MtsStorageItems.load(value, arg, login, id);
            case MTS_TRANSFER:
                return DQ_MtsTransferItems.load(value, arg, login, id);
            default:
                throw new IllegalStateException("Unhandled ItemLoader constant: " + this);
        }
    }

    public void saveItems(List<OdinPair<IItem, MapleInventoryType>> items, Integer... id) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        saveItems(items, con, id);
    }

    public void saveItems(List<OdinPair<IItem, MapleInventoryType>> items, Connection con, Integer... id) throws SQLException {
        switch (this) {
            case CASHSHOP_EXPLORER:
            case CASHSHOP_CYGNUS:
            case CASHSHOP_ARAN:
            case CASHSHOP_EVAN:
            case CASHSHOP_DB:
            case CASHSHOP_RESIST:
                DQ_Csitems.save(value, arg, items, con, id);
                return;
            case HIRED_MERCHANT:
                DQ_Hiredmerchitems.save(value, arg, items, con, id);
                return;
            case DUEY:
                DQ_Dueyitems.save(value, arg, items, con, id);
                return;
            case MTS:
                DQ_MtsStorageItems.save(value, arg, items, con, id);
                return;
            case MTS_TRANSFER:
                DQ_MtsTransferItems.save(value, arg, items, con, id);
                return;
            default:
                throw new IllegalStateException("Unhandled ItemLoader constant: " + this);
        }
    }
}
