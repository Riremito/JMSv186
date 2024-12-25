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
package tools;

import config.ServerConfig;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 * Provides a class for encrypting MapleStory packets with AES OFB encryption.
 *
 * @author Frz
 * @version 1.0
 * @since Revision 320
 */
public class MapleAESOFB {

    private byte iv[];
    private Cipher cipher;
    private short mapleVersion;
    private boolean login; // x64
    private boolean isCP; // x64

    private final static SecretKeySpec skey = new SecretKeySpec(new byte[]{0x13, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, (byte) 0xB4, 0x00, 0x00, 0x00, 0x1B, 0x00, 0x00, 0x00, 0x0F, 0x00, 0x00, 0x00, 0x33, 0x00, 0x00, 0x00, 0x52, 0x00, 0x00, 0x00}, "AES");
    private final static SecretKeySpec skey_kms = new SecretKeySpec(new byte[]{0x15, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, (byte) 0xB4, 0x00, 0x00, 0x00, 0x1B, 0x00, 0x00, 0x00, 0x0F, 0x00, 0x00, 0x00, 0x33, 0x00, 0x00, 0x00, 0x52, 0x00, 0x00, 0x00}, "AES");

    private static final byte[] funnyBytes = new byte[]{(byte) 0xEC, (byte) 0x3F, (byte) 0x77, (byte) 0xA4, (byte) 0x45, (byte) 0xD0, (byte) 0x71, (byte) 0xBF, (byte) 0xB7, (byte) 0x98, (byte) 0x20, (byte) 0xFC,
        (byte) 0x4B, (byte) 0xE9, (byte) 0xB3, (byte) 0xE1, (byte) 0x5C, (byte) 0x22, (byte) 0xF7, (byte) 0x0C, (byte) 0x44, (byte) 0x1B, (byte) 0x81, (byte) 0xBD, (byte) 0x63, (byte) 0x8D, (byte) 0xD4, (byte) 0xC3,
        (byte) 0xF2, (byte) 0x10, (byte) 0x19, (byte) 0xE0, (byte) 0xFB, (byte) 0xA1, (byte) 0x6E, (byte) 0x66, (byte) 0xEA, (byte) 0xAE, (byte) 0xD6, (byte) 0xCE, (byte) 0x06, (byte) 0x18, (byte) 0x4E, (byte) 0xEB,
        (byte) 0x78, (byte) 0x95, (byte) 0xDB, (byte) 0xBA, (byte) 0xB6, (byte) 0x42, (byte) 0x7A, (byte) 0x2A, (byte) 0x83, (byte) 0x0B, (byte) 0x54, (byte) 0x67, (byte) 0x6D, (byte) 0xE8, (byte) 0x65, (byte) 0xE7,
        (byte) 0x2F, (byte) 0x07, (byte) 0xF3, (byte) 0xAA, (byte) 0x27, (byte) 0x7B, (byte) 0x85, (byte) 0xB0, (byte) 0x26, (byte) 0xFD, (byte) 0x8B, (byte) 0xA9, (byte) 0xFA, (byte) 0xBE, (byte) 0xA8, (byte) 0xD7,
        (byte) 0xCB, (byte) 0xCC, (byte) 0x92, (byte) 0xDA, (byte) 0xF9, (byte) 0x93, (byte) 0x60, (byte) 0x2D, (byte) 0xDD, (byte) 0xD2, (byte) 0xA2, (byte) 0x9B, (byte) 0x39, (byte) 0x5F, (byte) 0x82, (byte) 0x21,
        (byte) 0x4C, (byte) 0x69, (byte) 0xF8, (byte) 0x31, (byte) 0x87, (byte) 0xEE, (byte) 0x8E, (byte) 0xAD, (byte) 0x8C, (byte) 0x6A, (byte) 0xBC, (byte) 0xB5, (byte) 0x6B, (byte) 0x59, (byte) 0x13, (byte) 0xF1,
        (byte) 0x04, (byte) 0x00, (byte) 0xF6, (byte) 0x5A, (byte) 0x35, (byte) 0x79, (byte) 0x48, (byte) 0x8F, (byte) 0x15, (byte) 0xCD, (byte) 0x97, (byte) 0x57, (byte) 0x12, (byte) 0x3E, (byte) 0x37, (byte) 0xFF,
        (byte) 0x9D, (byte) 0x4F, (byte) 0x51, (byte) 0xF5, (byte) 0xA3, (byte) 0x70, (byte) 0xBB, (byte) 0x14, (byte) 0x75, (byte) 0xC2, (byte) 0xB8, (byte) 0x72, (byte) 0xC0, (byte) 0xED, (byte) 0x7D, (byte) 0x68,
        (byte) 0xC9, (byte) 0x2E, (byte) 0x0D, (byte) 0x62, (byte) 0x46, (byte) 0x17, (byte) 0x11, (byte) 0x4D, (byte) 0x6C, (byte) 0xC4, (byte) 0x7E, (byte) 0x53, (byte) 0xC1, (byte) 0x25, (byte) 0xC7, (byte) 0x9A,
        (byte) 0x1C, (byte) 0x88, (byte) 0x58, (byte) 0x2C, (byte) 0x89, (byte) 0xDC, (byte) 0x02, (byte) 0x64, (byte) 0x40, (byte) 0x01, (byte) 0x5D, (byte) 0x38, (byte) 0xA5, (byte) 0xE2, (byte) 0xAF, (byte) 0x55,
        (byte) 0xD5, (byte) 0xEF, (byte) 0x1A, (byte) 0x7C, (byte) 0xA7, (byte) 0x5B, (byte) 0xA6, (byte) 0x6F, (byte) 0x86, (byte) 0x9F, (byte) 0x73, (byte) 0xE6, (byte) 0x0A, (byte) 0xDE, (byte) 0x2B, (byte) 0x99,
        (byte) 0x4A, (byte) 0x47, (byte) 0x9C, (byte) 0xDF, (byte) 0x09, (byte) 0x76, (byte) 0x9E, (byte) 0x30, (byte) 0x0E, (byte) 0xE4, (byte) 0xB2, (byte) 0x94, (byte) 0xA0, (byte) 0x3B, (byte) 0x34, (byte) 0x1D,
        (byte) 0x28, (byte) 0x0F, (byte) 0x36, (byte) 0xE3, (byte) 0x23, (byte) 0xB4, (byte) 0x03, (byte) 0xD8, (byte) 0x90, (byte) 0xC8, (byte) 0x3C, (byte) 0xFE, (byte) 0x5E, (byte) 0x32, (byte) 0x24, (byte) 0x50,
        (byte) 0x1F, (byte) 0x3A, (byte) 0x43, (byte) 0x8A, (byte) 0x96, (byte) 0x41, (byte) 0x74, (byte) 0xAC, (byte) 0x52, (byte) 0x33, (byte) 0xF0, (byte) 0xD9, (byte) 0x29, (byte) 0x80, (byte) 0xB1, (byte) 0x16,
        (byte) 0xD3, (byte) 0xAB, (byte) 0x91, (byte) 0xB9, (byte) 0x84, (byte) 0x7F, (byte) 0x61, (byte) 0x1E, (byte) 0xCF, (byte) 0xC5, (byte) 0xD1, (byte) 0x56, (byte) 0x3D, (byte) 0xCA, (byte) 0xF4, (byte) 0x05,
        (byte) 0xC6, (byte) 0xE5, (byte) 0x08, (byte) 0x49};

