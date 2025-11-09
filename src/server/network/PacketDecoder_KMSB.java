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
import debug.DebugLogger;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 *
 * @author Riremito
 */
public class PacketDecoder_KMSB extends CumulativeProtocolDecoder {

    @Override
    protected boolean doDecode(IoSession is, ByteBuffer bb, ProtocolDecoderOutput pdo) throws Exception {
        MapleAESOFB aes_dec = (MapleAESOFB) is.getAttribute(MapleAESOFB.AES_DEC_KEY);
        byte key[] = aes_dec.getIv();
        // header check
        bb.mark(); // rollback position

        int buffer_size = bb.remaining();
        if (buffer_size < 2) {
            DebugLogger.ErrorLog("doDecode_KMSB size error 1");
            return false;
        }
        int header_version = ((byte) (bb.get() ^ key[2] & 0xFF) | (((byte) (bb.get() ^ key[3])) << 8) & 0xFF00) & 0xFFFF;
        if (Version.getVersion() != header_version) {
            DebugLogger.ErrorLog("doDecode_KMSB dc.");
            is.close();
            return false;
        }

        buffer_size = bb.remaining();
        if (buffer_size < 2) {
            DebugLogger.ErrorLog("doDecode_KMSB size error 2");
            bb.reset();
            return false;
        }
        int required_size = (bb.get() & 0xFF) | (bb.get() << 8) & 0xFF00;
        buffer_size = bb.remaining();

        if (buffer_size < required_size) {
            DebugLogger.ErrorLog("KMSB size ( " + buffer_size + " / " + required_size + " )");
            bb.reset();
            return false;
        }

        byte packet[] = new byte[required_size];
        bb.get(packet, 0, required_size); // +required_size

        for (int i = 0; i < packet.length; i++) {
            int v = (byte) (packet[i] ^ key[0]) & 0xFF;
            packet[i] = (byte) ((v << 4) & 0xF0 | (v >> 4) & 0x0F);
        }

        pdo.write(packet);
        // warning
        if (required_size < buffer_size) {
            DebugLogger.InfoLog("KMSB size ( " + buffer_size + " / " + required_size + " )");
        }

        int seed = (int) ((key[0] & 0xFF) | (key[1] << 8 & 0xFF00) | (key[2] << 16 & 0xFF0000) | (key[3] << 24 & 0xFF000000) & 0xFFFFFFFF);
        int next_key = 214013 * seed + 2531011;
        key[0] = (byte) (next_key & 0xFF);
        key[1] = (byte) ((next_key >> 8) & 0xFF);
        key[2] = (byte) ((next_key >> 16) & 0xFF);
        key[3] = (byte) ((next_key >> 24) & 0xFF);
        aes_dec.setIv(key);
        return true;
    }
}
