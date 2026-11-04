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

import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
public class NpcKenta extends NpcScriptMan {

    private final int quest_tame_pig = 6002; // 見張りブタを守る
    private final int item_research_report = 4031508; // 研究報告書
    private final int item_pheromone = 4031507; // フェロモン

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                int state = getPlayer().getQuestStatus(this.quest_tame_pig);
                if (state == 2) {
                    // 翻訳
                    say("もうブタをしっかり守ってくれたね。改めておめでとう～");
                    return false;
                }
                if (state != 1) {
                    // 原文ママ
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("豚を守るとはどういうことでしょうか。どこでその話を聞きましたか。");
                    say(nt);
                    return false;
                }
                if (5 <= getPlayer().getItemQuantity(this.item_research_report, false) && 5 <= getPlayer().getItemQuantity(this.item_pheromone, false)) {
                    // 翻訳
                    say("ねえ～もう#b#t" + this.item_research_report + "##kと#b#t" + this.item_pheromone + "##kは十分持ってるじゃない。もう一度入る必要はないと思うよ～^^");
                    return false;
                }
                if (enterFieldSet("TamePig", 0) != TacosFieldSet.ENTER_SUCCESS) {
                    // 翻訳
                    getPlayer().dropMessage(OpsBroadcastMsg.BM_EVENT.get(), "すでに誰かが見張りブタを守ろうとしているよ。しばらくしてからもう一度試してね。");
                    return false;
                }
                int count_research_report = getPlayer().getItemQuantity(this.item_research_report, false);
                if (0 < count_research_report) {
                    exchange(0, this.item_research_report, -count_research_report);
                }
                int count_pheromone = getPlayer().getItemQuantity(this.item_pheromone, false);
                if (0 < count_pheromone) {
                    exchange(0, this.item_pheromone, -count_pheromone);
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
