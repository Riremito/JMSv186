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
// Npc.wz script : job4_item (JMS131-302), 会話文は lua_script_jp (job4_item) を利用
public class Npc_Leafre_ChiefTatamo extends NpcScriptMan {

    private final int quest_feeling = 7810; // 村長タタモの好感度 (クエストのデータに6桁の数値で保存)
    private final int item_magic_seed = 4031346; // 魔法の種
    private final int feeling_max = 800000;
    private final int MENU_SEED = 0;
    private final int MENU_DONATE = 1;
    private final int PHASE_TOP = 0;
    private final int PHASE_TOP_ANSWER = 1;
    private final int PHASE_SEED_MENU = 2;
    private final int PHASE_SEED_MENU_ANSWER = 3;
    private final int PHASE_SEED_NUMBER = 4;
    private final int PHASE_SEED_CONFIRM = 5;
    private final int PHASE_SEED_EXECUTE = 6;
    private final int PHASE_DONATE_MENU = 7;
    private final int PHASE_DONATE_MENU_ANSWER = 8;
    private final int PHASE_DONATE_CONFIRM = 9;
    private final int PHASE_DONATE_EXECUTE = 10;
    // 好感度の段階
    private final int[] feeling_levels = {0, 5000, 24000, 50000, 200000, 800000};
    // 魔法の種の値段
    private final int[] seed_prices = {30000, 27000, 24000, 18000, 12000, 8000};
    // 翻訳
    private final String[] seed_says_before = {
        "",
        "",
        "",
        "",
        "おやおや。そろそろ来るころだと思っていました…\r\nところで、少し前に、ドラゴンたちが住む竜の神殿で大きな戦いが起きたんです。何か聞いていませんか？",
        ""
    };
    // 翻訳
    private final String[] seed_menus = {
        "この町の人ではないようですね。何かご用ですか？\r\n#b#L0# 魔法の種がほしいです。#l",
        "前に会ったことがありませんか？見覚えがあると思ったら。ははは…\r\n今日は何のご用ですか？\r\n#b#L0# 魔法の種がほしいです。#l",
        "今日もいい天気ですね。こんな日は家族と公園でピクニックをして過ごすべきですよ。正直に言うと、初めて会ったときは、この町の人ではないこともあって少し警戒していました…でも今では、あなたと取引するのがとても心地いいんです。\r\n今日は何のご用ですか？\r\n#b#L0# 魔法の種がほしいです。#l",
        "うーん…今日はバークがいつもよりずっと大きな声で鳴いていますね。バークが鳴くのは、ドラゴンの赤ちゃんの卵がもうすぐ孵るという合図なんです。あなたもここの家族の一員になったのですから、そのときが来たら、ぜひドラゴンの赤ちゃんの誕生を見届けてほしいですね。\r\n今日は何かご用ですか？\r\n#b#L0# 魔法の種がほしいです。#l",
        "空が揺れ、大地が震え、ものすごい轟音が森じゅうに響き渡りました。ドラゴンの赤ちゃんたちは、次に何が起こるのかと恐怖に震えています。一体何が起きたのでしょう…とにかく、種を買いに来たんですよね？\r\n#b#L0# 魔法の種がほしいです。#l",
        "魔法の種を買いに来たんじゃありませんか？初めて会ってからずいぶん時間が経ちましたね。今ではあなたと話すたびに、穏やかでほっとした気持ちになります。この町の人たちはみんなあなたが大好きですし、私も同じ気持ちです。あなたは本当の友達です。\r\n#b#L0# そんなに優しい言葉をありがとう。今すぐ魔法の種がほしいな。#l"
    };
    // 翻訳
    private final String[] seed_says_after = {
        "",
        "ああ～思い出しました。間違っていなければ、前に#b魔法の種#kをお売りしましたね。いかがでしたか？そのお顔を見ると、前回の買い物にはとても満足していただけたようですね。",
        "",
        "",
        "",
        ""
    };
    // 翻訳
    private final String[] seed_numbers = {
        "#b魔法の種#kは貴重なアイテムなので、そう簡単にはあげられません。ちょっとした頼みを聞いてくれませんか？そうしたらあげましょう。#b魔法の種#kは1個#b30,000メル#kでお売りします。\r\n購入しますか？では、いくつ必要ですか？",
        "#b魔法の種#kは貴重なアイテムなので、そう簡単にはあげられません。ちょっとした頼みを聞いてくれませんか？そうしたらあげましょう。  #b魔法の種#kは1個#b30,000メル#kでお売りします。\r\nいくつ必要ですか？",
        "#b魔法の種#kは本当に珍しくて貴重なアイテムですが、もうずいぶん長い付き合いですから、特別に割引してあげましょう。#b魔法の種#k1個#b24,000メル#kでどうですか？船でここまで飛んでくるより安いですよ！いくつ必要ですか？",
        "#b魔法の種#kがなくなったのですね。私たちはとても親しくなりましたから、見返りを求めるのは気が引けるのですが、#b魔法の種#kはとても珍しくて手に入りにくいものだとわかってください。#b魔法の種1個#kにつき#b18,000メル#kでどうですか？いくつほしいですか？",
        "やっぱり。今では目を見るだけでわかります。あなたはいつでも私たちの力になってくれるって。#b魔法の種#kが貴重なアイテムなのはお互いわかっていますが、あなたには#b12,000メル#kでお売りします。いくつ必要ですか？",
        "いつでも用意してあるのは知っているでしょう。種1個につき#b8,000メル#kだけでいいですよ。どのみち、もう長い付き合いですからね。いくつほしいですか？"
    };
    // 翻訳
    private final String[] seed_oks = {
        "またお会いしましょう～",
        "またお会いしましょう～",
        "またお会いしましょう～",
        "またお会いしましょう～",
        "またお会いしましょう～",
        "ところで、森を守ってきた竜の長が姿を消したという話は聞きましたか？私に言わせれば、何かがおかしいです。ここまでホーンテイルの悪魔のような叫び声が聞こえてくるんですから。とても心配です…"
    };
    // 寄付するアイテムと好感度の上昇量
    private final int[][] donate_items = {
        {4000226, 2}, // ラッシュ
        {4000229, 4}, // ダークラッシュ
        {4000236, 3}, // ビートル
        {4000237, 6}, // デュアルビートル
        {4000260, 3}, // ホブ
        {4000261, 6}, // ピンホブ
        {4000231, 7}, // ヘンキー
        {4000238, 9}, // ハープ
        {4000239, 12}, // ブラッドハープ
        {4000241, 15}, // バーク
        {4000242, 20}, // デュアルバーク
        {4000234, 20}, // ケンタの骨
        {4000232, 20}, // ケンタの炎
        {4000233, 20}, // ケンタの精髄
        {4000235, 100}, // マノン
        {4000243, 100} // グリフィーの角
    };
    // 翻訳
    private final String[] donate_says = {
        "町をもっと住みやすくするのが村長の務めで、その務めを果たすにはたくさんのアイテムが必要なんです。リプレ周辺でアイテムを集めたなら、寄付していただけませんか？",
        "以前、素晴らしいアイテムを快く寄付してくださった方ですね。本当に助かりました。ところで、リプレ周辺でアイテムを集めたなら、もう一度寄付していただけませんか？",
        "今日もまた来てくださったのですね。あなたの多大な協力のおかげで、この町の暮らしはずいぶんよくなりました。町の人たちはみんな、あなたの貢献にとても感謝しています。ところで、リプレ周辺でアイテムを集めたなら、もう一度寄付していただけませんか？",
        "こんにちは！あなたの大きな貢献のおかげで、この町はほかに類を見ないほど栄えています。町はとても順調ですが、また手を貸していただけるとありがたいです。リプレ周辺でアイテムを集めたなら、もう一度寄付していただけませんか？",
        "リプレの一番の支援者のあなたですね！あなたが町にいると、いつもいいことが起こるような気がします。ところで、リプレ周辺でアイテムを集めたなら、もう一度寄付していただけませんか？",
        "" // 名前を含むため action 内で作成
    };
    private int phase = PHASE_TOP;
    private int last_status = 0;
    private int level = 0;
    private int number = 0;
    private int donate = 0;
    private String text_no = "";

