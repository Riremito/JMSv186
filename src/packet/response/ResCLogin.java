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

import client.MapleCharacter;
import client.MapleClient;
import config.ServerConfig;
import handling.MaplePacket;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import java.util.List;
import java.util.Random;
import packet.ServerPacket;
import packet.response.struct.AvatarLook;
import packet.response.struct.GW_CharacterStat;

/**
 *
 * @author Riremito
 */
public class ResCLogin {

    // ゲームサーバーへ接続
    // getServerIP
    // CClientSocket::OnSelectCharacter
    public static final MaplePacket SelectCharacterResult(final int port, final int clientId) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SelectCharacterResult);
        sp.Encode1(0);
        sp.Encode1(0);
        sp.Encode4((int) ResCClientSocket.getGameServerIP());
        sp.Encode2(port);
        sp.Encode4(clientId);
        sp.Encode1(0);
        sp.Encode4(0);
        if (ServerConfig.KMS148orLater()) {
            sp.Encode1(0);
            sp.Encode2(0);
            sp.Encode2(0);
        }
        return sp.get();
    }

    // v131 - v186 OK
    public enum LoginResult {
        SUCCESS(0x00),
        BLOCKED_MAPLEID_WITH_MESSAGE(0x02), // 青窓BAN
        BLOCKED_MAPLEID(0x03), // 無条件BAN
        INVALID_PASSWORD(0x04),
        UNREGISTERED_MAPLEID(0x05),
        SYSTEM_ERROR(0x06), // 0x08, 0x09
        ALREADY_LOGGEDIN(0x07),
        TOO_MANY_USERS(0x0A),
        INVALID_ADMIN_IP(0x0D),
        BLOCKED_IP(0x13),
        UNKNOWN;
        /*
        v131
        00 : OK
        01 : Crash
        02 : 削除又は接続中止になっているアカウントです。 (青窓BAN)
        03 : 削除又は接続中止になっているアカウントです。 (キノコBAN)
        04 : パスワードが間違っています。
        05 : 登録されてないアカウントです。
        06 : システムのエラーで接続出来ません。
        07 : 接続中のIDです。
        08 : システムのエラーで接続出来ません。
        09 : システムのエラーで接続出来ません。
        0A : 現在、サーバーへの接続要請が多すぎます。
        0B : 韓国語
        0C : なし
        0D : 現在のIPではマスターログイン出来ません。
        0E : 韓国語
        0F : 韓国語
        10 : Crash
        11 : 韓国語
        12 : Crash
        13 : 臨時遮断IP経由で接続しました。
        14以上 : Crash
         */
        private int value;

        LoginResult(int reason) {
            value = reason;
        }

        LoginResult() {
            value = -1;
        }

        public int Get() {
            return value;
        }

        public static LoginResult Find(int find_val) {
            for (final LoginResult lr : LoginResult.values()) {
                if (lr.Get() == find_val) {
                    return lr;
                }
            }
            return UNKNOWN;
        }
    }

