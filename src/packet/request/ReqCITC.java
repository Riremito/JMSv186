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
package packet.request;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import debug.DebugLogger;
import packet.ClientPacket;
import packet.ops.OpsITC;
import static packet.ops.OpsITC.ITCReq_RegisterSaleEntry;
import packet.response.ResCITC;
import packet.response.wrapper.WrapCITC;
import server.MTSCart;
import server.MTSStorage;
import server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class ReqCITC {

    public static boolean OnPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) {
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
                break;
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
                break;
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
                break;
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
        /*
        if (req == 2) {
            //put up for sale
            final byte invType = slea.readByte(); //1 = equip 2 = everything else
            if (invType != 1 && invType != 2) {
                //pet?
                c.getSession().write(WrapCITC.getMTSFailSell());
                doMTSPackets(cart, c);
                return;
            }
            final int itemid = slea.readInt(); //itemid
            if (slea.readByte() != 0) {
                c.getSession().write(WrapCITC.getMTSFailSell());
                doMTSPackets(cart, c);
                return; //we don't like uniqueIDs
            }
            slea.skip(8); //expiration, don't matter
            short stars = 1;
            short quantity = 1;
            byte slot = 0;
            if (invType == 1) {
                slea.skip(32);
            } else {
                stars = slea.readShort(); //the entire quantity of the item
            }
            slea.readMapleAsciiString(); //owner
            //again? =/
            if (invType == 1) {
                slea.skip(48);
                slot = (byte) slea.readInt();
                slea.skip(4); //skip the quantity int, equips are always 1
            } else {
                slea.readShort(); //flag
                if (GameConstants.isThrowingStar(itemid) || GameConstants.isBullet(itemid)) {
                    slea.skip(8); //recharge ID thing
                }
                slot = (byte) slea.readInt();
                if (GameConstants.isThrowingStar(itemid) || GameConstants.isBullet(itemid)) {
                    quantity = stars; //this is due to stars you need to use the entire quantity, not specified
                    slea.skip(4); //so just skip the quantity int
                } else {
                    quantity = (short) slea.readInt(); //specified quantity
                }
            }
            final int price = slea.readInt();
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final MapleInventoryType type = GameConstants.getInventoryType(itemid);
            final IItem item = c.getPlayer().getInventory(type).getItem(slot).copy();
            if (ii.isCash(itemid) || quantity <= 0 || item == null || item.getQuantity() <= 0 || item.getItemId() != itemid || item.getUniqueId() > 0 || item.getQuantity() < quantity || price < ServerConstants.MIN_MTS || c.getPlayer().getMeso() < ServerConstants.MTS_MESO || cart.getNotYetSold().size() >= 10 || ii.isDropRestricted(itemid) || ii.isAccountShared(itemid) || item.getExpiration() > -1 || item.getFlag() > 0) {
                c.getSession().write(WrapCITC.getMTSFailSell());
                doMTSPackets(cart, c);
                return;
            }
            if (type == MapleInventoryType.EQUIP) {
                final Equip eq = (Equip) item;
                if (eq.getHidden() > 0 || eq.getEnhance() > 0 || eq.getDurability() > -1) {
                    c.getSession().write(WrapCITC.getMTSFailSell());
                    doMTSPackets(cart, c);
                    return;
                }
            }
            final long expiration = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000);
            item.setQuantity(quantity);
            MTSStorage.getInstance().addToBuyNow(cart, item, price, c.getPlayer().getId(), c.getPlayer().getName(), expiration);
            MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
            c.getPlayer().gainMeso(-ServerConstants.MTS_MESO, false);
            c.getSession().write(WrapCITC.getMTSConfirmSell());
        }
        doMTSPackets(cart, c);
         */
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
