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
public class Npc_ElNath_Arec extends NpcScriptMan {

    private static final int MENU_JOB_3RD = 0;
    private static final int MENU_ZAKUM = 1;
    private static final int MODE_NONE = 0;
    private static final int MODE_START = 1;
    private static final int MODE_STRENGTH = 2;
    private static final int MODE_WISDOM = 3;
    private static final int MODE_CHANGE = 4;
    private final int[] job_from = {
        410, // アサシン
        420, // シーフ
        432 // ブレードスペシャリスト
    };
    private final int[] job_to = {
        411, // ハーミット
        421, // シーフマスター
        433 // ブレードロード
    };
    // 試験を行わない
    private final boolean[] skip_test = {
        false,
        false,
        true // TODO : デュアルブレードの試験が不明
    };
    // 転職後の文章 (翻訳)
    private final String[] job_texts = {
        "お前は正式に#bハーミット#kになった。この本には、影を分身や身代わりとして使うハーミットの新しい攻撃スキルが載っている。#bシャドーメル#k（MPの代わりにメルを使い、投げたメルの量に応じてモンスターにダメージを与える）や#bシャドーパートナー#k（ハーミットの動きをすべて真似る影を作り出し、まるでハーミットが2人いるかのようにモンスターを攻撃できる）といったスキルがある。これらのスキルを使って、倒しにくいモンスターを打ち倒せ。",
        "お前は正式に#bシーフマスター#kになった。スキルブックの新しいスキルの一つに#bバンドオブシーブス#kというスキルがある。これを使うと、ほかのシーフたちを呼び出して複数のモンスターを一度に攻撃できる。シーフマスターはメルをさまざまな方法で使うこともできる。モンスターを攻撃したり（#bメルエクスプロージョン#k、地面のメルを爆発させる）、身を守ったり（#bメルガード#k、物理ダメージを軽減する）できるんだ。",
        "You are now a #bBlade Lord#k. May #rOdin#k be with you!" // TODO : 文章が不明
    };
    // 3次転職, 4次転職後の挨拶 (翻訳)
    private final int[] greeting_jobs = {
        411, // ハーミット
        421, // シーフマスター
        412, // ナイトロード
        422 // シャドー
    };
    private final String[] greeting_texts = {
        "3次転職の試験に合格したのはお前だな。#bハーミット#kの生活はどうだ？この地を旅しながら修行を続けなければならないぞ。オシリアには、俺でさえ知らない強力なモンスターがたくさんいる。何か疑問があれば、この道の終わりで俺に話しかけてくれ。幸運を祈る。",
        "3次転職の試験に合格したのはお前だな。#bシーフマスター#kの生活はどうだ？この地を旅しながら修行を続けなければならないぞ。オシリアには、俺でさえ知らない強力なモンスターがたくさんいる。何か疑問があれば、この道の終わりで俺に話しかけてくれ。幸運を祈る。",
        "盗賊の中で最強になったのはお前だな。#bナイトロード#kとして、すべての者の手本とならなければならない。この世界にはまだたくさんの秘密がある。お前のような盗賊の力は大きな助けになるだろう。だから、修行を続けろ。",
        "盗賊の中で最強になったのはお前だな。#bシャドー#kとして、すべての者の手本とならなければならない。この世界にはまだたくさんの秘密がある。お前のような盗賊の力は大きな助けになるだろう。だから、修行を続けろ。"
    };
    private final int job_min = 400;
    private final int job_max = 499;
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
                if (getPlayer().getLevel() < this.zakum_level) {
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
                        String zakum_data = getPlayer().getQuest(MapleQuest.getInstance(this.quest_zakum)).getCustomData();
                        // 翻訳
                        if (zakum_data != null && !zakum_data.isEmpty()) {
                            say("ジャクムのダンジョンクエストはどうだ？その奥深くにはとんでもないモンスターがいると聞いた…とにかく、幸運を祈る。君ならきっとやり遂げられる。");
                            return false;
                        }
                        if (getPlayer().getJob() < this.job_min || this.job_max < getPlayer().getJob()) {
                            // 原文ママ
                            say("ジャクムダンジョンを探索しようとしてるか？盗賊でもないくせに声をかけるな。君の職業の長老を訪ねてくれ。");
                            return false;
                        }
                        MapleQuest.getInstance(this.quest_zakum).forceStart(getPlayer(), getNpcId(), "s");
                        say("ジャクムのダンジョンクエストを行う許可がほしいのだな？#b#p2030008##kからだろう…よし、いいだろう！君ならダンジョンでも大丈夫だと確信している。向こうでは気をつけてくれよ…");
                        return false;
                    }
                    case MENU_JOB_3RD -> {
                        int job = getPlayer().getJob();
                        if (getPlayer().getLevel() < this.required_level) {
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
                            say("俺は#bアレク#k、すべての盗賊の長だ。聞きたい者には、裏通りの知識や俺の苦労話を聞かせてやるつもりでいる。だが、お前は盗賊には見えないな。お前に俺の知識を分けてやることはできない。この部屋には、それぞれの職業の長たちがいる。何か用があれば、その誰かに話しかけろ。");
                            return false;
                        }
                        int quest_status = getPlayer().getQuestStatus(this.quest_job_3rd);
                        String quest_data = getPlayer().getQuest(MapleQuest.getInstance(this.quest_job_3rd)).getCustomData();
                        if (this.skip_test[this.job_index] || quest_status == 2) {
                            this.mode = MODE_CHANGE;
                            this.no_status = status - 1;
                            this.no_text = "君はもう試験に合格しているから、心配することはない…まあ、決めたら話しかけてくれ。準備ができたら、3次転職させてやろう。準備ができていればの話だがな…"; // 翻訳
                            askYesNo("よし！これからお前をはるかに強力な盗賊にしてやる。だがその前に、SPをきちんと使ったか確認しろ。3次転職するには、少なくともレベル70までにもらったSPをすべて使っている必要がある。ああ、それと、2次転職ですでに職業を選んでいるから、3次転職で選び直す必要はない。今すぐ転職するか？"); // 翻訳
                            return true;
                        }
                        if (quest_status == 0) {
                            this.mode = MODE_START;
                            this.no_status = status - 1;
                            this.no_text = "まだこれから待ち受ける試練に立ち向かう準備ができていないようだな。転職に伴う試練に立ち向かう準備ができたと自分で確信できたときにだけ、私に会いに来なさい。"; // 翻訳
                            askYesNo("ようこそ。俺は#bアレク#k、すべての盗賊の長だ。聞きたい者には、裏通りの知識や俺の苦労話を聞かせてやるつもりでいる。お前は一歩前に進んで、3次転職の試練に立ち向かう準備ができているようだな。3次転職の条件を満たせずに去っていった盗賊を、俺はたくさん見てきた。お前はどうだ？3次転職の試験に挑戦する準備はできているか？"); // 翻訳
                            return true;
                        }
                        if ("p2".equals(quest_data)) {
                            if (!checkItem(this.item_necklace_of_strength)) {
                                // 翻訳
                                say("#b#t4031057##kを持っていないようだな。カニングシティの#b#p1052001##kのところへ行って試験に合格し、#b#t4031057##kを持ってこい。そうすれば試験の前半に合格だ。幸運を祈る！");
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
                                say("#b#t4031058##kを持っていないようだな。オシリアの雪原にある聖地と呼ばれる薄暗い雪に覆われた場所を探し、黒水晶を捧げ物として供えて、すべての質問に正直に、確信を持って答えれば、#b#t4031058##kがもらえる。それを持ち帰れば、3次転職の試験は完了だ。幸運を祈る…");
                                return false;
                            }
                            this.mode = MODE_WISDOM;
                            // 翻訳
                            say("試験の知恵の部分を完了したな、おめでとう。すべての質問に正しく、賢明に答えた。君が示した知恵の高さには、正直とても感心したよ。次の一歩に進む前に、まずはネックレスを渡してくれ。", false, true);
                            return true;
                        }
                        // 翻訳
                        say("#b#t4031057##kを持っていないようだな。カニングシティの#b#p1052001##kのところへ行って試験に合格し、#b#t4031057##kを持ってこい。そうすれば2つ目の試験を受けられる。幸運を祈る。");
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
                        MapleQuest.getInstance(this.quest_job_3rd).forceStart(getPlayer(), getNpcId(), "s");
                        // 翻訳
                        say("よし。お前は盗賊にとって大切な2つの面、力と知恵を試されることになる。まず試験の力の部分について説明しよう。カニングシティの#b#p1052001##kを覚えているか？あいつのところへ行って、試験の最初の部分について説明を聞いてこい。ミッションを完了して、#p1052001#から#b#t4031057##kを受け取るんだ。", false, true);
                        return true;
                    }
                    case 3 -> {
                        // 翻訳
                        say("試験の知恵の部分は、力の部分に合格してから始まる。#b#t4031057##kが試験に合格した証になる。お前が向かうことを#b#p1052001##kに伝えておこう。準備しておけ。簡単ではないが、お前を大いに信じているぞ。幸運を祈る！");
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
                            say("本当に#b#p1052001##kからもらった#b#t4031057##kを持っているか？ETCインベントリに空きを作っておくのを忘れるな。");
                            return false;
                        }
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_necklace_of_strength, 1, true, false);
                        MapleQuest.getInstance(this.quest_job_3rd).forceStart(getPlayer(), getNpcId(), "end1");
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
                    MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_necklace_of_wisdom, 1, true, false);
                    MapleQuest.getInstance(this.quest_job_3rd).forceComplete(getPlayer(), getNpcId());
                    this.mode = MODE_CHANGE;
                    this.no_status = status - 1;
                    this.no_text = "君はもう試験に合格しているから、心配することはない…まあ、決めたら話しかけてくれ。準備ができたら、3次転職させてやろう。準備ができていればの話だがな…"; // 翻訳
                    askYesNo("よし！これからお前をはるかに強力な盗賊にしてやる。だがその前に、SPをきちんと使ったか確認しろ。3次転職するには、少なくともレベル70までにもらったSPをすべて使っている必要がある。ああ、それと、2次転職ですでに職業を選んでいるから、3次転職で選び直す必要はない。今すぐ転職するか？"); // 翻訳
                    return true;
                }
                return false;
            }
            case MODE_CHANGE -> {
                // はいを選択した直後
                if (status == this.no_status + 2) {
                    if (getPlayer().getJob() != this.job_from[this.job_index]) {
                        return false;
                    }
                    if (getPlayer().getRemainingSp() > (getPlayer().getLevel() - this.required_level) * 3) {
                        // 翻訳
                        say("ふむ…#bSP#kが多すぎるようですね。そんなにSPを使わずに残したままでは3次転職できません。1次と2次のスキルにもっとSPを使ってから戻ってきてください。");
                        return false;
                    }
                    getPlayer().changeJob(this.job_to[this.job_index]);
                    getPlayer().gainAp((short) 5);
                    say(this.job_texts[this.job_index], false, true);
                    return true;
                }
                if (status == this.no_status + 3) {
                    // 翻訳
                    say("スタートの助けになるよう、SPとAPを少し渡しておいた。これでお前は本当に強力な盗賊になった。だが、現実の世界では、さらに乗り越えがたい障害が待ち受けていることを覚えておけ。これ以上修行しても上の段階へ進めないと感じたときに、そのときだけ、俺に会いに来い。ここで待っているぞ。");
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