// v131
// CLogin::OnCheckGameGuardUpdatedResult
    public static MaplePacket CheckGameGuardUpdated(boolean isOK) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_CheckGameGuardUpdatedResult);
        // 0 = Update Game Guard
        // 1 = Enable Login Button
        sp.Encode1(isOK ? 1 : 0);
        return sp.get();
    }

    // v186+
    // CLogin::OnRecommendWorldMessage
    public static MaplePacket RecommendWorldMessage() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_RecommendWorldMessage);
        String[] recommendedReasons = {"これはSELECTを押してもワールドがアクティブになるだけです", "ゴミ機能です", "XXXX"};
        sp.Encode1(recommendedReasons.length);
        for (int world_id = 0; world_id < recommendedReasons.length; world_id++) {
            sp.Encode4(world_id);
            sp.EncodeStr(recommendedReasons[world_id]);
        }
        return sp.get();
    }

    // CLogin::OnGuestIDLoginResult
    // CWvsContext::SetAccountInfo
    public static final MaplePacket GuestIDLoginResult(MapleClient c, LoginResult result) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuestIDLoginResult);
        sp.Encode1(result.Get()); // result code
        switch (result) {
            case SUCCESS: {
                sp.Encode4(c.getAccID()); // dwAccountId
                sp.Encode1(c.getGender()); // nGender
                sp.Encode1(0); // nGradeCode
                sp.EncodeStr(c.getAccountName()); // sNexonClubID
                sp.Encode1(0);
                sp.Encode1(0);
                sp.Encode8(0); // buf
                sp.EncodeStr("");
                sp.EncodeStr("");
            }
            default: {
                break;
            }
        }
        return sp.get();
    }

    // CLogin::OnViewAllCharResult
    public static MaplePacket ViewAllCharResult(MapleClient c, boolean isAlloc) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ViewAllCharResult);
        List<MapleCharacter> chars = c.loadCharacters(0); // world 0 only (test)
        sp.Encode1(isAlloc ? 1 : 0);

        if (isAlloc) {
            sp.Encode4(1); // m_nCountRelatedSvrs
            sp.Encode4(chars.size()); // m_nCountCharacters
        } else {
            sp.Encode1(0); // nWorldID
            sp.Encode1(chars.size());
            for (MapleCharacter chr : chars) {
                sp.EncodeBuffer(GW_CharacterStat.Encode(chr));
                sp.EncodeBuffer(AvatarLook.Encode(chr));
                sp.Encode1(1); // ranking
                sp.Encode4(chr.getRank()); // all world ranking
                sp.Encode4(chr.getRankMove());
                sp.Encode4(chr.getJobRank()); // world ranking
                sp.Encode4(chr.getJobRankMove());
            }
        }
        return sp.get();
    }

    public static final MaplePacket CheckPasswordResult(MapleClient client, int result) {
        return CheckPasswordResult(client, LoginResult.Find(result));
    }

    // CLogin::OnCheckPasswordResult
    // CClientSocket::OnCheckPassword
    // getAuthSuccessRequest, getLoginFailed
    public static final MaplePacket CheckPasswordResult(MapleClient client, LoginResult result) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CheckPasswordResult);
        sp.Encode1(result.Get()); // result

        // EMS v55-v70
        if (ServerConfig.IsGMS() || (ServerConfig.IsEMS() && ServerConfig.IsPreBB())) {
            sp.Encode1(0);
            sp.Encode4(0); // unused
        }
        /*
        v186 Message Flag
        00 : OK
        20 : BAN Blue Message
        40 : BAN Blue Message
         */
        switch (result) {
            case SUCCESS: {
                {
                    switch (ServerConfig.GetRegion()) {
                        case KMS:
                        case KMST: {
                            sp.Encode4(client.getAccID()); // m_dwAccountId
                            sp.Encode1(client.getGender()); // m_nGender
                            sp.Encode1(client.isGm() ? 1 : 0); // m_nGradeCode
                            if (ServerConfig.JMS164orLater()) {
                                sp.Encode1(client.isGm() ? 1 : 0);
                            }
                            if (ServerConfig.KMS160orLater()) {
                                sp.Encode4(3);
                                sp.Encode1(1);
                                sp.Encode1(0);
                                sp.Encode8(0);
                                sp.Encode1(0);
                                sp.EncodeStr(client.getAccountName());
                                sp.EncodeStr("");
                                sp.Encode1(0);
                                sp.Encode1(0);
                                sp.Encode1(0);
                                break;
                            }
                            sp.EncodeStr(client.getAccountName()); // m_sNexonClubID
                            sp.Encode4(3); // should be 3 for KMS v2.114 to ignore personal number
                            sp.Encode1(ServerConfig.IsKMS() ? 1 : 0); // should be 1 for KMS v2.114 to ignore personal number
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
                            sp.Encode4(client.getAccID()); // m_dwAccountId
                            sp.Encode1(client.getGender()); // m_nGender
                            sp.Encode1(client.isGm() ? 1 : 0); // m_nGradeCode
                            if (ServerConfig.JMS164orLater()) {
                                sp.Encode1(client.isGm() ? 1 : 0);
                            }
                            sp.EncodeStr(client.getAccountName()); // m_sNexonClubID
                            sp.EncodeStr(client.getAccountName());
                            sp.Encode1(ServerConfig.IsKMS() ? 1 : 0);
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
                            if (ServerConfig.IsPostBB()) {
                                // -1, 無視
                                // 0, 初期化
                                // 1, 登録済み
                                sp.Encode1(-1);
                            }
                            // 旧かんたん会員
                            if (ServerConfig.JMS302orLater()) {
                                // 0, 旧かんたん会員
                                // 1, 通常
                                sp.Encode1(1);
                            }
                            sp.Encode8(0); // m_dtChatUnblockDate
                            sp.EncodeStr(""); // v131: available name for new character, later version does not use this string
                            break;
                        }
                        case CMS: {
                            sp.Encode4(client.getAccID());
                            sp.Encode1(client.getGender());
                            sp.Encode1(client.isGm() ? 1 : 0);
                            sp.Encode1(client.isGm() ? 1 : 0);
                            sp.EncodeStr(client.getAccountName());
                            sp.Encode4(0);
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.Encode8(0); // buffer
                            sp.Encode1(0);
                            sp.Encode8(0); // buffer
                            sp.Encode8(0); // buffer
                            sp.EncodeStr("");
                            sp.Encode1(0);
                            sp.EncodeStr(String.valueOf(client.getAccID()));
                            sp.EncodeStr(client.getAccountName());
                            sp.Encode1(1);
                            break;
                        }
                        case THMS: {
                            sp.Encode4(client.getAccID());
                            sp.Encode1(client.getGender());
                            sp.Encode1(client.isGm() ? 1 : 0);
                            sp.Encode1(client.isGm() ? 1 : 0);
                            sp.EncodeStr(client.getAccountName());
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.Encode8(0);
                            sp.Encode1(0);
                            sp.EncodeStr("");
                            break;
                        }
                        case TWMS: {
                            sp.Encode4(client.getAccID());
                            sp.Encode1(client.getGender());
                            sp.Encode1(client.isGm() ? 1 : 0);
                            if (ServerConfig.TWMS94orLater()) {
                                sp.Encode1(client.isGm() ? 1 : 0);
                            }
                            if (ServerConfig.TWMS121orLater()) {
                                sp.Encode4(0); // buffer4
                            }
                            sp.EncodeStr(client.getAccountName());
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
                            sp.Encode4(client.getAccID());
                            sp.Encode1(client.getGender());
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.EncodeStr(client.getAccountName());
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.Encode8(0);
                            sp.EncodeStr("");
                            break;
                        }
                        case GMS: {
                            if (ServerConfig.IsPreBB()) {
                                sp.Encode4(client.getAccID()); // m_dwAccountId
                                sp.Encode1(0);
                                sp.Encode1(client.getGender()); // m_nGender
                                sp.Encode1(client.isGm() ? 1 : 0); // m_nGradeCode
                                if (ServerConfig.JMS164orLater()) {
                                    sp.Encode1(client.isGm() ? 1 : 0);
                                }
                                sp.EncodeStr(client.getAccountName()); // m_sNexonClubID
                                sp.Encode1(0); // m_nPurchaseExp
                                sp.Encode1(0); // m_nChatBlockReason
                                sp.Encode8(0); // m_dtChatUnblockDate
                                sp.Encode8(0); // m_dtRegisterDate
                                sp.Encode4(0);
                            } else {
                                // GMS v95
                                sp.Encode4(client.getAccID()); // m_dwAccountId
                                sp.Encode1(0);
                                sp.Encode1(0);
                                sp.Encode2(0);
                                sp.Encode1(0);
                                sp.EncodeStr(client.getAccountName());
                                sp.Encode1(0);
                                sp.Encode1(0);
                                sp.Encode8(0);
                                sp.Encode8(0);
                                sp.Encode4(0);
                                sp.Encode1(1); // pic
                                sp.Encode1(0);
                                sp.Encode8(0);
                            }
                            break;
                        }
                        case EMS: {
                            sp.Encode4(client.getAccID()); // m_dwAccountId
                            // EMS v55-v70
                            if (ServerConfig.IsPreBB()) {
                                sp.Encode1(0);
                            }
                            sp.Encode1(client.getGender()); // m_nGender
                            sp.Encode1(client.isGm() ? 1 : 0); // m_nGradeCode
                            if (ServerConfig.JMS164orLater()) {
                                sp.Encode1(client.isGm() ? 1 : 0);
                            }
                            sp.EncodeStr(client.getAccountName()); // m_sNexonClubID
                            sp.Encode1(0); // m_nPurchaseExp
                            sp.Encode1(0); // m_nChatBlockReason
                            sp.Encode8(0); // m_dtChatUnblockDate
                            if (ServerConfig.IsPreBB()) {
                                sp.Encode8(0); // m_dtRegisterDate
                                // v70+
                                sp.Encode1(1);
                            } else {
                                sp.EncodeStr("");
                                sp.Encode4(0);
                            }
                            break;
                        }
                        case BMS: {
                            sp.Encode1(0); // m_nRegStatID
                            sp.Encode4(0); // m_nUseDay
                            sp.Encode4(client.getAccID()); // m_dwAccountId
                            sp.Encode1(client.getGender()); // m_nGender
                            sp.Encode1(client.isGm() ? 1 : 0); // m_nGradeCode
                            sp.Encode1(client.isGm() ? 1 : 0);
                            sp.EncodeStr(client.getAccountName()); // m_sNexonClubID
                            sp.Encode1(0); // m_nPurchaseExp
                            sp.Encode1(0); // m_nChatBlockReason
                            sp.Encode8(0); // m_dtChatUnblockDate
                            sp.Encode8(0); // m_dtRegisterDate
                            sp.Encode1(0);
                            sp.Encode1(2); // pic
                            break;
                        }
                        case VMS: {
                            sp.Encode4(client.getAccID());
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.EncodeStr(client.getAccountName());
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.Encode8(0);
                            sp.EncodeStr("");
                            break;
                        }
                        case IMS: {
                            sp.Encode4(client.getAccID());
                            sp.Encode1(client.getGender());
                            sp.Encode1(0);
                            sp.Encode1(0);
                            sp.EncodeStr(client.getAccountName());
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
            case BLOCKED_MAPLEID_WITH_MESSAGE: {
                sp.Encode1(32); // 0x20 and 0x40 are blue message flag
                break;
            }
            default: {
                sp.Encode1(0); // no blue message
                if (ServerConfig.IsBMS()) {
                    sp.Encode4(0);
                }
                break;
            }
        }
        return sp.get();
    }

    public static final MaplePacket addNewCharEntry(final MapleCharacter chr, final boolean worked) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CreateNewCharacterResult);
        sp.Encode1(worked ? 0 : 1);
        if (worked) {
            sp.EncodeBuffer(GW_CharacterStat.Encode(chr));
            sp.EncodeBuffer(AvatarLook.Encode(chr));
            // KMS92 no data
            if (ServerConfig.IsKMS()) {
                sp.Encode1(0);
                sp.Encode1(0);
                sp.EncodeZeroBytes(16);
            }
        }

        if (ServerConfig.IsKMS()) {
            sp.Encode1(0);
            sp.Encode1(0);
            sp.Encode4(0);
        }

        return sp.get();
    }

    // not tested
    public static final MaplePacket secondPwError(final byte mode) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CheckPinCodeResult);
        /*
        14 : Invalid password
        15 : Second password is incorrect
         */
        sp.Encode1(mode);
        return sp.get();
    }

    // ワールドセレクト
    public static final MaplePacket getServerList(final int serverId) {
        return getServerList(serverId, true, 0);
    }

    // ワールドセレクト
    public static final MaplePacket getServerList(final int serverId, boolean internalserver, int externalch) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_WorldInformation);
        // ワールドID
        sp.Encode1(serverId);
        // ワールド名
        sp.EncodeStr(LoginServer.WorldName[serverId]);
        // ワールドの旗
        sp.Encode1(LoginServer.WorldFlag[serverId]);
        // 吹き出し
        sp.EncodeStr(ServerConfig.IsBMS() ? "" : LoginServer.WorldEvent[serverId]);
        // 経験値倍率?
        sp.Encode2(100);
        // ドロップ倍率?
        sp.Encode2(100);

        if (ServerConfig.IsBMS() || ServerConfig.IsGMS()) {
            sp.Encode1(0);
        }

        // チャンネル数
        sp.Encode1(internalserver ? ChannelServer.getChannels() : externalch);
        if (ServerConfig.IsCMS()) {
            sp.Encode4(500); // 0 causes 0 div
        }
        // チャンネル情報
        for (int i = 0; i < (internalserver ? ChannelServer.getChannels() : externalch); i++) {
            // チャンネル名
            sp.EncodeStr(LoginServer.WorldName[serverId] + "-" + (i + 1));
            // 接続人数表示
            if (internalserver && ChannelServer.getPopulation(i + 1) < 5) {
                sp.Encode4(ChannelServer.getPopulation(i + 1) * 200);
            } else {
                sp.Encode4(1000);
            }
            // ワールドID
            sp.Encode1(serverId);
            sp.Encode1(i); // channel
            sp.Encode1(0);
            if (ServerConfig.JMS302orLater()) {
                sp.Encode1(0);
            }
        }
        sp.Encode2(0);
        if (ServerConfig.KMS118orLater() || ServerConfig.JMS302orLater() || ServerConfig.JMST110()) {
            sp.Encode4(0);
        }
        return sp.get();
    }

    public static byte[] CharList_TWMS(MapleClient c) {
        ServerPacket data = new ServerPacket();
        data.Encode4(1000000);
        List<MapleCharacter> chars = c.loadCharacters(c.getWorld());
        int charslots = c.getCharacterSlots();
        data.Encode1(chars.size());
        for (MapleCharacter chr : chars) {
            data.EncodeBuffer(GW_CharacterStat.Encode(chr));
            data.EncodeBuffer(AvatarLook.Encode(chr));
            if (ServerConfig.TWMS121orLater()) {
                data.Encode1(0);
            }
            data.Encode1(1);
            data.Encode4(chr.getRank());
            data.Encode4(chr.getRankMove());
            data.Encode4(chr.getJobRank());
            data.Encode4(chr.getJobRankMove());
        }

        if (ServerConfig.TWMS121orLater()) {
            data.Encode2(3); // 2nd password state
            data.Encode8(charslots);
            data.Encode8(0);
        } else {
            // TWMS v94
            data.Encode1(3);
            data.Encode1(0);
            data.Encode4(charslots);
        }
        return data.get().getBytes();
    }

    // キャラクターセレクト
    // getCharList
    // public static final MaplePacket getCharList(final boolean secondpw, final List<MapleCharacter> chars, int charslots) {
    public static final MaplePacket getCharList(MapleClient c, LoginResult result) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SelectWorldResult);
        sp.Encode1(result.Get());
        if (result != LoginResult.SUCCESS) {
            // error
            return sp.get();
        }
        if (ServerConfig.IsTWMS()) {
            sp.EncodeBuffer(CharList_TWMS(c));
            return sp.get();
        }
        if (ServerConfig.IsCMS()) {
            sp.EncodeBuffer(CharList_CMS(c));
            return sp.get();
        }
        List<MapleCharacter> chars = c.loadCharacters(c.getWorld());
        int charslots = c.getCharacterSlots();

        if (ServerConfig.IsJMS()) {
            sp.EncodeStr("");
        }

        if (ServerConfig.KMS160orLater()) {
            sp.Encode1(0);
        }

        if ((ServerConfig.IsKMS() && !ServerConfig.KMS160orLater()) || ServerConfig.IsCMS() || ServerConfig.IsIMS()) {
            sp.Encode4(1000000);
        }

        // キャラクターの数
        sp.Encode1(chars.size());
        for (MapleCharacter chr : chars) {
            //Structure.CharEntry(p, chr, true, false);
            sp.EncodeBuffer(GW_CharacterStat.Encode(chr));
            sp.EncodeBuffer(AvatarLook.Encode(chr));
            if ((ServerConfig.IsJMS() || ServerConfig.IsKMS() || ServerConfig.IsIMS() || ServerConfig.IsEMS() || ServerConfig.IsTHMS() || ServerConfig.IsMSEA() || ServerConfig.GMS95orLater())
                    && (ServerConfig.JMS180orLater() || ServerConfig.KMS84orLater())) {
                sp.Encode1(0); // family
            }
            sp.Encode1(1); // ranking
            sp.Encode4(chr.getRank()); // all world ranking
            sp.Encode4(chr.getRankMove());
            sp.Encode4(chr.getJobRank()); // world ranking
            sp.Encode4(chr.getJobRankMove());
        }

        if (ServerConfig.KMS160orLater() || ServerConfig.JMS302orLater()) {
            sp.Encode1(2); // 2次パス無視
            sp.Encode4(charslots);
            sp.Encode4(0);
            sp.Encode4(0);
            sp.Encode4(0);
            return sp.get();
        }

        if (ServerConfig.IsBMS()) {
            sp.Encode4(charslots);
            return sp.get();
        }

        if (ServerConfig.IsMSEA() || ServerConfig.IsIMS()) {
            sp.Encode1(2);
            sp.Encode1(0);
            sp.Encode4(charslots);
            sp.Encode4(0);
            return sp.get();
        }

        if (ServerConfig.IsTHMS()) {
            sp.Encode1(2); // 2nd password ingored
            sp.Encode1(0);
            sp.Encode4(charslots);
            sp.Encode4(0);
            sp.Encode8(0);
            return sp.get();
        }

        if ((ServerConfig.JMS146or147()) || ServerConfig.IsVMS()) {
            sp.Encode1(2); // 2次パス無視
            sp.Encode1(0);
            sp.Encode4(charslots); // m_nSlotCount
            return sp.get();
        }

        if (ServerConfig.GMS95orLater()) {
            sp.Encode1(2); // m_bLoginOpt
            sp.Encode4(charslots); // m_nSlotCount
            sp.Encode4(0); // m_nBuyCharCount
            return sp.get();
        }

        // EMS v55
        if ((ServerConfig.IsEMS() && ServerConfig.GetVersion() <= 55)
                || (ServerConfig.IsGMS() && ServerConfig.GetVersion() <= 73)) {
            sp.Encode4(charslots); // m_nSlotCount
            return sp.get();
        }
        if (ServerConfig.IsEMS() && ServerConfig.GetVersion() <= 70) {
            sp.Encode4(charslots); // m_nSlotCount
            sp.Encode4(0);
            sp.Encode8(0);
            return sp.get();
        }

        if (ServerConfig.IsKMS() || ServerConfig.IsEMS()) {
            sp.Encode1(2);
            sp.Encode1(0);
            sp.Encode4(charslots); // m_nSlotCount
            if (ServerConfig.IsPostBB()) {
                sp.Encode4(0); // m_nBuyCharCount
            }
            if (ServerConfig.IsEMS()) {
                sp.Encode8(0);
            }
            return sp.get();
        }
        // BIGBANG
        if (ServerConfig.IsJMS()
                && ServerConfig.GetVersion() == 187) {
            sp.Encode1(2); // 2次パス無視
            sp.Encode1(0);
            sp.Encode4(charslots);
            sp.Encode4(0); // Character Cards
            return sp.get();
        }
        if (ServerConfig.JMS131orEarlier()) {
            sp.Encode1(3); // charslots
            sp.Encode1(0);
            return sp.get();
        }
        // 2次パスワードの利用状態
        if (ServerConfig.IsPostBB()) {
            sp.Encode1(0);
        } else {
            sp.Encode2(2);
        }

        if (ServerConfig.JMS194orLater()) {
            sp.Encode4(charslots);
            sp.Encode4(0); // Character Card
            sp.Encode4(0); // idk
            return sp.get();
        }

        if (ServerConfig.IsJMS()
                && ServerConfig.GetVersion() <= 176) {
            sp.Encode4(charslots);
        } else {
            sp.Encode8(charslots);
        }
        return sp.get();
    }

    // キャラクター削除
    public static final MaplePacket deleteCharResponse(final int cid, final int state) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_DeleteCharacterResult);
        sp.Encode4(cid);
        sp.Encode1(state);
        return sp.get();
    }

    // CLogin::OnLatestConnectedWorld
    public static MaplePacket LatestConnectedWorld() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_LatestConnectedWorld);
        sp.Encode4(0); // World ID
        return sp.get();
    }

    // CLogin::OnViewAllCharResult
    public static MaplePacket ViewAllCharResult_v201(MapleClient c) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ViewAllCharResult);
        List<MapleCharacter> chars = c.loadCharacters(0);
        sp.Encode1(3); // error code
        sp.Encode1(0);
        sp.Encode1(0);
        return sp.get();
    }

    // ワールドセレクト
    public static final MaplePacket getEndOfServerList() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_WorldInformation);
        sp.Encode1(-1);
        if (ServerConfig.KMS148orLater()) {
            sp.Encode1(0);
        }
        return sp.get();
    }

    public static byte[] CharList_CMS(MapleClient c) {
        ServerPacket data = new ServerPacket();
        data.Encode4(1000000);
        List<MapleCharacter> chars = c.loadCharacters(c.getWorld());
        int charslots = c.getCharacterSlots();
        data.Encode1(chars.size());
        for (MapleCharacter chr : chars) {
            data.EncodeBuffer(GW_CharacterStat.Encode(chr));
            data.EncodeBuffer(AvatarLook.Encode(chr));
            data.Encode1(0);
        }
        data.Encode1(3);
        data.Encode1(0);
        data.Encode4(charslots);
        data.Encode4(0); // card
        return data.get().getBytes();
    }

    public static final MaplePacket getServerStatus(final int status) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CheckUserLimitResult);
        /*
        0 : Normal
        1 : Highly populated
        2 : Full
         */
        sp.Encode2(status);
        return sp.get();
    }

    public static final MaplePacket charNameResponse(final String charname, final boolean nameUsed) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CheckDuplicatedIDResult);
        sp.EncodeStr(charname);
        sp.Encode1(nameUsed ? 1 : 0);
        return sp.get();
    }

    // いらない機能
    public static final MaplePacket SetMapLogin() {
        // JMS v186.1には3つのログイン画面が存在するのでランダムに割り振ってみる
        String[] LoginScreen = {"MapLogin", "MapLogin1", "MapLogin2"};
        if (!(ServerConfig.IsJMS() && ServerConfig.GetVersion() == 186)) {
            return SetMapLogin(LoginScreen[0]);
        }
        return SetMapLogin(LoginScreen[(new Random().nextInt(3))]);
    }

    // ログイン画面へ切り替え
    public static final MaplePacket SetMapLogin(String LoginScreen) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_SetMapLogin);
        // ログイン画面の名称
        sp.EncodeStr(LoginScreen);
        if (ServerConfig.IsPostBB()) {
            sp.Encode4(0);
        }
        return sp.get();
    }

}
