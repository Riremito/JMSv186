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
package tacos.script.portal;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : EvanDbPortaljp (JMS186), js の処理を利用
public class Portal_Kerning_EvanDbPortaljp extends PortalScriptMan {

    private final int map_room_of_fire = 103050101; // 秘桜蔭 : 千火の部屋
    private final int map_floor2 = 103050100; // 秘桜蔭 : 秘桜蔭 2階

    @Override
    public boolean enter() {
        if (getMapId() == this.map_room_of_fire) {
            return warp(this.map_floor2, "in00");
        }
        return false;
    }
}
