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
public class NpcCloy extends NpcScriptMan {

    private static final int MENU_ABOUT = 0;
    private static final int MENU_RAISE = 1;
    private static final int MENU_DIE = 2;
    private static final int MENU_ACTION_PET = 3;
    private static final int MENU_TRANSFER = 4;
    // 各項目の説明 (翻訳)
    private final String[][] texts = {
        {
            "ペットについて知りたいのだな。ずっと昔、わしは人形を作り、それに#t5180000#を振りかけて呪文を唱え、魔法の動物を作り出したのだ。信じられないかもしれんが、本当に命を得た人形なのだよ。人の言うことをよく理解し、よく従うのだ。",
            "だが、#t5180000#は世界樹の根元にほんの少ししか湧かないので、長い命を与えてやることはできんのだ…わかっておる、とても悲しいことだ…だが、たとえまた人形に戻っても、わしはいつでも命を取り戻させてやれる。一緒にいる間は優しくしてやっておくれ。",
            "そうそう！ペットは特別なコマンドを与えると反応するのだ。叱ることも、かわいがることもできる…すべてはお前さんの世話の仕方しだいだ。ペットは飼い主と離れるのを怖がるから、優しくして、愛していることを示してやりなさい。急に悲しくなってしまうこともあるからな…"
        },
        {
            "与えるコマンドによって、ペットは喜んだり、嫌がったり、ほかにもいろいろな反応を見せる。コマンドを与えてペットがきちんと従えば、親密度が上がる。ペットをダブルクリックすれば、親密度、レベル、満腹度などを見ることができるぞ。",
            "ペットに話しかけて、気を配ってやれば、親密度が上がっていく。そうすれば、いずれ全体のレベルも上がる。親密度が上がると、その後すぐにペットのレベルも上がるのだ。レベルが上がっていくと、いつかペットは人間のように少しだけ話せるようになるかもしれん。だから、しっかり上げてみなさい！もちろん、そう簡単ではないがな…",
            "ただの命を得た人形かもしれんが、わしらと同じように普通の命を持っておる。だから、腹も減るのだ。#b満腹度#kはペットの空腹の度合いを示しておる。100が最大で、低くなるほどペットが腹を空かせているということだ。しばらくするとコマンドに従わなくなり、攻撃的になるから、気をつけるのだぞ。",
            "そうそう！ペットは人間の食べ物を食べられないのだ。わしの弟子の#b#p1012004##kが#m100000000#の市場で#b#t2120000##kを売っておる。ペットの餌が必要なら、#m100000000#へ行きなさい。餌を買いだめしておいて、ペットがひどく腹を空かせる前に食べさせてやるのがいいぞ。",
            "ああ…それから、長い間ペットに餌をやらないと、ペットは自分で家に帰ってしまう。家から連れ出して餌をやることもできるが、それはペットの健康にとってまったくよくない。そこまでならないように、定期的に餌をやるのだぞ、いいな？お前さんならきっとそうしてくれるだろう。"
        },
        {
            "死ぬ…か。ペットは厳密に言えば「生きている」わけではない。外からの力で命を得たものだから、死という言葉が正しいのかどうかわからんのだ。わしの魔力を宿した人形で、#t5180000#の力が物に命を与えておる。もちろん、生きている間は普通の動物と同じだがな…",
            "しばらくすると…これは確かなことだが、ペットは動かなくなる。魔法の効果が弱まり、#t5180000#が尽きると、ただの人形に戻ってしまうのだ。だが、永遠に止まってしまうわけではない。#t5180000#をかけてやれば、また命を取り戻すからな。",
            "また動き出すとはいえ、まったく動かなくなってしまうのを見るのは悲しいものだ。生きている間は優しくしてやるのだぞ、いいな？餌もちゃんとやりなさい。自分だけに従って、そばにいてくれる生き物がいるというのは、素敵なことだと思わんかね？"
        },
        {
            // TODO : アクションペットの説明 (文章が不明)
        },
        {
            "ペットのスキルポイント、親密度、レベルを移すには、ペットのスキルポイント初期化の書が必要だ。この書をエリニアの妖精マーのところへ持っていけば、ペットのレベルと親密度をほかのペットに移してくれる。これを渡すのは、何よりお前さんのペットへの愛情が感じられるからだ。とはいえ、ただで配るわけにはいかん。この本は250,000メルで渡そう。おっと、言い忘れるところだった！この本を持っていても、スキルポイントを移す新しいペットがいなければ役に立たんぞ。",
            "インベントリに空きスロットがあるか、お金が足りているか確認しなさい。"
        }
    };
    private int menu = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("君…もしや私の子供を育てているのではないか？私は生命の水と魔法を使って、人形に命を吹き込むことに成功した。人々はそのように命を得た私の子供達を#bペット#kと呼んでいるようだね。もしペットを持っているなら何でも私に聞いておくれ");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("何が知りたいのかね？");
                nt.addMenu(MENU_ABOUT, "ペットって何？");
                nt.addMenu(MENU_RAISE, "ペットはどうやって育てるの？");
                nt.addMenu(MENU_DIE, "ペットも死ぬの？");
                nt.addMenu(MENU_ACTION_PET, "アクションペットについて知りたい！");
                nt.addMenu(MENU_TRANSFER, "ペットの能力値を移動させる方法を知りたい！");
                askMenu(nt);
                return true;
            }
            case 2 -> {
                if (answer < 0 || this.texts.length <= answer) {
                    return false;
                }
                this.menu = answer;
            }
            default -> {
            }
        }
        // 説明
        int index = status - 2;
        if (this.menu < 0 || this.texts[this.menu].length <= index) {
            return false;
        }
        boolean next = index + 1 < this.texts[this.menu].length;
        say(this.texts[this.menu][index], false, next);
        return next;
    }
}
