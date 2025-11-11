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
package packet.response;

import odin.client.MapleCharacter;
import java.util.List;
import packet.ServerPacket;
import packet.ops.OpsMapleTV;
import packet.response.data.DataAvatarLook;
import server.network.MaplePacket;

/**
 *
 * @author Riremito
 */
public class ResCMapleTVMan {

    public static MaplePacket MapleTVUpdateMessage(byte nFlag, int m_nMessageType, MapleCharacter chr_from, List<String> messages, MapleCharacter chr_to) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MapleTVUpdateMessage);

        if (chr_to == null) {
            nFlag &= ~2; // safety
        }

        sp.Encode1(nFlag); // nFlag
        sp.Encode1(m_nMessageType); // m_nMessageType
        sp.EncodeBuffer(DataAvatarLook.Encode(chr_from));
        sp.EncodeStr(chr_from.getName());
        sp.EncodeStr((chr_to != null) ? chr_to.getName() : "");
        sp.EncodeStr(messages.get(0));
        sp.EncodeStr(messages.get(1));
        sp.EncodeStr(messages.get(2));
        sp.EncodeStr(messages.get(3));
        sp.EncodeStr(messages.get(4));
        sp.Encode4(1337); // m_nTotalWaitTimes

        if ((nFlag & 2) != 0) {
            sp.EncodeBuffer(DataAvatarLook.Encode(chr_to));
        }

        return sp.get();
    }

    public static MaplePacket MapleTVClearMessage() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MapleTVClearMessage);

        return sp.get();
    }

    public static MaplePacket MapleTVSendMessageResult(OpsMapleTV ops) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MapleTVSendMessageResult);
        boolean isOK = ops == OpsMapleTV.MapleTVResCode_Success; // fail with error message

        sp.Encode1(isOK ? OpsMapleTV.MapleTVResCode_Success.get() : OpsMapleTV.MapleTVResCode_Fail.get());
        if (!isOK) {
            sp.Encode1(ops.get());
        }
        return sp.get();
    }

    // unused
    public static MaplePacket BroadSetFlashChangeEvent() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_BroadSetFlashChangeEvent);

        return sp.get();
    }

}
