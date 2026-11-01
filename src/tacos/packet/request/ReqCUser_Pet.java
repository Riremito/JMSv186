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
package tacos.packet.request;

import tacos.server.map.object.TacosPet;
import tacos.packet.response.builder.PB_UserEffect;
import tacos.packet.ops.OpsUserEffect;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import tacos.server.map.object.TacosDrop;
import tacos.client.TacosClient;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import tacos.wz.ItemWz.PetCommand;
import tacos.config.Region;
import tacos.debug.DebugLogger;
import odin.handling.world.MapleParty.MaplePartyCharacter;
import java.util.LinkedList;
import java.util.List;
import tacos.packet.ClientPacket;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.ResCUser_Pet;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_InvOp;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.Randomizer;
import tacos.config.Config;
import tacos.packet.ClientPacketHeader;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class ReqCUser_Pet {

    private static final int EXCEPTION_LIST_MESO = 0x7FFFFFFF;

    public static boolean OnPetPacket(TacosClient client, ClientPacketHeader header, ClientPacket cp) {
        TacosCharacter chr = client.getPlayer();
        if (chr == null) {
            return true;
        }
        TacosMap map = chr.getMap();
        if (map == null) {
            return true;
        }
        TacosPet pet = null;
        if (Config.LessOrEqual(Region.KMS, 31) || Config.LessOrEqual(Region.JMS, 131)) {
            pet = chr.getPetByIndex(0); // JMS131
        } else if (Config.Between(Region.JMS, 147, 180) || Config.Equal(Region.GMS, 95)) {
            long pet_uid = cp.Decode8(); // JMS147-180
            pet = chr.getPetByUniqueId(pet_uid);
        } else if (Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186)) {
            int pet_index = cp.Decode4(); // JMS186
            pet = chr.getPetByIndex(pet_index);
        }
        if (pet == null) {
            DebugLogger.ErrorLog("OnPetPacket : no pet.");
            return true;
        }

        switch (header) {
            case CP_PetMove: {
                OnPetMove(chr, map, pet, cp);
                return true;
            }
            case CP_PetAction: {
                OnPetAction(chr, map, pet, cp);
                return true;
            }
            // PetCommand
            case CP_PetInteractionRequest: {
                return true;
            }
            case CP_PetDropPickUpRequest: {
                OnPetDropPickUpRequest(chr, pet, cp);
                return true;
            }
            case CP_PetStatChangeItemUseRequest: {
                OnStatChangeItemUse(chr, cp);
                return true;
            }
            case CP_PetUpdateExceptionListRequest: {
                byte item_count = cp.Decode1();
                for (int i = 0; i < item_count; i++) {
                    int item_id = cp.Decode4();
                }
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean OnPetFood(TacosCharacter chr, MapleInventoryType item_type, short item_slot, int item_id) {
        TacosPet pet = chr.getPetByIndex(0);
        TacosMap map = chr.getMap();

        if (pet == null || map == null) {
            return false;
        }

        Item toUse = chr.getInventory(item_type).getItem(item_slot);
        if (toUse == null || toUse.getItemId() != item_id || toUse.getQuantity() < 1) {
            chr.updateInv();
            return false;
        }

        int inc_fullness = WzXML.ITEM.getItemInfo(item_id).getInc();
        int pet_previous_level = pet.getLevel();
        pet.feed(inc_fullness, (item_type == MapleInventoryType.CASH) ? 100 : 10);
        MapleInventoryManipulator.removeById(chr, item_type, item_id, 1, true, false);
        chr.updateInv();

        // 情報更新
        chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.CASH, chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition())).build()));
        // pet level up
        if (pet_previous_level < pet.getLevel()) {
            chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_Pet, PB_UserEffect.builder().index(0).build()));
            map.splitSendPacket(chr, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_Pet, PB_UserEffect.builder().player(chr).index(0).build()), chr.getId());
        }
        return true;
    }

    // CVecCtrlPet::EndUpdateActive
    public static boolean OnPetMove(TacosCharacter chr, TacosMap map, TacosPet pet, ClientPacket cp) {
        byte unk = cp.Decode1(Config.GreaterOrEqual(Region.JMS, 302));

        ParseCMovePath move_path = new ParseCMovePath();
        if (move_path.Decode(cp)) {
            pet.update(move_path);
            map.splitSendPacket(chr, ResCUser_Pet.PetMove(chr, pet, move_path), chr.getId());
        }

        return true;
    }

    // CPet::DoAction
    public static boolean OnPetAction(TacosCharacter chr, TacosMap map, TacosPet pet, ClientPacket cp) {
        byte nType = cp.Decode1();
        byte nAction = cp.Decode1();
        String pet_message = cp.DecodeStr();

        map.splitSendPacket(chr, ResCUser_Pet.PetAction(chr, pet, nType, nAction, pet_message), chr.getId());
        return true;
    }

    // CPet::ParseCommand
    public static void OnPetInteractionRequest(TacosCharacter chr, ClientPacket cp) {
        int petIndex = cp.Decode4();

        if (petIndex == -1) {
            return;
        }
        TacosPet pet = chr.getPetByIndex(petIndex);
        if (pet == null) {
            return;
        }

        byte[] unk1 = cp.DecodeBuffer(5); // ?_?
        byte command = cp.Decode1();
        PetCommand petCommand = WzXML.ITEM.getPetCommand(pet.getPetItemId(), (int) command);
        boolean success = false;
        TacosMap map = chr.getMap();
        if (Randomizer.nextInt(99) <= petCommand.getProbability()) {
            success = true;
            if (pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness() + petCommand.getIncrease();
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness >= TacosPet.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);
                    chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_Pet, PB_UserEffect.builder().index(petIndex).build()));
                    map.splitSendPacket(chr, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_Pet, PB_UserEffect.builder().player(chr).index(petIndex).build()), chr.getId());
                }
                chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.CASH, chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition())).build()));
            }
        }

        map.splitSendPacket(chr, ResCUser_Pet.PetActionCommand(chr, pet, command, success, false), chr.getId());
    }

    // CPet::SendDropPickUpRequest
    public static boolean OnPetDropPickUpRequest(TacosCharacter chr, TacosPet pet, ClientPacket cp) {
        byte unk1 = cp.Decode1(); // unk
        int timestamp = cp.Decode4();
        short drop_x = cp.Decode2();
        short drop_y = cp.Decode2();
        int drop_id = cp.Decode4();
        int drop_CRC = Config.LessOrEqual(Region.JMS, 147) ? 0 : cp.Decode4();
        short unk2 = Region.KMST.check() ? 0 : cp.Decode2(); // unk
        // trap
        if ((drop_id % 13) == 0) {
            short pet_x = Config.LessOrEqual(Region.JMS, 147) ? 0 : cp.Decode2();
            short pet_y = Config.LessOrEqual(Region.JMS, 147) ? 0 : cp.Decode2();
            int pet_xy_CRC = Config.LessOrEqual(Region.JMS, 147) ? 0 : cp.Decode4();
            int drop_xy_CRC = Config.LessOrEqual(Region.JMS, 147) ? 0 : cp.Decode4();
        }

        TacosDrop mapitem = chr.getMap().getDropByOid(drop_id);
        if (mapitem == null || !mapitem.isVisible(chr)) {
            return false;
        }

        Pickup_Pet(chr, mapitem, chr.getPetIndex(pet));
        return true;
    }

    // CWvsContext::SendStatChangeItemUseRequestByPetQ
    public static boolean OnStatChangeItemUse(TacosCharacter chr, ClientPacket cp) {
        byte unk1 = cp.Decode1();
        int timestamp = cp.Decode4();
        short item_slot = cp.Decode2();
        int item_id = cp.Decode4();

        chr.useItem(item_slot, item_id);
        return true;
    }

    public static void Pickup_Pet(TacosCharacter chr, TacosDrop mapitem, int pet_index) {
        TacosClient client = chr.getClient();
        if (mapitem.getOwnerId() != chr.getId() && mapitem.isPlayerDrop()) {
            return;
        }
        TacosMap map = chr.getMap();
        if (mapitem.getOwnerId() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0) || (mapitem.isPlayerDrop() && map.getEverlast()))) {
            chr.updateInv();
            return;
        }
        if (!mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwnerId() != chr.getId() && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwnerId()) == null)) {
            chr.updateInv();
            return;
        }

        if (mapitem.getMeso() > 0) {
            if (chr.getParty() != null && mapitem.getOwnerId() != chr.getId()) {
                List<TacosCharacter> toGive = new LinkedList<>();
                int splitMeso = mapitem.getMeso() * 40 / 100;
                for (MaplePartyCharacter z : chr.getParty().getMembers()) {
                    TacosCharacter player = map.getPlayerById(z.getId());
                    if (player != null && player.getId() != chr.getId()) {
                        toGive.add(player);
                    }
                }
                for (TacosCharacter player : toGive) {
                    player.gainMeso(splitMeso / toGive.size() + (player.getStat().hasPartyBonus ? (int) (mapitem.getMeso() / 20.0) : 0), true);
                }
                chr.gainMeso(mapitem.getMeso() - splitMeso, true);
            } else {
                chr.gainMeso(mapitem.getMeso(), true);
            }
            ReqCDropPool.removeDropItem(chr, mapitem, true, pet_index);
        } else {
            if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItemId()) || mapitem.getItemId() / 10000 == 291) {
                chr.updateInv();
                return;
            }
            if (ReqCDropPool.useDropItem(chr, mapitem.getItemId())) {
                ReqCDropPool.removeDropItem(chr, mapitem, true, pet_index);
            } else if (MapleInventoryManipulator.checkSpace(chr, mapitem.getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                MapleInventoryManipulator.addFromDrop(chr, mapitem.getItem(), true, !mapitem.isPlayerDrop());
                ReqCDropPool.removeDropItem(chr, mapitem, true, pet_index);
            }
        }
    }
}
