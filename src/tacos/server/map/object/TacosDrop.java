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
package tacos.server.map.object;

import odin.client.inventory.Item;

/**
 *
 * @author Riremito
 */
public class TacosDrop extends TacosMapObject {

    private Item item = null;
    private int quest_id = 0;
    private int meso = 0;

    public TacosDrop(Item item, int quest_id, int meso) {
        this.item = item;
        this.quest_id = quest_id;
        this.meso = meso;
    }

    public Item getItem() {
        return this.item;
    }

    public int getItemId() {
        if (getMeso() > 0) {
            return this.meso;
        }
        return this.item.getItemId();
    }

    public int getMeso() {
        return this.meso;
    }

    public int getQuestId() {
        return this.quest_id;
    }
}
