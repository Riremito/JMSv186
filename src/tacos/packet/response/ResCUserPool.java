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
package tacos.packet.response;

import tacos.config.Config;
import tacos.client.TacosCharacter;
import tacos.config.Region;
import tacos.constants.TacosConstants;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.response.data.RD_CUser;

/**
 *
 * @author Riremito
 */
public class ResCUserPool {

    // CUserPool::OnUserEnterField
    public static ServerPacket UserEnterField(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEnterField);

        sp.Encode4(chr.getId());

        if (Config.GreaterOrEqual(Region.JMS, 302)) {
            sp.EncodeBuffer(RD_CUser.CUserRemote_Init_JMS302(chr));
        } else {
            sp.EncodeBuffer(RD_CUser.CUserRemote_Init(chr));
        }

        // FieldSpecificData?
        int map_id = chr.getMap().getId();
        if (TacosConstants.is_coconut(map_id)) {
            sp.Encode1(chr.getCoconutTeam());
            return sp;
        }
        if (TacosConstants.is_monster_carnival(map_id)) {
            sp.Encode1((chr.getCarnivalParty() != null) ? chr.getCarnivalParty().getTeam() : 0);
            return sp;
        }

        return sp;
    }

    // CUserPool::OnUserLeaveField
    public static ServerPacket UserLeaveField(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserLeaveField);

        sp.Encode4(chr.getId());
        return sp;
    }
}
