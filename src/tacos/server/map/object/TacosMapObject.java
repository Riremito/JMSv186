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

import java.awt.Point;

/**
 *
 * @author Riremito
 */
public class TacosMapObject {

    private static int OBJECT_ID = 100000;

    private int object_id = 0;
    private Point position = new Point();

    public int getObjectId() {
        return object_id;
    }

    public void setObjectId() {
        this.object_id = OBJECT_ID++;
    }

    public void setObjectId(int object_id) {
        this.object_id = object_id;
    }

    public Point getPosition() {
        return this.position.getLocation();
    }

    public void setPosition(Point position) {
        this.position.setLocation(position);
    }
}
