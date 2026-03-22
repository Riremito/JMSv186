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

/**
 *
 * @author Riremito
 */
public class ServerPacket {

    private ArrayList<Byte> packet = new ArrayList<>();
    private int encoded = 0;

    public ServerPacket(ServerPacketHeader header) {
        short header_value = (short) header.get();

        this.packet.add((byte) (header_value & 0xFF));
        this.encoded += 1;

        if (Content.PacketHeaderSize.getInt() == 2) {
            this.packet.add((byte) ((header_value >> 8) & 0xFF));
            this.encoded += 1;
        }
    }

    // for hello packet.
    public ServerPacket(short header_value) {
        this.packet.add((byte) (header_value & 0xFF));
        this.packet.add((byte) ((header_value >> 8) & 0xFF));
        this.encoded += 2;
    }

    // for data.
    public ServerPacket() {
        // do nothing.
    }

    // make hello packet buffer.
    public boolean setHello() {
        if (this.encoded < 2) {
            return false;
        }

        int data_size = this.encoded - 2;
        this.packet.set(0, (byte) (data_size & 0xFF));
        this.packet.set(1, (byte) ((data_size >> 8) & 0xFF));
        return true;
    }

    // TODO : fix return value to byte[]
    public MaplePacket get() {
        byte[] b = new byte[this.encoded];
        for (int i = 0; i < this.encoded; i++) {
            b[i] = this.packet.get(i);
        }

        return new ByteArrayMaplePacket(b);
    }

    public String getString() {
        String text = null;
        if (Content.PacketHeaderSize.getInt() == 2) {
            short header = (short) (((short) this.packet.get(0) & 0xFF) | ((short) this.packet.get(1) & 0xFF << 8));
            text = String.format("@%04X", header);
        } else {
            text = String.format("@%02X", this.packet.get(0));
        }

        for (int i = Content.PacketHeaderSize.getInt(); i < this.encoded; i++) {
            text += String.format(" %02X", this.packet.get(i));
        }

        return text;
    }

    /*
        COutPacket class functions.
     */
    public void Encode1(byte b) {
        this.packet.add(b);
        this.encoded += 1;
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

    public void EncodeBuffer(byte[] b, int size) {
        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
        for (int i = 0; i < (size - b.length); i++) {
            Encode1(0);
        }
    }

    /*
        supports other type input.
     */
    public void Encode1(int b) {
        this.packet.add((byte) b);
        this.encoded += 1;
    }

    public void Encode2(int w) {
        Encode1((byte) ((short) w & 0xFF));
        Encode1((byte) (((short) w >> 8) & 0xFF));
    }

    // encode all bytes.
    public void EncodeBuffer(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
    }

    // encode fixed size string.
    public void EncodeBuffer(String str, int size) {
        byte[] b = str.getBytes(CodePage.getCodePage());
        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
        for (int i = 0; i < (size - b.length); i++) {
            Encode1(0);
        }
    }

    /*
        functions below are not good for source code.
     */
    // padding.
    public void EncodeZeroBytes(int length) {
        for (int i = 0; i < length; i++) {
            Encode1(0);
        }
    }

}
