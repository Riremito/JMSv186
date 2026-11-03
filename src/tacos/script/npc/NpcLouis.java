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
public class NpcLouis extends NpcScriptMan {

    private final int map_ellinia = 101000000; // エリニア : エリニア

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("外へ出たいのか？　辛いのか…このくらいであきらめるなよ。一度ここから出ると、初めからやり直しだということは覚悟しているな。どうだ、出るのか？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                warp(this.map_ellinia); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
