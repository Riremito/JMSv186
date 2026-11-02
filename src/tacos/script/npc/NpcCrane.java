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

import tacos.client.TacosCharacter;

/**
 *
 * @author Riremito
 */
public class NpcCrane extends NpcScriptMan implements INpcScriptMan {

    @Override
    public boolean start(TacosCharacter chr) {
        super.start(this, chr);
        return true;
    }

    @Override
    public boolean end(TacosCharacter chr) {
        super.end(chr);
        return true;
    }

    private static final int MAP_ORBIS_TICKETING = 200000100; // オルビス : オルビスチケット売場
    private static final int MAP_ORBIS = 200000141; // オルビス : ステーション通路<武陵行き>
    private static final int MAP_MU_LUNG = 250000000; // 武陵 : 武陵
    private static final int MAP_MU_LUNG_TEMPLE = 250000100; // 武陵 : 武陵神社
    private static final int MAP_HERB_TOWN = 251000000; // 白草村 : 白草村
    private static final int MENU_ORBIS_TICKETING = 0;
    private static final int MENU_HERB_TOWN = 1;
    private static final int MENU_MU_LUNG_TEMPLE = 2;

    @Override
    public boolean action(TacosCharacter chr, int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcTag nt = new NpcTag();
                int map_id = chr.getMapId();
                if (map_id == MAP_MU_LUNG_TEMPLE) {
                    // TODO : 文章
                    nt.add("メッセージ準備中");
                    nt.addMenu(MENU_ORBIS_TICKETING, "#m" + MAP_ORBIS_TICKETING + "#に行く。");
                    nt.addMenu(MENU_HERB_TOWN, "#m" + MAP_HERB_TOWN + "#に行く。");
                    super.askMenu(chr, nt);
                    return true;
                }
                if (map_id == MAP_HERB_TOWN) {
                    // TODO : Yes Noに修正, 500 メルの支払い
                    nt.add("良く来た！　我輩は#b#m" + MAP_MU_LUNG + "##kへ往復している孤高の鶴であーる！今#m" + MAP_MU_LUNG + "#に出発したいであーるか？#b500 メル#kでいいあーる。");
                    nt.addMenu(MENU_MU_LUNG_TEMPLE, "#m" + MAP_MU_LUNG_TEMPLE + "#に行く。");
                    super.askMenu(chr, nt);
                    return true;
                }
                if (map_id == MAP_ORBIS) {
                    // TODO : 1500 メルの支払い
                    nt.add("良く来た！　我輩は#b#m" + MAP_MU_LUNG + "##kへ往復している孤高の鶴であーる！今#m" + MAP_MU_LUNG + "#に出発したいであーるか？");
                    nt.addMenu(MENU_MU_LUNG_TEMPLE, "#m" + MAP_MU_LUNG + "#(1500 メル)");
                    super.askMenu(chr, nt);
                    return true;
                }
                // デバッグモード
                nt.add("デバッグモード");
                nt.addMenu(MENU_MU_LUNG_TEMPLE, "#m" + MAP_MU_LUNG + "#(1500 メル)");
                nt.addMenu(MENU_MU_LUNG_TEMPLE, "#m" + MAP_MU_LUNG_TEMPLE + "#に行く。");
                super.askMenu(chr, nt);
                return true;
            }
            case 1 -> {
                switch (answer) {
                    case MENU_ORBIS_TICKETING -> {
                        chr.changeMapById(MAP_ORBIS_TICKETING); // TODO : portal name.
                    }
                    case MENU_HERB_TOWN -> {
                        chr.changeMapById(MAP_HERB_TOWN); // TODO : portal name.
                    }
                    case MENU_MU_LUNG_TEMPLE -> {
                        chr.changeMapById(MAP_MU_LUNG_TEMPLE, "sp");
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
