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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import tacos.config.DeveloperMode;
import tacos.database.ops.InvTypeDB;
import tacos.database.query.DQ_Inventoryitems;
import tacos.database.query.DQ_Storages;
import tacos.debug.DebugLogger;
import tacos.odin.OdinPair;
import tacos.packet.ops.OpsDBCHAR;
import tacos.wz.ids.DWI_Validation;

/**
 *
 * @author Riremito
 */
public class TacosStorage {

    private int account_id;
    private int meso = 0;
    private int slot = DeveloperMode.DM_INV_SLOT_STORAGE.getInt();
    private ArrayList<IItem> items = new ArrayList<>();
    private boolean changed = false;

    public TacosStorage(TacosCharacter chr) {
        this.account_id = chr.getAccountId();
    }

    public int getAccountId() {
        return this.account_id;
    }

    public int getMeso() {
        return this.meso;
    }

    public boolean setMeso(int meso) {
        this.changed = true;
        this.meso = meso;
        return true;
    }

    public int getSlot() {
        return this.slot;
    }

    public boolean setSlot(int slot) {
        this.changed = true;
        this.slot = slot;
        return true;
    }

    public ArrayList<IItem> getItems() {
        return this.items;
    }

    public boolean load() {
        if (DQ_Storages.load(this)) {
            DebugLogger.DebugLog("storage is found.");
            for (OdinPair<IItem, MapleInventoryType> mit : DQ_Inventoryitems.load(InvTypeDB.Trunk, this.account_id).values()) {
                this.items.add(mit.getLeft());
            }
            return true;
        }
        DebugLogger.DebugLog("storage is created.");
        return DQ_Storages.create(this);
    }

    public boolean update() {
        if (!this.changed) {
            return false;
        }
        this.changed = false;
        DQ_Storages.update(this);
        DQ_Inventoryitems.add(InvTypeDB.Trunk, this.account_id, this.items);
        return true;
    }
    private int npc_id = 1012003;

    public int getNpcId() {
        return this.npc_id;
    }

    public boolean setNpcId(int npc_id) {
        if (!DWI_Validation.isValidNPCID(npc_id)) {
            return false;
        }
        this.npc_id = npc_id;
        return true;
    }

    private OpsDBCHAR lastModified = OpsDBCHAR.DBCHAR_ALL;

    public OpsDBCHAR getLastModified() {
        return this.lastModified;
    }

    public boolean setLastModified(int type) {
        switch (type) {
            case 1: {
                setLastModified(OpsDBCHAR.DBCHAR_ITEMSLOTEQUIP);
                return true;
            }
            case 2: {
                setLastModified(OpsDBCHAR.DBCHAR_ITEMSLOTCONSUME);
                return true;
            }
            case 3: {
                setLastModified(OpsDBCHAR.DBCHAR_ITEMSLOTINSTALL);
                return true;
            }
            case 4: {
                setLastModified(OpsDBCHAR.DBCHAR_ITEMSLOTETC);
                return true;
            }
            case 5: {
                setLastModified(OpsDBCHAR.DBCHAR_ITEMSLOTCASH);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public void setLastModified(OpsDBCHAR lastModified) {
        this.lastModified = lastModified;
    }

    public List<IItem> filterItems(MapleInventoryType type) {
        List<IItem> ret = new LinkedList<>();

        for (IItem item : this.items) {
            if (GameConstants.getInventoryType(item.getItemId()) == type) {
                ret.add(item);
            }
        }
        return ret;
    }

    public boolean isFull() {
        return this.items.size() >= this.slot;
    }

    public IItem findById(int itemId) {
        for (IItem item : this.items) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    public IItem getItem(int type, int slot) {
        MapleInventoryType mit = MapleInventoryType.getByType((byte) type);

        if (mit == MapleInventoryType.UNDEFINED) {
            return null;
        }

        if (slot < 0) {
            return null;
        }

        int count = 0;

        for (IItem item : this.items) {
            if (GameConstants.getInventoryType(item.getItemId()) == mit) {
                if (count == slot) {
                    this.changed = true;
                    this.items.remove(item);
                    return item;
                }
                count++;
            }
        }

        return null;
    }

    public void putItem(IItem item) {
        this.changed = true;
        this.items.add(item);
    }

    public boolean sortItem() {
        return true;
    }

}
