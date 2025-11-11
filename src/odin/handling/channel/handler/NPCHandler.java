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

import odin.client.inventory.Equip;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import odin.client.MapleClient;
import odin.client.MapleCharacter;
import odin.constants.GameConstants;
import odin.client.MapleQuestStatus;
import odin.client.RockPaperScissors;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import packet.ClientPacket;
import packet.ops.OpsScriptMan;
import packet.ops.OpsUserEffect;
import packet.response.ResCRPSGameDlg;
import packet.response.wrapper.WrapCUserLocal;
import packet.response.wrapper.WrapCUserRemote;
import odin.server.MapleInventoryManipulator;
import odin.server.life.MapleNPC;
import odin.server.quest.MapleQuest;
import odin.scripting.NPCScriptManager;
import odin.scripting.NPCConversationManager;
import odin.server.MapleItemInformationProvider;
import odin.tools.ArrayMap;
import odin.tools.Pair;
import odin.tools.data.input.SeekableLittleEndianAccessor;

public class NPCHandler {

    public static final void NPCTalk(MapleClient c, final MapleCharacter chr, int npc_oid) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        final MapleNPC npc = chr.getMap().getNPCByOid(npc_oid);

        if (npc == null) {
            return;
        }
        if (chr.getConversation() != 0) {
            chr.dropMessage(-1, "You already are talking to an NPC. Use @ea if this is not intended.");
            return;
        }

