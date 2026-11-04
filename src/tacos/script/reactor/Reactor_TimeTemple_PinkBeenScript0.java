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
// Reactor.wz action : PinkBeenScript0 (JMS186), js の処理を利用
public class Reactor_TimeTemple_PinkBeenScript0 extends ReactorScriptMan {

    private final int mob_pink_bean = 8820008; // チビボス召喚用透明モンスター

    @Override
    public void action() {
        killAllMob();
        summonMob(this.mob_pink_bean);
    }
}
