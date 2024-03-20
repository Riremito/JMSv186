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
package packet.client.request;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.PetCommand;
import client.inventory.PetDataFactory;
import constants.GameConstants;
import handling.channel.handler.InventoryHandler;
import handling.world.MaplePartyCharacter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import packet.client.ClientPacket;
import packet.client.request.struct.CMovePath;
import packet.server.response.LocalResponse;
import packet.server.response.PetResponse;
import packet.server.response.RemoteResponse;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleMonster;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObjectType;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Riremito
 */
public class PetRequest {

    // CUserPool::OnUserCommonPacket
    public static boolean OnPetPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }
        // between CP_BEGIN_PET and CP_END_PET and some packets
        switch (header) {
            // 期限切れデンデン使用時のステータス更新とPointShopへ入場準備
            case CP_UserDestroyPetItemRequest: {
                c.getPlayer().UpdateStat(true); // OK, CANCEL 有効化
                return true;
            }
            // SpawnPet
            case CP_UserActivatePetRequest: {
                int timestamp = cp.Decode4();
                short item_slot = cp.Decode2();
                byte flag = cp.Decode1();
                chr.spawnPet(item_slot, flag > 0 ? true : false);
                chr.updateTick(timestamp); // unused
                return true;
            }
            // MovePet
            case CP_PetMove: {
                OnMove(cp, chr);
                return true;
            }
            // PetChat
            case CP_PetAction: {
                return true;
            }
            // PetCommand
            case CP_PetInteractionRequest: {

                return true;
            }
            // Pickup_Pet
            case CP_PetDropPickUpRequest: {
                OnDropPickUp(cp, chr);
                return true;
            }
            // Pet_AutoPotion
            case CP_PetStatChangeItemUseRequest: {

                return true;
            }
            case CP_PetUpdateExceptionListRequest: {

                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean OnMove(ClientPacket cp, MapleCharacter chr) {
        int pet_index = cp.Decode4();
        MaplePet pet = chr.getPet(pet_index);
        MapleMap map = chr.getMap();

        if (pet == null || map == null) {
            return false;
        }

        CMovePath data = CMovePath.Decode(cp);
        pet.setStance(data.getAction());
        pet.setPosition(data.getEnd());
        map.broadcastMessage(chr, PetResponse.movePet(chr, pet_index, data), false);
        return true;
    }

    public static boolean OnDropPickUp(ClientPacket cp, MapleCharacter chr) {
        int pet_index = cp.Decode4();
        MaplePet pet = chr.getPet(pet_index);
        MapleMap map = chr.getMap();

        if (pet == null || map == null) {
            return false;
        }

        byte unk1 = cp.Decode1(); // unk
        int timestamp = cp.Decode4();
        short drop_x = cp.Decode2();
        short drop_y = cp.Decode2();
        int drop_id = cp.Decode4();
        int drop_CRC = cp.Decode4();
        short unk2 = cp.Decode2(); // unk
        // trap
        if ((drop_id % 13) == 0) {
            short pet_x = cp.Decode2();
            short pet_y = cp.Decode2();
            int pet_xy_CRC = cp.Decode4();
            int drop_xy_CRC = cp.Decode4();
        }

        MapleMapItem mapitem = (MapleMapItem) chr.getMap().getMapObject(drop_id, MapleMapObjectType.ITEM);
        if (mapitem == null) {
            return false;
        }

        Pickup_Pet(chr, mapitem, pet_index);
        chr.updateTick(timestamp);
        return true;
    }

    public static final void PetCommand(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int petIndex = slea.readInt();
        /*chr.getPetIndex(slea.readInt());*/
        if (petIndex == -1) {
            return;
        }
        MaplePet pet = chr.getPet(petIndex);
        if (pet == null) {
            return;
        }
        slea.skip(5);
        final byte command = slea.readByte();
        final PetCommand petCommand = PetDataFactory.getPetCommand(pet.getPetItemId(), (int) command);
        boolean success = false;
        if (Randomizer.nextInt(99) <= petCommand.getProbability()) {
            success = true;
            if (pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness() + petCommand.getIncrease();
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);
                    c.getSession().write(LocalResponse.showOwnPetLevelUp(petIndex));
                    chr.getMap().broadcastMessage(RemoteResponse.showPetLevelUp(chr, petIndex));
                }
                c.getSession().write(PetResponse.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition())));
            }
        }
        chr.getMap().broadcastMessage(chr, PetResponse.commandResponse(chr.getId(), command, petIndex, success, false), true);
    }

    public static final void Pet_AutoPotion(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        slea.skip(13);
        final byte slot = slea.readByte();
        if (chr == null || !chr.isAlive() || chr.getMapId() == 749040100 || chr.getMap() == null || chr.hasDisease(MapleDisease.POTION)) {
            return;
        }
        final IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        final long time = System.currentTimeMillis();
        if (chr.getNextConsume() > time) {
            chr.dropMessage(5, "You may not use this item yet.");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit()) || chr.getMapId() == 610030600) {
            //cwk quick hack
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
                if (chr.getMap().getConsumeItemCoolTime() > 0) {
                    chr.setNextConsume(time + (chr.getMap().getConsumeItemCoolTime() * 1000));
                }
            }
        } else {
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }

    public static final void PetFood(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        int previousFullness = 100;
        MaplePet pet = null;
        if (chr == null) {
            return;
        }
        for (final MaplePet pets : chr.getPets()) {
            if (pets.getSummoned()) {
                if (pets.getFullness() < previousFullness) {
                    previousFullness = pets.getFullness();
                    pet = pets;
                }
            }
        }
        if (pet == null) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        slea.skip(6);
        final int itemId = slea.readInt();
        boolean gainCloseness = false;
        if (Randomizer.nextInt(99) <= 50) {
            gainCloseness = true;
        }
        if (pet.getFullness() < 100) {
            int newFullness = pet.getFullness() + 30;
            if (newFullness > 100) {
                newFullness = 100;
            }
            pet.setFullness(newFullness);
            final byte index = chr.getPetIndex(pet);
            if (gainCloseness && pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness() + 1;
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);
                    c.getSession().write(LocalResponse.showOwnPetLevelUp(index));
                    chr.getMap().broadcastMessage(RemoteResponse.showPetLevelUp(chr, index));
                }
            }
            c.getSession().write(PetResponse.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition())));
            chr.getMap().broadcastMessage(c.getPlayer(), PetResponse.commandResponse(chr.getId(), (byte) 1, index, true, true), true);
        } else {
            if (gainCloseness) {
                int newCloseness = pet.getCloseness() - 1;
                if (newCloseness < 0) {
                    newCloseness = 0;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness < GameConstants.getClosenessNeededForLevel(pet.getLevel())) {
                    pet.setLevel(pet.getLevel() - 1);
                }
            }
            c.getSession().write(PetResponse.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition())));
            chr.getMap().broadcastMessage(chr, PetResponse.commandResponse(chr.getId(), (byte) 1, chr.getPetIndex(pet), false, true), true);
        }
        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemId, 1, true, false);
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static final void PetChat( /*final int petid, final short command, final String text, */final SeekableLittleEndianAccessor slea, MapleCharacter chr) throws UnsupportedEncodingException {
        int petid = slea.readInt();
        short command = slea.readShort();
        short text_length = slea.readShort();
        String text = new String(slea.read(text_length), "SHIFT_JIS");
        if (chr == null || chr.getMap() == null || petid < 0) {
            return;
        }
        chr.getMap().broadcastMessage(chr, PetResponse.petChat(chr.getId(), command, text, petid), true);
    }

    public static final void Pickup_Pet(MapleCharacter chr, MapleMapItem mapitem, int pet_index) {
        final Lock lock = mapitem.getLock();
        MapleClient c = chr.getClient();

        lock.lock();
        try {
            if (mapitem.isPickedUp()) {
                chr.SendPacket(MaplePacketCreator.getInventoryFull());
                return;
            }
            if (mapitem.getOwner() != chr.getId() && mapitem.isPlayerDrop()) {
                return;
            }
            if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0) || (mapitem.isPlayerDrop() && chr.getMap().getEverlast()))) {
                chr.SendPacket(MaplePacketCreator.enableActions());
                return;
            }
            if (!mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != chr.getId() && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
                chr.SendPacket(MaplePacketCreator.enableActions());
                return;
            }

            if (mapitem.getMeso() > 0) {
                if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                    final List<MapleCharacter> toGive = new LinkedList<MapleCharacter>();
                    final int splitMeso = mapitem.getMeso() * 40 / 100;
                    for (MaplePartyCharacter z : chr.getParty().getMembers()) {
                        MapleCharacter m = chr.getMap().getCharacterById(z.getId());
                        if (m != null && m.getId() != chr.getId()) {
                            toGive.add(m);
                        }
                    }
                    for (final MapleCharacter m : toGive) {
                        m.gainMeso(splitMeso / toGive.size() + (m.getStat().hasPartyBonus ? (int) (mapitem.getMeso() / 20.0) : 0), true);
                    }
                    chr.gainMeso(mapitem.getMeso() - splitMeso, true);
                } else {
                    chr.gainMeso(mapitem.getMeso(), true);
                }
                InventoryHandler.removeItem_Pet(chr, mapitem, pet_index);
            } else {
                if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItemId()) || mapitem.getItemId() / 10000 == 291) {
                    chr.SendPacket(MaplePacketCreator.enableActions());
                } else if (InventoryHandler.useItem(c, mapitem.getItemId())) {
                    InventoryHandler.removeItem_Pet(chr, mapitem, pet_index);
                } else if (MapleInventoryManipulator.checkSpace(c, mapitem.getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                    MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster);
                    InventoryHandler.removeItem_Pet(chr, mapitem, pet_index);
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
