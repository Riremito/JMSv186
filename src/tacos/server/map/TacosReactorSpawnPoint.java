/*
 * Copyright (C) 2026 Riremito
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
package tacos.server.map;

import java.awt.Point;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleReactor;
import tacos.debug.DebugLogger;
import tacos.wz.MapleData;
import tacos.wz.WzDataStorage;
import tacos.wz.WzDataTool;

/**
 *
 * @author Riremito
 */
public class TacosReactorSpawnPoint {

    private int node_id;
    private int f;
    private int id;
    private String name;
    private int reactorTime;
    private int x;
    private int y;

    public boolean loadData(MapleData md_reactor) {
        this.node_id = Integer.parseInt(md_reactor.getName());
        this.f = WzDataTool.getInt(md_reactor.getChildByPath("f"), 0);
        this.id = WzDataTool.getInt(md_reactor.getChildByPath("id"), 0);
        this.name = WzDataTool.getString(md_reactor.getChildByPath("name"), "");
        this.reactorTime = WzDataTool.getInt(md_reactor.getChildByPath("reactorTime"), 0);
        if (1 <= this.reactorTime) {
            this.reactorTime *= 1000;
        }
        this.x = WzDataTool.getInt(md_reactor.getChildByPath("x"));
        this.y = WzDataTool.getInt(md_reactor.getChildByPath("y"));

        if (!WzDataStorage.REACTOR.check(this.id)) {
            DebugLogger.ErrorLog("TacosReactorSpawnPoint : invalid reactor id, " + this.id);
            return false;
        }

        return true;
    }

    public int getReactorTime() {
        return this.reactorTime;
    }

    private MapleReactor reactor = null;
    private long last_regen_time = 0;

    public MapleReactor regen(MapleMap map) {
        if (this.reactor != null) {
            return null;
        }

        this.reactor = new MapleReactor(this.id);
        this.reactor.setFacingDirection(this.f);
        this.reactor.setName(this.name);
        this.reactor.setState((byte) 0);
        this.reactor.setPosition(new Point(this.x, this.y));
        this.reactor.setMap(map);
        this.last_regen_time = System.currentTimeMillis();
        return this.reactor;
    }

    public MapleReactor getReactor() {
        return this.reactor;
    }

    public void removeReactor() {
        this.reactor = null;
    }

    public long getLastRegenTime() {
        return this.last_regen_time;
    }
}
