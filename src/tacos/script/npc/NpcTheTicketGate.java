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
public class NpcTheTicketGate extends NpcScriptMan {

    private final int map_line_1 = 103000101; // 地下鉄 : 1号線-1区
    private final int map_kerning_square = 103000310; // カニングシティー地下鉄 : カニングスクエア駅
    private static final int MENU_LINE_1 = 0;
    private static final int MENU_KERNING_SQUARE = 1;
    private static final int MENU_CONSTRUCTION_SITE = 2;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("行先を選択してください。");
                nt.add("#L" + MENU_LINE_1 + "##b#e1号線#n#k#l");
                nt.add("");
                nt.addMenu(MENU_KERNING_SQUARE, "カニングスクエア<地下鉄搭乗>");
                nt.addMenu(MENU_CONSTRUCTION_SITE, "工事場");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                switch (answer) {
                    case MENU_LINE_1 -> {
                        warp(this.map_line_1, "out00");
                    }
                    case MENU_KERNING_SQUARE -> {
                        warp(this.map_kerning_square, "out00");
                    }
                    case MENU_CONSTRUCTION_SITE -> {
                        // TODO : 工事場 (103000900, 103000903, 103000906) への移動, 乗車券 (4031036, 4031037, 4031038) の確認と消費
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
