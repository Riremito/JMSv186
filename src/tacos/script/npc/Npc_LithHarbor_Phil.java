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
public class Npc_LithHarbor_Phil extends NpcScriptMan {

    // 原文通りの並び
    private final int[] map_ids = {
        101000000, // エリニア : エリニア
        102000000, // ぺリオン : ぺリオン
        104000000, // リス : 港口
        100000000, // ヘネシス : ヘネシス
        103000000, // カニングシティー : カニングシティー
        120000000 // ノーチラス船着場 : ノーチラス
    };
    // 初心者は 90% 割引
    private final int[] costs = {
        1200,
        1200,
        0,
        800,
        1000,
        0 // TODO : ノーチラスの料金が不明
    };
    // 町の説明 (翻訳)
    private final int[] info_map_ids = {
        104000000, // リス : 港口
        102000000, // ぺリオン : ぺリオン
        101000000, // エリニア : エリニア
        100000000, // ヘネシス : ヘネシス
        103000000, // カニングシティー : カニングシティー
        105040300 // スリーピーウッド : スリーピーウッド
    };
    private final String[][] info_texts = {
        {
            "あなたが今いる町はリスハーバーです！では、#b#m104000000##kについて詳しく教えますね。船に乗ってビクトリアアイランドに来たときに降り立った場所、それが#m104000000#です。メイプルアイランドから来たばかりの初心者の多くが、ここから旅を始めます。",
            "港が島の西の端にあるおかげで、裏手に広い海が広がる静かな町です。ここの人たちの多くは、漁師か元漁師です。怖そうに見えるかもしれませんが、話しかけてみればきっと親切にしてくれますよ。",
            "町の周りには美しい草原が広がっています。ここのモンスターはほとんどが小さくておとなしいので、初心者にはぴったりです。まだ職業を選んでいないなら、レベルを上げるのにいい場所ですよ。"
        },
        {
            "では、#b#m102000000##kについて詳しく教えますね。ビクトリアアイランドの最北部にある戦士の町で、岩山に囲まれています。厳しい環境なので、強い者しか生き残れません。",
            "山岳地帯の近くでは、とても細い木や、うろつく野生のイノシシ、島じゅうに住んでいるサルたちに出会えます。深い谷もあって、そこに入っていくと、その大きさに見合うほどの力を持つ巨大なドラゴンに出会うでしょう。行くならよく気をつけるか、いっそ行かないほうがいいですよ。",
            "#b戦士#kになりたいなら、#m102000000#の長である#r#p1022000##kを訪ねてください。レベル10以上で、STRも十分にあれば、戦士にしてもらえます。そうでなければ、そのレベルに達するまで修行を続けたほうがいいですね。"
        },
        {
            "では、#b#m101000000##kについて詳しく教えますね。ビクトリアアイランドの東の奥にある魔法使いの町で、高くて神秘的な木々に覆われています。そこでは妖精にも出会えますよ。妖精はたいてい人間が好きではないので、近づかずにおとなしくしていたほうがいいでしょう。",
            "森の近くには、グリーンスライム、歩くキノコ、サル、ゾンビモンキーなどが住んでいます。森の奥深くに入ると、空飛ぶほうきに乗って空を飛び回る魔女たちに出会うでしょう。忠告しておきますが、本当に強くない限り、近づかないことをおすすめします。",
            "#b魔法使い#kになりたいなら、#m101000000#の大魔法使いである#r#p1032001##kを訪ねてください。レベル8以上で、INTも十分にあれば、魔法使いにしてもらえます。そうでなければ、もっと頑張って修行する必要がありますね。"
        },
        {
            "では、#b#m100000000##kについて詳しく教えますね。島の南部にある弓使いの町で、うっそうとした森と草原に囲まれた平地にあります。気候は最高で、町の周りには何でも豊富にあるので、暮らすには最適です。ぜひ行ってみてください！",
            "草原では、カタツムリやキノコ、ブタなどの弱いモンスターに出会えます。ただ、町とどこかでつながっているブタの公園の奥深くでは、ときどきキノコママと呼ばれる巨大で強力なキノコに出会うことがあると聞きました。",
            "#b弓使い#kになりたいなら、#m100000000#の#r#p1012100##kに会いに行ってください。レベル10以上で、DEXも十分にあれば、弓使いにしてもらえるでしょう。そうでなければ、修行して強くなってから、もう一度挑戦してください。"
        },
        {
            "では、#b#m103000000##kについて詳しく教えますね。ビクトリアアイランドの北西部にある盗賊の町です。そこにある建物には、どこか不思議な雰囲気が漂っています。ほとんどが黒い雲に覆われていますが、高いところに登れば、美しい夕日を見ることができますよ。",
            "#m103000000#からは、いろいろなダンジョンに行けます。ワニやヘビがいっぱいの沼地に行くこともできるし、幽霊やコウモリがいっぱいの地下トンネルに行くこともできます。地下の一番深いところでは、ドラゴンと同じくらい大きくて危険なレイスに出会うでしょう。",
            "#b盗賊#kになりたいなら、#m103000000#の闇の中心人物である#r#p1052001##kを訪ねてください。レベル10以上で、DEXも十分にあれば、立派な盗賊にしてもらえるでしょう。そうでなければ、狩りをして修行してください。"
        },
        {
            "では、#b#m105040300##kについて詳しく教えますね。ビクトリアアイランドの南東にある森の町です。#m100000000#とアリの巣ダンジョンのほぼ中間にあります。ホテルがあるので、ダンジョンで疲れた一日の後に休むことができますよ…普段は静かな町です。",
            "ホテルの前には、#r#p1061000##kという年老いた僧侶がいます。その僧侶について知っている人は誰もいません。旅人から材料を集めて何かを作っているらしいのですが、詳しいことはよく知りません。あのあたりに用事があるなら、代わりに確かめてきてもらえませんか。",
            "#m105040300#から東へ進むと、ビクトリアアイランドの最深部につながるアリの巣があります。そこには強力でおぞましいモンスターがたくさんいます。公園の散歩気分で行ったら、あっという間に死体になってしまいますよ。行く前に、厳しい道のりに備えて十分に準備しなければなりません。",
            "聞いた話ですが…どうやら#m105040300#には、未知の場所へ通じる秘密の入口があるそうです。奥深くまで入っていくと、動き回る黒い石の山が本当にあるらしいんです。いつか自分の目で見てみたいものですね…"
        }
    };
    private final int MENU_INFO = 0;
    private final int MENU_TELEPORT = 1;
    private int menu = -1;
    private int info_index = -1;
    private int to_map_index = -1;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        boolean beginner = getPlayer().getJob() == 0;
        switch (status) {
            case 0 -> {
                // 翻訳
                say("ほかの町へ行きたいですか？少しのお金でお連れできますよ。ちょっと高いですが、初心者には90%の特別割引をしています。", false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("ここに来るのが初めてなら、この場所に戸惑うのも無理はありません。この場所について質問があれば、何でも聞いてください。");
                nt.addMenu(MENU_INFO, "ビクトリアアイランドにはどんな町がありますか？");
                nt.addMenu(MENU_TELEPORT, "ほかの場所に連れて行って。");
                askMenu(nt);
                return true;
            }
            case 2 -> {
                if (this.asked) {
                    // 翻訳
                    say("この町にも見どころはたくさんありますよ。ほかの場所へ行きたくなったら言ってください。");
                    return false;
                }
                this.menu = answer;
                switch (this.menu) {
                    case MENU_INFO -> {
                        // 翻訳
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("ビクトリアアイランドには大きな町が6つあります。どの町について詳しく知りたいですか？");
                        for (int i = 0; i < this.info_map_ids.length; i++) {
                            nt.addMenu(i, getMapName(this.info_map_ids[i]));
                        }
                        askMenu(nt);
                        return true;
                    }
                    case MENU_TELEPORT -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        if (beginner) {
                            // 翻訳
                            nt.add("初心者の方には90%の特別割引があります。さて、どこへ行きますか？");
                        } else {
                            // 原文ママ
                            nt.add("君は初心者ではないな？なら料金は規定どおりにいただくぜ？さあ、どの村へ行きたいんだい？");
                        }
                        for (int i = 0; i < this.map_ids.length; i++) {
                            // 現在のマップはスキップ
                            if (getMapId() == this.map_ids[i]) {
                                continue;
                            }
                            int cost = beginner ? this.costs[i] / 10 : this.costs[i];
                            nt.addMenu(i, getMapName(this.map_ids[i]) + "(" + cost + "メル)");
                        }
                        askMenu(nt);
                        return true;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case 3 -> {
                switch (this.menu) {
                    case MENU_INFO -> {
                        if (answer < 0 || this.info_map_ids.length <= answer) {
                            return false;
                        }
                        this.info_index = answer;
                        say(this.info_texts[this.info_index][0], false, true);
                        return true;
                    }
                    case MENU_TELEPORT -> {
                        if (answer < 0 || this.map_ids.length <= answer || getMapId() == this.map_ids[answer]) {
                            return false;
                        }
                        this.to_map_index = answer;
                        this.asked = true;
                        // 原文ママ
                        askYesNo("ここではもう用事がないようですね。本当に" + getMapName(this.map_ids[this.to_map_index]) + "へ移動しますか？");
                        return true;
                    }
                    default -> {
                    }
                }
                return false;
            }
            default -> {
                switch (this.menu) {
                    case MENU_INFO -> {
                        int index = status - 3;
                        if (this.info_index < 0 || this.info_texts[this.info_index].length <= index) {
                            return false;
                        }
                        boolean next = index + 1 < this.info_texts[this.info_index].length;
                        say(this.info_texts[this.info_index][index], false, next);
                        return next;
                    }
                    case MENU_TELEPORT -> {
                        if (status != 4) {
                            return false;
                        }
                        int cost = beginner ? this.costs[this.to_map_index] / 10 : this.costs[this.to_map_index];
                        if (!payMoney(cost)) {
                            // 翻訳
                            say("お金が足りませんね。あなたほどの腕前なら、もっと持っていてもいいはずなのに！");
                            return false;
                        }
                        warp(this.map_ids[this.to_map_index]);
                        return false;
                    }
                    default -> {
                    }
                }
            }
        }
        return false;
    }
}
