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
public class NpcTylus extends NpcScriptMan {

    private final int npc_job = 1022000; // コブシを開いて立て
    private final int item_necklace_of_strength = 4031057; // 強靭のネックレス

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("私に何の用だい？");
                nt.addMenu(0, "3次転職したいです。");
                nt.addMenu(1, "ジャクムダンジョンクエストを許可してください。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer != 0) {
                    // TODO : ジャクムダンジョンクエストの許可
                    return false;
                }
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("おほん、何の用で私を訪ねたんだい？む・・・.3次転職をし、もっと強い戦士になりたいのか。もちろん私の力で君をもう少し強くしてあげる事はできるが、その前に君がどれほど熱心に修練したのかについて検証が必要なんだ。今までもっと強くなりたいと私を訪ねてきた若者たちは多かったが、実際に自分の強さを証明できたやつはあまりいなかった。どうだい？しくはないだろうが君を試してもいいかい？");
                askYesNo(nt);
                return true;
            }
            case 2 -> {
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("よし！君が証明しなければいけないのは、 君の力と知恵、この二つだ。まず、力についての試験から説明をしてあげるよう。君に1次、2次転職をさせてくれたペリオンの #b" + getNpcName(this.npc_job) + "#kを覚えているだろう？を訪ねると彼が君に一つの任務を託するだろう。その任務を無事に完遂し、 " + getNpcName(this.npc_job) + "から#b#z" + this.item_necklace_of_strength + "##kをもらって来てくれ。");
                say(nt);
                // TODO : 力のテストの開始, 以降の文章 (元のスクリプトは "xxx")
                // TODO : 3次転職 (110 → 111, 120 → 121, 130 → 131, 2110 → 2111), タイラスを守るクエスト(6192)と スタンス の習得 (コメントアウトされた旧スクリプト)
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
