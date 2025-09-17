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
package debug;

import client.MapleCharacter;
import java.util.ArrayList;
import packet.ClientPacket;
import packet.ops.OpsScriptMan;
import packet.response.ResCScriptMan;

/**
 *
 * @author Riremito
 */
public class DebugMan {

    protected static final int DEFAULT_NPC_ID = 1012003;

    public static boolean OnScriptMessageAnswerHook(MapleCharacter chr, ClientPacket cp) {
        IDebugMan dm = chr.getDebugMan();

        int cm_type = cp.Decode1();
        int action = cp.Decode1();

        OpsScriptMan type = OpsScriptMan.find(cm_type);

        ((DebugMan) dm).updateStatus(action);

        int m_nSelect = -1;
        if (action == 1) {
            if (type == OpsScriptMan.SM_ASKMENU) {
                m_nSelect = cp.Decode4();
            }
            if (type == OpsScriptMan.SM_ASKAVATAR) {
                m_nSelect = (int) cp.Decode1();
            }
        }

        chr.DebugMsg("DebugMan : anwser (" + type + ", " + action + ", " + m_nSelect + ")");

        switch (type) {
            case SM_SAY:
            case SM_ASKMENU:
            case SM_ASKAVATAR: {
                if (action != -1) {
                    if (dm.action(chr, ((DebugMan) dm).getStatus(), m_nSelect)) {
                        // continue
                        return true;
                    }
                }
                // end
                dm.end(chr);
                return false;
            }
            default: {
                break;
            }
        }

        dm.end(chr);
        DebugLogger.ErrorLog("OnScriptMessageAnswerHook not coded = " + cm_type + ", " + action);
        return false;
    }

    private int status = 0;

    private int getStatus() {
        return status;
    }

    private boolean updateStatus(int val) {
        switch (val) {
            case 1: // next
            {
                this.status++;
                return true;
            }
            case 0: // prev
            {
                this.status--;
                return true;
            }
            case -1: // cancel
            {
                this.status = -1;
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public boolean start(IDebugMan debugMan, MapleCharacter chr) {
        this.status = 0;
        chr.DebugMsg("DebugMan : started.");
        chr.setDebugMan(debugMan);
        debugMan.action(chr, this.status, 0);
        return true;
    }

    public boolean end(MapleCharacter chr) {
        this.status = -1;
        chr.DebugMsg("DebugMan : finished.");
        chr.setDebugMan(null);
        return true;
    }

    protected void askMenu(MapleCharacter chr, NpcTag nt) {
        askMenu(chr, nt, false, false);
    }

    protected void say(MapleCharacter chr, NpcTag nt) {
        say(chr, nt, false, false);
    }

    protected void askAvatar(MapleCharacter chr, NpcTag nt, ArrayList<Integer> ids) {
        chr.SendPacket(ResCScriptMan.ScriptMessage(DEFAULT_NPC_ID, OpsScriptMan.SM_ASKAVATAR, (byte) 0, nt.get(), false, false, ids));
    }

    protected void askMenu(MapleCharacter chr, NpcTag nt, boolean prev, boolean next) {
        chr.SendPacket(ResCScriptMan.ScriptMessage(DEFAULT_NPC_ID, OpsScriptMan.SM_ASKMENU, (byte) 0, nt.get(), prev, next));
    }

    protected void say(MapleCharacter chr, NpcTag nt, boolean prev, boolean next) {
        if (nt.get().contains("#L")) {
            DebugLogger.ErrorLog("DebugMan : say.");
            askMenu(chr, nt, prev, next);
            return;
        }
        chr.SendPacket(ResCScriptMan.ScriptMessage(DEFAULT_NPC_ID, OpsScriptMan.SM_SAY, (byte) 0, nt.get(), prev, next));
    }

    protected class NpcTag {

        protected String msg = "";

        protected void add(String text) {
            this.add(text, true);
        }

        protected void add(String text, boolean crlf) {
            this.msg += text;
            if (crlf) {
                this.msg += "\r\n";
            }
        }

        protected void addMenu(int num, String text) {
            this.msg += "#L" + num + "##b" + text + "#k#l\r\n";
        }

        protected void addMenuRed(int num, String text) {
            this.msg += "#L" + num + "##r" + text + "#k#l\r\n";
        }

        protected String get() {
            return this.msg;
        }

    }

}
