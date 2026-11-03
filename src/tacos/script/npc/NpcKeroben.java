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
public class NpcKeroben extends NpcScriptMan {

    private final int map_peak = 240040600; // リプレ : 大きい巣の峰
    private final int map_cave_entrance = 240050000; // リプレ : 洞窟の入口

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 変身状態の確認 (変身していなければ map_peak に追い出す), デバッグ用の選択肢の削除
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("人間じゃないか。俺がここを守っている限り、人間は一歩も入れないぞ。痛い目に会いたくなければ早く帰りな！");
                nt.addMenuRed(0, getMapName(this.map_peak));
                nt.addMenuRed(1, getMapName(this.map_cave_entrance));
                askMenu(nt);
                return true;
            }
            case 1 -> {
                switch (answer) {
                    case 0 -> {
                        warp(this.map_peak); // TODO : portal name. (st00?)
                    }
                    case 1 -> {
                        warp(this.map_cave_entrance); // TODO : portal name.
                    }
                    default -> {
                    }
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
