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
import handling.channel.handler.MovementParse;
import handling.world.MaplePartyCharacter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import packet.client.ClientPacket;
import packet.server.response.LocalResponse;
import packet.server.response.PetResponse;
import packet.server.response.RemoteResponse;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleMonster;
import server.maps.FieldLimitType;
import server.maps.MapleMapItem;
import server.movement.LifeMovementFragment;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Riremito
 */
public class PetRequest {

    // CUserPool::OnUserCommonPacket
    public static boolean OnPetPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) {

        // between CP_BEGIN_PET and CP_END_PET
        switch (header) {
            case CP_PetMove: {

                return true;
            }
            case CP_PetAction: {

                return true;
            }
            case CP_PetInteractionRequest: {

                return true;
            }
            case CP_PetDropPickUpRequest: {

                return true;
            }
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

    public static final void SpawnPet(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        chr.updateTick(slea.readInt());
        chr.spawnPet(slea.readByte(), slea.readByte() > 0);
    }

    public static final void MovePet(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
        final int petId = slea.readInt();
        slea.skip(8);
        final List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 3);
        if (res != null && chr != null && res.size() != 0) {
            // map crash hack
            final int slot = petId;
            if (slot == -1) {
                return;
            }
            chr.getPet(slot).updatePosition(res);
            chr.getMap().broadcastMessage(chr, PetResponse.movePet(chr.getId(), petId, slot, res), false);
            if (chr.getPlayerShop() != null || chr.getConversation() > 0 || chr.getTrade() != null) {
                //hack
                return;
            }
            if (chr.getStat().hasVac && (chr.getStat().hasMeso || chr.getStat().hasItem)) {
                List<MapleMapItem> objects = chr.getMap().getAllItems();
                for (MapleMapItem mapitem : objects) {
                    final Lock lock = mapitem.getLock();
                    lock.lock();
                    try {
                        if (mapitem.isPickedUp()) {
                            continue;
                        }
                        if (mapitem.getOwner() != chr.getId() && mapitem.isPlayerDrop()) {
                            continue;
                        }
                        if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0) || (mapitem.isPlayerDrop() && chr.getMap().getEverlast()))) {
                            continue;
                        }
                        if (!mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != chr.getId() && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
                            continue;
                        }
                        if (mapitem.getMeso() > 0 && chr.getStat().hasMeso) {
                            if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                                final List<MapleCharacter> toGive = new LinkedList<MapleCharacter>();
                                for (MaplePartyCharacter mem : chr.getParty().getMembers()) {
                                    MapleCharacter m = chr.getMap().getCharacterById(mem.getId());
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
                            InventoryHandler.removeItem_Pet(chr, mapitem, slot);
                        } else if (chr.getStat().hasItem && MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItem().getItemId())) {
                            if (InventoryHandler.useItem(chr.getClient(), mapitem.getItemId())) {
                                InventoryHandler.removeItem_Pet(chr, mapitem, slot);
                            } else if (MapleInventoryManipulator.checkSpace(chr.getClient(), mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                                if (mapitem.getItem().getQuantity() >= 50 && GameConstants.isUpgradeScroll(mapitem.getItem().getItemId())) {
                                    chr.getClient().setMonitored(true); //hack check
                                }
                                if (MapleInventoryManipulator.addFromDrop(chr.getClient(), mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster)) {
                                    InventoryHandler.removeItem_Pet(chr, mapitem, slot);
                                }
                            }
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
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
}
