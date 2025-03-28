/*
 * Copyright (C) 2023 Riremito
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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package packet.response.struct;

import client.MapleCharacter;
import client.inventory.IItem;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import config.ServerConfig;
import java.util.LinkedHashMap;
import java.util.Map;
import packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class AvatarLook {

    // AvatarLook::Decode, AvatarLook::AvatarLook
    // CharLook
    public static byte[] Encode(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        data.Encode1(chr.getGender()); // nGender
        data.Encode1(chr.getSkinColor()); // nSkin
        data.Encode4(chr.getFace()); // nFace

        if (ServerConfig.KMS138orLater() || ServerConfig.JMS302orLater()) {
            data.Encode4(0);
        }

        data.Encode1(0); // ignored byte
        data.Encode4(chr.getHair());

        final Map<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
        final Map<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
        MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);

        for (final IItem item : equip.list()) {
            if (item.getPosition() < -128) { //not visible
                continue;
            }
            byte pos = (byte) (item.getPosition() * -1);

            if (pos < 100 && myEquip.get(pos) == null) {
                myEquip.put(pos, item.getItemId());
            } else if ((pos > 100 || pos == -128) && pos != 111) {
                pos = (byte) (pos == -128 ? 28 : pos - 100);
                if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                }
                myEquip.put(pos, item.getItemId());
            } else if (myEquip.get(pos) != null) {
                maskedEquip.put(pos, item.getItemId());
            }
        }
        for (final Map.Entry<Byte, Integer> entry : myEquip.entrySet()) {
            data.Encode1(entry.getKey());
            data.Encode4(entry.getValue());
        }
        data.Encode1(0xFF); // end of visible items
        // masked itens
        for (final Map.Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            data.Encode1(entry.getKey());
            data.Encode4(entry.getValue());
        }
        data.Encode1(0xFF); // ending markers
        final IItem cWeapon = equip.getItem((byte) -111);
        data.Encode4(cWeapon != null ? cWeapon.getItemId() : 0); // nWeaponStickerID

        if (ServerConfig.IsBMS() || ServerConfig.IsVMS()) {
            data.Encode4(0);
            return data.get().getBytes();
        }

        if (ServerConfig.IsKMS()) {
            if (ServerConfig.IsPostBB()) {
                if (ServerConfig.KMS138orLater()) {
                    data.Encode1(0);
                }
                data.EncodeZeroBytes(12);
                if (ServerConfig.KMS138orLater()) {
                    if (chr.getJob() / 100 == 31 || chr.getJob() == 3001) {
                        data.Encode4(0);
                    }
                }
            } else {
                data.Encode4(0);
            }
            return data.get().getBytes();
        }

        if (ServerConfig.JMS302orLater() || ServerConfig.JMST110()) {
            if (ServerConfig.JMS302orLater()) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode1(0);
            }
            data.EncodeZeroBytes(12);
            // DemonSlayer
            if (chr.getJob() / 100 == 31 || chr.getJob() == 3001) {
                data.Encode4(0);
            }
            return data.get().getBytes();
        }

        if (ServerConfig.IsTHMS() || ServerConfig.IsTWMS() || ServerConfig.IsCMS() || ServerConfig.IsMSEA() || ServerConfig.IsEMS() || (ServerConfig.GMS95orLater())) {
            data.EncodeZeroBytes(12);
        } else {
            data.Encode4(0); // pet 1?

            if (ServerConfig.JMS146orLater()) {
                data.Encode8(0); // pet 2 and 3?
            }
        }

        return data.get().getBytes();
    }
}
