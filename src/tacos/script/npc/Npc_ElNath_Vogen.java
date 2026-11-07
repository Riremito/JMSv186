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
// Npc.wz script : refine_elnath (JMS131-194), 会話文は lua_script_jp (refine_elnath) を利用
public class Npc_ElNath_Vogen extends NpcScriptMan {

    // lua の関数
    private final int TYPE_CHAT_MESSAGE1 = 0; // chat_message1
    private final int TYPE_CHAT_MESSAGE3 = 1; // chat_message3
    private final int TYPE_CHAT_MESSAGE4 = 2; // chat_message4
    private final int TYPE_CHAT_MESSAGE6 = 3; // chat_message6
    private final int RECIPE = 1000;
    private final int PHASE_START = 0;
    private final int PHASE_MENU = 1;
    private final int PHASE_MENU_ANSWER = 2;
    private final int PHASE_RECIPE = 3;
    private final int PHASE_NUMBER_ANSWER = 4;
    private final int PHASE_EXECUTE = 5;
    // 翻訳
    private final String text_intro = "鉱石や宝石を少し持っているようだな。少しの手数料で、盾や武器を作るのに必要な材料に精錬してやれるぞ。50年もやっているから朝飯前さ！どうだい？やってほしいか？";
    // 翻訳
    private final String text_intro_no = "なるほど。手数料が高すぎるかな？でも、俺は長いことこの町にいるから、何か精錬したくなったらいつでも持ってきてくれ。";
    // メニュー
    // 翻訳
    private final String[] menu_texts = {
        "いい判断だ！原石と手数料をくれれば、役に立つものに精錬してやろう。その前に、新しいアイテムを入れる空きがETCインベントリに十分あるか確認しておけよ。さて、何をしてほしい？\r\n#b#L0#鉱物の原石を精錬する#l\r\n#L1#宝石の原石を精錬する#l\r\n#L2#希少な宝石を精錬する#l\r\n#L3#水晶を精錬する#l\r\n#L4#材料を作る#l\r\n#L5#矢を作る#l",
        "どの鉱物を作りたい？\r\n#b#L0##t4011000##l\r\n#L1##t4011001##l\r\n#L2##t4011002##l\r\n#L3##t4011003##l\r\n#L4##t4011004##l\r\n#L5##t4011005##l\r\n#L6##t4011006##l",
        "どの宝石を精錬したい？\r\n#b#L0##t4021000##l\r\n#L1##t4021001##l\r\n#L2##t4021002##l\r\n#L3##t4021003##l\r\n#L4##t4021004##l\r\n#L5##t4021005##l\r\n#L6##t4021006##l\r\n#L7##t4021007##l\r\n#L8##t4021008##l",
        "ああ、希少な宝石だって精錬できるぞ。材料はたくさん必要かもしれんが、できなくはない。どの宝石を精錬したい？\r\n#b#L0##t4011007##l\r\n#L1##t4021009##l#k",
        "ふむ…本当に水晶を持っているのか？もう長いこと見ていないから信じられんが、持っているなら役に立つものに精錬してやれる。さて、どの水晶を精錬したい？\r\n#b#L0# #t4005000##l\r\n#L1# #t4005001##l\r\n#L2# #t4005002##l\r\n#L3# #t4005003##l\r\n#L4# #t4005004##l#k",
        "材料を作りたいんだな！どれどれ、どんな材料を作りたい？\r\n#L0##b#t4000003#で#t4003001#を作る#k#l\r\n#L1##b#t4000018#で#t4003001#を作る#k#l\r\n#L2##b#t4003000#を作る#k#l",
        "矢を作りたいんだな！いい矢ほど、戦いで有利になるぞ。どれどれ、どんな矢を作ってほしい？\r\n#L0##b#t2060000##k#l\r\n#L1##b#t2061000##k#l\r\n#L2##b#t2060001##k#l\r\n#L3##b#t2061001##k#l\r\n#L4##b#t2060002##k#l\r\n#L5##b#t2061002##k#l"
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
        {}
    };
    // メニューの選択肢 (RECIPE 以上はレシピ番号)
    private final int[][] menu_children = {
        {1, 2, 3, 4, 5, 6},
        {RECIPE + 0, RECIPE + 1, RECIPE + 2, RECIPE + 3, RECIPE + 4, RECIPE + 5, RECIPE + 6},
        {RECIPE + 7, RECIPE + 8, RECIPE + 9, RECIPE + 10, RECIPE + 11, RECIPE + 12, RECIPE + 13, RECIPE + 14, RECIPE + 15},
        {RECIPE + 16, RECIPE + 17},
        {RECIPE + 18, RECIPE + 19, RECIPE + 20, RECIPE + 21, RECIPE + 22},
        {RECIPE + 23, RECIPE + 24, RECIPE + 25},
        {RECIPE + 26, RECIPE + 27, RECIPE + 28, RECIPE + 29, RECIPE + 30, RECIPE + 31}
    };
    // レシピ (lua の関数, index)
    private final int[][] recipes = {
        {TYPE_CHAT_MESSAGE6, 1},
        {TYPE_CHAT_MESSAGE6, 2},
        {TYPE_CHAT_MESSAGE6, 3},
        {TYPE_CHAT_MESSAGE6, 4},
        {TYPE_CHAT_MESSAGE6, 5},
        {TYPE_CHAT_MESSAGE6, 6},
        {TYPE_CHAT_MESSAGE6, 7},
        {TYPE_CHAT_MESSAGE6, 100},
        {TYPE_CHAT_MESSAGE6, 101},
        {TYPE_CHAT_MESSAGE6, 102},
        {TYPE_CHAT_MESSAGE6, 103},
        {TYPE_CHAT_MESSAGE6, 104},
        {TYPE_CHAT_MESSAGE6, 105},
        {TYPE_CHAT_MESSAGE6, 106},
        {TYPE_CHAT_MESSAGE6, 107},
        {TYPE_CHAT_MESSAGE6, 108},
        {TYPE_CHAT_MESSAGE1, 200},
        {TYPE_CHAT_MESSAGE1, 201},
        {TYPE_CHAT_MESSAGE6, 300},
        {TYPE_CHAT_MESSAGE6, 301},
        {TYPE_CHAT_MESSAGE6, 302},
        {TYPE_CHAT_MESSAGE6, 303},
        {TYPE_CHAT_MESSAGE6, 304},
        {TYPE_CHAT_MESSAGE3, 1},
        {TYPE_CHAT_MESSAGE3, 2},
        {TYPE_CHAT_MESSAGE3, 3},
        {TYPE_CHAT_MESSAGE4, 1},
        {TYPE_CHAT_MESSAGE4, 2},
        {TYPE_CHAT_MESSAGE4, 3},
        {TYPE_CHAT_MESSAGE4, 4},
        {TYPE_CHAT_MESSAGE4, 5},
        {TYPE_CHAT_MESSAGE4, 6}
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
        {"#t4011007#", "精錬された#t4011000#、#t4011001#、#t4011002#、#t4011003#、#t4011004#、#t4011005#、#t4011006#"},
        {"#t4021009#", "精錬された#t4021000#、#t4021001#、#t4021002#、#t4021003#、#t4021004#、#t4021005#、#t4021006#、#t4021007#、#t4021008#"},
        {"#t4005000#", "#v4004000#", "#t4004000#"},
        {"#t4005001#", "#v4004001#", "#t4004001#"},
        {"#t4005002#", "#v4004002#", "#t4004002#"},
        {"#t4005003#", "#v4004003#", "#t4004003#"},
        {"#t4005004#", "#v4004004#", "#t4004004#"},
        {"#t4003001#", "#t4000003#"},
        {"#t4003001#", "#t4000018#s"},
        {"#t4003000#s", "#t4011001#と#t4011000#を各"},
        {"#t2060000#", "#v4003001# #t4003001# \r\n#v4003004# #t4003004# ", ""},
        {"#t2061000#", "#v4003001# #t4003001# \r\n#v4003004# #t4003004# ", ""},
        {"#t2060001#", "#v4011000# #t4011000# \r\n#v4003001# 3 #t4003001#s \r\n#v4003004# 10 #t4003004#s ", "攻撃力 +1"},
        {"#t2061001#", "#v4011000# #t4011000# \r\n#v4003001# 3 #t4003001#s \r\n#v4003004# 10 #t4003004#s ", "攻撃力 +1"},
        {"#t2060002#", "#v4011001# #t4011001# \r\n#v4003001# 5 #t4003001#s \r\n#v4003005# 15 #t4003005#s ", "攻撃力 +2"},
        {"#t2061002#", "#v4011001# #t4011001# \r\n#v4003001# 5 #t4003001#s \r\n#v4003005# 15 #t4003005#s ", "攻撃力 +2"}
    };
    // レシピの数値の引数
    private final int[][] recipe_values = {
        {300},
        {300},
        {300},
        {500},
        {500},
        {500},
        {800},
        {500},
        {500},
        {500},
        {500},
        {500},
        {500},
        {500},
        {1000},
        {3000},
        {10000},
        {15000},
        {5000},
        {5000},
        {5000},
        {5000},
        {100000},
        {10, 1},
        {5, 1},
        {1, 15},
        {1000},
        {1000},
        {900},
        {900},
        {800},
        {800}
    };
    // 所持金の増減, アイテムIDと増減数の組 (個数を指定する場合は1個あたり)
    private final int[][] recipe_exchanges = {
        {-300, 4010000, -10, 4011000, 1}, // 4011000 青銅
        {-300, 4010001, -10, 4011001, 1}, // 4011001 鋼鉄
        {-300, 4010002, -10, 4011002, 1}, // 4011002 ミスリル
        {-500, 4010003, -10, 4011003, 1}, // 4011003 アダマンティウム
        {-500, 4010004, -10, 4011004, 1}, // 4011004 銀
        {-500, 4010005, -10, 4011005, 1}, // 4011005 オリハルコン
        {-800, 4010006, -10, 4011006, 1}, // 4011006 金
        {-500, 4020000, -10, 4021000, 1}, // 4021000 ガーネット
        {-500, 4020001, -10, 4021001, 1}, // 4021001 紫水晶
        {-500, 4020002, -10, 4021002, 1}, // 4021002 アクアマリン
        {-500, 4020003, -10, 4021003, 1}, // 4021003 エメラルド
        {-500, 4020004, -10, 4021004, 1}, // 4021004 オパール
        {-500, 4020005, -10, 4021005, 1}, // 4021005 サファイア
        {-500, 4020006, -10, 4021006, 1}, // 4021006 トパーズ
        {-1000, 4020007, -10, 4021007, 1}, // 4021007 ダイヤモンド
        {-3000, 4020008, -10, 4021008, 1}, // 4021008 黒水晶
        {-10000, 4011000, -1, 4011001, -1, 4011002, -1, 4011003, -1, 4011004, -1, 4011005, -1, 4011006, -1, 4011007, 1}, // 4011007 月の石
        {-15000, 4021000, -1, 4021001, -1, 4021002, -1, 4021003, -1, 4021004, -1, 4021005, -1, 4021006, -1, 4021007, -1, 4021008, -1, 4021009, 1}, // 4021009 星の石
        {-5000, 4004000, -10, 4005000, 1}, // 4005000 クリスタル(力)
        {-5000, 4004001, -10, 4005001, 1}, // 4005001 クリスタル(知恵)
        {-5000, 4004002, -10, 4005002, 1}, // 4005002 クリスタル(素早さ)
        {-5000, 4004003, -10, 4005003, 1}, // 4005003 クリスタル(幸運)
        {-100000, 4004004, -10, 4005004, 1}, // 4005004 闇のクリスタル
        {0, 4000003, -10, 4003001, 1}, // 4003001 加工された木
        {0, 4000018, -5, 4003001, 1}, // 4003001 加工された木
        {0, 4011001, -1, 4011000, -1, 4003000, 15}, // 4003000 ネジ
        {0, 4003001, -1, 4003004, -1, 2060000, 1000}, // 2060000 弓専用の矢
        {0, 4003001, -1, 4003004, -1, 2061000, 1000}, // 2061000 弩専用の矢
        {0, 4011000, -1, 4003001, -3, 4003004, -10, 2060001, 900}, // 2060001 弓専用の矢(青銅)
        {0, 4011000, -1, 4003001, -3, 4003004, -10, 2061001, 900}, // 2061001 弩専用の矢(青銅)
        {0, 4011001, -1, 4003001, -5, 4003005, -15, 2060002, 800}, // 2060002 弓専用の矢(鋼鉄)
        {0, 4011001, -1, 4003001, -5, 4003005, -15, 2061002, 800} // 2061002 弩専用の矢(鋼鉄)
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
                int unit_price = values[0];
                if (index == 200 || index == 201) {
                    int ret_num = this.number;
                    int price = unit_price * ret_num;
                    is_number = true;
                    number_default = 0;
                    number_min = 0;
                    number_max = 100;
                    text_ask = "よし、よし…#b" + make_item + "#kをいくつ作りたい？";
                    text_confirm = "よし、#b" + ret_num + " " + make_item + "#kを作りたいんだな？？それなら#r" + price + "メルと" + ret_num + " " + need_item + "#kがそれぞれ必要だ。どうだい？本当に作るかい？";
                    text_answer_no = "なるほど。手数料が高すぎるかな？でも、俺は長いことこの町にいるから、何か精錬したくなったらいつでも持ってきてくれ。";
                    text_fail = "ふむ…必要な材料がすべてそろっているか、ETCインベントリに空きがあるか確認してくれ。";
                    text_ok = "ほら！#b" + ret_num + " " + make_item + "#kだ。もう50年になるが、腕はまだ衰えちゃいないぞ。近いうちにまた手伝いが必要になったら、気軽に来てくれ。";
                } else {
                    int ret_num = this.number;
                    int price = unit_price * ret_num;
                    int all_num = ret_num * 10;
                    is_number = true;
                    number_default = 0;
                    number_min = 0;
                    number_max = 100;
                    text_ask = "" + make_item + "を作るには、以下の材料が必要だ。いくつ作りたい？\r\n\r\n#b" + need_item + "s\r\n#v4031138#" + unit_price + " mesos#k";
                    text_confirm = "#b" + ret_num + " " + make_item + "#kを作りたいのかい？？それなら#r#v4031138#" + price + "メルと" + all_num + " " + need_item + "#kが必要だ。どうだい？作るかい？";
                    text_answer_no = "なるほど…手数料が高すぎるかな？俺は長いことこの町にいるから、何か精錬したくなったらいつでも持ってきてくれ。";
                    text_fail = "ふむ…必要な材料がすべてそろっているか、ETCインベントリに空きがあるか確認してくれ。";
                    text_ok = "ほら！#b" + ret_num + " " + make_item + "#kだ。もう50年になるが、腕はまだ衰えちゃいないぞ。近いうちにまた手伝いが必要になったら、気軽に来てくれ。";
                }
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
                text_ask = "#b" + item_number + " " + make_item + "を" + need_number + " " + need_item + "#kから作ってやれるぞ。必要な材料さえあればタダだから、喜んでくれ。どうだい？いくつ作りたい？";
                text_confirm = "よし、#b" + make_item + "#k " + ret_num + "回作るかい？それなら#r" + need_num + " " + need_item + "#kが必要だ。それでも作ってほしいか？";
                text_answer_no = "材料がないのか？このあたりのモンスターを倒せば手に入るから、頑張って集めてくれ…";
                text_fail = "ふむ…必要な材料がすべてそろっているか、ETCインベントリに空きがあるか確認してくれ。";
                text_ok = "ほら！#b" + all_num + " " + make_item + "#kだ！もう50年になるが、腕はまだ衰えちゃいないぞ。近いうちにまた手伝いが必要になったら、気軽に来てくれ。";
            }
            case TYPE_CHAT_MESSAGE4 -> {
                String make_item = texts[0];
                String need_item = texts[1];
                int unit_num = values[0];
                String item_option = texts[2];
                if (item_option.isEmpty()) {
                    text_confirm = "#b" + unit_num + " "+ make_item + "#kを作るには、以下の材料が必要だ。材料を持っているなら、店で買うよりここで作った方がずっといい。で、作るかい？\r\n\r\n#b" + need_item;
                } else {
                    text_confirm = "#b" + unit_num + " " + make_item + "#kを作りたいのか？以下のアイテムを持ってきてくれたら作ってやろう。ちなみに、この矢には#r" + item_option + "#kの強化が付いている。特別な矢だから、材料を持っているなら悪くない話だぞ。作るかい？\r\n\r\n#b" + need_item;
                }
                text_answer_no = "弓使いにとって大切なアイテムを、安い値段で作っているんだ。じっくり見ていってくれ。それと、「タダ」のものなんてないのはわかってるよな？";
                text_fail = "";
                text_ok = "";
            }
            case TYPE_CHAT_MESSAGE6 -> {
                String make_item = texts[0];
                String need_item_icon = texts[1];
                String need_item_string = texts[2];
                int unit_price = values[0];
                if (index == 200 || index == 201) {
                    int ret_num = this.number;
                    int price = unit_price * ret_num;
                    is_number = true;
                    number_default = 0;
                    number_min = 0;
                    number_max = 100;
                    text_ask = "よし、よし…#b" + make_item + "#kをいくつ作りたい？";
                    text_confirm = "よし、#b" + ret_num + " " + make_item + "#kを作りたいんだな？？それなら#r" + price + "メルと" + need_item_icon + " " + ret_num + " " + need_item_string + "#kがそれぞれ必要だ。どうだい？本当に作るかい？";
                    text_answer_no = "なるほど。手数料が高すぎるかな？でも、俺は長いことこの町にいるから、何か精錬したくなったらいつでも持ってきてくれ。";
                    text_fail = "ふむ…必要な材料がすべてそろっているか、ETCインベントリに空きがあるか確認してくれ。";
                    text_ok = "ほら！#b" + ret_num + " " + make_item + "#kだ。もう50年になるが、腕はまだ衰えちゃいないぞ。近いうちにまた手伝いが必要になったら、気軽に来てくれ。";
                } else {
                    int ret_num = this.number;
                    int price = unit_price * ret_num;
                    int all_num = ret_num * 10;
                    is_number = true;
                    number_default = 0;
                    number_min = 0;
                    number_max = 100;
                    text_ask = "" + make_item + "を作るには、以下の材料が必要だ。いくつ作りたい？\r\n\r\n#b" + need_item_icon + " 10 " + need_item_string + "\r\n" + unit_price + " mesos#k";
                    text_confirm = "#b" + ret_num + " " + make_item + "#kを作りたいのかい？？それなら#r" + price + "メルと" + need_item_icon + " " + all_num + " " + need_item_string + "#kが必要だ。どうだい？作るかい？";
                    text_answer_no = "なるほど…手数料が高すぎるかな？俺は長いことこの町にいるから、何か精錬したくなったらいつでも持ってきてくれ。";
                    text_fail = "ふむ…必要な材料がすべてそろっているか、ETCインベントリに空きがあるか確認してくれ。";
                    text_ok = "ほら！#b" + ret_num + " " + make_item + "#kだ。もう50年になるが、腕はまだ衰えちゃいないぞ。近いうちにまた手伝いが必要になったら、気軽に来てくれ。";
                }
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
