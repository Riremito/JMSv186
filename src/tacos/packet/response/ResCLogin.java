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

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import java.util.List;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsLogin;
import tacos.packet.ops.OpsPinCodeResCode;
import tacos.packet.ops.OpsViewAllChar;
import tacos.packet.response.data.DataAvatarLook;
import tacos.packet.response.data.DataCharacterData;
import tacos.packet.response.data.DataGW_CharacterStat;
import tacos.property.Property_World;
import tacos.server.TacosChannel;
import tacos.server.TacosServer;
import tacos.server.TacosWorld;
import tacos.shared.SharedDate;
import tacos.tools.TacosTools;

/**
 *
 * @author Riremito
 */
public class ResCLogin {

    public static ServerPacket CheckPasswordResult(MapleClient client, int result) {
        return CheckPasswordResult(client, OpsLogin.find(result));
    }

    // CLogin::OnCheckPasswordResult
    public static ServerPacket CheckPasswordResult(MapleClient client, OpsLogin ops) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CheckPasswordResult);

        sp.Encode1(ops.get()); // ops

        // EMS v55-v70
        if (Region.GMS.check() || Region.GMST.check() || Version.Between(Region.EMS, 55, 70)) {
            sp.Encode1(0);
            sp.Encode4(0); // unused
        }
        /*
        v186 Message Flag
        00 : OK
        20 : BAN Blue Message
        40 : BAN Blue Message
         */
        switch (ops) {
            case LoginResCode_Success: {
                {
                    switch (Region.getRegion()) {
                        case KMSB: {
                            int server_id = 0;
                            sp.Encode4(client.getId());
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.EncodeStr(client.getMapleId());
                            sp.Encode4(0);
                            sp.Encode4(0);
                            sp.Encode1(1); // number of worlds
                            {
                                sp.Encode1(Property_World.getFlags());
                                sp.EncodeStr(Property_World.getName());
                                int world_id = 0;
                                sp.Encode1(TacosWorld.find(world_id).getChannels().size()); // number of  channels
                                for (int i = 0; i < TacosWorld.find(world_id).getChannels().size(); i++) {
                                    sp.EncodeStr(TacosWorld.find(world_id).getChannels().get(i).getName());
                                    sp.Encode4(TacosWorld.find(world_id).getChannels().get(i).getOnlinePlayers().get().size() * 200);
                                    sp.Encode1(server_id); // serverId
                                    sp.Encode1(i); // channel
                                    sp.Encode1(0);
                                }
                            }
                            break;
                        }
                        case KMS:
                        case KMST: {
                            sp.Encode4(client.getId()); // m_dwAccountId
                            sp.Encode1(client.getGender()); // m_nGender
                            sp.Encode1(client.isGameMaster() ? 1 : 0); // m_nGradeCode
                            if (ServerConfig.JMS164orLater()) {
                                sp.Encode1(client.isGameMaster() ? 1 : 0);
                            }
                            if (Version.GreaterOrEqual(Region.KMS, 160)) {
                                sp.Encode4(3);
                                if (Version.GreaterOrEqual(Region.KMS, 169)) {
                                    sp.Encode4(0);
                                }
                                sp.Encode1(1);
                                sp.Encode1(0);
                                sp.Encode8(0);
                                sp.Encode1(0);
                                sp.EncodeStr(client.getMapleId());
                                sp.EncodeStr("");
                                if (Version.GreaterOrEqual(Region.KMS, 197)) {
                                    sp.Encode1(1);
                                    sp.Encode1(0);
                                    for (int i = 0; i < 16; i++) {
                                        sp.Encode1(1); // 0079D99C (KMS197)
                                    }
                                    break;
                                }
                                // KMS160
                                sp.Encode1(0);
                                sp.Encode1(0);
                                sp.Encode1(0);
                                break;
                            }
                            sp.EncodeStr(client.getMapleId()); // m_sNexonClubID
                            sp.Encode4(3); // should be 3 for KMS v2.114 to ignore personal number
                            sp.Encode1(1); // should be 1 for KMS v2.114 to ignore personal number
                            sp.Encode1(0); // m_nPurchaseExp
                            sp.Encode1(0); // m_nChatBlockReason
                            sp.Encode8(0); // m_dtChatUnblockDate
                            sp.EncodeStr("");
                            break;
                        }
                        case JMS:
                        case JMST:
                        default: {
                            sp.Encode1(0); // OK
                            sp.Encode4(client.getId()); // m_dwAccountId
                            sp.Encode1(client.getGender()); // m_nGender
                            sp.Encode1(client.isGameMaster() ? 1 : 0); // m_nGradeCode
                            if (ServerConfig.JMS164orLater()) {
                                sp.Encode1(client.isGameMaster() ? 1 : 0);
                            }
                            if (Version.GreaterOrEqual(Region.JMS, 308)) {
                                sp.EncodeZeroBytes(4);
                                sp.Encode4(0);
                                sp.Encode1(0);
                                sp.Encode1(0);
                                sp.EncodeStr(client.getMapleId());
                                sp.EncodeStr(client.getMapleId());
                                sp.Encode1(0);
                                sp.Encode1(0);
                                sp.Encode1(0);
                                sp.Encode1(-1);
                                sp.Encode1(1);
                                sp.EncodeZeroBytes(8);
                                sp.Encode1(1);
                                sp.EncodeStr("");
                                break;
                            }
                            sp.EncodeStr(client.getMapleId()); // m_sNexonClubID
                            sp.EncodeStr(client.getMapleId());
                            sp.Encode1(0);
                            sp.Encode1(0); // m_nPurchaseExp
                            sp.Encode1(0); // m_nChatBlockReason
                            sp.Encode1(0);
                            if (ServerConfig.JMS164orLater()) {
                                sp.Encode1(0);
                            }
                            if (ServerConfig.JMS180orLater()) {
                                sp.Encode1(0);
                            }
                            // 2次パスワード
                            if (Version.PostBB()) {
                                // -1, 無視
                                // 0, 初期化
                                // 1, 登録済み
                                sp.Encode1(-1);
                            }
                            // 旧かんたん会員
                            if (Version.GreaterOrEqual(Region.JMS, 302)) {
                                // 0, 旧かんたん会員
                                // 1, 通常
                                sp.Encode1(1);
                            }
                            sp.Encode8(0); // m_dtChatUnblockDate or client key (never used in JMS?)
                            sp.EncodeStr(""); // v131: available name for new character, later version does not use this string
                            break;
                        }
                        case CMS: {
                            sp.Encode4(client.getId());
                            sp.Encode1(client.getGender());
                            sp.Encode1(client.isGameMaster() ? 1 : 0);
                            sp.Encode1(client.isGameMaster() ? 1 : 0);
                            sp.EncodeStr(client.getMapleId());
                            sp.Encode4(0);
                            sp.Encode1(0);
                            sp.Encode1(0);
                            if (Version.LessOrEqual(Region.CMS, 88)) {
                                sp.Encode1(0);
                            }
                            sp.Encode8(SharedDate.getTimestamp()); // buffer
                            sp.Encode1(0);
                            sp.Encode8(SharedDate.getTimestamp()); // buffer
                            sp.Encode8(0); // buffer
                            sp.EncodeStr("");
                            sp.Encode1(1); // 0 = open blue message box.
                            sp.EncodeStr(String.valueOf(client.getId()));
                            sp.EncodeStr(client.getMapleId());
                            sp.Encode1(1);
                            if (Version.GreaterOrEqual(Region.CMS, 104)) {
                                sp.Encode1(0);
                                sp.Encode1(0);
                                sp.Encode1(0);
                            }
                            break;
                        }
                        case THMS: {
                            sp.Encode4(client.getId());
                            sp.Encode1(client.getGender());
                            sp.Encode1(client.isGameMaster() ? 1 : 0);
                            sp.Encode1(client.isGameMaster() ? 1 : 0);
                            sp.EncodeStr(client.getMapleId());
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.Encode8(0);
                            sp.Encode1(0);
                            sp.EncodeStr("");
                            break;
                        }
                        case TWMS: {
                            sp.Encode4(client.getId());
                            sp.Encode1(client.getGender());
                            sp.Encode1(client.isGameMaster() ? 1 : 0);
                            if (Version.GreaterOrEqual(Region.TWMS, 94)) {
                                sp.Encode1(client.isGameMaster() ? 1 : 0);
                            }
                            if (Version.GreaterOrEqual(Region.TWMS, 121)) {
                                sp.Encode4(0); // buffer4
                            }
                            sp.EncodeStr(client.getMapleId());
                            sp.Encode4(0);
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.Encode8(0); // buffer
                            sp.Encode1(0);
                            sp.Encode8(0); // buffer
                            break;
                        }
                        case MSEA: {
                            // MSEA100
                            sp.Encode4(client.getId());
                            sp.Encode1(client.getGender());
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.EncodeStr(client.getMapleId());
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.Encode8(0);
                            sp.EncodeStr("");
                            break;
                        }
                        case GMS:
                        case GMST: {
                            if (Version.PreBB()) {
                                sp.Encode4(client.getId()); // m_dwAccountId
                                sp.Encode1(client.getGender()); // m_nGender
                                sp.Encode1(client.isGameMaster() ? 4 : 0); // m_nGradeCode
                                if (Version.GreaterOrEqual(Region.GMS, 68)) {
                                    sp.Encode1(client.isGameMaster() ? (Version.GreaterOrEqual(Region.GMS, 95) ? 0x80 : 0x80) : 0); // Admin F1
                                }
                                sp.Encode1(0);
                                sp.EncodeStr(client.getMapleId()); // m_sNexonClubID
                                sp.Encode1(0); // m_nPurchaseExp
                                sp.Encode1(0); // m_nChatBlockReason
                                sp.Encode8(0); // m_dtChatUnblockDate
                                sp.Encode8(0); // m_dtRegisterDate
                                sp.Encode4(0);
                                if (Version.GreaterOrEqual(Region.GMS, 82)) {
                                    sp.Encode1(1);
                                    sp.Encode1(0);
                                }
                                if (Version.GreaterOrEqual(Region.GMS, 84)) {
                                    sp.Encode8(client.getClientKey());
                                }
                            } else if (Version.GreaterOrEqual(Region.GMS, 131)) {
                                // GMS131
                                sp.Encode4(client.getId()); // m_dwAccountId
                                sp.Encode1(0);
                                sp.Encode1(0);
                                sp.Encode2(0);
                                sp.Encode4(0);
                                sp.Encode1(0);
                                sp.EncodeStr(client.getMapleId());
                                sp.Encode1(0);
                                sp.Encode1(0);
                                sp.Encode8(0);
                                sp.Encode8(0);
                                sp.Encode4(0);
                                sp.Encode1(1); // available job
                                {
                                    sp.Encode1(0);
                                    for (int i = 0; i < 17; i++) {
                                        sp.Encode1(1);
                                    }
                                }
                                sp.Encode1(1); // pic
                                sp.Encode1(0);
                                sp.Encode8(0);
                            } else {
                                // GMS v95
                                sp.Encode4(client.getId()); // m_dwAccountId
                                sp.Encode1(0);
                                sp.Encode1(0);
                                sp.Encode2(0);
                                if (Version.GreaterOrEqual(Region.GMS, 126)) {
                                    sp.Encode4(0);
                                }
                                sp.Encode1(0);
                                sp.EncodeStr(client.getMapleId());
                                sp.Encode1(0);
                                sp.Encode1(0);
                                sp.Encode8(0);

                                if (Version.GreaterOrEqual(Region.GMS, 111)) {
                                    sp.Encode1(0);
                                }
                                sp.Encode8(0);
                                sp.Encode4(0);
                                sp.Encode1(1); // pic
                                sp.Encode1(0);
                                sp.Encode8(client.getClientKey()); // client key. for migrate packet.
                            }
                            break;
                        }
                        case EMS: {
                            sp.Encode4(client.getId()); // m_dwAccountId
                            // EMS v55-v70
                            if (Version.PreBB()) {
                                sp.Encode1(0);
                            }
                            sp.Encode1(client.getGender()); // m_nGender
                            sp.Encode1(client.isGameMaster() ? 1 : 0); // m_nGradeCode
                            if (ServerConfig.JMS164orLater()) {
                                sp.Encode1(client.isGameMaster() ? 1 : 0);
                            }
                            sp.EncodeStr(client.getMapleId()); // m_sNexonClubID
                            sp.Encode1(0); // m_nPurchaseExp
                            sp.Encode1(0); // m_nChatBlockReason
                            sp.Encode8(0); // m_dtChatUnblockDate
                            if (Version.PreBB()) {
                                sp.Encode8(0); // m_dtRegisterDate
                                // v70+
                                sp.Encode1(1);
                            } else {
                                if (Version.GreaterOrEqual(Region.EMS, 89)) {
                                    sp.Encode1(0);
                                    sp.EncodeStr("");
                                }
                                sp.EncodeStr("");
                                sp.Encode4(0);
                            }
                            break;
                        }
                        case BMS: {
                            sp.Encode1(0); // m_nRegStatID
                            sp.Encode4(0); // m_nUseDay
                            sp.Encode4(client.getId()); // m_dwAccountId
                            sp.Encode1(client.getGender()); // m_nGender
                            sp.Encode1(client.isGameMaster() ? 1 : 0); // m_nGradeCode
                            sp.Encode1(client.isGameMaster() ? 1 : 0);
                            sp.EncodeStr(client.getMapleId()); // m_sNexonClubID
                            sp.Encode1(0); // m_nPurchaseExp
                            sp.Encode1(0); // m_nChatBlockReason
                            sp.Encode8(0); // m_dtChatUnblockDate
                            sp.Encode8(0); // m_dtRegisterDate
                            sp.Encode1(0);
                            sp.Encode1(2); // pic
                            break;
                        }
                        case VMS: {
                            sp.Encode4(client.getId());
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.EncodeStr(client.getMapleId());
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.Encode8(0);
                            sp.EncodeStr("");
                            break;
                        }
                        case IMS: {
                            sp.Encode4(client.getId());
                            sp.Encode1(client.getGender());
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.EncodeStr(client.getMapleId());
                            sp.Encode4(3);
                            sp.Encode1(1);
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.Encode8(0);
                            sp.EncodeStr("");
                            sp.Encode1(1); // unlock jobs
                            sp.Encode1(0); // order?
                            for (int i = 0; i < 6; i++) {
                                sp.Encode1(i == 5 ? 0 : 1); // available job, dual blade is broken
                                sp.Encode2(i); // job index
                            }
                            break;
                        }
                    }
                }
                break;
            }

            case LoginResCode_Blocked: {
                sp.Encode1(32); // 0x20 and 0x40 are blue message flag
                break;
            }
            default: {
                sp.Encode1(0); // no blue message
                if (Region.BMS.check()) {
                    sp.Encode4(0);
                }
                break;
            }
        }
        return sp;
    }

    // CLogin::OnGuestIDLoginResult
    public static ServerPacket GuestIDLoginResult(MapleClient client, OpsLogin ops, int m_nRegStatID) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuestIDLoginResult);

        sp.Encode1(ops.get()); // ops code
        sp.Encode1(m_nRegStatID); // m_nRegStatID

        switch (ops) {
            case LoginResCode_Success:
            case LoginResCode_NotAdult:
            case LoginResCode_NotagreedEULA: {
                if (m_nRegStatID == 0 || m_nRegStatID == 1) {
                    sp.Encode4(client.getId()); // m_dwAccountId
                    sp.Encode1(client.getGender()); // m_nGender
                    sp.Encode1(0); // m_nGradeCode
                    sp.Encode1(0); // m_nCountryID
                    sp.Encode1(0); // unused.
                    sp.EncodeStr(client.getMapleId()); // m_sNexonClubID
                    sp.Encode1(0); // m_nPurchaseExp
                    sp.Encode1(0); // m_nChatBlockReason
                    sp.Encode8(0); // m_dtChatUnblockDate
                    sp.Encode8(0); // m_dtRegisterDate
                    sp.Encode4(0); // m_nNumOfCharacter
                    sp.EncodeStr(""); // m_URLGuestIDRegistration
                }
            }
            default: {
                break;
            }
        }

        return sp;
    }

    // CLogin::OnAccountInfoResult
    public static ServerPacket AccountInfoResult(MapleClient client, OpsLogin ops) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AccountInfoResult);

        sp.Encode1(ops.get());
        switch (ops) {
            case LoginResCode_Success:
            case LoginResCode_NotAdult:
            case LoginResCode_NotagreedEULA: {
                sp.Encode4(client.getId()); // m_dwAccountId
                sp.Encode1(client.getGender()); // m_nGender
                sp.Encode1(0); // m_nGradeCode
                sp.Encode2(0); // m_nSubGradeCode | (m_bTesterAccount << 8)
                sp.Encode1(0); // m_nCountryID
                sp.EncodeStr(client.getMapleId()); // m_sNexonClubID
                sp.Encode1(0); // m_nPurchaseExp
                sp.Encode1(0); // m_nChatBlockReason
                sp.Encode8(0); // m_dtChatUnblockDate
                sp.Encode8(0); // m_dtRegisterDate
                sp.Encode4(0); // m_nNumOfCharacter
                sp.Encode8(0); // m_aClientKey
            }
            default: {
                break;
            }
        }

        return sp;
    }

    // CLogin::OnCheckUserLimitResult
    public static ServerPacket CheckUserLimitResult(int status) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CheckUserLimitResult);

        sp.Encode2(status);
        return sp;
    }

    // CLogin::OnSetAccountResult
    public static ServerPacket SetAccountResult() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetAccountResult);

        sp.Encode1(0); // m_nGender
        sp.Encode1(0);
        return sp;
    }

    // CLogin::OnConfirmEULAResult
    public static ServerPacket ConfirmEULAResult() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ConfirmEULAResult);

        sp.Encode1(0);
        return sp;
    }

    // CLogin::OnCheckPinCodeResult
    public static ServerPacket CheckPinCodeResult(OpsPinCodeResCode ops) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CheckPinCodeResult);

        sp.Encode1(ops.get());
        return sp;
    }

    // CLogin::OnUpdatePinCodeResult
    public static ServerPacket UpdatePinCodeResult() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UpdatePinCodeResult);

        sp.Encode1(OpsLogin.LoginResCode_Success.get());
        sp.Encode4(0);
        return sp;
    }

    // CLogin::OnViewAllCharResult
    public static ServerPacket ViewAllCharResult(MapleClient client, OpsViewAllChar ops) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ViewAllCharResult);
        List<MapleCharacter> chars = client.loadCharactersFromDB(); // world 0 only (test)

        sp.Encode1(ops.get());
        switch (ops) {
            case VAC_ResCode_Success: {
                sp.Encode1(0); // m_anWorldID
                sp.Encode1(chars.size()); // m_nCountRelatedSvrs
                for (MapleCharacter chr : chars) {
                    sp.EncodeBuffer(DataGW_CharacterStat.Encode(chr));
                    sp.EncodeBuffer(DataAvatarLook.Encode(chr));
                    sp.Encode1(1); // ranking
                    // m_aRankVAC 16 bytes.
                    sp.Encode4(chr.getRank()); // all world ranking
                    sp.Encode4(chr.getRankMove());
                    sp.Encode4(chr.getJobRank()); // world ranking
                    sp.Encode4(chr.getJobRankMove());
                }

                if (Region.GMS.check()) {
                    sp.Encode1(2); // m_bLoginOpt
                }
                break;
            }
            case VAC_ResCode_CountRelatedSvrs: {
                sp.Encode4(1); // m_nCountRelatedSvrs
                sp.Encode4(chars.size()); // m_nCountCharacters
                break;
            }
            case VAC_ResCode_TimedOut:
            case VAC_ResCode_DBError:
            case VAC_ResCode_VADDlgAlreadyOn: {
                sp.Encode1(1);
                sp.EncodeStr("");
                break;
            }
            default: {
                break;
            }
        }

        return sp;
    }

    // CLogin::OnSelectCharacterByVACResult
    public static ServerPacket SelectCharacterByVACResult(TacosServer game_server, int character_id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SelectCharacterByVACResult);

        sp.Encode1(0);
        sp.Encode1(0);
        sp.Encode4(TacosTools.getGameServerIP(game_server.getGlobalIP())); // sin_addr
        sp.Encode2(game_server.getPort()); // sin_port
        sp.Encode4(character_id); // m_dwCharacterId
        sp.Encode1(0); // bAuthenCode, (m_bPremium << 1)
        sp.Encode4(0); // m_ulPremiumArgument
        return sp;
    }

    // CLogin::OnWorldInformation
    public static ServerPacket WorldInformation(TacosWorld world) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_WorldInformation);

        if (Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
            sp.Encode2((world != null) ? world.getId() : -1);
        } else {
            sp.Encode1((world != null) ? world.getId() : -1); // nWorldID
        }

        // 終了
        if (world == null) {
            if (Version.GreaterOrEqual(Region.KMS, 148) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 116)) {
                sp.Encode1(0);
            }
            return sp;
        }

        sp.EncodeStr(world.getName()); // sName
        sp.Encode1(0); // nWorldState
        sp.EncodeStr(Region.BMS.check() ? "" : world.getEvent()); // sWorldEventDesc

        if (Version.LessOrEqual(Region.KMS, 1)) {

        } else {
            sp.Encode2(100); // nWorldEventEXP_WSE
            sp.Encode2(100); // nWorldEventDrop_WSE
            if (Region.GMS.check() || Region.GMST.check() || Region.BMS.check()) {
                sp.Encode1(0); // nBlockCharCreation
            }
        }

        // チャンネル数
        sp.Encode1(world.getChannels().size());
        if (Region.CMS.check()) {
            sp.Encode4(500); // 0 causes 0 div
        }
        // チャンネル情報
        for (TacosChannel channel : world.getChannels()) {
            // チャンネル名
            sp.EncodeStr(channel.getName()); // sName
            // 接続人数表示
            sp.Encode4(channel.getOnlinePlayers().get().size() * 200); // nUserNo
            // ワールドID
            sp.Encode1(world.getId()); // nWorldID
            sp.Encode1(channel.getWorld().getId()); // nChannelID
            sp.Encode1(channel.getLanguage()); // bAdultChannel?
            if (Version.GreaterOrEqual(Region.JMS, 302)) {
                sp.Encode1(0);
            }
        }

        sp.Encode2(0); // m_nBalloonCount
        if (ServerConfig.KMS118orLater() || Version.GreaterOrEqual(Region.JMS, 302) || Version.Equal(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode4(0);
        }
        if (Version.GreaterOrEqual(Region.EMS, 89)) {
            sp.Encode4(0);
        }
        return sp;
    }

    // CLogin::OnSelectWorldResult
    public static ServerPacket SelectWorldResult(MapleClient client, OpsLogin result) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SelectWorldResult);
        sp.Encode1(result.get());
        if (result != OpsLogin.LoginResCode_Success) {
            // error
            return sp;
        }

        if (Region.TWMS.check()) {
            sp.EncodeBuffer(CharList_TWMS(client));
            return sp;
        }
        if (Region.CMS.check()) {
            sp.EncodeBuffer(CharList_CMS(client));
            return sp;
        }
        List<MapleCharacter> chars = client.loadCharactersFromDB(true);
        int charslots = client.getCharSlots();

        if (Region.JMS.check() || Region.JMST.check()) {
            sp.EncodeStr("");
        }

        if (Region.KMSB.check() || Version.LessOrEqual(Region.KMS, 149) || Region.KMST.check() || Region.CMS.check() || Region.IMS.check()) {
            sp.Encode4(1000000);
        }

        // キャラクターの数
        sp.Encode1(chars.size());
        for (MapleCharacter chr : chars) {
            if (Region.KMSB.check()) {
                sp.EncodeBuffer(DataCharacterData.Encode(chr, 1));
                continue;
            }
            //Structure.CharEntry(p, chr, true, false);
            sp.EncodeBuffer(DataGW_CharacterStat.Encode(chr));
            sp.EncodeBuffer(DataAvatarLook.Encode(chr));
            if ((Region.JMS.check() || Region.JMST.check() || Region.KMS.check() || Region.KMST.check() || Region.IMS.check() || Region.EMS.check() || Region.THMS.check() || Region.MSEA.check())
                    && (ServerConfig.JMS180orLater() || Version.GreaterOrEqual(Region.KMS, 84))
                    || Version.GreaterOrEqual(Region.GMS, 83)) {
                sp.Encode1(0); // family
            }
            sp.Encode1(1); // ranking
            sp.Encode4(chr.getRank()); // all world ranking
            sp.Encode4(chr.getRankMove());
            sp.Encode4(chr.getJobRank()); // world ranking
            sp.Encode4(chr.getJobRankMove());
        }

        if (Region.KMSB.check() || Version.LessOrEqual(Region.KMS, 31)) {
            return sp;
        }

        if (Version.GreaterOrEqual(Region.KMS, 160)) {
            sp.Encode1(1); // 2nd password disabled
            sp.Encode1(0); // 2nd password disabled
            sp.Encode4(charslots);
            sp.Encode4(0);
            sp.Encode4(0);
            sp.Encode4(0);
            if (Version.GreaterOrEqual(Region.KMS, 169)) {
                sp.Encode4(0);
                sp.Encode1(0);
            }
            return sp;
        }

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(2); // 2次パス無視
            sp.Encode4(charslots);
            sp.Encode4(0);
            sp.Encode4(0);
            sp.Encode4(0);
            if (Version.GreaterOrEqual(Region.JMS, 308)) {
                sp.Encode4(0);
                sp.Encode1(0);
            }
            return sp;
        }

        if (Version.GreaterOrEqual(Region.EMS, 89)) {
            sp.Encode1(1);
            sp.Encode1(0);
            sp.Encode4(charslots);
            sp.Encode4(0);
            sp.Encode4(0);
            sp.Encode4(0);
            sp.Encode4(0);
            sp.Encode1(0);
            sp.Encode8(0);
            // job unlock (clickable, not gray out lol)
            sp.Encode1(1);
            sp.Encode1(1);
            sp.Encode1(1);
            sp.Encode1(1);
            sp.Encode1(1);
            sp.Encode1(1);
            sp.Encode1(1);
            sp.Encode1(1);
            sp.Encode1(1);
            sp.Encode1(1);
            sp.Encode1(1);
            sp.Encode1(1);
            sp.Encode1(1);
            sp.Encode1(1);
            return sp;
        }

        if (Region.BMS.check()) {
            sp.Encode4(charslots);
            return sp;
        }

        if (Region.MSEA.check() || Region.IMS.check()) {
            sp.Encode1(2);
            sp.Encode1(0);
            sp.Encode4(charslots);
            sp.Encode4(0);
            return sp;
        }

        if (Region.THMS.check()) {
            sp.Encode1(2); // 2nd password ingored
            sp.Encode1(0);
            sp.Encode4(charslots);
            sp.Encode4(0);
            sp.Encode8(0);
            return sp;
        }

        if (Version.Between(Region.JMS, 146, 147) || Region.VMS.check()) {
            sp.Encode1(2); // 2次パス無視
            sp.Encode1(0);
            sp.Encode4(charslots); // m_nSlotCount
            return sp;
        }

        if (Version.GreaterOrEqual(Region.GMS, 91)) {
            sp.Encode1(2); // m_bLoginOpt
            if (Version.GreaterOrEqual(Region.GMS, 111)) {
                sp.Encode1(0);
            }
            sp.Encode4(charslots); // m_nSlotCount
            sp.Encode4(0); // m_nBuyCharCount
            if (Version.GreaterOrEqual(Region.GMS, 116)) {
                sp.Encode4(0);
                sp.Encode4(0);
            }
            if (Version.GreaterOrEqual(Region.GMS, 126)) {
                sp.Encode4(0);
                sp.Encode1(0);
            }
            return sp;
        }

        if (Version.GreaterOrEqual(Region.GMS, 83)) {
            sp.Encode1(2); // m_bLoginOpt
        }
        if (Version.GreaterOrEqual(Region.GMS, 82)) {
            sp.Encode4(charslots); // m_nSlotCount
            return sp;
        }

        // EMS v55
        if (Version.LessOrEqual(Region.GMS, 73) || Version.LessOrEqual(Region.EMS, 55)) {
            sp.Encode4(charslots); // m_nSlotCount
            return sp;
        }
        if (Region.EMS.check() && Version.getVersion() <= 70) {
            sp.Encode4(charslots); // m_nSlotCount
            sp.Encode4(0);
            sp.Encode8(0);
            return sp;
        }

        if (Region.KMS.check() || Region.KMST.check() || Region.EMS.check()) {
            sp.Encode1(2);
            sp.Encode1(0);
            sp.Encode4(charslots); // m_nSlotCount
            if (Version.PostBB()) {
                sp.Encode4(0); // m_nBuyCharCount
            }
            if (Region.EMS.check()) {
                sp.Encode8(0);
            }
            return sp;
        }
        // BIGBANG
        if (Version.Equal(Region.JMS, 187)) {
            sp.Encode1(2); // 2次パス無視
            sp.Encode1(0);
            sp.Encode4(charslots);
            sp.Encode4(1); // Character Cards
            return sp;
        }
        if (Version.LessOrEqual(Region.JMS, 131)) {
            sp.Encode1(3); // charslots
            sp.Encode1(0);
            return sp;
        }
        // 2次パスワードの利用状態
        if (Version.PostBB()) {
            sp.Encode1(0);
        } else {
            sp.Encode2(2);
        }

        if (ServerConfig.JMS194orLater()) {
            sp.Encode4(charslots);
            sp.Encode4(0); // Character Card
            sp.Encode4(0); // idk
            return sp;
        }

        if (Version.LessOrEqual(Region.JMS, 176)) {
            sp.Encode4(charslots);
        } else {
            sp.Encode8(charslots);
        }
        return sp;
    }

    // CLogin::OnSelectCharacterResult
    public static ServerPacket SelectCharacterResult(TacosServer game_server, int character_id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SelectCharacterResult);
        sp.Encode1(0);
        sp.Encode1(0);
        sp.Encode4(TacosTools.getGameServerIP(game_server.getGlobalIP()));
        sp.Encode2(game_server.getPort());
        sp.Encode4(character_id);
        sp.Encode1(0);
        sp.Encode4(0);

        if (Version.GreaterOrEqual(Region.GMS, 126)) {
            sp.Encode1(0);
            sp.Encode1(0);
            sp.Encode8(0);
            sp.Encode1(0);
            return sp;
        }

        if (Version.GreaterOrEqual(Region.KMS, 169) || Version.GreaterOrEqual(Region.EMS, 89)) {
            sp.Encode1(0);
            sp.Encode8(0);
            return sp;
        }

        if (Version.GreaterOrEqual(Region.KMS, 148) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode1(0);
            sp.Encode2(0);
            sp.Encode2(0);
        }

        return sp;
    }

    // CLogin::OnCheckDuplicatedIDResult
    public static ServerPacket CheckDuplicatedIDResult(String name, boolean isOK) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CheckDuplicatedIDResult);

        sp.EncodeStr(name); // m_sCheckedName
        sp.Encode1(isOK ? 0 : 1); // 0 = OK
        return sp;
    }

    // CLogin::OnCreateNewCharacterResult
    public static ServerPacket CreateNewCharacterResult(MapleCharacter chr, OpsLogin ops) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CreateNewCharacterResult);

        sp.Encode1(ops.get());
        if (ops == OpsLogin.LoginResCode_Success) {
            if (Region.KMSB.check()) {
                sp.EncodeBuffer(DataCharacterData.Encode(chr, 1));
                return sp;
            }
            sp.EncodeBuffer(DataGW_CharacterStat.Encode(chr));
            sp.EncodeBuffer(DataAvatarLook.Encode(chr));
        }

        return sp;
    }

    // CLogin::OnDeleteCharacterResult
    public static ServerPacket DeleteCharacterResult(int character_id, OpsLogin ops) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_DeleteCharacterResult);

        sp.Encode4(character_id);
        sp.Encode1(ops.get());
        return sp;
    }

    // CLogin::OnCheckGameGuardUpdatedResult
    public static ServerPacket CheckGameGuardUpdated(boolean isOK) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_CheckGameGuardUpdatedResult);
        // 0 = Update Game Guard
        // 1 = Enable Login Button
        sp.Encode1(isOK ? 1 : 0);
        return sp;
    }

    // CLogin::OnEnableSPWResult
    public static ServerPacket EnableSPWResult() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_EnableSPWResult);

        sp.Encode1(0);
        sp.Encode1(0);
        return sp;
    }

    public static ServerPacket JMS187_UNK0012() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_187_UNK_0012);

        sp.EncodeStr("unk12");
        return sp;
    }

    public static ServerPacket SafetyPasswordResult(boolean is_failed) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_SafetyPasswordResult);

        sp.Encode1(is_failed ? 1 : 0);
        return sp;
    }

    // CLogin::OnLatestConnectedWorld
    public static ServerPacket LatestConnectedWorld() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_LatestConnectedWorld);

        sp.Encode4(0); // m_nLatestConnectedWorldID
        return sp;
    }

    // CLogin::OnRecommendWorldMessage
    public static ServerPacket RecommendWorldMessage() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_RecommendWorldMessage);
        String[] recommendedReasons = {"これはSELECTを押してもワールドがアクティブになるだけです", "ゴミ機能です", "XXXX"};

        sp.Encode1(recommendedReasons.length);

        for (int world_id = 0; world_id < recommendedReasons.length; world_id++) {
            sp.Encode4(world_id); // nWorldID
            sp.EncodeStr(recommendedReasons[world_id]); // sMessage
        }

        return sp;
    }

    public static ServerPacket SetMapLogin() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_SetMapLogin);

        // WzXMLの読み込み方法を変更しないと遅延するので、固定値にしておく
        sp.EncodeStr("MapLogin"); // WzXML.UI.getRandomMapLogin()
        if (Version.PostBB()) {
            sp.Encode4(2010121510); // JMS187 : 2010121510 (2010/12/15 10:00)
        }
        if (Version.GreaterOrEqual(Region.TWMS, 148)) {
            sp.Encode1(1);
        }

        return sp;
    }

    public static ServerPacket JMS187_UNK0019() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_187_UNK_0019);

        sp.Encode8(0);
        return sp;
    }

    // CLogin::OnExtraCharInfoResult, unused code.
    public static ServerPacket CheckExtraCharInfoResult(MapleClient client) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CheckExtraCharInfoResult);

        sp.Encode4(client.getId()); // m_dwAccountId
        sp.Encode1(0);
        return sp;
    }

    // CLogin::OnCheckSPWResult
    public static ServerPacket OnCheckSPWResult() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CheckSPWResult);

        sp.Encode1(0); // unused.
        return sp;
    }

    public static byte[] CharList_CMS(MapleClient client) {
        ServerPacket data = new ServerPacket();
        if (Version.LessOrEqual(Region.CMS, 88)) {
            data.Encode4(1000000);
        }
        List<MapleCharacter> chars = client.loadCharactersFromDB();
        int charslots = client.getCharSlots();
        data.Encode1(chars.size());
        for (MapleCharacter chr : chars) {
            data.EncodeBuffer(DataGW_CharacterStat.Encode(chr));
            data.EncodeBuffer(DataAvatarLook.Encode(chr));
            data.Encode1(0);
        }
        data.Encode1(3);
        data.Encode1(0);
        data.Encode4(charslots);
        data.Encode4(0); // card
        if (Version.GreaterOrEqual(Region.CMS, 104)) {
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
        }

        return data.getBytes();
    }

    public static byte[] CharList_TWMS(MapleClient client) {
        ServerPacket data = new ServerPacket();
        if (!Version.GreaterOrEqual(Region.TWMS, 148)) {
            data.Encode4(1000000);
        }
        List<MapleCharacter> chars = client.loadCharactersFromDB();
        int charslots = client.getCharSlots();
        data.Encode1(chars.size());
        for (MapleCharacter chr : chars) {
            data.EncodeBuffer(DataGW_CharacterStat.Encode(chr));
            data.EncodeBuffer(DataAvatarLook.Encode(chr));
            if (Version.GreaterOrEqual(Region.TWMS, 121)) {
                data.Encode1(0);
            }
            data.Encode1(1);
            data.Encode4(chr.getRank());
            data.Encode4(chr.getRankMove());
            data.Encode4(chr.getJobRank());
            data.Encode4(chr.getJobRankMove());
        }

        if (Version.GreaterOrEqual(Region.TWMS, 148)) {
            data.Encode1(3);
            data.Encode1(0);
            data.Encode4(charslots);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode8(0);
        } else if (Version.GreaterOrEqual(Region.TWMS, 121)) {
            data.Encode2(3); // 2nd password state
            data.Encode8(charslots);
            data.Encode8(0);
        } else {
            // TWMS v94
            data.Encode1(3);
            data.Encode1(0);
            data.Encode4(charslots);
        }

        return data.getBytes();
    }
}
