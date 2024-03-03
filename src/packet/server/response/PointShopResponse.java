/*
 * Copyright (C) 2024 Riremito
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
package packet.server.response;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import config.ServerConfig;
import handling.MaplePacket;
import java.util.List;
import java.util.Map;
import packet.server.ServerPacket;
import packet.server.response.struct.CharacterData;
import packet.server.response.struct.TestHelper;
import server.CashItemFactory;
import server.CashItemInfo;
import server.CashShop;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class PointShopResponse {

    public static MaplePacket warpCS(MapleClient c) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_SetCashShop);
        p.EncodeBuffer(CharacterData.Encode(c.getPlayer()));
        p.EncodeStr(c.getAccountName());
        if (194 <= ServerConfig.version) {
            p.Encode4(0);
        }
        p.Encode2(0);
        if (165 <= ServerConfig.version) {
            p.Encode2(0);
        }
        p.Encode1(0);
        p.EncodeZeroBytes(1080);
        p.Encode2(0);
        p.Encode2(0);
        p.Encode1(0);
        return p.Get();
    }

    public static MaplePacket showBoughtCSQuestItem(int price, short quantity, byte position, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.write(144);
        mplew.writeInt(price);
        mplew.writeShort(quantity);
        mplew.writeShort(position);
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }

    public static MaplePacket showCouponRedeemedItem(int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.writeShort(96);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeShort(1);
        mplew.writeShort(26);
        mplew.writeInt(itemid);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket showCouponRedeemedItem(Map<Integer, IItem> items, int mesos, int maplePoints, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.write(96); //use to be 4c
        mplew.write(items.size());
        for (Map.Entry<Integer, IItem> item : items.entrySet()) {
            addCashItemInfo(mplew, item.getValue(), c.getAccID(), item.getKey().intValue());
        }
        mplew.writeLong(maplePoints);
        mplew.writeInt(mesos);
        return mplew.getPacket();
    }

    public static MaplePacket showBoughtCSItem(int itemid, int sn, int uniqueid, int accid, int quantity, String giftFrom, long expire) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.write(88 /*0x5A*/ ); //use to be 4a
        addCashItemInfo(mplew, uniqueid, accid, itemid, sn, quantity, giftFrom, expire);
        return mplew.getPacket();
    }

    public static MaplePacket showBoughtCSItem(IItem item, int sn, int accid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.write(88 /*0x5A*/ );
        addCashItemInfo(mplew, item, accid, sn);
        return mplew.getPacket();
    }

    public static MaplePacket sendWishList(MapleCharacter chr, boolean update) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.write(update ? 86 : 82); //+12
        int[] list = chr.getWishlist();
        for (int i = 0; i < 10; i++) {
            mplew.writeInt(list[i] != -1 ? list[i] : 0);
        }
        return mplew.getPacket();
    }

    public static MaplePacket showBoughtCSPackage(Map<Integer, IItem> ccc, int accid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.write(140); //use to be 7a
        mplew.write(ccc.size());
        for (Map.Entry<Integer, IItem> sn : ccc.entrySet()) {
            addCashItemInfo(mplew, sn.getValue(), accid, sn.getKey().intValue());
        }
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static MaplePacket showNXMapleTokens(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_UPDATE.Get());
        mplew.writeInt(chr.getCSPoints(1)); // A-cash
        mplew.writeInt(chr.getCSPoints(2)); // MPoint
        return mplew.getPacket();
    }

    public static MaplePacket increasedInvSlots(int inv, int slots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.write(99);
        mplew.write(inv);
        mplew.writeShort(slots);
        return mplew.getPacket();
    }

    //also used for character slots !
    public static MaplePacket increasedStorageSlots(int slots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.write(101);
        mplew.writeShort(slots);
        return mplew.getPacket();
    }

    public static MaplePacket cashItemExpired(int uniqueid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.write(113); //use to be 5d
        mplew.writeLong(uniqueid);
        return mplew.getPacket();
    }

    public static MaplePacket confirmToCSInventory(IItem item, int accId, int sn) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.write(109);
        addCashItemInfo(mplew, item, accId, sn, false);
        return mplew.getPacket();
    }

    public static void addModCashItemInfo(MaplePacketLittleEndianWriter mplew, CashItemInfo.CashModInfo item) {
        int flags = item.flags;
        mplew.writeInt(item.sn);
        mplew.writeInt(flags);
        if ((flags & 1) != 0) {
            mplew.writeInt(item.itemid);
        }
        if ((flags & 2) != 0) {
            mplew.writeShort(item.count);
        }
        if ((flags & 4) != 0) {
            mplew.writeInt(item.discountPrice);
        }
        if ((flags & 8) != 0) {
            mplew.write(item.unk_1 - 1);
        }
        if ((flags & 16) != 0) {
            mplew.write(item.priority);
        }
        if ((flags & 32) != 0) {
            mplew.writeShort(item.period);
        }
        if ((flags & 64) != 0) {
            mplew.writeInt(0);
        }
        if ((flags & 128) != 0) {
            mplew.writeInt(item.meso);
        }
        if ((flags & 256) != 0) {
            mplew.write(item.unk_2 - 1);
        }
        if ((flags & 512) != 0) {
            mplew.write(item.gender);
        }
        if ((flags & 1024) != 0) {
            mplew.write(item.showUp ? 1 : 0);
        }
        if ((flags & 2048) != 0) {
            mplew.write(item.mark);
        }
        if ((flags & 4096) != 0) {
            mplew.write(item.unk_3 - 1);
        }
        if ((flags & 8192) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 16384) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 32768) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 65536) != 0) {
            List<CashItemInfo> pack = CashItemFactory.getInstance().getPackageItems(item.sn);
            if (pack == null) {
                mplew.write(0);
            } else {
                mplew.write(pack.size());
                for (int i = 0; i < pack.size(); i++) {
                    mplew.writeInt(pack.get(i).getSN());
                }
            }
        }
    }

    //work on this packet a little more
    public static MaplePacket getCSGifts(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.write(80); //use to be 40
        List<Pair<IItem, String>> mci = c.getPlayer().getCashInventory().loadGifts();
        mplew.writeShort(mci.size());
        for (Pair<IItem, String> mcz : mci) {
            mplew.writeLong(mcz.getLeft().getUniqueId());
            mplew.writeInt(mcz.getLeft().getItemId());
            mplew.writeAsciiString(mcz.getLeft().getGiftFrom(), 13);
            mplew.writeAsciiString(mcz.getRight(), 73);
        }
        return mplew.getPacket();
    }

    public static MaplePacket getCSInventory(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.write(78); // use to be 3e
        CashShop mci = c.getPlayer().getCashInventory();
        mplew.writeShort(mci.getItemsSize());
        for (IItem itemz : mci.getInventory()) {
            addCashItemInfo(mplew, itemz, c.getAccID(), 0); //test
        }
        mplew.writeShort(c.getPlayer().getStorage().getSlots());
        mplew.writeInt(c.getCharacterSlots());
        mplew.writeShort(4); //00 00 04 00 <-- added?
        return mplew.getPacket();
    }

    public static MaplePacket confirmFromCSInventory(IItem item, short pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.write(107);
        mplew.writeShort(pos);
        TestHelper.addItemInfo(mplew, item, true, true);
        return mplew.getPacket();
    }

    public static MaplePacket sendGift(int price, int itemid, int quantity, String receiver) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.write(142); //use to be 7C
        mplew.writeMapleAsciiString(receiver);
        mplew.writeInt(itemid);
        mplew.writeShort(quantity);
        mplew.writeShort(0); //maplePoints
        mplew.writeInt(price);
        return mplew.getPacket();
    }

    public static final void addCashItemInfo(MaplePacketLittleEndianWriter mplew, IItem item, int accId, int sn) {
        addCashItemInfo(mplew, item, accId, sn, true);
    }

    public static final void addCashItemInfo(MaplePacketLittleEndianWriter mplew, IItem item, int accId, int sn, boolean isFirst) {
        addCashItemInfo(mplew, item.getUniqueId(), accId, item.getItemId(), sn, item.getQuantity(), item.getGiftFrom(), item.getExpiration(), isFirst); //owner for the lulz
    }

    public static final void addCashItemInfo(MaplePacketLittleEndianWriter mplew, int uniqueid, int accId, int itemid, int sn, int quantity, String sender, long expire) {
        addCashItemInfo(mplew, uniqueid, accId, itemid, sn, quantity, sender, expire, true);
    }

    public static final void addCashItemInfo(MaplePacketLittleEndianWriter mplew, int uniqueid, int accId, int itemid, int sn, int quantity, String sender, long expire, boolean isFirst) {
        mplew.writeLong(uniqueid > 0 ? uniqueid : 0);
        mplew.writeLong(accId);
        mplew.writeInt(itemid);
        mplew.writeInt(isFirst ? sn : 0);
        mplew.writeShort(quantity);
        mplew.writeAsciiString(sender, 13); //owner for the lulzlzlzl
        TestHelper.addExpirationTime(mplew, expire);
        mplew.writeLong(isFirst ? 0 : sn);
        //if (isFirst && uniqueid > 0 && GameConstants.isEffectRing(itemid)) {
        //	MapleRing ring = MapleRing.loadFromDb(uniqueid);
        //	if (ring != null) { //or is this only for friendship rings, i wonder. and does isFirst even matter
        //		mplew.writeMapleAsciiString(ring.getPartnerName());
        //		mplew.writeInt(itemid);
        //		mplew.writeShort(quantity);
        //	}
        //}
    }

    public static MaplePacket enableCSUse() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(10);
        mplew.write(1);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket sendCSFail(int err) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.CS_OPERATION.Get());
        mplew.write(104);
        mplew.write(err);
        return mplew.getPacket();
    }
    
}
