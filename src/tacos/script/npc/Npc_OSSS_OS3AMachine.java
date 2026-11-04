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
public class Npc_OSSS_OS3AMachine extends NpcScriptMan {

    private final int[] map_ids = {
        502010100, // 現在のある日 : 秘密基地地下道
        502010000 // 現在のある日 : OSSS秘密基地格納庫
    };

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 文章
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("行き先を選択してください（テスト）");
                for (int i = 0; i < this.map_ids.length; i++) {
                    nt.addMenu(i, getMapName(this.map_ids[i]));
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer < 0 || this.map_ids.length <= answer) {
                    return false;
                }
                warp(this.map_ids[answer], "sp");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
