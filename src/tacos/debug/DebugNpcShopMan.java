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
package tacos.debug;

import tacos.constants.TacosConstants;
import tacos.script.npc.NpcShopMan;
import tacos.wz.WzDataStorage;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class DebugNpcShopMan extends NpcShopMan {

    public DebugNpcShopMan() {
        super(TacosConstants.DEFAULT_NPC_ID);
    }

    public boolean setItemTest(int item_sub_type) {
        int item_count = 0;
        for (int item_id : WzXML.ITEM.getItemIds(item_sub_type)) {
            addItem(item_id);
            item_count++;
        }

        DebugLogger.InfoLog("DebugNpcShopMan : setItemTest(" + item_sub_type + "), loaded " + item_count + " items.");
        return true;
    }

    public boolean setRandomItems(int count) {
        for (int i = 0; i < count; i++) {
            this.addItem(WzDataStorage.ITEM.getRandom());
        }
        return true;
    }
}
