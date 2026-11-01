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
package tacos.command;

import java.util.ArrayList;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import tacos.debug.DebugLogger;
import tacos.server.map.TacosSpawnPoint;
import tacos.wz.ServerImg;
import tacos.wz.ServerImg.RewardData;
import tacos.wz.WzNameStorage;

/**
 *
 * @author Riremito
 */
public class TacosCommandInfo {

    public static boolean executeCommand(TacosCommander dcmd, TacosCharacter chr) {

        TacosMap map = chr.getMap();
        switch (dcmd.get(0)) {
            // server.
            case "/threadid" -> {
                chr.DebugMsg("thread id = " + DebugLogger.getThreadId());
                return true;
            }
            // player.
            case "/playerxy" -> {
                chr.DebugMsg("X  : " + chr.getX());
                chr.DebugMsg("Y  : " + chr.getY());
                chr.DebugMsg("FH : " + chr.getFootholdId());
                chr.DebugMsg("MA : " + chr.getMoveAction());
                return true;
            }
            case "/cr" -> {
                chr.DebugMsg(String.format("Critical : %d%%", chr.getCriticalRate().get()));
                return true;
            }
            // map.
            case "/mapinfo" -> {
                for (TacosSpawnPoint sp : map.getMonsterSpawnPoint()) {
                    if (sp.getMobTime() <= -1) {
                        chr.DebugMsg(String.format("%d (%s) : map reset is required.", sp.getId(), WzNameStorage.MOB.get(sp.getId()).getName()));
                    } else {
                        double interval = (double) sp.getMobTime() / 1000 / 3600;
                        chr.DebugMsg(String.format("%d (%s) : %.1f hours.", sp.getId(), WzNameStorage.MOB.get(sp.getId()).getName(), interval));
                    }
                }
                return true;
            }
            case "/dropinfo" -> {
                ArrayList<Integer> mob_ids = new ArrayList<>();
                for (TacosSpawnPoint sp : map.getMonsterSpawnPoint()) {
                    int mob_id = sp.getId();
                    if (!mob_ids.contains(mob_id)) {
                        mob_ids.add(mob_id);
                    }
                }
                int server_drop_rate = chr.getChannelServer().getDropRate();
                for (int mob_id : mob_ids) {
                    chr.DebugMsg("[" + mob_id + " - " + WzNameStorage.MOB.get(mob_id).getName() + "]");
                    ArrayList<RewardData> list_reward = ServerImg.getAutoRewardData(mob_id);
                    if (list_reward != null) {
                        for (RewardData reward : list_reward) {
                            double reward_prob = reward.getProb() * 100.0 / ServerImg.PROB_MAX * server_drop_rate;
                            if (100.0 < reward_prob) {
                                reward_prob = 100.0;
                            }
                            if (reward.getItem() != 0) {
                                chr.DebugMsgItem(reward.getItem() + " : " + String.format("%05.2f%%", reward_prob) + " - " + WzNameStorage.ITEM.get(reward.getItem()).getName() + ((reward.getQuestId() != 0) ? " (quest " + reward.getQuestId() + ")" : ""), reward.getItem());
                            } else {
                                chr.DebugMsg("meso : " + String.format("%05.2f%%", reward_prob) + " - " + reward.getMoney());
                            }
                        }
                    }
                }
                return true;
            }
            default -> {
            }
        }

        return false;
    }
}
