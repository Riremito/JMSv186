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
package packet.client.handling;

import client.MapleClient;
import packet.client.ClientPacket;
import packet.server.response.ViciousHammerResponse;

/**
 *
 * @author Riremito
 */
public class ViciousHammerPacket {

    // @0119 [38 00 00 00] [00 00 00 00]
    // 0x38が成功フラグなのでクライアント側から成功可否を通知している可能性がある
    public static boolean Accept(MapleClient c, ClientPacket p) {
        // 成功可否
        int action = p.Decode4();
        // 用途不明
        int hammered = p.Decode4();
        // 関数に成功可否を渡しても良いと思われるが、成功確率が100%なので意味がない
        c.ProcessPacket(ViciousHammerResponse.Success());
        return true;
    }

}
