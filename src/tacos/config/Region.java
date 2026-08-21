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
    KMSB(1, "MS949"),
    KMS(1, "MS949"),
    KMST(2, "MS949"),
    JMS(3, "MS932"),
    JMST(3, "MS932"),
    CMS(4, "MS936"),
    GMST(5),
    TWMS(6, "MS950"),
    HKMS(61, "MS950"),
    THMS(7, "MS874"),
    MSEA(7),
    VMS(7),
    GMS(8),
    EMS(9),
    BMS(9),
    IMS(1),
    UNKNOWN(0);

    private final int value;
    private final String codepage;

    private Region(int value) {
        this.value = value;
        this.codepage = "MS932";
    }

    private Region(int value, String codepage) {
        this.value = value;
        this.codepage = codepage;
    }

    public int get() {
        return this.value;
    }

    public String getName() {
        return name();
    }

    public String getCodepage() {
        return this.codepage;
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
