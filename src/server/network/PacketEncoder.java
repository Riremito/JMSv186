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

import config.ClientEdit;
import config.Content;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 *
 * @author Riremito
 */
public class PacketEncoder implements ProtocolEncoder {

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
        final byte[] header = aes_enc.getPacketHeader(raw_server_packet.length); // 4 bytes
        final byte[] packet = raw_server_packet.clone();

        if (!ClientEdit.PacketEncryptionRemoved.get()) {
            if (Content.CustomEncryption.get()) {
                MapleCustomEncryption.encryptData(packet);
            }
            aes_enc.crypt(packet);
            aes_enc.updateIv();
        }

        final byte[] encrypted_server_packet = new byte[header.length + packet.length];
        System.arraycopy(header, 0, encrypted_server_packet, 0, header.length);
        System.arraycopy(packet, 0, encrypted_server_packet, header.length, packet.length);

        peo.write(ByteBuffer.wrap(encrypted_server_packet));
    }

    @Override
    public void dispose(IoSession is) throws Exception {

    }
}
