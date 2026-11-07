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
import tacos.debug.DebugLogger;
import tacos.config.Config;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsScriptMan;
import tacos.packet.response.builder.PB_ScriptMessage;
import tacos.server.map.object.TacosPet;

/**
 *
 * @author Riremito
 */
public class ResCScriptMan {

    // CScriptMan::OnPacket
    // CScriptMan::OnScriptMessage
    // getNPCTalk, getMapSelection, getNPCTalkStyle, getNPCTalkNum, getNPCTalkText, getEvanTutorial
    public static ServerPacket ScriptMessage(OpsScriptMan smt, PB_ScriptMessage data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ScriptMessage);
        sp.Encode1(4); // nSpeakerTypeID, not used
        sp.Encode4(data.npc_id); // nSpeakerTemplateID, npcid
        sp.Encode1(smt.get()); // nMsgType
        sp.Encode1(data.param, Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 84) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 83) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)); // v186+, not used

        switch (smt) {
            case SM_SAY: {
                if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 95) || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)) {
                    if ((data.param & 4) != 0) {
                        sp.Encode4(0); // nSpeakerTemplateID
                    }
                }
                sp.EncodeStr(data.text);
                sp.Encode1(data.prev ? 1 : 0);
                sp.Encode1(data.next ? 1 : 0);
                break;
            }
            case SM_SAYIMAGE: {
                sp.Encode1(0); // number of text
                sp.EncodeStr(data.text);
                break;
            }
            case SM_ASKYESNO: {
                sp.EncodeStr(data.text);
                break;
            }
            // CScriptMan::OnAskText
            case SM_ASKTEXT: {
                sp.EncodeStr(data.text);
                sp.EncodeStr(data.text_default);
                sp.Encode2(data.length_min);
                sp.Encode2(data.length_max);
                break;
            }
            // CScriptMan::OnAskNumber
            case SM_ASKNUMBER: {
                sp.EncodeStr(data.text);
                sp.Encode4(data.number_default);
                sp.Encode4(data.number_min);
                sp.Encode4(data.number_max);
                break;
            }
            case SM_ASKMENU: {
                sp.EncodeStr(data.text);
                break;
            }
            case SM_ASKQUIZ: {
                sp.Encode1(0);
                sp.EncodeStr(data.text);
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
                sp.EncodeStr(data.text);
                sp.Encode1((data.ids != null) ? data.ids.size() : 0);
                if (data.ids != null) {
                    for (int id : data.ids) {
                        sp.Encode4(id);
                    }
                }
                sp.Encode4(0, Config.GreaterOrEqual(Region.CMS, 88));
                break;
            }
            case SM_ASKMEMBERSHOPAVATAR: {
                break;
            }
            // CScriptMan::OnAskPet
            case SM_ASKPET: {
                sp.EncodeStr(data.text);
                sp.Encode1((data.pets != null) ? data.pets.size() : 0);
                if (data.pets != null) {
                    for (TacosPet pet : data.pets) {
                        sp.Encode8(pet.getUniqueId()); // liCashItemSN
                        sp.Encode1(pet.getInventoryPosition()); // not used
                    }
                }
                break;
            }
            case SM_ASKPETALL: {
                break;
            }
            case SM_ASKACCEPT: {
                sp.EncodeStr(data.text);
                break;
            }
            case SM_ASKBOXTEXT: {
                break;
            }
            case SM_ASKSLIDEMENU: {
                sp.Encode4(0);
                sp.Encode4(5);
                sp.EncodeStr(data.text);
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
