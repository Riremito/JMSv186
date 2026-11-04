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
// Npc.wz script : hontale_enter1 (JMS131-302), 会話文は lua_script_jp (hontale_enter1) を利用
public class Npc_Leafre_HontaleSign extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_ENTER = 1;
    private static final int NODE_RED_KEY = 2;
    private static final int NODE_BLUE_KEY = 3;
    private static final int NODE_CLEAR = 4;
    private static final int NODE_MOVE = 5;
    private final String field_set_name = "Hontale1";
    private final int map_cave_entrance = 240050000; // リプレ : 洞窟の入口
    private final int map_maze = 240050100; // リプレ : 迷路部屋
    private final int map_maze1 = 240050101; // リプレ : 一番目の迷路部屋
    private final int map_maze2 = 240050102; // リプレ : 二番目の迷路部屋
    private final int map_maze3 = 240050103; // リプレ : 三番目の迷路部屋
    private final int map_maze4 = 240050104; // リプレ : 四番目の迷路部屋
    private final int map_maze5 = 240050105; // リプレ : 五番目の迷路部屋
    private final int map_choice = 240050200; // リプレ : 選択の洞窟
    private final int map_light = 240050300; // リプレ : 光の洞窟
    private final int map_dark = 240050310; // リプレ : 闇の洞窟
    private final int map_hontale = 240050600; // リプレ : ホーンテイルの洞窟
    private final int item_red_key = 4001092; // 赤い鍵
    private final int item_blue_key = 4001093; // 青い鍵
    private final int[] item_hontale = {
        4001087, // 一番目の迷路部屋の水晶鍵
        4001088, // 二番目の迷路部屋の水晶鍵
        4001089, // 三番目の迷路部屋の水晶鍵
        4001090, // 四番目の迷路部屋の水晶鍵
        4001091, // 五番目の迷路部屋の水晶鍵
        4001092, // 赤い鍵
        4001093, // 青い鍵
    };
    private final int mob_skelegon = 9300076; // 洞窟スケルゴサウルス
    private final int mob_master_skelegon = 9300077; // 洞窟マスタースケルゴサウルス
    private final int mob_nobel_dran = 9300078; // 洞窟ノーブルドラン
    private final int mob_nobel_dran_golem = 9300079; // 洞窟ノーブルドランゴーレム
    private int node = NODE_START;
    private int last_status = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        TacosFieldSet field_set = getFieldSet(this.field_set_name);
        if (field_set == null) {
            return false;
        }
        int map_id = getMapId();
        boolean is_light = map_id == this.map_light;
        String var_blue = is_light ? "B1" : "B2";
        switch (this.node) {
            case NODE_START -> {
                if (map_id == this.map_cave_entrance) {
                    if (!isPartyBoss()) {
                        // 翻訳
                        say("お前がこの愚か者たちのリーダーか？");
                        return false;
                    }
                    NpcScriptTag nt = new NpcScriptTag();
                    // 翻訳
                    nt.add("生命の洞窟に足を踏み入れようなどと思うな…隠された鍵を見つけた者だけが我のもとへたどり着ける。この無謀なゲームに挑むか？\r\n#b#L0# はい、挑戦します#l", false);
                    askMenu(nt);
                    this.node = NODE_ENTER;
                    return true;
                }
                if (map_id == this.map_maze) {
                    if (!isPartyBoss()) {
                        // 翻訳
                        say("愚か者たちの代表として来たのか？");
                        return false;
                    }
                    if (getItemCount(this.item_red_key) <= 0) {
                        // 翻訳
                        say("選択の洞窟に入れるのは#t" + this.item_red_key + "#を持つ者だけだ。");
                        return false;
                    }
                    NpcScriptTag nt = new NpcScriptTag();
                    // 翻訳
                    nt.add("#r#t" + this.item_red_key + "##kを使って選択の洞窟へ移動しますか？\r\n#b#L0# はい、先へ進みます。#l", false);
                    askMenu(nt);
                    this.node = NODE_RED_KEY;
                    return true;
                }
                if (map_id == this.map_light || map_id == this.map_dark) {
                    if (!isPartyBoss()) {
                        // 翻訳
                        say("お前がこの愚か者たちのリーダーか？");
                        return false;
                    }
                    if (!field_set.getVar(var_blue).equals("1")) {
                        if (getItemCount(this.item_blue_key) < 6) {
                            // 翻訳
                            say("#b#t" + this.item_blue_key + "##kを6個持っているか？");
                            return false;
                        }
                        NpcScriptTag nt = new NpcScriptTag();
                        // 翻訳
                        nt.add("#b#t" + this.item_blue_key + "##kを6個使ってここから出るか？\r\n#b#L0#" + (is_light ? " " : "") + "青い鍵を使う。#l", false);
                        askMenu(nt);
                        this.node = NODE_BLUE_KEY;
                        return true;
                    }
                    NpcScriptTag nt = new NpcScriptTag();
                    // 翻訳
                    nt.add("この部屋のモンスターをすべて倒したか？\r\n#b#L0# すべて倒しました。#l", false);
                    askMenu(nt);
                    this.node = NODE_CLEAR;
                    return true;
                }
                return false;
            }
            case NODE_ENTER -> {
                int res = enterFieldSetParty(this.field_set_name, 0);
                // 翻訳
                switch (res) {
                    case TacosFieldSet.ENTER_SUCCESS -> {
                        // lua の hontale_takeawayitem
                        for (int item_id : this.item_hontale) {
                            removeItemAll(item_id);
                        }
                    }
                    case TacosFieldSet.ENTER_NO_PARTY, TacosFieldSet.ENTER_PARTY_MEMBER -> {
                        say("無謀な勇気を見せるな、愚か者め…もっと強い者たちと挑むがよい。");
                    }
                    case TacosFieldSet.ENTER_PARTY_LEVEL -> {
                        say("何様のつもりだ！お前は弱すぎる。帰れ。");
                    }
                    case TacosFieldSet.ENTER_USING -> {
                        say("すでに別のパーティーが挑戦中だ。そいつらもお前と同じくらい愚かだがな。");
                    }
                    default -> {
                        say("今は挑戦できない。後でまた来い。");
                    }
                }
                return false;
            }
            case NODE_RED_KEY -> {
                int count = getUserCount(this.map_maze1) + getUserCount(this.map_maze2) + getUserCount(this.map_maze3) + getUserCount(this.map_maze4) + getUserCount(this.map_maze5);
                if (0 < count) {
                    // 翻訳
                    say("まだ迷路の中に誰かいるのではないか？");
                    return false;
                }
                if (!exchange(0, this.item_red_key, -1)) {
                    // 翻訳
                    say("#t" + this.item_red_key + "#を持っているか確認せよ。");
                    return false;
                }
                transferFieldAll(this.map_choice, "st00");
                return false;
            }
            case NODE_BLUE_KEY -> {
                if (!exchange(0, this.item_blue_key, -6)) {
                    // 翻訳
                    say("#b#t" + this.item_blue_key + "##kを持っているか確認せよ。");
                    return false;
                }
                field_set.setVar(var_blue, "1");
                // lua の field.setMobGen( 0, 0 ) (モンスターの出現を停止する)
                setMobGen(map_id, false, 0);
                // 翻訳
                say("青い鍵でここから出るには、悪魔の魂をすべて浄化しなければならない。モンスターをすべて倒したら、もう一度話しかけよ。");
                return false;
            }
            case NODE_CLEAR -> {
                int count = is_light ? (getMobCount(map_id, this.mob_master_skelegon) + getMobCount(map_id, this.mob_skelegon)) : (getMobCount(map_id, this.mob_nobel_dran) + getMobCount(map_id, this.mob_nobel_dran_golem));
                if (0 < count) {
                    // 翻訳
                    say("この部屋はまだ浄化されていない。");
                    return false;
                }
                // 翻訳
                askYesNo("この部屋の浄化と#b#t" + this.item_blue_key + "##kの収集に成功した。ホーンテイルの洞窟へ移動するか？");
                this.node = NODE_MOVE;
                return true;
            }
            case NODE_MOVE -> {
                if (!ok) {
                    // 翻訳
                    say("もう一度考えてから話しかけよ。");
                    return false;
                }
                // lua の hontale_takeawayitem
                for (int item_id : this.item_hontale) {
                    removeItemAll(item_id);
                }
                transferFieldAll(this.map_hontale, "st00");
                return false;
            }
            default -> {
                return false;
            }
        }
    }
}
