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
import tacos.packet.response.ResCDropPool;
import tacos.packet.response.ResCDropPool.DropLeaveType;
import tacos.server.map.TacosMap;
import tacos.server.map.object.TacosDrop;

public class MapleMapItem extends TacosDrop {

    protected Item item;
    protected Object dropper;
    protected int meso = 0;
    protected int questid = -1;
    protected byte type;
    protected boolean playerDrop;
    protected boolean randDrop = false;

    public MapleMapItem(Item item, Point position, Object dropper, MapleCharacter owner, byte type, boolean playerDrop) {
        this.item = item;
        this.dropper = dropper;
        this.type = type;
        this.playerDrop = playerDrop;
        setOwnerId(owner.getId());
        setPosition(position);
    }

    public MapleMapItem(Item item, Point position, Object dropper, MapleCharacter owner, byte type, boolean playerDrop, int questid) {
        this.item = item;
        this.dropper = dropper;
        this.type = type;
        this.playerDrop = playerDrop;
        this.questid = questid;
        setOwnerId(owner.getId());
        setPosition(position);
    }

    public MapleMapItem(int meso, Point position, Object dropper, MapleCharacter owner, byte type, boolean playerDrop) {
        this.item = null;
        this.dropper = dropper;
        this.meso = meso;
        this.type = type;
        this.playerDrop = playerDrop;
        setOwnerId(owner.getId());
        setPosition(position);
    }

    public MapleMapItem(Point position, Item item) {
        this.item = item;
        this.type = 2;
        this.playerDrop = false;
        this.randDrop = true;
        setOwnerId(0);
        setPosition(position);
    }

    public final Item getItem() {
        return item;
    }

    public final int getQuest() {
        return questid;
    }

    public final int getItemId() {
        if (getMeso() > 0) {
            return meso;
        }
        return item.getItemId();
    }

    public final Object getDropper() {
        return dropper;
    }

    public final int getMeso() {
        return meso;
    }

    public final boolean isPlayerDrop() {
        return playerDrop;
    }

    public byte getDropType() {
        return type;
    }

    public void expire(TacosMap map) {
        map.removeDrop(getObjectId());
        map.broadcastMessage(ResCDropPool.DropLeaveField(this, DropLeaveType.EXPIRED));
    }
}
