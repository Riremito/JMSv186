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
package tacos.client;

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.server.MaplePortal;
import odin.server.maps.AbstractAnimatedMapleMapObject;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapFactory;
import odin.server.maps.MapleMapObjectType;
import tacos.config.Region;
import tacos.config.Version;
import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;
import tacos.network.MaplePacket;
import tacos.packet.ops.OpsMovePathAttr;
import tacos.packet.response.ResCStage;
import tacos.packet.response.ResCWvsContext;
import tacos.server.ServerOdinGame;

/**
 *
 * @author Riremito
 */
public class TacosCharacter extends AbstractAnimatedMapleMapObject {

    protected MapleClient client;
    protected MapleMap map;
    protected int dwPosMap;
    protected int nPortal;
    private TacosLastStat laststat = null;
    private int viewRange = 1600;
    private int viewRangeSq = 1600 * 1600;

    public void SendPacket(MaplePacket packet) {
        this.client.SendPacket(packet);
    }

    public int getViewRange() {
        return this.viewRange;
    }

    public void setViewRange(int viewRange) {
        this.viewRange = viewRange;
        this.viewRangeSq = viewRange * viewRange;
    }

    public int getViewRangeSq() {
        return this.viewRangeSq;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.PLAYER;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // enter game server.
    protected void sendSetField(MapleCharacter mchr, boolean bCharacterData) {
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            SendPacket(ResCStage.SetField_JMS_302(mchr, 1, bCharacterData, 0));
            SendPacket(ResCStage.SetField_JMS_302(mchr, 2, bCharacterData, -1));
            return;
        }

        SendPacket(ResCStage.SetField(mchr, bCharacterData));
    }

    public MapleMap getMap() {
        return this.map;
    }

    private void setMap(MapleMap map) {
        this.map = map;
    }

    public int getPosMap() {
        return this.dwPosMap;
    }

    private void setPosMap(int dwPosMap) {
        this.dwPosMap = dwPosMap;
    }

    public int getPortal() {
        return this.nPortal;
    }

    private void setPortal(int nPortal) {
        this.nPortal = nPortal;
    }

    public void updateMap(MapleMap map_to, MaplePortal portal_to) {
        setMap(map_to);
        setPosMap(map_to.getId());
        setPortal(portal_to.getId()); // spawn point
        setPosition(portal_to.getPosition()); // spawn point xy (server side), some version could not control spawn xy by packet.
        setFH(0); // foothold id is 0 while character is in the air.
        setStance(OpsMovePathAttr.MPA_NORMAL.get()); // default state (?)
    }

    public void updateMapById(int map_id, int portal_id) {
        MapleMapFactory mapFactory = ServerOdinGame.getInstance(client.getChannel()).getMapFactory();
        MapleMap map_to = mapFactory.getMap(map_id);

        if (map_to != null) {
            int forced_return_map_id = map_to.getForcedReturnId();
            if (forced_return_map_id != TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID) {
                map_to = map_to.getForcedReturnMap();
            }
        }
        if (map_to == null) {
            map_to = mapFactory.getMap(TacosConstants.DEFALT_RETURN_MAP_ID); // return to default map.
            DebugLogger.ErrorLog("updateMapById : invalid map = " + map_id);
        }
        MaplePortal portal_to = map_to.getPortal(portal_id);
        if (portal_to == null) {
            portal_to = map_to.getPortal(0);
            DebugLogger.ErrorLog("updateMapById : invalid portal = " + portal_id);
        }

        updateMap(map_to, portal_to);
    }

    // stat
    public void sendStatChanged(MapleCharacter mchr, boolean unlock) {
        if (this.laststat == null) {
            this.laststat = new TacosLastStat(mchr);
            return;
        }

        this.laststat.update(mchr);

        SendPacket(ResCWvsContext.StatChanged(mchr, unlock ? 1 : 0, this.laststat.getStatMask()));
        if (this.laststat.getStatMask() != 0) {
            mchr.equipChanged();
        }

        this.laststat.clearStatMask();
    }

    // old code.
    public int getMapId() {
        if (this.map != null) {
            return this.map.getId();
        }
        return this.dwPosMap;
    }
}
