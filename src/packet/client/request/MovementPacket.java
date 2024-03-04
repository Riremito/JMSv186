/*
 * Copyright (C) 2023 Riremito
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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package packet.client.request;

import config.ServerConfig;
import debug.Debug;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import packet.client.ClientPacket;
import packet.server.ServerPacket;
import server.movement.AbsoluteLifeMovement;
import server.movement.AranMovement;
import server.movement.BounceMovement;
import server.movement.ChairMovement;
import server.movement.ChangeEquipSpecialAwesome;
import server.movement.JumpDownMovement;
import server.movement.LifeMovementFragment;
import server.movement.RelativeLifeMovement;

/**
 *
 * @author Riremito
 */
public class MovementPacket {

    /*
        a = 0x07
        b = 0x08
        t = 0x09
        n = 0x0A
        v = 0x0B
        f = 0x0C
        r = 0x0D
          = 0x20
        ! = 0x21
        " = 0x22
        # = 0x23
        $ = 0x24
        % = 0x25
     */
    final private static ArrayList<Integer> movetype_1 = new ArrayList<Integer>(); // absolute movement
    final private static ArrayList<Integer> movetype_1_1 = new ArrayList<Integer>(); // absolute movement jump down
    final private static ArrayList<Integer> movetype_2 = new ArrayList<Integer>(); // relative movement
    final private static ArrayList<Integer> movetype_3 = new ArrayList<Integer>(); // flash jump?
    final private static ArrayList<Integer> movetype_4 = new ArrayList<Integer>(); // enter map from spawn point
    final private static ArrayList<Integer> movetype_5 = new ArrayList<Integer>(); // ?
    final private static ArrayList<Integer> movetype_6 = new ArrayList<Integer>(); // ?
    final private static ArrayList<Integer> movetype_7 = new ArrayList<Integer>(); // ?

    public static boolean Init() {
        // old version
        if (ServerConfig.version <= 131) {
            // type1
            movetype_1.add(0x00);
            movetype_1.add(0x05);
            // type2
            movetype_2.add(0x01);
            movetype_2.add(0x02);
            movetype_2.add(0x06);
            movetype_2.add(0x0C);
            // type3
            movetype_3.add(0x03);
            movetype_3.add(0x04);
            movetype_3.add(0x07);
            movetype_3.add(0x08); // flash jump
            movetype_3.add(0x09);
            movetype_3.add(0x0B);
            // type4, Spawn
            movetype_4.add(0x0A); // enter map
            return true;
        }
        // v164 jump down uses different value
        // v186.1
        // type1
        movetype_1.add(0x00);
        movetype_1.add(0x05);
        movetype_1_1.add(0x0C); // jump down
        movetype_1.add(0x0E);
        movetype_1.add(0x24);
        movetype_1.add(0x25);
        // type2
        movetype_2.add(0x01);
        movetype_2.add(0x02);
        movetype_2.add(0x0D);
        movetype_2.add(0x11);
        movetype_2.add(0x13);
        movetype_2.add(0x20);
        movetype_2.add(0x21);
        movetype_2.add(0x22);
        movetype_2.add(0x23);
        // type3
        movetype_3.add(0x03);
        movetype_3.add(0x04);
        movetype_3.add(0x06);
        movetype_3.add(0x07);
        movetype_3.add(0x08);
        movetype_3.add(0x0A);
        // type4, enter map
        movetype_4.add(0x09); // enter map
        // type5
        movetype_5.add(0x0B);
        // type6
        movetype_6.add(0x12);
        // type7
        movetype_7.add(0x15);
        movetype_7.add(0x16);
        movetype_7.add(0x17);
        movetype_7.add(0x18);
        movetype_7.add(0x19);
        movetype_7.add(0x1A);
        movetype_7.add(0x1B);
        movetype_7.add(0x1C);
        movetype_7.add(0x1D);
        movetype_7.add(0x1E);
        movetype_7.add(0x1F);
        return true;
    }

    private static boolean IsAbsoluteMovement(byte nAttr) {
        return movetype_1.contains((int) nAttr);
    }

    private static boolean IsAbsoluteMovement_JumpDown(byte nAttr) {
        return movetype_1_1.contains((int) nAttr);
    }

