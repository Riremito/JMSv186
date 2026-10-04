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
import java.util.List;
import odin.client.MapleCharacter;
import odin.server.life.MapleMonsterInformationProvider;
import odin.server.life.MonsterDropEntry;
import tacos.debug.DebugLogger;
import tacos.server.map.TacosReward;
import tacos.server.map.TacosSpawnPoint;
import tacos.wz.MapleData;
import tacos.wz.WzDataStorage;
import tacos.wz.WzDataTool;
import tacos.wz.WzNameStorage;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class TacosCommandInfo {

    public static boolean executeCommand(TacosCommander dcmd, MapleCharacter chr) {

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
            case "/mobtime" -> {
                for (TacosSpawnPoint sp : chr.getMap().getMonsterSpawnPoint()) {
                    if (sp.getMobTime() != 0) {
                        chr.DebugMsg(String.format("mobtime : %.1f hours.", (double) sp.getMobTime() / 1000 / 3600));
                    }
                }
                return true;
            }
            case "/dropinfo" -> {
                ArrayList<Integer> mob_ids = new ArrayList<>();
                for (TacosSpawnPoint sp : chr.getMap().getMonsterSpawnPoint()) {
                    int mob_id = sp.getId();
                    if (!mob_ids.contains(mob_id)) {
                        mob_ids.add(mob_id);
                    }
                }
                for (int mob_id : mob_ids) {
                    chr.DebugMsg("[" + mob_id + " - " + WzNameStorage.MOB.get(mob_id).getName() + "]");
                    for (TacosReward.Reward reward : TacosReward.getRewardData(mob_id)) {
                        if (reward.item != 0) {
                            chr.DebugMsgItem(reward.item + " : " + String.format("%05.2f%%", reward.prob * 100.0 / TacosReward.PROB_MAX) + " - " + WzNameStorage.ITEM.get(reward.item).getName(), reward.item);
                        } else {
                            chr.DebugMsg("meso : " + String.format("%05.2f%%", reward.prob * 100.0 / TacosReward.PROB_MAX) + " - " + reward.money);
                        }
                    }
                }
                return true;
            }
            case "/mapinfo" -> {
                checkMapData(chr);
                return true;
            }
            default -> {
            }
        }

        return false;
    }

    private static boolean checkMapData(MapleCharacter chr) {
        List<Integer> mob_ids = new ArrayList<>();
        List<Integer> mob_counts = new ArrayList<>();
        for (TacosSpawnPoint sp : chr.getMap().getMonsterSpawnPoint()) {
            int id = sp.getId();
            int index = mob_ids.indexOf(id);
            if (index != -1) {
                mob_counts.set(index, mob_counts.get(index) + 1);
                continue;
            }
            mob_ids.add(id);
            mob_counts.add(1);
        }

        for (int i = 0; i < mob_ids.size(); i++) {
            int mob_id = mob_ids.get(i);
            int mob_count = mob_counts.get(i);
            MapleData md_mob = WzXML.STRING.getMob().getChildByPath(Integer.toString(mob_id));
            String mob_name = md_mob != null ? WzDataTool.getString(md_mob.getChildByPath("name"), "NO_NAME") : "NO_NAME";
            if (!WzDataStorage.MOB.check(mob_id)) {
                chr.DebugMsg2("[" + mob_id + " (" + mob_count + ") : \"" + mob_name + "\" ]");
                continue;
            }
            chr.DebugMsg("[" + mob_id + " (" + mob_count + ") : \"" + mob_name + "\" ]");
            for (MonsterDropEntry mde : MapleMonsterInformationProvider.getInstance().retrieveDrop(mob_id)) {
                chr.DebugMsg(mde.itemId + " : " + mde.chance);
            }
        }

        return true;
    }
}
