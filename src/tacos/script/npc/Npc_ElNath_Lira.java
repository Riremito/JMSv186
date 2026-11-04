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
import odin.server.quest.MapleQuest;

/**
 *
 * @author Riremito
 */
public class Npc_ElNath_Lira extends NpcScriptMan {

    private final int quest_stage2 = 7001; // ジャクムダンジョン 2段階
    private final int item_breath_of_lava = 4031062; // 火山の息吹
    private final int map_door_to_zakum = 211042300; // エルナス : ジャクムへの門
    private final int reward_exp = 15000;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // 翻訳
                say("あんなに危険な道をどうやってここまで来たのですか？？信じられません！#b#t" + this.item_breath_of_lava + "##kはここにあります。これを私の兄に渡してください。もうすぐ、ずっと探していた相手に会えますよ。", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 入場から2分以内にクリアした場合は報酬なし (quest 7005)
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_breath_of_lava, 1, "")) {
                    // 翻訳
                    say("ETCインベントリがいっぱいのようです。アイテムを受け取るために空きを作ってください。");
                    return false;
                }
                MapleInventoryManipulator.addById(getPlayer(), this.item_breath_of_lava, (short) 1);
                // TODO : 1日3回まで (quest 7007)
                getPlayer().gainExp(this.reward_exp, true, true, true);
                MapleQuest.getInstance(this.quest_stage2).forceStart(getPlayer(), getNpcId(), "end");
                warp(this.map_door_to_zakum);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
