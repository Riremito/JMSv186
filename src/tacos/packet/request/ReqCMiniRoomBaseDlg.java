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

import tacos.debug.DebugLogger;
import tacos.packet.ops.OpsESWithdrawAll;
import java.util.ArrayList;
import tacos.packet.ops.OpsMREnterResult;
import tacos.client.TacosStoreBank;
import odin.server.shops.IPlayerShop;
import tacos.packet.ops.OpsMRLeave;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import java.util.List;
import odin.client.inventory.Item;
import odin.client.inventory.Item.ItemFlag;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleTrade;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsMiniRoomProtocol;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import odin.server.shops.MapleMiniGame;
import odin.server.shops.MaplePlayerShop;
import odin.server.shops.MaplePlayerShop.MaplePlayerShopItem;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.packet.ops.OpsMiniRoomType;
import tacos.packet.response.ResCUser;
import tacos.server.TacosRoom;
import tacos.server.TacosWorld;
import tacos.server.map.object.TacosMerchant;

/**
 *
 * @author Riremito
 */
public class ReqCMiniRoomBaseDlg {

    // CMiniRoomBaseDlg::OnPacketBase
    public static boolean OnMiniRoom(TacosCharacter chr, TacosMap map, ClientPacket cp) {
        byte protocol_req = cp.Decode1();

        TacosWorld world = chr.getWorld();
        switch (OpsMiniRoomProtocol.find(protocol_req)) {
            case MRP_Create: {
                byte miniroom_type = cp.Decode1();

                switch (OpsMiniRoomType.find(miniroom_type)) {
                    case MR_OmokRoom:
                    case MR_MemoryGameRoom: {
                        String desc = cp.DecodeStr();
                        String pass = "";
                        byte unk2 = cp.Decode1();
                        if (unk2 > 0) {
                            pass = cp.DecodeStr();
                        }
                        int piece = cp.Decode1();
                        int itemId = miniroom_type == OpsMiniRoomType.MR_OmokRoom.get() ? (4080000 + piece) : 4080100;
                        if (!chr.haveItem(itemId) || (chr.getMapId() >= 910000001 && chr.getMapId() <= 910000022)) {
                            return true;
                        }
                        MapleMiniGame game = new MapleMiniGame(chr, itemId, desc, pass, miniroom_type); //itemid
                        game.setPieceType(piece);
                        chr.setPlayerShop(game);
                        game.setAvailable(true);
                        game.setOpen(true);
                        game.send(chr);
                        map.addMiniGame(game);
                        map.splitSendPacket(chr, ResCUser.UserMiniRoomBalloon(chr));
                        return true;
                    }
                    case MR_TradingRoom: {
                        MapleTrade.startTrade(chr, false);
                        return true;
                    }
                    case MR_PersonalShop: {
                        String desc = cp.DecodeStr();
                        byte unk2 = cp.Decode1();
                        short item_slot = cp.Decode2();
                        int item_id = cp.Decode4();
                        Item shop = chr.getInventory(MapleInventoryType.CASH).getItem(item_slot);

                        if (shop == null || shop.getQuantity() <= 0 || shop.getItemId() != item_id || chr.getMapId() < 910000001 || chr.getMapId() > 910000022) {
                            return true;
                        }

                        // items left from a trade or a personal shop that did not fit in the inventory.
                        if (!chr.returnMiniRoomInventory()) {
                            return true;
                        }

                        MaplePlayerShop mps = new MaplePlayerShop(chr, shop.getItemId(), desc);
                        chr.setPlayerShop(mps);
                        map.addPlayerShop(mps);
                        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_PersonalShop(chr));
                        return true;
                    }
                    case MR_EntrustedShop: {
                        String desc = cp.DecodeStr();
                        byte unk2 = cp.Decode1();
                        short item_slot = cp.Decode2();
                        int item_id = cp.Decode4();
                        Item shop = chr.getInventory(MapleInventoryType.CASH).getItem(item_slot);

                        if (shop == null || shop.getQuantity() <= 0 || shop.getItemId() != item_id || chr.getMapId() < 910000001 || chr.getMapId() > 910000022) {
                            return true;
                        }

                        // one shop per character, and the store bank has to be empty.
                        if (TacosStoreBank.findMerchant(chr) != null || !TacosStoreBank.isEmpty(chr)) {
                            return true;
                        }

                        TacosMerchant merch = new TacosMerchant(chr, shop.getItemId(), desc);
                        chr.setPlayerShop(merch);
                        chr.setRemoteStore(merch);
                        // the employee appears on the map when the start button is pressed (MRP_Balloon).
                        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_EntrustedShop(chr, merch, true));
                        return true;
                    }
                    case MR_CashTradingRoom: {
                        // 未使用の可能性あり
                        int unk1 = cp.Decode4();
                        // 交換窓のID
                        int tradeid = cp.Decode4();
                        MapleTrade.startTrade(chr, true);
                        return true;
                    }
                    default: {
                        break;
                    }
                }
                return true;
            }
            case MRP_Invite: {
                int character_id = cp.Decode4();
                MapleTrade.inviteTrade(chr, map.getPlayerById(character_id));
                return true;
            }
            case MRP_InviteResult: {
                MapleTrade.declineTrade(chr);
                return true;
            }
            case MRP_Enter: {
                int object_id = cp.Decode4();
                // 雇用商人
                TacosMerchant merchant = map.getMerchantByOid(object_id);
                if (merchant != null) {
                    if (chr.getPlayerShop() != null || chr.getTrade() != null) {
                        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_Failed(OpsMREnterResult.MREnterResult_Busy));
                        return true;
                    }
                    OpsMREnterResult result = merchant.enter(chr);
                    if (result != OpsMREnterResult.MREnterResult_Success) {
                        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_Failed(result));
                        return true;
                    }
                    TacosRoom room = world.getRoom(merchant.getRoomId());
                    if (room != null) {
                        room.addPlayer(chr);
                    }
                    return true;
                }
                // 交換
                if (chr.getTrade() != null && chr.getTrade().getPartner() != null) {
                    MapleTrade.visitTrade(chr, chr.getTrade().getPartner().getChr(), chr.getTrade().getPartner().isPointTrade());
                    return true;
                }
                // 個人商店
                MaplePlayerShop player_shop = map.getPlayerShopByOid(object_id);
                if (player_shop != null) {
                    byte is_private = cp.Decode1();
                    if (is_private != 0) {
                        String password = cp.DecodeStr(); // personal shops have no password
                    }
                    if (chr.getPlayerShop() != null || chr.getTrade() != null || player_shop.isOwner(chr)) {
                        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_Failed(OpsMREnterResult.MREnterResult_Busy));
                        return true;
                    }
                    OpsMREnterResult result = player_shop.enter(chr);
                    if (result != OpsMREnterResult.MREnterResult_Success) {
                        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_Failed(result));
                    }
                    return true;
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
                    IPlayerShop ips = chr.getPlayerShop();
                    ips.broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_MRP_Chat(ips.getVisitorSlot(chr), chr.getName() + " : " + message));
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
                TacosRoom room = world.findRoom(chr);
                if (room != null) {
                    room.removePlayer(chr);
                }
                if (chr.getTrade() != null) {
                    MapleTrade.cancelTrade(chr.getTrade(), chr.getClient());
                    return true;
                }
                IPlayerShop ips = chr.getPlayerShop();
                if (ips == null) {
                    return true;
                }
                // hired merchant, the owner managing it from the shop or the remote controller, or a visitor.
                if (ips instanceof TacosMerchant) {
                    ((TacosMerchant) ips).leave(chr);
                    return true;
                }
                // personal shop, the owner closes it and gets the items back.
                if (ips instanceof MaplePlayerShop) {
                    if (ips.isOwner(chr)) {
                        ips.closeShop(false, true, OpsMRLeave.MRLeave_HostOut.get());
                    } else {
                        ((MaplePlayerShop) ips).leave(chr);
                    }
                    return true;
                }
                return true;
            }
            case MRP_Balloon: {
                IPlayerShop shop = chr.getPlayerShop();
                if (shop != null && shop.isOwner(chr) && shop.getShopType() < 3) {
                    if (map.allowPersonalShop()) {

                        if (shop.getShopType() == 1) {
                            // CEntrustedShopDlg::OnCorrectSSN2, the start button is pressed once, the dialog is closed with MRP_Leave after this.
                            TacosMerchant merchant = (TacosMerchant) shop;
                            if (!merchant.isOnMap()) {
                                merchant.open(chr);
                            }
                        } else if (shop.getShopType() == 2) {
                            shop.setOpen(true);
                            shop.setAvailable(true);
                            map.splitSendPacket(chr, ResCUser.UserMiniRoomBalloon(chr));
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
                Item item = chr.getInventory(ivType).getItem(item_slot);

                if (item == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : TRP_PutItem, item");
                    return true;
                }

                if ((quantity <= item.getQuantity() && quantity >= 0) || GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                    chr.getTrade().setItems(chr, item, targetSlot, quantity);
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
                MapleInventoryType type = MapleInventoryType.getByType(inv_type);
                byte slot = (byte) cp.Decode2();
                short bundles = cp.Decode2(); // How many in a bundle
                short perBundle = cp.Decode2(); // Price per bundle
                int price = cp.Decode4();

                if (price <= 0 || bundles <= 0 || perBundle <= 0) {
                    return true;
                }
                IPlayerShop shop = chr.getPlayerShop();

                if (shop == null || !shop.isOwner(chr) || shop instanceof MapleMiniGame) {
                    return true;
                }
                Item ivItem = chr.getInventory(type).getItem(slot);
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (ivItem != null) {
                    long check = bundles * perBundle;
                    if (check > 32767 || check <= 0) { //This is the better way to check.
                        return true;
                    }
                    short bundles_perbundle = (short) (bundles * perBundle);
//                    if (bundles_perbundle < 0) { // int_16 overflow
//                        return;
//                    }
                    if (ivItem.getQuantity() >= bundles_perbundle) {
                        byte flag = ivItem.getFlag();
                        if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                            chr.updateStat();
                            return true;
                        }
                        if (ii.isDropRestricted(ivItem.getItemId()) || ii.isAccountShared(ivItem.getItemId())) {
                            if (!(ItemFlag.KARMA_EQ.check(flag) || ItemFlag.KARMA_USE.check(flag))) {
                                chr.updateStat();
                                return true;
                            }
                        }
                        if (GameConstants.isThrowingStar(ivItem.getItemId()) || GameConstants.isBullet(ivItem.getItemId())) {
                            // Ignore the bundles
                            MapleInventoryManipulator.removeFromSlot(chr, type, slot, ivItem.getQuantity(), true);

                            Item sellItem = ivItem.copy();
                            shop.addItem(new MaplePlayerShopItem(sellItem, (short) 1, price));
                        } else {
                            MapleInventoryManipulator.removeFromSlot(chr, type, slot, bundles_perbundle, true);

                            Item sellItem = ivItem.copy();
                            sellItem.setQuantity(perBundle);
                            shop.addItem(new MaplePlayerShopItem(sellItem, bundles, price));
                        }
                        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_PSP_Refresh(shop));
                    }
                }
                return true;
            }
            case PSP_BuyItem:
            case ESP_BuyItem:
            case ESP_BuyResult: {
                int item = cp.Decode1();
                short quantity = cp.Decode2();
                //slea.skip(4);
                IPlayerShop shop = chr.getPlayerShop();

                if (shop == null || shop.isOwner(chr) || shop instanceof MapleMiniGame) {
                    return true;
                }
                if (item < 0 || shop.getItems().size() <= item) {
                    chr.updateStat();
                    return true;
                }
                MaplePlayerShopItem tobuy = shop.getItems().get(item);
                long total_quantity = (long) tobuy.item.getQuantity() * quantity;
                long price = (long) tobuy.price * quantity;
                if (quantity <= 0 || tobuy.bundles < quantity || 32767 < total_quantity || Integer.MAX_VALUE < price) {
                    chr.updateStat();
                    return true;
                }
                // the personal shop sends its own buy result.
                if (shop instanceof TacosMerchant && (chr.getMeso() < price || shop.getMeso() + price > Integer.MAX_VALUE)) {
                    chr.updateStat();
                    return true;
                }
                shop.buy(chr, item, quantity);
                shop.broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_PSP_Refresh(shop));
                return true;
            }
            case PSP_MoveItemToInventory:
            case ESP_MoveItemToInventory: {
                int slot = cp.Decode2();
                IPlayerShop shop = chr.getPlayerShop();
                if (shop == null || !shop.isOwner(chr) || shop instanceof MapleMiniGame || shop.getItems().size() <= 0 || shop.getItems().size() <= slot || slot < 0) {
                    chr.updateStat();
                    return true;
                }
                MaplePlayerShopItem item = shop.getItems().get(slot);

                if (item != null) {
                    if (item.bundles > 0) {
                        long check = item.bundles * item.item.getQuantity();
                        if (check <= 0 || check > 32767) {
                            chr.updateStat();
                            return true;
                        }
                        if (MapleInventoryManipulator.checkSpace(chr, item.item.getItemId(), (short) check, item.item.getOwner())) {
                            // the shop slot is removed below, so the stock itself goes back to the inventory.
                            item.item.setQuantity((short) check);
                            MapleInventoryManipulator.addFromDrop(chr, item.item, false);
                            item.bundles = 0;
                            shop.removeFromSlot(slot);
                        }
                    }
                }
                chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_PSP_Refresh(shop));
                chr.updateStat(); // CWvsContext::SetExclRequestSent
                return true;
            }
            case PSP_Ban: {
                // 営業許可証 追放
                byte visitor_slot = cp.Decode1();
                String visitor_name = cp.DecodeStr();
                IPlayerShop ips = chr.getPlayerShop();
                if (!(ips instanceof MaplePlayerShop) || !ips.isOwner(chr)) {
                    return true;
                }
                for (SimpleImmutableEntry<Byte, TacosCharacter> visitor : ips.getVisitors()) {
                    if (visitor.getValue().getName().equals(visitor_name)) {
                        ((MaplePlayerShop) ips).ban(visitor.getValue());
                        return true;
                    }
                }
                return true;
            }
            case PSP_DeliverBlackList: {
                // 営業許可証 ブラックリスト (クライアントの設定)
                short count = cp.Decode2();
                List<String> names = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    names.add(cp.DecodeStr());
                }
                IPlayerShop ips = chr.getPlayerShop();
                if (ips instanceof MaplePlayerShop && ips.isOwner(chr)) {
                    ((MaplePlayerShop) ips).setBlackList(names);
                }
                return true;
            }
            case ESP_GoOut: {
                // the owner leaves the shop opened, MRP_Leave follows this.
                return true;
            }
            case ESP_ArrangeItem: {
                // 雇用商人 "整理", sold out items are removed and the meso are withdrawn.
                TacosMerchant merchant = chr.getMyHiredMerchant();
                if (merchant == null) {
                    chr.updateStat();
                    return true;
                }
                merchant.arrangeItems(chr);
                chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_ESP_ArrangeItemResult(merchant.getMeso()));
                chr.updateStat();
                return true;
            }
            case ESP_WithdrawAll: {
                // 雇用商人 "商店とクローズ"
                // "イベントリに空きがないとアイテムはストアーバンクNPCのプレドリックのところで探すべきです。閉店しますか？" と聞かれてOKを押した場合の処理
                // closing the dialog of a shop that is not opened yet sends this without the question.
                TacosMerchant merchant = chr.getMyHiredMerchant();
                if (merchant == null) {
                    chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_ESP_WithdrawAllResult(OpsESWithdrawAll.ESWithdrawAll_NoMessage));
                    return true;
                }
                // the meso and the items that do not fit go to the store bank.
                chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_ESP_WithdrawAllResult(merchant.closeByOwner(chr)));
                return true;
            }
            case ESP_WithdrawMoney: {
                // 雇用商人 "メル回収"
                TacosMerchant merchant = chr.getMyHiredMerchant();
                if (merchant != null && merchant.withdrawMoney(chr)) {
                    chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_ESP_WithdrawMoneyResult());
                }
                chr.updateStat();
                return true;
            }
            case ESP_AdminChangeTitle: {
                // GMが右クリックした場合の雇用商人の名前を替えますか？でOKを押したときに送信されるデータ
                // @007F 2A [BC 7D 00 00 (ID)]
                return true;
            }
            case ESP_DeliverVisitList: {
                TacosMerchant merchant = chr.getMyHiredMerchant();
                if (merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_DeliverVisitList");
                    return true;
                }
                merchant.sendVisitor(chr);
                return true;
            }
            case ESP_DeliverBlackList: {
                TacosMerchant merchant = chr.getMyHiredMerchant();
                if (merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_DeliverBlackList");
                    return true;
                }
                merchant.sendBlackList(chr);
                return true;
            }
            case ESP_AddBlackList: {
                TacosMerchant merchant = chr.getMyHiredMerchant();
                if (merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_AddBlackList");
                    return true;
                }
                String character_name = cp.DecodeStr(); // sName
                merchant.addBlackList(character_name);
                return true;
            }
            case ESP_DeleteBlackList: {
                TacosMerchant merchant = chr.getMyHiredMerchant();
                if (merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_DeleteBlackList");
                    return true;
                }
                String character_name = cp.DecodeStr(); // sName
                merchant.removeBlackList(character_name);
                return true;
            }
            case MGRP_TieRequest: {
                IPlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        return true;
                    }
                    if (game.isOwner(chr)) {
                        game.broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_MGRP_TieRequest(), false);
                    } else {
                        game.getMCOwner().SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MGRP_TieRequest());
                    }
                    game.setRequestedTie(game.getVisitorSlot(chr));
                }
                return true;
            }
            case MGRP_TieResult: {
                IPlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        return true;
                    }
                    if (game.getRequestedTie() > -1 && game.getRequestedTie() != game.getVisitorSlot(chr)) {
                        byte unk1 = cp.Decode1();
                        if (unk1 > 0) {
                            game.setGameResult(1, game.getRequestedTie());
                            game.broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_MGRP_GameResult(game, 1, game.getRequestedTie()));
                            game.nextLoser();
                            game.setOpen(true);
                            map.splitSendPacket(chr, ResCUser.UserMiniRoomBalloon(chr));
                            game.checkExitAfterGame();
                        } else {
                            game.broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_MGRP_TieResult());
                        }
                        game.setRequestedTie(-1);
                    }
                }
                return true;
            }
            case MGRP_GiveUpRequest: {
                IPlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        return true;
                    }
                    game.setGameResult(0, game.getVisitorSlot(chr));
                    game.broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_MGRP_GameResult(game, 0, game.getVisitorSlot(chr)));
                    game.nextLoser();
                    game.setOpen(true);
                    map.splitSendPacket(chr, ResCUser.UserMiniRoomBalloon(chr));
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
                IPlayerShop ips = chr.getPlayerShop();
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
                game.broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_MGRP_LeaveEngage(game.isExitAfter(chr)));
                return true;
            }
            case MGRP_Ready:
            case MGRP_CancelReady: {
                IPlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (!game.isOwner(chr) && game.isOpen()) {
                        game.setReady(game.getVisitorSlot(chr));
                        game.broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_MGRP_Ready(game.isReady(game.getVisitorSlot(chr))));
                    }
                }
                return true;
            }
            case MGRP_Ban: {
                IPlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (!game.isOpen()) {
                        return true;
                    }
                    game.removeAllVisitors(OpsMRLeave.MRLeave_Kicked.get(), 1);
                }
                return true;
            }
            case MGRP_Start: {
                IPlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOwner(chr) && game.isOpen()) {
                        for (int i = 1; i < game.getSize(); i++) {
                            if (!game.isReady(i)) {
                                return true;
                            }
                        }
                        game.setGameType();
                        game.shuffleList();
                        game.broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_MGRP_Start(game, game.getLoser()));
                        game.setOpen(false);
                        map.splitSendPacket(chr, ResCUser.UserMiniRoomBalloon(chr));
                    }
                }
                return true;
            }
            case MGRP_GameResult: {
                return true;
            }
            case MGRP_TimeOver: {
                IPlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        return true;
                    }
                    game.broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_MGRP_TimeOver(game.getVisitorSlot(chr)));
                    game.nextLoser();
                }
                return true;
            }
            case ORP_PutStoneChecker: {
                IPlayerShop ips = chr.getPlayerShop();
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
                IPlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    int slot = cp.Decode1();
                    int turn = game.getTurn();
                    int fs = game.getFirstSlot();
                    if (turn == 1) {
                        game.setFirstSlot(slot);
                        if (game.isOwner(chr)) {
                            game.broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_MGP_TurnUpCard(turn, slot, fs, turn), false);
                        } else {
                            game.getMCOwner().SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MGP_TurnUpCard(turn, slot, fs, turn));
                        }
                        game.setTurn(0); //2nd turn nao
                        return true;
                    } else if (fs > 0 && game.getCardId(fs + 1) == game.getCardId(slot + 1)) {
                        game.broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_MGP_TurnUpCard(turn, slot, fs, game.isOwner(chr) ? 2 : 3));
                        game.setPoints(game.getVisitorSlot(chr)); //correct.. so still same loser. diff turn tho
                    } else {
                        game.broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_MGP_TurnUpCard(turn, slot, fs, game.isOwner(chr) ? 0 : 1));
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
    public static boolean remoteStore(TacosCharacter chr, short item_slot) {
        Runnable item_use = chr.checkItemSlot(item_slot, 5470000);

        if (item_use == null) {
            DebugLogger.ErrorLog("remoteStore : no item.");
            return false;
        }

        TacosMerchant merchant = chr.getRemoteStore();
        if (merchant == null) {
            TacosMerchant hm = new TacosMerchant(chr, 5030000, "DebugHiredMarchant");
            chr.getMap().addMerchant(hm);
            return false;
        }

        // "商店の主人が物品整理中でございます。もうしばらく後でご利用ください。" for the visitors.
        merchant.enter(chr);
        return true;
    }
}
