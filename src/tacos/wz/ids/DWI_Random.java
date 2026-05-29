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
package tacos.wz.ids;

import tacos.wz.WzDataStorage;
import java.util.Random;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class DWI_Random {

    public static int getMapIndex(int id) {
        return WzDataStorage.MAP.getIds().indexOf(id);
    }

    public static int getMapByIndex(int index) {
        if (index < 0 || WzDataStorage.MAP.getIds().size() <= index) {
            return -1;
        }
        return WzDataStorage.MAP.getIds().get(index);
    }

    public static int getRandomPotential(int rank) {
        Random rand = new Random();
        if (rank == 1) {
            return WzXML.ITEM.getRarePotential().get(rand.nextInt(WzXML.ITEM.getRarePotential().size()));
        }
        if (rank == 2) {
            return WzXML.ITEM.getEpicPotential().get(rand.nextInt(WzXML.ITEM.getEpicPotential().size()));
        }
        if (rank == 3) {
            return WzXML.ITEM.getUniquePotential().get(rand.nextInt(WzXML.ITEM.getUniquePotential().size()));
        }
        if (rank == 4) {
            return WzXML.ITEM.getLegendaryPotential().get(rand.nextInt(WzXML.ITEM.getLegendaryPotential().size()));
        }
        return 0;
    }
}
