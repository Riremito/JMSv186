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
package server.movement;

import config.ServerConfig;
import java.awt.Point;
import packet.ServerPacket;

import tools.data.output.LittleEndianWriter;

public class AbsoluteLifeMovement extends AbstractLifeMovement {

    private Point pixelsPerSecond, offset;
    private int unk;

    public AbsoluteLifeMovement(int type, Point position, int duration, int newstate) {
        super(type, position, duration, newstate);
    }

    public Point getPixelsPerSecond() {
        return pixelsPerSecond;
    }

    public void setPixelsPerSecond(Point wobble) {
        this.pixelsPerSecond = wobble;
    }

    public Point getOffset() {
        return offset;
    }

    public void setOffset(Point wobble) {
        this.offset = wobble;
    }

    public int getUnk() {
        return unk;
    }

    public void setUnk(int unk) {
        this.unk = unk;
    }

    @Override
    public void serialize(LittleEndianWriter lew) {
        lew.write(getType());
        lew.writePos(getPosition());
        lew.writePos(pixelsPerSecond);
        lew.writeShort(unk);
        if (ServerConfig.version > 131) {
            lew.writePos(offset);
        }
        lew.write(getNewstate());
        lew.writeShort(getDuration());
    }

    @Override
    public void serialize(ServerPacket data) {
        data.Encode1(getType());
        data.Encode2((short) getPosition().x);
        data.Encode2((short) getPosition().y);
        data.Encode2((short) pixelsPerSecond.x);
        data.Encode2((short) pixelsPerSecond.y);
        data.Encode2(unk);

        if (ServerConfig.version > 131) {
            data.Encode2((short) offset.x);
            data.Encode2((short) offset.y);
        }

        data.Encode1(getNewstate());
        data.Encode2(getDuration());
    }
}
