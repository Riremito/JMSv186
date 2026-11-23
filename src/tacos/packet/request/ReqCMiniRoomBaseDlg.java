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

import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.server.MapleTrade;
import odin.server.shops.MapleMemoryGame;
import odin.server.shops.MapleMiniGame;
import odin.server.shops.MapleOmok;
import odin.server.shops.MaplePlayerShop;
import odin.server.shops.MaplePlayerShop.MaplePlayerShopItem;
import tacos.client.TacosCharacter;
import tacos.client.TacosStoreBank;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsESWithdrawAll;
import tacos.packet.ops.OpsMREnterResult;
import tacos.packet.ops.OpsMiniRoomProtocol;
import tacos.packet.ops.OpsMiniRoomType;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import tacos.server.TacosRoom;
import tacos.server.TacosWorld;
import tacos.server.map.TacosMap;
import tacos.server.map.object.TacosMerchant;

/**
 *
 * @author Riremito
 */
public class ReqCMiniRoomBaseDlg {

    // CMiniRoomBaseDlg::OnPacketBase
    public static boolean OnMiniRoom(TacosCharacter chr, TacosMap map, ClientPacket cp) {
        byte protocol_req = cp.Decode1();
        OpsMiniRoomProtocol protocol = OpsMiniRoomProtocol.find(protocol_req);

        if (OnMiniRoomBase(chr, map, protocol, cp)) {
            return true;
        }
        if (isGroup(protocol, "TRP_")) {
            return OnTradingRoom(chr, protocol, cp);
        }
        if (isGroup(protocol, "PSP_")) {
            return OnPersonalShop(chr, protocol, cp);
        }
        if (isGroup(protocol, "ESP_")) {
            return OnEntrustedShop(chr, protocol, cp);
        }
        if (isGroup(protocol, "MGRP_")) {
            return OnMiniGame(chr, protocol, cp);
        }
        if (isGroup(protocol, "ORP_")) {
            return OnOmok(chr, protocol, cp);
        }
        if (isGroup(protocol, "MGP_")) {
            return OnMemoryGame(chr, protocol, cp);
        }
        DebugLogger.ErrorLog("OnMiniRoom : not coded = " + protocol_req);
        return false;
    }

    private static boolean isGroup(OpsMiniRoomProtocol protocol, String prefix) {
        return protocol != OpsMiniRoomProtocol.UNKNOWN && protocol.name().startsWith(prefix);
    }

    private static boolean notCoded(OpsMiniRoomProtocol protocol) {
        DebugLogger.ErrorLog("OnMiniRoom : not coded = " + protocol);
        return false;
    }