        if (npc.hasShop()) {
            chr.setConversation(1);
            npc.sendShop(c);
        } else {
            NPCScriptManager.getInstance().start(c, npc.getId());
        }
    }

    public static final void QuestAction(ClientPacket cp, MapleClient c) {
        MapleCharacter chr = c.getPlayer();

        byte action = cp.Decode1();
        int quest = cp.Decode2();

        // ?_?
        if (quest < 0) { //questid 50000 and above, WILL cast to negative, this was tested.
            quest += 65536; //probably not the best fix, but whatever
        }

        final MapleQuest q = MapleQuest.getInstance(quest);
        switch (action) {
            case 0: { // Restore lost item
                chr.updateTick(cp.Decode4());
                final int itemid = cp.Decode4();
                MapleQuest.getInstance(quest).RestoreLostItem(chr, itemid);
                break;
            }
            case 1: { // Start Quest
                final int npc = cp.Decode4();
                q.start(chr, npc);
                break;
            }
            case 2: { // Complete Quest
                final int npc = cp.Decode4();
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
                final int npc = cp.Decode4();
                short pos_x = cp.Decode2();
                short pos_y = cp.Decode2();
                NPCScriptManager.getInstance().startQuest(c, npc, quest);
                break;
            }
            case 5: { // Scripted End Quest
                final int npc = cp.Decode4();
                NPCScriptManager.getInstance().endQuest(c, npc, quest, false);
                chr.SendPacket(WrapCUserLocal.EffectLocal(OpsUserEffect.UserEffect_QuestComplete));
                chr.getMap().broadcastMessage(chr, WrapCUserRemote.EffectRemote(OpsUserEffect.UserEffect_QuestComplete, chr), false);
                break;
            }
        }

        chr.DebugMsg("Quest ID = " + quest + ", Action = " + action);
    }

    public static final void NPCMoreTalk(MapleClient c, OpsScriptMan smt, byte action, int selection, String text) {
        final NPCConversationManager cm = NPCScriptManager.getInstance().getCM(c);
        byte lastMsg = (byte) smt.get();

        if (cm == null || c.getPlayer().getConversation() == 0 || cm.getLastMsg() != lastMsg) {
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
                    NPCScriptManager.getInstance().startQuest(c, action, lastMsg, -1);
                } else if (cm.getType() == 1) {
                    NPCScriptManager.getInstance().endQuest(c, action, lastMsg, -1);
                } else {
                    NPCScriptManager.getInstance().action(c, action, lastMsg, -1);
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
                NPCScriptManager.getInstance().startQuest(c, action, lastMsg, selection);
            } else if (cm.getType() == 1) {
                NPCScriptManager.getInstance().endQuest(c, action, lastMsg, selection);
            } else {
                NPCScriptManager.getInstance().action(c, action, lastMsg, selection);
            }
            return;
        }

        cm.dispose();
        return;
    }

    public static final void repairAll(final MapleClient c) {
        if (c.getPlayer().getMapId() != 240000000) {
            return;
        }
        Equip eq;
        double rPercentage;
        int price = 0;
        Map<String, Integer> eqStats;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Map<Equip, Integer> eqs = new ArrayMap<Equip, Integer>();
        final MapleInventoryType[] types = {MapleInventoryType.EQUIP, MapleInventoryType.EQUIPPED};
        for (MapleInventoryType type : types) {
            for (IItem item : c.getPlayer().getInventory(type)) {
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
        if (eqs.size() <= 0 || c.getPlayer().getMeso() < price) {
            return;
        }
        c.getPlayer().gainMeso(-price, true);
        Equip ez;
        for (Entry<Equip, Integer> eqqz : eqs.entrySet()) {
            ez = eqqz.getKey();
            ez.setDurability(eqqz.getValue());
            c.getPlayer().forceReAddItem(ez.copy(), ez.getPosition() < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
        }
    }

    public static final void repair(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().getMapId() != 240000000 || slea.available() < 4) { //leafre for now
            return;
        }
        final int position = slea.readInt(); //who knows why this is a int
        final MapleInventoryType type = position < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
        final IItem item = c.getPlayer().getInventory(type).getItem((byte) position);
        if (item == null) {
            return;
        }
        final Equip eq = (Equip) item;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Map<String, Integer> eqStats = ii.getEquipStats(item.getItemId());
        if (eq.getDurability() < 0 || eqStats.get("durability") <= 0 || eq.getDurability() >= eqStats.get("durability")) {
            return;
        }
        final double rPercentage = (100.0 - Math.ceil((eq.getDurability() * 1000.0) / (eqStats.get("durability") * 10.0)));
        //drpq level 105 weapons - ~420k per %; 2k per durability point
        //explorer level 30 weapons - ~10 mesos per %
        final int price = (int) Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0 : 1.0)); // / 100 for level 30?
        //TODO: need more data on calculating off client
        if (c.getPlayer().getMeso() < price) {
            return;
        }
        c.getPlayer().gainMeso(-price, false);
        eq.setDurability(eqStats.get("durability"));
        c.getPlayer().forceReAddItem(eq.copy(), type);
    }

    public static void UpdateQuest(MapleCharacter chr, ClientPacket cp) {
        short quest_id = cp.Decode2();
        final MapleQuest quest = MapleQuest.getInstance(quest_id);
        if (quest != null) {
            chr.updateQuest(chr.getQuest(quest), true);
        }
    }

    public static void UseItemQuest(MapleCharacter chr, ClientPacket cp) {
        short slot = cp.Decode2();
        int itemId = cp.Decode4();
        IItem item = chr.getInventory(MapleInventoryType.ETC).getItem(slot);
        short qid = cp.Decode2();
        short unk = cp.Decode2();

        final MapleQuest quest = MapleQuest.getInstance(qid);
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Pair<Integer, List<Integer>> questItemInfo = null;
        boolean found = false;
        for (IItem i : chr.getInventory(MapleInventoryType.ETC)) {
            if (i.getItemId() / 10000 == 422) {
                questItemInfo = ii.questItemInfo(i.getItemId());
                if (questItemInfo != null && questItemInfo.getLeft() == qid && questItemInfo.getRight().contains(itemId)) {
                    found = true;
                    break; //i believe it's any order
                }
            }
        }
        if (quest != null && found && item != null && item.getQuantity() > 0 && item.getItemId() == itemId) {
            final int newData = cp.Decode4();
            final MapleQuestStatus stats = chr.getQuestNoAdd(quest);
            if (stats != null && stats.getStatus() == 1) {
                stats.setCustomData(String.valueOf(newData));
                chr.updateQuest(stats, true);
                MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.ETC, slot, (short) 1, false);
            }
        }
    }

    public static final void RPSGame(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (slea.available() == 0 || !c.getPlayer().getMap().containsNPC(9000019)) {
            if (c.getPlayer().getRPS() != null) {
                c.getPlayer().getRPS().dispose(c);
            }
            return;
        }
        final byte mode = slea.readByte();
        switch (mode) {
            case 0: //start game
            case 5: //retry
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().reward(c);
                }
                if (c.getPlayer().getMeso() >= 1000) {
                    c.getPlayer().setRPS(new RockPaperScissors(c, mode));
                } else {
                    c.getSession().write(ResCRPSGameDlg.getRPSMode((byte) 0x08, -1, -1, -1));
                }
                break;
            case 1: //answer
                if (c.getPlayer().getRPS() == null || !c.getPlayer().getRPS().answer(c, slea.readByte())) {
                    c.getSession().write(ResCRPSGameDlg.getRPSMode((byte) 0x0D, -1, -1, -1));
                }
                break;
            case 2: //time over
                if (c.getPlayer().getRPS() == null || !c.getPlayer().getRPS().timeOut(c)) {
                    c.getSession().write(ResCRPSGameDlg.getRPSMode((byte) 0x0D, -1, -1, -1));
                }
                break;
            case 3: //continue
                if (c.getPlayer().getRPS() == null || !c.getPlayer().getRPS().nextRound(c)) {
                    c.getSession().write(ResCRPSGameDlg.getRPSMode((byte) 0x0D, -1, -1, -1));
                }
                break;
            case 4: //leave
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().dispose(c);
                } else {
                    c.getSession().write(ResCRPSGameDlg.getRPSMode((byte) 0x0D, -1, -1, -1));
                }
                break;
        }
    }
}
