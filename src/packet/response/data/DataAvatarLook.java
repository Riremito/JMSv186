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
package packet.response.data;

import client.MapleCharacter;
import client.inventory.IItem;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import config.Region;
import config.ServerConfig;
import config.Version;
import java.util.LinkedHashMap;
import java.util.Map;
import packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class DataAvatarLook {

    // AvatarLook::Decode, AvatarLook::AvatarLook
    public static byte[] Encode(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        data.Encode1(chr.getGender()); // nGender
        data.Encode1(chr.getSkinColor()); // nSkin
        data.Encode4(chr.getFace()); // nFace
        int demon_something = 0;
        if (ServerConfig.KMS138orLater() || Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.GMS, 111) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
            data.Encode4(demon_something); // demon something
        }
        data.Encode1(0); // ignored byte
        data.Encode4(chr.getHair());
        final Map<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
        final Map<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
        MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);
        for (final IItem item : equip.list()) {
            if (item.getPosition() < -128) {
                //not visible
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
        data.Encode1(255); // end of visible items
        // masked itens
        if (Version.LessOrEqual(Region.KMS, 1)) {
            // nothing
        } else {
            for (final Map.Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
                data.Encode1(entry.getKey());
                data.Encode4(entry.getValue());
            }
            data.Encode1(255); // ending markers
        }
        if (Version.GreaterOrEqual(Region.JMS, 308) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
            data.Encode1(255); // ending markers
        }
        final IItem cWeapon = equip.getItem((byte) -111);
        data.Encode4(cWeapon != null ? cWeapon.getItemId() : 0); // nWeaponStickerID
        if (Region.IsBMS() || Region.IsVMS()) {
            data.Encode4(0);
            return data.get().getBytes();
        }
        if (Version.GreaterOrEqual(Region.EMS, 89)) {
            data.Encode4(0);
            data.Encode4(0);
            data.Encode1(0);
            data.EncodeZeroBytes(12);
            if (demon_something / 100 == 31 || demon_something == 3001) {
                data.Encode4(0);
            }
            return data.get().getBytes();
        }
        if (Region.IsKMS()) {
            if (Version.PostBB()) {
                if (Version.GreaterOrEqual(Region.KMS, 160)) {
                    data.Encode4(0);
                    data.Encode4(0);
                }
                if (ServerConfig.KMS138orLater() && !Version.Equal(Region.KMST, 391)) {
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
        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.Equal(Region.JMST, 110)) {
            if (Version.GreaterOrEqual(Region.JMS, 302)) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode1(0);
            }
            data.EncodeZeroBytes(12);
            // demon something, v9 / 100 == 31 || v9 == 3001, JMS302 0053A2B5
            // data.Encode4(0);
            return data.get().getBytes();
        }
        if (Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            data.Encode1(0); // mercedes ear
        }
        if (Region.IsTHMS() || Region.IsTWMS() || Region.IsCMS() || Region.IsMSEA() || Region.IsEMS() || Version.GreaterOrEqual(Region.GMS, 83)) {
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
