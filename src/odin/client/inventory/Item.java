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
package odin.client.inventory;

import odin.constants.GameConstants;

public class Item {

    protected MapleRing ring = null;
    private final int id;
    private int uniqueid = -1;
    private short position;
    private short quantity;
    private byte flag;
    private long expiration = -1;
    private MaplePet pet = null;
    private String owner = "";
    private String GameMaster_log = null;
    private String giftFrom = "";

    public Item(final int id, final short position, final short quantity, final byte flag, final int uniqueid) {
        super();
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = flag;
        this.uniqueid = uniqueid;
    }

    public Item(final int id, final short position, final short quantity, final byte flag) {
        super();
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = flag;
    }

    public Item(int id, byte position, short quantity) {
        super();
        this.id = id;
        this.position = position;
        this.quantity = quantity;
    }

    public Item copy() {
        final Item ret = new Item(id, position, quantity, flag, uniqueid);
        ret.pet = pet;
        ret.owner = owner;
        ret.GameMaster_log = GameMaster_log;
        ret.expiration = expiration;
        ret.giftFrom = giftFrom;
        return ret;
    }

    public final void setPosition(final short position) {
        this.position = position;

        if (pet != null) {
            pet.setInventoryPosition(position);
        }
    }

    public void setQuantity(final short quantity) {
        this.quantity = quantity;
    }

    /*
        0x0001 封印
        0x0002 滑り防止効果
        0x0004 寒気防止効果
        0x0008
        0x0010 1回交換可能
     */
    public final byte getFlag() {
        return flag;
    }

    public byte getType() {
        return 2; // An Item
    }

    public final String getOwner() {
        return owner;
    }

    public final void setOwner(final String owner) {
        this.owner = owner;
    }

    public final void setFlag(final byte flag) {
        this.flag = flag;
    }

    public final long getExpiration() {
        return expiration;
    }

    public final void setExpiration(final long expire) {
        this.expiration = expire;
    }

    public final String getGMLog() {
        return GameMaster_log;
    }

    public void setGMLog(final String GameMaster_log) {
        this.GameMaster_log = GameMaster_log;
    }

    public final int getUniqueId() {
        return uniqueid;
    }

    public final void setUniqueId(final int id) {
        this.uniqueid = id;
    }

    public final MaplePet getPet() {
        return pet;
    }

    public final void setPet(final MaplePet pet) {
        this.pet = pet;
    }

    public void setGiftFrom(String gf) {
        this.giftFrom = gf;
    }

    public String getGiftFrom() {
        return giftFrom;
    }

    public MapleRing getRing() {
        if (!GameConstants.isEffectRing(id) || getUniqueId() <= 0) {
            return null;
        }
        if (ring == null) {
            ring = MapleRing.loadFromDb(getUniqueId(), position < 0);
        }
        return ring;
    }

    public void setRing(MapleRing ring) {
        this.ring = ring;
    }

    public static int comparePosition(Item item1, Item item2) {
        if (Math.abs(item1.getPosition()) < Math.abs(item2.getPosition())) {
            return -1;
        }
        if (Math.abs(item1.getPosition()) == Math.abs(item2.getPosition())) {
            return 0;
        }
        return 1;
    }

    // used by script
    public final int getItemId() {
        return id;
    }

    // used by script
    public final short getPosition() {
        return position;
    }

    // used by script
    public final short getQuantity() {
        return quantity;
    }

}
