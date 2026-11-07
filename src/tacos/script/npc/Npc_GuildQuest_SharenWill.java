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
// Npc.wz script : guildquest1_will (JMS131-302), 会話文は lua_script_jp (guildquest1_will) を利用
public class Npc_GuildQuest_SharenWill extends NpcScriptMan {

    private final int item_pants = 4001031; // シャレン3世のズボン
    private final int item_shoes = 4001032; // シャレン3世の靴
    private final int item_coat = 4001033; // シャレン3世の上着
    private final int item_crown = 4001034; // シャレン3世の王冠

    @Override
    public boolean action(int status, int answer) {
        // 翻訳
        String[] says = {
            "私、シャレン3世は、全身を蝕む無念の中、ここで死にゆく。\rルビアンを守るために悪魔エルゴスを召喚したのは、とんでもない過ちであった。",
            "エルゴスはルビアンの神秘の力に酔いしれ、ほかの悪魔のモンスターを引き連れてシャレニアンを襲った。シャレニアンの王である私は王の衣を脱ぎ捨て、必死に混乱から逃げ惑い、水路で死を迎えることになったのだ。",
            "シャレニアンの没落は誰のせいでもない私の責任だが、服も着ないままここで死ぬかもしれないと思うと腹立たしい！いつか私の遺言を読む者よ、少しでも私を哀れに思うなら、服を探すのを手伝ってくれないか？",
            "#v" + this.item_shoes + "# #t" + this.item_shoes + "# \r #v" + this.item_pants + "# #t" + this.item_pants + "# \r #v" + this.item_coat + "# #t" + this.item_coat + "# \r #v" + this.item_crown + "# #t" + this.item_crown + "# \r これらを見つけて、私がいつもしていたように下から順に着せてくれれば、ようやく安らかに眠れるだろう。忍耐は美徳だ。アイテムは1つずつ落として、反応があるまで待つことをすすめる。"
        };
        if (status < 0 || says.length <= status) {
            return false;
        }
        say(says[status], 0 < status, status < says.length - 1);
        return status < says.length - 1;
    }
}
