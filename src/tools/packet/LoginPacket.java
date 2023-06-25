/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tools.packet;

import java.util.List;
import client.MapleClient;
import client.MapleCharacter;
import config.ServerConfig;
import handling.MaplePacket;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import java.util.Random;
import packet.ServerPacket;
import packet.ClientPacket;
import packet.Structure;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.HexTool;

public class LoginPacket {

    // サーバーのバージョン情報
    public static final MaplePacket getHello(final byte[] sendIv, final byte[] recvIv) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.HELLO); // dummy header

        if (ServerConfig.version < 414) {
            p.Encode2(ServerConfig.version);
            p.EncodeStr(String.valueOf(ServerConfig.version_sub));
            p.EncodeBuffer(recvIv);
            p.EncodeBuffer(sendIv);
            p.Encode1(3); // JMS
        } else {
            // x64
            p.Encode2(ServerConfig.version);
            p.EncodeStr("1:" + ServerConfig.version_sub); // 1:1 ?
            p.EncodeBuffer(recvIv);
            p.EncodeBuffer(sendIv);
            p.Encode1(3); // JMS
            p.Encode1(0);
            p.Encode1(5);
            p.Encode1(1);
        }

        // ヘッダにサイズを書き込む
        p.SetHello();
        return p.Get();
    }

    // いらない機能
    public static final MaplePacket LoginAUTH(ClientPacket p, MapleClient c) {
        // JMS v186.1には3つのログイン画面が存在するのでランダムに割り振ってみる
        String LoginScreen[] = {"MapLogin", "MapLogin1", "MapLogin2"};
        if (ServerConfig.version != 186) {
            return LoginAUTH(LoginScreen[0]);
        }
        return LoginAUTH(LoginScreen[(new Random().nextInt(3))]);
    }

    // ログイン画面へ切り替え
    public static final MaplePacket LoginAUTH(String LoginScreen) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LOGIN_AUTH);

        // ログイン画面の名称
        p.EncodeStr(LoginScreen);

        if (ServerConfig.version >= 187) {
            p.Encode4(0);
        }

        return p.Get();
    }

    // ログイン成功
    public static final MaplePacket getAuthSuccessRequest(final MapleClient client) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_CheckPasswordResult);

        p.Encode1(0);
        p.Encode1(0);
        p.Encode4(client.getAccID());
        // 性別
        p.Encode1(client.getGender());
        p.Encode1(client.isGm() ? 1 : 0);

        if (ServerConfig.version >= 164) {
            p.Encode1(client.isGm() ? 1 : 0);
        }

        p.EncodeStr(client.getAccountName());
        p.EncodeStr(client.getAccountName());
        p.Encode1(0);
        p.Encode1(0);
        p.Encode1(0);
        p.Encode1(0);

        if (ServerConfig.version >= 164) {
            p.Encode1(0);
        }

        if (ServerConfig.version > 165) {
            p.Encode1(0);
        }

        // 2次パスワード
        if (ServerConfig.version >= 188) {
            // -1, 無視
            // 0, 初期化
            // 1, 登録済み
            p.Encode1(-1);
        }

        // 旧かんたん会員
        if (ServerConfig.version >= 302) {
            // 0, 旧かんたん会員
            // 1, 通常
            p.Encode1(1);
        }

        p.Encode8(0); // buf
        p.EncodeStr(client.getAccountName());

        return p.Get();
    }

    // ログイン失敗
    public static final MaplePacket getLoginFailed(final int reason) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_CheckPasswordResult);

        // 理由
        p.Encode1(reason);
        p.Encode1(0);

        return p.Get();
    }

    // ワールドセレクト
    public static final MaplePacket getServerList(final int serverId) {
        return getServerList(serverId, true, 0);
    }

    // ワールドセレクト
    public static final MaplePacket getServerList(final int serverId, boolean internalserver, int externalch) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_WorldInformation);
        // ワールドID
        p.Encode1(serverId);
        // ワールド名
        p.EncodeStr(LoginServer.WorldName[serverId]);
        // ワールドの旗
        p.Encode1(LoginServer.WorldFlag[serverId]);
        // 吹き出し
        p.EncodeStr(LoginServer.WorldEvent[serverId]);
        // 経験値倍率?
        p.Encode2(100);
        // ドロップ倍率?
        p.Encode2(100);
        // チャンネル数
        p.Encode1(internalserver ? ChannelServer.getChannels() : externalch);
        // チャンネル情報
        for (int i = 0; i < (internalserver ? ChannelServer.getChannels() : externalch); i++) {
            // チャンネル名
            p.EncodeStr(LoginServer.WorldName[serverId] + "-" + (i + 1));
            // 接続人数表示
            if (internalserver && ChannelServer.getPopulation(i + 1) < 5) {
                p.Encode4(ChannelServer.getPopulation(i + 1) * 200);
            } else {
                p.Encode4(1000);
            }

            // ワールドID
            p.Encode1(serverId);
            if (ServerConfig.version < 302) {
                // チャンネルID
                p.Encode2(i);
            } else {
                p.Encode1(i);
                p.Encode1(0);
                p.Encode1(0);
            }
        }

        p.Encode2(0);

        if (ServerConfig.version >= 302) {
            p.Encode4(0);
        }

        return p.Get();
    }

    // ワールドセレクト
    public static final MaplePacket getEndOfServerList() {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_WorldInformation);
        p.Encode1(0xFF);
        return p.Get();
    }

    // キャラクターセレクト
    public static final MaplePacket getCharList(final boolean secondpw, final List<MapleCharacter> chars, int charslots) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_SelectWorldResult);
        p.Encode1(0);
        p.EncodeStr("");
        // キャラクターの数

        p.Encode1(chars.size());

        for (MapleCharacter chr : chars) {
            Structure.CharEntry(p, chr, false);
        }

        if (ServerConfig.version <= 131) {
            p.Encode1(3); // charslots
            p.Encode1(0);
            return p.Get();
        }

        // 2次パスワードの利用状態
        if (ServerConfig.version <= 186) {
            p.Encode2(2);
        } else {
            p.Encode1(0);
        }

        if (ServerConfig.version >= 302) {
            p.Encode4(0);
            p.Encode4(0);
            p.Encode4(0);
            p.Encode4(charslots);
        } else if (ServerConfig.version >= 190) {
            p.Encode4(0);
            p.Encode4(0);
            p.Encode4(charslots);
        } else if (ServerConfig.version <= 176) {
            p.Encode4(charslots);
        } else {
            p.Encode8(charslots);
        }

        return p.Get();
    }

    // キャラクター削除
    public static final MaplePacket deleteCharResponse(final int cid, final int state) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_DeleteCharacterResult);
        p.Encode4(cid);
        p.Encode1(state);
        return p.Get();
    }

    public static final MaplePacket getPing() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        mplew.writeShort(ServerPacket.Header.LP_AliveReq.Get());

        return mplew.getPacket();
    }

    public static final MaplePacket getPermBan(final byte reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        mplew.writeShort(ServerPacket.Header.LP_CheckPasswordResult.Get());
        mplew.writeShort(2); // Account is banned
        mplew.writeInt(0);
        mplew.writeShort(reason);
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01 01 00"));

        return mplew.getPacket();
    }

    public static final MaplePacket getTempBan(final long timestampTill, final byte reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(17);

        mplew.writeShort(ServerPacket.Header.LP_CheckPasswordResult.Get());
        mplew.write(2);
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00"));
        mplew.write(reason);
        mplew.writeLong(timestampTill); // Tempban date is handled as a 64-bit long, number of 100NS intervals since 1/1/1601. Lulz.

        return mplew.getPacket();
    }

    public static final MaplePacket secondPwError(final byte mode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);

        /*
         * 14 - Invalid password
         * 15 - Second password is incorrect
         */
        mplew.writeShort(ServerPacket.Header.LP_CheckPinCodeResult.Get());
        mplew.write(mode);

        return mplew.getPacket();
    }

    // 必要なさそう
    public static final MaplePacket getServerStatus(final int status) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        /*	 * 0 - Normal
         * 1 - Highly populated
         * 2 - Full*/
        mplew.writeShort(ServerPacket.Header.SERVERSTATUS.Get());
        mplew.writeShort(status);

        return mplew.getPacket();
    }

    public static final MaplePacket addNewCharEntry(final MapleCharacter chr, final boolean worked) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_CreateNewCharacterResult);
        p.Encode1(worked ? 0 : 1);
        Structure.CharStats(p, chr);
        Structure.CharLook(p, chr, true);
        return p.Get();
    }

    public static final MaplePacket charNameResponse(final String charname, final boolean nameUsed) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_CheckDuplicatedIDResult.Get());
        mplew.writeMapleAsciiString(charname);
        mplew.write(nameUsed ? 1 : 0);

        return mplew.getPacket();
    }

    // ログインボタンを有効化するために必要
    public static MaplePacket CheckGameGuardUpdate() {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_T_UpdateGameGuard);

        // 0 = Update Game Guard
        // 1 = Enable Login Button
        p.Encode1(1);

        return p.Get();
    }
}
