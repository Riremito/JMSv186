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

import odin.server.maps.SavedLocationType;
import tacos.client.TacosCharacter;

/**
 *
 * @author Riremito
 */
public class NpcDolphin extends NpcScriptMan {

    private static final int MAP_AQUARIUM = 230000000; // アクアリウム : アクアリウム
    private static final int MAP_SHARP_CLIFF = 230030200; // アクアロード : 四角地帯
    private static final int MAP_HERB_TOWN = 251000000; // 白草村 : 白草村
    private static final int MAP_HERB_TOWN_PIER = 251000100; // 白草村 : 海側波止場
    private static final int MAP_FOGGY_SEA = 923020000; // 霧の海 : 座礁された幽霊船
    private static final int MENU_AQUARIUM = 0;
    private static final int MENU_SHARP_CLIFF = 1;
    private static final int MENU_HERB_TOWN = 2;
    private static final int MENU_FOGGY_SEA = 3;

    @Override
    public boolean action(TacosCharacter chr, int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcTag nt = new NpcTag();
                nt.add("世の中の全ての海は繋がっています。歩くと遠い所でも海ならすぐですよ。どうですか？#bイルカタクシー#kに乗って移動しますか？");
                if (chr.getMapId() == MAP_AQUARIUM) {
                    nt.addMenu(MENU_SHARP_CLIFF, "#m" + MAP_SHARP_CLIFF + "#に行く。");
                    nt.addMenu(MENU_HERB_TOWN, "#m" + MAP_HERB_TOWN + "#に行く。");
                    nt.addMenu(MENU_FOGGY_SEA, "霧の海に行く。");
                    super.askMenu(chr, nt);
                    return true;
                }
                if (chr.getMapId() == MAP_HERB_TOWN_PIER) {
                    nt.addMenu(MENU_AQUARIUM, "#m" + MAP_AQUARIUM + "#に行く。");
                    nt.addMenu(MENU_FOGGY_SEA, "霧の海に行く。");
                    super.askMenu(chr, nt);
                    return true;
                }
                // デバッグモード
                nt.addMenu(MENU_AQUARIUM, "#m" + MAP_AQUARIUM + "#に行く。");
                nt.addMenu(MENU_SHARP_CLIFF, "#m" + MAP_SHARP_CLIFF + "#に行く。");
                nt.addMenu(MENU_HERB_TOWN, "#m" + MAP_HERB_TOWN + "#に行く。");
                nt.addMenu(MENU_FOGGY_SEA, "霧の海に行く。");
                super.askMenu(chr, nt);
                return true;
            }
            case 1 -> {
                switch (answer) {
                    case MENU_AQUARIUM -> {
                        chr.changeMapById(MAP_AQUARIUM, "market00");
                    }
                    case MENU_SHARP_CLIFF -> {
                        chr.changeMapById(MAP_SHARP_CLIFF, "east00");
                    }
                    case MENU_HERB_TOWN -> {
                        chr.changeMapById(MAP_HERB_TOWN_PIER, "out00");
                    }
                    case MENU_FOGGY_SEA -> {
                        chr.saveLocation(SavedLocationType.MULUNG_TC);
                        chr.changeMapById(MAP_FOGGY_SEA, "sp");
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
