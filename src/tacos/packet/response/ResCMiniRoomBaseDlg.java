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
package tacos.packet.response;

import tacos.client.TacosCharacter;
import odin.server.shops.IPlayerShop;
import tacos.packet.ops.OpsMREnterResult;
import tacos.packet.ops.OpsMRLeave;
import tacos.server.map.object.TacosMerchant;
import odin.client.inventory.Equip;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import java.util.List;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsMiniRoomProtocol;
import tacos.packet.ops.OpsMiniRoomType;
import tacos.packet.response.data.RD_AvatarLook;
import tacos.packet.response.data.RD_GW_ItemSlotBase;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleTrade;
import odin.server.shops.MapleMiniGame;
import odin.server.shops.MaplePlayerShop.MaplePlayerShopItem;
import tacos.config.Config;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.config.Region;
import tacos.packet.ServerPacketHeader;
import tacos.wz.WzDataStorage;

/**
 *
 * @author Riremito
 */
public class ResCMiniRoomBaseDlg {

    // LP_MiniRoom : CMiniRoomBaseDlg::OnPacketBase
    // MRP_Invite : CMiniRoomBaseDlg::OnInviteStatic
    public static ServerPacket MiniRoom_MRP_Invite(TacosCharacter inviter, boolean isPointTrade) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_Invite.get());
        sp.Encode1(isPointTrade ? OpsMiniRoomType.MR_CashTradingRoom.get() : OpsMiniRoomType.MR_TradingRoom.get()); // nMiniRoomType
        sp.EncodeStr(inviter.getName()); // sInviter
        sp.Encode4(0); // dwSN (trade id)
        return sp;
    }

    // MRP_Enter : CMiniRoomBaseDlg::OnEnterBase
    public static ServerPacket MiniRoom_MRP_Enter(int slot, TacosCharacter chr) {
        return MiniRoom_MRP_Enter(slot, chr, null);
    }

    // MRP_Enter : CMiniRoomBaseDlg::OnEnterBase, COmokDlg::OnEnter / CMemoryGameDlg::OnEnter
    public static ServerPacket MiniRoom_MRP_Enter(int slot, TacosCharacter chr, MapleMiniGame game) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_Enter.get());
        sp.Encode1(slot); // nSlot
        sp.EncodeBuffer(RD_AvatarLook.Encode(chr)); // CMiniRoomBaseDlg::DecodeAvatar
        sp.EncodeStr(chr.getName()); // m_asUserID[nSlot]
        sp.Encode2(chr.getJob(), Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.THMS, 87)); // m_anJobCode[nSlot]
        if (game != null) {
            sp.EncodeBuffer(GW_MiniGameRecord_Encode(chr, game));
        }
        return sp;
    }

    // MRP_EnterResult : CMiniRoomBaseDlg::OnEnterResultStatic (failed)
    public static ServerPacket MiniRoom_MRP_EnterResult_Failed(OpsMREnterResult result) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_EnterResult.get());
        sp.Encode1(0); // nMiniRoomType, 0 = failed
        sp.Encode1(result.get());
        return sp;
    }

    // MRP_EnterResult : CMiniRoomBaseDlg::OnEnterResultBase, CTradingRoomDlg::OnEnterResult
    public static ServerPacket MiniRoom_MRP_EnterResult_TradingRoom(TacosCharacter chr, MapleTrade trade, byte number, boolean isPointTrade) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_EnterResult.get());
        sp.Encode1(isPointTrade ? OpsMiniRoomType.MR_CashTradingRoom.get() : OpsMiniRoomType.MR_TradingRoom.get()); // nMiniRoomType
        sp.Encode1(2); // m_nMaxUsers
        sp.Encode1(number); // m_nMyPosition
        if (number == 1) {
            TacosCharacter partner = trade.getPartner().getChr();
            sp.Encode1(0); // slot
            sp.EncodeBuffer(RD_AvatarLook.Encode(partner)); // CMiniRoomBaseDlg::DecodeAvatar
            sp.EncodeStr(partner.getName()); // m_asUserID[i]
            sp.Encode2(partner.getJob(), Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.THMS, 87)); // m_anJobCode[i]
        }
        sp.Encode1(number); // slot
        sp.EncodeBuffer(RD_AvatarLook.Encode(chr)); // CMiniRoomBaseDlg::DecodeAvatar
        sp.EncodeStr(chr.getName()); // m_asUserID[i]
        sp.Encode2(chr.getJob(), Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.THMS, 87)); // m_anJobCode[i]
        sp.Encode1(-1); // end of users
        return sp;
    }

    // MRP_EnterResult : CMiniRoomBaseDlg::OnEnterResultBase, CPersonalShopDlg::OnEnterResult
    public static ServerPacket MiniRoom_MRP_EnterResult_PersonalShop(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        IPlayerShop ips = chr.getPlayerShop();
        TacosCharacter owner = ips.getMCOwner();
        sp.Encode1(OpsMiniRoomProtocol.MRP_EnterResult.get());
        sp.Encode1(OpsMiniRoomType.MR_PersonalShop.get()); // nMiniRoomType
        sp.Encode1(4); // m_nMaxUsers
        sp.Encode1(ips.getVisitorSlot(chr)); // m_nMyPosition
        sp.Encode1(0); // slot (owner)
        sp.EncodeBuffer(RD_AvatarLook.Encode(owner)); // CMiniRoomBaseDlg::DecodeAvatar
        sp.EncodeStr(ips.getOwnerName()); // m_asUserID[i]
        sp.Encode2(owner.getJob(), Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.THMS, 87)); // m_anJobCode[i]
        for (SimpleImmutableEntry<Byte, TacosCharacter> visitor : ips.getVisitors()) {
            sp.Encode1(visitor.getKey()); // slot
            sp.EncodeBuffer(RD_AvatarLook.Encode(visitor.getValue())); // CMiniRoomBaseDlg::DecodeAvatar
            sp.EncodeStr(visitor.getValue().getName()); // m_asUserID[i]
            sp.Encode2(visitor.getValue().getJob(), Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.THMS, 87)); // m_anJobCode[i]
        }
        sp.Encode1(-1); // end of users
        // CPersonalShopDlg::OnEnterResult
        sp.EncodeStr(ips.getDescription()); // m_sTitle
        sp.Encode1(10); // m_nItemMaxCount
        // CPersonalShopDlg::OnRefresh
        sp.Encode1(ips.getItems().size());
        for (MaplePlayerShopItem item : ips.getItems()) {
            sp.Encode2(item.bundles); // nNumber
            sp.Encode2(item.item.getQuantity()); // nSet
            sp.Encode4(item.price); // nPrice
            sp.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item.item));
        }
        return sp;
    }

    // MRP_EnterResult : CMiniRoomBaseDlg::OnEnterResultBase, CEntrustedShopDlg::OnEnterResult
    public static ServerPacket MiniRoom_MRP_EnterResult_EntrustedShop(TacosCharacter chr, TacosMerchant merch, boolean firstTime) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_EnterResult.get());
        sp.Encode1(OpsMiniRoomType.MR_EntrustedShop.get()); // nMiniRoomType
        sp.Encode1(4); // m_nMaxUsers
        sp.Encode1(merch.getVisitorSlot(chr)); // m_nMyPosition
        sp.Encode1(0); // slot (employer)
        sp.Encode4(merch.getItemId()); // RegisterEmployer, dwTemplateID
        sp.EncodeStr("雇用商人"); // m_asUserID[0]
        for (SimpleImmutableEntry<Byte, TacosCharacter> visitor : merch.getVisitors()) {
            sp.Encode1(visitor.getKey()); // slot
            sp.EncodeBuffer(RD_AvatarLook.Encode(visitor.getValue())); // CMiniRoomBaseDlg::DecodeAvatar
            sp.EncodeStr(visitor.getValue().getName()); // m_asUserID[i]
            sp.Encode2(visitor.getValue().getJob(), Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.THMS, 87)); // m_anJobCode[i]
        }
        sp.Encode1(-1); // end of users
        // CEntrustedShopDlg::OnEnterResult
        sp.Encode2(0); // chat count
        sp.EncodeStr(merch.getOwnerName());
        if (merch.isOwner(chr)) {
            sp.Encode4(merch.getTimeLeft());
            sp.Encode1(firstTime ? 1 : 0);
            sp.Encode1(merch.getBoughtItems().size());
            for (TacosMerchant.BoughtItem sold_item : merch.getBoughtItems()) {
                sp.Encode4(sold_item.id);
                sp.Encode2(sold_item.quantity); // number of purchased
                sp.Encode4(sold_item.totalPrice); // total price
                sp.EncodeStr(sold_item.buyer); // name of the buyer
            }
            sp.Encode4(merch.getMeso());
        }
        sp.EncodeStr(merch.getDescription()); // m_sTitle
        sp.Encode1(10); // m_nItemMaxCount
        // CEntrustedShopDlg::OnRefresh
        sp.Encode4(merch.getMeso()); // m_nMoney
        // CPersonalShopDlg::OnRefresh
        sp.Encode1(merch.getItems().size());
        for (MaplePlayerShopItem item : merch.getItems()) {
            sp.Encode2(item.bundles); // nNumber
            sp.Encode2(item.item.getQuantity()); // nSet
            sp.Encode4(item.price); // nPrice
            sp.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item.item));
        }
        return sp;
    }

    // MRP_EnterResult : CMiniRoomBaseDlg::OnEnterResultBase, COmokDlg::OnEnterResult / CMemoryGameDlg::OnEnterResult
    public static ServerPacket MiniRoom_MRP_EnterResult_MiniGame(TacosCharacter chr, MapleMiniGame game) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_EnterResult.get());
        sp.Encode1(game.getGameType()); // nMiniRoomType
        sp.Encode1(game.getMaxSize()); // m_nMaxUsers
        sp.Encode1(game.getVisitorSlot(chr)); // m_nMyPosition
        sp.Encode1(0); // slot (owner)
        sp.EncodeBuffer(RD_AvatarLook.Encode(game.getMCOwner())); // CMiniRoomBaseDlg::DecodeAvatar
        sp.EncodeStr(game.getOwnerName()); // m_asUserID[i]
        sp.Encode2(game.getMCOwner().getJob(), Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.THMS, 87)); // m_anJobCode[i]
        for (SimpleImmutableEntry<Byte, TacosCharacter> visitor : game.getVisitors()) {
            sp.Encode1(visitor.getKey()); // slot
            sp.EncodeBuffer(RD_AvatarLook.Encode(visitor.getValue())); // CMiniRoomBaseDlg::DecodeAvatar
            sp.EncodeStr(visitor.getValue().getName()); // m_asUserID[i]
            sp.Encode2(visitor.getValue().getJob(), Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.THMS, 87)); // m_anJobCode[i]
        }
        sp.Encode1(-1); // end of users
        // COmokDlg::OnEnterResult
        sp.Encode1(0); // slot (owner)
        sp.EncodeBuffer(GW_MiniGameRecord_Encode(game.getMCOwner(), game));
        for (SimpleImmutableEntry<Byte, TacosCharacter> visitor : game.getVisitors()) {
            sp.Encode1(visitor.getKey()); // slot
            sp.EncodeBuffer(GW_MiniGameRecord_Encode(visitor.getValue(), game));
        }
        sp.Encode1(-1); // end of records
        sp.EncodeStr(game.getDescription()); // m_sTitle
        sp.Encode2(game.getPieceType()); // m_nGameKind, m_bTournament
        return sp;
    }

    // test code
    // MRP_EnterResult : CMiniRoomBaseDlg::OnEnterResultBase, COmokDlg::OnEnterResult
    public static ServerPacket MiniRoom_MRP_EnterResult_OmokTest(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);
        byte m_nMyPosition = 0;
        sp.Encode1(OpsMiniRoomProtocol.MRP_EnterResult.get());
        // CMiniRoomBaseDlg::OnEnterResultBase
        sp.Encode1(OpsMiniRoomType.MR_OmokRoom.get());
        sp.Encode1(2); // m_nMaxUsers
        sp.Encode1(m_nMyPosition); // m_nMyPosition
        int visitors = 1;
        // VisitorSlot
        for (int visitor_index = 0; visitor_index < visitors; visitor_index++) {
            boolean isEmployer = false;
            sp.Encode1(visitor_index);
            sp.EncodeBuffer(RD_AvatarLook.Encode(chr)); // CMiniRoomBaseDlg::DecodeAvatar
            sp.EncodeStr(chr.getName());
            if (!isEmployer) {
                sp.Encode2(chr.getJob(), Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 95)); // m_anJobCode[i]
            }

        }
        sp.Encode1(-1); // visiter slot end
        // COmokDlg::OnEnterResult
        {
            sp.Encode1(0);
            // GW_MiniGameRecord::Decode
            {
                if (Config.GreaterOrEqual(Region.THMS, 87)) {
                    sp.EncodeZeroBytes(24);
                } else {
                    sp.EncodeZeroBytes(20);
                }
            }
            sp.Encode1(-1);
            sp.EncodeStr("minigame test"); // m_sTitle
            sp.Encode1(0); // m_nGameKind
            boolean m_bTournament = false;
            sp.Encode1(m_bTournament ? 1 : 0); // m_bTournament
            if (m_bTournament) {
                sp.Encode1(0); // m_nRound
            }
        }
        return sp;
    }

    // test code
    // MRP_EnterResult : CMiniRoomBaseDlg::OnEnterResultBase, CEntrustedShopDlg::OnEnterResult
    public static ServerPacket MiniRoom_MRP_EnterResult_EntrustedShopTest(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        byte m_nMyPosition = 0;

        sp.Encode1(OpsMiniRoomProtocol.MRP_EnterResult.get());
        sp.Encode1(OpsMiniRoomType.MR_EntrustedShop.get());
        sp.Encode1(4); // m_nMaxUsers (max 8)
        sp.Encode1(m_nMyPosition); // m_nMyPosition
        // VisitorSlot
        for (int visitor_index = 0; visitor_index <= 1; visitor_index++) {
            boolean isEmployer = false;
            sp.Encode1(visitor_index); // first slot is employer
            if (1 <= visitor_index) {
                sp.EncodeBuffer(RD_AvatarLook.Encode(chr)); // CMiniRoomBaseDlg::DecodeAvatar
            } else {
                sp.Encode4(5030000); // dwTemplateID
                isEmployer = true;
            }
            sp.EncodeStr(isEmployer ? "Employer" : chr.getName());
            if (!isEmployer) {
                // GMS95
                //sp.Encode2(chr.getJob()); // m_anJobCode[i]
            }
        }
        sp.Encode1(-1); // visiter slot end
        {
            sp.Encode2(0);
            sp.EncodeStr("TestPlayer");
            if (m_nMyPosition == 0) {
                sp.Encode4((16 * 60 * 60 + 52 * 60) * 1000); // 商店終了 / 07:07
                sp.Encode1(0); // 0 = already opened, 1 = open
                int sold_item_count = 3;
                sp.Encode1(sold_item_count);
                for (int i = 0; i < sold_item_count; i++) {
                    sp.Encode4(WzDataStorage.ITEM.getRandom());
                    sp.Encode2(7); // quanty
                    sp.Encode4(123); // price
                    sp.EncodeStr(chr.getName()); // buyer
                }
                sp.Encode4(123456); // 総受付金額 ?_?
            }
            sp.EncodeStr("test message.");
            sp.Encode1(4); // merchant slot
            sp.Encode4(789012); // merchant mesos

            int sale_item_count = 3;
            sp.Encode1(sale_item_count);
            for (int i = 0; i < sale_item_count; i++) {
                MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
                int itemid = WzDataStorage.ITEM.getRandom();
                Item item_gen = (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) ? miip.randomizeStats((Equip) miip.getEquipById(itemid)) : new odin.client.inventory.Item(itemid, (byte) 0, (short) 1, (byte) 0);

                sp.Encode2(1); // bundle
                sp.Encode2(1); // quanty
                sp.Encode4(456); // price
                sp.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item_gen));
            }
        }
        return sp;
    }

    // MRP_Chat : CMiniRoomBaseDlg::OnChat
    public static ServerPacket MiniRoom_MRP_Chat(int slot, String message) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_Chat.get());
        sp.Encode1(OpsMiniRoomProtocol.MRP_UserChat.get()); // MRP_GameMessage or MRP_UserChat
        sp.Encode1(slot);
        sp.EncodeStr(message); // sText
        return sp;
    }

    // MRP_Leave : CMiniRoomBaseDlg::OnLeaveBase
    public static ServerPacket MiniRoom_MRP_Leave(int slot) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_Leave.get());
        sp.Encode1(slot); // nSlot, キャラクターの場所を正しく指定しないとD/C
        return sp;
    }

    // MRP_Leave : CMiniRoomBaseDlg::OnLeaveBase, OnLeave
    public static ServerPacket MiniRoom_MRP_Leave(int slot, OpsMRLeave reason) {
        return MiniRoom_MRP_Leave(slot, reason.get());
    }

    // MRP_Leave : CMiniRoomBaseDlg::OnLeaveBase, OnLeave
    public static ServerPacket MiniRoom_MRP_Leave(int slot, int reason) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_Leave.get());
        sp.Encode1(slot); // nSlot, キャラクターの場所を正しく指定しないとD/C, 0 = 開いている人
        sp.Encode1(reason); // OpsMRLeave, read only when nSlot is m_nMyPosition
        return sp;
    }

    // TRP_PutItem : CTradingRoomDlg::OnPutItem
    public static ServerPacket MiniRoom_TRP_PutItem(byte slot, Item item) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.TRP_PutItem.get());
        sp.Encode1(slot); // user slot
        sp.Encode1(item.getPosition()); // item position
        sp.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item));
        return sp;
    }

    // TRP_PutMoney : CTradingRoomDlg::OnPutMoney
    public static ServerPacket MiniRoom_TRP_PutMoney(byte slot, int meso) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.TRP_PutMoney.get());
        sp.Encode1(slot); // user slot
        sp.Encode4(meso); // m_anMoney[slot]
        return sp;
    }

    // TRP_Trade : CTradingRoomDlg::OnTrade
    public static ServerPacket MiniRoom_TRP_Trade() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.TRP_Trade.get());
        return sp;
    }

    // PSP_Refresh : CPersonalShopDlg::OnRefresh, CEntrustedShopDlg::OnRefresh
    public static ServerPacket MiniRoom_PSP_Refresh(IPlayerShop shop) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.PSP_Refresh.get());
        // CEntrustedShopDlg::OnRefresh
        if (shop.getShopType() == 1) {
            sp.Encode4(0); // m_nMoney
        }
        // CPersonalShopDlg::OnRefresh
        sp.Encode1(shop.getItems().size());
        for (MaplePlayerShopItem item : shop.getItems()) {
            sp.Encode2(item.bundles); // nNumber
            sp.Encode2(item.item.getQuantity()); // nSet
            sp.Encode4(item.price); // nPrice
            sp.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item.item));
        }
        return sp;
    }

    // ESP_DeliverVisitList : CEntrustedShopDlg::OnVisitListResult
    public static ServerPacket MiniRoom_ESP_DeliverVisitList(List<String> visitors) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.ESP_DeliverVisitList.get());
        sp.Encode2(visitors.size());
        for (String visitor : visitors) {
            sp.EncodeStr(visitor); // sName
            sp.Encode4(1);
        }
        return sp;
    }

    // ESP_DeliverBlackList : CEntrustedShopDlg::OnBlackListResult
    public static ServerPacket MiniRoom_ESP_DeliverBlackList(List<String> blackList) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.ESP_DeliverBlackList.get());
        sp.Encode2(blackList.size());
        for (String name : blackList) {
            if (name != null) {
                sp.EncodeStr(name);
            }
        }
        return sp;
    }

    // MGRP_TieRequest : COmokDlg::OnTieRequest
    public static ServerPacket MiniRoom_MGRP_TieRequest() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MGRP_TieRequest.get());
        return sp;
    }

    // MGRP_TieResult : COmokDlg::OnTieResult (denied)
    public static ServerPacket MiniRoom_MGRP_TieResult() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MGRP_TieResult.get());
        return sp;
    }

    // MGRP_LeaveEngage, MGRP_LeaveEngageCancel
    public static ServerPacket MiniRoom_MGRP_LeaveEngage(boolean engage) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(engage ? OpsMiniRoomProtocol.MGRP_LeaveEngage.get() : OpsMiniRoomProtocol.MGRP_LeaveEngageCancel.get());
        return sp;
    }

    // MGRP_Ready, MGRP_CancelReady : COmokDlg::OnUserReady, COmokDlg::OnUserCancelReady
    public static ServerPacket MiniRoom_MGRP_Ready(boolean ready) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(ready ? OpsMiniRoomProtocol.MGRP_Ready.get() : OpsMiniRoomProtocol.MGRP_CancelReady.get());
        return sp;
    }

    // MGRP_Start : COmokDlg::OnUserStart, CMemoryGameDlg::OnUserStart
    public static ServerPacket MiniRoom_MGRP_Start(MapleMiniGame game, int loser) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MGRP_Start.get());
        sp.Encode1(loser == 1 ? 0 : 1); // first turn
        if (game.getGameType() == OpsMiniRoomType.MR_MemoryGameRoom.get()) {
            int count = game.getPieceType() == 1 ? 20 : (game.getPieceType() == 2 ? 30 : 12);
            sp.Encode1(count); // m_nCount
            for (int i = 1; i <= count; i++) {
                sp.Encode4(game.getCardId(i)); // m_anShuffle
            }
        }
        return sp;
    }

    // MGRP_GameResult : COmokDlg::OnGameResult, CMemoryGameDlg::OnGameResult
    public static ServerPacket MiniRoom_MGRP_GameResult(MapleMiniGame game, int type, int x) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MGRP_GameResult.get());
        sp.Encode1(type); // m_nGameResultType, lose = 0, tie = 1, win = 2
        if (type != 1) {
            sp.Encode1(type == 0 ? (x == 1 ? 0 : 1) : x); // m_nWinnerIdx
        }
        sp.EncodeBuffer(GW_MiniGameRecord_Encode(game.getMCOwner(), game));
        for (SimpleImmutableEntry<Byte, TacosCharacter> visitor : game.getVisitors()) {
            sp.EncodeBuffer(GW_MiniGameRecord_Encode(visitor.getValue(), game));
        }
        return sp;
    }

    // MGRP_TimeOver : COmokDlg::OnTimeOver
    public static ServerPacket MiniRoom_MGRP_TimeOver(int slot) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MGRP_TimeOver.get());
        sp.Encode1(slot); // next turn
        return sp;
    }

    // ORP_PutStoneChecker : COmokDlg::OnPutStoneChecker
    public static ServerPacket MiniRoom_ORP_PutStoneChecker(int x, int y, int type) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.ORP_PutStoneChecker.get());
        sp.Encode4(x); // m_pt.x
        sp.Encode4(y); // m_pt.y
        sp.Encode1(type);
        return sp;
    }

    // MGP_TurnUpCard : CMemoryGameDlg::OnTurnUpCard
    public static ServerPacket MiniRoom_MGP_TurnUpCard(int turn, int slot, int firstslot, int type) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MGP_TurnUpCard.get());
        sp.Encode1(turn);
        sp.Encode1(slot);
        if (turn == 0) {
            sp.Encode1(firstslot);
            sp.Encode1(type);
        }
        return sp;
    }

    // GW_MiniGameRecord::Decode
    public static byte[] GW_MiniGameRecord_Encode(TacosCharacter chr, MapleMiniGame game) {
        ServerPacket data = new ServerPacket();

        data.Encode4(game.getGameType());
        data.Encode4(game.getWins(chr));
        data.Encode4(game.getTies(chr));
        data.Encode4(game.getLosses(chr));
        data.Encode4(game.getScore(chr)); // points
        data.Encode4(0, Config.GreaterOrEqual(Region.THMS, 87));
        return data.getBytes();
    }
}
