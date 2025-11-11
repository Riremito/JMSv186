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
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import java.util.Collection;
import tacos.packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class DataCWvsContext {

    // CWvsContext::OnSetLogoutGiftConfig
    public static byte[] LogoutGiftConfig() {
        ServerPacket data = new ServerPacket();
        data.Encode4(0); // something
        if (Version.GreaterOrEqual(Region.GMS, 126)) {
            // 0 = no data.
            return data.get().getBytes();
        }
        if (ServerConfig.JMS194orLater()) {
            data.Encode4(0);
        }
        data.Encode4(0); // item1?
        data.Encode4(0); // item2?
        data.Encode4(0); // item3?
        return data.get().getBytes();
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
            data_friend.Encode1(0);
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
        data.EncodeBuffer(data_friend.get().getBytes());
        data.EncodeBuffer(data_in_shop.get().getBytes());
        return data.get().getBytes();
    }

}
