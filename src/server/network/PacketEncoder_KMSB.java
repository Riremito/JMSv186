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
package server.network;

import config.Version;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 *
 * @author Riremito
 */
public class PacketEncoder_KMSB implements ProtocolEncoder {

    @Override
    public void encode(IoSession is, Object o, ProtocolEncoderOutput peo) throws Exception {
        MapleAESOFB aes_enc = (MapleAESOFB) is.getAttribute(MapleAESOFB.AES_ENC_KEY);

        // raw packet
        if (aes_enc == null) {
            peo.write(ByteBuffer.wrap(((MaplePacket) o).getBytes()));
            return;
        }

        // packet encryption
        final byte[] raw_server_packet = ((MaplePacket) o).getBytes();
        final byte[] header_version = new byte[2];
        final byte[] header_size = new byte[2];
        final byte[] packet = raw_server_packet.clone();
        byte key[] = aes_enc.getIv();
        short version = (short) (0xFFFF - Version.getVersion());

        header_version[0] = (byte) (version & 0xFF);
        header_version[0] = (byte) (header_version[0] ^ key[2]);
        header_version[1] = (byte) ((version >> 8) & 0xFF);
        header_version[1] = (byte) (header_version[1] ^ key[3]);

        header_size[0] = (byte) (packet.length & 0xFF);
        header_size[1] = (byte) ((packet.length >> 8) & 0xFF);

        for (int i = 0; i < packet.length; i++) {
            packet[i] = (byte) ((packet[i] << 4) & 0xF0 | (packet[i] >> 4) & 0x0F);
            packet[i] = (byte) (packet[i] ^ key[0]);
        }

        final byte[] encrypted_server_packet = new byte[header_version.length + header_size.length + packet.length];
        System.arraycopy(header_version, 0, encrypted_server_packet, 0, header_version.length);
        System.arraycopy(header_size, 0, encrypted_server_packet, header_version.length, header_size.length);
        System.arraycopy(packet, 0, encrypted_server_packet, header_version.length + header_size.length, packet.length);

        peo.write(ByteBuffer.wrap(encrypted_server_packet));

        int seed = (int) ((key[0] & 0xFF) | (key[1] << 8 & 0xFF00) | (key[2] << 16 & 0xFF0000) | (key[3] << 24 & 0xFF000000)) & 0xFFFFFFFF;
        int next_key = 214013 * seed + 2531011;
        key[0] = (byte) (next_key & 0xFF);
        key[1] = (byte) ((next_key >> 8) & 0xFF);
        key[2] = (byte) ((next_key >> 16) & 0xFF);
        key[3] = (byte) ((next_key >> 24) & 0xFF);
        aes_enc.setIv(key);
    }

    @Override
    public void dispose(IoSession is) throws Exception {

    }
}
