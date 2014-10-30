package com.foxycode.testapp.Security;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

//import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by gve on 19/09/2014.
 */
public class MySecureManager {
    private static final String TAG = "MySecureManager";
    private static int ITERATION_COUNT = 1000;
    private static int KEY_LENGTH = 256;
    public static final String PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";


    public static String generateCode(){
        return UUID.randomUUID().toString().substring(1,6);
    }

    public static void saveEcnryptedImageOnDisk(Bitmap bitMapToShare,String path,String pwd) {

        File file = new File(path);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitMapToShare.compress(Bitmap.CompressFormat.JPEG, 85, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Derive a password, the password is stored as char[] and not string for security matters
     * @param password
     * @return
     */
    public static SecretKey deriveKeyPbkdf2( String password) {
        byte[] salt = "notgood".getBytes();
        try {
         //   long start = System.currentTimeMillis();
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_DERIVATION_ALGORITHM);
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            // SALog.d(HighLevelOfSecurity.class, "key bytes: " + toHex(keyBytes));

            SecretKey result = new SecretKeySpec(keyBytes, "AES");
          //  long elapsed = System.currentTimeMillis() - start;
            //SALog.d(HighLevelOfSecurity.class, String.format("PBKDF2 key derivation took %d [ms].",  elapsed));
            return result;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param out
     * @param sk
     * @return

     */
    public static CipherOutputStream encrypt(OutputStream out, SecretKey sk) {
        Cipher cipher = null;
        try {
            SecureRandom random = new SecureRandom();
            cipher = Cipher.getInstance("AES");
            byte[] iv = new byte[cipher.getBlockSize()];
            random.nextBytes(iv);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, sk, ivParams);

            //cipher.init(Cipher.ENCRYPT_MODE, sk);
//			cipher.doFinal();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Out of luck, AES isn't implemented on your system!");
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            Log.e(TAG, "Padding invalid");
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            Log.e(TAG, "Invalid key provided");
            e.printStackTrace();
        }
//			catch (IllegalBlockSizeException e) {
//			Log.e(TAG, "Padding invalid");
//			e.printStackTrace();
//		} catch (BadPaddingException e) {
//			Log.e(TAG, "Padding invalid");
//			e.printStackTrace();
//		}
        catch (InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        CipherOutputStream cipherOut = new CipherOutputStream(out, cipher);
        return cipherOut;
    }


    /**
     * Given an InputStream and a symmetric key, decrypts the file using
     * CipherStreams
     *
     * @param fileStream
     *            InputStream corresponding to file to be decrypted
     * @param sk
     *            Symmetric key
     * @return InputStream representing the decrypted file
     */
    public static CipherInputStream decrypt(InputStream fileStream, SecretKey sk) {
        Cipher cipher = null;
		/* TODO: Add Log code to catch blocks */
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sk);

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Out of luck, AES isn't implemented on your system!");
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            Log.e(TAG, "No such padding");
            Log.e(TAG, e.getMessage());
        } catch (InvalidKeyException e) {
            Log.e(TAG, "Invalid key provided");
            Log.e(TAG, e.getMessage());
        }
        CipherInputStream cipherIn = new CipherInputStream(fileStream, cipher);
        return cipherIn;
    }


    /**
     * Encrypts the provided message using the given SecretKey
     *
     * @param message
     *            Message to be encrypted as byte buffer
     * @param sk
     *            SecretKey to be used in encryption
     * @return Encrypted message as byte buffer
     */
    private static byte[] encrypt(byte[] message, SecretKey sk) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sk);
            // cipherText = new byte[cipher.getOutputSize(message.length)];
            byte[] cipherText = cipher.doFinal(message);
            return cipherText;
        } catch (IllegalBlockSizeException e) {
            Log.e(TAG, "Change your block size");
            Log.e(TAG, e.getMessage());
        } catch (BadPaddingException e) {
            Log.e(TAG, "Bad padding");
            Log.e(TAG, e.getMessage());
        } catch (InvalidKeyException e) {
            Log.e(TAG, "The key provided is invalid");
            Log.e(TAG, e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG,
                    "You're out of luck, AES is not implemented on your system");
            Log.e(TAG, e.getMessage());
        } catch (NoSuchPaddingException e) {
            Log.e(TAG, "Padding invalid");
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

/*

    public  String encrypt(String seed, String cleartext) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    public  String decrypt(String seed, String encrypted) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }*/
    public static String encrypt(String message, SecretKey sk) {
        Log.v(TAG,"message"+message);
        try {
           // byte[] messB = Base64.decodeBase64(message);
            //byte[] messB = Base64.encode(message.getBytes(), Base64.NO_WRAP);
            // byte[] messB = toByte(toHex(message));
            byte[] messB = message.getBytes();
            byte[] encrypt = encrypt(messB, sk);
            return toHex(encrypt);
          //  return Base64.encodeToString(encrypt,Base64.NO_WRAP);
        }
        catch(Exception e){
            Log.e(TAG,e.getMessage());
            return null;
        }
    }

    public static String decrypt(String message, SecretKey sk) {
       // byte[] encrypt =Base64.decodeBase64(message);

      //  byte[] encrypt = Base64.decode(message, Base64.NO_WRAP);
        byte[] encrypt = toByte(message);
        byte[] decrypt = decrypt(encrypt, sk);
        return new String(decrypt);//fromHex(toHex(decrypt));
       // return Base64.encodeToString(decrypt,Base64.NO_WRAP);

       // return Base64.encodeBase64String(decrypt);
    }

    /**
     *
     * @param cipherBytes
     *            Ciphertext to be decrypted
     * @param sk
     *            Symmetric key to be used in decryption
     * @return Byte buffer containing decrypted content
     */
    private static byte[] decrypt(byte[] cipherBytes, SecretKey sk) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sk);
            // plainText = new byte[cipher
                   // .getOutputSize(cipherBytes.length)];
            byte[] plainText = cipher.doFinal(cipherBytes);
            return plainText;
        } catch (BadPaddingException e) {
            Log.e(TAG, "Bad padding");
            Log.e(TAG, e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Out of luck, AES isn't implemented on your system!");
            Log.e(TAG, e.getMessage());
        } catch (NoSuchPaddingException e) {
            Log.e(TAG, "No such padding");
            Log.e(TAG, e.getMessage());
        } catch (InvalidKeyException e) {
            Log.e(TAG, "Invalid key provided");
            Log.e(TAG, e.getMessage());
        } catch (IllegalBlockSizeException e) {
            Log.e(TAG, "Illegal block size");
            Log.e(TAG, e.getMessage());
        }
        return null;
    }


  public static  String getJoinedPwd(String pwd){
      SecretKey key = deriveKeyPbkdf2(pwd);
      byte[] pwdByte = encrypt("joined".getBytes(),key);
     // return Base64.encodeBase64String(pwdByte);
      return Base64.encodeToString(pwdByte,Base64.NO_WRAP);
  }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }
    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }
    private final static String HEX = "0123456789ABCDEF";
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }
}
