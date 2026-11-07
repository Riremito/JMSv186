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

import java.awt.Point;
import tacos.server.map.TacosMap;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : portalNPC (JMS194-302), js の処理を利用
// 獅子王の城の謁見室の出入り
public class Portal_ElNath_PortalNPC extends PortalScriptMan {

    private final int map_corridor = 211070000; // 獅子王の城 : 謁見室前の廊下
    private final int map_audience_room = 211070100; // 獅子王の城 : 謁見室
    private final String portal_corridor = "lionCastleenter";
    private final String portal_audience_room = "lioncastleout";
    private final int npc_von_leon = 2161000; // ヴァンレオン
    private final Point von_leon_position = new Point(-40, -181);

    @Override
    public boolean enter() {
        // 謁見室前の廊下 -> 謁見室
        if (getPortalName().equals(this.portal_corridor)) {
            TacosMap map = getMapById(this.map_audience_room);
            map.resetFully();
            warp(this.map_audience_room, this.portal_audience_room);
            map.spawnNpc(this.npc_von_leon, this.von_leon_position);
            return true;
        }
        // 謁見室 -> 謁見室前の廊下
        if (getPortalName().equals(this.portal_audience_room)) {
            warp(this.map_corridor, this.portal_corridor);
            return true;
        }
        return false;
    }
}
