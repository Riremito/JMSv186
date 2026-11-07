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

import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCBattleRecordMan {

    // CBattleRecordMan::OnDotDamageInfo, not tested.
    public static ServerPacket BattleRecordDotDamageInfo(int damage, int count, boolean bAttrRate, int nDotAttrRate) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_BattleRecordDotDamageInfo);

        sp.Encode4(damage);
        sp.Encode4(count);
        sp.Encode1(bAttrRate ? 1 : 0); // bAttrRate
        sp.Encode4(nDotAttrRate, bAttrRate); // nDotAttrRate
        return sp;
    }

    // CBattleRecordMan::OnServerOnCalcRequestResult, not tested.
    public static ServerPacket BattleRecordRequestResult(int result) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_BattleRecordRequestResult);

        sp.Encode1(result);
        return sp;
    }
}
