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
import tacos.server.map.object.TacosReactor;

/**
 *
 * @author Riremito
 */
// Reactor.wz action : (なし) (JMS186), js の処理を利用
public class Reactor_Magatia_2618001 extends ReactorScriptMan {

    private final int reactor_out2 = 2618001; // 2つ先のマップの Reactor
    private final int map_romeo = 926100200; // 特殊な実験室 (ロミオ)

    @Override
    public void action() {
        boolean romeo = getMap().getId() == this.map_romeo;
        TacosMap map;
        String name;
        if (getReactorId() == this.reactor_out2) {
            map = getMapById(getMap().getId() + 2);
            name = romeo ? "rnj32_out" : "jnr32_out";
        } else {
            map = getMapById(getMap().getId() + 1);
            name = romeo ? "rnj31_out" : "jnr31_out";
        }
        if (map == null) {
            return;
        }
        TacosReactor reactor = map.getReactorByName(name);
        if (reactor != null) {
            reactor.hitReactor(getClient());
        }
    }
}
