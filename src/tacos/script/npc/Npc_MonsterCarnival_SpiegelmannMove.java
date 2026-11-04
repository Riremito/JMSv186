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

import odin.server.quest.MapleQuest;

/**
 *
 * @author Riremito
 */
// Npc.wz script : mc_move (JMS131-302), 会話文は lua_script_jp (mc_move) を利用
public class Npc_MonsterCarnival_SpiegelmannMove extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_MENU = 1;
    private static final int NODE_DESC_MENU = 2;
    private static final int NODE_DESC_SAYS = 3;
    private static final int NODE_REWARD = 4;
    private static final int NODE_WEAPON = 5;
    private static final int NODE_RESULT = 6;
    private static final int WEAPON_WARRIOR1 = 0;
    private static final int WEAPON_WARRIOR2 = 1;
    private static final int WEAPON_MAGICIAN = 2;
    private static final int WEAPON_ARCHER = 3;
    private static final int WEAPON_THIEF = 4;
    private final int map_kerning = 103000000; // カニングシティー : カニングシティー
    private final int map_orbis = 200000000; // オルビス : オルビス
    private final int map_ludi = 220000000; // ルディブリアム : ルディブリアム
    private final int map_entrance = 980000000; // モンスターカーニバル : モンスターカーニバル入口
    private final int map_exit = 980000010; // モンスターカーニバル : モンスターカーニバル出口
    private final int[] maps_win = {980000103, 980000203, 980000303, 980000403, 980000503, 980000603}; // カーニバルフィールド<勝者ルーム>
    private final int[] maps_lose = {980000104, 980000204, 980000304, 980000404, 980000504, 980000604}; // カーニバルフィールド<敗者ルーム>
    private final int quest_return = 7050; // モンスターカーニバルの帰還先
    private final int quest_cp_mine = 7051; // 自分のパーティーの CP
    private final int quest_cp_enemy = 7052; // 相手のパーティーの CP
    private final int quest_time = 7053; // カーニバルの経過時間
    private final int item_coin = 4001129; // メイプルコイン
    private final int item_necklace = 1122007; // シュピゲルマンのネックレス
    private final int item_egg = 2041211; // シュピゲルマンの卵
    private final int cost_necklace = 50;
    private final int cost_egg = 40;
    private final int cost_low = 7;
    private final int cost_middle = 10;
    private final int cost_high = 20;
    // {アイテム, メイプルコイン}
    private final int[][][] weapons = {
        // 戦士用の武器 (1/2)
        {
            {1302004, cost_low}, // カトルロス
            {1402006, cost_low}, // ハイランダ
            {1302009, cost_middle}, // トラウス
            {1402007, cost_middle}, // ジャド
            {1302010, cost_high}, // ジュエルクアダラ
            {1402003, cost_high}, // 虎剣
            {1312006, cost_low}, // ダンカ
            {1412004, cost_low}, // ニアム
            {1312007, cost_middle}, // ブルーカウンター
            {1412005, cost_middle}, // バードビル
            {1312008, cost_high}, // バック
            {1412003, cost_high}, // ライジング
        },
        // 戦士用の武器 (2/2)
        {
            {1322015, cost_low}, // ホスマンズ
            {1422008, cost_low}, // ビッグハンマー
            {1322016, cost_middle}, // ゼックカ
            {1422007, cost_middle}, // タイタン
            {1322017, cost_high}, // ナックルメイス
            {1422005, cost_high}, // ゴールデンモウル
            {1432003, cost_low}, // ナカマキ
            {1442003, cost_low}, // 両刃長柄戦斧
            {1432005, cost_middle}, // ゼコ
            {1442009, cost_middle}, // クレセント
            {1442005, cost_high}, // 九龍刀
            {1432004, cost_high}, // 蛇矛
        },
        // 魔法使い用の武器
        {
            {1372001, cost_low}, // ウィザードワンド
            {1382018, cost_low}, // プリクルスタッフ
            {1372012, cost_middle}, // クリスタルワンド
            {1382019, cost_middle}, // サークルフレイムスタッフ
            {1382001, cost_high}, // アークスタッフ
            {1372007, cost_high}, // クロミ
        },
        // 弓使い用の武器
        {
            {1452006, cost_low}, // レッドバイパー
            {1452007, cost_middle}, // バルター2000
            {1452008, cost_high}, // オリムポス
            {1462005, cost_low}, // ヘクラー
            {1462006, cost_middle}, // シルバークロウ
            {1462007, cost_high}, // ローア
        },
        // 盗賊用の武器
        {
            {1472013, cost_low}, // ダークガーディアン
            {1472017, cost_middle}, // ダークボニン
            {1472021, cost_high}, // ダークスレイン
            {1332014, cost_low}, // ゲパート
            {1332031, cost_middle}, // ドラゴンネイル
            {1332011, cost_middle}, // ベズルロード
            {1332016, cost_high}, // サイ
            {1332003, cost_high}, // シンギター
        },
    };
    // 翻訳
    private final String[] weapon_texts = {
        "ほしい武器に必要なメイプルコインが十分あるか確認してくれ。メイプルコインと交換したい武器を選んでくれ。自分で言うのもなんだが、品ぞろえはなかなかのものだぞ！ ",
        "交換したい武器を選んでくれ。ここにある武器はどれもとても役に立つぞ。見てごらん！ ",
        "交換したい武器を選んでくれ。ここにある武器はどれもとても魅力的だぞ。自分の目で確かめてくれ！",
        "交換したい武器を選んでくれ。ここにある武器はどれも実に魅力的だぞ。自分の目で確かめてくれ！",
        "交換したい武器を選んでくれ。ここにある武器はどれも最高品質だ。気に入ったものを選んでくれ！",
    };
    private int node = NODE_START;
    private int weapon = WEAPON_WARRIOR1;
    private String[] says = null;
    private int says_index = 0;

    @Override
    public boolean action(int status, int answer) {
        int map_id = getMapId();
        switch (this.node) {
            case NODE_START -> {
                if (map_id == this.map_kerning || map_id == this.map_orbis || map_id == this.map_ludi) {
                    NpcScriptTag nt = new NpcScriptTag();
                    // 翻訳
                    nt.add("何をしたい？モンスターカーニバルに参加したことがないなら、参加する前に少し知っておいた方がいいぞ。\r\n#b#L0# モンスターカーニバルのフィールドへ行く。#l\r\n#L1# モンスターカーニバルについて知る。#l\r\n#L2# #t" + this.item_coin + "#を交換する。#l", false);
                    askMenu(nt);
                    this.node = NODE_MENU;
                    return true;
                }
                if (map_id == this.map_exit) {
                    // 翻訳
                    say("今すぐここから出してあげよう。", false, true);
                    this.node = NODE_RESULT;
                    return true;
                }
                boolean is_win = false;
                boolean is_lose = false;
                for (int i = 0; i < this.maps_win.length; i++) {
                    is_win |= map_id == this.maps_win[i];
                    is_lose |= map_id == this.maps_lose[i];
                }
                if (!is_win && !is_lose) {
                    return false;
                }
                int cp_mine = 0;
                int time = 0;
                try {
                    cp_mine = Integer.parseInt(getQuestData(this.quest_cp_mine));
                } catch (NumberFormatException e) {
                }
                try {
                    time = Integer.parseInt(getQuestData(this.quest_time));
                } catch (NumberFormatException e) {
                }
                int exp;
                String rank;
                if (cp_mine <= 50) {
                    exp = is_win ? 3000 : 1000;
                    rank = "D";
                } else if (cp_mine <= 250) {
                    exp = is_win ? 21000 : 7000;
                    rank = "C";
                } else if (cp_mine <= 500) {
                    exp = is_win ? 25500 : 8500;
                    rank = "B";
                } else {
                    exp = is_win ? 30000 : 10000;
                    rank = "A";
                }
                if (time < 130) {
                    exp = 0;
                } else if (time < 310) {
                    exp = (exp / 10) * 3;
                } else if (time < 490) {
                    exp = (exp / 10) * 5;
                }
                // 翻訳
                String text;
                if (is_win) {
                    switch (rank) {
                        case "D" ->
                            text = "勝利おめでとう。ただ、活躍ぶりはそれほどでもなかったな。次にモンスターカーニバルに参加するときは、もっと積極的にな！";
                        case "C" ->
                            text = "勝利おめでとう。あちこちで頑張ってはいたが、大勝利とは言えないな。次はもっと期待しているぞ。";
                        case "B" ->
                            text = "勝利おめでとう！実に見事だった！相手パーティーをかなり痛めつけたな！あと少しで、次はきっとAが取れるぞ。 ";
                        default ->
                            text = "勝利おめでとう！！！素晴らしい活躍だった！相手パーティーは手も足も出なかったな！次も同じ活躍を期待しているぞ！";
                    }
                } else {
                    switch (rank) {
                        case "D" ->
                            text = "残念ながら引き分けか負けてしまったな。活躍ぶりにもはっきり表れていた。次はもっと期待しているぞ。";
                        case "C" ->
                            text = "残念ながら引き分けか負けてしまったな。勝利は努力した者に訪れる。君の努力は見えているから、勝利はそう遠くないはずだ。頑張り続けてくれ！";
                        case "B" ->
                            text = "素晴らしい活躍だったのに、残念ながら引き分けか負けてしまったな。あと少しで、勝利は君のものだったはずだ。";
                        default ->
                            text = "見事な活躍だったのに、残念ながら引き分けか負けてしまったな。次こそは勝利が君のものになるはずだ。";
                    }
                }
                say(text + "\r\n\r\n#bモンスターカーニバルのランク :" + rank, false, true);
                MapleQuest.getInstance(this.quest_cp_mine).forceComplete(getPlayer(), getNpcId());
                MapleQuest.getInstance(this.quest_cp_enemy).forceComplete(getPlayer(), getNpcId());
                MapleQuest.getInstance(this.quest_time).forceComplete(getPlayer(), getNpcId());
                if (exp != 0) {
                    getPlayer().gainExp(exp, true, true, true);
                }
                this.node = NODE_RESULT;
                return true;
            }
            case NODE_RESULT -> {
                warp(this.map_entrance);
                return false;
            }
            case NODE_MENU -> {
                switch (answer) {
                    case 0 -> {
                        int level = getPlayer().getLevel();
                        if (level < 30 || 50 < level) {
                            // 翻訳
                            say("悪いが、モンスターカーニバルに参加できるのはレベル30～50のユーザーだけだ。");
                            return false;
                        }
                        String code = (map_id == this.map_kerning) ? "0" : (map_id == this.map_orbis) ? "1" : "2";
                        setQuestData(this.quest_return, code);
                        warp(this.map_entrance, "st00");
                        return false;
                    }
                    case 1 -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        // 翻訳
                        nt.add("何をしたい？\r\n#b#L0# モンスターカーニバルって何？#l\r\n#L1# モンスターカーニバルの概要#l\r\n#L2# モンスターカーニバルの詳しい説明#l\r\n#L3# 特に何も。気が変わった。#l", false);
                        askMenu(nt);
                        this.node = NODE_DESC_MENU;
                        return true;
                    }
                    case 2 -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        // 翻訳
                        nt.add("メイプルコインを持っていれば、アイテムと交換できることを忘れるな。ほしいアイテムに必要なメイプルコインが十分あるか確認してくれ。交換したいアイテムを選んでくれ！ \r\n#b#L0# #t" + this.item_necklace + "#(" + this.cost_necklace + "コイン)#l\r\n#L1# #t" + this.item_egg + "#(" + this.cost_egg + "コイン)#l\r\n#L2# 戦士用の武器#l\r\n#L3# 魔法使い用の武器#l\r\n#L4# 弓使い用の武器#l\r\n#L5# 盗賊用の武器#l", false);
                        askMenu(nt);
                        this.node = NODE_REWARD;
                        return true;
                    }
                    default -> {
                        return false;
                    }
                }
            }
            case NODE_DESC_MENU -> {
                // 翻訳
                switch (answer) {
                    case 0 -> {
                        this.says = new String[]{
                            "はは！私はスピーゲルマン、この旅回りのカーニバルの団長だ。ここで史上初の#bモンスターカーニバル#kを始めて、君のような旅人がこの大イベントに参加してくれるのを待っていたんだ！",
                            "#bモンスターカーニバル#kとは何かって？ははは！決して忘れられない体験になると言っておこう！それは#b君のようなほかの旅人たちとの戦い！#kなのさ！",
                            "本物の武器で互いに戦うのは危険すぎるし、そんな野蛮なことをすすめるつもりもない。いや、友よ、私が提供するのは競争だ。戦いのスリル、そして君と同じくらい強くてやる気のある相手と競い合う興奮さ。君のパーティーと相手パーティーが両方とも#bモンスターを召喚して、相手パーティーが召喚したモンスターを倒すんだ。それがモンスターカーニバルの本質さ。それに、モンスターカーニバルで手に入れたメイプルコインで、新しいアイテムや武器を手に入れられるぞ！#k",
                            "もちろん、そんなに単純な話じゃない。相手パーティーのモンスター狩りを妨害する方法はいろいろあって、それをどう使うかは君次第だ。どうだい？ちょっとした友好的な（あるいはそうでもない）競争に興味はないか？",
                        };
                    }
                    case 1 -> {
                        this.says = new String[]{
                            "#bモンスターカーニバル#kは、2つのパーティーが戦場に入り、相手パーティーが召喚したモンスターを狩るものだ。#b獲得したカーニバルポイント（CP）の量で勝者が決まる戦闘クエスト#kなんだ。",
                            "カーニバルフィールドに入ったら、やるべきことは#b相手パーティーのモンスターを狩ってCPを稼ぎ、そのCPを使って相手パーティーのモンスター狩りを妨害すること#kだ。",
                            "相手パーティーを妨害する方法は3つ。#bモンスター召喚、スキル、守護者#kだ。もっと知りたければ「詳しい説明」で詳しく教えよう。",
                            "ただし、これは覚えておいてくれ。CPをただ貯め込むのは決していい考えじゃない。#b使ったCPもカーニバルの勝敗を決める要素になる#kんだ。",
                        };
                    }
                    case 2 -> {
                        this.says = new String[]{
                            "カーニバルフィールドに入ると、モンスターカーニバルのウィンドウが表示される。#b使いたいものを選んでOKを押す#kだけでいい。簡単だろう？",
                            "コマンドに慣れてきたら、#bショートカットキーのTABとF1～F12#kを使ってみるといい。#bTABでモンスター召喚/スキル/守護者を切り替え#k、#bF1～F12で各ウィンドウに直接入れる#kんだ。",
                            "#bモンスター召喚#kは、自分の思いどおりに相手パーティーを攻撃するモンスターを呼び出す。CPを使って召喚モンスターを呼び出すと、同じエリアに現れて相手パーティーを攻撃する。",
                            "#bスキル#kは、暗闇や弱体などのスキルを使って、相手パーティーがモンスターを倒せないようにする選択肢だ。CPはたくさん必要だが、それだけの価値はある。ただ問題は、効果があまり長続きしないことだ。この戦術はうまく使ってくれ！",
                            "#b守護者#kは、基本的に自分のパーティーが召喚したモンスターの能力を大幅に高める召喚アイテムだ。守護者は相手パーティーに壊されない限り効果が続くから、まずモンスターをたくさん召喚してから守護者を出すことをすすめる。",
                            "最後に、モンスターカーニバルの間は#b持っている回復アイテムやポーションは使えない#k。でも、モンスターがときどきそういったアイテムを落として、#b拾った瞬間にすぐ効果が発動する#kんだ。だから、いつアイテムを拾うかを見極めることも同じくらい大切なのさ。",
                        };
                    }
                    default -> {
                        return false;
                    }
                }
                this.says_index = 0;
                say(this.says[this.says_index], false, true);
                this.node = NODE_DESC_SAYS;
                return true;
            }
            case NODE_DESC_SAYS -> {
                // lua では説明の後に説明のメニューに戻る
                this.says_index++;
                if (this.says_index < this.says.length) {
                    say(this.says[this.says_index], false, true);
                    return true;
                }
                NpcScriptTag nt = new NpcScriptTag();
                // 翻訳
                nt.add("何をしたい？\r\n#b#L0# モンスターカーニバルって何？#l\r\n#L1# モンスターカーニバルの概要#l\r\n#L2# モンスターカーニバルの詳しい説明#l\r\n#L3# 特に何も。気が変わった。#l", false);
                askMenu(nt);
                this.node = NODE_DESC_MENU;
                return true;
            }
            case NODE_REWARD -> {
                switch (answer) {
                    case 0 -> {
                        // TODO : lua の exchangeEx の Variation:2 (能力値の変動) は未実装
                        if (!exchange(0, this.item_coin, -this.cost_necklace, this.item_necklace, 1)) {
                            // 翻訳
                            say("#b#t" + this.item_coin + "##kが足りないか、装備インベントリがいっぱいでないか確認してくれ。");
                        }
                        return false;
                    }
                    case 1 -> {
                        if (!exchange(0, this.item_coin, -this.cost_egg, this.item_egg, 1)) {
                            // 翻訳
                            say("#b#t" + this.item_coin + "##kが足りないか、消費インベントリがいっぱいでないか確認してくれ。");
                        }
                        return false;
                    }
                    case 2, 3, 4, 5 -> {
                        this.weapon = (answer == 2) ? WEAPON_WARRIOR1 : (answer == 3) ? WEAPON_MAGICIAN : (answer == 4) ? WEAPON_ARCHER : WEAPON_THIEF;
                    }
                    default -> {
                        return false;
                    }
                }
                // lua の魔法使い・弓使い・盗賊の最後の選択肢は「コイン」が「 coins」になっている
                StringBuilder sb = new StringBuilder();
                int[][] list = this.weapons[this.weapon];
                for (int i = 0; i < list.length; i++) {
                    sb.append("\r\n").append(i == 0 ? "#b" : "").append("#L").append(i).append("# #z").append(list[i][0]).append("#(").append(list[i][1]).append("コイン)#l");
                }
                if (this.weapon == WEAPON_WARRIOR1) {
                    // 翻訳
                    sb.append("\r\n#L12# 次のページへ(1/2)#l");
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(this.weapon_texts[this.weapon] + sb.toString(), false);
                askMenu(nt);
                this.node = NODE_WEAPON;
                return true;
            }
            case NODE_WEAPON -> {
                int[][] list = this.weapons[this.weapon];
                if (answer == 12 && (this.weapon == WEAPON_WARRIOR1 || this.weapon == WEAPON_WARRIOR2)) {
                    this.weapon = (this.weapon == WEAPON_WARRIOR1) ? WEAPON_WARRIOR2 : WEAPON_WARRIOR1;
                    list = this.weapons[this.weapon];
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < list.length; i++) {
                        sb.append("\r\n").append(i == 0 ? "#b" : "").append("#L").append(i).append("# #z").append(list[i][0]).append("#(").append(list[i][1]).append("コイン)#l");
                    }
                    // 翻訳
                    sb.append(this.weapon == WEAPON_WARRIOR1 ? "\r\n#L12# 次のページへ(1/2)#l" : "\r\n#L12# 最初のページに戻る(2/2)#l");
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add(this.weapon_texts[this.weapon] + sb.toString(), false);
                    askMenu(nt);
                    return true;
                }
                if (answer < 0 || list.length <= answer) {
                    return false;
                }
                // TODO : lua の exchangeEx の Variation:3 (能力値の変動) は未実装
                if (!exchange(0, this.item_coin, -list[answer][1], list[answer][0], 1)) {
                    // 翻訳
                    if (this.weapon == WEAPON_WARRIOR1 && answer == 0) {
                        say("#b#t" + this.item_coin + "##kが足りないか、インベントリがいっぱいなのかもしれない。もう一度確認してくれ。");
                    } else {
                        say("#b#t" + this.item_coin + "##kが足りないか、インベントリがいっぱいのようだ。もう一度確認してくれ。");
                    }
                }
                return false;
            }
            default -> {
                return false;
            }
        }
    }
}
