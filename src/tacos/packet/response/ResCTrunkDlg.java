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
import odin.client.inventory.MapleInventoryType;
import tacos.config.Region;
import tacos.config.Version;
import tacos.network.MaplePacket;
import java.util.List;
import odin.server.MapleStorage;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsDBCHAR;
import tacos.packet.ops.OpsTrunk;
import tacos.packet.response.data.DataGW_ItemSlotBase;

/**
 *
 * @author Riremito
 */
public class ResCTrunkDlg {

    // CTrunkDlg::OnPacket
    public static MaplePacket TrunkResult(MapleStorage storage, OpsTrunk ops) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TrunkResult);

        sp.Encode1(ops.get());
        switch (ops) {
            case TrunkRes_GetSuccess: // goto.
            case TrunkRes_PutSuccess: {
                sp.EncodeBuffer(SetGetItems(storage, storage.getLastModified().get()));
                break;
            }
            case TrunkRes_SortItem: {
                sp.EncodeBuffer(SetGetItems(storage, OpsDBCHAR.DBCHAR_ALL.get()));
                break;
            }
            case TrunkRes_MoneySuccess: {
                sp.EncodeBuffer(SetGetItems(storage, OpsDBCHAR.DBCHAR_MONEY.get()));
                break;
            }
            case TrunkRes_OpenTrunkDlg: {
                sp.EncodeBuffer(SetTrunkDlg(storage));
                break;
            }
            case TrunkRes_ServerMsg: {
                byte isMsg = 0;
                sp.Encode1(isMsg);
                if (isMsg != 0) {
                    sp.EncodeStr("msg.");
                }
                break;
            }
            default: {
                break;
            }
        }

        return sp.get();
    }

    // CTrunkDlg::SetTrunkDlg
    public static byte[] SetTrunkDlg(MapleStorage storage) {
        ServerPacket data = new ServerPacket();

        data.Encode4(storage.getNpcId()); // m_dwNpcTemplateID
        data.EncodeBuffer(SetGetItems(storage, OpsDBCHAR.DBCHAR_ALL.get()));
        return data.get().getBytes();
    }

    // CTrunkDlg::SetGetItems
    public static byte[] SetGetItems(MapleStorage storage, long dbcharFlag) {
        ServerPacket data = new ServerPacket();

        data.Encode1(storage.getSlots()); // m_nSlotCount
        if (Version.LessOrEqual(Region.KMS, 43) || Version.LessOrEqual(Region.JMS, 131)) {
            data.Encode2((short) dbcharFlag);
        } else {
            data.Encode8(dbcharFlag); // dbcharFlag
        }

        // 0x02
        if ((dbcharFlag & OpsDBCHAR.DBCHAR_MONEY.get()) != 0) {
            data.Encode4(storage.getMeso()); // m_nMoney
        }
        // 0x04, Equip
        if ((dbcharFlag & OpsDBCHAR.DBCHAR_ITEMSLOTEQUIP.get()) != 0) {
            List<IItem> items = storage.filterItems(MapleInventoryType.EQUIP);
            data.Encode1(items.size()); // nCount
            for (IItem item : items) {
                data.EncodeBuffer(DataGW_ItemSlotBase.Encode(item));
            }
        }
        // 0x08, Consume
        if ((dbcharFlag & OpsDBCHAR.DBCHAR_ITEMSLOTCONSUME.get()) != 0) {
            List<IItem> items = storage.filterItems(MapleInventoryType.USE);
            data.Encode1(items.size()); // nCount
            for (IItem item : items) {
                data.EncodeBuffer(DataGW_ItemSlotBase.Encode(item));
            }
        }
        // 0x10, Install
        if ((dbcharFlag & OpsDBCHAR.DBCHAR_ITEMSLOTINSTALL.get()) != 0) {
            List<IItem> items = storage.filterItems(MapleInventoryType.SETUP);
            data.Encode1(items.size()); // nCount
            for (IItem item : items) {
                data.EncodeBuffer(DataGW_ItemSlotBase.Encode(item));
            }
        }
        // 0x20, Etc
        if ((dbcharFlag & OpsDBCHAR.DBCHAR_ITEMSLOTETC.get()) != 0) {
            List<IItem> items = storage.filterItems(MapleInventoryType.ETC);
            data.Encode1(items.size()); // nCount
            for (IItem item : items) {
                data.EncodeBuffer(DataGW_ItemSlotBase.Encode(item));
            }
        }
        // 0x40, Cash
        if ((dbcharFlag & OpsDBCHAR.DBCHAR_ITEMSLOTCASH.get()) != 0) {
            List<IItem> items = storage.filterItems(MapleInventoryType.CASH);
            data.Encode1(items.size()); // nCount
            for (IItem item : items) {
                data.EncodeBuffer(DataGW_ItemSlotBase.Encode(item));
            }
        }

        return data.get().getBytes();
    }

}
