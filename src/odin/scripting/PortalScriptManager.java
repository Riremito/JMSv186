/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.scripting;

import odin.client.MapleCharacter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import odin.client.MapleClient;
import tacos.property.Property_Java;
import tacos.debug.DebugLogger;
import odin.server.MaplePortal;
import odin.tools.FileoutputUtil;

public class PortalScriptManager {

    private static final PortalScriptManager instance = new PortalScriptManager();
    private final Map<String, PortalScript> scripts = new HashMap<String, PortalScript>();
    private final static ScriptEngineFactory sef = new ScriptEngineManager().getEngineByName("nashorn").getFactory();

    public final static PortalScriptManager getInstance() {
        return instance;
    }

    private final PortalScript getPortalScript(final String scriptName) {
        /*
        if (scripts.containsKey(scriptName)) {
            return scripts.get(scriptName);
        }
         */

        final File scriptFile = new File(Property_Java.getDir_Scripts() + "portal/" + scriptName + ".js");

        /*
        if (!scriptFile.exists()) {
            scripts.put(scriptName, null);
            return null;
        }
         */
        FileReader fr = null;
        final ScriptEngine portal = sef.getScriptEngine();
        try {
            fr = new FileReader(scriptFile);
            CompiledScript compiled = ((Compilable) portal).compile(fr);
            compiled.eval();
        } catch (final Exception e) {
            System.err.println("Error executing Portalscript: " + scriptName + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Portal script. (" + scriptName + ") " + e);
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (final IOException e) {
                    System.err.println("ERROR CLOSING" + e);
                }
            }
        }
        final PortalScript script = ((Invocable) portal).getInterface(PortalScript.class);
        //scripts.put(scriptName, script);
        return script;
    }

    public boolean executePortalScript(MaplePortal portal, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        PortalScript script = getPortalScript(portal.getScriptName());
        String text = "Portal Script = " + portal.getScriptName() + ", MapID = " + chr.getMapId();

        chr.DebugMsg(text);

        if (script == null) {
            DebugLogger.ErrorLog(text);
            return false;
        }

        try {
            script.enter(new PortalPlayerInteraction(c, portal));
        } catch (Exception e) {
            DebugLogger.ExceptionLog(text);
            return false;
        }

        return true;
    }

    public final void clearScripts() {
        scripts.clear();
    }
}
