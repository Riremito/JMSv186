/*
 * Copyright (C) 2024 Riremito
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
package packet.server.response;

import client.MapleCharacter;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import config.ServerConfig;
import constants.GameConstants;
import handling.MaplePacket;
import java.sql.Timestamp;
import packet.server.ServerPacket;
import packet.server.response.struct.GW_CharacterStat;
import packet.server.response.struct.GW_ItemSlotBase;
import packet.server.response.struct.SecondaryStat;

/**
 *
 * @author Riremito
 */
public class ContextResponse {

    // CWvsContext::OnChangeSkillRecordResult
    public static final MaplePacket updateSkill(int skillid, int level, int masterlevel, long expiration) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ChangeSkillRecordResult);
        sp.Encode1(1);
        sp.Encode2(1);
        sp.Encode4(skillid);
        sp.Encode4(level);
        sp.Encode4(masterlevel);
        if ((ServerConfig.IsJMS() && 164 <= ServerConfig.GetVersion()) || ServerConfig.IsKMS()) {
            sp.Encode8((Timestamp.valueOf("2027-07-07 07:00:00").getTime() + Timestamp.valueOf("2339-01-01 18:00:00").getTime()) * 10000);
        }
        sp.Encode1(4);
        return sp.Get();
    }

    // CWvsContext::OnTemporaryStatSet
    public static final MaplePacket TemporaryStatSet(MapleCharacter chr, int skill_id) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_TemporaryStatSet);
        // SecondaryStat::DecodeForLocal
        p.EncodeBuffer(SecondaryStat.EncodeForLocal(chr, skill_id));
        p.Encode2(0); // delay
        p.Encode1(0);
        return p.Get();
    }

    // CWvsContext::OnInventoryOperation
    public static MaplePacket addInventorySlot(MapleInventoryType type, IItem item) {
        return addInventorySlot(type, item, false);
    }

    public static MaplePacket addInventorySlot(MapleInventoryType type, IItem item, boolean fromDrop) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_InventoryOperation);
        sp.Encode1(fromDrop ? 1 : 0);
        sp.Encode1(1); // add mode
        sp.Encode1(0);
        sp.Encode1(type.getType()); // iv type
        sp.Encode2(item.getPosition()); // v131-v194
        sp.EncodeBuffer(GW_ItemSlotBase.Encode(item));
        return sp.Get();
    }

    public static MaplePacket scrolledItem(IItem scroll, IItem item, boolean destroyed, boolean potential) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_InventoryOperation);
        sp.Encode1(1); // fromdrop always true
        sp.Encode1(destroyed ? 2 : 3);
        sp.Encode1(scroll.getQuantity() > 0 ? 1 : 3);
        sp.Encode1(GameConstants.getInventoryType(scroll.getItemId()).getType()); //can be cash
        sp.Encode2(scroll.getPosition());
        if (scroll.getQuantity() > 0) {
            sp.Encode2(scroll.getQuantity());
        }
        sp.Encode1(3);
        if (!destroyed) {
            sp.Encode1(MapleInventoryType.EQUIP.getType());
            sp.Encode2(item.getPosition());
            sp.Encode1(0);
        }
        sp.Encode1(MapleInventoryType.EQUIP.getType());
        sp.Encode2(item.getPosition());
        if (!destroyed) {
            sp.EncodeBuffer(GW_ItemSlotBase.Encode(item));
        }
        sp.Encode1(1);
        return sp.Get();
    }

    // CWvsContext::OnInventoryGrow
    // CWvsContext::OnStatChanged
    public static final MaplePacket StatChanged(MapleCharacter chr, int unlock, int statmask) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_StatChanged);
        // 0 = lock   -> do not clear lock flag
        // 1 = unlock -> clear lock flag
        sp.Encode1(unlock); // CWvsContext->bExclRequestSent
        sp.EncodeBuffer(GW_CharacterStat.EncodeChangeStat(chr, statmask));
        if (ServerConfig.IsPreBB()) {
            // Pet
            if ((statmask & GW_CharacterStat.Flag.PET1.get()) > 0) {
                int v5 = 0; // CVecCtrlUser::AddMovementInfo
                sp.Encode1(v5);
            }
        } else {
            // v188+
            sp.Encode1(0); // not 0 -> Encode1
            sp.Encode1(0); // not 0 -> Encode4, Encode4
        }
        return sp.Get();
    }

}
