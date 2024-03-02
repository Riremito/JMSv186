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
package packet.client.handling;

import config.ServerConfig;
import debug.Debug;
import handling.MaplePacket;
import packet.server.ServerPacket;

/**
 *
 * @author Riremito
 */
public class ScriptManPacket {

    public enum Flag {
        SM_SAY(0),
        SM_SAY_IMAGE(1),
        SM_ASK_YES_NO(2),
        SM_ASK_TEXT(3),
        SM_ASK_NUMBER(4),
        SM_ASK_MENU(5),
        SM_ASK_QUIZ(6),
        SM_ASK_SPEED_QUIZ(7),
        SM_ASK_AVATAR(8),
        SM_ASK_MEMBER_SHOP_AVATAR(9),
        SM_ASK_PET(10),
        SM_ASK_PET_ALL(11),
        SM_ASK_YES_NO_QUEST(13),
        SM_ASK_BOX_TEXT(14),
        SM_ASK_SLIDE_MENU(15),
        UNKNOWN(-1);

        private int value;

        Flag(int flag) {
            value = flag;
        }

        Flag() {
            value = -1;
        }

        public boolean set(int flag) {
            value = flag;
            return true;
        }

        public int get() {
            return value;
        }

        public static Flag get(int v) {
            for (final Flag f : Flag.values()) {
                if (f.get() == v) {
                    return f;
                }
            }
            return Flag.UNKNOWN;
        }
    }

    // CScriptMan::OnPacket
    // CScriptMan::OnScriptMessage
    // getNPCTalk, getMapSelection, getNPCTalkStyle, getNPCTalkNum, getNPCTalkText, getEvanTutorial
    public static MaplePacket ScriptMessage(int npcid, byte type, byte param, String text, boolean prev, boolean next) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_ScriptMessage);
        Flag flag = Flag.get(type);
        p.Encode1(4); // nSpeakerTypeID, not used
        p.Encode4(npcid); // nSpeakerTemplateID, npcid
        p.Encode1(flag.get()); // nMsgType

        if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion())
                || ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()) {
            p.Encode1(param); // v186+, not used
        }

        switch (flag) {
            case SM_SAY: {
                if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion())
                        || ServerConfig.IsTWMS()
                        || ServerConfig.IsCMS()) {
                    if ((param & 4) > 0) {
                        p.Encode4(0); // nSpeakerTemplateID
                    }
                }
                p.EncodeStr(text);
                p.Encode1(prev ? 1 : 0);
                p.Encode1(next ? 1 : 0);
                break;
            }
            case SM_SAY_IMAGE: {
                p.Encode1(0); // number of text
                p.EncodeStr(text);
                break;
            }
            case SM_ASK_YES_NO: {
                p.EncodeStr(text);
                break;
            }
            case SM_ASK_TEXT: {
                p.EncodeStr(text);
                p.EncodeStr("");
                p.Encode2(0);
                p.Encode2(0);
                break;
            }
            case SM_ASK_NUMBER: {
                p.EncodeStr(text);
                //p.Encode4(0);
                //p.Encode4(0);
                //p.Encode4(0);
                break;
            }
            case SM_ASK_MENU: {
                p.EncodeStr(text);
                break;
            }
            case SM_ASK_QUIZ: {
                p.Encode1(0);
                p.EncodeStr(text);
                p.EncodeStr("");
                p.EncodeStr("");
                p.Encode4(0);
                p.Encode4(0);
                p.Encode4(0);
                break;
            }
            case SM_ASK_SPEED_QUIZ: {
                break;
            }
            case SM_ASK_AVATAR: {
                p.EncodeStr(text);
                // 1 byte size
                // 4 bytes array
                break;
            }
            case SM_ASK_MEMBER_SHOP_AVATAR: {
                break;
            }
            case SM_ASK_PET: {
                break;
            }
            case SM_ASK_PET_ALL: {
                break;
            }
            case SM_ASK_YES_NO_QUEST: {
                break;
            }
            case SM_ASK_BOX_TEXT: {
                p.Encode4(0);
                p.Encode4(5);
                p.EncodeStr(text);
                break;
            }
            case SM_ASK_SLIDE_MENU: {
                break;
            }
            default: {
                Debug.ErrorLog("ScriptMessage not coded " + type);
                break;
            }
        }

        return p.Get();
    }

}
