/*
 * Copyright (C) 2025 Riremito
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
package packet.ops;

import config.Region;
import config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsDBCHAR {
    DBCHAR_ALL(-1L),
    DBCHAR_CHARACTER(0x1L),
    DBCHAR_MONEY(0x2L),
    DBCHAR_ITEMSLOTEQUIP(0x4L),
    DBCHAR_ITEMSLOTCONSUME(0x8L),
    DBCHAR_ITEMSLOTINSTALL(0x10L),
    DBCHAR_ITEMSLOTETC(0x20L),
    DBCHAR_ITEMSLOTCASH(0x40L),
    DBCHAR_INVENTORYSIZE(0x80L),
    DBCHAR_SKILLRECORD(0x100L),
    DBCHAR_QUESTRECORD(0x200L),
    DBCHAR_MINIGAMERECORD(0x400L),
    DBCHAR_COUPLERECORD(0x800L),
    DBCHAR_MAPTRANSFER(0x1000L),
    DBCHAR_AVATAR(0x2000L),
    DBCHAR_QUESTCOMPLETE(0x4000L),
    DBCHAR_SKILLCOOLTIME(0x8000L),
    DBCHAR_MONSTERBOOKCARD(0x10000L),
    DBCHAR_MONSTERBOOKCOVER(0x20000L),
    DBCHAR_NEWYEARCARD(0x40000L),
    DBCHAR_QUESTRECORDEX(0x80000L),
    //DBCHAR_ADMINSHOPCOUNT(0x100000L),
    DBCHAR_EQUIPEXT(0x100000L),
    DBCHAR_WILDHUNTERINFO(0x200000L),
    DBCHAR_QUESTCOMPLETE_OLD(0x400000L),
    DBCHAR_VISITORLOG(0x800000L),
    DBCHAR_VISITORLOG1(0x1000000L),
    DBCHAR_VISITORLOG2(0x2000000L),
    DBCHAR_VISITORLOG3(0x4000000L),
    DBCHAR_VISITORLOG4(0x8000000L),
    DBCHAR_ITEMSLOT(0x7CL),
    UNKNOWN(0);

    private long value;

    OpsDBCHAR(long val) {
        value = val;
    }

    OpsDBCHAR() {
        value = -1;
    }

    public long get() {
        return value;
    }

    public void set(long val) {
        value = val;
    }

    public boolean check(long mask) {
        if ((mask & value) != 0) {
            return true;
        }
        return false;
    }

    public static boolean init() {
        // never changed.
        if (Version.GreaterOrEqual(Region.JMS, 308)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.JMS, 194)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.JMS, 187)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.JMS, 186)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.JMS, 180)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.JMS, 165)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.JMS, 164)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.JMS, 147)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.JMS, 146)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.JMS, 131)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 183)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 160)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 149)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 148)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 138)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 127)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 121)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 119)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 118)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 114)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 95)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 92)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 84)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 71)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.KMS, 65)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.TWMS, 148)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.TWMS, 124)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.CMS, 104)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.CMS, 85)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.THMS, 96)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.THMS, 87)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.MSEA, 102)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.MSEA, 100)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.EMS, 89)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.EMS, 76)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.EMS, 70)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.EMS, 50)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.GMS, 95)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.GMS, 91)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.GMS, 84)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.GMS, 83)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.GMS, 66)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.GMS, 65)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.GMS, 62)) {
            return true;
        }
        if (Version.GreaterOrEqual(Region.GMS, 61)) {
            return true;
        }
        if (Version.Equal(Region.BMS, 24)) {
            return true;
        }
        if (Version.Equal(Region.VMS, 35)) {
            return true;
        }
        if (Version.Equal(Region.IMS, 1)) {
            return true;
        }
        return false;
    }
}
