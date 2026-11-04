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
// Npc.wz script : naomi1 (JMS146-302), 会話文は lua_script_jp (naomi1) を利用
public class Npc_Zipangu_Spindle extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_NONE_MENU = 1;
    private static final int NODE_TOP_MENU = 2;
    private static final int NODE_ORB_MENU = 3;
    private static final int NODE_WEAPON_MENU = 4;
    private static final int NODE_WEAPON_ASK = 5;
    private static final int NODE_MANUAL_MENU = 6;
    private static final int NODE_MANUAL_ASK = 7;
    private static final int NODE_SAY_END = 8;
    private static final int MENU_ORB = 0;
    private static final int MENU_FOXWIT = 1;
    private static final int MENU_BARRICADE = 2;
    private final int item_orb = 4031761; // マテリアルオーブ
    // マテリアルオーブで作る武器
    private final int[] weapons = {
        1302079, // アストラルブレード
        1332064, // 星雲の短剣（ゴールド)
        1332065, // 星雲の短剣（赤)
        1382053, // アストロッド
        1412032, // レッドクレセント
        1432045, // サンスピア
        1442060, // ヘブンリーハルベルト
        1452052, // アンドロメダ
        1462046, // ボイドハンター
        1472062, // ブラックホール
        1322059 // スペースホール
    };
    // 製法 (フォックスウィット)
    private final int[] manuals_foxwit = {
        4031826, // ジェットマント製法
        4031827, // シリウスマント製法
        4031828, // デーモンハートイヤリング製法
        4031829 // ブラックデザートシールド製法
    };
    // 製法 (ジョン・バリケード)
    private final int[] manuals_barricade = {
        4031822, // 永遠のサクリット製法
        4031823, // アンテリオンの冠製法
        4031824, // ギャラクシーグローブ製法
        4031825, // クリスタルリーフイヤリング製法
        4031907, // トラの刀爪製法書
        4031908, // ネーバの製法書
        4031909, // ヴィンケル製法書
        4031910, // グリッターグローブ製法書
        4031911, // 邪悪な戦士の靴製法書
        4031912 // クリスタルの手裏剣製法書
    };
    // 製法で作るアイテム (フォックスウィット)
    private final int[] results_foxwit = {
        1102146, // ジェットマント
        1102145, // シリウスマント
        1032049, // デーモンハートイヤリング
        1092052 // ブラックデザートシールド
    };
    // 製法で作るアイテム (ジョン・バリケード)
    private final int[] results_barricade = {
        1002676, // 永遠のサクリット
        1002675, // アンテリオンの冠
        1082223, // ギャラクシーグローブ
        1032048, // クリスタルリーフイヤリング
        1402045, // トラの爪刀
        1472064, // ネーバ
        1452053, // ヴィンケル
        1082230, // グリッターグローブ
        1072344, // 邪悪な戦士の靴
        2070016 // 水晶の手裏剣
    };
    // 製法の交換 (lua の chat_message2 の index 順)
    private final int[][] manual_exchanges = {
        {4004000, -5, 4010006, -5, 4021004, -5, 4031826, -1, 1102146, 1},
        {4011006, -5, 4021005, -5, 4021007, -2, 4031827, -1, 1102145, 1},
        {4020008, -10, 4004000, -10, 4031828, -1, 1032049, 1},
        {4011002, -10, 4021005, -2, 4004004, -1, 4031829, -1, 1092052, 1},
        {4011004, -5, 4005000, -1, 4005001, -1, 4031822, -1, 1002676, 1},
        {4011006, -5, 4011001, -5, 4003000, -20, 4031823, -1, 1002675, 1},
        {4005000, -2, 4000021, -15, 4031824, -1, 1082223, 1},
        {4021007, -2, 4031825, -1, 1032048, 1},
        {4031907, -1, 1402003, -1, 4021008, -6, 4005000, -6, 1402045, 1},
        {4031908, -1, 1472007, -1, 4021008, -6, 4005003, -6, 1472064, 1},
        {4031909, -1, 4131010, -1, 4003000, -50, 4021008, -6, 4005002, -6, 1452053, 1},
        {4031910, -1, 1082002, -1, 4021007, -6, 4005001, -6, 1082230, 1},
        {4031911, -1, 4011001, -50, 4003000, -25, 4000021, -25, 4000030, -50, 1072344, 1},
        {4031912, -1, 2070006, -1, 4005003, -7, 4005004, -1, 2070016, 1}
    };
    // 作るアイテムの表示 (lua の chat_message2 の index 順)
    private final String[] manual_make_texts = {
        "#v1102146# : #t1102146#",
        "#v1102145# : #t1102145#",
        "#v1032049# : #t1032049#",
        "#v1092052# : #t1092052#",
        "#v1002676# : #t1002676#",
        "#v1002675# : #t1002675#",
        "#v1082223# : #t1082223#",
        "#v1032048# : #t1032048#",
        "#v1402045# : #t1402045#",
        "#v1472064# : #t1472064#",
        "#v1452053# : #t1452053#",
        "#v1082230# : #t1082230#",
        "#v1072344# : #t1072344#",
        "#v2070016# : #t2070016#"
    };
    // 必要なアイテムの表示 (lua の chat_message2 の index 順)
    private final String[] manual_need_texts = {
        "#v4004000:# 5 #t4004000#s\r\n#v4010006:# 5 #t4010006#s\r\n#v4021004:# 5 #t4021004#s",
        "#v4011006:# 5 #t4011006#s\r\n#v4021005:# 5 #t4021005#s\r\n#v4021007:# 2 #t4021007#s",
        "#v4020008:# 10 #t4020008#s\r\n#v4004000:# 10 #t4004000#s",
        "#v4011002:# 10 #t4011002#s\r\n#v4021005:# 2 #t4021005#s\r\n#v4004004:# 1 #t4004004#",
        "#v4011004:# 5 #t4011004#s\r\n#v4005000:# 1 #t4005000#\r\n#v4005001:# 1 #t4005001#",
        "#v4011006:# 5 #t4011006#s\r\n#v4011001:# 5 #t4011001#s\r\n#v4003000:# 20 #t4003000#s",
        "#v4005000:# 2 #t4005000#s\r\n#v4000021:# 15 #t4000021#s",
        "#v4021007:# 2 #t4021007#s",
        "#v4031907:# 1 #t4031907#s \r\n#v1402003:# 1 #t1402003# \r\n#v4021008:# 6 #t4021008# \r\n#v4005000:# 6 #t4005000#",
        "#v4031908:# 1 #t4031908#s \r\n#v1472007:# 1 #t1472007# \r\n#v4021008:# 6 #t4021008# \r\n#v4005003:# 6 #t4005003#",
        "#v4031909:# 1 #t4031909#s \r\n#v4131010:# 1 #t4131010# \r\n#v4003000:# 50 #t4003000# \r\n#v4021008:# 6 #t4021008# \r\n#v4005002:# 6 #t4005002#",
        "#v4021007:# 6 #t4021007#s \r\n#v4031910:# 1 #t4031910# \r\n#v1082002:# 1 #t1082002# \r\n#v4005001:# 6 #t4005001#",
        "#v4031911:# 1 #t4031911#s \r\n#v4011001:# 50 #t4011001# \r\n#v4003000:# 25 #t4003000# \r\n#v4000021:# 25 #t4000021#  \r\n#v4000030:# 50 #t4000030# ",
        "#v4031912:# 1 #t4031912#s \r\n#v2070006:# 800 #t2070006# \r\n#v4005003:# 7 #t4005003# \r\n#v4005004:# 1 #t4005004#"
    };
    // 武器の表示 (lua の chat_message1 の index 順)
    private final String[] weapon_make_texts = {
        "#t1302079#: #v1302079:#",
        "#t1332064#: #v1332064:#",
        "#t1332065#: #v1332065:#",
        "#t1382053#: #v1382053:#",
        "#t1412032#: #v1412032:#",
        "#t1432045#: #v1432045:#",
        "#t1442060#: #v1442060:#",
        "#t1452052#: #v1452052:#",
        "#t1462046#: #v1462046:#",
        "#t1472062#: #v1472062:#",
        "#t1322059#: #v1322059:#"
    };
    private int node = NODE_START;
    private int last_status = 0;
    private String text = "";
    private int menu = 0;
    private int index = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status < status;
        this.last_status = status;
        boolean has_orb = 0 < getPlayer().getItemQuantity(this.item_orb, false);
        boolean[] has_foxwit = new boolean[this.manuals_foxwit.length];
        boolean has_foxwit_any = false;
        for (int i = 0; i < this.manuals_foxwit.length; i++) {
            has_foxwit[i] = 0 < getPlayer().getItemQuantity(this.manuals_foxwit[i], false);
            has_foxwit_any |= has_foxwit[i];
        }
        boolean[] has_barricade = new boolean[this.manuals_barricade.length];
        boolean has_barricade_any = false;
        for (int i = 0; i < this.manuals_barricade.length; i++) {
            has_barricade[i] = 0 < getPlayer().getItemQuantity(this.manuals_barricade[i], false);
            has_barricade_any |= has_barricade[i];
        }
        // 前の会話の応答の処理
        switch (this.node) {
            case NODE_START -> {
                if (!has_orb && !has_foxwit_any && !has_barricade_any) {
                    this.node = NODE_NONE_MENU;
                    break;
                }
                this.node = NODE_TOP_MENU;
            }
            case NODE_NONE_MENU -> {
                // 翻訳
                this.text = "ジョン・バリケードからもフォックスウィット教授からも何ももらっていないみたいだな。機会があったら、メイプルストーリー トレーディングカードゲームを見てみな…思っている以上にいろいろ入ってるぞ！じゃあな！";
                this.node = NODE_SAY_END;
            }
            case NODE_TOP_MENU -> {
                this.menu = answer;
                switch (this.menu) {
                    case MENU_ORB -> {
                        this.node = NODE_ORB_MENU;
                    }
                    case MENU_FOXWIT, MENU_BARRICADE -> {
                        this.node = NODE_MANUAL_MENU;
                    }
                    default -> {
                        return false;
                    }
                }
            }
            case NODE_ORB_MENU -> {
                if (answer != 0) {
                    // 翻訳
                    this.text = "まさか本当にそう言う奴がいるとはな。みんなお前みたいだったらいいのに！";
                    this.node = NODE_SAY_END;
                    break;
                }
                this.node = NODE_WEAPON_MENU;
            }
            case NODE_WEAPON_MENU -> {
                if (answer < 0 || this.weapons.length <= answer) {
                    return false;
                }
                this.index = answer;
                this.node = NODE_WEAPON_ASK;
            }
            case NODE_WEAPON_ASK -> {
                if (!ok) {
                    // 翻訳
                    this.text = "大きな決断だよな、わかるよ。ゆっくり考えてくれ！準備ができたら戻ってきな。<ぶつぶつ> 戻ってこなくてもいいけどな…";
                    this.node = NODE_SAY_END;
                    break;
                }
                String make_item = this.weapon_make_texts[this.index];
                if (!exchange(0, this.item_orb, -1, this.weapons[this.index], 1)) {
                    // 翻訳
                    this.text = "必要なアイテムがすべてそろっているか、ETCインベントリがいっぱいでないか確認してくれ。";
                    this.node = NODE_SAY_END;
                    break;
                }
                // 翻訳
                this.text = "ほら、この" + make_item + "を持っていきな。さあ、できたぞ！見るものすべてを傷つけたりするんじゃないぞ！";
                this.node = NODE_SAY_END;
            }
            case NODE_MANUAL_MENU -> {
                if (this.menu == MENU_FOXWIT) {
                    if (answer < 0 || this.manuals_foxwit.length <= answer || !has_foxwit[answer]) {
                        return false;
                    }
                    this.index = answer;
                } else {
                    if (answer < 0 || this.manuals_barricade.length <= answer || !has_barricade[answer]) {
                        return false;
                    }
                    this.index = this.manuals_foxwit.length + answer;
                }
                this.node = NODE_MANUAL_ASK;
            }
            case NODE_MANUAL_ASK -> {
                if (!ok) {
                    // 翻訳
                    this.text = "大きな決断だよな、わかるよ。ゆっくり考えてくれ！準備ができたら戻ってきな。";
                    this.node = NODE_SAY_END;
                    break;
                }
                String make_item = this.manual_make_texts[this.index];
                if (!exchange(0, this.manual_exchanges[this.index])) {
                    // 翻訳
                    this.text = "必要なアイテムがすべてそろっているか、ETCインベントリがいっぱいでないか確認してくれ。";
                    this.node = NODE_SAY_END;
                    break;
                }
                // 翻訳
                this.text = "うーん…よし、そのマニュアルと材料を渡してくれるか？<マニュアルを熟読する> これなら簡単にわかるな。よし、道具を取ってくるか…<カン、カン、ウィーン、ウィーン、ガチャ、ガチャ>  ほら、"  + make_item + "を持っていきな。幸運を祈るぜ！";
                this.node = NODE_SAY_END;
            }
            default -> {
                return false;
            }
        }
        // 次の会話の表示
        switch (this.node) {
            case NODE_NONE_MENU -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("よう。俺の名前はスピンドル。壊れたものを直すのも、壊れないものを作るのも得意なんだ。何か用か？ないなら、ちょっと忙しいんだが…\r\n#b#L0#特に何も。あなたが誰なのか気になっただけ。#l", false);
                askMenu(nt);
                return true;
            }
            case NODE_TOP_MENU -> {
                String list = "";
                if (has_orb) {
                    list += "\r\n#b#L0##b#t4031761#っていうのを持ってるんだけど#l#k";
                }
                if (has_foxwit_any) {
                    list += "\r\n#b#L1#フォックスウィットにマニュアルをもらって、あなたなら作ってくれるって言われたんだ。#l#k";
                }
                if (has_barricade_any) {
                    list += "\r\n#b#L2#ジョン・バリケードにマニュアルをもらって、あなたなら作ってくれるって言われたんだ。#l#k";
                }
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("よう。俺の名前はスピンドル。壊れたものを直すのも、壊れないものを作るのも得意なんだ。何か用か？ないなら、ちょっと忙しいんだが…" + list, false);
                askMenu(nt);
                return true;
            }
            case NODE_ORB_MENU -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#b#t4031761##kだって？これを作れるのは一人しかいない…ってことは、グリマーマンと話をしたんだな。<顔をしかめる> 何がほしいのかも想像がつくよ。あいつの助けにはやっぱり裏があったか！まあ、あのイカれた魔法使いに「くだらん！」って俺が言ってたと伝えてくれ。とにかく、このオーブを異次元の破壊兵器に変えてほしいんだろう。\r\n#b#L0#はい、お願いします#l\r\n#L1#異次元の破壊兵器？うーん、平和にいこうかな。#l#k", false);
                askMenu(nt);
                return true;
            }
            case NODE_WEAPON_MENU -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("まったく…ふん。あいつには借りがあるからやってやるが、あのイカれたじいさんに、そこらへんの危なそうな奴に誰彼かまわずマテリアオーブを配るなって言っといてくれ！悪く思うなよ。まあ、あいつが武器商人なのはわかってるが、こんなものが悪い奴の手に渡ったら…まあ、今さら俺の知ったことじゃないか。とにかく、マテリアオーブはどんな形にでも作り変えられる。グリマーからは武器のデザインをいくつかもらっていて、どれもすごいもんで、簡単に体を傷つけられるぞ。どんな形の武器にしたい？\r\n#b#L0#- 片手剣#l\r\n#L1#- LUK短剣#l\r\n#L2#- STR短剣#l\r\n#L3#- スタッフ#l\r\n#L4#- 両手斧#l\r\n#L5#- 槍#l\r\n#L6#- 矛#l\r\n#L7#- 弓#l\r\n#L8#- クロスボウ#l\r\n#L9#- アーム#l\r\n#L10#- 片手鈍器#l", false);
                askMenu(nt);
                return true;
            }
            case NODE_WEAPON_ASK -> {
                String make_item = this.weapon_make_texts[this.index];
                // 翻訳
                askYesNo("本当に" + make_item + "がほしいのか？悪い奴の手に渡ったら、ものすごく危険な代物だ。本当に作るのか？");
                return true;
            }
            case NODE_MANUAL_MENU -> {
                String list = "";
                if (this.menu == MENU_FOXWIT) {
                    for (int i = 0; i < this.manuals_foxwit.length; i++) {
                        if (has_foxwit[i]) {
                            list += "\r\n#b#L" + i + "#- #t" + this.results_foxwit[i] + "##k#l";
                        }
                    }
                    // 翻訳
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("あいつがそう言ったのか？  よし、すぐに取りかかるぞ！  今日は何を作る？" + list, false);
                    askMenu(nt);
                    return true;
                }
                for (int i = 0; i < this.manuals_barricade.length; i++) {
                    if (has_barricade[i]) {
                        list += "\r\n#b#L" + i + "#- #t" + this.results_barricade[i] + "##k#l";
                    }
                }
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("バリケードに言われて来たのか？  うーん…古代の技術を扱うのは嫌いなんだ。あの神秘的なまじないみたいなやつがな。  ちゃんと動くのに理由がわからないと、ぞっとするんだよ。  でも、ジョンはいい奴だし、いくつか借りもあるからやってやるよ。  俺が何を作れるって言ってた？" + list, false);
                askMenu(nt);
                return true;
            }
            case NODE_MANUAL_ASK -> {
                String make_item = this.manual_make_texts[this.index];
                String need_item = this.manual_need_texts[this.index];
                // 翻訳
                askYesNo("本当に" + make_item + "がほしいのか？\n マニュアルを見せてくれ。<マニュアルにざっと目を通す> どうやら、以下の追加アイテムが必要みたいだな。作るのは" + make_item + "\n 本当に作るのか？\r\n\r\n#b" + need_item);
                return true;
            }
            case NODE_SAY_END -> {
                say(this.text);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
