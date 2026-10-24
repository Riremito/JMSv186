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
package tacos.client;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import tacos.database.InvTypeDB;
import tacos.database.query.DQ_Hiredmerch;
import tacos.database.query.DQ_Inventoryitems;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsEntrustedShop;
import tacos.packet.response.ResCStoreBankDlg;
import tacos.server.map.object.TacosMerchant;

/**
 * The store bank (Fredrick) keeps the goods of a closed hired merchant until
 * the owner takes them. The items are inventoryitems rows (InvTypeDB.StoreBank)
 * and the meso is the owner's hiredmerch row, which belongs to the open hired
 * merchant while there is one.
 *
 * @author Riremito
 */
public class TacosStoreBank {

    // field id of the LoadFailed message when nothing is stored.
    private static final int NO_MERCHANT_FIELD = 999999999;

    public static TacosMerchant findMerchant(TacosCharacter chr) {
        return chr.getWorld().findMerchant(chr.getId());
    }

    public static TacosHoldInventory load(TacosCharacter chr) {
        TacosHoldInventory goods = new TacosHoldInventory();
        for (SimpleImmutableEntry<Item, MapleInventoryType> mit : DQ_Inventoryitems.load(InvTypeDB.StoreBank, chr.getId()).values()) {
            goods.addItem(mit.getKey());
        }
        if (findMerchant(chr) == null) {
            DQ_Hiredmerch.HiredmerchRow row = DQ_Hiredmerch.load(chr.getId());
            if (row != null) {
                goods.setMeso(row.meso);
            }
        }
        return goods;
    }

    public static boolean isEmpty(TacosCharacter chr) {
        return load(chr).isEmpty();
    }

    // the goods of a closed hired merchant, they have been saved as InvTypeDB.Employee.
    public static boolean putMerchantGoods(int character_id) {
        if (!DQ_Inventoryitems.changeType(InvTypeDB.Employee, InvTypeDB.StoreBank, character_id)) {
            return false;
        }
        return DQ_Hiredmerch.updateTime(character_id, System.currentTimeMillis());
    }

    // items that could not go back to the inventory.
    public static boolean putItems(int character_id, List<Item> items) {
        ArrayList<Item> stored = new ArrayList<>();
        for (SimpleImmutableEntry<Item, MapleInventoryType> mit : DQ_Inventoryitems.load(InvTypeDB.StoreBank, character_id).values()) {
            stored.add(mit.getKey());
        }
        stored.addAll(items);
        return DQ_Inventoryitems.add(InvTypeDB.StoreBank, character_id, stored);
    }

    // the store bank npc is selected.
    public static ServerPacket open(TacosCharacter chr, int npc_id) {
        TacosMerchant merchant = findMerchant(chr);
        if (merchant != null) {
            return ResCStoreBankDlg.StoreBankLoadFailed(npc_id, merchant.getMap().getId(), merchant.getChannel() - 1);
        }
        TacosHoldInventory goods = load(chr);
        if (goods.isEmpty()) {
            return ResCStoreBankDlg.StoreBankLoadFailed(npc_id, NO_MERCHANT_FIELD, 0);
        }
        return ResCStoreBankDlg.OpenStoreBankDlg(npc_id, goods);
    }

    public static int getPassingDay(TacosCharacter chr) {
        DQ_Hiredmerch.HiredmerchRow row = DQ_Hiredmerch.load(chr.getId());
        if (row == null) {
            return 0;
        }
        return (int) ((System.currentTimeMillis() - row.time) / (24L * 60 * 60 * 1000));
    }

    // CStoreBankDlg::SendGetAllRequest, no fee is taken.
    public static OpsEntrustedShop getAll(TacosCharacter chr) {
        if (findMerchant(chr) != null) {
            return OpsEntrustedShop.StoreBankRes_GetAll_NoSlot;
        }
        TacosHoldInventory goods = load(chr);
        if (!goods.hasMesoRoomIn(chr)) {
            return OpsEntrustedShop.StoreBankRes_GetAll_OverPrice;
        }
        if (goods.hasOnlyItemIn(chr)) {
            return OpsEntrustedShop.StoreBankRes_GetAll_OnlyItem;
        }
        if (!goods.hasRoomIn(chr)) {
            return OpsEntrustedShop.StoreBankRes_GetAll_NoSlot;
        }
        goods.returnTo(chr);
        // what could not be returned stays in the store bank.
        DQ_Inventoryitems.add(InvTypeDB.StoreBank, chr.getId(), goods.getItems());
        if (goods.getMeso() == 0) {
            DQ_Hiredmerch.deleteByCharacterId(chr.getId());
        }
        chr.saveToDB(false);
        return OpsEntrustedShop.StoreBankRes_GetAll_Done;
    }
}
