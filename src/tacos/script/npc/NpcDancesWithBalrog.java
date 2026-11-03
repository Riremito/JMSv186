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

import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class NpcDancesWithBalrog extends NpcScriptMan {

    private final int job_1st_level = 10;
    private final int job_1st = 100; // 戦士
    private final int job_2nd_level = 30;
    private final int[] job_2nd = {
        110, // ファイター
        120, // ページ
        130 // スピアマン
    };
    private final String[] job_2nd_names = {
        "ソードマン",
        "ページ",
        "スピアマン"
    };
    private final int job_3rd_level = 70;
    private final int[] job_3rd = {
        111, // クルセイダー
        121, // ナイト
        131 // ドラゴンナイト
    };
    private final int job_4th_level = 120;
    private final int item_letter = 4031008; // コブシを開いて立ての手紙
    private final int item_proof_of_hero = 4031012; // 英雄の証
    private final int npc_instructor = 1072000; // 戦士転職教官
    private final int map_instructor = 102020300; // ぺリオン : 西の岩山4
    private static final int MENU_SELECT_JOB = 0;
    private static final int STEP_NONE = 0;
    private static final int STEP_1ST = 1;
    private static final int STEP_2ND = 2;
    private static final int STEP_3RD = 3;
    private static final int STEP_4TH = 4;
    // 転職中に条件が切り替わってしまうためNPC会話開始時に職業を判定する
    private int step = -1;
    private int job_index = -1;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        if (this.step == -1) {
            int job = getPlayer().getJob();
            int level = getPlayer().getLevel();
            this.step = STEP_NONE;
            if (this.job_1st_level <= level && job == 0) {
                this.step = STEP_1ST;
            } else if (this.job_2nd_level <= level && job == this.job_1st) {
                this.step = STEP_2ND;
            } else if (this.job_3rd_level <= level) {
                for (int job_id : this.job_2nd) {
                    if (job == job_id) {
                        this.step = STEP_3RD;
                        break;
                    }
                }
            }
            if (this.step == STEP_NONE && this.job_4th_level <= level) {
                for (int job_id : this.job_3rd) {
                    if (job == job_id) {
                        this.step = STEP_4TH;
                        break;
                    }
                }
            }
        }
        // 転職を断った
        if (status == -1) {
            say("まだ心の準備ができてないか？");
            return false;
        }
        switch (this.step) {
            case STEP_1ST -> {
                // TODO : 文章が盗賊転職のもの (ダークロード) になっている, 戦士転職の文章が不明
                switch (status) {
                    case 0 -> {
                        askYesNo("盗賊のアジトへようこそ。こうでもしないと入口もなかなか分かり辛い所だ。ふふ、出る時に門をよく憶えておいてくれ。じゃ、盗賊になる準備はできたか？");
                        return true;
                    }
                    case 1 -> {
                        // TODO : 元のスクリプトは resetStats(4, 25, 4, 4) で盗賊のステータスにしていた, 戦士のステータスとインベントリの拡張
                        getPlayer().changeJob(this.job_1st);
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("これで君は盗賊になった。盗賊のスキルが使用できるようになったからスキルウィンドウを開いてみてくれ。レベルを上げるとより多くのスキルを覚えることができるんだ。");
                        say(nt, false, true);
                        return true;
                    }
                    case 2 -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("スキルだけでは物足りないな？ステータスも盗賊らしくならないと本物の盗賊じゃないな。盗賊はLUKがメインステータスで、DEXが補助ステータスになる。ステータスの上げ方が分からなかったら#b自動配分#kを使用するといい。");
                        say(nt, true, true);
                        return true;
                    }
                    case 3 -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("そして、プレゼントがあるぞ。君の装備とETCアイテムの保管箱の個数を増やしてあげたんだ。インベントリが広いと旅が楽しくなるもの。ふふ…");
                        say(nt, true, true);
                        return true;
                    }
                    case 4 -> {
                        // TODO : 武器の支給
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("さて！俺が君に教えられるのはここまでだ。君に役立つような武器もいくつか渡したから旅をしながら自身を鍛えてみてくれ。");
                        say(nt, true, false);
                        return false;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case STEP_2ND -> {
                // 試験前
                if (!checkItem(this.item_proof_of_hero)) {
                    switch (status) {
                        case 0 -> {
                            // 原文ママ
                            askYesNo("ほう…君は見違えるように成長したな！　この前のひ弱な姿はどこかに行って今は戦士としての威厳が満ち溢れているではないか！　さあ…どうだ？　ここでさらに強くなりたくないかね？　簡単な試験さえパスしたら君をより一層強くしてあげるよ！　やってみないかね？");
                            return true;
                        }
                        case 1 -> {
                            // 原文ママ
                            NpcScriptTag nt = new NpcScriptTag();
                            nt.add("よく考えたな。君は強そうに見えるがそれが本物なのか確認してみる必要がある。簡単なテストだから君なら充分にパスすることができるだろう。さあ…まずここで私の手紙を受け取ってくれ。忘れないように気を付けろよ。");
                            say(nt, false, true);
                            return true;
                        }
                        case 2 -> {
                            // TODO : 手紙 (item_letter) の受け取り, 元のスクリプトは所持確認せずに毎回手紙を渡していた
                            // インベントリに空きがない場合 : インベントリを開けてください
                            // 原文ママ
                            NpcScriptTag nt = new NpcScriptTag();
                            nt.add("この手紙をペリオン辺り#b#m" + this.map_instructor + "##kのどこかにいる#b#p" + this.npc_instructor + "##kに伝えるように。忙しい私の代わりに教官の仕事をしてくれる有り難い人だ。手紙を伝えてくれれば君をワシの代わりにテストしてくれる。詳しいことは彼に直接聞けばわかる。それじゃ無事に帰って来てほしい。");
                            say(nt, true, false);
                            return false;
                        }
                        default -> {
                        }
                    }
                    return false;
                }
                // 試験合格
                switch (status) {
                    case 0 -> {
                        // 原文ママ
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("おお…無事に帰ってきたね！　君ならそんな試験くらいは簡単にパスすると思ったね。君が本当に強い戦士ということを認めよう。さあ…君をもっと強くしてあげる。その前に…！　君は3つの道の中で一つを選択しなければならない。難しいことかも知れないが…分からないことがあったら聞きなさい。");
                        say(nt, false, true);
                        return true;
                    }
                    case 1 -> {
                        // 原文ママ
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("さあ…決定が終わったら…一番下にある[職業を選択します！]を選択しなさい。");
                        for (int i = 0; i < this.job_2nd_names.length; i++) {
                            nt.addMenu(i + 1, this.job_2nd_names[i] + "について説明して下さい。");
                        }
                        nt.addMenu(MENU_SELECT_JOB, "職業を選択します！");
                        askMenu(nt);
                        return true;
                    }
                    case 2 -> {
                        // 転職を断った
                        if (this.asked) {
                            say("まだ心の準備ができてないか？");
                            return false;
                        }
                        if (answer != MENU_SELECT_JOB) {
                            // TODO : 各職業の説明
                            return false;
                        }
                        // 原文ママ
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("さあ…心は決めたのか？　2次転職したい職業を選択しなさい。");
                        nt.addMenu(0, "ソードマン(Swordman)");
                        nt.addMenu(1, "ページ(Page)");
                        nt.addMenu(2, "スピアマン(Spearman)");
                        askMenu(nt);
                        return true;
                    }
                    case 3 -> {
                        if (answer < 0 || this.job_2nd.length <= answer) {
                            return false;
                        }
                        this.job_index = answer;
                        this.asked = true;
                        // 原文ママ (元のスクリプトはソードマンのみ)
                        askYesNo("#b" + this.job_2nd_names[this.job_index] + "#kで2次転職したいのだな？　一度決めれば他の2次転職職業には転職することができないぞ。その決心…間違いないかね？");
                        return true;
                    }
                    case 4 -> {
                        // TODO : 元のスクリプトはソードマンのみ転職可能だった, ページとスピアマンの転職後の文章が不明
                        // TODO : アイテム欄の拡張
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_proof_of_hero, 1, true, false);
                        getPlayer().changeJob(this.job_2nd[this.job_index]);
                        // 原文ママ
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("よし！　君はこれから#b" + this.job_2nd_names[this.job_index] + "#kだ！　ソードマンは強さを求めながら絶えず闘うもの…決してそのことを忘れずに前に進みなさい。さあ私の力で君をもっと強くしよう。");
                        say(nt, false, true);
                        return true;
                    }
                    case 5 -> {
                        // 原文ママ
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("君に今からソードマンが学べるスキルが書かれている本を与えよう。その本にはさまざまなファイターと係わるスキルが書かれている。それに消費、ETCにアイテムを保管できる数も増えたぞ。それぞれ1ライン増えているだろう。最大HPも増えたしね…一回確認しなさい。");
                        say(nt, true, true);
                        return true;
                    }
                    case 6 -> {
                        // 原文ママ
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("君に少しの#bSP#kをあげたから、#bスキルメニュー#kを開けてみろ。新たに得た2次スキルをあげることができる。ただしはじめから全てをあげることはできない。他のスキルをある程度上げてこそ学ぶことができるスキルもあるからな。覚えておくように。");
                        say(nt, true, false);
                        return false;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case STEP_3RD, STEP_4TH -> {
                // TODO : 3次転職 (job_3rd) と 4次転職, 元のスクリプトはテスト用の選択肢で職業IDを直接選択していた
                return false;
            }
            default -> {
            }
        }
        // TODO : 文章が盗賊転職のもの (ダークロード) になっている
        say("隠密な会話…？隠密な行動が特技の盗賊だが、それは敵と戦う程度で充分だ。");
        return false;
    }
}
