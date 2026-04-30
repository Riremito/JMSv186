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
package tacos.client;

import tacos.packet.ops.OpsMovePathAttr;
import tacos.packet.request.parse.ParseCMovePath;

/**
 *
 * @author Riremito
 */
public class TacosObject {

    private int id;
    private int x;
    private int y;
    private int move_action;
    private int foothold_id;

    public TacosObject(int id, int x, int y, int move_action, int foothold_id) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.move_action = move_action;
        this.foothold_id = foothold_id;
    }

    public int getId() {
        return this.id;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getMoveAction() {
        return this.move_action;
    }

    public int getFootHoldId() {
        return this.foothold_id;
    }

    public void update(ParseCMovePath move_path) {
        this.x = move_path.getX();
        this.y = move_path.getY();
        this.move_action = move_path.getMoveAction();
        this.foothold_id = move_path.getFootHoldId();
    }

    public void reset(TacosCharacter chr) {
        this.x = chr.getPosition().x;
        this.y = chr.getPosition().y;
        this.move_action = OpsMovePathAttr.MPA_NORMAL.get();
        this.foothold_id = chr.getFH();
    }

}
