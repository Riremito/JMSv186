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
package tacos.packet.response;

import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import tacos.network.MaplePacket;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.server.TacosServer;
import tacos.tools.TacosTools;

/**
 *
 * @author Riremito
 */
public class ResCClientSocket {

    // サーバーのバージョン情報
    public static final MaplePacket getHello(final byte[] sendIv, final byte[] recvIv) {
        ServerPacket sp = new ServerPacket((short) 0); // dummy

        switch (Region.getRegion()) {
            case KMSB: {
                sp.Encode2(Version.getVersion());
                sp.EncodeStr(String.valueOf(Version.getSubVersion()));
                break;
            }
            case KMS:
            case KMST: {
                long xor_version = 0;
                xor_version ^= Version.getVersion();
                xor_version ^= 1 << 15;
                xor_version ^= Version.getSubVersion() << 16;
                sp.Encode2(291); // magic number
                sp.EncodeStr(String.valueOf(xor_version));
                break;
            }
            case VMS: {
                sp.Encode2(Version.getVersion());
                break;
            }
            case IMS: {
                sp.Encode2(Version.getVersion());
                sp.Encode1(0);
                sp.Encode1(Version.getSubVersion());
                break;
            }
            default: {
                sp.Encode2(Version.getVersion());
                sp.EncodeStr(String.valueOf(Version.getSubVersion()));
                break;
            }
        }
        sp.EncodeBuffer(recvIv);
        sp.EncodeBuffer(sendIv);
        sp.Encode1(Region.getRegionNumber()); // JMS = 3

        /*
            // x64
            sp.Encode2(ServerConfig.GetVersion());
            sp.EncodeStr("1:" + ServerConfig.GetSubVersion()); // 1:1
            sp.EncodeBuffer(recvIv);
            sp.EncodeBuffer(sendIv);
            sp.Encode1(ServerConfig.GetRegionNumber());
            sp.Encode1(0);
            sp.Encode1(5);
            sp.Encode1(1);
         */
        // ヘッダにサイズを書き込む
        sp.setHello();
        return sp.get();
    }

    // CClientSocket::OnAuthenMessage
    public static final MaplePacket AuthenMessage() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AuthenMessage);
        sp.Encode4(1); // id
        sp.Encode1(1);
        return sp.get();
    }

    // Internet Cafe
    // プレミアムクーポン itemid 5420007
    // CClientSocket::OnAuthenCodeChanged
    public static final MaplePacket AuthenCodeChanged() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AuthenCodeChanged);
        sp.Encode1(2); // Open UI
        sp.Encode4(1);
        return sp.get();
    }

    // CClientSocket::OnMigrateCommand
    public static MaplePacket MigrateCommand(TacosServer server) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MigrateCommand);
        sp.Encode1(1);
        sp.Encode4(TacosTools.getGameServerIP(server.getGlobalIP()));
        sp.Encode2(server.getPort());

        if (Version.Equal(Region.JMST, 110) || Version.GreaterOrEqual(Region.JMS, 302) || Version.Equal(Region.KMST, 391) || ServerConfig.KMS118orLater() || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode1(0);
        }
        return sp.get();
    }

    // CClientSocket::OnAliveReq
    public static final MaplePacket AliveReq() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AliveReq);
        return sp.get();
    }
}
