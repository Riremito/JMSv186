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
// Map.wz portal script : mirtalk00, mirtalk01 (JMS186), js の処理を利用
// エヴァンのチュートリアル, ドラゴンの会話
public class Portal_Henesys_Mirtalk extends PortalScriptMan {

    @Override
    public boolean enter() {
        String state;
        String path;
        switch (getScriptName()) {
            case "mirtalk00" -> {
                state = "dt00=o;mo00=o";
                path = "evan/dragonTalk00";
            }
            case "mirtalk01" -> {
                state = "dt00=o;dt01=o;mo00=o;mo01=o;mo10=o;mo02=o";
                path = "evan/dragonTalk01";
            }
            default -> {
                return false;
            }
        }
        if (getEvanIntroState(state)) {
            return false;
        }
        updateEvanIntroState(state);
        showMapEffect(path);
        return true;
    }
}
