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

import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import tacos.debug.DebugLogger;
import tacos.debug.DebugMan;
import tacos.odin.OdinNPCConversationManager;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsScriptMan;
import tacos.script.TacosScriptNPC;
import tacos.script.TacosScriptQuest;

/**
 *
 * @author Riremito
 */
public class ReqCScriptMan {

    public static boolean OnScriptMessageAnswer(MapleCharacter chr, ClientPacket cp) {
        int nMsgType = cp.Decode1();
        int action = cp.Decode1();

        int m_nSelect = -1;
        String m_sInputStr_Result = null;

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
        if (chr.getDebugMan() != null) {
            return DebugMan.OnScriptMessageAnswer(chr, ops, nMsgType, action, m_nSelect);
        }

        // js coding.
        return OnOdinScript(chr, nMsgType, action, m_nSelect, m_sInputStr_Result);
    }

    public static boolean OnOdinScript(MapleCharacter chr, int nMsgType, int action, int m_nSelect, String m_sInputStr_Result) {
        TacosClient client = chr.getClient();
        boolean is_npc_talk = false;
        boolean is_quest_start = false;
        boolean is_quest_end = false;
        OdinNPCConversationManager cm = TacosScriptNPC.getInstance().getCM(client); // check npc talk script.

        if (cm != null) {
            is_npc_talk = true;
        }

        if (!is_npc_talk) {
            cm = TacosScriptQuest.getInstance().getCM(client); // check quest script.
            if (cm != null) {
                if (cm.getType() == 0) {
                    is_quest_start = true;
                }
                if (cm.getType() == 1) {
                    is_quest_end = true;
                }
            }
        }

        if (cm == null || (!is_npc_talk && !is_quest_start && !is_quest_end)) {
            DebugLogger.ErrorLog("OnOdinScript : not found.");
            return false;
        }

        if (chr.getConversation() == 0) {
            DebugLogger.ErrorLog("OnOdinScript : getConversation = 0.");
            return false;
        }

        if (cm.getLastMsg() != nMsgType) {
            DebugLogger.ErrorLog("OnOdinScript : getLastMsg.");
            return false;
        }
        cm.setLastMsg(-1);

        // i cannot understand what odin script handling wanted to do.
        OpsScriptMan ops = OpsScriptMan.find(nMsgType);
        switch (ops) {
            case SM_SAY:
            case SM_SAYIMAGE:
            case SM_ASKYESNO:
            case SM_ASKMENU:
            case SM_ASKAVATAR:
            case SM_ASKACCEPT:
            case SM_ASKSLIDEMENU: {
                break;
            }
            case SM_ASKTEXT: {
                cm.setGetText(m_sInputStr_Result);
                if (action == 0) {
                    cm.dispose();
                    return true;
                }
                break;
            }
            default: {
                DebugLogger.ErrorLog("OnOdinScript : not coded, nMsgType = " + nMsgType);
                return false;
            }
        }

        if (action == -1 || m_nSelect <= -2) {
            cm.dispose();
            return true;
        }

        if (is_npc_talk) {
            TacosScriptNPC.getInstance().action(client, action, nMsgType, m_nSelect);
            return true;
        }
        if (is_quest_start) {
            TacosScriptQuest.getInstance().startQuest(client, action, nMsgType, m_nSelect);
            return true;
        }
        if (is_quest_end) {
            TacosScriptQuest.getInstance().endQuest(client, action, nMsgType, m_nSelect);
            return true;
        }

        cm.dispose();
        return false;
    }
}