    @Override
    public boolean action(int status, int answer) {
        if (status < this.last_status) {
            // askYesNo でいいえを選択した
            say(this.text_no);
            return false;
        }
        this.last_status = status;
        if (this.phase == PHASE_TOP) {
            NpcScriptTag nt = new NpcScriptTag();
            // 翻訳
            nt.add("…何か用ですか？\r\n#b#L0# 魔法の種を買う#l\r\n#L1# リプレのために何かする#l\r\n", false);
            askMenu(nt);
            this.phase = PHASE_TOP_ANSWER;
            return true;
        }
        if (this.phase == PHASE_TOP_ANSWER) {
            String data = getQuestData(this.quest_feeling);
            if (data.isEmpty() && answer == MENU_SEED) {
                data = "000000";
                setQuestData(this.quest_feeling, data);
            }
            int feeling = 0;
            try {
                feeling = Integer.parseInt(data);
            } catch (NumberFormatException e) {
                feeling = 0;
            }
            for (int i = 0; i < this.feeling_levels.length; i++) {
                if (this.feeling_levels[i] <= feeling) {
                    this.level = i;
                }
            }
            if (answer == MENU_SEED) {
                this.phase = PHASE_SEED_MENU;
                if (!this.seed_says_before[this.level].isEmpty()) {
                    say(this.seed_says_before[this.level], false, true);
                    return true;
                }
            } else if (answer == MENU_DONATE) {
                this.phase = PHASE_DONATE_MENU;
                if (this.level == this.feeling_levels.length - 1) {
                    // 翻訳
                    say("#b" + getCharacterName() + "#kさんではありませんか？またお会いできてうれしいです！あなたの素晴らしい働きのおかげで、町はとても順調で、最近は私のすることがほとんどないくらいです。この町の誰もがあなたを尊敬しているようですよ、本当に。多大なご協力には心から感謝していますが…もう一度だけ手を貸していただけませんか？リプレ周辺でアイテムを集めたなら、また私たちに寄付していただけませんか？", false, true);
                    return true;
                }
                say(this.donate_says[this.level], false, true);
                return true;
            } else {
                return false;
            }
        }
        // 魔法の種の購入
        if (this.phase == PHASE_SEED_MENU) {
            NpcScriptTag nt = new NpcScriptTag();
            nt.add(this.seed_menus[this.level], false);
            askMenu(nt);
            this.phase = PHASE_SEED_MENU_ANSWER;
            return true;
        }
        if (this.phase == PHASE_SEED_MENU_ANSWER) {
            if (answer != 0) {
                return false;
            }
            this.phase = PHASE_SEED_NUMBER;
            if (!this.seed_says_after[this.level].isEmpty()) {
                say(this.seed_says_after[this.level], false, true);
                return true;
            }
        }
        if (this.phase == PHASE_SEED_NUMBER) {
            askNumber(this.seed_numbers[this.level], 0, 0, 100);
            this.phase = PHASE_SEED_CONFIRM;
            return true;
        }
        if (this.phase == PHASE_SEED_CONFIRM) {
            this.number = answer;
            if (this.number == 0) {
                // 翻訳
                say("0個は売れません。");
                return false;
            }
            int price = this.seed_prices[this.level] * this.number;
            // 翻訳
            this.text_no = "よく考えてください。決めたら教えてくださいね。";
            // 翻訳
            askYesNo("#b" + this.number + "個の魔法の種#kの購入には#b" + price + "メル#kかかります。本当に購入しますか？");
            this.phase = PHASE_SEED_EXECUTE;
            return true;
        }
        if (this.phase == PHASE_SEED_EXECUTE) {
            int price = this.seed_prices[this.level] * this.number;
            if (!exchange(-price, this.item_magic_seed, this.number)) {
                // 翻訳
                say("購入に必要なメルがあるか確認してください。それと、ETCインベントリに十分な空きがあるかも確認することをおすすめします。");
                return false;
            }
            say(this.seed_oks[this.level]);
            return false;
        }
        // アイテムの寄付
        if (this.phase == PHASE_DONATE_MENU) {
            NpcScriptTag nt = new NpcScriptTag();
            // 翻訳
            nt.add("どのアイテムを寄付しますか？\r\n#b#L0# #t4000226##l\r\n#L1# #t4000229##l\r\n#L2# #t4000236##l\r\n#L3# #t4000237##l\r\n#L4# #t4000260##l\r\n#L5# #t4000261##l\r\n#L6# #t4000231##l\r\n#L7# #t4000238##l\r\n#L8# #t4000239##l\r\n#L9# #t4000241##l\r\n#L10# #t4000242##l\r\n#L11# #t4000234##l\r\n#L12# #t4000232##l\r\n#L13# #t4000233##l\r\n#L14# #t4000235#\r\n#L15# #t4000243##l\r\n", false);
            askMenu(nt);
            this.phase = PHASE_DONATE_MENU_ANSWER;
            return true;
        }
        if (this.phase == PHASE_DONATE_MENU_ANSWER) {
            if (answer < 0 || this.donate_items.length <= answer) {
                return false;
            }
            this.donate = answer;
            int item_id = this.donate_items[this.donate][0];
            int max_give = getItemCount(item_id);
            if (max_give <= 0) {
                // 翻訳
                say("アイテムを持っていないようですね。");
                return false;
            }
            // 翻訳
            askNumber("#b#t" + item_id + "##kをいくつ寄付しますか？\r\n#b< 所持数 :" + max_give + " >#k", 0, 0, max_give);
            this.phase = PHASE_DONATE_CONFIRM;
            return true;
        }
        if (this.phase == PHASE_DONATE_CONFIRM) {
            this.number = answer;
            int item_id = this.donate_items[this.donate][0];
            // 翻訳
            this.text_no = "よく考えてから、決めたら教えてください。";
            // 翻訳
            askYesNo("本当に#b" + this.number + " #t" + item_id + "##k?");
            this.phase = PHASE_DONATE_EXECUTE;
            return true;
        }
        if (this.phase == PHASE_DONATE_EXECUTE) {
            int item_id = this.donate_items[this.donate][0];
            int like = this.donate_items[this.donate][1];
            if (!exchange(0, item_id, -this.number)) {
                // 翻訳
                say("アイテムが十分にあるか確認してください。");
                return false;
            }
            String data = getQuestData(this.quest_feeling);
            int feeling = 0;
            try {
                feeling = Integer.parseInt(data);
            } catch (NumberFormatException e) {
                feeling = 0;
            }
            feeling = Math.min(feeling + like * this.number, this.feeling_max);
            setQuestData(this.quest_feeling, String.format("%06d", feeling));
            // 翻訳
            say("本当にありがとうございます。");
            return false;
        }
        return false;
    }
}
