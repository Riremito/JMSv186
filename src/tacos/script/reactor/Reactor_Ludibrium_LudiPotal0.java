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

import odin.server.Randomizer;

/**
 *
 * @author Riremito
 */
// Reactor.wz action : ludiPotal0 (JMS186), js の処理を利用 (ReactorAction.img の type 0 と同じ移動先)
public class Reactor_Ludibrium_LudiPotal0 extends ReactorScriptMan {

    private final int map_factory1 = 922000020;
    private final int map_factory2 = 922000021;

    @Override
    public void action() {
        // TODO : 文章 (js の英文を翻訳, 原文 : You have found a secret factory!)
        playerMessage(5, "秘密の工場を見つけました！");
        warp(Randomizer.nextInt(2) == 0 ? this.map_factory1 : this.map_factory2);
    }
}
