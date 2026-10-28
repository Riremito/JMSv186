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

import tacos.client.TacosClient;
import tacos.client.TacosCharacter;
import odin.client.inventory.Equip;
import odin.client.inventory.MapleInventoryType;
import tacos.constants.TacosConstants;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ops.OpsGoldHammer;
import tacos.packet.response.ResCUIGoldHammer;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_InvOp;
import tacos.server.TacosEnhancement;

/**
 *
 * @author Riremito
 */
public class ReqCUIGoldHammer {

    public static boolean OnPacket(TacosClient client, ClientPacketHeader header, ClientPacket cp) {
        TacosCharacter chr = client.getPlayer();
        if (chr == null) {
            return false;
        }

        switch (header) {
            case CP_GoldHammerRequest: {
                OnGoldHammerRequest(chr, cp);
                return true;
            }
            case CP_GoldHammerComplete: {
                OnGoldHammerComplete(chr, cp);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnGoldHammerRequest(TacosCharacter chr, ClientPacket cp) {
        int update_time = cp.Decode4();
        int nPOS = cp.Decode4();
        int nItemID = cp.Decode4();
        int m_nItemTI = cp.Decode4();
        int m_nSlotPosition = cp.Decode4();

        if (!TacosConstants.is_gold_hammer(nItemID)) {
            return false;
        }
        Runnable item_use = chr.checkItemSlot((short) nPOS, nItemID);
        if (item_use == null) {
            return false;
        }
        Equip equip = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem((short) m_nSlotPosition);
        if (equip == null) {
            return false;
        }

        TacosEnhancement enhancement = new TacosEnhancement(equip);
        boolean success = enhancement.useViciousHammer();
        item_use.run();
        chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.EQUIP, equip).build()));
        chr.SendPacket(ResCUIGoldHammer.GoldHammerResult(success ? OpsGoldHammer.GoldHammerRes_Success : OpsGoldHammer.GoldHammerRes_Fail, equip));
        return true;
    }

    public static boolean OnGoldHammerComplete(TacosCharacter chr, ClientPacket cp) {
        int unk1 = cp.Decode4();
        int unk2 = cp.Decode4();

        chr.SendPacket(ResCUIGoldHammer.GoldHammerResult(OpsGoldHammer.GoldHammerRes_Done, null));
        return true;
    }

}
