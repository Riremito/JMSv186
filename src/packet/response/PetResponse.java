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
package packet.response;

import client.MapleCharacter;
import client.inventory.IItem;
import client.inventory.MaplePet;
import handling.MaplePacket;
import packet.request.struct.CMovePath;
import packet.ServerPacket;
import packet.response.struct.GW_ItemSlotBase;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class PetResponse {

    /*
        @00B4 : LP_PetActivated, CUserLocal::OnPetActivated, CUserRemote::OnPetActivated
        @00B5 : LP_PetEvol
        @00B6 : LP_PetTransferField
        @00B7 : LP_PetMove
        @00B8 : LP_PetAction
        @00B9 : LP_PetNameChanged
        @00BA : LP_PetLoadExceptionList
        @00BB : LP_PetActionCommand
     */
    public enum DeActivatedMsg {
        // アイテムクリック時の動作だと思う
        PET_NO_MSG(0),
        // ペットはお腹がすいたので、家に帰ってしまいました。
        PET_WENT_BACK_HOME(1),
        // ペットが魔法の効力が切れて人形に戻りました。
        PET_TURNED_BACK_INTO_DOLL(2),
        // ここではペットが使用不可です。
        PET_COULD_NOT_USE_THIS_LOCATION(3),
        UNKNOWN(-1);

        private int value;

        DeActivatedMsg(int flag) {
            value = flag;
        }

        DeActivatedMsg() {
            value = -1;
        }

        public int get() {
            return value;
        }

        public static DeActivatedMsg find(int val) {
            for (final DeActivatedMsg o : DeActivatedMsg.values()) {
                if (o.get() == val) {
                    return o;
                }
            }
            return UNKNOWN;
        }
    }

    // showPet
    public static MaplePacket Activated(MapleCharacter chr, MaplePet pet, boolean spawn, DeActivatedMsg msg, boolean transfer_field) {
        ServerPacket sp = new ServerPacket(transfer_field ? ServerPacket.Header.LP_PetTransferField : ServerPacket.Header.LP_PetActivated);
        sp.Encode4(chr.getId());
        sp.Encode4(chr.getPetIndex(pet));
        sp.Encode1(spawn ? 1 : 0);

        if (spawn) {
            sp.Encode1(0);
            sp.Encode4(pet.getPetItemId());
            sp.EncodeStr(pet.getName());
            sp.Encode8(pet.getUniqueId()); // buffer
            sp.Encode2(pet.getPosition().x);
            sp.Encode2(pet.getPosition().y);
            sp.Encode1(pet.getStance());
            sp.Encode2(pet.getFh());
        } else {
            sp.Encode1(msg.get());
        }

        return sp.Get();
    }

    public static MaplePacket Activated(MapleCharacter chr, MaplePet pet) {
        return Activated(chr, pet, true, DeActivatedMsg.UNKNOWN, false);
    }

    public static MaplePacket Deactivated(MapleCharacter chr, MaplePet pet, DeActivatedMsg msg) {
        return Activated(chr, pet, false, msg, false);
    }

    public static MaplePacket TransferField(MapleCharacter chr, MaplePet pet) {
        return Activated(chr, pet, true, DeActivatedMsg.UNKNOWN, true);
    }

    public static final MaplePacket movePet(MapleCharacter chr, int pet_index, CMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PetMove);
        sp.Encode4(chr.getId());
        sp.Encode4(pet_index);
        sp.EncodeBuffer(data.get());
        return sp.Get();
    }

    public static final MaplePacket petChat(MapleCharacter chr, int pet_index, byte nType, byte nAction, String pet_message) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PetAction);
        sp.Encode4(chr.getId());
        sp.Encode4(pet_index);
        sp.Encode1(nType);
        sp.Encode1(nAction);
        sp.EncodeStr(pet_message);
        // post BB may have extra 1 bytes
        return sp.Get();
    }

    public static MaplePacket changePetName(MapleCharacter chr, int pet_index, String pet_name) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PetNameChanged);
        sp.Encode4(chr.getId());
        sp.Encode4(pet_index);
        sp.EncodeStr(pet_name);
        return sp.Get();
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

    public static final MaplePacket updatePet(final MaplePet pet, final IItem item) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_InventoryOperation);

        sp.Encode1(0);
        sp.Encode1(2);
        sp.Encode1(3);
        sp.Encode1(5);
        sp.Encode2(pet.getInventoryPosition());
        sp.Encode1(0);
        sp.Encode1(5);
        sp.Encode2(pet.getInventoryPosition());
        sp.EncodeBuffer(GW_ItemSlotBase.Encode(item));
        return sp.Get();
    }

}
