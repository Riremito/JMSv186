/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.handling.channel.handler;

import odin.client.MapleClient;
import odin.client.MapleCharacter;
import tacos.packet.ops.OpsScriptMan;
import tacos.odin.OdinNPCConversationManager;
import tacos.script.TacosScriptNPC;
import tacos.script.TacosScriptQuest;

public class NPCHandler {

    public static void NPCMoreTalk(MapleClient client, OpsScriptMan smt, int action, int selection, String text) {
        MapleCharacter chr = client.getPlayer();
        OdinNPCConversationManager cm = TacosScriptNPC.getInstance().getCM(client);
        if (cm == null) {
            cm = TacosScriptQuest.getInstance().getCM(client);
        }
        byte lastMsg = (byte) smt.get();

        if (cm == null || chr.getConversation() == 0 || cm.getLastMsg() != lastMsg) {
            return;
        }

        if (action == 0) {
            selection = -1;
        }

        cm.setLastMsg(-1);

        if (smt == OpsScriptMan.SM_ASKTEXT) {
            if (action != 0) {
                cm.setGetText(text);
                if (cm.getType() == 0) {
                    TacosScriptQuest.getInstance().startQuest(client, action, lastMsg, -1);
                } else if (cm.getType() == 1) {
                    TacosScriptQuest.getInstance().endQuest(client, action, lastMsg, -1);
                } else {
                    TacosScriptNPC.getInstance().action(client, action, lastMsg, -1);
                }
            } else {
                cm.dispose();
            }
            return;
        }

        if (selection == -1 && OpsScriptMan.SM_ASKTEXT.get() <= lastMsg && smt != OpsScriptMan.SM_ASKACCEPT) {
            cm.dispose();
            return;
        }

        if (selection >= -1 && action != -1) {
            if (cm.getType() == 0) {
                TacosScriptQuest.getInstance().startQuest(client, action, lastMsg, selection);
            } else if (cm.getType() == 1) {
                TacosScriptQuest.getInstance().endQuest(client, action, lastMsg, selection);
            } else {
                TacosScriptNPC.getInstance().action(client, action, lastMsg, selection);
            }
            return;
        }

        cm.dispose();
        return;
    }
}
