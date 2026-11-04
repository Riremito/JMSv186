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
// Npc.wz script : refine_ellinia (JMS131-194), 会話文は lua_script_jp (refine_ellinia) を利用
public class NpcFrancois extends NpcScriptMan {

    // lua の関数
    private final int TYPE_CHAT_MESSAGE1 = 0; // chat_message1
    private final int TYPE_CHAT_MESSAGE2 = 1; // chat_message2
    private final int RECIPE = 1000;
    private final int PHASE_START = 0;
    private final int PHASE_MENU = 1;
    private final int PHASE_MENU_ANSWER = 2;
    private final int PHASE_RECIPE = 3;
    private final int PHASE_NUMBER_ANSWER = 4;
    private final int PHASE_EXECUTE = 5;
    // 翻訳
    private final String text_intro = "アイテムを見ていきたいのかい？それとも…作ろうと思っているのかな？実は私は、禁じられた呪文を唱えたせいで町を追放された魔法使いなんだ。それでここに隠れて、ちょっとした商売をしているのさ…まあ、そんなことはどうでもいい。私と取引したいかい？";
    // 翻訳
    private final String text_intro_no = "私の腕前を信用していないようだね…ははは…私はかつて偉大な魔法使いだったんだよ。まだ信じられないか…ふむ…だが、かつて私が偉大な魔法使いだったことは覚えておいてくれ…";
    // メニュー
    // 翻訳
    private final String[] menu_texts = {
        "よろしい…お互いのためだろう？やりたいことを選んでくれ… \r\n#b#L0#ワンドを作る#l\r\n#L1#スタッフを作る#l\r\n#L2#グローブを作る#l\r\n#L3#グローブを強化する#l\r\n#L4#帽子を強化する#l",
        "材料をたくさん持ってきてくれれば、私の魔力でワンドを作ってあげよう。さて…どんなワンドを作ってほしい？\r\n#L0##b #t1372005##k(制限レベル: 8, 全職業)#l\r\n#L1##b #t1372006##k(制限レベル: 13, 全職業)#l\r\n#L2##b #t1372002##k(制限レベル: 18, 全職業)#l\r\n#L3##b #t1372004##k(制限レベル: 23, 魔法使い)#l\r\n#L4##b #t1372003##k(制限レベル: 28, 魔法使い)#l\r\n#L5##b #t1372001##k(制限レベル: 33, 魔法使い)#l\r\n#L6##b #t1372000##k(制限レベル: 38, 魔法使い)#l\r\n#L7##b #t1372007##k(制限レベル: 48, 魔法使い)#l",
        "材料をたくさん持ってきてくれれば、私の魔力でスタッフを作ってあげよう。さて…どんなスタッフを作ってほしい？\r\n#L0##b#t1382000##k (制限レベル: 10, 魔法使い)#l\r\n#L1##b#t1382003##k(制限レベル: 15, 魔法使い)#l\r\n#L2##b#t1382005##k(制限レベル: 15, 魔法使い)#l\r\n#L3##b#t1382004##k(制限レベル: 20, 魔法使い)#l\r\n#L4##b#t1382002##k(制限レベル: 25, 魔法使い)#l\r\n#L5##b#t1382001##k(制限レベル: 45, 魔法使い)#l",
        "材料をたくさん持ってきてくれれば、私の魔力でグローブを作ってあげよう。さて…どんなグローブを作ってほしい？\r\n#L0##b #t1082019##k (制限レベル: 15, 魔法使い)#l\r\n#L1##b #t1082020##k(制限レベル: 20, 魔法使い)#l\r\n#L2##b #t1082026##k(制限レベル: 25, 魔法使い)#l\r\n#L3##b #t1082051##k(制限レベル: 30, 魔法使い)#l\r\n#L4##b #t1082054##k(制限レベル: 35, 魔法使い)#l\r\n#L5##b #t1082062##k(制限レベル: 40, 魔法使い)#l\r\n#L6##b #t1082081##k(制限レベル: 50, 魔法使い)#l\r\n#L7##b #t1082086##k(制限レベル: 60, 魔法使い)#l",
        "さて…どのグローブを強化したい？？\r\n#L0##b #t1082021##k(制限レベル: 20, 魔法使い)#l\r\n#L1##b #t1082022##k(制限レベル: 20, 魔法使い)#l\r\n#L2##b #t1082027##k(制限レベル: 25, 魔法使い)#l\r\n#L3##b #t1082028##k(制限レベル: 25, 魔法使い)#l\r\n#L4##b #t1082052##k(制限レベル: 30, 魔法使い)#l\r\n#L5##b #t1082053##k(制限レベル: 30, 魔法使い)#l\r\n#L6##b #t1082055##k(制限レベル: 35, 魔法使い)#l\r\n#L7##b #t1082056##k(制限レベル: 35, 魔法使い)#l\r\n#L8##b #t1082063##k(制限レベル: 40, 魔法使い)#l\r\n#L9##b #t1082064##k(制限レベル: 40, 魔法使い)#l\r\n#L10##b #t1082082##k(制限レベル: 50, 魔法使い)#l\r\n#L11##b #t1082080##k(制限レベル: 50, 魔法使い)#l\r\n#L12##b #t1082087##k(制限レベル: 60, 魔法使い)#l\r\n#L13##b #t1082088##k(制限レベル: 60, 魔法使い)#l",
        "いいだろう…どの帽子を強化したい？\r\n#L0##b#t1002065##k(制限レベル: 30, 魔法使い)#l\r\n#L1##b#t1002013##k(制限レベル: 30, 魔法使い)#l"
    };
    // メニューの前の会話
    // 翻訳
    private final String[][] menu_says = {
        {},
        {},
        {},
        {},
        {
            "グローブを強化したいのかい？気をつけて！強化に使うアイテムはすべてなくなるし、すでに書で#r強化#kされたアイテムを使っても、強化したときにその効果はなくなってしまう。だから決める前によく考えた方がいい…"
        },
        {
            "帽子を強化したいんだね…気をつけて！強化に使うアイテムはすべてなくなるし、すでに書で#r強化#kされたアイテムを使っても、強化したときにその効果はなくなってしまう。だから決める前によく考えた方がいい…"
        }
    };
    // メニューの選択肢 (RECIPE 以上はレシピ番号)
    private final int[][] menu_children = {
        {1, 2, 3, 4, 5},
        {RECIPE + 0, RECIPE + 1, RECIPE + 2, RECIPE + 3, RECIPE + 4, RECIPE + 5, RECIPE + 6, RECIPE + 7},
        {RECIPE + 8, RECIPE + 9, RECIPE + 10, RECIPE + 11, RECIPE + 12, RECIPE + 13},
        {RECIPE + 14, RECIPE + 15, RECIPE + 16, RECIPE + 17, RECIPE + 18, RECIPE + 19, RECIPE + 20, RECIPE + 21},
        {RECIPE + 22, RECIPE + 23, RECIPE + 24, RECIPE + 25, RECIPE + 26, RECIPE + 27, RECIPE + 28, RECIPE + 29, RECIPE + 30, RECIPE + 31, RECIPE + 32, RECIPE + 33, RECIPE + 34, RECIPE + 35},
        {RECIPE + 36, RECIPE + 37}
    };
    // レシピ (lua の関数, index)
    private final int[][] recipes = {
        {TYPE_CHAT_MESSAGE1, 1},
        {TYPE_CHAT_MESSAGE1, 2},
        {TYPE_CHAT_MESSAGE1, 3},
        {TYPE_CHAT_MESSAGE1, 4},
        {TYPE_CHAT_MESSAGE1, 5},
        {TYPE_CHAT_MESSAGE1, 6},
        {TYPE_CHAT_MESSAGE1, 7},
        {TYPE_CHAT_MESSAGE1, 8},
        {TYPE_CHAT_MESSAGE1, 100},
        {TYPE_CHAT_MESSAGE1, 101},
        {TYPE_CHAT_MESSAGE1, 102},
        {TYPE_CHAT_MESSAGE1, 103},
        {TYPE_CHAT_MESSAGE1, 104},
        {TYPE_CHAT_MESSAGE1, 105},
        {TYPE_CHAT_MESSAGE1, 200},
        {TYPE_CHAT_MESSAGE1, 201},
        {TYPE_CHAT_MESSAGE1, 202},
        {TYPE_CHAT_MESSAGE1, 203},
        {TYPE_CHAT_MESSAGE1, 204},
        {TYPE_CHAT_MESSAGE1, 205},
        {TYPE_CHAT_MESSAGE1, 206},
        {TYPE_CHAT_MESSAGE1, 207},
        {TYPE_CHAT_MESSAGE2, 1},
        {TYPE_CHAT_MESSAGE2, 2},
        {TYPE_CHAT_MESSAGE2, 3},
        {TYPE_CHAT_MESSAGE2, 4},
        {TYPE_CHAT_MESSAGE2, 5},
        {TYPE_CHAT_MESSAGE2, 6},
        {TYPE_CHAT_MESSAGE2, 7},
        {TYPE_CHAT_MESSAGE2, 8},
        {TYPE_CHAT_MESSAGE2, 9},
        {TYPE_CHAT_MESSAGE2, 10},
        {TYPE_CHAT_MESSAGE2, 11},
        {TYPE_CHAT_MESSAGE2, 12},
        {TYPE_CHAT_MESSAGE2, 13},
        {TYPE_CHAT_MESSAGE2, 14},
        {TYPE_CHAT_MESSAGE2, 100},
        {TYPE_CHAT_MESSAGE2, 101}
    };
    // レシピの文字列の引数
    // 翻訳
    private final String[][] recipe_texts = {
        {"#t1372005#", "#v4003001# #t4003001# 5個\r\n#v4031138# 1,000メル"},
        {"#t1372006#", "#v4003001# #t4003001# 10個\r\n#v4000001# #t4000001# 50個\r\n#v4031138# 3,000メル"},
        {"#t1372002#", "#v4011001# #t4011001#\r\n#v4000009# #t4000009# 30個\r\n#v4003000# #t4003000# 5個\r\n#v4031138# 5,000メル"},
        {"#t1372004#", "#v4011002# #t4011002# 2個\r\n#v4003002# #t4003002#\r\n#v4003000# #t4003000# 10個\r\n#v4031138# 12,000メル"},
        {"#t1372003#", "#v4011002# #t4011002# 3個\r\n#v4021002# #t4021002#\r\n#v4003000# #t4003000# 10個\r\n#v4031138# 30,000メル"},
        {"#t1372001#", "#v4021006# #t4021006# 5個\r\n#v4011002# #t4011002# 3個\r\n#v4011001# #t4011001#\r\n#v4003000# #t4003000# 15個\r\n#v4031138# 60,000メル"},
        {"#t1372000#", "#v4021006# #t4021006# 5個\r\n#v4021005# #t4021005# 5個\r\n#v4021007# #t4021007#\r\n#v4003003# #t4003003#\r\n#v4003000# #t4003000# 20個\r\n#v4031138# 120,000メル"},
        {"#t1372007#", "#v4011006# #t4011006# 4個\r\n#v4021003# #t4021003# 3個\r\n#v4021007# #t4021007# 2個\r\n#v4021002# #t4021002#\r\n#v4003002# #t4003002#\r\n#v4003000# #t4003000# 30個\r\n#v4031138# 200,000メル"},
        {"#t1382000#", "#v4003001# #t4003001# 5個\r\n2,000メル"},
        {"#t1382003#", "#v4021005# #t4021005#\r\n#v4011001# #t4011001#\r\n#v4003000# #t4003000# 5個\r\n#v4031138# 2,000メル"},
        {"#t1382005#", "#v4021003# #t4021003#\r\n#v4011001# #t4011001#\r\n#v4003000# #t4003000# 5個\r\n#v4031138# 2,000メル"},
        {"#t1382004#", "#v4003001# #t4003001# 50個\r\n#v4011001# #t4011001#\r\n#v4003000# #t4003000# 10個\r\n#v4031138# 5,000メル"},
        {"#t1382002#", "#v4021006# #t4021006# 2個\r\n#v4021001# #t4021001#\r\n#v4011001# #t4011001#\r\n#v4003000# #t4003000# 15個\r\n#v4031138# 12,000メル"},
        {"#t1382001#", "#v4011001# #t4011001# 3個\r\n#v4021001# #t4021001# 5個\r\n#v4021006# #t4021006# 5個\r\n#v4021005# #t4021005# 5個\r\n#v4003003# #t4003003#\r\n#v4000010# #t4000010# 50個\r\n#v4003000# #t4003000# 30個\r\n#v4031138# 180,000メル"},
        {"#t1082019#", "#v4000021# #t4000021# 15個\r\n#v4031138# 7,000メル"},
        {"#t1082020#", "#v4000021# #t4000021# 30個\r\n#v4011001# #t4011001#\r\n#v4031138# 15,000メル"},
        {"#t1082026#", "#v4000021# #t4000021# 50個\r\n#v4011006# #t4011006# 2個\r\n#v4031138# 20,000メル"},
        {"#t1082051#", "#v4000021# #t4000021# 60個\r\n#v4021006# #t4021006#\r\n#v4021000# #t4021000# 2個\r\n#v4031138# 25,000メル"},
        {"#t1082054#", "#v4000021# #t4000021# 70個\r\n#v4011006# #t4011006#\r\n#v4011001# #t4011001# 3個\r\n#v4021000# #t4021000# 2個\r\n#v4031138# 30,000メル"},
        {"#t1082062#", "#v4000021# #t4000021# 80個\r\n#v4021000# #t4021000# 3個\r\n#v4021006# #t4021006# 3個\r\n#v4003000# #t4003000# 30個\r\n#v4031138# 40,000メル"},
        {"#t1082081#", "#v4021000# #t4021000# 3個\r\n#v4011006# #t4011006# 2個\r\n#v4000030# #t4000030# 35個\r\n#v4003000# #t4003000# 40個\r\n#v4031138# 50,000メル"},
        {"#t1082086#", "#v4011007# #t4011007#\r\n#v4011001# #t4011001# 8個\r\n#v4021007# #t4021007#\r\n#v4000030# #t4000030# 50個\r\n#v4003000# #t4003000# 50個\r\n#v4031138# 70,000メル"},
        {"#t1082021#", "#v1082020# #t1082020#\r\n#t4021003#\r\n#v4031138# 20,000メル", "INT +1"},
        {"#t1082022#", "#v1082020# #t1082020#\r\n#v4021001# #t4021001# 2個\r\n#v4031138# 25,000メル", "INT +2"},
        {"#t1082027#", "#v1082026# #t1082026#\r\n#v4021000# #t4021000# 3個\r\n#v4031138# 30,000メル", "INT +1"},
        {"#t1082028#", "#v1082026# #t1082026#\r\n#v4021008# #t4021008#\r\n#v4031138# 40,000メル", "INT +2"},
        {"#t1082052#", "#v1082051# #t1082051#\r\n#v4021005# #t4021005# 3個\r\n#v4031138# 35,000メル", "INT +1"},
        {"#t1082053#", "#v1082051# #t1082051#\r\n#v4021008# #t4021008#s\r\n#v4031138# 40,000メル", "INT +2"},
        {"#t1082055#", "#v1082054# #t1082054#\r\n#v4021005# #t4021005# 3個\r\n#v4031138# 40,000メル", "INT +1"},
        {"#t1082056#", "#v1082054# #t1082054#\r\n#v4021008# #t4021008#s\r\n#v4031138# 45,000メル", "INT +2"},
        {"#t1082063#", "#v1082062# #t1082062#\r\n#v4021002# #t4021002# 4個\r\n#v4031138# 45,000メル", "INT +2"},
        {"#t1082064#", "#v1082062# #t1082062#\r\n#v4021008# #t4021008# 2個\r\n#v4031138# 50,000メル", "INT +3"},
        {"#t1082082#", "#v1082081# #t1082081#\r\n#v4021002# #t4021002# 5個\r\n#v4031138# 55,000メル", "INT +2, MP +15"},
        {"#t1082080#", "#v1082081# #t1082081#\r\n#v4021008# #t4021008# 3個\r\n#v4031138# 60,000メル", "INT +3, MP +30"},
        {"#t1082087#", "#v1082086# #t1082086#\r\n#v4011004# #t4011004# 3個\r\n#v4011006# #t4011006# 5個\r\n#v4031138# 70,000メル", "INT +2, LUK +1, MP +15"},
        {"#t1082088#", "#v1082086# #t1082086#\r\n#v4021008# #t4021008# 2個\r\n#v4011006# #t4011006# 3個\r\n#v4031138# 80,000メル", "INT + 3, LUK + 1, MP + 30"},
        {"#t1002065#", "#v1002064# #t1002064#\r\n#v4011001# #t4011001# 3個\r\n#v4031138# 40,000メル", "INT +1"},
        {"#t1002013#", "#v1002064# #t1002064#\r\n#v4011006# #t4011006# 3個\r\n#v4031138# 50,000メル", "INT +2"}
    };
    // レシピの数値の引数
    private final int[][] recipe_values = {
        {8},
        {13},
        {18},
        {23},
        {28},
        {33},
        {38},
        {48},
        {10},
        {15},
        {15},
        {20},
        {25},
        {45},
        {15},
        {20},
        {25},
        {30},
        {35},
        {40},
        {50},
        {60},
        {20},
        {20},
        {25},
        {25},
        {30},
        {30},
        {35},
        {35},
        {40},
        {40},
        {50},
        {50},
        {60},
        {60},
        {30},
        {30}
    };
    // 所持金の増減, アイテムIDと増減数の組 (個数を指定する場合は1個あたり)
    private final int[][] recipe_exchanges = {
        {-1000, 4003001, -5, 1372005, 1}, // 1372005 ウッドワンド
        {-3000, 4003001, -10, 4000001, -50, 1372006, 1}, // 1372006 ハードウッドワンド
        {-5000, 4011001, -1, 4000009, -30, 4003000, -5, 1372002, 1}, // 1372002 メタルワンド
        {-12000, 4011002, -2, 4003002, -1, 4003000, -10, 1372004, 1}, // 1372004 アイスワンド
        {-30000, 4011002, -3, 4021002, -1, 4003000, -10, 1372003, 1}, // 1372003 ミスリルワンド
        {-60000, 4021006, -5, 4011002, -3, 4011001, -1, 4003000, -15, 1372001, 1}, // 1372001 ウィザードワンド
        {-120000, 4021006, -5, 4021005, -5, 4021007, -1, 4003003, -1, 4003000, -20, 1372000, 1}, // 1372000 フェアリーワンド
        {-200000, 4011006, -4, 4021003, -3, 4021007, -2, 4021002, -1, 4003002, -1, 4003000, -30, 1372007, 1}, // 1372007 クロミ
        {-2000, 4003001, -5, 1382000, 1}, // 1382000 ウッドスタッフ
        {-2000, 4021005, -1, 4011001, -1, 4003000, -5, 1382003, 1}, // 1382003 サファイアスタッフ
        {-2000, 4021003, -1, 4011001, -1, 4003000, -5, 1382005, 1}, // 1382005 エメラルドスタッフ
        {-5000, 4003001, -50, 4011001, -1, 4003000, -10, 1382004, 1}, // 1382004 枯木のスタッフ
        {-12000, 4021006, -2, 4021001, -1, 4011001, -1, 4003000, -15, 1382002, 1}, // 1382002 ウィジャードスタッフ
        {-180000, 4011001, -3, 4021001, -5, 4021006, -5, 4021005, -5, 4003003, -1, 4000010, -50, 4003000, -30, 1382001, 1}, // 1382001 アークスタッフ
        {-7000, 4000021, -15, 1082019, 1}, // 1082019 レモナ
        {-15000, 4000021, -30, 4011001, -1, 1082020, 1}, // 1082020 モリガン(青)
        {-20000, 4000021, -50, 4011006, -2, 1082026, 1}, // 1082026 メッサナ(藍)
        {-25000, 4000021, -60, 4021006, -1, 4021000, -2, 1082051, 1}, // 1082051 ルティア(赤)
        {-30000, 4000021, -70, 4011006, -1, 4011001, -3, 4021000, -2, 1082054, 1}, // 1082054 ノエル(赤)
        {-40000, 4000021, -80, 4021000, -3, 4021006, -3, 4003000, -30, 1082062, 1}, // 1082062 アルテン(赤)
        {-50000, 4021000, -3, 4011006, -2, 4000030, -35, 4003000, -40, 1082081, 1}, // 1082081 ペナンス(赤)
        {-70000, 4011007, -1, 4011001, -8, 4021007, -1, 4000030, -50, 4003000, -50, 1082086, 1}, // 1082086 鋼マヌット
        {-20000, 1082020, -1, 4021003, -1, 1082021, 1}, // 1082021 モリガン(緑)
        {-25000, 1082020, -1, 4021001, -2, 1082022, 1}, // 1082022 モリガン(紫)
        {-30000, 1082026, -1, 4021000, -3, 1082027, 1}, // 1082027 メッサナ(血)
        {-40000, 1082026, -1, 4021008, -1, 1082028, 1}, // 1082028 メッサナ(黒)
        {-35000, 1082051, -1, 4021005, -3, 1082052, 1}, // 1082052 ルティア(青)
        {-40000, 1082051, -1, 4021008, -1, 1082053, 1}, // 1082053 ルティア(黒)
        {-40000, 1082054, -1, 4021005, -3, 1082055, 1}, // 1082055 ノエル(青)
        {-45000, 1082054, -1, 4021008, -1, 1082056, 1}, // 1082056 ノエル(黒)
        {-45000, 1082062, -1, 4021002, -4, 1082063, 1}, // 1082063 アルテン(青)
        {-50000, 1082062, -1, 4021008, -2, 1082064, 1}, // 1082064 アルテン(黒)
        {-55000, 1082081, -1, 4021002, -5, 1082082, 1}, // 1082082 ペナンス(青)
        {-60000, 1082081, -1, 4021008, -3, 1082080, 1}, // 1082080 ペナンス(黒)
        {-70000, 1082086, -1, 4011004, -3, 4011006, -5, 1082087, 1}, // 1082087 マヌット(金)
        {-80000, 1082086, -1, 4021008, -2, 4011006, -3, 1082088, 1}, // 1082088 マヌット(黒)
        {-40000, 1002064, -1, 4011001, -3, 1002065, 1}, // 1002065 孫悟空の鋼鉄鉢巻
        {-50000, 1002064, -1, 4011006, -3, 1002013, 1} // 1002013 孫悟空の鉢巻(金)
    };
    private int phase = PHASE_START;
    private int last_status = 0;
    private int node = 0;
    private int said = 0;
    private int recipe = 0;
    private int number = 0;
    private String text_no = "";

