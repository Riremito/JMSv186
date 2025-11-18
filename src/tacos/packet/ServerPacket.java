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
package tacos.packet;

import tacos.config.CodePage;
import tacos.config.Content;
import tacos.network.ByteArrayMaplePacket;
import tacos.network.MaplePacket;
import java.util.ArrayList;

public class ServerPacket {

    // Encoder
    private ArrayList<Byte> packet = new ArrayList<>();
    private int encoded = 0;

    public ServerPacket(ServerPacketHeader header) {
        short w = (short) header.get();

        packet.add((byte) (w & 0xFF));
        if (Content.PacketHeaderSize.getInt() == 2) {
            packet.add((byte) ((w >> 8) & 0xFF));
        }
        encoded += Content.PacketHeaderSize.getInt();
    }

    public ServerPacket(short w) {
        packet.add((byte) (w & 0xFF));
        packet.add((byte) ((w >> 8) & 0xFF));
        encoded += 2;
    }

    public boolean setHello() {
        if (encoded < 2) {
            return false;
        }
        int data_size = encoded - 2;
        packet.set(0, (byte) (data_size & 0xFF));
        packet.set(1, (byte) ((data_size >> 8) & 0xFF));
        return true;
    }

    public void Encode1(byte b) {
        packet.add(b);
        encoded += 1;
    }

    public ServerPacket() {
        // データ構造用
    }

    public void Encode2(short w) {
        Encode1((byte) (w & 0xFF));
        Encode1((byte) ((w >> 8) & 0xFF));
    }

    public void Encode4(int dw) {
        Encode2((short) (dw & 0xFFFF));
        Encode2((short) ((dw >> 16) & 0xFFFF));
    }

    public void Encode8(long qw) {
        Encode4((int) (qw & 0xFFFFFFFF));
        Encode4((int) ((qw >> 32) & 0xFFFFFFFF));
    }

    public void EncodeDouble(double d) {
        Encode8(Double.doubleToLongBits(d));
    }

    public void EncodeStr(String str) {
        byte[] b = str.getBytes(CodePage.getCodePage());
        Encode2((short) b.length);

        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
    }

    public void EncodeBuffer(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
    }

    public void EncodeBuffer(String str, int size) {
        byte[] b = str.getBytes(CodePage.getCodePage());
        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
        for (int i = 0; i < (size - b.length); i++) {
            Encode1(0);
        }
    }

    public void EncodeBuffer(byte[] b, int size) {
        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
        for (int i = 0; i < (size - b.length); i++) {
            Encode1(0);
        }
    }

    public static ServerPacketHeader ToHeader(short w) {
        for (final ServerPacketHeader h : ServerPacketHeader.values()) {
            if (h.get() == w) {
                return h;
            }
        }

        return ServerPacketHeader.UNKNOWN;
    }

    public String Packet() {
        byte[] b = new byte[encoded];
        for (int i = 0; i < encoded; i++) {
            b[i] = packet.get(i);
        }

        String text = null;

        if (Content.PacketHeaderSize.getInt() == 2) {
            short header = (short) (((short) b[0] & 0xFF) | ((short) b[1] & 0xFF << 8));
            text = String.format("@%04X", header);
        } else {
            text = String.format("@%02X", b[0]);
        }

        for (int i = Content.PacketHeaderSize.getInt(); i < encoded; i++) {
            text += String.format(" %02X", b[i]);
        }

        return text;
    }

    public String getOpcodeName() {
        if (encoded < Content.PacketHeaderSize.getInt()) {
            return ServerPacketHeader.UNKNOWN.toString();
        }

        short header = (short) (((short) packet.get(0) & 0xFF) | ((short) packet.get(1) & 0xFF << 8));
        return ToHeader(header).toString();
    }

    public MaplePacket get() {
        byte[] b = new byte[encoded];
        for (int i = 0; i < encoded; i++) {
            b[i] = packet.get(i);
        }
        return new ByteArrayMaplePacket(b);
    }

    public void Encode1(int b) {
        packet.add((byte) b);
        encoded += 1;
    }

    public void Encode2(int w) {
        Encode1((byte) ((short) w & 0xFF));
        Encode1((byte) (((short) w >> 8) & 0xFF));
    }

    public void EncodeZeroBytes(int length) {
        for (int i = 0; i < length; i++) {
            Encode1(0);
        }
    }

}
