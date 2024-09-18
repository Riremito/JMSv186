/*
 * Copyright (C) 2024 Riremito
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
package test;

import config.ServerConfig;
import debug.Debug;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import packet.ClientPacket;
import packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class DumpMan {

    public final static void main(String args[]) {
        DumpOpcodeNames();
        // add valid ids dumper here plz
    }

    private static void DumpOpcodeNames() {
        FileWriter fw;
        try {
            fw = new FileWriter("dump/ServerPacket.properties");
            PrintWriter pw = new PrintWriter(fw);
            for (ServerPacket.Header header : ServerPacket.Header.values()) {
                pw.println(header.name() + " = " + "-1");
            }
            pw.close();
            fw.close();

            fw = new FileWriter("dump/ClientPacket.properties");
            pw = new PrintWriter(fw);
            for (ClientPacket.Header header : ClientPacket.Header.values()) {
                pw.println(header.name() + " = " + "-1");
            }
            pw.close();
            fw.close();
        } catch (IOException e) {
        }
    }

    private static void DumpOpcodes() {
        FileWriter fw;
        try {
            fw = new FileWriter("properties/packet/" + ServerConfig.GetRegionName() + "_v" + ServerConfig.GetVersion() + "_ServerPacket.properties");
            PrintWriter pw = new PrintWriter(fw);
            for (ServerPacket.Header header : ServerPacket.Header.values()) {
                int val = (short) header.Get();
                if (val != -1) {
                    Debug.DebugLog(String.format("@%04X", val) + " : " + header.name());
                    pw.println(header.name() + " = " + String.format("@%04X", val));
                }
            }
            pw.close();
            fw.close();

            fw = new FileWriter("properties/packet/" + ServerConfig.GetRegionName() + "_v" + ServerConfig.GetVersion() + "_ClientPacket.properties");
            pw = new PrintWriter(fw);
            for (ClientPacket.Header header : ClientPacket.Header.values()) {
                int val = (short) header.Get();
                if (val != -1) {
                    Debug.DebugLog(String.format("@%04X", val) + " : " + header.name());
                    pw.println(header.name() + " = " + String.format("@%04X", val));
                }
            }
            pw.close();
            fw.close();
        } catch (IOException e) {
        }
    }
}
