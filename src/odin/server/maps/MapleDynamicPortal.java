/*
 * Copyright (C) 2024 Riremito
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
package odin.server.maps;

import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import java.awt.Point;
import tacos.packet.response.Res_JMS_CInstancePortalPool;
import tacos.server.map.TacosPortal;

/**
 *
 * @author Riremito
 */
// Pink Bean Cake Event TWMS/CMS/JMS dynamic portal, this is like mystic door
public class MapleDynamicPortal {

    private Point position = new Point();
    private int objectId;

    public Point getPosition() {
        return new Point(position);
    }

    public void setPosition(Point position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int id) {
        this.objectId = id;
    }

    final private int item_id;
    final private int map_id;

    public MapleDynamicPortal(int item_id, int map_id, int x, int y) {
        super();
        this.item_id = item_id;
        this.map_id = map_id;
        setPosition(new Point(x, y));
    }

    public MapleDynamicPortal(int map_id, int x, int y) {
        super();
        this.item_id = 2420004; // or 2420000
        this.map_id = map_id;
        setPosition(new Point(x, y));
    }

    // test
    public final void warp(MapleCharacter chr) {
        int map_id_from = chr.getPosMap();
        MapleMap map_to = chr.findMap(map_id);
        map_to.findDynamicPortalLink(map_id_from);

        /*
        if (dynamic_portal_to != null) {
            chr.changeMapDynamicPortal(map_to, dynamic_portal_to.getPosition());
        }
         */
        // no dynamic portal
        TacosPortal spawn_point = map_to.getPortal(0);
        chr.changeMapInternal(map_to, spawn_point.getPosition(), spawn_point);
    }

    public int getItemID() {
        return item_id;
    }

    public int getMapID() {
        return map_id;
    }
}
