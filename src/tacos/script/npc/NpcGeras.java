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
public class NpcGeras extends NpcScriptMan {

    private final int item_ticket = 4031576; // アリアント行きチケット(一般)
    private final int map_station = 260000100; // アリアント : アリアント乗降場

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("いったん船に乗ると長旅になりますので急な用があれば先に解決してください。いかがですか？船に乗りますか？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                // TODO : チケット(item_ticket)の確認と消費, 乗船, 文章
                warp(this.map_station); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
