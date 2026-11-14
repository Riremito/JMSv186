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
package tacos.debug;

import java.util.HashMap;
import java.util.List;
import odin.server.MapleInventoryManipulator;
import odin.server.quest.MapleQuest;
import tacos.script.npc.NpcScriptTag;
import tacos.server.map.TacosMap;
import tacos.server.map.TacosPortal;
import tacos.server.map.object.TacosNpc;
import tacos.wz.QuestWz.Node1_Item;
import tacos.wz.QuestWz.QuestInfoData;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class DebugMan_Quest extends DebugNpcScriptMan {

    private final HashMap<Integer, QuestInfoData> quest_list = new HashMap<>();
    private List<Integer> npc_location_map_ids;
    private int npc_id = 0;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                int index = 0;
                for (QuestInfoData quest_data : WzXML.QUEST.getAllQuests().values()) {
                    this.quest_list.put(index, quest_data);
                    nt.addMenu(index, quest_data.getId() + " : " + quest_data.getName());
                    index++;
                    if (120 < index) {
                        break;
                    }
                }
                if (index == 0) {
                    say("no quest.");
                    return false;
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                QuestInfoData quest = this.quest_list.get(answer);
                if (quest == null) {
                    return false;
                }

                MapleQuest mq = MapleQuest.getInstance(quest.getId());
                getPlayer().DebugMsg("quest id : " + quest.getId());
                mq.forceStart(getPlayer(), quest.getNpc(), null);
                if (!quest.getNode1_items().isEmpty()) {
                    for (Node1_Item n1i : quest.getNode1_items()) {
                        MapleInventoryManipulator.addById(getPlayer(), n1i.getId(), n1i.getCount());
                    }
                }
                this.npc_id = quest.getNode1_npc();
                this.npc_location_map_ids = WzXML.ETC.getNpcLocationMapIds(quest.getNode1_npc());
                if (this.npc_location_map_ids.isEmpty()) {
                    getPlayer().DebugMsg("npc id : " + quest.getNode1_npc() + ", not found in NpcLocation");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                for (int index = 0; index < this.npc_location_map_ids.size(); index++) {
                    nt.add("teleport to npc location?");
                    nt.addMenu(index, this.npc_location_map_ids.get(index) + " : #m" + this.npc_location_map_ids.get(index) + "#");
                }
                askMenu(nt);
                //QuestMan.start(getPlayer(), npc_id, quest_id, true, false);
                return true;
            }
            case 2 -> {
                if (answer < 0 || this.npc_location_map_ids.size() <= answer) {
                    return false;
                }
                TacosMap map = findMap(this.npc_location_map_ids.get(answer));
                if (map != null) {
                    TacosNpc npc = map.getNpcById(this.npc_id);
                    if (npc != null) {
                        TacosPortal portal = map.findSpawnPoint(npc.getPosition());
                        warp(this.npc_location_map_ids.get(answer), portal.getName());
                    } else {
                        warp(this.npc_location_map_ids.get(answer));
                    }
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
