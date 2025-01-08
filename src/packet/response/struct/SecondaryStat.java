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
import config.ServerConfig;
import packet.ServerPacket;
import packet.ops.OpsSecondaryStat;
import server.MapleStatEffect;

/**
 *
 * @author Riremito
 */
public class SecondaryStat {

    // SecondaryStat::DecodeForLocal
    public static byte[] EncodeForLocal(MapleStatEffect mse) {
        ServerPacket data = new ServerPacket();

        int skill_id = mse.getSourceId();
        int flag_mask_1 = 0;
        int flag_mask_2 = 0;
        int flag_mask_3 = 0;
        int flag_mask_4 = 0;
        int flag_mask_5 = 0;
        int buff_time = mse.getDuration();

        // test
        if (mse.getOss() == OpsSecondaryStat.CTS_Booster) {
            flag_mask_1 |= (1 << OpsSecondaryStat.CTS_Booster.get());
        }
        if (mse.getOss() == OpsSecondaryStat.CTS_SoulArrow) {
            flag_mask_1 |= (1 << OpsSecondaryStat.CTS_SoulArrow.get());
        }
        if (mse.getOss() == OpsSecondaryStat.CTS_SharpEyes) {
            flag_mask_2 |= (1 << OpsSecondaryStat.CTS_SharpEyes.get());
        }

        // JMS v187+
        if (ServerConfig.IsPostBB()) {
            data.Encode4(flag_mask_5);
        }
        if (ServerConfig.JMS164orLater()) {
            data.Encode4(flag_mask_4);
            data.Encode4(flag_mask_3);
        }
        data.Encode4(flag_mask_2); // シャープアイズ等
        data.Encode4(flag_mask_1); // ブースター等

        if ((flag_mask_1 & (1 << OpsSecondaryStat.CTS_Booster.get())) > 0) {
            data.Encode2(mse.getX());
            data.Encode4(skill_id);
            data.Encode4(buff_time); // JMS v131 2 bytes
        }

        if ((flag_mask_1 & (1 << OpsSecondaryStat.CTS_SoulArrow.get())) > 0) {
            data.Encode2(mse.getX());
            data.Encode4(skill_id);
            data.Encode4(buff_time);
        }

        if ((flag_mask_2 & (1 << OpsSecondaryStat.CTS_SharpEyes.get())) > 0) {
            data.Encode2((mse.getX() << 8) | mse.getY());
            data.Encode4(skill_id);
            data.Encode4(buff_time);
        }

        data.Encode1(0);
        data.Encode1(0);
        return data.Get().getBytes();
    }

    // SecondaryStat::DecodeForRemote
    public static byte[] EncodeForRemote(MapleCharacter chr, int statmask) {
        ServerPacket data = new ServerPacket();
        return data.Get().getBytes();
    }
}
