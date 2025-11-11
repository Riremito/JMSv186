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

import odin.client.MapleCharacter;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MaplePet;
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import tacos.packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class DataCUIUserInfo {

    // CUIUserInfo::SetMultiPetInfo (GMS)
    // CUIUserInfo::SetPetInfo (KMS)
    public static byte[] SetPetInfo(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        //IItem inv_pet = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -114);
        for (int i = 0; i < 4; i++) {
            MaplePet pet = chr.getPet(i);
            data.Encode1(pet != null ? 1 : 0); // 3 -> null
            if (pet == null) {
                break;
            }
            if (Version.PostBB()) {
                data.Encode4(i);
            }
            data.Encode4(pet.getPetItemId()); // dwTemplateID
            data.EncodeStr(pet.getName());
            data.Encode1(pet.getLevel()); // nLevel
            data.Encode2(pet.getCloseness()); // pet closeness
            data.Encode1(pet.getFullness()); // pet fullness
            data.Encode2(pet.getFlags());
            data.Encode4(/*inv_pet != null ? inv_pet.getItemId() : 0*/0); // nItemID
        }

        return data.get().getBytes();
    }

    public static byte[] SetPetInfo_JMS131(MapleCharacter chr, MaplePet pet) {
        ServerPacket data = new ServerPacket();

        data.Encode4(pet.getPetItemId()); // dwTemplateID
        data.EncodeStr(pet.getName());
        data.Encode1(pet.getLevel()); // nLevel
        data.Encode2(pet.getCloseness()); // pet closeness
        data.Encode1(pet.getFullness()); // pet fullness
        data.Encode2(0);
        data.Encode4(/*inv_pet != null ? inv_pet.getItemId() : 0*/0); // nItemID
        return data.get().getBytes();
    }
}
