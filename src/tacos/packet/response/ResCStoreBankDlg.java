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

import odin.client.inventory.IItem;
import tacos.network.MaplePacket;
import tacos.packet.ServerPacket;
import odin.server.MerchItemPackage;
import tacos.packet.ServerPacketHeader;
import tacos.packet.response.data.DataGW_ItemSlotBase;

/**
 *
 * @author Riremito
 */
public class ResCStoreBankDlg {

    public static final MaplePacket merchItemStore(final byte op) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_StoreBankResult);

        // [28 01] [22 01] - Invalid Asiasoft Passport
        // [28 01] [22 00] - Open Asiasoft pin typing
        sp.Encode1(op);
        switch (op) {
            case 36:
                sp.Encode8(0);
                break;
            default:
                sp.Encode1(0);
                break;
        }

        return sp.get();
    }

    public static final MaplePacket merchItem_Message(final byte op) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_StoreBankGetAllResult);

        sp.Encode1(op);
        return sp.get();
    }

    public static final MaplePacket sendHiredMerchantMessage(final byte type) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_StoreBankGetAllResult);

        // 07 = send title box
        // 09 = Please pick up your items from Fredrick and then try again.
        // 0A = Your another character is using the item now. Please close the shop with that character or empty your store bank.
        // 0B = You cannot open it now.
        // 0F = Please retrieve your items from Fredrick.
        sp.Encode1(type);
        return sp.get();
    }

    public static final MaplePacket merchItemStore_ItemData(final MerchItemPackage pack) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_StoreBankResult);

        sp.Encode1(35);
        sp.Encode4(9030000); // Fredrick
        sp.Encode4(32272); // pack.getPackageid()
        sp.EncodeZeroBytes(5);
        sp.Encode4(pack.getMesos());
        sp.Encode1(0);
        sp.Encode1(pack.getItems().size());
        for (final IItem item : pack.getItems()) {
            sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(item));
        }
        sp.EncodeZeroBytes(3);
        return sp.get();
    }

}
