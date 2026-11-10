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

/**
 *
 * @author Riremito
 */
// Reactor.wz action : s4hitmanMap0, s4hitmanMap1, s4hitmanMap2 (JMS186), js の処理を利用
public class Reactor_Victoria_S4hitmanMap0 extends ReactorScriptMan {

    private final int map_hitman = 910200000; // 未公開の遺跡１
    private final int reactor_hitman_map0 = 1020000; // s4hitmanMap0

    @Override
    public void action() {
        // s4hitmanMap0 = 1, s4hitmanMap1 = 2, s4hitmanMap2 = 3
        warp(this.map_hitman, getReactorId() - this.reactor_hitman_map0 + 1);
    }
}
