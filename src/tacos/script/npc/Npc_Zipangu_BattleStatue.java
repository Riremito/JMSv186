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
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class Npc_Zipangu_BattleStatue extends NpcScriptMan {

    private final int mob_margana = 9400433; // マーガナ
    private final int map_exit = 803000505; // クリムゾンウッドの聖地 : 永遠の警戒地
    private int selected = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("あなたは5つの恐怖を打ち破ることができると、本当に思いますか？　彼らはこの古代王国の中で、最も強い戦士たちなのです。");
                nt.addMenu(0, "はい！　打ち果たしてみせます！");
                nt.addMenu(1, "いいえ…ここから出たいです。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                this.selected = answer;
                NpcScriptTag nt = new NpcScriptTag();
                switch (this.selected) {
                    case 0 -> {
                        nt.add("解りました…では、聖なる戦いをここに開始します…！");
                    }
                    case 1 -> {
                        nt.add("解りました。あなたを外へとお送りしましょう。");
                    }
                    default -> {
                        return false;
                    }
                }
                say(nt, false, true);
                return true;
            }
            case 2 -> {
                switch (this.selected) {
                    case 0 -> {
                        // TODO : 召喚済みの確認, マーガナから順番に自動召喚される
                        getPlayer().getMap().spawnMonsterOnGroundBelow(WzXML.MOB.createMob(this.mob_margana), new Point(124, 144));
                    }
                    case 1 -> {
                        warp(this.map_exit, "st00");
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
