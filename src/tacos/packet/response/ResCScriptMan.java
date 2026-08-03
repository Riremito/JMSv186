/*
 * Copyright (C) 2023 Riremito
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
import tacos.config.Version;
import tacos.debug.DebugLogger;
import java.util.ArrayList;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsScriptMan;

/**
 *
 * @author Riremito
 */
public class ResCScriptMan {

    // CScriptMan::OnPacket
    // CScriptMan::OnScriptMessage
    // getNPCTalk, getMapSelection, getNPCTalkStyle, getNPCTalkNum, getNPCTalkText, getEvanTutorial
    public static ServerPacket ScriptMessage(int npcid, OpsScriptMan smt, byte param, String text, boolean prev, boolean next) {
        return ScriptMessage(npcid, smt, param, text, prev, next, null);
    }

    public static ServerPacket ScriptMessage(int npcid, OpsScriptMan smt, byte param, String text, boolean prev, boolean next, ArrayList<Integer> ids) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ScriptMessage);
        sp.Encode1(4); // nSpeakerTypeID, not used
        sp.Encode4(npcid); // nSpeakerTemplateID, npcid
        sp.Encode1(smt.get()); // nMsgType

        if (Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 92) || Version.GreaterOrEqual(Region.JMS, 180) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70) || Version.GreaterOrEqual(Region.KMS, 84) || Version.GreaterOrEqual(Region.GMS, 83)) {
            sp.Encode1(param); // v186+, not used
        }

        switch (smt) {
            case SM_SAY: {
                if (Version.PostBB() || Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70) || Version.GreaterOrEqual(Region.KMS, 95)) {
                    if ((param & 4) > 0) {
                        sp.Encode4(0); // nSpeakerTemplateID
                    }
                }
                sp.EncodeStr(text);
                sp.Encode1(prev ? 1 : 0);
                sp.Encode1(next ? 1 : 0);
                break;
            }
            case SM_SAYIMAGE: {
                sp.Encode1(0); // number of text
                sp.EncodeStr(text);
                break;
            }
            case SM_ASKYESNO: {
                sp.EncodeStr(text);
                break;
            }
            case SM_ASKTEXT: {
                sp.EncodeStr(text);
                sp.EncodeStr("");
                sp.Encode2(0);
                sp.Encode2(0);
                break;
            }
            case SM_ASKNUMBER: {
                sp.EncodeStr(text);
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode4(0);
                break;
            }
            case SM_ASKMENU: {
                sp.EncodeStr(text);
                break;
            }
            case SM_ASKQUIZ: {
                sp.Encode1(0);
                sp.EncodeStr(text);
                sp.EncodeStr("");
                sp.EncodeStr("");
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode4(0);
                break;
            }
            case SM_ASKSPEEDQUIZ: {
                break;
            }
            case SM_ASKAVATAR: {
                sp.EncodeStr(text);
                sp.Encode1((ids != null) ? ids.size() : 0);
                if (ids != null) {
                    for (int id : ids) {
                        sp.Encode4(id);
                    }
                }
                if (Version.GreaterOrEqual(Region.CMS, 88)) {
                    sp.Encode4(0);
                }
                break;
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
                sp.EncodeStr(text);
                break;
            }
            case SM_ASKBOXTEXT: {
                break;
            }
            case SM_ASKSLIDEMENU: {
                sp.Encode4(0);
                sp.Encode4(5);
                sp.EncodeStr(text);
                break;
            }
            default: {
                DebugLogger.ErrorLog("ScriptMessage not coded.");
                break;
            }
        }

        return sp;
    }

    // TODO : fix
    public static ServerPacket getEvanTutorial(String data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ScriptMessage);

        sp.Encode4(8);
        sp.Encode1(0);
        sp.Encode1(1);
        sp.Encode1(1);
        sp.Encode1(1);
        sp.EncodeStr(data);
        return sp;
    }
}
