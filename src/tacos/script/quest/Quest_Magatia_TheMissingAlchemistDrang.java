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
// Quest.wz startscript : q3321s (JMS186), 失踪した錬金術師ドラン, lua の処理を利用
public class Quest_Magatia_TheMissingAlchemistDrang extends QuestScriptMan {

    private final int map_restricted_area = 261020401; // アルカドノ研究所 : 関係者以外出入禁止区域

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 3) {
            // 翻訳
            say("失礼な方ですね…");
            return false;
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                say("…なぜこんなところに来られたのかわかりませんが…錬金術師の実験室はあまり楽しい場所ではありません。錬金術師でない人の目には、とても退屈に見えるそうですよ。もっとも…彼女は妖精ですから、なおさらつまらなく見えるかもしれませんね…", false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                say("彼女は誰かって？彼女は…私の妻です。そういえば、彼女の顔をもうずいぶん見ていませんね…娘の顔もおぼろげなくらいですから…彼女はきっとすごく怒るでしょうね。もちろん、優しい彼女ならすぐに許してくれるでしょうけど…", true, true);
                return true;
            }
            case 2 -> {
                // 翻訳
                say("…でも仕方ありません。この研究を終えるまでは彼女の顔を見ないと決めたのですから。とても会いたいけれど…研究を終えるまでは…研究さえ終われば、永遠に#b#p2111004##kの顔を見られるはずです。", true, true);
                return true;
            }
            case 3 -> {
                // 翻訳
                say("そういえば、#bペンダント#kをまだ彼女に贈れていませんでした。彼女に見つからないように、#b額縁の裏#kに隠しておいたのですが…彼女の顔を見られないので、贈ることすらできません。いつになったら彼女に会えるのでしょう…", true, true);
                return true;
            }
            case 4 -> {
                // 翻訳
                askAccept("…余計な話が長くなりすぎましたね。申し訳ありませんが、研究を続けなければならないので…この研究室から出ていってください。");
                return true;
            }
            case 5 -> {
                forceStartQuest();
                warp(this.map_restricted_area);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
