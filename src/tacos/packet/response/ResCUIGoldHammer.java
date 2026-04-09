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

import tacos.config.Region;
import tacos.network.MaplePacket;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsGoldHammer;

/**
 *
 * @author Riremito
 */
public class ResCUIGoldHammer {

    public static MaplePacket GoldHammerResult(OpsGoldHammer m_nReturnResult) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GoldHammerResult);

        sp.Encode1(m_nReturnResult.get()); // m_nReturnResult
        switch (m_nReturnResult) {
            case GoldHammerRes_Success:
            case GoldHammerRes_Fail: {
                sp.Encode4(0);
                if (Region.check(Region.JMS)) {
                    sp.Encode4(0); // m_nIUC?
                }
                break;
            }
            case GoldHammerRes_Done: {
                sp.Encode4(0); // error code
                break;
            }
            case GoldHammerRes_Err: {
                sp.Encode4(0); // error code
                break;
            }
            default: {
                break;
            }
        }
        return sp.get();
    }

}
