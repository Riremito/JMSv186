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

import tacos.client.TacosClient;
import tacos.config.Region;
import tacos.config.Config;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.server.TacosServer;
import tacos.tools.TacosTools;

/**
 *
 * @author Riremito
 */
public class ResCClientSocket {

    // first raw packet.
    public static ServerPacket getHello(TacosClient client) {
        ServerPacket sp = new ServerPacket((short) 0); // dummy

        switch (Config.REGION) {
            case KMSB: {
                sp.Encode2(Config.VERSION);
                sp.EncodeStr(String.valueOf(Config.VERSION_SUB));
                break;
            }
            case KMS:
            case KMST: {
                long xor_version = 0;
                xor_version ^= Config.VERSION;
                xor_version ^= 1 << 15;
                xor_version ^= Config.VERSION_SUB << 16;
                sp.Encode2(291); // magic number
                sp.EncodeStr(String.valueOf(xor_version));
                break;
            }
            case VMS: {
                sp.Encode2(Config.VERSION);
                break;
            }
            case IMS: {
                sp.Encode2(Config.VERSION);
                sp.Encode1(0);
                sp.Encode1(Config.VERSION_SUB);
                break;
            }
            default: {
                sp.Encode2(Config.VERSION);
                sp.EncodeStr(String.valueOf(Config.VERSION_SUB));
                break;
            }
        }

        sp.EncodeBuffer(client.getSeqSnd()); // m_uSeqSnd (from client)
        sp.EncodeBuffer(client.getSeqRcv()); // m_uSeqRcv (from client)
        sp.Encode1(Config.REGION.get()); // JMS = 3

        sp.setHello(); // write size of this packet length.
        return sp;
    }

    // CClientSocket::OnMigrateCommand
    public static ServerPacket MigrateCommand(TacosServer server) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MigrateCommand);

        sp.Encode1(1);
        sp.Encode4(TacosTools.getGameServerIP(server.getGlobalIP()));
        sp.Encode2(server.getPort());
        sp.Encode1(0, Config.GreaterOrEqual(Region.KMS, 118) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 302) || Config.Equal(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 89));
        return sp;
    }

    // CClientSocket::OnAliveReq
    public static ServerPacket AliveReq() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AliveReq);

        return sp;
    }

    // CClientSocket::OnAuthenMessage
    public static ServerPacket AuthenMessage() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AuthenMessage);

        sp.Encode4(1); // id
        sp.Encode1(1);
        return sp;
    }

    // CClientSocket::OnAuthenCodeChanged
    public static ServerPacket AuthenCodeChanged() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AuthenCodeChanged);

        sp.Encode1(2); // Open UI
        sp.Encode4(1);
        return sp;
    }

    // CSecurityClient::OnPacket
    public static ServerPacket SecurityPacket(int type) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SecurityPacket);

        sp.Encode1(type); // CSecurityClient::OnCheckClientIntegrityRequest
        switch (type) {
            case 0: // CSecurityClient::OnMemoryRegion
            {
                int size = 1;
                sp.Encode2(size); // loop count for CSecurityClient::REGION.
                for (int i = 0; i < size; i++) {
                    sp.Encode4(0);
                    sp.Encode4(0);
                }

                break;
            }
            case 2: // CSecurityClient::OnCheckClientIntegrityRequest, HackShield HeartBeat.
            {
                int nLength = 402; // _AHNHS_TRANS_BUFFER size.
                sp.Encode2(nLength);
                sp.EncodeZeroBytes(nLength);
                break;
            }
            case 3: // CSecurityClient::OnGameGuardAuth
            {
                sp.Encode4(0);
                break;
            }
            case 4: // CSecurityClient::OnGameGuardAuth2
            {
                sp.EncodeZeroBytes(16);
                break;
            }
            default: {
                break;
            }
        }

        return sp;
    }

    // same as CheckCrcResult.
    public static ServerPacket JMS187_UNK000D() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_187_UNK_000D);

        sp.Encode1(9); // 9 or not.
        return sp;
    }

    // CClientSocket::OnGuardInspectProcess
    public static ServerPacket GuardInspectProcess(byte[] sign_code_data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_GuardInspectProcess);

        sp.Encode1(sign_code_data.length / 22); // 22x bytes, SignCodeData.
        sp.EncodeBuffer(sign_code_data);
        return sp;
    }

    // CClientSocket::OnCheckCrcResult
    public static ServerPacket CheckCrcResult(boolean is_failed) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CheckCrcResult);

        sp.Encode1(is_failed ? 1 : 0);
        return sp;
    }
}
