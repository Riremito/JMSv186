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
// Npc.wz script : refine_nautillus (JMS146-194), lua_script_jp の refine_nautillus は中身がないため js の処理を利用
public class Npc_Nautilus_Serryl extends NpcScriptMan {

    // lua の関数
    private final int TYPE_MAKE = 0; // make
    private final int RECIPE = 1000;
    private final int PHASE_START = 0;
    private final int PHASE_MENU = 1;
    private final int PHASE_MENU_ANSWER = 2;
    private final int PHASE_RECIPE = 3;
    private final int PHASE_NUMBER_ANSWER = 4;
    private final int PHASE_EXECUTE = 5;
    // メニュー
    // TODO : 文章 (適当)
    private final String[] menu_texts = {
        "何だ？自分の武器やグローブを作りたいのか？経験がないと一人で作るのは大変だぞ…手伝ってやるよ。俺は20年海賊をやってきて、その間ずっとここの船員たちのためにいろいろなアイテムを作ってきたんだ。お手のものさ。\r\n#b#L0# ナックルを作る#l\r\n#L1# 短銃を作る#l\r\n#L2# グローブを作る#l",
        "材料さえ持ってきてくれれば、立派なナックルを作ってやるよ。どのナックルを作りたい？\r\n#L0##b #t1482001##k(制限レベル: 15, 海賊)#l\r\n#L1##b #t1482002##k(制限レベル: 20, 海賊)#l\r\n#L2##b #t1482003##k(制限レベル: 25, 海賊)#l\r\n#L3##b #t1482004##k(制限レベル: 30, 海賊)#l\r\n#L4##b #t1482005##k(制限レベル: 35, 海賊)#l\r\n#L5##b #t1482006##k(制限レベル: 40, 海賊)#l\r\n#L6##b #t1482007##k(制限レベル: 50, 海賊)#l",
        "材料さえ持ってきてくれれば、立派な短銃を作ってやるよ。どの短銃を作りたい？\r\n#L0##b #t1492001##k(制限レベル: 15, 海賊)#l\r\n#L1##b #t1492002##k(制限レベル: 20, 海賊)#l\r\n#L2##b #t1492003##k(制限レベル: 25, 海賊)#l\r\n#L3##b #t1492004##k(制限レベル: 30, 海賊)#l\r\n#L4##b #t1492005##k(制限レベル: 35, 海賊)#l\r\n#L5##b #t1492006##k(制限レベル: 40, 海賊)#l\r\n#L6##b #t1492007##k(制限レベル: 50, 海賊)#l",
        "材料さえ持ってきてくれれば、立派なグローブを作ってやるよ。どのグローブを作りたい？\r\n#L0##b #t1082180##k(制限レベル: 15, 海賊)#l\r\n#L1##b #t1082183##k(制限レベル: 20, 海賊)#l\r\n#L2##b #t1082186##k(制限レベル: 25, 海賊)#l\r\n#L3##b #t1082189##k(制限レベル: 30, 海賊)#l\r\n#L4##b #t1082192##k(制限レベル: 35, 海賊)#l\r\n#L5##b #t1082195##k(制限レベル: 40, 海賊)#l\r\n#L6##b #t1082198##k(制限レベル: 50, 海賊)#l\r\n#L7##b #t1082201##k(制限レベル: 60, 海賊)#l"
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
        {RECIPE + 0, RECIPE + 1, RECIPE + 2, RECIPE + 3, RECIPE + 4, RECIPE + 5, RECIPE + 6},
        {RECIPE + 7, RECIPE + 8, RECIPE + 9, RECIPE + 10, RECIPE + 11, RECIPE + 12, RECIPE + 13},
        {RECIPE + 14, RECIPE + 15, RECIPE + 16, RECIPE + 17, RECIPE + 18, RECIPE + 19, RECIPE + 20, RECIPE + 21}
    };
    // レシピ (lua の関数, index)
    private final int[][] recipes = {
        {TYPE_MAKE, 0},
        {TYPE_MAKE, 1},
        {TYPE_MAKE, 2},
        {TYPE_MAKE, 3},
        {TYPE_MAKE, 4},
        {TYPE_MAKE, 5},
        {TYPE_MAKE, 6},
        {TYPE_MAKE, 100},
        {TYPE_MAKE, 101},
        {TYPE_MAKE, 102},
        {TYPE_MAKE, 103},
        {TYPE_MAKE, 104},
        {TYPE_MAKE, 105},
        {TYPE_MAKE, 106},
        {TYPE_MAKE, 200},
        {TYPE_MAKE, 201},
        {TYPE_MAKE, 202},
        {TYPE_MAKE, 203},
        {TYPE_MAKE, 204},
        {TYPE_MAKE, 205},
        {TYPE_MAKE, 206},
        {TYPE_MAKE, 207}
    };
    // レシピの文字列の引数
    // TODO : 文章 (適当)
    private final String[][] recipe_texts = {
        {"#t1482001#", "#v4000021# #t4000021# 20個 \r\n#v4031138# 1,000メル"},
        {"#t1482002#", "#v4011001# #t4011001# \r\n#v4011000# #t4011000# \r\n#v4000021# #t4000021# 10個 \r\n#v4003000# #t4003000# 5個 \r\n#v4031138# 2,000メル"},
        {"#t1482003#", "#v4011000# #t4011000# 2個 \r\n#v4011001# #t4011001# \r\n#v4003000# #t4003000# 10個 \r\n#v4031138# 5,000メル"},
        {"#t1482004#", "#v4011000# #t4011000# \r\n#v4011001# #t4011001# \r\n#v4000021# #t4000021# 30個 \r\n#v4003000# #t4003000# 10個 \r\n#v4031138# 15,000メル"},
        {"#t1482005#", "#v4011000# #t4011000# 2個 \r\n#v4011001# #t4011001# 2個 \r\n#v4000021# #t4000021# 30個 \r\n#v4003000# #t4003000# 20個 \r\n#v4031138# 30,000メル"},
        {"#t1482006#", "#v4011000# #t4011000# \r\n#v4011001# #t4011001# \r\n#v4021000# #t4021000# 2個 \r\n#v4000021# #t4000021# 50個 \r\n#v4003000# #t4003000# 20個 \r\n#v4031138# 50,000メル"},
        {"#t1482007#", "#v4000039# #t4000039# 150個 \r\n#v4011000# #t4011000# \r\n#v4011001# #t4011001# 2個 \r\n#v4000030# #t4000030# 20個 \r\n#v4000021# #t4000021# 20個 \r\n#v4003000# #t4003000# 20個 \r\n#v4031138# 100,000メル"},
        {"#t1492001#", "#v4011000# #t4011000# \r\n#v4003000# #t4003000# 5個 \r\n#v4003001# #t4003001# \r\n#v4031138# 1,000メル"},
        {"#t1492002#", "#v4011000# #t4011000# \r\n#v4003000# #t4003000# 10個 \r\n#v4003001# #t4003001# 5個 \r\n#v4000021# #t4000021# 10個 \r\n#v4031138# 2,000メル"},
        {"#t1492003#", "#v4011000# #t4011000# 2個 \r\n#v4003000# #t4003000# 10個 \r\n#v4031138# 5,000メル"},
        {"#t1492004#", "#v4011001# #t4011001# 2個 \r\n#v4000021# #t4000021# 10個 \r\n#v4003000# #t4003000# 10個 \r\n#v4031138# 15,000メル"},
        {"#t1492005#", "#v4011006# #t4011006# 10個 \r\n#v4011001# #t4011001# 2個 \r\n#v4000021# #t4000021# 5個 \r\n#v4003000# #t4003000# 10個 \r\n#v4031138# 30,000メル"},
        {"#t1492006#", "#v4011004# #t4011004# \r\n#v4011001# #t4011001# 2個 \r\n#v4000021# #t4000021# 10個 \r\n#v4003000# #t4003000# 20個 \r\n#v4031138# 50,000メル"},
        {"#t1492007#", "#v4011006# #t4011006# \r\n#v4011004# #t4011004# 2個 \r\n#v4011001# #t4011001# 4個 \r\n#v4000030# #t4000030# 30個 \r\n#v4003000# #t4003000# 30個 \r\n#v4031138# 100,000メル"},
        {"#t1082180#", "#v4000021# #t4000021# 15個 \r\n#v4021003# #t4021003# \r\n#v4031138# 1,000メル"},
        {"#t1082183#", "#v4000021# #t4000021# 35個 \r\n#v4031138# 8,000メル"},
        {"#t1082186#", "#v4011000# #t4011000# 2個 \r\n#v4000021# #t4000021# 20個 \r\n#v4031138# 15,000メル"},
        {"#t1082189#", "#v4021006# #t4021006# 2個 \r\n#v4000021# #t4000021# 50個 \r\n#v4003000# #t4003000# 10個 \r\n#v4031138# 25,000メル"},
        {"#t1082192#", "#v4011000# #t4011000# 3個 \r\n#v4000021# #t4000021# 60個 \r\n#v4003000# #t4003000# 15個 \r\n#v4031138# 30,000メル"},
        {"#t1082195#", "#v4000021# #t4000021# 80個 \r\n#v4011000# #t4011000# 3個 \r\n#v4011001# #t4011001# 3個 \r\n#v4003000# #t4003000# 25個 \r\n#v4031138# 40,000メル"},
        {"#t1082198#", "#v4011000# #t4011000# 3個 \r\n#v4000021# #t4000021# 20個 \r\n#v4000030# #t4000030# 40個 \r\n#v4003000# #t4003000# 30個 \r\n#v4031138# 50,000メル"},
        {"#t1082201#", "#v4011007# #t4011007# \r\n#v4021008# #t4021008# \r\n#v4021007# #t4021007# \r\n#v4000030# #t4000030# 50個 \r\n#v4003000# #t4003000# 50個 \r\n#v4031138# 70,000メル"}
    };
    // レシピの数値の引数
    private final int[][] recipe_values = {
        {15},
        {20},
        {25},
        {30},
        {35},
        {40},
        {50},
        {15},
        {20},
        {25},
        {30},
        {35},
        {40},
        {50},
        {15},
        {20},
        {25},
        {30},
        {35},
        {40},
        {50},
        {60}
    };
    // 所持金の増減, アイテムIDと増減数の組 (個数を指定する場合は1個あたり)
    private final int[][] recipe_exchanges = {
        {-1000, 4000021, -20, 1482001, 1}, // 1482001 レザーアームズ
        {-2000, 4011001, -1, 4011000, -1, 4000021, -10, 4003000, -5, 1482002, 1}, // 1482002 ダブルテイルナックル
        {-5000, 4011000, -2, 4011001, -1, 4003000, -10, 1482003, 1}, // 1482003 ノマングリップ
        {-15000, 4011000, -1, 4011001, -1, 4000021, -30, 4003000, -10, 1482004, 1}, // 1482004 プライムハンズ
        {-30000, 4011000, -2, 4011001, -2, 4000021, -30, 4003000, -20, 1482005, 1}, // 1482005 メイデン(銀)
        {-50000, 4011000, -1, 4011001, -1, 4021000, -2, 4000021, -50, 4003000, -20, 1482006, 1}, // 1482006 ネオジャード
        {-100000, 4000039, -150, 4011000, -1, 4011001, -2, 4000030, -20, 4000021, -20, 4003000, -20, 1482007, 1}, // 1482007 デビルクロー
        {-1000, 4011000, -1, 4003000, -5, 4003001, -1, 1492001, 1}, // 1492001 デリンジャー
        {-2000, 4011000, -1, 4003000, -10, 4003001, -5, 4000021, -10, 1492002, 1}, // 1492002 ゲランドピストル
        {-5000, 4011000, -2, 4003000, -10, 1492003, 1}, // 1492003 ゴールデンフック
        {-15000, 4011001, -2, 4000021, -10, 4003000, -10, 1492004, 1}, // 1492004 コールドマインド
        {-30000, 4011006, -10, 4011001, -2, 4000021, -5, 4003000, -10, 1492005, 1}, // 1492005 シュ―ティングスター
        {-50000, 4011004, -1, 4011001, -2, 4000021, -10, 4003000, -20, 1492006, 1}, // 1492006 ルナシューター
        {-100000, 4011006, -1, 4011004, -2, 4011001, -4, 4000030, -30, 4003000, -30, 1492007, 1}, // 1492007 ラスペルトガン
        {-1000, 4000021, -15, 4021003, -1, 1082180, 1}, // 1082180 ラガーハーフグローブ（緑)
        {-8000, 4000021, -35, 1082183, 1}, // 1082183 レザーアーマーグローブ（茶)
        {-15000, 4011000, -2, 4000021, -20, 1082186, 1}, // 1082186 ハードレザーグローブ
        {-25000, 4021006, -2, 4000021, -50, 4003000, -10, 1082189, 1}, // 1082189 タルティス（黄)
        {-30000, 4011000, -3, 4000021, -60, 4003000, -15, 1082192, 1}, // 1082192 ジュエルド（茶)
        {-40000, 4000021, -80, 4011000, -3, 4011001, -3, 4003000, -25, 1082195, 1}, // 1082195 バルビー（茶)
        {-50000, 4011000, -3, 4000021, -20, 4000030, -40, 4003000, -30, 1082198, 1}, // 1082198 ロイス（茶)
        {-70000, 4011007, -1, 4021008, -1, 4021007, -1, 4000030, -50, 4003000, -50, 1082201, 1} // 1082201 シュルト（黒)
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
            case TYPE_MAKE -> {
                String make_item = texts[0];
                String need_item = texts[1];
                int req_level = values[0];
                text_confirm = "#b" + make_item + "#kを1つ作るには、以下の材料が必要だ。制限レベルは" + req_level + "だから、本当に必要か確かめてから作ってくれ。どうする？作るかい？\r\n\r\n#b" + need_item;
                text_answer_no = "";
                text_fail = "必要な材料がすべてそろっているか確認してくれ。それと、装備インベントリに空きがないと渡せないからな。";
                text_ok = "できたぞ。ほかにも何か必要なら…まあ、俺はどこにも行かないからな。";
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
