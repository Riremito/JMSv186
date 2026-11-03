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
public class NpcPalakeen extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章
                nt.add("");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                if (getPlayer().getMapId() == 800000000) {
                    // キノコ神社 -> 楓城 天下泰平
                    warp(800040000); // TODO : portal name.
                } else {
                    // 楓城 天下泰平 -> キノコ神社
                    warp(800000000); // TODO : portal name.
                }
                return false;
            }
            case -1 -> {
                // not coded yet.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
