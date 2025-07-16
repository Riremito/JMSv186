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
package packet.response;

import config.Region;
import config.ServerConfig;
import config.Version;
import debug.Debug;
import server.network.MaplePacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class ResCClientSocket {

    public static long GameServerIP = 0;

    public static long getGameServerIP() {
        if (GameServerIP != 0) {
            return GameServerIP;
        }
        try {
            byte[] ip_bytes = InetAddress.getByName("127.0.0.1").getAddress();
            GameServerIP = ip_bytes[0] | (ip_bytes[1] << 8) | (ip_bytes[2] << 16) | (ip_bytes[3] << 24);
        } catch (UnknownHostException ex) {
            GameServerIP = 16777343; // 127.0.0.1
            Debug.ErrorLog("GameServerIP set to 127.0.0.1");
        }
        return GameServerIP;
    }

    // サーバーのバージョン情報
    public static final MaplePacket getHello(final byte[] sendIv, final byte[] recvIv) {
        ServerPacket sp = new ServerPacket((short) 0); // dummy

        switch (Region.getRegion()) {
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
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AuthenMessage);
        sp.Encode4(1); // id
        sp.Encode1(1);
        return sp.get();
    }

    // Internet Cafe
    // プレミアムクーポン itemid 5420007
    // CClientSocket::OnAuthenCodeChanged
    public static final MaplePacket AuthenCodeChanged() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AuthenCodeChanged);
        sp.Encode1(2); // Open UI
        sp.Encode4(1);
        return sp.get();
    }

    // CClientSocket::OnMigrateCommand
    // getChannelChange
    public static final MaplePacket MigrateCommand(final int port) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MigrateCommand);
        sp.Encode1(1);
        sp.Encode4((int) GameServerIP); // IP, 127.0.0.1
        sp.Encode2(port);

        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.Equal(Region.KMST, 391) || ServerConfig.KMS118orLater() || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
            sp.Encode1(0);
        }
        return sp.get();
    }

    // CClientSocket::OnAliveReq
    // getPing
    public static final MaplePacket AliveReq() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AliveReq);
        return sp.get();
    }
}
