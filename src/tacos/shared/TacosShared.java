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
package tacos.shared;

import java.util.Map;
import odin.client.inventory.IEquip;
import odin.server.MapleItemInformationProvider;

/**
 *
 * @author Riremito
 */
public class TacosShared {

    public static int getDurabilityMax(IEquip equip) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Map<String, Integer> eqStats = ii.getEquipStats(equip.getItemId());
        int durability_max = eqStats.get("durability");

        return durability_max;
    }

    public static int getRepairPrice(IEquip equip) {
        int item_id = equip.getItemId();
        int target_durability = equip.getDurability();
        int durability_max = getDurabilityMax(equip);

        if (target_durability < 0 || durability_max < 0 || durability_max <= target_durability) {
            return 0;
        }

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        double per = target_durability * 100.0 / durability_max;
        double price_t = ii.getPrice(item_id) * 100.0 / durability_max * 200.0;
        int price = (int) (price_t * per);

        return price;
    }
}
