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
public class Npc_ElNath_Pedro extends NpcScriptMan {

    private final int map_job_town = 120000000; // ノーチラス船着場 : ノーチラス
    private final int npc_job = 1072008; // カイリン
    private final int item_necklace_of_strength = 4031057; // 強靭のネックレス (or 4032785)
    private final int item_necklace_of_wisdom = 4031058; // 知恵のネックレス (or 4032789)
    private static final int STEP_NONE = 0;
    private static final int STEP_STRENGTH = 1;
    private static final int STEP_WISDOM = 2;
    private int step = STEP_NONE;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("私に何の用かね？");
                nt.addMenu(0, "3次転職がしたいです");
                nt.addMenu(1, "ジャクムダンジョンクエストを許可してください。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer != 0) {
                    // TODO : ジャクムダンジョンクエストの許可
                    return false;
                }
                if (checkItem(this.item_necklace_of_wisdom)) {
                    this.step = STEP_WISDOM;
                } else if (checkItem(this.item_necklace_of_strength)) {
                    this.step = STEP_STRENGTH;
                } else {
                    this.step = STEP_NONE;
                }
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                switch (this.step) {
                    case STEP_WISDOM -> {
                        nt.add("ほう…無事に聖地を見つけ、問いに正しい答えを返せたようだな。これで晴れて2つのテストはクリアだ。おめでとう。では、まず先にネックレスを預かろう。");
                        say(nt, false, true);
                    }
                    case STEP_STRENGTH -> {
                        nt.add("ほほう…#b" + getNpcName(this.npc_job) + "#kからの任務を完遂したようだな。君ならできると思っていたよ。だが、まだ気を抜くのは早い。2つ目のテストが残っているからな。じゃあ、まずはテストの前にネックレスを預かろうか。");
                        say(nt, false, true);
                    }
                    default -> {
                        nt.add("ん？　何用だね？　ほう……3次転職をして、更に強い海賊になりたいと？　無論、私の力で君を更に強くさせることはできるが、その前に君がどれだけ熱心に修練に励んだかを見せて欲しい。これまで強くなりたいと願って多くの者が私の元を訪れてきたが、実際に己の強さを署名できた者は数少ない。どうかね？　そうそう簡単ではないが、やってみるかね？");
                        askYesNo(nt);
                    }
                }
                return true;
            }
            case 2 -> {
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                switch (this.step) {
                    case STEP_WISDOM -> {
                        // TODO : 知恵のネックレスの回収
                        nt.add("よし…これで君はもっと強い海賊になれるはずだ。だが、その前にSPを全て消費したかどうか、確認させて欲しい。レベル70までに得た SPを全て消費しないと、3次転職はできないからね。あぁ、それと、3次転職ではどの職業にするかは特に悩む必要はない。そのために、2次転職の時に数々悩んでもらったのだ。さて、今すぐ転職するかね？");
                        askYesNo(nt);
                    }
                    case STEP_STRENGTH -> {
                        // TODO : 強靭のネックレスの回収
                        nt.add("うむ…確かに。では、これで残りは知恵のテストのみだな。このテストをクリアできれば、君はより一層強い海賊になるだろう。2つ目のテストは、オシリアの雪原のどこかにある、モンスターの近寄れない小さな聖地で行われる。見た目は平凡な聖地だが、特別なアイテムを捧げると、その者の知恵を試す試練が訪れるだろう。");
                        say(nt, true, true);
                    }
                    default -> {
                        nt.add("よし！　では早速テストをしよう。君に証明して欲しいのは2つ。君の持つ力と知恵を示すのだ。まずは力のテストから説明しよう。君が、1次、2次転職をした際に世話になったであろう、" + getMapName(this.map_job_town) + "の#b" + getNpcName(this.npc_job) + "#kがいるだろう？　彼女を訪ねれば、君に1つ任務を与えてくれるだろう。その任務を完遂し、" + getNpcName(this.npc_job) + "から #b#z" + this.item_necklace_of_strength + "##kを受け取ってくるのだ。");
                        say(nt, false, true);
                    }
                }
                return true;
            }
            case 3 -> {
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                switch (this.step) {
                    case STEP_WISDOM -> {
                        // TODO : 3次転職 (510 → 511, 520 → 521), SPとAPの付与
                        nt.add("おお…君はこの間、3次転職の難関をクリアした者だね。#bヴァイキング#kとしての生活はどうかね？　これから、更なる広い世界を旅しようと思っているなら、より一層強くならねばならないだろう。この大陸には、私も知らないような凶悪なモンスターがまだまだたくさんいるからな。何か気になることがあれば、いつでも訪ねてくるといい。頑張るのだぞ。");
                        say(nt, false, true);
                        return true;
                    }
                    case STEP_STRENGTH -> {
                        nt.add("まずは聖地を訪ね、特別なアイテムを捧げるのだ。そして、知恵の試練が始まったら、与えられた質問に正しい答えを返すのだ。君の答えに迷いがなければ、聖地は君を認め、#b#z" + this.item_necklace_of_wisdom + "##kを与えるだろう。その#b#z" + this.item_necklace_of_wisdom + "##kを私の元まで持ってくることができれば、私も君を認め、更に強い海賊へと転身させよう。では、幸運を祈る。");
                        say(nt, true, false);
                        return false;
                    }
                    default -> {
                        // TODO : 力のテストの開始
                        nt.add("2つ目は知恵のテストだが、これは力のテストを無事クリアできたら行う。まずは、力のテストをクリアし、#b#z" + this.item_necklace_of_strength + "##kを持ってくるのだ。じゃあ、#b" + getNpcName(this.npc_job) + "#kには私から話を通しておくから、この後すぐに訪ねてみるといい。決して簡単ではないだろうが、きっと君ならできると信じているよ。");
                        say(nt, true, false);
                        return false;
                    }
                }
            }
            case 4 -> {
                if (this.step != STEP_WISDOM) {
                    return false;
                }
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("さて。少しだが、君に SPと APを与えたから確認してみて欲しい。これで君は十分強い海賊になれただろう。だが、これから先はまだまだ辛く厳しい冒険が君を待ち受けている。もし君が己の成長に限界を感じたら、もう一度私を訪ねて欲しい。その時きっとまた、君の力になれるだろう。");
                say(nt, true, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
