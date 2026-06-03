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

import odin.client.MapleCharacter;
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import java.util.ArrayList;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsSecondaryStat;
import odin.server.MapleStatEffect;
import tacos.odin.OdinPair;

/**
 *
 * @author Riremito
 */
public class DataSecondaryStat {

    // SecondaryStat::DecodeForRemote
    public static byte[] EncodeForRemote_JMS147(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        data.EncodeZeroBytes(16);
        data.Encode1(0);
        data.Encode1(0);
        return data.get().getBytes();
    }

    public static byte[] EncodeForRemote_JMS302(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        data.EncodeZeroBytes(32);
        data.Encode1(0);
        data.Encode1(0);
        data.Encode1(0);
        return data.get().getBytes();
    }

    public static int getBuffSize() {
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            return 48;
        }
        if (Version.GreaterOrEqual(Region.EMS, 89)) {
            return 36;
        }
        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
            return 32;
        }
        // post bb ex.
        if (Version.Equal(Region.KMST, 330) || Region.check(Region.THMS) || Region.check(Region.IMS)) {
            return 16;
        }
        // JMS187
        if (Version.PostBB()) {
            return 20;
        }
        // JMS147
        if (ServerConfig.JMS146orLater()) {
            return 16;
        }
        // JMS131, reverse order.
        return 8;
    }

    // SecondaryStat::DecodeForLocal
    public static byte[] EncodeForLocal(MapleStatEffect mse) {
        ServerPacket data = new ServerPacket();

        int[] buff_mask = new int[getBuffSize() / 4];
        // test
        ServerPacket data_effect = new ServerPacket();
        ArrayList<OdinPair<OpsSecondaryStat, Integer>> pss_array = mse.getOss();
        for (OdinPair<OpsSecondaryStat, Integer> pss : pss_array) {
            buff_mask[pss.getLeft().getNl()] |= pss.getLeft().getNr();

            if (Version.GreaterOrEqual(Region.THMS, 96)) {
                data_effect.Encode4(pss.getRight());
            } else {
                data_effect.Encode2(pss.getRight());
            }

            data_effect.Encode4(mse.isSkill() ? mse.getSourceId() : -mse.getSourceId());
            if (ServerConfig.JMS146orLater()) {
                data_effect.Encode4(mse.getDuration());
            } else {
                data_effect.Encode2(mse.getDuration());
            }
        }

        for (int index = 0; index < buff_mask.length; index++) {
            data.Encode4(buff_mask[buff_mask.length - 1 - index]);
        }
        data.EncodeBuffer(data_effect.get().getBytes());

        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            data.Encode2(0);
        }
        if (ServerConfig.JMS146orLater()) {
            data.Encode1(0);
            data.Encode1(0);
        }
        if (Version.GreaterOrEqual(Region.KMS, 197) || Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.TWMS, 148)) {
            data.Encode1(0);
        }
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            data.Encode4(0);
            data.Encode4(0);
        }

        return data.get().getBytes();
    }
}
