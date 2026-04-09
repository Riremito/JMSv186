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
package tacos.packet.response;

import tacos.network.MaplePacket;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsCashItem;

/**
 *
 * @author Riremito
 */
public class ResCUIItemUpgrade {

    public static MaplePacket ItemUpgradeResult(OpsCashItem m_nRet1, int m_nIUC) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ItemUpgradeResult);

        sp.Encode1(m_nRet1.get());
        switch (m_nRet1) {
            case CashItemRes_ItemUpgradeSuccess: {
                sp.Encode4(0); // m_nResult
                sp.Encode4(m_nIUC); // m_nIUC
                break;
            }
            case CashItemRes_ItemUpgradeDone: {
                sp.Encode4(0); // error
                break;
            }
            case CashItemRes_ItemUpgradeErr: {
                /*
                0x01            このアイテムには使用できません。
                0x02            すでにアップグレード可能回数を超えました。これ以上使用することができません。
                0x03            ホーンテイルのネックレスには使用できません。
                上記以外        原因不明の不具合
                 */
                sp.Encode4(2); // error
                break;
            }
            default: {
                break;
            }
        }
        return sp.get();
    }

}
