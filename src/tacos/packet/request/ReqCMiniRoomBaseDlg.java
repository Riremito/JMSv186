/*
 * Copyright (C) 2025 Riremito
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
package tacos.packet.request;

import java.util.Arrays;
import odin.client.MapleCharacter;
import tacos.debug.DebugLogger;
import java.util.List;
import odin.client.inventory.IItem;
import odin.client.inventory.ItemFlag;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleTrade;
import odin.server.maps.FieldLimitType;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsMiniRoomProtocol;
import tacos.packet.response.ResCEmployeePool;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapObject;
import odin.server.maps.MapleMapObjectType;
import odin.server.shops.HiredMerchant;
import odin.server.shops.IMaplePlayerShop;
import odin.server.shops.MapleMiniGame;
import odin.server.shops.MaplePlayerShop;
import odin.server.shops.MaplePlayerShopItem;
import odin.tools.Pair;
import tacos.packet.response.wrapper.WrapCWvsContext;

/**
 *
 * @author Riremito
 */
public class ReqCMiniRoomBaseDlg {

    public static boolean OnMiniRoom(MapleMap map, MapleCharacter chr, ClientPacket cp) {
        byte protocol_req = cp.Decode1();

        switch (OpsMiniRoomProtocol.find(protocol_req)) {
            case MRP_Create: {
                // new code.
                /*
                byte miniroom_type = cp.Decode1();
                if (OpsMiniRoomType.find(miniroom_type) == OpsMiniRoomType.MR_EntrustedShop) {
                    String title = cp.DecodeStr();
                    byte es_req = cp.Decode1();
                    if (OpsEntrustedShop.find(es_req) != OpsEntrustedShop.EntrustedShopReq_CheckOpenPossible) {
                        return false;
                    }
                    short cash_item_slot = cp.Decode2();
                    int cash_item_id = cp.Decode4();

                    int cash_item_type = cash_item_id / 10000;
                    if (cash_item_type != 503 || !DWI_LoadXML.getItem().isValidID(cash_item_id)) {
                        return false;
                    }

                    Runnable item_use = chr.checkItemSlot(cash_item_slot, cash_item_id);
                    if (item_use == null) {
                        return false;
                    }

                    return true;
                }
                return false;
                 */
                // old code

                final byte createType = cp.Decode1();
                if (createType == 3) { // trade
                    MapleTrade.startTrade(chr, false);
                    return true;
                }
                if (createType == 6) {
                    // 未使用の可能性あり
                    int unk1 = cp.Decode4();
                    // 交換窓のID
                    final int tradeid = cp.Decode4();
                    MapleTrade.startTrade(chr, true);
                    return true;
                }
                if (createType == 1 || createType == 2 || createType == 4 || createType == 5) { // shop
                    /*
                    if (createType == 4 && !chr.isAdmin()) { //not hired merch... blocked playershop
                        c.getSession().write(MaplePacketCreator.enableActions());
                        return;
                    }
                     */
                    if (chr.getMap().getMapObjectsInRange(chr.getPosition(), 20000, Arrays.asList(MapleMapObjectType.SHOP, MapleMapObjectType.HIRED_MERCHANT)).isEmpty()) {
                        if (createType == 1 || createType == 2) {
                            if (FieldLimitType.Minigames.check(chr.getMap().getFieldLimit())) {
                                chr.dropMessage(1, "You may not use minigames here.");
                                chr.SendPacket(WrapCWvsContext.updateStat());
                                return true;
                            }
                        }
                    } else {
                        chr.dropMessage(1, "You may not establish a store here.");
                        chr.SendPacket(WrapCWvsContext.updateStat());
                        return true;
                    }
                    final String desc = cp.DecodeStr();
                    String pass = "";
                    byte unk2 = cp.Decode1();
                    if (unk2 > 0 && (createType == 1 || createType == 2)) {
                        pass = cp.DecodeStr();
                    }
                    // たぶんなんかがおかしい
                    // ごもく @007F 00 02 04 00 63 61 72 64 00 02
                    // 神経衰弱 @007F 00 02 01 00 66 00 [01] 最後の1バイトがサイズ
                    if (createType == 1 || createType == 2) {
                        final int piece = cp.Decode1();
                        final int itemId = createType == 1 ? (4080000 + piece) : 4080100;
                        if (!chr.haveItem(itemId) || (chr.getMapId() >= 910000001 && chr.getMapId() <= 910000022)) {
                            return true;
                        }
                        MapleMiniGame game = new MapleMiniGame(chr, itemId, desc, pass, createType); //itemid
                        game.setPieceType(piece);
                        chr.setPlayerShop(game);
                        game.setAvailable(true);
                        game.setOpen(true);
                        game.send(chr.getClient());
                        chr.getMap().addMapObject(game);
                        game.update();
                    } else {
                        short item_slot = cp.Decode2();
                        int item_id = cp.Decode4();
                        IItem shop = chr.getInventory(MapleInventoryType.CASH).getItem(item_slot);

                        if (shop == null || shop.getQuantity() <= 0 || shop.getItemId() != item_id || chr.getMapId() < 910000001 || chr.getMapId() > 910000022) {
                            return true;
                        }
                        if (createType == 4) {
                            MaplePlayerShop mps = new MaplePlayerShop(chr, shop.getItemId(), desc);
                            chr.setPlayerShop(mps);
                            chr.getMap().addMapObject(mps);
                            chr.SendPacket(ResCMiniRoomBaseDlg.getPlayerStore(chr, true));
                        } else {
                            final HiredMerchant merch = new HiredMerchant(chr, shop.getItemId(), desc);
                            chr.setPlayerShop(merch);
                            chr.setRemoteStore(merch);
                            chr.getMap().addMapObject(merch);
                            chr.SendPacket(ResCMiniRoomBaseDlg.getHiredMerch(chr, merch, true));
                        }
                    }
                }
                return true;
            }
            case MRP_Invite: {
                int character_id = cp.Decode4();
                MapleTrade.inviteTrade(chr, chr.getMap().getCharacterById(character_id));
                return true;
            }
            case MRP_InviteResult: {
                MapleTrade.declineTrade(chr);
                return true;
            }
            case MRP_Enter: {
                // 交換
                if (chr.getTrade() != null && chr.getTrade().getPartner() != null) {
                    MapleTrade.visitTrade(chr, chr.getTrade().getPartner().getChr(), chr.getTrade().getPartner().IsPointTrading());
                    return true;
                }
                // new code
                /*
                int miniroom_id = cp.Decode4();
                byte unk = cp.Decode1();

                HiredMerchant hm = (HiredMerchant) chr.getMap().getMapObject(miniroom_id, MapleMapObjectType.HIRED_MERCHANT);
                if (hm == null) {
                    DebugLogger.ErrorLog("hm == null.");
                    return false;
                }
                //hm.addVisitor(chr);
                chr.SendPacket(ResCMiniRoomBaseDlg.EnterResultStatic(hm, chr));
                 */
                // old code
                {
                    final int obid = cp.Decode4();
                    MapleMapObject ob = chr.getMap().getMapObject(obid, MapleMapObjectType.HIRED_MERCHANT);
                    if (ob == null) {
                        ob = chr.getMap().getMapObject(obid, MapleMapObjectType.SHOP);
                    }

                    if (ob instanceof IMaplePlayerShop && chr.getPlayerShop() == null) {
                        final IMaplePlayerShop ips = (IMaplePlayerShop) ob;

                        if (ob instanceof HiredMerchant) {
                            final HiredMerchant merchant = (HiredMerchant) ips;
                            if (merchant.isOwner(chr)) {
                                merchant.setOpen(false);
                                // "商店の主人が物品整理中でございます。もうしばらく後でご利用ください。"
                                //merchant.removeAllVisitors((byte) 17, (byte) 0);
                                List<Pair<Byte, MapleCharacter>> visitors = ips.getVisitors();
                                for (int i = 0; i < visitors.size(); i++) {
                                    visitors.get(i).getRight().getClient().getSession().write(ResCMiniRoomBaseDlg.MaintenanceHiredMerchant((byte) i + 1));
                                    System.out.println("slot = " + i + "char = " + visitors.get(i).getRight().getName());
                                    visitors.get(i).getRight().setPlayerShop(null);
                                    ips.removeVisitor(visitors.get(i).getRight());
                                }

                                chr.setPlayerShop(ips);
                                chr.SendPacket(ResCMiniRoomBaseDlg.getHiredMerch(chr, merchant, false));
                            } else {
                                if (!merchant.isOpen() || !merchant.isAvailable()) {
                                    // パケットでこのメッセージが出せそう
                                    chr.dropMessage(1, "商店の主人が物品整理中でございます。もうしばらく後でご利用ください。test");
                                    //c.getSession().write(PlayerShopPacket.MaintenanceHiredMerchant(1));
                                } else {
                                    if (ips.getFreeSlot() == -1) {
                                        chr.dropMessage(1, "This shop has reached it's maximum capacity, please come by later.");
                                    } else if (merchant.isInBlackList(chr.getName())) {
                                        chr.dropMessage(1, "You have been banned from this store.");
                                    } else {
                                        chr.setPlayerShop(ips);
                                        merchant.addVisitor(chr);
                                        chr.SendPacket(ResCMiniRoomBaseDlg.getHiredMerch(chr, merchant, false));
                                    }
                                }
                            }
                        } else {
                            if (ips instanceof MaplePlayerShop && ((MaplePlayerShop) ips).isBanned(chr.getName())) {
                                chr.dropMessage(1, "You have been banned from this store.");
                                return true;
                            } else {
                                if (ips.getFreeSlot() < 0 || ips.getVisitorSlot(chr) > -1 || !ips.isOpen() || !ips.isAvailable()) {
                                    chr.SendPacket(ResCMiniRoomBaseDlg.getMiniGameFull());
                                } else {
                                    byte unk1 = cp.Decode1();
                                    if (unk1 > 0) { //a password has been entered
                                        String pass = cp.DecodeStr();
                                        if (!pass.equals(ips.getPassword())) {
                                            chr.dropMessage(1, "The password you entered is incorrect.");
                                            return true;
                                        }
                                    } else if (ips.getPassword().length() > 0) {
                                        chr.dropMessage(1, "The password you entered is incorrect.");
                                        return true;
                                    }
                                    chr.setPlayerShop(ips);
                                    ips.addVisitor(chr);
                                    if (ips instanceof MapleMiniGame) {
                                        ((MapleMiniGame) ips).send(chr.getClient());
                                    } else {
                                        chr.SendPacket(ResCMiniRoomBaseDlg.getPlayerStore(chr, false));
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            }
            case MRP_Chat: {
                int unk = cp.Decode4();
                String message = cp.DecodeStr();
                if (chr.getTrade() != null) {
                    chr.getTrade().chat(message);
                    return true;
                }
                if (chr.getPlayerShop() != null) {
                    IMaplePlayerShop ips = chr.getPlayerShop();
                    ips.broadcastToVisitors(ResCMiniRoomBaseDlg.shopChat(chr.getName() + " : " + message, ips.getVisitorSlot(chr)));
                    return true;
                }
                DebugLogger.ErrorLog("OnMiniRoom : MRP_Chat, not  coded.");
                return true;
            }
            case MRP_GameMessage: {
                return true;
            }
            case MRP_UserChat: {
                return true;
            }
            case MRP_Avatar: {
                return true;
            }
            case MRP_Leave: {
                if (chr.getTrade() != null) {
                    MapleTrade.cancelTrade(chr.getTrade(), chr.getClient());
                    return true;
                }
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null) {
                    return true;
                }
                if (!ips.isAvailable() || (ips.isOwner(chr) && ips.getShopType() != 1)) {
                    ips.closeShop(false, ips.isAvailable(), 3);
                } else {
                    ips.removeVisitor(chr);
                }
                chr.setPlayerShop(null);
                return true;
            }
            case MRP_Balloon: {
                final IMaplePlayerShop shop = chr.getPlayerShop();
                if (shop != null && shop.isOwner(chr) && shop.getShopType() < 3) {
                    if (chr.getMap().allowPersonalShop()) {

                        if (shop.getShopType() == 1) {
                            final HiredMerchant merchant = (HiredMerchant) shop;
                            merchant.setStoreid(chr.getClient().getChannelServer().addMerchant(merchant));
                            merchant.setOpen(true);
                            merchant.setAvailable(true);
                            chr.getMap().broadcastMessage(ResCEmployeePool.EmployeeEnterField(merchant));
                            chr.setPlayerShop(null);

                        } else if (shop.getShopType() == 2) {
                            shop.setOpen(true);
                            shop.setAvailable(true);
                            shop.update();
                        }
                    }
                }
                return true;
            }
            case TRP_PutItem: {
                MapleTrade trade = chr.getTrade();
                if (trade == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : TRP_PutItem, trade");
                    return true;
                }

                byte item_type = cp.Decode1();
                short item_slot = cp.Decode2();
                short quantity = cp.Decode2();
                byte targetSlot = cp.Decode1();

                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                MapleInventoryType ivType = MapleInventoryType.getByType(item_type);
                IItem item = chr.getInventory(ivType).getItem(item_slot);

                if (item == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : TRP_PutItem, item");
                    return true;
                }

                if ((quantity <= item.getQuantity() && quantity >= 0) || GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                    chr.getTrade().setItems(chr.getClient(), item, targetSlot, quantity);
                }
                return true;
            }
            case TRP_PutMoney: {
                MapleTrade trade = chr.getTrade();
                if (trade == null) {
                    return true;
                }
                int mesos = cp.Decode4();
                trade.setMeso(mesos);
                return true;
            }
            case TRP_Trade: {
                if (chr.getTrade() == null) {
                    return true;
                }
                MapleTrade.completeTrade(chr);
                return true;
            }
            case PSP_PutItem:
            case ESP_PutItem: {
                byte inv_type = cp.Decode1();
                final MapleInventoryType type = MapleInventoryType.getByType(inv_type);
                final byte slot = (byte) cp.Decode2();
                final short bundles = cp.Decode2(); // How many in a bundle
                final short perBundle = cp.Decode2(); // Price per bundle
                final int price = cp.Decode4();

                if (price <= 0 || bundles <= 0 || perBundle <= 0) {
                    return true;
                }
                final IMaplePlayerShop shop = chr.getPlayerShop();

                if (shop == null || !shop.isOwner(chr) || shop instanceof MapleMiniGame) {
                    return true;
                }
                final IItem ivItem = chr.getInventory(type).getItem(slot);
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (ivItem != null) {
                    long check = bundles * perBundle;
                    if (check > 32767 || check <= 0) { //This is the better way to check.
                        return true;
                    }
                    final short bundles_perbundle = (short) (bundles * perBundle);
//                    if (bundles_perbundle < 0) { // int_16 overflow
//                        return;
//                    }
                    if (ivItem.getQuantity() >= bundles_perbundle) {
                        final byte flag = ivItem.getFlag();
                        if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                            chr.SendPacket(WrapCWvsContext.updateStat());
                            return true;
                        }
                        if (ii.isDropRestricted(ivItem.getItemId()) || ii.isAccountShared(ivItem.getItemId())) {
                            if (!(ItemFlag.KARMA_EQ.check(flag) || ItemFlag.KARMA_USE.check(flag))) {
                                chr.SendPacket(WrapCWvsContext.updateStat());
                                return true;
                            }
                        }
                        if (GameConstants.isThrowingStar(ivItem.getItemId()) || GameConstants.isBullet(ivItem.getItemId())) {
                            // Ignore the bundles
                            MapleInventoryManipulator.removeFromSlot(chr.getClient(), type, slot, ivItem.getQuantity(), true);

                            final IItem sellItem = ivItem.copy();
                            shop.addItem(new MaplePlayerShopItem(sellItem, (short) 1, price));
                        } else {
                            MapleInventoryManipulator.removeFromSlot(chr.getClient(), type, slot, bundles_perbundle, true);

                            final IItem sellItem = ivItem.copy();
                            sellItem.setQuantity(perBundle);
                            shop.addItem(new MaplePlayerShopItem(sellItem, bundles, price));
                        }
                        chr.SendPacket(ResCMiniRoomBaseDlg.shopItemUpdate(shop));
                    }
                }
                return true;
            }
            case PSP_BuyItem:
            case ESP_BuyItem:
            case ESP_BuyResult: {
                final int item = cp.Decode1();
                final short quantity = cp.Decode2();
                //slea.skip(4);
                final IMaplePlayerShop shop = chr.getPlayerShop();

                if (shop == null || shop.isOwner(chr) || shop instanceof MapleMiniGame) {
                    return true;
                }
                final MaplePlayerShopItem tobuy = shop.getItems().get(item);
                if (tobuy == null) {
                    return true;
                }
                long check = tobuy.bundles * quantity;
                long check2 = tobuy.price * quantity;
                long check3 = tobuy.item.getQuantity() * quantity;
                if (check > 32767 || check <= 0 || check2 > 2147483647 || check2 <= 0 || check3 > 32767 || check3 <= 0) { //This is the better way to check.
                    return true;
                }
                if (quantity <= 0 || tobuy.bundles < quantity || (tobuy.bundles % quantity != 0 && GameConstants.isEquip(tobuy.item.getItemId())) // Buying
                        || chr.getMeso() - (check2) < 0 || shop.getMeso() + (check2) < 0) {
                    return true;
                }
                shop.buy(chr.getClient(), item, quantity);
                shop.broadcastToVisitors(ResCMiniRoomBaseDlg.shopItemUpdate(shop));
                return true;
            }
            case PSP_MoveItemToInventory:
            case ESP_MoveItemToInventory: {
                int slot = cp.Decode2();
                final IMaplePlayerShop shop = chr.getPlayerShop();
                if (shop == null || !shop.isOwner(chr) || shop instanceof MapleMiniGame || shop.getItems().size() <= 0 || shop.getItems().size() <= slot || slot < 0) {
                    return true;
                }
                final MaplePlayerShopItem item = shop.getItems().get(slot);

                if (item != null) {
                    if (item.bundles > 0) {
                        IItem item_get = item.item.copy();
                        long check = item.bundles * item.item.getQuantity();
                        if (check <= 0 || check > 32767) {
                            return true;
                        }
                        item_get.setQuantity((short) check);
                        if (MapleInventoryManipulator.checkSpace(chr.getClient(), item_get.getItemId(), item_get.getQuantity(), item_get.getOwner())) {
                            MapleInventoryManipulator.addFromDrop(chr.getClient(), item_get, false);
                            item.bundles = 0;
                            shop.removeFromSlot(slot);
                        }
                    }
                }
                chr.SendPacket(ResCMiniRoomBaseDlg.shopItemUpdate(shop));
                return true;
            }
            case PSP_Ban: {
                // 営業許可証 追放
                byte visitor_slot = cp.Decode1();
                String visitor_name = cp.DecodeStr();
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null) {
                    for (Pair<Byte, MapleCharacter> visitors : ips.getVisitors()) {
                        if (visitors.getRight().getName().equals(visitor_name)) {
                            visitors.getRight().getClient().getSession().write(ResCMiniRoomBaseDlg.shopBlockPlayer(visitor_slot));
                            visitors.getRight().setPlayerShop(null);
                            ips.removeVisitor(visitors.getRight());
                            return true;
                        }
                    }
                }
                return true;
            }
            case ESP_GoOut: {
                // 雇用商人 "商店から出る" 間違ってるかも?
                // ?_?
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null) {
                    ips.setOpen(true);
                }
                return true;
            }
            case ESP_ArrangeItem: {
                final IMaplePlayerShop imps = chr.getPlayerShop();
                if (imps != null && imps.isOwner(chr) && !(imps instanceof MapleMiniGame)) {
                    for (int i = 0; i < imps.getItems().size(); i++) {
                        if (imps.getItems().get(i).bundles == 0) {
                            imps.getItems().remove(i);
                        }
                    }
                    if (chr.getMeso() + imps.getMeso() < 0) {
                        chr.SendPacket(ResCMiniRoomBaseDlg.shopItemUpdate(imps));
                    } else {
                        chr.gainMeso(imps.getMeso(), false);
                        imps.setMeso(0);
                        chr.SendPacket(ResCMiniRoomBaseDlg.shopItemUpdate(imps));
                    }
                }
                return true;
            }
            case ESP_WithdrawAll: {
                // 雇用商人 "商店とクローズ"
                // "イベントリに空きがないとアイテムはストアーバンクNPCのプレドリックのところで探すべきです。閉店しますか？" と聞かれてOKを押した場合の処理
                // ダイアログを出さずに閉じる処理が必要となる
                chr.SendPacket(ResCMiniRoomBaseDlg.CloseHiredMerchant());

                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (!ips.isAvailable() || ips.isOwner(chr)) {

                    // アイテム回収
                    for (MaplePlayerShopItem items : ips.getItems()) {
                        if (items.bundles > 0) {
                            IItem newItem = items.item.copy();
                            newItem.setQuantity((short) (items.bundles * newItem.getQuantity()));
                            if (MapleInventoryManipulator.addFromDrop(chr.getClient(), newItem, false)) {
                                items.bundles = 0;
                            }
                        }
                    }

                    ips.closeShop(false, true, 20);
                }

                chr.setPlayerShop(null);
                chr.setRemoteStore(null);
                return true;
            }
            case ESP_AdminChangeTitle: {
                // GMが右クリックした場合の雇用商人の名前を替えますか？でOKを押したときに送信されるデータ
                // @007F 2A [BC 7D 00 00 (ID)]
                return true;
            }
            case ESP_DeliverVisitList: {
                HiredMerchant merchant = chr.getMyHiredMerchant();
                if (merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_DeliverVisitList");
                    return true;
                }
                merchant.sendVisitor(chr);
                return true;
            }
            case ESP_DeliverBlackList: {
                HiredMerchant merchant = chr.getMyHiredMerchant();
                if (merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_DeliverBlackList");
                    return true;
                }
                merchant.sendBlackList(chr);
                return true;
            }
            case ESP_AddBlackList: {
                HiredMerchant merchant = chr.getMyHiredMerchant();
                if (merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_AddBlackList");
                    return true;
                }
                String character_name = cp.DecodeStr(); // sName
                merchant.addBlackList(character_name);
                return true;
            }
            case ESP_DeleteBlackList: {
                HiredMerchant merchant = chr.getMyHiredMerchant();
                if (merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_DeleteBlackList");
                    return true;
                }
                String character_name = cp.DecodeStr(); // sName
                merchant.removeBlackList(character_name);
                return true;
            }
            case MGRP_TieRequest: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        return true;
                    }
                    if (game.isOwner(chr)) {
                        game.broadcastToVisitors(ResCMiniRoomBaseDlg.getMiniGameRequestTie(), false);
                    } else {
                        game.getMCOwner().getClient().getSession().write(ResCMiniRoomBaseDlg.getMiniGameRequestTie());
                    }
                    game.setRequestedTie(game.getVisitorSlot(chr));
                }
                return true;
            }
            case MGRP_TieResult: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        return true;
                    }
                    if (game.getRequestedTie() > -1 && game.getRequestedTie() != game.getVisitorSlot(chr)) {
                        byte unk1 = cp.Decode1();
                        if (unk1 > 0) {
                            game.broadcastToVisitors(ResCMiniRoomBaseDlg.getMiniGameResult(game, 1, game.getRequestedTie()));
                            game.nextLoser();
                            game.setOpen(true);
                            game.update();
                            game.checkExitAfterGame();
                        } else {
                            game.broadcastToVisitors(ResCMiniRoomBaseDlg.getMiniGameDenyTie());
                        }
                        game.setRequestedTie(-1);
                    }
                }
                return true;
            }
            case MGRP_GiveUpRequest: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        return true;
                    }
                    game.broadcastToVisitors(ResCMiniRoomBaseDlg.getMiniGameResult(game, 0, game.getVisitorSlot(chr)));
                    game.nextLoser();
                    game.setOpen(true);
                    game.update();
                    game.checkExitAfterGame();
                }
                return true;
            }
            case MGRP_GiveUpResult: {
                return true;
            }
            case MGRP_RetreatRequest: {
                return true;
            }
            case MGRP_RetreatResult: {
                return true;
            }
            case MGRP_LeaveEngage:
            case MGRP_LeaveEngageCancel: {
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null || !(ips instanceof MapleMiniGame)) {
                    DebugLogger.ErrorLog("OnMiniRoom : MGRP_LeaveEngage, ips");
                    return true;
                }
                MapleMiniGame game = (MapleMiniGame) ips;
                if (game.isOpen()) {
                    DebugLogger.ErrorLog("OnMiniRoom : MGRP_LeaveEngage, game");
                    return true;
                }
                game.setExitAfter(chr);
                game.broadcastToVisitors(ResCMiniRoomBaseDlg.getMiniGameExitAfter(game.isExitAfter(chr)));
                return true;
            }
            case MGRP_Ready:
            case MGRP_CancelReady: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (!game.isOwner(chr) && game.isOpen()) {
                        game.setReady(game.getVisitorSlot(chr));
                        game.broadcastToVisitors(ResCMiniRoomBaseDlg.getMiniGameReady(game.isReady(game.getVisitorSlot(chr))));
                    }
                }
                return true;
            }
            case MGRP_Ban: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    if (!((MapleMiniGame) ips).isOpen()) {
                        return true;
                    }
                    // 5 = 強制退場されました。
                    ips.removeAllVisitors(5, 1);
                }
                return true;
            }
            case MGRP_Start: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOwner(chr) && game.isOpen()) {
                        for (int i = 1; i < ips.getSize(); i++) {
                            if (!game.isReady(i)) {
                                return true;
                            }
                        }
                        game.setGameType();
                        game.shuffleList();
                        if (game.getGameType() == 1) {
                            game.broadcastToVisitors(ResCMiniRoomBaseDlg.getMiniGameStart(game.getLoser()));
                        } else {
                            game.broadcastToVisitors(ResCMiniRoomBaseDlg.getMatchCardStart(game, game.getLoser()));
                        }
                        game.setOpen(false);
                        game.update();
                    }
                }
                return true;
            }
            case MGRP_GameResult: {
                return true;
            }
            case MGRP_TimeOver: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        return true;
                    }
                    ips.broadcastToVisitors(ResCMiniRoomBaseDlg.getMiniGameSkip(ips.getVisitorSlot(chr)));
                    game.nextLoser();
                }
                return true;
            }
            case ORP_PutStoneChecker: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        return true;
                    }
                    int unk1 = cp.Decode4();
                    int unk2 = cp.Decode4();
                    byte unk3 = cp.Decode1();
                    game.setPiece(unk1, unk2, unk3, chr);
                }
                return true;
            }
            case MGP_TurnUpCard: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    final int slot = cp.Decode1();
                    final int turn = game.getTurn();
                    final int fs = game.getFirstSlot();
                    if (turn == 1) {
                        game.setFirstSlot(slot);
                        if (game.isOwner(chr)) {
                            game.broadcastToVisitors(ResCMiniRoomBaseDlg.getMatchCardSelect(turn, slot, fs, turn), false);
                        } else {
                            game.getMCOwner().getClient().getSession().write(ResCMiniRoomBaseDlg.getMatchCardSelect(turn, slot, fs, turn));
                        }
                        game.setTurn(0); //2nd turn nao
                        return true;
                    } else if (fs > 0 && game.getCardId(fs + 1) == game.getCardId(slot + 1)) {
                        game.broadcastToVisitors(ResCMiniRoomBaseDlg.getMatchCardSelect(turn, slot, fs, game.isOwner(chr) ? 2 : 3));
                        game.setPoints(game.getVisitorSlot(chr)); //correct.. so still same loser. diff turn tho
                    } else {
                        game.broadcastToVisitors(ResCMiniRoomBaseDlg.getMatchCardSelect(turn, slot, fs, game.isOwner(chr) ? 0 : 1));
                        game.nextLoser();//wrong haha

                    }
                    game.setTurn(1);
                    game.setFirstSlot(0);
                }
                return true;
            }
            case MGP_MatchCard: {
                return true;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnMiniRoom : not coded = " + protocol_req);
        return false;
    }

    // 雇用商店遠隔管理機
    public static boolean RemoteStore(MapleCharacter chr, short item_slot) {
        Runnable item_use = chr.checkItemSlot(item_slot, 5470000);

        if (item_use == null) {
            DebugLogger.ErrorLog("RemoteStore : no item.");
            return false;
        }

        final HiredMerchant merchant = (HiredMerchant) chr.getRemoteStore();
        if (merchant == null) {
            // test
            //chr.SendPacket(ResCMiniRoomBaseDlg.EnterResultStaticTest(chr));

            HiredMerchant hm = new HiredMerchant(chr, 5030000, "DebugHiredMarchant");
            chr.getMap().addMapObject(hm);
            chr.SendPacket(ResCEmployeePool.EmployeeEnterField(hm));
            return false;
        }

        merchant.setOpen(false);

        // "商店の主人が物品整理中でございます。もうしばらく後でご利用ください。"
        List<Pair<Byte, MapleCharacter>> visitors = merchant.getVisitors();
        for (int i = 0; i < visitors.size(); i++) {
            visitors.get(i).getRight().getClient().getSession().write(ResCMiniRoomBaseDlg.MaintenanceHiredMerchant((byte) i + 1));
            visitors.get(i).getRight().setPlayerShop(null);
            merchant.removeVisitor(visitors.get(i).getRight());
        }

        chr.setPlayerShop(merchant);
        chr.SendPacket(ResCMiniRoomBaseDlg.getHiredMerch(chr, merchant, false));
        return true;
    }

}
