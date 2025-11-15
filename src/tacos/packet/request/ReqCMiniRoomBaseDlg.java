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
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsEntrustedShop;
import tacos.packet.ops.OpsMiniRoomProtocol;
import tacos.packet.ops.OpsMiniRoomType;
import tacos.packet.response.ResCEmployeePool;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapObjectType;
import odin.server.shops.HiredMerchant;
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
                return true;
            }
            case MRP_Balloon: {
                return true;
            }
            default: {
                break;
            }
        }

        //PlayerInteractionHandler.PlayerInteraction(p, chr.getClient(), chr);
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
