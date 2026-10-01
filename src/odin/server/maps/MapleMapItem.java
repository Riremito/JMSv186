/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.server.maps;

import java.awt.Point;
import odin.client.inventory.Item;
import odin.client.MapleCharacter;
import tacos.server.map.object.TacosDrop;

public class MapleMapItem extends TacosDrop {

    private byte type;

    public MapleMapItem(Item item, Point position, MapleCharacter owner, byte type, boolean playerDrop) {
        this.type = type;
        super(item, 0, 0);
        setOwnerId(owner.getId());
        setPosition(position);
        setPlayerDrop(playerDrop);
    }

    public MapleMapItem(Item item, Point position, MapleCharacter owner, byte type, boolean playerDrop, int quest_id) {
        this.type = type;
        super(item, quest_id, 0);
        setOwnerId(owner.getId());
        setPosition(position);
        setPlayerDrop(playerDrop);
    }

    public MapleMapItem(int meso, Point position, MapleCharacter owner, byte type, boolean playerDrop) {
        this.type = type;
        super(null, 0, meso);
        setOwnerId(owner.getId());
        setPosition(position);
        setPlayerDrop(playerDrop);
    }

    public MapleMapItem(Point position, Item item) {
        this.type = 2;
        super(item, 0, 0);
        setOwnerId(0);
        setPosition(position);
    }

    public byte getDropType() {
        return type;
    }
}
