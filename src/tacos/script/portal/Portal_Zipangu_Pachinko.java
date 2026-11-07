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
// Map.wz portal script : pachinko (JMS186), js の処理を利用
// パチンコ屋から元のマップへ戻る
public class Portal_Zipangu_Pachinko extends PortalScriptMan {

    private final int map_pachinko_entrance = 810000000; // ルディブリアム : パチンコ屋入口
    private final int map_showa_street = 801000300; // ジパング : ショーワ町通り

    @Override
    public boolean enter() {
        int map_return = getSavedLocation("MULUNG_TC");
        clearSavedLocation("MULUNG_TC");
        if (map_return == this.map_pachinko_entrance || map_return == this.map_showa_street) {
            return warp(map_return, "in00");
        }
        return warp(map_return);
    }
}
