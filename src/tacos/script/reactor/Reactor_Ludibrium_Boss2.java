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
package tacos.script.reactor;

import tacos.server.map.TacosMap;

/**
 *
 * @author Riremito
 */
// Reactor.wz action : boss2 (JMS186), js の処理を利用
public class Reactor_Ludibrium_Boss2 extends ReactorScriptMan {

    private final int mob_papulatus = 8500000; // 時間の球
    private final int map_clock_tower = 220080000;

    @Override
    public void action() {
        // TODO : 文章 (js の英文を翻訳, 原文 : The dimensional hole has been filled by the <Piece of Cracked Dimension>.)
        mapMessage(5, "<次元の亀裂のかけら>で次元の穴が埋まりました。");
        changeMusic("Bgm09/TimeAttack");
        summonMob(this.mob_papulatus, -413, -400);
        TacosMap map = getMapById(this.map_clock_tower);
        if (map != null) {
            map.setReactorState();
        }
    }
}
