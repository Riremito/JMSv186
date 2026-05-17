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

import tacos.network.MaplePacket;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsRPS;

/**
 *
 * @author Riremito
 */
public class ResCRPSGameDlg {

    public static MaplePacket RPSGame(OpsRPS ops) {
        return RPSGame(ops, 0, 0, 0);
    }

    public static MaplePacket RPSGame(OpsRPS ops, int m_nNpcSelect, int m_nCntStraightVictories) {
        return RPSGame(ops, m_nNpcSelect, m_nCntStraightVictories, 0);
    }

    // CRPSGameDlg::OnPacket
    public static MaplePacket RPSGame(OpsRPS ops, int m_nNpcSelect, int m_nCntStraightVictories, int nTemplateID) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_RPSGame);

        sp.Encode1(ops.get()); // nType

        switch (ops) {
            case RPSRes_NotEnoughMoney:
            case RPSRes_NoEmptySlotForReward:
            case RPSRes_Retry: // CRPSGameDlg::ProcessPacket
            {
                break;
            }
            case RPSRes_StartGame:
            case RPSRes_Coninue: // CRPSGameDlg::ProcessPacket
            {
                break;
            }
            case RPSRes_TimeOver: // CRPSGameDlg::ProcessPacket
            {
                break;
            }
            case RPSRes_NpcSelection: {
                sp.Encode1(m_nNpcSelect);
                sp.Encode1(m_nCntStraightVictories); // < 0 lose.
                break;
            }
            case RPSRes_Open: {
                sp.Encode4(nTemplateID); // nTemplateID
                break;
            }
            case RPSRes_Quit: {
                break;
            }
        }

        return sp.get();
    }
}
