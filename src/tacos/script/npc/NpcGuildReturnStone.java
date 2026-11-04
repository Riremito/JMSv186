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
// Npc.wz script : guildquest1_out (JMS131-302), 会話文は lua_script_jp (guildquest1_out) を利用
public class NpcGuildReturnStone extends NpcScriptMan {

    private final int map_return = 990001100; // 帰り道
    private final String field_set_name = "Guild1";

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // 翻訳
                askYesNo("本当にギルドクエストを途中で終了しますか？次に来たときは、また最初からやり直しになります。また、ギルドのメンバーが6人未満になった場合も、ギルドクエストは終了します。");
                return true;
            }
            case 1 -> {
                // 翻訳
                askYesNo("本当に終了しますか？");
                return true;
            }
            case 2 -> {
                // 翻訳
                say("ギルドクエストを終了しました。帰還の通路へ移動します。");
                warp(this.map_return, "st00");
                TacosFieldSet field_set = getFieldSet(this.field_set_name);
                if (field_set != null) {
                    // 翻訳
                    field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, getPlayer().getName() + "がギルドクエストを途中で終了しました。");
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
