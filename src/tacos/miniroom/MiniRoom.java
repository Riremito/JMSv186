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
package tacos.miniroom;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import tacos.client.TacosCharacter;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsMRLeave;
import tacos.packet.ops.OpsMiniRoomType;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import tacos.packet.response.ResCUser;
import tacos.server.map.TacosMap;

/**
 *
 * @author Riremito
 */
public abstract class MiniRoom {

    private final TacosCharacter owner;
    private final TacosCharacter[] visitors;
    private final TacosMap map;
    private final int itemId;
    private final String description;
    private int objectId = 0;

    protected MiniRoom(TacosCharacter owner, int itemId, String description, int max_visitors) {
        this.owner = owner;
        this.visitors = new TacosCharacter[max_visitors];
        this.map = owner.getMap();
        this.itemId = itemId;
        this.description = (description != null) ? description : "";
    }

    // m_nMiniRoomType
    public abstract OpsMiniRoomType getMiniRoomType();

    // m_bPrivate
    public abstract boolean isPrivate();

    // m_bGameOn
    public abstract boolean isGameOn();

    // the character does not belong to this room anymore.
    protected abstract void detach(TacosCharacter chr);

    public TacosCharacter getOwner() {
        return this.owner;
    }

    public boolean isOwner(TacosCharacter chr) {
        return this.owner.getId() == chr.getId();
    }

    public TacosMap getMap() {
        return this.map;
    }

    public int getObjectId() {
        return this.objectId;
    }

    public void setObjectId(int object_id) {
        this.objectId = object_id;
    }

    public int getItemId() {
        return this.itemId;
    }

    public String getDescription() {
        return this.description;
    }

    public int getMaxUsers() {
        return this.visitors.length + 1;
    }

    public int getUserCount() {
        return getVisitors().size() + 1;
    }

    public boolean isFull() {
        return getFreeSlot() == -1;
    }

    // 0 = owner, 1 or more = visitor, -1 = not in this room
    public int getSlot(TacosCharacter chr) {
        if (isOwner(chr)) {
            return 0;
        }
        for (int i = 0; i < this.visitors.length; i++) {
            if (this.visitors[i] != null && this.visitors[i].getId() == chr.getId()) {
                return i + 1;
            }
        }
        return -1;
    }

    // slot 0 = owner
    public TacosCharacter getUser(int slot) {
        if (slot == 0) {
            return this.owner;
        }
        if (slot < 1 || this.visitors.length < slot) {
            return null;
        }
        return this.visitors[slot - 1];
    }

    public List<SimpleImmutableEntry<Byte, TacosCharacter>> getVisitors() {
        List<SimpleImmutableEntry<Byte, TacosCharacter>> ret = new ArrayList<>();
        for (int i = 0; i < this.visitors.length; i++) {
            if (this.visitors[i] != null) {
                ret.add(new SimpleImmutableEntry<>((byte) (i + 1), this.visitors[i]));
            }
        }
        return ret;
    }

    private int getFreeSlot() {
        for (int i = 0; i < this.visitors.length; i++) {
            if (this.visitors[i] == null) {
                return i + 1;
            }
        }
        return -1;
    }

    // the owner and all visitors.
    public void broadcast(ServerPacket packet) {
        this.owner.SendPacket(packet);
        for (TacosCharacter visitor : this.visitors) {
            if (visitor != null) {
                visitor.SendPacket(packet);
            }
        }
    }

    public void broadcast(ServerPacket packet, int except_slot) {
        for (int slot = 0; slot < getMaxUsers(); slot++) {
            TacosCharacter user = getUser(slot);
            if (user != null && slot != except_slot) {
                user.SendPacket(packet);
            }
        }
    }

    // MRP_Chat
    public void chat(TacosCharacter chr, String message) {
        broadcast(ResCMiniRoomBaseDlg.MiniRoom_MRP_Chat(getSlot(chr), chr.getName() + " : " + message));
    }

    // MRP_Enter for the users already in this room.
    protected ServerPacket getEnterPacket(int slot, TacosCharacter visitor) {
        return ResCMiniRoomBaseDlg.MiniRoom_MRP_Enter(slot, visitor);
    }

    // the slot of the visitor, or -1 when this room is full.
    protected int addVisitor(TacosCharacter visitor) {
        int slot = getFreeSlot();
        if (slot <= 0) {
            return -1;
        }
        broadcast(getEnterPacket(slot, visitor));
        this.visitors[slot - 1] = visitor;
        return slot;
    }

    // the dialog of the visitor is already closed.
    protected void removeVisitor(TacosCharacter visitor) {
        int slot = getSlot(visitor);
        if (slot <= 0) {
            return;
        }
        this.visitors[slot - 1] = null;
        detach(visitor);
        broadcast(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(slot));
    }

    // the dialog of the visitor is closed with the reason.
    protected void kickVisitor(TacosCharacter visitor, OpsMRLeave reason) {
        int slot = getSlot(visitor);
        if (slot <= 0) {
            return;
        }
        // the reason is read only for the own slot.
        visitor.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(slot, reason));
        removeVisitor(visitor);
    }

    protected void kickAllVisitors(OpsMRLeave reason) {
        for (TacosCharacter visitor : this.visitors) {
            if (visitor != null) {
                kickVisitor(visitor, reason);
            }
        }
    }

    // the number of users, or no balloon after the room is closed.
    public void updateBalloon() {
        this.map.splitSendPacket(this.owner, ResCUser.UserMiniRoomBalloon(this.owner));
    }
}
