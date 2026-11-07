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

import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : mc_roomout (JMS131-302), 会話文は lua_script_jp (mc_roomout) を利用
// 助手レッド, 助手ブルー
public class Npc_MonsterCarnival_CarnivalAssistant extends NpcScriptMan {

    private final int map_entrance = 980000000; // モンスターカーニバル : モンスターカーニバル入口
    // モンスターカーニバルのマップ
    private final int[] maps_carnival = {980000100, 980000200, 980000300, 980000400, 980000500, 980000600};
    // FieldSet 名
    private final String[] field_set_names = {"MCarnival100", "MCarnival110", "MCarnival120", "MCarnival130", "MCarnival140", "MCarnival150"};

    @Override
    public boolean action(int status, int answer) {
        int map_id = getMapId();
        for (int i = 0; i < this.maps_carnival.length; i++) {
            if (this.maps_carnival[i] != map_id) {
                continue;
            }
            TacosFieldSet field_set = getFieldSet(this.field_set_names[i]);
            if (field_set != null) {
                field_set.transferFieldAll(this.map_entrance, "");
            }
            break;
        }
        return false;
    }
}
