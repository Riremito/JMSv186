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
public class NpcHen extends NpcScriptMan {

    private final int quest_collect_eggs = 22007; // 卵を集める
    private final int item_egg = 4032451; // たまご

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getPlayer().getQuestStatus(this.quest_collect_eggs) != 1) {
                    // TODO : 文章 (適当)
                    say("#b今はたまごを取る必要はない。#k");
                    return false;
                }
                // TODO : jsでは持ち物の空きを確認していない
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_egg, 1, "")) {
                    return false;
                }
                MapleInventoryManipulator.addById(getPlayer(), this.item_egg, (short) 1);
                // TODO : 文章 (適当)
                say("#b(たまごを手に入れた。#p1013101#に届けよう。)#k");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
