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

import odin.client.MapleCharacter;
import config.Region;
import config.Version;
import server.network.MaplePacket;
import packet.ServerPacket;
import packet.response.data.DataCUserRemote;

/**
 *
 * @author Riremito
 */
public class ResCUserPool {

    // CUserPool::OnUserEnterField
    public static MaplePacket UserEnterField(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEnterField);

        sp.Encode4(chr.getId());

        if (Version.Equal(Region.JMS, 147)) {
            sp.EncodeBuffer(DataCUserRemote.Init_JMS147(chr));
        } else if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.EncodeBuffer(DataCUserRemote.Init_JMS302(chr));
        } else {
            sp.EncodeBuffer(DataCUserRemote.Init(chr));
        }
        return sp.get();
    }

    // CUserPool::OnUserLeaveField
    public static MaplePacket UserLeaveField(int player_id) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserLeaveField);
        sp.Encode4(player_id);
        return sp.get();
    }

}
