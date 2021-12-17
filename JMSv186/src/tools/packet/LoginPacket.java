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
import client.inventory.IItem;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import handling.MaplePacket;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import packet.InPacket;
import packet.OutPacket;
import server.Start;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.HexTool;

public class LoginPacket {

    // サーバーのバージョン情報
    public static final MaplePacket getHello(final byte[] sendIv, final byte[] recvIv) {
        InPacket p = new InPacket(InPacket.Header.HELLO);
        p.Encode2(Start.getMainVersion());
        p.EncodeStr(String.valueOf(Start.getSubVersion()));
        p.EncodeBuffer(recvIv);
        p.EncodeBuffer(sendIv);
        p.Encode1(3);
        return p.Get();
    }

    // いらない機能
    public static final MaplePacket LoginAUTH(OutPacket p, MapleClient c) {
        // JMS v186.1には3つのログイン画面が存在するのでランダムに割り振ってみる
        String LoginScreen[] = {"MapLogin", "MapLogin1", "MapLogin2"};
        if (Start.getMainVersion() != 186) {
            return LoginAUTH(LoginScreen[0]);
        }
        return LoginAUTH(LoginScreen[(new Random().nextInt(3))]);
    }

    // ログイン画面へ切り替え
    public static final MaplePacket LoginAUTH(String LoginScreen) {
        InPacket p = new InPacket(InPacket.Header.LOGIN_AUTH);

        // ログイン画面の名称
        p.EncodeStr(LoginScreen);

        if (Start.getMainVersion() >= 187) {
            p.Encode4(0);
        }

        return p.Get();
    }

    // ログイン成功
    public static final MaplePacket getAuthSuccessRequest(final MapleClient client) {
        InPacket p = new InPacket(InPacket.Header.LOGIN_STATUS);

        p.Encode1(0);
        p.Encode1(0);
        p.Encode4(client.getAccID());
        // 性別
        p.Encode1(client.getGender());
        p.Encode1(client.isGm() ? 1 : 0);
        p.Encode1(client.isGm() ? 1 : 0);
        p.EncodeStr(client.getAccountName());
        p.EncodeStr(client.getAccountName());
        p.Encode1(0);
        p.Encode1(0);
        p.Encode1(0);
        p.Encode1(0);
        p.Encode1(0);

        if (Start.getMainVersion() > 164) {
            p.Encode1(0);
        }

        p.Encode8(0); // buf
        p.EncodeStr(client.getAccountName());

        return p.Get();
    }

    // ログイン失敗
    public static final MaplePacket getLoginFailed(final int reason) {
        InPacket p = new InPacket(InPacket.Header.LOGIN_STATUS);

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
        InPacket p = new InPacket(InPacket.Header.SERVERLIST);
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
            // チャンネルID
            p.Encode2(i);
        }
        p.Encode2(0);

