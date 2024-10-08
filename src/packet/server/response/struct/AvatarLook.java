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
package packet.server.response.struct;

import client.MapleCharacter;
import client.inventory.IItem;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import config.ServerConfig;
import java.util.LinkedHashMap;
import java.util.Map;
import packet.server.ServerPacket;

/**
 *
 * @author Riremito
 */
public class AvatarLook {

    // AvatarLook::Decode, AvatarLook::AvatarLook
    // CharLook
    public static byte[] Encode(MapleCharacter chr) {
        ServerPacket p = new ServerPacket();

        p.Encode1(chr.getGender()); // nGender
        p.Encode1(chr.getSkinColor()); // nSkin
        p.Encode4(chr.getFace()); // nFace

        if (ServerConfig.IsJMS() && 302 <= ServerConfig.GetVersion()) {
            p.Encode4(0);
        }

        p.Encode1(0); // ignored byte
        p.Encode4(chr.getHair());

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
            p.Encode1(entry.getKey());
            p.Encode4(entry.getValue());
        }
        p.Encode1(0xFF); // end of visible items
        // masked itens
        for (final Map.Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            p.Encode1(entry.getKey());
            p.Encode4(entry.getValue());
        }
        p.Encode1(0xFF); // ending markers
        final IItem cWeapon = equip.getItem((byte) -111);
        p.Encode4(cWeapon != null ? cWeapon.getItemId() : 0);

        if (ServerConfig.IsKMS()) {
            if (ServerConfig.IsPostBB()) {
                p.EncodeZeroBytes(12);
            } else {

                p.Encode4(0);
            }
            return p.Get().getBytes();
        }

        if (ServerConfig.IsJMS() && 302 <= ServerConfig.GetVersion()) {
            p.Encode4(0);
            p.Encode4(0);
            p.Encode1(0);
            p.EncodeZeroBytes(12);
            // DemonSlayer -> Encode4
            return p.Get().getBytes();
        }

        if (ServerConfig.IsTWMS() || ServerConfig.IsCMS()) {
            p.EncodeZeroBytes(12);
        } else {
            p.Encode4(0); // pet 1?

            if (ServerConfig.IsJMS() && 164 <= ServerConfig.GetVersion()) {
                p.Encode8(0); // pet 2 and 3?
            }
        }

        return p.Get().getBytes();
    }
}
