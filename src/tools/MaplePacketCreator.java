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

import handling.MaplePacket;
import constants.ServerConstants;
import tools.data.output.MaplePacketLittleEndianWriter;
import packet.ServerPacket;

public class MaplePacketCreator {

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

}
