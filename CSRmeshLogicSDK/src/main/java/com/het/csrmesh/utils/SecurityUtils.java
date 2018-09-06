/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.het.csrmesh.utils;

import android.util.Base64;
import android.util.Log;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *  Security utils class used for encryption and decryption of messages which use mesh databaseId as password.
 */
public class SecurityUtils {

    private final static int IV_SIZE = 16;
    private final static String UTF_8 = "UTF-8";
    private final static String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private final static String ALGORITHM = "AES";








    private static final String TAG = "SecurityUtils";
    public static final int IV_NUM_BYTES = 16;

    /**
     * AES encrypt a text string.
     * @param networkKey The mesh network key for the place.
     * @param value The text to encrypt.
     * @return Byte array of 16 byte IV followed by encrypted data or null if encryption failed.
     */
    public static byte [] aesEncrypt(String networkKey, String value) {
        SecureRandom random = new SecureRandom();
        byte [] ivBytes = new byte[IV_NUM_BYTES];
        random.nextBytes(ivBytes);
        try {
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            byte [] key = getHash(networkKey);
            if (key != null) {
                SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

                byte[] encrypted = cipher.doFinal(value.getBytes());
                byte[] ivAndEncrypted = new byte[IV_NUM_BYTES + encrypted.length];
                System.arraycopy(ivBytes, 0, ivAndEncrypted, 0, ivBytes.length);
                System.arraycopy(encrypted, 0, ivAndEncrypted, ivBytes.length, encrypted.length);
                return Base64.encodeToString(ivAndEncrypted, Base64.NO_WRAP).getBytes(UTF_8);
            }
            return null;
        }
        catch (Exception ex) {
            Log.d(TAG, "Encrypt: " + ex.toString());
            return null;
        }
    }

    /**
     * AES decrypt to a text string.
     * @param networkKey The mesh network key for the place.
     * @param ivAndEncrypted Byte array containing a 16 byte IV followed by encrypted data.
     * @return Plain text as string or null if encryption failed.
     */
    public static String aesDecrypt(String networkKey, byte [] ivAndEncrypted) {

        byte[] ivAndCipherText = Base64.decode(ivAndEncrypted, Base64.NO_WRAP);

        byte [] ivBytes = new byte[IV_NUM_BYTES];
        byte [] encrypted = new byte[ivAndCipherText.length - IV_NUM_BYTES];
        System.arraycopy(ivAndCipherText, 0, ivBytes, 0, ivBytes.length);
        System.arraycopy(ivAndCipherText, IV_NUM_BYTES, encrypted, 0, encrypted.length);

        try {
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            byte [] key = getHash(networkKey);

            if (key != null) {
                SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

                byte[] original = cipher.doFinal(encrypted);

                return new String(original);
            }
            return null;
        }
        catch (Exception ex)
        {
            Log.d(TAG, "Decrypt: " + ex.toString() + "(" + encrypted.length + ")");
            return null;
        }
    }

    private static final int NETWORK_KEY_SIZE = 16;

