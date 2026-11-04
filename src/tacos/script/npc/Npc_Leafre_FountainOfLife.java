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
public class Npc_Leafre_FountainOfLife extends NpcScriptMan {

    private final int quest_holy_sword = 6280; // 聖なる騎士の剣
    private final int item_holy_grail = 4031454; // 聖杯
    private final int item_holy_water = 4031455; // 生命の聖水

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getQuestStatus(this.quest_holy_sword) != 1 || !checkItem(this.item_holy_grail)) {
                    return false;
                }
                if (checkItem(this.item_holy_water)) {
                    // 翻訳
                    say("すでに#b#t" + this.item_holy_water + "##kを持っています。");
                    return false;
                }
                if (!canHold(this.item_holy_water, 1)) {
                    // 翻訳
                    say("ETCウィンドウに空きがないので、聖水を受け取れません。空きを作ってからもう一度お試しください。");
                    return false;
                }
                removeItem(this.item_holy_grail, 1);
                gainItem(this.item_holy_water, 1);
                // 翻訳
                say("泉から聖水を汲みました。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
