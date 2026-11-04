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
// Map.wz portal script : aranTutorGuide1 (JMS186), js の処理を利用
public class Portal_Rien_AranTutorGuide1 extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (getInfoQuest(21002).equals("normal=o;arr0=o;arr1=o;mo1=o;mo2=o;mo3=o;mo4=o")) {
            // TODO : 文章 (js の英文を翻訳, 原文 : Press C repeatedly for a combo attack.)
            playerMessage(5, "Cキーを連続で押すとコンボ攻撃ができます。");
            updateInfoQuest(21002, "normal=o;arr0=o;arr1=o;mo1=o;chain=o;mo2=o;mo3=o;mo4=o");
            avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialGuide2");
        }
        return false;
    }
}
