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
import config.Region;
import debug.DebugLogger;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 *
 * @author Riremito
 */
public class PacketDecoder extends CumulativeProtocolDecoder {

    @Override
    protected boolean doDecode(IoSession is, ByteBuffer bb, ProtocolDecoderOutput pdo) throws Exception {
        MapleAESOFB aes_dec = (MapleAESOFB) is.getAttribute(MapleAESOFB.AES_DEC_KEY);

        // header check
        bb.mark(); // rollback position

        int buffer_size = bb.remaining();
        if (buffer_size < 4) {
            DebugLogger.ErrorLog("doDecode size error");
            return false;
        }

        int header_data = bb.getInt(); // +4
        if (aes_dec.checkPacket(header_data)) {
            int required_size = MapleAESOFB.getPacketLength(header_data);
            buffer_size = bb.remaining();

            if (required_size <= buffer_size) {
                byte decryptedPacket[] = new byte[required_size];
                bb.get(decryptedPacket, 0, required_size); // +required_size
                if (!ClientEdit.PacketEncryptionRemoved.get()) {
                    if (Region.check(Region.KMS) || Region.check(Region.KMST) || Region.check(Region.IMS)) {
                        aes_dec.kms_decrypt(decryptedPacket);
                    } else {
                        aes_dec.crypt(decryptedPacket);
                        if (Content.CustomEncryption.get()) {
                            MapleCustomEncryption.decryptData(decryptedPacket);
                        }
                        aes_dec.updateIv();
                    }
                }
                pdo.write(decryptedPacket);
                // warning
                if (required_size < buffer_size) {
                    //Debug.InfoLog("doDecode size ( " + buffer_size + " / " + required_size + " )");
                }
                return true;
            }
            // reset
            //Debug.ErrorLog("doDecode size ( " + buffer_size + " / " + required_size + " )");
            bb.reset(); // rollback because client still does not send full size of packet buffer.
            return false;
        }

        DebugLogger.ErrorLog("doDecode dc.");
        is.close();
        return false;
    }
}
