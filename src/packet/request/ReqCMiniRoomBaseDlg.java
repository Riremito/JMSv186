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
import debug.Debug;
import java.util.List;
import packet.ClientPacket;
import packet.response.ResCField;
import packet.response.ResCMiniRoomBaseDlg;
import server.maps.MapleMap;
import server.shops.HiredMerchant;
import tools.Pair;

/**
 *
 * @author Riremito
 */
public class ReqCMiniRoomBaseDlg {

    public static boolean OnMiniRoom(MapleMap map, MapleCharacter chr, ClientPacket cp) {

        //PlayerInteractionHandler.PlayerInteraction(p, chr.getClient(), chr);
        return false;
    }

    // 雇用商店遠隔管理機
    public static boolean RemoteStore(MapleCharacter chr, short item_slot) {
        Runnable item_use = chr.checkItemSlot(item_slot, 5470000);

        if (item_use == null) {
            Debug.ErrorLog("RemoteStore : no item.");
            return false;
        }

        final HiredMerchant merchant = (HiredMerchant) chr.getRemoteStore();
        if (merchant == null) {
            // test
            chr.SendPacket(ResCMiniRoomBaseDlg.EnterResultStatic(chr));
            return false;
        }

        merchant.setOpen(false);

        // "商店の主人が物品整理中でございます。もうしばらく後でご利用ください。"
        List<Pair<Byte, MapleCharacter>> visitors = merchant.getVisitors();
        for (int i = 0; i < visitors.size(); i++) {
            visitors.get(i).getRight().getClient().getSession().write(ResCField.MaintenanceHiredMerchant((byte) i + 1));
            visitors.get(i).getRight().setPlayerShop(null);
            merchant.removeVisitor(visitors.get(i).getRight());
        }

        chr.setPlayerShop(merchant);
        chr.SendPacket(ResCField.getHiredMerch(chr, merchant, false));
        return true;
    }

}
