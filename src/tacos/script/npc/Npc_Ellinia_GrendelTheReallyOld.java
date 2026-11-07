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
public class Npc_Ellinia_GrendelTheReallyOld extends NpcScriptMan {

    private static final int MODE_NONE = 0;
    private static final int MODE_1ST = 1;
    private static final int MODE_2ND_TEST = 2;
    private static final int MODE_2ND = 3;
    private static final int MODE_QUESTION = 4;
    private static final int MODE_SAYS = 5;
    private static final int MENU_SELECT_JOB = 3;
    private final int job_1st_level = 8;
    private final int job_1st_int = 20;
    private final int job_1st = 200; // 魔法使い
    private final int job_2nd_level = 30;
    private final int[] job_2nd = {
        210, // ウィザード(火・毒)
        220, // ウィザード(氷・雷)
        230 // クレリック
    };
    private final String[] job_2nd_names = {
        "ウィザード（火・毒）",
        "ウィザード（氷・雷）",
        "クレリック"
    };
    private final int item_weapon = 1372043; // 初歩魔法使いのワンド
    private final int item_letter = 4031009; // 'ハインズ'の手紙
    private final int item_proof_of_hero = 4031012; // 英雄の証
    private final int item_black_charm = 4031059; // 黒い護符
    private final int item_necklace_of_strength = 4031057; // 強靭のネックレス
    private final int quest_job_3rd = 7500; // 3次転職
    private final int npc_instructor = 1072001; // 魔法使い転職教官
    private final int map_town = 101000000; // エリニア : エリニア
    private final int map_instructor = 101020000; // エリニア : 北の森
    // 1次転職後の文章 (翻訳)
    private final String[] says_1st = {
        "これであなたははるかに大きな魔力を身につけました。修行を続けて、日々成長してください…時々見守っていますよ…",
        "#bSP#kも少し渡しておきました。画面左下の#bスキルメニュー#kを開くと、SPを使って習得できるスキルが表示されます。注意しておきますが、一度に全部上げることはできません。いくつかのスキルを先に習得してからでないと使えないスキルもあります。",
        "もう一つ注意です。職業を選んだら、できるだけ長く生き延びるようにしてください。死ぬと経験値をすべて失ってしまいます。苦労して稼いだ経験値を失いたくはないでしょう？",
        "OK！私が教えられるのはこれだけです。いろいろな場所へ行って修行し、さらに成長してください。できることはすべてやり尽くして、何か面白いことが必要になったら私を訪ねてください。ここで待っていますよ…",
        "ああ、それと…魔法使いについて何か疑問があれば、いつでも聞いてください。正直なところ何でも知っているわけではありませんが、知っていることなら何でも教えましょう。それまで…"
    };
    // 2次転職の職業の説明 (翻訳)
    private final String[][] says_2nd_about = {
        {
            "ウィザード（火・毒）について説明しましょう。火と毒の魔法を専門としています。自分とパーティー全員の魔力を一定時間高める#b#q2101001##kや、一定の確率で敵のMPを少し吸収できる#b#q2100000##kといったスキルは、攻撃を担う魔法使いには欠かせません。",
            "#b#q2101004##kという攻撃魔法について説明しましょう。敵に炎の矢を放つ魔法で、2次スキルの中で最も強力なスキルです。炎に弱い敵には特に効果的で、ダメージがかなり大きくなります。反対に、炎に強い敵に使うとダメージが半減してしまいます。それを忘れないでください。",
            "#b#q2101005##kという攻撃魔法について説明しましょう。敵に毒の泡を放ち、毒状態にします。その後、時間が経つにつれて敵のHPがどんどん減っていきます。魔法があまり効かなかったり、モンスターのHPが高かったりする場合は、毒を何度も浴びせて倒すのもいい手かもしれません…"
        },
        {
            "ウィザード（氷・雷）について説明しましょう。氷と雷の魔法を専門としています。自分とパーティー全員の魔力を一定時間高める#b#q2101001##kや、一定の確率で敵のMPを少し吸収できる#b#q2100000##kといったスキルは、攻撃を担う魔法使いには欠かせません。",
            "#b#q2201004##kという攻撃魔法について説明しましょう。敵に氷のかけらを放つ魔法で、#q2101004#ほど強力ではありませんが、攻撃を受けた敵は短い間凍りつきます。氷に弱い敵には、ダメージがずっと大きくなります。反対に、氷に強い敵にはダメージがあまり大きくなりません。それを忘れないでください。",
            "#b#q2201005##kという攻撃魔法について説明しましょう。2次の魔法使いのスキルの中で唯一、全体魔法と呼べるもので、複数のモンスターに同時に効果があります。ダメージはそれほど大きくないかもしれませんが、周りにいる複数のモンスターにダメージを与えられるのが利点です。ただし、一度に攻撃できるのは6体までです。それでも素晴らしい攻撃ですよ。"
        },
        {
            "クレリックについて説明しましょう。クレリックは祈りと呪文によって、モンスターに対して聖なる魔法を使います。物理防御力、魔法防御力、命中率、回避率を一時的に高める#b#q2301004##kや、物理ダメージを一定量軽減する#b#q2301003##kといったスキルは、魔法使いの弱点を補ってくれます…",
            "クレリックは、回復魔法を使える唯一の魔法使いです。回復魔法を使えるのはクレリックだけなのです。その魔法は#b#q2301002##kといい、MP、INT、このスキルのレベルが高いほど、多くのHPを回復できます。近くにいるパーティーメンバーにも効果があるので、ポーションに頼らずに狩りを続けられる、とても便利なスキルです。",
            "クレリックには#b#q2301005##kという攻撃魔法もあります。モンスターに聖なる矢を放つ呪文です。効果はそれほど大きくありませんが、ゾンビやそのほかの邪悪なモンスターには大ダメージを与えられます。そうしたモンスターは聖なる攻撃にまったく耐性がないのです。どうですか？面白いでしょう？"
        }
    };
    // 2次転職の確認 (翻訳)
    private final String[] asks_2nd = {
        "#bウィザード（火・毒）#kとして2次転職したいのですね？一度決めたら、後戻りして職業を変えることはできません。本当にそれでいいですか？",
        "#bウィザード（氷・雷）#kとして2次転職したいのですね？一度決めたら、後戻りして職業を変えることはできません…本当にそれでいいですか？",
        "#bクレリック#kとして2次転職したいのですね？一度決めたら、後戻りしてほかの職業を選ぶことはできません…本当にそれでいいですか？"
    };
    // 2次転職を断った (翻訳)
    private final String[] no_texts_2nd = {
        "そうですか？もっとよく考えたいのですね？急がなくていいですよ、急がなくて。いいかげんに決めていいことではありませんから…決めたら話しかけてください…",
        "そうか？もっとよく考えたいんだな？急ぐな、急ぐな。いいかげんに決めていいことではないからな…決めたら話しかけてくれ、いいな？",
        "そうですか？もっとよく考えたいのですね？急がなくていいですよ、急がなくて。いいかげんに決めていいことではありませんから…決めたら話しかけてください…"
    };
    // 2次転職後の文章 (翻訳)
    private final String[][] says_2nd = {
        {
            "今日からあなたは#bウィザード（火・毒）#kです…ウィザードは高い知性と、私たちの周りにある自然の力を使って敵を倒します…研究を続けてください。いつか私自身の力で、あなたをずっと強くしてあげましょう…",
            "ウィザード（火・毒）として習得できるスキルのリストが載った本を渡しておきました…ETCインベントリも1列分拡張し、最大MPも増やしておきました…確認してみてください。",
            "#bSP#kも少し渡しておいた。左下にある#bスキルメニュー#kを開いてみてくれ。新しく習得した2次スキルを強化できる。注意しておくが、一度に全部強化することはできない。ほかのスキルを習得してからでないと使えないスキルもある。それを忘れないようにな。",
            "ウィザードは強くなければなりません。でも、その力を乱用して、自分より弱い者に向けてはいけないことを覚えておいてください。その大きな力を正しく使うのです…正しく使うことは、ただ強くなるよりずっと難しいですから。十分に成長したら、また私を訪ねてください…"
        },
        {
            "さあ、これであなたは#bウィザード（氷・雷）#kになりました…ウィザードは高い知性と、私たちの周りにある自然の力を使って敵を倒します…研究を続けてください。いつか私自身の力で、あなたをずっと強くしてあげましょう…",
            "ウィザード（氷・雷）として習得できるスキルのリストが載った本を渡しておきました…ETCインベントリも1列分拡張しました。最大MPも増えています。確認してみてください。",
            "#bSP#kも少し渡しておいた。左下にある#bスキルメニュー#kを開いてみてくれ。新しく習得した2次スキルを強化できる。注意しておくが、一度に全部強化することはできない。ほかのスキルを習得してからでないと使えないスキルもある。それを忘れないようにな。",
            "ウィザードは強くなければなりません。でも、その力を乱用して、自分より弱い者に向けてはいけないことを覚えておいてください。その大きな力を正しく使うのです…正しく使うことは、ただ強くなるよりずっと難しいですから。十分に成長したら、また私を訪ねてください。待っていますよ…"
        },
        {
            "さあ、今日からあなたは#bクレリック#kです。クレリックは神への揺るぎない信仰で、あらゆる生命に命を吹き込みます。信仰を磨くことを決して怠らないでください…いつか、あなたがずっと強くなれるよう手助けしましょう…",
            "クレリックとして習得できるスキルのリストが載った本を渡しておきました…ETCインベントリも1列分拡張し、最大MPも増やしておきました…確認してみてください…",
            "#bSP#kも少し渡しておいた。左下にある#bスキルメニュー#kを開いてみてくれ。新しく習得した2次スキルを強化できる。注意しておくが、一度に全部強化することはできない。ほかのスキルを習得してからでないと使えないスキルもある。それを忘れないようにな。",
            "クレリックには何よりも信仰が必要です。神への信仰を持ち続け、すべての人をふさわしい敬意と尊厳をもって扱ってください。努力を続ければ、いつかもっと大きな魔力と聖なる力を手にするでしょう…さあ…もっと巡礼を重ねたら、また私を訪ねてください。待っていますよ…"
        }
    };
    // 魔法使いについての質問 (翻訳)
    private final String[] questions = {
        "魔法使いの基本的な特徴は？",
        "魔法使いの武器は？",
        "魔法使いの防具は？",
        "魔法使いが使えるスキルは？"
    };
    // 翻訳
    private final String[][] says_questions = {
        {
            "魔法使いについてもう少しお話ししましょう。魔法使いは高い魔力と知性を使います。私たちの周りにある自然の力を使って敵を倒せますが、接近戦にはとても弱いのです。体力も高くないので、くれぐれも倒されないよう気をつけてください。",
            "離れた場所からモンスターを攻撃できるのは、大きな強みになります。魔法を正確に当てたいなら、INTを上げるようにしてください。知性が高いほど、魔法をうまく扱えるようになりますよ…"
        },
        {
            "魔法使いの武器についてお話ししましょう。実のところ、魔法使いにとって武器で敵を攻撃することはあまり意味がありません。魔法使いには力も器用さもないので、カタツムリ1匹倒すのにも苦労するかもしれません。",
            "でも、魔力となると話は「別」です。魔法使いは鈍器、スタッフ、ワンドを使います。鈍器は、まあ、力で攻撃するにはいいのですが…魔法使いにはまったくおすすめしません。",
            "実は、スタッフとワンドこそが一番の武器なんです。特別な魔力が宿っているので、魔法使いの能力を高めてくれます。魔力の高い武器を持つのがいいでしょう…"
        },
        {
            "魔法使いの防具についてお話ししましょう。正直なところ、魔法使いは体力も力も少ないので、身につけられる防具はあまり多くありません。防御力も高くないので、役に立つかどうか…",
            "ただ、防具の中には魔力を打ち消す力を持つものもあるので、魔法攻撃から身を守ってくれます。大した助けにはなりませんが、何も着ないよりはましです…ですから、余裕があればぜひ買っておいてください…"
        },
        {
            "魔法使いが使えるスキルは、魔法使いの持つ高い知性と魔力を活かすものです。体力の低い魔法使いが死なないように守ってくれる、マジックガードやマジックアーマーもあります。",
            "攻撃スキルは#b#q2001004##kと#b#q2001005##kです。まず、#q2001004#は最小限のMPで敵に大きなダメージを与えるスキルです。",
            "一方、#q2001005#はMPを多く使って敵を「2回」攻撃します。ただし、#q2001004#を1回以上強化してからでないと使えません。それを忘れないでください。どうするかはあなた次第です…"
        }
    };
    // 3次転職 力の試験の開始 (翻訳)
    private final String[] says_3rd_start = {
        "あなたを待っていました。数日前、オシリアの#bロベイラ#kからあなたのことを聞きました。さて…あなたの力を試したいと思います。エリニアの森の近くに秘密の通路があります。そこを通れるのはあなただけです。中に入ると、もう一人の私に会うでしょう。それを倒して、#b#t4031059##kを持ってきてください。",
        "もう一人の私はかなり強いぞ。特殊なスキルをたくさん使うから、接近戦で戦わなければならない。ただし、秘密の通路には長くいられない。できるだけ早く倒すことが肝心だ。さて…幸運を祈る！#b#t4031059##kを持ってきてくれるのを待っているよ。"
    };
    // 職業ごとの挨拶 (翻訳)
    private final int[] greeting_jobs = {
        210, // ウィザード(火・毒)
        220, // ウィザード(氷・雷)
        230, // クレリック
        211, // メイジ(火・毒)
        221, // メイジ(氷・雷)
        231 // プリースト
    };
    private final String[] greeting_texts = {
        "おお…あなたですね…ウィザードとしての生活はどうですか？あなた…炎の矢をすっかり使いこなしているようですね…これからも精進して、さらに修行を積んでください。",
        "おお…あなたですね…ウィザードとしての生活はどうですか？あなた…氷と雷を楽々と操れるようですね…これからも精進して、さらに修行を積んでください。",
        "おお…あなたですね…クレリックとしての生活はどうですか？あなた…聖なる魔法を楽々と操れるようですね…これからも精進して、さらに修行を積んでください。",
        "おお…ついに#bメイジ#kになりましたね…私を失望させないとわかっていましたよ。それで、メイジとしての生活はどうですか？これからも精進して、さらに修行を積んでください。",
        "おお…ついに#bメイジ#kになりましたね…私を失望させないとわかっていましたよ。それで、メイジとしての生活はどうですか？これからも精進して、さらに修行を積んでください。",
        "おお…ついに#bプリースト#kになりましたね…私を失望させないとわかっていましたよ。それで、プリーストとしての生活はどうですか？これからも精進して、さらに修行を積んでください。"
    };
    // 転職中に条件が切り替わってしまうためNPC会話開始時に判定する
    private int mode = -1;
    private String[] says = null;
    private int says_status = 0;
    private int job_index = -1;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        if (this.mode == -1) {
            int job = getJob();
            int level = getLevel();
            String quest_data = getQuestData(this.quest_job_3rd);
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
                setQuestData(this.quest_job_3rd, "p1");
                this.mode = MODE_SAYS;
                this.says = this.says_3rd_start;
            } else if ("p1".equals(quest_data)) {
                if (!checkItem(this.item_black_charm)) {
                    // 翻訳
                    say("エリニアの森の近くに秘密の通路があります。そこを通れるのはあなただけです。中に入ると、もう一人の私に会うでしょう。それを倒して、#b#t" + this.item_black_charm + "##kを持ってきてください。もう一人の私はかなり強いですよ。特殊なスキルをたくさん使うので、接近戦で戦わなければなりません。ただし、秘密の通路には長くいられません。できるだけ早く倒すことが肝心です。さて…幸運を祈ります！#b#t" + this.item_black_charm + "##kを持ってきてくれるのを待っていますよ。");
                    return false;
                }
                if (!canHold(this.item_necklace_of_strength, 1)) {
                    // 翻訳
                    say("うーん…おかしいな。本当に#b#t" + this.item_black_charm + "##kを持っているかい？持っているなら、アイテムタブに空きスロットがあるか確認してくれ。");
                    return false;
                }
                removeItem(this.item_black_charm, 1);
                gainItem(this.item_necklace_of_strength, 1);
                setQuestData(this.quest_job_3rd, "p2");
                // 翻訳
                say("おお…もう一人の私を倒して、#b#t" + this.item_black_charm + "##kを持ってきてくれたのですね。素晴らしい！これであなたの力は確かに証明されました。力の面では、3次転職の準備ができています。約束どおり、#b#t" + this.item_necklace_of_strength + "##kを渡しましょう。このネックレスをオシリアの#bロベイラ#kに渡せば、3次転職のための2つ目の試験を受けられます。幸運を祈ります～。");
                return false;
            } else if ("p2".equals(quest_data)) {
                if (checkItem(this.item_necklace_of_strength)) {
                    // 翻訳
                    say("このネックレスをオシリアの#bロベイラ#kに渡せば、3次転職のための2つ目の試験を受けられます。幸運を祈ります～！");
                    return false;
                }
                if (!canHold(this.item_necklace_of_strength, 1)) {
                    // 翻訳
                    say("うーん…おかしいな。アイテムタブに空きスロットがあるか確認してくれ。");
                    return false;
                }
                gainItem(this.item_necklace_of_strength, 1);
                // 翻訳
                say("ああ…#b#t" + this.item_necklace_of_strength + "##kをなくしたのですか？気をつけるように言ったでしょう…まったく、もう一つあげます…「また」ね。今度は気をつけてください。これがないと3次転職の試験を受けられませんよ。");
                return false;
            } else if (job == 0) {
                this.mode = MODE_1ST;
            } else if (job == this.job_1st) {
                if (level < this.job_2nd_level) {
                    this.mode = MODE_QUESTION;
                } else if (checkItem(this.item_letter)) {
                    // 翻訳
                    say("まだ会っていないのですか？#m" + this.map_town + "#の近く、#b#m" + this.map_instructor + "##kのあたりにいる#b#p" + this.npc_instructor + "##kのところへ行って…この手紙を渡せば、何をすべきか教えてくれるでしょう…");
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
                say("自然そのものの力を手にしたいと思いませんか？長く険しい道のりかもしれませんが、最後には必ず報われ、魔法使いの技の頂点に到達できるでしょう…");
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
                            say("そうですか？もっとよく考えたいのですね？急がなくていいですよ、急がなくて。いいかげんに決めていいことではありませんから…決めたら話しかけてください…");
                            return false;
                        }
                        // 翻訳
                        say("魔法使いになりたいのですか？そのためにはいくつかの条件を満たす必要があります。少なくとも#bレベル" + this.job_1st_level + "で、INTが" + this.job_1st_int + "#k必要です。あなたに魔法使いになる素質があるか見てみましょう…", false, true);
                        return true;
                    }
                    case 1 -> {
                        if (getLevel() < this.job_1st_level || getPlayer().getStat().getInt() < this.job_1st_int) {
                            // 翻訳
                            say("魔法使いになるには、もっと修行が必要です。今より強くなれるよう努力してください。もっと強くなったら戻ってきてください。");
                            return false;
                        }
                        this.asked = true;
                        // 翻訳
                        askYesNo("あなたには間違いなく魔法使いの素質があります。まだその域には達していないかもしれませんが、あなたの中に魔法使いの姿が見えます…どうですか？魔法使いになりたいですか？");
                        return true;
                    }
                    case 2 -> {
                        if (!canHold(this.item_weapon, 1)) {
                            // 翻訳
                            say("ふむ…装備インベントリに空きスロットがあるか確認してください。1次転職のご褒美として、修行用の武器を渡したいのです。");
                            return false;
                        }
                        // 翻訳
                        say("さあ、魔法使いのリーダーである私、" + getNpcName() + "が認めたので、これであなたは魔法使いです。大したものではありませんが、私の持っているものを少し分けてあげましょう…", false, true);
                        return true;
                    }
                    case 3 -> {
                        if (getJob() != 0 || !canHold(this.item_weapon, 1)) {
                            // 翻訳
                            say("ふむ…装備ウィンドウに空きスロットがあるか確認してください。1次転職のご褒美として、修行用の武器を渡したいのです。");
                            return false;
                        }
                        gainItem(this.item_weapon, 1);
                        changeJob(this.job_1st);
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
                        say("そうですか？早く強くなれば、旅でとても役に立つのですが…将来気が変わったら、いつでもここに来てください。今よりずっと強くしてあげることを忘れないでくださいね…");
                        return false;
                    }
                    case 0 -> {
                        // 翻訳
                        askYesNo("ふむ…前に会ったときからずいぶん成長しましたね。見違えるほど大きく強くなりました…今のあなたからは魔法使いの風格を感じます…それで…どうですか？今よりもっと強くなりたいですか？簡単な試験を受けるだけです…挑戦してみますか？");
                        return true;
                    }
                    case 1 -> {
                        // 翻訳
                        say("いいでしょう…確かにあなたは強そうですが、その力が本物かどうか確かめる必要があります。試験は難しくありませんから、あなたなら合格できると思いますよ。さあ、この手紙を持っていってください。なくさないでくださいね。", false, true);
                        return true;
                    }
                    case 2 -> {
                        if (!checkItem(this.item_letter)) {
                            if (!canHold(this.item_letter, 1)) {
                                // 翻訳
                                say("インベントリに私の手紙を受け取る空きがないようですね。ETCインベントリに空きを作ってから、もう一度話しかけてください。手紙がなければ試験は受けられませんから。");
                                return false;
                            }
                            gainItem(this.item_letter, 1);
                        }
                        // 翻訳
                        say("この手紙を、#m" + this.map_town + "#の近く、#b#m" + this.map_instructor + "##kのあたりにいる#b#p" + this.npc_instructor + "##kに届けてください。彼が私の代わりに教官を務めています…手紙を渡せば、私の代わりに試験をしてくれます。必要な情報はすべて彼が教えてくれるでしょう。幸運を祈ります…");
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
                        // 翻訳
                        say("無事に戻ってきましたね。よくやりました。あなたなら簡単に試験に合格すると思っていましたよ…さて、これからあなたをさらに強くしてあげましょう。でもその前に…3つの道から1つを選ばなければなりません。簡単な決断ではありませんが…質問があれば遠慮なくどうぞ。", false, true);
                        return true;
                    }
                    case 1 -> {
                        // 翻訳
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("さあ、決めたら下の[職業を選びたい！]をクリックしてください…");
                        for (int i = 0; i < this.job_2nd_names.length; i++) {
                            nt.addMenu(i, this.job_2nd_names[i] + "の特徴について教えて。");
                        }
                        nt.addMenu(MENU_SELECT_JOB, "職業を選びたい！");
                        askMenu(nt);
                        return true;
                    }
                    case 2 -> {
                        // 転職を断った
                        if (this.asked) {
                            say(this.no_texts_2nd[this.job_index]);
                            return false;
                        }
                        if (0 <= answer && answer < this.says_2nd_about.length) {
                            this.says = this.says_2nd_about[answer];
                            this.says_status = status;
                            say(this.says[0], false, true);
                            return true;
                        }
                        if (answer != MENU_SELECT_JOB) {
                            return false;
                        }
                        // 翻訳
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("さて、決まりましたか？2次転職の職業を選んでください。");
                        for (int i = 0; i < this.job_2nd_names.length; i++) {
                            nt.addMenu(i, this.job_2nd_names[i]);
                        }
                        askMenu(nt);
                        return true;
                    }
                    case 3 -> {
                        if (answer < 0 || this.job_2nd.length <= answer) {
                            return false;
                        }
                        this.job_index = answer;
                        this.asked = true;
                        askYesNo(this.asks_2nd[this.job_index]);
                        return true;
                    }
                    case 4 -> {
                        if (getRemainingSp() > (getLevel() - this.job_2nd_level) * 3) {
                            // 翻訳
                            say("ふむ…#bSP#kが多すぎますね…そんなにSPを残したままでは2次転職できません。1次スキルにもっとSPを使ってから、また来てください…");
                            return false;
                        }
                        if (getJob() != this.job_1st || !checkItem(this.item_proof_of_hero)) {
                            // 翻訳
                            say("ふむ…本当に#p" + this.npc_instructor + "#からもらった#b#t" + this.item_proof_of_hero + "##kを持っていますか？よく確認してください…それがないと2次転職できませんから…");
                            return false;
                        }
                        removeItem(this.item_proof_of_hero, 1);
                        changeJob(this.job_2nd[this.job_index]);
                        expandInventory(4, 4);
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
                        nt.add("魔法使いについて何か質問がありますか？");
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
