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
// Quest.wz startscript : q20015s (JMS186), 幼い女王の挨拶, 旧 js (scripts_JMS) を参考に実装
public class Quest_Cygnus_EncounterWithTheYoungQueen extends QuestScriptMan {

    private final int item_medal = 1142065; // ノーブレスの勲章

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 1) {
            // TODO : 文章 (js の英文を翻訳)
            say("今はためらっているかもしれませんが、あなたの目の奥にはすばらしい勇気が見えます。目を閉じて、あなたの中にある勇気と情熱を感じてください。", false, true);
            safeDispose();
            return true;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("知っていますか？メイプルワールドは平和に見えても、ところどころ闇の力で満ちているのです。暗黒の魔法使いと、暗黒の魔法使いを復活させようとする者たちが、メイプルワールドを脅かしています。", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("敵が強くなっていくのを、何もせずに見ているわけにはいきません。恐れていては、いずれその恐れが私たちに返ってくるだけです。", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("でも、あまり心配はしていません。あなたのように意志の強い人なら、きっとメイプルワールドを危険から守ってくれますよね？自ら騎士に志願するほど勇敢なあなたなら、頼りにできると信じています。 \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i" + this.item_medal + "# #t" + this.item_medal + "# - 1");
                return true;
            }
            case 3 -> {
                if (getQuestStatus(getQuestId()) == 0) {
                    gainItem(this.item_medal, 1);
                    forceCompleteQuest();
                }
                // TODO : 文章 (js の英文を翻訳)
                say("ふふ、そう言ってくれると思っていました。でも、メイプルワールドのために戦えるようになるまでには、まだ道のりがあることはわかっていますよね？", false, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("私の隣にいる軍師の#p1101002#が、あなたが立派な騎士になれるよう手助けしてくれます。あなたの成長を楽しみにしていますね。期待していますよ！", true, false);
                safeDispose();
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
