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

import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : party3_r4pt (JMS186), lua の処理を利用
// ラウンジの正解のポータル (1 段目 : 11 ~ 13, 2 段目 : 14 ~ 16) はランダムに決まる
public class Portal_Orbis_Party3_r4pt extends PortalScriptMan {

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("Party5");
        if (field_set == null) {
            return false;
        }
        if (!field_set.getVar("r4_rp").equals("1")) {
            field_set.setVar("r4way1", String.valueOf(random(1, 3)));
            field_set.setVar("r4way2", String.valueOf(random(1, 3)));
            field_set.setVar("r4_rp", "1");
        }
        int id = getPortalId();
        if (11 <= id && id <= 13) {
            if (field_set.getVar("r4way1").equals(String.valueOf(id - 10))) {
                return transferField(-1, "np00");
            }
            return transferField(-1, "np02");
        }
        if (14 <= id && id <= 16) {
            if (field_set.getVar("r4way2").equals(String.valueOf(id - 13))) {
                return transferField(-1, "np01");
            }
            return transferField(-1, "np02");
        }
        return false;
    }
}