    public MapleAESOFB(byte iv[], boolean isLogin, boolean isOutbound) {
        // 暗号化
        if (ServerConfig.PacketEncryptionEnabled()) {
            try {
                cipher = Cipher.getInstance("AES");
                if (!ServerConfig.IsKMS()) {
                    cipher.init(Cipher.ENCRYPT_MODE, skey);
                }
                // Thank you for reading code!
                //cipher.init(Cipher.ENCRYPT_MODE, skey_x64);
            } catch (NoSuchAlgorithmException e) {
                System.err.println("ERROR" + e);
            } catch (NoSuchPaddingException e) {
                System.err.println("ERROR" + e);
            } catch (InvalidKeyException e) {
                System.err.println("Error initalizing the encryption cipher.  Make sure you're using the Unlimited Strength cryptography jar files.");
            }
        }

        this.login = isLogin; // LoginServer
        this.isCP = isOutbound; // ClientPacket
        this.setIv(iv);

        short vesrion = isOutbound ? (short) (0xFFFF - (short) ServerConfig.GetVersion()) : (short) ServerConfig.GetVersion();
        this.mapleVersion = (short) (((vesrion >> 8) & 0xFF) | ((vesrion << 8) & 0xFF00));
    }

    private void setIv(byte[] iv) {
        this.iv = iv;
    }

