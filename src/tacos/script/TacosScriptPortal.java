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
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.scripting.PortalPlayerInteraction;
import tacos.debug.DebugLogger;
import tacos.server.map.TacosPortal;

/**
 *
 * @author Riremito
 */
public class TacosScriptPortal extends TacosScript {

    private static TacosScriptPortal instance = null;

    public static TacosScriptPortal getInstance() {
        if (instance == null) {
            instance = new TacosScriptPortal();
        }

        return instance;
    }

    public TacosScriptPortal() {

    }

    public boolean enter(TacosPortal portal, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        String text = "Portal Script = " + portal.getScriptName() + ", MapID = " + chr.getPosMap();
        chr.DebugMsg(text);

        ScriptEngine engine = getScript(TacosScriptType.PORTAL.get() + portal.getScriptName());
        IScriptPortal script = ((Invocable) engine).getInterface(IScriptPortal.class);

        if (script == null) {
            DebugLogger.ErrorLog("portal_script : executePortalScript not found, " + portal.getScriptName());
            return false;
        }

        PortalPlayerInteraction ppi = new PortalPlayerInteraction(c, portal);
        return script.enter(ppi);
    }

}
