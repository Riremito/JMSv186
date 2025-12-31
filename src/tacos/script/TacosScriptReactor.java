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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import odin.client.MapleClient;
import tacos.odin.OdinReactorActionManager;
import odin.server.maps.MapleReactor;
import odin.server.maps.ReactorDropEntry;
import tacos.database.query.DQ_ReactorDrops;

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

    private Map<Integer, List<ReactorDropEntry>> drops = new HashMap<>();

    public TacosScriptReactor() {

    }

    public boolean act(MapleClient c, MapleReactor reactor) {
        DebugMsg(c, TacosScriptType.REACOTR, reactor.getReactorId());

        ScriptEngine engine = getScript(TacosScriptType.REACOTR.get() + reactor.getReactorId());
        if (engine == null) {
            return false;
        }

        IScriptReactor script = ((Invocable) engine).getInterface(IScriptReactor.class);
        if (script == null) {
            return false;
        }

        OdinReactorActionManager rm = new OdinReactorActionManager(c, reactor);
        engine.put("rm", rm);
        script.act();
        return true;
    }

    public List<ReactorDropEntry> getDrops(int reactor_id) {
        if (this.drops.containsKey(reactor_id)) {
            return this.drops.get(reactor_id);
        }

        List<ReactorDropEntry> ret = DQ_ReactorDrops.getDrops(reactor_id);
        this.drops.put(reactor_id, ret);
        return ret;
    }

    public void clearDrops() {
        this.drops.clear();
    }

}
