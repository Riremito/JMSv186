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
public class Npc_MonsterCarnival_Gingerman extends NpcScriptMan {

    private final int map_witch_tower_entrance = 980040000; // 魔女の塔 : 魔女の塔入口
    private final int MENU_EXIT = 0;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 文章 (適当)
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("わあ、最上階まで来られた！");
                nt.addMenu(MENU_EXIT, "ここから出たい。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer != MENU_EXIT) {
                    return false;
                }
                warp(this.map_witch_tower_entrance);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
