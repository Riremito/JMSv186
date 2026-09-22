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
 */
package tacos.packet.response;

import odin.client.MapleCharacter;
import tacos.config.Region;
import java.awt.Point;
import tacos.packet.ServerPacket;
import odin.server.maps.MapleMapItem;
import tacos.config.Config;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCDropPool {

    // CDropPool::OnDropEnterField
    public static ServerPacket DropEnterField(MapleMapItem drop, DropEnterType et, Point dropto, Point dropfrom, int mobid) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_DropEnterField);

        sp.Encode1(et.get()); // nEnterType
        sp.Encode4(drop.getObjectId()); // dwId
        sp.Encode1(drop.getMeso() > 0 ? 1 : 0); // bIsMoney
        sp.Encode4(drop.getItemId()); // nInfo
        sp.Encode4(drop.getOwnerId()); // dwOwnerID
        sp.Encode1(drop.getDropType()); // nOwnType, 3 or not
        sp.Encode2(dropto.x); // x
        sp.Encode2(dropto.y); // y
        sp.Encode4(mobid); // dwSourceID, MobID

        switch (et) {
            case UPDATE:
            case NORMAL:
            case SPAWN:
            case NO_ROTATE: {
                sp.Encode2(dropfrom.x); // x
                sp.Encode2(dropfrom.y); // y
                sp.Encode2(0); // tDelay
                break;
            }
            case SILENT: {
                break;
            }
            default: {
                break;
            }
        }

        // meso does not have this data
        if (drop.getMeso() == 0) {
            sp.Encode8(-1); // m_dateExpire
        }

        sp.Encode1(drop.isPlayerDrop() ? 0 : 1); // bByPet
        sp.Encode1(0);

        if (Config.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode2(0);
        }

        return sp;
    }

    // CDropPool::OnDropLeaveField
    public static ServerPacket DropLeaveField(MapleMapItem drop, DropLeaveType lt, MapleCharacter chr, int pet_slot) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_DropLeaveField);

        sp.Encode1(lt.get()); // leave type
        sp.Encode4(drop.getObjectId()); // id

        switch (lt) {
            case EXPIRED:
            case REMOVE: {
                // no data is needed
                break;
            }
            case EXPLOSION: {
                sp.Encode2(655); // tLeaveTime
                break;
            }
            case NORMAL:
            case SILENT: {
                sp.Encode4(chr.getObjectId()); // dwPickupID
                break;
            }
            case PET: {
                sp.Encode4(chr.getObjectId()); // dwPickupID
                sp.Encode4(pet_slot);
                break;
            }
            default: {
                break;
            }
        }

        return sp;
    }

    public static ServerPacket DropEnterField(MapleMapItem drop, DropEnterType et, Point dropto) {
        return DropEnterField(drop, et, dropto, null, 0);
    }

    public static ServerPacket DropEnterField(MapleMapItem drop, DropEnterType et, Point dropto, Point dropfrom) {
        return DropEnterField(drop, et, dropto, dropfrom, 0);
    }

    public static ServerPacket DropLeaveField(MapleMapItem drop, DropLeaveType lt) {
        return DropLeaveField(drop, lt, null, 0);
    }

    public enum DropEnterType {
        UPDATE(0),
        NORMAL(1),
        SILENT(2),
        SPAWN(3),
        NO_ROTATE(4),
        UNKNOWN;

        private int value;

        private DropEnterType(int value) {
            this.value = value;
        }

        private DropEnterType() {
            this.value = -1;
        }

        private int get() {
            return this.value;
        }
    }

    public enum DropLeaveType {
        EXPIRED(0),
        REMOVE(1),
        NORMAL(2),
        SILENT(3),
        EXPLOSION(4),
        PET(5),
        UNKNOWN;

        private int value;

        private DropLeaveType(int value) {
            this.value = value;
        }

        private DropLeaveType() {
            this.value = -1;
        }

        private int get() {
            return this.value;
        }
    }
}
