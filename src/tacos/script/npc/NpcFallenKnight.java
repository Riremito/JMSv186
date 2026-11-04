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
package tacos.script.npc;

import java.awt.Point;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.map.TacosMap;

/**
 *
 * @author Riremito
 */
// Npc.wz script : enterBlackEreb (JMS165-302), lua_script_jp の enterBlackEreb は中身がないため js の処理を利用
public class NpcFallenKnight extends NpcScriptMan {

    private final int map_hidden_ereb = 913030000; // 隠れ道 : エレヴ
    private final int npc_eleanor = 1104002; // エレオノール
    private final Point eleanor_position = new Point(-430, 88);
    private final String portal_name = "sp";

    @Override
    public boolean action(int status, int answer) {
        TacosMap map = getPlayer().findMap(this.map_hidden_ereb);
        if (map == null) {
            return false;
        }
        if (0 < map.getCharactersSize()) {
            // TODO : メッセージの種類
            // TODO : 文章 (適当)
            getPlayer().dropMessage(OpsBroadcastMsg.BM_EVENT.get(), "ほかの誰かが黒魔女と戦っています。");
            return false;
        }
        map.removeNpcById(this.npc_eleanor);
        map.killAllMonsters(false);
        map.spawnNpc(this.npc_eleanor, this.eleanor_position);
        warp(this.map_hidden_ereb, this.portal_name);
        return false;
    }
}
