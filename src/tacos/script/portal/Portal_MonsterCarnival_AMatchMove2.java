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
// Map.wz portal script : aMatchMove2 (JMS186), js の処理を利用
// アリアント闘技大会の控え室から元のマップへ戻る
public class Portal_MonsterCarnival_AMatchMove2 extends PortalScriptMan {

    private final int map_henesys = 100000000; // ヘネシス

    @Override
    public boolean enter() {
        int map_return = getSavedLocation("ARIANT_PQ");
        clearSavedLocation("ARIANT_PQ");
        if (map_return == this.map_henesys) {
            return warp(map_return, 4);
        }
        return warp(map_return, 0);
    }
}
