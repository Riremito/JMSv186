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
package tacos.wz.data;

import tacos.wz.TacosWz;
import tacos.config.Content;
import odin.provider.IMapleDataProvider;

/**
 *
 * @author Riremito
 */
public class MapWz {

    private static TacosWz wz = null;

    private static TacosWz getWz() {
        if (wz == null) {
            wz = new TacosWz(Content.Wz_SingleFile.get() ? "Data.wz/Map" : "Map.wz");
        }
        return wz;
    }

    public static IMapleDataProvider getWzRoot() {
        return getWz().getWzRoot();
    }
}
