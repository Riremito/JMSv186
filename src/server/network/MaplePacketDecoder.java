/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.network;

import client.MapleClient;
import config.ClientEdit;
import config.Content;
import config.Region;
import config.Version;
import debug.Debug;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class MaplePacketDecoder extends CumulativeProtocolDecoder {

    protected boolean doDecode_KMSB(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) throws Exception {
        final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
        byte key[] = client.getReceiveCrypto().getIv();
        // header check
        in.mark(); // rollback position

        int buffer_size = in.remaining();
        if (buffer_size < 2) {
            Debug.ErrorLog("doDecode_KMSB size error 1");
            return false;
        }
        int header_version = ((in.get() ^ key[2] & 0xFF) | ((in.get() ^ key[3]) << 8) & 0xFF00) & 0xFFFF;
        if (Version.getVersion() != header_version) {
            session.close();
            Debug.ErrorLog("doDecode_KMSB header error : " + String.format("%04X", header_version));
            return false;
        }

        buffer_size = in.remaining();
        if (buffer_size < 2) {
            Debug.ErrorLog("doDecode_KMSB size error 2");
            in.reset();
            return false;
        }
        int required_size = (in.get() & 0xFF) | (in.get() << 8) & 0xFF00;
        buffer_size = in.remaining();

        if (buffer_size < required_size) {
            Debug.ErrorLog("KMSB size ( " + buffer_size + " / " + required_size + " )");
            in.reset();
            return false;
        }

        byte packet[] = new byte[required_size];
        in.get(packet, 0, required_size); // +required_size

        for (int i = 0; i < packet.length; i++) {
            int v = (byte) (packet[i] ^ key[0]) & 0xFF;
            packet[i] = (byte) ((v << 4) & 0xF0 | (v >> 4) & 0x0F);
        }

        out.write(packet);
        // warning
        if (required_size < buffer_size) {
            Debug.InfoLog("KMSB size ( " + buffer_size + " / " + required_size + " )");
        }
        return true;
    }

    @Override
    protected boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) throws Exception {

        if (Region.check(Region.KMSB)) {
            return doDecode_KMSB(session, in, out);
        }

        final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

        // header check
        in.mark(); // rollback position

        int buffer_size = in.remaining();
        if (buffer_size < 4) {
            Debug.ErrorLog("doDecode size error");
            return false;
        }

        int header_data = in.getInt(); // +4
        if (client.getReceiveCrypto().checkPacket(header_data)) {
            int required_size = MapleAESOFB.getPacketLength(header_data);
            buffer_size = in.remaining();

            if (required_size <= buffer_size) {
                byte decryptedPacket[] = new byte[required_size];
                in.get(decryptedPacket, 0, required_size); // +required_size
                if (!ClientEdit.PacketEncryptionRemoved.get()) {
                    if (Region.check(Region.KMS) || Region.check(Region.KMST) || Region.check(Region.IMS)) {
                        client.getReceiveCrypto().kms_decrypt(decryptedPacket);
                    } else {
                        client.getReceiveCrypto().crypt(decryptedPacket);
                        if (Content.CustomEncryption.get()) {
                            MapleCustomEncryption.decryptData(decryptedPacket);
                        }
                        client.getReceiveCrypto().updateIv();
                    }
                }
                out.write(decryptedPacket);
                // warning
                if (required_size < buffer_size) {
                    //Debug.InfoLog("doDecode size ( " + buffer_size + " / " + required_size + " )");
                }
                return true;
            }
            // reset
            //Debug.ErrorLog("doDecode size ( " + buffer_size + " / " + required_size + " )");
            in.reset(); // rollback because client still does not send full size of packet buffer.
            return false;
        }

        session.close();
        Debug.ErrorLog("doDecode header error");
        return false;
    }
}
