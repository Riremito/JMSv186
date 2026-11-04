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
public class Npc_Leafre_Moose extends NpcScriptMan {

    private final int quest_power_of_shield = 6180; // 盾の力
    private final int map_training = 924000000; // 修練場への道

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getQuestStatus(this.quest_power_of_shield) != 1) {
                    // 翻訳
                    say("盾の修行場？どこでその話を聞いたんだ？");
                    return false;
                }
                // 翻訳
                say("よし。盾の修行場へ送ってやろう。もう一度話しかけてくれ。");
                warp(this.map_training);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
