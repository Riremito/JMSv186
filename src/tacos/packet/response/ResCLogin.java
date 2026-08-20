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
import tacos.client.TacosClient;
import tacos.config.Region;
import tacos.config.Config;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsLogin;
import tacos.packet.ops.OpsPinCodeResCode;
import tacos.packet.ops.OpsViewAllChar;
import tacos.packet.response.data.RD_AvatarLook;
import tacos.packet.response.data.RD_CharacterData;
import tacos.packet.response.data.RD_CharacterStat;
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

    public static ServerPacket CheckPasswordResult(TacosClient client, int result) {
        return CheckPasswordResult(client, OpsLogin.find(result));
    }

    // CLogin::OnCheckPasswordResult
    public static ServerPacket CheckPasswordResult(TacosClient client, OpsLogin ops) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CheckPasswordResult);

        sp.Encode1(ops.get()); // ops
        sp.Encode1(0, Region.GMS.check() || Region.GMST.check() || Config.Between(Region.EMS, 55, 70));
        sp.Encode4(0, Region.GMS.check() || Region.GMST.check() || Config.Between(Region.EMS, 55, 70)); // unused

        switch (ops) {
            case LoginResCode_Success: {
                {
                    switch (Config.REGION) {
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
                            if (Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.KMST, 330)) {
                                sp.Encode1(client.isGameMaster() ? 1 : 0);
                            }
                            if (Config.GreaterOrEqual(Region.KMS, 160)) {
                                sp.Encode4(3);
                                if (Config.GreaterOrEqual(Region.KMS, 169)) {
                                    sp.Encode4(0);
                                }
                                sp.Encode1(0);
                                sp.Encode1(0);
                                sp.Encode8(0);
                                sp.Encode1(0);
                                sp.EncodeStr(client.getMapleId());
                                sp.EncodeStr("");
                                if (Config.GreaterOrEqual(Region.KMS, 197)) {
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
                            } else {
                                sp.EncodeStr(client.getMapleId()); // m_sNexonClubID
                                sp.Encode4(3); // should be 3 for KMS v2.114 to ignore personal number
                                sp.Encode1(1); // should be 1 for KMS v2.114 to ignore personal number
                                sp.Encode1(0); // m_nPurchaseExp
                                sp.Encode1(0); // m_nChatBlockReason
                                sp.Encode8(0); // m_dtChatUnblockDate
                                sp.EncodeStr("");
                            }
                            break;
                        }
                        case JMS:
                        case JMST:
                        default: {
                            sp.Encode1(0); // OK
                            sp.Encode4(client.getId()); // m_dwAccountId
                            sp.Encode1(client.getGender()); // m_nGender
                            sp.Encode1(client.isGameMaster() ? 1 : 0); // m_nGradeCode
                            sp.Encode1(client.isGameMaster() ? 1 : 0, Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.JMST, 110));
                            sp.Encode4(0, Config.GreaterOrEqual(Region.JMS, 308));
                            sp.Encode4(0, Config.GreaterOrEqual(Region.JMS, 308));
                            sp.Encode1(0, Config.GreaterOrEqual(Region.JMS, 308));
                            sp.Encode1(0, Config.GreaterOrEqual(Region.JMS, 308));
                            sp.EncodeStr(client.getMapleId()); // m_sNexonClubID
                            sp.EncodeStr(client.getMapleId());
                            sp.Encode1(0);
                            sp.Encode1(0); // m_nPurchaseExp
                            sp.Encode1(0); // m_nChatBlockReason
                            sp.Encode1(0, Config.Between(Region.JMS, 131, 302) || Config.GreaterOrEqual(Region.JMST, 110));
                            sp.Encode1(0, Config.Between(Region.JMS, 164, 302) || Config.GreaterOrEqual(Region.JMST, 110));
                            sp.Encode1(0, Config.Between(Region.JMS, 180, 302) || Config.GreaterOrEqual(Region.JMST, 110));
                            sp.Encode1(-1, Config.GreaterOrEqual(Region.JMS, 187) || Config.GreaterOrEqual(Region.JMST, 110)); // 2nd password.
                            sp.Encode1(1, Config.GreaterOrEqual(Region.JMS, 302)); // NexonID state.
                            sp.Encode8(0); // m_dtChatUnblockDate or client key (never used in JMS?)
                            sp.Encode1(1, Config.GreaterOrEqual(Region.JMS, 308));
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
                            if (Config.Between(Region.CMS, 85, 88)) {
                                sp.Encode1(0);
                            }
                            sp.Encode8(SharedDate.getTimestamp());
                            sp.Encode1(0);
                            sp.Encode8(SharedDate.getTimestamp());
                            sp.Encode8(0);
                            sp.EncodeStr("");
                            sp.Encode1(1); // 0 = open blue message box.
                            sp.EncodeStr(String.valueOf(client.getId()));
                            sp.EncodeStr(client.getMapleId());
                            sp.Encode1(1);
                            if (Config.GreaterOrEqual(Region.CMS, 104)) {
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
                            if (Config.GreaterOrEqual(Region.TWMS, 94)) {
                                sp.Encode1(client.isGameMaster() ? 1 : 0);
                            }
                            if (Config.GreaterOrEqual(Region.TWMS, 121)) {
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
                            sp.Encode4(client.getId()); // m_dwAccountId
                            sp.Encode1(client.getGender()); // m_nGender
                            sp.Encode1(client.isGameMaster() ? 4 : 0); // m_nGradeCode
                            if (Config.GreaterOrEqual(Region.GMS, 95) || Config.GreaterOrEqual(Region.GMST, 2)) {
                                sp.Encode2(client.isGameMaster() ? 1 : 0); // Admin F1
                            } else if (Config.GreaterOrEqual(Region.GMS, 72)) {
                                sp.Encode1(client.isGameMaster() ? 0x80 : 0); // Admin F1
                            }
                            if (Config.GreaterOrEqual(Region.GMS, 126)) {
                                sp.Encode4(0);
                            }
                            sp.Encode1(0);
                            sp.EncodeStr(client.getMapleId()); // m_sNexonClubID
                            sp.Encode1(0); // m_nPurchaseExp
                            sp.Encode1(0); // m_nChatBlockReason
                            sp.Encode8(0); // m_dtChatUnblockDate
                            if (Config.Between(Region.GMS, 111, 126)) {
                                sp.Encode1(0);
                            }
                            sp.Encode8(0); // m_dtRegisterDate
                            sp.Encode4(0);
                            if (Config.GreaterOrEqual(Region.GMS, 131)) {
                                sp.Encode1(1); // available job
                                {
                                    sp.Encode1(0);
                                    for (int i = 0; i < 17; i++) {
                                        sp.Encode1(1);
                                    }
                                }
                            }
                            if (Config.GreaterOrEqual(Region.GMS, 82) || Config.GreaterOrEqual(Region.GMST, 2)) {
                                sp.Encode1(1); // 2nd password.
                                sp.Encode1(0);
                            }
                            if (Config.GreaterOrEqual(Region.GMS, 84) || Config.GreaterOrEqual(Region.GMST, 2)) {
                                sp.Encode8(client.getClientKey());
                            }
                            break;
                        }
                        case EMS: {
                            sp.Encode4(client.getId()); // m_dwAccountId
                            if (Config.Between(Region.EMS, 55, 70)) {
                                sp.Encode1(0);
                            }
                            sp.Encode1(client.getGender()); // m_nGender
                            sp.Encode1(client.isGameMaster() ? 1 : 0); // m_nGradeCode
                            sp.Encode1(client.isGameMaster() ? 1 : 0);
                            sp.EncodeStr(client.getMapleId()); // m_sNexonClubID
                            sp.Encode1(0); // m_nPurchaseExp
                            sp.Encode1(0); // m_nChatBlockReason
                            sp.Encode8(0); // m_dtChatUnblockDate
                            if (Config.Between(Region.EMS, 55, 70)) {
                                sp.Encode8(0); // m_dtRegisterDate
                                sp.Encode1(0); // password renew.
                            }
                            if (Config.GreaterOrEqual(Region.EMS, 89)) {
                                sp.Encode1(0);
                                sp.EncodeStr("");
                            }
                            if (Config.GreaterOrEqual(Region.EMS, 76)) {
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
    public static ServerPacket GuestIDLoginResult(TacosClient client, OpsLogin ops, int m_nRegStatID) {
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
    public static ServerPacket AccountInfoResult(TacosClient client, OpsLogin ops) {
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
    public static ServerPacket ViewAllCharResult(TacosClient client, OpsViewAllChar ops) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ViewAllCharResult);

        sp.Encode1(ops.get());
        switch (ops) {
            case VAC_ResCode_Success: {
                sp.Encode1(0); // m_anWorldID
                sp.Encode1(client.getCharacters().size()); // m_nCountRelatedSvrs
                for (MapleCharacter chr : client.getCharacters()) {
                    sp.EncodeBuffer(RD_CharacterStat.Encode(chr));
                    sp.EncodeBuffer(RD_AvatarLook.Encode(chr));
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
                sp.Encode4(client.getCharacters().size()); // m_nCountCharacters
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

        if (Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.CMS, 104)) {
            sp.Encode2((world != null) ? world.getId() : -1);
        } else {
            sp.Encode1((world != null) ? world.getId() : -1); // nWorldID
        }

        // world list end.
        if (world == null) {
            if (Config.GreaterOrEqual(Region.KMS, 148) || Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.GMS, 116)) {
                sp.Encode1(0);
            }
            return sp;
        }

        sp.EncodeStr(world.getName()); // sName
        sp.Encode1(0); // nWorldState
        sp.EncodeStr(Region.BMS.check() ? "" : world.getEvent()); // sWorldEventDesc
        if (Config.LessOrEqual(Region.KMS, 3)) {
            // none
        } else {
            sp.Encode2(100); // nWorldEventEXP_WSE
            sp.Encode2(100); // nWorldEventDrop_WSE
            if (Region.GMS.check() || Region.GMST.check() || Region.BMS.check()) {
                sp.Encode1(0); // nBlockCharCreation
            }
        }
        sp.Encode1(world.getChannels().size());
        if (Region.CMS.check()) {
            sp.Encode4(500); // 0 causes 0 div
        }
        for (TacosChannel channel : world.getChannels()) {
            sp.EncodeStr(channel.getName()); // sName
            sp.Encode4(channel.getOnlinePlayers().get().size() * 200); // nUserNo
            sp.Encode1(world.getId()); // nWorldID
            sp.Encode1(channel.getWorld().getId()); // nChannelID
            sp.Encode1(channel.getLanguage()); // bAdultChannel?
            if (Config.GreaterOrEqual(Region.JMS, 302)) {
                sp.Encode1(0);
            }
        }
        if (Config.LessOrEqual(Region.KMS, 43) || Config.LessOrEqual(Region.JMS, 131)) {
            return sp;
        }
        sp.Encode2(0); // m_nBalloonCount
        if (Config.GreaterOrEqual(Region.KMS, 118) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode4(0);
        }
        if (Config.GreaterOrEqual(Region.EMS, 89)) {
            sp.Encode4(0);
        }

        return sp;
    }

    // CLogin::OnSelectWorldResult
    public static ServerPacket SelectWorldResult(TacosClient client, OpsLogin result) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SelectWorldResult);

        sp.Encode1(result.get());
        if (result != OpsLogin.LoginResCode_Success) {
            // error
            return sp;
        }
        if (Region.JMS.check() || Region.JMST.check()) {
            sp.EncodeStr("");
        }
        if (Region.KMSB.check() || Config.Between(Region.KMS, 1, 149) || Config.Between(Region.KMST, 330, 391) || Config.Between(Region.CMS, 85, 88) || Config.Between(Region.TWMS, 74, 125) || Region.IMS.check()) {
            // KMS1-149
            // KMST330-391
            // CMS85-88
            // TWMS74-125
            // IMS1
            sp.Encode4(1000000);
        }
        // character list
        sp.Encode1(client.getCharacters().size());
        for (MapleCharacter chr : client.getCharacters()) {
            if (Region.KMSB.check()) {
                sp.EncodeBuffer(RD_CharacterData.Encode(chr, 1));
                continue;
            }
            // character data
            sp.EncodeBuffer(RD_CharacterStat.Encode(chr));
            sp.EncodeBuffer(RD_AvatarLook.Encode(chr));
            // family
            if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 84) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 83) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)) {
                sp.Encode1(0);
            }
            if (Region.CMS.check()) {
                continue;
            }
            // ranking
            sp.Encode1(1);
            sp.Encode4(chr.getRank()); // all world ranking
            sp.Encode4(chr.getRankMove());
            sp.Encode4(chr.getJobRank()); // world ranking
            sp.Encode4(chr.getJobRankMove());
        }
        // 2nd password.
        if (Config.LessOrEqual(Region.KMS, 31)) {
            return sp;
        }
        if (Config.GreaterOrEqual(Region.KMS, 160) || Config.GreaterOrEqual(Region.EMS, 89)) {
            sp.Encode1(1); // m_bLoginOpt
            sp.Encode1(0);
        } else if (Config.GreaterOrEqual(Region.JMS, 302) || Config.Between(Region.GMS, 83, 95) || Config.GreaterOrEqual(Region.GMST, 2)) {
            sp.Encode1(2); // m_bLoginOpt
        } else if (Config.Between(Region.JMS, 188, 194) || Config.GreaterOrEqual(Region.JMST, 110)) {
            sp.Encode1(0);
        } else if (Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 74)) {
            sp.Encode1(3); // m_bLoginOpt
            sp.Encode1(0);
        } else if (Config.Between(Region.GMS, 61, 73) || Config.Between(Region.EMS, 55, 70)) {
            // none.
        } else {
            sp.Encode1(2);
            sp.Encode1(0);
        }
        // character slot.
        if (Config.LessOrEqual(Region.KMS, 43) || Config.LessOrEqual(Region.JMS, 131)) {
            return sp;
        }
        sp.Encode4(client.getCharSlots()); // m_nSlotCount
        if (Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)) {
            sp.Encode4(0); // m_nBuyCharCount
        }
        if (Config.GreaterOrEqual(Region.KMS, 160) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 116) || Config.GreaterOrEqual(Region.EMS, 89)) {
            sp.Encode4(0);
        }
        if (Config.GreaterOrEqual(Region.KMS, 160) || Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 116) || Config.GreaterOrEqual(Region.EMS, 89)) {
            sp.Encode4(0);
        }
        if (Config.GreaterOrEqual(Region.KMS, 169) || Config.GreaterOrEqual(Region.JMS, 308) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 126) || Config.GreaterOrEqual(Region.EMS, 89)) {
            sp.Encode4(0);
        }
        if (Config.GreaterOrEqual(Region.KMS, 169) || Config.GreaterOrEqual(Region.JMS, 308) || Config.GreaterOrEqual(Region.GMS, 126) || Config.GreaterOrEqual(Region.EMS, 89)) {
            sp.Encode1(0);
        }
        if (Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.EMS, 70)) {
            sp.Encode8(0);
        }
        // job unlock.
        if (Config.GreaterOrEqual(Region.EMS, 89)) {
            for (int i = 0; i < 14; i++) {
                sp.Encode1(1);
            }
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

        if (Config.GreaterOrEqual(Region.GMS, 126)) {
            sp.Encode1(0);
            sp.Encode1(0);
            sp.Encode8(0);
            sp.Encode1(0);
            return sp;
        }

        if (Config.GreaterOrEqual(Region.KMS, 169) || Config.GreaterOrEqual(Region.EMS, 89)) {
            sp.Encode1(0);
            sp.Encode8(0);
            return sp;
        }

        if (Config.GreaterOrEqual(Region.KMS, 148) || Config.GreaterOrEqual(Region.GMS, 111)) {
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
                sp.EncodeBuffer(RD_CharacterData.Encode(chr, 1));
                return sp;
            }
            sp.EncodeBuffer(RD_CharacterStat.Encode(chr));
            sp.EncodeBuffer(RD_AvatarLook.Encode(chr));
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
        if (Config.PostBB()) {
            sp.Encode4(2010121510); // JMS187 : 2010121510 (2010/12/15 10:00)
        }
        if (Config.GreaterOrEqual(Region.TWMS, 148)) {
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
    public static ServerPacket CheckExtraCharInfoResult(TacosClient client) {
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
}
