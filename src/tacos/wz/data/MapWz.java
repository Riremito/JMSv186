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

import odin.provider.IMapleData;
import tacos.config.Content;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class MapWz extends WzXML {

    public MapWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Map" : "Map.wz");
    }

    public IMapleData getImg(int map_id) {
        String target_img_path = String.format("Map/Map%d/%09d.img", (map_id / 100000000), map_id);
        return getData(target_img_path);
    }
}
