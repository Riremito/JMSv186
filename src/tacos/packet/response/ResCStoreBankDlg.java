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

import java.util.ArrayList;
import java.util.List;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import tacos.client.TacosHoldInventory;
import tacos.config.Config;
import tacos.config.Region;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsDBCHAR;
import tacos.packet.ops.OpsEntrustedShop;
import tacos.packet.response.data.RD_GW_ItemSlotBase;

/**
 *
 * @author Riremito
 */
public class ResCStoreBankDlg {

    // CStoreBankDlg::OnPacket
    public static ServerPacket StoreBankGetAllResult(OpsEntrustedShop ops) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_StoreBankGetAllResult);

        sp.Encode1(ops.get());
        return sp;
    }

    // CStoreBankDlg::OnPacket
    public static ServerPacket OpenStoreBankDlg(int npc_id, TacosHoldInventory goods) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_StoreBankResult);

        sp.Encode1(OpsEntrustedShop.StoreBankRes_OpenStoreBankDlg.get());
        // CStoreBankDlg::SetStoreBankDlg
        sp.Encode4(npc_id); // m_dwNpcTemplateID
        sp.EncodeBuffer(SetItems(goods));
        return sp;
    }

    // CStoreBankDlg::OnPacket, the fee is asked before CStoreBankDlg::SendGetAllRequest.
    public static ServerPacket StoreBankCalculateFee(int passing_day, int fee) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_StoreBankResult);

        sp.Encode1(OpsEntrustedShop.StoreBankRes_StoreBankCalculateFee.get());
        sp.Encode4(passing_day);
        sp.Encode4(fee); // 0 = no fee message
        return sp;
    }

    // CStoreBankDlg::OnPacket, the hired merchant is still open, or nothing is stored when field_id is 999999999.
    public static ServerPacket StoreBankLoadFailed(int npc_id, int field_id, int channel) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_StoreBankResult);

        sp.Encode1(OpsEntrustedShop.StoreBankRes_StoreBankLoadFailed.get());
        sp.Encode4(npc_id);
        sp.Encode4(field_id); // free market room = field_id % 100
        sp.Encode1(channel); // from 0
        return sp;
    }

    // CStoreBankDlg::OnPacket, "This is currently unavailable."
    public static ServerPacket StoreBankBlock() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_StoreBankResult);

        sp.Encode1(OpsEntrustedShop.StoreBankRes_StoreBankBlock.get());
        return sp;
    }

    // CStoreBankDlg::SetItems
    private static byte[] SetItems(TacosHoldInventory goods) {
        ServerPacket data = new ServerPacket();
        long dbcharFlag = OpsDBCHAR.DBCHAR_ALL.get();

        data.Encode1(goods.getItems().size()); // m_nSlotCount, used for the scroll bar
        if (Config.LessOrEqual(Region.KMS, 43) || Config.LessOrEqual(Region.JMS, 131)) {
            data.Encode2((short) dbcharFlag);
        } else {
            data.Encode8(dbcharFlag); // dbcharFlag
        }
        // 0x02
        data.Encode4(goods.getMeso()); // m_nMoney
        // 0x04, 0x08, 0x10, 0x20, 0x40
        MapleInventoryType[] types = {MapleInventoryType.EQUIP, MapleInventoryType.USE, MapleInventoryType.SETUP, MapleInventoryType.ETC, MapleInventoryType.CASH};
        for (MapleInventoryType type : types) {
            List<Item> items = new ArrayList<>();
            for (Item item : goods.getItems()) {
                if (GameConstants.getInventoryType(item.getItemId()) == type) {
                    items.add(item);
                }
            }
            data.Encode1(items.size()); // nCount
            for (Item item : items) {
                data.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item));
            }
        }
        return data.getBytes();
    }
}
