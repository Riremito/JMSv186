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
import odin.client.inventory.IEquip;
import odin.client.inventory.IItem;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryIdentifier;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MaplePet;
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
import tacos.odin.OdinPair;

/**
 * Extracted from the former generic {@code odin.client.inventory.ItemLoader}
 * enum logic, scoped to the "dueyitems" / "dueyequipment" table pair.
 *
 * NOTE: unlike most DQ_ classes, {@code load}/{@code save} here declare
 * {@code throws SQLException} instead of catching it internally. This
 * matches the original ItemLoader behavior exactly, since existing callers
 * (e.g. CashShop, MTSCart, MTSStorage, AbstractPlayerStore) rely on the
 * exception propagating rather than being swallowed.
 *
 * @author Riremito
 */
public class DQ_Dueyitems {

    public static final String DB_TABLE_NAME = "dueyitems";
    public static final String DB_TABLE_NAME_EQUIP = "dueyequipment";

    public static Map<Integer, OdinPair<IItem, MapleInventoryType>> load(int value, List<String> arg, boolean login, Integer... id) throws SQLException {
        List<Integer> lulz = Arrays.asList(id);
        Map<Integer, OdinPair<IItem, MapleInventoryType>> items = new LinkedHashMap<>();
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

        rs.close();
        ps.close();
        return items;
    }

    public static void save(int value, List<String> arg, List<OdinPair<IItem, MapleInventoryType>> items, Integer... id) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        save(value, arg, items, con, id);
    }

    public static void save(int value, List<String> arg, List<OdinPair<IItem, MapleInventoryType>> items, Connection con, Integer... id) throws SQLException {
        List<Integer> lulz = Arrays.asList(id);
        if (lulz.size() != arg.size()) {
            return;
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
            return;
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
        final Iterator<OdinPair<IItem, MapleInventoryType>> iter = items.iterator();
        OdinPair<IItem, MapleInventoryType> pair;
        while (iter.hasNext()) {
            pair = iter.next();
            IItem item = pair.getLeft();
            MapleInventoryType mit = pair.getRight();
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
                IEquip equip = (IEquip) item;
                pse.setInt(2, equip.getUpgradeSlots());
                pse.setInt(3, equip.getLevel());
                pse.setInt(4, equip.getStr());
                pse.setInt(5, equip.getDex());
                pse.setInt(6, equip.getInt());
                pse.setInt(7, equip.getLuk());
                pse.setInt(8, equip.getHp());
                pse.setInt(9, equip.getMp());
                pse.setInt(10, equip.getWatk());
                pse.setInt(11, equip.getMatk());
                pse.setInt(12, equip.getWdef());
                pse.setInt(13, equip.getMdef());
                pse.setInt(14, equip.getAcc());
                pse.setInt(15, equip.getAvoid());
                pse.setInt(16, equip.getHands());
                pse.setInt(17, equip.getSpeed());
                pse.setInt(18, equip.getJump());
                pse.setInt(19, equip.getViciousHammer());
                pse.setInt(20, equip.getItemEXP());
                pse.setInt(21, equip.getDurability());
                pse.setInt(22, equip.getEnhance());
                pse.setInt(23, equip.getRank());
                pse.setInt(24, equip.getHidden());
                pse.setInt(25, equip.getPotential1());
                pse.setInt(26, equip.getPotential2());
                pse.setInt(27, equip.getPotential3());
                pse.setInt(28, equip.getHpR());
                pse.setInt(29, equip.getMpR());
                pse.setInt(30, equip.getIncAttackSpeed());
                pse.executeUpdate();
            }
        }
        pse.close();
        ps.close();
    }

}
