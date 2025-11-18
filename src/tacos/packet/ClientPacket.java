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

    private byte[] packet;
    private int decoded;

    // MapleのInPacketのDecodeのように送信されたパケットを再度Decodeする
    public ClientPacket(byte[] b) {
        packet = b;
        decoded = 0;
    }

    public int getSize() {
        return packet.length;
    }

    public static ClientPacketHeader ToHeader(short w) {
        for (final ClientPacketHeader h : ClientPacketHeader.values()) {
            if (h.get() == w) {
                return h;
            }
        }

        return ClientPacketHeader.UNKNOWN;
    }

    public String get() {
        String text = null;
        if (Content.PacketHeaderSize.getInt() == 2) {
            short header = (short) (((int) packet[0] & 0xFF) | ((int) (packet[1] & 0xFF) << 8));
            text = String.format("@%04X", header);
        } else {
            text = String.format("@%02X", packet[0]);
        }

        for (int i = Content.PacketHeaderSize.getInt(); i < packet.length; i++) {
            text += String.format(" %02X", packet[i]);
        }

        return text;
    }

    public String GetOpcodeName() {
        if (packet.length < Content.PacketHeaderSize.getInt()) {
            return ClientPacketHeader.UNKNOWN.toString();
        }

        if (Content.PacketHeaderSize.getInt() == 2) {
            short header = (short) (((int) packet[0] & 0xFF) | ((int) (packet[1] & 0xFF) << 8));
            return ToHeader(header).toString();
        }

        return ToHeader((short) (packet[0] & 0xFF)).toString();
    }

    public ClientPacketHeader GetOpcode() {
        if (packet.length < Content.PacketHeaderSize.getInt()) {
            return ClientPacketHeader.UNKNOWN;
        }

        if (Content.PacketHeaderSize.getInt() == 2) {
            short header = (short) (((int) packet[0] & 0xFF) | ((int) (packet[1] & 0xFF) << 8));
            return ToHeader(header);
        }

        return ToHeader((short) (packet[0] & 0xFF));
    }

    public byte Decode1() {
        return (byte) packet[decoded++];
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

    public byte[] DecodeBuffer() {
        int length = Decode2();
        byte[] buffer = new byte[length];

        for (int i = 0; i < length; i++) {
            buffer[i] = Decode1();
        }

        return buffer;
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

    public byte[] DecodeAll() {
        int length = packet.length - decoded;
        byte[] buffer = new byte[length];

        for (int i = 0; i < length; i++) {
            buffer[i] = Decode1();
        }

        return buffer;
    }

    public boolean setBackCursor(int cur) {
        if (getRemainingSize() + cur < 0) {
            return false;
        }
        this.decoded = packet.length + cur;
        return true;
    }

    public int getRemainingSize() {
        return packet.length - decoded;
    }

}
