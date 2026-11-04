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
// Npc.wz script : TCG3 (JMS164-302), 会話文は lua_script_jp (TCG3) を利用
public class NpcCorine extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_TOP_MENU = 1;
    private static final int NODE_SAYS = 2;
    private static final int NODE_SAY_END = 3;
    // タル族の話
    private static final int NODE_TAL_SAY = 10;
    private static final int NODE_TAL_MENU1 = 11;
    private static final int NODE_TAL_MENU2 = 12;
    private static final int NODE_TAL_MENU3 = 13;
    private static final int NODE_TAL_MENU4 = 14;
    private static final int NODE_TAL_MENU5 = 15;
    private static final int NODE_TAL_MENU6 = 16;
    private static final int NODE_TAL_MENU7 = 17;
    // タルの品の話
    private static final int NODE_ITEM_MENU = 20;
    private static final int NODE_FEATHER_MENU1 = 21;
    private static final int NODE_FEATHER_MENU2 = 22;
    // スマの儀式
    private static final int NODE_SUMA_MENU1 = 30;
    private static final int NODE_SUMA_MENU2 = 31;
    private static final int NODE_SUMA_MENU3 = 32;
    private static final int NODE_SUMA_MENU4 = 33;
    private static final int NODE_SUMA_MENU5 = 34;
    private static final int NODE_SUMA_SELECT = 35;
    private static final int NODE_SUMA_ASK = 36;
    private static final int NODE_SUMA_RITUAL = 37;
    // マグナの儀式
    private static final int NODE_MAGNA_MENU = 40;
    private static final int NODE_MAGNA_SELECT = 41;
    private static final int NODE_MAGNA_ASK = 42;
    private static final int NODE_MAGNA_RITUAL = 43;
    private static final int MENU_TAL = 0;
    private static final int MENU_ITEM = 1;
    private static final int MENU_SUMA = 2;
    private static final int MENU_MAGNA = 3;
    private final int quest_spirit = 8215; // タルの精霊クエスト (クエストの状態)
    private final int item_feather = 4031936; // タルの精霊の羽
    private final int item_lily = 4031937; // ジャングルのユリ
    private final int item_crystal = 4005001; // クリスタル(知恵)
    private final int item_totem = 4031755; // タル・トーテム
    private final int item_cape = 1102165; // タルの精霊マント
    private final int exp_item = 500;
    private final int exp_suma = 50000;
    private final int exp_magna = 150000;
    // タルの武器 (基本, スマ, マグナ), lua の Item01 ~ Item18 の順
    private final int[][] weapons = {
        {1452054, 1452055, 1452056}, // アッカ
        {1462047, 1462048, 1462049}, // ザール
        {1332067, 1332068, 1332069}, // マク（幸運)
        {1332070, 1332071, 1332072}, // マク（力)
        {1472065, 1472066, 1472067}, // クマ
        {1382054, 1382055, 1382056} // ウマル
    };
    private int node = NODE_START;
    private int last_status = 0;
    private String text = "";
    private String[] says = null;
    private int says_index = 0;
    private int says_after = NODE_SAY_END;
    private int weapon = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status < status;
        this.last_status = status;
        int pre_val = getPlayer().getQuestStatus(this.quest_spirit);
        // lua の Item01 ~ Item23 の所持数 (番号 = lua の Item の番号 = 選択肢の番号)
        int[] item_ids = new int[24];
        for (int i = 0; i < this.weapons.length; i++) {
            for (int j = 0; j < 3; j++) {
                item_ids[1 + i * 3 + j] = this.weapons[i][j];
            }
        }
        item_ids[19] = this.item_cape;
        item_ids[20] = this.item_feather;
        item_ids[21] = this.item_lily;
        item_ids[22] = this.item_crystal;
        item_ids[23] = this.item_totem;
        int[] counts = new int[24];
        for (int i = 1; i < item_ids.length; i++) {
            counts[i] = getPlayer().getItemQuantity(item_ids[i], false);
        }
        boolean has_base = false;
        boolean has_suma = false;
        for (int i = 0; i < this.weapons.length; i++) {
            has_base |= 0 < counts[1 + i * 3];
            has_suma |= 0 < counts[2 + i * 3];
        }
        boolean has_tal = has_base || has_suma || 0 < counts[19] || 0 < counts[20];
        // 前の会話の応答の処理
        switch (this.node) {
            case NODE_START -> {
                this.node = NODE_TOP_MENU;
            }
            case NODE_TOP_MENU -> {
                switch (answer) {
                    case MENU_TAL -> {
                        this.node = NODE_TAL_SAY;
                    }
                    case MENU_ITEM -> {
                        if (!has_tal && counts[23] <= 0) {
                            // 翻訳
                            this.text = " それでは、友よ。安全な旅を。あなたの次のタルの発見を心待ちにしているわ！";
                            this.node = NODE_SAY_END;
                            break;
                        }
                        this.node = NODE_ITEM_MENU;
                    }
                    case MENU_SUMA -> {
                        if (pre_val != 1 || !has_base) {
                            return false;
                        }
                        if (counts[20] <= 0 || counts[21] <= 0 || counts[22] <= 0) {
                            // 翻訳
                            this.text = " スマの儀式を行うには、#bジャングルリリー10個#k、#b知恵の水晶5個#k、#b精霊の羽根1枚#kが必要よ。まだ全部集まっていないわね。集まったら戻ってきて…待っているわ。";
                            this.node = NODE_SAY_END;
                            break;
                        }
                        this.node = NODE_SUMA_MENU1;
                    }
                    case MENU_MAGNA -> {
                        // TODO : lua ではクエストの状態が 2 の場合のみ実行されるが, 選択肢はクエストの状態が 1 の場合のみ表示される
                        if (pre_val != 2) {
                            return false;
                        }
                        if (!has_suma) {
                            // 翻訳
                            this.text = "マグナの儀式を行うには、#bジャングルリリー50個#k、#b知恵の水晶15個#k、#b精霊の羽根2枚#kが必要よ。まだ全部集まっていないわね。集まったら戻ってきて…待っているわ。";
                            this.node = NODE_SAY_END;
                            break;
                        }
                        this.node = NODE_MAGNA_MENU;
                    }
                    default -> {
                        return false;
                    }
                }
            }
            case NODE_TAL_SAY -> {
                this.node = NODE_TAL_MENU1;
            }
            case NODE_TAL_MENU1 -> {
                if (answer == 1) {
                    // 翻訳
                    this.text = " <首を横に振る> タルの古いことわざにこうあるの。「自分の道に印をつけない者は、同じところをぐるぐる回る運命にある」。結局、自分がどこから出発したのか知らなければ、どこへ向かっているのかもわからないでしょう？それではさようなら。ジャングルの精霊があなたの道を導きますように。";
                    this.node = NODE_SAY_END;
                    break;
                }
                if (answer != 0) {
                    return false;
                }
                this.node = NODE_TAL_MENU2;
            }
            case NODE_TAL_MENU2 -> {
                if (answer != 0) {
                    return false;
                }
                // 翻訳
                this.says = new String[]{
                    " もちろんよ。何百年も昔の古代マステリアに、タルという名の誇り高い神秘的なジャングルの戦士の部族がいたの。彼らは今クラキアジャングルと呼ばれている場所に住んでいたわ。勇猛な戦士にもなれたけれど、タル族は平和を愛する人々で、ジャングルやそこに住む生き物たちと調和して暮らしていたの。",
                    " 彼らは、すべての人間の魂には対となる動物がいると信じていたわ。だから、タルの戦士はみんな生まれたときに動物の相棒と絆を結んだの。二人は生涯離れることなく、共に生き、共に戦うことを学んでいった。その絆はとても強く、戦士とペットとの関係の強さが、実際に戦士の武器の力となって現れたほどなのよ。"
                };
                this.says_index = 0;
                this.says_after = NODE_TAL_MENU3;
                this.node = NODE_SAYS;
            }
            case NODE_TAL_MENU3 -> {
                switch (answer) {
                    case 0 -> {
                        // 翻訳
                        this.says = new String[]{
                            " 私…残念だけど、いないと言わざるを得ないわ。千年前にマステリア大陸が消える前から、タル族はジャングルから姿を消していたの。私が知っているのは、マステリアの歴史によるとクラキアンたちが彼らをジャングルから追い出し…もしかすると滅ぼしてしまったということだけ。でも、それは筋が通らないの。クラキアンは平和で優しい生き物で、タル族は何百年も彼らと平和に共存していたんだもの！",
                            " 千年もの時が過ぎた今、ここで彼らが消えた手がかりを見つけるのは、暗闇の中でささやき声の出どころを突き止めるようなものだとわかっているわ。でも、心の中では、祖先の部族の何かがまだジャングルに残っていると信じているの！きっとそうよ！信じてもらえないかもしれないけど、私の血の中で、ジャングルの精霊が私を呼ぶ声が聞こえるの…マステリアが戻ってきたあの夜、海の向こうのヘネシスで彼女の声を聞いたのよ。だから私は祖先の故郷であるここに来たの…私の民に何が起きたのか、彼らが消えた真相を知るために！これが私の精霊クエストなの。"
                        };
                        this.says_after = NODE_TAL_MENU4;
                    }
                    case 1 -> {
                        // 翻訳
                        this.says = new String[]{
                            "これ？ええ。この弓はアカマグナというタルの武器よ。生まれてからずっと使ってきたの。小さな女の子のころからずっと一緒…ここにいるカフレと同じようにね。<カフレがゴロゴロと喉を鳴らす> そうね、私のもふもふのオレンジ色のお友達、いい旅だったわね…ごめんなさい、話の続きだけど…",
                            "動物の相棒と同じように、タルの勇者は幼いころに自分の武器と引き合わされたの。タルの赤ん坊がはいはいできるようになると、弓、クロスボウ、鉤爪など、いろいろな武器の前に置かれたのよ。赤ん坊が最初にはっていった武器が、その子が訓練を受ける武器になり、生涯の武器になったの。タルの勇者は戦士として成長する間ずっと自分の武器を持ち続け…戦士と武器とペットは一緒に成長していったのよ。"
                        };
                        this.says_after = NODE_TAL_MENU5;
                    }
                    case 2 -> {
                        this.node = NODE_TAL_MENU7;
                        break;
                    }
                    default -> {
                        return false;
                    }
                }
                if (this.node != NODE_TAL_MENU7) {
                    this.says_index = 0;
                    this.node = NODE_SAYS;
                }
            }
            case NODE_TAL_MENU4 -> {
                if (answer != 0) {
                    return false;
                }
                // 翻訳
                this.text = " ありがとう、友よ。もしかすると、ジャングルの精霊が何かの理由で私たちを引き合わせたのかもしれないわね。またすぐにお話しできるのを楽しみにしているわ。オハナ・タルテハ…「ジャングルの精霊があなたの道を導きますように」という意味よ。";
                this.node = NODE_SAY_END;
            }
            case NODE_TAL_MENU5 -> {
                if (answer != 0) {
                    return false;
                }
                // 翻訳
                this.says = new String[]{
                    " タルの武器はジャングルから作られた、とても特別なものなの。だからこそ、武器の中にはジャングルの精霊が宿っているのよ。さっき言ったように、タルの戦士とペットは離れられない存在で、人生の旅を通して二人の絆はさらに深まっていった。タルの戦士の武器に宿る精霊はその絆を感じ取ることができて、時が来ると、タルの勇者と動物の相棒は精霊クエストと呼ばれる特別なクエストに挑むことができたの。二人がクエストから戻ると神秘的な儀式が行われ、武器の中の精霊が戦士とペットの間の深まった絆を認めるの。そうすることで、中の精霊は強化された「スマ」の姿、さらに強力な「マグナ」の姿になり、戦士の武器は以前よりもっと強くなったのよ。"
                };
                this.says_index = 0;
                this.says_after = NODE_TAL_MENU6;
                this.node = NODE_SAYS;
            }
            case NODE_TAL_MENU6 -> {
                if (answer != 0) {
                    return false;
                }
                // 翻訳
                this.says = new String[]{
                    " ええ、そうよ。でも、すべての勇者がこの精霊クエストに挑むことを許されたわけではないの。ふさわしいと認められ、#b精霊の羽根#kを与えられた者だけが挑むことができたのよ。",
                    " 精霊クエストには、#bスマ#k、そして#bマグナ#kという2つの段階があるの。スマの段階を始めるには#bその羽根が1枚#k、次のマグナの段階には#b2枚#k必要だったわ。カフレと私はマグナの段階まで精霊クエストを果たしたから、私のアカマグナは、幼いころに使っていた素朴なアカよりも何倍も強力なのよ。旅の途中でタルの武器や品物を見つけたら、私のところに持ってきて。もっと詳しく教えてあげるわ…"
                };
                this.says_index = 0;
                this.says_after = NODE_SAY_END;
                this.node = NODE_SAYS;
            }
            case NODE_TAL_MENU7 -> {
                if (answer != 0) {
                    return false;
                }
                // 翻訳
                this.says = new String[]{
                    " ジャングルの精霊は、死は生の一部であり、旅立つ人が長く満ち足りた旅の終わりを迎えたのなら悲しむべきことではないと教えているわ。でも、気持ちはありがたく受け取るわね、友よ。",
                    " ナナは死の床につくまで私の出自を教えてくれなかったけど、それでも私を昔ながらのタルのやり方で育ててくれたの。赤ん坊のころにカフィと引き合わせ、アカを与え、寝る前のお話としてタルの伝説を聞かせて、自然の摂理を愛し尊ぶことを教えてくれたわ。あの世へ旅立つ前に、ナナは古いお守りをくれたの。テハルという伝説のタル族の族長のものだったそうよ。それは私が生まれ持った権利で、いつかヘネシスを離れてそれに従わなければならないと言っていたわ。",
                    " 最初は意味がわからなくて、信じられなかった…でもある夜、血の中でそれを感じたの。そして次の朝、失われたマステリア大陸が戻ってきたという知らせを聞いたのよ。タル族のことや、この人生での自分の使命をすべてわかっているとは言わない。でも、私の居場所はここ、祖先の土地だとわかっているの。"
                };
                this.says_index = 0;
                this.says_after = NODE_SAY_END;
                this.node = NODE_SAYS;
            }
            case NODE_ITEM_MENU -> {
                if (answer < 1 || item_ids.length <= answer || counts[answer] <= 0) {
                    return false;
                }
                String[] texts = null;
                switch (answer) {
                    case 1 -> {
                        // 翻訳
                        texts = new String[]{
                            " アカは、私が使っているものと同じタルの弓の、最も基本的な形よ。クラキアジャングルの奥深くにある、ジャングルの精霊にとって神聖なゴボの木から作られているの。ゴボの木の材はとても硬くてしなやかで、研いだ石でさえ切ることができないのよ。タルの弓職人がゴボの木からアカを作り始める前には、まずジャングルの精霊への祈りを通して、木に協力してくれるよう「説得」しなければならなかったの。そうして初めて、木は形を変えることを許してくれたのよ。",
                            " ジャングルの精霊に気に入られて#b精霊の羽根#kを手に入れたら、あなたのアカがより強力なアカスマの姿になれるよう、精霊クエストに送り出してあげられるわ。"
                        };
                    }
                    case 2 -> {
                        // 翻訳
                        texts = new String[]{" あなたのアカスマを、さらに強力なアカマグナにアップグレードしたいのね！#b精霊の羽根#kをあと2枚手に入れられたら、精霊クエストの次の段階について教えてあげるわ。"};
                    }
                    case 4 -> {
                        // 翻訳
                        texts = new String[]{
                            " 私の民を原始的だったと言う歴史家もいるけれど、ザルのようなタルの品が、その歴史家たちが間違っていることを証明しているわ。このクロスボウの独創的な設計を見て。強力なのに、小さくて軽いでしょう！タル族は簡単な材料でも、この武器のようにとても効率的な道具を作ることができたの。性急な結論を出す前に、もっと多くの人がちゃんと調べてくれたらいいのに。",
                            " ジャングルの精霊に気に入られて#b精霊の羽根#kを手に入れたら、あなたのザルがより強力なザルスマの姿になれるよう、精霊クエストに送り出してあげられるわ。"
                        };
                    }
                    case 5 -> {
                        // 翻訳
                        texts = new String[]{" あなたのザルスマを、さらに強力なザルマグナにアップグレードしたいのね！#b精霊の羽根#kをあと2枚手に入れられたら、精霊クエストの次の段階について教えてあげるわ。"};
                    }
                    case 7, 10 -> {
                        // 翻訳
                        texts = new String[]{" マクはタル族の伝統的な狩猟用の短剣よ。武器として使われることはめったになかったけれど、武器として役に立たないわけじゃない、むしろその逆よ。実際、最も尊敬されたタルの族長の一人、テハルは、タル族が伝統的に好んだほかの遠距離武器よりも、マクを自分の武器として好んで使ったの。ジャングルの精霊に気に入られて#b精霊の羽根#kを手に入れたら、あなたのマクがより強力なマクスマの姿になれるよう、精霊クエストに送り出してあげられるわ。"};
                    }
                    case 8, 11 -> {
                        // 翻訳
                        texts = new String[]{" あなたのマクスマを、さらに強力なマクマグナにアップグレードしたいのね！#b精霊の羽根#kをあと2枚手に入れられたら、精霊クエストの次の段階について教えてあげるわ。"};
                    }
                    case 13 -> {
                        // 翻訳
                        texts = new String[]{"クマは手首に取り付ける投てき具で、今の盗賊が使っている鉤爪に似ているわ。この武器のおかげで、タルの勇者は両手を空けたまま、素早い遠距離攻撃ができたの。もともとはマヘキという鋭く削った黒曜石のかけらを投げるために作られたものだけど、今どこでも手に入る手裏剣を投げられるように改造することもきっとできるわ。ジャングルの精霊に気に入られて#b精霊の羽根#kを手に入れたら、あなたのクマがより強力なクマスマの姿になれるよう、精霊クエストに送り出してあげられるわ。"};
                    }
                    case 14 -> {
                        // 翻訳
                        texts = new String[]{" あなたのクマスマを、さらに強力なクママグナにアップグレードしたいのね！#b精霊の羽根#kをあと2枚手に入れられたら、精霊クエストの次の段階について教えてあげるわ。"};
                    }
                    case 16 -> {
                        // 翻訳
                        texts = new String[]{" タル族の子どもの中には、ジャングルの精霊に選ばれ、その弟子としてほかの人々にジャングルの道を教える者がいたの。その子たちはウマルを与えられ、部族のシャーマンになるための訓練を受けたのよ。この杖はジャングルと調和していて、タルのシャーマンが周囲の自然のエネルギーを引き出せるようにしてくれたの。ジャングルの精霊に気に入られて#b精霊の羽根#kを手に入れたら、あなたのウマルがより強力なウマルスマの姿になれるよう、精霊クエストに送り出してあげられるわ。"};
                    }
                    case 17 -> {
                        // 翻訳
                        texts = new String[]{" あなたのウマルスマを、さらに強力なウマルマグナにアップグレードしたいのね！#b精霊の羽根#kをあと2枚手に入れられたら、精霊クエストの次の段階について教えてあげるわ。"};
                    }
                    case 19 -> {
                        // 翻訳
                        texts = new String[]{" これはタル族にとってとても神聖な品よ。ジャングルそのものから作られているの。このマントはタルの族長から最も勇敢なタルの戦士にだけ授けられたもので、とても珍しいのよ。タルの精霊のマントは、身に着けた者にジャングルの保護色だけでなく、その原始の力も与えてくれるの。"};
                    }
                    case 20 -> {
                        this.node = NODE_FEATHER_MENU1;
                    }
                    case 23 -> {
                        // 翻訳
                        this.text = " 姿を消す前、タル族は恐ろしい戦争に巻き込まれ、かつては平和な隣人だったクラキアンと呼ばれる植物の生き物から、自分たちの家を守らなければならなくなったの。多くの勇者とその動物の相棒たちが、クラキアンの恐ろしい武器に倒れたわ。その犠牲を忘れないように、倒れたタルの戦士たちの姿は、部族のほかの者たちによってトーテムに彫られたの。そして生き残った者たちはそのトーテムを戦いに持っていき、ほかの者たちを勇気づけたのよ。";
                        this.node = NODE_SAY_END;
                    }
                    default -> {
                        return false;
                    }
                }
                if (texts == null) {
                    break;
                }
                MapleQuest.getInstance(this.quest_spirit).forceStart(getPlayer(), getNpcId(), "");
                getPlayer().gainExp(this.exp_item, true, true, true);
                this.says = texts;
                this.says_index = 0;
                this.says_after = NODE_SAY_END;
                this.node = NODE_SAYS;
            }
            case NODE_FEATHER_MENU1 -> {
                if (answer != 1) {
                    return false;
                }
                this.node = NODE_FEATHER_MENU2;
            }
            case NODE_FEATHER_MENU2 -> {
                if (answer != 2) {
                    return false;
                }
                MapleQuest.getInstance(this.quest_spirit).forceStart(getPlayer(), getNpcId(), "");
                getPlayer().gainExp(this.exp_item, true, true, true);
                // 翻訳
                this.text = " あなたがどうにかしてそれを見つけたということは、過去の、あるいはこれから成し遂げる勇敢な功績のために、あなたが選ばれているとしか思えないわ。この珍しくて貴重なしるしを大切に持っていて。いつかあなたも精霊クエストに挑む日が来るかもしれない。時が来て、ちゃんとしたタルの武器を手にしたら、私に聞いて。道を示してあげるわ。それまで、オハナ・タルテハ、友よ。";
                this.node = NODE_SAY_END;
            }
            case NODE_SUMA_MENU1 -> {
                if (answer != 0) {
                    return false;
                }
                this.node = NODE_SUMA_MENU2;
            }
            case NODE_SUMA_MENU2 -> {
                if (answer == 1) {
                    this.node = NODE_SUMA_SELECT;
                    break;
                }
                if (answer != 0) {
                    return false;
                }
                this.node = NODE_SUMA_MENU3;
            }
            case NODE_SUMA_MENU3 -> {
                if (answer != 0) {
                    return false;
                }
                this.node = NODE_SUMA_MENU4;
            }
            case NODE_SUMA_MENU4 -> {
                if (answer != 0) {
                    return false;
                }
                this.node = NODE_SUMA_MENU5;
            }
            case NODE_SUMA_MENU5 -> {
                if (answer != 0) {
                    return false;
                }
                // 翻訳
                this.text = " 精霊クエストのスマの段階を完了したら、いずれ武器のためにマグナの段階のクエストに挑んで、武器をさらに強力にすることができるわ。でも、まずは目の前のことからね！";
                this.node = NODE_SAY_END;
            }
            case NODE_SUMA_SELECT, NODE_MAGNA_SELECT -> {
                int level = (this.node == NODE_SUMA_SELECT) ? 0 : 1;
                int index = (answer - 1 - level) / 3;
                if (answer < 1 || (answer - 1) % 3 != level || this.weapons.length <= index || counts[answer] <= 0) {
                    return false;
                }
                this.weapon = index;
                this.node = (this.node == NODE_SUMA_SELECT) ? NODE_SUMA_ASK : NODE_MAGNA_ASK;
            }
            case NODE_SUMA_ASK -> {
                if (answer == 1) {
                    // 翻訳
                    this.text = " <うなずく> 準備ができていないと認めるのは、戦士の知恵のしるしよ。準備ができたら戻ってきて…カフレと私はここにいるわ。";
                    this.node = NODE_SAY_END;
                    break;
                }
                if (answer != 0) {
                    return false;
                }
                this.node = NODE_SUMA_RITUAL;
            }
            case NODE_SUMA_RITUAL -> {
                int[] w = this.weapons[this.weapon];
                if (!exchange(0, w[0], -1, this.item_feather, -1, this.item_crystal, -5, this.item_lily, -10, w[1], 1)) {
                    // 翻訳
                    this.text = "必要なアイテムがすべてそろっているか、ETCインベントリがいっぱいでないか確認してくれ。";
                    this.node = NODE_SAY_END;
                    break;
                }
                getPlayer().gainExp(this.exp_suma, true, true, true);
                // 翻訳
                this.text = "若き勇者よ、あなたとペットとの深まる絆を映して、あなたの武器は強くなったわ。これからも賢く使えば、ジャングルの精霊が報いてくれるでしょう";
                this.node = NODE_SAY_END;
            }
            case NODE_MAGNA_MENU -> {
                if (answer == 0) {
                    // 翻訳
                    this.says = new String[]{
                        " 若き勇者よ、ペットの相棒と一緒に旅を続けなければならないわ。#b[マグナの段階を完了するには、ペットがレベル30に達する必要があります。]#k 儀式の材料を集める2つ目の課題は、さらに難しくなったわ。あなたの武器にマグナの儀式を行うには、#bジャングルリリー50個#kと#b知恵の水晶15個#kが必要よ。",
                        " これらの課題をやり遂げたら、ペットの相棒と一緒に、リリーと水晶、そしてあなたの#b精霊の羽根2枚#kを持って戻ってきて。それらを使ってマグナ精霊の儀式を行うわ。そうすれば、あなたとペットの相棒との深まる絆を映して、あなたの武器はさらに強くなるのよ。#b[レベル40のタルの武器がレベル80の武器になります。]#k"
                    };
                    this.says_index = 0;
                    this.says_after = NODE_SAY_END;
                    this.node = NODE_SAYS;
                    break;
                }
                if (answer != 1) {
                    return false;
                }
                this.node = NODE_MAGNA_SELECT;
            }
            case NODE_MAGNA_ASK -> {
                if (answer == 1) {
                    // 翻訳
                    this.text = " <うなずく> 準備ができていないと認めるのは、戦士の知恵のしるしよ。準備ができたら戻ってきて…カフレと私はここにいるわ。";
                    this.node = NODE_SAY_END;
                    break;
                }
                if (answer != 0) {
                    return false;
                }
                this.node = NODE_MAGNA_RITUAL;
            }
            case NODE_MAGNA_RITUAL -> {
                int[] w = this.weapons[this.weapon];
                if (!exchange(0, w[1], -1, this.item_feather, -2, this.item_crystal, -15, this.item_lily, -50, w[2], 1)) {
                    // 翻訳
                    this.text = "必要なアイテムがすべてそろっているか、ETCインベントリがいっぱいでないか確認してくれ。";
                    this.node = NODE_SAY_END;
                    break;
                }
                MapleQuest.getInstance(this.quest_spirit).forceStart(getPlayer(), getNpcId(), "");
                getPlayer().gainExp(this.exp_magna, true, true, true);
                // 翻訳
                this.text = "儀式は完了したわ、若き勇者よ。あなたの武器はさらに強くなった！あなたはジャングルの精霊に祝福されたの。精霊は、あなたがペットの相棒と築いた友情をとても温かく見守っているわ。この先にも精霊クエストの段階があるのかって？伝説ではそれをほのめかしているけど、確かなことはわからないの。私自身はマグナの段階までしか到達していないし、精霊クエストについての知識もそこまでなの。もしかすると、あなたか私がその先の道を見つけるかもしれないわね！それまで、ジャングルの精霊があなたに力を与え、あなたとペットの相棒を見守ってくれますように。";
                this.node = NODE_SAY_END;
            }
            case NODE_SAYS -> {
                this.says_index++;
                if (this.says.length <= this.says_index) {
                    if (this.says_after == NODE_SAY_END) {
                        return false;
                    }
                    this.node = this.says_after;
                }
            }
            default -> {
                return false;
            }
        }
        // 次の会話の表示
        switch (this.node) {
            case NODE_TOP_MENU -> {
                NpcScriptTag nt = new NpcScriptTag();
                // 翻訳
                nt.add(" サル・オカハリ、友よ！あら…どういう意味かって？ごめんなさい、タルの言葉で「こんにちは」という意味なの。", false);
                if (pre_val == 0) {
                    if (has_tal) {
                        // 翻訳
                        nt.add("\r\n#b#L1#ねえ、あなたのタルの装備に似たものを見つけたんだけど…#l#k", false);
                    } else {
                        // 翻訳
                        nt.add("\r\n#b#L0#えっと、サル・オーカ…いや、こんにちは！あなたは誰ですか？どうしてタル語を話しているんですか？#l#k", false);
                    }
                }
                if (pre_val == 1) {
                    if (has_base) {
                        // 翻訳
                        nt.add("\r\n#b#L2#タルの精霊クエスト「スマ」#l#k", false);
                    }
                    if (has_suma) {
                        // 翻訳
                        nt.add("\r\n#b#L3#タルの精霊クエスト「マグナ」#l#k", false);
                    }
                }
                askMenu(nt);
                return true;
            }
            case NODE_TAL_SAY -> {
                // 翻訳
                say(" 私はコリン。そして、このかわいいオレンジ色のもじゃもじゃの毛皮は、友達のカフレよ。<カフレがうなる> もう、静かにして、カフィ…あなたはとってもハンサムで、ちっとももじゃもじゃなんかじゃないと思ってるわ。カフレは虎の中でも特にプライドが高くて、ときどき少し神経質になるの。あなたの質問に答えると、私がタル語を話すのは、それが私の祖先の言葉だからよ。私は古代マステリアのタル族の血を引くレンジャーなの。今ではもう話す人は多くないけれど、それが私という人間の一部だから話しているのよ。", false, true);
                return true;
            }
            case NODE_TAL_MENU1, NODE_TAL_MENU2, NODE_TAL_MENU3, NODE_TAL_MENU4, NODE_TAL_MENU5, NODE_TAL_MENU6, NODE_TAL_MENU7 -> {
                // 翻訳
                String[] texts = {
                    "祖先の伝統を忘れずに受け継いでいくことは大切だと思わない？ \r\n#b#L0# はい、過去から自分について多くのことを学んで、未来への道しるべにできますね。#l \r\n#b#L1# タルなんて聞いたことないな。古代の歴史だって？古いものは捨てて、新しいものを取り入れるべきだよ！#l#k",
                    " まあ、まあ！こんなに若いのに、なんて賢いの！この人をどう思う、カフレ？<カフレがあなたを見つめてゴロゴロと喉を鳴らす> 私もそう思うわ。旅の中でたくさんの人に出会うけど、みんながあなたのように成熟した言葉を話すわけじゃない。カフレと私のことを話させてちょうだい。私たちはもともとヘネシスの出身だけど、祖先の故郷であるこのマステリアに戻ってきたの。\r\n#b#L0# ぜひ…あなたの祖先、タル族についてもっと教えてください。#l#k",
                    " \r\n#b#L0#それで、彼らはどうなったんですか？今でもクラキアジャングルにタル族はいるんですか？#l \r\n#b#L1#武器といえば、すごく強そうな弓を持っていますね…#l \r\n#b#L2#すみません、コリンさん。でも、タル族が千年以上前にマステリアから消えたのなら、どうして自分がその子孫だとわかるんですか？#l#k",
                    " \r\n#b#L0#ジャングルを旅していてタル族に関するものを見つけたら、知らせますね。#l#k",
                    " \r\n#b#L0# 「成長する」ってどういう意味ですか？ #l#k",
                    " \r\n#b#L0#わあ…じゃあ、タルの武器はすべていわばアップグレードできるんですね？#l#k",
                    " <カフレが威嚇するようにうなる> カフレ！やめなさい。私たちの友達はもっともな質問をしているのよ。実を言うと、生まれたときは自分もほかのビクトリアアイランドの人たちと同じだと思っていて、祖先が誰なのか知ったのはずっと大きくなってからだったの。私は両親を知らなくて、ナナ、つまり数年前に亡くなった祖母に育てられたのよ。\r\n#b#L0# それはお気の毒に… #l#k"
                };
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(texts[this.node - NODE_TAL_MENU1], false);
                askMenu(nt);
                return true;
            }
            case NODE_ITEM_MENU -> {
                // 翻訳
                String[] texts = {
                    null,
                    "\r\n#b#L1#この#b#t1452054##kについてもっと教えてもらえますか？#l",
                    "\r\n#b#L2#この#b#t1452055##kについてもっと教えてもらえますか？#l",
                    null,
                    "\r\n#b#L4#この#b#t1462047##kについてもっと教えてもらえますか？#l",
                    "\r\n#b#L5#この#b#t1462048##kについてもっと教えてもらえますか？#l",
                    null,
                    "\r\n#b#L7#この#b#t1332067##kについてもっと教えてもらえますか？#l",
                    "\r\n#b#L8#この#b#t1332068##kについてもっと教えてもらえますか？#l",
                    null,
                    "\r\n#b#L10#この#b#t1332070##kについてもっと教えてもらえますか？#l",
                    "\r\n#b#L11#この#b#t1332071##kについてもっと教えてもらえますか？#l",
                    null,
                    "\r\n#b#L13#この#b#t1472065##kについてもっと教えてもらえますか？#l",
                    "\r\n#b#L14#この#b#t1472066##kについてもっと教えてもらえますか？#l",
                    null,
                    "\r\n#b#L16#この#b#t1382054##kについてもっと教えてもらえますか？#l",
                    "\r\n#b#L17#この#b#t1382055##kについてもっと教えてもらえますか？#l",
                    null,
                    "\r\n#b#L19#この#b#t1102165##kについてもっと教えてもらえますか？#l",
                    "\r\n#b#L20#この#b#t4031936##kについてもっと教えてもらえますか？#l",
                    null,
                    null,
                    "\r\n#b#L23#この#b#t4031755##kについてもっと教えてもらえますか？#l"
                };
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("見て、カフレ！私たちの友達が、祖先の部族のものを見つけてくれたわ！<カフレが小さくほえる> このタルの品について話してほしいの？もちろんよ！私の民とその文化について人に話せるなんて、胸がいっぱいになるわ。何について知りたいの？", false);
                for (int i = 1; i < texts.length; i++) {
                    if (texts[i] != null && 0 < counts[i]) {
                        nt.add(texts[i], false);
                    }
                }
                askMenu(nt);
                return true;
            }
            case NODE_FEATHER_MENU1 -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(" 部族のために偉大な功績を挙げた最も強いタルの勇者たちは、タルの族長から#b精霊の羽根#kを授けられたの。勇者たちはその羽根を最高の名誉の証として身に着けたわ。こうして称えられたタルの戦士は、精霊クエストに挑むことを許されたの。武器をより強力にするための儀式よ。この儀式を通して、伝統的なタルの武器は強化されたスマの姿、そして最終的には最強のマグナの姿になることができたの。 \r\n#b#L1#続けてください…#k#l", false);
                askMenu(nt);
                return true;
            }
            case NODE_FEATHER_MENU2 -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(" いい、友よ、#b精霊の羽根#kはただの羽根じゃなかったの…精霊グリフォンの姿をしたジャングルの精霊から授かったものだと言われていたわ。彼女はこの姿で、それもこの地で偉大なことを成し遂げる必要があるときに、タルの族長の前にだけ現れたの。でも、ごくまれに、タルの勇者が羽根を見つけることがあった…たとえば、ジャングルを進む道の上に不思議と落ちていたりね。そんなとき、勇者はそれを、ジャングルの精霊の恩恵を受けるための功績を果たす者として選ばれたしるしだと受け止めたのよ。 \r\n#b#L2#じゃあ、私がそれを見つけたことにはどんな意味があるんですか？#k#l", false);
                askMenu(nt);
                return true;
            }
            case NODE_SUMA_MENU1, NODE_SUMA_MENU2, NODE_SUMA_MENU3, NODE_SUMA_MENU4, NODE_SUMA_MENU5 -> {
                // 翻訳
                String[] texts = {
                    " あら、これは何かしら、カフレ？私たちの友達が#b精霊の羽根#kを見つけたみたいよ！ \r\n#b#L0#これで精霊クエストに挑んで、タルの武器をアップグレードできるということですか？#l#k",
                    "ええ…そうよ。#b精霊の羽根#kを持っているなら、あなたは偉大な功績のために選ばれたということ  \r\n#b#L0#精霊クエストに挑みたいです。何をすればいいですか？#l \r\n#b#L1#精霊クエストを果たしたので、武器のスマの儀式を行う準備ができました。#l#k",
                    " 精霊クエストは、それ自体が特定のクエストというわけではなく、むしろペットの相棒と一緒に挑む旅なの。言うまでもなく、クエストを始めるにはペットがいなければならないわ。このクエストの間に何をするかは、本当にあなたとペット次第…自分自身の道を進まなければならないの。この期間に成し遂げるべき一番大切なことは、経験と会話を共にしてペットの相棒との絆を深めることよ。#b[精霊クエストを完了するには、ペットがレベル15以上になる必要があります。]#k \r\n#b#L0#ほかには何かありますか？#l#k",
                    " 精霊クエストを完了するための2つ目の課題は、武器強化の儀式に使う材料を集めることよ。クラキアジャングルに行って、#bジャングルリリーを10個#k持ってきてちょうだい。この珍しくて美しい花は、谷の特別な条件の場所でしか育たないのだけど、今ジャングルを荒らしている機械のモンスターたちが押し寄せてきたせいで、もろいつるの多くが踏みつけられたり引き抜かれたりして、ますます見つけにくくなっているの。でも、まだジャングルにあるのはわかっているわ…私自身もいくつか見つけたもの。リリーのほかに、#b知恵の水晶を5個#kも持ってきてちょうだい。  \r\n#b#L0#それから？#l#k",
                    " これらの課題をやり遂げたら、ペットの相棒と一緒に、リリーと水晶、そしてあなたの#b精霊の羽根#kを持って戻ってきて。それらを使ってスマ精霊の儀式を行うわ。そうすれば、あなたとペットとの絆の強さがあなたの武器に宿り、精霊クエストのこの段階は完了よ。#b[完了すると、基本のタルの武器がレベル40の武器になります。]#k \r\n#b#L0#武器がアップグレードされたら、その後はどうなるんですか？#l#k"
                };
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(texts[this.node - NODE_SUMA_MENU1], false);
                askMenu(nt);
                return true;
            }
            case NODE_SUMA_SELECT -> {
                // 翻訳
                String[] texts = {
                    "\r\n#L1##b#t1452054##kのスマの儀式を行いますか？#l",
                    "\r\n#L4##b#t1462047##kのスマの儀式を行いますか？#l",
                    "\r\n#L7##b#t1332067##kのスマの儀式を行いますか？#l",
                    "\r\n#L10##b#t1332070##kのスマの儀式を行いますか？#l",
                    "\r\n#L13##b#t1472065##kのスマの儀式を行いますか？#l",
                    "\r\n#L16##b#t1382054##kのスマの儀式を行いますか？#l"
                };
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(" それはいい知らせね。どのタルの武器に儀式を行う？ #b[注意: この行為により、レベル10の武器が未強化のレベル40の武器に変わります。]#k", false);
                for (int i = 0; i < this.weapons.length; i++) {
                    if (0 < counts[1 + i * 3]) {
                        nt.add(texts[i], false);
                    }
                }
                askMenu(nt);
                return true;
            }
            case NODE_SUMA_ASK -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(" わかったわ、若き勇者よ…ペットの相棒と一緒に前に出て、私が頼んだものを持ってきて。警告しておくけど、スマの儀式を行うと、基本のタルの武器にかけた強化はすべて消えてしまうわ。あなたと相棒の準備はいい？ \r\n#b#L0#頼まれたアイテムです…準備はできています。#l \r\n#b#L1#やっぱり…まだ少し準備ができていません。#l#k", false);
                askMenu(nt);
                return true;
            }
            case NODE_SUMA_RITUAL -> {
                // 翻訳
                say(" 精霊の儀式を始めましょう、勇者たちよ…<コリンがジャングルリリーに火をつけると、香り高い煙が立ちのぼる。> カラウ・オナケイ、オハナ・アカ・カハナ！トー・アマナケイ、タラ・ウナネイ！オハナ・スマ・カナ、テハル・タルテハ！<知恵の水晶がどんどん明るく輝き、緑色の閃光とともに消える！>", false, true);
                return true;
            }
            case NODE_MAGNA_MENU -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(" あら、これは何かしら、カフレ？私たちの友達が#b精霊の羽根#kをさらに2枚見つけたみたいよ！ジャングルの精霊は、本当にあなたを偉大なことのために選んでいるに違いないわ！ \r\n#b#L0#精霊クエストの次の段階に挑みたいです。何をすればいいですか？#l \r\n#b#L1#精霊クエストの次の段階を果たしたので、武器にマグナの儀式を行ってほしいです。#l#k", false);
                askMenu(nt);
                return true;
            }
            case NODE_MAGNA_SELECT -> {
                // 翻訳
                String[] texts = {
                    "\r\n#L2# #b#t1452055##kのマグナの儀式を行いますか？#l",
                    "\r\n#L5# #b#t1462048##kのマグナの儀式を行いますか？#l",
                    "\r\n#L8# #b#t1332068##kのマグナの儀式を行いますか？#l",
                    "\r\n#L11# #b#t1332071##kのマグナの儀式を行いますか？#l",
                    "\r\n#L14# #b#t1472066##kのマグナの儀式を行いますか？#l",
                    "\r\n#L17# #b#t1382055##kのマグナの儀式を行いますか？#l"
                };
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(" それはいい知らせね。どのタルの武器にマグナの儀式を行う？ #b[注意: マグナの儀式により、レベル40のスマ武器が未強化のレベル80の武器に変わります。]#k", false);
                for (int i = 0; i < this.weapons.length; i++) {
                    if (0 < counts[2 + i * 3]) {
                        nt.add(texts[i], false);
                    }
                }
                askMenu(nt);
                return true;
            }
            case NODE_MAGNA_ASK -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(" わかったわ、若き勇者よ…ペットの相棒と一緒に前に出て、私が頼んだものを持ってきて。警告しておくけど、マグナの儀式を行うと、スマ段階のタルの武器にかけた強化はすべて消えてしまうわ。あなたと相棒の準備はいい？ \r\n#b#L0#頼まれたアイテムです…準備はできています。#l \r\n#b#L1#やっぱり…まだ少し準備ができていません。#l#k", false);
                askMenu(nt);
                return true;
            }
            case NODE_MAGNA_RITUAL -> {
                // 翻訳
                say(" マグナ精霊の儀式を始めましょう、勇者たちよ…<コリンがジャングルリリーに火をつけると、香り高い煙が立ちのぼる。> カラウ・オナケイ、オハナ・アカ・カハナ！トー・アマナケイ、タラ・ウナネイ！オハナ・マグナ・カナ、テハル・タルテハ！<知恵の水晶がどんどん明るく輝き、緑色の閃光とともに消える！>", false, true);
                return true;
            }
            case NODE_SAYS -> {
                boolean next = this.says_index + 1 < this.says.length || this.says_after != NODE_SAY_END;
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
