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
// Map.wz portal script : aranTutorMono1 (JMS186), js の処理を利用
public class Portal_Rien_AranTutorMono1 extends PortalScriptMan {

    private final int quest_aran_tutorial = 21002; // アランのチュートリアル (InfoQuest)

    @Override
    public boolean enter() {
        if (getInfoQuest(this.quest_aran_tutorial).equals("mo1=o")) {
            aranStart();
            updateInfoQuest(this.quest_aran_tutorial, "mo1=o;mo2=o");
            avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/legendBalloon2");
        }
        return false;
    }
}
