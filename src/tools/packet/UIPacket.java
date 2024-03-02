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

import handling.MaplePacket;
import packet.server.ServerPacket;
import tools.MaplePacketCreator;
import tools.data.output.MaplePacketLittleEndianWriter;

public class UIPacket {

    public static final MaplePacket EarnTitleMsg(final String msg) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

// "You have acquired the Pig's Weakness skill."
        mplew.writeShort(ServerPacket.Header.EARN_TITLE_MSG.Get());
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static MaplePacket getTopMsg(String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_ScriptProgressMessage.Get());
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static final MaplePacket MapEff(final String path) {
        return MaplePacketCreator.environmentChange(path, 3);
    }

    public static final MaplePacket MapNameDisplay(final int mapid) {
        return MaplePacketCreator.environmentChange("maplemap/enter/" + mapid, 3);
    }

    public static final MaplePacket Aran_Start() {
        return MaplePacketCreator.environmentChange("Aran/balloon", 4);
    }

    public static final MaplePacket AranTutInstructionalBalloon(final String data) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.Get());
        mplew.write(0x18);
        mplew.writeMapleAsciiString(data);
        mplew.writeInt(1);

        return mplew.getPacket();
    }

    public static final MaplePacket ShowWZEffect(final String data) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.Get());
        mplew.write(0x13);
        mplew.writeMapleAsciiString(data);

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

    public static MaplePacket IntroLock(boolean enable) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_SetDirectionMode.Get());
        mplew.write(enable ? 1 : 0);
        mplew.writeInt(enable ? 1 : 0);

        return mplew.getPacket();
    }

    public static MaplePacket IntroDisableUI(boolean enable) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_SetStandAloneMode.Get());
        mplew.write(enable ? 1 : 0);

        return mplew.getPacket();
    }

    public static MaplePacket fishingUpdate(byte type, int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.FISHING_BOARD_UPDATE.Get());
        mplew.write(type);
        mplew.writeInt(id);

        return mplew.getPacket();
    }

    public static MaplePacket fishingCaught(int chrid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.FISHING_CAUGHT.Get());
        mplew.writeInt(chrid);
        return mplew.getPacket();
    }
}
