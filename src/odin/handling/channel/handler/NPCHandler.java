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

import java.util.LinkedHashMap;
import odin.client.inventory.Equip;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import odin.client.MapleClient;
import odin.client.MapleCharacter;
import odin.constants.GameConstants;
import odin.client.RockPaperScissors;
import java.util.Map;
import java.util.Map.Entry;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsScriptMan;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.ResCRPSGameDlg;
import tacos.packet.response.wrapper.WrapCUserLocal;
import tacos.packet.response.wrapper.WrapCUserRemote;
import odin.server.life.MapleNPC;
import odin.server.quest.MapleQuest;
import tacos.odin.OdinNPCConversationManager;
import odin.server.MapleItemInformationProvider;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.script.TacosScriptNPC;
import tacos.script.TacosScriptQuest;

public class NPCHandler {

    public static void NPCTalk(MapleClient client, MapleCharacter chr, int npc_oid) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        MapleNPC npc = chr.getMap().getNPCByOid(npc_oid);

        if (npc == null) {
            return;
        }
        if (chr.getConversation() != 0) {
            chr.DebugMsg("NPCTalk = err " + chr.getConversation());
            return;
        }

        if (npc.hasShop()) {
            chr.setConversation(1);
            npc.sendShop(client);
        } else {
            TacosScriptNPC.getInstance().start(client, npc.getId());
        }
    }

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

    public static void repairAll(MapleClient client) {
        MapleCharacter chr = client.getPlayer();
        if (chr.getMapId() != 240000000) {
            return;
        }
        Equip eq;
        double rPercentage;
        int price = 0;
        Map<String, Integer> eqStats;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Map<Equip, Integer> eqs = new LinkedHashMap<>();
        MapleInventoryType[] types = {MapleInventoryType.EQUIP, MapleInventoryType.EQUIPPED};
        for (MapleInventoryType type : types) {
            for (IItem item : chr.getInventory(type)) {
                if (item instanceof Equip) { //redundant
                    eq = (Equip) item;
                    if (eq.getDurability() >= 0) {
                        eqStats = ii.getEquipStats(eq.getItemId());
                        if (eqStats.get("durability") > 0 && eq.getDurability() < eqStats.get("durability")) {
                            rPercentage = (100.0 - Math.ceil((eq.getDurability() * 1000.0) / (eqStats.get("durability") * 10.0)));
                            eqs.put(eq, eqStats.get("durability"));
                            price += (int) Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0 : 1.0));
                        }
                    }
                }
            }
        }
        if (eqs.size() <= 0 || chr.getMeso() < price) {
            return;
        }
        chr.gainMeso(-price, true);
        Equip ez;
        for (Entry<Equip, Integer> eqqz : eqs.entrySet()) {
            ez = eqqz.getKey();
            ez.setDurability(eqqz.getValue());
            client.SendPacket(ResWrapper.addInventorySlot(ez.getPosition() < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, ez.copy()));
        }
    }

    public static void repair(ClientPacket cp, MapleClient client) {
        MapleCharacter chr = client.getPlayer();
        if (chr.getMapId() != 240000000/* || slea.available() < 4*/) { //leafre for now
            return;
        }
        int position = cp.Decode4(); //who knows why this is a int
        MapleInventoryType type = position < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
        IItem item = chr.getInventory(type).getItem((byte) position);
        if (item == null) {
            return;
        }
        Equip eq = (Equip) item;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Map<String, Integer> eqStats = ii.getEquipStats(item.getItemId());
        if (eq.getDurability() < 0 || eqStats.get("durability") <= 0 || eq.getDurability() >= eqStats.get("durability")) {
            return;
        }
        double rPercentage = (100.0 - Math.ceil((eq.getDurability() * 1000.0) / (eqStats.get("durability") * 10.0)));
        //drpq level 105 weapons - ~420k per %; 2k per durability point
        //explorer level 30 weapons - ~10 mesos per %
        int price = (int) Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0 : 1.0)); // / 100 for level 30?
        //TODO: need more data on calculating off client
        if (chr.getMeso() < price) {
            return;
        }
        chr.gainMeso(-price, false);
        eq.setDurability(eqStats.get("durability"));
        client.SendPacket(ResWrapper.addInventorySlot(type, eq.copy()));
    }

    public static void RPSGame(ClientPacket cp, MapleClient client) {
        MapleCharacter chr = client.getPlayer();
        if (!chr.getMap().containsNPC(9000019)) {
            if (chr.getRPS() != null) {
                chr.getRPS().dispose(client);
            }
            return;
        }
        byte mode = cp.Decode1();
        switch (mode) {
            case 0: //start game
            case 5: //retry
                if (chr.getRPS() != null) {
                    chr.getRPS().reward(client);
                }
                if (chr.getMeso() >= 1000) {
                    chr.setRPS(new RockPaperScissors(client, mode));
                } else {
                    client.SendPacket(ResCRPSGameDlg.getRPSMode((byte) 0x08, -1, -1, -1));
                }
                break;
            case 1: //answer
                if (chr.getRPS() == null || !chr.getRPS().answer(client, cp.Decode1())) {
                    client.SendPacket(ResCRPSGameDlg.getRPSMode((byte) 0x0D, -1, -1, -1));
                }
                break;
            case 2: //time over
                if (chr.getRPS() == null || !chr.getRPS().timeOut(client)) {
                    client.SendPacket(ResCRPSGameDlg.getRPSMode((byte) 0x0D, -1, -1, -1));
                }
                break;
            case 3: //continue
                if (chr.getRPS() == null || !chr.getRPS().nextRound(client)) {
                    client.SendPacket(ResCRPSGameDlg.getRPSMode((byte) 0x0D, -1, -1, -1));
                }
                break;
            case 4: //leave
                if (chr.getRPS() != null) {
                    chr.getRPS().dispose(client);
                } else {
                    client.SendPacket(ResCRPSGameDlg.getRPSMode((byte) 0x0D, -1, -1, -1));
                }
                break;
        }
    }
}
