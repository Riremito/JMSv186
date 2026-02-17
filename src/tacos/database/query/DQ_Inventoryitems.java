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

import tacos.database.ops.InvTypeDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import odin.client.inventory.Equip;
import odin.client.inventory.IEquip;
import odin.client.inventory.IItem;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryIdentifier;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MaplePet;
import odin.client.inventory.MapleRing;
import odin.constants.GameConstants;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;
import tacos.odin.OdinPair;

/**
 *
 * @author Riremito
 */
public class DQ_Inventoryitems {

    public static final String DB_TABLE_NAME = "inventoryitems";

    public static boolean delete(InvTypeDB itb, int owner_id) {
        /*
        if (!DatabaseConnection.setManual()) {
            return false;
        }
         */

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE type = ? AND characterid = ?;")) {
                ps.setByte(1, (byte) itb.ordinal());
                ps.setInt(2, owner_id);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "delete");
            //DatabaseConnection.rollback();
        } finally {
            //DatabaseConnection.commit();
            //DatabaseConnection.setAuto();
        }

        return false;
    }

    public static boolean add(InvTypeDB itb, int owner_id, ArrayList<IItem> items) {
        if (!DatabaseConnection.setManual()) {
            return false;
        }

        delete(itb, owner_id);

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (type, characterid, itemid, inventorytype, position, quantity, owner, GM_Log, uniqueid, expiredate, flag, sender) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {

                for (IItem item : items) {
                    MapleInventoryType inv_type = GameConstants.getInventoryType(item.getItemId());
                    if (inv_type == MapleInventoryType.EQUIP) {
                        if (item.getPosition() < 0) {
                            inv_type = MapleInventoryType.EQUIPPED;
                        }
                    }
                    ps.setByte(1, (byte) itb.ordinal()); // inventory, storage...
                    ps.setInt(2, owner_id);
                    ps.setInt(3, item.getItemId());
                    ps.setInt(4, inv_type.getType()); // equip, consume...
                    ps.setInt(5, item.getPosition());
                    ps.setInt(6, item.getQuantity());
                    ps.setString(7, item.getOwner());
                    ps.setString(8, item.getGMLog());
                    ps.setInt(9, item.getUniqueId());
                    ps.setLong(10, item.getExpiration());
                    ps.setByte(11, item.getFlag());
                    ps.setString(12, item.getGiftFrom());
                    ps.executeUpdate();
                    // equip stat
                    if (inv_type == MapleInventoryType.EQUIP || inv_type == MapleInventoryType.EQUIPPED) {
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (!rs.next()) {
                                return false;
                            }
                            int unique_id = rs.getInt(1);
                            DQ_Inventoryequipment.add(unique_id, (IEquip) item);
                        }
                    }
                }
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "add");
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "" + ex);
            DatabaseConnection.rollback();
        } finally {
            DatabaseConnection.commit();
            DatabaseConnection.setAuto();
        }

        return false;
    }

    public static Map<Integer, OdinPair<IItem, MapleInventoryType>> load(InvTypeDB itb, int owner_id) {
        return load(itb, owner_id, false);
    }

    public static Map<Integer, OdinPair<IItem, MapleInventoryType>> load(InvTypeDB itb, int owner_id, boolean is_avatar_look) {
        Map<Integer, OdinPair<IItem, MapleInventoryType>> items = new LinkedHashMap<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE type = ? AND characterid = ?;")) {
                ps.setByte(1, (byte) itb.ordinal());
                ps.setInt(2, owner_id);
                // AND inventorytype = MapleInventoryType.EQUIPPED.getType()
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    MapleInventoryType mit = MapleInventoryType.getByType(rs.getByte("inventorytype"));
                    int inventory_item_uid = rs.getInt("inventoryitemid");
                    int item_id = rs.getInt("itemid"); // item id.
                    int item_slot = rs.getInt("position"); // item slot.
                    int item_quantity = rs.getInt("quantity");
                    int item_uid = rs.getInt("uniqueid"); // item uid.
                    int item_flag = rs.getByte("flag");
                    long item_expiredate = rs.getLong("expiredate");
                    String item_owner = rs.getString("owner"); // name tag.
                    String item_sender = rs.getString("sender");

                    if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
                        Equip equip = new Equip(item_id, (short) item_slot, item_uid, (byte) item_flag);
                        equip.setOwner(item_owner); // inv
                        equip.setExpiration(item_expiredate); // inv
                        equip.setGiftFrom(item_sender); // inv
                        equip.setQuantity((short) item_quantity); // should be 1.

                        if (!is_avatar_look) {
                            DQ_Inventoryequipment.load(inventory_item_uid, equip); // equip stat.
                            if (equip.getUniqueId() > -1) {
                                if (GameConstants.isEffectRing(item_id)) {
                                    MapleRing ring = MapleRing.loadFromDb(equip.getUniqueId(), mit.equals(MapleInventoryType.EQUIPPED));
                                    if (ring != null) {
                                        equip.setRing(ring);
                                    }
                                }
                            }
                        }
                        items.put(inventory_item_uid, new OdinPair<>(equip.copy(), mit));
                    } else {
                        Item item = new Item(item_id, (short) item_slot, (short) item_quantity, (byte) item_flag);
                        item.setUniqueId(item_uid);
                        item.setOwner(item_owner);
                        item.setExpiration(item_expiredate);
                        item.setGiftFrom(item_sender);
                        if (GameConstants.isPet(item.getItemId())) {
                            if (item.getUniqueId() > -1) {
                                MaplePet pet = MaplePet.loadFromDb(item.getItemId(), item.getUniqueId(), item.getPosition());
                                if (pet != null) {
                                    item.setPet(pet);
                                }
                            } else {
                                //O_O hackish fix
                                final int new_unique = MapleInventoryIdentifier.getInstance();
                                item.setUniqueId(new_unique);
                                item.setPet(MaplePet.createPet(item.getItemId(), new_unique));
                            }
                        }
                        items.put(inventory_item_uid, new OdinPair<>(item.copy(), mit));
                    }
                }
                return items;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "load");
            items.clear();
        }
        return items;
    }

}
