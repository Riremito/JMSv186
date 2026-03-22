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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import odin.client.MapleClient;
import tacos.client.TacosClient;
import tacos.odin.OdinEventManager;
import tacos.odin.OdinNPCConversationManager;
import tacos.debug.DebugLogger;
import tacos.property.Property_Java;

/**
 *
 * @author Riremito
 */
public class TacosScript {

    protected Map<MapleClient, OdinNPCConversationManager> cms = new WeakHashMap<>();

    public OdinNPCConversationManager getCM(MapleClient c) {
        return cms.get(c);
    }

    protected Map<String, OdinEventManager> ems = new HashMap<>();

    public OdinEventManager getEM(String event_name) {
        return ems.get(event_name);
    }

    protected void DebugMsg(TacosClient client, TacosScriptType type, int script_id) {
        client.getPlayer().DebugMsg("Script : " + type.get() + script_id + ".js");
    }

    protected void DebugMsg(TacosClient client, TacosScriptType type, String script_name) {
        client.getPlayer().DebugMsg("Script : " + type.get() + script_name + ".js");
    }

    protected Map<String, ScriptEngine> script_cache = new HashMap<>();

    protected ScriptEngine getScript(String script_name) {
        ScriptEngine script = null;
        String script_file_name = script_name + ".js";
        // cache
        if (this.script_cache.containsKey(script_file_name)) {
            script = this.script_cache.get(script_file_name);
            DebugLogger.ScriptLog("getScript : " + script_file_name + " (cache)" + ((script != null) ? "" : " = null"));
            return script;
        }
        // add to cache
        script = loadScript(script_file_name);
        this.script_cache.put(script_file_name, script);

        DebugLogger.ScriptLog("getScript : " + script_file_name + ((script != null) ? "" : " = null"));
        return script;
    }

    protected ScriptEngine loadScript(String script_file_name) {
        String script_full_path = Property_Java.getDir_Scripts() + script_file_name;
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByName("nashorn");

        // script engine.
        try {
            engine.eval("load('nashorn:mozilla_compat.js');" + System.lineSeparator());
        } catch (ScriptException ex) {
            DebugLogger.ErrorLog("loadScript : nashorn error");
            return null;
        }
        // open file.
        File scriptFile = new File(script_full_path);
        if (!scriptFile.exists()) {
            DebugLogger.ErrorLog("loadScript : not found, " + script_file_name);
            return null;
        }
        // read file.
        FileReader fr = null;
        try {
            fr = new FileReader(scriptFile);
            engine.eval(fr);
        } catch (FileNotFoundException | ScriptException ex) {
            DebugLogger.ErrorLog("loadScript : script error, " + script_file_name);
            return null;
        }
        // close file.
        try {
            fr.close();
        } catch (IOException ex) {
            DebugLogger.ErrorLog("loadScript : close error, " + script_file_name);
            return null;
        }

        return engine;
    }

    public void clearScripts() {
        this.script_cache.clear();
    }

}
