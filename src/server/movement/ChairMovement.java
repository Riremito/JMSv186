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

import java.awt.Point;
import packet.ServerPacket;

import tools.data.output.LittleEndianWriter;

public class ChairMovement extends AbstractLifeMovement {

    private int unk;

    public ChairMovement(int type, Point position, int duration, int newstate) {
        super(type, position, duration, newstate);
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
        lew.writeShort(getPosition().x);
        lew.writeShort(getPosition().y);
        lew.writeShort(unk);
        lew.write(getNewstate());
        lew.writeShort(getDuration());
    }

    @Override
    public void serialize(ServerPacket data) {
        data.Encode1(getType());
        data.Encode2(getPosition().x);
        data.Encode2(getPosition().y);
        data.Encode2(unk);
        data.Encode1(getNewstate());
        data.Encode2(getDuration());
    }
}
