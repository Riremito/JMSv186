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
// Map.wz portal script : enterSecondDH (JMS186), js の処理を利用
public class Portal_Ereve_EnterSecondDH extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (getQuestStatus(20201) == 1 || getQuestStatus(20202) == 1 || getQuestStatus(20203) == 1 || getQuestStatus(20204) == 1 || getQuestStatus(20205) == 1) {
            return warp(108000600, "sp");
        }
        return false;
    }
}
