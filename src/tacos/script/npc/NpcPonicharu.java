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

import java.awt.Point;
import tacos.server.map.TacosMap;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class NpcPonicharu extends NpcScriptMan {

    private final int map_boss = 802000211; // ジパング : お台場 2100年
    private final int map_exit = 802000210; // ジパング : お台場 2100年
    private final int mob_bergamot = 9400263; // ベルガモット
    private static final int MENU_ENTER = 1;
    private static final int MENU_EXIT = 2;

    @Override
    public boolean action(int status, int answer) {
        // ベルガモット
        switch (status) {
            case 0 -> {
                // TODO : 文章が不明 (元のスクリプトの仮の文章), 入場条件
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("ベルガモットテスト");
                nt.addMenu(MENU_ENTER, "入場");
                nt.addMenu(MENU_EXIT, "退出");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                switch (answer) {
                    case MENU_ENTER -> {
                        TacosMap map = getPlayer().findMap(this.map_boss);
                        boolean first_user = map != null && map.getCharactersSize() == 0;
                        warp(this.map_boss);
                        if (first_user) {
                            map.resetFully();
                            map.spawnMonsterOnGroundBelow(WzXML.MOB.createMob(this.mob_bergamot), new Point(559, 545));
                        }
                    }
                    case MENU_EXIT -> {
                        warp(this.map_exit);
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
