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
package server.maps;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import client.MapleCharacter;
import client.MapleClient;
import debug.Debug;
import java.lang.ref.WeakReference;
import packet.response.ResCTownPortalPool;
import server.MaplePortal;
import server.Randomizer;

public class MapleDoor extends AbstractMapleMapObject {

    private WeakReference<MapleCharacter> owner;
    private MapleMap map_town;
    private MaplePortal townPortal;
    private MapleMap map_field;
    private int skillId, ownerId;
    private boolean is_town_door;
    MapleDoor linked_door = null;
    private int map_id;
    private MapleMap map;

    public MapleDoor(final MapleCharacter owner, final Point targetPosition, final int skillId) {
        super();
        this.owner = new WeakReference<MapleCharacter>(owner);
        this.ownerId = owner.getId();
        this.map_field = owner.getMap();
        setPosition(targetPosition);
        this.map_town = this.map_field.getReturnMap();
        this.townPortal = getFreePortal();
        this.skillId = skillId;
        this.is_town_door = false;
        this.map_id = this.map_field.getId();
        this.map = owner.getMap();
    }

    public MapleDoor(final MapleDoor origDoor) {
        super();
        this.owner = new WeakReference<MapleCharacter>(origDoor.owner.get());
        this.map_town = origDoor.map_town;
        this.townPortal = origDoor.townPortal;
        this.map_field = origDoor.map_field;
        this.townPortal = origDoor.townPortal;
        this.skillId = origDoor.skillId;
        this.ownerId = origDoor.ownerId;
        setPosition(townPortal.getPosition());
        this.is_town_door = true;
        linked_door = origDoor;
        this.map_id = this.map_town.getId();
        this.map = this.map_town;
    }

    public MapleDoor getLink() {
        return linked_door;
    }

    public void setLink(MapleDoor linked_door) {
        this.linked_door = linked_door;
    }

    public MapleMap getMap() {
        return this.map;
    }

    public int getMapId() {
        return this.map_id;
    }

    public final int getSkillId() {
        return skillId;
    }

    public final int getOwnerId() {
        return ownerId;
    }

    public int getTownMapId() {
        return map_town.getId();
    }

    public int getFieldMapId() {
        return map_field.getId();
    }

    private final MaplePortal getFreePortal() {
        final List<MaplePortal> freePortals = new ArrayList<MaplePortal>();

        for (final MaplePortal port : map_town.getPortals()) {
            if (port.getType() == MaplePortal.DOOR_PORTAL) {
                freePortals.add(port);
                Debug.DebugLog("MYSTIC = " + (byte) port.getId());
            }
        }
        // already used
        for (final MapleMapObject obj : map_town.getAllDoorsThreadsafe()) {
            MapleDoor door = (MapleDoor) obj;
            freePortals.remove(door.getTownPortal());
        }
        if (freePortals.size() <= 0) {
            return null;
        }

        return freePortals.get(Randomizer.nextInt(freePortals.size()));
    }

    @Override
    public final void sendSpawnData(final MapleClient client) {
        if (getOwner() == null) {
            return;
        }
        if (map_field.getId() == client.getPlayer().getMapId() || getOwnerId() == client.getPlayer().getId() || (getOwner() != null && getOwner().getParty() != null && getOwner().getParty().getMemberById(client.getPlayer().getId()) != null)) {
            //client.getSession().write(MysticDoorResponse.spawnDoor(getOwnerId(), map_town.getId() == client.getPlayer().getMapId() ? townPortal.getPosition() : getPosition(), true));
            if (getOwner() != null && getOwner().getParty() != null && (getOwnerId() == client.getPlayer().getId() || getOwner().getParty().getMemberById(client.getPlayer().getId()) != null)) {
                //client.SendPacket(MysticDoorResponse.partyPortal(this));
            }
            //client.SendPacket(MysticDoorResponse.setMysticDoorInfo(this));
        }
    }

    @Override
    public final void sendDestroyData(final MapleClient client) {
        if (getOwner() == null) {
            return;
        }
        if (map_field.getId() == client.getPlayer().getMapId() || getOwnerId() == client.getPlayer().getId() || (getOwner() != null && getOwner().getParty() != null && getOwner().getParty().getMemberById(client.getPlayer().getId()) != null)) {
            if (getOwner().getParty() != null && (getOwnerId() == client.getPlayer().getId() || getOwner().getParty().getMemberById(client.getPlayer().getId()) != null)) {
                //client.SendPacket(MysticDoorResponse.resetPartyMysticDoorInfo());
            }
        }
        client.SendPacket(ResCTownPortalPool.removeDoor(this));
    }

    public final MapleCharacter getOwner() {
        return owner.get();
    }

    public final MapleMap getTown() {
        return map_town;
    }

    public final MaplePortal getTownPortal() {
        return townPortal;
    }

    public final MapleMap getTarget() {
        return map_field;
    }

    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.DOOR;
    }
}
