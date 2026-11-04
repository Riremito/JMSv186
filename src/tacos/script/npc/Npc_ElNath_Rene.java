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
public class Npc_ElNath_Rene extends NpcScriptMan {

    private static final int MENU_JOB_3RD = 0;
    private static final int MENU_ZAKUM = 1;
    private static final int MODE_NONE = 0;
    private static final int MODE_START = 1;
    private static final int MODE_STRENGTH = 2;
    private static final int MODE_WISDOM = 3;
    private static final int MODE_CHANGE = 4;
    private final int[] job_from = {
        310, // ハンター
        320 // クロスボウマン
    };
    private final int[] job_to = {
        311, // レンジャー
        321 // スナイパー
    };
    // 試験を行わない
    private final boolean[] skip_test = {
        false,
        false
    };
    // 転職後の文章 (翻訳)
    private final String[] job_texts = {
        "あなたは今、#bレンジャー#kになりました。ぜひ極めてほしいスキルの一つが#bモータルブロー#kで、レンジャーが至近距離から矢を放てるスキルです。#bインフェルノ#kでは、レンジャーが一時的にモンスターに炎属性の攻撃を行えます。さらに、#bパペット#k（モンスターの注意を引くかかしを召喚）や#bシルバーホーク#k（モンスターを攻撃する銀の鷹を召喚）といったスキルが、遠距離攻撃のスペシャリストとしての弓使いの地位を確かなものにしてくれます。",
        "あなたは今、#bスナイパー#kになりました。ぜひ極めてほしいスキルの一つが#bモータルブロー#kで、スナイパーが至近距離から矢を放てるスキルです。#bブリザード#kでは、スナイパーが一時的にモンスターに氷属性の攻撃を行えます。さらに、#bパペット#k（モンスターの注意を引くかかしを召喚）や#bゴールデンイーグル#k（モンスターを攻撃する金の鷲を召喚）といったスキルが、遠距離攻撃のスペシャリストとしての弓使いの地位を確かなものにしてくれます。"
    };
    // 3次転職, 4次転職後の挨拶 (翻訳)
    private final int[] greeting_jobs = {
        311, // レンジャー
        321, // スナイパー
        312, // ボウマスター
        322 // クロスボウマスター
    };
    private final String[] greeting_texts = {
        "3次転職の試験に合格したのはあなたですね。#bレンジャー#kの生活はどうですか？この地を旅しながら修行を続けなければなりませんよ。オシリアには、私でさえ知らない強力なモンスターがたくさんいます。何か疑問があれば、この道の終わりで私に話しかけてください。幸運を祈ります。",
        "3次転職の試験に合格したのはあなたですね。#bスナイパー#kの生活はどうですか？この地を旅しながら修行を続けなければなりませんよ。オシリアには、私でさえ知らない強力なモンスターがたくさんいます。何か疑問があれば、この道の終わりで私に話しかけてください。幸運を祈ります。",
        "弓使いの中で最強になったのはあなたですね。#bボウマスター#kとして、すべての者の手本とならなければなりません。この世界にはまだたくさんの秘密があります。あなたのような弓使いの力は大きな助けになるでしょう。ですから、修行を続けてください。",
        "弓使いの中で最強になったのはあなたですね。#bクロスボウマスター#kとして、すべての者の手本とならなければなりません。この世界にはまだたくさんの秘密があります。あなたのような弓使いの力は大きな助けになるでしょう。ですから、修行を続けてください。"
    };
    private final int job_min = 300;
    private final int job_max = 399;
    private final int required_level = 70;
    private final int zakum_level = 50;
    private final int quest_job_3rd = 7500; // 3次転職
    private final int quest_zakum = 7000; // ジャクムダンジョン 1段階
    private final int item_necklace_of_strength = 4031057; // 強靭のネックレス
    private final int item_necklace_of_wisdom = 4031058; // 知恵のネックレス
    private int mode = MODE_NONE;
    private int job_index = -1;
    // いいえを選択した場合の status と文章
    private int no_status = -2;
    private String no_text = "";

