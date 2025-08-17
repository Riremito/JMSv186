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
package packet.response.wrapper;

import packet.response.ResCWvsContext;
import server.network.MaplePacket;

/**
 *
 * @author Riremito
 */
public class WrapCWvsContext {

    public static MaplePacket updateInv() {
        return ResCWvsContext.InventoryOperation(true, null);
    }

    public static MaplePacket updateStat() {
        return ResCWvsContext.StatChanged(null, 1, 0);
    }

}
