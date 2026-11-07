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

import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : party3_room8 (JMS186), lua の処理を利用
public class Portal_Orbis_Party3_room8 extends PortalScriptMan {

    private final int map_dark_room = 920011000; // 女神の塔 : 暗黒の部屋

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("Party5");
        if (field_set == null) {
            return false;
        }
        if (!isPartyBoss()) {
            if (getUserCount(this.map_dark_room) <= 0) {
                return false;
            }
            return transferField(this.map_dark_room, "st00");
        }
        // 翻訳
        field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "パーティーリーダーが<闇の部屋>に入りました。");
        return transferField(this.map_dark_room, "st00");
    }
}
