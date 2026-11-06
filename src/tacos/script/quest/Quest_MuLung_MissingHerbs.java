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
// Quest.wz endscript : q3833e (JMS186), 足りない薬材探し, lua の処理を利用 (年老いた桔梗集め)
public class Quest_MuLung_MissingHerbs extends QuestScriptMan {

    private final int item_herb = 4000294; // しなびたキキョウ
    // 所持数の下限ごとの報酬
    private final int[] tier_min = {1000, 900, 700, 500, 300, 200, 100, 50, 10, 1};
    private final int[][] tier_items = {
        {
            2000005, 50, // パワーエリクサー
            2040501, 1 // 素早さの書(全身鎧)60%
        },
        {
            2020013, 50, // トナカイの乳
            2040502, 1 // 素早さの書(全身鎧)10%
        },
        {
            2000004, 50, // エリクサー
            2040500, 1 // 素早さの書(全身鎧)
        },
        {
            2020013, 50 // トナカイの乳
        },
        {
            2020012, 50 // どろ～りチーズ
        },
        {
            2001001, 50 // チューチューアイス
        },
        {
            2020008, 50 // ミニソーセージ
        },
        {
            2020007, 50 // するめ
        },
        {
            2022144, 10 // 蛇尾湯
        },
        {
            2000000, 1 // 赤い薬
        }
    };
    private final int[] tier_exp = {54000, 54000, 54000, 54000, 51000, 48000, 45000, 10000, 1000, 10};
    private final String[] tier_texts = {
        // 翻訳
        "ほ…ほっほっほっほ。お前さんは人間か？！こんなにたくさん集めてくるとは、一体年老いた桔梗を何匹倒したんじゃ…？！ごほん。とにかくありがとう。",
        // 翻訳
        "いや…本当にこれを全部お前さんが集めたのか？す、すごいのう…ここまで集めるとは…お前さんは真に素晴らしい冒険者じゃ。本当に見事じゃ！これだけ集めたなら、こんなものをあげても惜しくはないのう。",
        // 翻訳
        "ほう…本当にすごいのう。ここまで集めるのは簡単ではなかったじゃろうに。本当にありがとう。",
        // 翻訳
        "おお…多いのう。これだけあれば太上も文句は言えんじゃろう。お前さん、見た目よりやるのう。",
        // 翻訳
        "ふむ…まあ、これだけあれば当分は太上も何も言わんじゃろう。ありがとう。",
        // 翻訳
        "よし。これだけあれば、なんとか太上をなだめられそうじゃ。ありがとう。",
        // 翻訳
        "ふむ。少し足りないが、まあ今急ぎなのはこっちじゃから仕方ないのう。",
        // 翻訳
        "ええい。たったこれだけか？すごく少ないわけではないが、満足はできんのう。",
        // 翻訳
        "たったこれだけか？ふむ…お前さんの力がこれだけなら何も言えんが…やれやれ。若い者がこれではのう。",
        // 翻訳
        "…まあ、そうか。とにかく持ってきてはくれたんじゃのう。"
    };
    private final String[] tier_texts_end = {
        // 翻訳
        "これだけあれば太上も気絶するんじゃないかのう…",
        // 翻訳
        "こんなにたくさん、どうやって武陵まで持っていけばいいんじゃ…？",
        // 翻訳
        "ふむ。下手をすると、桔梗を全部船に積むのも難しそうじゃのう。",
        // 翻訳
        "桔梗を船まで運ぶのも簡単ではなさそうじゃのう。ファン船長に運んでもらうよう頼まねば…",
        // 翻訳
        "これだけあれば太上も満足するじゃろう。",
        // 翻訳
        "さあて、それじゃあ桔梗をよく干してみるかのう。薬材に使うには干すのも大事なんじゃ。",
        // 翻訳
        "ちっ。太上がしばらくぶつぶつ言いそうじゃのう..",
        // 翻訳
        "略奪さえなければ…こんなふうに集める必要もないんじゃが。",
        // 翻訳
        "これでは太上に合わせる顔がないのう…",
        // 翻訳
        "しばらく太上とは連絡を取らない方がよさそうじゃのう…"
    };
    private int herb_count = 0;
    private int tier = -1;

    @Override
    public boolean questEnd(int status, int answer) {
        switch (status) {
            case 0 -> {
                this.herb_count = getItemCount(this.item_herb);
                this.tier = -1;
                for (int i = this.tier_min.length - 1; 0 <= i; i--) {
                    if (this.tier_min[i] <= this.herb_count) {
                        this.tier = i;
                    }
                }
                if (this.tier < 0) {
                    // 翻訳
                    say("まだ100年物の桔梗は手に入れていないようじゃのう。それは年老いた桔梗を倒すと手に入るんじゃ。");
                    return false;
                }
                // 翻訳
                say(this.tier_texts[this.tier] + "お前さんが集めた#b#t" + this.item_herb + "#" + this.herb_count + "個#kを早くわしにくれ。\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#fUI/UIWindow.img/QuestIcon/5/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.tier_exp[this.tier] + " 経験値", false, true);
                return true;
            }
            case 1 -> {
                int[] reward = this.tier_items[this.tier];
                int[] items = new int[reward.length + 2];
                items[0] = this.item_herb;
                items[1] = -this.herb_count;
                System.arraycopy(reward, 0, items, 2, reward.length);
                if (!exchange(0, items)) {
                    // 翻訳
                    say("どうしてそんなにたくさん持ち歩いているのかね？インベントリに空きがあるか確認してくれたまえ。");
                    return false;
                }
                gainExp(this.tier_exp[this.tier]);
                forceCompleteQuest();
                questEndEffect();
                say(this.tier_texts_end[this.tier]);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
