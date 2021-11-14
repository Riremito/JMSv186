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
package handling.channel.handler;

import java.util.Arrays;

import client.inventory.IItem;
import client.inventory.ItemFlag;
import constants.GameConstants;
import client.MapleClient;
import client.MapleCharacter;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleTrade;
import server.maps.FieldLimitType;
import server.shops.HiredMerchant;
import server.shops.IMaplePlayerShop;
import server.shops.MaplePlayerShop;
import server.shops.MaplePlayerShopItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.shops.MapleMiniGame;
import tools.MaplePacketCreator;
import tools.packet.PlayerShopPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class PlayerInteractionHandler {

    private static final byte CREATE = 0x00, // アバター交換, @007F 00 06 00 00 00 00 76 7D 00 00
            // 交換申込, アバター交換
            // @007F 02 76 7D 00 00
            INVITE_TRADE = 0x02,
            DENY_TRADE = 0x03,
            VISIT = 0x04,
            CHAT = 0x06,
            EXIT = 0x0A,
            OPEN = 0x0B,
            SET_ITEMS = 0x0D,
            SET_MESO = 0x0E,
            CONFIRM_TRADE = 0x0F,
            PLAYER_SHOP_ADD_ITEM = 0x13,
            BUY_ITEM_PLAYER_SHOP = 0x14,
            MERCHANT_EXIT = 0x1B,
            ADD_ITEM = 0x1E,
            BUY_ITEM_STORE = 0x20,
            BUY_ITEM_HIREDMERCHANT = 0x1F,
            REMOVE_ITEM = 0x23, // 0x24
            PLAYER_SHOP_REMOVE_ITEM = 0x19,
            HIREDMERCHANT_EXIT = 0x24,
            HIREDMERCHANT_ORGANISE = 0x25,
            HIREDMERCHANT_ORGANISE_CLOSE = 0x26,
            ADMIN_STORE_NAMECHANGE = 0x2A,
            VIEW_MERCHANT_VISITOR = 0x2B, // 0x2C
            VIEW_MERCHANT_BLACKLIST = 0x2C, // 0x2D
            MERCHANT_BLACKLIST_ADD = 0x2D, // 0x2E
            MERCHANT_BLACKLIST_REMOVE = 0x2E, // 0x2F
            REQUEST_TIE = 0x2F,
            ANSWER_TIE = 0x30,
            GIVE_UP = 0x31,
            EXIT_AFTER_GAME = 0x35,
            CANCEL_EXIT = 0x36,
            READY = 0x37,
            UN_READY = 0x38,
            EXPEL = 0x39,
            START = 0x3A,
            // たぶん
            SKIP = 0x3C,
            MOVE_OMOK = 0x3D,
            SELECT_CARD = 0x41;

    public static final void PlayerInteraction(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        //System.out.println(slea.toString());
        if (chr == null) {
            return;
        }
        final byte action = slea.readByte();
        switch (action) { // Mode
            case CREATE: {
                final byte createType = slea.readByte();
                if (createType == 3) { // trade
                    MapleTrade.startTrade(chr);
                } else if (createType == 1 || createType == 2 || createType == 4 || createType == 5) { // shop
                    /*
                    if (createType == 4 && !chr.isAdmin()) { //not hired merch... blocked playershop
                        c.getSession().write(MaplePacketCreator.enableActions());
                        return;
                    }
                     */
                    if (chr.getMap().getMapObjectsInRange(chr.getPosition(), 20000, Arrays.asList(MapleMapObjectType.SHOP, MapleMapObjectType.HIRED_MERCHANT)).size() != 0) {
                        chr.dropMessage(1, "You may not establish a store here.");
                        c.getSession().write(MaplePacketCreator.enableActions());
                        return;
                    } else if (createType == 1 || createType == 2) {
                        if (FieldLimitType.Minigames.check(chr.getMap().getFieldLimit())) {
                            chr.dropMessage(1, "You may not use minigames here.");
                            c.getSession().write(MaplePacketCreator.enableActions());
                            return;
                        }
                    }
                    final String desc = slea.readMapleAsciiString();
                    String pass = "";
                    if (slea.readByte() > 0 && (createType == 1 || createType == 2)) {
                        pass = slea.readMapleAsciiString();
                    }
                    // たぶんなんかがおかしい
                    // ごもく @007F 00 02 04 00 63 61 72 64 00 02
                    // 神経衰弱 @007F 00 02 01 00 66 00 [01] 最後の1バイトがサイズ
                    if (createType == 1 || createType == 2) {
                        final int piece = slea.readByte();
                        final int itemId = createType == 1 ? (4080000 + piece) : 4080100;
                        if (!chr.haveItem(itemId) || (c.getPlayer().getMapId() >= 910000001 && c.getPlayer().getMapId() <= 910000022)) {
                            return;
                        }
                        MapleMiniGame game = new MapleMiniGame(chr, itemId, desc, pass, createType); //itemid
                        game.setPieceType(piece);
                        chr.setPlayerShop(game);
                        game.setAvailable(true);
                        game.setOpen(true);
                        game.send(c);
                        chr.getMap().addMapObject(game);
                        game.update();
                    } else {
                        IItem shop = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) slea.readShort());
                        if (shop == null || shop.getQuantity() <= 0 || shop.getItemId() != slea.readInt() || c.getPlayer().getMapId() < 910000001 || c.getPlayer().getMapId() > 910000022) {
                            return;
                        }
                        if (createType == 4) {
                            MaplePlayerShop mps = new MaplePlayerShop(chr, shop.getItemId(), desc);
                            chr.setPlayerShop(mps);
                            chr.getMap().addMapObject(mps);
                            c.getSession().write(PlayerShopPacket.getPlayerStore(chr, true));
                        } else {
                            final HiredMerchant merch = new HiredMerchant(chr, shop.getItemId(), desc);
                            chr.setPlayerShop(merch);
                            chr.getMap().addMapObject(merch);
                            c.getSession().write(PlayerShopPacket.getHiredMerch(chr, merch, true));
                        }
                    }
                }
                break;
            }
            case INVITE_TRADE: {
                MapleTrade.inviteTrade(chr, chr.getMap().getCharacterById(slea.readInt()));
                break;
            }
            case DENY_TRADE: {
                MapleTrade.declineTrade(chr);
                break;
            }
            case VISIT: {
                if (chr.getTrade() != null && chr.getTrade().getPartner() != null) {
                    MapleTrade.visitTrade(chr, chr.getTrade().getPartner().getChr());
                } else if (chr.getMap() != null) {
                    final int obid = slea.readInt();
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
                                merchant.removeAllVisitors((byte) 16, (byte) 0);
                                chr.setPlayerShop(ips);
                                c.getSession().write(PlayerShopPacket.getHiredMerch(chr, merchant, false));
                            } else {
                                if (!merchant.isOpen() || !merchant.isAvailable()) {
                                    chr.dropMessage(1, "This shop is in maintenance, please come by later.");
                                } else {
                                    if (ips.getFreeSlot() == -1) {
                                        chr.dropMessage(1, "This shop has reached it's maximum capacity, please come by later.");
                                    } else if (merchant.isInBlackList(chr.getName())) {
                                        chr.dropMessage(1, "You have been banned from this store.");
                                    } else {
                                        chr.setPlayerShop(ips);
                                        merchant.addVisitor(chr);
                                        c.getSession().write(PlayerShopPacket.getHiredMerch(chr, merchant, false));
                                    }
                                }
                            }
                        } else {
                            if (ips instanceof MaplePlayerShop && ((MaplePlayerShop) ips).isBanned(chr.getName())) {
                                chr.dropMessage(1, "You have been banned from this store.");
                                return;
                            } else {
                                if (ips.getFreeSlot() < 0 || ips.getVisitorSlot(chr) > -1 || !ips.isOpen() || !ips.isAvailable()) {
                                    c.getSession().write(PlayerShopPacket.getMiniGameFull());
                                } else {
                                    if (slea.available() > 0 && slea.readByte() > 0) { //a password has been entered
                                        String pass = slea.readMapleAsciiString();
                                        if (!pass.equals(ips.getPassword())) {
                                            c.getPlayer().dropMessage(1, "The password you entered is incorrect.");
                                            return;
                                        }
                                    } else if (ips.getPassword().length() > 0) {
                                        c.getPlayer().dropMessage(1, "The password you entered is incorrect.");
                                        return;
                                    }
                                    chr.setPlayerShop(ips);
                                    ips.addVisitor(chr);
                                    if (ips instanceof MapleMiniGame) {
                                        ((MapleMiniGame) ips).send(c);
                                    } else {
                                        c.getSession().write(PlayerShopPacket.getPlayerStore(chr, false));
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            case CHAT: {
                slea.readInt();
                if (chr.getTrade() != null) {
                    chr.getTrade().chat(slea.readMapleAsciiString());
                } else if (chr.getPlayerShop() != null) {
                    final IMaplePlayerShop ips = chr.getPlayerShop();
                    ips.broadcastToVisitors(PlayerShopPacket.shopChat(chr.getName() + " : " + slea.readMapleAsciiString(), ips.getVisitorSlot(chr)));
                }
                break;
            }
            case EXIT: {
                if (chr.getTrade() != null) {
                    MapleTrade.cancelTrade(chr.getTrade(), chr.getClient());
                } else {
                    final IMaplePlayerShop ips = chr.getPlayerShop();
                    if (ips == null) {
                        return;
                    }
                    if (!ips.isAvailable() || (ips.isOwner(chr) && ips.getShopType() != 1)) {
                        ips.closeShop(false, ips.isAvailable(), 3);
                    } else {
                        ips.removeVisitor(chr);
                    }
                    chr.setPlayerShop(null);
                }
                break;
            }
            case OPEN: {
                // c.getPlayer().haveItem(mode, 1, false, true)

                final IMaplePlayerShop shop = chr.getPlayerShop();
                if (shop != null && shop.isOwner(chr) && shop.getShopType() < 3) {
                    if (chr.getMap().allowPersonalShop()) {

                        if (shop.getShopType() == 1) {
                            final HiredMerchant merchant = (HiredMerchant) shop;
                            merchant.setStoreid(c.getChannelServer().addMerchant(merchant));
                            merchant.setOpen(true);
                            merchant.setAvailable(true);
                            chr.getMap().broadcastMessage(PlayerShopPacket.spawnHiredMerchant(merchant));
                            chr.setPlayerShop(null);

                        } else if (shop.getShopType() == 2) {
                            shop.setOpen(true);
                            shop.setAvailable(true);
                            shop.update();
                        }
                    }
                }

                break;
            }
            case SET_ITEMS: {
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final MapleInventoryType ivType = MapleInventoryType.getByType(slea.readByte());
                final IItem item = chr.getInventory(ivType).getItem((byte) slea.readShort());
                final short quantity = slea.readShort();
                final byte targetSlot = slea.readByte();

                if (chr.getTrade() != null && item != null) {
                    if ((quantity <= item.getQuantity() && quantity >= 0) || GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                        chr.getTrade().setItems(c, item, targetSlot, quantity);
                    }
                }
                break;
            }
            case SET_MESO: {
                final MapleTrade trade = chr.getTrade();
                if (trade != null) {
                    trade.setMeso(slea.readInt());
                }
                break;
            }
            case CONFIRM_TRADE: {
                if (chr.getTrade() != null) {
                    MapleTrade.completeTrade(chr);
                }
                break;
            }
            case MERCHANT_EXIT: {
                IMaplePlayerShop shop = chr.getPlayerShop();
                shop.setOpen(true);
                break;
            }
            case PLAYER_SHOP_ADD_ITEM:
            case ADD_ITEM: {
                final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
                final byte slot = (byte) slea.readShort();
                final short bundles = slea.readShort(); // How many in a bundle
                final short perBundle = slea.readShort(); // Price per bundle
                final int price = slea.readInt();

                if (price <= 0 || bundles <= 0 || perBundle <= 0) {
                    return;
                }
                final IMaplePlayerShop shop = chr.getPlayerShop();

                if (shop == null || !shop.isOwner(chr) || shop instanceof MapleMiniGame) {
                    return;
                }
                final IItem ivItem = chr.getInventory(type).getItem(slot);
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (ivItem != null) {
                    long check = bundles * perBundle;
                    if (check > 32767 || check <= 0) { //This is the better way to check.
                        return;
                    }
                    final short bundles_perbundle = (short) (bundles * perBundle);
//                    if (bundles_perbundle < 0) { // int_16 overflow
//                        return;
//                    }
                    if (ivItem.getQuantity() >= bundles_perbundle) {
                        final byte flag = ivItem.getFlag();
                        if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                            c.getSession().write(MaplePacketCreator.enableActions());
                            return;
                        }
                        if (ii.isDropRestricted(ivItem.getItemId()) || ii.isAccountShared(ivItem.getItemId())) {
                            if (!(ItemFlag.KARMA_EQ.check(flag) || ItemFlag.KARMA_USE.check(flag))) {
                                c.getSession().write(MaplePacketCreator.enableActions());
                                return;
                            }
                        }
                        if (bundles_perbundle >= 50 && GameConstants.isUpgradeScroll(ivItem.getItemId())) {
                            c.setMonitored(true); //hack check
                        }
                        if (GameConstants.isThrowingStar(ivItem.getItemId()) || GameConstants.isBullet(ivItem.getItemId())) {
                            // Ignore the bundles
                            MapleInventoryManipulator.removeFromSlot(c, type, slot, ivItem.getQuantity(), true);

                            final IItem sellItem = ivItem.copy();
                            shop.addItem(new MaplePlayerShopItem(sellItem, (short) 1, price));
                        } else {
                            MapleInventoryManipulator.removeFromSlot(c, type, slot, bundles_perbundle, true);

                            final IItem sellItem = ivItem.copy();
                            sellItem.setQuantity(perBundle);
                            shop.addItem(new MaplePlayerShopItem(sellItem, bundles, price));
                        }
                        c.getSession().write(PlayerShopPacket.shopItemUpdate(shop));
                    }
                }
                break;
            }
            case BUY_ITEM_PLAYER_SHOP:
            case BUY_ITEM_STORE:
            case BUY_ITEM_HIREDMERCHANT: { // Buy and Merchant buy
                final int item = slea.readByte();
                final short quantity = slea.readShort();
                //slea.skip(4);
                final IMaplePlayerShop shop = chr.getPlayerShop();

                if (shop == null || shop.isOwner(chr) || shop instanceof MapleMiniGame) {
                    return;
                }
                final MaplePlayerShopItem tobuy = shop.getItems().get(item);
                if (tobuy == null) {
                    return;
                }
                long check = tobuy.bundles * quantity;
                long check2 = tobuy.price * quantity;
                long check3 = tobuy.item.getQuantity() * quantity;
                if (check > 32767 || check <= 0 || check2 > 2147483647 || check2 <= 0 || check3 > 32767 || check3 <= 0) { //This is the better way to check.
                    return;
                }
                if (quantity <= 0 || tobuy.bundles < quantity || (tobuy.bundles % quantity != 0 && GameConstants.isEquip(tobuy.item.getItemId())) // Buying
                        || chr.getMeso() - (check2) < 0 || shop.getMeso() + (check2) < 0) {
                    return;
                }
                if (quantity >= 50 && GameConstants.isUpgradeScroll(tobuy.item.getItemId())) {
                    c.setMonitored(true); //hack check
                }
                shop.buy(c, item, quantity);
                shop.broadcastToVisitors(PlayerShopPacket.shopItemUpdate(shop));
                break;
            }
            case PLAYER_SHOP_REMOVE_ITEM:
            case REMOVE_ITEM: {
                int slot = slea.readShort(); //0
                final IMaplePlayerShop shop = chr.getPlayerShop();

                if (shop == null || !shop.isOwner(chr) || shop instanceof MapleMiniGame || shop.getItems().size() <= 0 || shop.getItems().size() <= slot || slot < 0) {
                    return;
                }
                final MaplePlayerShopItem item = shop.getItems().get(slot);

                if (item != null) {
                    if (item.bundles > 0) {
                        IItem item_get = item.item.copy();
                        long check = item.bundles * item.item.getQuantity();
                        if (check <= 0 || check > 32767) {
                            return;
                        }
                        item_get.setQuantity((short) check);
                        if (item_get.getQuantity() >= 50 && GameConstants.isUpgradeScroll(item.item.getItemId())) {
                            c.setMonitored(true); //hack check
                        }
                        if (MapleInventoryManipulator.checkSpace(c, item_get.getItemId(), item_get.getQuantity(), item_get.getOwner())) {
                            MapleInventoryManipulator.addFromDrop(c, item_get, false);
                            item.bundles = 0;
                            shop.removeFromSlot(slot);
                        }
                    }
                }
                c.getSession().write(PlayerShopPacket.shopItemUpdate(shop));
                break;
            }
            case HIREDMERCHANT_EXIT: {
                /*
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                    merchant.closeShop(true, true, 3);
                    chr.setPlayerShop(null);
                }
                 */
                break;
            }
            case HIREDMERCHANT_ORGANISE: {
                final IMaplePlayerShop imps = chr.getPlayerShop();
                if (imps != null && imps.isOwner(chr) && !(imps instanceof MapleMiniGame)) {
                    for (int i = 0; i < imps.getItems().size(); i++) {
                        if (imps.getItems().get(i).bundles == 0) {
                            imps.getItems().remove(i);
                        }
                    }
                    if (chr.getMeso() + imps.getMeso() < 0) {
                        c.getSession().write(PlayerShopPacket.shopItemUpdate(imps));
                    } else {
                        chr.gainMeso(imps.getMeso(), false);
                        imps.setMeso(0);
                        c.getSession().write(PlayerShopPacket.shopItemUpdate(imps));
                    }
                }
                break;
            }
            case HIREDMERCHANT_ORGANISE_CLOSE: {

                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null) {
                    return;
                }
                if (!ips.isAvailable() || ips.isOwner(chr)) {
                    ips.closeShop(false, ips.isAvailable(), 20);
                }
                chr.setPlayerShop(null);
                /*
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null) {
                    if (merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                        c.getSession().write(MaplePacketCreator.serverNotice(1, "プレドリックからアイテムを回収してください。"));
                        c.getSession().write(MaplePacketCreator.enableActions());
                        merchant.closeShop(true, true, 20);
                        chr.setPlayerShop(null);
                    } else {
                        merchant.closeShop(true, true, 20);
                    }
                }
                 */
                break;
            }
            //case TRADE_SOMETHING:
            // GMが右クリックした場合の雇用商人の名前を替えますか？でOKを押したときに送信されるデータ
            case ADMIN_STORE_NAMECHANGE: {
                // @007F 2A [BC 7D 00 00 (ID)]
                break;
            }
            case VIEW_MERCHANT_VISITOR: {
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                    ((HiredMerchant) merchant).sendVisitor(c);
                }
                break;
            }
            case VIEW_MERCHANT_BLACKLIST: {
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                    ((HiredMerchant) merchant).sendBlackList(c);
                }
                break;
            }
            case MERCHANT_BLACKLIST_ADD: {
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                    ((HiredMerchant) merchant).addBlackList(slea.readMapleAsciiString());
                }
                break;
            }
            case MERCHANT_BLACKLIST_REMOVE: {
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                    ((HiredMerchant) merchant).removeBlackList(slea.readMapleAsciiString());
                }
                break;
            }
            case GIVE_UP: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    game.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(game, 0, game.getVisitorSlot(chr)));
                    game.nextLoser();
                    game.setOpen(true);
                    game.update();
                    game.checkExitAfterGame();
                }
                break;
            }
            // 追放
            case EXPEL: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    if (!((MapleMiniGame) ips).isOpen()) {
                        break;
                    }
                    // 5 = 強制退場されました。
                    ips.removeAllVisitors(5, 1);
                }
                break;
            }
            case READY:
            case UN_READY: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (!game.isOwner(chr) && game.isOpen()) {
                        game.setReady(game.getVisitorSlot(chr));
                        game.broadcastToVisitors(PlayerShopPacket.getMiniGameReady(game.isReady(game.getVisitorSlot(chr))));
                    }
                }
                break;
            }
            case START: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOwner(chr) && game.isOpen()) {
                        for (int i = 1; i < ips.getSize(); i++) {
                            if (!game.isReady(i)) {
                                return;
                            }
                        }
                        game.setGameType();
                        game.shuffleList();
                        if (game.getGameType() == 1) {
                            game.broadcastToVisitors(PlayerShopPacket.getMiniGameStart(game.getLoser()));
                        } else {
                            game.broadcastToVisitors(PlayerShopPacket.getMatchCardStart(game, game.getLoser()));
                        }
                        game.setOpen(false);
                        game.update();
                    }
                }
                break;
            }
            case REQUEST_TIE: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    if (game.isOwner(chr)) {
                        game.broadcastToVisitors(PlayerShopPacket.getMiniGameRequestTie(), false);
                    } else {
                        game.getMCOwner().getClient().getSession().write(PlayerShopPacket.getMiniGameRequestTie());
                    }
                    game.setRequestedTie(game.getVisitorSlot(chr));
                }
                break;
            }
            case ANSWER_TIE: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    if (game.getRequestedTie() > -1 && game.getRequestedTie() != game.getVisitorSlot(chr)) {
                        if (slea.readByte() > 0) {
                            game.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(game, 1, game.getRequestedTie()));
                            game.nextLoser();
                            game.setOpen(true);
                            game.update();
                            game.checkExitAfterGame();
                        } else {
                            game.broadcastToVisitors(PlayerShopPacket.getMiniGameDenyTie());
                        }
                        game.setRequestedTie(-1);
                    }
                }
                break;
            }
            case SKIP: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    ips.broadcastToVisitors(PlayerShopPacket.getMiniGameSkip(ips.getVisitorSlot(chr)));
                    game.nextLoser();
                }
                break;
            }
            case MOVE_OMOK: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    game.setPiece(slea.readInt(), slea.readInt(), slea.readByte(), chr);
                }
                break;
            }
            case SELECT_CARD: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    final int slot = slea.readByte();
                    final int turn = game.getTurn();
                    final int fs = game.getFirstSlot();
                    if (turn == 1) {
                        game.setFirstSlot(slot);
                        if (game.isOwner(chr)) {
                            game.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, fs, turn), false);
                        } else {
                            game.getMCOwner().getClient().getSession().write(PlayerShopPacket.getMatchCardSelect(turn, slot, fs, turn));
                        }
                        game.setTurn(0); //2nd turn nao
                        return;
                    } else if (fs > 0 && game.getCardId(fs + 1) == game.getCardId(slot + 1)) {
                        game.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, fs, game.isOwner(chr) ? 2 : 3));
                        game.setPoints(game.getVisitorSlot(chr)); //correct.. so still same loser. diff turn tho
                    } else {
                        game.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, fs, game.isOwner(chr) ? 0 : 1));
                        game.nextLoser();//wrong haha

                    }
                    game.setTurn(1);
                    game.setFirstSlot(0);

                }
                break;
            }
            case EXIT_AFTER_GAME:
            case CANCEL_EXIT: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    game.setExitAfter(chr);
                    game.broadcastToVisitors(PlayerShopPacket.getMiniGameExitAfter(game.isExitAfter(chr)));
                }
                break;
            }
            default: {
                //some idiots try to send huge amounts of data to this (:
                //System.out.println("Unhandled interaction action by " + chr.getName() + " : " + action + ", " + slea.toString());
                //19 (0x13) - 00 OR 01 -> itemid(maple leaf) ? who knows what this is
                break;
            }
        }
    }
}
