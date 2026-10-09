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
package tacos.network;

import java.nio.ByteOrder;
import tacos.config.Content;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import tacos.client.TacosClient;
import tacos.config.Config;
import tacos.packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class PacketEncoder implements ProtocolEncoder {

    private static final int ENC_HEADER_SIZE = 4;

    private boolean encrypt(TacosClient client, byte[] packet) {
        // COutPacket::MakeBufferList
        if (Content.KMSEncryption.get()) {
            CIGCipher.innoEncrypt(packet, packet, packet.length, client.getSeqRcv().clone());
        } else {
            // Shanda
            if (Content.EncryptedByShanda.get()) {
                CIOBufferManipulator._En(packet);
            }
            // AES
            CAESCipher.CryptData(packet, packet, packet.length, client.getSeqRcv().clone());
        }
        // IV
        byte[] iv_new = CIGCipher.innoHash(client.getSeqRcv(), null);
        client.setSeqRcv(iv_new);
        return true;
    }

    @Override
    public void encode(IoSession is, Object o, ProtocolEncoderOutput peo) throws Exception {
        TacosClient client = (TacosClient) is.getAttribute(TacosClient.CLIENT_KEY);

        byte[] packet = ((ServerPacket) o).getBytes().clone();
        // raw packet
        if (client == null) {
            peo.write(ByteBuffer.wrap(packet));
            return;
        }

        byte[] iv = client.getSeqRcv();

        // packet encryption
        short m_uDataLen = (short) packet.length;
        short uSeqKey = (short) (((iv[3] << 8) & 0xFF00) | (iv[2] & 0x00FF));
        short uSeqBase = (short) (0xFFFF - (short) Config.VERSION);
        short uRawSeq = (short) (uSeqKey ^ uSeqBase);
        short m_uOffset = (short) (uRawSeq ^ m_uDataLen);

        encrypt(client, packet);
        ByteBuffer enc_packet = ByteBuffer.allocate(ENC_HEADER_SIZE + m_uDataLen);
        enc_packet.order(ByteOrder.LITTLE_ENDIAN);
        enc_packet.putShort(uRawSeq);
        enc_packet.putShort(m_uOffset);
        enc_packet.put(packet);
        enc_packet.flip();
        peo.write(enc_packet);
    }

    @Override
    public void dispose(IoSession is) throws Exception {

    }
}
