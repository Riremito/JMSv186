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
package handling.channel.handler;

import config.ServerConfig;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import packet.server.ServerPacket;

import server.maps.AnimatedMapleMapObject;
import server.movement.*;
import tools.data.input.SeekableLittleEndianAccessor;

public class MovementParse {

    //1 = player, 2 = mob, 3 = pet, 4 = summon, 5 = dragon
    public static final List<LifeMovementFragment> parseMovement(final SeekableLittleEndianAccessor lea, int kind) {
        final List<LifeMovementFragment> res = new ArrayList<LifeMovementFragment>();
        final byte numCommands = lea.readByte();

        for (byte i = 0; i < numCommands; i++) {
            final byte command = lea.readByte();
            switch (command) {
                //-2 = 12 02 0D FE 00 00 00 00 07 1E 00 00 BE
                //FE 12 02 0C FE 00 00 27 00 05 00 00
                //-3 = 12 02 0D FE 00 00 00 00 07 1E 00 00 F2
                //FD 12 02 0C FE 00 00 3F 00 05 00 00
                case -1: // Bounce movement?
                case 0x12: // Soaring?
                {
                    final short xpos = lea.readShort();
                    final short ypos = lea.readShort();
                    final short unk = lea.readShort();
                    final short fh = lea.readShort();
                    final byte newstate = lea.readByte();
                    final short duration = lea.readShort();
                    final BounceMovement bm = new BounceMovement(command, new Point(xpos, ypos), duration, newstate);
                    bm.setFH(fh);
                    bm.setUnk(unk);
                    res.add(bm);
                    break;
                }
                case 0:
                case 5:
                case 0xE:
                case 0x24:
                case 0x25: {
                    final short xpos = lea.readShort();
                    final short ypos = lea.readShort();
                    final short xwobble = lea.readShort();
                    final short ywobble = lea.readShort();
                    final short unk = lea.readShort();

                    if (ServerConfig.version <= 131) {
                        final byte newstate = lea.readByte();
                        final short duration = lea.readShort();
                        final AbsoluteLifeMovement alm = new AbsoluteLifeMovement(command, new Point(xpos, ypos), duration, newstate);
                        alm.setUnk(unk);
                        alm.setPixelsPerSecond(new Point(xwobble, ywobble));
                        res.add(alm);
                        return res;
                    }

                    final short xoffset = lea.readShort();
                    final short yoffset = lea.readShort();
                    final byte newstate = lea.readByte();
                    final short duration = lea.readShort();
                    final AbsoluteLifeMovement alm = new AbsoluteLifeMovement(command, new Point(xpos, ypos), duration, newstate);
                    alm.setUnk(unk);
                    alm.setPixelsPerSecond(new Point(xwobble, ywobble));
                    alm.setOffset(new Point(xoffset, yoffset));
                    // log.trace("Move to {},{} command {} wobble {},{} ? {} state {} duration {}", new Object[] { xpos,
                    // xpos, command, xwobble, ywobble, newstate, duration });
                    res.add(alm);
                    break;
                }
                case 1:
                case 2:
                case 0xD:
                case 0x11:
                case 0x13:
                case 0x20:
                case 0x21:
                case 0x22:
                case 0x23: {
                    final short xmod = lea.readShort();
                    final short ymod = lea.readShort();
                    final byte newstate = lea.readByte();
                    final short duration = lea.readShort();
                    final RelativeLifeMovement rlm = new RelativeLifeMovement(command, new Point(xmod, ymod), duration, newstate);
                    res.add(rlm);
                    // log.trace("Relative move {},{} state {}, duration {}", new Object[] { xmod, ymod, newstate,
                    // duration });
                    break;
                }
                case 0xF:
                case 0x10:
                case 0x14:
                case 0x15:
                case 0x16:
                case 0x17:
                case 0x18:
                case 0x19:
                case 0x1A:
                case 0x1B:
                case 0x1C:
                case 0x1D:
                case 0x1E:
                case 0x1F: {
                    final byte newstate = lea.readByte();
                    final short unk = lea.readShort();
                    final AranMovement am = new AranMovement(command, new Point(0, 0), unk, newstate);
                    res.add(am);
                    break;
                }
                case 3:
                case 4:
                case 6:
                case 7:
                case 8:
                case 0xA:
                case 0xB: {
                    final short xpos = lea.readShort();
                    final short ypos = lea.readShort();
                    final short unk = lea.readShort();
                    final byte newstate = lea.readByte();
                    final short duration = lea.readShort();
                    final ChairMovement cm = new ChairMovement(command, new Point(xpos, ypos), duration, newstate);
                    cm.setUnk(unk);
                    res.add(cm);
                    break;
                }
                case 9: {// change equip ???
                    res.add(new ChangeEquipSpecialAwesome(command, lea.readByte()));
                    break;
                }
                case 0xC: { // Jump Down
                    final short xpos = lea.readShort();
                    final short ypos = lea.readShort();
                    final short xwobble = lea.readShort();
                    final short ywobble = lea.readShort();
                    final short unk = lea.readShort();
                    final short fh = lea.readShort();
                    final short xoffset = lea.readShort();
                    final short yoffset = lea.readShort();
                    final byte newstate = lea.readByte();
                    final short duration = lea.readShort();
                    final JumpDownMovement jdm = new JumpDownMovement(command, new Point(xpos, ypos), duration, newstate);
                    jdm.setUnk(unk);
                    jdm.setPixelsPerSecond(new Point(xwobble, ywobble));
                    jdm.setOffset(new Point(xoffset, yoffset));
                    jdm.setFH(fh);
                    res.add(jdm);
                    break;
                }
                default:
                    final byte newstate = lea.readByte();
                    final short unk = lea.readShort();
                    final AranMovement am = new AranMovement(command, new Point(0, 0), unk, newstate);
                    res.add(am);
                    break;
            }
        }
        if (numCommands != res.size()) {
            System.out.println("error in movement");
            return null; // Probably hack
        }
        return res;
    }

    public static final void updatePosition(final List<LifeMovementFragment> movement, final AnimatedMapleMapObject target, final int yoffset) {
        for (final LifeMovementFragment move : movement) {
            if (move instanceof LifeMovement) {
                if (move instanceof AbsoluteLifeMovement) {
                    Point position = ((LifeMovement) move).getPosition();
                    position.y += yoffset;
                    target.setPosition(position);
                }
                target.setStance(((LifeMovement) move).getNewstate());
            }
        }
    }
}
