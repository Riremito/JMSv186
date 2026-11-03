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
import odin.server.quest.MapleQuest;

/**
 *
 * @author Riremito
 */
public class NpcDancesWithBalrog extends NpcScriptMan {

    private static final int MODE_NONE = 0;
    private static final int MODE_1ST = 1;
    private static final int MODE_2ND_TEST = 2;
    private static final int MODE_2ND = 3;
    private static final int MODE_QUESTION = 4;
    private static final int MODE_SAYS = 5;
    private static final int MENU_SELECT_JOB = 0;
    private final int job_1st_level = 10;
    private final int job_1st_str = 35;
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
    private final int item_weapon = 1302077; // 初歩戦士の剣
    private final int item_letter = 4031008; // コブシを開いて立ての手紙
    private final int item_proof_of_hero = 4031012; // 英雄の証
    private final int item_black_charm = 4031059; // 黒い護符
    private final int item_necklace_of_strength = 4031057; // 強靭のネックレス
    private final int quest_job_3rd = 7500; // 3次転職
    private final int npc_instructor = 1072000; // 戦士転職教官
    private final int map_instructor = 102020300; // ぺリオン : 西の岩山4
    // 1次転職後の文章 (翻訳)
    private final String[] says_1st = {
        "これで君はずっと強くなった。それに、すべてのインベントリのスロットが増えている。正確には1列分だ。確認してみるといい。#bSP#kも少し渡しておいた。画面左下の#bスキルメニュー#kを開くと、SPを使って習得できるスキルが表示される。注意しておくが、一度に全部上げることはできない。いくつかのスキルを先に習得してからでないと使えないスキルもある。",
        "もう一つ注意だ。職業を選んだら、できるだけ長く生き延びるようにしろ。死ぬと経験値を失ってしまう。苦労して稼いだ経験値を失いたくはないだろう？私が教えられるのはこれだけだ…ここから先は、もっともっと上を目指して努力し続けるんだ。今よりも力がついたと感じたら、また私に会いに来てくれ。",
        "ああ、それと…戦士について何か疑問があれば、いつでも聞きに来てくれ。何でも知っているわけではないが、知っていることなら何でも教えてやろう。それまでな…"
    };
    // 2次転職の職業の説明 (翻訳)
    private final String[][] says_2nd_about = {
        {
            "ファイターの役割について説明しよう。ファイターは最も一般的なタイプの戦士だ。後で習得できる上級スキルがあるので、使う武器は#b剣#kと#b斧#kだ。両方の武器を同時に使うのはやめて、自分が一番気に入った方だけを使うといい…",
            "さらに、ファイターには#b#q1101006##kや#b#q1101007##kといったスキルもある。#b#q1101006##kは、自分とパーティーの武器を強化できるスキルだ。突然の力の爆発で敵をなぎ倒せるので、とても役に立つ。欠点は、防御力が少し下がってしまうことだ。",
            "#b#q1101007##kは、敵の武器から受けたダメージの一部を跳ね返せるスキルだ。攻撃が強ければ強いほど、相手が受ける反撃ダメージも大きくなる。接近戦を好む者には心強いだろう。どうだ？ファイターってかっこいいだろう？"
        },
        {
            "ページの役割について説明しよう。ページは最初の一歩を踏み出したばかりの騎士見習いだ。主に#b剣#kや#b鈍器#kを使う。両方の武器を使うのはよくないので、どちらか一方を選んで使い続けた方がいい。",
            "さらに、#b#q1201006##kや#b#q1101007##kといったスキルも習得できる。#b#q1201006##kは、周りにいるすべての敵の攻撃力と防御力を一定時間下げるスキルだ。攻撃力の高い強力なモンスターにとても有効で、パーティープレイでも役に立つ。",
            "#b#q1101007##kは、モンスターから受けたダメージの一定量を一定時間跳ね返せるスキルだ。受けるダメージが大きいほど、敵に与えるダメージも大きくなる。接近戦に特化した戦士にぴったりのスキルだ。どうだ？ページってかっこいいだろう？"
        },
        {
            "スピアマンの役割について説明しよう。#b槍#kや#b矛#kのような長い武器の扱いに特化した職業だ。どちらの武器でも習得できる便利なスキルがたくさんあるが、どちらか一方を選んで使い続けることをおすすめする。",
            "さらに、#b#q1301006##kや#b#q1301007##kといったスキルも習得できる。#b#q1301006##kは、自分とパーティーメンバーの物理防御力と魔法防御力を一定時間強化できるスキルだ。両手武器を使うため同時に防御ができないスピアマンにとって、とても便利なスキルだ。",
            "#b#q1301007##kは、自分とパーティーの最大HPと最大MPを一時的に強化できるスキルだ。最大160%まで上げられるので、本当に強力な敵に立ち向かうときに特に役立つ。どうだ？スピアマンってかっこいいと思わないか？"
        }
    };
    // 2次転職後の文章
    private final String[][] says_2nd = {
        // 原文ママ
        {
            "よし！　君はこれから#bソードマン#kだ！　ソードマンは強さを求めながら絶えず闘うもの…決してそのことを忘れずに前に進みなさい。さあ私の力で君をもっと強くしよう。",
            "君に今からソードマンが学べるスキルが書かれている本を与えよう。その本にはさまざまなファイターと係わるスキルが書かれている。それに消費、ETCにアイテムを保管できる数も増えたぞ。それぞれ1ライン増えているだろう。最大HPも増えたしね…一回確認しなさい。",
            "君に少しの#bSP#kをあげたから、#bスキルメニュー#kを開けてみろ。新たに得た2次スキルをあげることができる。ただしはじめから全てをあげることはできない。他のスキルをある程度上げてこそ学ぶことができるスキルもあるからな。覚えておくように。"
        },
        // 翻訳
        {
            "よし！これで君は#bページ#kになった！ページは戦士としての知性と勇気を兼ね備えている…正しい心構えで正しい道を歩んでくれることを願う…今より強くなれるよう手助けしよう。",
            "ページとして習得できるスキルのリストが載った本を渡しておいた。その本には、ページが習得できるさまざまなスキルが載っている。消費インベントリとETCインベントリにも新しいスロットが1列追加された。最大MPも増えているはずだ…確認してみるといい。",
            "#bSP#kも少し渡しておいた。左下にある#bスキルメニュー#kを開いてみてくれ。新しく習得した2次スキルを強化できる。注意しておくが、一度に全部強化することはできない。ほかのスキルを習得してからでないと使えないスキルもある。それを忘れないようにな。",
            "ページは強くなければならない。だが、その力を乱用して、自分より弱い者に向けてはいけないことを覚えておけ。その大きな力を正しく使うんだ…正しく使うことは、ただ強くなるよりずっと難しいからな。十分に成長したら、また私を訪ねてくれ。"
        },
        // 翻訳
        {
            "よし！これで君は#bスピアマン#kになった！スピアマンは闇の力を使い、常に影の中から敵を打ち倒す…旅の間、自分自身と自分の驚くべき力を信じるんだ…今より強くなれるよう手助けしよう。",
            "スピアマンとして習得できるスキルのリストが載った本を渡しておいた。その本には、スピアマンが習得できるさまざまなスキルが載っている。消費インベントリとETCインベントリにも新しいスロットが1列追加された。最大MPも増えているはずだ…確認してみるといい。",
            "#bSP#kも少し渡しておいた。左下にある#bスキルメニュー#kを開いてみてくれ。新しく習得した2次スキルを強化できる。注意しておくが、一度に全部強化することはできない。ほかのスキルを習得してからでないと使えないスキルもある。それを忘れないようにな。",
            "スピアマンは強くなければならない。だが、その力を乱用して、自分より弱い者に向けてはいけないことを覚えておけ。その大きな力を正しく使うんだ…正しく使うことは、ただ強くなるよりずっと難しいからな。十分に成長したら、また私を訪ねてくれ。待っているぞ。"
        }
    };
    // 戦士についての質問 (翻訳)
    private final String[] questions = {
        "戦士の一般的な特徴は？",
        "戦士の武器は？",
        "戦士の防具は？",
        "戦士が使えるスキルは？"
    };
    // 翻訳
    private final String[][] says_questions = {
        {
            "戦士の役割について説明しよう。戦士は驚くべき体力と力を持っている。モンスターの攻撃から身を守る術も心得ているので、モンスターとの接近戦に最も向いているんだ。体力が高いから、簡単には死なないぞ。",
            "ただし、モンスターに正確に攻撃を当てるには、DEXもそれなりに必要だ。だからSTRばかり上げることに集中しないようにな。早く強くなりたいなら、より強いモンスターに挑むことをすすめる。"
        },
        {
            "戦士が使う武器について説明しよう。戦士は斬る、突く、叩くといった攻撃ができる武器を使う。弓や投げ武器のような武器は使えない。小さな杖などなおさらだ。",
            "最も一般的な武器は、剣、鈍器、矛、槍、斧などだ…どの武器にも長所と短所があるから、よく調べてから選ぶといい。今のところは、攻撃力の高いものを使ってみるといいだろう。"
        },
        {
            "戦士が使う防具について説明しよう。戦士は力が強く体力もあるので、重くて頑丈な防具を身につけることができる。見た目はあまりよくないが…最高の防具としての役目はしっかり果たしてくれる。",
            "特に盾は戦士にぴったりだ。  ただし、両手武器を持っている場合は盾を使えないことを覚えておいてくれ。難しい選択になるのはわかっているが…"
        },
        {
            "戦士が使えるスキルは、その驚くべき体力と力を活かすためのものだ。接近戦を強化するスキルが一番役に立つだろう。HPを回復できるスキルもある。それを極めておくといい。",
            "使える攻撃スキルは#b#q1001004##kと#b#q1001005##kの2つだ。#q1001004#は、1体の敵に大きなダメージを与えるスキルだ。このスキルは最初から強化できる。",
            "一方、#q1001005#はそれほどダメージは大きくないが、範囲内の複数の敵を一度に攻撃できる。#q1001004#を一度強化してからでないと使えない。どうするかは君次第だ。"
        }
    };
    // 3次転職 力の試験の開始 (翻訳)
    private final String[] says_3rd_start = {
        "君を待っていたよ。数日前、オシリアの#bタイラス#kから君のことを聞いたんだ。さて…君の力を試してみたい。アリの巣の近くに秘密の通路がある。そこを通れるのは君だけだ。中に入ると、もう一人の私に会うだろう。そいつを倒して、#b#t4031059##kを持ってきてくれ。",
        "もう一人の私はかなり強いぞ。特殊なスキルをたくさん使うから、接近戦で戦わなければならない。ただし、秘密の通路には長くいられない。できるだけ早く倒すことが肝心だ。さて…幸運を祈る！#b#t4031059##kを持ってきてくれるのを待っているよ。"
    };
    // 職業ごとの挨拶 (翻訳)
    private final int[] greeting_jobs = {
        110,
        120,
        130,
        111,
        121,
        131,
        112,
        122,
        132
    };
    private final String[] greeting_texts = {
        "おお！君か！どうだ？ファイターの生活は？君は…前よりずっと強くなったようだな！これからも鍛錬を続けてくれよ。",
        "おお…君か！どうだ？ページの生活は？まだ見習いなのはわかっているが、もうすぐ修行も終わって、騎士と呼ばれるようになるだろう！",
        "おお…君か！どうだ？スピアマンの生活は？熱心に修行を続けるんだ。いつか比類なき騎士になれるだろうからな…",
        "おお…ついに#bクルセイダー#kになったか…私を失望させないとわかっていたぞ。それで、クルセイダーとしての生活はどうだ？これからも精進して、さらに修行を積んでくれ。",
        "おお…ついに#bナイト#kになったか…私を失望させないとわかっていたぞ。それで、ナイトとしての生活はどうだ？これからも精進して、さらに修行を積んでくれ。",
        "おお…ついに#bドラゴンナイト#kになったか…私を失望させないとわかっていたぞ。それで、ドラゴンナイトとしての生活はどうだ？これからも精進して、さらに修行を積んでくれ。",
        "おお…ついに#bヒーロー#kになったか…私を失望させないとわかっていたぞ。それで、ヒーローとしての生活はどうだ？これからも精進して、さらに修行を積んでくれ。",
        "おお…ついに#bパラディン#kになったか…私を失望させないとわかっていたぞ。それで、パラディンとしての生活はどうだ？これからも精進して、さらに修行を積んでくれ。",
        "おお…ついに#bダークナイト#kになったか…私を失望させないとわかっていたぞ。それで、ダークナイトとしての生活はどうだ？これからも精進して、さらに修行を積んでくれ。"
    };
    // 転職中に条件が切り替わってしまうためNPC会話開始時に判定する
    private int mode = -1;
    private String[] says = null;
    private int says_status = 0;
    private int job_index = -1;
    private int about_index = -1;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        if (this.mode == -1) {
            int job = getPlayer().getJob();
            int level = getPlayer().getLevel();
            String quest_data = getPlayer().getQuest(MapleQuest.getInstance(this.quest_job_3rd)).getCustomData();
            boolean job_2nd_now = false;
            for (int job_id : this.job_2nd) {
                if (job == job_id) {
                    job_2nd_now = true;
                    break;
                }
            }
            this.mode = MODE_NONE;
            if ("s".equals(quest_data) && job_2nd_now) {
                // 3次転職 力の試験の開始
                MapleQuest.getInstance(this.quest_job_3rd).forceStart(getPlayer(), getNpcId(), "p1");
                this.mode = MODE_SAYS;
                this.says = this.says_3rd_start;
            } else if ("p1".equals(quest_data)) {
                if (!checkItem(this.item_black_charm)) {
                    // 翻訳
                    say("アリの巣の近くに秘密の通路がある。そこを通れるのは君だけだ。中に入ると、もう一人の私に会うだろう。そいつを倒して、#b#t" + this.item_black_charm + "##kを持ってきてくれ。");
                    return false;
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_necklace_of_strength, 1, "")) {
                    // 翻訳
                    say("うーん…おかしいな。本当に#b#t" + this.item_black_charm + "##kを持っているかい？持っているなら、アイテムタブに空きスロットがあるか確認してくれ。");
                    return false;
                }
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_black_charm, 1, true, false);
                MapleInventoryManipulator.addById(getPlayer(), this.item_necklace_of_strength, (short) 1);
                MapleQuest.getInstance(this.quest_job_3rd).forceStart(getPlayer(), getNpcId(), "p2");
                // 翻訳
                say("おお…もう一人の私を倒して、#b#t" + this.item_black_charm + "##kを持ってきてくれたんだな。素晴らしい！これで君の力は確かに証明された。力の面では、もう3次転職に進んでも大丈夫だ。約束どおり、#b#t" + this.item_necklace_of_strength + "##kを渡そう。このネックレスをオシリアの#bタイラス#kに渡せば、3次転職のための2つ目の試験を受けられる。幸運を祈る～。");
                return false;
            } else if ("p2".equals(quest_data)) {
                if (checkItem(this.item_necklace_of_strength)) {
                    // 翻訳
                    say("このネックレスをオシリアの#bタイラス#kに渡せば、3次転職のための2つ目の試験を受けられる。幸運を祈る～！");
                    return false;
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_necklace_of_strength, 1, "")) {
                    // 翻訳
                    say("うーん…おかしいな。アイテムタブに空きスロットがあるか確認してくれ。");
                    return false;
                }
                MapleInventoryManipulator.addById(getPlayer(), this.item_necklace_of_strength, (short) 1);
                // 翻訳
                say("ああ！#b#t" + this.item_necklace_of_strength + "##kをなくしたのか？気をつけろと言っただろう…まったく、もう一つやるよ…「また」な。今度は気をつけてくれ。これがないと3次転職の試験を受けられないぞ。");
                return false;
            } else if (job == 0) {
                this.mode = MODE_1ST;
            } else if (job == this.job_1st) {
                if (level < this.job_2nd_level) {
                    this.mode = MODE_QUESTION;
                } else if (checkItem(this.item_letter)) {
                    // 翻訳
                    say("まだその人に会っていないのか？#m102000000#の近く、#b#m" + this.map_instructor + "##kのあたりにいる#b#p" + this.npc_instructor + "##kを探すんだ。手紙を渡せば、何をすべきか教えてくれるかもしれない。");
                    return false;
                } else if (checkItem(this.item_proof_of_hero)) {
                    this.mode = MODE_2ND;
                } else {
                    this.mode = MODE_2ND_TEST;
                }
            } else {
                for (int i = 0; i < this.greeting_jobs.length; i++) {
                    if (job == this.greeting_jobs[i]) {
                        say(this.greeting_texts[i]);
                        return false;
                    }
                }
                // 翻訳
                say("なんと見事な肉体！なんと驚くべき力！戦士こそ最高だ！！！！どうだ？戦士として転職したいか？？");
                return false;
            }
        }
        // 連続した会話
        if (this.says != null) {
            int index = status - this.says_status;
            if (index < 0 || this.says.length <= index) {
                return false;
            }
            boolean next = index + 1 < this.says.length;
            say(this.says[index], false, next);
            return next;
        }
        switch (this.mode) {
            case MODE_1ST -> {
                switch (status) {
                    case 0 -> {
                        // 転職を断った
                        if (this.asked) {
                            // 翻訳
                            say("そうか？もっとよく考える時間が必要か？好きにするといい…いいかげんに決めていいことではないからな。決めたら話しかけてくれ。");
                            return false;
                        }
                        // 翻訳
                        say("戦士になりたいのか？そのためにはいくつかの条件を満たす必要がある。#bレベル" + this.job_1st_level + "以上で、STRが" + this.job_1st_str + "以上#k必要だ。どれどれ…", false, true);
                        return true;
                    }
                    case 1 -> {
                        if (getPlayer().getLevel() < this.job_1st_level || getPlayer().getStat().getStr() < this.job_1st_str) {
                            // 翻訳
                            say("まだ君に戦士の素質があるとは思えない。戦士になるにはたくさん修行しなければならないし、そうでなければ務まらないだろう。もっとずっと強くなってから、私を訪ねてくれ。");
                            return false;
                        }
                        this.asked = true;
                        // 翻訳
                        askYesNo("君には間違いなく戦士の素質がある。まだその域には達していないかもしれないが、君の中に戦士の姿が見えるぞ。どうだ？戦士になりたいか？");
                        return true;
                    }
                    case 2 -> {
                        if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_weapon, 1, "")) {
                            // 翻訳
                            say("ふむ…装備ウィンドウに空きスロットがあるか確認してくれ。君の活躍へのご褒美として武器を渡したいんだ。");
                            return false;
                        }
                        // 翻訳
                        say("今日から君は戦士だ！努力を続けるんだぞ…今より強くなるために修行を続けてくれることを願って、君の能力を少し高めてやろう。ハアアアアッ！！", false, true);
                        return true;
                    }
                    case 3 -> {
                        if (getPlayer().getJob() != 0 || !MapleInventoryManipulator.checkSpace(getPlayer(), this.item_weapon, 1, "")) {
                            // 翻訳
                            say("ふむ。装備ウィンドウに少なくとも1つ空きスロットがあるか確認してくれ。1次転職のご褒美として武器を渡したいんだ。");
                            return false;
                        }
                        MapleInventoryManipulator.addById(getPlayer(), this.item_weapon, (short) 1);
                        getPlayer().changeJob(this.job_1st);
                        getPlayer().expandInventory((byte) 1, 4);
                        getPlayer().expandInventory((byte) 2, 4);
                        getPlayer().expandInventory((byte) 3, 4);
                        getPlayer().expandInventory((byte) 4, 4);
                        this.says = this.says_1st;
                        this.says_status = status;
                        say(this.says[0], false, true);
                        return true;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case MODE_2ND_TEST -> {
                switch (status) {
                    case -1 -> {
                        // 翻訳
                        say("そうか？早く強くなれば、旅でとても役に立つんだが…将来気が変わったら、いつでもここに来てくれ。今よりずっと強くしてやることを忘れないでくれ。");
                        return false;
                    }
                    case 0 -> {
                        // 原文ママ
                        askYesNo("ほう…君は見違えるように成長したな！　この前のひ弱な姿はどこかに行って今は戦士としての威厳が満ち溢れているではないか！　さあ…どうだ？　ここでさらに強くなりたくないかね？　簡単な試験さえパスしたら君をより一層強くしてあげるよ！　やってみないかね？");
                        return true;
                    }
                    case 1 -> {
                        // 原文ママ
                        say("よく考えたな。君は強そうに見えるがそれが本物なのか確認してみる必要がある。簡単なテストだから君なら充分にパスすることができるだろう。さあ…まずここで私の手紙を受け取ってくれ。忘れないように気を付けろよ。", false, true);
                        return true;
                    }
                    case 2 -> {
                        if (!checkItem(this.item_letter)) {
                            if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_letter, 1, "")) {
                                // 翻訳
                                say("ふむ…ETCインベントリに十分な空きがないから、手紙を渡せない。インベントリに1つか2つ空きを作ってから戻ってきてくれ。手紙がないと試験を受けられないからな。");
                                return false;
                            }
                            MapleInventoryManipulator.addById(getPlayer(), this.item_letter, (short) 1);
                        }
                        // 原文ママ
                        say("この手紙をペリオン辺り#b#m" + this.map_instructor + "##kのどこかにいる#b#p" + this.npc_instructor + "##kに伝えるように。忙しい私の代わりに教官の仕事をしてくれる有り難い人だ。手紙を伝えてくれれば君をワシの代わりにテストしてくれる。詳しいことは彼に直接聞けばわかる。それじゃ無事に帰って来てほしい。");
                        return false;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case MODE_2ND -> {
                switch (status) {
                    case 0 -> {
                        // 原文ママ
                        say("おお…無事に帰ってきたね！　君ならそんな試験くらいは簡単にパスすると思ったね。君が本当に強い戦士ということを認めよう。さあ…君をもっと強くしてあげる。その前に…！　君は3つの道の中で一つを選択しなければならない。難しいことかも知れないが…分からないことがあったら聞きなさい。", false, true);
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
                        if (1 <= answer && answer <= this.says_2nd_about.length) {
                            this.about_index = answer - 1;
                            this.says = this.says_2nd_about[this.about_index];
                            this.says_status = status;
                            say(this.says[0], false, true);
                            return true;
                        }
                        if (answer != MENU_SELECT_JOB) {
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
                        // 原文ママ
                        askYesNo("#b" + this.job_2nd_names[this.job_index] + "#kで2次転職したいのだな？　一度決めれば他の2次転職職業には転職することができないぞ。その決心…間違いないかね？");
                        return true;
                    }
                    case 4 -> {
                        if (getPlayer().getRemainingSp() > (getPlayer().getLevel() - this.job_2nd_level) * 3) {
                            // 翻訳
                            say("ふむ…#bSP#kが多すぎる…そんなにSPを残したままでは2次転職できない。1次スキルにもっとSPを使ってから、また来てくれ。");
                            return false;
                        }
                        if (getPlayer().getJob() != this.job_1st || !checkItem(this.item_proof_of_hero)) {
                            // 翻訳
                            say("ふむ…本当に#p" + this.npc_instructor + "#からもらった#b#t" + this.item_proof_of_hero + "##kを持っているか？それがないと転職させられないぞ。");
                            return false;
                        }
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_proof_of_hero, 1, true, false);
                        getPlayer().changeJob(this.job_2nd[this.job_index]);
                        getPlayer().expandInventory((byte) 2, 4);
                        getPlayer().expandInventory((byte) 4, 4);
                        this.says = this.says_2nd[this.job_index];
                        this.says_status = status;
                        say(this.says[0], false, true);
                        return true;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case MODE_QUESTION -> {
                switch (status) {
                    case 0 -> {
                        // 翻訳
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("おや、質問があるのか？ ");
                        for (int i = 0; i < this.questions.length; i++) {
                            nt.addMenu(i, this.questions[i]);
                        }
                        askMenu(nt);
                        return true;
                    }
                    case 1 -> {
                        if (answer < 0 || this.says_questions.length <= answer) {
                            return false;
                        }
                        this.says = this.says_questions[answer];
                        this.says_status = status;
                        say(this.says[0], false, true);
                        return true;
                    }
                    default -> {
                    }
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