    /**
     * Calculate SHA 256 of a pass phrase for the specified protocol.
     *
     * @param passPhrase The pass phrase.
     * @return 16 byte array containing the hash or null if operation failed.
     */
    private static byte[] getHash(String passPhrase) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e1) {
            return null;
        }
        digest.reset();
        byte[] hash = digest.digest(passPhrase.getBytes());
        byte[] result = new byte[NETWORK_KEY_SIZE];
        // We only want the first NETWORK_KEY_SIZE octets.
        System.arraycopy(hash, 0, result, 0, NETWORK_KEY_SIZE);

        return result;
    }






    /**
     * Encrypts a String using a password and AES key.
     * @param message Message to be encrypted
     * @param password Password to use in the encryption
     * @return byte[] with the IV as header plus encrypted message (IV + encrypted_message)
     * @throws Exception
     */
    public static byte[] encrypt(String message, final String password) throws Exception {

        // Create the key
        Log.d("edutest", "password:" + password);
        byte[] passwordBytes = hexStringToByteArray(password);
        SecretKeySpec keySpec = new SecretKeySpec(passwordBytes, ALGORITHM);

        // Create Random IV
        byte[] iv = new byte[] {(byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08, (byte)0x09, (byte)0x10, (byte)0x11, (byte)0x12, (byte)0x13, (byte)0x14, (byte)0x15,};/*generateRandomIv(IV_SIZE);*/
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Encrypt the message
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] cipherText = cipher.doFinal(message.getBytes(UTF_8));
        byte[] ivAndCipherText = buildCombinedArray(iv, cipherText);

        return Base64.encodeToString(ivAndCipherText, Base64.NO_WRAP).getBytes(UTF_8);
    }

    /**
     * Decrypts encoded message using password and AES
     * @param encodedMessage Encoded byte[] including the IV as a header (1st 16bytes) and the AES encrypted message
     * @param password Password to use in the decryption
     * @return byte[] with the decrypted message
     * @throws Exception
     */
    public static byte[] decrypt(byte[] encodedMessage, final String password) throws Exception {

        // Create the key
        byte[] passwordBytes = hexStringToByteArray(password);
        SecretKeySpec keySpec = new SecretKeySpec(passwordBytes, ALGORITHM);

        // Decode encrypted message
        byte[] ivAndCipherText = Base64.decode(encodedMessage, Base64.NO_WRAP);

        // Extract IV from decoded byte[]
        byte[] iv = Arrays.copyOfRange(ivAndCipherText, 0, IV_SIZE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Extract cipher text from decoded byte[]
        byte[] cipherText = Arrays.copyOfRange(ivAndCipherText, IV_SIZE, ivAndCipherText.length);

        // Decrypt the message
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] decryptedTextBytes = null;
        try {
            decryptedTextBytes = cipher.doFinal(cipherText);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return decryptedTextBytes;
    }


    /**
     * Generates a random IV of the length indicated by parameter
     * @param length Length of the IV
     * @return byte[] IV value
     * @throws GeneralSecurityException
     */
    private static byte[] generateRandomIv(int length) throws GeneralSecurityException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] b = new byte[length];
        random.nextBytes(b);
        return b;
    }


    /**
     * Creates a clean version of the meshId to be later converted from hex String to byte[]
     * @param password MeshId to be used as password with the "-" as separators
     * @return String with the meshID values separated in bytes by spaces.
     */
    public static String prepareMeshIdPassword(String password) {

        String cleanPassword = password.replace("-", "");

        int frequency = 2;
        String space = " ";

        StringBuilder builder = new StringBuilder(cleanPassword.length() + space.length() * (cleanPassword.length()/frequency)+1);

        int index = 0;
        String prefix = "";
        while (index < cleanPassword.length())
        {
            builder.append(prefix);
            prefix = space;
            builder.append(cleanPassword.substring(index, Math.min(index + frequency, cleanPassword.length())));
            index += frequency;
        }
        return builder.toString();
    }


    /**
     * Builds a combined array out of two arrays sent in the signature
     * @param first First array to combine
     * @param second Second array to combine
     * @return
     */
    private static byte[] buildCombinedArray(byte[] first, byte[] second) {
        byte[] combined = new byte[first.length + second.length];
        for (int i = 0; i < combined.length; ++i) {
            combined[i] = i < first.length ? first[i] : second[i - first.length];
        }
        return combined;
    }


    /**
     * Builds from a hex String a byte[]
     * @param s hex String to be converted
     * @return byte[] formed from String
     */
    public static byte[] hexStringToByteArray(final String s) {
        String[] v = s.split(" ");
        byte[] arr = new byte[v.length];
        int i = 0;
        for(String val: v) {
            arr[i++] =  Integer.decode("0x" + val).byteValue();

        }
        return arr;
    }
}
