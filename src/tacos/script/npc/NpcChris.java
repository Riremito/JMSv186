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
// Npc.wz script : refine_kerning2 (JMS131-194), 会話文は lua_script_jp (refine_kerning2) を利用
public class NpcChris extends NpcScriptMan {

    // lua の関数
    private final int TYPE_CHAT_MESSAGE4 = 0; // chat_message4
    private final int TYPE_CHAT_MESSAGE5 = 1; // chat_message5
    private final int TYPE_INLINE0 = 2; // lua のスクリプト内の処理
    private final int RECIPE = 1000;
    private final int PHASE_START = 0;
    private final int PHASE_MENU = 1;
    private final int PHASE_MENU_ANSWER = 2;
    private final int PHASE_RECIPE = 3;
    private final int PHASE_NUMBER_ANSWER = 4;
    private final int PHASE_EXECUTE = 5;
    // 翻訳
    private final String text_intro = "よう！元気か？珍しい原石か宝石を持っていないか？少しの手数料で、武器や防具に使える材料に変えてやれるぜ。アイテムの修理の仕事をしながら精錬のやり方を覚えたんだ…どうだい？やらせてくれるか？";
    // 翻訳
    private final String text_intro_no = "わかったよ…でも、いつかきっと俺の助けが必要になるはずさ…その日が来たら、また話しかけてくれよな？";
    // メニュー
    // 翻訳
    private final String[] menu_texts = {
        "OK！原石と少しの手数料があれば、使えるように精錬してやるぜ。その前に、ETCインベントリに空きスロットがあるか確認してくれ。さて…何をしてほしい？\r\n#b#L0#鉱物の原石を精錬する#l\r\n#L1#宝石の原石を精錬する#l\r\n#L2##t4000039#を持っているんだけど…#l\r\n#L3#アームを強化したい…#l",
        "どの鉱物を作りたい？\r\n#b#L0##t4011000##l\r\n#L1##t4011001##l\r\n#L2##t4011002##l\r\n#L3##t4011003##l\r\n#L4##t4011004##l\r\n#L5##t4011005##l\r\n#L6##t4011006##l",
        "どの宝石を精錬したい？\r\n#b#L0##t4021000##l\r\n#L1##t4021001##l\r\n#L2##t4021002##l\r\n#L3##t4021003##l\r\n#L4##t4021004##l\r\n#L5##t4021005##l\r\n#L6##t4021006##l\r\n#L7##t4021007##l\r\n#L8##t4021008##l",
        "さて…どのアームを強化したい？\r\n#L0##b#t1472023##k(制限レベル: 60, 盗賊)#l\r\n#L1##b#t1472024##k(制限レベル: 60, 盗賊)#l\r\n#L2##b#t1472025##k(制限レベル: 60, 盗賊)#k#l"
    };
    // メニューの前の会話
    // 翻訳
    private final String[][] menu_says = {
        {},
        {},
        {},
        {
            "グローブを強化したいんだな？気をつけろよ！強化に使うアイテムはすべてなくなるし、すでに書で#r強化#kされたアイテムを使っても、強化したときにその効果はなくなってしまう。だから決める前によく考えた方がいいぞ…"
        }
    };
    // メニューの選択肢 (RECIPE 以上はレシピ番号)
    private final int[][] menu_children = {
        {1, 2, RECIPE + 16, 3},
        {RECIPE + 0, RECIPE + 1, RECIPE + 2, RECIPE + 3, RECIPE + 4, RECIPE + 5, RECIPE + 6},
        {RECIPE + 7, RECIPE + 8, RECIPE + 9, RECIPE + 10, RECIPE + 11, RECIPE + 12, RECIPE + 13, RECIPE + 14, RECIPE + 15},
        {RECIPE + 17, RECIPE + 18, RECIPE + 19}
    };
    // レシピ (lua の関数, index)
    private final int[][] recipes = {
        {TYPE_CHAT_MESSAGE5, 1},
        {TYPE_CHAT_MESSAGE5, 2},
        {TYPE_CHAT_MESSAGE5, 3},
        {TYPE_CHAT_MESSAGE5, 4},
        {TYPE_CHAT_MESSAGE5, 5},
        {TYPE_CHAT_MESSAGE5, 6},
        {TYPE_CHAT_MESSAGE5, 7},
        {TYPE_CHAT_MESSAGE5, 100},
        {TYPE_CHAT_MESSAGE5, 101},
        {TYPE_CHAT_MESSAGE5, 102},
        {TYPE_CHAT_MESSAGE5, 103},
        {TYPE_CHAT_MESSAGE5, 104},
        {TYPE_CHAT_MESSAGE5, 105},
        {TYPE_CHAT_MESSAGE5, 106},
        {TYPE_CHAT_MESSAGE5, 107},
        {TYPE_CHAT_MESSAGE5, 108},
        {TYPE_INLINE0, 0},
        {TYPE_CHAT_MESSAGE4, 1},
        {TYPE_CHAT_MESSAGE4, 2},
        {TYPE_CHAT_MESSAGE4, 3}
    };
    // レシピの文字列の引数
    // 翻訳
    private final String[][] recipe_texts = {
        {"#t4011000#", "#v4010000#", "#t4010000#s"},
        {"#t4011001#", "#v4010001#", "#t4010001#s"},
        {"#t4011002#", "#v4010002#", "#t4010002#s"},
        {"#t4011003#", "#v4010003#", "#t4010003#s"},
        {"#t4011004#", "#v4010004#", "#t4010004#s"},
        {"#t4011005#", "#v4010005#", "#t4010005#s"},
        {"#t4011006#", "#v4010006#", "#t4010006#s"},
        {"#t4021000#", "#v4020000#", "#t4020000#s"},
        {"#t4021001#", "#v4020001#", "#t4020001#s"},
        {"#t4021002#", "#v4020002#", "#t4020002#s"},
        {"#t4021003#", "#v4020003#", "#t4020003#s"},
        {"#t4021004#", "#v4020004#", "#t4020004#s"},
        {"#t4021005#", "#v4020005#", "#t4020005#s"},
        {"#t4021006#", "#v4020006#", "#t4020006#s"},
        {"#t4021007#", "#v4020007#", "#t4020007#s"},
        {"#t4021008#", "#v4020008#", "#t4020008#s"},
        {},
        {"#t1472023#", "#v1472022# #t1472022#\r\n#v4011007# #t4011007#\r\n#v4021000# #t4021000# 8個\r\n#v2012000# #t2012000# 10個\r\n#v4031138# 80,000メル", "DEX + 4, 回避率 + 3"},
        {"#t1472024#", "#v1472022# #t1472022#\r\n#v4011007# #t4011007#\r\n#v4021005# #t4021005# 8個\r\n#v2012002# #t2012002# 10個\r\n#v4031138# 80,000メル", "LUK + 4, 回避率 + 3"},
        {"#t1472025#", "#v1472022# #t1472022#\r\n#v4011007# #t4011007#\r\n#v4021008# #t4021008# 3個\r\n#v4000046# #t4000046# 5個\r\n#v4031138# 100,000メル", "LUK + 5, 回避率 + 4"}
    };
    // レシピの数値の引数
    private final int[][] recipe_values = {
        {250},
        {250},
        {250},
        {450},
        {450},
        {450},
        {750},
        {450},
        {450},
        {450},
        {450},
        {450},
        {450},
        {450},
        {950},
        {2900},
        {},
        {60, 30},
        {60, 30},
        {60, 30}
    };
    // 所持金の増減, アイテムIDと増減数の組 (個数を指定する場合は1個あたり)
    private final int[][] recipe_exchanges = {
        {-250, 4010000, -10, 4011000, 1}, // 4011000 青銅
        {-250, 4010001, -10, 4011001, 1}, // 4011001 鋼鉄
        {-250, 4010002, -10, 4011002, 1}, // 4011002 ミスリル
        {-450, 4010003, -10, 4011003, 1}, // 4011003 アダマンティウム
        {-450, 4010004, -10, 4011004, 1}, // 4011004 銀
        {-450, 4010005, -10, 4011005, 1}, // 4011005 オリハルコン
        {-750, 4010006, -10, 4011006, 1}, // 4011006 金
        {-450, 4020000, -10, 4021000, 1}, // 4021000 ガーネット
        {-450, 4020001, -10, 4021001, 1}, // 4021001 紫水晶
        {-450, 4020002, -10, 4021002, 1}, // 4021002 アクアマリン
        {-450, 4020003, -10, 4021003, 1}, // 4021003 エメラルド
        {-450, 4020004, -10, 4021004, 1}, // 4021004 オパール
        {-450, 4020005, -10, 4021005, 1}, // 4021005 サファイア
        {-450, 4020006, -10, 4021006, 1}, // 4021006 トパーズ
        {-950, 4020007, -10, 4021007, 1}, // 4021007 ダイヤモンド
        {-2900, 4020008, -10, 4021008, 1}, // 4021008 黒水晶
        {-1000, 4000039, -100, 4011001, 1}, // 4011001 鋼鉄
        {-80000, 1472022, -1, 4011007, -1, 4021000, -8, 2012000, -10, 1472023, 1}, // 1472023 ブラッドギガンティス
        {-80000, 1472022, -1, 4011007, -1, 4021005, -8, 2012002, -10, 1472024, 1}, // 1472024 サファイアギガンティス
        {-100000, 1472022, -1, 4011007, -1, 4021008, -3, 4000046, -5, 1472025, 1} // 1472025 ダークギガンティス
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
            case TYPE_CHAT_MESSAGE4 -> {
                String make_item = texts[0];
                String need_item = texts[1];
                int req_level = values[0];
                String item_option = texts[2];
                int pad = values[1];
                text_confirm = "" + make_item + "を強化したいのかい？強化するには以下の材料が必要だ。このアイテムには#r" + item_option + "#kが付き、制限レベルは" + req_level + "、攻撃力は#r" + pad + "#kになる。強化済みのアイテムを強化の材料に使わないよう気をつけてくれ。やってみるかい？\r\n\r\n#b" + need_item;
                text_answer_no = "ほかの鉱物や宝石も精錬できるから、考えてみてくれ、いいね？";
                text_fail = "必要なアイテムがすべてそろっているか、ETCインベントリがいっぱいでないか確認してくれ。";
                text_ok = "ほら、" + make_item + "だ。どうだい？いい出来だろう？ははは…強化の技を細かく学んで過ごした日々が、ついに報われたよ。また来てくれ！";
            }
            case TYPE_CHAT_MESSAGE5 -> {
                String make_item = texts[0];
                String need_item_icon = texts[1];
                String need_item_string = texts[2];
                int unit_price = values[0];
                int ret_num = this.number;
                int price = unit_price * ret_num;
                int all_num = ret_num * 10;
                is_number = true;
                number_default = 0;
                number_min = 0;
                number_max = 100;
                text_ask = "" + make_item + "を作りたいのかい？作るには以下の材料が必要だ。いくつ作りたい？\r\n\r\n#b" + need_item_icon + " 10 " + need_item_string + "\r\n" + unit_price + " mesos#k";
                text_confirm = "#b" + ret_num + " " + make_item + "#kを作るには、以下のアイテムが必要だ。本当に作るかい？\r\n\r\n#b" + need_item_icon + " " + all_num + " " + need_item_string + "\r\n" + price + " mesos#k";
                text_answer_no = "ほかの鉱物や宝石も精錬できるから、考えてみてくれ、いいね？";
                text_fail = "" + need_item_icon + need_item_string + "を持っているか、ETCインベントリがいっぱいでないか確認してね。";
                text_ok = "はい、" + ret_num + " " + make_item + "だ。どうだい？見事な精錬だろう？ははは…精錬の技術を学んで過ごした日々が、ついに報われたよ。また来てくれ！";
            }
            case TYPE_INLINE0 -> {
                text_confirm = "#t4000039#を持っているのか？ふむ…それがあれば#t4011001#を作れるかもしれない。#b#b#t4000039#100個#kと#b1,000メル#kをくれたら、#b精錬された#t4011001##kを作ってやれる。どうだ？やってみるか？";
                text_answer_no = "これ以外のほかの鉱物や宝石も精錬できるから、考えてみてくれ…";
                text_fail = "お金が足りないのかもしれないな…#t4000039#100個と1,000メルを持っているか、ETCインベントリに空きスロットがあるか確認してくれ。";
                text_ok = "さあ…#b#t4011001##kだ。どうだい？いい出来だろう？ははは…精錬の技術を一生懸命勉強しておいてよかったよ…いつでもまた来てくれ～！";
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
