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
// Map.wz portal script : crane_MR, crane_SS (JMS186), lua の処理を利用
// 鶴での移動中のマップから到着先へ移動
public class Portal_Orbis_Crane extends PortalScriptMan {

    private final int map_mulung = 250000100; // 武陵 : 武陵神社
    private final int map_orbis = 200000141; // オルビス : ステーション通路<武陵行き>
    private final int time_arrive = 58;

    @Override
    public boolean enter() {
        boolean to_mulung = getScriptName().equals("crane_MR");
        TacosFieldSet field_set = getFieldSet(to_mulung ? "Crane_MR" : "Crane_SS");
        if (field_set == null) {
            return false;
        }
        if (field_set.getQuestTime() < this.time_arrive) {
            return false;
        }
        return transferField(to_mulung ? this.map_mulung : this.map_orbis, "");
    }
}
