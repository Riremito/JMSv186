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
import tacos.client.TacosClient;
import odin.constants.GameConstants;
import tacos.config.Region;
import odin.handling.world.MaplePartyCharacter;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.life.MapleMonster;
import tacos.packet.ClientPacket;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapItem;
import tacos.config.Config;
import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.ResCDropPool;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_UserEffect;
import tacos.packet.ops.OpsMessage;
import tacos.packet.ops.OpsDropPickUpMessage;
import tacos.packet.response.builder.PB_Message;

/**
 *
 * @author Riremito
 */
public class ReqCDropPool {

    public static boolean OnPacket(TacosClient client, ClientPacketHeader header, ClientPacket cp) {
        MapleCharacter chr = client.getPlayer();
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
                byte unk1 = Config.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode1();
                int update_time = Config.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
                short drop_x = cp.Decode2();
                short drop_y = cp.Decode2();
                int object_id = cp.Decode4();
                // CRC
                if (!OnDropPickUpRequest(chr, object_id)) {
                    chr.sendStatChanged(true);
                }
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnDropPickUpRequest(MapleCharacter chr, int object_id) {
        MapleMapItem mapitem = chr.getMap().findDrop(object_id);
        if (mapitem == null) {
            DebugLogger.ErrorLog("PickUp : item null");
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
                    MapleCharacter player = chr.getMap().getPlayerById(z.getId());
                    if (player != null) {
                        toGive.add(player);
                    }
                }
                for (final MapleCharacter player : toGive) {
                    player.gainMeso(mapitem.getMeso() / toGive.size() + (player.getStat().hasPartyBonus ? (int) (mapitem.getMeso() / 20.0) : 0), true, true);
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
        // monster book.
        int drop_item_id = mapitem.getItemId();
        if (TacosConstants.is_monster_card(drop_item_id)) {
            MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
            // Item.wz/Consume/0238.img/02380000/info/spec/consumeOnPickup = 1
            if (miip.isConsumeOnPickup(drop_item_id) == 1) {
                if (chr.getMonsterBook().add(drop_item_id, mapitem.getItem().getQuantity())) {
                    int nCardID = drop_item_id;
                    int nCardCount = chr.getMonsterBook().getCardCount(nCardID);
                    chr.SendPacket(ResCWvsContext.MonsterBookSetCard(true, nCardID, nCardCount));

                    PB_UserEffect pb = PB_UserEffect.builder()
                            .player(chr)
                            .build();
                    chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_MonsterBookCardGet));
                    chr.getMap().broadcastMessage(chr, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_MonsterBookCardGet, pb), false);

                    chr.SendPacket(ResCWvsContext.Message(OpsMessage.MS_DropPickUpMessage, PB_Message.builder().dt(OpsDropPickUpMessage.PICKUP_MONSTER_CARD).ItemID(nCardID).build()));
                } else {
                    chr.SendPacket(ResCWvsContext.MonsterBookSetCard(false, 0, 0));
                }
                removeDropItem(chr, mapitem);
                chr.SendPacket(ResCWvsContext.Message(OpsMessage.MS_DropPickUpMessage, PB_Message.builder().dt(OpsDropPickUpMessage.PICKUP_ITEM).ItemID(drop_item_id).Inc_ItemCount(mapitem.getItem().getQuantity()).build()));
                chr.updateInv();
                return true;
            }
        }
        if (useDropItem(chr, mapitem.getItemId())) {
            removeDropItem(chr, mapitem);
            DebugLogger.InfoLog("PickUp : useItem");
            return true;
        }
        if (!MapleInventoryManipulator.checkSpace(chr.getClient(), mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
            chr.SendPacket(ResCWvsContext.Message(OpsMessage.MS_DropPickUpMessage, PB_Message.builder().dt(OpsDropPickUpMessage.PICKUP_INVENTORY_FULL).build()));
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
        chr.getMap().removeDrop(mapitem.getObjectId());
        chr.getMap().broadcastMessage(ResCDropPool.DropLeaveField(mapitem, is_pet ? ResCDropPool.DropLeaveType.PET : ResCDropPool.DropLeaveType.NORMAL, chr, pet_index), mapitem.getPosition());
    }

    public static boolean useDropItem(MapleCharacter chr, int id) {
        if (GameConstants.isUse(id)) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            byte consumeval = ii.isConsumeOnPickup(id);
            if (consumeval > 0) {
                if (consumeval == 2) {
                    if (chr.getParty() != null) {
                        for (MaplePartyCharacter pc : chr.getParty().getMembers()) {
                            MapleCharacter chr_to = chr.getMap().getPlayerById(pc.getId());
                            if (chr_to != null) {
                                ii.getItemEffect(id).applyTo(chr_to);
                            }
                        }
                    } else {
                        ii.getItemEffect(id).applyTo(chr);
                    }
                } else {
                    ii.getItemEffect(id).applyTo(chr);
                }
                chr.SendPacket(ResCWvsContext.Message(OpsMessage.MS_DropPickUpMessage, PB_Message.builder().dt(OpsDropPickUpMessage.PICKUP_ITEM).ItemID(id).Inc_ItemCount((byte) 1).build()));
                return true;
            }
        }
        return false;
    }
}
