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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package packet.response;

import config.Region;
import config.ServerConfig;
import config.Version;
import debug.DebugLogger;
import java.util.ArrayList;
import server.network.MaplePacket;
import packet.ServerPacket;
import packet.ops.OpsScriptMan;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCScriptMan {

    // CScriptMan::OnPacket
    // CScriptMan::OnScriptMessage
    // getNPCTalk, getMapSelection, getNPCTalkStyle, getNPCTalkNum, getNPCTalkText, getEvanTutorial
    public static MaplePacket ScriptMessage(int npcid, OpsScriptMan smt, byte param, String text, boolean prev, boolean next) {
        return ScriptMessage(npcid, smt, param, text, prev, next, null);
    }

    public static MaplePacket ScriptMessage(int npcid, OpsScriptMan smt, byte param, String text, boolean prev, boolean next, ArrayList<Integer> ids) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ScriptMessage);
        sp.Encode1(4); // nSpeakerTypeID, not used
        sp.Encode4(npcid); // nSpeakerTemplateID, npcid
        sp.Encode1(smt.get()); // nMsgType

        if (ServerConfig.JMS180orLater() || Version.GreaterOrEqual(Region.KMS, 84) || Version.GreaterOrEqual(Region.GMS, 84)) {
            sp.Encode1(param); // v186+, not used
        }

        switch (smt) {
            case SM_SAY: {
                if (ServerConfig.JMS186orLater()
                        || Version.GreaterOrEqual(Region.KMS, 95)) {
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
                //p.Encode4(0);
                //p.Encode4(0);
                //p.Encode4(0);
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

        return sp.get();
    }

    public static MaplePacket getEvanTutorial(String data) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ScriptMessage.get());
        mplew.writeInt(8);
        mplew.write(0);
        mplew.write(1);
        mplew.write(1);
        mplew.write(1);
        mplew.writeMapleAsciiString(data);
        return mplew.getPacket();
    }

}
