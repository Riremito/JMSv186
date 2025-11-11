// 倉庫
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
import tacos.packet.response.wrapper.WrapCWvsContext;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleStorage;

public class ReqCTrunkDlg {

    public static boolean OnPacket(ClientPacket cp, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }
        MapleStorage storage = chr.getStorage();

        byte mode = cp.Decode1();

        switch (OpsTrunk.find(mode)) {
            case TrunkReq_GetItem: {
                // 手数料不足
                /*
                if (chr.getMeso() < 100) {
                    c.SendPacket(TrunkPacket.Error(SP_Flag.NOT_ENOUGH_MESOS_OUT));
                    return false;
                }
                 */

                final byte type = cp.Decode1();
                final byte slot = storage.getSlot(MapleInventoryType.getByType(type), cp.Decode1());
                final IItem item = storage.takeOut(slot); // 取り出す?

                if (item == null) {
                    return false;
                }

                if (!MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                    storage.store(item); // 戻す?
                    c.SendPacket(ResCTrunkDlg.Error(OpsTrunk.TrunkRes_GetUnknown));
                    return false;
                }

                //chr.gainMeso(-100, false, true, false);
                MapleInventoryManipulator.addFromDrop(c, item, false);
                storage.sendTakenOut(c, GameConstants.getInventoryType(item.getItemId()));
                return true;
            }
            case TrunkReq_PutItem: {
                // 手数料不足
                if (chr.getMeso() < 100) {
                    c.SendPacket(ResCTrunkDlg.Error(OpsTrunk.TrunkRes_PutNoMoney));
                    return false;
                }

                final byte slot = (byte) cp.Decode2();
                final int itemId = cp.Decode4();
                short quantity = cp.Decode2();
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

                // packet hack
                if (quantity < 1) {
                    c.SendPacket(ResCTrunkDlg.Error(OpsTrunk.TrunkRes_PutIncorrectRequest));
                    return false;
                }

                // 空きスロットがない
                if (storage.isFull()) {
                    c.SendPacket(ResCTrunkDlg.Error(OpsTrunk.TrunkRes_PutNoSpace));
                    return false;
                }

                MapleInventoryType type = GameConstants.getInventoryType(itemId);
                IItem item = chr.getInventory(type).getItem(slot).copy();

                if (GameConstants.isPet(item.getItemId())) {
                    c.SendPacket(ResCTrunkDlg.Error(OpsTrunk.TrunkRes_PutIncorrectRequest));
                    return false;
                }

                final byte flag = item.getFlag();
                if (ii.isPickupRestricted(item.getItemId()) && storage.findById(item.getItemId()) != null) {
                    c.getSession().write(WrapCWvsContext.updateStat());
                    return false;
                }

                if (item.getItemId() == itemId && (item.getQuantity() >= quantity || GameConstants.isThrowingStar(itemId) || GameConstants.isBullet(itemId))) {
                    if (ii.isDropRestricted(item.getItemId())) {
                        if (ItemFlag.KARMA_EQ.check(flag)) {
                            item.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
                        } else if (ItemFlag.KARMA_USE.check(flag)) {
                            item.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
                        } else {
                            c.getSession().write(WrapCWvsContext.updateStat());
                            return false;
                        }
                    }
                    if (GameConstants.isThrowingStar(itemId) || GameConstants.isBullet(itemId)) {
                        quantity = item.getQuantity();
                    }
                    chr.gainMeso(-100, false, true, false);
                    MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
                    item.setQuantity(quantity);
                    storage.store(item);
                } else {
                    // ?
                    return false;
                }

                storage.sendStored(c, GameConstants.getInventoryType(itemId));
                return true;
            }
            case TrunkReq_Money: {
                int meso = cp.Decode4();
                final int storageMesos = storage.getMeso();
                final int playerMesos = chr.getMeso();

                // packet hack
                if (meso == 0) {
                    c.SendPacket(ResCTrunkDlg.Error(OpsTrunk.TrunkRes_PutIncorrectRequest));
                    return false;
                }

                // packet hack
                if (meso <= 0 && (storageMesos - meso < 0)) {
                    c.SendPacket(ResCTrunkDlg.Error(OpsTrunk.TrunkRes_PutIncorrectRequest));
                    return false;
                }

                // packet hack
                if (meso > 0 && (playerMesos + meso < 0)) {
                    c.SendPacket(ResCTrunkDlg.Error(OpsTrunk.TrunkRes_PutIncorrectRequest));
                    return false;
                }

                storage.setMeso(storageMesos - meso);
                chr.gainMeso(meso, false, true, false);
                storage.sendMeso(c);
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
