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

import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class NpcAncientIcyStone extends NpcScriptMan {

    private final int quest_hammer = 6263; // 氷を壊すハンマー
    private final int item_orichalcum_hammer = 4031450; // オリハルコンハンマー
    private final int item_ancient_ice_powder = 2280011; // 古代の氷粉

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getPlayer().getQuestStatus(this.quest_hammer) != 2 || !checkItem(this.item_orichalcum_hammer)) {
                    return false;
                }
                // 翻訳
                askYesNo("#b#t" + this.item_orichalcum_hammer + "##kで古代の氷石を叩きますか？");
                return true;
            }
            case 1 -> {
                if (!checkItem(this.item_orichalcum_hammer)) {
                    return false;
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_ancient_ice_powder, 1, "")) {
                    // 翻訳
                    say("消費ウィンドウに空き枠を作ってから、もう一度お試しください。");
                    return false;
                }
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_orichalcum_hammer, 1, true, false);
                MapleInventoryManipulator.addById(getPlayer(), this.item_ancient_ice_powder, (short) 1);
                // 翻訳
                say("#b#t" + this.item_orichalcum_hammer + "##kで叩くと、氷石の粉がこぼれ落ちました。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