        return p.Get();
    }

    // ワールドセレクト
    public static final MaplePacket getEndOfServerList() {
        InPacket p = new InPacket(InPacket.Header.SERVERLIST);
        p.Encode1(0xFF);
        return p.Get();
    }

    // キャラクターセレクト
    public static final MaplePacket getCharList(final boolean secondpw, final List<MapleCharacter> chars, int charslots) {
        InPacket p = new InPacket(InPacket.Header.CHARLIST);
        p.Encode1(0);
        p.EncodeStr("");
        // キャラクターの数
        p.Encode1(chars.size());

        // [addCharEntry]
        for (MapleCharacter chr : chars) {
            // [addCharStats]
            // キャラクターID
            p.Encode4(chr.getId());
            // キャラクター名
            p.EncodeBuffer(chr.getName(), 13);
            // 性別
            p.Encode1(chr.getGender());
            // 肌の色
            p.Encode1(chr.getSkinColor());
            // 顔
            p.Encode4(chr.getFace());
            // 髪型
            p.Encode4(chr.getHair());
            p.EncodeZeroBytes(24);
            // レベル
            p.Encode1(chr.getLevel());
            // 職業ID
            p.Encode2(chr.getJob());
            // [connectData]
            // STR
            p.Encode2(chr.getStat().str);
            // DEX
            p.Encode2(chr.getStat().dex);
            // INT
            p.Encode2(chr.getStat().int_);
            // LUK
            p.Encode2(chr.getStat().luk);
            // HP, MP
            if (Start.getMainVersion() <= 186) {
                // BB前
                p.Encode2(chr.getStat().hp);
                p.Encode2(chr.getStat().maxhp);
                p.Encode2(chr.getStat().mp);
                p.Encode2(chr.getStat().maxmp);
            } else {
                // BB後
                p.Encode4(chr.getStat().hp);
                p.Encode4(chr.getStat().maxhp);
                p.Encode4(chr.getStat().mp);
                p.Encode4(chr.getStat().maxmp);
            }
            // SP情報
            p.Encode2(chr.getRemainingAp());
            if (GameConstants.isEvan(chr.getJob()) || GameConstants.isResist(chr.getJob())) {
                p.Encode1(chr.getRemainingSpSize());
                for (int i = 0; i < chr.getRemainingSps().length; i++) {
                    if (chr.getRemainingSp(i) > 0) {
                        p.Encode1(i + 1);
                        p.Encode1(chr.getRemainingSp(i));
                    }
                }
            } else {
                p.Encode2(chr.getRemainingSp());
            }
            // 経験値
            p.Encode4(chr.getExp());
            // 人気度
            p.Encode2(chr.getFame());
            // Gachapon exp?
            p.Encode4(0);
            // マップID
            p.Encode4(chr.getMapId());
            // マップ入場位置
            p.Encode1(chr.getInitialSpawnpoint());
            if (Start.getMainVersion() > 176) {
                // デュアルブレイドフラグ
                p.Encode2(chr.getSubcategory());
                p.EncodeZeroBytes(20);
            } else {
                p.EncodeZeroBytes(16);
            }
            // [addCharLook]
            // 性別
            p.Encode1(chr.getGender());
            // 肌の色
            p.Encode1(chr.getSkinColor());
            // 顔
            p.Encode4(chr.getFace());
            // ?
            p.Encode1(1);
            // 髪型
            p.Encode4(chr.getHair());

            final Map<Byte, Integer> myEquip = new LinkedHashMap<>();
            final Map<Byte, Integer> maskedEquip = new LinkedHashMap<>();
            MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);

            for (final IItem item : equip.list()) {
                if (item.getPosition() < -128) { //not visible
                    continue;
                }
                byte pos = (byte) (item.getPosition() * -1);

                if (pos < 100 && myEquip.get(pos) == null) {
                    myEquip.put(pos, item.getItemId());
                } else if ((pos > 100 || pos == -128) && pos != 111) {
                    pos = (byte) (pos == -128 ? 28 : pos - 100);
                    if (myEquip.get(pos) != null) {
                        maskedEquip.put(pos, myEquip.get(pos));
                    }
                    myEquip.put(pos, item.getItemId());
                } else if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, item.getItemId());
                }
            }
            for (final Map.Entry<Byte, Integer> entry : myEquip.entrySet()) {
                p.Encode1(entry.getKey());
                p.Encode4(entry.getValue());
            }
            p.Encode1(0xFF); // end of visible itens
            // masked itens
            for (final Map.Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
                p.Encode1(entry.getKey());
                p.Encode4(entry.getValue());
            }
            p.Encode1(0xFF); // ending markers

            final IItem cWeapon = equip.getItem((byte) -111);
            p.Encode4(cWeapon != null ? cWeapon.getItemId() : 0);
            p.Encode4(0);
            p.Encode8(0);

            // [ランキング情報]
            if (Start.getMainVersion() > 176) {
                p.Encode1(0);
            }
            // ランキングフラグ
            p.Encode1(0);
            // ランキング情報が存在する場合は追記
        }

        // 2次パスワードの利用状態
        p.Encode2(2);

        // キャラクタースロットの数
        if (Start.getMainVersion() <= 176) {
            p.Encode4(charslots);
        } else {
            p.Encode8(charslots); // buffer
        }

        return p.Get();
    }

    // キャラクター削除
    public static final MaplePacket deleteCharResponse(final int cid, final int state) {
        InPacket p = new InPacket(InPacket.Header.DELETE_CHAR_RESPONSE);
        p.Encode4(cid);
        p.Encode1(state);
        return p.Get();
    }

    public static final MaplePacket getPing() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        mplew.writeShort(InPacket.Header.PING.Get());

        return mplew.getPacket();
    }

    public static final MaplePacket getPermBan(final byte reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        mplew.writeShort(InPacket.Header.LOGIN_STATUS.Get());
        mplew.writeShort(2); // Account is banned
        mplew.writeInt(0);
        mplew.writeShort(reason);
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01 01 00"));

        return mplew.getPacket();
    }

    public static final MaplePacket getTempBan(final long timestampTill, final byte reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(17);

        mplew.writeShort(InPacket.Header.LOGIN_STATUS.Get());
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
        mplew.writeShort(InPacket.Header.SECONDPW_ERROR.Get());
        mplew.write(mode);

        return mplew.getPacket();
    }

    // 必要なさそう
    public static final MaplePacket getServerStatus(final int status) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        /*	 * 0 - Normal
         * 1 - Highly populated
         * 2 - Full*/
        mplew.writeShort(InPacket.Header.SERVERSTATUS.Get());
        mplew.writeShort(status);

        return mplew.getPacket();
    }

    public static final MaplePacket addNewCharEntry(final MapleCharacter chr, final boolean worked) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(InPacket.Header.ADD_NEW_CHAR_ENTRY.Get());
        mplew.write(worked ? 0 : 1);
        PacketHelper.addCharStats(mplew, chr);
        PacketHelper.addCharLook(mplew, chr, true);
        return mplew.getPacket();
    }

    public static final MaplePacket charNameResponse(final String charname, final boolean nameUsed) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(InPacket.Header.CHAR_NAME_RESPONSE.Get());
        mplew.writeMapleAsciiString(charname);
        mplew.write(nameUsed ? 1 : 0);

        return mplew.getPacket();
    }

    private static final void addCharEntry(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr, boolean ranking) {
        PacketHelper.addCharStats(mplew, chr);
        PacketHelper.addCharLook(mplew, chr, true);
        if (Start.getMainVersion() > 176) {
            mplew.write(0);
        }
        mplew.write(ranking ? 1 : 0);
        if (ranking) {
            mplew.writeInt(chr.getRank());
            mplew.writeInt(chr.getRankMove());
            mplew.writeInt(chr.getJobRank());
            mplew.writeInt(chr.getJobRankMove());
        }
    }
}