    @Override
    public boolean action(int status, int answer) {
        if (status == this.no_status) {
            say(this.no_text);
            return false;
        }
        switch (status) {
            case 0 -> {
                if (getLevel() < this.zakum_level) {
                    // 翻訳
                    say("ふむ…君を手助けできることは何もなさそうだ。もっと強くなってから来てくれ。");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("私に何の用だい？");
                nt.addMenu(MENU_JOB_3RD, "3次転職したいです。");
                nt.addMenu(MENU_ZAKUM, "ジャクムダンジョンクエストを許可してください。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                switch (answer) {
                    case MENU_ZAKUM -> {
                        String zakum_data = getQuestData(this.quest_zakum);
                        // 翻訳
                        if (!zakum_data.isEmpty()) {
                            say("ジャクムのダンジョンクエストはどうだ？その奥深くにはとんでもないモンスターがいると聞いた…とにかく、幸運を祈る。君ならきっとやり遂げられる。");
                            return false;
                        }
                        if (getJob() < this.job_min || this.job_max < getJob()) {
                            // 原文ママ
                            say("ジャクムダンジョンを探索しようとしてるんですか？…弓使いではないようですね？弓使いでない人を判断する能力までは持っていません。あなたを見極めることができる職業の長老を訪ねてください。");
                            return false;
                        }
                        setQuestData(this.quest_zakum, "s");
                        say("ジャクムのダンジョンクエストを行う許可がほしいのだな？#b#p2030008##kからだろう…よし、いいだろう！君ならダンジョンでも大丈夫だと確信している。向こうでは気をつけてくれよ…");
                        return false;
                    }
                    case MENU_JOB_3RD -> {
                        int job = getJob();
                        if (getLevel() < this.required_level) {
                            // 翻訳
                            say("あなたはまだ3次転職する資格がありません。そのためには少なくともレベル70が必要です。もっと修行してから私に会いに来てください。");
                            return false;
                        }
                        for (int i = 0; i < this.greeting_jobs.length; i++) {
                            if (job == this.greeting_jobs[i]) {
                                say(this.greeting_texts[i]);
                                return false;
                            }
                        }
                        for (int i = 0; i < this.job_from.length; i++) {
                            if (job == this.job_from[i]) {
                                this.job_index = i;
                                break;
                            }
                        }
                        if (this.job_index < 0) {
                            // 翻訳
                            say("私は#bルネ#k、すべての弓使いの長であり、私の導きを必要とする弓使いたちの力を最大限に引き出す役目を担っています。でも、あなたは弓使いではありませんね。あなたの力にはなれそうにありません。この部屋には、それぞれの職業の長たちがいます。何か用があれば、その誰かに話しかけてください。");
                            return false;
                        }
                        int quest_status = getQuestStatus(this.quest_job_3rd);
                        String quest_data = getQuestData(this.quest_job_3rd);
                        if (this.skip_test[this.job_index] || quest_status == 2) {
                            this.mode = MODE_CHANGE;
                            this.no_status = status - 1;
                            this.no_text = "あなたはもう試験に合格しているので、心配することはありません…まあ、決めたら話しかけてください。準備ができたら、3次転職させてあげましょう。準備ができていればの話ですが…"; // 翻訳
                            askYesNo("よし！これからあなたをはるかに強力な弓使いにしてあげましょう。でもその前に、SPをきちんと使ったか確認してください。3次転職するには、少なくともレベル70までにもらったSPをすべて使っている必要があります。ああ、それと、2次転職ですでに職業を選んでいるので、3次転職で選び直す必要はありません。今すぐ転職しますか？"); // 翻訳
                            return true;
                        }
                        if (quest_status == 0) {
                            this.mode = MODE_START;
                            this.no_status = status - 1;
                            this.no_text = "まだこれから待ち受ける試練に立ち向かう準備ができていないようだな。転職に伴う試練に立ち向かう準備ができたと自分で確信できたときにだけ、私に会いに来なさい。"; // 翻訳
                            askYesNo("ようこそ。私は#bルネ#k、すべての弓使いの長であり、私の導きを必要とする弓使いたちの力を最大限に引き出す役目を担っています。あなたは一歩前に進みたいと願う弓使いのようですね。3次転職の試練に立ち向かう準備ができているように見えます。でも、あなたのように飛躍しようとして、最後には失敗した弓使いを私はたくさん見てきました。あなたはどうですか？3次転職の試験に挑戦する準備はできていますか？"); // 翻訳
                            return true;
                        }
                        if ("p2".equals(quest_data)) {
                            if (!checkItem(this.item_necklace_of_strength)) {
                                // 翻訳
                                say("#b#t4031057##kを持っていないようですね。ヘネシスの#b#p1012100##kのところへ行って試験に合格し、#b#t4031057##kを持ってきてください。そうすれば試験の前半に合格です。幸運を祈ります！");
                                return false;
                            }
                            this.mode = MODE_STRENGTH;
                            // 翻訳
                            say("試験の力の部分を完了したな、おめでとう。君ならできるとわかっていた。試験の前半に合格したので、後半を受けられる。まずはネックレスを渡してくれ。", false, true);
                            return true;
                        }
                        if ("end1".equals(quest_data)) {
                            if (!checkItem(this.item_necklace_of_wisdom)) {
                                // 翻訳
                                say("#b#t4031058##kを持っていないようだな。オシリアの雪原にある聖地と呼ばれる薄暗い雪に覆われた場所を探し、特別なアイテムを捧げ物として供えて、すべての質問に正直に、確信を持って答えれば、#b#t4031058##kがもらえる。それを持ち帰れば、3次転職の試験は完了だ。幸運を祈る…");
                                return false;
                            }
                            this.mode = MODE_WISDOM;
                            // 翻訳
                            say("試験の知恵の部分を完了したな、おめでとう。すべての質問に正しく、賢明に答えた。君が示した知恵の高さには、正直とても感心したよ。次の一歩に進む前に、まずはネックレスを渡してくれ。", false, true);
                            return true;
                        }
                        // 翻訳
                        say("#b#t4031057##kを持っていないようですね。ヘネシスの#b#p1012100##kのところへ行って試験に合格し、#b#t4031057##kを持ってきてください。そうすれば2つ目の試験を受けられます。幸運を祈ります。");
                        return false;
                    }
                    default -> {
                    }
                }
                return false;
            }
            default -> {
            }
        }
        switch (this.mode) {
            case MODE_START -> {
                switch (status) {
                    case 2 -> {
                        setQuestData(this.quest_job_3rd, "s");
                        // 翻訳
                        say("よろしい。あなたは弓使いにとって大切な2つの面、力と知恵を試されることになります。まず試験の力の部分について説明しましょう。ヘネシスの#b#p1012100##kを覚えていますか？彼女のところへ行って、試験の最初の部分について説明を聞いてください。ミッションを完了して、#p1012100#から#b#t4031057##kを受け取るのです。", false, true);
                        return true;
                    }
                    case 3 -> {
                        // 翻訳
                        say("試験の知恵の部分は、力の部分に合格してから始まります。#b#t4031057##kが試験に合格した証になります。あなたが向かうことを#b#p1012100##kに伝えておきましょう。準備しておいてください。簡単ではありませんが、あなたを大いに信じていますよ。幸運を祈ります！");
                        return false;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case MODE_STRENGTH -> {
                switch (status) {
                    case 2 -> {
                        if (!checkItem(this.item_necklace_of_strength)) {
                            // 翻訳
                            say("本当に#b#p1012100##kからもらった#b#t4031057##kを持っていますか？ETCインベントリに空きを作っておくのを忘れないでください。");
                            return false;
                        }
                        removeItem(this.item_necklace_of_strength, 1);
                        setQuestData(this.quest_job_3rd, "end1");
                        // 翻訳
                        say("これが試験の後半です。この試験で、あなたが偉大さへの次の一歩を踏み出せるほど賢いかどうかが決まります。オシリアの雪原に、聖地と呼ばれる薄暗い雪に覆われた場所があります。モンスターさえたどり着けない場所です。その中心には聖なる石と呼ばれる巨大な石があります。そこに特別なアイテムを捧げ物として供えなければなりません。聖なる石がその場であなたの知恵を試すでしょう。", false, true);
                        return true;
                    }
                    case 3 -> {
                        // 翻訳
                        say("すべての質問に正直に、確信を持って答えなければならない。すべて正しく答えれば、聖なる石は君を正式に認めて#b#t4031058##kを授けてくれる。そのネックレスを持ち帰ってくれれば、次の一歩を踏み出す手助けをしよう。幸運を祈る！");
                        return false;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case MODE_WISDOM -> {
                if (status == 2) {
                    if (!checkItem(this.item_necklace_of_wisdom)) {
                        // 翻訳
                        say("本当に聖なる石から授かった#b#t4031058##kを持っているか？確かなら、ETCインベントリに空きを作っておくのを忘れないでくれ。");
                        return false;
                    }
                    removeItem(this.item_necklace_of_wisdom, 1);
                    MapleQuest.getInstance(this.quest_job_3rd).forceComplete(getPlayer(), getNpcId());
                    this.mode = MODE_CHANGE;
                    this.no_status = status - 1;
                    this.no_text = "あなたはもう試験に合格しているので、心配することはありません…まあ、決めたら話しかけてください。準備ができたら、3次転職させてあげましょう。準備ができていればの話ですが…"; // 翻訳
                    askYesNo("よし！これからあなたをはるかに強力な弓使いにしてあげましょう。でもその前に、SPをきちんと使ったか確認してください。3次転職するには、少なくともレベル70までにもらったSPをすべて使っている必要があります。ああ、それと、2次転職ですでに職業を選んでいるので、3次転職で選び直す必要はありません。今すぐ転職しますか？"); // 翻訳
                    return true;
                }
                return false;
            }
            case MODE_CHANGE -> {
                // はいを選択した直後
                if (status == this.no_status + 2) {
                    if (getJob() != this.job_from[this.job_index]) {
                        return false;
                    }
                    if (getRemainingSp() > (getLevel() - this.required_level) * 3) {
                        // 翻訳
                        say("#bSP#kが多すぎるようですね。そんなにSPを使わずに残したままでは3次転職できません。1次と2次のスキルにもっとSPを使ってから戻ってきてください。");
                        return false;
                    }
                    changeJob(this.job_to[this.job_index]);
                    gainAp(5);
                    say(this.job_texts[this.job_index], false, true);
                    return true;
                }
                if (status == this.no_status + 3) {
                    // 翻訳
                    say("あなたの助けになるよう、SPとAPも少し渡しておきました。これであなたは本当に強力な弓使いになりました。でも、現実の世界では、さらに乗り越えがたい障害が待ち受けていることを覚えておいてください。これ以上修行しても上の段階へ進めないと感じたときに、そのときだけ、私に会いに来てください。ここで待っていますよ。");
                    return false;
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
