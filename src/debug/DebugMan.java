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
import packet.ClientPacket;
import packet.ops.OpsScriptMan;
import packet.response.ResCScriptMan;

/**
 *
 * @author Riremito
 */
public class DebugMan {

    private int status = 0;
    private MapleCharacter chr = null;

    public DebugMan(MapleCharacter chr) {
        this.chr = chr;
    }

    public int getStatus() {
        return status;
    }

    public boolean updateStatus(int val) {
        switch (val) {
            case 1: {
                this.status++;
                return true;
            }
            case -1: {
                this.status = -1;
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public boolean action() {
        return action(-1);
    }

    public class NpcTag {

        private String msg = "";

        private void addMenu(int num, String text) {
            this.msg += "#L" + num + "##b" + text + "#k#l\r\n";
        }

        private String get() {
            return this.msg;
        }

    }

    public boolean action(int answer) {
        switch (status) {
            case 0: {
                chr.DebugMsg("DebugMan started.");
                NpcTag nt = new NpcTag();
                nt.addMenu(2, "#m100000000#");
                nt.addMenu(3, "#m200000000#");
                chr.SendPacket(ResCScriptMan.ScriptMessage(DEFAULT_NPC_ID, OpsScriptMan.SM_ASKMENU, (byte) 0, nt.get(), false, false));
                return true;
            }
            case 1: {
                if (answer == 2) {
                    chr.DebugMsg("Henesys.");
                    chr.setDebugMan(null);
                    return true;
                }
                if (answer == 3) {
                    chr.DebugMsg("Oribs.");
                    chr.setDebugMan(null);
                    return true;
                }

                chr.setDebugMan(null);
                return true;
            }
            default: {
                break;
            }
        }

        chr.setDebugMan(null); // NPC talk ended.
        chr.DebugMsg("DebugMan finished.");
        return false;
    }

    private static final int DEFAULT_NPC_ID = 1012003;

    public static boolean OnScriptMessageAnswerHook(MapleCharacter chr, ClientPacket cp) {
        DebugMan dm = chr.getDebugMan();

        int cm_type = cp.Decode1();
        int action = cp.Decode1();

        OpsScriptMan type = OpsScriptMan.find(cm_type);

        chr.DebugMsg("DebugMan : " + type + ", " + action);
        switch (type) {
            case SM_ASKMENU: {
                dm.updateStatus(action);
                if (action == 1) {
                    int m_nSelect = cp.Decode4();
                    dm.action(m_nSelect);
                }
                return true;
            }
            default: {
                break;
            }
        }

        Debug.ErrorLog("OnScriptMessageAnswerHook not coded = " + cm_type + ", " + action);
        return false;
    }

}