    public byte[] crypt(byte[] data) {
        /*
        if (ServerConfig.IsJMS() && 414 <= ServerConfig.GetVersion()) {
            return crypt_v414(data);
        }
         */

        int remaining = data.length;
        int llength = 0x5B0;
        int start = 0;

        try {
            while (remaining > 0) {
                byte[] myIv = BitTools.multiplyBytes(this.iv, 4, 4);
                if (remaining < llength) {
                    llength = remaining;
                }
                for (int x = start; x < (start + llength); x++) {
                    if ((x - start) % myIv.length == 0) {
                        byte[] newIv = cipher.doFinal(myIv);
                        System.arraycopy(newIv, 0, myIv, 0, myIv.length);
                        // System.out
                        // .println("Iv is now " + HexTool.toString(this.iv));

                    }
                    data[x] ^= myIv[(x - start) % myIv.length];
                }
                start += llength;
                remaining -= llength;
                llength = 0x5B4;
            }
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return data;
    }

    // KMS v2.95
    public byte[] kms_encrypt(byte[] data) {
        byte[] tempiv = this.iv;
        updateIv();
        for (int i = 0; i < data.length; i++) {
            int input = data[i] & 0xFF;
            int crypted = (funnyBytes[tempiv[0] & 0xFF] ^ (((0x10 * input | (input >> 4)) >> 1) & 0x55 | 2 * ((0x10 * input | (input >> 4)) & 0xD5))) & 0xFF;
            data[i] = (byte) crypted;
            funnyShit((byte) input, tempiv);
        }
        return data;
    }

    public byte[] kms_decrypt(byte[] data) {
        byte[] ivtemp = this.iv;
        updateIv();
        for (int i = 0; i < data.length; i++) {
            int first = ((data[i] & 0xFF) ^ funnyBytes[(ivtemp[0] & 0xFF)]) & 0xFF;
            int second = (((first >> 1) & 0x55) | ((first & 0xD5) << 1)) & 0xFF;
            int finals = ((second << 4) | (second >> 4)) & 0xFF;
            data[i] = (byte) finals;
            funnyShit(data[i], ivtemp);
        }
        return data;
    }

    // JMS v414 x64
    public byte[] crypt_v414(byte[] delta) {
        if (this.login || this.isCP) { // server recv or login server
            int a = delta.length;
            int b = a;
            int c = 0;
            if (a >= 0x5B0) {
                b = 0x5B0;
            }
            if (a >= 0xFF00) { // for outpacket?
                b -= 4;
            }
            while (a > 0) {
                byte[] d = BitTools.multiplyBytes(this.iv, 4, 4);
                for (int e = c; e < (c + b); e++) {
                    if ((e - c) % d.length == 0) {
                        try {
                            //cipher.encrypt(d);
                            byte[] newIv = cipher.doFinal(d);
                            System.arraycopy(newIv, 0, d, 0, d.length);
                        } catch (Exception ex) {
                            ex.printStackTrace(); // may eventually want to remove this
                        }
                    }
                    delta[e] ^= d[(e - c) % d.length];
                }
                c += b;
                a -= b;
                b = a;
                if (b >= 0x5B4) {
                    b = 0x5B4;
                }
            }
        } else { // server send packet in all servers except login
            int seqSnd = (this.iv[0] & 0xff) | (this.iv[1] & 0xff) << 8 | (this.iv[2] & 0xff) << 16 | (this.iv[3] & 0xff) << 24;
            for (int i = 0, n = delta.length; i < n; i++) {
                delta[i] += (byte) seqSnd; // isn't this just gamma[0]?
            }
        }
        return delta;
    }

    public void updateIv() {
        this.iv = getNewIv(this.iv);
    }

    public byte[] getPacketHeader(int length) {
        /*
        if (ServerConfig.IsJMS() && 414 <= ServerConfig.GetVersion()) {
            return getPacketHeader_v414(length);
        }
         */

        int iiv = (((iv[3]) & 0xFF) | ((iv[2] << 8) & 0xFF00)) ^ mapleVersion;
        int mlength = (((length << 8) & 0xFF00) | (length >>> 8)) ^ iiv;

        return new byte[]{(byte) ((iiv >>> 8) & 0xFF), (byte) (iiv & 0xFF), (byte) ((mlength >>> 8) & 0xFF), (byte) (mlength & 0xFF)};
    }

    public static int getPacketLength(int packetHeader) {
        int packetLength = ((packetHeader >>> 16) ^ (packetHeader & 0xFFFF));
        packetLength = ((packetLength << 8) & 0xFF00) | ((packetLength >>> 8) & 0xFF); // fix endianness
        return packetLength;
    }

    public byte[] getPacketHeader_v414(int length) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        int uSeqSnd = ((iv[2] & 0xFF) | (iv[3] << 8)) & 0xFFFF;
        uSeqSnd ^= (0xFFFF - (short) ServerConfig.GetVersion());

        mplew.writeShort((short) uSeqSnd);
        if (length >= 0xFF00) {
            mplew.writeShort((short) (0xFF00 ^ uSeqSnd));
            mplew.writeInt(length ^ uSeqSnd);
        } else {
            mplew.writeShort((short) (length ^ uSeqSnd));
        }
        return mplew.getPacket().getBytes();
    }

