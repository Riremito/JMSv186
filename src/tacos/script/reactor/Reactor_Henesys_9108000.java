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
// js では HenesysPQ (EventManager) の進行状況を利用しているが、満月の Reactor の状態で判定する
public class Reactor_Henesys_9108000 extends ReactorScriptMan {

    private final int mob_moon_bunny = 9300061; // ストンプラビット (月うさぎ)
    private final int seed_count = 6;

    @Override
    public void action() {
        // TODO : 文章 (js の英文を翻訳, 原文 : One of the seeds has been placed.)
        mapMessage(6, "月見草の種が1つ植えられました。");
        TacosReactor reactor_fullmoon = getMap().getReactorByName("fullmoon");
        if (reactor_fullmoon == null) {
            return;
        }
        int state = reactor_fullmoon.getState() + 1;
        reactor_fullmoon.forceHitReactor((byte) state);
        if (state != this.seed_count) {
            return;
        }
        // TODO : 文章 (js の英文を翻訳, 原文 : Protect the Moon Bunny!!!)
        mapMessage(6, "月うさぎを守って！！！");
        // TODO : js の setSpawns, respawn (マップのモンスターの出現) は未移行
        summonMob(this.mob_moon_bunny, -183, -433);
    }
}
