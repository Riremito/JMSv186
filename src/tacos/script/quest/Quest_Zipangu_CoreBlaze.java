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
// Quest.wz endscript : q50004e, q50005e (JMS186), コアブレイズ始動, コアブレイズ暴走, 旧 js (scripts_JMS) を参考に実装
public class Quest_Zipangu_CoreBlaze extends QuestScriptMan {

    @Override
    public boolean questEnd(int status, int answer) {
        if (getQuestStatus(getQuestId()) == 0) {
            forceStartQuest();
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("（2102年の渋谷に再びやって来た。そこに見えたのは…#p9120033#だ！） \n……君は……！", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("…ああ、#o9400296#を倒すために送られてきたんだな。本当にすまない…\n（そう言うと、#p9120033#は私の目をまともに見られないようだった）", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("敵の本拠地は六本木の中心、#b六本木モール#kにある。もちろん正面からは入れない。メインロビーには#o9400287#というロボットの軍団が警備として待ち構えているんだ。まずはそのロボットたちの目を欺いて建物に入るのが最初の任務だ。\n（そう言いながら、#p9120033#は地図を一枚手渡してくれた。）", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("実は、渋谷から六本木モールへ続く地下通路があるんだ。そこを使えば、#o9400287#に気づかれずに建物に入れる。これがそこまでの地図だ。一本道だから迷うことはないと思うが、念のため地図を渡しておく。", true, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("2102年の渋谷へ行って、地下通路を使ってモールに入ってくれ。モールは敵の本拠地だから、見たこともないモンスターがたくさんいるかもしれない。決して甘く見ないでくれ。幸運を祈る！");
                forceCompleteQuest();
                questEndEffect();
                safeDispose();
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
