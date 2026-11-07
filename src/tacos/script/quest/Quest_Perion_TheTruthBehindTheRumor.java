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
package tacos.script.quest;

/**
 *
 * @author Riremito
 */
// Quest.wz startscript : q2148s - q2152s (JMS186), 噂の真相-豚と一緒に踊りを, 噂の真相-マンジ, 噂の真相-イヤン, 噂の真相-コブシを開いて立て, 噂の真相-Dr.ベティ, lua の処理を利用
public class Quest_Perion_TheTruthBehindTheRumor extends QuestScriptMan {

    private final int quest_first = 2148; // 噂の真相-豚と一緒に踊りを
    // [クエスト] の選択肢つきの会話
    private final String[][] menus = {
        {
            // 翻訳
            "何の用だ？\r\n#b#L0#幽霊の木について聞いたことはありますか？#l",
            // 翻訳
            "幽霊の木？ああ、昔に消えたあの巨大なスタンプのことか？祖父が子どものころ、そんな木があったという話を聞いたことがある。伝わる噂では、枝ごとに赤い布が下がっていて、霊の血で染まったものだそうだ。だが、私も実際には一度も見たことがない。だから本当かどうかはわからんな。\r\n#b#L0#ほかの噂は聞いていませんか？#l"
        },
        {
            // 翻訳
            "…何の用だ？\r\n#b#L0#幽霊の木について聞いたことはありますか？#l",
            // 翻訳
            "臆病者たちの話を聞いたようだな。幽霊の木だなんて、そんなものがあるはずがない。長い間ペリオンの岩山を歩き回って修行してきたが、そんな木は見たことも聞いたこともない。\r\n#b#L0#あ、そうなんですか？…#l"
        },
        {
            // 翻訳
            "こんにちは、旅人さん。今日はどんなご用ですか？\r\n#b#L0#幽霊の木について知ってる？#l",
            // 翻訳
            "まあ！その噂を聞いたんですか？少し前に、ヘネシスのカミラがお母さんのお使いでペリオンに来て、帰る途中で幽霊を見たんですって。\r\n#b#L0#本当？#l"
        },
        {
            // 翻訳
            "何の用でわしを訪ねてきたのだ？\r\n#b#L0#幽霊の木について何か知っていますか？#l",
            // 翻訳
            "幽霊の木か…おそらくスタンピーのことだろう。\r\n#b#L0#スタンピーって何ですか？#l",
            // 翻訳
            "ペリオンがまだ緑の森だったころから今まで生き残ってきた、とても古い木だ。だが、長い年月を経るうちに、木は怒り始めた。森を破壊する人間を見て怒り、枯れていく森を見て怒ったのだ\r\n#b#L0#それでどうなったんですか？#l"
        },
        {
            // 翻訳
            "いらっしゃい。ご用は何ですか？\r\n#b#L0#幽霊の木について何か知っていますか？#l",
            // 翻訳
            "ウィンストン博士の研究を手伝っているのですね？そうですね。私も博士に頼まれて少し調べてみたのですが、何もわかりませんでした。ただ、最近ペリオンとの境にあるエリニアの森が急速に枯れ始めていることはわかりました。進行は遅いですが、警戒すべきことです。\r\n#b#L0#はい。お時間をいただいてありがとうございました。#l"
        }
    };
    // [クエスト] の選択肢のあとの会話
    private final String[][] says = {
        {
            // 翻訳
            "残念ながら、私は噂に詳しい人間ではないのだ。"
        },
        {
            // 翻訳
            "ただ、最近東の岩山で謎の襲撃を受けることが増えたというのが、少し気になるな…"
        },
        {
            // 翻訳
            "夜遅くヘネシスに帰る途中だったんですけど、暗闇の中で木の幹を踏んだような気がして周りを見回したら、ぎょろぎょろした目が今にもカミラを食べてしまいそうな勢いでにらんでいたそうですよ。",
            // 翻訳
            "カミラはあまりの怖さに、そのまま気を失ってしまったんですって。夜が明けてから大人たちがその場所にもう一度行ってみたけど、何もなかったそうです。幽霊に違いないと思います。どうしよう？もう怖くて村の外に出られそうにありません。"
        },
        {
            // 翻訳
            "結局、木の怒りは木をモンスターに変えてしまい、今では手当たり次第に大地の養分を食い荒らす、ただの怪物になってしまった。あまり深く知ろうとするな。お前の好奇心はわかるが、奴はすべてのスタンプの王だ。決して甘く見てはならんぞ。"
        },
        {
            // 翻訳
            "あまりお役に立てなくてごめんなさいね。"
        }
    };
    private final int[] reward_exp = {100, 100, 100, 100, 200};

    @Override
    public boolean questStart(int status, int answer) {
        int index = getQuestId() - this.quest_first;
        if (index < 0 || this.menus.length <= index || status < 0) {
            return false;
        }
        String[] menu = this.menus[index];
        String[] texts = this.says[index];
        if (status < menu.length) {
            askMenu(menu[status]);
            return true;
        }
        if (status < menu.length + texts.length) {
            say(texts[status - menu.length], menu.length < status, true);
            return true;
        }
        gainExp(this.reward_exp[index]);
        // lua の qr.setState( id, 1 ), qr.setState( id, 2 )
        forceCompleteQuest();
        questEndEffect();
        return false;
    }
}
