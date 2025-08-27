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

import config.ClientEdit;
import config.Content;
import config.Region;
import config.Version;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class MaplePacketEncoder implements ProtocolEncoder {

    public void encode_KMSB(final IoSession session, final Object message, final ProtocolEncoderOutput out) throws Exception {
        MapleAESOFB aes_enc = (MapleAESOFB) session.getAttribute(MapleAESOFB.AES_ENC_KEY);

        // raw packet
        if (aes_enc == null) {
            out.write(ByteBuffer.wrap(((MaplePacket) message).getBytes()));
            return;
        }

        // packet encryption
        final byte[] raw_server_packet = ((MaplePacket) message).getBytes();
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

        out.write(ByteBuffer.wrap(encrypted_server_packet));

        int seed = (int) ((key[0] & 0xFF) | (key[1] << 8 & 0xFF00) | (key[2] << 16 & 0xFF0000) | (key[3] << 24 & 0xFF000000)) & 0xFFFFFFFF;
        int next_key = 214013 * seed + 2531011;
        key[0] = (byte) (next_key & 0xFF);
        key[1] = (byte) ((next_key >> 8) & 0xFF);
        key[2] = (byte) ((next_key >> 16) & 0xFF);
        key[3] = (byte) ((next_key >> 24) & 0xFF);
        aes_enc.setIv(key);
        return;
    }

    @Override
    public void encode(final IoSession session, final Object message, final ProtocolEncoderOutput out) throws Exception {
        if (Region.check(Region.KMSB)) {
            encode_KMSB(session, message, out);
            return;
        }

        MapleAESOFB aes_enc = (MapleAESOFB) session.getAttribute(MapleAESOFB.AES_ENC_KEY);

        // raw packet
        if (aes_enc == null) {
            out.write(ByteBuffer.wrap(((MaplePacket) message).getBytes()));
            return;
        }

        // packet encryption
        final byte[] raw_server_packet = ((MaplePacket) message).getBytes();
        final byte[] header = aes_enc.getPacketHeader(raw_server_packet.length); // 4 bytes
        final byte[] packet = raw_server_packet.clone();

        if (!ClientEdit.PacketEncryptionRemoved.get()) {
            if (Region.check(Region.KMS) || Region.check(Region.KMST) || Region.check(Region.IMS)) {
                aes_enc.kms_encrypt(packet);
            } else {
                if (Content.CustomEncryption.get()) {
                    MapleCustomEncryption.encryptData(packet);
                }
                aes_enc.crypt(packet);
                aes_enc.updateIv();
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
