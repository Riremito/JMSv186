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
import tacos.debug.DebugLogger;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import tacos.config.Config;

/**
 *
 * @author Riremito
 */
public class PacketDecoder extends CumulativeProtocolDecoder {

    private static final int DEC_HEADER_SIZE = 4;

    private boolean decrypt(CAESCipher cipher, byte[] packet) {
        // CInPacket::DecryptData
        if (Content.KMSEncryption.get()) {
            CIGCipher.innoDecrypt(packet, packet, packet.length, cipher.getIv().clone());
        } else {
            // AES
            cipher.CInPacket_DecryptData(packet, packet, packet.length, cipher.getIv().clone());
            // Shanda
            if (Content.EncryptedByShanda.get()) {
                CIOBufferManipulator._De(packet);
            }
        }
        // IV
        byte[] iv = CIGCipher.innoHash(cipher.getIv(), null);
        cipher.setIv(iv);
        return true;
    }

    @Override
    protected boolean doDecode(IoSession is, ByteBuffer bb, ProtocolDecoderOutput pdo) throws Exception {
        CAESCipher cipher = (CAESCipher) is.getAttribute(CAESCipher.AES_DEC_KEY);

        if (bb.remaining() < DEC_HEADER_SIZE) {
            return false;
        }

        // rollback position.
        bb.mark();
        bb.order(ByteOrder.LITTLE_ENDIAN);

        short m_uRawSeq = bb.getShort();
        short m_uDataLen = (short) (bb.getShort() ^ m_uRawSeq);
        short uSeqKey = (short) (((cipher.getIv()[3] << 8) & 0xFF00) | (cipher.getIv()[2] & 0x00FF));

        if ((short) (uSeqKey ^ m_uRawSeq) != Config.VERSION) {
            is.close();
            DebugLogger.ErrorLog("doDecode : version.");
            return false;
        }

        if (bb.remaining() < m_uDataLen) {
            // rollback.
            bb.reset();
            return false;
        }

        byte[] packet = new byte[m_uDataLen];

        bb.get(packet, 0, m_uDataLen);
        decrypt(cipher, packet);
        pdo.write(packet);
        return true;
    }
}
