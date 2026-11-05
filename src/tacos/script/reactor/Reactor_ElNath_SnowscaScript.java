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
// Reactor.wz action : snowscaScript0, snowscaScript1, snowscaScript2 (JMS186), マクロ対策モンスターの弱体化
public class Reactor_ElNath_SnowscaScript extends ReactorScriptMan {

    private final int mob_ghost = 6090001; // 雪山の亡霊

    @Override
    public void action() {
        // マクロ対策モンスターの弱体化
        weakenMacroMob(this.mob_ghost);
    }
}
