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

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class MaplePacketEncoder implements ProtocolEncoder {

    @Override
    public void encode(final IoSession session, final Object message, final ProtocolEncoderOutput out) throws Exception {
        final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

        // raw packet
        if (client == null) {
            out.write(ByteBuffer.wrap(((MaplePacket) message).getBytes()));
            return;
        }

        // packet encryption
        final byte[] raw_server_packet = ((MaplePacket) message).getBytes();
        final byte[] header = client.getSendCrypto().getPacketHeader(raw_server_packet.length); // 4 bytes
        final byte[] packet = raw_server_packet.clone();

        if (!ClientEdit.PacketEncryptionRemoved.get()) {
            if (Region.check(Region.KMS) || Region.check(Region.KMST) || Region.check(Region.IMS)) {
                client.getSendCrypto().kms_encrypt(packet);
            } else {
                if (Content.CustomEncryption.get()) {
                    MapleCustomEncryption.encryptData(packet);
                }
                client.getSendCrypto().crypt(packet);
                client.getSendCrypto().updateIv();
            }
        }

        final byte[] encrypted_server_packet = new byte[header.length + packet.length];
        System.arraycopy(header, 0, encrypted_server_packet, 0, header.length);
        System.arraycopy(packet, 0, encrypted_server_packet, header.length, packet.length);

        out.write(ByteBuffer.wrap(encrypted_server_packet));
        return;
    }

    @Override
    public void dispose(IoSession session) throws Exception {
        // nothing to do
    }
}
