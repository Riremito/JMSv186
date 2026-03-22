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
package tacos.packet.request;

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.constants.ServerConstants;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsITC;
import static tacos.packet.ops.OpsITC.ITCReq_RegisterSaleEntry;
import tacos.packet.response.ResCITC;
import tacos.packet.response.wrapper.WrapCITC;
import odin.server.MTSCart;
import odin.server.MTSStorage;
import odin.server.MapleInventoryManipulator;
import tacos.packet.ClientPacketHeader;

/**
 *
 * @author Riremito
 */
public class ReqCITC {

    public static boolean OnPacket(MapleClient c, ClientPacketHeader header, ClientPacket cp) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            DebugLogger.ErrorLog("character is not online (ITC).");
            return false;
        }

        switch (header) {
            case CP_ITCChargeParamRequest: {
                chr.SendPacket(ResCITC.ITCChargeParamResult());
                return true;
            }
            case CP_ITCQueryCashRequest: {
                chr.SendPacket(ResCITC.ITCQueryCashResult(chr));
                return true;
            }
            case CP_ITCItemRequest: {
                OnITCItemRequest(c, chr, cp);
                chr.SendPacket(ResCITC.ITCQueryCashResult(chr));
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnITCItemRequest(MapleClient c, MapleCharacter chr, ClientPacket cp) {
        MTSCart cart = MTSStorage.getInstance().getCart(chr.getId());
        byte req = cp.Decode1();
        OpsITC ops_req = OpsITC.find(req);

        switch (ops_req) {
            case ITCReq_GetMaplePoint: {
                break;
            }
            case ITCReq_CharacterModifiedNFlush: {
                break;
            }
            case ITCReq_RegisterSaleEntry: {
                // 2
                byte unused_item_type = cp.Decode1();
                int item_id = cp.Decode4();
                // skip unused item data.
                cp.setBackCursor(-14);
                int inv_slot = cp.Decode4();
                int item_quantity = cp.Decode4();
                int price = cp.Decode4();
                byte hours = cp.Decode1(); // 07
                byte unk1 = cp.Decode1(); // 01
                if (hours != 7 || price < 0 || item_quantity <= 0 || inv_slot <= 0) {
                    chr.SendPacket(WrapCITC.getMTSFailSell());
                    return true;
                }
                MapleInventoryType inv_type = GameConstants.getInventoryType(item_id);
                IItem item = chr.getInventory(inv_type).getItem((short) inv_slot);
                if (GameConstants.isRechargable(item_id)) {
                    item_quantity = item.getQuantity();
                }
                if (item.getItemId() != item_id || item_quantity <= 0 || item.getQuantity() < item_quantity) {
                    chr.SendPacket(WrapCITC.getMTSFailSell());
                    return true;
                }

                IItem item_copy = item.copy();
                item_copy.setQuantity((short) item_quantity);
                long expiration = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000);
                MTSStorage.getInstance().addToBuyNow(cart, item_copy, price, chr.getId(), chr.getName(), expiration);
                MapleInventoryManipulator.removeFromSlot(c, inv_type, (short) inv_slot, (short) item_quantity, false);
                chr.gainMeso(-ServerConstants.MTS_MESO, false);
                chr.SendPacket(WrapCITC.getMTSConfirmSell());
                sendMTSPackets(cart, c, true);
                return true;
            }
            case ITCReq_SaleCurrentItemToWish: {
                break;
            }
            case ITCReq_RegisterBuyOrder: {
                // 4
                int item_id = cp.Decode4();
                int item_price = cp.Decode4();
                int item_quantity = cp.Decode4();
                byte unk1 = cp.Decode1(); // 2E
                byte unk2 = cp.Decode1(); // 01
                String msg = cp.DecodeStr();
                break;
            }
            case ITCReq_GetITCList: {
                int unk1 = cp.Decode4(); // main tab
                int unk2 = cp.Decode4(); // sub tab
                int unk3 = cp.Decode4(); // page
                cart.changeInfo(unk1, unk2, unk3);
                doMTSPackets(cart, c);
                return true;
            }
            case ITCReq_GetSearchITCList: {
                break;
            }
            case ITCReq_CancelSaleItem: {
                int unk1 = cp.Decode4();

                if (!MTSStorage.getInstance().removeFromBuyNow(unk1, chr.getId(), true)) {
                    chr.SendPacket(WrapCITC.getMTSFailCancel());
                } else {
                    chr.SendPacket(WrapCITC.getMTSConfirmCancel());
                    sendMTSPackets(cart, c, true);
                }
                return true;
            }
            case ITCReq_MoveITCPurchaseItemLtoS: {
                // 8
                int unk1 = cp.Decode4();
                int id = Integer.MAX_VALUE - unk1; // fake id
                if (id >= cart.getInventory().size()) {
                    sendMTSPackets(cart, c, true);
                    return true;
                }
                IItem item = cart.getInventory().get(id);
                if (item == null || item.getQuantity() <= 0 || !MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                    chr.SendPacket(WrapCITC.getMTSFailBuy());
                    return true;
                }

                IItem item_ = item.copy();
                short pos = MapleInventoryManipulator.addbyItem(c, item_, true);
                if (pos < 0) {
                    chr.SendPacket(WrapCITC.getMTSFailBuy());
                    return true;
                }
                if (item_.getPet() != null) {
                    item_.getPet().setInventoryPosition(pos);
                    chr.addPet(item_.getPet());
                }
                cart.removeFromInventory(item);
                chr.SendPacket(WrapCITC.getMTSConfirmTransfer(item_.getQuantity(), pos));
                sendMTSPackets(cart, c, true);
                return true;
            }
            case ITCReq_SetZzim: {
                int unk1 = cp.Decode4();

                if (MTSStorage.getInstance().checkCart(unk1, chr.getId()) && cart.addToCart(unk1)) {
                    chr.SendPacket(WrapCITC.addToCartMessage(false, false));
                } else {
                    chr.SendPacket(WrapCITC.addToCartMessage(true, false));
                }
                return true;
            }
            case ITCReq_DeleteZzim: {
                int unk1 = cp.Decode4();

                if (cart.getCart().contains(unk1)) {
                    cart.removeFromCart(unk1);
                    chr.SendPacket(WrapCITC.addToCartMessage(false, true));
                } else {
                    chr.SendPacket(WrapCITC.addToCartMessage(true, true));
                }
                return true;
            }
            case ITCReq_LoadWishSaleList: {
                break;
            }
            case ITCReq_BuyWish: {
                break;
            }
            case ITCReq_CancelWish: {
                break;
            }
            case ITCReq_BuyWishChargeCash: {
                break;
            }
            case ITCReq_BuyWishCancel: {
                break;
            }
            case ITCReq_BuyItem: {
                // 16
                int unk1 = cp.Decode4();

                MTSStorage.MTSItemInfo mts = MTSStorage.getInstance().getSingleItem(unk1);
                if (mts == null) {
                    break;
                }
                // TODO : account checks
                if (mts.getCharacterId() == chr.getId()) {
                    chr.SendPacket(WrapCITC.getMTSFailBuy());
                    return true;
                }
                if (chr.getNexonPoint() < mts.getRealPrice()) {
                    chr.SendPacket(WrapCITC.getMTSFailBuy());
                    return true;
                }
                if (!MTSStorage.getInstance().removeFromBuyNow(mts.getId(), chr.getId(), false)) {
                    chr.SendPacket(WrapCITC.getMTSFailBuy());
                    return true;
                }
                chr.modifyCSPoints(1, -mts.getRealPrice(), false);
                MTSStorage.getInstance().getCart(mts.getCharacterId()).increaseOwedNX(mts.getPrice());
                c.getSession().write(WrapCITC.getMTSConfirmBuy());
                sendMTSPackets(cart, c, true);
                return true;
            }
            case ITCReq_BuyZzimItem: {
                int unk1 = cp.Decode4();
                // 17

                MTSStorage.MTSItemInfo mts = MTSStorage.getInstance().getSingleItem(unk1);
                if (mts == null) {
                    break;
                }
                // TODO : account checks
                if (mts.getCharacterId() == chr.getId()) {
                    chr.SendPacket(WrapCITC.getMTSFailBuy());
                    return true;
                }
                if (chr.getNexonPoint() < mts.getRealPrice()) {
                    chr.SendPacket(WrapCITC.getMTSFailBuy());
                    return true;
                }
                if (!MTSStorage.getInstance().removeFromBuyNow(mts.getId(), chr.getId(), false)) {
                    chr.SendPacket(WrapCITC.getMTSFailBuy());
                    return true;
                }
                chr.modifyCSPoints(1, -mts.getRealPrice(), false);
                MTSStorage.getInstance().getCart(mts.getCharacterId()).increaseOwedNX(mts.getPrice());
                c.getSession().write(WrapCITC.getMTSConfirmBuy());
                sendMTSPackets(cart, c, true);
                return true;
            }
            case ITCReq_RegAuction: {
                // 18
                // item info.
                byte inv_type = cp.Decode1();
                int item_id = cp.Decode4();
                //
                int item_slot = cp.Decode4();
                int item_quantity = cp.Decode4();
                int price_start = cp.Decode4();
                int price_end = cp.Decode4();
                byte hours = cp.Decode1(); // 24 to 168
                byte unk1 = cp.Decode1(); // 01
                int price_range = cp.Decode4();
                break;
            }
            case ITCReq_BidAuction: {
                break;
            }
            case ITCReq_BuyAuctionImm: {
                break;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnITCItemRequest : not coded = " + ops_req + "(" + req + ")");
        return false;
    }

    private static void doMTSPackets(final MTSCart cart, final MapleClient c) {
        sendMTSPackets(cart, c, false);
    }

    public static void MTSUpdate(final MTSCart cart, final MapleClient c) {
        c.getPlayer().modifyCSPoints(1, MTSStorage.getInstance().getCart(c.getPlayer().getId()).getSetOwedNX(), false);
        c.SendPacket(WrapCITC.getMTSWantedListingOver(0, 0));
        doMTSPackets(cart, c);
    }

    private static void sendMTSPackets(final MTSCart cart, final MapleClient c, final boolean changed) {
        c.SendPacket(MTSStorage.getInstance().getCurrentMTS(cart));
        c.SendPacket(MTSStorage.getInstance().getCurrentNotYetSold(cart));
        c.SendPacket(MTSStorage.getInstance().getCurrentTransfer(cart, changed));
        c.SendPacket(ResCITC.ITCQueryCashResult(c.getPlayer()));
        MTSStorage.getInstance().checkExpirations();
    }

}
