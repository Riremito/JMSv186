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
package packet.request;

import client.MapleCharacter;
import handling.MaplePacket;
import java.awt.Point;
import packet.ServerPacket;
import server.maps.MapleMapItem;

/**
 *
 * @author Riremito
 */
public class DropPacket {

    public enum EnterType {
        PICK_UP_ENABLED(0),
        ANIMATION(1),
        NO_ANIMATION(2),
        SPAWN(3),
        NO_ROTATE(4), // idk
        UNKNOWN;

        private int value;

        EnterType(int flag) {
            value = flag;
        }

        EnterType() {
            value = -1;
        }

        public boolean set(int flag) {
            value = flag;
            return true;
        }

        public int get() {
            return value;
        }
    }

    public enum LeaveType {
        EXPIRED(0),
        NO_ANIMATION(1), // not defined
        PICK_UP(2),
        PICK_UP_NO_SOUND(3),
        MESO_EXPLOSION(4),
        PICK_UP_PET(5),
        UNKNOWN;

        private int value;

        LeaveType(int flag) {
            value = flag;
        }

        LeaveType() {
            value = -1;
        }

        public boolean set(int flag) {
            value = flag;
            return true;
        }

        public int get() {
            return value;
        }
    }

    // CDropPool::OnDropEnterField
    // dropItemFromMapObject
    public static MaplePacket DropEnterField(MapleMapItem drop, EnterType et, Point dropto, Point dropfrom, int mobid) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_DropEnterField);

        p.Encode1(et.get());
        p.Encode4(drop.getObjectId());
        p.Encode1(drop.getMeso() > 0 ? 1 : 0);
        p.Encode4(drop.getItemId());
        p.Encode4(drop.getOwner());
        p.Encode1(drop.getDropType()); // 3 or not
        p.Encode2(dropto.x);
        p.Encode2(dropto.y);
        p.Encode4(mobid); // dwSourceID (MobID)

        switch (et) {
            case PICK_UP_ENABLED:
            case ANIMATION:
            case SPAWN:
            case NO_ROTATE: {
                p.Encode2(dropfrom.x);
                p.Encode2(dropfrom.y);
                p.Encode2(0);
                break;
            }
            case NO_ANIMATION: {
                break;
            }
            default: {
                break;
            }
        }

        // meso does not have this data
        if (drop.getMeso() == 0) {
            p.Encode8(-1);
        }

        p.Encode1(drop.isPlayerDrop() ? 0 : 1); // pet pick up?
        p.Encode1(0);

        return p.Get();
    }

    // CDropPool::OnDropLeaveField
    // removeItemFromMap
    // explodeDrop
    public static MaplePacket DropLeaveField(MapleMapItem drop, LeaveType lt, MapleCharacter chr, int pet_slot) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_DropLeaveField);

        p.Encode1(lt.get());
        p.Encode4(drop.getObjectId());

        switch (lt) {
            case EXPIRED:
            case NO_ANIMATION: {
                // no data is needed
                break;
            }
            case MESO_EXPLOSION: {
                p.Encode2(655); // explosion delay
                break;
            }
            case PICK_UP:
            case PICK_UP_NO_SOUND: {
                p.Encode4(chr.getObjectId()); // dwPickupID
                break;
            }
            case PICK_UP_PET: {
                p.Encode4(chr.getObjectId()); // dwPickupID
                p.Encode4(pet_slot);
                break;
            }
            default: {
                break;
            }
        }

        return p.Get();
    }

    public static MaplePacket DropEnterField(MapleMapItem drop, EnterType et, Point dropto) {
        return DropEnterField(drop, et, dropto, null, 0);
    }

    public static MaplePacket DropEnterField(MapleMapItem drop, EnterType et, Point dropto, Point dropfrom) {
        return DropEnterField(drop, et, dropto, dropfrom, 0);
    }

    public static MaplePacket DropLeaveField(MapleMapItem drop, LeaveType lt) {
        return DropLeaveField(drop, lt, null, 0);
    }
}
