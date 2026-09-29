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
package tacos.server.map.object;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import odin.client.MapleCharacter;
import odin.server.shops.HiredMerchant;
import tacos.packet.ServerPacket;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import tacos.server.TacosRoom;

/**
 *
 * @author Riremito
 */
public class TacosMerchant extends TacosMapObject {

    private boolean open = false;
    private final MapleCharacter[] visitors = new MapleCharacter[4];
    private final List<String> visitors_names = new LinkedList<>();
    private final List<String> blacklist = new LinkedList<>();
    private final int room_id;

    public TacosMerchant(MapleCharacter owner) {
        TacosRoom room = owner.getWorld().createRoom();
        room.setMerchant(this);
        this.room_id = room.getId();
    }

    public int getRoomId() {
        return this.room_id;
    }

    public boolean enter(MapleCharacter visitor) {
        if (getOwnerId() == visitor.getId()) {
            setOpen(false);
            removeAllVisitors(0, 0);
            visitor.setPlayerShop((HiredMerchant) this);
            visitor.SendPacket(ResCMiniRoomBaseDlg.getHiredMerch(visitor, (HiredMerchant) this, false));
            return true;
        }
        if (!isOpen()) {
            visitor.DebugMsg("商店の主人が物品整理中でございます。もうしばらく後でご利用ください。");
            return false;
        }
        if (getFreeSlot() == -1) {
            visitor.DebugMsg("This shop has reached it's maximum capacity, please come by later.");
            return false;
        }
        if (isInBlackList(visitor.getName())) {
            visitor.DebugMsg("You have been banned from this store.");
            return false;
        }
        visitor.setPlayerShop((HiredMerchant) this);
        addVisitor(visitor);
        visitor.SendPacket(ResCMiniRoomBaseDlg.getHiredMerch(visitor, (HiredMerchant) this, false));
        return true;
    }

    public boolean leave(MapleCharacter visitor) {
        if (getOwnerId() == visitor.getId()) {
            setOpen(true);
            visitor.setPlayerShop(null);
            return true;
        }
        visitor.setPlayerShop(null);
        removeVisitor(visitor);
        return true;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return this.open;
    }

    private ArrayList<MapleCharacter> getAllVisitors() {
        ArrayList<MapleCharacter> ret = new ArrayList<>();
        for (MapleCharacter visitor : this.visitors) {
            if (visitor != null) {
                ret.add(visitor);
            }
        }
        return ret;
    }

    public void SendPacket(ServerPacket packet) {
        for (MapleCharacter visitor : getAllVisitors()) {
            visitor.SendPacket(packet);
        }
    }

    // merge
    public void broadcastToVisitors(ServerPacket packet) {
        SendPacket(packet);
    }

    public void update() {
        //getMap().broadcastMessage(ResCEmployeePool.EmployeeMiniRoomBalloon(this));
    }

    public int getMaxSize() {
        return visitors.length + 1;
    }

    public int getSize() {
        return getFreeSlot() == -1 ? getMaxSize() : getFreeSlot();
    }

    public MapleCharacter getVisitor(int num) {
        return this.visitors[num];
    }

    public void addVisitor(MapleCharacter visitor) {
        int i = getFreeSlot();
        if (0 < i) {
            SendPacket(ResCMiniRoomBaseDlg.shopVisitorAdd(visitor, i));
            visitors[i - 1] = visitor;
            if (getOwnerId() != visitor.getId()) {
                this.visitors_names.add(visitor.getName());
            }
            update();
        }
    }

    public void removeVisitor(MapleCharacter visitor) {
        byte slot = getVisitorSlot(visitor);
        if (0 < slot) {
            SendPacket(ResCMiniRoomBaseDlg.shopVisitorLeave(slot));
            visitors[slot - 1] = null;
            update();
        }
    }

    public byte getVisitorSlot(MapleCharacter visitor) {
        for (byte i = 0; i < visitors.length; i++) {
            if (visitors[i] != null && visitors[i].getId() == visitor.getId()) {
                return (byte) (i + 1);
            }
        }
        if (visitor.getId() == getOwnerId()) {
            return 0;
        }
        return -1;
    }

    public void removeAllVisitors(int error, int type) {
        for (int i = 0; i < visitors.length; i++) {
            MapleCharacter visitor = getVisitor(i);
            if (visitor != null) {
                if (type != -1) {
                    visitor.SendPacket(ResCMiniRoomBaseDlg.shopErrorMessage(error, type));
                }
                SendPacket(ResCMiniRoomBaseDlg.shopVisitorLeave(getVisitorSlot(visitor)));
                visitor.setPlayerShop(null);
                visitors[i] = null;
            }
        }
        update();
    }

    public List<AbstractMap.SimpleImmutableEntry<Byte, MapleCharacter>> getVisitors() {
        List<AbstractMap.SimpleImmutableEntry<Byte, MapleCharacter>> chrz = new LinkedList<>();
        for (byte i = 0; i < visitors.length; i++) { //include owner or no
            if (visitors[i] != null) {
                chrz.add(new AbstractMap.SimpleImmutableEntry<>((byte) (i + 1), visitors[i]));
            }
        }
        return chrz;
    }

    public byte getFreeSlot() {
        for (byte i = 0; i < visitors.length; i++) {
            if (visitors[i] == null) {
                return (byte) (i + 1);
            }
        }
        return -1;
    }

    public boolean isInBlackList(String bl) {
        return blacklist.contains(bl);
    }

    public void addBlackList(String bl) {
        blacklist.add(bl);
    }

    public void removeBlackList(String bl) {
        blacklist.remove(bl);
    }

    public void sendBlackList(MapleCharacter chr) {
        chr.SendPacket(ResCMiniRoomBaseDlg.MerchantBlackListView(blacklist));
    }

    public void sendVisitor(MapleCharacter chr) {
        chr.SendPacket(ResCMiniRoomBaseDlg.MerchantVisitorView(visitors_names));
    }
}
