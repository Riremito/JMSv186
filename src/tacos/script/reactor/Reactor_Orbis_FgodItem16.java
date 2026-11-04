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
// Reactor.wz action : fgodItem16, fgodItem17 (JMS186), js の処理を利用
public class Reactor_Orbis_FgodItem16 extends ReactorScriptMan {

    private final int reactor_fgod_item16 = 2002017; // fgodItem16
    private final int nx = 250;

    @Override
    public void action() {
        // TODO : js の givePartyNX (JMS に存在するかは不明)
        givePartyNX(this.nx);
        dropItems();
        if (getReactorId() == this.reactor_fgod_item16) {
            getMap().setReactorState();
        }
    }
}
