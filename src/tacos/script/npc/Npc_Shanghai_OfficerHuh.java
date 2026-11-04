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
public class Npc_Shanghai_OfficerHuh extends NpcScriptMan {

    private final int map_boss = 701010323; // 中国 : 危険な丘陵

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // 原文ママ
                nt.add("大王ムカデを倒しに来た方ですね。では、任務遂行にため、大王ムカデがいるところに行ってみましょう。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                // TODO : 10分の制限時間, 大王ムカデの召喚
                TacosMap map = getPlayer().findMap(this.map_boss);
                if (map != null && map.getCharactersSize() == 0) {
                    map.resetFully();
                }
                warp(this.map_boss);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
