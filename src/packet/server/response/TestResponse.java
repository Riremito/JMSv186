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
package packet.server.response;

import client.MapleCharacter;
import client.inventory.IItem;
import handling.MaplePacket;
import java.sql.ResultSet;
import java.sql.SQLException;
import packet.server.ServerPacket;
import packet.server.response.struct.TestHelper;
import tools.MaplePacketCreator;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class TestResponse {
    // 0x005E @005E 00, ミニマップ点滅, 再読み込みかも?

    public static MaplePacket ReloadMiniMap() {
        ServerPacket p = new ServerPacket(ServerPacket.Header.UNKNOWN_RELOAD_MINIMAP);
        p.Encode1((byte) 0x00);
        return p.Get();
    }

    // 0x0083 @0083, 画面の位置をキャラクターを中心とした場所に変更, 背景リロードしてるかも?
    public static MaplePacket ReloadMap() {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_ClearBackgroundEffect);
        return p.Get();
    }

    public static MaplePacket sendMesobagSuccess(int mesos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MesoGive_Succeeded.Get());
        mplew.writeInt(mesos);
        return mplew.getPacket();
    }

    public static MaplePacket RandomMesoBagSuccess(byte type, int mesos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_Random_Mesobag_Succeed.Get());
        mplew.write(type);
        mplew.writeInt(mesos);
        return mplew.getPacket();
    }

    public static MaplePacket sendMesobagFailed() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MesoGive_Failed.Get());
        return mplew.getPacket();
    }

    public static MaplePacket RandomMesoBagFailed() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_Random_Mesobag_Failed.Get());
        return mplew.getPacket();
    }

    public static MaplePacket showNotes(ResultSet notes, int count) throws SQLException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MemoResult.Get());
        mplew.write(3);
        mplew.write(count);
        for (int i = 0; i < count; i++) {
            mplew.writeInt(notes.getInt("id"));
            mplew.writeMapleAsciiString(notes.getString("from"));
            mplew.writeMapleAsciiString(notes.getString("message"));
            mplew.writeLong(TestHelper.getKoreanTimestamp(notes.getLong("timestamp")));
            mplew.write(notes.getInt("gift"));
            notes.next();
        }
        return mplew.getPacket();
    }

    public static MaplePacket playCashSong(int itemid, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_PlayJukeBox.Get());
        mplew.writeInt(itemid);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }

    public static MaplePacket getTrockRefresh(MapleCharacter chr, boolean vip, boolean delete) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MapTransferResult.Get());
        mplew.write(delete ? 2 : 3);
        mplew.write(vip ? 1 : 0);
        if (vip) {
            int[] map = chr.getRocks();
            for (int i = 0; i < 10; i++) {
                mplew.writeInt(map[i]);
            }
        } else {
            int[] map = chr.getRegRocks();
            for (int i = 0; i < 5; i++) {
                mplew.writeInt(map[i]);
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket showXmasSurprise(int idFirst, IItem item, int accid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.XMAS_SURPRISE.Get());
        mplew.write(230);
        mplew.writeLong(idFirst); //uniqueid of the xmas surprise itself
        mplew.writeInt(0);
        PointShopResponse.addCashItemInfo(mplew, item, accid, 0); //info of the new item, but packet shows 0 for sn?
        mplew.writeInt(item.getItemId());
        mplew.write(1);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket useCharm(byte charmsleft, byte daysleft) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.Get());
        mplew.write(6);
        mplew.write(1);
        mplew.write(charmsleft);
        mplew.write(daysleft);
        return mplew.getPacket();
    }

    public static MaplePacket useChalkboard(final int charid, final String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserADBoard.Get());
        mplew.writeInt(charid);
        if (msg == null || msg.length() <= 0) {
            mplew.write(0);
        } else {
            mplew.write(1);
            mplew.writeMapleAsciiString(msg);
        }
        return mplew.getPacket();
    }

    public static MaplePacket useWheel(byte charmsleft) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.Get());
        mplew.write(21);
        mplew.writeLong(charmsleft);
        return mplew.getPacket();
    }

    public static MaplePacket changePetName(MapleCharacter chr, String newname, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_PetNameChanged.Get());
        mplew.writeInt(chr.getId());
        mplew.write(0);
        mplew.writeMapleAsciiString(newname);
        mplew.write(slot);
        return mplew.getPacket();
    }

    public static final MaplePacket Aran_Start() {
        return MaplePacketCreator.environmentChange("Aran/balloon", 4);
    }

    public static MaplePacket fishingUpdate(byte type, int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.FISHING_BOARD_UPDATE.Get());
        mplew.write(type);
        mplew.writeInt(id);
        return mplew.getPacket();
    }

    public static MaplePacket getTopMsg(String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ScriptProgressMessage.Get());
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }

    public static MaplePacket summonHelper(boolean summon) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserHireTutor.Get());
        mplew.write(summon ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket summonMessage(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserTutorMsg.Get());
        mplew.write(1);
        mplew.writeInt(type);
        mplew.writeInt(7000); // probably the delay
        return mplew.getPacket();
    }

    public static MaplePacket summonMessage(String message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserTutorMsg.Get());
        mplew.write(0);
        mplew.writeMapleAsciiString(message);
        mplew.writeInt(200); // IDK
        mplew.writeShort(0);
        mplew.writeInt(10000); // Probably delay
        return mplew.getPacket();
    }

    public static final MaplePacket ShowWZEffect(final String data) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.Get());
        mplew.write(19);
        mplew.writeMapleAsciiString(data);
        return mplew.getPacket();
    }

    public static final MaplePacket AranTutInstructionalBalloon(final String data) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.Get());
        mplew.write(24);
        mplew.writeMapleAsciiString(data);
        mplew.writeInt(1);
        return mplew.getPacket();
    }

    public static MaplePacket IntroDisableUI(boolean enable) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SetStandAloneMode.Get());
        mplew.write(enable ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket IntroLock(boolean enable) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SetDirectionMode.Get());
        mplew.write(enable ? 1 : 0);
        mplew.writeInt(enable ? 1 : 0);
        return mplew.getPacket();
    }

    public static final MaplePacket MapEff(final String path) {
        return MaplePacketCreator.environmentChange(path, 3);
    }

    public static final MaplePacket MapNameDisplay(final int mapid) {
        return MaplePacketCreator.environmentChange("maplemap/enter/" + mapid, 3);
    }

    public static MaplePacket fishingCaught(int chrid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.FISHING_CAUGHT.Get());
        mplew.writeInt(chrid);
        return mplew.getPacket();
    }

    public static final MaplePacket EarnTitleMsg(final String msg) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // "You have acquired the Pig's Weakness skill."
        mplew.writeShort(ServerPacket.Header.EARN_TITLE_MSG.Get());
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }
}
