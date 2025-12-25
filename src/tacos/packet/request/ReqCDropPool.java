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
package tacos.packet.request;

import java.util.LinkedList;
import java.util.List;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.constants.GameConstants;
import tacos.config.Region;
import tacos.config.Version;
import odin.handling.world.MaplePartyCharacter;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.life.MapleMonster;
import tacos.packet.ClientPacket;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapItem;
import odin.server.maps.MapleMapObject;
import odin.server.maps.MapleMapObjectType;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacketHeader;
import tacos.packet.response.ResCDropPool;
import tacos.packet.response.wrapper.ResWrapper;

/**
 *
 * @author Riremito
 */
public class ReqCDropPool {

    public static boolean OnPacket(MapleClient c, ClientPacketHeader header, ClientPacket cp) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        switch (header) {
            case CP_DropPickUpRequest: // CWvsContext::SendDropPickUpRequest
            {
                byte unk1 = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode1();
                int update_time = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
                short drop_x = cp.Decode2();
                short drop_y = cp.Decode2();
                int object_id = cp.Decode4();
                // CRC

                if (!OnDropPickUpRequest(chr, object_id)) {
                    chr.sendStatChanged(true);
                }
                chr.updateTick(update_time);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnDropPickUpRequest(MapleCharacter chr, int object_id) {
        MapleMapObject object = chr.getMap().getMapObject(object_id, MapleMapObjectType.ITEM);
        if (object == null) {
            DebugLogger.ErrorLog("PickUp : item null");
            return false;
        }
        MapleMapItem mapitem = (MapleMapItem) object;
        if (mapitem.isPickedUp()) {
            DebugLogger.ErrorLog("PickUp : isPickedUp");
            return false;
        }
        if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0) || (mapitem.isPlayerDrop() && chr.getMap().getEverlast()))) {
            DebugLogger.ErrorLog("PickUp : getOwner");
            return false;
        }
        if (!mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != chr.getId() && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
            DebugLogger.ErrorLog("PickUp : isPlayerDrop");
            return false;
        }
        // Meso
        if (mapitem.getMeso() > 0) {
            // ?_?
            if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                final List<MapleCharacter> toGive = new LinkedList<>();
                for (MaplePartyCharacter z : chr.getParty().getMembers()) {
                    MapleCharacter m = chr.getMap().getCharacterById(z.getId());
                    if (m != null) {
                        toGive.add(m);
                    }
                }
                for (final MapleCharacter m : toGive) {
                    m.gainMeso(mapitem.getMeso() / toGive.size() + (m.getStat().hasPartyBonus ? (int) (mapitem.getMeso() / 20.0) : 0), true, true);
                }
            } else {
                chr.gainMeso(mapitem.getMeso(), true, true);
            }
            removeDropItem(chr, mapitem);
            return true;
        }
        // item
        if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItem().getItemId())) {
            DebugLogger.ErrorLog("PickUp : isPickupBlocked");
            return false;
        }
        if (useDropItem(chr.getClient(), mapitem.getItemId())) {
            removeDropItem(chr, mapitem);
            DebugLogger.InfoLog("PickUp : useItem");
            return true;
        }
        if (!MapleInventoryManipulator.checkSpace(chr.getClient(), mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
            chr.SendPacket(ResWrapper.getShowInventoryFull());
            DebugLogger.ErrorLog("PickUp : checkSpace");
            return false;
        }
        if (!MapleInventoryManipulator.addFromDrop(chr.getClient(), mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster)) {
            DebugLogger.ErrorLog("PickUp : addFromDrop");
            return false;
        }
        removeDropItem(chr, mapitem);
        return true;
    }

    public static void removeDropItem(MapleCharacter chr, MapleMapItem mapitem) {
        removeDropItem(chr, mapitem, false, 0);
    }

    public static void removeDropItem(MapleCharacter chr, MapleMapItem mapitem, boolean is_pet, int pet_index) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(ResCDropPool.DropLeaveField(mapitem, is_pet ? ResCDropPool.LeaveType.PICK_UP_PET : ResCDropPool.LeaveType.PICK_UP, chr, pet_index), mapitem.getPosition());
        chr.getMap().removeMapObject(mapitem);
        if (mapitem.isRandDrop()) {
            chr.getMap().spawnRandDrop();
        }
    }

    public static boolean useDropItem(final MapleClient c, final int id) {
        if (GameConstants.isUse(id)) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final byte consumeval = ii.isConsumeOnPickup(id);
            if (consumeval > 0) {
                if (consumeval == 2) {
                    if (c.getPlayer().getParty() != null) {
                        for (final MaplePartyCharacter pc : c.getPlayer().getParty().getMembers()) {
                            final MapleCharacter chr = c.getPlayer().getMap().getCharacterById(pc.getId());
                            if (chr != null) {
                                ii.getItemEffect(id).applyTo(chr);
                            }
                        }
                    } else {
                        ii.getItemEffect(id).applyTo(c.getPlayer());
                    }
                } else {
                    ii.getItemEffect(id).applyTo(c.getPlayer());
                }
                c.SendPacket(ResWrapper.DropPickUpMessage(id, (byte) 1));
                return true;
            }
        }
        return false;
    }

}
