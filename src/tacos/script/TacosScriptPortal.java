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

import java.util.regex.Pattern;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import tacos.debug.DebugLogger;
import tacos.odin.OdinPortalPlayerInteraction;
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
        DebugMsg(c, TacosScriptType.PORTAL, portal.getScriptName());

        if (enterHook(c.getPlayer(), portal)) {
            DebugLogger.ScriptLog("enterHook : " + portal.getScriptName());
            return true;
        }

        ScriptEngine engine = getScript(TacosScriptType.PORTAL.get() + portal.getScriptName());
        if (engine == null) {
            return false;
        }

        IScriptPortal script = ((Invocable) engine).getInterface(IScriptPortal.class);
        if (script == null) {
            return false;
        }

        OdinPortalPlayerInteraction ppi = new OdinPortalPlayerInteraction(c, portal);
        return script.enter(ppi);
    }

    public boolean enterHook(MapleCharacter chr, TacosPortal portal) {
        if (portal.getScriptName().equals("market00")) {
            chr.getFreeMarketPortal().leave(chr);
            return true;
        }
        if (Pattern.compile("market(J|)(\\d+)").matcher(portal.getScriptName()).matches()) {
            chr.getFreeMarketPortal().enter(chr, portal);
            return true;
        }
        return false;
    }

}
