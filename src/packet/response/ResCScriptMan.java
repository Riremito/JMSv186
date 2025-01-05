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

import config.ServerConfig;
import debug.Debug;
import handling.MaplePacket;
import packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class ResCScriptMan {

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
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ScriptMessage);
        Flag flag = Flag.get(type);
        sp.Encode1(4); // nSpeakerTypeID, not used
        sp.Encode4(npcid); // nSpeakerTemplateID, npcid
        sp.Encode1(flag.get()); // nMsgType

        if (ServerConfig.JMS180orLater()) {
            sp.Encode1(param); // v186+, not used
        }

        switch (flag) {
            case SM_SAY: {
                if (ServerConfig.JMS186orLater()
                        || ServerConfig.KMS95orLater()) {
                    if ((param & 4) > 0) {
                        sp.Encode4(0); // nSpeakerTemplateID
                    }
                }
                sp.EncodeStr(text);
                sp.Encode1(prev ? 1 : 0);
                sp.Encode1(next ? 1 : 0);
                break;
            }
            case SM_SAY_IMAGE: {
                sp.Encode1(0); // number of text
                sp.EncodeStr(text);
                break;
            }
            case SM_ASK_YES_NO: {
                sp.EncodeStr(text);
                break;
            }
            case SM_ASK_TEXT: {
                sp.EncodeStr(text);
                sp.EncodeStr("");
                sp.Encode2(0);
                sp.Encode2(0);
                break;
            }
            case SM_ASK_NUMBER: {
                sp.EncodeStr(text);
                //p.Encode4(0);
                //p.Encode4(0);
                //p.Encode4(0);
                break;
            }
            case SM_ASK_MENU: {
                sp.EncodeStr(text);
                break;
            }
            case SM_ASK_QUIZ: {
                sp.Encode1(0);
                sp.EncodeStr(text);
                sp.EncodeStr("");
                sp.EncodeStr("");
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode4(0);
                break;
            }
            case SM_ASK_SPEED_QUIZ: {
                break;
            }
            case SM_ASK_AVATAR: {
                sp.EncodeStr(text);
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
                sp.Encode4(0);
                sp.Encode4(5);
                sp.EncodeStr(text);
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

        return sp.Get();
    }

}
