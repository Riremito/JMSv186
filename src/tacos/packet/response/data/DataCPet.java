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

import odin.client.inventory.MaplePet;
import tacos.packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class DataCPet {

    // CPet::Init
    public static byte[] Init(MaplePet pet) {
        ServerPacket data = new ServerPacket();
        data.Encode4(pet.getPetItemId());
        data.EncodeStr(pet.getName());
        data.Encode8(pet.getUniqueId());
        data.Encode2(pet.getPosition().x);
        data.Encode2(pet.getPosition().y);
        data.Encode1(pet.getStance());
        data.Encode2(pet.getFh());
        return data.get().getBytes();
    }
}
