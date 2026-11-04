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

import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class NpcNautilusBush extends NpcScriptMan {

    private final int quest_find_glasses = 2186; // メガネを探して！
    private final int item_abel_glasses = 4031853; // アベルのメガネ
    private final int item_someone_glasses1 = 4031854; // 誰かのメガネ
    private final int item_someone_glasses2 = 4031855; // 誰かのメガネ

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getPlayer().getQuestStatus(this.quest_find_glasses) != 1) {
                    return false;
                }
                int rand = (int) (Math.random() * 2);
                int item_id = this.item_someone_glasses2;
                if (rand == 0 && !checkItem(this.item_abel_glasses)) {
                    item_id = this.item_abel_glasses;
                } else if (rand == 1) {
                    item_id = this.item_someone_glasses1;
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), item_id, 1, "")) {
                    return false;
                }
                MapleInventoryManipulator.addById(getPlayer(), item_id, (short) 1);
                if (item_id == this.item_abel_glasses) {
                    // TODO : 文章 (適当)
                    say("#b#t" + this.item_abel_glasses + "##kを見つけた。");
                    return false;
                }
                // TODO : 文章 (適当)
                say("メガネを見つけたが、アベルのものではないようだ。アベルのメガネは角縁のはずだ…");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
