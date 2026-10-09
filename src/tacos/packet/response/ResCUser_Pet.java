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
package tacos.packet.response;

import tacos.client.TacosCharacter;
import tacos.config.Config;
import tacos.config.Region;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.response.data.RD_CUser;
import tacos.server.map.object.TacosPet;

/**
 *
 * @author Riremito
 */
public class ResCUser_Pet {

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
    public static ServerPacket PetActivated(TacosCharacter chr, TacosPet pet, boolean spawn, DeActivatedMsg msg, boolean transfer_field) {
        ServerPacket sp = new ServerPacket((transfer_field || Config.LessOrEqual(Region.JMS, 131)) ? ServerPacketHeader.LP_PetTransferField : ServerPacketHeader.LP_PetActivated);

        sp.Encode4(chr.getId());

        if (Config.Equal(Region.JMS, 147)) {
            sp.Encode1(chr.getPetIndex(pet));
            if (!transfer_field) {
                sp.Encode1(spawn ? 1 : 0);
            }
            if (!spawn) {
                sp.Encode1(msg.get());
                return sp;
            }
            sp.Encode1(0);
            sp.EncodeBuffer(RD_CUser.CPet_Init(pet));
            if (transfer_field) {
                sp.Encode2(0);
            }
            return sp;
        }

        if (Config.LessOrEqual(Region.KMS, 31) || Config.LessOrEqual(Region.JMS, 131)) {
            // no data
        } else {
            sp.Encode4(chr.getPetIndex(pet));
        }
        sp.Encode1(spawn ? 1 : 0);

        if (spawn) {
            if (Config.LessOrEqual(Region.KMS, 31) || Config.LessOrEqual(Region.JMS, 131)) {
                // no data
            } else {
                sp.Encode1(0);
            }
            sp.EncodeBuffer(RD_CUser.CPet_Init(pet));
        } else {
            sp.Encode1(msg.get());
        }

        return sp;
    }

    public static ServerPacket Activated(TacosCharacter chr, TacosPet pet) {
        return PetActivated(chr, pet, true, DeActivatedMsg.PET_NO_MSG, false);
    }

    public static ServerPacket Deactivated(TacosCharacter chr, TacosPet pet, DeActivatedMsg msg) {
        return PetActivated(chr, pet, false, msg, false);
    }

    public static ServerPacket TransferField(TacosCharacter chr, TacosPet pet) {
        return PetActivated(chr, pet, true, DeActivatedMsg.PET_NO_MSG, true);
    }

    public static ServerPacket PetMove(TacosCharacter chr, TacosPet pet, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PetMove);

        sp.Encode4(chr.getId());

        if (Config.LessOrEqual(Region.JMS, 147)) {
            sp.Encode1(0);
        } else {
            sp.Encode4(chr.getPetIndex(pet));
        }

        sp.EncodeBuffer(data.get());
        return sp;
    }

    public static ServerPacket PetAction(TacosCharacter chr, int pet_index, byte nType, byte nAction, String pet_message) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PetAction);

        sp.Encode4(chr.getId());
        sp.Encode4(pet_index);
        sp.Encode1(nType);
        sp.Encode1(nAction);
        sp.EncodeStr(pet_message);
        // post BB may have extra 1 bytes
        return sp;
    }

    public static ServerPacket PetNameChanged(TacosCharacter chr, TacosPet pet, String pet_name) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PetNameChanged);

        sp.Encode4(chr.getId());
        if (Config.LessOrEqual(Region.JMS, 147)) {
            sp.Encode1(0); // 0 = success
        } else {
            sp.Encode4(chr.getPetIndex(pet));
        }

        sp.EncodeStr(pet_name);
        return sp;
    }

    public static ServerPacket PetActionCommand(TacosCharacter chr, byte command, int slot, boolean success, boolean food) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PetActionCommand);

        sp.Encode4(chr.getId());
        sp.Encode4(slot);
        sp.Encode1(command == 1 ? 1 : 0);
        sp.Encode1(command);
        if (command == 1) {
            sp.Encode1(0);
        } else {
            sp.Encode2(success ? 1 : 0);
        }

        return sp;
    }
}
