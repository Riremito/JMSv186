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
// Reactor.wz action : (なし) (JMS186), js の処理を利用
public class Reactor_ElNath_2118003 extends ReactorScriptMan {

    private final int mob_rex = 9300281; // 迷路の変身ライムデンデン, TODO : JMS のレックスの ID は不明

    @Override
    public void action() {
        getReactor().forceTrigger();
        getReactor().delayedDestroyReactor(1000);
        // TODO : 文章 (js の英文を翻訳, 原文 : Rex has been summoned.)
        mapMessage("レックスが召喚されました。");
        summonMob(this.mob_rex);
    }
}
