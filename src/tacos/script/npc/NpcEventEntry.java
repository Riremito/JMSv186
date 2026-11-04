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

import tacos.packet.ops.OpsBroadcastMsg;

/**
 *
 * @author Riremito
 */
// Npc.wz script : Event00 (JMS131-302), 会話文は lua_script_jp (Event00) を利用
// テストサーバー (serverType == 2) の分岐は削除
public class NpcEventEntry extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_GM_MENU = 1;
    private static final int NODE_GM_SET = 2;
    private static final int NODE_GM_COUNT = 3;
    private static final int NODE_SAYS = 4;
    private static final int NODE_MENU = 5;
    private static final int NODE_DESC = 6;
    private final int map_south_perry = 60000; // サウスペリ : サウスペリ
    private final int map_lith_harbor = 104000000; // リス : 港口
    private final int map_orbis = 200000000; // オルビス : オルビス
    private final int map_ludi = 220000000; // ルディブリアム : ルディブリアム
    private final int npc_paul = 9000000; // ポル
    private final int npc_jean = 9000001; // ジャング
    private final int npc_martin = 9000011; // マティン
    private final int npc_tony = 9000013; // トニ
    private final int item_trophy = 4000038; // イベントのトロフィー
    private final int item_scroll = 4031019; // 悪魔の文書
    private final int item_certificate = 4031332; // 1勝証明書
    private final int quest_event_from = 9000; // イベントに参加した場所
    private final int quest_event_time = 9001; // イベントに参加した時刻
    // イベントマップ
    private final int[] maps_event = {
        109030001, // 隠れ道 : のぼれ～のぼれ～＜1段階＞
        109030101, // 隠れ道 : のぼれ～のぼれ～＜1段階＞
        109030201, // 隠れ道 : のぼれ～のぼれ～＜1段階＞
        109030301, // 隠れ道 : のぼれ～のぼれ～＜1段階＞
        109030401, // 隠れ道 : のぼれ～のぼれ～＜1段階＞
        109040000, // 隠れ道 : 頂上を目指して＜控え室＞
        109020001, // 隠れ道 : ○Ｘクイズ
        109080000, // 隠れ道 : ココナッツシーズン
        109080001, // 隠れ道 : ココナッツシーズン
        109080002, // 隠れ道 : ココナッツシーズン
        109060001, // 隠れ道 : イベントマップへの入口
        109010000, // 隠れ道 : 宝を探せ！
    };
    // イベントマップの入場人数
    private final int[] counts_event = {80, 80, 80, 80, 80, 70, 90, 60, 60, 60, 80, 60};
    // 翻訳
    private final String[] names_count = {
        "オラオラ 1 ( 109030001 )",
        "オラオラ 2 ( 109030101 )",
        "オラオラ 3 ( 109030201 )",
        "オラオラ 4 ( 109030301 )",
        "オラオラ 5 ( 109030401 )",
        "「メイプル体力テスト」( 109040000 )",
        "OXクイズ ( 109030001 )",
        "ココナッツ収穫 1 ( 109080000 )",
        "ココナッツ収穫 2 ( 109080001 )",
        "ココナッツ収穫 3 ( 109080002 )",
        "雪玉転がし ( 109060001 )",
        "宝探し ( 109010000 )",
    };
    private final String[] names_unavailable = {
        "オラオラ 1 ( 109030001 )",
        "オラオラ 2 ( 109030101 )",
        "オラオラ 3 ( 109030201 )",
        "オラオラ 4 ( 109030301 )",
        "オラオラ 5 ( 109030401 )",
        "メイプル体力テスト ( 109040000 )",
        "OXクイズ ( 109030001 )",
        "ココナッツ収穫 1 ( 109080000 )",
        "ココナッツ収穫 2 ( 109080001 )",
        "ココナッツ収穫 3 ( 109080002 )",
        "雪玉転がし ( 109060001 )",
        "宝探し ( 109010000 )",
    };
    private final String text_events = "\r\n#b#L0# オラオラ 1 ( 109030001 )#l\r\n#L1# オラオラ 2 ( 109030101 )#l\r\n#L2# オラオラ 3 ( 109030201 )#l\r\n#L3# オラオラ 4 ( 109030301 )#l\r\n#L4# オラオラ 5 ( 109030401 )#l\r\n#L5# メイプル体力テスト ( 109040000 )#l\r\n#L6# OXクイズ ( 109020001 )#l\r\n#L7# ココナッツ収穫 1 ( 109080000 )#l\r\n#L8# ココナッツ収穫 2 ( 109080001 )#l\r\n#L9# ココナッツ収穫 3 ( 109080002 )#l\r\n#L10# 雪玉転がし ( 109060001 )#l\r\n#L11# 宝探し ( 109010000 )#l";
    private int node = NODE_START;
    private String[] says = null;
    private int says_index = 0;

    @Override
    public boolean action(int status, int answer) {
        int map_id = getMapId();
        // 翻訳
        switch (this.node) {
            case NODE_START -> {
                // lua の isSuperGM
                if (getPlayer().isAdmin()) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("実行する操作を選んでください。\r\n#b#L0# イベントマップを選択#l\r\n#L1# イベントマップのユーザー数を確認#l#k", false);
                    askMenu(nt);
                    this.node = NODE_GM_MENU;
                    return true;
                }
                if (map_id == this.map_south_perry) {
                    this.says = new String[]{
                        "やあ、僕は#b#p" + this.npc_paul + "##k。今忙しくなければ…一緒にいてもいいかな？このあたりで#rイベント#kのために人が集まってるって聞いたんだけど、ひとりで行くのは嫌でさ…ねえ、一緒にちょっと見に行かない？"
                    };
                } else if (map_id == this.map_lith_harbor) {
                    this.says = new String[]{
                        "やあ、僕は#b#p" + this.npc_jean + "##k。ここで兄の#bポール#kを待ってるんだ。もう来てるはずなんだけど…",
                        "うーん…どうしよう？もうすぐイベントが始まるのに…たくさんの人がイベントに参加しに行ったから、急いだ方がいいよね…"
                    };
                } else if (map_id == this.map_orbis) {
                    this.says = new String[]{
                        "やあ、僕は#b#p" + this.npc_martin + "##k。ここで兄弟たちを待ってるんだ…なんでこんなに遅いんだろう？もうイライラしてきたよ…時間までに着かないと、イベントに参加できないかもしれないのに…",
                        "うーん…どうしよう？もうすぐイベントが始まるのに…たくさんの人がイベントに参加しに行ったから、急いだ方がいいよね…"
                    };
                } else if (map_id == this.map_ludi) {
                    this.says = new String[]{
                        "やあ、僕は#b#p" + this.npc_tony + "##k。今は兄弟たちを待ってるんだけど、まだ来ないんだ。何でもひとりでやるのはもううんざりだよ。せめてイベントの間は、周りにたくさん人がいるからそんなに寂しくないんだけどね。どのイベントも参加できる人数が決まってるから、早く行かないと参加できなくなっちゃう。",
                        "僕たちはいとこ同士だけど、いつもお互いが恋しくなっちゃうんだ。ああ、どうしよう？イベントはもういつ始まってもおかしくないのに…向こうではたくさんの人が待っていて、もう場所がないかもしれない…"
                    };
                } else {
                    return false;
                }
                this.says_index = 0;
                say(this.says[this.says_index], false, true);
                this.node = NODE_SAYS;
                return true;
            }
            case NODE_GM_MENU -> {
                NpcScriptTag nt = new NpcScriptTag();
                if (answer == 0) {
                    nt.add("イベントを選んでください。 " + this.text_events + "\r\n#L12# イベントマップの入口を閉じる#l\r\n#k", false);
                    askMenu(nt);
                    this.node = NODE_GM_SET;
                    return true;
                }
                nt.add("イベントを選んでください。 " + this.text_events + "#k", false);
                askMenu(nt);
                this.node = NODE_GM_COUNT;
                return true;
            }
            case NODE_GM_SET -> {
                if (0 <= answer && answer < this.maps_event.length) {
                    setNpcVar("map", String.valueOf(this.maps_event[answer]));
                    setNpcVar("count", String.valueOf(this.counts_event[answer]));
                    fieldNotice(OpsBroadcastMsg.BM_NOTICE, "イベントが開始されました。イベントNPCをクリックしてイベントマップに入場してください。");
                } else if (answer == this.maps_event.length) {
                    setNpcVar("map", "-1");
                    setNpcVar("count", "0");
                }
                return false;
            }
            case NODE_GM_COUNT -> {
                if (answer < 0 || this.maps_event.length <= answer) {
                    return false;
                }
                if (getNpcVar("map").equals(String.valueOf(this.maps_event[answer]))) {
                    say(this.names_count[answer] + " には合計" + this.counts_event[answer] + "人まで入場できます。#r" + getUserCount(this.maps_event[answer]) + "#k人がイベントマップに入場しました。");
                } else {
                    say(this.names_unavailable[answer] + " このイベントは利用できません。");
                }
                return false;
            }
            case NODE_SAYS -> {
                this.says_index++;
                if (this.says_index < this.says.length) {
                    say(this.says[this.says_index], true, true);
                    return true;
                }
                // lua の check_kawi (じゃんけんの勝者証明書)
                boolean proof = false;
                for (int i = 0; i < 10; i++) {
                    proof |= 0 < getItemCount(this.item_certificate + i);
                }
                NpcScriptTag nt = new NpcScriptTag();
                if (map_id == this.map_south_perry) {
                    nt.add("え？どんなイベントかって？ええとね… \r\n#L0##e1. #n#bそれってどんなイベント？#k#l\r\n#L1##e2. #n#bイベントのゲームについて説明して。#k#l\r\n#L2##e3. #n#bよし、行こう！#k#l" + (proof ? "\r\n#L3##e4. #k\n#bじゃんけんの勝者証明書をほかのアイテムと交換したい。#l#k" : ""), false);
                } else if (map_id == this.map_lith_harbor) {
                    nt.add((proof ? "ねえ…僕と一緒に行かない？ " : "ねえ…僕と一緒に行かない？兄はほかの人たちと一緒に来ると思うんだ。") + "\r\n#L0##e1. #n#bそれってどんなイベント？#k#l\r\n#L1##e2. #n#bイベントのゲームについて説明して。#k#l\r\n#L2##e3. #n#bよし、行こう！#k#l" + (proof ? "\r\n#L3#じゃんけんの勝者証明書をほかのアイテムと交換したい。#l#k" : ""), false);
                } else if (map_id == this.map_orbis) {
                    nt.add("ねえ…じゃあ、僕と一緒に来ない？\r\n#L0##e1. #n#bそれってどんなイベント？#k#l\r\n#L1##e2. #n#bイベントのゲームについて説明して。#k#l\r\n#L2##e3. #n#bよし、行こう！#k#l" + (proof ? "\r\n#L3#じゃんけんの勝者証明書をほかのアイテムと交換したい。#l#k" : ""), false);
                } else {
                    nt.add("どうかな？僕と一緒にイベントに行かない？\r\n#b#L0# どんなイベント？#l\r\n#L1# イベントのゲームについて説明して。#l\r\n#L2# よし、行こう！#l#k" + (proof ? "\r\n#L3# じゃんけんの勝者証明書をほかのアイテムと交換したい。#l#k" : ""), false);
                }
                askMenu(nt);
                this.node = NODE_MENU;
                return true;
            }
            case NODE_MENU -> {
                switch (answer) {
                    case 0 -> {
                        say("メイプルストーリーグローバルは今月いっぱい、サービス開始1周年をお祝いしています！期間中はGMがサプライズでGMイベントを開催します。見逃さないように気をつけて、素敵な賞品をもらうために、少なくとも1つはイベントに参加してくださいね！");
                        return false;
                    }
                    case 1 -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("このイベントにはたくさんのゲームがあるんだ。ゲームを始める前に、遊び方を説明するね。詳しく知りたいイベントを選んで！\r\n#b#L0# オラオラ#l\r\n#L1# メイプル体力テスト#l\r\n#L2# 雪玉転がし#l\r\n#L3# ココナッツ収穫#l\r\n#L4# OXクイズ#l\r\n#L5# 宝探し#l#k", false);
                        askMenu(nt);
                        this.node = NODE_DESC;
                        return true;
                    }
                    case 2 -> {
                        int map_event = -1;
                        int count = 0;
                        try {
                            map_event = Integer.parseInt(getNpcVar("map"));
                        } catch (NumberFormatException e) {
                        }
                        try {
                            count = Integer.parseInt(getNpcVar("count"));
                        } catch (NumberFormatException e) {
                        }
                        // lua の incIntReg( "count", -1 )
                        count--;
                        setNpcVar("count", String.valueOf(count));
                        String time = currentTime();
                        String time_last = getQuestData(this.quest_event_time);
                        boolean go_event = time_last.isEmpty() || 1440 <= compareTime(time, time_last);
                        boolean is_event_map = 0 <= map_event && String.valueOf(map_event).startsWith("109");
                        if (go_event && getItemCount(this.item_scroll) < 1 && 0 <= count && is_event_map) {
                            if (exchange(0, this.item_trophy, 1)) {
                                String from = (map_id == this.map_south_perry) ? "maple" : (map_id == this.map_lith_harbor) ? "victoria" : (map_id == this.map_orbis) ? "ossyria" : "ludi";
                                setQuestData(this.quest_event_from, from);
                                setQuestData(this.quest_event_time, time);
                                warp(map_event);
                                return false;
                            }
                            setNpcVar("count", String.valueOf(count + 1));
                            say("ETCインベントリに空きスロットはある？もう一度確認してね！");
                            return false;
                        }
                        setNpcVar("count", String.valueOf(count + 1));
                        say("イベントがまだ始まっていないか、すでに#t" + this.item_scroll + "#を持っているか、24時間以内にこのイベントに参加済みです。しばらくしてからもう一度お試しください！");
                        return false;
                    }
                    default -> {
                        // lua でも未実装
                        return false;
                    }
                }
            }
            case NODE_DESC -> {
                switch (answer) {
                    case 0 ->
                        say("#b[オラオラ]#kは、参加者がはしごを登って頂上を目指すゲームだよ。たくさんあるポータルの中から正しいポータルを選んで、次のレベルへ進もう。 \r\n\r\nゲームは3つのレベルで構成されていて、制限時間は#b6分#k。[オラオラ]の間は、#bジャンプ、テレポート、ダッシュ、ポーションやアイテムによる移動速度アップはできないよ#k。\n 変な場所に飛ばされるトリックポータルもあるから、気をつけてね。");
                    case 1 ->
                        say("#b[メイプル体力テスト]は、忍耐の森のような障害物レース#kだよ。制限時間内に障害物を乗り越えてゴールにたどり着けば勝ち。\r\n\r\nゲームは4つのレベルで構成されていて、制限時間は#b15分#k。[メイプル体力テスト]の間は、テレポートやダッシュはできないよ。");
                    case 2 ->
                        say("#b[雪玉転がし]#kは、メイプルチームとストーリーチームの2チームに分かれて、#b制限時間内にどちらが大きな雪玉をより遠くまで押せるか#kを競うゲームだよ。制限時間内に決着がつかない場合は、雪玉をより遠くまで押したチームの勝ち。\r\n\r\n雪玉を押すにはCtrlキーを押してね。遠距離攻撃やスキル攻撃はここでは使えないよ。#b近距離攻撃のみ#k有効なんだ。\r\n\r\nキャラクターが雪玉に触れると、スタート地点に戻されちゃう。スタート地点の前にある雪だるまを攻撃して、相手チームが雪玉を進めるのを防ごう。雪玉に向かうか雪だるまに向かうか、チームで決める作戦が勝負の鍵だよ。");
                    case 3 ->
                        say("#b[ココナッツ収穫]#kは、メイプルチームとストーリーチームの2チームに分かれて、#bどちらがより多くのココナッツを集めるか#kを競うゲームだよ。制限時間は#b5分#k。引き分けの場合は、2分間の延長戦で勝者を決めるよ。それでも同点の場合は、引き分けで終了だ。\r\n\r\n遠距離攻撃やスキル攻撃はここでは使えないよ。#b近距離攻撃のみ有効#kなんだ。近距離攻撃用の武器を持っていない場合は、イベントマップ内のNPCから買えるよ。キャラクターのレベル、武器、スキルに関係なく、ダメージはすべて同じなんだ。\r\n\r\nマップにある障害物や罠に注意してね。ゲーム中にキャラクターが死んでしまうと脱落だよ。ココナッツが落ちる前に最後に攻撃したプレイヤーの得点になるんだ。地面に落ちたココナッツだけがカウントされるよ。ただし、ヤシの木から落ちたものや、たまに爆発するものはカウント「されない」からね。マップの下の方にある貝殻の1つには隠しポータルもあるから、うまく使ってね！");
                    case 4 ->
                        say("#b[OXクイズ]#kは、XとOで答えるメイプルストーリーのクイズゲームだよ。ゲームに参加したら、Mキーでミニマップを表示して、XとOの位置を確認してね。#r10問#kの問題が出題されて、全問正解したプレイヤーが勝者だよ。\r\n\r\n問題が出されたら、はしごを使ってXかOか、正解だと思うエリアに移動してね。答えを選ばなかったり、制限時間を過ぎてもはしごにいたりすると脱落だよ。画面に[正解]が表示されるまでその場所にいてね。不正防止のため、OXクイズの間はすべてのチャットが使えなくなるよ。");
                    default ->
                        say("#b[宝探し]#kは、マップ中に隠された#b宝の書#kを10分以内に見つけるゲームだよ#r #k。謎の宝箱がたくさん隠されていて、壊すとたくさんのアイテムが出てくるんだ。その中から宝の書を見つけ出すのが君の仕事さ。 \n\r\n宝箱は#b通常攻撃#kで壊せるよ。宝の書を手に入れたら、アイテム交換担当のNPCで秘密の書と交換できるんだ。アイテム交換のNPCは宝探しのマップにいるけど、リスハーバーの#b[バイキン]#kのところでも交換できるよ。\r\n\r\nこのゲームには隠しポータルや隠しテレポート地点がいくつかあるんだ。使うには、特定の場所で#b上矢印キー#kを押すと、別の場所にテレポートするよ。適当にジャンプしてみると、隠されたはしごやロープが見つかるかもしれないよ。隠し場所に連れて行ってくれる宝箱や、秘密のポータルからしか行けない秘密の宝箱もあるから、あちこち探してみてね。\r\n\r\n宝探しの間は、すべての攻撃スキルが#r使用不可#kになるから、普通の武器で宝箱を壊してね。");
                }
                return false;
            }
            default -> {
                return false;
            }
        }
    }
}
