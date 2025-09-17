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
package packet.response;

import client.MapleCharacter;
import client.inventory.IItem;
import server.network.MaplePacket;
import java.util.List;
import packet.ServerPacket;
import packet.response.struct.TestHelper;
import server.MTSStorage;
import tools.KoreanDateUtil;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCITC {

    // CITC::OnChargeParamResult
    public static final MaplePacket ITCChargeParamResult() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ITCChargeParamResult);

        return sp.get();
    }

    // CITC::OnQueryCashResult
    public static MaplePacket ITCQueryCashResult(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ITCQueryCashResult);

        sp.Encode4(chr.getNexonPoint()); // nNexonCash (signed)
        sp.Encode4(chr.getMaplePoint()); // nMaplePoint (signed)
        return sp.get();
    }

    // CITC::OnNormalItemResult
    public static final MaplePacket ITCNormalItemResult() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ITCNormalItemResult);

        sp.Encode1(0);
        return sp.get();
    }

    public static final MaplePacket getMTSConfirmCancel() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ITCNormalItemResult.get());
        mplew.write(37);
        return mplew.getPacket();
    }

    public static final MaplePacket getMTSWantedListingOver(final int nx, final int items) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ITCNormalItemResult.get());
        mplew.write(61);
        mplew.writeInt(nx);
        mplew.writeInt(items);
        return mplew.getPacket();
    }

    public static final MaplePacket getMTSConfirmSell() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ITCNormalItemResult.get());
        mplew.write(29);
        return mplew.getPacket();
    }

    public static final MaplePacket getMTSFailCancel() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ITCNormalItemResult.get());
        mplew.write(38);
        mplew.write(66);
        return mplew.getPacket();
    }

    public static final void addMTSItemInfo(final MaplePacketLittleEndianWriter mplew, final MTSStorage.MTSItemInfo item) {
        TestHelper.addItemInfo(mplew, item.getItem(), true, true);
        mplew.writeInt(item.getId()); //id
        mplew.writeInt(item.getTaxes()); //this + below = price
        mplew.writeInt(item.getPrice()); //price
        mplew.writeLong(0);
        mplew.writeInt(KoreanDateUtil.getQuestTimestamp(item.getEndingDate()));
        mplew.writeInt(KoreanDateUtil.getQuestTimestamp(item.getEndingDate()));
        mplew.writeMapleAsciiString(item.getSeller()); //account name (what was nexon thinking?)
        mplew.writeMapleAsciiString(item.getSeller()); //char name
        mplew.writeZeroBytes(28);
    }

    public static final MaplePacket getMTSConfirmTransfer(final int quantity, final int pos) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ITCNormalItemResult.get());
        mplew.write(39);
        mplew.writeInt(quantity);
        mplew.writeInt(pos);
        return mplew.getPacket();
    }

    public static final MaplePacket sendMTS(final List<MTSStorage.MTSItemInfo> items, final int tab, final int type, final int page, final int pages) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ITCNormalItemResult.get());
        mplew.write(21); //operation
        mplew.writeInt(pages * 10); //total items
        mplew.writeInt(items.size()); //number of items on this page
        mplew.writeInt(tab);
        mplew.writeInt(type);
        mplew.writeInt(page);
        mplew.write(1);
        mplew.write(1);
        for (MTSStorage.MTSItemInfo item : items) {
            addMTSItemInfo(mplew, item);
        }
        mplew.write(0); //0 or 1?
        return mplew.getPacket();
    }

    public static final MaplePacket getMTSFailBuy() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ITCNormalItemResult.get());
        mplew.write(52);
        mplew.write(66);
        return mplew.getPacket();
    }

    public static final MaplePacket getMTSFailSell() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ITCNormalItemResult.get());
        mplew.write(30);
        mplew.write(66);
        return mplew.getPacket();
    }

    public static final MaplePacket getMTSConfirmBuy() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ITCNormalItemResult.get());
        mplew.write(51);
        return mplew.getPacket();
    }

    public static final MaplePacket addToCartMessage(boolean fail, boolean remove) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ITCNormalItemResult.get());
        if (remove) {
            if (fail) {
                mplew.write(44);
                mplew.writeInt(-1);
            } else {
                mplew.write(43);
            }
        } else {
            if (fail) {
                mplew.write(42);
                mplew.writeInt(-1);
            } else {
                mplew.write(41);
            }
        }
        return mplew.getPacket();
    }

    public static final MaplePacket getTransferInventory(final List<IItem> items, final boolean changed) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ITCNormalItemResult.get());
        mplew.write(33);
        mplew.writeInt(items.size());
        int i = 0;
        for (IItem item : items) {
            TestHelper.addItemInfo(mplew, item, true, true);
            mplew.writeInt(Integer.MAX_VALUE - i); //fake ID
            mplew.writeInt(110);
            mplew.writeInt(1011); //fake
            mplew.writeZeroBytes(48);
            i++;
        }
        mplew.writeInt(-47 + i - 1);
        mplew.write(changed ? 1 : 0);
        return mplew.getPacket();
    }

    public static final MaplePacket getNotYetSoldInv(final List<MTSStorage.MTSItemInfo> items) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ITCNormalItemResult.get());
        mplew.write(35);
        mplew.writeInt(items.size());
        for (MTSStorage.MTSItemInfo item : items) {
            ResCITC.addMTSItemInfo(mplew, item);
        }
        return mplew.getPacket();
    }

}
