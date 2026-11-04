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
public class Npc_Ludibrium_GatekeeperMoonBunny extends NpcScriptMan {

    private final int map_moon = 922230000; // 隠れ道 : 月の国
    private final int[] map_ids = {
        922230100, // 隠れ道 : 灯る月花庭園
        922230200, // 隠れ道 : 光る月花庭園
        922230300 // 隠れ道 : 煌く月花庭園
    };

    @Override
    public boolean action(int status, int answer) {
        boolean enter = getMapId() == this.map_moon;
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                if (enter) {
                    // TODO : 文章, 入場条件 (同一マップが7個あるのでミニダンジョンタイプ?)
                    nt.add("入場しますか？（テスト）");
                } else {
                    nt.add("退出しますか？");
                }
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                if (enter) {
                    // TODO : 3個のマップへランダム入場らしい?
                    int index = (int) (Math.random() * this.map_ids.length);
                    warp(this.map_ids[index], "sp");
                } else {
                    warp(this.map_moon, "sp");
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
