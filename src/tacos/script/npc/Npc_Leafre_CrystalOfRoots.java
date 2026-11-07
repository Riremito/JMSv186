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
public class Npc_Leafre_CrystalOfRoots extends NpcScriptMan {

    // 迷路
    private final int[] map_mazes = {
        240050100, // リプレ : 迷路部屋
        240050101, // リプレ : 一番目の迷路部屋
        240050102, // リプレ : 二番目の迷路部屋
        240050103, // リプレ : 三番目の迷路部屋
        240050104, // リプレ : 四番目の迷路部屋
        240050105, // リプレ : 五番目の迷路部屋
        240050200, // リプレ : 選択の洞窟
        240050300, // リプレ : 光の洞窟
        240050310 // リプレ : 闇の洞窟
    };
    // 翻訳
    private final String[] maze_texts = {
        "水晶の鍵を持つ者だけが迷路の部屋に入れる。\r\n残った者だけが迷路の部屋の扉を開けられる。\r\nきらめく木の穴から望むものを得られる。\r\n炎で作られた鍵が洞窟を照らす。",
        "何かを得るには、何かを捨てなければならない。\r\nきらめく木の穴に、落としたいものを落とすことができる。",
        "何かを得るには、何かを捨てなければならない。\r\nきらめく木の穴に、落としたいものを落とすことができる。",
        "何かを得るには、何かを捨てなければならない。\r\nきらめく木の穴に、落としたいものを落とすことができる。",
        "何かを得るには、何かを捨てなければならない。\r\nきらめく木の穴に、落としたいものを落とすことができる。",
        "炎で作られた鍵を見つけたとき、迷路の終わりが見えるだろう。",
        "闇は闇に、光は光につながっている。\r\nあなたの選択が常に結果を生む。",
        "氷で作られた鍵が洞窟を照らす。",
        "氷で作られた鍵が洞窟を照らす。"
    };
    private final int map_cave_entrance = 240050000; // リプレ : 洞窟の入口
    private final int map_horntail_entrance = 240050400; // リプレ : ホーンテイルの洞窟入口
    private final int map_cave_exit = 240050500; // リプレ : 洞窟の出口
    // 遠征隊
    private final int[] map_expeditions = {
        240060000, // リプレ : 試験の洞窟1
        240060100, // リプレ : 試験の洞窟2
        240060200 // リプレ : ホーンテイルの洞窟
    };
    private final int quest_horntail_count = 7311; // クエスト名なし (ホーンテイルの洞窟の入場回数)
    private final String portal_name = "st00";
    private final int MENU_READ = 0;
    private final int MENU_EXIT = 1;
    private final int MENU_TOUCH = 0;
    private int maze_index = -1;
    private int warp_map = 0;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                if (this.asked) {
                    // 翻訳
                    say("もう一度考えてから話しかけよ。");
                }
                return false;
            }
            case 0 -> {
                for (int i = 0; i < this.map_mazes.length; i++) {
                    if (getMapId() == this.map_mazes[i]) {
                        this.maze_index = i;
                        this.warp_map = this.map_cave_exit;
                        NpcScriptTag nt = new NpcScriptTag();
                        // 翻訳
                        nt.add("絡み合った水晶の根の表面に文字が浮かび上がっています。\r\n");
                        nt.addMenu(MENU_READ, "文字をよく読む。");
                        nt.addMenu(MENU_EXIT, "諦めて外に出ますか？");
                        askMenu(nt);
                        return true;
                    }
                }
                if (getMapId() == this.map_horntail_entrance) {
                    this.asked = true;
                    this.warp_map = this.map_cave_entrance;
                    // 翻訳
                    askYesNo("#m" + this.map_cave_entrance + "#に戻りますか");
                    return true;
                }
                if (getMapId() == this.map_cave_exit) {
                    this.warp_map = this.map_cave_entrance;
                    NpcScriptTag nt = new NpcScriptTag();
                    // 翻訳
                    nt.add("水晶に洞窟の入口が映っています。触れればそこへ行けそうです。\r\n");
                    nt.addMenu(MENU_TOUCH, "水晶に触れる");
                    askMenu(nt);
                    return true;
                }
                for (int map_expedition : this.map_expeditions) {
                    if (getMapId() == map_expedition) {
                        this.asked = true;
                        this.warp_map = this.map_horntail_entrance;
                        String count = getQuestData(this.quest_horntail_count);
                        if ("2".equals(count)) {
                            // 翻訳
                            askYesNo("遠征隊を諦めて退場しますか？ 入場できるのは1日2回までです。今退場すると、今日はもう入場できません。");
                            return true;
                        }
                        // TODO : 入場回数が1または2以外の場合の処理 (luaでは何も表示されない)
                        // 翻訳
                        askYesNo("遠征隊を諦めて退場しますか？  入場できるのは1日2回までです。今退場すると、今日はあと1回しか入場できません。");
                        return true;
                    }
                }
                return false;
            }
            case 1 -> {
                if (0 <= this.maze_index && answer == MENU_READ) {
                    say(this.maze_texts[this.maze_index]);
                    return false;
                }
                if (getMapId() == this.map_cave_exit) {
                    // TODO : hontale_takeawayitem (アイテムの回収処理)
                }
                warp(this.warp_map, this.portal_name);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