    public boolean checkPacket(byte[] packet) {
        // x64
        if (ServerConfig.IsJMS() && 414 <= ServerConfig.GetVersion()) {
            // KMS v373
            return true;
        }

        return ((((packet[0] ^ iv[2]) & 0xFF) == ((mapleVersion >> 8) & 0xFF)) && (((packet[1] ^ iv[3]) & 0xFF) == (mapleVersion & 0xFF)));
    }

    public boolean checkPacket(int packetHeader) {
        return checkPacket(new byte[]{(byte) ((packetHeader >> 24) & 0xFF), (byte) ((packetHeader >> 16) & 0xFF)});
    }

    public static byte[] getNewIv(byte oldIv[]) {
        byte[] in = {(byte) 0xf2, 0x53, (byte) 0x50, (byte) 0xc6}; // magic
        for (int x = 0; x < 4; x++) {
            funnyShit(oldIv[x], in);
        }
        return in;
    }

    @Override
    public String toString() {
        return "IV: " + HexTool.toString(this.iv);
    }

    public static final void funnyShit(byte inputByte, byte[] in) {
        byte elina = in[1];
        byte anna = inputByte;
        byte moritz = funnyBytes[(int) elina & 0xFF];
        moritz -= inputByte;
        in[0] += moritz;
        moritz = in[2];
        moritz ^= funnyBytes[(int) anna & 0xFF];
        elina -= (int) moritz & 0xFF;
        in[1] = elina;
        elina = in[3];
        moritz = elina;
        elina -= (int) in[0] & 0xFF;
        moritz = funnyBytes[(int) moritz & 0xFF];
        moritz += inputByte;
        moritz ^= in[2];
        in[2] = moritz;
        elina += (int) funnyBytes[(int) anna & 0xFF] & 0xFF;
        in[3] = elina;

        int merry = ((int) in[0]) & 0xFF;
        merry |= (in[1] << 8) & 0xFF00;
        merry |= (in[2] << 16) & 0xFF0000;
        merry |= (in[3] << 24) & 0xFF000000;
        int ret_value = merry >>> 0x1d;
        merry <<= 3;
        ret_value |= merry;

        in[0] = (byte) (ret_value & 0xFF);
        in[1] = (byte) ((ret_value >> 8) & 0xFF);
        in[2] = (byte) ((ret_value >> 16) & 0xFF);
        in[3] = (byte) ((ret_value >> 24) & 0xFF);
    }
}
