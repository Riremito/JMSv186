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

import java.util.concurrent.atomic.AtomicInteger;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import tacos.odin.OdinEventManager;
import tacos.debug.DebugLogger;
import tacos.property.Property_World;

/**
 *
 * @author Riremito
 */
public class TacosScriptEvent extends TacosScript {

    private static TacosScriptEvent instance = null;

    public static TacosScriptEvent getInstance() {
        if (instance == null) {
            instance = new TacosScriptEvent();
        }

        return instance;
    }

    // temporary written for s4nest.js.
    public TacosScriptEvent() {

    }

    public void loadAllEvents(int channel) {
        for (String event_name : Property_World.getEvents().split(",")) {
            TacosScriptEvent.getInstance().init(event_name, channel);
        }
    }

    public void unloadAllEvents(int channel) {
        for (String event_name : Property_World.getEvents().split(",")) {
            TacosScriptEvent.getInstance().cancelSchedule(event_name, channel);
        }
    }

    public OdinEventManager getEventManager(String event_name) {
        OdinEventManager em = ems.get(event_name);
        if (em == null) {
            DebugLogger.ErrorLog("event_script : get, " + event_name);
            return null;
        }
        return em;
    }

    private AtomicInteger runningInstanceMapId = new AtomicInteger(0);

    public int getNewInstanceMapId() {
        return this.runningInstanceMapId.addAndGet(1);
    }

    public boolean init(String event_name, int channel) {
        ScriptEngine engine = getScript(TacosScriptType.EVENT.get() + event_name);
        if (engine == null) {
            return false;
        }

        IScriptEvent script = ((Invocable) engine).getInterface(IScriptEvent.class);
        if (script == null) {
            return false;
        }

        String event_name_ch = event_name + "_" + channel;

        DebugLogger.ScriptLog("event_script : init, " + event_name);

        OdinEventManager em = new OdinEventManager(channel, (Invocable) engine, event_name);
        ems.remove(event_name);
        ems.put(event_name, em);
        engine.put("em", em);
        return script.init();
    }

    public boolean cancelSchedule(String event_name, int channel) {
        OdinEventManager em = ems.get(event_name);
        if (em == null) {
            DebugLogger.ErrorLog("event_script : cancelSchedule, " + event_name);
            return false;
        }

        DebugLogger.ScriptLog("event_script : cancelSchedule, " + event_name);
        IScriptEvent script = ((Invocable) em.getIv()).getInterface(IScriptEvent.class);
        return script.cancelSchedule();
    }

}
