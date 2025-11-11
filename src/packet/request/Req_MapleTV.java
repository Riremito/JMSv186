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
package packet.request;

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import java.util.ArrayList;
import java.util.List;
import packet.ClientPacket;
import packet.ops.OpsMapleTV;
import packet.request.sub.ReqSub_UserConsumeCashItemUseRequest;
import packet.response.ResCMapleTVMan;
import odin.server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
public class Req_MapleTV {

    public static boolean OnPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        switch (header) {
            case CP_MapleTVSendMessageRequest: // /mapleTV
            {
                byte nFlag = cp.Decode1();
                byte unk1 = cp.Decode1();
                byte unk2 = cp.Decode1();
                byte unk3 = cp.Decode1();
                int chr_id = cp.Decode4(); // character_id

                List<String> messages = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    messages.add(cp.DecodeStr());
                }

                String name_to = "";
                if ((nFlag & 2) != 0) {
                    name_to = cp.DecodeStr();
                }

                MapleCharacter chr_to = name_to.equals("") ? null : ReqSub_UserConsumeCashItemUseRequest.findCharacterByName(name_to);

                if ((nFlag & 2) != 0 && chr_to == null) {
                    chr.SendPacket(ResCMapleTVMan.MapleTVSendMessageResult(OpsMapleTV.MapleTVResCode_WrongUser));
                    return true;
                }

                if (!chr.isGM()) {
                    chr.SendPacket(ResCMapleTVMan.MapleTVSendMessageResult(OpsMapleTV.MapleTVResCode_IsNotGM));
                    return true;
                }

                chr.SendPacket(ResCMapleTVMan.MapleTVSendMessageResult(OpsMapleTV.MapleTVResCode_Success));
                chr.SendPacket(ResCMapleTVMan.MapleTVUpdateMessage(nFlag, 0, chr, messages, chr_to));
                return true;
            }
            case CP_MapleTVUpdateViewCount: {
                return true;
            }
            default: {
                break;
            }

        }

        return false;
    }

}
