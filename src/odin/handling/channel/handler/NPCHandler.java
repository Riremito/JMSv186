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
import odin.constants.GameConstants;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsScriptMan;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.wrapper.WrapCUserLocal;
import tacos.packet.response.wrapper.WrapCUserRemote;
import odin.server.quest.MapleQuest;
import tacos.odin.OdinNPCConversationManager;
import tacos.script.TacosScriptNPC;
import tacos.script.TacosScriptQuest;

public class NPCHandler {

    public static void QuestAction(ClientPacket cp, MapleClient client) {
        MapleCharacter chr = client.getPlayer();

        byte action = cp.Decode1();
        int quest = cp.Decode2();

        // ?_?
        if (quest < 0) { //questid 50000 and above, WILL cast to negative, this was tested.
            quest += 65536; //probably not the best fix, but whatever
        }

        MapleQuest q = MapleQuest.getInstance(quest);
        switch (action) {
            case 0: { // Restore lost item
                chr.updateTick(cp.Decode4());
                int itemid = cp.Decode4();
                MapleQuest.getInstance(quest).RestoreLostItem(chr, itemid);
                break;
            }
            case 1: { // Start Quest
                int npc = cp.Decode4();
                q.start(chr, npc);
                break;
            }
            case 2: { // Complete Quest
                int npc = cp.Decode4();
                int selection = cp.Decode4();
                // ?_?
                if (selection != -1) {
                    q.complete(chr, npc, selection);
                } else {
                    q.complete(chr, npc);
                }
                // c.getSession().write(MaplePacketCreator.completeQuest(c.getPlayer(), quest));
                //c.getSession().write(MaplePacketCreator.updateQuestInfo(c.getPlayer(), quest, npc, (byte)14));
                // 6 = start quest
                // 7 = unknown error
                // 8 = equip is full
                // 9 = not enough mesos
                // 11 = due to the equipment currently being worn wtf o.o
                // 12 = you may not posess more than one of this item
                break;
            }
            case 3: { // Forefit Quest
                if (GameConstants.canForfeit(q.getId())) {
                    q.forfeit(chr);
                } else {
                    chr.dropMessage(1, "You may not forfeit this quest.");
                }
                break;
            }
            case 4: { // Scripted Start Quest
                int npc = cp.Decode4();
                short pos_x = cp.Decode2();
                short pos_y = cp.Decode2();
                TacosScriptQuest.getInstance().startQuest(client, npc, quest);
                break;
            }
            case 5: { // Scripted End Quest
                int npc = cp.Decode4();
                TacosScriptQuest.getInstance().endQuest(client, npc, quest, false);
                chr.SendPacket(WrapCUserLocal.EffectLocal(OpsUserEffect.UserEffect_QuestComplete));
                chr.getMap().broadcastMessage(chr, WrapCUserRemote.EffectRemote(OpsUserEffect.UserEffect_QuestComplete, chr), false);
                break;
            }
        }

        chr.DebugMsg("Quest ID = " + quest + ", Action = " + action);
    }

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
