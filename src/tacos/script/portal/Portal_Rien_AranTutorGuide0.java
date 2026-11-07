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
// Map.wz portal script : aranTutorGuide0 (JMS186), js の処理を利用
public class Portal_Rien_AranTutorGuide0 extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (getInfoQuest(21002).equals("arr0=o;mo1=o;mo2=o;mo3=o")) {
            // TODO : 文章 (js の英文を翻訳, 原文 : Press C to apply a regular attack on a monster.)
            playerMessage(5, "Cキーを押すとモンスターに通常攻撃ができます。");
            updateInfoQuest(21002, "normal=o;arr0=o;mo1=o;mo2=o;mo3=o");
            avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialGuide1");
        }
        return false;
    }
}
