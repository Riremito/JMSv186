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
// Map.wz portal script : aranTutorGuide2 (JMS186), js の処理を利用
public class Portal_Rien_AranTutorGuide2 extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (getInfoQuest(21002).equals("normal=o;arr0=o;arr1=o;arr2=o;mo1=o;chain=o;mo2=o;mo3=o;mo4=o")) {
            // TODO : 文章 (js の英文を翻訳, 原文 : After the combo attack, use a combination of arrows and C for a command attack.)
            playerMessage(5, "コンボ攻撃の後、方向キーとCキーを組み合わせるとコマンド攻撃ができます。");
            updateInfoQuest(21002, "cmd=o;normal=o;arr0=o;arr1=o;arr2=o;mo1=o;chain=o;mo2=o;mo3=o;mo4=o");
            avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialGuide3");
        }
        return false;
    }
}
