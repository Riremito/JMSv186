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

import tacos.server.map.object.TacosReactor;

/**
 *
 * @author Riremito
 */
// Reactor.wz action : (なし) (JMS186), js の処理を利用
// TODO : js では OrbisPQ (EventManager) の進行状況を設定しているが、グループクエストの Java の処理が未完成のため未移行
public class Reactor_Orbis_2008000 extends ReactorScriptMan {

    private final int reactor_music = 2008006; // 音楽

    @Override
    public void action() {
        if (getReactorId() == this.reactor_music) {
            // TODO : 文章 (js の英文を翻訳, 原文 : The music played through the air.)
            mapMessage(6, "辺りに音楽が鳴り響いた。");
            return;
        }
        // TODO : 文章 (js の英文を翻訳, 原文 : One of the pieces has been placed.)
        mapMessage(6, "かけらが1つはめ込まれました。");
        TacosReactor reactor_minerva = getMap().getReactorByName("minerva");
        if (reactor_minerva != null) {
            reactor_minerva.forceHitReactor((byte) (reactor_minerva.getState() + 1));
        }
    }
}
