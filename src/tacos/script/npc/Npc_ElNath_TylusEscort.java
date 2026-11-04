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
public class Npc_ElNath_TylusEscort extends NpcScriptMan {

    private final int quest_escort_tylus = 6192; // タイラスの護衛
    private final int item_tylus_trust = 4031495; // タイラスの信頼
    private final int map_chief_residence = 211000001; // エルナス : 長老の官邸

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getPlayer().getQuestStatus(this.quest_escort_tylus) != 1) {
                    warp(this.map_chief_residence);
                    return false;
                }
                // 翻訳
                say("守ってくれてありがとう。おかげで任務を果たせたよ。外に出たら話しかけてくれ。", false, true);
                return true;
            }
            case 1 -> {
                if (!checkItem(this.item_tylus_trust)) {
                    if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_tylus_trust, 1, "")) {
                        // 翻訳
                        say("ETCウィンドウに空きがないので、アイテムを渡せない。空きを作ってからもう一度話しかけてくれ。");
                        return false;
                    }
                    MapleInventoryManipulator.addById(getPlayer(), this.item_tylus_trust, (short) 1);
                }
                warp(this.map_chief_residence);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
