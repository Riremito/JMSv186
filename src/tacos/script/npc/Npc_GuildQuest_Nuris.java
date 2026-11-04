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
// Npc.wz script : guildquest1_clear (JMS131-302), 会話文は lua_script_jp (guildquest1_clear) を利用
public class Npc_GuildQuest_Nuris extends NpcScriptMan {

    private final int map_camp = 101030104; // ぺリオン : 遺跡発掘ベースキャンプ
    private final int item_ruby = 4001024; // 魔性のルビー
    private final int item_guard_stone = 1032033; // 守護石
    private final int[] item_guild_quest = {
        4001024, // 魔性のルビー
        4001025, // ロンギヌス槍
        4001026, // カギ
        4001027, // 勇猛の勲章
        4001028, // 知恵の文書
        4001029, // 腐った食品
        4001030, // 熟成ジュニアネッキ酒
        4001031, // シャレン3世のズボン
        4001032, // シャレン3世の靴
        4001033, // シャレン3世の上着
        4001034, // シャレン3世の王冠
        4001035, // 悪魔の紋章
        4001036, // 透明アイテム
        4001037, // 錆びた鍵
        1032033, // 守護石
    };
    private boolean has_ruby = false;

    @Override
    public boolean action(int status, int answer) {
        // 翻訳
        if (status == 0) {
            this.has_ruby = 1 <= getItemCount(this.item_ruby);
            if (this.has_ruby) {
                say("何だって？！本当に#t" + this.item_ruby + "#を持ってきたのですか？勇敢な一団がギルドクエストに挑んでいると噂には聞いていましたが、まさか本当に成し遂げるとは思っていませんでした。", false, true);
            } else {
                say("それでは皆さんをキャンプへお送りします。", false, true);
            }
            return true;
        }
        if (status == 1 && this.has_ruby) {
            say("あなたたちが見つけた#t" + this.item_ruby + "#は、この地の歴史上最も重要な発掘品の一つとして、永遠に語り継がれる歴史的なアイテムです。さて、それではキャンプへお送りします。", true, true);
            return true;
        }
        if (status < 1) {
            return false;
        }
        // lua の takeawayitem
        for (int item_id : this.item_guild_quest) {
            removeItemAll(item_id);
        }
        if (removeEquippedItem(this.item_guard_stone, 1)) {
            // 翻訳
            message("装備していた守護石が削除されました。");
        }
        warp(this.map_camp);
        return false;
    }
}
