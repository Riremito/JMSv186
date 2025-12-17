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

import odin.client.MapleClient;
import odin.server.maps.AbstractAnimatedMapleMapObject;
import odin.server.maps.MapleMapObjectType;
import tacos.network.MaplePacket;

/**
 *
 * @author Riremito
 */
public class TacosCharacter extends AbstractAnimatedMapleMapObject {

    protected MapleClient client;
    private int viewRange = 1600;
    private int viewRangeSq = 1600 * 1600;

    public void SendPacket(MaplePacket packet) {
        client.SendPacket(packet);
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

}
