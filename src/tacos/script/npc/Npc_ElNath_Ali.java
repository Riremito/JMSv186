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
public class Npc_ElNath_Ali extends NpcScriptMan {

    private final int quest_stage1 = 7000; // ジャクムダンジョン 1段階
    private final int npc_adobis = 2030008; // アドビス
    private final int map_door_to_zakum = 211042300; // エルナス : ジャクムへの門
    // 持ち出せないアイテム
    private final int[] item_quest_items = {
        4001015, // 帰還書の紙文書
        4001016, // 閉鉱のカギ
        4001018 // 火の原石
    };

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                String val = getQuestData(this.quest_stage1);
                if ("end".equals(val)) {
                    // 翻訳
                    say("レベル1のクリア、よくやったな！よし…#b" + getNpcName(this.npc_adobis) + "#kのところへ送ってやろう。その前に！！ここで手に入れたいろいろな特別なアイテムは、ここから持ち出せないことを覚えておいてくれ。アイテムインベントリからそれらのアイテムを回収するから、忘れないようにな。じゃあな！", false, true);
                    return true;
                }
                // 翻訳
                say("途中でやめたようだな。よし、今すぐ送ってやろう。その前に！！ここで手に入れたいろいろな特別なアイテムは、ここから持ち出せないことを覚えておいてくれ。アイテムインベントリからそれらのアイテムを回収するから、忘れないようにな。じゃあな！", false, true);
                return true;
            }
            case 1 -> {
                for (int item_id : this.item_quest_items) {
                    int count = getItemCount(item_id);
                    if (count > 0) {
                        removeItem(item_id, count);
                    }
                }
                warp(this.map_door_to_zakum);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
