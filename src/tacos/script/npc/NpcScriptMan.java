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
package tacos.script.npc;

import java.util.ArrayList;
import tacos.client.TacosCharacter;
import tacos.debug.DebugLogger;
import tacos.packet.ops.OpsScriptMan;
import tacos.packet.response.ResCScriptMan;

/**
 *
 * @author Riremito
 */
public class NpcScriptMan {

    protected static final int DEFAULT_NPC_ID = 1012003;
    private int npc_script_id = 0;

    public int getNpcId() {
        if (this.npc_script_id == 0) {
            return DEFAULT_NPC_ID;
        }
        return this.npc_script_id;
    }

    public void setNpcId(int npc_script_id) {
        this.npc_script_id = npc_script_id;
    }

    public static boolean OnScriptMessageAnswer(TacosCharacter chr, OpsScriptMan ops, int nMsgType, int action, int m_nSelect) {
        INpcScriptMan dm = chr.getNpcScript();
        ((NpcScriptMan) dm).updateStatus(action);

        switch (ops) {
            case SM_SAY:
            case SM_ASKYESNO:
            case SM_ASKMENU:
            case SM_ASKAVATAR: {
                if (action != -1) {
                    if (dm.action(chr, ((NpcScriptMan) dm).getStatus(), m_nSelect)) {
                        // continue
                        return true;
                    }
                }
                // end
                dm.end(chr);
                return true;
            }
            default: {
                break;
            }
        }

        dm.end(chr);
        DebugLogger.ErrorLog("NpcScriptMan : not coded, nMsgType = " + nMsgType);
        return false;
    }

    private int status = 0;

    private int getStatus() {
        return this.status;
    }

    private boolean updateStatus(int val) {
        switch (val) {
            case 1 -> // next
            {
                this.status++;
                return true;
            }
            case 0 -> // prev
            {
                this.status--;
                return true;
            }
            case -1 -> // cancel
            {
                this.status = -1;
                return true;
            }
            default -> {
            }
        }
        return false;
    }

    public boolean start(INpcScriptMan debugMan, TacosCharacter chr) {
        this.status = 0;
        chr.DebugMsg("NpcScriptMan : started.");
        chr.setNpcScript(debugMan);
        debugMan.action(chr, this.status, 0);
        return true;
    }

    public boolean end(TacosCharacter chr) {
        this.status = -1;
        chr.DebugMsg("NpcScriptMan : finished.");
        chr.setNpcScript(null);
        return true;
    }

    protected void askMenu(TacosCharacter chr, NpcTag nt) {
        askMenu(chr, nt, false, false);
    }

    protected void say(TacosCharacter chr, NpcTag nt) {
        say(chr, nt, false, false);
    }

    protected void say(TacosCharacter chr, NpcTag nt, boolean prev, boolean next) {
        if (nt.get().contains("#L")) {
            DebugLogger.ErrorLog("NpcScriptMan : say.");
            askMenu(chr, nt, prev, next);
            return;
        }
        chr.SendPacket(ResCScriptMan.ScriptMessage(getNpcId(), OpsScriptMan.SM_SAY, (byte) 0, nt.get(), prev, next));
    }

    protected void askYesNo(TacosCharacter chr, NpcTag nt) {
        chr.SendPacket(ResCScriptMan.ScriptMessage(getNpcId(), OpsScriptMan.SM_ASKYESNO, (byte) 0, nt.get(), false, false));
    }

    protected void askMenu(TacosCharacter chr, NpcTag nt, boolean prev, boolean next) {
        chr.SendPacket(ResCScriptMan.ScriptMessage(getNpcId(), OpsScriptMan.SM_ASKMENU, (byte) 0, nt.get(), prev, next));
    }

    protected void askAvatar(TacosCharacter chr, NpcTag nt, ArrayList<Integer> ids) {
        chr.SendPacket(ResCScriptMan.ScriptMessage(getNpcId(), OpsScriptMan.SM_ASKAVATAR, (byte) 0, nt.get(), false, false, ids));
    }

    public class NpcTag {

        protected String msg = "";

        public void add(String text) {
            this.add(text, true);
        }

        public void add(String text, boolean crlf) {
            this.msg += text;
            if (crlf) {
                this.msg += "\r\n";
            }
        }

        public void addMenu(int num, String text) {
            this.msg += "#L" + num + "##b" + text + "#k#l\r\n";
        }

        public void addMenuRed(int num, String text) {
            this.msg += "#L" + num + "##r" + text + "#k#l\r\n";
        }

        public String get() {
            return this.msg;
        }
    }
}
