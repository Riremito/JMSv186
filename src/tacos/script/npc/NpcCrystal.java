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

import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class NpcCrystal extends NpcScriptMan {

    private final int item_toad_wallet = 5252002; // 質屋にあった大ガマガエルの財布
    private final int[] map_ids = {
        802000200, // ジパング : お台場 2100年
        802000300, // ジパング : 公園 2095年
        802000500, // ジパング : 秋葉原司令室 2102年
        802000600, // ジパング : 旗艦ファイア・オールドフォックス 2102年
        802000700, // ジパング : 渋谷 2102年
        802000800, // ジパング : 六本木モール2102年
        800040000, // ジパング : 楓城 天下泰平
        910000000 // フリーマーケット入口
    };
    private final String[] portal_names = {
        "st00",
        "st00",
        "st00",
        "st00",
        "st00",
        "st00",
        "st00",
        "out00"
    };
    private final String[] map_names = {
        "未来 - 2100年 トウキョウお台場",
        "未来 - 2095年 トウキョウ公園",
        "未来 - 2102年 トウキョウ秋葉原",
        "未来 - 2102年 トウキョウ上空",
        "未来 - 2102年 トウキョウ渋谷",
        "未来 - 2102年 トウキョウ六本木モール最上層",
        "過去 - 楓城　天下泰平",
        "デバッグ - #m910000000#"
    };

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("私はアーシア様の力を増幅させる者。タイムワープの許可がアーシア様より下りていれば、過去や未来のジパングに飛ばしてあげよう。");
                for (int i = 0; i < this.map_ids.length; i++) {
                    nt.addMenu(i, this.map_names[i]);
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer < 0 || this.map_ids.length <= answer) {
                    return false;
                }
                // debug
                if (!getPlayer().haveItem(this.item_toad_wallet)) {
                    MapleInventoryManipulator.addById(getPlayer(), this.item_toad_wallet, 1); // TODO : item effect.
                }
                warp(this.map_ids[answer], this.portal_names[answer]);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
