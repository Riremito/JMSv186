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
public class NpcPerionWarningPost extends NpcScriptMan {

    private final int quest_guard_third_job = 22530; // 警備兵の三番目の仕事 : 警告文は事前に
    private final int item_reward = 1952000; // シルバーペンダント
    private final int reward_exp = 710;
    private final int reward_sp = 1;
    private final int reward_sp_skillbook = 1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getPlayer().getQuestStatus(this.quest_guard_third_job) != 1) {
                    // TODO : 文章 (適当)
                    say("警告板だ。");
                    return false;
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_reward, 1, "")) {
                    // TODO : 文章 (適当)
                    say("持ち物に空きが必要です。");
                    return false;
                }
                MapleQuest.getInstance(this.quest_guard_third_job).forceComplete(getPlayer(), getNpcId());
                getPlayer().gainExp(this.reward_exp, true, true, true);
                MapleInventoryManipulator.addById(getPlayer(), this.item_reward, (short) 1);
                // TODO : jsではエヴァンのスキルブック1にSPを加算している
                getPlayer().gainSP(this.reward_sp, this.reward_sp_skillbook);
                // TODO : 文章 (適当)
                say("警告板を調べた。警備兵の頼みを終えた。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
