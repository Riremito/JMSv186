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

import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : hontale_Bdoor (JMS131-302), 会話文は lua_script_jp (hontale_Bdoor) を利用
public class Npc_Leafre_HontaleStump extends NpcScriptMan {

    private final String field_set_name = "Hontale1";
    private final int[] item_keys = {
        4001088, // 二番目の迷路部屋の水晶鍵
        4001089, // 三番目の迷路部屋の水晶鍵
        4001090, // 四番目の迷路部屋の水晶鍵
        4001091, // 五番目の迷路部屋の水晶鍵
    };
    private int door = -1;

    @Override
    public boolean action(int status, int answer) {
        TacosFieldSet field_set = getFieldSet(this.field_set_name);
        if (field_set == null) {
            return false;
        }
        if (status == 0) {
            // passKey1 から順に作動しているリアクターの数
            int passed = 0;
            for (int i = 1; i <= this.item_keys.length; i++) {
                if (field_set.getReactorState(i, "passKey" + i) != 1) {
                    break;
                }
                passed++;
            }
            // 残りのリアクターがすべて未作動でない場合は対象外
            for (int i = passed + 1; i <= this.item_keys.length; i++) {
                if (field_set.getReactorState(i, "passKey" + i) != 0) {
                    passed = 0;
                    break;
                }
            }
            this.door = passed - 1;
            if (this.door < 0 || field_set.getVar("Bdoor" + passed).equals("1")) {
                // 翻訳
                say("不思議な切り株。深い黒い穴に何かを入れられそうです。");
                return false;
            }
            NpcScriptTag nt = new NpcScriptTag();
            // 翻訳
            if (this.door == 0) {
                nt.add("切り株の黒い穴に何かを入れられそうです。入れるもの\r\n#b#L0# #t" + this.item_keys[this.door] + "#。#l", false);
            } else {
                nt.add("深い黒い穴に何かを入れられそうです。入れるもの \r\n#b#L0# #t" + this.item_keys[this.door] + "#。#l", false);
            }
            askMenu(nt);
            return true;
        }
        int item_id = this.item_keys[this.door];
        int count = getItemCount(item_id);
        if (count <= 0) {
            return false;
        }
        if (!exchange(0, item_id, -count)) {
            // 翻訳
            say("もしかして#t" + item_id + "#をなくしましたか？");
            return false;
        }
        field_set.setVar("Bdoor" + (this.door + 1), "1");
        // 翻訳
        String text = "迷路の部屋の" + (this.door + 2) + "番目の扉が開きました。";
        field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, text);
        say(text);
        return false;
    }
}
