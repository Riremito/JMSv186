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
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
public class NpcDarkLord extends NpcScriptMan {

    private static final int MODE_NONE = 0;
    private static final int MODE_1ST = 1;
    private static final int MODE_2ND_TEST = 2;
    private static final int MODE_2ND = 3;
    private static final int MODE_QUESTION = 4;
    private static final int MODE_SAYS = 5;
    private static final int MODE_AMBUSH = 6;
    private static final int MENU_SELECT_JOB = 2;
    private final int job_1st_level = 10;
    private final int job_1st = 400; // 盗賊
    private final int job_2nd_level = 30;
    private final int[] job_2nd = {
        410, // アサシン
        420 // シーフ
    };
    private final String[] job_2nd_names = {
        "アサシン",
        "シーフ"
    };
    // 1次転職の支給品 {アイテムID, 個数}
    private final int[][] items_1st = {
        {1472061, 1}, // 初歩盗賊の篭手
        {1332063, 1}, // 初歩盗賊の短剣
        {2070015, 1000}, // 初歩盗賊の手裏剣
        {2070015, 1000}, // 初歩盗賊の手裏剣
        {2070015, 1000} // 初歩盗賊の手裏剣
    };
    private final int item_letter = 4031011; // 'ダークロード'の手紙
    private final int item_proof_of_hero = 4031012; // 英雄の証
    private final int item_black_charm = 4031059; // 黒い護符
    private final int item_necklace_of_strength = 4031057; // 強靭のネックレス
    private final int quest_job_3rd = 7500; // 3次転職
    private final int quest_ambush = 6141; // 4次転職 (待ち伏せ)
    private final int npc_instructor = 1072003; // 盗賊転職教官
    private final int map_instructor = 102040000; // カニングシティー : 工事現場-北
    // 2次転職の職業の説明 (翻訳)
    private final String[][] says_2nd_about = {
        {
            "アサシンの役割について説明しよう。アサシンは手裏剣や短剣を使う盗賊だ。#b#q4100000##kや#b#q4100001##kといったスキルは、手裏剣をうまく使うのに役立つ。#q4100000#をさらに強化すれば、手裏剣の最大所持数が増える。だから、このスキルは覚えておいた方がいい。それを忘れるな。",
            "アサシンのスキルの一つ、#b#q4101004##kについて説明しよう。自分とパーティーのジャンプ力と移動速度を一時的に高めるスキルだ。とても素早い敵と戦うときにぴったりだし、遠くへ移動するときにも便利だ。丸一日かかるところをすぐに目的地に着けたら最高だろう？",
            "アサシンが使えるもう一つのスキルが#b#q4101005##kだ。敵に与えたダメージの一部をHPとして吸収できる！ダメージが大きいほど、回復量も多くなる…すごいだろう？一度に吸収できるのは最大HPの半分までだということを覚えておけ。敵のHPが多いほど、多く吸収できる。"
        },
        {
            "シーフについて説明しよう。シーフは短剣の扱いに特化した盗賊だ。#b#q4200000##kや#b#q4201002##kといったスキルは、短剣をうまく使うのに役立つ。もともと短剣は攻撃速度が速いが、そこに熟練が加われば…すごいぞ！どんなモンスターも怯えるほどの速さになる！",
            "#b#q4201004##kがシーフにとってどんなスキルか説明しよう。敵からアイテムを盗むチャンスが得られる。盗めるのは1体の敵からだけだが、成功するまで何度でも挑戦できる。盗んだアイテムは地面に落ちる。誰でも拾えるから、すぐに拾うのを忘れるなよ。",
            "#b#q4201005##kがシーフにとってどんなスキルか説明しよう。HPとMPを使って、短剣で敵を「6回」攻撃する。スキルのレベルが高いほど、攻撃回数が増えることもある。短剣で敵を細切れにするんだ…ああ、たまらないだろう？どうだ？シーフになってアドレナリンの高まりを感じてみたいか？"
        }
    };
    // 2次転職の確認 (翻訳)
    private final String[] asks_2nd = {
        "#bアサシン#kとして2次転職したいんだな？一度決めたら、後戻りして気を変えることはできない。「本当に」いいんだな？",
        "#bシーフ#kとして2次転職したいんだな？一度決めたら、後戻りはできない。ほかの職業には変えられないぞ。「本当に」いいんだな？"
    };
    // 2次転職後の文章 (翻訳)
    private final String[][] says_2nd = {
        {
            "よし、今日からお前は#bアサシン#kだ。アサシンは影と闇を好み、素早く突然に短剣で敵の心臓を貫く瞬間を待つ…修行を怠るなよ。今よりもっと強くしてやる！",
            "アサシンとして習得できるスキルのリストが載った本を渡しておいた。消費インベントリにも新しく1列追加して、最大HPと最大MPも上げておいた…確認してみろ。",
            "#bSP#kも少し渡しておいた。左下にある#bスキルメニュー#kを開いてみろ。新しく習得した2次スキルを強化できる。注意しておくが、一度に全部強化することはできない。ほかのスキルを習得してからでないと使えないスキルもある。それを忘れるな。",
            "アサシンは強くなければならない。だが、その力を乱用して、自分より弱い者に向けてはいけないことを覚えておけ。その大きな力を正しく使うんだ…正しく使うことは、ただ強くなるよりずっと難しいからな。十分に成長したら、また俺を訪ねてこい。"
        },
        {
            "よし、今日からお前は#bシーフ#kだ。シーフは素早い手足で敵をねじ伏せる。修行を怠るなよ。今よりもっと強くしてやる！",
            "シーフとして習得できるスキルのリストが載った本を渡しておいた。消費インベントリにも1列追加しておいた。最大HPと最大MPも上がっている…確認してみろ。",
            "#bSP#kも少し渡しておきました。左下にある#bスキルメニュー#kを開いてみてください。新しく習得した2次スキルを強化できます。注意しておきますが、一度に全部強化することはできません。ほかのスキルを習得してからでないと使えないスキルもあります。それを忘れないでくださいね。",
            "シーフは強くなければならない…だが、その力を乱用して、自分より弱い者に向けてはいけないことを覚えておけ。その大きな力を正しく使うんだ…正しく使うことは、ただ強くなるよりずっと難しいからな。十分に成長したら、また俺を訪ねてこい。"
        }
    };
    // 盗賊についての質問 (翻訳)
    private final String[] questions = {
        "盗賊の基本的な特徴は？",
        "盗賊の武器は？",
        "盗賊の防具は？",
        "盗賊が使えるスキルは？"
    };
    // 翻訳
    private final String[][] says_questions = {
        {
            "盗賊とは何かを説明しよう。盗賊は生き延びるのに必要なだけの体力と力しか持っていない。戦士のように力を鍛えることはすすめない。必要なのはLUKとDEXだ…",
            "LUKとDEXを上げれば、敵に与えるダメージも大きくなる。盗賊は手裏剣や投げナイフのような投げ武器を使う点でも、ほかの者たちとは違う。DEXが高いので、多くの攻撃をかわすこともできる。"
        },
        {
            "盗賊が使う武器はこうだ。盗賊は知力も腕力もほどほどしかない…強みは素早い動きと、それ以上に素早い頭の回転だ…",
            "だから、俺たちは普通、短剣か投げ武器を使う。短剣は基本的に小さな剣だが、とても素早いので何度も攻撃できる。接近戦になったら、敵が反撃する前に短剣で素早く仕留めろ。",
            "投げ武器としては、手裏剣と投げナイフがある。だが、それだけでは使えない。#m103000000#の武器屋に行けば、#t1472000#というアームを売っている。それを装備すれば、消費インベントリにある手裏剣を投げられるようになる。",
            "正しいアームを選ぶのと同じくらい、適切な種類の手裏剣を選ぶのも大切だ。手裏剣をどこで手に入れるか知りたいか？この町の近くの防具屋を見てみろ…たぶん専門に扱っているやつがいるはずだ…"
        },
        {
            "盗賊が使う防具について説明しよう。盗賊は素早さを重視するから、体にぴったり合った服が必要だ。だが、弓使いのような鎖かたびらは必要ない。何の役にも立たないからな。",
            "派手な服や、硬くて頑丈な金の板金鎧ではなく、体にぴったり合って役目をきちんと果たす、シンプルで着心地のいい服を着るようにしろ。モンスターを狩るときにとても役立つぞ。"
        },
        {
            "盗賊には、俺たちが持つ高い命中率と器用さを支えるスキルがある。手裏剣用にも短剣用にも、さまざまなスキルがある。武器によって使えるスキルが決まるから、武器は慎重に選べ。",
            "手裏剣を使うなら、#b#q4000001##kや#b#q4001344##kといったスキルがぴったりだ。#q4001344#は一度に複数の手裏剣を投げられるので、敵を追い詰めるときにとても役立つ。",
            "短剣を使うなら、#b#q4001002##kか#b#q4001334##kを選べ。#q4001334#は、目にも止まらぬ連続攻撃ができるので、とても使い勝手のいいスキルだ。敵を間違いなく翻弄できるぞ。"
        }
    };
    // 3次転職 力の試験の開始 (翻訳)
    private final String[] says_3rd_start = {
        "来たか。数日前、オシリアの#bアレク#kからお前のことを聞いた。盗賊の3次転職という闇の世界に足を踏み入れることに興味があるようだな。その目標を達成するには、お前が進むにふさわしいか、力を試さなければならない。ビクトリアアイランドの深い沼の真ん中に、秘密の通路へと続く裂け目がある。その中で、俺の分身と戦ってもらう。お前の任務は、それを倒して#b#t4031059##kを持ち帰ることだ。",
        "俺の分身だから、激しい戦いになることは覚悟しておけ。見たこともない特殊な攻撃スキルを使ってくるが、お前の任務は接近戦でそれを倒すことだ。秘密の通路には制限時間があるから、時間内に倒すことが肝心だ。幸運を祈る。#b#t4031059##kを持ち帰ってくれることを期待しているぞ。"
    };
    // 職業ごとの挨拶 (翻訳)
    private final int[] greeting_jobs = {
        410, // アサシン
        420, // シーフ
        411, // ハーミット
        421 // シーフマスター
    };
    private final String[] greeting_texts = {
        "お前か。アサシンの生活はどうだ？今のお前からはアサシンの風格を感じるぞ…いいぞ！これからも頑張れ！",
        "お前か。シーフの生活はどうだ？今のお前からはシーフの風格を感じるぞ…いいぞ！これからも頑張れ！",
        "おお…お前か。試験に合格して#bハーミット#kになったんだな！お前ならできるとわかっていたぞ！どうだ？いつかこの地方で最高の盗賊になる者が見えるぞ…",
        "おお…お前か。試験に合格して#bシーフマスター#kになったんだな！お前ならできるとわかっていたぞ！どうだ？いつかこの地方で最高の盗賊になる者が見えるぞ…"
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
            // 4次転職
            if (getPlayer().getQuestStatus(this.quest_ambush) == 1) {
                this.mode = MODE_AMBUSH;
                // 翻訳
                askYesNo("さて、修行場へ行く準備はできたか？");
                return true;
            }
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
                    say("ビクトリアアイランドの深い沼の真ん中に、秘密の通路へと続く裂け目がある。その中で、俺の分身と戦ってもらう。お前の任務は、それを倒して#b#t" + this.item_black_charm + "##kを持ち帰ることだ。");
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
                say("よくやった。俺の分身を倒して、#b#t" + this.item_black_charm + "##kを無事に持ち帰ったな。その身体能力で、3次転職にふさわしいことを証明した。さあ、試験の後半を受けるため、このネックレスをオシリアの#bアレク#kに渡せ。幸運を祈る！必要になるだろうからな。");
                return false;
            } else if ("p2".equals(quest_data)) {
                if (checkItem(this.item_necklace_of_strength)) {
                    // 翻訳
                    say("#b#t" + this.item_necklace_of_strength + "##kを持って、オシリアの#bアレク#kに会いに行け。3次転職の試験の一つに合格した証だ。もう一つの試験にもきっと楽に合格できるさ。幸運を祈る…");
                    return false;
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_necklace_of_strength, 1, "")) {
                    // 翻訳
                    say("うーん…何かおかしいな。アイテムインベントリがいっぱいなのか？ETCインベントリに十分な空きがあるか確認しろ。");
                    return false;
                }
                MapleInventoryManipulator.addById(getPlayer(), this.item_necklace_of_strength, (short) 1);
                // 翻訳
                say("どうやら#b#t" + this.item_necklace_of_strength + "##kをなくしたようだな。次からは気をつけろ。ほら、もう一つだ。これがないと3次転職の試験を受けられないぞ");
                return false;
            } else if (job == 0 && this.job_1st_level <= level) {
                this.mode = MODE_1ST;
            } else if (job == this.job_1st) {
                if (level < this.job_2nd_level) {
                    this.mode = MODE_QUESTION;
                } else if (checkItem(this.item_letter)) {
                    // 翻訳
                    say("まだ会っていないんだろう？#m103000000#の近く、#b#m" + this.map_instructor + "##kのあたりにいる#b#p" + this.npc_instructor + "##kを探せ。この手紙を渡せば、何をすべきか教えてくれる。");
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
                // BB前? : 冒険も強くなることも良いが楽しみながら暮したくないか…？　我々と一緒に世の中を楽しみながら生きて行くのはどうだ？　本当に面白いぞ？
                say("隠密な会話…？隠密な行動が特技の盗賊だが、それは敵と戦う程度で充分だ。");
                /*
                マイが言ってた人が君か？#h0#…ふむ。彼女に言われたどおりに、かなり資質があるような若造だな…おい、盗賊になりたいって？盗賊について分かってるか？
                盗賊というとちっぽけなこそ泥を思う人もいるが、実は違うんだ。メイプルワールドの盗賊は闇の中で鋭い短刀と手裏剣で戦う人たちだ。もしかしたら正々堂々としてない部分があるかもしれない。だが、それも盗賊の本質。否定する必要はないだろ。
                職業としての盗賊は素早くて強力なスキルで敵を攻撃するんだ。体力は弱いほうだが、その分動きが早いためモンスターを避けるのも難しくない。強力な運でクリティカルな攻撃もうまいしな。
                どうだ？盗賊の道を共に歩むか？君が盗賊の道に進むと決めたら転職官の特権で君をすぐに#bカニングシティー、盗賊のアジト#kに招待する…隠密な所だから光栄に思うんだな。#rだが、他の職業がもっと良かったら？それなら断ってくれ。盗賊じゃない他の道を推薦するから#k。
                 */
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
            case MODE_AMBUSH -> {
                if (status != 1) {
                    // 翻訳
                    say("まだ準備ができていないようだな。");
                    return false;
                }
                if (enterFieldSet("S4ambush", 0) != TacosFieldSet.ENTER_SUCCESS) {
                    // 翻訳
                    say("すでにほかの盗賊がミッション中だ。しばらくしてからもう一度試してくれ。");
                }
                return false;
            }
            case MODE_1ST -> {
                switch (status) {
                    case -1 -> {
                        say("まだ心の準備ができてないか？");
                        return false;
                    }
                    case 0 -> {
                        askYesNo("盗賊のアジトへようこそ。こうでもしないと入口もなかなか分かり辛い所だ。ふふ、出る時に門をよく憶えておいてくれ。じゃ、盗賊になる準備はできたか？");
                        return true;
                    }
                    case 1 -> {
                        boolean space = true;
                        for (int[] item : this.items_1st) {
                            if (!MapleInventoryManipulator.checkSpace(getPlayer(), item[0], item[1], "")) {
                                space = false;
                            }
                        }
                        if (getPlayer().getJob() != 0 || !space) {
                            // 翻訳
                            say("装備インベントリと消費インベントリを確認しろ。1次転職のご褒美として、修行用の武器を渡したいんだ。");
                            return false;
                        }
                        for (int[] item : this.items_1st) {
                            MapleInventoryManipulator.addById(getPlayer(), item[0], (short) item[1]);
                        }
                        getPlayer().resetStats(4, 25, 4, 4);
                        getPlayer().changeJob(this.job_1st);
                        getPlayer().expandInventory((byte) 1, 4);
                        getPlayer().expandInventory((byte) 4, 4);
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("これで君は盗賊になった。盗賊のスキルが使用できるようになったからスキルウィンドウを開いてみてくれ。レベルを上げるとより多くのスキルを覚えることができるんだ。");
                        say(nt, false, true);
                        return true;
                    }
                    case 2 -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("スキルだけでは物足りないな？ステータスも盗賊らしくならないと本物の盗賊じゃないな。盗賊はLUKがメインステータスで、DEXが補助ステータスになる。ステータスの上げ方が分からなかったら#b自動配分#kを使用するといい。");
                        say(nt, false, true);
                        return true;
                    }
                    case 3 -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("そして、プレゼントがあるぞ。君の装備とETCアイテムの保管箱の個数を増やしてあげたんだ。インベントリが広いと旅が楽しくなるもの。ふふ…");
                        say(nt, false, true);
                        return true;
                    }
                    case 4 -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("さて！俺が君に教えられるのはここまでだ。君に役立つような武器もいくつか渡したから旅をしながら自身を鍛えてみてくれ。");
                        say(nt);
                        return false;
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
                        say("本当か…？早く強くなれば、旅でとても役に立つんだがな。気が変わったら俺に会いに来い。今よりもっと強くしてやる。");
                        return false;
                    }
                    case 0 -> {
                        // 翻訳
                        askYesNo("ふむ…ずいぶん強くなったようだな。あの平凡で弱々しい姿から抜け出して、今ではすっかり盗賊らしくなった。さて、どうだ？もっと強くなりたいか？簡単な試験を受けるだけだ。受けてみるか？");
                        return true;
                    }
                    case 1 -> {
                        // 翻訳
                        say("いい判断だ。だが、お前が見た目どおり強いかどうか確かめる必要がある。難しい試験じゃない、お前なら合格できる。まずはこの手紙を持っていけ…なくすなよ。", false, true);
                        return true;
                    }
                    case 2 -> {
                        if (!checkItem(this.item_letter)) {
                            if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_letter, 1, "")) {
                                // 翻訳
                                say("うーん…この手紙を入れる空きがないようだな。ETCインベントリに空きを作ってから話しかけてこい。手紙がなければ試験は受けられない。");
                                return false;
                            }
                            MapleInventoryManipulator.addById(getPlayer(), this.item_letter, (short) 1);
                        }
                        // 翻訳
                        say("この手紙を、#m103000000#の近く、#b#m" + this.map_instructor + "##kのあたりにいる#b#p" + this.npc_instructor + "##kに届けろ。あいつが俺の代わりに教官を務めている。手紙を渡せば、俺の代わりに試験をしてくれる。詳しいことはあいつに直接聞け。幸運を祈る。");
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
                        say("ふむ…無事に戻ってきたな。あの試験はお前には簡単すぎるとわかっていたよ。お前が優れた盗賊だと認めよう。さて…今よりもっと強くしてやる。だがその前に…2つの道から1つを選ばなければならない。難しい決断になるだろうが…何でも質問していいぞ。", false, true);
                        return true;
                    }
                    case 1 -> {
                        // 翻訳
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("よし、決めたら下の[職業を選びたい！]をクリックしろ…");
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
                        nt.add("よし…決まったか？2次転職で就きたい職業を選べ。");
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
                            say("ふむ…本当に#p" + this.npc_instructor + "#からもらった#b#t" + this.item_proof_of_hero + "##kを持っているか？よく確認しろ。それがないと転職させられない。");
                            return false;
                        }
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_proof_of_hero, 1, true, false);
                        getPlayer().changeJob(this.job_2nd[this.job_index]);
                        getPlayer().expandInventory((byte) 2, 4);
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
                        nt.add("盗賊について何か知りたいことがあるか？");
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
