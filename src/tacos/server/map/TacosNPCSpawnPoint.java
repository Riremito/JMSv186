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
import odin.server.life.MapleNPC;
import odin.server.maps.MapleMap;
import tacos.debug.DebugLogger;
import tacos.wz.MapWz;
import tacos.wz.WzDataStorage;
import tacos.wz.WzXML;
import tacos.wz.ids.DWI_Block;

/**
 *
 * @author Riremito
 */
public class TacosNPCSpawnPoint {

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
        this.rx0 = life.getRx0();
        this.rx1 = life.getRx1();
        this.x = life.getX();
        this.y = life.getY();

        if (!WzDataStorage.NPC.check(this.id)) {
            DebugLogger.ErrorLog("TacosNPCSpawnPoint : invalid npc id, " + this.id);
            return false;
        }

        return true;
    }

    public int getId() {
        return this.id;
    }

    private MapleNPC npc = null;

    public MapleNPC regen(MapleMap map) {
        if (this.npc != null) {
            return null;
        }

        this.npc = WzXML.NPC.createNpc(this.id);
        if (this.npc == null) {
            return null;
        }

        this.npc.setPosition(new Point(this.x, this.y));
        this.npc.setFootholdId(this.fh);
        this.npc.setF(npc.getF() == 1 ? 0 : 1);
        this.npc.setRx0(this.rx0);
        this.npc.setRx1(this.rx1);
        this.npc.setCy(this.cy);
        this.npc.setHide((this.hide != 0));

        if (this.hide != 0) {
            DebugLogger.InfoLog("loadLife : hidden npc, " + this.id);
        }
        if (DWI_Block.checkNpc(this.id)) {
            DebugLogger.InfoLog("loadLife : blocked npc, " + this.id);
            return null;
        }

        return this.npc;
    }

    public MapleNPC getNPC() {
        return this.npc;
    }

    public void removeNPC() {
        this.npc = null;
    }
}
