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
public class ResCUserLocal {

    public static MaplePacket SitResult(int id) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserSitResult);
        boolean is_cancel = (id == -1);
        sp.Encode1(is_cancel ? 0 : 1);
        if (!is_cancel) {
            sp.Encode2(id); // sit
        }
        return sp.Get();
    }

}