    private static boolean IsRelativeMovement(byte nAttr) {
        return movetype_2.contains((int) nAttr);
    }

    private static boolean IsFlashJump(byte nAttr) {
        return movetype_3.contains((int) nAttr);
    }

    private static boolean IsEnterMap(byte nAttr) {
        return movetype_4.contains((int) nAttr);
    }

    private static boolean Is5(byte nAttr) {
        return movetype_5.contains((int) nAttr);
    }

    private static boolean Is6(byte nAttr) {
        return movetype_6.contains((int) nAttr);
    }

    private static boolean Is7(byte nAttr) {
        return movetype_7.contains((int) nAttr);
    }

    // CMovePath::Decode
    // parseMovement
    public static final List<LifeMovementFragment> CMovePath_Decode(ClientPacket p, int kind) {
        final List<LifeMovementFragment> res = new ArrayList<LifeMovementFragment>();
        final byte numCommands = p.Decode1();

        byte command = 0;
        for (byte i = 0; i < numCommands; i++) {
            command = p.Decode1();
            // v131, v186
            if (IsAbsoluteMovement(command)) {
                final short xpos = p.Decode2();
                final short ypos = p.Decode2();
                final short xwobble = p.Decode2();
                final short ywobble = p.Decode2();
                final short unk = p.Decode2();
                short xoffset = 0;
                short yoffset = 0;

                if ((ServerConfig.IsJMS() && 165 <= ServerConfig.GetVersion())
                        || ServerConfig.IsTWMS()
                        || ServerConfig.IsCMS()) {
                    xoffset = p.Decode2();
                    yoffset = p.Decode2();
                }

                final byte newstate = p.Decode1();
                final short duration = p.Decode2();
                final AbsoluteLifeMovement alm = new AbsoluteLifeMovement(command, new Point(xpos, ypos), duration, newstate);
                alm.setUnk(unk);
                alm.setPixelsPerSecond(new Point(xwobble, ywobble));

                if ((ServerConfig.IsJMS() && 165 <= ServerConfig.GetVersion())
                        || ServerConfig.IsTWMS()
                        || ServerConfig.IsCMS()) {
                    alm.setOffset(new Point(xoffset, yoffset));
                }

                res.add(alm);
                continue;
            }
            // v186
            if (IsAbsoluteMovement_JumpDown(command)) {
                final short xpos = p.Decode2();
                final short ypos = p.Decode2();
                final short xwobble = p.Decode2();
                final short ywobble = p.Decode2();
                final short unk = p.Decode2();
                final short fh = p.Decode2();
                final short xoffset = p.Decode2();
                final short yoffset = p.Decode2();
                final byte newstate = p.Decode1();
                final short duration = p.Decode2();
                final JumpDownMovement jdm = new JumpDownMovement(command, new Point(xpos, ypos), duration, newstate);
                jdm.setUnk(unk);
                jdm.setPixelsPerSecond(new Point(xwobble, ywobble));
                jdm.setOffset(new Point(xoffset, yoffset));
                jdm.setFH(fh);
                res.add(jdm);
                continue;
            }
            if (IsRelativeMovement(command)) {
                final short xmod = p.Decode2();
                final short ymod = p.Decode2();
                final byte newstate = p.Decode1();
                final short duration = p.Decode2();
                final RelativeLifeMovement rlm = new RelativeLifeMovement(command, new Point(xmod, ymod), duration, newstate);
                res.add(rlm);
                continue;
            }
            // v131, v186
            if (IsFlashJump(command) || Is5(command)) {
                final short xpos = p.Decode2();
                final short ypos = p.Decode2();
                final short unk = p.Decode2();
                final byte newstate = p.Decode1();
                final short duration = p.Decode2();
                final ChairMovement cm = new ChairMovement(command, new Point(xpos, ypos), duration, newstate);
                cm.setUnk(unk);
                res.add(cm);
                continue;
            }
            // v131, v186
            if (IsEnterMap(command)) {
                res.add(new ChangeEquipSpecialAwesome(command, p.Decode1()));
                continue;
            }
            // v186
            if (Is7(command)) {
                final byte newstate = p.Decode1();
                final short unk = p.Decode2();
                final AranMovement am = new AranMovement(command, new Point(0, 0), unk, newstate);
                res.add(am);
                continue;
            }

            Debug.ErrorLog("CMovePath_Decode, type = " + kind + ", command = " + command);
            break;
        }

        if (numCommands != res.size()) {
            Debug.ErrorLog("parseMovement, type = " + kind + ", command = " + command);
            return null;
        }
        return res;
    }

