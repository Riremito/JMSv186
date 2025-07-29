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
package data.wz.ids;

import data.wz.DW_Item;
import java.util.Random;

/**
 *
 * @author Riremito
 */
public class DWI_Random {

    public static int getMapIndex(int id) {
        return DWI_LoadXML.getMap().getIds().indexOf(id);
    }

    public static int getMapByIndex(int index) {
        if (index < 0 || DWI_LoadXML.getMap().getIds().size() <= index) {
            return -1;
        }
        return DWI_LoadXML.getMap().getIds().get(index);
    }

    public static int getRandomPotential(int rank) {
        Random rand = new Random();
        if (rank == 1) {
            return DW_Item.getRarePotential().get(rand.nextInt(DW_Item.getRarePotential().size()));
        }
        if (rank == 2) {
            return DW_Item.getEpicPotential().get(rand.nextInt(DW_Item.getEpicPotential().size()));
        }
        if (rank == 3) {
            return DW_Item.getUniquePotential().get(rand.nextInt(DW_Item.getUniquePotential().size()));
        }
        if (rank == 4) {
            return DW_Item.getLegendaryPotential().get(rand.nextInt(DW_Item.getLegendaryPotential().size()));
        }
        return 0;
    }

}
