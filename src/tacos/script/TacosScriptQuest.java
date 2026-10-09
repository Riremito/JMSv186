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
import tacos.client.TacosClient;
import tacos.odin.OdinNPCConversationManager;
import odin.server.quest.MapleQuest;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class TacosScriptQuest extends TacosScript {

    private static TacosScriptQuest instance = null;

    public static TacosScriptQuest getInstance() {
        if (instance == null) {
            instance = new TacosScriptQuest();
        }

        return instance;
    }

    public TacosScriptQuest() {

    }

    public boolean startQuest(TacosClient client, int npc_id, int quest_script_id) {
        DebugMsg(client, TacosScriptType.QUEST, quest_script_id);

        String quest_script_path = TacosScriptType.QUEST.get() + quest_script_id;
        clearScripts();

        ScriptEngine engine = getScript(quest_script_path);
        if (engine == null) {
            return false;
        }

        OdinNPCConversationManager cm = new OdinNPCConversationManager(client, npc_id, quest_script_id, (byte) 0, (Invocable) engine);
        cms.put(client, cm);
        engine.put("qm", cm);

        IScriptQuest script = ((Invocable) engine).getInterface(IScriptQuest.class);
        if (script == null) {
            return false;
        }

        client.getPlayer().setConversation(1);
        return startQuest(client, 1, 0, 0);
    }

    public boolean startQuest(TacosClient client, int mode, int type, int selection) {
        OdinNPCConversationManager cm = cms.get(client);

        if (cm == null || -1 < cm.getLastMsg()) {
            return false;
        }
        if (cm.pendingDisposal) {
            dispose(client);
            return false;
        }

        IScriptQuest script = ((Invocable) cm.getIv()).getInterface(IScriptQuest.class);
        script.start(mode, type, selection);
        return true;
    }

    public boolean endQuest(TacosClient client, int npc_id, int quest_script_id, boolean customEnd) {
        if (!customEnd && !MapleQuest.getInstance(quest_script_id).canComplete(client.getPlayer(), null)) {
            return false;
        }

        String quest_script_path = TacosScriptType.QUEST.get() + quest_script_id;
        clearScripts();
        ScriptEngine engine = getScript(quest_script_path);
        OdinNPCConversationManager cm = new OdinNPCConversationManager(client, npc_id, quest_script_id, (byte) 1, (Invocable) engine);
        cms.put(client, cm);
        engine.put("qm", cm);
        IScriptQuest script = ((Invocable) engine).getInterface(IScriptQuest.class);
        client.getPlayer().setConversation(1);

        if (script == null) {
            DebugLogger.ErrorLog("quest_script : endQuest not found, " + quest_script_id);
            return false;
        }

        return endQuest(client, 1, 0, 0);
    }

    public boolean endQuest(TacosClient client, int mode, int type, int selection) {
        OdinNPCConversationManager cm = cms.get(client);

        if (cm == null || -1 < cm.getLastMsg()) {
            return false;
        }
        if (cm.pendingDisposal) {
            dispose(client);
            return false;
        }

        IScriptQuest script = ((Invocable) cm.getIv()).getInterface(IScriptQuest.class);
        script.end(mode, type, selection);
        return true;
    }

    public boolean dispose(TacosClient client) {
        OdinNPCConversationManager npccm = cms.get(client);
        if (npccm == null) {
            client.getPlayer().setConversation(0);
            return false;
        }
        cms.remove(client);
        client.getPlayer().setConversation(0);
        return true;
    }

}
