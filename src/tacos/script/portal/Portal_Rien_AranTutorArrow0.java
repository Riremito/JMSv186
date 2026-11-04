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
// Map.wz portal script : aranTutorArrow0 (JMS186), js の処理を利用
public class Portal_Rien_AranTutorArrow0 extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (getInfoQuest(21002).equals("mo1=o;mo2=o;mo3=o")) {
            updateInfoQuest(21002, "arr0=o;mo1=o;mo2=o;mo3=o");
            avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
        }
        return false;
    }
}
