/*
 * Copyright (C) 2026 Riremito
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
package tacos.packet.request;

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.inventory.IItem;
import odin.client.inventory.ItemFlag;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsTrunk;
import tacos.packet.response.ResCTrunkDlg;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleStorage;

/**
 *
 * @author Riremito
 */
public class ReqCTrunkDlg {

    public static boolean OnPacket(ClientPacket cp, MapleClient client) {
        MapleCharacter chr = client.getPlayer();
        if (chr == null) {
            return false;
        }
        MapleStorage storage = chr.getStorage();

        byte mode = cp.Decode1();

        switch (OpsTrunk.find(mode)) {
            case TrunkReq_GetItem: {
                byte type = cp.Decode1();
                byte slot = cp.Decode1();
                IItem item = storage.takeOut(type, slot);
                if (item == null) {
                    return false;
                }

                if (!MapleInventoryManipulator.checkSpace(client, item.getItemId(), item.getQuantity(), item.getOwner())) {
                    storage.store(GameConstants.getInventoryType(item.getItemId()), item);
                    chr.SendPacket(ResCTrunkDlg.TrunkResult(storage, OpsTrunk.TrunkRes_GetUnknown));
                    return false;
                }

                MapleInventoryManipulator.addFromDrop(client, item, false);
                storage.setLastModified(type);
                chr.SendPacket(ResCTrunkDlg.TrunkResult(storage, OpsTrunk.TrunkRes_GetSuccess));
                return true;
            }
            case TrunkReq_PutItem: {
                // 手数料不足
                if (chr.getMeso() < 100) {
                    chr.SendPacket(ResCTrunkDlg.TrunkResult(storage, OpsTrunk.TrunkRes_PutNoMoney));
                    return false;
                }

                short slot = cp.Decode2();
                int itemId = cp.Decode4();
                short quantity = cp.Decode2();

                MapleInventoryType type = GameConstants.getInventoryType(itemId); // no type in packet.
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

                // packet hack
                if (quantity < 1) {
                    chr.SendPacket(ResCTrunkDlg.TrunkResult(storage, OpsTrunk.TrunkRes_PutIncorrectRequest));
                    return false;
                }

                // 空きスロットがない
                if (storage.isFull()) {
                    chr.SendPacket(ResCTrunkDlg.TrunkResult(storage, OpsTrunk.TrunkRes_PutNoSpace));
                    return false;
                }

                IItem item = chr.getInventory(type).getItem(slot).copy();

                if (GameConstants.isPet(item.getItemId())) {
                    chr.SendPacket(ResCTrunkDlg.TrunkResult(storage, OpsTrunk.TrunkRes_PutIncorrectRequest));
                    return false;
                }

                final byte flag = item.getFlag();
                if (ii.isPickupRestricted(item.getItemId()) && storage.findById(item.getItemId()) != null) {
                    chr.updateInv();
                    return false;
                }

                if (item.getItemId() == itemId && (item.getQuantity() >= quantity || GameConstants.isThrowingStar(itemId) || GameConstants.isBullet(itemId))) {
                    if (ii.isDropRestricted(item.getItemId())) {
                        if (ItemFlag.KARMA_EQ.check(flag)) {
                            item.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
                        } else if (ItemFlag.KARMA_USE.check(flag)) {
                            item.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
                        } else {
                            chr.updateInv();
                            return false;
                        }
                    }
                    if (GameConstants.isThrowingStar(itemId) || GameConstants.isBullet(itemId)) {
                        quantity = item.getQuantity();
                    }
                    chr.gainMeso(-100, false, true, false);
                    MapleInventoryManipulator.removeFromSlot(client, type, slot, quantity, false);
                    item.setQuantity(quantity);
                } else {
                    // ?
                    return false;
                }

                storage.store(type, item);
                storage.setLastModified(type.getType());
                client.SendPacket(ResCTrunkDlg.TrunkResult(storage, OpsTrunk.TrunkRes_PutSuccess));
                return true;
            }
            case TrunkReq_Money: {
                int meso = cp.Decode4();
                int storageMesos = storage.getMeso();
                int playerMesos = chr.getMeso();

                // packet hack
                if (meso == 0) {
                    chr.SendPacket(ResCTrunkDlg.TrunkResult(storage, OpsTrunk.TrunkRes_PutIncorrectRequest));
                    return false;
                }

                // packet hack
                if (meso <= 0 && (storageMesos - meso < 0)) {
                    chr.SendPacket(ResCTrunkDlg.TrunkResult(storage, OpsTrunk.TrunkRes_PutIncorrectRequest));
                    return false;
                }

                // packet hack
                if (meso > 0 && (playerMesos + meso < 0)) {
                    chr.SendPacket(ResCTrunkDlg.TrunkResult(storage, OpsTrunk.TrunkRes_PutIncorrectRequest));
                    return false;
                }

                storage.setMeso(storageMesos - meso);
                chr.gainMeso(meso, false, true, false);
                client.SendPacket(ResCTrunkDlg.TrunkResult(storage, OpsTrunk.TrunkRes_MoneySuccess));
                return true;
            }
            case TrunkReq_CloseDialog: {
                storage.close();
                chr.setConversation(0);
                return true;
            }
            default: {
                DebugLogger.DebugLog("ReqCTrunkDlg : not coded.");
                break;
            }
        }

        return false;
    }

}
