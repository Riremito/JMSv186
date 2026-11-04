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
// Npc.wz script : go_tree1, go_tree2, go_tree3 (JMS131-302), 会話文は lua_script_jp (go_tree1, go_tree2, go_tree3) を利用
public class NpcTreeSnowman extends NpcScriptMan {

    private final int npc_snowman1 = 2001001; // 木の手雪だるま
    private final int npc_snowman2 = 2001002; // バケツ雪だるま
    private final int npc_cliff = 2001000; // クリッフ
    private final int map_tree1 = 209000001; // 隠れ道 : クリスマスの丘
    private final int map_tree2 = 209000006; // 隠れ道 : クリスマスの丘
    private final int map_tree3 = 209000011; // 隠れ道 : クリスマスの丘
    private final int room_max = 5;
    private final int user_max = 6;

    @Override
    public boolean action(int status, int answer) {
        // 翻訳
        if (status == 0) {
            NpcScriptTag nt = new NpcScriptTag();
            nt.add("こんにちは～私は#p" + getNpcId() + "#。大きなクリスマスツリーがある部屋へ連れて行ってあげられるよ！詳しいことは#b#p" + this.npc_cliff + "##kに聞いてね。どの部屋に入る？ \r\n#b#L0#1番目のツリーがある部屋#l\r\n#L1#2番目のツリーがある部屋#l\r\n#L2#3番目のツリーがある部屋#l\r\n#L3#4番目のツリーがある部屋#l\r\n#L4#5番目のツリーがある部屋#l", false);
            askMenu(nt);
            return true;
        }
        if (answer < 0 || this.room_max <= answer) {
            return false;
        }
        int map_base = (getNpcId() == this.npc_snowman1) ? this.map_tree1 : (getNpcId() == this.npc_snowman2) ? this.map_tree2 : this.map_tree3;
        if (getUserCount(map_base + answer) < this.user_max) {
            warp(map_base + answer);
            return false;
        }
        // lua の部屋の名前はポルトガル語のまま
        String[] names = {"Primeiro", "Segundo", "Terceiro", "Quarto", "Quinto"};
        say("#b" + names[answer] + "番目のツリーの部屋#kに入る？うーん…ごめんね、今ここは満員なの。別の部屋に入ってね～");
        return false;
    }
}
