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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package server.maps;

import client.MapleCharacter;
import client.MapleClient;
import handling.channel.ChannelServer;
import java.awt.Point;
import packet.response.Res_JMS_CInstancePortalPool;

/**
 *
 * @author Riremito
 */
// Pink Bean Cake Event TWMS/CMS/JMS dynamic portal, this is like mystic door
public class MapleDynamicPortal extends AbstractMapleMapObject {

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

    public final void warp(MapleCharacter chr) {
        int map_id_from = chr.getMapId();
        MapleMap map_to = ChannelServer.getInstance(chr.getClient().getChannel()).getMapFactory().getMap(map_id);
        MapleDynamicPortal dynamic_portal_to = map_to.findDynamicPortalLink(map_id_from);

        if (dynamic_portal_to != null) {
            // dynamic portal is there
            // currently not working, because SetField does not have xy coordinates
            chr.changeMap(map_to, dynamic_portal_to.getPosition());
        } else {
            // no dynamic portal
            chr.changeMap(map_to, map_to.getPortal(0).getPosition());
        }
    }

    public int getItemID() {
        return item_id;
    }

    public int getMapID() {
        return map_id;
    }

    // do not use spawn data
    public final void sendSpawnPacket(final MapleClient client) {
        client.SendPacket(Res_JMS_CInstancePortalPool.CreatePinkBeanEventPortal(this));
    }

    @Override
    public final void sendSpawnData(final MapleClient client) {
        //client.SendPacket(ItemPacket.CreatePinkBeanEventPortal(this));
    }

    @Override
    public final void sendDestroyData(final MapleClient client) {
    }

    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.DYNAMIC_PORTAL;
    }
}
