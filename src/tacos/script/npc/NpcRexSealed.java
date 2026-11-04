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
// Npc.wz script : lex (JMS186-302), lua が存在しないため旧 js (scripts_JMS) を参考に実装
public class NpcRexSealed extends NpcScriptMan {

    private final int quest_seal = 3122; // 封印の確認

    @Override
    public boolean action(int status, int answer) {
        if (status != 0) {
            return false;
        }
        // 封印の確認
        if (getPlayer().getQuestStatus(this.quest_seal) == 1) {
            setQuestData(this.quest_seal, "1");
        }
        // TODO : 文章 (適当)
        say("封印されているレックスだ。");
        return false;
    }
}
