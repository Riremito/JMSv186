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
public class NpcAthenaPierce extends NpcScriptMan {

    private static final int MODE_NONE = 0;
    private static final int MODE_1ST = 1;
    private static final int MODE_2ND_TEST = 2;
    private static final int MODE_2ND = 3;
    private static final int MODE_QUESTION = 4;
    private static final int MODE_SAYS = 5;
    private static final int MENU_SELECT_JOB = 2;
    private final int job_1st_level = 10;
    private final int job_1st_dex = 25;
    private final int job_1st = 300; // 弓使い
    private final int job_2nd_level = 30;
    private final int[] job_2nd = {
        310, // ハンター
        320 // クロスボウマン
    };
    private final String[] job_2nd_names = {
        "ハンター",
        "クロスボウマン"
    };
    // 1次転職の支給品 {アイテムID, 個数}
    private final int[][] items_1st = {
        {1452051, 1}, // 初歩弓使いの弓
        {2060000, 2000}, // 弓専用の矢
        {2060000, 2000}, // 弓専用の矢
        {2060000, 2000} // 弓専用の矢
    };
    private final int item_letter = 4031010; // 'ヘレナ'の手紙
    private final int item_proof_of_hero = 4031012; // 英雄の証
    private final int item_black_charm = 4031059; // 黒い護符
    private final int item_necklace_of_strength = 4031057; // 強靭のネックレス
    private final int quest_job_3rd = 7500; // 3次転職
    private final int npc_instructor = 1072002; // 弓使い転職教官
    private final int map_town = 100000000; // ヘネシス : ヘネシス
    private final int map_instructor = 106010000; // へネシス : ダンジョンへの道
    // 1次転職後の文章 (翻訳)
    private final String[] says_1st = {
        "装備インベントリとETCインベントリにスロットを追加しておきました。あなたもずっと強くなりましたよ。しっかり修行すれば、いつか弓使いの技の頂点に到達できるでしょう。遠くから見守っています。頑張ってくださいね。",
        "#bSP#kも少し渡しておきました。画面左下の#bスキルメニュー#kを開くと、SPを使って習得できるスキルが表示されます。注意しておきますが、一度に上げることはできません。いくつかのスキルを先に習得してからでないと使えないスキルもあります。",
        "もう一つ注意です。職業を選んだら、できるだけ長く生き延びるようにしてください。死ぬと経験値をすべて失ってしまいます。苦労して稼いだ経験値を失いたくはないでしょう？",
        "OK！私が教えられるのはこれだけです。いろいろな場所へ行って修行し、さらに成長してください。できることはすべてやり尽くして、何か面白いことが必要になったら私を訪ねてください。待っていますよ。",
        "ああ、それと…弓使いについて何か疑問があれば、遠慮なく聞いてください。弓使いについて何でも知っているわけではありませんが、答えられることには答えますよ。それまで…"
    };
    // 2次転職の職業の説明 (翻訳)
    private final String[][] says_2nd_about = {
        {
            "OK。ハンターについて説明しましょう。ハンターには、弓を使いこなすための#q3100000#や#q3101002#といったスキルがあります。矢をたくさん使うハンターのための#q3101004#というスキルもあります。これを使うと、実際には矢を消費せずに長い間矢を放ち続けられるんです。ですから、矢にたくさんメルを使ってきた人にはぴったりのスキルですよ…",
            "ハンターのスキルの一つ、#b#q3101003##kについて説明しましょう。遠距離攻撃でハンターの右に出る者はいませんが、敵がたくさんいたり、近くで攻撃しなければならなかったりすると話はまったく変わります。だからこのスキルはとても大切なんです。近くの敵を攻撃できるだけでなく、複数のモンスターを遠くへ吹き飛ばせます。どうしても必要な間合いを確保するための、とても重要なスキルですよ。",
            "ハンターの攻撃スキル、#b#q3101005##kについて説明しましょう。爆弾付きの矢を放つスキルです。命中すると敵の上で爆弾が爆発し、周りの敵にもダメージを与えて一時的に気絶させます。1次スキルの#q3000001#と組み合わせれば、ダメージは驚くほどになりますよ。ハンターになってみてはどうですか。"
        },
        {
            "OK。クロスボウマンについて説明しましょう。クロスボウマンには、#q3200000#や#q3201002#といったスキルに加えて、たくさん撃っても当たりが少なく、クロスボウを消耗させてしまう人のための#q3101004#があります。このスキルを使うと、クロスボウを消耗させずに長い間矢を放ち続けられます。ですから、クロスボウにたくさんメルを使ってきた人は、ぜひ試してみてください…",
            "OK。クロスボウマンが習得できるスキルの一つに#b#q3101003##kがあります。クロスボウマンの遠距離攻撃に近づける者はいませんが、接近戦や一度に多くの敵と戦うとなると話は別です。だからこのスキルはとても大切なんです。全力で攻撃しながら、同時に複数の敵を遠くへ吹き飛ばせます。どうしても必要な間合いを確保できる、とても重要なスキルですよ。",
            "OK、クロスボウマンの攻撃スキルの一つ、#b#q3201005##kについて説明しましょう。矢がモンスターを貫通して、その後ろにいる別のモンスターにも当たるので、複数の敵を攻撃できるスキルです。敵を貫通するたびにダメージは減っていきますが、それでも一度に複数の敵に当てられる、とても便利なスキルです。しかも…#q3000001#と組み合わせれば…まさに圧巻ですよ。"
        }
    };
    // 2次転職の確認 (翻訳)
    private final String[] asks_2nd = {
        "#bハンター#kとして2次転職したいのですね？一度決めたら、もうほかの職業は選べないことはわかっていますよね？",
        "#bクロスボウマン#kとして2次転職したいのですね？一度決めたら、後戻りして職業を変えることはできません。本当にそれでいいですか？"
    };
    // 2次転職後の文章 (翻訳)
    private final String[][] says_2nd = {
        {
            "さあ、今日からあなたは#bハンター#kです。ハンターは驚くべき視力を持つ賢い集団で、モンスターの心臓に楽々と矢を突き立てることができます…毎日修行しなければなりませんよ。今より強くなれるよう手助けしましょう。",
            "ハンターとして習得できるスキルのリストが載った本を渡しておきました。ETCインベントリも1列分拡張しました。最大HPと最大MPも増えています。確認してみてください。",
            "#bSP#kも少し渡しておいた。左下にある#bスキルメニュー#kを開いてみてくれ。新しく習得した2次スキルを強化できる。注意しておくが、一度に全部強化することはできない。ほかのスキルを習得してからでないと使えないスキルもある。それを忘れないようにな。",
            "ハンターは強くなければなりません。でも、その力を乱用して、自分より弱い者に向けてはいけないことを覚えておいてください。その大きな力を正しく使うのです…正しく使うことは、ただ強くなるよりずっと難しいですから。十分に成長したら、また私を訪ねてください。待っていますよ。"
        },
        {
            "よろしい。これであなたは#bクロスボウマン#kです。クロスボウマンは驚くべき視力を持つ賢い者たちで、モンスターの心臓に楽々と矢を突き立てることができます…一日たりとも修行を怠らないでください。今より強くなれるよう手助けしましょう。",
            "クロスボウマンとして習得できるスキルのリストが載った本を渡しておきました。ETCインベントリも1列分拡張しました。最大HPと最大MPも増えています。確認してみてください。",
            "#bSP#kも少し渡しておきました。左下にある#bスキルメニュー#kを開いてみてください。新しく習得した2次スキルを強化できます。注意しておきますが、一度に全部強化することはできません。ほかのスキルを習得してからでないと使えないスキルもあります。それを忘れないでくださいね。",
            "クロスボウマンは強くなければなりません。でも、その力を乱用して、自分より弱い者に向けてはいけないことを覚えておいてください。その大きな力を正しく使うのです…正しく使うことは、ただ強くなるよりずっと難しいですから。十分に成長したら、また私を訪ねてください。待っていますよ。"
        }
    };
    // 弓使いについての質問 (翻訳)
    private final String[] questions = {
        "弓使いの基本的な特徴は？",
        "弓使いの武器は？",
        "弓使いの防具は？",
        "弓使いのスキルは？"
    };
    // 翻訳
    private final String[][] says_questions = {
        {
            "弓使いについて説明しましょう。弓使いはそこそこの体力と力を持っています。最も大切な能力はDEXです。体力はあまりないので、できるだけ接近戦は避けてください。",
            "一番の利点は、遠くから攻撃できるので、モンスターの接近攻撃を避けられることです。さらに、DEXが高いので、近づいてくる攻撃の多くもかわすことができます。DEXが高いほど、与えられるダメージも大きくなりますよ。"
        },
        {
            "弓使いの武器について説明しましょう。敵を叩いたり斬ったりする武器ではなく、弓やクロスボウのような遠距離武器でモンスターを倒します。どちらにも長所と短所があります。",
            "弓はクロスボウほど威力はありませんが、攻撃速度はずっと速いです。一方、クロスボウは威力が高い分、速度は劣ります。決めるのは難しいでしょうね…",
            "矢やクロスボウはモンスターから手に入るので、頻繁に狩りをすることが大切です。でも、簡単ではありませんよ。一日たりとも修行を怠らなければ、成功はきっとついてきます…"
        },
        {
            "弓使いの防具について説明しましょう。弓使いは素早く動く必要があるので、重くて複雑な防具を着ても意味がありません。長くて邪魔なひものついた服などもってのほかです。",
            "戦士のような大きくて頑丈な防具を着ていたら、すぐに敵に囲まれてしまいます。体にぴったり合って、役目をきちんと果たしてくれる、シンプルで着心地のいい防具を身につけましょう。モンスターを狩るときにとても役立ちますよ。"
        },
        {
            "弓使いが使えるスキルは、高い命中率と器用さをうまく活かすものです。弓使いにとって、敵を正確に攻撃できるスキルを習得することが欠かせません。",
            "弓使いの攻撃スキルは#b#q3001004##kと#b#q3001005##kの2種類です。#q3001004#は、最小限のMPで敵に大きなダメージを与えられる、シンプルで優れたスキルです。",
            "一方、#q3001005#はかなりのMPを使って敵を2回攻撃できます。このスキルは、#q3001004#を少なくとも1回強化してからでないと習得できません。それを忘れないでください。どちらを選ぶにしても、そのスキルを極めてくださいね。"
        }
    };
    // 3次転職 力の試験の開始 (翻訳)
    private final String[] says_3rd_start = {
        "待っていましたよ。少し前に、オシリアの#bルネ#kからあなたのことを聞きました。3次転職に興味があるそうですね。そのためには、あなたが進むにふさわしいか、力を試す試験をしなければなりません。ビクトリアアイランドのどこかにある深い森の中に、新たな次元へと続く秘密の通路があります。その中で、私の分身と戦ってもらいます。あなたの任務は、それを倒して#b#t4031059##kを持ち帰ることです。",
        "私の分身ですから、激しい戦いになることを覚悟してください。見たこともない特殊な攻撃スキルを使ってきますが、あなたの任務はそれを倒すことです。秘密の通路には制限時間があるので、素早く倒すことが肝心です。幸運を祈っています。#b#t4031059##kを持ち帰ってくれることを期待していますよ。"
    };
    // 職業ごとの挨拶 (翻訳)
    private final int[] greeting_jobs = {
        310, // ハンター
        320, // クロスボウマン
        311, // レンジャー
        321, // スナイパー
        312, // ボウマスター
        322 // クロスボウマスター
    };
    private final String[] greeting_texts = {
        "おお…あなたですね。どうですか？ハンターの生活は？爆発する矢を楽々と扱えているようですね…これからも頑張ってください。",
        "おお…あなたですね。どうですか？クロスボウマンの生活は？その回転する矢には気をつけてくださいね。罪のない人を傷つけたくはないでしょう。",
        "おお…すべての試験に合格して#bレンジャー#kになったのですね！あなたならできるとわかっていました。どうですか？頂点に立ち、この地方で最高の弓使いになる日もそう遠くないでしょう。",
        "おお…すべての試験に合格して#bスナイパー#kになったのですね！あなたならできるとわかっていました。どうですか？頂点に立ち、この地方で最高の弓使いになる日もそう遠くないでしょう。",
        "ああ、なんてこと…ついにやり遂げたのですね！#bボウマスター#kになったのですね！あなたならできるとわかっていました！それで、最高の弓使いになった気分はどうですか？",
        "ああ、なんてこと…ついにやり遂げたのですね！#bクロスボウマスター#kになったのですね！あなたならできるとわかっていました！それで、最高の弓使いになった気分はどうですか？"
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
                    say("ビクトリアアイランドのどこかにある深い森の中に、新たな次元へと続く秘密の通路があります。その中で、私の分身と戦ってもらいます。あなたの任務は、それを倒して#b#t" + this.item_black_charm + "##kを持ち帰ることです。");
                    return false;
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_necklace_of_strength, 1, "")) {
                    // 翻訳
                    say("本当に#b#t" + this.item_black_charm + "##kを持っていますか？確かなら、ETCインベントリに空きがあるか確認してください。");
                    return false;
                }
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_black_charm, 1, true, false);
                MapleInventoryManipulator.addById(getPlayer(), this.item_necklace_of_strength, (short) 1);
                MapleQuest.getInstance(this.quest_job_3rd).forceStart(getPlayer(), getNpcId(), "p2");
                // 翻訳
                say("よくやりました。私の分身を倒して、#b#t" + this.item_black_charm + "##kを無事に持ち帰ってくれましたね。感心しました。これで3次転職の試験の前半に合格です。試験の後半を受けるには、このネックレスをオシリアの#bルネ#kに渡してください。気をつけて。応援していますよ。");
                return false;
            } else if ("p2".equals(quest_data)) {
                if (checkItem(this.item_necklace_of_strength)) {
                    // 翻訳
                    say("#b#t" + this.item_necklace_of_strength + "##kを持って、オシリアの#bルネ#kに会いに行ってください。3次転職の試験の一つに合格した証です。もう一つの試験にもきっと簡単に合格できますよ。さあ、行ってらっしゃい！");
                    return false;
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_necklace_of_strength, 1, "")) {
                    // 翻訳
                    say("うーん…何かおかしいですね。アイテムインベントリがいっぱいですか？ETCインベントリに十分な空きがあるか確認してください。");
                    return false;
                }
                MapleInventoryManipulator.addById(getPlayer(), this.item_necklace_of_strength, (short) 1);
                // 翻訳
                say("どうやら#b#t" + this.item_necklace_of_strength + "##kをなくしてしまったようですね。次からは気をつけてください。はい、もう一つどうぞ。これがないと3次転職の試験を受けられませんよ");
                return false;
            } else if (job == 0) {
                this.mode = MODE_1ST;
            } else if (job == this.job_1st) {
                if (level < this.job_2nd_level) {
                    this.mode = MODE_QUESTION;
                } else if (checkItem(this.item_letter)) {
                    // 翻訳
                    say("まだ彼女に会っていないのですね？#m" + this.map_town + "#の近く、#b#m" + this.map_instructor + "##kのあたりにいる#b#p" + this.npc_instructor + "##kを探してください。この手紙を渡せば、何をすべきか教えてくれますよ。");
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
                say("ゼロからモンスターを狩るスリルを味わいたくありませんか？それができるのは弓使いだけです…");
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
                            say("そうか？もっとよく考えたいんだな？急ぐな、急ぐな。いいかげんに決めていいことではないからな…決めたら話しかけてくれ、いいな？");
                            return false;
                        }
                        // 翻訳
                        say("弓使いになりたいのですか？？？そうですね…そのためにはいくつかの条件を満たす必要があります…少なくとも#bレベル" + this.job_1st_level + "でDEXが" + this.job_1st_dex + "#k必要です。どれどれ…ふむ…", false, true);
                        return true;
                    }
                    case 1 -> {
                        if (getPlayer().getLevel() < this.job_1st_level || getPlayer().getStat().getDex() < this.job_1st_dex) {
                            // 翻訳
                            say("もっと修行が必要です。弓使いになるのが簡単だとは思わないでくださいね…");
                            return false;
                        }
                        this.asked = true;
                        // 翻訳
                        askYesNo("資格は十分ですね。本物のモンスターを見抜く素晴らしい目と、矢で射抜くのに必要な技術を持っています…私たちはあなたのような人を求めていました。弓使いになりたいですか？");
                        return true;
                    }
                    case 2 -> {
                        for (int[] item : this.items_1st) {
                            if (!MapleInventoryManipulator.checkSpace(getPlayer(), item[0], item[1], "")) {
                                // 翻訳
                                say("ふむ…もう一度装備インベントリと消費インベントリを確認してください。  修行用の武器を渡そうとしているのです。");
                                return false;
                            }
                        }
                        // 翻訳
                        say("よし！私がそう命じるから、これからあなたは弓使いです…ふふ。私の力を少し分けてあげましょう…はあああっ！", false, true);
                        return true;
                    }
                    case 3 -> {
                        boolean space = true;
                        for (int[] item : this.items_1st) {
                            if (!MapleInventoryManipulator.checkSpace(getPlayer(), item[0], item[1], "")) {
                                space = false;
                            }
                        }
                        if (getPlayer().getJob() != 0 || !space) {
                            // 翻訳
                            say("ふむ…装備ウィンドウと消費ウィンドウを確認してください。1次転職のご褒美として、修行用の武器を渡したいのです。");
                            return false;
                        }
                        for (int[] item : this.items_1st) {
                            MapleInventoryManipulator.addById(getPlayer(), item[0], (short) item[1]);
                        }
                        getPlayer().changeJob(this.job_1st);
                        getPlayer().expandInventory((byte) 1, 4);
                        getPlayer().expandInventory((byte) 2, 4);
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
                        say("そうか？もっとよく考えたいんだな？急ぐな、急ぐな。いいかげんに決めていいことではないからな…決めたら話しかけてくれ、いいな？");
                        return false;
                    }
                    case 0 -> {
                        // 翻訳
                        askYesNo("ふむ…前に会ったときからずいぶん成長しましたね。以前の弱々しい姿はもう見当たりません。それどころか、今ではすっかり弓使いらしくなりました。さて、どうですか？もっと強くなりたいですか？簡単な試験を受けるだけです。受けてみますか？");
                        return true;
                    }
                    case 1 -> {
                        // 翻訳
                        say("いい決断です。あなたは強そうですが、試験に合格できるほど強いか確かめる必要があります。難しい試験ではないので、きっとうまくいきますよ。さあ、この手紙を持っていってください…なくさないでくださいね！", false, true);
                        return true;
                    }
                    case 2 -> {
                        if (!checkItem(this.item_letter)) {
                            if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_letter, 1, "")) {
                                // 翻訳
                                say("ふむ…インベントリがいっぱいのようですね…ETCインベントリに空きを作ってから私のところに来てください。手紙がないと試験を受けられませんから。");
                                return false;
                            }
                            MapleInventoryManipulator.addById(getPlayer(), this.item_letter, (short) 1);
                        }
                        // 翻訳
                        say("この手紙を、#m" + this.map_town + "#の近く、#b#m" + this.map_instructor + "##kのあたりにいる#b#p" + this.npc_instructor + "##kに届けてください。彼女が私の代わりに教官を務めています。手紙を渡せば、私の代わりに試験をしてくれます。幸運を祈ります。");
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
                        say("ふふ…あなたならあの試験を楽々こなすとわかっていましたよ。あなたが優れた弓使いだと認めましょう。今よりずっと強くしてあげます。でもその前に…2つの道から1つを選ばなければなりません。難しい決断になるでしょうが…何でも質問してくださいね。", false, true);
                        return true;
                    }
                    case 1 -> {
                        // 翻訳
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("さあ、決めたら下の[職業を選びたい！]をクリックしてください。");
                        for (int i = 0; i < this.job_2nd_names.length; i++) {
                            nt.addMenu(i, this.job_2nd_names[i] + "について教えて。");
                        }
                        nt.addMenu(MENU_SELECT_JOB, "職業を選びたい！");
                        askMenu(nt);
                        return true;
                    }
                    case 2 -> {
                        // 転職を断った
                        if (this.asked) {
                            // 翻訳
                            say("そうか？もっとよく考えたいんだな？急ぐな、急ぐな。いいかげんに決めていいことではないからな…決めたら話しかけてくれ、いいな？");
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
                        nt.add("さて…決まりましたか？2次転職で就きたい職業を選んでください。");
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
                        if (getPlayer().getRemainingSp() > (getPlayer().getLevel() - this.job_2nd_level) * 3) {
                            // 翻訳
                            say("ふむ…#bSP#kが多すぎますね…そんなにSPを残したままでは2次転職できません。1次スキルにもっとSPを使ってから、また来てください。");
                            return false;
                        }
                        if (getPlayer().getJob() != this.job_1st || !checkItem(this.item_proof_of_hero)) {
                            // 翻訳
                            say("ふむ…本当に#p" + this.npc_instructor + "#からもらった#b#t" + this.item_proof_of_hero + "##kを持っていますか？よく確認してください。それがないと次の職業に転職させられませんから…");
                            return false;
                        }
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_proof_of_hero, 1, true, false);
                        getPlayer().changeJob(this.job_2nd[this.job_index]);
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
                        nt.add("弓使いの生活について何か質問がありますか？");
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
