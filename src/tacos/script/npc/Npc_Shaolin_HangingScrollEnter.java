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

import tacos.server.map.TacosMap;

/**
 *
 * @author Riremito
 */
public class Npc_Shaolin_HangingScrollEnter extends NpcScriptMan {

    private final int map_boss = 702060000; // 中国 : 修行の間

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : グループの確認
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("グループを結成していますね。それではあなた達を修行の間に送ります。幸運を祈ります～");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                TacosMap map = findMap(this.map_boss);
                if (map != null && map.getCharactersSize() == 0) {
                    map.resetFully();
                }
                warp(this.map_boss);
                return false;
            }
            case -1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("残念ですね…。後で時間ができましたら、ご利用ください。");
                say(nt);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