    // MRP_, false = not a common protocol
    private static boolean OnMiniRoomBase(TacosCharacter chr, TacosMap map, OpsMiniRoomProtocol protocol, ClientPacket cp) {
        switch (protocol) {
            case MRP_Create: {
                OnCreate(chr, map, cp);
                return true;
            }
            case MRP_Invite: {
                int character_id = cp.Decode4();
                if (chr.getTrade() != null) {
                    chr.getTrade().invite(map.getPlayerById(character_id));
                }
                return true;
            }
            case MRP_InviteResult: {
                if (chr.getTrade() != null) {
                    chr.getTrade().decline();
                }
                return true;
            }
            case MRP_Enter: {
                OnEnter(chr, map, cp);
                return true;
            }
            case MRP_Chat: {
                int unk = cp.Decode4();
                String message = cp.DecodeStr();
                OnChat(chr, message);
                return true;
            }
            case MRP_GameMessage:
            case MRP_UserChat:
            case MRP_Avatar: {
                return true;
            }
            case MRP_Leave: {
                OnLeave(chr);
                return true;
            }
            case MRP_Balloon: {
                OnBalloon(chr, map);
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // MRP_Create
    private static void OnCreate(TacosCharacter chr, TacosMap map, ClientPacket cp) {
        byte miniroom_type = cp.Decode1();

        switch (OpsMiniRoomType.find(miniroom_type)) {
            case MR_OmokRoom:
            case MR_MemoryGameRoom: {
                String desc = cp.DecodeStr();
                byte is_private = cp.Decode1();
                String pass = (is_private != 0) ? cp.DecodeStr() : "";
                int game_kind = cp.Decode1();
                boolean is_omok = miniroom_type == OpsMiniRoomType.MR_OmokRoom.get();
                int item_id = is_omok ? (4080000 + game_kind) : 4080100;
                if (chr.isInMiniRoom() || !chr.haveItem(item_id) || isFreeMarket(chr)) {
                    return;
                }
                MapleMiniGame game = is_omok ? new MapleOmok(chr, item_id, desc, pass, game_kind) : new MapleMemoryGame(chr, item_id, desc, pass, game_kind);
                game.create();
                return;
            }
            case MR_TradingRoom: {
                MapleTrade.start(chr, false);
                return;
            }
            case MR_CashTradingRoom: {
                // 未使用の可能性あり
                int unk1 = cp.Decode4();
                // 交換窓のID
                int trade_id = cp.Decode4();
                MapleTrade.start(chr, true);
                return;
            }
            case MR_PersonalShop: {
                String desc = cp.DecodeStr();
                byte unk2 = cp.Decode1();
                short item_slot = cp.Decode2();
                int item_id = cp.Decode4();
                if (chr.isInMiniRoom() || !hasShopItem(chr, item_slot, item_id) || !isFreeMarket(chr)) {
                    return;
                }
                // items left from a trade or a personal shop that did not fit in the inventory.
                if (!chr.returnMiniRoomInventory()) {
                    return;
                }
                new MaplePlayerShop(chr, item_id, desc).create();
                return;
            }
            case MR_EntrustedShop: {
                String desc = cp.DecodeStr();
                byte unk2 = cp.Decode1();
                short item_slot = cp.Decode2();
                int item_id = cp.Decode4();
                if (chr.isInMiniRoom() || !hasShopItem(chr, item_slot, item_id) || !isFreeMarket(chr)) {
                    return;
                }
                // one shop per character, and the store bank has to be empty.
                if (TacosStoreBank.findMerchant(chr) != null || !TacosStoreBank.isEmpty(chr)) {
                    return;
                }
                TacosMerchant merch = new TacosMerchant(chr, item_id, desc);
                chr.setMerchant(merch);
                chr.setRemoteStore(merch);
                // the employee appears on the map when the start button is pressed (MRP_Balloon).
                chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_EntrustedShop(chr, merch, true));
                return;
            }
            default: {
                break;
            }
        }
    }

    private static boolean isFreeMarket(TacosCharacter chr) {
        return 910000001 <= chr.getMapId() && chr.getMapId() <= 910000022;
    }

    private static boolean hasShopItem(TacosCharacter chr, short item_slot, int item_id) {
        Item shop = chr.getInventory(MapleInventoryType.CASH).getItem(item_slot);
        return shop != null && 0 < shop.getQuantity() && shop.getItemId() == item_id;
    }

    // MRP_Enter
    private static void OnEnter(TacosCharacter chr, TacosMap map, ClientPacket cp) {
        int object_id = cp.Decode4();
        // 交換, the invited user accepted.
        if (chr.getTrade() != null) {
            chr.getTrade().enter();
            return;
        }
        OpsMREnterResult result = OpsMREnterResult.MREnterResult_NoRoom;
        if (chr.isInMiniRoom()) {
            result = OpsMREnterResult.MREnterResult_Busy;
        } else if (map.getMerchantByOid(object_id) != null) {
            // 雇用商人
            TacosMerchant merchant = map.getMerchantByOid(object_id);
            result = merchant.enter(chr);
            TacosRoom room = chr.getWorld().getRoom(merchant.getRoomId());
            if (result == OpsMREnterResult.MREnterResult_Success && room != null) {
                room.addPlayer(chr);
            }
        } else if (map.getPlayerShopByOid(object_id) != null) {
            // 個人商店
            MaplePlayerShop player_shop = map.getPlayerShopByOid(object_id);
            byte is_private = cp.Decode1();
            if (is_private != 0) {
                String password = cp.DecodeStr(); // personal shops have no password
            }
            result = player_shop.enter(chr);
        } else if (map.getMiniGameByOid(object_id) != null) {
            // ミニゲーム
            MapleMiniGame game = map.getMiniGameByOid(object_id);
            byte is_private = cp.Decode1();
            String password = (is_private != 0) ? cp.DecodeStr() : "";
            result = game.enter(chr, password);
        }
        if (result != OpsMREnterResult.MREnterResult_Success) {
            chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_Failed(result));
        }
    }

    // MRP_Chat
    private static void OnChat(TacosCharacter chr, String message) {
        if (chr.getTrade() != null) {
            chr.getTrade().chat(message);
            return;
        }
        if (chr.getMiniGame() != null) {
            chr.getMiniGame().chat(chr, message);
            return;
        }
        if (chr.getPlayerShop() != null) {
            chr.getPlayerShop().chat(chr, message);
            return;
        }
        if (chr.getMerchant() != null) {
            TacosMerchant merchant = chr.getMerchant();
            merchant.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Chat(merchant.getVisitorSlot(chr), chr.getName() + " : " + message));
            return;
        }
        DebugLogger.ErrorLog("OnMiniRoom : MRP_Chat, not  coded.");
    }

