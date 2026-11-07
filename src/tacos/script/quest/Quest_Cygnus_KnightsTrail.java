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
// Quest.wz startscript : q20400s, q20401s, q20405s, q20406s (JMS186), 騎士の軌跡を追って, ゾンビ倒し, 暗黒の魔女の洞窟, 消えた騎士, 旧 js (scripts_JMS) を参考に実装
public class Quest_Cygnus_KnightsTrail extends QuestScriptMan {

    private final int quest_trail = 20400; // 騎士の軌跡を追って
    private final int quest_zombie = 20401; // ゾンビ倒し
    private final int quest_cave = 20405; // 暗黒の魔女の洞窟

    @Override
    public boolean questStart(int status, int answer) {
        if (getQuestId() == this.quest_trail) {
            // TODO : 文章 (js の英文を翻訳)
            say("詳しいことは、エルナスにいるジェーンを訪ねてください。", false, true);
        } else if (getQuestId() == this.quest_zombie) {
            // TODO : 文章 (js の英文を翻訳)
            say("わかりません…手がかりを探しに#bゾンビを倒して#kみてはどうでしょう。何かアイテムが手がかりになるかもしれません。", false, true);
        } else if (getQuestId() == this.quest_cave) {
            // TODO : 文章 (js の英文を翻訳)
            say("エレヴに戻って状況を報告してください。", false, true);
        } else {
            // TODO : 文章 (js の英文を翻訳)
            say("洞窟に戻って、誰かいないか確かめてみてはどうでしょう…", false, true);
        }
        forceStartQuest();
        forceCompleteQuest();
        return false;
    }
}
