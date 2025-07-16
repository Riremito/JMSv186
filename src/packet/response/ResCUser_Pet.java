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
import client.inventory.MaplePet;
import config.Region;
import config.Version;
import server.network.MaplePacket;
import packet.request.parse.ParseCMovePath;
import packet.ServerPacket;
import packet.response.data.DataCPet;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCUser_Pet {

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
        ServerPacket sp = new ServerPacket((transfer_field || Version.LessOrEqual(Region.JMS, 131)) ? ServerPacket.Header.LP_PetTransferField : ServerPacket.Header.LP_PetActivated);
        sp.Encode4(chr.getId());
        if (Version.LessOrEqual(Region.JMS, 131)) {
            // no data
        } else {
            sp.Encode4(chr.getPetIndex(pet));
        }
        sp.Encode1(spawn ? 1 : 0);

        if (spawn) {
            if (Version.LessOrEqual(Region.JMS, 131)) {
                // no data
            } else {
                sp.Encode1(0);
            }
            sp.EncodeBuffer(DataCPet.Init(pet));
        } else {
            sp.Encode1(msg.get());
        }

        return sp.get();
    }

    public static MaplePacket Activated(MapleCharacter chr, MaplePet pet) {
        return Activated(chr, pet, true, DeActivatedMsg.PET_NO_MSG, false);
    }

    public static MaplePacket Deactivated(MapleCharacter chr, MaplePet pet, DeActivatedMsg msg) {
        return Activated(chr, pet, false, msg, false);
    }

    public static MaplePacket TransferField(MapleCharacter chr, MaplePet pet) {
        return Activated(chr, pet, true, DeActivatedMsg.PET_NO_MSG, true);
    }

    public static final MaplePacket movePet(MapleCharacter chr, int pet_index, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PetMove);
        sp.Encode4(chr.getId());
        sp.Encode4(pet_index);
        sp.EncodeBuffer(data.get());
        return sp.get();
    }

    public static final MaplePacket petChat(MapleCharacter chr, int pet_index, byte nType, byte nAction, String pet_message) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PetAction);
        sp.Encode4(chr.getId());
        sp.Encode4(pet_index);
        sp.Encode1(nType);
        sp.Encode1(nAction);
        sp.EncodeStr(pet_message);
        // post BB may have extra 1 bytes
        return sp.get();
    }

    public static MaplePacket changePetName(MapleCharacter chr, int pet_index, String pet_name) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PetNameChanged);
        sp.Encode4(chr.getId());
        sp.Encode4(pet_index);
        sp.EncodeStr(pet_name);
        return sp.get();
    }

    public static final MaplePacket commandResponse(final int cid, final byte command, final int slot, final boolean success, final boolean food) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_PetActionCommand.get());
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

}