    // MRP_Leave, the dialog is closed.
    private static void OnLeave(TacosCharacter chr) {
        TacosWorld world = chr.getWorld();
        TacosRoom room = world.findRoom(chr);
        if (room != null) {
            room.removePlayer(chr);
        }
        if (chr.getTrade() != null) {
            chr.getTrade().cancel();
            return;
        }
        if (chr.getMiniGame() != null) {
            chr.getMiniGame().leave(chr);
            return;
        }
        // personal shop, the owner closes it and gets the items back.
        if (chr.getPlayerShop() != null) {
            chr.getPlayerShop().leave(chr);
            return;
        }
        // hired merchant, the owner managing it from the shop or the remote controller, or a visitor.
        if (chr.getMerchant() != null) {
            chr.getMerchant().leave(chr);
        }
    }

    // MRP_Balloon, the owner opens the shop.
    private static void OnBalloon(TacosCharacter chr, TacosMap map) {
        if (!map.allowPersonalShop()) {
            return;
        }
        MaplePlayerShop player_shop = chr.getPlayerShop();
        if (player_shop != null && player_shop.isOwner(chr)) {
            player_shop.open();
            return;
        }
        // CEntrustedShopDlg::OnCorrectSSN2, the start button is pressed once, the dialog is closed with MRP_Leave after this.
        TacosMerchant merchant = chr.getMyHiredMerchant();
        if (merchant != null && !merchant.isOnMap()) {
            merchant.open(chr);
        }
    }

    // TRP_ : CTradingRoomDlg
    private static boolean OnTradingRoom(TacosCharacter chr, OpsMiniRoomProtocol protocol, ClientPacket cp) {
        MapleTrade trade = chr.getTrade();
        if (trade == null) {
            DebugLogger.ErrorLog("OnMiniRoom : " + protocol + ", no trade");
            return true;
        }
        switch (protocol) {
            case TRP_PutItem: {
                byte item_type = cp.Decode1();
                short item_slot = cp.Decode2();
                short quantity = cp.Decode2();
                byte target_slot = cp.Decode1();
                trade.putItem(MapleInventoryType.getByType(item_type), item_slot, quantity, target_slot);
                return true;
            }
            case TRP_PutMoney: {
                int meso = cp.Decode4();
                trade.putMoney(meso);
                return true;
            }
            case TRP_Trade: {
                trade.confirm();
                return true;
            }
            default: {
                break;
            }
        }
        return notCoded(protocol);
    }

    // PSP_ : CPersonalShopDlg
    private static boolean OnPersonalShop(TacosCharacter chr, OpsMiniRoomProtocol protocol, ClientPacket cp) {
        MaplePlayerShop shop = chr.getPlayerShop();
        if (shop == null) {
            DebugLogger.ErrorLog("OnMiniRoom : " + protocol + ", no personal shop");
            chr.updateStat();
            return true;
        }
        switch (protocol) {
            case PSP_PutItem: {
                byte inv_type = cp.Decode1();
                short slot = cp.Decode2();
                short bundles = cp.Decode2(); // How many in a bundle
                short per_bundle = cp.Decode2(); // Price per bundle
                int price = cp.Decode4();
                shop.putItem(chr, MapleInventoryType.getByType(inv_type), slot, bundles, per_bundle, price);
                return true;
            }
            case PSP_BuyItem: {
                int index = cp.Decode1();
                short quantity = cp.Decode2();
                shop.buy(chr, index, quantity);
                return true;
            }
            case PSP_MoveItemToInventory: {
                int index = cp.Decode2();
                shop.moveItemToInventory(chr, index);
                return true;
            }
            case PSP_Ban: {
                // 営業許可証 追放
                byte visitor_slot = cp.Decode1();
                String visitor_name = cp.DecodeStr();
                shop.ban(chr, visitor_name);
                return true;
            }
            case PSP_DeliverBlackList: {
                // 営業許可証 ブラックリスト (クライアントの設定)
                short count = cp.Decode2();
                for (int i = 0; i < count; i++) {
                    shop.addBlackList(chr, cp.DecodeStr());
                }
                return true;
            }
            case PSP_AddBlackList: {
                String character_name = cp.DecodeStr(); // sName
                shop.addBlackList(chr, character_name);
                return true;
            }
            case PSP_DeleteBlackList: {
                String character_name = cp.DecodeStr(); // sName
                shop.deleteBlackList(chr, character_name);
                return true;
            }
            default: {
                break;
            }
        }
        return notCoded(protocol);
    }

