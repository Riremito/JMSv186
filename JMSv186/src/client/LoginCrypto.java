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
package client;

import java.util.Random;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;

import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import tools.HexTool;

public class LoginCrypto {

    protected final static int extralength = 6;
    private final static String[] Alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private final static String[] Number = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private final static Random rand = new Random();
    private static KeyFactory RSAKeyFactory;

    static {
        Security.addProvider(new BouncyCastleProvider());

        try {
            RSAKeyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException nsa) {
            System.err.println("[LoginCrypto] Error occured with RSA KeyFactory");
        }
    }

    public static final String Generate_13DigitAsiasoftPassport() {
        StringBuilder sb = new StringBuilder();
        sb.append(Alphabet[rand.nextInt(Alphabet.length)]); // First Letter

        for (int i = 0; i < 11; i++) {
            sb.append(Number[rand.nextInt(Number.length)]); // 11 Numbers
        }
        sb.append(Alphabet[rand.nextInt(Alphabet.length)]); // Last Letter

        return sb.toString();
    }

    private static final String toSimpleHexString(final byte[] bytes) {
        return HexTool.toString(bytes).replace(" ", "").toLowerCase();
    }

    private static final String hashWithDigest(final String in, final String digest) {
        try {
            MessageDigest Digester = MessageDigest.getInstance(digest);
            Digester.update(in.getBytes("UTF-8"), 0, in.length());
            byte[] sha1Hash = Digester.digest();
            return toSimpleHexString(sha1Hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Hashing the password failed", ex);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding the string failed", e);
        }

    }

    private static final String hexSha1(final String in) {
        return hashWithDigest(in, "SHA-1");
    }

    private static final String hexSha512(final String in) {
        return hashWithDigest(in, "SHA-512");
    }

    public static final boolean checkSha1Hash(final String hash, final String password) {
        return hash.equals(hexSha1(password));
    }

    public static final boolean checkSaltedSha512Hash(final String hash, final String password, final String salt) {
        return hash.equals(makeSaltedSha512Hash(password, salt));
    }

    public static final String makeSaltedSha512Hash(final String password, final String salt) {
        return hexSha512(password + salt);
    }

    public static final String makeSalt() {
        byte[] salt = new byte[16];
        rand.nextBytes(salt);
        return toSimpleHexString(salt);
    }

    public final static String rand_s(final String in) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < extralength; i++) {
            sb.append(rand.nextBoolean() ? Alphabet[rand.nextInt(Alphabet.length)] : Number[rand.nextInt(Number.length)]);
        }
        return sb.toString() + in;
    }

    public final static String rand_r(final String in) {
        return in.substring(extralength, extralength + 128);
    }

    public final static String decryptRSA(final String EncryptedPassword) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPPadding", "BC");
            BigInteger modulus = new BigInteger("107657795738756861764863218740655861479186575385923787150128619142132921674398952720882614694082036467689482295621654506166910217557126105160228025353603544726428541751588805629215516978192030682053419499436785335057001573080195806844351954026120773768050428451512387703488216884037312069441551935633523181351");
            BigInteger privateExponent = new BigInteger("5550691850424331841608142211646492148529402295329912519344562675759756203942720314385192411176941288498447604817211202470939921344057999440566557786743767752684118754789131428284047255370747277972770485804010629706937510833543525825792410474569027516467052693380162536113699974433283374142492196735301185337");
            RSAPrivateKeySpec privKey1 = new RSAPrivateKeySpec(modulus, privateExponent);
            PrivateKey privKey = RSAKeyFactory.generatePrivate(privKey1);

            byte[] bytes = Hex.decode(EncryptedPassword);
            cipher.init(Cipher.DECRYPT_MODE, privKey);
            return new String(cipher.doFinal(bytes));

        } catch (InvalidKeyException ike) {
            System.err.println("[LoginCrypto] Error initalizing the encryption cipher.  Make sure you're using the Unlimited Strength cryptography jar files.");
        } catch (NoSuchProviderException nspe) {
            System.err.println("[LoginCrypto] Security provider not found");
        } catch (Exception e) {
            System.err.println("[LoginCrypto] Error occured with RSA password decryption.");
        }
        return "";
    }
}
