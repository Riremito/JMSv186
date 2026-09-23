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
import tacos.client.TacosCharacter;
import tacos.packet.ops.OpsMovePathAttr;
import tacos.packet.request.parse.ParseCMovePath;

/**
 *
 * @author Riremito
 */
public class TacosMapObject {

    private static int OBJECT_ID = 100000;

    private int object_id = 0;
    private long time_created = 0;
    private int owner_id = 0;
    private Point position = new Point();
    private int move_action = OpsMovePathAttr.MPA_NORMAL.get();
    private int foothold_id = 0;

    public int getObjectId() {
        return this.object_id;
    }

    public void setObjectId() {
        this.object_id = OBJECT_ID++;
        setTimeCreated();
    }

    public void setObjectId(int object_id) {
        this.object_id = object_id;
        setTimeCreated(); // TODO : move
    }

    public long getTimeCreated() {
        return this.time_created;
    }

    public void setTimeCreated() {
        this.time_created = System.currentTimeMillis();
    }

    public boolean checkTime(long time_current, int duration) {
        if (this.time_created + duration <= time_current) {
            return true;
        }
        return false;
    }

    public int getOwnerId() {
        return this.owner_id;
    }

    public void setOwnerId(int owner_id) {
        this.owner_id = owner_id;
    }

    public Point getPosition() {
        return this.position.getLocation();
    }

    public void setPosition(Point position) {
        this.position.setLocation(position);
    }

    public void setPosition(int x, int y) {
        this.position.setLocation(x, y);
    }

    public int getX() {
        return this.position.x;
    }

    public int getY() {
        return this.position.y;
    }

    public int getMoveAction() {
        return this.move_action;
    }

    public int getFootHoldId() {
        return this.foothold_id;
    }

    public void update(ParseCMovePath move_path) {
        setPosition(move_path.getX(), move_path.getY());
        this.move_action = move_path.getMoveAction();
        this.foothold_id = move_path.getFootHoldId();
    }

    public void reset(TacosCharacter chr) {
        this.owner_id = chr.getId();
        setPosition(chr.getPosition());
        this.move_action = OpsMovePathAttr.MPA_NORMAL.get();
        this.foothold_id = chr.getFH();
    }
}
