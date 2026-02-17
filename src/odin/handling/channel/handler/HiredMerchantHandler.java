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
package odin.handling.channel.handler;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import odin.client.MapleClient;
import odin.client.MapleCharacter;
import odin.constants.GameConstants;
import odin.client.inventory.ItemLoader;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;
import java.util.Map;
import tacos.packet.ops.OpsEntrustedShop;
import tacos.packet.response.ResCStoreBankDlg;
import tacos.packet.response.ResCWvsContext;
import odin.server.MapleInventoryManipulator;
import odin.server.MerchItemPackage;
import tacos.odin.OdinPair;

public class HiredMerchantHandler {

    public static final void UseHiredMerchant(MapleClient client) {
//	slea.readInt(); // TimeStamp

        if (client.getPlayer().getMap().allowPersonalShop()) {
            final byte state = checkExistance(client.getPlayer().getAccountId(), client.getPlayer().getId());

            switch (state) {
                case 1:
                    client.getPlayer().dropMessage(1, "Please claim your items from Fredrick first.");
                    break;
                case 0:
                    boolean merch = client.getWorld().hasMerchant(client.getPlayer().getAccountId());
                    if (!merch) {
//		    c.getPlayer().dropMessage(1, "The Hired Merchant is temporary disabled until it's fixed.");
                        client.getSession().write(ResCWvsContext.EntrustedShopCheckResult(OpsEntrustedShop.EntrustedShopRes_OpenPossible));
                    } else {
                        client.getPlayer().dropMessage(1, "Please close the existing store and try again.");
                    }
                    break;
                default:
                    client.getPlayer().dropMessage(1, "An unknown error occured.");
                    break;
            }
        } else {
            DebugLogger.ErrorLog("UseHiredMerchant dc.");
            client.getSession().close();
        }
    }

    private static final byte checkExistance(final int accid, final int charid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where accountid = ? OR characterid = ?");
            ps.setInt(1, accid);
            ps.setInt(2, charid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ps.close();
                rs.close();
                return 1;
            }
            rs.close();
            ps.close();
            return 0;
        } catch (SQLException se) {
            return -1;
        }
    }

    public static final void MerchantItemStore(MapleClient client, byte operation, String str) {
        if (client.getPlayer() == null) {
            return;
        }

        switch (operation) {
            case 20: {
                final int conv = client.getPlayer().getConversation();
                boolean merch = client.getWorld().hasMerchant(client.getPlayer().getAccountId());
                if (merch) {
                    client.getPlayer().dropMessage(1, "Please close the existing store and try again.");
                    client.getPlayer().setConversation(0);
                } else if (conv == 3) { // Hired Merch
                    final MerchItemPackage pack = loadItemFrom_Database(client.getPlayer().getId(), client.getPlayer().getAccountId());

                    if (pack == null) {
                        client.getPlayer().dropMessage(1, "You do not have any item(s) with Fredrick.");
                        client.getPlayer().setConversation(0);
                    } else if (pack.getItems().size() <= 0) { //error fix for complainers.
                        if (!check(client.getPlayer(), pack)) {
                            client.getSession().write(ResCStoreBankDlg.merchItem_Message((byte) 0x21));
                            return;
                        }
                        if (deletePackage(client.getPlayer().getId(), client.getPlayer().getAccountId(), pack.getPackageid())) {
                            client.getPlayer().gainMeso(pack.getMesos(), false);
                            client.getSession().write(ResCStoreBankDlg.merchItem_Message((byte) 0x1d));
                        } else {
                            client.getPlayer().dropMessage(1, "An unknown error occured.");
                        }
                    } else {
                        client.getSession().write(ResCStoreBankDlg.merchItemStore_ItemData(pack));
                    }
                }
                break;
            }
            case 25: { // Request take out iteme
                if (client.getPlayer().getConversation() != 3) {
                    return;
                }
                client.getSession().write(ResCStoreBankDlg.merchItemStore((byte) 0x24));
                break;
            }
            case 26: { // Take out item
                if (client.getPlayer().getConversation() != 3) {
                    return;
                }
                final MerchItemPackage pack = loadItemFrom_Database(client.getPlayer().getId(), client.getPlayer().getAccountId());

                if (pack == null) {
                    client.getPlayer().dropMessage(1, "An unknown error occured.");
                    return;
                }
                if (!check(client.getPlayer(), pack)) {
                    client.getSession().write(ResCStoreBankDlg.merchItem_Message((byte) 0x21));
                    return;
                }
                if (deletePackage(client.getPlayer().getId(), client.getPlayer().getAccountId(), pack.getPackageid())) {
                    client.getPlayer().gainMeso(pack.getMesos(), false);
                    for (IItem item : pack.getItems()) {
                        MapleInventoryManipulator.addFromDrop(client, item, false);
                    }
                    client.getSession().write(ResCStoreBankDlg.merchItem_Message((byte) 0x1d));
                } else {
                    client.getPlayer().dropMessage(1, "An unknown error occured.");
                }
                break;
            }
            case 27: { // Exit
                client.getPlayer().setConversation(0);
                break;
            }
        }
    }

    private static final boolean check(final MapleCharacter chr, final MerchItemPackage pack) {
        if (chr.getMeso() + pack.getMesos() < 0) {
            return false;
        }
        byte eq = 0, use = 0, setup = 0, etc = 0, cash = 0;
        for (IItem item : pack.getItems()) {
            final MapleInventoryType invtype = GameConstants.getInventoryType(item.getItemId());
            if (invtype == MapleInventoryType.EQUIP) {
                eq++;
            } else if (invtype == MapleInventoryType.USE) {
                use++;
            } else if (invtype == MapleInventoryType.SETUP) {
                setup++;
            } else if (invtype == MapleInventoryType.ETC) {
                etc++;
            } else if (invtype == MapleInventoryType.CASH) {
                cash++;
            }
        }
        if (chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq || chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() < use || chr.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup || chr.getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc || chr.getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash) {
            return false;
        }
        return true;
    }

    private static final boolean deletePackage(final int charid, final int accid, final int packageid) {
        final Connection con = DatabaseConnection.getConnection();

        try {
            PreparedStatement ps = con.prepareStatement("DELETE from hiredmerch where characterid = ? OR accountid = ? OR packageid = ?");
            ps.setInt(1, charid);
            ps.setInt(2, accid);
            ps.setInt(3, packageid);
            ps.execute();
            ps.close();
            ItemLoader.HIRED_MERCHANT.saveItems(null, packageid, accid, charid);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static final MerchItemPackage loadItemFrom_Database(final int charid, final int accountid) {
        final Connection con = DatabaseConnection.getConnection();

        try {
            PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where characterid = ? OR accountid = ?");
            ps.setInt(1, charid);
            ps.setInt(2, accountid);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                ps.close();
                rs.close();
                return null;
            }
            final int packageid = rs.getInt("PackageId");

            final MerchItemPackage pack = new MerchItemPackage();
            pack.setPackageid(packageid);
            pack.setMesos(rs.getInt("Mesos"));
            pack.setSentTime(rs.getLong("time"));

            ps.close();
            rs.close();

            Map<Integer, OdinPair<IItem, MapleInventoryType>> items = ItemLoader.HIRED_MERCHANT.loadItems(false, packageid, accountid, charid);
            if (items != null) {
                List<IItem> iters = new ArrayList<IItem>();
                for (OdinPair<IItem, MapleInventoryType> z : items.values()) {
                    iters.add(z.getLeft());
                }
                pack.setItems(iters);
            }

            return pack;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
