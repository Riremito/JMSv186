/*
 * Copyright (C) 2026 Riremito
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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import tacos.config.Config;
import tacos.config.Content;
import tacos.config.Region;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class CAESCipher {

    public static final String AES_ENC_KEY = "AES_ENC";
    public static final String AES_DEC_KEY = "AES_DEC";

    private static SecretKeySpec skey = new SecretKeySpec(new byte[]{0x13, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, (byte) 0xB4, 0x00, 0x00, 0x00, 0x1B, 0x00, 0x00, 0x00, 0x0F, 0x00, 0x00, 0x00, 0x33, 0x00, 0x00, 0x00, 0x52, 0x00, 0x00, 0x00}, "AES");

    // CAESCipher::bDefaultAESKeyValue
    private static final byte[] bDefaultAESKeyValue = new byte[]{
        (byte) 0xC6, (byte) 0x50, (byte) 0x53, (byte) 0xF2,
        (byte) 0xA8, (byte) 0x42, (byte) 0x9D, (byte) 0x7F,
        (byte) 0x77, (byte) 0x09, (byte) 0x1D, (byte) 0x26,
        (byte) 0x42, (byte) 0x53, (byte) 0x88, (byte) 0x7C
    };

    private byte[] iv = null;
    private final short mapleVersion;
    private Cipher cipher;

    public CAESCipher(byte[] iv, boolean isOutbound) {
        this.iv = iv;
        short vesrion = isOutbound ? (short) (0xFFFF - (short) Config.VERSION) : (short) Config.VERSION;
        this.mapleVersion = (short) (((vesrion >>> 8) & 0xFF) | ((vesrion << 8) & 0xFF00));

        try {
            this.cipher = Cipher.getInstance("AES");
            this.cipher.init(Cipher.ENCRYPT_MODE, skey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex) {
            DebugLogger.ExceptionLog("CAESCipher");
        }
    }

    public byte[] getPacketHeader(int length) {
        int iiv = (((this.iv[3]) & 0xFF) | ((this.iv[2] << 8) & 0xFF00)) ^ this.mapleVersion;
        int mlength = (((length << 8) & 0xFF00) | (length >>> 8)) ^ iiv;

        return new byte[]{(byte) ((iiv >>> 8) & 0xFF), (byte) (iiv & 0xFF), (byte) ((mlength >>> 8) & 0xFF), (byte) (mlength & 0xFF)};
    }

    public static int getPacketLength(int packetHeader) {
        int packetLength = ((packetHeader >>> 16) ^ (packetHeader & 0xFFFF));
        packetLength = ((packetLength << 8) & 0xFF00) | ((packetLength >>> 8) & 0xFF); // fix endianness
        return packetLength;
    }

    public boolean checkPacket(byte[] packet) {
        return ((((packet[0] ^ this.iv[2]) & 0xFF) == ((this.mapleVersion >>> 8) & 0xFF)) && (((packet[1] ^ this.iv[3]) & 0xFF) == (this.mapleVersion & 0xFF)));
    }

    public boolean checkPacket(int packetHeader) {
        return checkPacket(new byte[]{(byte) ((packetHeader >>> 24) & 0xFF), (byte) ((packetHeader >>> 16) & 0xFF)});
    }

    public byte[] getIv() {
        return this.iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    // CInPacket::DecryptData
    public byte[] CInPacket_DecryptData(byte[] data) {
        int nLen = data.length;
        int offset = 0;

        if (1456 <= nLen) {
            nLen = 1456;
        }

        do {
            Decrypt(data, offset, nLen);
            offset += nLen;
            nLen = data.length - offset;
            if (1460 <= nLen) {
                nLen = 1460;
            }
        } while (offset < data.length);

        this.iv_old_hkms = null; // clear for detecting next packet.
        return data;
    }

    // CAESCipher::Decrypt
    private boolean Decrypt(byte[] data, int offset, int nLen) {
        // CAESCipher::AES_EncKeySchedule
        // CAESCipher::AES_DecInit
        byte[] myIv = null;
        if (Content.OldIV.get()) {
            if (Region.TWMS.check() || Region.HKMS.check()) {
                // iv is changed, you need to keep old iv for next loop (1456+ bytes).
                myIv = AES_DecInit_HKMS5();
            } else {
                // iv is never changed.
                myIv = AES_DecInit_JMS131();
            }
        } else {
            // iv is never changed.
            myIv = AES_DecInit();
        }

        for (int i = offset; i < (offset + nLen); i++) {
            // CAESCipher::OFB_DecUpdate
            if ((i - offset) % myIv.length == 0) {
                try {
                    byte[] newIv = this.cipher.doFinal(myIv);
                    System.arraycopy(newIv, 0, myIv, 0, myIv.length);
                } catch (IllegalBlockSizeException | BadPaddingException ex) {
                    // iv error.
                    DebugLogger.ExceptionLog("CAESCipher_Decrypt");
                    return false;
                }
            }
            // CAESCipher::OFB_DecFinal
            data[i] ^= myIv[(i - offset) % myIv.length];
        }

        return true;
    }

    // CAESCipher::AES_DecInit
    private byte[] AES_DecInit() {
        byte[] ChainVar = new byte[16];

        if (this.iv != null) {
            for (int i = 0; i < ChainVar.length; i++) {
                ChainVar[i] = this.iv[i % 4];
            }
        } else {
            // never executed.
            DebugLogger.ErrorLog("CAESCipher_AES_DecInit : iv = null.");
            for (int i = 0; i < ChainVar.length; i++) {
                ChainVar[i] = bDefaultAESKeyValue[i];
            }
        }

        return ChainVar;
    }

    // CAESCipher::AES_DecInit, JMS131
    private byte[] AES_DecInit_JMS131() {
        byte[] ChainVar = new byte[16];

        if (this.iv != null) {
            for (int i = 0; i < ChainVar.length; i++) {
                ChainVar[i] = this.iv[0];
            }
        } else {
            // never executed.
            DebugLogger.ErrorLog("CAESCipher_AES_DecInit_JMS131 : iv = null.");
            for (int i = 0; i < ChainVar.length; i++) {
                ChainVar[i] = bDefaultAESKeyValue[3]; // F2
            }
        }

        return ChainVar;
    }

    private byte[] iv_old_hkms = null;

    // CAESCipher::AES_DecInit, HKMS5
    private byte[] AES_DecInit_HKMS5() {
        byte[] ChainVar = new byte[16];

        if (this.iv != null) {
            byte[] iv_copy = (this.iv_old_hkms == null) ? this.iv.clone() : this.iv_old_hkms.clone();
            for (int i = 0; i < 4; i++) {
                CIGCipher.MorphKey(iv_copy, CIGCipher.bShuffle[i]);
                ChainVar[i * 4] = iv_copy[0];
                ChainVar[i * 4 + 1] = iv_copy[1];
                ChainVar[i * 4 + 2] = iv_copy[2];
                ChainVar[i * 4 + 3] = iv_copy[3];
            }
            this.iv_old_hkms = iv_copy;
        } else {
            // never executed.
            DebugLogger.ErrorLog("CAESCipher_AES_DecInit_HKMS5 : iv = null.");
            for (int i = 0; i < ChainVar.length; i++) {
                ChainVar[i] = bDefaultAESKeyValue[i]; // C65053F2A8...
            }
        }

        return ChainVar;
    }

    public static boolean setAesKey() {
        byte aes_key[] = new byte[32]; // filled with 0.
        if (Config.Equal(Region.GMS, 126)) {
            aes_key[0] = (byte) 0x8B;
            aes_key[4] = (byte) 0x24;
            aes_key[8] = (byte) 0x8B;
            aes_key[12] = (byte) 0x6D;
            aes_key[16] = (byte) 0xB5;
            aes_key[20] = (byte) 0xC6;
            aes_key[24] = (byte) 0x08;
            aes_key[28] = (byte) 0xB0;
            skey = new SecretKeySpec(aes_key, "AES");
            DebugLogger.InfoLog("aes_key = GMS126");
            return true;
        }
        if (Config.Equal(Region.GMS, 131)) {
            aes_key[0] = (byte) 0x44;
            aes_key[4] = (byte) 0xB9;
            aes_key[8] = (byte) 0x0F;
            aes_key[12] = (byte) 0xB3;
            aes_key[16] = (byte) 0x76;
            aes_key[20] = (byte) 0x23;
            aes_key[24] = (byte) 0x38;
            aes_key[28] = (byte) 0xAE;
            skey = new SecretKeySpec(aes_key, "AES");
            DebugLogger.InfoLog("aes_key = GMS131");
            return true;
        }
        DebugLogger.InfoLog("aes_key = default");
        return false;
    }
}
