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
// Npc.wz script : refine_kerning (JMS131-194), 会話文は lua_script_jp (refine_kerning) を利用
public class NpcJMFromThaStreetz extends NpcScriptMan {

    // lua の関数
    private final int TYPE_CHAT_MESSAGE6 = 0; // chat_message6
    private final int TYPE_CHAT_MESSAGE7 = 1; // chat_message7
    private final int TYPE_CHAT_MESSAGE8 = 2; // chat_message8
    private final int TYPE_CHAT_MESSAGE9 = 3; // chat_message9
    private final int RECIPE = 1000;
    private final int PHASE_START = 0;
    private final int PHASE_MENU = 1;
    private final int PHASE_MENU_ANSWER = 2;
    private final int PHASE_RECIPE = 3;
    private final int PHASE_NUMBER_ANSWER = 4;
    private final int PHASE_EXECUTE = 5;
    // 翻訳
    private final String text_intro = "もしかして鉱物か#t4000021#を持っていないか？手数料をもらえば、完璧な盗賊用装備を作ってやれるぜ。ああ、そうそう！この町での俺の商売のことは誰にも言うなよ。で、どうする？";
    // 翻訳
    private final String text_intro_no = "そうか？俺に任せてくれれば、これっぽっちも後悔させないんだがな…気が変わったら俺を訪ねてこい。取引しようぜ…";
    // メニュー
    // 翻訳
    private final String[] menu_texts = {
        "まあ、手数料は手頃だから心配いらないぜ。何をしたい？\r\n#b#L0# アームを作る#l\r\n#L1# グローブを作る#l\r\n#L2# アームを強化する#l\r\n#L3# グローブを強化する#l\r\n#L4# 材料を作る#l",
        "アームは手裏剣を投げるためにはめるグローブだ。でも、短剣を使う盗賊には役に立たない。さて、どんなアームを作ってほしい？\r\n#L0##b #t1472001##k(制限レベル: 15, 盗賊)#l\r\n#L1##b #t1472004##k(制限レベル: 20, 盗賊)#l\r\n#L2##b #t1472008##k(制限レベル: 30, 盗賊)#l\r\n#L3##b #t1472011##k(制限レベル: 35, 盗賊)#l\r\n#L4##b #t1472014##k(制限レベル: 40, 盗賊)#l\r\n#L5##b #t1472018##k(制限レベル: 50, 盗賊)#l",
        "さて…どんなグローブを作ってほしい？\r\n#L0##b #t1082002##k(制限レベル: 10, 全職業)#l\r\n#L1##b #t1082029##k(制限レベル: 15, 盗賊)#l\r\n#L2##b #t1082030##k(制限レベル: 15, 盗賊)#l\r\n#L3##b #t1082031##k(制限レベル: 15, 盗賊)#l\r\n#L4##b #t1082032##k(制限レベル: 20, 盗賊)#l\r\n#L5##b #t1082037##k(制限レベル: 25, 盗賊)#l\r\n#L6##b #t1082042##k(制限レベル: 30, 盗賊)#l\r\n#L7##b #t1082046##k(制限レベル: 35, 盗賊)#l\r\n#L8##b #t1082075##k(制限レベル: 40, 盗賊)#l\r\n#L9##b #t1082065##k(制限レベル: 50, 盗賊)#l\r\n#L10##b #t1082092##k(制限レベル: 60, 盗賊)#l",
        "アームは手裏剣を投げるために身につける装備だ。でも、短剣を使う盗賊には役に立たない。さて、どんなアームを作りたい？\r\n#L0##b #t1472002##k(制限レベル: 15, 盗賊)#l\r\n#L1##b #t1472003##k(制限レベル: 15, 盗賊)#l\r\n#L2##b #t1472005##k(制限レベル: 20, 盗賊)#k#l\r\n#L3##b #t1472006##k(制限レベル: 20, 盗賊)#l\r\n#L4##b #t1472007##k(制限レベル: 25, 盗賊)#l\r\n#L5##b #t1472009##k(制限レベル: 30, 盗賊)#l\r\n#L6##b #t1472010##k(制限レベル: 30, 盗賊)#l\r\n#L7##b #t1472012##k(制限レベル: 35, 盗賊)#l\r\n#L8##b #t1472013##k(制限レベル: 35, 盗賊)#l\r\n#L9##b #t1472015##k(制限レベル: 40, 盗賊)#l\r\n#L10##b #t1472016##k(制限レベル: 40, 盗賊)#l\r\n#L11##b #t1472017##k(制限レベル: 40, 盗賊)#l\r\n#L12##b #t1472019##k(制限レベル: 50, 盗賊)#l\r\n#L13##b #t4021005# #k(制限レベル: 50, 盗賊)#l",
        "それで…どのグローブを強化したい？\r\n#L0##b #t1082033##k(制限レベル: 20, 盗賊)#l\r\n#L1##b #t1082034##k(制限レベル: 20, 盗賊)#l\r\n#L2##b #t1082038##k(制限レベル: 25, 盗賊)#l\r\n#L3##b #t1082039##k(制限レベル: 25, 盗賊)#l\r\n#L4##b #t1082043##k(制限レベル: 30, 盗賊)#l\r\n#L5##b #t1082044##k(制限レベル: 30, 盗賊)#l\r\n#L6##b #t1082047##k(制限レベル: 35, 盗賊)#l\r\n#L7##b #t1082045##k(制限レベル: 35, 盗賊)#l\r\n#L8##b #t1082076##k(制限レベル: 40, 盗賊)#l\r\n#L9##b #t1082074##k(制限レベル: 40, 盗賊)#l\r\n#L10##b #t1082067##k(制限レベル: 50, 盗賊)#l\r\n#L11##b #t1082066##k(制限レベル: 50, 盗賊)#l\r\n#L12##b #t1082093##k(制限レベル: 60, 盗賊)#l\r\n#L13##b #t1082094##k(制限レベル: 60, 盗賊)#l",
        "材料を作りたいって？よし…どんな材料だ？\r\n#L0##b #t4000003#で#t4003001#を作る#k#l\r\n#L1##b #t4000018#で#t4003001#を作る#k#l\r\n#L2##b #t4003000#を作る#k#l"
    };
    // メニューの前の会話
    // 翻訳
    private final String[][] menu_says = {
        {},
        {},
        {},
        {
            "アームを強化したいのか？よし、わかった。だが一つ忠告しておこう。強化に使うアイテムはすべてなくなるし、すでに書で#r強化#kされたアイテムを使っても、強化したときにその効果は引き継がれない。それを踏まえて決めてくれよ、いいな？"
        },
        {
            "グローブを強化したいのか？よし、わかった。だが一つ忠告しておこう。強化に使うアイテムはすべてなくなるし、すでに書で#r強化#kされたアイテムを使っても、強化したときにその効果は引き継がれない。それを踏まえて決めてくれよ、いいな？"
        },
        {}
    };
    // メニューの選択肢 (RECIPE 以上はレシピ番号)
    private final int[][] menu_children = {
        {1, 2, 3, 4, 5},
        {RECIPE + 0, RECIPE + 1, RECIPE + 2, RECIPE + 3, RECIPE + 4, RECIPE + 5},
        {RECIPE + 6, RECIPE + 7, RECIPE + 8, RECIPE + 9, RECIPE + 10, RECIPE + 11, RECIPE + 12, RECIPE + 13, RECIPE + 14, RECIPE + 15, RECIPE + 16},
        {RECIPE + 17, RECIPE + 18, RECIPE + 19, RECIPE + 20, RECIPE + 21, RECIPE + 22, RECIPE + 23, RECIPE + 24, RECIPE + 25, RECIPE + 26, RECIPE + 27, RECIPE + 28, RECIPE + 29, RECIPE + 30},
        {RECIPE + 31, RECIPE + 32, RECIPE + 33, RECIPE + 34, RECIPE + 35, RECIPE + 36, RECIPE + 37, RECIPE + 38, RECIPE + 39, RECIPE + 40, RECIPE + 41, RECIPE + 42, RECIPE + 43, RECIPE + 44},
        {RECIPE + 45, RECIPE + 46, RECIPE + 47}
    };
    // レシピ (lua の関数, index)
    private final int[][] recipes = {
        {TYPE_CHAT_MESSAGE6, 1},
        {TYPE_CHAT_MESSAGE6, 2},
        {TYPE_CHAT_MESSAGE6, 3},
        {TYPE_CHAT_MESSAGE6, 4},
        {TYPE_CHAT_MESSAGE6, 5},
        {TYPE_CHAT_MESSAGE6, 6},
        {TYPE_CHAT_MESSAGE6, 100},
        {TYPE_CHAT_MESSAGE6, 101},
        {TYPE_CHAT_MESSAGE6, 102},
        {TYPE_CHAT_MESSAGE6, 103},
        {TYPE_CHAT_MESSAGE6, 104},
        {TYPE_CHAT_MESSAGE6, 105},
        {TYPE_CHAT_MESSAGE6, 106},
        {TYPE_CHAT_MESSAGE6, 107},
        {TYPE_CHAT_MESSAGE6, 108},
        {TYPE_CHAT_MESSAGE6, 109},
        {TYPE_CHAT_MESSAGE6, 110},
        {TYPE_CHAT_MESSAGE7, 1},
        {TYPE_CHAT_MESSAGE7, 2},
        {TYPE_CHAT_MESSAGE7, 3},
        {TYPE_CHAT_MESSAGE7, 4},
        {TYPE_CHAT_MESSAGE7, 5},
        {TYPE_CHAT_MESSAGE7, 6},
        {TYPE_CHAT_MESSAGE7, 7},
        {TYPE_CHAT_MESSAGE7, 8},
        {TYPE_CHAT_MESSAGE7, 9},
        {TYPE_CHAT_MESSAGE8, 1},
        {TYPE_CHAT_MESSAGE8, 2},
        {TYPE_CHAT_MESSAGE8, 3},
        {TYPE_CHAT_MESSAGE8, 4},
        {TYPE_CHAT_MESSAGE8, 5},
        {TYPE_CHAT_MESSAGE7, 100},
        {TYPE_CHAT_MESSAGE7, 101},
        {TYPE_CHAT_MESSAGE7, 102},
        {TYPE_CHAT_MESSAGE7, 103},
        {TYPE_CHAT_MESSAGE7, 104},
        {TYPE_CHAT_MESSAGE7, 105},
        {TYPE_CHAT_MESSAGE7, 106},
        {TYPE_CHAT_MESSAGE7, 107},
        {TYPE_CHAT_MESSAGE7, 108},
        {TYPE_CHAT_MESSAGE7, 109},
        {TYPE_CHAT_MESSAGE7, 110},
        {TYPE_CHAT_MESSAGE7, 111},
        {TYPE_CHAT_MESSAGE7, 112},
        {TYPE_CHAT_MESSAGE7, 113},
        {TYPE_CHAT_MESSAGE9, 1},
        {TYPE_CHAT_MESSAGE9, 2},
        {TYPE_CHAT_MESSAGE9, 3}
    };
    // レシピの文字列の引数
    // 翻訳
    private final String[][] recipe_texts = {
        {"#t1472001#", "#v4011001# #t4011001# \r\n#v4000021# #t4000021# 20個 \r\n#v4003000# #t4003000# 5個 \r\n#v4031138# 2000メル"},
        {"#t1472004#", "#v4011000# #t4011000# 2個 \r\n#v4011001# #t4011001# \r\n#v4000021# #t4000021# 30個 \r\n#v4003000# #t4003000# 10個 \r\n#v4031138# 3000メル"},
        {"#t1472008#", "#v4011000# #t4011000# 3個 \r\n#v4011001# #t4011001# 2個 \r\n#v4000021# #t4000021# 50個 \r\n#v4003000# #t4003000# 20個 \r\n#v4031138# 15000メル"},
        {"#t1472011#", "#v4011000# #t4011000# 4個 \r\n#v4011001# #t4011001# 3個 \r\n#v4000021# 80#t4000021#s \r\n#v4003000# #t4003000# 25個 \r\n#v4031138# 30000メル"},
        {"#t1472014#", "#v4011000# #t4011000# 3個 \r\n#v4011001# #t4011001# 4個 \r\n#v4000021# #t4000021# 100個 \r\n#v4003000# #t4003000# 30個 \r\n#v4031138# 40000メル"},
        {"#t1472018#", "#v4011000# #t4011000# 4個 \r\n#v4011001# #t4011001# 5個 \r\n#v4000030# #t4000030# 40個 \r\n#v4003000# #t4003000# 35個 \r\n#v4031138# 50000メル"},
        {"#t1082002#", "#v4000021# #t4000021# 15個 \r\n#v4031138# 1,000メル"},
        {"#t1082029#", "#v4000021# #t4000021# 30個 \r\n#v4000018# #t4000018# 20個 \r\n#v4031138# 7,000メル"},
        {"#t1082030#", "#v4000021# #t4000021# 30個 \r\n#v4000015# #t4000015# 20個 \r\n#v4031138# 7,000メル"},
        {"#t1082031#", "#v4000021# #t4000021# 30個 \r\n#v4000020# #t4000020# 20個 \r\n#v4031138# 7,000メル"},
        {"#t1082032#", "#v4011000# #t4011000# 2個 \r\n#v4000021# #t4000021# 40個 \r\n#v4031138# 10,000メル"},
        {"#t1082037#", "#v4011000# #t4011000# 2個 \r\n#v4011001# #t4011001# \r\n#v4000021# #t4000021# 10個 \r\n#v4031138# 15,000メル"},
        {"#t1082042#", "#v4011001# #t4011001# 2個 \r\n#v4000021# #t4000021# 50個 \r\n#v4003000# #t4003000# 10個 \r\n#v4031138# 25,000メル"},
        {"#t1082046#", "#v4011001# #t4011001# 3個 \r\n#v4011000# #t4011000# \r\n#v4000021# #t4000021# 60個 \r\n#v4003000# #t4003000# 15個 \r\n#v4031138# 30,000メル"},
        {"#t1082075#", "#v4021000# #t4021000# 3個 \r\n#v4000014# #t4000014# 200個 \r\n#v4000021# #t4000021# 80個 \r\n#v4003000# #t4003000# 30個 \r\n#v4031138# 40,000メル"},
        {"#t1082065#", "#v4021005# #t4021005# 3個 \r\n#v4021008# #t4021008# \r\n#v4000030# #t4000030# 40個 \r\n#v4003000# #t4003000# 30個 \r\n#v4031138# 50,000メル"},
        {"#t1082092#", "#v4011007# #t4011007# \r\n#v4011000# #t4011000# 8個 \r\n#v4021007# #t4021007# \r\n#v4000030# #t4000030# 50個 \r\n#v4003000# #t4003000# 50個 \r\n#v4031138# 70,000メル"},
        {"#t1472002#", "#v1472001# #t1472001# \r\n#v4011002# #t4011002# \r\n#v4031138# 1,000メル"},
        {"#t1472003#", "#v1472001# #t1472001# \r\n#v4011006# #t4011006# \r\n#v4031138# 2,000メル"},
        {"#t1472005#", "#v1472004# #t1472004# \r\n#v4011001# #t4011001# 2個 \r\n#v4031138# 3,000メル"},
        {"#t1472006#", "#v1472004# #t1472004# \r\n#v4011003# #t4011003# 2個 \r\n#v4031138# 5,000メル"},
        {"#t1472007#", "#v1472000# #t1472000# \r\n#v4011001# #t4011001# 3個 \r\n#v4000021# #t4000021# 20個 \r\n#v4003001# #t4003001# 30個 \r\n#v4031138# 5,000メル"},
        {"#t1472009#", "#v1472008# #t1472008# \r\n#v4011002# #t4011002# 3個 \r\n#v4031138# 10,000メル"},
        {"#t1472010#", "#v1472008# #t1472008# \r\n#v4011003# #t4011003# 3個 \r\n#v4031138# 15,000メル"},
        {"#t1472012#", "#v1472011# #t1472011# \r\n#v4011004# #t4011004# 4個 \r\n#v4031138# 20,000メル"},
        {"#t1472013#", "#v1472011# #t1472011# \r\n#v4021008# #t4021008# \r\n#v4031138# 25,000メル"},
        {"#t1472015#", "#v1472014# #t1472014# \r\n#v4021000# #t4021000# 5個 \r\n#v4031138# 30,000メル", "DEX +2"},
        {"#t1472016#", "#v1472014# #t1472014# \r\n#v4011003# #t4011003# 5個 \r\n#v4031138# 30,000メル", "LUK +2"},
        {"#t1472017#", "#v1472014# #t1472014# \r\n#v4021008# #t4021008# 2個 \r\n#v4031138# 35,000メル", "LUK +3"},
        {"#t1472019#", "#v1472018# #t1472018# \r\n#v4021000# #t4021000# 6個 \r\n#v4031138# 40,000メル", "DEX +3"},
        {"#t1472020#", "#v1472018# #t1472018# \r\n#v4021005# #t4021005# 6個 \r\n#v4031138# 40,000メル", "LUK +3"},
        {"#t1082033#", "#v1082032# #t1082032# \r\n#v4011002# #t4011002# \r\n#v4031138# 5,000メル"},
        {"#t1082034#", "#v1082032# #t1082032# \r\n#v4021004# #t4021004# \r\n#v4031138# 7,000メル"},
        {"#t1082038#", "#v1082037# #t1082037# \r\n#v4011002# #t4011002# 2個 \r\n#v4031138# 10,000メル"},
        {"#t1082039#", "#v1082037# #t1082037# \r\n#v4021004# #t4021004# 2個 \r\n#v4031138# 12,000メル"},
        {"#t1082043#", "#v1082042# #t1082042# \r\n#v4011004# #t4011004# 2個 \r\n#v4031138# 15,000メル"},
        {"#t1082044#", "#v1082042# #t1082042# \r\n#v4011006# #t4011006# \r\n#v4031138# 20,000メル"},
        {"#t1082047#", "#v1082046# #t1082046# \r\n#v4011005# #t4011005# 3個 \r\n#v4031138# 22,000メル"},
        {"#t1082045#", "#v1082046# #t1082046# \r\n#v4011006# #t4011006# 2個 \r\n#v4031138# 25,000メル"},
        {"#t1082076#", "#v1082075# #t1082075# \r\n#v4011006# #t4011006# 4個 \r\n#v4031138# 45,000メル"},
        {"#t1082074#", "#v1082075# #t1082075# \r\n#v4021008# #t4021008# 2個 \r\n#v4031138# 50,000メル"},
        {"#t1082067#", "#v1082065# #t1082065# \r\n#v4021000# #t4021000# 5個 \r\n#v4031138# 55,000メル"},
        {"#t1082066#", "#v1082065# #t1082065# \r\n#v4011006# #t4011006# 2個 \r\n#v4021008# #t4021008# \r\n#v4031138# 60,000メル"},
        {"#t1082093#", "#v1082092# #t1082092# \r\n#v4011001# #t4011001# 7個 \r\n#v4000014# #t4000014# 200個 \r\n#v4031138# 70,000メル"},
        {"#t1082094#", "#v1082092# #t1082092# \r\n#v4011006# #t4011006# 7個 \r\n#v4000027# #t4000027# 150個 \r\n#v4031138# 80,000メル"},
        {"#t4003001#(s)", "#t4000003#es"},
        {"#t4003001#(s)", "#t4000018#s"},
        {"#t4003000#s", "#t4011001#と#t4011000#を各"}
    };
    // レシピの数値の引数
    private final int[][] recipe_values = {
        {15},
        {20},
        {30},
        {35},
        {40},
        {50},
        {10},
        {15},
        {15},
        {15},
        {20},
        {25},
        {30},
        {35},
        {40},
        {50},
        {60},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {10, 1},
        {5, 1},
        {1, 15}
    };
    // 所持金の増減, アイテムIDと増減数の組 (個数を指定する場合は1個あたり)
    private final int[][] recipe_exchanges = {
        {-2000, 4011001, -1, 4000021, -20, 4003000, -5, 1472001, 1}, // 1472001 スチールティタンズ
        {-3000, 4011000, -2, 4011001, -1, 4000021, -30, 4003000, -10, 1472004, 1}, // 1472004 ブロンズイゴール
        {-15000, 4011000, -3, 4011001, -2, 4000021, -50, 4003000, -20, 1472008, 1}, // 1472008 スチールカーズ
        {-30000, 4011000, -4, 4011001, -3, 4000021, -80, 4003000, -25, 1472011, 1}, // 1472011 ブロンズガーディアン
        {-40000, 4011000, -3, 4011001, -4, 4000021, -100, 4003000, -30, 1472014, 1}, // 1472014 スチールボニン
        {-50000, 4011000, -4, 4011001, -5, 4000030, -40, 4003000, -35, 1472018, 1}, // 1472018 スチールスレイン
        {-1000, 4000021, -15, 1082002, 1}, // 1082002 軍手
        {-7000, 4000021, -30, 4000018, -20, 1082029, 1}, // 1082029 デュオ(茶)
        {-7000, 4000021, -30, 4000015, -20, 1082030, 1}, // 1082030 デュオ(青)
        {-7000, 4000021, -30, 4000020, -20, 1082031, 1}, // 1082031 デュオ(黒)
        {-10000, 4011000, -2, 4000021, -40, 1082032, 1}, // 1082032 ブロンズスチルライン
        {-15000, 4011000, -2, 4011001, -1, 4000021, -10, 1082037, 1}, // 1082037 ブロンズウッドシップ
        {-25000, 4011001, -2, 4000021, -50, 4003000, -10, 1082042, 1}, // 1082042 鋼シルビア
        {-30000, 4011001, -3, 4011000, -1, 4000021, -60, 4003000, -15, 1082046, 1}, // 1082046 スチルアルビヨン
        {-40000, 4000021, -80, 4021000, -3, 4000014, -200, 4003000, -30, 1082075, 1}, // 1082075 クリフ(赤)
        {-50000, 4021005, -3, 4021008, -1, 4000030, -40, 4003000, -30, 1082065, 1}, // 1082065 青月
        {-70000, 4011007, -1, 4011000, -8, 4021007, -1, 4000030, -50, 4003000, -50, 1082092, 1}, // 1082092 ブロンズパーウ
        {-1000, 1472001, -1, 4011002, -1, 1472002, 1}, // 1472002 ミスリルティタンズ
        {-2000, 1472001, -1, 4011006, -1, 1472003, 1}, // 1472003 ゴールドティタンズ
        {-3000, 1472004, -1, 4011001, -2, 1472005, 1}, // 1472005 スチールイゴール
        {-5000, 1472004, -1, 4011003, -2, 1472006, 1}, // 1472006 アダマンイゴール
        {-5000, 1472000, -1, 4011001, -3, 4000021, -20, 4003001, -30, 1472007, 1}, // 1472007 メーバ
        {-10000, 1472008, -1, 4011002, -3, 1472009, 1}, // 1472009 ミスリルカーズ
        {-15000, 1472008, -1, 4011003, -3, 1472010, 1}, // 1472010 アダマンカーズ
        {-20000, 1472011, -1, 4011004, -4, 1472012, 1}, // 1472012 シルバーガーディアン
        {-25000, 1472011, -1, 4021008, -1, 1472013, 1}, // 1472013 ダークガーディアン
        {-30000, 1472014, -1, 4021000, -5, 1472015, 1}, // 1472015 ブラッドボニン
        {-30000, 1472014, -1, 4011003, -5, 1472016, 1}, // 1472016 アダマンボニン
        {-35000, 1472014, -1, 4021008, -2, 1472017, 1}, // 1472017 ダークボニン
        {-40000, 1472018, -1, 4021000, -6, 1472019, 1}, // 1472019 ブラッドスレイン
        {-40000, 1472018, -1, 4021005, -6, 1472020, 1}, // 1472020 サファイアスレイン
        {-5000, 1082032, -1, 4011002, -1, 1082033, 1}, // 1082033 ミスリルスチルライン
        {-7000, 1082032, -1, 4021004, -1, 1082034, 1}, // 1082034 スチルライン(黒)
        {-10000, 1082037, -1, 4011002, -2, 1082038, 1}, // 1082038 ミスリルウッドシップ
        {-12000, 1082037, -1, 4021004, -2, 1082039, 1}, // 1082039 ウッドシップ(黒)
        {-15000, 1082042, -1, 4011004, -2, 1082043, 1}, // 1082043 シルビア(銀)
        {-20000, 1082042, -1, 4011006, -1, 1082044, 1}, // 1082044 シルビア(金)
        {-22000, 1082046, -1, 4011005, -3, 1082047, 1}, // 1082047 オリハルコンアルビヨン
        {-25000, 1082046, -1, 4011006, -2, 1082045, 1}, // 1082045 ゴールドアルビヨン
        {-45000, 1082075, -1, 4011006, -4, 1082076, 1}, // 1082076 クリフ(金)
        {-50000, 1082075, -1, 4021008, -2, 1082074, 1}, // 1082074 クリフ(黒)
        {-55000, 1082065, -1, 4021000, -5, 1082067, 1}, // 1082067 赤月
        {-60000, 1082065, -1, 4011006, -2, 4021008, -1, 1082066, 1}, // 1082066 黄月
        {-70000, 1082092, -1, 4011001, -7, 4000014, -200, 1082093, 1}, // 1082093 スチルパーウ
        {-80000, 1082092, -1, 4011006, -7, 4000027, -150, 1082094, 1}, // 1082094 ゴールドパーウ
        {0, 4000003, -10, 4003001, 1}, // 4003001 加工された木
        {0, 4000018, -5, 4003001, 1}, // 4003001 加工された木
        {0, 4011001, -1, 4011000, -1, 4003000, 15} // 4003000 ネジ
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
            case TYPE_CHAT_MESSAGE6 -> {
                String make_item = texts[0];
                String need_item = texts[1];
                int req_level = values[0];
                text_confirm = "" + make_item + "を作るには、以下の材料が必要だ。制限レベルは" + req_level + "だ。どうだい？作ってほしいかい？\r\n\r\n#b" + need_item;
                text_answer_no = "おや、そうなのか？材料が足りないんだな？どうせ町にいるんだろうから、材料を集めて持ってきてくれ…素晴らしいアイテムを作ってやるよ…";
                text_fail = "必要なものがすべてそろっているか、装備インベントリに空きがあるか、よく確認してくれ。";
                text_ok = "ほら！" + make_item + "だ。どうだい？かっこいいだろう？こんな逸品はめったにお目にかかれないぞ！";
            }
            case TYPE_CHAT_MESSAGE7 -> {
                String make_item = texts[0];
                String need_item = texts[1];
                text_confirm = "" + make_item + "を強化するには、以下の材料が必要だ。ああ、それと、すでに強化されたアイテムを使って新たに強化しないよう気をつけてくれ。どうだい？作ってほしいかい？\r\n\r\n#b" + need_item;
                text_answer_no = "おや、そうなのか？材料が足りないんだな？どうせ町にいるんだろうから、材料を集めて持ってきてくれ…素晴らしいアイテムを作ってやるよ…";
                text_fail = "必要なものがすべてそろっているか、装備インベントリに空きがあるか、よく確認してくれ..";
                text_ok = "ほら！" + make_item + "だ。どうだい？かっこいいだろう？こんな逸品はめったにお目にかかれないぞ！";
            }
            case TYPE_CHAT_MESSAGE8 -> {
                String make_item = texts[0];
                String need_item = texts[1];
                String item_option = texts[2];
                text_confirm = "" + make_item + "を強化するには、以下の材料が必要だ。このアイテムには#r" + item_option + "#kの強化も付く。すでに強化されたアイテムを新たな強化の材料に使わないよう気をつけてくれ。どうだい？やってみるか？\r\n\r\n#b" + need_item;
                text_answer_no = "おや、そうなのか？材料が足りないんだな？どうせ町にいるんだろうから、材料を集めて持ってきてくれ…素晴らしいアイテムを作ってやるよ…";
                text_fail = "必要なものがすべてそろっているか、装備インベントリに空きがあるか、よく確認してくれ..";
                text_ok = "ほら！" + make_item + "だ。どうだい？かっこいいだろう？こんな逸品はめったにお目にかかれないぞ！";
            }
            case TYPE_CHAT_MESSAGE9 -> {
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
                text_ask = "#b" + need_number + " " + need_item + "#kで、" + item_number + " " + make_item + "を作ってやれるよ。うれしいことに、これはタダで作ってやる。どうだい？いくつ作りたい？";
                text_confirm = "#b" + make_item + " " + ret_num + "回#k作るかい？それなら#r" + need_num + " " + need_item + "#kが必要だよ。";
                text_answer_no = "必要なアイテムが全部そろっていないようだな。どうせ町にいるんだろうから、材料を集めて持ってきてくれ…素晴らしいアイテムを作ってやるよ…";
                text_fail = "必要なものがすべてそろっているか、ETCインベントリに空きがあるか、よく確認してくれ..";
                text_ok = "ほら！" + all_num + " " + make_item + "だ。どうだい？かっこいいだろう？こんな逸品はめったにお目にかかれないぞ！";
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
