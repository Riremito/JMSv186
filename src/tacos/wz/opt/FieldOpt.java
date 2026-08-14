/*
 * Copyright (C) 2026 Riremito
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
package tacos.wz.opt;

/**
 *
 * @author Riremito
 */
public enum FieldOpt {
    FIELDOPT_MOVELIMIT(1), // 0x1
    FIELDOPT_SKILLLIMIT(2), // 0x2
    FIELDOPT_SUMMONLIMIT(3), // 0x4
    FIELDOPT_MYSTICDOORLIMIT(4), // 0x8
    FIELDOPT_MIGRATELIMIT(5), // 0x10
    FIELDOPT_PORTALSCROLLLIMIT(6), // 0x20
    FIELDOPT_TELEPORTITEMLIMIT(7), // 0x40
    FIELDOPT_MINIGAMELIMIT(8), // 0x80
    FIELDOPT_SPECIFICPORTALSCROLLLIMIT(9), // 0x100
    FIELDOPT_TAMINGMOBLIMIT(10), // 0x200
    FIELDOPT_STATCHANGEITEMCONSUMELIMIT(11), // 0x400
    FIELDOPT_PARTYBOSSCHANGELIMIT(12), // 0x800
    FIELDOPT_NOMOBCAPACITYLIMIT(13), // 0x1000
    FIELDOPT_WEDDINGINVITATIONLIMIT(14), // 0x2000
    FIELDOPT_CASHWEATHERCONSUMELIMIT(15), // 0x4000
    FIELDOPT_NOPET(16), // 0x8000
    FIELDOPT_ANTIMACROLIMIT(17), // 0x10000
    FIELDOPT_FALLDOWNLIMIT(18), // 0x20000
    FIELDOPT_SUMMONNPCLIMIT(19), // 0x40000
    FIELDOPT_NOEXPDECREASE(20), // 0x80000
    FIELDOPT_NODAMAGEONFALLING(21), // 0x100000
    FIELDOPT_PARCELOPENLIMIT(22), // 0x200000
    FIELDOPT_DROPLIMIT(23), // 0x400000
    FIELDOPT_ROCKETBOOSTER_LIMIT(24), // 0x800000
    UNKNOWN;

    private int value;

    FieldOpt(int val) {
        this.value = val;
    }

    FieldOpt() {
        this.value = -1;
    }

    public int get() {
        return 1 << (this.value - 1);
    }

    public boolean check(int flag) {
        return (flag & get()) != 0;
    }
}
