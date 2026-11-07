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
// Quest.wz startscript : q20700s (JMS186), このまま出られる？, 旧 js (scripts_JMS) を参考に実装
public class Quest_Cygnus_AreYouSureYouCanLeave extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 0) {
            // TODO : 文章 (js の英文を翻訳)
            say("自分がどれほど弱いか、いつになったら気づくのですか…ビクトリアアイランドで痛い目にあってからですか？", false, true);
            safeDispose();
            return true;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ついに見習い騎士になりましたね。すぐにでも任務を与えたいところですが、まだ一人で任務をこなせるようにはとても見えません。本当にこのままビクトリアアイランドへ行くつもりですか？", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("ビクトリアアイランドへ行くのはあなたの自由ですが、戦いで自分の身も守れない見習い騎士は、女王陛下の名声に傷をつけかねません。この島の軍師として、それだけは絶対に許すわけにはいきません。しかるべき時が来るまで修行を続けてください。");
                return true;
            }
            case 2 -> {
                forceCompleteQuest();
                // TODO : 文章 (js の英文を翻訳)
                say("修練教官の#p1102000#が、役に立つ騎士になれるよう修行を手伝ってくれます。レベル13になったら、任務をいくつか与えましょう。それまでは修行を続けてください。", false, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("それと、#p1101001#に話しかけると祝福をしてくれることを知っていますか？祝福はきっと旅の助けになるはずです。", true, false);
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
