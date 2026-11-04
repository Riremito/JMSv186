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
// Map.wz portal script : party3_jailin (JMS186), lua の処理を利用
// 監獄 (920010910 など) のルイネルを倒している場合は監獄倉庫 (920010912 など), 倒していない場合は監獄倉庫 (920010911 など) へ移動
public class Portal_Orbis_Party3_jailin extends PortalScriptMan {

    private final int mob_lunarel = 9300044; // 女神塔のルイネル

    @Override
    public boolean enter() {
        if (getMobCount(getMapId(), this.mob_lunarel) == 0) {
            return transferField(getMapId() + 2, "out00");
        }
        return transferField(getMapId() + 1, "out00");
    }
}
