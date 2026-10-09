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
package tacos.server.map;

import lombok.Data;

/**
 *
 * @author Riremito
 */
@Data
public class TacosFoothold {

    private int id;
    private int next;
    private int prev;
    private int x1;
    private int y1;
    private int x2;
    private int y2;

    public boolean isWall() {
        return this.x1 == this.x2;
    }
}
