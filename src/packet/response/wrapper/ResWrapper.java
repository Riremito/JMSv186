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
package packet.response.wrapper;

import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.GameConstants;
import handling.MaplePacket;
import packet.ServerPacket;
import packet.request.ContextPacket;
import packet.response.ResCWvsContext;
import packet.response.struct.InvOp;
import packet.response.struct.TestHelper;
import tools.HexTool;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResWrapper {

    public static MaplePacket getInventoryFull() {
        return ResCWvsContext.InventoryOperation(true, null);
    }

    public static MaplePacket addInventorySlot(MapleInventoryType type, IItem item) {
        return addInventorySlot(type, item, false);
    }

    public static MaplePacket addInventorySlot(MapleInventoryType type, IItem item, boolean fromDrop) {
        InvOp io = new InvOp();
        io.add(type, item);
        return ResCWvsContext.InventoryOperation(fromDrop, io);
    }

    public static MaplePacket updateInventorySlot(MapleInventoryType type, IItem item, boolean fromDrop) {
        InvOp io = new InvOp();
        io.update(type, item);
        return ResCWvsContext.InventoryOperation(fromDrop, io);
    }

    public static MaplePacket dropInventoryItemUpdate(MapleInventoryType type, IItem item) {
        InvOp io = new InvOp();
        io.update(type, item);
        return ResCWvsContext.InventoryOperation(true, io);
    }

    public static final MaplePacket updatePet(final MaplePet pet, final IItem item) {
        InvOp io = new InvOp();
        // ペットと装備の更新時はアイテムを削除する必要はなく、同一スロットにアイテムを追加するだけで良い
        // アイテム削除を行うとペットと装備固有のクエストが再発生する
        io.add(MapleInventoryType.CASH, item);
        return ResCWvsContext.InventoryOperation(false, io);
    }

    public static MaplePacket moveInventoryItem(MapleInventoryType type, int src, int dst) {
        return moveInventoryItem(type, src, dst, (byte) -1);
    }

    public static MaplePacket moveInventoryItem(MapleInventoryType type, int src, int dst, short equipIndicator) {
        InvOp io = new InvOp();
        io.move(type, src, dst);
        return ResCWvsContext.InventoryOperation(true, io);
    }

    public static MaplePacket dropInventoryItem(MapleInventoryType type, short src) {
        InvOp io = new InvOp();
        io.remove(type, src);
        return ResCWvsContext.InventoryOperation(true, io);
    }

    public static MaplePacket clearInventoryItem(MapleInventoryType type, short slot, boolean fromDrop) {
        InvOp io = new InvOp();
        io.remove(type, slot);
        return ResCWvsContext.InventoryOperation(fromDrop, io);
    }

    public static MaplePacket scrolledItem(IItem scroll, IItem item, boolean destroyed, boolean potential) {
        InvOp io = new InvOp();

        // 書
        if (0 < scroll.getQuantity()) {
            io.update(GameConstants.getInventoryType(scroll.getItemId()), scroll);
        } else {
            io.remove(GameConstants.getInventoryType(scroll.getItemId()), scroll.getPosition());
        }

        // 装備
        if (!destroyed) {
            //io.remove(GameConstants.getInventoryType(item.getItemId()), item.getPosition());
            io.add(GameConstants.getInventoryType(item.getItemId()), item);
        } else {
            io.remove(GameConstants.getInventoryType(item.getItemId()), item.getPosition());
        }

        return ResCWvsContext.InventoryOperation(true, io);
    }

    public static MaplePacket updateSpecialItemUse(IItem item, byte invType) {
        return updateSpecialItemUse(item, invType, item.getPosition());
    }

    public static MaplePacket updateSpecialItemUse(IItem item, byte invType, short pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(0); // could be from drop
        mplew.write(2); // always 2
        mplew.write(3); // quantity > 0 (?)
        mplew.write(invType); // Inventory type
        mplew.writeShort(pos); // item slot
        mplew.write(0);
        mplew.write(invType);
        if (item.getType() == 1) {
            mplew.writeShort(pos);
        } else {
            mplew.write(pos);
        }
        TestHelper.addItemInfo(mplew, item, true, true);
        if (item.getPosition() < 0) {
            mplew.write(2); //?
        }
        return mplew.getPacket();
    }

    public static MaplePacket moveAndMergeWithRestInventoryItem(MapleInventoryType type, short src, short dst, short srcQ, short dstQ) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 01"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(srcQ);
        mplew.write(HexTool.getByteArrayFromHexString("01"));
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(dstQ);
        return mplew.getPacket();
    }

    public static MaplePacket moveAndMergeInventoryItem(MapleInventoryType type, short src, short dst, short total) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 03"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.write(1); // merge mode?
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(total);
        return mplew.getPacket();
    }

    public static MaplePacket updateSpecialItemUse_(IItem item, byte invType) {
        return updateSpecialItemUse_(item, invType, item.getPosition());
    }

    public static MaplePacket updateSpecialItemUse_(IItem item, byte invType, short pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(0); // could be from drop
        mplew.write(1); // always 2
        mplew.write(0); // quantity > 0 (?)
        mplew.write(invType); // Inventory type
        if (item.getType() == 1) {
            mplew.writeShort(pos);
        } else {
            mplew.write(pos);
        }
        TestHelper.addItemInfo(mplew, item, true, true);
        if (item.getPosition() < 0) {
            mplew.write(1); //?
        }
        return mplew.getPacket();
    }

    public static MaplePacket getShowInventoryFull() {
        return ContextPacket.getShowInventoryStatus(ContextPacket.DropPickUpMessageType.PICKUP_INVENTORY_FULL);
    }

}
