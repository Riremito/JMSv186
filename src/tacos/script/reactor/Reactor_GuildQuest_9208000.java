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
// lua (guildquest1) では NPC が Reactor の状態を確認するため、js の EventInstance の像の順番の処理は未移行
public class Reactor_GuildQuest_9208000 extends ReactorScriptMan {

    private final int reactor_gp = 9208004; // ギルドポイント
    private final int reactor_spear_gate = 9208007; // 槍の門
    private final int map_spear_gate = 990000400;
    private final int gp = 20;

    @Override
    public void action() {
        if (getReactorId() == this.reactor_gp) {
            gainGP(this.gp);
            return;
        }
        if (getReactorId() == this.reactor_spear_gate) {
            TacosMap map = getMapById(this.map_spear_gate);
            if (map == null) {
                return;
            }
            TacosReactor reactor = map.getReactorByName("speargate");
            if (reactor != null) {
                reactor.hitReactor(getClient());
            }
        }
        // TODO : 像の順番 (9208000 - 9208002) の処理
    }
}
