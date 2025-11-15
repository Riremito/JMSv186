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
import tacos.packet.response.struct.TestHelper;
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
import odin.tools.data.output.MaplePacketLittleEndianWriter;
import tacos.config.Region;
import tacos.config.Version;

/**
 *
 * @author Riremito
 */
public class ResCMiniRoomBaseDlg {

    public static MaplePacket EnterResultStatic(HiredMerchant hm, MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MiniRoom);

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
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MiniRoom);
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
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MiniRoom);

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
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(2);
        mplew.write(isPointTrade ? 6 : 3);
        mplew.writeMapleAsciiString(c.getName());
        mplew.writeInt(0); // Trade ID
        return mplew.getPacket();
    }

    public static MaplePacket getTradePartnerAdd(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(4);
        mplew.write(1);
        TestHelper.addCharLook(mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        mplew.writeShort(c.getJob());
        return mplew.getPacket();
    }

    public static MaplePacket getPlayerShopNewVisitor(MapleCharacter c, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(HexTool.getByteArrayFromHexString("04 0" + slot));
        TestHelper.addCharLook(mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        mplew.writeShort(c.getJob());
        return mplew.getPacket();
    }

    public static MaplePacket getTradeItemAdd(byte number, IItem item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        //mplew.write(0xE);
        mplew.write(13);
        mplew.write(number);
        TestHelper.addItemInfo(mplew, item, false, false, true);
        return mplew.getPacket();
    }

    public static MaplePacket getTradeConfirmation() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        //mplew.write(0x10); //or 7? what
        mplew.write(15);
        return mplew.getPacket();
    }

    public static MaplePacket TradeMessage(final byte UserSlot, final byte message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(10);
        mplew.write(UserSlot);
        mplew.write(message);
        //0x02 = cancelled
        //0x07 = success [tax is automated]
        //0x08 = unsuccessful
        //0x09 = "You cannot make the trade because there are some items which you cannot carry more than one."
        //0x0A = "You cannot make the trade because the other person's on a different map."
        return mplew.getPacket();
    }

    public static MaplePacket getPlayerShopRemoveVisitor(int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(HexTool.getByteArrayFromHexString("0A 0" + slot));
        return mplew.getPacket();
    }

    public static MaplePacket getTradeStart(MapleClient c, MapleTrade trade, byte number, boolean isPointTrade) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(5);
        mplew.write(isPointTrade ? 6 : 3);
        mplew.write(2);
        mplew.write(number);
        if (number == 1) {
            mplew.write(0);
            TestHelper.addCharLook(mplew, trade.getPartner().getChr(), false);
            mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
            mplew.writeShort(trade.getPartner().getChr().getJob());
        }
        mplew.write(number);
        TestHelper.addCharLook(mplew, c.getPlayer(), false);
        mplew.writeMapleAsciiString(c.getPlayer().getName());
        mplew.writeShort(c.getPlayer().getJob());
        mplew.write(255);
        return mplew.getPacket();
    }

    public static MaplePacket getTradeCancel(final byte UserSlot, final int unsuccessful) {
        //0 = canceled 1 = invent space 2 = pickuprestricted
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(10);
        mplew.write(UserSlot);
        mplew.write(unsuccessful == 0 ? 2 : (unsuccessful == 1 ? 8 : 9));
        return mplew.getPacket();
    }

    public static MaplePacket getTradeMesoSet(byte number, int meso) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        //mplew.write(0xF);
        mplew.write(14);
        mplew.write(number);
        mplew.writeInt(meso);
        return mplew.getPacket();
    }

    public static final MaplePacket shopBlockPlayer(final byte slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(10);
        // キャラクターの場所を正しく指定しないとD/C
        mplew.write(slot);
        // 強制退場されました。
        mplew.write(5);
        return mplew.getPacket();
    }

    public static final MaplePacket shopErrorMessage(final int error, final int type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(10);
        // 退場する人の番号 0 = 開いている人
        mplew.write(type);
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
        mplew.write(error);
        return mplew.getPacket();
    }

    public static final MaplePacket MerchantBlackListView(final List<String> blackList) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(44); // 45
        mplew.writeShort(blackList.size());
        for (int i = 0; i < blackList.size(); i++) {
            if (blackList.get(i) != null) {
                mplew.writeMapleAsciiString(blackList.get(i));
            }
        }
        return mplew.getPacket();
    }

    public static final MaplePacket MerchantVisitorView(List<String> visitor) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(43); // 44
        mplew.writeShort(visitor.size());
        for (String visit : visitor) {
            mplew.writeMapleAsciiString(visit);
            mplew.writeInt(1); /////for the lul
        }
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameRequestTie() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(48);
        return mplew.getPacket();
    }

    public static MaplePacket getMatchCardSelect(int turn, int slot, int firstslot, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(65);
        mplew.write(turn);
        mplew.write(slot);
        if (turn == 0) {
            mplew.write(firstslot);
            mplew.write(type);
        }
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameExitAfter(boolean ready) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(ready ? 53 : 54);
        return mplew.getPacket();
    }

    public static final MaplePacket shopMessage(final int type) {
        // show when closed the shop
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 0x28 = All of your belongings are moved successfully.
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(type);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static final MaplePacket getHiredMerch(final MapleCharacter chr, final HiredMerchant merch, final boolean firstTime) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(5);
        mplew.write(5);
        mplew.write(4);
        mplew.writeShort(merch.getVisitorSlot(chr));
        mplew.writeInt(merch.getItemId());
        mplew.writeMapleAsciiString("\u96c7\u7528\u5546\u4eba");
        for (final Pair<Byte, MapleCharacter> storechr : merch.getVisitors()) {
            mplew.write(storechr.left);
            TestHelper.addCharLook(mplew, storechr.right, false);
            mplew.writeMapleAsciiString(storechr.right.getName());
            mplew.writeShort(storechr.right.getJob());
        }
        mplew.write(-1);
        mplew.writeShort(0);
        mplew.writeMapleAsciiString(merch.getOwnerName());
        if (merch.isOwner(chr)) {
            mplew.writeInt(merch.getTimeLeft());
            mplew.write(firstTime ? 1 : 0);
            mplew.write(merch.getBoughtItems().size());
            for (AbstractPlayerStore.BoughtItem SoldItem : merch.getBoughtItems()) {
                mplew.writeInt(SoldItem.id);
                mplew.writeShort(SoldItem.quantity); // number of purchased
                mplew.writeInt(SoldItem.totalPrice); // total price
                mplew.writeMapleAsciiString(SoldItem.buyer); // name of the buyer
            }
            mplew.writeInt(merch.getMeso());
        }
        mplew.writeMapleAsciiString(merch.getDescription());
        mplew.write(10);
        mplew.writeInt(merch.getMeso()); // meso
        mplew.write(merch.getItems().size());
        for (final MaplePlayerShopItem item : merch.getItems()) {
            mplew.writeShort(item.bundles);
            mplew.writeShort(item.item.getQuantity());
            mplew.writeInt(item.price);
            TestHelper.addItemInfo(mplew, item.item, true, true);
        }
        return mplew.getPacket();
    }

    public static final MaplePacket shopVisitorAdd(final MapleCharacter chr, final int slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(4);
        mplew.write(slot);
        TestHelper.addCharLook(mplew, chr, false);
        mplew.writeMapleAsciiString(chr.getName());
        mplew.writeShort(chr.getJob());
        return mplew.getPacket();
    }

    // 雇用商人 整理中 (他人用)
    public static MaplePacket MaintenanceHiredMerchant(int slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        // UIを閉じる
        mplew.write(10);
        // キャラクターを指定
        mplew.write(slot);
        // UI閉じるときのメッセージ（何も表示しない設定)
        mplew.write(17);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGame(MapleClient c, MapleMiniGame minigame) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(5);
        mplew.write(minigame.getGameType());
        mplew.write(minigame.getMaxSize());
        mplew.writeShort(minigame.getVisitorSlot(c.getPlayer()));
        TestHelper.addCharLook(mplew, minigame.getMCOwner(), false);
        mplew.writeMapleAsciiString(minigame.getOwnerName());
        mplew.writeShort(minigame.getMCOwner().getJob());
        for (Pair<Byte, MapleCharacter> visitorz : minigame.getVisitors()) {
            mplew.write(visitorz.getLeft());
            TestHelper.addCharLook(mplew, visitorz.getRight(), false);
            mplew.writeMapleAsciiString(visitorz.getRight().getName());
            mplew.writeShort(visitorz.getRight().getJob());
        }
        mplew.write(-1);
        mplew.write(0);
        addGameInfo(mplew, minigame.getMCOwner(), minigame);
        for (Pair<Byte, MapleCharacter> visitorz : minigame.getVisitors()) {
            mplew.write(visitorz.getLeft());
            addGameInfo(mplew, visitorz.getRight(), minigame);
        }
        mplew.write(-1);
        mplew.writeMapleAsciiString(minigame.getDescription());
        mplew.writeShort(minigame.getPieceType());
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameFull() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.writeShort(5);
        mplew.write(2);
        return mplew.getPacket();
    }

    public static final MaplePacket shopChat(final String message, final int slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(6);
        mplew.write(8);
        mplew.write(slot);
        mplew.writeMapleAsciiString(message);
        return mplew.getPacket();
    }

    public static final MaplePacket getPlayerStore(final MapleCharacter chr, final boolean firstTime) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        IMaplePlayerShop ips = chr.getPlayerShop();
        switch (ips.getShopType()) {
            case 2:
                mplew.write(5);
                mplew.write(4);
                mplew.write(4);
                break;
            case 3:
                mplew.write(5);
                mplew.write(2);
                mplew.write(2);
                break;
            case 4:
                mplew.write(5);
                mplew.write(1);
                mplew.write(2);
                break;
        }
        mplew.writeShort(ips.getVisitorSlot(chr));
        TestHelper.addCharLook(mplew, ((MaplePlayerShop) ips).getMCOwner(), false);
        mplew.writeMapleAsciiString(ips.getOwnerName());
        mplew.writeShort(((MaplePlayerShop) ips).getMCOwner().getJob());
        for (final Pair<Byte, MapleCharacter> storechr : ips.getVisitors()) {
            mplew.write(storechr.left);
            TestHelper.addCharLook(mplew, storechr.right, false);
            mplew.writeMapleAsciiString(storechr.right.getName());
            mplew.writeShort(storechr.right.getJob());
        }
        mplew.write(255);
        mplew.writeMapleAsciiString(ips.getDescription());
        mplew.write(10);
        mplew.write(ips.getItems().size());
        for (final MaplePlayerShopItem item : ips.getItems()) {
            mplew.writeShort(item.bundles);
            mplew.writeShort(item.item.getQuantity());
            mplew.writeInt(item.price);
            TestHelper.addItemInfo(mplew, item.item, true, true);
        }
        return mplew.getPacket();
    }

    public static final MaplePacket Merchant_Buy_Error(final byte message) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 2 = You have not enough meso
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(24);
        mplew.write(message);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameClose(byte number) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(10);
        mplew.write(1);
        mplew.write(number);
        return mplew.getPacket();
    }

    public static final MaplePacket shopItemUpdate(final IMaplePlayerShop shop) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(22); // 0x17
        if (shop.getShopType() == 1) {
            mplew.writeInt(0);
        }
        mplew.write(shop.getItems().size());
        for (final MaplePlayerShopItem item : shop.getItems()) {
            mplew.writeShort(item.bundles);
            mplew.writeShort(item.item.getQuantity());
            mplew.writeInt(item.price);
            TestHelper.addItemInfo(mplew, item.item, true, true);
        }
        return mplew.getPacket();
    }

    public static final MaplePacket shopVisitorLeave(final byte slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(10);
        mplew.write(slot);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameSkip(int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(60);
        //owner = 1 visitor = 0?
        mplew.write(slot);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameResult(MapleMiniGame game, int type, int x) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(59);
        mplew.write(type); //lose = 0, tie = 1, win = 2
        game.setPoints(x, type);
        if (type != 0) {
            game.setPoints(x == 1 ? 0 : 1, type == 2 ? 0 : 1);
        }
        if (type != 1) {
            if (type == 0) {
                mplew.write(x == 1 ? 0 : 1); //who did it?
            } else {
                mplew.write(x);
            }
        }
        addGameInfo(mplew, game.getMCOwner(), game);
        for (Pair<Byte, MapleCharacter> visitorz : game.getVisitors()) {
            addGameInfo(mplew, visitorz.right, game);
        }
        return mplew.getPacket();
    }

    public static MaplePacket getMatchCardStart(MapleMiniGame game, int loser) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(58);
        mplew.write(loser == 1 ? 0 : 1);
        int times = game.getPieceType() == 1 ? 20 : (game.getPieceType() == 2 ? 30 : 12);
        mplew.write(times);
        for (int i = 1; i <= times; i++) {
            mplew.writeInt(game.getCardId(i));
        }
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameDenyTie() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(49);
        return mplew.getPacket();
    }

    // 雇用商人 閉店
    public static MaplePacket CloseHiredMerchant() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        // UIを閉じる
        mplew.write(10);
        // 自分のキャラクターを指定
        mplew.write(0);
        // UI閉じるときのメッセージ（何も表示しない設定)
        mplew.write(20);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameMoveOmok(int move1, int move2, int move3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(61);
        mplew.writeInt(move1);
        mplew.writeInt(move2);
        mplew.write(move3);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameNewVisitor(MapleCharacter c, int slot, MapleMiniGame game) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(4);
        mplew.write(slot);
        TestHelper.addCharLook(mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        mplew.writeShort(c.getJob());
        addGameInfo(mplew, c, game);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameStart(int loser) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(58);
        mplew.write(loser == 1 ? 0 : 1);
        return mplew.getPacket();
    }

    public static void addGameInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, MapleMiniGame game) {
        mplew.writeInt(game.getGameType()); // start of visitor; unknown
        mplew.writeInt(game.getWins(chr));
        mplew.writeInt(game.getTies(chr));
        mplew.writeInt(game.getLosses(chr));
        mplew.writeInt(game.getScore(chr)); // points
    }

    public static MaplePacket getMiniGameReady(boolean ready) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.get());
        mplew.write(ready ? 55 : 56);
        return mplew.getPacket();
    }

}
