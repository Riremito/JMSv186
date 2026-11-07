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

/**
 *
 * @author Riremito
 */
// Npc.wz script : out_tree (JMS131-302), 会話文は lua_script_jp (out_tree) を利用
public class Npc_HappyVille_MufflerSnowman extends NpcScriptMan {

    private final int map_happy_village = 209000000; // 隠れ道 : 幸福の村
    private int last_status = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        // 翻訳
        if (status == 0) {
            askYesNo("ツリーはきれいに飾れた？ほかの人と一緒にクリスマスツリーを飾るのは楽しい経験よね。そう思わない？あ、そうそう…本当にここから出ていいの？");
            return true;
        }
        if (!ok) {
            say("ツリーを飾るのにもっと時間が必要なのね？ここから出たくなったら、私に話しかけてね～");
            return false;
        }
        warp(this.map_happy_village);
        return false;
    }
}
