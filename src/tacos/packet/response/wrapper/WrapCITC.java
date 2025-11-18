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
package tacos.packet.response.wrapper;

import odin.client.inventory.IItem;
import java.util.List;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsITC;
import tacos.packet.ops.arg.ArgITCNormalItemResult;
import tacos.packet.response.ResCITC;
import odin.server.MTSStorage;
import tacos.network.MaplePacket;
import odin.tools.KoreanDateUtil;
import tacos.packet.ServerPacketHeader;
import tacos.packet.response.data.DataGW_ItemSlotBase;

/**
 *
 * @author Riremito
 */
public class WrapCITC {

    public static final MaplePacket getMTSFailCancel() {
        ArgITCNormalItemResult arg = new ArgITCNormalItemResult();
        arg.ops_res = OpsITC.ITCRes_CancelSaleItem_Failed;
        arg.ops_fail_reason = OpsITC.ITCFailReason_NoRemainCash;

        return ResCITC.ITCNormalItemResult(arg);
    }

    public static final MaplePacket getMTSConfirmSell() {
        ArgITCNormalItemResult arg = new ArgITCNormalItemResult();
        arg.ops_res = OpsITC.ITCRes_RegisterSaleEntry_Done;

        return ResCITC.ITCNormalItemResult(arg);
    }

    public static final MaplePacket addToCartMessage(boolean fail, boolean remove) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ITCNormalItemResult);

        if (remove) {
            if (fail) {
                sp.Encode1(44);
                sp.Encode4(-1);
            } else {
                sp.Encode1(43);
            }
        } else {
            if (fail) {
                sp.Encode1(42);
                sp.Encode4(-1);
            } else {
                sp.Encode1(41);
            }
        }
        return sp.get();
    }

    public static final MaplePacket getMTSConfirmCancel() {
        ArgITCNormalItemResult arg = new ArgITCNormalItemResult();
        arg.ops_res = OpsITC.ITCRes_CancelSaleItem_Done;

        return ResCITC.ITCNormalItemResult(arg);
    }

    public static final MaplePacket sendMTS(final List<MTSStorage.MTSItemInfo> items, final int tab, final int type, final int page, final int pages) {
        final ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ITCNormalItemResult);

        sp.Encode1(21); //operation
        sp.Encode4(pages * 10); //total items
        sp.Encode4(items.size()); //number of items on this page
        sp.Encode4(tab);
        sp.Encode4(type);
        sp.Encode4(page);
        sp.Encode1(1);
        sp.Encode1(1);
        for (MTSStorage.MTSItemInfo item : items) {
            sp.EncodeBuffer(addMTSItemInfo(item));
        }
        sp.Encode1(0); //0 or 1?
        return sp.get();
    }

    public static byte[] addMTSItemInfo(MTSStorage.MTSItemInfo item) {
        ServerPacket data = new ServerPacket();

        data.EncodeBuffer(DataGW_ItemSlotBase.Encode(item.getItem()));
        data.Encode4(item.getId()); //id
        data.Encode4(item.getTaxes()); //this + below = price
        data.Encode4(item.getPrice()); //price
        data.Encode8(0);
        data.Encode4(KoreanDateUtil.getQuestTimestamp(item.getEndingDate()));
        data.Encode4(KoreanDateUtil.getQuestTimestamp(item.getEndingDate()));
        data.EncodeStr(item.getSeller()); //account name (what was nexon thinking?)
        data.EncodeStr(item.getSeller()); //char name
        data.EncodeZeroBytes(28);

        return data.get().getBytes();
    }

    public static final MaplePacket getMTSWantedListingOver(final int nx, final int items) {
        final ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ITCNormalItemResult);

        sp.Encode1(61);
        sp.Encode4(nx);
        sp.Encode4(items);
        return sp.get();
    }

    public static final MaplePacket getTransferInventory(final List<IItem> items, final boolean changed) {
        final ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ITCNormalItemResult);

        sp.Encode1(33);
        sp.Encode4(items.size());
        int i = 0;
        for (IItem item : items) {
            sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(item));
            sp.Encode4(Integer.MAX_VALUE - i); //fake ID
            sp.Encode4(110);
            sp.Encode4(1011); //fake
            sp.EncodeZeroBytes(48);
            i++;
        }
        sp.Encode4(-47 + i - 1);
        sp.Encode1(changed ? 1 : 0);
        return sp.get();
    }

    public static final MaplePacket getMTSConfirmBuy() {
        ArgITCNormalItemResult arg = new ArgITCNormalItemResult();
        arg.ops_res = OpsITC.ITCRes_BuyItem_Done;

        return ResCITC.ITCNormalItemResult(arg);
    }

    public static final MaplePacket getMTSConfirmTransfer(final int quantity, final int pos) {
        final ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ITCNormalItemResult);

        sp.Encode1(39);
        sp.Encode4(quantity);
        sp.Encode4(pos);
        return sp.get();
    }

    public static final MaplePacket getMTSFailBuy() {
        ArgITCNormalItemResult arg = new ArgITCNormalItemResult();
        arg.ops_res = OpsITC.ITCRes_BuyItem_Failed;
        arg.ops_fail_reason = OpsITC.ITCFailReason_NoRemainCash;

        return ResCITC.ITCNormalItemResult(arg);
    }

    public static final MaplePacket getNotYetSoldInv(final List<MTSStorage.MTSItemInfo> items) {
        final ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ITCNormalItemResult);

        sp.Encode1(35);
        sp.Encode4(items.size());
        for (MTSStorage.MTSItemInfo item : items) {
            sp.EncodeBuffer(addMTSItemInfo(item));
        }
        return sp.get();
    }

    public static final MaplePacket getMTSFailSell() {
        ArgITCNormalItemResult arg = new ArgITCNormalItemResult();
        arg.ops_res = OpsITC.ITCRes_RegisterSaleEntry_Failed;
        arg.ops_fail_reason = OpsITC.ITCFailReason_NoRemainCash;

        return ResCITC.ITCNormalItemResult(arg);
    }

}
