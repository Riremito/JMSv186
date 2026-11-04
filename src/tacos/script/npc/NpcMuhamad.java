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
// Npc.wz script : make_ariant1 (JMS131-194), lua_script_jp の make_ariant1 は中身がないため js の処理を利用
public class NpcMuhamad extends NpcScriptMan {

    // lua の関数
    private final int TYPE_REFINE = 0; // refine
    private final int RECIPE = 1000;
    private final int PHASE_START = 0;
    private final int PHASE_MENU = 1;
    private final int PHASE_MENU_ANSWER = 2;
    private final int PHASE_RECIPE = 3;
    private final int PHASE_NUMBER_ANSWER = 4;
    private final int PHASE_EXECUTE = 5;
    // TODO : 文章 (適当)
    private final String text_intro = "鉱物や宝石の原石を精錬しに来たのですか？どれだけ原石を持っていても、私のような職人が精錬しなければ使い物になりませんよ。今すぐ精錬しますか？";
    // TODO : 文章 (適当)
    private final String text_intro_no = "急ぎでなければ、また後で来てください。ご覧のとおり、今は仕事が山積みなのです。";
    // メニュー
    // TODO : 文章 (適当)
    private final String[] menu_texts = {
        "いい心がけです！さっそく取りかかりましょう。どの原石を精錬しますか？\r\n#b#L0# 鉱物の原石を精錬する#l\r\n#L1# 宝石の原石を精錬する#l\r\n#L2# クリスタルの原石を精錬する#l",
        "どの鉱物を精錬しますか？\r\n#b#L0# #t4011000##l\r\n#L1# #t4011001##l\r\n#L2# #t4011002##l\r\n#L3# #t4011003##l\r\n#L4# #t4011004##l\r\n#L5# #t4011005##l\r\n#L6# #t4011006##l\r\n#L7# #t4011008##l",
        "どの宝石を精錬しますか？\r\n#b#L0# #t4021000##l\r\n#L1# #t4021001##l\r\n#L2# #t4021002##l\r\n#L3# #t4021003##l\r\n#L4# #t4021004##l\r\n#L5# #t4021005##l\r\n#L6# #t4021006##l\r\n#L7# #t4021007##l\r\n#L8# #t4021008##l",
        "どのクリスタルを精錬しますか？\r\n#b#L0# #t4005000##l\r\n#L1# #t4005001##l\r\n#L2# #t4005002##l\r\n#L3# #t4005003##l"
    };
    // メニューの前の会話
    // TODO : 文章 (適当)
    private final String[][] menu_says = {
        {},
        {},
        {},
        {}
    };
    // メニューの選択肢 (RECIPE 以上はレシピ番号)
    private final int[][] menu_children = {
        {1, 2, 3},
        {RECIPE + 0, RECIPE + 1, RECIPE + 2, RECIPE + 3, RECIPE + 4, RECIPE + 5, RECIPE + 6, RECIPE + 7},
        {RECIPE + 8, RECIPE + 9, RECIPE + 10, RECIPE + 11, RECIPE + 12, RECIPE + 13, RECIPE + 14, RECIPE + 15, RECIPE + 16},
        {RECIPE + 17, RECIPE + 18, RECIPE + 19, RECIPE + 20}
    };
    // レシピ (lua の関数, index)
    private final int[][] recipes = {
        {TYPE_REFINE, 1},
        {TYPE_REFINE, 2},
        {TYPE_REFINE, 3},
        {TYPE_REFINE, 4},
        {TYPE_REFINE, 5},
        {TYPE_REFINE, 6},
        {TYPE_REFINE, 7},
        {TYPE_REFINE, 8},
        {TYPE_REFINE, 100},
        {TYPE_REFINE, 101},
        {TYPE_REFINE, 102},
        {TYPE_REFINE, 103},
        {TYPE_REFINE, 104},
        {TYPE_REFINE, 105},
        {TYPE_REFINE, 106},
        {TYPE_REFINE, 107},
        {TYPE_REFINE, 108},
        {TYPE_REFINE, 200},
        {TYPE_REFINE, 201},
        {TYPE_REFINE, 202},
        {TYPE_REFINE, 203}
    };
    // レシピの文字列の引数
    // TODO : 文章 (適当)
    private final String[][] recipe_texts = {
        {"#t4011000#", "#v4010000# #t4010000# 10個"},
        {"#t4011001#", "#v4010001# #t4010001# 10個"},
        {"#t4011002#", "#v4010002# #t4010002# 10個"},
        {"#t4011003#", "#v4010003# #t4010003# 10個"},
        {"#t4011004#", "#v4010004# #t4010004# 10個"},
        {"#t4011005#", "#v4010005# #t4010005# 10個"},
        {"#t4011006#", "#v4010006# #t4010006# 10個"},
        {"#t4011008#", "#v4010007# #t4010007# 10個"},
        {"#t4021000#", "#v4020000# #t4020000# 10個"},
        {"#t4021001#", "#v4020001# #t4020001# 10個"},
        {"#t4021002#", "#v4020002# #t4020002# 10個"},
        {"#t4021003#", "#v4020003# #t4020003# 10個"},
        {"#t4021004#", "#v4020004# #t4020004# 10個"},
        {"#t4021005#", "#v4020005# #t4020005# 10個"},
        {"#t4021006#", "#v4020006# #t4020006# 10個"},
        {"#t4021007#", "#v4020007# #t4020007# 10個"},
        {"#t4021008#", "#v4020008# #t4020008# 10個"},
        {"#t4005000#", "#v4004000# #t4004000# 10個"},
        {"#t4005001#", "#v4004001# #t4004001# 10個"},
        {"#t4005002#", "#v4004002# #t4004002# 10個"},
        {"#t4005003#", "#v4004003# #t4004003# 10個"}
    };
    // レシピの数値の引数
    private final int[][] recipe_values = {
        {270},
        {270},
        {270},
        {450},
        {450},
        {450},
        {720},
        {270},
        {450},
        {450},
        {450},
        {450},
        {450},
        {450},
        {450},
        {900},
        {2700},
        {4500},
        {4500},
        {4500},
        {4500}
    };
    // 所持金の増減, アイテムIDと増減数の組 (個数を指定する場合は1個あたり)
    private final int[][] recipe_exchanges = {
        {-270, 4010000, -10, 4011000, 1}, // 4011000 青銅
        {-270, 4010001, -10, 4011001, 1}, // 4011001 鋼鉄
        {-270, 4010002, -10, 4011002, 1}, // 4011002 ミスリル
        {-450, 4010003, -10, 4011003, 1}, // 4011003 アダマンティウム
        {-450, 4010004, -10, 4011004, 1}, // 4011004 銀
        {-450, 4010005, -10, 4011005, 1}, // 4011005 オリハルコン
        {-720, 4010006, -10, 4011006, 1}, // 4011006 金
        {-270, 4010007, -10, 4011008, 1}, // 4011008 リチウム
        {-450, 4020000, -10, 4021000, 1}, // 4021000 ガーネット
        {-450, 4020001, -10, 4021001, 1}, // 4021001 紫水晶
        {-450, 4020002, -10, 4021002, 1}, // 4021002 アクアマリン
        {-450, 4020003, -10, 4021003, 1}, // 4021003 エメラルド
        {-450, 4020004, -10, 4021004, 1}, // 4021004 オパール
        {-450, 4020005, -10, 4021005, 1}, // 4021005 サファイア
        {-450, 4020006, -10, 4021006, 1}, // 4021006 トパーズ
        {-900, 4020007, -10, 4021007, 1}, // 4021007 ダイヤモンド
        {-2700, 4020008, -10, 4021008, 1}, // 4021008 黒水晶
        {-4500, 4004000, -10, 4005000, 1}, // 4005000 クリスタル(力)
        {-4500, 4004001, -10, 4005001, 1}, // 4005001 クリスタル(知恵)
        {-4500, 4004002, -10, 4005002, 1}, // 4005002 クリスタル(素早さ)
        {-4500, 4004003, -10, 4005003, 1} // 4005003 クリスタル(幸運)
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
            case TYPE_REFINE -> {
                String make_item = texts[0];
                String need_item = texts[1];
                int unit_price = values[0];
                int ret_num = this.number;
                int price = unit_price * ret_num;
                int all_num = ret_num * 10;
                is_number = true;
                number_default = 1;
                number_min = 1;
                number_max = 100;
                text_ask = "#b" + make_item + "#kを作るには、以下の材料が必要です。いくつ作りますか？\r\n\r\n#b" + need_item + "\r\n#v4031138# " + unit_price + "メル#k";
                text_answer_no = "";
                text_fail = "必要な材料とメルがそろっているか、ETCインベントリに空きがあるか確認してください。";
                text_ok = "はい、できました。どうです、見事な出来栄えでしょう？ほかに用があれば、いつでも来てください。";
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
        if (this.phase == PHASE_NUMBER_ANSWER && text_confirm.isEmpty()) {
            // 確認なし
            this.phase = PHASE_EXECUTE;
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
