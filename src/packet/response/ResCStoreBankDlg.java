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

import client.inventory.IItem;
import handling.MaplePacket;
import packet.ServerPacket;
import packet.response.struct.TestHelper;
import server.MerchItemPackage;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCStoreBankDlg {

    public static final MaplePacket merchItemStore(final byte op) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // [28 01] [22 01] - Invalid Asiasoft Passport
        // [28 01] [22 00] - Open Asiasoft pin typing
        mplew.writeShort(ServerPacket.Header.LP_StoreBankResult.Get());
        mplew.write(op);
        switch (op) {
            case 36:
                mplew.writeZeroBytes(8);
                break;
            default:
                mplew.write(0);
                break;
        }
        return mplew.getPacket();
    }

    public static final MaplePacket merchItem_Message(final byte op) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_StoreBankGetAllResult.Get());
        mplew.write(op);
        return mplew.getPacket();
    }

    //BELOW ARE UNUSED PLEASE RECONSIDER.
    public static final MaplePacket sendHiredMerchantMessage(final byte type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 07 = send title box
        // 09 = Please pick up your items from Fredrick and then try again.
        // 0A = Your another character is using the item now. Please close the shop with that character or empty your store bank.
        // 0B = You cannot open it now.
        // 0F = Please retrieve your items from Fredrick.
        mplew.writeShort(ServerPacket.Header.LP_StoreBankGetAllResult.Get());
        mplew.write(type);
        return mplew.getPacket();
    }

    public static final MaplePacket merchItemStore_ItemData(final MerchItemPackage pack) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_StoreBankResult.Get());
        mplew.write(35);
        mplew.writeInt(9030000); // Fredrick
        mplew.writeInt(32272); // pack.getPackageid()
        mplew.writeZeroBytes(5);
        mplew.writeInt(pack.getMesos());
        mplew.write(0);
        mplew.write(pack.getItems().size());
        for (final IItem item : pack.getItems()) {
            TestHelper.addItemInfo(mplew, item, true, true);
        }
        mplew.writeZeroBytes(3);
        return mplew.getPacket();
    }
    
}
