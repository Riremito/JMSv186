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
// Npc.wz script : refine_TCG1 (JMS146-302), 会話文は lua_script_jp (refine_TCG1) を利用, 期間限定イベント (感謝祭) の会話は削除
public class Npc_LithHarbor_ProfessorFoxwit extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_TOP_MENU = 1;
    private static final int NODE_ARTIFACT_MENU = 2;
    private static final int NODE_INFO_MENU = 3;
    private static final int NODE_RECIPE_MENU = 4;
    private static final int NODE_RECIPE_SAY = 5;
    private static final int NODE_RECIPE_ASK = 6;
    private static final int NODE_SKIP_MENU = 7;
    private static final int NODE_INTRO_MENU = 8;
    private static final int NODE_INTRO_SAY = 9;
    private static final int NODE_INTRO_ASK = 10;
    private static final int NODE_SAYS = 11;
    private static final int NODE_SAY_END = 12;
    private static final int RECIPE_SKIP = 4;
    private final int quest_artifact = 4926; // 星間アーティファクト (クエストの状態)
    // 星間アーティファクト
    private final int[] items_artifact = {
        4031750, // ダークマター
        4031751, // ヴォルティキュラー・ジャイロ
        4031752, // 輝く文字盤
        4031753, // ゼータ・レジデュー
        4031754 // ブラック・バーサル・マテリアル
    };
    // 製法で作るアイテム
    private final int[] recipe_results = {
        1102146, // ジェットマント
        1102145, // シリウスマント
        1032049, // デーモンハートイヤリング
        1092052 // ブラックデザートシールド
    };
    // 製法の交換 {アイテムID, 増減数, ...}
    private final int[][] recipe_exchanges = {
        {4031750, -1, 4031751, -1, 4031826, 1},
        {4031752, -1, 4031750, -1, 4031754, -1, 4031827, 1},
        {4031752, -1, 4031753, -1, 4031828, 1},
        {4031751, -1, 4031753, -1, 4031754, -1, 4031829, 1}
    };
    // 製法に必要なアーティファクトの番号
    private final int[][] recipe_requires = {
        {0, 1},
        {0, 2, 4},
        {2, 3},
        {1, 3, 4}
    };
    private int node = NODE_START;
    private int last_status = 0;
    private String text = "";
    private String[] says = null;
    private int says_index = 0;
    private int recipe = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status < status;
        this.last_status = status;
        // 所持しているアーティファクト
        boolean[] has = new boolean[this.items_artifact.length];
        boolean has_any = false;
        for (int i = 0; i < this.items_artifact.length; i++) {
            has[i] = 0 < getItemCount(this.items_artifact[i]);
            has_any |= has[i];
        }
        // 作れるアイテム
        boolean[] can_make = new boolean[this.recipe_results.length];
        boolean can_make_any = false;
        for (int i = 0; i < this.recipe_requires.length; i++) {
            can_make[i] = true;
            for (int index : this.recipe_requires[i]) {
                can_make[i] &= has[index];
            }
            can_make_any |= can_make[i];
        }
        // 前の会話の応答の処理
        switch (this.node) {
            case NODE_START -> {
                this.node = NODE_TOP_MENU;
            }
            case NODE_TOP_MENU -> {
                if (answer != 0) {
                    return false;
                }
                if (!has_any) {
                    // 翻訳
                    this.text = "君が持っているものは、未来的とはほど遠いね。この話を続けるには未来的なものが必要なんだ。何か見つけたら戻ってきてくれ！";
                    this.node = NODE_SAY_END;
                    break;
                }
                if (getQuestStatus(this.quest_artifact) == 2) {
                    this.node = NODE_ARTIFACT_MENU;
                    break;
                }
                this.node = NODE_INTRO_MENU;
            }
            case NODE_ARTIFACT_MENU -> {
                if (answer == 0) {
                    this.node = NODE_INFO_MENU;
                    break;
                }
                if (answer != 1) {
                    return false;
                }
                if (!can_make_any) {
                    // 翻訳
                    this.text = "";
                    this.node = NODE_SAY_END;
                    break;
                }
                this.node = NODE_RECIPE_MENU;
            }
            case NODE_INFO_MENU -> {
                if (answer < 0 || this.items_artifact.length <= answer || !has[answer]) {
                    return false;
                }
                // 翻訳
                String[][] texts = {
                    {"素晴らしい！  どうやって手に入れたかは聞かないでおこう！  簡単に言うと、ダークマターは「そこに」あるのに肉眼ではなかなか見えない物質なんだ。  でも私は、ばらばらの黒い塊に見えるものが、実は別の時間軸で私たちの空間を移動している物体だということを知った…つまり、「ここ」にはあるけど「今」にはないということさ。  頭が痛くなってきたかい？  まあいい、理解する必要はない。  知っておいてほしいのは、この物質の時間を曲げる性質を使って、君のために作るもののエネルギー効率を高められるということだけだ。"},
                    {"なんと、動くジャイロじゃないか！  これは実に便利で用途の広い部品なんだ！  高度な文明では、ボーティキュラー・ジャイロは宇宙船から超高速フードプロセッサーまで、あらゆるものに使われていたんだよ！  いい知らせは、この仕組みを使えば間違いなく君のために何かを発明できるということだ。  実は、私はフォックスウィット・ドアの原子安定化モジュールを作るのにこれを使っているんだ。そうしないと、ドアを通るときに君の分子がみんなばらばらになって、移動がずっと不快なものになってしまうからね！  今ではジャイロを作る材料はなかなか手に入らないから、動くものを見つけてくれてうれしいよ。"},
                    {"興味深い…この装置から波動信号が出ているのを感じる。  これは星間通信の中継装置、いわば高度な信号発生器の一部として使われていたんじゃないかな。  点滅しているライトは、まだ動いていることを示しているようだ…まさに今この瞬間、誰が、あるいは何が、この装置に連絡を取ろうとしているのかわからないぞ！"},
                    {
                        "いやあ、実に興味深い…いや、いいんだ、持たなくていい。  しばらく持ち歩いていると言ったね？  それで体に異常は感じない？  ない？  よかった。  これを見たことはあるんだが、かなり珍しいものでね…私たちの誰も直接触れたことはないんだ。  少なくとも防護服なしではね。  私の同僚の中には、この残留物こそがグレイたちを故郷のゼータ・レティキュランから追い出した汚染物質だと信じている者もいる！  でも、君に悪影響はないようだから、実証的な根拠に基づいてその説は否定できそうだ。  正直に言うと、グレイについてはまだわからないことがたくさんあるんだ。",
                        "それに、このゼータの残留物は単なる異星人の…ええと、何と言えばいいか…グレイの消化過程で出る副産物にすぎないと考える学派もあるんだ。  ごほん。  念のため、少なくとも手袋をはめて扱った方がいいかもしれないね！"
                    },
                    {"ふむ…これは実に不思議な元素だね。  周りの暗いオーラを見てごらん…まるで周囲の光子そのものを吸い込んでいるようだ！  こんなものは見たことがない！  ふむ、この元素がこの世界のものかどうかさえわからないな。  ちょっと待って、簡単に測定してみよう…うわっ！  計測不能だ！  この元素は非常に不安定なようだね！  まあいい、その混沌とした性質も何かに活用できるはずさ！"}
                };
                this.says = texts[answer];
                this.says_index = 0;
                this.node = NODE_SAYS;
            }
            case NODE_RECIPE_MENU -> {
                if (answer == RECIPE_SKIP) {
                    this.node = NODE_SKIP_MENU;
                    break;
                }
                if (answer < 0 || this.recipe_results.length <= answer || !can_make[answer]) {
                    return false;
                }
                this.recipe = answer;
                this.node = NODE_RECIPE_SAY;
            }
            case NODE_RECIPE_SAY -> {
                this.node = NODE_RECIPE_ASK;
            }
            case NODE_RECIPE_ASK, NODE_SKIP_MENU -> {
                if (this.node == NODE_SKIP_MENU) {
                    if (answer < 0 || this.recipe_results.length <= answer || !can_make[answer]) {
                        return false;
                    }
                    this.recipe = answer;
                } else if (answer != 0) {
                    // 翻訳
                    this.text = "心配いらない！  アーティファクトを見つけたらまた持ってきてくれ。きっと君の気に入る発明品の設計図があるはずだ！";
                    this.node = NODE_SAY_END;
                    break;
                }
                if (!exchange(0, this.recipe_exchanges[this.recipe])) {
                    // 翻訳
                    this.text = "必要なアイテムがすべてそろっているか、ETCインベントリがいっぱいでないか確認してくれ。";
                    this.node = NODE_SAY_END;
                    break;
                }
                // 翻訳
                String[] texts = {
                    "よし！  アーティファクトを必要な部品に分解して、アイテムの作り方を書いておいたよ。  この#b#t4031826##kを持っていくんだ！オメガセクターのスピンドルのところへ持っていってくれ。このアイテムを作るにはまだほかの材料も必要で、何が必要かはスピンドルが説明してくれる。彼なら私の設計図を読み解いて、全部組み立ててくれるはずさ。",
                    "よし！  アーティファクトを必要な部品に分解して、アイテムの作り方を書いておいたよ。  この#b#t4031827##kを持っていくんだ！オメガセクターのスピンドルのところへ持っていってくれ。このアイテムを作るにはまだほかの材料も必要で、何が必要かはスピンドルが説明してくれる。彼なら私の設計図を読み解いて、全部組み立ててくれるはずさ。",
                    "よし！  アーティファクトを必要な部品に分解して、アイテムの作り方を書いておいたよ。  この#b#t4031828##kを持っていくんだ！オメガセクターのスピンドルのところへ持っていってくれ。このアイテムを作るにはまだほかの材料も必要で、何が必要かはスピンドルが説明してくれる。彼なら私の設計図を読み解いて、全部組み立ててくれるはずさ。",
                    "よし！  アーティファクトを必要な部品に分解して、アイテムの作り方を書いておいたよ。  この#b#t4031829##kを持っていくんだ！オメガセクターのスピンドルのところへ持っていってくれ。このアイテムを作るにはまだほかの材料も必要で、何が必要かはスピンドルが説明してくれる。彼なら私の設計図を読み解いて、全部組み立ててくれるはずさ。"
                };
                this.text = texts[this.recipe];
                this.node = NODE_SAY_END;
            }
            case NODE_INTRO_MENU -> {
                if (answer == 1) {
                    // 翻訳
                    this.text = "物の外見の向こう側を見る目を鍛えるんだ。本当の謎は常に表面のずっと奥深くにあって、探求を続ければ何を解き放つことになるかわからないのだから…";
                    this.node = NODE_SAY_END;
                    break;
                }
                if (answer != 0) {
                    return false;
                }
                this.node = NODE_INTRO_SAY;
            }
            case NODE_INTRO_SAY -> {
                this.node = NODE_INTRO_ASK;
            }
            case NODE_INTRO_ASK -> {
                if (!ok) {
                    // 翻訳
                    this.text = "ふむ…君の中には好奇心の炎があると思っていたんだが。  その炎が戻ったら、また来てくれ。";
                    this.node = NODE_SAY_END;
                    break;
                }
                setQuestData(this.quest_artifact, "1");
                MapleQuest.getInstance(this.quest_artifact).forceComplete(getPlayer(), getNpcId());
                return false;
            }
            case NODE_SAYS -> {
                this.says_index++;
                if (this.says.length <= this.says_index) {
                    return false;
                }
            }
            default -> {
                return false;
            }
        }
        // 次の会話の表示
        switch (this.node) {
            case NODE_TOP_MENU -> {
                // 原文ママ
                // nt.add("どうしたんじゃ？");
                // nt.addMenu(0, "こんなものを見つけたからちょっと見てほしくて、あなたが適任者だと思ったの。");
                // nt.addMenu(1, "いいえ、なんでもないです。");
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("やあ！君が何を考えているかわかるぞ？何の用だい？");
                nt.addMenu(0, "未来的なものを見つけたんだ！  時間旅行をする科学者のあなたなら、話を聞くのにぴったりだと思って…");
                askMenu(nt);
                return true;
            }
            case NODE_ARTIFACT_MENU -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("おや、また君か。星間アーティファクトの探索は順調かい？\r\n#b#L0#持っているアーティファクトについてもっと教えてくれる？#l\r\n#L1#インベントリにあるアーティファクトで何か作れる？#l", false);
                askMenu(nt);
                return true;
            }
            case NODE_INFO_MENU -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("ああ、もちろんだとも！  若い世代が学ぶことに興味を示してくれるのは、本当にうれしいよ。  どのアーティファクトについて知りたい？", false);
                for (int i = 0; i < this.items_artifact.length; i++) {
                    if (has[i]) {
                        nt.add("\r\n#L" + i + "#この#b#t" + this.items_artifact[i] + "##kについてもっと教えてくれる？#l", false);
                    }
                }
                askMenu(nt);
                return true;
            }
            case NODE_RECIPE_MENU -> {
                String list2 = "";
                for (int i = 0; i < this.recipe_results.length; i++) {
                    if (can_make[i]) {
                        list2 += "\r\n#b#L" + i + "#- #t" + this.recipe_results[i] + "##k#l";
                    }
                }
                String list3 = "";
                for (int i = 0; i < this.items_artifact.length; i++) {
                    if (has[i]) {
                        list3 += "#t" + this.items_artifact[i] + "# ";
                    }
                }
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("いいぞ！  君のインベントリにあるアイテム（" + list3 + "）からすると、以下の設計図のものが作れるよ。発明品についてもっと知りたければ、名前をクリックしてくれ。" + list2 + "\r\n\r\n#b#L4#説明は飛ばして…アーティファクトからアイテムを作る準備はできてる！#k#l", false);
                askMenu(nt);
                return true;
            }
            case NODE_RECIPE_SAY -> {
                // 翻訳
                String[] texts = {
                    "面白い選択だね。  #bゼータケープ#kの仕組みには少し謎がある。  グレイたちはケープの素材にパワークリスタルを染み込ませる方法を発見したんだ。  グレイはテレパシーを使う種族だから、ケープの力は着用者と結びついていた。着用者の精神力や内なる意志の強さに反応して、それを着用者の周りの防護フィールドに変えていたんだ。  グレイがあれほど脅威なのも無理はない！  私の推測では、人間も知性ある存在だから、ケープは人間にも効果があるはずだ。  まあ、少なくとも大半の人間にはね…",
                    "はるか未来へ旅したとき、ハイテクの荒廃した戦場を目にしたことがある。一人の戦士が#b#t1102145##kを使って、たった一人で軍団をまるごと倒していたんだ。その防護マントは敵の魔法弾やレーザーをはじくだけでなく、彼の力と動きも強化していたんだよ！",
                    "この#bダークシャード#kは、その見た目と秘めた力から名付けられたんだ。  偶然それを生み出した科学者を見たことがあってね。  彼は原子より小さなレベルの衝突を使って、パワークリスタルとブラッククリスタルを融合させたんだ。  できあがったかけらは不思議なオーラをまとっていて、近くにあるだけで物理的な力や魔法の力への耐性が上がるようだった。  新たに得た力を使って、彼はさらに危険な実験に身を投じていったんだが…それはまた別の機会に話そう。",
                    "歴史のあるものを選んだね。元祖#bブラックフェニックスシールド#kは、私が別の時間旅行で出会った伝説の超戦士集団の戦闘装備の一部だったんだ。  この盾は代々受け継がれ、年長の戦士たちは息子や娘を鍛え、ともに戦った。  年長者がついに戦場で倒れると、その亡骸が盾の上に置かれ、盾の動力源が起動する。  動力源は倒れた戦士の魂と力の一部を吸収し、その後、盾は後継者へと受け継がれた。  こうして盾の守りの力は世代を重ねるごとに強まり、新米の戦士は歴戦の勇士たちの力に守られるようになったんだ。"
                };
                say(texts[this.recipe], false, true);
                return true;
            }
            case NODE_RECIPE_ASK -> {
                // 翻訳
                String[] texts = {
                    "いずれにせよ、オメガセクターでの研究の結果、作るには#b#t4031751##kと#b#t4031750##kが必要だとわかっている。ケープの製作を助ける追加の合成アイテムもいくつか必要だ。作ってみるかい？\r\n#b#L0##t1102146#を作る#k#l\r\n#b#L1#いや…まだ決めていない。とりあえずこのアーティファクトは取っておいて、ほかに何が作れるか見てみるよ。#k#l",
                    "その時代で何日か過ごして、マントを複製する方法を突き止めた。  再現するのに足りなかった部品は、#b点滅するディンバット#k、#bダークマター#k、そして#bブラック・ヴェルサル・マテリア#kだけなんだが…どうやら君は全部持っているようだね！  マントの製作を助ける合成アイテムもいくつか必要になる。  シリウスクロークを作ってみるかい？\r\n#b#L0##t1102145#を作る#k#l\r\n#b#L1#いや…まだ決めていない。とりあえずこのアーティファクトは取っておいて、ほかに何が作れるか見てみるよ。#k#l",
                    "同僚のメモはまだ持っているから、彼の手順をたどってダークシャードを再現できる。  難しいのは#b点滅するディンバット#kと#bゼータの残留物#kを手に入れることなんだが、君はそれをやってのけた！  シャードをイヤリングに仕立てれば、身につけられるようになるよ。  融合を助ける合成アイテムもいくつか必要だ。  作ってみるかい？\r\n#b#L0##t1032049#を作る#k#l\r\n#b#L1#いや…まだ決めていない。とりあえずこのアーティファクトは取っておいて、ほかに何が作れるか見てみるよ。#k#l",
                    "私はこの立派な戦士たちとしばらく過ごして、盾やほかの装備の作りを研究することができた。  盾そのものを作るのは難しくなく、比較的ありふれた合成材料がいくつかあればいいんだが、特別な動力源を再現するには#bボーティキュラー・ジャイロ#k、#bゼータの残留物#k、#bブラック・ヴェルサル・マテリア#kが必要なんだ。  君はこのアーティファクトを持っているから、レプリカを作れると思う！  作ってみるかい？\r\n#b#L0##t1092052#を作る#k#l\r\n#b#L1#いや…まだ決めていない。とりあえずこのアーティファクトは取っておいて、ほかに何が作れるか見てみるよ。#k#l"
                };
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(texts[this.recipe], false);
                askMenu(nt);
                return true;
            }
            case NODE_SKIP_MENU -> {
                String list2 = "";
                for (int i = 0; i < this.recipe_results.length; i++) {
                    if (can_make[i]) {
                        list2 += "\r\n#b#L" + i + "#- #t" + this.recipe_results[i] + "##k#l";
                    }
                }
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("よし…一つだけ警告しておく。  一度このアーティファクトをいじり始めたら、もう後戻りはできないぞ！  何を作りたい？" + list2, false);
                askMenu(nt);
                return true;
            }
            case NODE_INTRO_MENU -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("そう、そう、君はまさにぴったりの人物のところに来たよ！  素晴らしい…それは私が思っているものかい？  そう、これは間違いなく星間アーティファクトだ…もっとよく見てもいいかな？\r\n#b#L0#どうぞ…ついでに、このアーティファクトについて教えてくれる？#l\r\n#L1#は？星間アーティファクト？  そんなのでたらめだよ。#l#k", false);
                askMenu(nt);
                return true;
            }
            case NODE_INTRO_SAY -> {
                // 翻訳
                say("このアーティファクトは間違いなく宇宙から来たものだ。おそらく技術的に進んだ文明のものだろう！実際、いくつかは宇宙船から来たもののように見える…でも、このあたりで宇宙船は見たことがないな。  これは部品として使うことができて、本当にたくさんのいろいろなアイテムを作れるんだ。この技術の一部を使ってフォックスウィット・ドアを作るのには慣れているんだが、ほかに何が作れるのかずっと気になっていたんだ。  その力は計り知れない！  こうしよう。  君のおかげで私の発明家魂に火がついた！  星間アーティファクトを見つけたら、君のために何か発明してみせるよ！  どうだい？", false, true);
                return true;
            }
            case NODE_INTRO_ASK -> {
                // 翻訳
                askYesNo("素晴らしい！  アーティファクトをいくつか手に入れたら戻ってきてくれ。  その間、私はもっと発明のアイデアを練っておくよ！");
                return true;
            }
            case NODE_SAYS -> {
                boolean next = this.says_index + 1 < this.says.length;
                say(this.says[this.says_index], false, next);
                return next;
            }
            case NODE_SAY_END -> {
                say(this.text);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
