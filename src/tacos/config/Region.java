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
package tacos.config;

/**
 *
 * @author Riremito
 */
public enum Region {
    KMSB(1),
    KMS(1),
    KMST(2),
    JMS(3),
    JMST(3),
    CMS(4),
    GMST(5),
    TWMS(6),
    THMS(7),
    MSEA(7),
    VMS(7),
    GMS(8),
    EMS(9),
    BMS(9),
    IMS(1),
    UNKNOWN(0);

    private final int value;

    private Region(int value) {
        this.value = value;
    }

    public int get() {
        return this.value;
    }

    public String getName() {
        return name();
    }

    public boolean check() {
        return equals(Config.REGION);
    }

    public static Region find(String name) {
        for (Region r : values()) {
            if (r.name().equals(name)) {
                return r;
            }
        }
        return UNKNOWN;
    }
}
