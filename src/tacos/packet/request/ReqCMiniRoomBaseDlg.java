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
import tacos.wz.ids.DWI_LoadXML;
import tacos.debug.DebugLogger;
import java.util.List;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleTrade;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsEntrustedShop;
import tacos.packet.ops.OpsMiniRoomProtocol;
import tacos.packet.ops.OpsMiniRoomType;
import tacos.packet.response.ResCEmployeePool;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapObjectType;
import odin.server.shops.HiredMerchant;
import odin.server.shops.IMaplePlayerShop;
import odin.tools.Pair;

/**
 *
 * @author Riremito
 */
public class ReqCMiniRoomBaseDlg {

    public static boolean OnMiniRoom(MapleMap map, MapleCharacter chr, ClientPacket cp) {
        byte protocol_req = cp.Decode1();

        switch (OpsMiniRoomProtocol.find(protocol_req)) {
            case MRP_Create: {
                byte miniroom_type = cp.Decode1();
                if (OpsMiniRoomType.find(miniroom_type) == OpsMiniRoomType.MR_EntrustedShop) {
                    String title = cp.DecodeStr();
                    byte es_req = cp.Decode1();
                    if (OpsEntrustedShop.find(es_req) != OpsEntrustedShop.EntrustedShopReq_CheckOpenPossible) {
                        return false;
                    }
                    short cash_item_slot = cp.Decode2();
                    int cash_item_id = cp.Decode4();

                    int cash_item_type = cash_item_id / 10000;
                    if (cash_item_type != 503 || !DWI_LoadXML.getItem().isValidID(cash_item_id)) {
                        return false;
                    }

                    Runnable item_use = chr.checkItemSlot(cash_item_slot, cash_item_id);
                    if (item_use == null) {
                        return false;
                    }

                    return true;
                }
                return false;
            }
            case MRP_Invite: {
                int character_id = cp.Decode4();
                MapleTrade.inviteTrade(chr, chr.getMap().getCharacterById(character_id));
                return true;
            }
            case MRP_InviteResult: {
                MapleTrade.declineTrade(chr);
                return true;
            }
            case MRP_Enter: {
                int miniroom_id = cp.Decode4();
                byte unk = cp.Decode1();

                HiredMerchant hm = (HiredMerchant) chr.getMap().getMapObject(miniroom_id, MapleMapObjectType.HIRED_MERCHANT);
                if (hm == null) {
                    DebugLogger.ErrorLog("hm == null.");
                    return false;
                }
                //hm.addVisitor(chr);
                chr.SendPacket(ResCMiniRoomBaseDlg.EnterResultStatic(hm, chr));
                return true;
            }
            case MRP_Chat: {
                int unk = cp.Decode4();
                String message = cp.DecodeStr();
                if (chr.getTrade() != null) {
                    chr.getTrade().chat(message);
                    return true;
                }
                if (chr.getPlayerShop() != null) {
                    IMaplePlayerShop ips = chr.getPlayerShop();
                    ips.broadcastToVisitors(ResCMiniRoomBaseDlg.shopChat(chr.getName() + " : " + message, ips.getVisitorSlot(chr)));
                    return true;
                }
                DebugLogger.ErrorLog("OnMiniRoom : MRP_Chat, not  coded.");
                return true;
            }
            case MRP_GameMessage: {
                return true;
            }
            case MRP_UserChat: {
                return true;
            }
            case MRP_Avatar: {
                return true;
            }
            case MRP_Leave: {
                if (chr.getTrade() != null) {
                    MapleTrade.cancelTrade(chr.getTrade(), chr.getClient());
                    return true;
                }
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null) {
                    return true;
                }
                if (!ips.isAvailable() || (ips.isOwner(chr) && ips.getShopType() != 1)) {
                    ips.closeShop(false, ips.isAvailable(), 3);
                } else {
                    ips.removeVisitor(chr);
                }
                chr.setPlayerShop(null);
                return true;
            }
            case MRP_Balloon: {
                return true;
            }
            case TRP_PutItem: {
                MapleTrade trade = chr.getTrade();
                if (trade == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : TRP_PutItem, trade");
                    return true;
                }

                byte item_type = cp.Decode1();
                short item_slot = cp.Decode2();
                short quantity = cp.Decode2();
                byte targetSlot = cp.Decode1();

                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                MapleInventoryType ivType = MapleInventoryType.getByType(item_type);
                IItem item = chr.getInventory(ivType).getItem(item_slot);

                if (item == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : TRP_PutItem, item");
                    return true;
                }

                if ((quantity <= item.getQuantity() && quantity >= 0) || GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                    chr.getTrade().setItems(chr.getClient(), item, targetSlot, quantity);
                }
                return true;
            }
            case TRP_PutMoney: {
                MapleTrade trade = chr.getTrade();
                if (trade == null) {
                    return true;
                }
                int mesos = cp.Decode4();
                trade.setMeso(mesos);
                return true;
            }
            case TRP_Trade: {
                if (chr.getTrade() == null) {
                    return true;
                }
                MapleTrade.completeTrade(chr);
                return true;
            }
            case ESP_AdminChangeTitle: {
                // GMが右クリックした場合の雇用商人の名前を替えますか？でOKを押したときに送信されるデータ
                // @007F 2A [BC 7D 00 00 (ID)]
                return true;
            }
            case ESP_DeliverVisitList: {
                HiredMerchant merchant = chr.getMyHiredMerchant();
                if (merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_DeliverVisitList");
                    return true;
                }
                merchant.sendVisitor(chr);
                return true;
            }
            case ESP_DeliverBlackList: {
                HiredMerchant merchant = chr.getMyHiredMerchant();
                if (merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_DeliverBlackList");
                    return true;
                }
                merchant.sendBlackList(chr);
                return true;
            }
            case ESP_AddBlackList: {
                HiredMerchant merchant = chr.getMyHiredMerchant();
                if (merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_AddBlackList");
                    return true;
                }
                String character_name = cp.DecodeStr(); // sName
                merchant.addBlackList(character_name);
                return true;
            }
            case ESP_DeleteBlackList: {
                HiredMerchant merchant = chr.getMyHiredMerchant();
                if (merchant == null) {
                    DebugLogger.ErrorLog("OnMiniRoom : ESP_DeleteBlackList");
                    return true;
                }
                String character_name = cp.DecodeStr(); // sName
                merchant.removeBlackList(character_name);
                return true;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnMiniRoom : not coded = " + protocol_req);
        return false;
    }

    // 雇用商店遠隔管理機
    public static boolean RemoteStore(MapleCharacter chr, short item_slot) {
        Runnable item_use = chr.checkItemSlot(item_slot, 5470000);

        if (item_use == null) {
            DebugLogger.ErrorLog("RemoteStore : no item.");
            return false;
        }

        final HiredMerchant merchant = (HiredMerchant) chr.getRemoteStore();
        if (merchant == null) {
            // test
            //chr.SendPacket(ResCMiniRoomBaseDlg.EnterResultStaticTest(chr));

            HiredMerchant hm = new HiredMerchant(chr, 5030000, "DebugHiredMarchant");
            chr.getMap().addMapObject(hm);
            chr.SendPacket(ResCEmployeePool.EmployeeEnterField(hm));
            return false;
        }

        merchant.setOpen(false);

        // "商店の主人が物品整理中でございます。もうしばらく後でご利用ください。"
        List<Pair<Byte, MapleCharacter>> visitors = merchant.getVisitors();
        for (int i = 0; i < visitors.size(); i++) {
            visitors.get(i).getRight().getClient().getSession().write(ResCMiniRoomBaseDlg.MaintenanceHiredMerchant((byte) i + 1));
            visitors.get(i).getRight().setPlayerShop(null);
            merchant.removeVisitor(visitors.get(i).getRight());
        }

        chr.setPlayerShop(merchant);
        chr.SendPacket(ResCMiniRoomBaseDlg.getHiredMerch(chr, merchant, false));
        return true;
    }

}