    public static final List<LifeMovementFragment> parseMovement(ClientPacket p, int kind) {
        final List<LifeMovementFragment> res = new ArrayList<LifeMovementFragment>();
        final byte numCommands = p.Decode1();

        byte command = 0;
        for (byte i = 0; i < numCommands; i++) {
            command = p.Decode1();
            switch (command) {
                case -1: // Bounce movement?
                case 0x12: // Soaring?
                {
                    final short xpos = p.Decode2();
                    final short ypos = p.Decode2();
                    final short unk = p.Decode2();
                    final short fh = p.Decode2();
                    final byte newstate = p.Decode1();
                    final short duration = p.Decode2();
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
                    final short xpos = p.Decode2();
                    final short ypos = p.Decode2();
                    final short xwobble = p.Decode2();
                    final short ywobble = p.Decode2();
                    final short unk = p.Decode2();
                    short xoffset = 0;
                    short yoffset = 0;

                    if (131 < ServerConfig.version) {
                        xoffset = p.Decode2();
                        yoffset = p.Decode2();
                    }

                    final byte newstate = p.Decode1();
                    final short duration = p.Decode2();
                    final AbsoluteLifeMovement alm = new AbsoluteLifeMovement(command, new Point(xpos, ypos), duration, newstate);
                    alm.setUnk(unk);
                    alm.setPixelsPerSecond(new Point(xwobble, ywobble));

                    if (131 < ServerConfig.version) {
                        alm.setOffset(new Point(xoffset, yoffset));
                    }

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
                    final short xmod = p.Decode2();
                    final short ymod = p.Decode2();
                    final byte newstate = p.Decode1();
                    final short duration = p.Decode2();
                    final RelativeLifeMovement rlm = new RelativeLifeMovement(command, new Point(xmod, ymod), duration, newstate);
                    res.add(rlm);
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
                    final byte newstate = p.Decode1();
                    final short unk = p.Decode2();
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
                    final short xpos = p.Decode2();
                    final short ypos = p.Decode2();
                    final short unk = p.Decode2();
                    final byte newstate = p.Decode1();
                    final short duration = p.Decode2();
                    final ChairMovement cm = new ChairMovement(command, new Point(xpos, ypos), duration, newstate);
                    cm.setUnk(unk);
                    res.add(cm);
                    break;
                }
                case 9: {// change equip ???
                    res.add(new ChangeEquipSpecialAwesome(command, p.Decode1()));
                    break;
                }
                case 0xC: { // Jump Down
                    final short xpos = p.Decode2();
                    final short ypos = p.Decode2();
                    final short xwobble = p.Decode2();
                    final short ywobble = p.Decode2();
                    final short unk = p.Decode2();
                    final short fh = p.Decode2();
                    final short xoffset = p.Decode2();
                    final short yoffset = p.Decode2();
                    final byte newstate = p.Decode1();
                    final short duration = p.Decode2();
                    final JumpDownMovement jdm = new JumpDownMovement(command, new Point(xpos, ypos), duration, newstate);
                    jdm.setUnk(unk);
                    jdm.setPixelsPerSecond(new Point(xwobble, ywobble));
                    jdm.setOffset(new Point(xoffset, yoffset));
                    jdm.setFH(fh);
                    res.add(jdm);
                    break;
                }
                default:
                    final byte newstate = p.Decode1();
                    final short unk = p.Decode2();
                    final AranMovement am = new AranMovement(command, new Point(0, 0), unk, newstate);
                    res.add(am);
                    break;
            }
        }
        if (numCommands != res.size()) {
            Debug.ErrorLog("parseMovement, type = " + kind + ", command = " + command);
            return null;
        }
        return res;
    }

    public static byte[] serializeMovementList(List<LifeMovementFragment> moves) {
        ServerPacket data = new ServerPacket();
        data.Encode1(moves.size());

        for (LifeMovementFragment move : moves) {
            move.serialize(data);
        }

        return data.Get().getBytes();
    }
}
