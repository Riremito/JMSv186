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

import odin.client.inventory.Equip;
import tacos.debug.DebugLogger;
import tacos.server.map.object.TacosPet;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MapleRing;
import odin.constants.GameConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tacos.database.DatabaseConnection;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.server.TacosWorld;

/**
 * Extracted from the former generic {@code odin.client.inventory.ItemLoader}
 * enum logic, scoped to the "mtsitems" / "mtsequipment" table pair.
 *
 * @author Riremito
 */
public class DQ_MtsStorageItems {

    public static final String DB_TABLE_NAME = "mtsitems";
    public static final String DB_TABLE_NAME_EQUIP = "mtsequipment";

    public static Map<Integer, SimpleImmutableEntry<Item, MapleInventoryType>> load(int value, List<String> arg, boolean login, Integer... id) {
        try {
            List<Integer> lulz = Arrays.asList(id);
            Map<Integer, SimpleImmutableEntry<Item, MapleInventoryType>> items = new LinkedHashMap<>();
            if (lulz.size() != arg.size()) {
                return items;
            }
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM `");
            query.append(DB_TABLE_NAME);
            query.append("` LEFT JOIN `");
            query.append(DB_TABLE_NAME_EQUIP);
            query.append("` USING(`inventoryitemid`) WHERE `type` = ?");
            for (String g : arg) {
                query.append(" AND `");
                query.append(g);
                query.append("` = ?");
            }

            if (login) {
                query.append(" AND `inventorytype` = ");
                query.append(MapleInventoryType.EQUIPPED.getType());
            }

            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(query.toString());
            ps.setInt(1, value);
            for (int i = 0; i < lulz.size(); i++) {
                ps.setInt(i + 2, lulz.get(i));
            }
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MapleInventoryType mit = MapleInventoryType.getByType(rs.getByte("inventorytype"));

                if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
                    Equip equip = new Equip(rs.getInt("itemid"), rs.getShort("position"), rs.getInt("uniqueid"), rs.getByte("flag"));
                    if (!login) {
                        equip.setQuantity((short) 1);
                        equip.setOwner(rs.getString("owner"));
                        equip.setExpiration(rs.getLong("expiredate"));
                        equip.setRUC(rs.getInt("upgradeslots"));
                        equip.setCUC(rs.getByte("level"));
                        // not coded, incattackSpeed
                        equip.setSTR(rs.getInt("str"));
                        equip.setDEX(rs.getInt("dex"));
                        equip.setINT(rs.getInt("int"));
                        equip.setLUK(rs.getInt("luk"));
                        equip.setMaxHP(rs.getInt("hp"));
                        equip.setMaxMP(rs.getInt("mp"));
                        equip.setPAD(rs.getInt("watk"));
                        equip.setMAD(rs.getInt("matk"));
                        equip.setPDD(rs.getInt("wdef"));
                        equip.setMDD(rs.getInt("mdef"));
                        equip.setACC(rs.getInt("acc"));
                        equip.setEVA(rs.getInt("avoid"));
                        equip.setCraft(rs.getInt("hands"));
                        equip.setSpeed(rs.getInt("speed"));
                        equip.setJump(rs.getInt("jump"));
                        equip.setIUC(rs.getInt("ViciousHammer"));
                        equip.setLevelEXP(rs.getInt("itemEXP"));
                        //equip.setGMLog(rs.getString("GM_Log"));
                        equip.setDurability(rs.getInt("durability"));
                        equip.setCHUC(rs.getInt("enhance"));
                        equip.setGrade(rs.getInt("rank"));
                        equip.setHidden(rs.getInt("hidden"));
                        equip.setOption1(rs.getInt("potential1"));
                        equip.setPOption2(rs.getInt("potential2"));
                        equip.setOption3(rs.getInt("potential3"));
                        equip.setMaxHPr(rs.getInt("hpR"));
                        equip.setMaxMPr(rs.getInt("mpR"));
                        equip.setAttackSpeed(rs.getInt("incattackSpeed"));
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
                    items.put(rs.getInt("inventoryitemid"), new SimpleImmutableEntry<>(equip, mit));
                } else {
                    Item item = new Item(rs.getInt("itemid"), rs.getShort("position"), rs.getShort("quantity"), rs.getByte("flag"));
                    item.setUniqueId(rs.getInt("uniqueid"));
                    item.setOwner(rs.getString("owner"));
                    item.setExpiration(rs.getLong("expiredate"));
                    item.setGMLog(rs.getString("GM_Log"));
                    item.setGiftFrom(rs.getString("sender"));
                    if (GameConstants.isPet(item.getItemId())) {
                        if (item.getUniqueId() > -1) {
                            TacosPet pet = TacosPet.loadFromDb(item.getItemId(), item.getUniqueId(), item.getPosition());
                            if (pet != null) {
                                item.setPet(pet);
                            }
                        } else {
                            //O_O hackish fix
                            int new_unique = TacosWorld.getNextItemUniqueId();
                            item.setUniqueId(new_unique);
                            item.setPet(TacosPet.createPet(item.getItemId(), new_unique));
                        }
                    }
                    items.put(rs.getInt("inventoryitemid"), new SimpleImmutableEntry<>(item, mit));
                }
            }

            rs.close();
            ps.close();
            return items;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "load");
        }
        return new LinkedHashMap<>();
    }

    public static boolean save(int value, List<String> arg, List<SimpleImmutableEntry<Item, MapleInventoryType>> items, Integer... id) {
        return save(value, arg, items, DatabaseConnection.getConnection(), id);
    }

    public static boolean save(int value, List<String> arg, List<SimpleImmutableEntry<Item, MapleInventoryType>> items, Connection con, Integer... id) {
        try {
            List<Integer> lulz = Arrays.asList(id);
            if (lulz.size() != arg.size()) {
                return true;
            }
            StringBuilder query = new StringBuilder();
            query.append("DELETE FROM `");
            query.append(DB_TABLE_NAME);
            query.append("` WHERE `type` = ? AND (`");
            query.append(arg.get(0));
            query.append("` = ?");
            if (arg.size() > 1) {
                for (int i = 1; i < arg.size(); i++) {
                    query.append(" OR `");
                    query.append(arg.get(i));
                    query.append("` = ?");
                }
            }
            query.append(")");

            PreparedStatement ps = con.prepareStatement(query.toString());
            ps.setInt(1, value);
            for (int i = 0; i < lulz.size(); i++) {
                ps.setInt(i + 2, lulz.get(i));
            }
            ps.executeUpdate();
            ps.close();
            if (items == null) {
                return true;
            }
            StringBuilder query_2 = new StringBuilder("INSERT INTO `");
            query_2.append(DB_TABLE_NAME);
            query_2.append("` (");
            for (String g : arg) {
                query_2.append(g);
                query_2.append(", ");
            }
            query_2.append("itemid, inventorytype, position, quantity, owner, GM_Log, uniqueid, expiredate, flag, `type`, sender) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
            for (String g : arg) {
                query_2.append(", ?");
            }
            query_2.append(")");
            ps = con.prepareStatement(query_2.toString(), Statement.RETURN_GENERATED_KEYS);
            PreparedStatement pse = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME_EQUIP + " VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            Iterator<SimpleImmutableEntry<Item, MapleInventoryType>> iter = items.iterator();
            SimpleImmutableEntry<Item, MapleInventoryType> pair;
            while (iter.hasNext()) {
                pair = iter.next();
                Item item = pair.getKey();
                MapleInventoryType mit = pair.getValue();
                int i = 1;
                for (int x = 0; x < lulz.size(); x++) {
                    ps.setInt(i, lulz.get(x));
                    i++;
                }
                ps.setInt(i, item.getItemId());
                ps.setInt(i + 1, mit.getType());
                ps.setInt(i + 2, item.getPosition());
                ps.setInt(i + 3, item.getQuantity());
                ps.setString(i + 4, item.getOwner());
                ps.setString(i + 5, item.getGMLog());
                ps.setInt(i + 6, item.getUniqueId());
                ps.setLong(i + 7, item.getExpiration());
                ps.setByte(i + 8, item.getFlag());
                ps.setByte(i + 9, (byte) value);
                ps.setString(i + 10, item.getGiftFrom());
                ps.executeUpdate();

                if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
                    ResultSet rs = ps.getGeneratedKeys();

                    if (!rs.next()) {
                        throw new RuntimeException("Inserting item failed.");
                    }

                    pse.setInt(1, rs.getInt(1));
                    rs.close();
                    Equip equip = (Equip) item;
                    pse.setInt(2, equip.getRUC());
                    pse.setInt(3, equip.getCUC());
                    pse.setInt(4, equip.getSTR());
                    pse.setInt(5, equip.getDEX());
                    pse.setInt(6, equip.getINT());
                    pse.setInt(7, equip.getLUK());
                    pse.setInt(8, equip.getMaxHP());
                    pse.setInt(9, equip.getMaxMP());
                    pse.setInt(10, equip.getPAD());
                    pse.setInt(11, equip.getMAD());
                    pse.setInt(12, equip.getPDD());
                    pse.setInt(13, equip.getMDD());
                    pse.setInt(14, equip.getACC());
                    pse.setInt(15, equip.getEVA());
                    pse.setInt(16, equip.getCraft());
                    pse.setInt(17, equip.getSpeed());
                    pse.setInt(18, equip.getJump());
                    pse.setInt(19, equip.getIUC());
                    pse.setInt(20, equip.getLevelEXP());
                    pse.setInt(21, equip.getDurability());
                    pse.setInt(22, equip.getCHUC());
                    pse.setInt(23, equip.getGrade());
                    pse.setInt(24, equip.getHidden());
                    pse.setInt(25, equip.getOption1());
                    pse.setInt(26, equip.getOption2());
                    pse.setInt(27, equip.getOption3());
                    pse.setInt(28, equip.getMaxHPr());
                    pse.setInt(29, equip.getMaxMPr());
                    pse.setInt(30, equip.getAttackSpeed());
                    pse.executeUpdate();
                }
            }
            pse.close();
            ps.close();
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "save");
        }
        return false;
    }

}
