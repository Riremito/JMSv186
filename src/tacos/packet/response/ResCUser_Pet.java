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

import java.util.ArrayList;
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
            for (DeActivatedMsg o : DeActivatedMsg.values()) {
                if (o.get() == val) {
                    return o;
                }
            }
            return UNKNOWN;
        }
    }

    private enum MultiPetSupport {
        Idx4, // JMS186
        Idx1, // JMS147 or before
        NONE, // JMS131
    }

    private static byte[] getPetIdxBuffer(int nIdx) {
        ServerPacket data = new ServerPacket();
        MultiPetSupport mps;

        if (Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 330) || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 88) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.THMS, 96) || Config.GreaterOrEqual(Region.EMS, 70) || Config.GreaterOrEqual(Region.IMS, 1)) {
            mps = MultiPetSupport.Idx4; // KMS114, KMST330, JMS186, JMST110, CMS88, TWMS148, THMS96, EMS70, IMS
        } else if (Config.GreaterOrEqual(Region.JMS, 147) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 77) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 95) || Config.GreaterOrEqual(Region.MSEA, 100)) {
            mps = MultiPetSupport.Idx1; // JMS147-180, CMS73, TWMS77-125, THMS87, GMS95-111, MSEA100-102
        } else {
            mps = MultiPetSupport.NONE; // KMS95, JMS131, BMS, VMS
        }

        switch (mps) {
            case Idx1 -> {
                data.Encode1(nIdx);
            }
            case Idx4 -> {
                data.Encode4(nIdx);
            }
            default -> {
                // no multi pet.
            }
        }

        return data.getBytes();
    }

    public static ServerPacket PetActivated(TacosCharacter chr, TacosPet pet) {
        return PetActivated(chr, pet, true, DeActivatedMsg.PET_NO_MSG);
    }

    public static ServerPacket PetDeactivated(TacosCharacter chr, TacosPet pet, DeActivatedMsg msg) {
        return PetActivated(chr, pet, false, msg);
    }

    // CUserLocal::OnPetActivated, CUserRemote::OnPetActivated
    public static ServerPacket PetActivated(TacosCharacter chr, TacosPet pet, boolean spawn, DeActivatedMsg msg) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PetActivated);

        sp.Encode4(chr.getId()); // dwCharacterID
        sp.EncodeBuffer(getPetIdxBuffer(chr.getPetIndex(pet))); // nIdx
        sp.Encode1(spawn ? 1 : 0);

        if (!spawn) {
            sp.Encode1(msg.get());
            return sp;
        }

        if (Config.LessOrEqual(Region.KMS, 31) || Config.LessOrEqual(Region.JMS, 131)) {
            // none.
        } else {
            sp.Encode1(1); // JMS302 has to be 1. (diasble NPC dialog)
        }

        sp.EncodeBuffer(RD_CUser.CPet_Init(pet));
        return sp;
    }

    // CUserLocal::OnPetEvol, CUserRemote::OnPetEvol
    public static ServerPacket PetEvol(TacosCharacter chr, TacosPet pet) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PetEvol);

        sp.Encode4(chr.getId()); // dwCharacterID
        // CUser::OnPetEvol
        sp.Encode1(1); // spawn or not.
        sp.EncodeBuffer(getPetIdxBuffer(chr.getPetIndex(pet))); // nIdx
        sp.EncodeBuffer(RD_CUser.CPet_Init(pet));
        return sp;
    }

    public static ServerPacket PetTransferField(TacosCharacter chr, TacosPet pet) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PetTransferField);

        sp.Encode4(chr.getId()); // dwCharacterID
        sp.EncodeBuffer(getPetIdxBuffer(chr.getPetIndex(pet))); // nIdx
        if (Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186)) {
            sp.Encode1(1); // spawn or not. (JMS186)
        }
        sp.Encode1(0); // remove or not. (JMS147), msg (JMS186)
        sp.EncodeBuffer(RD_CUser.CPet_Init(pet));
        sp.Encode2(0);
        return sp;
    }

    // CPet::OnMove
    public static ServerPacket PetMove(TacosCharacter chr, TacosPet pet, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PetMove);

        sp.Encode4(chr.getId()); // dwCharacterID
        sp.EncodeBuffer(getPetIdxBuffer(chr.getPetIndex(pet))); // nIdx
        sp.EncodeBuffer(data.get());
        return sp;
    }

    // CPet::OnAction
    public static ServerPacket PetAction(TacosCharacter chr, TacosPet pet, byte nType, byte nAction, String pet_message) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PetAction);

        sp.Encode4(chr.getId()); // dwCharacterID
        sp.EncodeBuffer(getPetIdxBuffer(chr.getPetIndex(pet))); // nIdx
        sp.Encode1(nType); // nType
        sp.Encode1(nAction); // nAction
        sp.EncodeStr(pet_message);
        sp.Encode1(1); // bChatBalloon, GMS95, not in KMST330.
        return sp;
    }

    // CPet::OnNameChanged
    public static ServerPacket PetNameChanged(TacosCharacter chr, TacosPet pet) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PetNameChanged);

        sp.Encode4(chr.getId()); // dwCharacterID
        sp.EncodeBuffer(getPetIdxBuffer(chr.getPetIndex(pet))); // nIdx
        sp.EncodeStr(pet.getName());
        sp.Encode1(1); // new name or name removal, GMS95, not in KMST330.
        return sp;
    }

    // CPet::OnLoadExceptionList
    public static ServerPacket PetLoadExceptionList(TacosCharacter chr, TacosPet pet, ArrayList<Integer> item_ids) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PetLoadExceptionList);

        sp.Encode4(chr.getId()); // dwCharacterID
        sp.EncodeBuffer(getPetIdxBuffer(chr.getPetIndex(pet))); // nIdx
        sp.Encode8(pet.getUniqueId()); // liPetSN
        sp.Encode1(item_ids.size()); // count
        for (int item_id : item_ids) {
            sp.Encode4(item_id);
        }

        return sp;
    }

    // CPet::OnActionCommand
    public static ServerPacket PetActionCommand(TacosCharacter chr, TacosPet pet, byte command, boolean success, boolean food) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PetActionCommand);

        sp.Encode4(chr.getId()); // dwCharacterID
        sp.EncodeBuffer(getPetIdxBuffer(chr.getPetIndex(pet))); // nIdx
        sp.Encode1(command); // nType

        switch (command) {
            case 0 -> {
                sp.Encode1(0); // m_aInteraction[0]
                sp.Encode1(success ? 1 : 0);
                sp.Encode1(1);// bChatBalloon
            }
            case 1 -> {
                sp.Encode1(success ? 1 : 0);
            }
            default -> {
                sp.Encode1(success ? 1 : 0);
                sp.Encode1(1);// bChatBalloon
            }
        }

        return sp;
    }
}
