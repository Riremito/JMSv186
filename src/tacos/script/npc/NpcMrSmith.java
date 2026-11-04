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
// Npc.wz script : refine_perion2 (JMS131-194), 会話文は lua_script_jp (refine_perion2) を利用
public class NpcMrSmith extends NpcScriptMan {

    // lua の関数
    private final int TYPE_CHAT_MESSAGE3 = 0; // chat_message3
    private final int TYPE_CHAT_MESSAGE4 = 1; // chat_message4
    private final int TYPE_CHAT_MESSAGE5 = 2; // chat_message5
    private final int RECIPE = 1000;
    private final int PHASE_START = 0;
    private final int PHASE_MENU = 1;
    private final int PHASE_MENU_ANSWER = 2;
    private final int PHASE_RECIPE = 3;
    private final int PHASE_NUMBER_ANSWER = 4;
    private final int PHASE_EXECUTE = 5;
    // 翻訳
    private final String text_intro = "俺はミスター・サンダーの弟子さ。師匠もずいぶん年を取って、昔ほどの腕じゃなくなってきたんだ…はは！ああ、しまった！俺がこう言ったことは師匠に内緒にしてくれよ…さて…俺は戦士専用のアイテムをいろいろ作れるんだが、どうだい？作らせてくれるか？";
    // 翻訳
    private final String text_intro_no = "はあ…今日ちゃんと仕事をしないと、きっと師匠にこっぴどく叱られるよ…ああ、もう…まったく！";
    // メニュー
    // 翻訳
    private final String[] menu_texts = {
        "よし！手数料は手頃だから心配いらないよ。何をしたい？\r\n#b#L0# グローブを作る#l\r\n#L1# グローブを強化する#l\r\n#L2# 材料を作る#l",
        "俺はこの町一番のグローブ職人さ！！さて…どんなグローブを作ってほしい？\r\n#L0##b #t1082003##k (制限レベル: 10, 戦士)#l\r\n#L1##b #t1082000##k(制限レベル: 15, 戦士)#l\r\n#L2##b #t1082004##k(制限レベル: 20, 戦士)#l\r\n#L3##b #t1082001##k(制限レベル: 25, 戦士)#l\r\n#L4##b #t1082007##k(制限レベル: 30, 戦士)#l\r\n#L5##b #t1082008##k(制限レベル: 35, 戦士)#l\r\n#L6##b #t1082023##k(制限レベル: 40, 戦士)#l\r\n#L7##b #t1082009##k(制限レベル: 50, 戦士)#l\r\n#L8##b #t1082059##k(制限レベル: 60, 戦士)#l",
        "それで～どのグローブを強化・製作したい？\r\n#L0##b #t1082005##k(制限レベル: 30, 戦士)#l\r\n#L1##b #t1082006##k(制限レベル: 30, 戦士)#l\r\n#L2##b #t1082035##k(制限レベル: 35, 戦士)#l\r\n#L3##b #t1082036##k(制限レベル: 35, 戦士)#l\r\n#L4##b #t1082024##k(制限レベル: 40, 戦士)#l\r\n#L5##b #t1082025##k(制限レベル: 40, 戦士)#l\r\n#L6##b #t1082010##k(制限レベル: 50, 戦士)#l\r\n#L7##b #t1082011##k(制限レベル: 50, 戦士)#l\r\n#L8##b #t1082060##k(制限レベル: 60, 戦士)#l\r\n#L9##b #t1082061##k(制限レベル: 60, 戦士)#l",
        "材料を作りたいんだな？よし…どんな材料を作りたい？\r\n#L0##b#t4000003#で#t4003001#を作る#k#l\r\n#L1##b#t4000018#で#t4003001#を作る#k#l\r\n#L2##b#t4003000#を作る#k#l"
    };
    // メニューの前の会話
    // 翻訳
    private final String[][] menu_says = {
        {},
        {},
        {
            "グローブを強化したいんだな？よし、わかった。だが一つ忠告しておこう。強化に使うアイテムはすべてなくなるし、すでに書で#r強化#kされたアイテムを使っても、強化したときにその効果はなくなってしまう。それを踏まえて決めてくれよ、いいな？"
        },
        {}
    };
    // メニューの選択肢 (RECIPE 以上はレシピ番号)
    private final int[][] menu_children = {
        {1, 2, 3},
        {RECIPE + 0, RECIPE + 1, RECIPE + 2, RECIPE + 3, RECIPE + 4, RECIPE + 5, RECIPE + 6, RECIPE + 7, RECIPE + 8},
        {RECIPE + 9, RECIPE + 10, RECIPE + 11, RECIPE + 12, RECIPE + 13, RECIPE + 14, RECIPE + 15, RECIPE + 16, RECIPE + 17, RECIPE + 18},
        {RECIPE + 19, RECIPE + 20, RECIPE + 21}
    };
    // レシピ (lua の関数, index)
    private final int[][] recipes = {
        {TYPE_CHAT_MESSAGE3, 1},
        {TYPE_CHAT_MESSAGE3, 2},
        {TYPE_CHAT_MESSAGE3, 3},
        {TYPE_CHAT_MESSAGE3, 4},
        {TYPE_CHAT_MESSAGE3, 5},
        {TYPE_CHAT_MESSAGE3, 6},
        {TYPE_CHAT_MESSAGE3, 7},
        {TYPE_CHAT_MESSAGE3, 8},
        {TYPE_CHAT_MESSAGE3, 9},
        {TYPE_CHAT_MESSAGE4, 1},
        {TYPE_CHAT_MESSAGE4, 2},
        {TYPE_CHAT_MESSAGE4, 3},
        {TYPE_CHAT_MESSAGE4, 4},
        {TYPE_CHAT_MESSAGE4, 5},
        {TYPE_CHAT_MESSAGE4, 6},
        {TYPE_CHAT_MESSAGE4, 7},
        {TYPE_CHAT_MESSAGE4, 8},
        {TYPE_CHAT_MESSAGE4, 9},
        {TYPE_CHAT_MESSAGE4, 10},
        {TYPE_CHAT_MESSAGE5, 1},
        {TYPE_CHAT_MESSAGE5, 2},
        {TYPE_CHAT_MESSAGE5, 3}
    };
    // レシピの文字列の引数
    // 翻訳
    private final String[][] recipe_texts = {
        {"#t1082003#", "#v4000021# #t4000021# 15個\r\n#v4011001# #t4011001# \r\n#v4031138# 1,000メル"},
        {"#t1082000#", "#v4011001# #t4011001# 2個 \r\n#v4031138# 2,000メル"},
        {"#t1082004#", "#v4000021# #t4000021# 40個 \r\n#v4011000# #t4011000# 2個 \r\n#v4031138# 5,000メル"},
        {"#t1082001#", "#v4011001# #t4011001# 2個 \r\n#v4031138# 10,000メル"},
        {"#t1082007#", "#v4011000# #t4011000# 3個 \r\n#v4011001# #t4011001# 2個 \r\n#v4003000# #t4003000# 15個 \r\n#v4031138# 20,000メル"},
        {"#t1082008#", "#v4000021# #t4000021# 30個 \r\n#v4011001# #t4011001# 4個 \r\n#v4003000# #t4003000# 30個 \r\n#v4031138# 30,000メル"},
        {"#t1082023#", "#v4000021# #t4000021# 50個 \r\n#v4011001# #t4011001# 5個 \r\n#v4003000# #t4003000# 40個 \r\n#v4031138# 40,000メル"},
        {"#t1082009#", "#v4011001# #t4011001# 3個 \r\n#v4021007# #t4021007# 2個 \r\n#v4000030# #t4000030# 30個 \r\n#v4003000# #t4003000# 45個 \r\n#v4031138# 50,000メル"},
        {"#t1082059#", "#v4011007# #t4011007# \r\n#v4011000# #t4011000# 8個 \r\n#v4011006# #t4011006# 2個 \r\n#v4000030# #t4000030# 50個 \r\n#v4003000# #t4003000# 50個 \r\n#v4031138# 70,000メル"},
        {"#t1082005#", "#v1082007# #t1082007# \r\n#v4011001# #t4011001# \r\n#v4031138# 20,000メル"},
        {"#t1082006#", "#v1082007# #t1082007# \r\n#v4011005# #t4011005# 2個 \r\n#v4031138# 25,000メル"},
        {"#t1082035#", "#v1082008# #t1082008# \r\n#v4021006# #t4021006# 3個 \r\n#v4031138# 30,000メル"},
        {"#t1082036#", "#v1082008# #t1082008# \r\n#v4021008# #t4021008# \r\n#v4031138# 40,000メル"},
        {"#t1082024#", "#v1082023# #t1082023# \r\n#v4011003# #t4011003# 4個 \r\n#v4031138# 45,000メル"},
        {"#t1082025#", "#v1082023# #t1082023# \r\n#v4021008# #t4021008# 2個 \r\n#v4031138# 50,000メル"},
        {"#t1082010#", "#v1082009# #t1082009# \r\n#v4011002# #t4011002# 5個 \r\n#v4031138# 55,000メル"},
        {"#t1082011#", "#v1082009# #t1082009# \r\n#v4011006# #t4011006# 4個 \r\n#v4031138# 60,000メル"},
        {"#t1082060#", "#v1082059# #t1082059# \r\n#v4011002# #t4011002# 3個 \r\n#v4021005# #t4021005# 5個 \r\n#v4031138# 70,000メル"},
        {"#t1082061#", "#v1082059# #t1082059# \r\n#v4021007# #t4021007# 2個 \r\n#v4021008# #t4021008# 2個 \r\n#v4031138# 80,000メル"},
        {"#t4003001#(s)", "#t4000003#es"},
        {"#t4003001#(s)", "#t4000018#s"},
        {"#t4003000#s", "#t4011001#と#t4011000#を各"}
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
        {50},
        {60},
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
        {1, 15}
    };
    // 所持金の増減, アイテムIDと増減数の組 (個数を指定する場合は1個あたり)
    private final int[][] recipe_exchanges = {
        {-1000, 4000021, -15, 4011001, -1, 1082003, 1}, // 1082003 ジュノ
        {-2000, 4011001, -2, 1082000, 1}, // 1082000 鋼鉄ハーフグローブ
        {-5000, 4000021, -40, 4011000, -2, 1082004, 1}, // 1082004 ベノン
        {-10000, 4011001, -2, 1082001, 1}, // 1082001 ハーフグローブ(白P)
        {-20000, 4011000, -3, 4011001, -2, 4003000, -15, 1082007, 1}, // 1082007 ブロンズミシェル
        {-30000, 4011001, -4, 4000021, -30, 4003000, -30, 1082008, 1}, // 1082008 鋼ブリガン
        {-40000, 4011001, -5, 4000021, -50, 4003000, -40, 1082023, 1}, // 1082023 鉄ナックル
        {-50000, 4011001, -3, 4021007, -2, 4000030, -30, 4003000, -45, 1082009, 1}, // 1082009 鋼ブリスト
        {-70000, 4011007, -1, 4011000, -8, 4011006, -2, 4000030, -50, 4003000, -50, 1082059, 1}, // 1082059 ブロンズクランチ
        {-20000, 1082007, -1, 4011001, -1, 1082005, 1}, // 1082005 鋼ミシェル
        {-25000, 1082007, -1, 4011005, -2, 1082006, 1}, // 1082006 オリハルコンミシェル
        {-30000, 1082008, -1, 4021006, -3, 1082035, 1}, // 1082035 ブリガン(黄)
        {-40000, 1082008, -1, 4021008, -1, 1082036, 1}, // 1082036 ブリガン(黒)
        {-45000, 1082023, -1, 4011003, -4, 1082024, 1}, // 1082024 アダマンナックル
        {-50000, 1082023, -1, 4021008, -2, 1082025, 1}, // 1082025 ナックル(黒)
        {-55000, 1082009, -1, 4011002, -5, 1082010, 1}, // 1082010 ミスリルブリスト
        {-60000, 1082009, -1, 4011006, -4, 1082011, 1}, // 1082011 ブリスト(金)
        {-70000, 1082059, -1, 4011002, -3, 4021005, -5, 1082060, 1}, // 1082060 サファイアクランチ
        {-80000, 1082059, -1, 4021007, -2, 4021008, -2, 1082061, 1}, // 1082061 クランチ(黒)
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
            case TYPE_CHAT_MESSAGE3 -> {
                String make_item = texts[0];
                String need_item = texts[1];
                int req_level = values[0];
                text_confirm = "" + make_item + "を作るには、以下の材料が必要だ。制限レベルは" + req_level + "だ。それと、強化中のアイテムを材料に使わないよう気をつけてくれ。どうだい？作るかい？\r\n\r\n#b" + need_item;
                text_answer_no = "材料がないのか？まあいいさ…全部集めてから話しかけてくれ、いいな？待ってるよ…";
                text_fail = "必要なアイテムがすべてそろっているか、装備インベントリがいっぱいでないか、よく確認してくれ。";
                text_ok = "ほら！" + make_item + "だ。俺もミスター・サンダーに負けない腕前だと思わないか？俺の作ったものに大満足するはずだぞ。";
            }
            case TYPE_CHAT_MESSAGE4 -> {
                String make_item = texts[0];
                String need_item = texts[1];
                int req_level = values[0];
                text_confirm = "" + make_item + "を作るには、以下の材料が必要だ。制限レベルは" + req_level + "だ。それと、強化中のアイテムを材料に使わないよう気をつけてくれ。どうだい？作るかい？\r\n\r\n#b" + need_item;
                text_answer_no = "材料がないのか？まあいいさ…全部集めてから話しかけてくれ、いいな？待ってるよ…";
                text_fail = "必要なアイテムがすべてそろっているか、装備インベントリがいっぱいでないか、よく確認してくれ。";
                text_ok = "ほら！" + make_item + "だ。俺もミスター・サンダーに負けない腕前だと思わないか？俺の作ったものに大満足するはずだぞ。";
            }
            case TYPE_CHAT_MESSAGE5 -> {
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
                text_ask = "#b" + need_number + " " + need_item + "#kで、" + item_number + " " + make_item + "を作ってやれるよ。うれしいことに、これは俺のおごりだ。どうだい？いくつほしい？";
                text_confirm = "#b" + make_item + "#k " + ret_num + "回作るかい？それなら#r" + need_num + " " + need_item + "#kが必要だよ。";
                text_answer_no = "材料がないのか？問題ないさ…全部集めてから話しかけてくれ、いいな？待ってるよ…";
                text_fail = "必要なアイテムがすべてそろっているか、ETCインベントリがいっぱいでないか、よく確認してくれ。";
                text_ok = "ほら！どうぞ！" + all_num + " " + make_item + "だ。俺もミスター・サンダーに負けない腕前だと思わないか？俺の作ったものに大満足するはずだぞ。";
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
