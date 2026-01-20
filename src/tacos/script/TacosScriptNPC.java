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
package tacos.script;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import odin.client.MapleClient;
import tacos.odin.OdinNPCConversationManager;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class TacosScriptNPC extends TacosScript {

    private static TacosScriptNPC instance = null;

    public static TacosScriptNPC getInstance() {
        if (instance == null) {
            instance = new TacosScriptNPC();
        }

        return instance;
    }

    public TacosScriptNPC() {

    }

    public boolean start(MapleClient c, int npc_script_id) {
        return start(c, npc_script_id, npc_script_id);
    }

    public boolean start(MapleClient c, int npc_script_id, int npc_icon_id) {
        DebugMsg(c, TacosScriptType.NPC, npc_script_id);

        String npc_script_path = TacosScriptType.NPC.get() + npc_script_id;
        clearScripts();

        ScriptEngine engine = getScript(npc_script_path);
        if (engine == null) {
            return false;
        }

        OdinNPCConversationManager cm = new OdinNPCConversationManager(c, npc_icon_id, -1, (byte) -1, (Invocable) engine, npc_script_id);
        engine.put("cm", cm);
        cms.put(c, cm);

        // TODO : remove
        IScriptNPC_with_start script_ws = ((Invocable) engine).getInterface(IScriptNPC_with_start.class);
        if (script_ws != null) {
            DebugLogger.DebugLog("IScriptNPC_with_start is detected.");
            c.getPlayer().setConversation(1);
            script_ws.start();
            return true;
        }

        IScriptNPC script = ((Invocable) engine).getInterface(IScriptNPC.class);

        if (script == null) {
            return false;
        }

        c.getPlayer().setConversation(1);
        script.action(1, 0, 0);
        return true;
    }

    public boolean action(MapleClient c, int mode, int type, int selection) {
        if (mode == -1) {
            DebugLogger.ErrorLog("npc_script : action 1");
            return false;
        }
        OdinNPCConversationManager cm = cms.get(c);
        if (cm == null || -1 < cm.getLastMsg()) {
            DebugLogger.ErrorLog("npc_script : action 2");
            return false;
        }
        if (cm.pendingDisposal) {
            dispose(c);
            return false;
        }
        IScriptNPC script = ((Invocable) cm.getIv()).getInterface(IScriptNPC.class);
        script.action(mode, type, selection);
        return true;
    }

    public boolean dispose(MapleClient c) {
        OdinNPCConversationManager npccm = cms.get(c);
        if (npccm == null) {
            c.getPlayer().setConversation(0);
            return false;
        }
        cms.remove(c);
        c.getPlayer().setConversation(0);
        return true;
    }

}
