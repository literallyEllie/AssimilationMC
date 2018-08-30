package net.assimilationmc.assicore.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class UtilSecurity {

    /**
     * Encrypt a string to AES.
     *
     * @param input To encrypt
     * @param key   Encryption Key
     * @return Encrypted String
     */
    public static String encrypt(String input, String key) {
        byte[] crypted;

        try {
            final SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(1, skey);
            crypted = cipher.doFinal(input.getBytes());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return input;
        }

        return new String(Base64.getEncoder().encode(crypted));
    }

    /**
     * Decrypt a string.
     *
     * @param input Encrypted String
     * @param key   Encryption Key
     * @return Decrypted String
     */
    public static String decrypt(String input, String key) {
        byte[] output;

        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(2, skey);
            output = cipher.doFinal(Base64.getDecoder().decode(input));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return input;
        }

        return new String(output);
    }

}
