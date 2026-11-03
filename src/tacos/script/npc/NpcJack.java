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

import tacos.server.map.TacosMap;

/**
 *
 * @author Riremito
 */
public class NpcJack extends NpcScriptMan {

    private final int map_boss = 803100000; // クリムゾンウッドの聖地 : 支配者の秘密の間-孤高の戦場-
    private final int map_free_market_entrance = 910000000; // フリーマーケット入口
    private static final int MENU_NORMAL = 0;
    private static final int MENU_HARD = 1;
    private static final int MENU_SOLO = 2;
    private static final int MENU_GROUP = 3;
    private static final int MENU_FREE_MARKET = 4;

    @Override
    public boolean action(int status, int answer) {
        // TODO : 803000505, 910000000 以外のマップでの会話 (元のスクリプトはデバッグ情報を表示していた)
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("さぁ…何をするんだ？");
                nt.addMenu(MENU_NORMAL, "ダンジョン入場（難易度：ノーマル・遠征隊用）");
                nt.addMenu(MENU_HARD, "ダンジョン入場（難易度：ハード・遠征隊用）");
                nt.addMenu(MENU_SOLO, "ボスモンスター対戦（ソロ）");
                nt.addMenu(MENU_GROUP, "ボスモンスター対戦（グループ）");
                nt.addMenu(MENU_FREE_MARKET, "フリマ入口へ");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                switch (answer) {
                    case MENU_SOLO -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("クリムゾンウッドの聖地に眠る敵と一人で戦ってみるか？");
                        askYesNo(nt);
                        return true;
                    }
                    case MENU_FREE_MARKET -> {
                        warp(this.map_free_market_entrance, "out00");
                        return false;
                    }
                    default -> {
                        // TODO : ダンジョン入場 (ノーマル, ハード), ボスモンスター対戦 (グループ)
                        say("ボスモンスター対戦（ソロ）を選択してください");
                        return false;
                    }
                }
            }
            case 2 -> {
                TacosMap map = getPlayer().findMap(this.map_boss);
                if (map != null && map.getCharactersSize() == 0) {
                    map.resetFully();
                }
                warp(this.map_boss, "sp");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