    @Override
    public boolean action(int status, int answer) {
        if (status < this.last_status) {
            // askYesNo でいいえを選択した
            if (!this.text_no.isEmpty()) {
                say(this.text_no);
            }
            return false;
        }
        this.last_status = status;
        if (this.phase == PHASE_START) {
            this.phase = PHASE_MENU;
            this.text_no = this.text_intro_no;
            askYesNo(this.text_intro);
            return true;
        }
        if (this.phase == PHASE_MENU_ANSWER) {
            if (answer < 0 || this.menu_children[this.node].length <= answer || this.menu_children[this.node][answer] < 0) {
                return false;
            }
            int child = this.menu_children[this.node][answer];
            if (child < RECIPE) {
                this.node = child;
                this.said = 0;
                this.phase = PHASE_MENU;
            } else {
                this.recipe = child - RECIPE;
                this.phase = PHASE_RECIPE;
            }
        }
        if (this.phase == PHASE_MENU) {
            if (this.said < this.menu_says[this.node].length) {
                // メニューがない場合は会話のみ
                if (this.menu_texts[this.node].isEmpty() && this.said == this.menu_says[this.node].length - 1) {
                    say(this.menu_says[this.node][this.said]);
                    return false;
                }
                say(this.menu_says[this.node][this.said++], false, true);
                return true;
            }
            if (this.menu_texts[this.node].isEmpty()) {
                return false;
            }
            NpcScriptTag nt = new NpcScriptTag();
            nt.add(this.menu_texts[this.node], false);
            askMenu(nt);
            this.phase = PHASE_MENU_ANSWER;
            return true;
        }
        if (this.phase == PHASE_NUMBER_ANSWER) {
            this.number = answer;
        }
        int type = this.recipes[this.recipe][0];
        int index = this.recipes[this.recipe][1];
        String[] texts = this.recipe_texts[this.recipe];
        int[] values = this.recipe_values[this.recipe];
        boolean is_number = false;
        int number_default = 0;
        int number_min = 0;
        int number_max = 0;
        String text_ask = "";
        String text_confirm = "";
        String text_answer_no = "";
        String text_fail = "";
        String text_ok = "";
        switch (type) {
            case TYPE_CHAT_MESSAGE1 -> {
                String make_item = texts[0];
                String need_item = texts[1];
                int req_level = values[0];
                text_confirm = "" + make_item + "を作るには、以下のアイテムが必要だ。このアイテムの制限レベルは" + req_level + "だから、まずは本当にそのアイテムが必要か確認してくれ。本当に作るかい？\r\n\r\n#b" + need_item;
                text_answer_no = "そうか？材料が全部そろっていないんだろう。町で全部集めてみるといい。幸い、森の周りのモンスターはいろんな種類の材料を持っているようだ。";
                text_fail = "必要なアイテムがすべてそろっているか、ETCインベントリがいっぱいでないか確認してくれ。";
                text_ok = "ほら、" + make_item + "だ。見れば見るほど完璧に思えるよ。ははは、ほかの魔法使いたちが私の腕前を恐れていると思っても罰は当たらないだろう…";
            }
            case TYPE_CHAT_MESSAGE2 -> {
                String make_item = texts[0];
                String need_item = texts[1];
                int req_level = values[0];
                String item_option = texts[2];
                text_confirm = "" + make_item + "を強化するには、以下のアイテムが必要だ。このアイテムの制限レベルは" + req_level + "で、#r" + item_option + "#kのオプションが付いているから、本当に必要か確かめてくれ。ああ！それともう一つ。強化済みのアイテムを強化の材料に「使わない」よう気をつけてくれ。本当にこのアイテムを作るかい？\r\n\r\n#b" + need_item;
                text_answer_no = "そうか？材料が全部そろっていないんだろう。町で全部集めてみるといい。幸い、森の周りのモンスターはいろんな種類の材料を持っているようだ。";
                text_fail = "必要なアイテムがすべてそろっているか、ETCインベントリがいっぱいでないか確認してくれ。";
                text_ok = "ほら、" + make_item + "…見れば見るほど完璧に思える…私の腕前のせいで、ほかの魔法使いたちに危険人物だと思われても驚かないね…";
            }
            default -> {
                return false;
            }
        }
        if (this.phase == PHASE_RECIPE && is_number) {
            this.phase = PHASE_NUMBER_ANSWER;
            askNumber(text_ask, number_default, number_min, number_max);
            return true;
        }
        if (this.phase == PHASE_RECIPE || this.phase == PHASE_NUMBER_ANSWER) {
            this.phase = PHASE_EXECUTE;
            this.text_no = text_answer_no;
            askYesNo(text_confirm);
            return true;
        }
        if (this.phase != PHASE_EXECUTE) {
            return false;
        }
        int[] base = this.recipe_exchanges[this.recipe];
        int count = is_number ? this.number : 1;
        int[] items = new int[base.length - 1];
        for (int i = 0; i < items.length; i++) {
            items[i] = (i % 2 == 0) ? base[i + 1] : base[i + 1] * count;
        }
        if (!exchange(base[0] * count, items)) {
            say(text_fail);
            return false;
        }
        say(text_ok);
        return false;
    }
}
