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

import tacos.config.Content;
import java.util.ArrayList;
import tacos.config.Config;

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

    public byte[] getBytes() {
        byte[] b = new byte[this.encoded];
        for (int i = 0; i < this.encoded; i++) {
            b[i] = this.packet.get(i);
        }

        return b;
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
    public boolean Encode1(byte b) {
        this.packet.add(b);
        this.encoded += 1;
        return true;
    }

    public boolean Encode2(short w) {
        Encode1((byte) (w & 0xFF));
        Encode1((byte) ((w >> 8) & 0xFF));
        return true;
    }

    public boolean Encode4(int dw) {
        Encode2((short) (dw & 0xFFFF));
        Encode2((short) ((dw >> 16) & 0xFFFF));
        return true;
    }

    public boolean Encode8(long qw) {
        Encode4((int) (qw & 0xFFFFFFFF));
        Encode4((int) ((qw >> 32) & 0xFFFFFFFF));
        return true;
    }

    public boolean EncodeDouble(double d) {
        Encode8(Double.doubleToLongBits(d));
        return true;
    }

    public boolean EncodeStr(String str) {
        byte[] b = str.getBytes(Config.CODEPAGE);
        Encode2((short) b.length);

        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
        return true;
    }

    public boolean EncodeBuffer(byte[] b, int size) {
        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
        for (int i = 0; i < (size - b.length); i++) {
            Encode1(0);
        }
        return true;
    }

    /*
        supports other type input.
     */
    public boolean Encode1(int b) {
        this.packet.add((byte) b);
        this.encoded += 1;
        return true;
    }

    public boolean Encode2(int w) {
        Encode1((byte) ((short) w & 0xFF));
        Encode1((byte) (((short) w >> 8) & 0xFF));
        return true;
    }

    // encode all bytes.
    public boolean EncodeBuffer(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
        return true;
    }

    // encode fixed size string.
    public boolean EncodeBuffer(String str, int size) {
        byte[] b = str.getBytes(Config.CODEPAGE);
        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
        for (int i = 0; i < (size - b.length); i++) {
            Encode1(0);
        }
        return true;
    }

    /*
        functions below are not good for source code.
     */
    public boolean EncodeZeroBytes(int length) {
        for (int i = 0; i < length; i++) {
            Encode1(0);
        }
        return true;
    }

    /*
        conditional
     */
    public boolean Encode1(byte b, boolean cond) {
        if (!cond) {
            return false;
        }
        return Encode1(b);
    }

    public boolean Encode2(short w, boolean cond) {
        if (!cond) {
            return false;
        }
        return Encode2(w);
    }

    public boolean Encode4(int dw, boolean cond) {
        if (!cond) {
            return false;
        }
        return Encode4(dw);
    }

    public boolean Encode8(long qw, boolean cond) {
        if (!cond) {
            return false;
        }
        return Encode8(qw);
    }

    public boolean EncodeDouble(double d, boolean cond) {
        if (!cond) {
            return false;
        }
        return EncodeDouble(d);
    }

    public boolean EncodeStr(String str, boolean cond) {
        if (!cond) {
            return false;
        }
        return EncodeStr(str);
    }

    public boolean EncodeBuffer(byte[] b, int size, boolean cond) {
        if (!cond) {
            return false;
        }
        return EncodeBuffer(b, size);
    }

    public boolean Encode1(int b, boolean cond) {
        if (!cond) {
            return false;
        }
        return Encode1(b);
    }

    public boolean Encode2(int w, boolean cond) {
        if (!cond) {
            return false;
        }
        return Encode2(w);
    }

    public boolean EncodeBuffer(byte[] b, boolean cond) {
        if (!cond) {
            return false;
        }
        return EncodeBuffer(b);
    }

    public boolean EncodeBuffer(String str, int size, boolean cond) {
        if (!cond) {
            return false;
        }
        return EncodeBuffer(str, size);
    }

    public boolean EncodeZeroBytes(int length, boolean cond) {
        if (!cond) {
            return false;
        }
        return EncodeZeroBytes(length);
    }
}
