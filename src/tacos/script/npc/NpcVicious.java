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
// Npc.wz script : refine_henesys (JMS131-194), 会話文は lua_script_jp (refine_henesys) を利用
public class NpcVicious extends NpcScriptMan {

    // lua の関数
    private final int TYPE_CHAT_MESSAGE1 = 0; // chat_message1
    private final int TYPE_CHAT_MESSAGE2 = 1; // chat_message2
    private final int TYPE_CHAT_MESSAGE3 = 2; // chat_message3
    private final int TYPE_CHAT_MESSAGE6 = 3; // chat_message6
    private final int RECIPE = 1000;
    private final int PHASE_START = 0;
    private final int PHASE_MENU = 1;
    private final int PHASE_MENU_ANSWER = 2;
    private final int PHASE_RECIPE = 3;
    private final int PHASE_NUMBER_ANSWER = 4;
    private final int PHASE_EXECUTE = 5;
    // 翻訳
    private final String text_intro = "ねえ、そこのあなた～何か必要なものはない？ここではタダのものはないけど…少しの材料と少しの手数料があれば、アイテムを作ってあげられるわ。どう？いい取引をしない？ ただ、この町の誰にもこのことは知られないようにね。";
    // 翻訳
    private final String text_intro_no = "あまり興味がなさそうね。後で何か必要になったら、またここに来て。お店では見つからないアイテムだって手に入れてあげられるわよ。";
    // メニュー
    // 翻訳
    private final String[] menu_texts = {
        "いいわね～！何をしてあげようか？必要なものを言ってちょうだい！ \r\n#b#L0# 弓を作る#l\r\n#L1# クロスボウを作る#l\r\n#L2# グローブを作る#l\r\n#L3# グローブを強化する#l\r\n#L4# 材料を作る#l\r\n#L5# 矢を作る#l",
        "材料を持ってきてくれたら弓を作ってあげるわ。どんな弓がほしい？\r\n#L0##b #t1452002##k(制限レベル: 10, 弓使い)#l\r\n#L1##b #t1452003##k(制限レベル: 15, 弓使い)#l\r\n#L2##b #t1452001##k(制限レベル: 20, 弓使い)#l\r\n#L3##b #t1452000##k(制限レベル: 25, 弓使い)#l\r\n#L4##b #t1452005##k(制限レベル: 30, 弓使い)#l\r\n#L5##b #t1452006##k(制限レベル: 35, 弓使い)#l\r\n#L6##b #t1452007##k(制限レベル: 40, 弓使い)#l",
        "材料を持ってきてくれたらクロスボウを作ってあげるわ。どんなクロスボウがほしい？\r\n#L0##b #t1462001##k(制限レベル: 12, 弓使い)#l\r\n#L1##b #t1462002##k(制限レベル: 18, 弓使い)#l\r\n#L2##b #t1462003##k(制限レベル: 22, 弓使い)#l\r\n#L3##b #t1462000##k(制限レベル: 28, 弓使い)#l\r\n#L4##b #t1462004##k(制限レベル: 32, 弓使い)#l\r\n#L5##b #t1462005##k(制限レベル: 38, 弓使い)#l\r\n#L6##b #t1462006##k(制限レベル: 42, 弓使い)#l\r\n#L7##b #t1462007##k(制限レベル: 50, 弓使い)#l",
        "材料を持ってきてくれたらグローブを作ってあげるわ。どんなグローブがほしい？\r\n#L0##b #t1082012##k(制限レベル: 15, 弓使い)#l\r\n#L1##b #t1082013##k(制限レベル: 20, 弓使い)#l\r\n#L2##b #t1082016##k(制限レベル: 25, 弓使い)#l\r\n#L3##b #t1082048##k(制限レベル: 30, 弓使い)#l\r\n#L4##b #t1082068##k(制限レベル: 35, 弓使い)#l\r\n#L5##b #t1082071##k(制限レベル: 40, 弓使い)#l\r\n#L6##b #t1082084##k(制限レベル: 50, 弓使い)#l\r\n#L7##b #t1082089##k(制限レベル: 60, 弓使い)#l",
        "どれどれ、どのグローブを強化したい？\r\n#L0##b #t1082014##k(制限レベル: 20, 弓使い)#l\r\n#L1##b #t1082015##k(制限レベル: 20, 弓使い)#l\r\n#L2##b #t1082017##k(制限レベル: 25, 弓使い)#l\r\n#L3##b #t1082018##k(制限レベル: 25, 弓使い)#l\r\n#L4##b #t1082049##k(制限レベル: 30, 弓使い)#l\r\n#L5##b #t1082050##k(制限レベル: 30, 弓使い)#l\r\n#L6##b #t1082069##k(制限レベル: 35, 弓使い)#l\r\n#L7##b #t1082070##k(制限レベル: 35, 弓使い)#l\r\n#L8##b#t1082072##k(制限レベル: 40, 弓使い)#l\r\n#L9##b#t1082073##k(制限レベル: 40, 弓使い)#l\r\n#L10##b#t1082085##k(制限レベル: 50, 弓使い)#l\r\n#L11##b#t1082083##k(制限レベル: 50, 弓使い)#l\r\n#L12##b #t1082090##k(制限レベル: 60, 弓使い)#l\r\n#L13##b #t1082091##k(制限レベル: 60, 弓使い)#l",
        "材料を作りたいのね？よし…どんな材料？\r\n#L0##b #t4000003#で#t4003001#を作る#k#l\r\n#L1##b #t4000018#で#t4003001#を作る#k#l\r\n#L2##b #t4003000#を作る#k#l",
        "矢を作りたいのね？いい矢ほど、戦いで有利になるわよ。よし、どんな矢？\r\n#L0##b #t2060000##k#l\r\n#L1##b #t2061000##k#l\r\n#L2##b #t2060001##k#l\r\n#L3##b #t2061001##k#l"
    };
    // メニューの前の会話
    // 翻訳
    private final String[][] menu_says = {
        {},
        {},
        {},
        {},
        {
            "グローブを強化したいの？気をつけた方がいいわよ。強化に使うアイテムはすべてなくなるし、すでに書で#r強化#kされたアイテムを使っても、強化したときにその効果はなくなってしまうの。よく考えてね。"
        },
        {},
        {}
    };
    // メニューの選択肢 (RECIPE 以上はレシピ番号)
    private final int[][] menu_children = {
        {1, 2, 3, 4, 5, 6},
        {RECIPE + 0, RECIPE + 1, RECIPE + 2, RECIPE + 3, RECIPE + 4, RECIPE + 5, RECIPE + 6},
        {RECIPE + 7, RECIPE + 8, RECIPE + 9, RECIPE + 10, RECIPE + 11, RECIPE + 12, RECIPE + 13, RECIPE + 14},
        {RECIPE + 15, RECIPE + 16, RECIPE + 17, RECIPE + 18, RECIPE + 19, RECIPE + 20, RECIPE + 21, RECIPE + 22},
        {RECIPE + 23, RECIPE + 24, RECIPE + 25, RECIPE + 26, RECIPE + 27, RECIPE + 28, RECIPE + 29, RECIPE + 30, RECIPE + 31, RECIPE + 32, RECIPE + 33, RECIPE + 34, RECIPE + 35, RECIPE + 36},
        {RECIPE + 37, RECIPE + 38, RECIPE + 39},
        {RECIPE + 40, RECIPE + 41, RECIPE + 42, RECIPE + 43}
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
        {TYPE_CHAT_MESSAGE1, 100},
        {TYPE_CHAT_MESSAGE1, 101},
        {TYPE_CHAT_MESSAGE1, 102},
        {TYPE_CHAT_MESSAGE1, 103},
        {TYPE_CHAT_MESSAGE1, 104},
        {TYPE_CHAT_MESSAGE1, 105},
        {TYPE_CHAT_MESSAGE1, 106},
        {TYPE_CHAT_MESSAGE1, 107},
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
        {TYPE_CHAT_MESSAGE3, 1},
        {TYPE_CHAT_MESSAGE3, 2},
        {TYPE_CHAT_MESSAGE3, 3},
        {TYPE_CHAT_MESSAGE6, 1},
        {TYPE_CHAT_MESSAGE6, 2},
        {TYPE_CHAT_MESSAGE6, 3},
        {TYPE_CHAT_MESSAGE6, 4}
    };
    // レシピの文字列の引数
    // 翻訳
    private final String[][] recipe_texts = {
        {"#t1452002#", "#v4003001# #t4003001# 5個 \r\n#v4000000# #t4000000# 30個 \r\n#v4031138# 800メル"},
        {"#t1452003#", "#v4011001# #t4011001# \r\n#v4003000# #t4003000# 3個 \r\n#v4031138# 2,000メル"},
        {"#t1452001#", "#v4003001# #t4003001# 30個 \r\n#v4000016# #t4000016# 50個 \r\n#v4031138# 3,000メル"},
        {"#t1452000#", "#v4011001# #t4011001# 2個 \r\n#v4021006# #t4021006# 2個 \r\n#v4003000# #t4003000# 8個 \r\n#v4031138# 5,000メル"},
        {"#t1452005#", "#v4011001# #t4011001# 5個 \r\n#v4011006# #t4011006# 5個 \r\n#v4021003# #t4021003# 3個 \r\n#v4021006# #t4021006# 2個 \r\n#v4003000# #t4003000# 30個 \r\n#v4031138# 30,000メル"},
        {"#t1452006#", "#v4011004# #t4011004# 7個 \r\n#v4021000# #t4021000# 6個 \r\n#v4021004# #t4021004# 3個 \r\n#v4003000# #t4003000# 35個 \r\n#v4031138# 40,000メル"},
        {"#t1452007#", "#v4021008# #t4021008# \r\n#v4011001# #t4011001# 10個 \r\n#v4011006# #t4011006# 3個 \r\n#v4000014# #t4000014# 50個 \r\n#v4003000# #t4003000# 40個 \r\n#v4031138# 80,000メル"},
        {"#t1462001#", "#v4003001# #t4003001# 7個 \r\n#v4003000# #t4003000# 2個 \r\n#v4031138# 1,000メル"},
        {"#t1462002#", "#v4003001# #t4003001# 20個 \r\n#v4011001# #t4011001# \r\n#v4003000# #t4003000# 5個 \r\n#v4031138# 2,000メル"},
        {"#t1462003#", "#v4003001# #t4003001# 50個 \r\n#v4011001# #t4011001# \r\n#v4003000# #t4003000# 8個 \r\n#v4031138# 3,000メル"},
        {"#t1462000#", "#v4011001# #t4011001# 2個 \r\n#v4021006# #t4021006# \r\n#v4021002# #t4021002# \r\n#v4003000# #t4003000# 10個 \r\n#v4031138# 10,000メル"},
        {"#t1462004#", "#v4011001# #t4011001# 5個 \r\n#v4011005# #t4011005# 5個 \r\n#v4021006# #t4021006# 3個 \r\n#v4003001# #t4003001# 50個 \r\n#v4003000# #t4003000# 15個 \r\n#v4031138# 30,000メル"},
        {"#t1462005#", "#v4021008# #t4021008# \r\n#v4011001# #t4011001# 8個 \r\n#v4011006# #t4011006# 4個 \r\n#v4021006# #t4021006# 2個 \r\n#v4003000# #t4003000# 30個 \r\n#v4031138# 50,000メル"},
        {"#t1462006#", "#v4021008# #t4021008# 2個 \r\n#v4011004# #t4011004# 6個 \r\n#v4003001# #t4003001# 30個 \r\n#v4003000# #t4003000# 30個 \r\n#v4031138# 80,000メル"},
        {"#t1462007#", "#v4021008# #t4021008# 2個 \r\n#v4011006# #t4011006# 5個 \r\n#v4021006# #t4021006# 3個 \r\n#v4003001# #t4003001# 40個 \r\n#v4003000# #t4003000# 40個 \r\n#v4031138# 100,000メル"},
        {"#t1082012#", "#v4000021# #t4000021# 15個 \r\n#v4000009# #t4000009# 20個 \r\n#v4031138# 5,000メル"},
        {"#t1082013#", "#v4000021# #t4000021# 20個 \r\n#v4000009# #t4000009# 20個 \r\n#v4011001# #t4011001# 2個 \r\n#v4031138# 10,000メル"},
        {"#t1082016#", "#v4000021# #t4000021# 40個 \r\n#v4000009# #t4000009# 50個 \r\n#v4011006# #t4011006# 2個 \r\n#v4031138# 15,000メル"},
        {"#t1082048#", "#v4000021# #t4000021# 50個 \r\n#v4021001# #t4021001# \r\n#v4011006# #t4011006# 2個 \r\n#v4031138# 20,000メル"},
        {"#t1082068#", "#v4000021# #t4000021# 60個 \r\n#v4011001# #t4011001# 3個 \r\n#v4011000# #t4011000# \r\n#v4003000# #t4003000# 15個 \r\n#v4031138# 30,000メル"},
        {"#t1082071#", "#v4000021# #t4000021# 80個 \r\n#v4021002# #t4021002# 3個 \r\n#v4011001# #t4011001# 3個 \r\n#v4021000# #t4021000# \r\n#v4003000# #t4003000# 25個 \r\n#v4031138# 40,000メル"},
        {"#t1082084#", "#v4011004# #t4011004# 3個 \r\n#v4021002# #t4021002# 2個 \r\n#v4011006# #t4011006# \r\n#v4000030# #t4000030# 40個 \r\n#v4003000# #t4003000# 35個 \r\n#v4031138# 50,000メル"},
        {"#t1082089#", "#v4011007# #t4011007# \r\n#v4021006# #t4021006# 8個 \r\n#v4011006# #t4011006# 2個 \r\n#v4000030# #t4000030# 50個 \r\n#v4003000# #t4003000# 50個 \r\n#v4031138# 70,000メル"},
        {"#t1082014#", "#v1082013# #t1082013# \r\n#v4021000# #t4021000# \r\n#v4031138# 5,000メル"},
        {"#t1082015#", "#v1082013# #t1082013# \r\n#v4021003# #t4021003# 2個 \r\n#v4031138# 7,000メル"},
        {"#t1082017#", "#v1082016# #t1082016# \r\n#v4021000# #t4021000# 3個 \r\n#v4031138# 10,000メル"},
        {"#t1082018#", "#v1082016# #t1082016# \r\n#v4021008# #t4021008# \r\n#v4031138# 12,000メル"},
        {"#t1082049#", "#v1082048# #t1082048# \r\n#v4021003# #t4021003# 3個 \r\n#v4031138# 15,000メル"},
        {"#t1082050#", "#v1082048# #t1082048# \r\n#v4021008# #t4021008# \r\n#v4031138# 20,000メル"},
        {"#t1082069#", "#v1082068# #t1082068# \r\n#v4011002# #t4011002# 4個 \r\n#v4031138# 22,000メル"},
        {"#t1082070#", "#v1082068# #t1082068# \r\n#v4011006# #t4011006# 2個 \r\n#v4031138# 25,000メル"},
        {"#t1082072#", "#v1082071# #t1082071# \r\n#v4011006# #t4011006# 4個 \r\n#v4031138# 30,000メル"},
        {"#t1082073#", "#v1082071# #t1082071# \r\n#v4021008# #t4021008# 2個 \r\n#v4031138# 40,000メル"},
        {"#t1082085#", "#v1082084# #t1082084# \r\n#v4021000# #t4021000# 5個 \r\n#v4011000# #t4011000# \r\n#v4031138# 55,000メル"},
        {"#t1082083#", "#v1082084# #t1082084# \r\n#v4021008# #t4021008# 2個 \r\n#v4011006# #t4011006# 2個 \r\n#v4031138# 60,000メル"},
        {"#t1082090#", "#v1082089# #t1082089# \r\n#v4021007# #t4021007# \r\n#v4021000# #t4021000# 5個 \r\n#v4031138# 70,000メル"},
        {"#t1082091#", "#v1082089# #t1082089# \r\n#v4021007# #t4021007# 2個 \r\n#v4021008# #t4021008# 2個 \r\n#v4031138# 80,000メル"},
        {"#t4003001#", "#t4000003#"},
        {"#t4003001#", "#t4000018#(s)"},
        {"#t4003000#", "#t4011001#と#t4011000#を各"},
        {"#t2060000#", "#v4003001# #t4003001# \r\n#v4003004# #t4003004# ", ""},
        {"#t2061000#", "#v4003001# #t4003001# \r\n#v4003004# #t4003004# ", ""},
        {"#t2060001#", "#v4011000# #t4011000# \r\n#v4003001# 3 #t4003001#s \r\n#v4003004# 10 #t4003004#s ", "攻撃力 +1"},
        {"#t2061001#", "#v4011000# #t4011000# \r\n#v4003001# 3 #t4003001#s \r\n#v4003004# 10 #t4003004#s ", "攻撃力 +1"}
    };
    // レシピの数値の引数
    private final int[][] recipe_values = {
        {10},
        {15},
        {20},
        {25},
        {30},
        {35},
        {40},
        {12},
        {18},
        {22},
        {28},
        {32},
        {38},
        {42},
        {50},
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
        {10, 1},
        {5, 1},
        {1, 15},
        {1000},
        {1000},
        {900},
        {900}
    };
    // 所持金の増減, アイテムIDと増減数の組 (個数を指定する場合は1個あたり)
    private final int[][] recipe_exchanges = {
        {-800, 4003001, -5, 4000000, -30, 1452002, 1}, // 1452002 ウォーボウ
        {-2000, 4011001, -1, 4003000, -3, 1452003, 1}, // 1452003 合金弓
        {-3000, 4003001, -30, 4000016, -50, 1452001, 1}, // 1452001 狩人の弓
        {-5000, 4011001, -2, 4021006, -2, 4003000, -8, 1452000, 1}, // 1452000 バトルボウ
        {-30000, 4011001, -5, 4011006, -5, 4021003, -3, 4021006, -2, 4003000, -30, 1452005, 1}, // 1452005 ライデン
        {-40000, 4011004, -7, 4021000, -6, 4021004, -3, 4003000, -35, 1452006, 1}, // 1452006 レッドバイパー
        {-80000, 4021008, -1, 4011001, -10, 4011006, -3, 4000014, -50, 4003000, -40, 1452007, 1}, // 1452007 バルター2000
        {-1000, 4003001, -7, 4003000, -2, 1462001, 1}, // 1462001 弩
        {-2000, 4003001, -20, 4011001, -1, 4003000, -5, 1462002, 1}, // 1462002 戦闘弩
        {-3000, 4003001, -50, 4011001, -1, 4003000, -8, 1462003, 1}, // 1462003 バルランシェー
        {-10000, 4011001, -2, 4021006, -1, 4021002, -1, 4003000, -10, 1462000, 1}, // 1462000 山羊弩
        {-30000, 4011001, -5, 4011005, -5, 4021006, -3, 4003001, -50, 4003000, -15, 1462004, 1}, // 1462004 イーグルクロー
        {-50000, 4021008, -1, 4011001, -8, 4011006, -4, 4021006, -2, 4003000, -20, 1462005, 1}, // 1462005 ヘクラー
        {-80000, 4021008, -2, 4011004, -6, 4003001, -30, 4003000, -30, 1462006, 1}, // 1462006 シルバークロウ
        {-100000, 4021008, -2, 4011006, -5, 4021006, -3, 4003001, -40, 4003000, -40, 1462007, 1}, // 1462007 ローア
        {-5000, 4000021, -15, 4000009, -20, 1082012, 1}, // 1082012 アル
        {-10000, 4000021, -20, 4000009, -20, 4011001, -2, 1082013, 1}, // 1082013 ディロス(青)
        {-15000, 4000021, -40, 4000009, -50, 4011006, -2, 1082016, 1}, // 1082016 サバタ(青)
        {-20000, 4000021, -50, 4021001, -1, 4011006, -2, 1082048, 1}, // 1082048 マーカー(茶)
        {-30000, 4000021, -60, 4011001, -3, 4011000, -1, 4003000, -15, 1082068, 1}, // 1082068 ブロンズスケイラ
        {-40000, 4000021, -80, 4021002, -3, 4011001, -3, 4021000, -1, 4003000, -25, 1082071, 1}, // 1082071 ブレイス(海)
        {-50000, 4011004, -3, 4021002, -2, 4011006, -1, 4000030, -40, 4003000, -35, 1082084, 1}, // 1082084 ウィロー(青)
        {-70000, 4011007, -1, 4021006, -8, 4011006, -2, 4000030, -50, 4003000, -50, 1082089, 1}, // 1082089 オーカガーナ
        {-5000, 1082013, -1, 4021000, -1, 1082014, 1}, // 1082014 ディロス(赤)
        {-7000, 1082013, -1, 4021003, -2, 1082015, 1}, // 1082015 ディロス(緑)
        {-10000, 1082016, -1, 4021000, -3, 1082017, 1}, // 1082017 サバタ(赤)
        {-12000, 1082016, -1, 4021008, -1, 1082018, 1}, // 1082018 サバタ(黒)
        {-15000, 1082048, -1, 4021003, -3, 1082049, 1}, // 1082049 マーカー(緑)
        {-20000, 1082048, -1, 4021008, -1, 1082050, 1}, // 1082050 マーカー(黒)
        {-22000, 1082068, -1, 4011002, -4, 1082069, 1}, // 1082069 ミスリルスケイラ
        {-25000, 1082068, -1, 4011006, -2, 1082070, 1}, // 1082070 スケイラ(金)
        {-30000, 1082071, -1, 4011006, -4, 1082072, 1}, // 1082072 ブレイス(金)
        {-40000, 1082071, -1, 4021008, -2, 1082073, 1}, // 1082073 ブレイス(黒)
        {-55000, 1082084, -1, 4021000, -5, 4011000, -1, 1082085, 1}, // 1082085 ウィロー(赤)
        {-60000, 1082084, -1, 4021008, -2, 4011006, -2, 1082083, 1}, // 1082083 ウィロー(黒)
        {-70000, 1082089, -1, 4021007, -1, 4021000, -5, 1082090, 1}, // 1082090 セピアガーナ
        {-80000, 1082089, -1, 4021007, -2, 4021008, -2, 1082091, 1}, // 1082091 ダークガーナ
        {0, 4000003, -10, 4003001, 1}, // 4003001 加工された木
        {0, 4000018, -5, 4003001, 1}, // 4003001 加工された木
        {0, 4011001, -1, 4011000, -1, 4003000, 15}, // 4003000 ネジ
        {0, 4003001, -1, 4003004, -1, 2060000, 1000}, // 2060000 弓専用の矢
        {0, 4003001, -1, 4003004, -1, 2061000, 1000}, // 2061000 弩専用の矢
        {0, 4011000, -1, 4003001, -3, 4003004, -10, 2060001, 900}, // 2060001 弓専用の矢(青銅)
        {0, 4011000, -1, 4003001, -3, 4003004, -10, 2061001, 900} // 2061001 弩専用の矢(青銅)
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
                text_confirm = "" + make_item + "を作るには、以下の材料が必要よ。制限レベルは" + req_level + "。それと、強化中のアイテムを材料に使わないよう気をつけてね。どう？作る？\r\n\r\n#b" + need_item;
                text_answer_no = "あら、そうなの？材料がないのね？また後で来てちょうだい。私はしばらくここにいるつもりだから。";
                text_fail = "必要なアイテムがすべてそろっているか、装備インベントリがいっぱいでないか、よく確認してくれ。";
                text_ok = "はい！" + make_item + "よ。ちょっと手間がかかったけど…素晴らしい仕上がりになったわ。また何か必要になったら話しかけてね。その日まで、じゃあね..";
            }
            case TYPE_CHAT_MESSAGE2 -> {
                String make_item = texts[0];
                String need_item = texts[1];
                int req_level = values[0];
                text_confirm = "" + make_item + "を作るには、以下の材料が必要よ。制限レベルは" + req_level + "だ。それと、強化中のアイテムを材料に使わないよう気をつけてくれ。どうだい？作るかい？\r\n\r\n#b" + need_item;
                text_answer_no = "あら、そうなの？材料がないのね？また後で来てちょうだい。私はしばらくここにいるつもりだから。";
                text_fail = "必要なアイテムがすべてそろっているか、ETCインベントリがいっぱいでないか、よく確認してくれ。";
                text_ok = "はい！" + make_item + "よ。ちょっと手間がかかったけど…素晴らしい仕上がりになったわ。また何か必要になったら話しかけてね。その日まで、じゃあね..";
            }
            case TYPE_CHAT_MESSAGE3 -> {
                String make_item = texts[0];
                String need_item = texts[1];
                int need_number = values[0];
                int item_number = values[1];
                int ret_num = this.number;
                int need_num = ret_num * need_number;
                int all_num = ret_num * item_number;
                is_number = true;
                number_default = 0;
                number_min = 0;
                number_max = 100;
                text_ask = "#b" + need_number + " " + need_item + "#kで、" + item_number + " " + make_item + "を作ってあげられるわ。材料を持ってきてくれたらタダで作ってあげる。さて…いくつ作ってほしい？";
                text_confirm = "#b" + make_item + "#k " + ret_num + "回作る？それなら#r" + need_num + " " + need_item + "#k.";
                text_answer_no = "あら、そうなの？材料がないのね？また後で来てちょうだい。私はしばらくここにいるつもりだから。";
                text_fail = "必要なアイテムがすべてそろっているか、ETCインベントリがいっぱいでないか、よく確認してくれ。";
                text_ok = "よう！ほら、" + all_num + " " + make_item + "よ。ちょっと手間がかかったけど…素晴らしい仕上がりになったわ。また何か必要になったら話しかけてね。その日まで、じゃあね..";
            }
            case TYPE_CHAT_MESSAGE6 -> {
                String make_item = texts[0];
                String need_item = texts[1];
                int unit_num = values[0];
                String item_option = texts[2];
                if (item_option.isEmpty()) {
                    text_confirm = "#b" + unit_num + " "+ make_item + "#kを作るには、以下のアイテムが必要よ。材料を持っているなら、お店で買うよりここで作った方がずっといいわ。本当に作る？\r\n\r\n#b" + need_item;
                } else {
                    text_confirm = "#b" + unit_num + " "+ make_item + "#kを作るには、以下のアイテムが必要よ。ちなみに、この矢には#b" + item_option + "#kの強化も付いているの。特別な矢だから、必要な材料を持っているなら作ってみたらどう？本当に作ってほしい？\r\n\r\n#b" + need_item;
                }
                text_answer_no = "私は弓使いに大切なアイテムを作って、手頃な値段で売っているの。遠慮なく全部見ていってね。タダのものなんてないのはわかってるわよね？";
                text_fail = "必要なアイテムがすべてそろっているか、消費インベントリがいっぱいでないか、よく確認してね。";
                text_ok = "よう！ほら、" + unit_num + " " + make_item + "よ。ちょっと手間がかかったけど…素晴らしい仕上がりになったわ。また何か必要になったら話しかけてね。その日まで、じゃあね..";
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
