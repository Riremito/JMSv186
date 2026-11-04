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
public class Npc_Taiwan_Melonon extends NpcScriptMan {

    private final int map_boss = 741020101; // 台湾 : 夜市場裏道1

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 前提クエスト 資格のテスト
                // 本当に屋台を倒せる勇敢なものがいないか…
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("君なら屋台を倒せるだろう。では、屋台があるところに送ってあげよう。気を付けてね！");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                // TODO : 移動先のマップ (741020101 ランダム?)
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
