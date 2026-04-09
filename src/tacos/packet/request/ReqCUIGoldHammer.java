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

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.inventory.Equip;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.MapleItemInformationProvider;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ops.OpsGoldHammer;
import tacos.packet.response.ResCUIGoldHammer;
import tacos.packet.response.wrapper.ResWrapper;

/**
 *
 * @author Riremito
 */
public class ReqCUIGoldHammer {

    public static boolean OnPacket(MapleClient client, ClientPacketHeader header, ClientPacket cp) {
        MapleCharacter chr = client.getPlayer();
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

    public static boolean OnGoldHammerRequest(MapleCharacter chr, ClientPacket cp) {
        int update_time = cp.Decode4();
        int nPOS = cp.Decode4();
        int nItemID = cp.Decode4();
        int m_nItemTI = cp.Decode4();
        int m_nSlotPosition = cp.Decode4();

        // 2470000
        if ((nItemID / 10000) != 247) {
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
        if (!GameConstants.canHammer(equip.getItemId()) || MapleItemInformationProvider.getInstance().getSlots(equip.getItemId()) <= 0) {
            return false;
        }
        if (2 <= equip.getViciousHammer()) {
            // max count in KMS is 1.
            return false;
        }
        equip.setViciousHammer(equip.getViciousHammer() + 1);
        equip.setUpgradeSlots(equip.getUpgradeSlots() + 1);
        item_use.run();
        chr.SendPacket(ResWrapper.addInventorySlot(MapleInventoryType.EQUIP, equip));
        chr.SendPacket(ResCUIGoldHammer.GoldHammerResult(OpsGoldHammer.GoldHammerRes_Success));
        return true;
    }

    public static boolean OnGoldHammerComplete(MapleCharacter chr, ClientPacket cp) {
        int unk1 = cp.Decode4();
        int unk2 = cp.Decode4();

        chr.SendPacket(ResCUIGoldHammer.GoldHammerResult(OpsGoldHammer.GoldHammerRes_Done));
        return true;
    }

}
