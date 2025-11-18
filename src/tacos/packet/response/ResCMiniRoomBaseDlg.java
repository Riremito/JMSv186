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

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.inventory.Equip;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import tacos.wz.ids.DWI_LoadXML;
import java.util.List;
import tacos.network.MaplePacket;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsMiniRoomProtocol;
import tacos.packet.ops.OpsMiniRoomType;
import tacos.packet.response.data.DataAvatarLook;
import tacos.packet.response.data.DataGW_ItemSlotBase;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleTrade;
import odin.server.shops.AbstractPlayerStore;
import odin.server.shops.HiredMerchant;
import odin.server.shops.IMaplePlayerShop;
import odin.server.shops.MapleMiniGame;
import odin.server.shops.MaplePlayerShop;
import odin.server.shops.MaplePlayerShopItem;
import odin.tools.HexTool;
import odin.tools.Pair;
import tacos.config.Region;
import tacos.config.Version;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCMiniRoomBaseDlg {

    public static MaplePacket EnterResultStatic(HiredMerchant hm, MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        byte m_nMyPosition = hm.getVisitorSlot(chr);

        sp.Encode1(OpsMiniRoomProtocol.MRP_EnterResult.get());
        sp.Encode1(OpsMiniRoomType.MR_EntrustedShop.get());
        sp.Encode1(hm.getMaxSize()); // m_nMaxUsers (max 8)
        sp.Encode1(m_nMyPosition); // m_nMyPosition
        List<Pair<Byte, MapleCharacter>> visitors = hm.getVisitors();
        // VisitorSlot
        for (int visitor_index = 0; visitor_index <= visitors.size(); visitor_index++) {
            boolean isEmployer = false;
            sp.Encode1(visitor_index == 0 ? visitor_index : visitors.get(visitor_index).left); // first slot is employer
            if (1 <= visitor_index) {
                sp.EncodeBuffer(DataAvatarLook.Encode(visitors.get(visitor_index).right)); // CMiniRoomBaseDlg::DecodeAvatar
            } else {
                sp.Encode4(hm.getItemId()); // dwTemplateID
                isEmployer = true;
            }
            sp.EncodeStr(isEmployer ? "Employer" : visitors.get(visitor_index).right.getName());
            if (!isEmployer) {
                // GMS95
                //sp.Encode2(chr.getJob()); // m_anJobCode[i]
            }
        }
        sp.Encode1(-1); // visiter slot end

        // CEntrustedShopDlg::OnEnterResult
        {
            sp.Encode2(0);
            sp.EncodeStr(hm.getOwnerName());
            if (m_nMyPosition == 0) {
                sp.Encode4((16 * 60 * 60 + 52 * 60) * 1000); // 商店終了 / 07:07
                sp.Encode1(0); // 0 = already opened, 1 = open
                sp.Encode1(hm.getBoughtItems().size());
                for (AbstractPlayerStore.BoughtItem item_sold : hm.getBoughtItems()) {
                    sp.Encode4(item_sold.id);
                    sp.Encode2(item_sold.quantity); // quanty
                    sp.Encode4(item_sold.totalPrice); // price
                    sp.EncodeStr(item_sold.buyer); // buyer
                }
                sp.Encode4(hm.getMeso()); // 総受付金額 ?_?
            }
            sp.EncodeStr(hm.getDescription());
            sp.Encode1(4); // merchant slot
            sp.Encode4(hm.getMeso()); // merchant mesos
            sp.Encode1(hm.getItems().size());
            for (MaplePlayerShopItem mpsi : hm.getItems()) {
                sp.Encode2(mpsi.bundles); // bundle
                sp.Encode2(mpsi.item.getQuantity()); // quanty
                sp.Encode4(mpsi.price); // price
                sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(mpsi.item));
            }
        }
        return sp.get();
    }

    // CMiniRoomBaseDlg::OnEnterResultStatic
    public static MaplePacket EnterResultStaticOmokTest(MapleCharacter chr) {
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
            // CMiniRoomBaseDlg::DecodeAvatar
            sp.EncodeBuffer(DataAvatarLook.Encode(chr)); // CMiniRoomBaseDlg::DecodeAvatar
            sp.EncodeStr(chr.getName());
            if (!isEmployer) {
                if (Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 95)) {
                    sp.Encode2(chr.getJob()); // m_anJobCode[i]
                }
            }

        }
        sp.Encode1(-1); // visiter slot end
        // COmokDlg::OnEnterResult
        {
            sp.Encode1(0);
            // GW_MiniGameRecord::Decode
            {
                if (Version.GreaterOrEqual(Region.THMS, 87)) {
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
        return sp.get();
    }

    public static MaplePacket EnterResultStaticTest(MapleCharacter chr) {
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
                sp.EncodeBuffer(DataAvatarLook.Encode(chr)); // CMiniRoomBaseDlg::DecodeAvatar
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
                    sp.Encode4(DWI_LoadXML.getItem().getRandom());
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
                int itemid = DWI_LoadXML.getItem().getRandom();
                IItem item_gen = (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) ? miip.randomizeStats((Equip) miip.getEquipById(itemid)) : new odin.client.inventory.Item(itemid, (byte) 0, (short) 1, (byte) 0);

                sp.Encode2(1); // bundle
                sp.Encode2(1); // quanty
                sp.Encode4(456); // price
                sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(item_gen));
            }
        }
        return sp.get();
    }

    public static MaplePacket getTradeInvite(MapleCharacter c, boolean isPointTrade) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_Invite.get());
        sp.Encode1(isPointTrade ? 6 : 3);
        sp.EncodeStr(c.getName());
        sp.Encode4(0); // Trade ID
        return sp.get();
    }

    public static MaplePacket getTradePartnerAdd(MapleCharacter c) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_Enter.get());
        sp.Encode1(1);
        sp.EncodeBuffer(DataAvatarLook.Encode(c));
        sp.EncodeStr(c.getName());
        if (Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.THMS, 87) || Version.PostBB()) {
            sp.Encode2(c.getJob());
        }
        return sp.get();
    }

    public static MaplePacket getPlayerShopNewVisitor(MapleCharacter c, int slot) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.EncodeBuffer(HexTool.getByteArrayFromHexString("04 0" + slot));
        sp.EncodeBuffer(DataAvatarLook.Encode(c));
        sp.EncodeStr(c.getName());
        if (Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.THMS, 87) || Version.PostBB()) {
            sp.Encode2(c.getJob());
        }
        return sp.get();
    }

    public static MaplePacket getTradeItemAdd(byte number, IItem item) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.TRP_PutItem.get());
        sp.Encode1(number);
        sp.Encode1(item.getPosition());
        sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(item));
        return sp.get();
    }

    public static MaplePacket getTradeConfirmation() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.TRP_Trade.get());
        return sp.get();
    }

    public static MaplePacket TradeMessage(final byte UserSlot, final byte message) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_Leave.get());
        sp.Encode1(UserSlot);
        sp.Encode1(message);
        //0x02 = cancelled
        //0x07 = success [tax is automated]
        //0x08 = unsuccessful
        //0x09 = "You cannot make the trade because there are some items which you cannot carry more than one."
        //0x0A = "You cannot make the trade because the other person's on a different map."
        return sp.get();
    }

    public static MaplePacket getPlayerShopRemoveVisitor(int slot) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.EncodeBuffer(HexTool.getByteArrayFromHexString("0A 0" + slot));
        return sp.get();
    }

    public static MaplePacket getTradeStart(MapleClient c, MapleTrade trade, byte number, boolean isPointTrade) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_EnterResult.get());
        sp.Encode1(isPointTrade ? 6 : 3);
        sp.Encode1(2);
        sp.Encode1(number);
        if (number == 1) {
            sp.Encode1(0);
            sp.EncodeBuffer(DataAvatarLook.Encode(trade.getPartner().getChr()));
            sp.EncodeStr(trade.getPartner().getChr().getName());
            if (Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.THMS, 87) || Version.PostBB()) {
                sp.Encode2(trade.getPartner().getChr().getJob());
            }
        }
        sp.Encode1(number);
        sp.EncodeBuffer(DataAvatarLook.Encode(c.getPlayer()));
        sp.EncodeStr(c.getPlayer().getName());
        if (Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.THMS, 87) || Version.PostBB()) {
            sp.Encode2(c.getPlayer().getJob());
        }
        sp.Encode1(-1);
        return sp.get();
    }

    public static MaplePacket getTradeCancel(final byte UserSlot, final int unsuccessful) {
        //0 = canceled 1 = invent space 2 = pickuprestricted
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_Leave.get());
        sp.Encode1(UserSlot);
        sp.Encode1(unsuccessful == 0 ? 2 : (unsuccessful == 1 ? 8 : 9));
        return sp.get();
    }

    public static MaplePacket getTradeMesoSet(byte number, int meso) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.TRP_PutMoney.get());
        sp.Encode1(number);
        sp.Encode4(meso);
        return sp.get();
    }

    public static MaplePacket shopBlockPlayer(final byte slot) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_Leave.get());
        // キャラクターの場所を正しく指定しないとD/C
        sp.Encode1(slot);
        // 強制退場されました。
        sp.Encode1(5);
        return sp.get();
    }

    public static MaplePacket shopErrorMessage(final int error, final int type) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_Leave.get());
        // 退場する人の番号 0 = 開いている人
        sp.Encode1(type);
        /*
        商店を閉じる理由
        0   =   なし
        1   =   ここではオープン出来ません。
        2   =   なし
        3   =   商店が閉じています
        4   =   なし
        5   =   強制退場されました。
        6   =   制限時間が経過し、商店を開くことができませんした
        7   =   なし
        8   =   なし
        9   =   なし
        10  =   なし
        11  =   なし
        12  =   なし
        13  =   なし
        14  =   品物は売れ切れです。
         */
        sp.Encode1(error);
        return sp.get();
    }

    public static MaplePacket MerchantBlackListView(final List<String> blackList) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.ESP_DeliverBlackList.get());
        sp.Encode2(blackList.size());
        for (int i = 0; i < blackList.size(); i++) {
            if (blackList.get(i) != null) {
                sp.EncodeStr(blackList.get(i));
            }
        }
        return sp.get();
    }

    public static MaplePacket MerchantVisitorView(List<String> visitor) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.ESP_DeliverVisitList.get());
        sp.Encode2(visitor.size());
        for (String visit : visitor) {
            sp.EncodeStr(visit);
            sp.Encode4(1); /////for the lul
        }
        return sp.get();
    }

    public static MaplePacket getMiniGameRequestTie() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MGRP_TieResult.get());
        return sp.get();
    }

    public static MaplePacket getMatchCardSelect(int turn, int slot, int firstslot, int type) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MGP_TurnUpCard.get());
        sp.Encode1(turn);
        sp.Encode1(slot);
        if (turn == 0) {
            sp.Encode1(firstslot);
            sp.Encode1(type);
        }
        return sp.get();
    }

    public static MaplePacket getMiniGameExitAfter(boolean ready) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(ready ? OpsMiniRoomProtocol.MGRP_LeaveEngage.get() : OpsMiniRoomProtocol.MGRP_LeaveEngageCancel.get());
        return sp.get();
    }

    public static final MaplePacket shopMessage(final int type) {
        // show when closed the shop
        final ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);
        // 0x28 = All of your belongings are moved successfully.

        sp.Encode1(type); // hard coded.
        sp.Encode1(0);
        return sp.get();
    }

    public static final MaplePacket getHiredMerch(final MapleCharacter chr, final HiredMerchant merch, final boolean firstTime) {
        final ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(5);
        sp.Encode1(5);
        sp.Encode1(4);
        sp.Encode2(merch.getVisitorSlot(chr));
        sp.Encode4(merch.getItemId());
        sp.EncodeStr("\u96c7\u7528\u5546\u4eba");
        for (final Pair<Byte, MapleCharacter> storechr : merch.getVisitors()) {
            sp.Encode1(storechr.left);
            sp.EncodeBuffer(DataAvatarLook.Encode(storechr.right));
            sp.EncodeStr(storechr.right.getName());
            if (Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.THMS, 87) || Version.PostBB()) {
                sp.Encode2(storechr.right.getJob());
            }
        }
        sp.Encode1(-1);
        sp.Encode2(0);
        sp.EncodeStr(merch.getOwnerName());
        if (merch.isOwner(chr)) {
            sp.Encode4(merch.getTimeLeft());
            sp.Encode1(firstTime ? 1 : 0);
            sp.Encode1(merch.getBoughtItems().size());
            for (AbstractPlayerStore.BoughtItem SoldItem : merch.getBoughtItems()) {
                sp.Encode4(SoldItem.id);
                sp.Encode2(SoldItem.quantity); // number of purchased
                sp.Encode4(SoldItem.totalPrice); // total price
                sp.EncodeStr(SoldItem.buyer); // name of the buyer
            }
            sp.Encode4(merch.getMeso());
        }
        sp.EncodeStr(merch.getDescription());
        sp.Encode1(10);
        sp.Encode4(merch.getMeso()); // meso
        sp.Encode1(merch.getItems().size());
        for (final MaplePlayerShopItem item : merch.getItems()) {
            sp.Encode2(item.bundles);
            sp.Encode2(item.item.getQuantity());
            sp.Encode4(item.price);
            sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(item.item));
        }
        return sp.get();
    }

    public static final MaplePacket shopVisitorAdd(final MapleCharacter chr, final int slot) {
        final ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(4);
        sp.Encode1(slot);
        sp.EncodeBuffer(DataAvatarLook.Encode(chr));
        sp.EncodeStr(chr.getName());
        if (Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.THMS, 87) || Version.PostBB()) {
            sp.Encode2(chr.getJob());
        }
        return sp.get();
    }

    // 雇用商人 整理中 (他人用)
    public static MaplePacket MaintenanceHiredMerchant(int slot) {
        final ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        // UIを閉じる
        sp.Encode1(10);
        // キャラクターを指定
        sp.Encode1(slot);
        // UI閉じるときのメッセージ（何も表示しない設定)
        sp.Encode1(17);
        return sp.get();
    }

    public static MaplePacket getMiniGame(MapleClient c, MapleMiniGame minigame) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_EnterResult.get());
        sp.Encode1(minigame.getGameType());
        sp.Encode1(minigame.getMaxSize());
        sp.Encode2(minigame.getVisitorSlot(c.getPlayer()));
        sp.EncodeBuffer(DataAvatarLook.Encode(minigame.getMCOwner()));
        sp.EncodeStr(minigame.getOwnerName());
        if (Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.THMS, 87) || Version.PostBB()) {
            sp.Encode2(minigame.getMCOwner().getJob());
        }
        for (Pair<Byte, MapleCharacter> visitorz : minigame.getVisitors()) {
            sp.Encode1(visitorz.getLeft());
            sp.EncodeBuffer(DataAvatarLook.Encode(visitorz.getRight()));
            sp.EncodeStr(visitorz.getRight().getName());
            if (Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.THMS, 87) || Version.PostBB()) {
                sp.Encode2(visitorz.getRight().getJob());
            }
        }
        sp.Encode1(-1);
        sp.Encode1(0);
        sp.EncodeBuffer(GW_MiniGameRecord_Encode(minigame.getMCOwner(), minigame));
        for (Pair<Byte, MapleCharacter> visitorz : minigame.getVisitors()) {
            sp.Encode1(visitorz.getLeft());
            sp.EncodeBuffer(GW_MiniGameRecord_Encode(visitorz.getRight(), minigame));
        }
        sp.Encode1(-1);
        sp.EncodeStr(minigame.getDescription());
        sp.Encode2(minigame.getPieceType());
        return sp.get();
    }

    public static MaplePacket getMiniGameFull() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_EnterResult.get());
        sp.Encode1(0);
        sp.Encode1(2);
        return sp.get();
    }

    public static final MaplePacket shopChat(final String message, final int slot) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_Chat.get());
        sp.Encode1(8);
        sp.Encode1(slot);
        sp.EncodeStr(message);
        return sp.get();
    }

    public static final MaplePacket getPlayerStore(final MapleCharacter chr, final boolean firstTime) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        IMaplePlayerShop ips = chr.getPlayerShop();
        switch (ips.getShopType()) {
            case 2:
                sp.Encode1(OpsMiniRoomProtocol.MRP_EnterResult.get());
                sp.Encode1(OpsMiniRoomType.MR_PersonalShop.get());
                sp.Encode1(4);
                break;
            case 3:
                sp.Encode1(OpsMiniRoomProtocol.MRP_EnterResult.get());
                sp.Encode1(OpsMiniRoomType.MR_MemoryGameRoom.get());
                sp.Encode1(2);
                break;
            case 4:
                sp.Encode1(OpsMiniRoomProtocol.MRP_EnterResult.get());
                sp.Encode1(OpsMiniRoomType.MR_OmokRoom.get());
                sp.Encode1(2);
                break;
        }

        sp.Encode2(ips.getVisitorSlot(chr));
        sp.EncodeBuffer(DataAvatarLook.Encode(((MaplePlayerShop) ips).getMCOwner()));
        sp.EncodeStr(ips.getOwnerName());
        if (Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.THMS, 87) || Version.PostBB()) {
            sp.Encode2(((MaplePlayerShop) ips).getMCOwner().getJob());
        }
        for (final Pair<Byte, MapleCharacter> storechr : ips.getVisitors()) {
            sp.Encode1(storechr.left);
            sp.EncodeBuffer(DataAvatarLook.Encode(storechr.right));
            sp.EncodeStr(storechr.right.getName());
            if (Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.THMS, 87) || Version.PostBB()) {
                sp.Encode2(storechr.right.getJob());
            }
        }
        sp.Encode1(-1);
        sp.EncodeStr(ips.getDescription());
        sp.Encode1(10);
        sp.Encode1(ips.getItems().size());
        for (final MaplePlayerShopItem item : ips.getItems()) {
            sp.Encode2(item.bundles);
            sp.Encode2(item.item.getQuantity());
            sp.Encode4(item.price);
            sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(item.item));
        }
        return sp.get();
    }

    public static final MaplePacket Merchant_Buy_Error(final byte message) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);
        // 2 = You have not enough meso

        sp.Encode1(OpsMiniRoomProtocol.PSP_MoveItemToInventory.get());
        sp.Encode1(message);
        return sp.get();
    }

    public static MaplePacket getMiniGameClose(byte number) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_Leave.get());
        sp.Encode1(1);
        sp.Encode1(number);
        return sp.get();
    }

    public static final MaplePacket shopItemUpdate(final IMaplePlayerShop shop) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.PSP_Refresh.get());
        if (shop.getShopType() == 1) {
            sp.Encode4(0);
        }
        sp.Encode1(shop.getItems().size());
        for (final MaplePlayerShopItem item : shop.getItems()) {
            sp.Encode2(item.bundles);
            sp.Encode2(item.item.getQuantity());
            sp.Encode4(item.price);
            sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(item.item));
        }
        return sp.get();
    }

    public static final MaplePacket shopVisitorLeave(final byte slot) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_Leave.get());
        sp.Encode1(slot);
        return sp.get();
    }

    public static MaplePacket getMiniGameSkip(int slot) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MGRP_TimeOver.get());
        //owner = 1 visitor = 0?
        sp.Encode1(slot);
        return sp.get();
    }

    public static MaplePacket getMiniGameResult(MapleMiniGame game, int type, int x) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MGRP_GameResult.get());
        sp.Encode1(type); //lose = 0, tie = 1, win = 2
        game.setPoints(x, type);
        if (type != 0) {
            game.setPoints(x == 1 ? 0 : 1, type == 2 ? 0 : 1);
        }
        if (type != 1) {
            if (type == 0) {
                sp.Encode1(x == 1 ? 0 : 1); //who did it?
            } else {
                sp.Encode1(x);
            }
        }
        sp.EncodeBuffer(GW_MiniGameRecord_Encode(game.getMCOwner(), game));
        for (Pair<Byte, MapleCharacter> visitorz : game.getVisitors()) {
            sp.EncodeBuffer(GW_MiniGameRecord_Encode(visitorz.right, game));
        }
        return sp.get();
    }

    public static MaplePacket getMatchCardStart(MapleMiniGame game, int loser) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MGRP_Start.get());
        sp.Encode1(loser == 1 ? 0 : 1);
        int times = game.getPieceType() == 1 ? 20 : (game.getPieceType() == 2 ? 30 : 12);
        sp.Encode1(times);
        for (int i = 1; i <= times; i++) {
            sp.Encode4(game.getCardId(i));
        }
        return sp.get();
    }

    public static MaplePacket getMiniGameDenyTie() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MGRP_GiveUpRequest.get());
        return sp.get();
    }

    // 雇用商人 閉店
    public static MaplePacket CloseHiredMerchant() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        // UIを閉じる
        sp.Encode1(OpsMiniRoomProtocol.MRP_Leave.get());
        // 自分のキャラクターを指定
        sp.Encode1(0);
        // UI閉じるときのメッセージ（何も表示しない設定)
        sp.Encode1(20);
        return sp.get();
    }

    public static MaplePacket getMiniGameMoveOmok(int move1, int move2, int move3) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.ORP_PutStoneChecker.get());
        sp.Encode4(move1);
        sp.Encode4(move2);
        sp.Encode1(move3);
        return sp.get();
    }

    public static MaplePacket getMiniGameNewVisitor(MapleCharacter c, int slot, MapleMiniGame game) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MRP_Enter.get());
        sp.Encode1(slot);
        sp.EncodeBuffer(DataAvatarLook.Encode(c));
        sp.EncodeStr(c.getName());
        if (Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.THMS, 87) || Version.PostBB()) {
            sp.Encode2(c.getJob());
        }
        sp.EncodeBuffer(GW_MiniGameRecord_Encode(c, game));
        return sp.get();
    }

    public static MaplePacket getMiniGameStart(int loser) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(OpsMiniRoomProtocol.MGRP_Start.get());
        sp.Encode1(loser == 1 ? 0 : 1);
        return sp.get();
    }

    public static MaplePacket getMiniGameReady(boolean ready) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniRoom);

        sp.Encode1(ready ? OpsMiniRoomProtocol.MGRP_Ready.get() : OpsMiniRoomProtocol.MGRP_CancelReady.get());
        return sp.get();
    }

    // GW_MiniGameRecord::Decode
    public static byte[] GW_MiniGameRecord_Encode(MapleCharacter chr, MapleMiniGame game) {
        ServerPacket data = new ServerPacket();
        data.Encode4(game.getGameType());
        data.Encode4(game.getWins(chr));
        data.Encode4(game.getTies(chr));
        data.Encode4(game.getLosses(chr));
        data.Encode4(game.getScore(chr)); // points
        if (Version.GreaterOrEqual(Region.THMS, 87)) {
            data.Encode4(0);
        }
        return data.get().getBytes();
    }

}
