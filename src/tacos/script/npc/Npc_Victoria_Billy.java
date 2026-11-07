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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import odin.server.Randomizer;

/**
 *
 * @author Riremito
 */
// Npc.wz script : mouse (JMS131-302), 会話文は lua_script_jp (mouse) を利用
public class Npc_Victoria_Billy extends NpcScriptMan {

    private static final int NODE_END = 0;
    private static final int NODE_START = 1;
    private static final int NODE_JOIN_SAY = 2;
    private static final int NODE_JOIN_ASK = 3;
    private static final int NODE_SAYS = 4;
    private static final int NODE_MENU = 5;
    private static final int NODE_GIVE_SAY = 6;
    private static final int NODE_GIVE_NUMBER = 7;
    private static final int NODE_GIVE_ASK = 8;
    private static final int NODE_REWARD_SAY = 9;
    private static final int NODE_REWARD_MENU = 10;
    private static final int NODE_REWARD_ASK = 11;
    private static final int NODE_SAY_END = 12;
    private static final int MENU_ABOUT = 0;
    private static final int MENU_CHECK = 1;
    private static final int MENU_GIVE = 2;
    private static final int MENU_REWARD = 3;
    private final int quest_point = 1001300; // ネットカフェのポイント (クエストのデータ)
    private final int quest_time = 1001301; // 毎日のポイントを受け取った時刻 (クエストのデータ)
    private final int item_mouse = 4000047; // マウス
    private final int point_per_mouse = 10;
    private final int point_daily = 50;
    private final int daily_minutes = 1440;
    // lua の currentTime の形式
    private final DateTimeFormatter time_format = DateTimeFormatter.ofPattern("yy/MM/dd/HH/mm");
    // 交換に必要なポイント
    private final int[] reward_points = {150, 300, 500, 500, 1500, 2000, 2500, 2800};
    // 交換で入手するアイテムの個数
    private final int[] reward_counts = {10, 10, 10, 10, 1, 1, 10, 10};
    // 交換で入手するアイテム (ランダムで1種類)
    private final int[][] reward_items = {
        {
            2000000, // 赤い薬
            2000001, // 橙色の薬
            2000002, // 白い薬
            2000003 // 青い薬
        },
        {
            2020000, // サラダ
            2020001, // チキン
            2020002, // ケーキ
            2020003, // ピザ
            2020004, // ハンバーガー
            2020005, // ロングホットドッグ
            2020006, // ビッグホットドッグ
            2020007 // するめ
        },
        {
            2012000, // ドレイクの血
            2012001, // 妖精の蜜
            2012002, // 古木の樹液
            2012003 // ドレイクの肉
        },
        {
            2030000, // 帰還の書
            2030001, // 帰還の書(リス港口)
            2030002, // 帰還の書(エリニア)
            2030003, // 帰還の書(ペリオン)
            2030004, // 帰還の書(ヘネシス)
            2030005, // 帰還の書(カニング)
            2030006 // 帰還の書(スリーピー)
        },
        {
            4011000, // 青銅
            4011001, // 鋼鉄
            4011002, // ミスリル
            4011003, // アダマンティウム
            4011004, // 銀
            4011005, // オリハルコン
            4011006 // 金
        },
        {
            4021000, // ガーネット
            4021001, // 紫水晶
            4021002, // アクアマリン
            4021003, // エメラルド
            4021004, // オパール
            4021005, // サファイア
            4021006, // トパーズ
            4021007 // ダイヤモンド
        },
        {
            4003000 // ネジ
        },
        {
            2001000, // スイカ
            2001001, // チューチューアイス
            2001002 // 小豆かき氷
        }
    };
    private int node = NODE_START;
    private int last_status = 0;
    private String text = "";
    private String[] says = null;
    private int says_index = 0;
    private int points = 0;
    private int mouse_count = 0;
    private int give_count = 0;
    private int reward = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status < status;
        this.last_status = status;
        // 前の会話の応答の処理
        switch (this.node) {
            case NODE_START -> {
                String val = getQuestData(this.quest_point);
                if (val.isEmpty()) {
                    this.node = NODE_JOIN_SAY;
                    break;
                }
                try {
                    this.points = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    this.points = 0;
                }
                // 毎日1回、ポイントを追加
                String val2 = getQuestData(this.quest_time);
                LocalDateTime now = LocalDateTime.now();
                long minutes = this.daily_minutes;
                if (val2 != null && !val2.isEmpty()) {
                    try {
                        minutes = ChronoUnit.MINUTES.between(LocalDateTime.parse(val2, this.time_format), now);
                    } catch (DateTimeParseException e) {
                        minutes = this.daily_minutes;
                    }
                }
                if (this.daily_minutes <= minutes) {
                    setQuestData(this.quest_time, now.format(this.time_format));
                    int new_point = this.points + this.point_daily;
                    setQuestData(this.quest_point, String.valueOf(new_point));
                    // 翻訳
                    this.text = "当ネットカフェにお越しいただきありがとうございます。そのお礼に、ネットカフェの累計ポイントに#b50ポイント#kを追加します。  #b" + getCharacterName() + "#kさんの現在のポイントは#r" + new_point + "ポイント#kです。貯まったポイントは、後でここにあるいろいろな品物と交換できますよ。";
                    this.node = NODE_SAY_END;
                    break;
                }
                this.node = NODE_MENU;
            }
            case NODE_JOIN_SAY -> {
                this.node = NODE_JOIN_ASK;
            }
            case NODE_JOIN_ASK -> {
                if (!ok) {
                    // 翻訳
                    this.text = "うーん…今は時間がないんですね。きっとお得なのに。でも、気が変わったら、いつでも当ネットカフェに戻ってきてくださいね～";
                    this.node = NODE_SAY_END;
                    break;
                }
                setQuestData(this.quest_point, "0");
                // 翻訳
                this.says = new String[]{
                    "よし！これであなたは正式に当ネットカフェの会員です！2階の右側にあるパソコンが見えますか？あのパソコンからダンジョンに入れるんです。そこで#bモンスターを倒して#kいくうちに、#t4000047#が手に入るかもしれません。",
                    "なくなった#t4000047#はたくさんあるので、集めるのは大変かもしれません。取り戻してくれた#t4000047#1個につき、ご褒美として10ポイントあげますね。ポイントを貯めたり、合計を確認したり、品物と交換したりしたいときは、私に話しかけてください。一日中ここにいますから。",
                    "ああ、そうそう！#t4000047#を集めるのが大変なら、当ネットカフェに通ってくれるだけでもいいんです。1日1回、追加で50ポイントあげますよ。忘れないでくださいね、1日1回私に話しかけてくれればポイントを増やしてあげます。それでは、お会いできてうれしかったです～"
                };
                this.says_index = 0;
                this.node = NODE_SAYS;
            }
            case NODE_SAYS -> {
                this.says_index++;
                if (this.says.length <= this.says_index) {
                    return false;
                }
            }
            case NODE_MENU -> {
                switch (answer) {
                    case MENU_ABOUT -> {
                        // 翻訳
                        this.says = new String[]{
                            "ネットカフェポイントについて説明しますね。当ネットカフェからなくなった#b#t4000047##kを見つけてくれたら、1個につき10ポイントあげます。ポイントが十分に貯まったら、ここで用意している品物と交換できます。すごいものもありますよ。早くたくさん集めることをおすすめします！",
                            "#t4000047#は、当ネットカフェの2階右側のパソコンから入れるダンジョンで手に入ります。モンスターを倒しているうちに見つかりますよ。なくなった#t4000047#はたくさんあるので、探し続ければ簡単にたくさんポイントが手に入ります。",
                            "ポイントを貯める方法はもう一つあります。1日1回、当ネットカフェに来てくれたお礼に#b50ポイント#kを追加であげています。忘れないでくださいね、これは1日1回だけです。わかりましたか？"
                        };
                        this.says_index = 0;
                        this.node = NODE_SAYS;
                    }
                    case MENU_CHECK -> {
                        // 翻訳
                        this.text = "ポイントを確認しますね。#b" + getCharacterName() + "#kさんの現在のポイントは#r" + this.points + "ポイント#kです。貯まったポイントは、後で当ネットカフェにある品物と交換できますから、集め続けてくださいね～";
                        this.node = NODE_SAY_END;
                    }
                    case MENU_GIVE -> {
                        this.mouse_count = getItemCount(this.item_mouse);
                        if (this.mouse_count < 1) {
                            // 翻訳
                            this.text = "さっき当ネットカフェから持ち去られた#t4000047#は、まだ見つけていないみたいですね。途中で#t4000047#を見つけたら、戻ってきてポイントと交換してください！";
                            this.node = NODE_SAY_END;
                            break;
                        }
                        this.node = NODE_GIVE_SAY;
                    }
                    case MENU_REWARD -> {
                        this.node = NODE_REWARD_SAY;
                    }
                    default -> {
                        return false;
                    }
                }
            }
            case NODE_GIVE_SAY -> {
                this.node = NODE_GIVE_NUMBER;
            }
            case NODE_GIVE_NUMBER -> {
                if (answer < 0) {
                    return false;
                }
                this.give_count = answer;
                this.node = NODE_GIVE_ASK;
            }
            case NODE_GIVE_ASK -> {
                int give_count = this.give_count;
                int add_point = this.point_per_mouse * give_count;
                int total_point = this.points + add_point;
                if (!ok) {
                    // 翻訳
                    this.text = "確かに私たちが探していた#t4000047#のようですね…途中でポイントと交換したくなったら、#t4000047#を当ネットカフェに返してくださいね。";
                    this.node = NODE_SAY_END;
                    break;
                }
                if (!exchange(0, this.item_mouse, -give_count)) {
                    // 翻訳
                    this.text = "本当に" + give_count + "個の#b#t4000047##kを集めましたか？もう一度確認してください～";
                    this.node = NODE_SAY_END;
                    break;
                }
                setQuestData(this.quest_point, String.valueOf(total_point));
                // 翻訳
                this.text = "新しく" + add_point + "ポイントを加えて、累計ポイントは#r" + total_point + "#kになりました。これからも私たちのために#t4000047#を集めてくださいね～";
                this.node = NODE_SAY_END;
            }
            case NODE_REWARD_SAY -> {
                this.node = NODE_REWARD_MENU;
            }
            case NODE_REWARD_MENU -> {
                if (answer < 0 || this.reward_points.length <= answer) {
                    return false;
                }
                this.reward = answer;
                int need_point = this.reward_points[this.reward];
                if (this.points < need_point) {
                    // 翻訳
                    String[] texts = {
                        "ポイントが足りません。#bランダムなポーション10個#kと交換するには、少なくとも" + need_point + "ポイント必要です。",
                        "ポイントが足りません。#bランダムな食べ物10個#kと交換するには、少なくとも" + need_point + "ポイント必要です。",
                        "ポイントが足りません。#bランダムな能力値アップアイテム10個#kと交換するには、少なくとも" + need_point + "ポイント必要です。",
                        "ポイントが足りません。#b帰還の書10枚#kと交換するには、少なくとも" + need_point + "ポイント必要です。",
                        "ポイントが足りません。#bランダムな精錬済み鉱物1個#kと交換するには、少なくとも" + need_point + "ポイント必要です。",
                        "ポイントが足りません。#bランダムな精錬済み宝石1個#kと交換するには、少なくとも" + need_point + "ポイント必要です。",
                        "ポイントが足りません。#bネジ10個#kを手に入れるには、少なくとも" + need_point + "ポイント必要です。",
                        "ポイントが足りません。#bランダムな夏の食べ物10個#kと交換するには、少なくとも" + need_point + "ポイント必要です。"
                    };
                    this.text = texts[this.reward];
                    this.node = NODE_SAY_END;
                    break;
                }
                this.node = NODE_REWARD_ASK;
            }
            case NODE_REWARD_ASK -> {
                // TODO : lua では いいえ を選択しても交換が行われる
                if (!ok) {
                    return false;
                }
                int need_point = this.reward_points[this.reward];
                int[] items = this.reward_items[this.reward];
                int item_id = items[Randomizer.rand(0, items.length - 1)];
                if (!canHold(item_id, this.reward_counts[this.reward])) {
                    // 翻訳
                    String[] texts = {
                        "インベントリがいっぱいのようです。空きを作ってからもう一度お試しください。",
                        "インベントリがいっぱいのようです。インベントリに空きを作ってから話しかけてくださいね～",
                        "インベントリがいっぱいのようです。インベントリに空きを作ってから話しかけてくださいね～",
                        "インベントリがいっぱいのようです。インベントリに空きを作ってから話しかけてくださいね～",
                        "ETCインベントリがいっぱいのようです。ETCインベントリに空きを作ってから話しかけてくださいね～",
                        "ETCインベントリがいっぱいのようです。空きを作ってから話しかけてくださいね～",
                        "ETCインベントリがいっぱいのようです。ETCインベントリに空きを作ってから話しかけてくださいね～",
                        "ETCインベントリがいっぱいのようです。ETCインベントリに空きを1つ作ってから、もう一度お試しください～"
                    };
                    this.text = texts[this.reward];
                    this.node = NODE_SAY_END;
                    break;
                }
                if (!exchange(0, item_id, this.reward_counts[this.reward])) {
                    // 翻訳
                    String[] texts = {
                        "少なくとも#r" + need_point + "ポイント#kあるか、インベントリに空きがあるか確認してください～",
                        "少なくとも#r300ポイント#kあるか、インベントリにアイテムを入れる空きがあるか確認してください。",
                        "少なくとも#r" + need_point + "ポイント#kあるか、インベントリに空きがあるか確認してください～",
                        "少なくとも#r" + need_point + "ポイント#kあるか、インベントリに空きがあるか確認してください～",
                        "少なくとも#r" + need_point + "ポイント#kあるか、ETCインベントリに空きスロットがあるか確認してください～",
                        "少なくとも#r" + need_point + "ポイント#kあるか、ETCインベントリに空きがあるか確認してください～",
                        "少なくとも#r" + need_point + "ポイント#kあるか、ETCインベントリに空きがあるか確認してください～",
                        "ポイントが#r" + need_point + "ポイント#kを超えているか、ETCインベントリに空きがあるか確認してください～"
                    };
                    this.text = texts[this.reward];
                    this.node = NODE_SAY_END;
                    break;
                }
                int remain_point = this.points - need_point;
                setQuestData(this.quest_point, String.valueOf(remain_point));
                // 翻訳
                String[] texts = {
                    "#r" + need_point + "ポイント#kを#bランダムなポーション#k10個と交換して、#b#t" + item_id + "# 10個#kを手に入れました。残りは#r" + remain_point + "ポイント#kです。お渡ししたものに満足していただけましたか？またのお越しを～",
                    "#r" + need_point + "ポイント#kを#bランダムな食べ物10個#kと交換して、#b#t" + item_id + "# 10個#kを手に入れました。残りは#r" + remain_point + "ポイント#kです。お渡ししたものに満足していただけましたか？またのお越しを～",
                    "#r" + need_point + "ポイント#kを#b能力値アップアイテム#k10個と交換して、#b#t" + item_id + "# 10個#kを手に入れました。残りは#r" + remain_point + "ポイント#kです。お渡ししたものに満足していただけましたか？またのお越しを～",
                    "#r" + need_point + "ポイント#kを#bランダムな帰還の書10枚#kと交換して、#b#t" + item_id + "# 10個#kを手に入れました。残りは#r" + remain_point + "ポイント#kです。お渡ししたものに満足していただけましたか？またのお越しを～",
                    "#bランダムな精錬済み宝石1個#kを、#r" + need_point + "ポイント#kと引き換えに渡して、#b#t" + item_id + "# 1個#kをお渡ししました。残りは#r" + remain_point + "ポイント#kです。お渡ししたものに満足していただけましたか？またのお越しを～",
                    "#r" + need_point + "ポイント#kを#bランダムな精錬済み宝石1個#kと交換して、#b#t" + item_id + "# 1個#kをお渡ししました。残りは#r" + remain_point + "ポイント#kです。お渡ししたものに満足していただけましたか？またのお越しを～",
                    "#b#t4003000# 10個#kを、あなたの#r" + need_point + "ポイント#kと交換して、#b#t4003000# 10個#kをお渡ししました。残りは#r" + remain_point + "ポイント#kです。お渡ししたものに満足していただけましたか？またのお越しを～",
                    "#r" + need_point + "ポイント#kを#bランダムな夏の食べ物10個#kと交換して、#b#t" + item_id + "# 1個#kをお渡ししました。残りは#r" + remain_point + "ポイント#kです。お渡ししたものは気に入っていただけましたか？またのお越しを～"
                };
                this.text = texts[this.reward];
                this.node = NODE_SAY_END;
            }
            default -> {
                return false;
            }
        }
        // 次の会話の表示
        switch (this.node) {
            case NODE_JOIN_SAY -> {
                // 翻訳
                say("#Gいらっしゃいませ:いらっしゃいませ#、#Gいらっしゃいませ:いらっしゃいませ#～当ネットカフェは素晴らしい設備で有名なんです。え、何ですって？パソコンに#t4000047#がない？うーん…困りましたね…実は少し前に変な人たちが来て、ここにあった#t4000047#を全部盗んでいったんです…どうしよう…", false, true);
                return true;
            }
            case NODE_JOIN_ASK -> {
                // 翻訳
                askYesNo("よかった！よかった！探してきてくれますか？探してくれたら、当ネットカフェの特別会員として登録して、ポイントを貯めてあげます。ポイントが十分に貯まったら、ここにあるいろいろな品物と交換できますよ。どうですか？引き受けてくれますか？");
                return true;
            }
            case NODE_SAYS -> {
                boolean next = this.says_index + 1 < this.says.length;
                say(this.says[this.says_index], false, next);
                return next;
            }
            case NODE_MENU -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("こちらは会員専用です。メニューを選んでください～");
                nt.addMenu(MENU_ABOUT, "ポイントについて");
                nt.addMenu(MENU_CHECK, "ポイントの合計を確認");
                nt.addMenu(MENU_GIVE, "ポイントを貯める");
                nt.addMenu(MENU_REWARD, "品物と交換する");
                askMenu(nt);
                return true;
            }
            case NODE_GIVE_SAY -> {
                // 翻訳
                say("#t4000047#はたくさん集めましたか？返してくれた#t4000047#1個につき#b10#kポイントあげます。そのポイントは後でいろいろな品物と交換できますよ。ポイントと交換したマウスは後で回収されることを覚えておいてくださいね。", false, true);
                return true;
            }
            case NODE_GIVE_NUMBER -> {
                // 翻訳
                askNumber("#b" + this.mouse_count + "#k個の#t4000047#を持っていますね。そのうちいくつをポイントと交換しますか？交換したい#t4000047#の数を入力してください。", this.mouse_count, 0, 999);
                return true;
            }
            case NODE_GIVE_ASK -> {
                int give_count = this.give_count;
                int add_point = this.point_per_mouse * give_count;
                int total_point = this.points + add_point;
                // 翻訳
                askYesNo("" + give_count + "個の#b#t4000047##kをポイントと交換しますか？#r" + add_point + "ポイント#kが、当ネットカフェの累計ポイント#r" + total_point + "ポイント#kに追加されます。");
                return true;
            }
            case NODE_REWARD_SAY -> {
                // 翻訳
                say("現在、当ネットカフェのポイントは#r" + this.points + "ポイント#kです。交換したい品物を選んでください。言い忘れるところでしたが、交換で選んだ品物の中には価値のないアイテムもあるかもしれないので、よく考えてから決めてくださいね～", false, true);
                return true;
            }
            case NODE_REWARD_MENU -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("現在のポイント:" + this.points + "ポイント\r\n#b#L0#ランダムなポーション10個（150ポイントと交換）)#l\r\n#b#L1#ランダムな食べ物10個（300ポイントと交換）)#l\r\n#b#L2#能力値アップアイテム10個（500ポイントと交換）)#l\r\n#b#L3#ランダムな帰還の書10枚（500ポイントと交換）)#l\r\n#b#L4#ランダムな精錬済み鉱石1個（1500ポイントと交換）)#l\r\n#b#L5#ランダムな精錬済み宝石1個（2000ポイントと交換）)#l\r\n#b#L6##t4003000# 10個（2500ポイントと交換）#l\r\n#b#L7#ランダムな夏の食べ物10個（2800ポイントと交換）#l", false);
                askMenu(nt);
                return true;
            }
            case NODE_REWARD_ASK -> {
                int need_point = this.reward_points[this.reward];
                // 翻訳
                String[] texts = {
                    "本当に#r" + need_point + "ポイント#kを#bランダムなポーション#k10個と交換しますか？次のいずれかが10個もらえます : #t" + 2000000 + "#, #t" + 2000001 + "#, #t" + 2000002 + "#、#t" + 2000003 + "#。交換する前に、インベントリに十分な空きがあるか確認してください。",
                    "本当に#r" + need_point + "ポイント#kを#bランダムな食べ物10個#kと交換しますか？次のいずれかが10個もらえます : #t" + 2020000 + "#, #t" + 2020001 + "#, #t" + 2020002 + "#, #t" + 2020003 + "#, #t" + 2020004 + "#, #t" + 2020005 + "#, #t" + 2020006 + "#、#t" + 2020007 + "#。交換する前に、インベントリに十分な空きがあるか確認してください。",
                    "本当に#r" + need_point + "ポイント#kを#bランダムな能力値アップアイテム#k10個と交換しますか？次のいずれかがランダムで10個もらえます: #t" + 2012000 + "#, #t" + 2012001 + "#, #t" + 2012002 + "#、#t" + 2012003 + "#。交換する前に、インベントリに十分な空きがあるか確認してください。",
                    "本当に#r" + need_point + "ポイント#kを#bランダムな帰還の書10枚#kと交換しますか？次のいずれかの場所への帰還の書がランダムで10枚もらえます: #t" + 2030000 + "#, #t" + 2030001 + "#, #t" + 2030002 + "#, #t" + 2030003 + "#, #t" + 2030004 + "#, #t" + 2030005 + "#、#t" + 2030006 + "#。交換する前に、インベントリに十分な空きがあるか確認してください。",
                    "本当に#r" + need_point + "ポイント#kを#bランダムな精錬済み鉱物1個#kと交換しますか？次のいずれかがランダムでもらえます: #t" + 4011000 + "#, #t" + 4011001 + "#, #t" + 4011002 + "#, #t" + 4011003 + "#, #t" + 4011004 + "#, #t" + 4011005 + "#、#t" + 4011006 + "#。交換する前に、ETCインベントリに空きスロットがあるか確認してください。",
                    "本当に#r" + need_point + "ポイント#kを#bランダムな精錬済み宝石1個#kと交換しますか？次の精錬済み宝石のいずれかがランダムで1個もらえます: #t" + 4021000 + "#, #t" + 4021001 + "#, #t" + 4021002 + "#, #t" + 4021003 + "#, #t" + 4021004 + "#, #t" + 4021005 + "#, #t" + 4021006 + "#、#t" + 4021007 + "#。交換する前に、ETCインベントリに空きがあるか確認してください。",
                    "本当に#r" + need_point + "ポイント#kを使って#bネジ10個#kを手に入れますか？交換する前に、ETCインベントリに十分な空きがあるか確認してください。",
                    "本当に#r" + need_point + "ポイント#kを#bランダムな夏の食べ物10個#kと交換しますか？次のいずれかがランダムで10個もらえます: #t" + 2001000 + "#, #t" + 2001001 + "#、#t" + 2001002 + "#。交換する前に、ETCインベントリに空きスロットがあるか確認してください。"
                };
                askYesNo(texts[this.reward]);
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
