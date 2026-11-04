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
public class NpcPiglet extends NpcScriptMan {

    private final int quest_rescue = 22005; // ちびオオカミを救う
    private final int item_baby_wolf = 4032449; // 赤ちゃんオオカミ

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : jsではクエスト22015を完了しているが、JMS186には存在しないため22005 (完了時に赤ちゃんオオカミが必要) で判定する
                if (getPlayer().getQuestStatus(this.quest_rescue) != 1 || checkItem(this.item_baby_wolf)) {
                    return false;
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_baby_wolf, 1, "")) {
                    return false;
                }
                MapleInventoryManipulator.addById(getPlayer(), this.item_baby_wolf, (short) 1);
                // TODO : メッセージ表示 (赤ちゃんオオカミを救出した)
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
