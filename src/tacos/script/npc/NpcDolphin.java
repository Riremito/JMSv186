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

import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;
import odin.server.maps.SavedLocationType;

/**
 *
 * @author Riremito
 */
public class NpcDolphin extends NpcScriptMan {

    private static final int MAP_AQUARIUM = 230000000; // アクアリウム : アクアリウム
    private static final int MAP_SHARP_CLIFF = 230030200; // アクアロード : 四角地帯
    private static final int MAP_HERB_TOWN_PIER = 251000100; // 白草村 : 海側波止場
    private static final int MAP_FOGGY_SEA = 923020000; // 霧の海 : 座礁された幽霊船
    private static final int MENU_AQUARIUM = 0;
    private static final int MENU_SHARP_CLIFF = 1;
    private static final int MENU_HERB_TOWN = 2;
    private static final int MENU_FOGGY_SEA = 3;
    private static final int MENU_SHARP_CLIFF_TICKET = 4;
    // 初心者は 1/10
    private final int cost = 10000;
    private final int cost_sharp_cliff = 1000;
    private final int ticket = 4031242; // イルカタクシーチケット

    @Override
    public boolean action(int status, int answer) {
        boolean beginner = getPlayer().getJob() == 0;
        int cost_now = beginner ? this.cost / 10 : this.cost;
        int cost_sharp_cliff_now = beginner ? this.cost_sharp_cliff / 10 : this.cost_sharp_cliff;
        switch (status) {
            case 0 -> {
                /*
                世界の全ての海は繋がっています。歩いては遠いところでも海でいけば近くなるんです。#bイルカタクシー#kに乗って移動しますか。
                #bイルカタクシーチケット#kを使用して#m230030200#に移動する
                10000 メルを払い、#m251000100#に移動する。
                 */
                NpcScriptTag nt = new NpcScriptTag();
                // 原文ママ
                nt.add("世の中の全ての海は繋がっています。歩くと遠い所でも海ならすぐですよ。どうですか？#bイルカタクシー#kに乗って移動しますか？");
                if (beginner) {
                    // 翻訳
                    nt.add("初心者には90%割引の特別チケットがあるよ！");
                }
                if (getMapId() == MAP_AQUARIUM) {
                    if (checkItem(this.ticket)) {
                        nt.addMenu(MENU_SHARP_CLIFF_TICKET, "#b#t" + this.ticket + "##kを使って#b#m" + MAP_SHARP_CLIFF + "##kへ行く。");
                    } else {
                        nt.addMenu(MENU_SHARP_CLIFF, "料金を払って#b#m" + MAP_SHARP_CLIFF + "##kへ行く（料金：" + cost_sharp_cliff_now + "メル）");
                    }
                    nt.addMenu(MENU_HERB_TOWN, "料金を払って#b#m" + MAP_HERB_TOWN_PIER + "##kへ行く（料金：" + cost_now + "メル）");
                } else {
                    nt.addMenu(MENU_AQUARIUM, "料金を払って#b#m" + MAP_AQUARIUM + "##kへ行く（料金：" + cost_now + "メル）");
                }
                nt.addMenu(MENU_FOGGY_SEA, "霧の海に行く。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                switch (answer) {
                    case MENU_SHARP_CLIFF_TICKET -> {
                        if (!checkItem(this.ticket)) {
                            // 翻訳
                            say("#b#t" + this.ticket + "##kを持っていないみたいだね。ヘネシスのカミラから#b#t" + this.ticket + "##kを手に入れる方法があるはずだよ…");
                            return false;
                        }
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.ticket, 1, true, false);
                        warp(MAP_SHARP_CLIFF, "east00");
                    }
                    case MENU_SHARP_CLIFF -> {
                        if (!payMoney(cost_sharp_cliff_now)) {
                            // 翻訳
                            say("お金が足りないみたいだね…");
                            return false;
                        }
                        warp(MAP_SHARP_CLIFF, "east00");
                    }
                    case MENU_HERB_TOWN -> {
                        if (!payMoney(cost_now)) {
                            // 翻訳
                            say("お金が足りないみたいだね…");
                            return false;
                        }
                        warp(MAP_HERB_TOWN_PIER, "out00");
                    }
                    case MENU_AQUARIUM -> {
                        if (!payMoney(cost_now)) {
                            // 翻訳
                            say("お金が足りないみたいだね…");
                            return false;
                        }
                        warp(MAP_AQUARIUM, "market00");
                    }
                    case MENU_FOGGY_SEA -> {
                        getPlayer().saveLocation(SavedLocationType.MULUNG_TC);
                        warp(MAP_FOGGY_SEA, "sp");
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
