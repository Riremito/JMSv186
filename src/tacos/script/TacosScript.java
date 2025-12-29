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
import odin.scripting.EventManager;
import odin.scripting.NPCConversationManager;
import tacos.debug.DebugLogger;
import tacos.property.Property_Java;

/**
 *
 * @author Riremito
 */
public class TacosScript {

    protected Map<MapleClient, NPCConversationManager> cms = new WeakHashMap<>();

    public NPCConversationManager getCM(MapleClient c) {
        return cms.get(c);
    }

    protected Map<String, EventManager> ems = new HashMap<>();

    public EventManager getEM(String event_name) {
        return ems.get(event_name);
    }

    protected Map<String, ScriptEngine> script_cache = new HashMap<>();

    protected ScriptEngine getScript(String script_name) {
        // cache
        if (this.script_cache.containsKey(script_name)) {
            DebugLogger.ScriptLog("getScript : " + script_name + " (cache)");
            return this.script_cache.get(script_name);
        }
        // add to cache
        ScriptEngine script = loadScript(script_name);
        this.script_cache.put(script_name, script);

        DebugLogger.ScriptLog("getScript : " + script_name);
        return script;
    }

    protected ScriptEngine loadScript(String script_name) {
        String script_full_path = Property_Java.getDir_Scripts() + script_name + ".js";
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
            DebugLogger.ErrorLog("loadScript : not found, " + script_name);
            return null;
        }
        // read file.
        FileReader fr = null;
        try {
            fr = new FileReader(scriptFile);
            engine.eval(fr);
        } catch (FileNotFoundException | ScriptException ex) {
            DebugLogger.ErrorLog("loadScript : script error, " + script_name);
            return null;
        }
        // close file.
        try {
            fr.close();
        } catch (IOException ex) {
            DebugLogger.ErrorLog("loadScript : close error, " + script_name);
            return null;
        }

        return engine;
    }

    public void clearScripts() {
        this.script_cache.clear();
    }

}
