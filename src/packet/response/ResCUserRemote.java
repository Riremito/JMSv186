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

import handling.MaplePacket;
import packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class ResCUserRemote {

    public static MaplePacket SetActivePortableChair(int characterid, int itemid) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserSetActivePortableChair);
        sp.Encode4(characterid);
        sp.Encode4(itemid);
        return sp.Get();
    }

}
