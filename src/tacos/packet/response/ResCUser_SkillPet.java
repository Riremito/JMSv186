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
package tacos.packet.response;

import odin.client.MapleCharacter;
import tacos.network.MaplePacket;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.request.parse.ParseCMovePath;

/**
 *
 * @author Riremito
 */
public class ResCUser_SkillPet {

    // CSkillPet::OnMove
    public static MaplePacket SkillPetMove(MapleCharacter chr, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SkillPetMove);

        sp.Encode4(chr.getId()); // m_dwCharacterID
        sp.Encode4(chr.getId()); // pet id
        sp.EncodeBuffer(data.get());
        return sp.get();
    }

    // CSkillPet::OnAction
    // CSkillPet::OnState
    // CUserLocal::OnSkillPetTrensferField
    public static MaplePacket SkillPetTransferField(MapleCharacter chr, int skill_pet_id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SkillPetTransferField);

        sp.Encode4(chr.getId()); // m_dwCharacterID
        sp.Encode4(chr.getId()); // pet id
        sp.EncodeBuffer(CSkillPet__Init(chr, skill_pet_id));
        return sp.get();
    }

    public static byte[] CSkillPet__Init(MapleCharacter chr, int skill_pet_id) {
        ServerPacket data = new ServerPacket();

        data.Encode4(skill_pet_id); // nSkillID (haku)
        data.Encode1(1); // eState (show)
        data.Encode2(chr.getPosition().x); // m_ptPos.x
        data.Encode2(chr.getPosition().y); // m_ptPos.y
        data.Encode1(0); // m_nMoveAction
        data.Encode2(0); // sFootholdSN
        return data.get().getBytes();
    }
}
