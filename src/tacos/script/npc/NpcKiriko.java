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
public class NpcKiriko extends NpcScriptMan {

    private final int map_training_entrance = 130020000; // クィーンズロード : 訓練場の入口

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                // TODO : 文章 (適当)
                say("もう少し時間が必要かもしれませんね。");
                return false;
            }
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (適当)
                nt.add("試練の証はすべて見つけましたか？ここから出ますか？");
                askAccept(nt);
                return true;
            }
            case 1 -> {
                warp(this.map_training_entrance);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
