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
// Quest.wz startscript : q3305s, q3306s (JMS186), ジェニミストのマントを再び受け取る。, アルカドノのマントを再び受け取る。, lua の処理を利用
public class Quest_Magatia_ReceivingTheCapeAgain extends QuestScriptMan {

    private final int quest_zenumist = 3305; // ジェニミストのマントを再び受け取る。
    private final int item_cape_zenumist = 1102135; // ジェニミストのマント
    private final int item_cape_alcadno = 1102136; // アルカドノのマント
    private final int[] quests_finished = {3347, 3348, 3349}; // lua の qr.getState( 3347 - 3349 ) == 2

    @Override
    public boolean questStart(int status, int answer) {
        boolean zenumist = getQuestId() == this.quest_zenumist;
        // 受諾しなかった
        if (getLastAction() == 0 && status == -1) {
            if (zenumist) {
                // 翻訳
                say("嫌なら仕方ない。君はもうジェナミスト所属ではない。");
                return false;
            }
            // 翻訳
            say("嫌なら仕方ない。マントがないからといってアルカドノ所属でなくなるわけではないが…誰も君をアルカドノとは思わないだろうから、肝に銘じておきたまえ。");
            return false;
        }
        switch (status) {
            case 0 -> {
                for (int quest_id : this.quests_finished) {
                    if (getQuestStatus(quest_id) == 2) {
                        if (zenumist) {
                            // 翻訳
                            say("ジェナミストとして受けられる恩恵はもうすべて受けたようだが…残念だが、これ以上ジェナミストのマントを再支給することはできない。");
                            return false;
                        }
                        // 翻訳
                        say("アルカドノとして受けられる恩恵はもうすべて受けたではないか。残念だが、これ以上アルカドノのマントを再支給することはできない。");
                        return false;
                    }
                }
                if (isWear(zenumist ? this.item_cape_zenumist : this.item_cape_alcadno)) {
                    // lua ではジェニミストでもアルカドノのマントと表示される
                    // 翻訳
                    say("すでにアルカドノのマントを持っているようだから、新しいマントは必要ないな。");
                    return false;
                }
                if (zenumist) {
                    // 翻訳
                    askAccept("うむ？君は少し前にジェナミスト所属になった錬金術師ではないか？ところで、ジェナミストのマントはどこに…やれやれ。マントをなくしたようだな。マントは誇り高きジェナミスト学派であることを証明する大切なもの。それをなくすとは…正気かね？実に不愉快だ。君のような者にはもうマントを渡したくないが…仕方あるまい。マントを再び受け取るかね？");
                    return true;
                }
                // 翻訳
                askAccept("見覚えのある顔だから、君は確かにアルカドノ所属の錬金術師のようだが…なぜアルカドノのマントをつけていないのかね？アルカドノ所属なら誰でもつけていなければならないと言ったはずだが…何？マントをなくしただと…？もうアルカドノでいたくないということか？ふむ…そうではないようだな。では、マントを再支給するから、受け取るかね？");
                return true;
            }
            case 1 -> {
                forceStartQuest();
                if (zenumist) {
                    // 翻訳
                    say("もう一度だけチャンスをやろう。ジェナミストのマントを作りたければ、#b#t4000021# 10個#kと#b#t4021003##k 5個、それに錬成する者への手間賃#b10000メル#kを持ってきたまえ。");
                    return false;
                }
                // 翻訳
                say("アルカドノに初めて入ったときはマントをそのまま支給したが、再支給のときは自分で材料を集めてこなければならない。材料は#b#t4000021# 10個#kと#b#t4021006# 5個#k、それに錬成する者に支払う#b10000メル#kだ。では、忘れずに持ってきたまえ。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
