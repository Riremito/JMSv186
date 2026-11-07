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
// Map.wz portal script : party3_r6pt (JMS186), lua の処理を利用
// 登り道の 16 段の正解のポータル (各段 4 個, 24 ~ 87) はランダムに決まる
// 正解の場合は次の段 (np00 ~ np15), 不正解の場合は 4 段ごとの開始地点 (np16, np03, np07, np11) へ戻される
public class Portal_Orbis_Party3_r6pt extends PortalScriptMan {

    private final int portal_first = 24;
    private final int portal_last = 87;
    private final String[] portals_wrong = {"np16", "np03", "np07", "np11"};

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("Party5");
        if (field_set == null) {
            return false;
        }
        if (!field_set.getVar("r6_rp").equals("1")) {
            for (int i = 1; i <= 16; i++) {
                field_set.setVar("r6way" + i, String.valueOf(random(1, 4)));
            }
            field_set.setVar("r6_rp", "1");
        }
        int id = getPortalId();
        if (id < this.portal_first || this.portal_last < id) {
            return false;
        }
        int floor = (id - this.portal_first) / 4;
        int answer = (id - this.portal_first) % 4 + 1;
        if (field_set.getVar("r6way" + (floor + 1)).equals(String.valueOf(answer))) {
            return transferField(-1, String.format("np%02d", floor));
        }
        return transferField(-1, this.portals_wrong[floor / 4]);
    }
}
