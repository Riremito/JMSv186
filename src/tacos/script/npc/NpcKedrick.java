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
import odin.server.maps.SavedLocationType;

/**
 *
 * @author Riremito
 */
public class NpcKedrick extends NpcScriptMan {

    private final int item_fishing_rod = 5340000; // 釣竿
    private final int item_fishing_rod_premium = 5340001; // 高級釣竿
    private final int item_fishing_chair = 3011000; // 釣り用の椅子
    private final int item_bait = 2300000; // 餌
    private final int item_bait_premium = 2300001; // 高級餌
    private final int map_fishing_lagoon = 741000200; // 釣り場
    private final int bait_quantity = 120;
    private final int bait_cost = 3000;
    private final int chair_cost = 50000;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("こんな遠いところまで、何の用ですか。");
                nt.addMenu(0, "釣り場に入る");
                nt.addMenu(1, "餌を買う");
                nt.addMenu(2, "釣り用の椅子を買う");
                nt.addMenu(3, "釣りの説明");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                switch (answer) {
                    case 0 -> {
                        // TODO : レベル11以上の確認
                        if (!checkItem(this.item_fishing_rod) && !checkItem(this.item_fishing_rod_premium)) {
                            say("[test]釣竿がありません");
                            return false;
                        }
                        if (!checkItem(this.item_fishing_chair)) {
                            say("[test]釣り用の椅子ががありません");
                            return false;
                        }
                        if (!checkItem(this.item_bait) && !checkItem(this.item_bait_premium)) {
                            say("[test]餌ががありません");
                            return false;
                        }
                        getPlayer().saveLocation(SavedLocationType.FISHING);
                        warp(this.map_fishing_lagoon, "out00");
                        return false;
                    }
                    case 1 -> {
                        if (getPlayer().getMeso() < this.bait_cost) {
                            say("[test]メルが足りません");
                            return false;
                        }
                        if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_bait, this.bait_quantity, "")) {
                            say("[test]アイテム欄が不足しています");
                            return false;
                        }
                        payMoney(this.bait_cost);
                        MapleInventoryManipulator.addById(getPlayer(), this.item_bait, this.bait_quantity); // TODO : item effect.
                        return false;
                    }
                    case 2 -> {
                        if (checkItem(this.item_fishing_chair)) {
                            say("[test]既に持っています");
                            return false;
                        }
                        if (getPlayer().getMeso() < this.chair_cost) {
                            say("[test]メルが足りません");
                            return false;
                        }
                        if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_fishing_chair, 1, "")) {
                            say("[test]アイテム欄が不足しています");
                            return false;
                        }
                        payMoney(this.chair_cost);
                        MapleInventoryManipulator.addById(getPlayer(), this.item_fishing_chair, 1); // TODO : item effect.
                        return false;
                    }
                    case 3 -> {
                        say("釣竿、餌、釣り用の椅子を所持し、なおかつレベル11以上であれば釣り場に入場することができます、1分毎には何かを釣ることができるでしょう。釣り結果を知りたければ、釣り爺に話しかけてください。");
                        return false;
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
