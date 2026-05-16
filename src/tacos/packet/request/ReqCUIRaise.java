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
package tacos.packet.request;

import java.util.List;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.MapleQuestStatus;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.maps.MapleMap;
import odin.server.quest.MapleQuest;
import tacos.debug.DebugLogger;
import tacos.odin.OdinPair;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;
import tacos.packet.response.wrapper.ResWrapper;

/**
 *
 * @author Riremito
 */
public class ReqCUIRaise {

    public static boolean OnPacket(MapleClient client, ClientPacketHeader header, ClientPacket cp) {
        MapleCharacter chr = client.getPlayer();

        if (chr == null) {
            return true;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return true;
        }

        switch (header) {
            case CP_RaiseRefesh: {
                OnRaiseRefesh(chr, cp);
                return true;
            }
            case CP_RaiseUIState: {
                OnRaiseUIState(chr, cp);
                return true;
            }
            case CP_RaiseIncExp: {
                OnRaiseIncExp(chr, cp);
                return true;
            }
            case CP_RaiseAddPiece: {
                OnRaiseAddPiece(chr, cp);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // CUIRaiseWndBase::OnCreate
    public static boolean OnRaiseRefesh(MapleCharacter chr, ClientPacket cp) {
        short nQuestID = cp.Decode2();
        int uQuestID = Short.toUnsignedInt(nQuestID);

        MapleQuest quest = MapleQuest.getInstance(uQuestID);
        if (quest == null) {
            DebugLogger.ErrorLog("OnRaiseRefesh : nQuestID = " + uQuestID);
            return false;
        }
        MapleQuestStatus quest_status = chr.getQuest(quest);
        if (quest_status == null) {
            DebugLogger.ErrorLog("OnRaiseRefesh : nQuestID = " + uQuestID + ", status");
            return false;
        }

        chr.SendPacket(ResWrapper.updateQuest(quest_status));
        chr.DebugMsg("OnRaiseRefesh : nQuestID = " + uQuestID);
        return true;
    }

    // CUIRaiseWndBase::CUIRaiseWndBase
    // CUIRaiseWndBase::~CUIRaiseWndBase
    public static boolean OnRaiseUIState(MapleCharacter chr, ClientPacket cp) {
        int nQuestID = cp.Decode4();
        byte state = cp.Decode1(); // open or close.

        MapleQuest quest = MapleQuest.getInstance(nQuestID);

        if (quest == null) {
            DebugLogger.ErrorLog("OnRaiseUIState : nQuestID = " + nQuestID);
            return false;
        }

        if (state != 0) {
            chr.DebugMsg("OnRaiseUIState : nQuestID = " + nQuestID);
            if (chr.getQuestStatus(nQuestID) == 0) {
                chr.setQuestAdd(quest, (byte) 1, null);
            }
        }

        return true;
    }

    // CUIRaiseWnd::SendPutItem
    public static boolean OnRaiseIncExp(MapleCharacter chr, ClientPacket cp) {
        // KMST330 & JMS187
        short nSlotPosition = cp.Decode2();
        int nItemID = cp.Decode4();
        int nQuestID = cp.Decode4();
        int exp = cp.Decode4();

        IItem item_dropped = chr.getInventory(MapleInventoryType.ETC).getItem(nSlotPosition);
        if (item_dropped.getItemId() != nItemID) {
            return false;
        }

        MapleQuest quest = MapleQuest.getInstance(nQuestID);
        if (quest == null) {
            DebugLogger.ErrorLog("OnRaiseIncExp : nQuestID = " + nQuestID);
            return false;
        }
        MapleQuestStatus quest_status = chr.getQuest(quest);
        if (quest_status == null || quest_status.getStatus() != 1) {
            DebugLogger.ErrorLog("OnRaiseIncExp : nQuestID = " + nQuestID + ", status");
            return false;
        }

        chr.DebugMsg("OnRaiseIncExp : nItemID = " + nItemID + ", nQuestID = " + nQuestID + ", exp = " + exp);

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (IItem item : chr.getInventory(MapleInventoryType.ETC)) {
            if (item.getItemId() / 10000 != 422) {
                continue;
            }
            OdinPair<Integer, List<Integer>> questItemInfo = ii.questItemInfo(item.getItemId());
            if (questItemInfo == null) {
                continue;
            }
            if (questItemInfo.getLeft() != nQuestID) {
                continue;
            }
            if (!questItemInfo.getRight().contains(nItemID)) {
                continue;
            }
            // found.
            int quest_value = Integer.parseInt(quest_status.getCustomData()) + exp;
            quest_status.setCustomData(String.valueOf(quest_value));
            chr.updateQuest(quest_status, true);
            MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.ETC, nSlotPosition, (short) 1, false);
            chr.updateInv();
            return true;
        }

        return false;
    }

    // CUIRaisePieceWnd::SendPutItem
    public static boolean OnRaiseAddPiece(MapleCharacter chr, ClientPacket cp) {
        // KMST330 & JMS187
        short nSlotPosition = cp.Decode2();
        int nItemID = cp.Decode4();
        int nQuestID = cp.Decode4();
        int EnableDropItemIndex = cp.Decode4(); // flags.

        chr.DebugMsg("OnRaiseAddPiece : nItemID = " + nItemID + ", nQuestID = " + nQuestID);
        return true;
    }
}
