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
public class Npc_LithHarbor_Olaf extends NpcScriptMan {

    private final int map_temporary_harbor = 914100000; // 眠った島 : 臨時港

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("私は既に引退した船乗りだ。これからは強い冒険者の育成に力を注ぎたいんだ。");
                nt.add("#b#m914100000##kに移動しますか？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                // TODO : 2行目の文章
                warp(this.map_temporary_harbor); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
