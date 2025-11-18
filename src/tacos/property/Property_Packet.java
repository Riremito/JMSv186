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
package tacos.property;

import tacos.config.Region;
import tacos.config.Version;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.IPacketHeader;

/**
 *
 * @author Riremito
 */
public class Property_Packet {

    public static boolean init() {
        int retval = 0;
        String path_sp = "properties/packet/" + Region.GetRegionName() + "_v" + Version.getVersion() + "_ServerPacket.properties";
        String path_cp = "properties/packet/" + Region.GetRegionName() + "_v" + Version.getVersion() + "_ClientPacket.properties";

        Property pr_sp = new Property(path_sp);
        if (pr_sp.open()) {
            for (ServerPacketHeader header : ServerPacketHeader.values()) {
                String[] vars = pr_sp.get(header.name(), "@FFFF").trim().split(" ");
                header.set(parseHeader(vars, ServerPacketHeader.values()));
            }
            retval++;
        }
        Property pr_cp = new Property(path_cp);
        if (pr_cp.open()) {
            for (ClientPacketHeader header : ClientPacketHeader.values()) {
                String[] vars = pr_cp.get(header.name(), "@FFFF").trim().split(" ");
                header.set(parseHeader(vars, ClientPacketHeader.values()));
            }
            retval++;
        }

        if (retval < 2) {
            return false;
        }

        return true;
    }

    public static void reload() {
        // reset
        for (ServerPacketHeader header : ServerPacketHeader.values()) {
            header.set(-1);
        }
        for (ClientPacketHeader header : ClientPacketHeader.values()) {
            header.set(-1);
        }
        // reload
        init();
    }

    public static int parseHeader(String[] vars, IPacketHeader[] iph) {
        int base = -1;
        int offset = 0;
        switch (vars.length) {
            case 1: {
                if (vars[0].length() == 0) {
                    return -1;
                }
                if ("@FFFF".length() <= vars[0].length() && vars[0].charAt(0) == '@') {
                    base = Integer.parseInt(vars[0].substring(1), 16);
                } else {
                    base = Integer.parseInt(vars[0]);
                }
                break;
            }
            case 3: {
                // + or -
                if (vars[1].length() != 1 || (vars[1].charAt(0) != '+' && vars[1].charAt(0) != '-')) {
                    return -1;
                }
                offset = Integer.parseInt(vars[2]);
                if (vars[1].charAt(0) == '-') {
                    offset = -offset;
                }
                // get base value
                if ("@FFFF".length() <= vars[0].length() && vars[0].charAt(0) == '@') {
                    base = Integer.parseInt(vars[0].substring(1), 16);
                } else {
                    for (IPacketHeader base_header : iph) {
                        // not work if the enum name is defined above the current value.
                        if (base_header.name().equals(vars[0])) {
                            base = base_header.get();
                            if (base == -1) {
                                DebugLogger.ErrorLog("base is @FFFF.");
                            }
                            break;
                        }
                    }
                }
                break;
            }
            default: {
                break;
            }
        }

        if (base == -1) {
            return -1;
        }

        return base + offset;
    }
}
