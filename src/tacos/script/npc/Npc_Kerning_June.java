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
public class Npc_Kerning_June extends NpcScriptMan {

    private final int npc_client = 9120056; // 依頼主
    private final int map_jealousy_locker = 103040410; // カニングスクエア : 7階 8階 B区域

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : クエストの確認
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("大変申し訳ございません。ここから先、関係者以外立ち入り禁止です。入場は制限させていただいております。");
                nt.addMenu(0, "私は#e" + getNpcName(this.npc_client) + "#nさんを手伝っているところなんだ。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#b" + getNpcName(this.npc_client) + "さん#kを手伝っている#b#h #さん#kですね。お待たせいたしました。7、8階の#b一般ゾーン#kへご入場ください。");
                say(nt, false, true);
                return true;
            }
            case 2 -> {
                TacosMap map = findMap(this.map_jealousy_locker);
                if (map != null && map.getCharactersSize() == 0) {
                    map.resetFully();
                }
                warp(this.map_jealousy_locker, "right01");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
