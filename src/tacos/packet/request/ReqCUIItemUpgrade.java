/*
 * Copyright (C) 2024 Riremito
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
package tacos.packet.request;

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;
import static tacos.packet.ClientPacketHeader.CP_ItemUpgradeComplete;
import tacos.packet.response.ResCUIItemUpgrade;

/**
 *
 * @author Riremito
 */
public class ReqCUIItemUpgrade {

    public static boolean OnPacket(MapleClient client, ClientPacketHeader header, ClientPacket cp) {
        MapleCharacter chr = client.getPlayer();
        if (chr == null) {
            return false;
        }

        switch (header) {
            case CP_ItemUpgradeComplete: {
                OnItemUpgradeComplete(chr, cp);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // @0119 [38 00 00 00] [00 00 00 00]
    // 0x38が成功フラグなのでクライアント側から成功可否を通知している可能性がある
    public static boolean OnItemUpgradeComplete(MapleCharacter chr, ClientPacket p) {
        int action = p.Decode4(); // 成功可否
        int hammered = p.Decode4(); // 用途不明
        // 関数に成功可否を渡しても良いと思われるが、成功確率が100%なので意味がない
        chr.SendPacket(ResCUIItemUpgrade.Success());
        return true;
    }

}
