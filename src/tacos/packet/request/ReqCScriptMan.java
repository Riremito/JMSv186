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
import odin.client.MapleClient;
import tacos.debug.DebugLogger;
import odin.handling.channel.handler.NPCHandler;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsScriptMan;

/**
 *
 * @author Riremito
 */
public class ReqCScriptMan {

    public static boolean OnScriptMessageAnswer(ClientPacket cp, MapleClient c) {
        MapleCharacter chr = c.getPlayer();

        byte cm_type = cp.Decode1();
        byte action = cp.Decode1();

        OpsScriptMan type = OpsScriptMan.find(cm_type);

        switch (type) {
            case SM_SAY: {
                NPCHandler.NPCMoreTalk(c, type, action, -1, null);
                return true;
            }
            case SM_ASKYESNO: {
                NPCHandler.NPCMoreTalk(c, type, action, -1, null);
                return true;
            }
            case SM_SAYIMAGE: {
                break;
            }
            case SM_ASKTEXT: {
                String text = cp.DecodeStr();
                NPCHandler.NPCMoreTalk(c, type, action, -1, text);
                return true;
            }
            case SM_ASKNUMBER: {
                break;
            }
            case SM_ASKMENU: {
                if (action != 0) {
                    int m_nSelect = cp.Decode4();
                    NPCHandler.NPCMoreTalk(c, type, action, m_nSelect, null);
                    return true;
                }

                NPCHandler.NPCMoreTalk(c, type, action, -1, null);
                return true;
            }
            case SM_ASKQUIZ: {
                break;
            }
            case SM_ASKSPEEDQUIZ: {
                break;
            }
            case SM_ASKAVATAR: {
                if (action != 0) {
                    byte m_nAvatarIndex = cp.Decode1();
                    NPCHandler.NPCMoreTalk(c, type, action, m_nAvatarIndex, null);
                    return true;
                }

                NPCHandler.NPCMoreTalk(c, type, action, -1, null);
                return true;
            }
            case SM_ASKMEMBERSHOPAVATAR: {
                break;
            }
            case SM_ASKPET: {
                break;
            }
            case SM_ASKPETALL: {
                break;
            }
            case SM_ASKACCEPT: {
                NPCHandler.NPCMoreTalk(c, type, action, -1, null);
                return true;
            }
            case SM_ASKBOXTEXT: {
                break;
            }
            case SM_ASKSLIDEMENU: {
                if (action != 0) {
                    int SelectResult = cp.Decode4();
                    NPCHandler.NPCMoreTalk(c, type, action, SelectResult, null);
                    return true;
                }

                NPCHandler.NPCMoreTalk(c, type, action, -1, null);
                return true;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnScriptMessageAnswer not coded.");
        //NPCHandler.NPCMoreTalk(c, cp); // test
        return false;
    }
}
