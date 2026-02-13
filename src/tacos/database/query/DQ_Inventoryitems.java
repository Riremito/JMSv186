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
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import odin.client.MapleCharacter;
import odin.client.inventory.Equip;
import odin.client.inventory.IEquip;
import odin.client.inventory.IItem;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventory;
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

    public static boolean delete(MapleCharacter chr) {
        /*
        if (!DatabaseConnection.setManual()) {
            return false;
        }
         */

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE type = ? AND characterid = ?;")) {
                ps.setByte(1, (byte) InvTypeDB.INVENTORY.get());
                ps.setInt(2, chr.getId());
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

    public static boolean add(MapleCharacter chr) {
        if (!DatabaseConnection.setManual()) {
            return false;
        }

        delete(chr);

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (characterid, itemid, inventorytype, position, quantity, owner, GM_Log, uniqueid, expiredate, flag, type, sender) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
                for (MapleInventory iv : chr.getInventorys()) {
                    for (IItem item : iv.list()) {
                        ps.setInt(1, chr.getId());
                        ps.setInt(2, item.getItemId());
                        ps.setInt(3, iv.getType().getType()); // equip, consume...
                        ps.setInt(4, item.getPosition());
                        ps.setInt(5, item.getQuantity());
                        ps.setString(6, item.getOwner());
                        ps.setString(7, item.getGMLog());
                        ps.setInt(8, item.getUniqueId());
                        ps.setLong(9, item.getExpiration());
                        ps.setByte(10, item.getFlag());
                        ps.setByte(11, (byte) InvTypeDB.INVENTORY.get()); // inventory, storage...
                        ps.setString(12, item.getGiftFrom());
                        ps.executeUpdate();
                        // equip stat
                        if (iv.getType() == MapleInventoryType.EQUIP || iv.getType() == MapleInventoryType.EQUIPPED) {
                            try (ResultSet rs = ps.getGeneratedKeys()) {
                                if (!rs.next()) {
                                    return false;
                                }
                                int unique_id = rs.getInt(1);
                                DQ_Inventoryequipment.add(unique_id, (IEquip) item);
                            }
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

    public static Map<Integer, OdinPair<IItem, MapleInventoryType>> load(MapleCharacter chr, boolean is_avatar_look) {
        Map<Integer, OdinPair<IItem, MapleInventoryType>> items = new LinkedHashMap<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " LEFT JOIN " + DQ_Inventoryequipment.DB_TABLE_NAME + " USING(inventoryitemid) WHERE type = ? AND characterid = ?;")) {
                ps.setByte(1, (byte) InvTypeDB.INVENTORY.get());
                ps.setInt(2, chr.getId());
                // AND inventorytype = MapleInventoryType.EQUIPPED.getType()
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    MapleInventoryType mit = MapleInventoryType.getByType(rs.getByte("inventorytype"));

                    if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
                        Equip equip = new Equip(rs.getInt("itemid"), rs.getShort("position"), rs.getInt("uniqueid"), rs.getByte("flag"));
                        if (!is_avatar_look) {
                            equip.setQuantity((short) 1);
                            equip.setOwner(rs.getString("owner"));
                            equip.setExpiration(rs.getLong("expiredate"));
                            equip.setUpgradeSlots(rs.getInt("upgradeslots"));
                            equip.setLevel(rs.getByte("level"));
                            // not coded, incattackSpeed
                            equip.setStr(rs.getInt("str"));
                            equip.setDex(rs.getInt("dex"));
                            equip.setInt(rs.getInt("int"));
                            equip.setLuk(rs.getInt("luk"));
                            equip.setHp(rs.getInt("hp"));
                            equip.setMp(rs.getInt("mp"));
                            equip.setWatk(rs.getInt("watk"));
                            equip.setMatk(rs.getInt("matk"));
                            equip.setWdef(rs.getInt("wdef"));
                            equip.setMdef(rs.getInt("mdef"));
                            equip.setAcc(rs.getInt("acc"));
                            equip.setAvoid(rs.getInt("avoid"));
                            equip.setHands(rs.getInt("hands"));
                            equip.setSpeed(rs.getInt("speed"));
                            equip.setJump(rs.getInt("jump"));
                            equip.setViciousHammer(rs.getInt("ViciousHammer"));
                            equip.setItemEXP(rs.getInt("itemEXP"));
                            //equip.setGMLog(rs.getString("GM_Log"));
                            equip.setDurability(rs.getInt("durability"));
                            equip.setEnhance(rs.getInt("enhance"));
                            equip.setRank(rs.getInt("rank"));
                            equip.setHidden(rs.getInt("hidden"));
                            equip.setPotential1(rs.getInt("potential1"));
                            equip.setPotential2(rs.getInt("potential2"));
                            equip.setPotential3(rs.getInt("potential3"));
                            equip.setHpR(rs.getInt("hpR"));
                            equip.setMpR(rs.getInt("mpR"));
                            equip.setIncAttackSpeed(rs.getInt("incattackSpeed"));
                            equip.setGiftFrom(rs.getString("sender"));
                            if (equip.getUniqueId() > -1) {
                                if (GameConstants.isEffectRing(rs.getInt("itemid"))) {
                                    MapleRing ring = MapleRing.loadFromDb(equip.getUniqueId(), mit.equals(MapleInventoryType.EQUIPPED));
                                    if (ring != null) {
                                        equip.setRing(ring);
                                    }
                                }
                            }
                        }
                        items.put(rs.getInt("inventoryitemid"), new OdinPair<>(equip.copy(), mit));
                    } else {
                        Item item = new Item(rs.getInt("itemid"), rs.getShort("position"), rs.getShort("quantity"), rs.getByte("flag"));
                        item.setUniqueId(rs.getInt("uniqueid"));
                        item.setOwner(rs.getString("owner"));
                        item.setExpiration(rs.getLong("expiredate"));
                        item.setGMLog(rs.getString("GM_Log"));
                        item.setGiftFrom(rs.getString("sender"));
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
                        items.put(rs.getInt("inventoryitemid"), new OdinPair<>(item.copy(), mit));
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
