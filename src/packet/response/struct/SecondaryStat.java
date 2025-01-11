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
import java.util.ArrayList;
import packet.ServerPacket;
import packet.ops.OpsSecondaryStat;
import server.MapleStatEffect;
import tools.Pair;

/**
 *
 * @author Riremito
 */
public class SecondaryStat {

    // SecondaryStat::DecodeForLocal
    public static byte[] EncodeForLocal(MapleStatEffect mse) {
        ServerPacket data = new ServerPacket();

        int skill_id = mse.getSourceId();
        int buff_time = mse.getDuration();
        int buff_mask[] = {0, 0, 0, 0, 0, 0, 0, 0};

        // test
        ArrayList<Pair<OpsSecondaryStat, Integer>> pss_array = mse.getOss();
        for (Pair<OpsSecondaryStat, Integer> pss : pss_array) {
            buff_mask[pss.getLeft().getN()] |= (1 << pss.getLeft().get());
        }

        if (ServerConfig.JMS302orLater()) {
            data.Encode4(buff_mask[7]);
            data.Encode4(buff_mask[6]);
            data.Encode4(buff_mask[5]);
        }
        // JMS v187+
        if (ServerConfig.IsPostBB()) {
            data.Encode4(buff_mask[4]);
        }
        if (ServerConfig.JMS164orLater()) {
            data.Encode4(buff_mask[3]);
            data.Encode4(buff_mask[2]);
        }
        data.Encode4(buff_mask[1]); // シャープアイズ等
        data.Encode4(buff_mask[0]); // ブースター等

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 32; j++) {
                if ((buff_mask[i] & (1 << j)) > 0) {
                    int effect = 0;
                    for (Pair<OpsSecondaryStat, Integer> pss : pss_array) {
                        if (pss.getLeft().getN() == i && pss.getLeft().get() == j) {
                            effect = pss.getRight();
                        }
                    }
                    data.Encode2(effect);
                    data.Encode4(skill_id);
                    if (ServerConfig.JMS164orLater()) {
                        data.Encode4(buff_time);
                    } else {
                        data.Encode2(buff_time);
                    }
                }
            }
        }

        if (ServerConfig.JMS164orLater()) {
            data.Encode1(0);
            data.Encode1(0);
        }
        if (ServerConfig.JMS302orLater()) {
            data.Encode1(0);
        }
        return data.Get().getBytes();
    }

    // SecondaryStat::DecodeForRemote
    public static byte[] EncodeForRemote(MapleCharacter chr, int statmask) {
        ServerPacket data = new ServerPacket();
        return data.Get().getBytes();
    }
}
