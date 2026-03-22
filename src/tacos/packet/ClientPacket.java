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

/**
 *
 * @author Riremito
 */
public class ClientPacket {

    private byte[] packet = null;
    private int decoded = 0;
    private ClientPacketHeader header = ClientPacketHeader.UNKNOWN;

    public ClientPacket(byte[] packet) {
        this.packet = packet;
        this.decoded = 0;
    }

    // check packet size.
    public boolean check() {
        if (this.packet.length < Content.PacketHeaderSize.getInt()) {
            return false;
        }
        return true;
    }

    // get decoded header.
    public ClientPacketHeader getHeader() {
        return this.header;
    }

    // decode header.
    public ClientPacketHeader DecodeHeader() {
        if (this.decoded != 0) {
            // warning.
            return this.header;
        }

        short header_value = (Content.PacketHeaderSize.getInt() == 2) ? Decode2() : Decode1();
        this.header = ClientPacketHeader.find(header_value);

        return this.header;
    }

    // get packet buffer string.
    public String getString() {
        String text = null;
        if (Content.PacketHeaderSize.getInt() == 2) {
            short header = (short) (((int) this.packet[0] & 0xFF) | ((int) (this.packet[1] & 0xFF) << 8));
            text = String.format("@%04X", header);
        } else {
            text = String.format("@%02X", this.packet[0]);
        }

        for (int i = Content.PacketHeaderSize.getInt(); i < this.packet.length; i++) {
            text += String.format(" %02X", this.packet[i]);
        }

        return text;
    }

    /*
        CInPacket class functions.
     */
    public byte Decode1() {
        return (byte) this.packet[this.decoded++];
    }

    public short Decode2() {
        return (short) (((short) Decode1() & 0xFF) | (((short) Decode1() & 0xFF) << 8));
    }

    public int Decode4() {
        return (int) (((int) Decode2() & 0xFFFF) | (((int) Decode2() & 0xFFFF) << 16));
    }

    public long Decode8() {
        return (long) (((long) Decode4() & 0xFFFFFFFF) | (((long) Decode4() & 0xFFFFFFFF) << 32));
    }

    public byte[] DecodeBuffer(int size) {
        byte[] buffer = new byte[size];

        for (int i = 0; i < size; i++) {
            buffer[i] = Decode1();
        }

        return buffer;
    }

    public String DecodeStr() {
        int length = Decode2();
        byte[] buffer = new byte[length];

        for (int i = 0; i < length; i++) {
            buffer[i] = Decode1();
        }

        return new String(buffer, CodePage.getCodePage());
    }

    /*
        functions below are not good for source code.
     */
    // decode all remaining data.
    public byte[] DecodeAll() {
        int length = this.packet.length - this.decoded;
        byte[] buffer = new byte[length];

        for (int i = 0; i < length; i++) {
            buffer[i] = Decode1();
        }

        return buffer;
    }

    // read data from the end.
    public boolean setBackCursor(int cur) {
        if (getRemainingSize() + cur < 0) {
            return false;
        }
        this.decoded = this.packet.length + cur;
        return true;
    }

    // get remaining data size.
    public int getRemainingSize() {
        return packet.length - this.decoded;
    }

}
