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
package tacos.script.npc;

/**
 *
 * @author Riremito
 */
public class NpcSmallStreetLight extends NpcScriptMan {

    private final int mob_shade = 5090000; // シェード

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                getPlayer().getMap().killMonster(this.mob_shade);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
