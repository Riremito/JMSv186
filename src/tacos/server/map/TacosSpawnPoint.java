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
import odin.server.life.MapleLifeFactory;
import odin.server.life.MapleMonster;
import odin.server.maps.MapleMap;
import tacos.debug.DebugLogger;
import tacos.packet.ops.OpsMobAppear;
import tacos.wz.MapleData;
import tacos.wz.WzDataStorage;
import tacos.wz.WzDataTool;

/**
 *
 * @author Riremito
 */
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

    public boolean loadData(MapleData life) {
        this.node_id = Integer.parseInt(life.getName());
        this.cy = WzDataTool.getInt(life.getChildByPath("cy"));
        this.f = WzDataTool.getInt(life.getChildByPath("f"), 0);
        this.fh = WzDataTool.getInt(life.getChildByPath("fh"), 0);
        this.hide = WzDataTool.getInt(life.getChildByPath("hide"), 0);
        this.id = WzDataTool.getInt(life.getChildByPath("id"), 0);
        this.mobTime = WzDataTool.getInt(life.getChildByPath("mobTime"), 0) * 1000;
        this.rx0 = WzDataTool.getInt(life.getChildByPath("rx0"));
        this.rx1 = WzDataTool.getInt(life.getChildByPath("rx1"));
        //this.type = WzDataTool.getString(life.getChildByPath("type"));
        this.x = WzDataTool.getInt(life.getChildByPath("x"));
        this.y = WzDataTool.getInt(life.getChildByPath("y"));

        if (!WzDataStorage.MOB.check(this.id)) {
            DebugLogger.ErrorLog("TacosSpawnPoint : invalid mob id, " + this.id);
            return false;
        }

        //this.team = WzDataTool.getInt(life.getChildByPath("team"), -1);
        return true;
    }

    public int getId() {
        return this.id;
    }

    public int getMobTime() {
        return this.mobTime;
    }

    private MapleMonster monster = null;
    private long last_regen_time = 0;

    public MapleMonster regen(MapleMap map) {
        if (this.monster != null) {
            return null;
        }

        this.monster = MapleLifeFactory.getMonster(this.id);
        this.monster.setObjectId();
        this.monster.setMap(map); // TODO : remove from monster object.
        this.monster.setPosition(new Point(this.x, this.y));
        this.monster.setFootholdId(this.fh);
        this.monster.setHomeFoothold(this.fh);
        this.monster.setAT(OpsMobAppear.MOBAPPEAR_REGEN);
        this.monster.setATEx(OpsMobAppear.MOBAPPEAR_REGEN.get());

        //chr.SendPacket(ResCMobPool.MobEnterField(this.monster));
        //chr.SendPacket(ResCMobPool.MobChangeController(this.monster, false));
        this.last_regen_time = System.currentTimeMillis();
        return this.monster;
    }

    public MapleMonster getMonster() {
        return this.monster;
    }

    public void removeMonster() {
        this.monster = null;
        this.last_regen_time = System.currentTimeMillis();
    }

    public long getLastRegenTime() {
        return this.last_regen_time;
    }

    public String getInfo() {
        return String.format("%3d : id=%8d, f=%d, fh=%3d, xy=%5d,%5d, time=%d", node_id, id, f, fh, x, y, mobTime);
    }
}
