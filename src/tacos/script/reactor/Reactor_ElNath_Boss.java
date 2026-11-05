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
// Reactor.wz action : boss, chaosboss (JMS186), js の処理を利用
public class Reactor_ElNath_Boss extends ReactorScriptMan {

    private final int reactor_chaos_zakum = 2111101; // chaosboss

    @Override
    public void action() {
        changeMusic("Bgm06/FinalFight");
        if (getReactorId() == this.reactor_chaos_zakum) {
            getMap().spawnChaosZakum(-10, -215);
            // TODO : 文章 (js の英文を翻訳, 原文 : Chaos Zakum is summoned by the force of eye of fire.)
            mapMessage2("火の目の力でカオスジャクムが召喚されます。");
            return;
        }
        spawnZakum();
        // TODO : 文章 (js の文章をそのまま利用)
        mapMessage2("火の目の力でジャクムが召喚されます。");
    }
}
