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

import tacos.server.map.object.TacosReactor;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import tacos.client.TacosClient;
import tacos.odin.OdinReactorActionManager;

/**
 *
 * @author Riremito
 */
public class TacosScriptReactor extends TacosScript {

    private static TacosScriptReactor instance = null;

    public static TacosScriptReactor getInstance() {
        if (instance == null) {
            instance = new TacosScriptReactor();
        }

        return instance;
    }

    public TacosScriptReactor() {

    }

    public boolean act(TacosClient client, TacosReactor reactor) {
        DebugMsg(client, TacosScriptType.REACOTR, reactor.getId());

        ScriptEngine engine = getScript(TacosScriptType.REACOTR.get() + reactor.getId());
        if (engine == null) {
            return false;
        }

        IScriptReactor script = ((Invocable) engine).getInterface(IScriptReactor.class);
        if (script == null) {
            return false;
        }

        OdinReactorActionManager rm = new OdinReactorActionManager(client, reactor);
        engine.put("rm", rm);
        // most of the scripts return nothing.
        script.act();
        return true;
    }

}
