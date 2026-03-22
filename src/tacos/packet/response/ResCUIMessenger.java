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
package tacos.packet.response;

import odin.client.MapleCharacter;
import tacos.network.MaplePacket;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.response.data.DataAvatarLook;

/**
 *
 * @author Riremito
 */
public class ResCUIMessenger {

    public static MaplePacket removeMessengerPlayer(int position) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Messenger);

        sp.Encode1(2);
        sp.Encode1(position);
        return sp.get();
    }

    public static MaplePacket messengerInvite(String from, int messengerid) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Messenger);

        sp.Encode1(3);
        sp.EncodeStr(from);
        sp.Encode1(0);
        sp.Encode4(messengerid);
        sp.Encode1(0);
        return sp.get();
    }

    public static MaplePacket updateMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Messenger);

        sp.Encode1(7);
        sp.Encode1(position);
        sp.EncodeBuffer(DataAvatarLook.Encode(chr));
        sp.EncodeStr(from);
        sp.Encode2(channel);
        return sp.get();
    }

    public static MaplePacket addMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Messenger);

        sp.Encode1(0);
        sp.Encode1(position);
        sp.EncodeBuffer(DataAvatarLook.Encode(chr));
        sp.EncodeStr(from);
        sp.Encode2(channel);
        return sp.get();
    }

    public static MaplePacket messengerNote(String text, int mode, int mode2) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Messenger);

        sp.Encode1(mode);
        sp.EncodeStr(text);
        sp.Encode1(mode2);
        return sp.get();
    }

    public static MaplePacket messengerChat(String text) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Messenger);

        sp.Encode1(6);
        sp.EncodeStr(text);
        return sp.get();
    }

    public static MaplePacket joinMessenger(int position) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Messenger);

        sp.Encode1(1);
        sp.Encode1(position);
        return sp.get();
    }

}
