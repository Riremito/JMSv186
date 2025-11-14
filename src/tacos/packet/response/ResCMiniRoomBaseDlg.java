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
                // not in JMS.
                if (Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 95)) {
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

}
