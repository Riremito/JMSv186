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
public class NpcTylus extends NpcScriptMan {

    private static final int MENU_JOB_3RD = 0;
    private static final int MENU_ZAKUM = 1;
    private static final int MENU_4TH = 2;
    private static final int MODE_NONE = 0;
    private static final int MODE_START = 1;
    private static final int MODE_STRENGTH = 2;
    private static final int MODE_WISDOM = 3;
    private static final int MODE_CHANGE = 4;
    private final int[] job_from = {
        110, // ファイター
        120, // ページ
        130 // スピアマン
    };
    private final int[] job_to = {
        111, // クルセイダー
        121, // ナイト
        131 // ドラゴンナイト
    };
    // 試験を行わない
    private final boolean[] skip_test = {
        false,
        false,
        false
    };
    // 転職後の文章 (翻訳)
    private final String[] job_texts = {
        "君は今、#bクルセイダー#kになった。新しい攻撃スキルの#bシャウト#kや#bコンボアタック#kは強烈だし、#bアーマークラッシュ#kはモンスターの防御力を崩す。ファイター時代に極めた武器用のスキルを習得することに集中するといい。",
        "君は今、#bナイト#kになった。たくさんの新しい攻撃スキルや属性攻撃が載った新しいスキルブックが与えられる。ページ時代に使っていた剣か鈍器のどちらかを、ナイトになっても使い続けることをすすめる。#bチャージ#kというスキルは武器に炎、氷、雷の属性を付与するもので、ナイトは属性攻撃ができる唯一の戦士だ。モンスターの弱点となる属性を武器に宿して、#bチャージブロー#kで大ダメージを与えろ。そうすれば、間違いなく圧倒的な力になる。",
        "今日から君は#bドラゴンナイト#kだ。槍と矛用の新しい攻撃スキルがいろいろと使えるようになる。スピアマン時代に選んだ武器を、ドラゴンナイトになっても使い続けるといい。#bクラッシャー#k（1体のモンスターに最大ダメージ）や#bドラゴンフューリー#k（複数のモンスターにダメージ）が主力の攻撃スキルとしておすすめだ。また、#bドラゴンロアー#kというスキルは、画面上のあらゆる生き物に圧倒的な力でダメージを与える。欠点は、現在のHPの半分以上を消費することだ。"
    };
    // 3次転職, 4次転職後の挨拶 (翻訳)
    private final int[] greeting_jobs = {
        111, // クルセイダー
        121, // ナイト
        131, // ドラゴンナイト
        112, // ヒーロー
        122, // パラディン
        132 // ダークナイト
    };
    private final String[] greeting_texts = {
        "3次転職の試験に合格したのは君だな。#bクルセイダー#kの生活はどうだ？この地を旅しながら修行を続けなければならないぞ。オシリアには、私でさえ知らない強力なモンスターがたくさんいる。何か疑問があれば、この道の終わりで私に話しかけてくれ。幸運を祈る。",
        "3次転職の試験に合格したのは君だな。#bナイト#kの生活はどうだ？この地を旅しながら修行を続けなければならないぞ。オシリアには、私でさえ知らない強力なモンスターがたくさんいる。何か疑問があれば、この道の終わりで私に話しかけてくれ。幸運を祈る。",
        "3次転職の試験に合格したのは君だな。#bドラゴンナイト#kの生活はどうだ？この地を旅しながら修行を続けなければならないぞ。オシリアには、私でさえ知らない強力なモンスターがたくさんいる。何か疑問があれば、この道の終わりで私に話しかけてくれ。幸運を祈る。",
        "戦士の中で最強になったのは君だな。#bヒーロー#kとして、すべての者の手本とならなければならない。この世界にはまだたくさんの秘密がある。君のような戦士の力は大きな助けになるだろう。だから、修行を続けてくれ。",
        "戦士の中で最強になったのは君だな。#bパラディン#kとして、すべての者の手本とならなければならない。この世界にはまだたくさんの秘密がある。君のような戦士の力は大きな助けになるだろう。だから、修行を続けてくれ。",
        "戦士の中で最強になったのは君だな。#bダークナイト#kとして、すべての者の手本とならなければならない。この世界にはまだたくさんの秘密がある。君のような戦士の力は大きな助けになるだろう。だから、修行を続けてくれ。"
    };
    private final int job_min = 0;
    private final int job_max = 199;
    private final int required_level = 70;
    private final int zakum_level = 50;
    private final int quest_job_3rd = 7500; // 3次転職
    private final int quest_zakum = 7000; // ジャクムダンジョン 1段階
    private final int quest_4th = 6192; // タイラスの護衛
    private final int npc_job = 1022000; // コブシを開いて立て
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
                if (getPlayer().getQuestStatus(this.quest_4th) == 1) {
                    nt.addMenu(MENU_4TH, "#q" + this.quest_4th + "#");
                }
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
                            say("ジャクムのダンジョンクエストを行う許可がほしいのだな。悪いが、君は戦士には見えない。自分の職業の長を訪ねてくれ。");
                            return false;
                        }
                        MapleQuest.getInstance(this.quest_zakum).forceStart(getPlayer(), getNpcId(), "s");
                        say("ジャクムのダンジョンクエストを行う許可がほしいのだな？#b#p2030008##kからだろう…よし、いいだろう！君ならダンジョンでも大丈夫だと確信している。向こうでは気をつけてくれよ…");
                        return false;
                    }
                    case MENU_4TH -> {
                        // TODO : FieldSet (S4common1) で入場する (パーティーのリーダーのみ, 4次転職した戦士のみ)
                        return false;
                    }
                    case MENU_JOB_3RD -> {
                        int job = getPlayer().getJob();
                        if (getPlayer().getLevel() < this.required_level) {
                            // 翻訳
                            say("君はまだ3次転職する資格がない。そのためには少なくともレベル70が必要だ。もっと修行してから私に会いに来てくれ。");
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
                            say("私は#bタイラス#k、すべての戦士の長であり、私の導きを必要とする戦士たちの力を最大限に引き出す役目を担っている。だが、君は戦士には見えないな。残念ながら、君の力にはなれない。この部屋には、それぞれの職業の長たちがいる。何か用があれば、その誰かに話しかけてくれ。");
                            return false;
                        }
                        int quest_status = getPlayer().getQuestStatus(this.quest_job_3rd);
                        String quest_data = getPlayer().getQuest(MapleQuest.getInstance(this.quest_job_3rd)).getCustomData();
                        if (this.skip_test[this.job_index] || quest_status == 2) {
                            this.mode = MODE_CHANGE;
                            this.no_status = status - 1;
                            this.no_text = "君はもう試験に合格しているから、心配することはない…まあ、決めたら話しかけてくれ。準備ができたら、3次転職させてやろう。準備ができていればの話だがな…"; // 翻訳
                            askYesNo("よし！これから君をはるかに強力な戦士にしてやろう。だがその前に、SPをきちんと使ったか確認してくれ。3次転職するには、少なくともレベル70までにもらったSPをすべて使っている必要がある。ああ、それと、2次転職ですでに職業を選んでいるから、3次転職で選び直す必要はない。今すぐ転職するか？"); // 翻訳
                            return true;
                        }
                        if (quest_status == 0) {
                            this.mode = MODE_START;
                            this.no_status = status - 1;
                            this.no_text = "まだこれから待ち受ける試練に立ち向かう準備ができていないようだな。転職に伴う試練に立ち向かう準備ができたと自分で確信できたときにだけ、私に会いに来なさい。"; // 翻訳
                            askYesNo("おほん、何の用で私を訪ねたんだい？む・・・.3次転職をし、もっと強い戦士になりたいのか。もちろん私の力で君をもう少し強くしてあげる事はできるが、その前に君がどれほど熱心に修練したのかについて検証が必要なんだ。今までもっと強くなりたいと私を訪ねてきた若者たちは多かったが、実際に自分の強さを証明できたやつはあまりいなかった。どうだい？しくはないだろうが君を試してもいいかい？"); // 原文ママ
                            return true;
                        }
                        if ("p2".equals(quest_data)) {
                            if (!checkItem(this.item_necklace_of_strength)) {
                                // 翻訳
                                say("#b#t4031057##kを持っていないようだな。ペリオンの#b#p1022000##kのところへ行って試験に合格し、#b#t4031057##kを持ってきてくれ。そうすれば試験の前半に合格だ。幸運を祈る！");
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
                        say("#b#t4031057##kを持っていないようだな。ペリオンの#b#p1022000##kのところへ行って試験に合格し、#b#t4031057##kを持ってきてくれ。そうすれば2つ目の試験を受けられる。幸運を祈る。");
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
                        // 原文ママ
                        say("よし！君が証明しなければいけないのは、 君の力と知恵、この二つだ。まず、力についての試験から説明をしてあげるよう。君に1次、2次転職をさせてくれたペリオンの #b" + getNpcName(this.npc_job) + "#kを覚えているだろう？を訪ねると彼が君に一つの任務を託するだろう。その任務を無事に完遂し、 " + getNpcName(this.npc_job) + "から#b#z" + this.item_necklace_of_strength + "##kをもらって来てくれ。", false, true);
                        return true;
                    }
                    case 3 -> {
                        // 翻訳
                        say("試験の知恵の部分は、力の部分に合格してから始まる。#b#t4031057##kが試験に合格した証になる。君が向かうことを#b#p1022000##kに伝えておこう。準備しておけ。簡単ではないが、君を大いに信じているぞ。幸運を祈る！");
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
                            say("本当に#b#p1022000##kからもらった#b#t4031057##kを持っているか？ETCインベントリに空きを作っておくのを忘れないでくれ。");
                            return false;
                        }
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_necklace_of_strength, 1, true, false);
                        MapleQuest.getInstance(this.quest_job_3rd).forceStart(getPlayer(), getNpcId(), "end1");
                        // 翻訳
                        say("これが試験の後半だ。この試験で、君が偉大さへの次の一歩を踏み出せるほど賢いかどうかが決まる。オシリアの雪原に、聖地と呼ばれる薄暗い雪に覆われた場所がある。モンスターさえたどり着けない場所だ。その中心には聖なる石と呼ばれる巨大な石がある。そこに特別なアイテムを捧げ物として供えなければならない。聖なる石がその場で君の知恵を試すだろう。", false, true);
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
                    askYesNo("よし！これから君をはるかに強力な戦士にしてやろう。だがその前に、SPをきちんと使ったか確認してくれ。3次転職するには、少なくともレベル70までにもらったSPをすべて使っている必要がある。ああ、それと、2次転職ですでに職業を選んでいるから、3次転職で選び直す必要はない。今すぐ転職するか？"); // 翻訳
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
                        say("ふむ…#bSP#kが多すぎるようだ。そんなにSPを使わずに残したままでは3次転職できない。1次と2次のスキルにもっとSPを使ってから戻ってきてくれ。");
                        return false;
                    }
                    getPlayer().changeJob(this.job_to[this.job_index]);
                    getPlayer().gainAp((short) 5);
                    say(this.job_texts[this.job_index], false, true);
                    return true;
                }
                if (status == this.no_status + 3) {
                    // 翻訳
                    say("スタートの助けになるよう、SPとAPも少し渡しておいた。これで君は本当に強力な戦士になった。だが、現実の世界では、さらに乗り越えがたい障害が待ち受けていることを覚えておいてくれ。これ以上修行しても上の段階へ進めないと感じたときに、そのときだけ、私に会いに来い。ここで待っているぞ。");
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