    // ESP_ : CEntrustedShopDlg
    private static boolean OnEntrustedShop(TacosCharacter chr, OpsMiniRoomProtocol protocol, ClientPacket cp) {
        TacosMerchant merchant = chr.getMerchant();
        TacosMerchant my_merchant = chr.getMyHiredMerchant();
        switch (protocol) {
            case ESP_PutItem: {
                byte inv_type = cp.Decode1();
                short slot = cp.Decode2();
                short bundles = cp.Decode2(); // How many in a bundle
                short per_bundle = cp.Decode2(); // Price per bundle
                int price = cp.Decode4();
                if (my_merchant == null) {
                    return true;
                }
                MaplePlayerShopItem item = MaplePlayerShopItem.takeFromInventory(chr, MapleInventoryType.getByType(inv_type), slot, bundles, per_bundle, price);
                if (item != null) {
                    my_merchant.addItem(item);
                    chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_PSP_Refresh_EntrustedShop(my_merchant));
                }
                return true;
            }
            case ESP_BuyItem:
            case ESP_BuyResult: {
                int index = cp.Decode1();
                short quantity = cp.Decode2();
                if (merchant == null || merchant.isOwner(chr) || !MaplePlayerShopItem.isValidBuy(merchant.getItems(), index, quantity)) {
                    chr.updateStat();
                    return true;
                }
                long price = (long) merchant.getItems().get(index).price * quantity;
                if (chr.getMeso() < price || Integer.MAX_VALUE < merchant.getMeso() + price) {
                    chr.updateStat();
                    return true;
                }
                merchant.buy(chr, index, quantity);
                merchant.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_PSP_Refresh_EntrustedShop(merchant));
                return true;
            }
            case ESP_MoveItemToInventory: {
                int index = cp.Decode2();
                if (my_merchant == null || index < 0 || my_merchant.getItems().size() <= index) {
                    chr.updateStat();
                    return true;
                }
                if (my_merchant.getItems().get(index).returnTo(chr)) {
                    my_merchant.removeFromSlot(index);
                }
                chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_PSP_Refresh_EntrustedShop(my_merchant));
                chr.updateStat(); // CWvsContext::SetExclRequestSent
                return true;
            }
            case ESP_GoOut: {
                // the owner leaves the shop opened, MRP_Leave follows this.
                return true;
            }
            case ESP_ArrangeItem: {
                // 雇用商人 "整理", sold out items are removed and the meso are withdrawn.
                if (my_merchant == null) {
                    chr.updateStat();
                    return true;
                }
                my_merchant.arrangeItems(chr);
                chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_ESP_ArrangeItemResult(my_merchant.getMeso()));
                chr.updateStat();
                return true;
            }
            case ESP_WithdrawAll: {
                // 雇用商人 "商店とクローズ"
                // "イベントリに空きがないとアイテムはストアーバンクNPCのプレドリックのところで探すべきです。閉店しますか？" と聞かれてOKを押した場合の処理
                // closing the dialog of a shop that is not opened yet sends this without the question.
                if (my_merchant == null) {
                    chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_ESP_WithdrawAllResult(OpsESWithdrawAll.ESWithdrawAll_NoMessage));
                    return true;
                }
                // the meso and the items that do not fit go to the store bank.
                chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_ESP_WithdrawAllResult(my_merchant.closeByOwner(chr)));
                return true;
            }
            case ESP_WithdrawMoney: {
                // 雇用商人 "メル回収"
                if (my_merchant != null && my_merchant.withdrawMoney(chr)) {
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
                if (my_merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_DeliverVisitList");
                    return true;
                }
                my_merchant.sendVisitor(chr);
                return true;
            }
            case ESP_DeliverBlackList: {
                if (my_merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_DeliverBlackList");
                    return true;
                }
                my_merchant.sendBlackList(chr);
                return true;
            }
            case ESP_AddBlackList: {
                if (my_merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_AddBlackList");
                    return true;
                }
                String character_name = cp.DecodeStr(); // sName
                my_merchant.addBlackList(character_name);
                return true;
            }
            case ESP_DeleteBlackList: {
                if (my_merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_DeleteBlackList");
                    return true;
                }
                String character_name = cp.DecodeStr(); // sName
                my_merchant.removeBlackList(character_name);
                return true;
            }
            default: {
                break;
            }
        }
        return notCoded(protocol);
    }

    // MGRP_ : COmokDlg, CMemoryGameDlg
    private static boolean OnMiniGame(TacosCharacter chr, OpsMiniRoomProtocol protocol, ClientPacket cp) {
        MapleMiniGame game = chr.getMiniGame();
        if (game == null) {
            DebugLogger.ErrorLog("OnMiniRoom : " + protocol + ", no mini game");
            return true;
        }
        switch (protocol) {
            case MGRP_TieRequest: {
                game.requestTie(chr);
                return true;
            }
            case MGRP_TieResult: {
                boolean accepted = cp.Decode1() != 0;
                game.answerTie(chr, accepted);
                return true;
            }
            case MGRP_GiveUpRequest: {
                game.giveUp(chr);
                return true;
            }
            case MGRP_GiveUpResult:
            case MGRP_RetreatRequest:
            case MGRP_RetreatResult:
            case MGRP_GameResult: {
                return true;
            }
            case MGRP_LeaveEngage: {
                game.setExitAfter(chr, true);
                return true;
            }
            case MGRP_LeaveEngageCancel: {
                game.setExitAfter(chr, false);
                return true;
            }
            case MGRP_Ready: {
                game.setReady(chr, true);
                return true;
            }
            case MGRP_CancelReady: {
                game.setReady(chr, false);
                return true;
            }
            case MGRP_Ban: {
                game.ban(chr);
                return true;
            }
            case MGRP_Start: {
                game.start(chr);
                return true;
            }
            case MGRP_TimeOver: {
                game.timeOver(chr);
                return true;
            }
            default: {
                break;
            }
        }
        return notCoded(protocol);
    }

    // ORP_ : COmokDlg
    private static boolean OnOmok(TacosCharacter chr, OpsMiniRoomProtocol protocol, ClientPacket cp) {
        if (!(chr.getMiniGame() instanceof MapleOmok)) {
            DebugLogger.ErrorLog("OnMiniRoom : " + protocol + ", no omok");
            return true;
        }
        MapleOmok omok = (MapleOmok) chr.getMiniGame();
        switch (protocol) {
            case ORP_PutStoneChecker: {
                int x = cp.Decode4();
                int y = cp.Decode4();
                byte type = cp.Decode1();
                omok.putStone(chr, x, y, type);
                return true;
            }
            default: {
                break;
            }
        }
        return notCoded(protocol);
    }

    // MGP_ : CMemoryGameDlg
    private static boolean OnMemoryGame(TacosCharacter chr, OpsMiniRoomProtocol protocol, ClientPacket cp) {
        if (!(chr.getMiniGame() instanceof MapleMemoryGame)) {
            DebugLogger.ErrorLog("OnMiniRoom : " + protocol + ", no memory game");
            return true;
        }
        MapleMemoryGame memory_game = (MapleMemoryGame) chr.getMiniGame();
        switch (protocol) {
            case MGP_TurnUpCard: {
                // CMemoryGameDlg::SendTurnUpCard
                boolean first = cp.Decode1() != 0;
                int card = cp.Decode1();
                memory_game.turnUpCard(chr, first, card);
                return true;
            }
            case MGP_MatchCard: {
                return true;
            }
            default: {
                break;
            }
        }
        return notCoded(protocol);
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
