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
// Quest.wz startscript : q2300s - q2310s, endscript : q2300e - q2310e (JMS186), キノコ王国の事件！, 旧 js (scripts_JMS) を参考に実装
// TODO : js では入口まで送ると言いつつ移動処理がない
public class Quest_MushroomCastle_KingdomInDanger extends QuestScriptMan {

    private final int quest_first = 2300; // キノコ王国の事件！ (最初の 1 つだけ処理が異なる)
    private final int quest_next_first = 2312; // 勇者の試験
    private final int quest_next = 2311; // TODO : JMS186 には存在しないクエスト (js のまま)
    private final int item_letter = 4032375; // 転職官の推薦書

    @Override
    public boolean questStart(int status, int answer) {
        boolean first = (getQuestId() == this.quest_first);
        // 受諾しなかった
        if (getLastAction() == 0 && status == -1) {
            // TODO : 文章 (js の英文を翻訳)
            say("そうですか？急ぎの用件なので、時間があるときに来てください。");
            return false;
        }
        // いいえを選択した
        if (getLastAction() == 0 && status == 2) {
            if (!first) {
                // TODO : 文章 (js の英文を翻訳)
                say("そうですか？急ぎの用件なので、時間があるときに来てください。");
                return false;
            }
            // TODO : js では推薦書を渡さずにクエストを開始している
            // TODO : 文章 (js の英文を翻訳)
            say("わかりました。それでは、キノコ王国への行き方だけ教えましょう。#bヘネシスの西の入口の近くに#k#b空き家#kがあります。その家に入って左へ進むと、#b<テーマダンジョン : キノコ城>#kに入れます。そこがキノコ王国の入口です。時間がありません！", false, true);
            forceStartQuest();
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("転職を終えたあなたなら、もう任せても大丈夫そうですね。手を貸してほしいことがあるのですが、聞いてもらえますか？");
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("実は今、#bキノコ王国#kが混乱に陥っているのです。キノコ王国はヘネシスの近くにあり、平和を愛する聡明なキノコ王が治めています。最近、王が病に伏せるようになり、一人娘の#bビオレッタ姫#kに後を継がせることにしたのです。その後、何かが起こって王国が今のような状態になってしまったのでしょう。", false, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("詳しいことはわかりませんが、何か大変なことが起きたのは明らかです。直接行って被害を確かめてきたほうがいいでしょう。あなたのような冒険家なら、きっとキノコ王国を救えるはずです。#b推薦状#kを書いておきましたので、すぐにキノコ王国へ向かって#b警備隊長#kを探してください。\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v" + this.item_letter + "# #t" + this.item_letter + "#", false, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                askYesNo("ところで、キノコ王国がどこにあるか知っていますか？自分で行けるならそれでいいのですが、よければ入口まで直接送ってあげますよ。");
                return true;
            }
            case 4 -> {
                gainItem(this.item_letter, 1);
                forceStartQuest();
                return false;
            }
            default -> {
            }
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("んん？それは#b転職教官の推薦状#kではないか？！どういうことだ、お前が我らキノコ王国を救いに来てくれた者なのか？", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ふむ…わかった。転職教官からの手紙なら、お前が本当にその者なのだろう。名乗るのが遅れてすまない。私はキノコ王をお守りする#b警備隊長#kだ。見てのとおり、この仮の隠れ家は警備隊と兵士たちが守っている。状況は厳しいが、それでもキノコ王国へようこそ。", true, true);
                return true;
            }
            case 2 -> {
                removeItem(this.item_letter, 1);
                forceCompleteQuest();
                questEndEffect();
                if (getQuestId() == this.quest_first) {
                    forceStartQuest(this.quest_next_first);
                } else {
                    forceStartQuest(this.quest_next);
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
