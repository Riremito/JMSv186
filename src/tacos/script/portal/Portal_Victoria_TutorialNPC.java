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
// Map.wz portal script : tutorialNPC (JMS186), js の処理を利用
public class Portal_Victoria_TutorialNPC extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (getLevel() <= 10 && getJob() == 0) {
            int m = getMapId();
            int npcid = 0;
            if (m == 120000101) {
                npcid = 1072008;
            } else if (m == 102000003) {
                npcid = 1022000;
            } else if (m == 103000003) {
                npcid = 1052001;
            } else if (m == 100000201) {
                npcid = 1012100;
            } else if (m == 101000003) {
                npcid = 1032001;
            }
            if (npcid != 0) {
                openNpc(npcid);
            }
        }
        return false;
    }
}
