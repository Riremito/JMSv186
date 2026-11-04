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
// Npc.wz script : refine_perion (JMS131-194), 会話文は lua_script_jp (refine_perion) を利用
public class NpcMrThunder extends NpcScriptMan {

    // lua の関数
    private final int TYPE_CHAT_MESSAGE10 = 0; // chat_message10
    private final int TYPE_CHAT_MESSAGE11 = 1; // chat_message11
    private final int TYPE_CHAT_MESSAGE2 = 2; // chat_message2
    private final int RECIPE = 1000;
    private final int PHASE_START = 0;
    private final int PHASE_MENU = 1;
    private final int PHASE_MENU_ANSWER = 2;
    private final int PHASE_RECIPE = 3;
    private final int PHASE_NUMBER_ANSWER = 4;
    private final int PHASE_EXECUTE = 5;
    // 翻訳
    private final String text_intro = "待った！宝石や鉱物の原石を持っていないか？少しの手数料で、盾や武器を作るのに必要な材料に変えてやれるぞ。それだけじゃない！アイテムを強化して、もっといい品にすることもできるんだ！どうだい？やってみるか？";
    // 翻訳
    private final String text_intro_no = "そうかい？それは残念だ。必要ないなら、まあ…将来原石をたくさん集めたら、俺のところに来てくれ。俺にしかできない仕事をしてやるよ。";
    // メニュー
    // 翻訳
    private final String[] menu_texts = {
        "よし、原石と少しの手数料があれば、使えるように精錬してやろう。ETCインベントリに空きがあるか確認してくれ。さて…何をしてほしい？\r\n#b#L0# 鉱物の原石を精錬する#l\r\n#L1# 宝石の原石を精錬する#l\r\n#L2# 兜を強化する#l\r\n#L3# 盾を強化する#l",
        "どの鉱物を作りたい？\r\n#b#L0# #t4011000##l\r\n#L1# #t4011001##l\r\n#L2# #t4011002##l\r\n#L3# #t4011003##l\r\n#L4# #t4011004##l\r\n#L5# #t4011005##l\r\n#L6# #t4011006##l",
        "どの宝石を精錬したい？\r\n#b#L0# #t4021000##l\r\n#L1# #t4021001##l\r\n#L2# #t4021002##l\r\n#L3# #t4021003##l\r\n#L4# #t4021004##l\r\n#L5# #t4021005##l\r\n#L6# #t4021006##l\r\n#L7# #t4021007##l\r\n#L8# #t4021008##l",
        "さて…どの兜を強化・製作したい？\r\n#L0##b #t1002041##k(制限レベル: 15, 全職業)#l\r\n#L1##b #t1002042##k(制限レベル: 15, 全職業)#l\r\n#L2##b #t1002002##k(制限レベル: 10, 戦士)#l\r\n#L3##b(制限レベル: 10, 戦士)#l\r\n#L4##b #t1002003##k(制限レベル: 12, 戦士)#l\r\n#L5##b #t1002040##k(制限レベル: 12, 戦士)#l\r\n#L6##b #t1002007##k(制限レベル: 15, 戦士)#l\r\n#L7##b #t1002052##k(制限レベル: 15, 戦士)#l\r\n#L8##b #t1002011##k(制限レベル: 20, 戦士)#l\r\n#L9##b #t1002058##k(制限レベル: 20, 戦士)#l\r\n#L10##b #t1002009##k(制限レベル: 20, 戦士)#l\r\n#L11##b #t1002056##k(制限レベル: 20, 戦士)#l\r\n#L12##b #t1002087##k(制限レベル: 22, 戦士)#l\r\n#L13##b #t1002088##k(制限レベル: 22, 戦士)#l\r\n#L14##b #t1002049##k(制限レベル: 25, 戦士)#l\r\n#L15##b #t1002050##k(制限レベル: 25, 戦士)#l\r\n#L16##b #t1002047##k(制限レベル: 35, 戦士)#l\r\n#L17##b #t1002048##k(制限レベル: 35, 戦士)#l\r\n#L18##b #t1002099##k(制限レベル: 40, 戦士)#l\r\n#L19##b #t1002098##k(制限レベル: 40, 戦士)#l\r\n#L20##b #t1002085##k(制限レベル: 50, 戦士)#l\r\n#L21##b #t1002028##k(制限レベル: 50, 戦士)#l\r\n#L22##b #t1002022##k(制限レベル: 55, 戦士)#l\r\n#L23##b #t1002101##k(制限レベル: 55, 戦士)#l",
        "それで～どの盾を強化・製作したい？\r\n#L0##b #t1092013##k(制限レベル: 40, 戦士)#l\r\n#L1##b #t1092014##k(制限レベル: 40, 戦士)#l\r\n#L2##b #t1092010##k(制限レベル: 60, 戦士)#l\r\n#L3##b #t1092011##k(制限レベル: 60, 戦士)#l"
    };
    // メニューの前の会話
    // 翻訳
    private final String[][] menu_says = {
        {},
        {},
        {},
        {
            "兜を強化したいんだな？よし、わかった。だが一つ忠告しておこう。強化に使うアイテムはすべてなくなるし、すでに書で#r強化#kされたアイテムを使っても、強化したときにその効果は引き継がれない。それを踏まえて決めてくれよ、いいな？"
        },
        {
            "盾を強化したいんだな？よし、わかった。だが一つ忠告しておこう。強化に使うアイテムはすべてなくなるし、すでに書で#r強化#kされたアイテムを使っても、強化したときにその効果は引き継がれない。それを踏まえて決めてくれよ、いいな？"
        }
    };
    // メニューの選択肢 (RECIPE 以上はレシピ番号)
    private final int[][] menu_children = {
        {1, 2, 3, 4},
        {RECIPE + 0, RECIPE + 1, RECIPE + 2, RECIPE + 3, RECIPE + 4, RECIPE + 5, RECIPE + 6},
        {RECIPE + 7, RECIPE + 8, RECIPE + 9, RECIPE + 10, RECIPE + 11, RECIPE + 12, RECIPE + 13, RECIPE + 14, RECIPE + 15},
        {RECIPE + 16, RECIPE + 17, RECIPE + 18, RECIPE + 19, RECIPE + 20, RECIPE + 21, RECIPE + 22, RECIPE + 23, RECIPE + 24, RECIPE + 25, RECIPE + 26, RECIPE + 27, RECIPE + 28, RECIPE + 29, RECIPE + 30, RECIPE + 31, RECIPE + 32, RECIPE + 33, RECIPE + 34, RECIPE + 35, RECIPE + 36, RECIPE + 37, RECIPE + 38, RECIPE + 39},
        {RECIPE + 40, RECIPE + 41, RECIPE + 42, RECIPE + 43}
    };
    // レシピ (lua の関数, index)
    private final int[][] recipes = {
        {TYPE_CHAT_MESSAGE11, 1},
        {TYPE_CHAT_MESSAGE11, 2},
        {TYPE_CHAT_MESSAGE11, 3},
        {TYPE_CHAT_MESSAGE11, 4},
        {TYPE_CHAT_MESSAGE11, 5},
        {TYPE_CHAT_MESSAGE11, 6},
        {TYPE_CHAT_MESSAGE11, 7},
        {TYPE_CHAT_MESSAGE11, 100},
        {TYPE_CHAT_MESSAGE11, 101},
        {TYPE_CHAT_MESSAGE11, 102},
        {TYPE_CHAT_MESSAGE11, 103},
        {TYPE_CHAT_MESSAGE11, 104},
        {TYPE_CHAT_MESSAGE11, 105},
        {TYPE_CHAT_MESSAGE11, 106},
        {TYPE_CHAT_MESSAGE11, 107},
        {TYPE_CHAT_MESSAGE11, 108},
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
        {TYPE_CHAT_MESSAGE2, 15},
        {TYPE_CHAT_MESSAGE2, 16},
        {TYPE_CHAT_MESSAGE2, 17},
        {TYPE_CHAT_MESSAGE2, 18},
        {TYPE_CHAT_MESSAGE2, 19},
        {TYPE_CHAT_MESSAGE2, 20},
        {TYPE_CHAT_MESSAGE2, 21},
        {TYPE_CHAT_MESSAGE2, 22},
        {TYPE_CHAT_MESSAGE10, 100},
        {TYPE_CHAT_MESSAGE10, 101},
        {TYPE_CHAT_MESSAGE10, 1},
        {TYPE_CHAT_MESSAGE10, 2},
        {TYPE_CHAT_MESSAGE10, 3},
        {TYPE_CHAT_MESSAGE10, 4}
    };
    // レシピの文字列の引数
    // 翻訳
    private final String[][] recipe_texts = {
        {"#t4011000#", "#v4010000#", "#t4010000#"},
        {"#t4011001#", "#v4010001#", "#t4010001#"},
        {"#t4011002#", "#v4010002#", "#t4010002#"},
        {"#t4011003#", "#v4010003#", "#t4010003#"},
        {"#t4011004#", "#v4010004#", "#t4010004#"},
        {"#t4011005#", "#v4010005#", "#t4010005#"},
        {"#t4011006#", "#v4010006#", "#t4010006#"},
        {"#t4021000#", "#v4020000#", "#t4020000#"},
        {"#t4021001#", "#v4020001#", "#t4020001#"},
        {"#t4021002#", "#v4020002#", "#t4020002#"},
        {"#t4021003#", "#v4020003#", "#t4020003#"},
        {"#t4021004#", "#v4020004#", "#t4020004#"},
        {"#t4021005#", "#v4020005#", "#t4020005#"},
        {"#t4021006#", "#v4020006#", "#t4020006#"},
        {"#t4021007#", "#v4020007#", "#t4020007#"},
        {"#t4021008#", "#v4020008#", "#t4020008#"},
        {"#t1002041#", "#v1002001# #t1002001# \r\n#v4021006# #t4021006# \r\n#v4031138# 300メル"},
        {"#t1002042#", "#v1002001# #t1002001# \r\n#v4011002# #t4011002# \r\n#v4031138# 500メル"},
        {"#t1002002#", "#v1002043# #t1002043# \r\n#v4011001# #t4011001# \r\n#v4031138# 500メル"},
        {"#t1002044#", "#v1002043# #t1002043# \r\n#v4011002# #t4011002# \r\n#v4031138# 800メル"},
        {"#t1002003#", "#v1002039# #t1002039# \r\n#v4011001# #t4011001# \r\n#v4031138# 500メル"},
        {"#t1002040#", "#v1002039# #t1002039# \r\n#v4011002# #t4011002# \r\n#v4031138# 800メル"},
        {"#t1002007#", "#v1002051# #t1002051# \r\n#v4011001# #t4011001# 2個 \r\n#v4031138# 1,000メル"},
        {"#t1002052#", "#v1002051# #t1002051# \r\n#v4011002# #t4011002# 2個 \r\n#v4031138# 1,500メル"},
        {"#t1002011#", "#v1002059# #t1002059# \r\n#v4011001# #t4011001# 3個 \r\n#v4031138# 1,500メル"},
        {"#t1002058#", "#v1002059# #t1002059# \r\n#v4011002# #t4011002# 3個 \r\n#v4031138# 2,000メル"},
        {"#t1002009#", "#v1002055# #t1002055# \r\n#v4011001# #t4011001# 3個 \r\n#v4031138# 1,500メル"},
        {"#t1002056#", "#v1002055# #t1002055# \r\n#v4011002# #t4011002# 3個 \r\n#v4031138# 2,000メル"},
        {"#t1002087#", "#v1002027# #t1002027# \r\n#v4011002# #t4011002# 4個 \r\n#v4031138# 2,000メル"},
        {"#t1002088#", "#v1002027# #t1002027# \r\n#v4011006# #t4011006# 4個 \r\n#v4031138# 4,000メル"},
        {"#t1002049#", "#v1002005# #t1002005# \r\n#v4011006# #t4011006# 5個 \r\n#v4031138# 4,000メル"},
        {"#t1002050#", "#v1002005# #t1002005# \r\n#v4011005# #t4011005# 5個 \r\n#v4031138# 5,000メル"},
        {"#t1002047#", "#v1002004# #t1002004# \r\n#v4021000# #t4021000# 3個 \r\n#v4031138# 8,000メル"},
        {"#t1002048#", "#v1002004# #t1002004# \r\n#v4021005# #t4021005# 3個 \r\n#v4031138# 10,000メル"},
        {"#t1002099#", "#v1002021# #t1002021# \r\n#v4011002# #t4011002# 5個 \r\n#v4031138# 12,000メル"},
        {"#t1002098#", "#v1002021# #t1002021# \r\n#v4011006# #t4011006# 6個 \r\n#v4031138# 15,000メル"},
        {"#t1002085#", "#v1002086# #t1002086# \r\n#v4011002# #t4011002# 5個 \r\n#v4031138# 20,000メル"},
        {"#t1002028#", "#v1002086# #t1002086# \r\n#v4011004# #t4011004# 4個 \r\n#v4031138# 25,000メル"},
        {"#t1002022#", "#v1002100# #t1002100# \r\n#v4011007# #t4011007# \r\n#v4011001# #t4011001# 7個 \r\n#v4031138# 30,000メル", "DEX +1, MP +30"},
        {"#t1002101#", "#v1002100# #t1002100# \r\n#v4011007# #t4011007# \r\n#v4011002# #t4011002# 7個 \r\n#v4031138# 30000メル", "STR +1, MP +30"},
        {"#t1092013#", "#v1092012# #t1092012# \r\n#v4011002# #t4011002# 10個 \r\n#v4031138# 100,000メル", "STR +2"},
        {"#t1092014#", "#v1092012# #t1092012# \r\n#v4011003# #t4011003# \r\n#v4031138# 100,000メル", "DEX +2"},
        {"#t1092010#", "#v1092009# #t1092009# \r\n#v4011007# #t4011007# \r\n#v4011004# #t4011004# 15個 \r\n#v4031138# 120,000メル", "DEX +2"},
        {"#t1092011#", "#v1092009# #t1092009# \r\n#v4011007# #t4011007# \r\n#v4011003# #t4011003# 15個 \r\n#v4031138# 120,000メル", "STR +2"}
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
        {}
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
        {-300, 1002001, -1, 4021006, -1, 1002041, 1}, // 1002041 メタルギア(黄)
        {-500, 1002001, -1, 4011002, -1, 1002042, 1}, // 1002042 メタルギア(青)
        {-500, 1002043, -1, 4011001, -1, 1002002, 1}, // 1002002 メタルコイプ
        {-800, 1002043, -1, 4011002, -1, 1002044, 1}, // 1002044 ミスリルコイプ
        {-500, 1002039, -1, 4011001, -1, 1002003, 1}, // 1002003 スチールキャップ
        {-800, 1002039, -1, 4011002, -1, 1002040, 1}, // 1002040 ミスリルキャップ
        {-1000, 1002051, -1, 4011001, -2, 1002007, 1}, // 1002007 鋼フルヘルム
        {-1500, 1002051, -1, 4011002, -2, 1002052, 1}, // 1002052 ミスリルプルヘルム
        {-1500, 1002059, -1, 4011001, -3, 1002011, 1}, // 1002011 鉄バイキングヘルム
        {-2000, 1002059, -1, 4011002, -3, 1002058, 1}, // 1002058 ミスリルバイキングヘルム
        {-1500, 1002055, -1, 4011001, -3, 1002009, 1}, // 1002009 鋼ディフェンダーヘルム
        {-2000, 1002055, -1, 4011002, -3, 1002056, 1}, // 1002056 ミスリルディフェンダーH
        {-2000, 1002027, -1, 4011002, -4, 1002087, 1}, // 1002087 ミスリルシャープヘルム
        {-4000, 1002027, -1, 4011006, -4, 1002088, 1}, // 1002088 シャープヘルム(金)
        {-4000, 1002005, -1, 4011006, -5, 1002049, 1}, // 1002049 ゴールドバーガネットヘルム
        {-5000, 1002005, -1, 4011005, -5, 1002050, 1}, // 1002050 オリハルコンバーガネットヘルム
        {-8000, 1002004, -1, 4021000, -3, 1002047, 1}, // 1002047 レッドグレートヘルメット
        {-10000, 1002004, -1, 4021005, -3, 1002048, 1}, // 1002048 ブルーグレートヘルメット
        {-12000, 1002021, -1, 4011002, -5, 1002099, 1}, // 1002099 ミスリルノルマンH
        {-15000, 1002021, -1, 4011006, -6, 1002098, 1}, // 1002098 ゴールドノルマンH
        {-20000, 1002086, -1, 4011002, -5, 1002085, 1}, // 1002085 ミスリルクルセイダーH
        {-25000, 1002086, -1, 4011004, -4, 1002028, 1}, // 1002028 クルセイダーヘルム(銀)
        {-30000, 1002100, -1, 4011007, -1, 4011001, -7, 1002022, 1}, // 1002022 古びた鋼ノルマンヘルム
        {-30000, 1002100, -1, 4011007, -1, 4011002, -7, 1002101, 1}, // 1002101 古びたミスリルノルマンH
        {-100000, 1092012, -1, 4011002, -10, 1092013, 1}, // 1092013 ミスリルタワーシールド
        {-100000, 1092012, -1, 4011003, -10, 1092014, 1}, // 1092014 アダマンタワーシールド
        {-120000, 1092009, -1, 4011007, -1, 4011004, -15, 1092010, 1}, // 1092010 レジェンドS(銀)
        {-120000, 1092009, -1, 4011007, -1, 4011003, -15, 1092011, 1} // 1092011 アダマンレジェンドS
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
            case TYPE_CHAT_MESSAGE10 -> {
                String make_item = texts[0];
                String need_item = texts[1];
                String item_option = texts[2];
                text_confirm = "" + make_item + "を強化するには、以下の材料が必要だ。このアイテムには次のオプションが付く：" + item_option + "。強化中のアイテムを材料に使わないよう気をつけてくれ。どうだい？作るかい？\r\n\r\n#b" + need_item;
                text_answer_no = "そうかい？それは残念だ。用ができたらまた来てくれ。";
                text_fail = "必要なアイテムがすべてそろっているか、ETCインベントリがいっぱいでないか、よく確認してくれ。";
                text_ok = "よう！ほら、" + make_item + "だ。俺の腕はいいだろう…こんなふうに精錬されたアイテムを、ほかのどこかで見たことがあるかい？また来てくれよ～";
            }
            case TYPE_CHAT_MESSAGE11 -> {
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
                text_ask = "" + make_item + "を作るには、以下の材料が必要だ。いくつ作りたい？\r\n\r\n#b" + need_item_icon + " 10 " + need_item_string + "\r\n#v4031138# " + unit_price + " mesos#k";
                text_confirm = "#b" + ret_num + " " + make_item + "(個)#kを作るには、以下の材料が必要だ。本当に作るかい？\r\n\r\n#b" + need_item_icon + " " + all_num + " " + need_item_string + "\r\n#v4031138# " + price + " mesos#k";
                text_answer_no = "ありとあらゆるアイテムがそろっているから、慌てずに買いたいものを選んでくれ…";
                text_fail = "必要なアイテムがすべてそろっているか、ETCインベントリがいっぱいでないか、よく確認してくれ。";
                text_ok = "よう！ほら、" + ret_num + " " + make_item + "だ。思った以上の出来栄えさ…こんなに見事に精錬されたアイテムは、ほかじゃまず見つからないぞ！！また来てくれよ～";
            }
            case TYPE_CHAT_MESSAGE2 -> {
                String make_item = texts[0];
                String need_item = texts[1];
                text_confirm = "" + make_item + "を作るには、以下の材料が必要だ。強化中のアイテムを材料に使わないよう気をつけてくれ。どうだい？作るかい？\r\n\r\n#b" + need_item;
                text_answer_no = "そうかい？それは残念だ。用ができたらまた来てくれ。";
                text_fail = "必要なアイテムがすべてそろっているか、ETCインベントリがいっぱいでないか、よく確認してくれ。";
                text_ok = "よう！ほら、" + make_item + "だ。俺の腕はいいだろう…こんなふうに精錬されたアイテムを、ほかのどこかで見たことがあるかい？また来てくれよ～";
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
