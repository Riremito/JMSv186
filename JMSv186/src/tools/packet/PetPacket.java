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
package tools.packet;

import client.inventory.IItem;
import java.util.List;

import client.inventory.MaplePet;
import client.MapleStat;
import client.MapleCharacter;
import handling.MaplePacket;
import packet.ServerPacket;
import server.movement.LifeMovementFragment;
import tools.MaplePacketCreator;
import tools.data.output.MaplePacketLittleEndianWriter;

public class PetPacket {

    private final static byte[] ITEM_MAGIC = new byte[]{(byte) 0x80, 5};

    public static final MaplePacket updatePet(final MaplePet pet, final IItem item) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(0);
        mplew.write(2);
        mplew.write(3);
        mplew.write(5);
        mplew.writeShort(pet.getInventoryPosition());
        mplew.write(0);
        mplew.write(5);
        mplew.writeShort(pet.getInventoryPosition());
        mplew.write(3);
        mplew.writeInt(pet.getPetItemId());
        mplew.write(1);
        mplew.writeLong(pet.getUniqueId());
        PacketHelper.addPetItemInfo(mplew, item, pet);
        return mplew.getPacket();
    }

    public static final MaplePacket showPet(final MapleCharacter chr, final MaplePet pet, final boolean remove, final boolean hunger) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_PetActivated.Get());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getPetIndex(pet));
        if (remove) {
            mplew.write(0);
            mplew.write(hunger ? 1 : 0);
        } else {
            mplew.write(1);
            mplew.write(0); //1?
            mplew.writeInt(pet.getPetItemId());
            mplew.writeMapleAsciiString(pet.getName());
            mplew.writeLong(pet.getUniqueId());
            mplew.writeShort(pet.getPos().x);
            mplew.writeShort(pet.getPos().y - 20);
            mplew.write(pet.getStance());
            mplew.writeInt(pet.getFh());
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }

    public static final MaplePacket removePet(final int cid, final int index) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_PetActivated.Get());
        mplew.writeInt(cid);
        mplew.writeInt(index);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static final MaplePacket movePet(final int cid, final int pid, final int slot, final List<LifeMovementFragment> moves) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_PetMove.Get());
        mplew.writeInt(cid);
        mplew.writeInt(slot);
        mplew.writeLong(pid);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static final MaplePacket petChat(final int cid, final int un, final String text, final int slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_PetAction.Get());
        mplew.writeInt(cid);
        mplew.writeInt(slot);
        mplew.writeShort(un);
        mplew.writeMapleAsciiString(text);
        mplew.write(0); //hasQuoteRing

        return mplew.getPacket();
    }

    public static final MaplePacket commandResponse(final int cid, final byte command, final int slot, final boolean success, final boolean food) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_PetActionCommand.Get());
        mplew.writeInt(cid);
        mplew.writeInt(slot);
        mplew.write(command == 1 ? 1 : 0);
        mplew.write(command);
        if (command == 1) {
            mplew.write(0);

        } else {
            mplew.writeShort(success ? 1 : 0);
        }
        return mplew.getPacket();
    }

    public static final MaplePacket showOwnPetLevelUp(final int index) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.Get());
        mplew.write(4);
        mplew.write(0);
        mplew.writeInt(index); // Pet Index

        return mplew.getPacket();
    }

    public static final MaplePacket showPetLevelUp(final MapleCharacter chr, final int index) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserEffectRemote.Get());
        mplew.writeInt(chr.getId());
        mplew.write(4);
        mplew.write(0);
        mplew.writeInt(index);

        return mplew.getPacket();
    }

    public static final MaplePacket emptyStatUpdate() {
        return MaplePacketCreator.enableActions();
    }

    public static final MaplePacket petStatUpdate_Empty() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_StatChanged.Get());
        mplew.write(0);
        mplew.writeInt(MapleStat.PET.getValue());
        mplew.writeZeroBytes(25);
        return mplew.getPacket();
    }

    public static final MaplePacket petStatUpdate(final MapleCharacter chr) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_StatChanged.Get());
        mplew.write(0);
        mplew.writeInt(MapleStat.PET.getValue());

        byte count = 0;
        for (final MaplePet pet : chr.getPets()) {
            if (pet.getSummoned()) {
                mplew.writeLong(pet.getUniqueId());
                count++;
            }
        }
        while (count < 3) {
            mplew.writeZeroBytes(8);
            count++;
        }
        mplew.write(0);

        return mplew.getPacket();
    }
}
