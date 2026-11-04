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
// Npc.wz script : About_NLC (BMS8-24, EMS70-76, GMS92-95), 会話文は lua_script_jp (About_NLC) を利用
public class NpcIcebyrdSlimm extends NpcScriptMan {

    private final int quest_quiz = 4900; // NLC のクイズ
    private final int level_min = 15;
    // 翻訳
    private final String[] says = {
        "俺はずっと町を作るのが夢だったんだ。ただの町じゃない、誰もが歓迎される町さ。昔はカニングシティに住んでいたんだが、自分で町を作れるか試してみようと思ってね。その方法を探していくうちに、たくさんの人に出会った。その中には友達だと思えるようになった人もいる。たとえばフォックスウィット教授、この町の天才だ。人食い植物の群れから彼を助けたんだ。ジャック・マスクはアモリア時代からの狩り仲間で、ちょっと口がうますぎるのが玉にきずだな。リタとはカニングシティ時代からの古い友達で、あの武器で何度か俺を助けてくれた。だから保安官にぴったりだと思ったんだ。説得には少し手間取ったが、彼女も自分の運命はここにあると信じるようになった。この町の探検家バリケードは、何かを探してやってきた。見つけたものは何でも博物館に持ってくると約束してくれたよ。カニングシティにいたころから、彼とその兄弟の噂は聞いていた。それとエルパムは…まあ、このあたりの者じゃないとだけ言っておこう。まったくな。前に話したことがあるが、悪い奴じゃなさそうだから、ここにいることを許したんだ。おっと、ずいぶんしゃべりすぎちまったな！ほかに何が知りたい？",
        "97歳にしてはなかなか元気なじいさんさ。ある日、町の外で出会った時間旅行者なんだ。じいさんはジャングルの生き物たちに困らされていた…食べられそうになっていたんだよ。助けたお礼に、時間博物館を建てることを約束してくれた。でも、ほかにも目的があってここに来た気がするんだ。ニューリーフシティは未来で面白い役割を果たすことになるって、何度も言っていたからな。君なら何か探り出せるかもしれないな…",
        "へへ、教授がそれを作っているのを見たとき、俺も同じことを聞いたよ。あれはワープポイントさ。上キーを押すと別の場所にワープするんだ。使いこなせるようになっておくといい。俺たちの交通手段だからな。",
        "メソギアはビッガーベンの地下にある。バリケードが発見した、モンスターがはびこるビッガーベンの一角さ。塔とは別の区画にあるようで、俺に言わせれば実に奇妙だ。探検するのに少し手を貸してほしいらしいから、会いに行ってみるといい。でも気をつけろよ、あそこのウルフスパイダーはただ者じゃない。",
        "ああ…そうだな。クラキアのジャングルはニューリーフシティの郊外にある。新しくて強力な生き物がたくさんうろついているから、行くなら戦う覚悟をしておいた方がいい。町の左端にあるよ。ジャングルは失われた都市に通じているという噂がいろいろあるが、まだ何も見つかっていない。",
        "ジョンがビッガーベンのメソギアの区画にいたとき、それに乗ったら別の場所に移動したそうだ。ただ、行ったり来たりしかできなくて、フォックスウィット・ドアのように循環はしないんだ。古代の技術ってやつさ。",
        "まあ、そこら中で見かけるだろう。あれは工事中のエリアなんだ。赤いライトはまだ完成していないという意味で、緑のライトは開いているという意味だ。こまめにチェックしてくれ、いつも何かしら建設中だからな！",
        "ああ、ジャックか。学校なんて行かなくても平気そうなクールな奴っているだろう？何をしても許されちまうような奴。しかも女の子にもモテる。そう、それがジャックさ。ただし女の子はいないけどな。チャンスを逃したと思い込んで、正体を隠すためにあの仮面をつけ始めたんだ。あいつが誰なのかは口が裂けても言えないが、アモリアの出身だよ。聞けばもう少し話してくれるかもしれないな。",
        "リタとは長い付き合いだが、最近になってまた友情を取り戻したところなんだ。しばらく会っていなかったが、理由はわかる。彼女は盗賊として、とても、とても長い間修行していたんだ。実は、それが俺たちの出会いのきっかけだった。俺がはぐれキノコの群れに囲まれていたとき、彼女が飛び込んで助けてくれたんだ。保安官を選ぶことになったとき、迷う余地はなかったよ。彼女はほかの人の修行を手伝い、町を守ると約束してくれた。だから、町のために少し働いてみたいなら、彼女に話しかけてみるといい。",
        "もうすぐさ、友よ。目には見えないかもしれないが、町の開発者たちは一生懸命働いている。準備ができたら開くよ。君も楽しみにしているだろう？俺もさ！"
    };

    @Override
    public boolean action(int status, int answer) {
        if (status < 0) {
            return false;
        }
        // 偶数の status で選択肢を表示し、奇数の status で選択肢の会話を表示する (lua の while(1))
        if (status % 2 == 0) {
            NpcScriptTag nt = new NpcScriptTag();
            // 翻訳
            nt.add("よう！俺の名はアイスバード・スリム、ニューリーフシティの市長さ！招待を受けてくれてうれしいよ。で、何か用かい？	\r\n#b#L0#ここはどんな場所？ #l\r\n#L1#フォックスウィット教授って誰？ #l\r\n#L2#フォックスウィット・ドアって何？ #l\r\n#L3#メソギアはどこにあるの？ #l\r\n#L4#クラキアのジャングルって何？ #l\r\n#L5#ギアポータルって何？ #l\r\n#L6#道路標識はどういう意味？ #l\r\n#L7#ジャック・マスクってどんな人？ #l\r\n#L8#リタ・ローレスって手ごわそうだけど、どんな人なの？#l\r\n#L9#町に新しい地区はいつできるの？ #l\r\n#L10#クイズを受けたい！#l#k", false);
            askMenu(nt);
            return true;
        }
        if (0 <= answer && answer < this.says.length) {
            say(this.says[answer], false, true);
            return true;
        }
        if (answer != 10) {
            return false;
        }
        if (getPlayer().getLevel() < this.level_min) {
            // 翻訳
            say("悪いが、このクイズはレベル15以上でないと受けられないんだ。受ける準備ができたら、また来てくれ。", false, true);
            return true;
        }
        if (getPlayer().getQuestStatus(this.quest_quiz) == 2) {
            // 翻訳
            say("君はもう俺の問題を解いたよ。NLCでの旅を楽しんでくれ！！！", false, true);
            return true;
        }
        // 翻訳
        say("いいとも。正解したら、いいものをあげよう！");
        setQuestData(this.quest_quiz, "1");
        return false;
    }
}
