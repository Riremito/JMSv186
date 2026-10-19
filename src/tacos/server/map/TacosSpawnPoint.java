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
import lombok.Data;
import odin.server.life.MapleMonster;
import odin.server.maps.MapleMap;
import tacos.debug.DebugLogger;
import tacos.packet.ops.OpsMobAppear;
import tacos.wz.MapWz;
import tacos.wz.WzDataStorage;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
@Data
public class TacosSpawnPoint {

    private int node_id;
    private int cy;
    private int f;
    private int fh;
    private int hide;
    private int id;
    private int mobTime;
    private String type;
    private int rx0;
    private int rx1;
    private int team;
    private int x;
    private int y;

    public boolean loadData(MapWz.LifeInfo life) {
        this.node_id = life.getNodeId();
        this.cy = life.getCy();
        this.f = life.getF();
        this.fh = life.getFh();
        this.hide = life.getHide();
        this.id = life.getId();
        this.mobTime = life.getMobTime() * 1000;
        this.rx0 = life.getRx0();
        this.rx1 = life.getRx1();
        //this.type = life.getType();
        this.x = life.getX();
        this.y = life.getY();

        if (!WzDataStorage.MOB.check(this.id)) {
            DebugLogger.ErrorLog("TacosSpawnPoint : invalid mob id, " + this.id);
            return false;
        }

        //this.team = life.getInt("team", -1);
        return true;
    }

    private MapleMonster monster = null;
    private long lastRegenTime = 0;

    public MapleMonster regen(MapleMap map) {
        if (this.monster != null) {
            return null;
        }

        this.monster = WzXML.MOB.createMob(this.id);
        if (this.monster != null) {
            this.monster.setObjectId();
            this.monster.setMap(map); // TODO : remove from monster object.
            this.monster.setPosition(new Point(this.x, this.y));
            this.monster.setFootholdId(this.fh);
            this.monster.setHomeFoothold(this.fh);
            this.monster.setAT(OpsMobAppear.MOBAPPEAR_REGEN);
            this.monster.setATEx(OpsMobAppear.MOBAPPEAR_REGEN.get());
        }
        this.lastRegenTime = System.currentTimeMillis();
        return this.monster;
    }

    public void removeMonster() {
        this.monster = null;
        this.lastRegenTime = System.currentTimeMillis();
    }

    public long getLastRegenTime() {
        return this.lastRegenTime;
    }
}
