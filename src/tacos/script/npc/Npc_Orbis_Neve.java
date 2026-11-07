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
// Npc.wz script : make_orbis (JMS131-194), 会話文は lua_script_jp (make_orbis) を利用
public class Npc_Orbis_Neve extends NpcScriptMan {

    // lua の関数
    private final int TYPE_CHAT_MESSAGE5 = 0; // chat_message5
    private final int RECIPE = 1000;
    private final int PHASE_START = 0;
    private final int PHASE_MENU = 1;
    private final int PHASE_MENU_ANSWER = 2;
    private final int PHASE_RECIPE = 3;
    private final int PHASE_NUMBER_ANSWER = 4;
    private final int PHASE_EXECUTE = 5;
    // メニュー
    // 翻訳
    private final String[] menu_texts = {
        "最近の人たちは、グローブをあまり大切なものだと思っていないみたい…それは、いいグローブの本当の価値をわかっていないからよ。どう？必要なら、私に任せてみない？\r\n#b#L0# 戦士のグローブを作る#l\r\n#L1# 魔法使いのグローブを作る#l\r\n#L2# 弓使いのグローブを作る#l\r\n#L3# 盗賊のグローブを作る#l\r\n#L4# 戦士のグローブを強化する#l\r\n#L5# 魔法使いのグローブを強化する#l\r\n#L6# 弓使いのグローブを強化する#l\r\n#L7# 盗賊のグローブを強化する#l",
        "戦士専用のグローブがほしいのね？必要なものをここから選んで！\r\n#b#L0##b #t1082059##k(制限レベル: 60, 戦士)#l\r\n#b#L1##b #t1082103##k(制限レベル: 70, 戦士)#l\r\n#b#L2##b #t1082114##k(制限レベル: 80, 戦士)#l",
        "魔法使い専用のグローブがほしいのね？必要なものをここから選んで！\r\n#b#L0##b #t1082086##k(制限レベル: 60, 魔法使い)#l\r\n#b#L1##b #t1082098##k(制限レベル: 70, 魔法使い)#l\r\n#b#L2##b #t1082121##k(制限レベル: 80, 魔法使い)#l",
        "弓使い専用のグローブがほしいのね？必要なものをここから選んで！\r\n#b#L0##b #t1082089##k(制限レベル: 60, 弓使い)#l\r\n#b#L1##b #t1082106##k(制限レベル: 70, 弓使い)#l\r\n#b#L2##b #t1082109##k(制限レベル: 80, 弓使い)#l",
        "盗賊専用のグローブがほしいのね？必要なものをここから選んで！\r\n#b#L0##b #t1082092##k(制限レベル: 60, 盗賊)#l\r\n#b#L1##b #t1082095##k(制限レベル: 70, 盗賊)#l\r\n#b#L2##b #t1082118##k(制限レベル: 80, 盗賊)#l",
        "戦士のグローブを強化したいのね？強化したいものを選んで…\r\n#b#L0##b #t1082060##k(制限レベル: 60, 戦士)#l\r\n#b#L1##b #t1082061##k(制限レベル: 60, 戦士)#l\r\n#b#L2##b #t1082104##k(制限レベル: 70, 戦士)#l\r\n#b#L3##b #t1082105##k(制限レベル: 70, 戦士)#l\r\n#b#L4##b #t1082115##k(制限レベル: 80, 戦士)#l\r\n#b#L5##b #t1082116##k(制限レベル: 80, 戦士)#l\r\n#b#L6##b #t1082117##k(制限レベル: 80, 戦士)#l",
        "魔法使いのグローブを強化したいのね？強化したいものを選んで…\r\n#b#L0##b #t1082087##k(制限レベル: 60, 魔法使い)#l\r\n#b#L1##b #t1082088##k(制限レベル: 60, 魔法使い)#l\r\n#b#L2##b #t1082099##k(制限レベル: 70, 魔法使い)#l\r\n#b#L3##b #t1082100##k(制限レベル: 70, 魔法使い)#l\r\n#b#L4##b #t1082122##k(制限レベル: 80, 魔法使い)#l\r\n#b#L5##b #t1082123##k(制限レベル: 80, 魔法使い)#l",
        "弓使いのグローブを強化したいのね？強化したいものを選んで…\r\n#b#L0##b #t1082090##k(制限レベル: 60, 弓使い)#l\r\n#b#L1##b #t1082091##k(制限レベル: 60, 弓使い)#l\r\n#b#L2##b #t1082107##k(制限レベル: 70, 弓使い)#l\r\n#b#L3##b #t1082108##k(制限レベル: 70, 弓使い)#l\r\n#b#L4##b #t1082110##k(制限レベル: 80, 弓使い)#l\r\n#b#L5##b #t1082111##k(制限レベル: 80, 弓使い)#l\r\n#b#L6##b #t1082112##k(制限レベル: 80, 弓使い)#l",
        "盗賊のグローブを強化したいのね？強化したいものを選んで…\r\n#b#L0##b #t1082093##k(制限レベル: 60, 盗賊)#l\r\n#b#L1##b #t1082094##k(制限レベル: 60, 盗賊)#l\r\n#b#L2##b #t1082096##k(制限レベル: 70, 盗賊)#l\r\n#b#L3##b #t1082097##k(制限レベル: 70, 盗賊)#l\r\n#b#L4##b #t1082119##k(制限レベル: 80, 盗賊)#l\r\n#b#L5##b #t1082120##k(制限レベル: 80, 盗賊)#l"
    };
    // メニューの前の会話
    // 翻訳
    private final String[][] menu_says = {
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {}
    };
    // メニューの選択肢 (RECIPE 以上はレシピ番号)
    private final int[][] menu_children = {
        {1, 2, 3, 4, 5, 6, 7, 8},
        {RECIPE + 0, RECIPE + 1, RECIPE + 2},
        {RECIPE + 3, RECIPE + 4, RECIPE + 5},
        {RECIPE + 6, RECIPE + 7, RECIPE + 8},
        {RECIPE + 9, RECIPE + 10, RECIPE + 11},
        {RECIPE + 12, RECIPE + 13, RECIPE + 14, RECIPE + 15, RECIPE + 16, RECIPE + 17, RECIPE + 18},
        {RECIPE + 19, RECIPE + 20, RECIPE + 21, RECIPE + 22, RECIPE + 23, RECIPE + 24},
        {RECIPE + 25, RECIPE + 26, RECIPE + 27, RECIPE + 28, RECIPE + 29, RECIPE + 30, RECIPE + 31},
        {RECIPE + 32, RECIPE + 33, RECIPE + 34, RECIPE + 35, RECIPE + 36, RECIPE + 37}
    };
    // レシピ (lua の関数, index)
    private final int[][] recipes = {
        {TYPE_CHAT_MESSAGE5, 0},
        {TYPE_CHAT_MESSAGE5, 1},
        {TYPE_CHAT_MESSAGE5, 2},
        {TYPE_CHAT_MESSAGE5, 100},
        {TYPE_CHAT_MESSAGE5, 101},
        {TYPE_CHAT_MESSAGE5, 102},
        {TYPE_CHAT_MESSAGE5, 200},
        {TYPE_CHAT_MESSAGE5, 201},
        {TYPE_CHAT_MESSAGE5, 202},
        {TYPE_CHAT_MESSAGE5, 300},
        {TYPE_CHAT_MESSAGE5, 301},
        {TYPE_CHAT_MESSAGE5, 302},
        {TYPE_CHAT_MESSAGE5, 400},
        {TYPE_CHAT_MESSAGE5, 401},
        {TYPE_CHAT_MESSAGE5, 402},
        {TYPE_CHAT_MESSAGE5, 403},
        {TYPE_CHAT_MESSAGE5, 404},
        {TYPE_CHAT_MESSAGE5, 405},
        {TYPE_CHAT_MESSAGE5, 406},
        {TYPE_CHAT_MESSAGE5, 500},
        {TYPE_CHAT_MESSAGE5, 501},
        {TYPE_CHAT_MESSAGE5, 502},
        {TYPE_CHAT_MESSAGE5, 503},
        {TYPE_CHAT_MESSAGE5, 504},
        {TYPE_CHAT_MESSAGE5, 505},
        {TYPE_CHAT_MESSAGE5, 600},
        {TYPE_CHAT_MESSAGE5, 601},
        {TYPE_CHAT_MESSAGE5, 602},
        {TYPE_CHAT_MESSAGE5, 603},
        {TYPE_CHAT_MESSAGE5, 604},
        {TYPE_CHAT_MESSAGE5, 605},
        {TYPE_CHAT_MESSAGE5, 606},
        {TYPE_CHAT_MESSAGE5, 700},
        {TYPE_CHAT_MESSAGE5, 701},
        {TYPE_CHAT_MESSAGE5, 702},
        {TYPE_CHAT_MESSAGE5, 703},
        {TYPE_CHAT_MESSAGE5, 704},
        {TYPE_CHAT_MESSAGE5, 705}
    };
    // レシピの文字列の引数
    // 翻訳
    private final String[][] recipe_texts = {
        {"#t1082059#", "#v4011007# #t4011007# \r\n#v4011000# #t4011000# 8個 \r\n#v4011006# #t4011006# 2個 \r\n#v4000030# #t4000030# 50個 \r\n#v4003000# #t4003000# 50個 \r\n#v4031138# 70,000メル", "STR +1"},
        {"#t1082103#", "#v4005000# #t4005000# 2個 \r\n#v4011000# #t4011000# 8個 \r\n#v4011006# #t4011006# 3個 \r\n#v4000030# #t4000030# 70個 \r\n#v4003000# #t4003000# 55個 \r\n#v4031138# 90,000メル", "STR +2"},
        {"#t1082114#", "#v4005000# #t4005000# 2個 \r\n#v4005002# #t4005002# \r\n#v4021005# #t4021005# 8個 \r\n#v4000030# #t4000030# 90個 \r\n#v4003000# #t4003000# 60個 \r\n#v4031138# 100,000メル", "STR +2, DEX +1, MP +10"},
        {"#t1082086#", "#v4011007# #t4011007# \r\n#v4011001# #t4011001# 8個 \r\n#v4021007# #t4021007# \r\n#v4000030# #t4000030# 50個 \r\n#v4003000# #t4003000# 50個 \r\n#v4031138# 70,000メル", "INT + 1, LUK + 1, MP + 30"},
        {"#t1082098#", "#v4005001# #t4005001# 2個 \r\n#v4011000# #t4011000# 6個 \r\n#v4011004# #t4011004# 6個 \r\n#v4000030# #t4000030# 70個 \r\n#v4003000# #t4003000# 55個 \r\n#v4031138# 90,000メル", "INT +2"},
        {"#t1082121#", "#v4005001# #t4005001# 2個 \r\n#v4005003# #t4005003# \r\n#v4021003# #t4021003# 8個 \r\n#v4000030# #t4000030# 90個 \r\n#v4003000# #t4003000# 60個 \r\n#v4031138# 100,000メル", "INT +2, LUK +1, MP +10"},
        {"#t1082089#", "#v4011007# #t4011007# \r\n#v4021006# #t4021006# 8個 \r\n#v4011006# #t4011006# 2個 \r\n#v4000030# #t4000030# 50個 \r\n#v4003000# #t4003000# 50個 \r\n#v4031138# 70,000メル", "DEX + 1, STR + 1"},
        {"#t1082106#", "#v4005002# #t4005002# 2個 \r\n#v4021005# #t4021005# 8個 \r\n#v4011004# #t4011004# 3個 \r\n#v4000030# #t4000030# 70個 \r\n#v4003000# #t4003000# 55個 \r\n#v4031138# 90,000メル", "DEX +2"},
        {"#t1082109#", "#v4005002# #t4005002# 2個 \r\n#v4005000# #t4005000# \r\n#v4021000# #t4021000# 8個 \r\n#v4000030# #t4000030# 90個 \r\n#v4003000# #t4003000# 60個 \r\n#v4031138# 100,000メル", "DEX +2, STR +1, MP +10"},
        {"#t1082092#", "#v4011007# #t4011007# \r\n#v4011000# #t4011000# 8個 \r\n#v4021007# #t4021007# \r\n#v4000030# #t4000030# 50個 \r\n#v4003000# #t4003000# 50個 \r\n#v4031138# 70,000メル", "LUK +1, DEX +1"},
        {"#t1082095#", "#v4005003# #t4005003# 2個 \r\n#v4011000# #t4011000# 6個 \r\n#v4011003# #t4011003# 6個 \r\n#v4000030# #t4000030# 70個 \r\n#v4003000# #t4003000# 55個 \r\n#v4031138# 90,000メル", "LUK +2"},
        {"#t1082118#", "#v4005003# #t4005003# 2個 \r\n#v4005002# #t4005002# \r\n#v4011002# #t4011002# 8個 \r\n#v4000030# #t4000030# 90個 \r\n#v4003000# #t4003000# 60個 \r\n#v4031138# 100,000メル", "LUK +2, DEX +1, MP +10"},
        {"#t1082060#", "#v1082059# #t1082059# \r\n#v4011002# #t4011002# 3個 \r\n#v4021005# #t4021005# 5個 \r\n#v4031138# 70,000メル", "STR + 2, 命中率 + 1"},
        {"#t1082061#", "#v1082059# #t1082059# \r\n#v4021007# #t4021007# 2個 \r\n#v4021008# #t4021008# 2個 \r\n#v4031138# 80,000メル", "STR + 3, 命中率 + 2"},
        {"#t1082104#", "#v1082103# #t1082103# \r\n#v4011002# #t4011002# 6個 \r\n#v4021006# #t4021006# 4個 \r\n#v4031138# 90,000メル", "STR + 3, 命中率 + 1"},
        {"#t1082105#", "#v1082103# #t1082103# \r\n#v4021006# #t4021006# 8個 \r\n#v4021008# #t4021008# 3個 \r\n#v4031138# 100,000メル", "STR + 4, 命中率 + 1"},
        {"#t1082115#", "#v1082114# #t1082114# \r\n#v4005000# #t4005000# \r\n#v4005002# #t4005002# \r\n#v4021003# #t4021003# 7個 \r\n#v4031138# 110,000メル", "STR +3, DEX +2, MP +15"},
        {"#t1082116#", "#v1082114# #t1082114# \r\n#v4005002# #t4005002# 3個 \r\n#v4021000# #t4021000# 8個 \r\n#v4031138# 120,000メル", "DEX +4, STR +2, MP +20"},
        {"#t1082117#", "#v1082114# #t1082114# \r\n#v4005000# #t4005000# 2個 \r\n#v4005002# #t4005002# \r\n#v4021008# #t4021008# 4個 \r\n#v4031138# 120,000メル", "STR +4, DEX +2, MP +20"},
        {"#t1082087#", "#v1082086# #t1082086# \r\n#v4011004# #t4011004# 3個 \r\n#v4011006# #t4011006# 5個 \r\n#v4031138# 70,000メル", "INT +2, LUK +1, MP +30"},
        {"#t1082088#", "#v1082086# #t1082086# \r\n#v4021008# #t4021008# 2個 \r\n#v4011006# #t4011006# 3個 \r\n#v4031138# 80,000メル", "INT + 3, LUK + 1, MP + 30"},
        {"#t1082099#", "#v1082098# #t1082098# \r\n#v4021002# #t4021002# 6個 \r\n#v4021007# #t4021007# 2個 \r\n#v4031138# 90,000メル", "INT +3, LUK +1"},
        {"#t1082100#", "#v1082098# #t1082098# \r\n#v4021008# #t4021008# 3個 \r\n#v4011006# #t4011006# 3個 \r\n#v4031138# 100,000メル", "INT +4, LUK +1"},
        {"#t1082122#", "#v1082121# #t1082121# \r\n#v4005001# #t4005001# \r\n#v4005003# #t4005003# \r\n#v4021005# #t4021005# 7個 \r\n#v4031138# 110,000メル", "INT + 3, LUK + 2, MP + 15"},
        {"#t1082123#", "#v1082121# #t1082121# \r\n#v4005001# #t4005001# 2個 \r\n#v4005003# #t4005003# \r\n#v4021008# #t4021008# 4個 \r\n#v4031138# 120,000メル", "INT + 4, LUK + 2, MP + 30"},
        {"#t1082090#", "#v1082089# #t1082089# \r\n#v4021007# #t4021007# \r\n#v4021000# #t4021000# 5個 \r\n#v4031138# 70,000メル", "DEX +2, STR +1, MP +15"},
        {"#t1082091#", "#v1082089# #t1082089# \r\n#v4021007# #t4021007# 2個 \r\n#v4021008# #t4021008# 2個 \r\n#v4031138# 80,000メル", "DEX +3, STR +1, MP +30"},
        {"#t1082107#", "#v1082106# #t1082106# \r\n#v4021006# #t4021006# 5個 \r\n#v4011006# #t4011006# 3個 \r\n#v4031138# 90,000メル", "DEX + 3, 回避率 + 1"},
        {"#t1082108#", "#v1082106# #t1082106# \r\n#v4021007# #t4021007# 2個 \r\n#v4021008# #t4021008# 3個 \r\n#v4031138# 100,000メル", "DEX + 4, 回避率 + 1"},
        {"#t1082110#", "#v1082109# #t1082109# \r\n#v4005002# #t4005002# \r\n#v4005000# #t4005000# \r\n#v4021005# #t4021005# 7個 \r\n#v4031138# 110,000メル", "DEX +3, STR +2, MP +15"},
        {"#t1082111#", "#v1082109# #t1082109# \r\n#v4005002# #t4005002# \r\n#v4005000# #t4005000# \r\n#v4021003# #t4021003# 7個 \r\n#v4031138# 110,000メル", "DEX +3, STR +2, MP +15"},
        {"#t1082112#", "#v1082109# #t1082109# \r\n#v4005002# #t4005002# 2個 \r\n#v4005000# #t4005000# \r\n#v4021008# #t4021008# 4個 \r\n#v4031138# 120,000メル", "DEX +4, STR +2, MP +30"},
        {"#t1082093#", "#v1082092# #t1082092# \r\n#v4011001# #t4011001# \r\n#v4000014# #t4000014# 200個 \r\n#v4031138# 70,000メル", "LUK +2, DEX +1, MP +15"},
        {"#t1082094#", "#v1082092# #t1082092# \r\n#v4011006# #t4011006# 7個 \r\n#v4000027# #t4000027# 150個 \r\n#v4031138# 80,000メル", "LUK +3, DEX +1, MP +30"},
        {"#t1082096#", "#v1082095# #t1082095# \r\n#v4011004# #t4011004# 6個 \r\n#v4021007# #t4021007# 2個 \r\n#v4031138# 90,000メル", "LUK + 3, 回避率 + 1"},
        {"#t1082097#", "#v1082095# #t1082095# \r\n#v4021007# #t4021007# 3個 \r\n#v4011006# #t4011006# 3個 \r\n#v4031138# 100,000メル", "LUK + 4, 回避率 + 1"},
        {"#t1082119#", "#v1082118# #t1082118# \r\n#v4005003# #t4005003# \r\n#v4005002# #t4005002# \r\n#v4021001# #t4021001# 7個 \r\n#v4031138# 110,000メル", "LUK +3, DEX +2, MP +15"},
        {"#t1082120#", "#v1082118# #t1082118# \r\n#v4005003# #t4005003# 2個 \r\n#v4005002# #t4005002# \r\n#v4021000# #t4021000# 8個 \r\n#v4031138# 120,000メル", "LUK +4, DEX +2, MP +30"}
    };
    // レシピの数値の引数
    private final int[][] recipe_values = {
        {60},
        {70},
        {80},
        {60},
        {70},
        {80},
        {60},
        {70},
        {80},
        {60},
        {70},
        {80},
        {60},
        {60},
        {70},
        {70},
        {80},
        {80},
        {80},
        {60},
        {60},
        {70},
        {70},
        {80},
        {80},
        {60},
        {60},
        {70},
        {70},
        {80},
        {80},
        {80},
        {60},
        {60},
        {70},
        {70},
        {80},
        {80}
    };
    // 所持金の増減, アイテムIDと増減数の組 (個数を指定する場合は1個あたり)
    private final int[][] recipe_exchanges = {
        {-70000, 4011007, -1, 4011000, -8, 4011006, -2, 4000030, -50, 4003000, -50, 1082059, 1}, // 1082059 ブロンズクランチ
        {-90000, 4005000, -2, 4011000, -8, 4011006, -3, 4000030, -70, 4003000, -55, 1082103, 1}, // 1082103 ブロンズハスク
        {-100000, 4005000, -2, 4005002, -1, 4021005, -8, 4000030, -90, 4003000, -60, 1082114, 1}, // 1082114 エムパラ（青)
        {-70000, 4011007, -1, 4011001, -8, 4021007, -1, 4000030, -50, 4003000, -50, 1082086, 1}, // 1082086 鋼マヌット
        {-90000, 4005001, -2, 4011000, -6, 4011004, -6, 4000030, -70, 4003000, -55, 1082098, 1}, // 1082098 ローリン(茶)
        {-100000, 4005001, -2, 4005003, -1, 4021003, -8, 4000030, -90, 4003000, -60, 1082121, 1}, // 1082121 ワイズ（緑)
        {-70000, 4011007, -1, 4021006, -8, 4011006, -2, 4000030, -50, 4003000, -50, 1082089, 1}, // 1082089 オーカガーナ
        {-90000, 4005002, -2, 4021005, -8, 4011004, -3, 4000030, -70, 4003000, -55, 1082106, 1}, // 1082106 アイズ(青)
        {-100000, 4005002, -2, 4005000, -1, 4021000, -8, 4000030, -90, 4003000, -60, 1082109, 1}, // 1082109 コードン(赤)
        {-70000, 4011007, -1, 4011000, -8, 4021007, -1, 4000030, -50, 4003000, -50, 1082092, 1}, // 1082092 ブロンズパーウ
        {-90000, 4005003, -2, 4011000, -6, 4011003, -6, 4000030, -70, 4003000, -55, 1082095, 1}, // 1082095 ワイアース(銅)
        {-100000, 4005003, -2, 4005002, -1, 4011002, -8, 4000030, -90, 4003000, -60, 1082118, 1}, // 1082118 ローバ（緑)
        {-70000, 1082059, -1, 4011002, -3, 4021005, -5, 1082060, 1}, // 1082060 サファイアクランチ
        {-80000, 1082059, -1, 4021007, -2, 4021008, -2, 1082061, 1}, // 1082061 クランチ(黒)
        {-90000, 1082103, -1, 4011002, -6, 4021006, -4, 1082104, 1}, // 1082104 ミスリルハスク
        {-100000, 1082103, -1, 4021006, -8, 4021008, -3, 1082105, 1}, // 1082105 ダークハスク
        {-110000, 1082114, -1, 4005000, -1, 4005002, -1, 4021003, -7, 1082115, 1}, // 1082115 エムパラ（緑)
        {-120000, 1082114, -1, 4005002, -3, 4021000, -8, 1082116, 1}, // 1082116 エムパラ（赤)
        {-120000, 1082114, -1, 4005000, -2, 4005002, -1, 4021008, -4, 1082117, 1}, // 1082117 エムパラ（闇)
        {-70000, 1082086, -1, 4011004, -3, 4011006, -5, 1082087, 1}, // 1082087 マヌット(金)
        {-80000, 1082086, -1, 4021008, -2, 4011006, -3, 1082088, 1}, // 1082088 マヌット(黒)
        {-90000, 1082098, -1, 4021002, -6, 4021007, -2, 1082099, 1}, // 1082099 ローリン(青)
        {-100000, 1082098, -1, 4021008, -3, 4011006, -3, 1082100, 1}, // 1082100 ローリン(黒)
        {-110000, 1082121, -1, 4005001, -1, 4005003, -1, 4021005, -7, 1082122, 1}, // 1082122 ワイズ（青)
        {-120000, 1082121, -1, 4005001, -2, 4005003, -1, 4021008, -4, 1082123, 1}, // 1082123 ワイズ（闇)
        {-70000, 1082089, -1, 4021007, -1, 4021000, -5, 1082090, 1}, // 1082090 セピアガーナ
        {-80000, 1082089, -1, 4021007, -2, 4021008, -2, 1082091, 1}, // 1082091 ダークガーナ
        {-90000, 1082106, -1, 4021006, -5, 4011006, -3, 1082107, 1}, // 1082107 アイズ(金)
        {-100000, 1082106, -1, 4021007, -2, 4021008, -3, 1082108, 1}, // 1082108 アイズ(闇)
        {-110000, 1082109, -1, 4005002, -1, 4005000, -1, 4021005, -7, 1082110, 1}, // 1082110 コードン(青)
        {-110000, 1082109, -1, 4005002, -1, 4005000, -1, 4021003, -7, 1082111, 1}, // 1082111 コードン(緑)
        {-120000, 1082109, -1, 4005002, -2, 4005000, -1, 4021008, -4, 1082112, 1}, // 1082112 コードン(闇)
        {-70000, 1082092, -1, 4011001, -7, 4000014, -200, 1082093, 1}, // 1082093 スチルパーウ
        {-80000, 1082092, -1, 4011006, -7, 4000027, -150, 1082094, 1}, // 1082094 ゴールドパーウ
        {-90000, 1082095, -1, 4011004, -6, 4021007, -2, 1082096, 1}, // 1082096 ワイアース(銀)
        {-100000, 1082095, -1, 4021007, -3, 4011006, -3, 1082097, 1}, // 1082097 ワイアース(金)
        {-110000, 1082118, -1, 4005003, -1, 4005002, -1, 4021001, -7, 1082119, 1}, // 1082119 ローバ（紫)
        {-120000, 1082118, -1, 4005003, -2, 4005002, -1, 4021000, -8, 1082120, 1} // 1082120 ローバ（赤)
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
            case TYPE_CHAT_MESSAGE5 -> {
                String make_item = texts[0];
                String need_item = texts[1];
                int req_level = values[0];
                String item_option = texts[2];
                text_confirm = "" + make_item + "を作りたいの？作るには、以下の材料が必要よ。このアイテムには#r" + item_option + "#kの強化も付き、制限レベルは" + req_level + "。どう？作る？？\r\n\r\n#b" + need_item;
                text_answer_no = "グローブを作るだけじゃなく、グローブの強化もできるから、よく考えたらまた話しかけてね…";
                text_fail = "必要なアイテムがすべてそろっているか、装備インベントリに十分な空きがあるか確認してね。";
                text_ok = "はい…ご注文の" + make_item + "よ。どう？こんなに丁寧に作られた品物は、どこを探しても見つからないわよ！いつでも気軽にまた来てね…";
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
