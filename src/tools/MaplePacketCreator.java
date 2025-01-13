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
package tools;

import client.inventory.IItem;
import client.MapleCharacter;
import handling.MaplePacket;
import constants.ServerConstants;
import server.maps.MapleMap;
import server.life.MapleMonster;
import tools.data.output.MaplePacketLittleEndianWriter;
import packet.ServerPacket;
import packet.response.struct.TestHelper;
import server.maps.MapleNodes.MapleNodeInfo;
import server.maps.MapleNodes.MaplePlatform;

public class MaplePacketCreator {

    public static MaplePacket getRelogResponse() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);

        mplew.writeShort(ServerPacket.Header.RELOG_RESPONSE.Get());
        mplew.write(1);

        return mplew.getPacket();
    }

    /**
     * Possible values for <code>type</code>:<br>
     * 1: You cannot move that channel. Please try again later.<br>
     * 2: You cannot go into the cash shop. Please try again later.<br>
     * 3: The Item-Trading shop is currently unavailable, please try again
     * later.<br>
     * 4: You cannot go into the trade shop, due to the limitation of user
     * count.<br>
     * 5: You do not meet the minimum level requirement to access the Trade
     * Shop.<br>
     *
     * @param type The type
     * @return The "block" packet.
     */
    public static MaplePacket serverBlocked(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_TransferChannelReqIgnored.Get());
        mplew.write(type);

        return mplew.getPacket();
    }

    public static MaplePacket serverMessage(String message) {
        return serverMessage(4, 0, message, false);
    }

    public static MaplePacket serverNotice(int type, String message) {
        return serverMessage(type, 0, message, false);
    }

    public static MaplePacket serverNotice(int type, int channel, String message) {
        return serverMessage(type, channel, message, false);
    }

    public static MaplePacket serverNotice(int type, int channel, String message, boolean smegaEar) {
        return serverMessage(type, channel, message, smegaEar);
    }

    private static MaplePacket serverMessage(int type, int channel, String message, boolean megaEar) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        /*
        0x00    [告知事項]青文字
        0x01    ダイアログ
        0x02    メガホン
        0x03    拡声器
        0x04    画面上部のメッセージ
        0x05    ピンク文字
        0x06    青文字
        0x07    ??? 直接関数ポインタへ処理が移る 0x00B93F3F[0x07] = 00B93E27
        0x08    アイテム拡声器
        0x09    ワールド拡声器 (テスト)
        0x0A    三連拡声器
        0x0B    不明 0x00B93F3F[0x0B] = 00B93ECA
        0x0C    ハート拡声器
        0x0D    ドクロ拡声器
        0x0E    ガシャポン 0x00B93F3F[0x0E] = 00B93779
        0x0F    青文字 名前:アイテム名(xxxx個))
        0x10    体験用アバター獲得 0x00B93F3F[0x10] = 00B93950
        0x11    青文字 アイテム表示 0x00B93F3F[0x11] = 00B93DA1
         */
        mplew.writeShort(ServerPacket.Header.LP_BroadcastMsg.Get());
        mplew.write(type);

        if (type == 4) {
            mplew.write(1);
        }

        // 0x10 = 名前
        mplew.writeMapleAsciiString(message);

        switch (type) {
            case 0x0A:
                mplew.write(0x03);
                mplew.writeMapleAsciiString(message);
                mplew.writeMapleAsciiString(message);
                mplew.write(channel - 1);
                mplew.write(megaEar ? 1 : 0);
                break;
            // 拡声器, ハート拡声器, ドクロ拡声器
            case 0x03:
            case 0x0C:
            case 0x0D:
                mplew.write(channel - 1); // channel
                mplew.write(megaEar ? 1 : 0);
                break;
            case 0x06:
                mplew.writeInt(channel >= 1000000 && channel < 6000000 ? channel : 0); //cash itemID, displayed in yellow by the {name}
                break;
            case 0x07:
                mplew.writeInt(0);
                break;
            // ワールド拡声器
            case 0x09:
                // ワールド番号
                mplew.write(0);
                break;
            // 不明
            case 0x0B:
                mplew.writeInt(0); // 不明
                break;
            // アイテム情報 個数付き
            case 0x0F:
                mplew.writeInt(0); // 不明
                mplew.writeInt(1472117); // アイテムID
                mplew.writeInt(256); // 個数
                break;
            // お勧め体験用アバター
            case 0x10:
                // 必要なメッセージはキャラ名のみとなる
                break;
            // アイテム情報
            case 0x11:
                mplew.writeInt(1472117); // アイテムID
                break;
        }
        return mplew.getPacket();
    }

    public static MaplePacket getGachaponMega(final String name, final String message, final IItem item, final byte rareness) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_BroadcastMsg.Get());
        //mplew.write(rareness == 2 ? 15 : 14);
        mplew.write(0x0E);
        mplew.writeMapleAsciiString(name + " : " + message);
        mplew.writeInt(0x01010000);
        //mplew.writeMapleAsciiString(name);
        TestHelper.addItemInfo(mplew, item, true, true);
        mplew.writeZeroBytes(10);
        return mplew.getPacket();
    }

    public static MaplePacket getAvatarMega(MapleCharacter chr, int channel, int itemId, String message, boolean ear) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AvatarMegaphoneRes.Get());
        mplew.writeInt(itemId);
        mplew.writeMapleAsciiString(chr.getName());
        mplew.writeMapleAsciiString(message);
        mplew.writeInt(channel - 1); // channel
        mplew.write(ear ? 1 : 0);
        TestHelper.addCharLook(mplew, chr, true);

        return mplew.getPacket();
    }

    public static MaplePacket GameMaster_Func(int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AdminResult.Get());
        mplew.write(value);
        mplew.writeZeroBytes(17);

        return mplew.getPacket();
    }

    public static MaplePacket testCombo(int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_IncCombo.Get());
        mplew.writeInt(value);

        return mplew.getPacket();
    }

    public static MaplePacket getShowQuestCompletion(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_QuestClear.Get());
        mplew.writeShort(id);

        return mplew.getPacket();
    }

    public static MaplePacket fairyPendantMessage(int type, int percent) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.FAIRY_PEND_MSG.Get());
        mplew.writeShort(21); // 0x15
        mplew.writeInt(0); // idk
        mplew.writeShort(0); // idk
        mplew.writeShort(percent); // percent
        mplew.writeShort(0); // idk

        return mplew.getPacket();
    }

    public static MaplePacket showMagnet(int mobid, byte success) { // Monster Magnet
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.SHOW_MAGNET.Get());
        mplew.writeInt(mobid);
        mplew.write(success);

        return mplew.getPacket();
    }

    public static MaplePacket sendHint(String hint, int width, int height) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (width < 1) {
            width = hint.length() * 10;
            if (width < 40) {
                width = 40;
            }
        }
        if (height < 5) {
            height = 5;
        }
        mplew.writeShort(ServerPacket.Header.PLAYER_HINT.Get());
        mplew.writeMapleAsciiString(hint);
        mplew.writeShort(width);
        mplew.writeShort(height);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static MaplePacket yellowChat(String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SetWeekEventMessage.Get());
        mplew.write(-1); //could be something like mob displaying message.
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }

    public static MaplePacket getPeanutResult(int itemId, short quantity, int itemId2, short quantity2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_IncubatorResult.Get());
        mplew.writeInt(itemId);
        mplew.writeShort(quantity);
        mplew.writeInt(5060003);
        mplew.writeInt(itemId2);
        mplew.writeInt(quantity2);

        return mplew.getPacket();
    }

    public static final MaplePacket sendPyramidUpdate(final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.PYRAMID_UPDATE.Get());
        mplew.writeInt(amount); //1-132 ?
        return mplew.getPacket();
    }

    public static final MaplePacket sendPyramidResult(final byte rank, final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.PYRAMID_RESULT.Get());
        mplew.write(rank);
        mplew.writeInt(amount); //1-132 ?
        return mplew.getPacket();
    }

    //show_status_info - 01 53 1E 01
    //10/08/14/19/11
    //update_quest_info - 08 53 1E 00 00 00 00 00 00 00 00
    //show_status_info - 01 51 1E 01 01 00 30
    //update_quest_info - 08 51 1E 00 00 00 00 00 00 00 00
    public static final MaplePacket sendPyramidEnergy(final String type, final String amount) {
        return sendString(1, type, amount);
    }

    public static final MaplePacket sendString(final int type, final String object, final String amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        switch (type) {
            case 1:
                mplew.writeShort(ServerPacket.Header.ENERGY.Get());
                break;
            case 2:
                mplew.writeShort(ServerPacket.Header.GHOST_POINT.Get());
                break;
            case 3:
                mplew.writeShort(ServerPacket.Header.GHOST_STATUS.Get());
                break;
        }
        mplew.writeMapleAsciiString(object); //massacre_hit, massacre_cool, massacre_miss, massacre_party, massacre_laststage, massacre_skill
        mplew.writeMapleAsciiString(amount);
        return mplew.getPacket();
    }

    public static final MaplePacket sendGhostPoint(final String type, final String amount) {
        return sendString(2, type, amount); //PRaid_Point (0-1500???)
    }

    public static final MaplePacket sendGhostStatus(final String type, final String amount) {
        return sendString(3, type, amount); //Red_Stage(1-5), Blue_Stage, blueTeamDamage, redTeamDamage
    }

    public static MaplePacket MulungEnergy(int energy) {
        return sendPyramidEnergy("energy", String.valueOf(energy));
    }

    public static MaplePacket getPollQuestion() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.GAME_POLL_QUESTION.Get());
        mplew.writeInt(1);
        mplew.writeInt(14);
        mplew.writeMapleAsciiString(ServerConstants.Poll_Question);
        mplew.writeInt(ServerConstants.Poll_Answers.length); // pollcount
        for (byte i = 0; i < ServerConstants.Poll_Answers.length; i++) {
            mplew.writeMapleAsciiString(ServerConstants.Poll_Answers[i]);
        }

        return mplew.getPacket();
    }

    public static MaplePacket showEventInstructions() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_Desc.Get());
        mplew.write(0);
        return mplew.getPacket();
    }

    public static final MaplePacket getNodeProperties(final MapleMonster objectid, final MapleMap map) {
        //idk.
        if (objectid.getNodePacket() != null) {
            return objectid.getNodePacket();
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.MONSTER_PROPERTIES.Get());
        mplew.writeInt(objectid.getObjectId()); //?
        mplew.writeInt(map.getNodes().size());
        mplew.writeInt(objectid.getPosition().x);
        mplew.writeInt(objectid.getPosition().y);
        for (MapleNodeInfo mni : map.getNodes()) {
            mplew.writeInt(mni.x);
            mplew.writeInt(mni.y);
            mplew.writeInt(mni.attr);
            if (mni.attr == 2) { //msg
                mplew.writeInt(500); //? talkMonster
            }
        }
        mplew.writeZeroBytes(6);
        objectid.setNodePacket(mplew.getPacket());
        return objectid.getNodePacket();
    }

    public static final MaplePacket getMovingPlatforms(final MapleMap map) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_FootHoldInfo.Get());
        mplew.writeInt(map.getPlatforms().size());
        for (MaplePlatform mp : map.getPlatforms()) {
            mplew.writeMapleAsciiString(mp.name);
            mplew.writeInt(mp.start);
            mplew.writeInt(mp.SN.size());
            for (int x = 0; x < mp.SN.size(); x++) {
                mplew.writeInt(mp.SN.get(x));
            }
            mplew.writeInt(mp.speed);
            mplew.writeInt(mp.x1);
            mplew.writeInt(mp.x2);
            mplew.writeInt(mp.y1);
            mplew.writeInt(mp.y2);
            mplew.writeInt(mp.x1);//?
            mplew.writeInt(mp.y1);
            mplew.writeShort(mp.r);
        }
        return mplew.getPacket();
    }

}
