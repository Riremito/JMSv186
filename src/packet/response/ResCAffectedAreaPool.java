/*
 * Copyright (C) 2025 Riremito
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
package packet.response;

import config.ServerConfig;
import server.network.MaplePacket;
import packet.ServerPacket;
import odin.server.maps.MapleMist;

/**
 *
 * @author Riremito
 */
public class ResCAffectedAreaPool {

    // CAffectedAreaPool::OnAffectedAreaCreated
    // CAffectedArea::MakeEnterFieldPacket
    public static MaplePacket spawnMist(MapleMist mist) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AffectedAreaCreated);

        sp.Encode4(mist.getObjectId()); // m_dwID
        sp.Encode4(mist.isMobMist() ? 0 : (mist.isPoisonMist() != 0 ? 1 : 2)); // m_bMobSkill
        sp.Encode4(mist.getOwnerId()); // m_dwOwnerID
        sp.Encode4((mist.getMobSkill() == null) ? mist.getSourceSkill().getId() : mist.getMobSkill().getSkillId()); // m_nSkillID
        sp.Encode1(mist.getSkillLevel()); // m_nSLV
        sp.Encode2(mist.getSkillDelay()); // time / 100
        // buffer 0x10
        {
            sp.Encode4(mist.getBox().x); // rcArea.left
            sp.Encode4(mist.getBox().y); // rcArea.top
            sp.Encode4(mist.getBox().x + mist.getBox().width); // rcArea.righ
            sp.Encode4(mist.getBox().y + mist.getBox().height); // rcArea.bottom
        }
        // old ver = 1 byte m_bSmoke
        sp.Encode4(0); // nElemAttr

        // not in JMS147-164
        if (ServerConfig.JMS186orLater()) {
            sp.Encode4(0); // nPhase
        }

        return sp.get();
    }

    // CAffectedAreaPool::OnAffectedAreaRemoved
    // CAffectedArea::MakeLeaveFieldPacket
    public static MaplePacket removeMist(MapleMist mist) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AffectedAreaRemoved);

        sp.Encode4(mist.getObjectId()); // m_dwID
        return sp.get();
    }

}
