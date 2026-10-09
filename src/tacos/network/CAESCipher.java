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

    private static SecretKeySpec sks = null;
    private static Cipher cipher_aes = null;

    // CAESCipher::UserKey
    private static final int[] UserKey = {
        0x00000013, 0x00000052, 0x0000002A, 0x0000005B,
        0x00000008, 0x00000002, 0x00000010, 0x00000060,
        0x00000006, 0x00000002, 0x00000043, 0x0000000F,
        0x000000B4, 0x0000004B, 0x00000035, 0x00000005,
        0x0000001B, 0x0000000A, 0x0000005F, 0x00000009,
        0x0000000F, 0x00000050, 0x0000000C, 0x0000001B,
        0x00000033, 0x00000055, 0x00000001, 0x00000009,
        0x00000052, 0x000000DE, 0x000000C7, 0x0000001E
    };

    // CAESCipher::bDefaultAESKeyValue
    private static final byte[] bDefaultAESKeyValue = {
        (byte) 0xC6, (byte) 0x50, (byte) 0x53, (byte) 0xF2,
        (byte) 0xA8, (byte) 0x42, (byte) 0x9D, (byte) 0x7F,
        (byte) 0x77, (byte) 0x09, (byte) 0x1D, (byte) 0x26,
        (byte) 0x42, (byte) 0x53, (byte) 0x88, (byte) 0x7C
    };

    private static Cipher getCipher() {
        if (cipher_aes != null) {
            return cipher_aes;
        }
        try {
            cipher_aes = Cipher.getInstance("AES");
            cipher_aes.init(Cipher.ENCRYPT_MODE, sks);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex) {
            DebugLogger.ExceptionLog("CAESCipher");
        }

        return cipher_aes;
    }

    // CInPacket::DecryptData
    public static byte[] CryptData(byte[] pDest, byte[] pSrc, int nLen, byte[] pdwKey) {
        int offset = 0;

        if (1456 <= nLen) {
            nLen = 1456;
        }

        do {
            Crypt(pDest, pSrc, nLen, pdwKey, offset);
            offset += nLen;
            nLen = pSrc.length - offset;
            if (1460 <= nLen) {
                nLen = 1460;
            }
        } while (offset < pSrc.length);

        return pDest;
    }

    // CAESCipher::Encrypt, CAESCipher::Decrypt
    private static boolean Crypt(byte[] pDest, byte[] pSrc, int nLen, byte[] pdwKey, int offset) {
        // CAESCipher::AES_EncKeySchedule
        // CAESCipher::AES_DecInit
        byte[] ChainVar = new byte[16]; // CAESCipher::AES_ALG_INFO *AlgInfo
        if (Content.OldIV.get()) {
            if (Region.TWMS.check() || Region.HKMS.check()) {
                // iv is changed, you need to keep old iv for next loop (1456+ bytes).
                AES_Init_HKMS5(ChainVar, pdwKey);
            } else {
                // iv is never changed.
                AES_Init_JMS131(ChainVar, pdwKey);
            }
        } else {
            // iv is never changed.
            AES_Init(ChainVar, pdwKey);
        }

        for (int i = offset; i < (offset + nLen); i++) {
            // CAESCipher::OFB_DecUpdate
            if ((i - offset) % ChainVar.length == 0) {
                try {
                    byte[] newIv = getCipher().doFinal(ChainVar);
                    System.arraycopy(newIv, 0, ChainVar, 0, ChainVar.length);
                } catch (IllegalBlockSizeException | BadPaddingException ex) {
                    // iv error.
                    DebugLogger.ExceptionLog("Crypt");
                    return false;
                }
            }
            // CAESCipher::OFB_DecFinal
            pDest[i] = (byte) (pSrc[i] ^ ChainVar[(i - offset) % ChainVar.length]);
        }

        return true;
    }

    // CAESCipher::AES_EncInit, CAESCipher::AES_DecInit
    private static byte[] AES_Init(byte[] ChainVar, byte[] pdwKey) {
        if (pdwKey != null) {
            for (int i = 0; i < ChainVar.length; i++) {
                ChainVar[i] = pdwKey[i % 4];
            }
        } else {
            // never executed.
            DebugLogger.ErrorLog("AES_Init : iv = null.");
            for (int i = 0; i < ChainVar.length; i++) {
                ChainVar[i] = bDefaultAESKeyValue[i];
            }
        }

        return ChainVar;
    }

    // JMS131
    private static byte[] AES_Init_JMS131(byte[] ChainVar, byte[] pdwKey) {
        if (pdwKey != null) {
            for (int i = 0; i < ChainVar.length; i++) {
                ChainVar[i] = pdwKey[0];
            }
        } else {
            // never executed.
            DebugLogger.ErrorLog("AES_Init_JMS131 : iv = null.");
            for (int i = 0; i < ChainVar.length; i++) {
                ChainVar[i] = bDefaultAESKeyValue[3]; // F2
            }
        }

        return ChainVar;
    }

    // HKMS5
    private static byte[] AES_Init_HKMS5(byte[] ChainVar, byte[] pdwKey) {
        if (pdwKey != null) {
            for (int i = 0; i < 4; i++) {
                CIGCipher.MorphKey(pdwKey, CIGCipher.bShuffle[i]);
                ChainVar[i * 4] = pdwKey[0];
                ChainVar[i * 4 + 1] = pdwKey[1];
                ChainVar[i * 4 + 2] = pdwKey[2];
                ChainVar[i * 4 + 3] = pdwKey[3];
            }
        } else {
            // never executed.
            DebugLogger.ErrorLog("AES_Init_HKMS5 : iv = null.");
            for (int i = 0; i < ChainVar.length; i++) {
                ChainVar[i] = bDefaultAESKeyValue[i]; // C65053F2A8...
            }
        }

        return ChainVar;
    }

    // CAESCipher::RIJNDAEL_KeySchedule
    public static boolean setAesKey() {
        byte[] e_key = new byte[32]; // filled with 0.
        if (Config.Equal(Region.GMS, 126)) {
            e_key[0] = (byte) 0x8B;
            e_key[4] = (byte) 0x24;
            e_key[8] = (byte) 0x8B;
            e_key[12] = (byte) 0x6D;
            e_key[16] = (byte) 0xB5;
            e_key[20] = (byte) 0xC6;
            e_key[24] = (byte) 0x08;
            e_key[28] = (byte) 0xB0;
            sks = new SecretKeySpec(e_key, "AES");
            DebugLogger.InfoLog("aes_key = GMS126");
            return true;
        }
        if (Config.Equal(Region.GMS, 131)) {
            e_key[0] = (byte) 0x44;
            e_key[4] = (byte) 0xB9;
            e_key[8] = (byte) 0x0F;
            e_key[12] = (byte) 0xB3;
            e_key[16] = (byte) 0x76;
            e_key[20] = (byte) 0x23;
            e_key[24] = (byte) 0x38;
            e_key[28] = (byte) 0xAE;
            sks = new SecretKeySpec(e_key, "AES");
            DebugLogger.InfoLog("aes_key = GMS131");
            return true;
        }

        for (int i = 0; i < 8; i++) {
            e_key[i * 4] = (byte) (UserKey[i * 4]); // 13 08...
        }
        sks = new SecretKeySpec(e_key, "AES");
        DebugLogger.InfoLog("aes_key = default");
        return false;
    }
}
