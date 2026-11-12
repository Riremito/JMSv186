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
import odin.server.quest.MapleQuest;
import tacos.script.npc.NpcScriptTag;
import tacos.wz.QuestWz.QuestInfoData;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class DebugMan_Quest extends DebugNpcScriptMan {

    private final HashMap<Integer, QuestInfoData> quest_list = new HashMap<>();

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
                int npc_id = 1012003;
                QuestInfoData quest = this.quest_list.get(answer);
                if (quest == null) {
                    return false;
                }

                MapleQuest mq = MapleQuest.getInstance(quest.getId());
                mq.forceStart(getPlayer(), quest.getNpc(), null);
                //QuestMan.start(getPlayer(), npc_id, quest_id, true, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
