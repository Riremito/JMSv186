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
package tacos.script.portal;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : DragonEggNotice (JMS186), js の処理を利用
public class Portal_Henesys_DragonEggNotice extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (getEvanIntroState("egg=o;mo30=o;mo40=o;mo41=o;mo50=o;mo42=o;mo60=o")) {
            return false;
        }
        updateEvanIntroState("egg=o;mo30=o;mo40=o;mo41=o;mo50=o;mo42=o;mo60=o");
        evanTutorial("UI/tutorial/evan/8/0", -1);
        return true;
    }
}
