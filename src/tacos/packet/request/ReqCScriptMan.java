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
package tacos.packet.request;

import tacos.client.TacosCharacter;
import tacos.debug.DebugLogger;
import tacos.script.npc.NpcScriptMan;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsScriptMan;

/**
 *
 * @author Riremito
 */
public class ReqCScriptMan {

    // CUIInitialQuiz::SendResult, CUISpeedQuiz::SendResult
    public static boolean OnScriptMessageAnswer(TacosCharacter chr, ClientPacket cp) {
        int nMsgType = cp.Decode1();
        int action = cp.Decode1();

        int m_nSelect = -1;
        String m_sInputStr_Result = null;
        long liCashItemSN = 0;

        OpsScriptMan ops = OpsScriptMan.find(nMsgType);
        if (action != 0) { // JMS is always 1, CMS104 is not 1.
            switch (ops) {
                case SM_ASKMENU: {
                    m_nSelect = cp.Decode4(); // m_nSelect
                    break;
                }
                case SM_ASKTEXT: {
                    m_sInputStr_Result = cp.DecodeStr(); // m_sInputStr_Result
                    break;
                }
                case SM_ASKNUMBER: {
                    m_nSelect = cp.Decode4(); // m_nInputNo_Result
                    break;
                }
                case SM_ASKPET: {
                    // 2 = no pet
                    if (action == 1) {
                        liCashItemSN = cp.Decode8(); // liCashItemSN
                    }
                    break;
                }
                case SM_ASKAVATAR: {
                    m_nSelect = (int) cp.Decode1(); // m_nAvatarIndex
                    break;
                }
                case SM_ASKSLIDEMENU: {
                    m_nSelect = cp.Decode4(); // m_nCurrentMoveInfo
                    break;
                }
                default: {
                    break;
                }
            }
        }

        // java coding.
        if (chr.getNpcScript() != null) {
            return NpcScriptMan.OnScriptMessageAnswer(chr, ops, nMsgType, action, m_nSelect, m_sInputStr_Result, liCashItemSN);
        }

        DebugLogger.ErrorLog("OnScriptMessageAnswer : no npc script.");
        return false;
    }
}
