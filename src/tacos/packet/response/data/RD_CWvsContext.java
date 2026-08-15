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
package tacos.packet.response.data;

import odin.client.BuddylistEntry;
import odin.client.MapleCharacter;
import java.util.Collection;
import odin.client.inventory.MaplePet;
import tacos.client.TacosCharacter;
import tacos.client.TacosForcedStat;
import tacos.config.Config;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsForcedStat;

/**
 *
 * @author Riremito
 */
public class RD_CWvsContext {

    // ForcedStat::Decode
    public static byte[] ForcedStat_Encode(TacosCharacter chr) {
        ServerPacket data = new ServerPacket();

        TacosForcedStat fs = chr.getForcedStat();
        int mask = fs.getMask();

        data.Encode4(mask);

        if ((mask & OpsForcedStat.STR.get()) != 0) {
            data.Encode2(fs.getSTR());
        }
        if ((mask & OpsForcedStat.DEX.get()) != 0) {
            data.Encode2(fs.getDEX());
        }
        if ((mask & OpsForcedStat.INT.get()) != 0) {
            data.Encode2(fs.getINT());
        }
        if ((mask & OpsForcedStat.LUK.get()) != 0) {
            data.Encode2(fs.getLUK());
        }
        if ((mask & OpsForcedStat.PAD.get()) != 0) {
            data.Encode2(fs.getPAD());
        }
        if ((mask & OpsForcedStat.PDD.get()) != 0) {
            data.Encode2(fs.getPDD());
        }
        if ((mask & OpsForcedStat.MAD.get()) != 0) {
            data.Encode2(fs.getMAD());
        }
        if ((mask & OpsForcedStat.MDD.get()) != 0) {
            data.Encode2(fs.getMDD());
        }
        if ((mask & OpsForcedStat.ACC.get()) != 0) {
            data.Encode2(fs.getACC());
        }
        if ((mask & OpsForcedStat.EVA.get()) != 0) {
            data.Encode2(fs.getEVA());
        }
        if ((mask & OpsForcedStat.SPEED.get()) != 0) {
            data.Encode1(fs.getSpeed());
        }
        if ((mask & OpsForcedStat.JUMP.get()) != 0) {
            data.Encode1(fs.getJump());
        }
        if ((mask & OpsForcedStat.SPEEDMAX.get()) != 0) {
            data.Encode1(fs.getSpeedMax());
        }

        return data.getBytes();
    }

    // CWvsContext::CFriend::Reset
    public static byte[] CFriend_Reset(MapleCharacter chr) {
        Collection<BuddylistEntry> friend_list = chr.getBuddylist().getBuddies();

        ServerPacket data_friend = new ServerPacket();
        for (BuddylistEntry friend : friend_list) {
            // KMS55 : 22 bytes
            // KMS65 : 39 bytes
            data_friend.Encode4(friend.getCharacterId());
            data_friend.EncodeBuffer(friend.getName(), 13);
            data_friend.Encode1(friend.getHidden() ? 1 : 0);
            data_friend.Encode4(friend.getChannel() == -1 ? -1 : friend.getChannel() - 1);
            if (friend.getGroup() != null) {
                data_friend.EncodeBuffer(friend.getGroup(), 17); // マイ友 (tag)
            }
        }

        ServerPacket data_in_shop = new ServerPacket();
        for (BuddylistEntry friend : friend_list) {
            // 4 bytes
            data_in_shop.Encode4(0);
        }

        ServerPacket data = new ServerPacket();
        data.Encode1(friend_list.size());
        data.EncodeBuffer(data_friend.getBytes());
        data.EncodeBuffer(data_in_shop.getBytes());
        return data.getBytes();
    }
    
        // CUIUserInfo::SetMultiPetInfo
    public static byte[] CUIUserInfo_SetMultiPetInfo_GMS95(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        // GMS does not have first pet checks inside this function.
        MaplePet pet = chr.getPet(0);
        data.Encode4(pet.getPetItemId()); // dwTemplateID
        data.EncodeStr(pet.getName());
        data.Encode1(pet.getLevel()); // nLevel
        data.Encode2(pet.getCloseness()); // nTameness
        data.Encode1(pet.getFullness()); // nRepleteness
        data.Encode2(pet.getFlags()); // usPetSkill
        data.Encode4(0); // dwTemplateID
        data.Encode1(0); // next pet is null.
        return data.getBytes();
    }

    // CUIUserInfo::SetPetInfo (KMS)
    public static byte[] CUIUserInfo_SetPetInfo(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        for (int i = 0; i < 4; i++) {
            MaplePet pet = chr.getPet(i);
            data.Encode1(pet != null ? 1 : 0); // 3 -> null
            if (pet == null) {
                break;
            }

            if (Config.PostBB()) {
                data.Encode4(i);
            }

            data.Encode4(pet.getPetItemId()); // dwTemplateID
            data.EncodeStr(pet.getName());
            data.Encode1(pet.getLevel()); // nLevel
            data.Encode2(pet.getCloseness()); // nTameness
            data.Encode1(pet.getFullness()); // nRepleteness
            data.Encode2(pet.getFlags()); // usPetSkill
            data.Encode4(/*inv_pet != null ? inv_pet.getItemId() : 0*/0); // dwTemplateID
        }

        return data.getBytes();
    }

    public static byte[] CUIUserInfo_SetPetInfo_JMS131(MapleCharacter chr, MaplePet pet) {
        ServerPacket data = new ServerPacket();

        data.Encode4(pet.getPetItemId()); // dwTemplateID
        data.EncodeStr(pet.getName());
        data.Encode1(pet.getLevel()); // nLevel
        data.Encode2(pet.getCloseness()); // nTameness
        data.Encode1(pet.getFullness()); // nRepleteness
        data.Encode2(0); // usPetSkill
        data.Encode4(/*inv_pet != null ? inv_pet.getItemId() : 0*/0); // nItemID
        return data.getBytes();
    }
}
