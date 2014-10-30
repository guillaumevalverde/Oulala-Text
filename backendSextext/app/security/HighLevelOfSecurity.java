package security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import play.Logger;

public class HighLevelOfSecurity {
	
	
	

    public static final String PKCS12_DERIVATION_ALGORITHM = "PBEWITHSHA256AND256BITAES-CBC-BC";
    public static final String PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    private static String DELIMITER = "]";

    private static int KEY_LENGTH = 256;
   
    // minimum values recommended by PKCS#5, increase as necessary
    private static int ITERATION_COUNT = 1000;
    private static final int PKCS5_SALT_LENGTH = 8;
    private static SecureRandom random = new SecureRandom();
	

	

	
	/**
	 * we derive the password to have a symmetric key, we get the hash of it, we compare
	 * it to the one we have, if it is the good one we load in memory what we need.
	 * @param password
	 * @return
	 * @throws BackendException 
	 */
	public static boolean isRightPassword(char[] password, byte[] salt,String hashStored){
		
		//TODO
		return false;
	}
	
	
	

	/**
	 * Use AES to encrypt the file (byte array)
	 * @param filedata the byte array of the file
	 * @param key the securekey
	 * @return cipherdata if encrypt successfully
	 * @return null if encrypt error
	 * 
	 * **/
	public static byte[] encryptByteArray(byte[] filedata, SecretKey key){
		SecureRandom random = new SecureRandom();
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] iv = new byte[cipher.getBlockSize()];
			random.nextBytes(iv);
			IvParameterSpec ivParams = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
			byte[] cipherdata = cipher.doFinal(filedata);
			byte[] retour = new byte[cipherdata.length+cipher.getBlockSize()];
			System.arraycopy(iv, 0, retour, 0, iv.length);
			System.arraycopy(cipherdata, 0, retour, iv.length, cipherdata.length);
			return retour;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			return null;
		} catch (BadPaddingException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	/**
	 * Use AES to encrypt the file (byte array)
	 * @param filedata the byte array of the file
	 * @param key the securekey
	 * @return cipherdata if encrypt successfully
	 * @return null if encrypt error
	 * 
	 * **/
	public static String encryptByteArray(String data, SecretKey key){
		Logger.info("Encrypt data "+ data);
		byte[] filedata = fromBase64(data);
		
		Logger.info("Encrypt data "+ data+" length byte "+ filedata.length);
		SecureRandom random = new SecureRandom();
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] iv = new byte[cipher.getBlockSize()];
			random.nextBytes(iv);
			IvParameterSpec ivParams = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
			byte[] cipherdata = cipher.doFinal(filedata);
			byte[] retour = new byte[cipherdata.length+cipher.getBlockSize()];
			System.arraycopy(iv, 0, retour, 0, iv.length);
			System.arraycopy(cipherdata, 0, retour, iv.length, cipherdata.length);
			
			Logger.info("Encrypt ending  length byte "+ retour.length+ " resultEncrypted "+toBase64(retour));
			
			return toBase64(retour);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			return null;
		} catch (BadPaddingException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	/**
	 * use AES to decrypt the file
	 * the key will be retrieved from the shared preference
	 * @param cipherdata
	 * @return decipherdata
	 * @return null if decrypt error
	 * @throws PasswordException 
	 * **/
	public static byte[] decryptByteArray(byte[] cipherdataWithIv, SecretKey key) {
		
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] cipherdata = new byte[cipherdataWithIv.length-cipher.getBlockSize()];
			byte[] iv = new byte[cipher.getBlockSize()];

			System.arraycopy(cipherdataWithIv, 0, iv, 0, iv.length);
			System.arraycopy(cipherdataWithIv, cipher.getBlockSize(), cipherdata,0, cipherdata.length);
			IvParameterSpec ivParams = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE,key, ivParams);
			byte[] decipherData = cipher.doFinal(cipherdata);
			return decipherData;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			return null;
		} catch (BadPaddingException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * use AES to decrypt the file
	 * the key will be retrieved from the shared preference
	 * @param cipherdata
	 * @return decipherdata
	 * @return null if decrypt error
	 * @throws PasswordException 
	 * **/
	public static String decryptByteArray(String data, SecretKey key) {
		
		byte[]  cipherdataWithIv = fromBase64(data);

		Logger.info("Decrypt data encrypted "+ data+" length byte "+ cipherdataWithIv.length);
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] cipherdata = new byte[cipherdataWithIv.length-cipher.getBlockSize()];
			byte[] iv = new byte[cipher.getBlockSize()];

			System.arraycopy(cipherdataWithIv, 0, iv, 0, iv.length);
			System.arraycopy(cipherdataWithIv, cipher.getBlockSize(), cipherdata,0, cipherdata.length);
			IvParameterSpec ivParams = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE,key, ivParams);
			byte[] decipherData = cipher.doFinal(cipherdata);
			Logger.info("Decrypt data decrypted "+toBase64(decipherData));
			
			return toBase64(decipherData);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			return null;
		} catch (BadPaddingException e) {
			e.printStackTrace();
			return null;
		}
		
	}



	
	    /**
	     * Derive a password, the password is stored as char[] and not string for security matters
	     * @param salt
	     * @param password
	     * @return
	     */
	    public static SecretKey deriveKeyPbkdf2(byte[] salt, char[] password) {
	        try {
	           // long start = System.currentTimeMillis();
	            KeySpec keySpec = new PBEKeySpec(password, salt,ITERATION_COUNT, KEY_LENGTH);
	            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_DERIVATION_ALGORITHM);
	            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
	           // SALog.d(HighLevelOfSecurity.class, "key bytes: " + toHex(keyBytes));

	            SecretKey result = new SecretKeySpec(keyBytes, "AES");
	            //long elapsed = System.currentTimeMillis() - start;
	            //SALog.d(HighLevelOfSecurity.class, String.format("PBKDF2 key derivation took %d [ms].",  elapsed));
	            return result;
	        } catch (GeneralSecurityException e) {
	            throw new RuntimeException(e);
	        }
	    }

	    /**
	     * Derive a password, the password is stored as char[] and not string for security matters
	     * @param salt
	     * @param password
	     * @return
	     */
	    public static SecretKey deriveKeyPbkdf2(String saltS, char[] password) {
	        try {
	        	byte[] salt = fromBase64(saltS);
	           // long start = System.currentTimeMillis();
	            KeySpec keySpec = new PBEKeySpec(password, salt,ITERATION_COUNT, KEY_LENGTH);
	            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_DERIVATION_ALGORITHM);
	            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
	           // SALog.d(HighLevelOfSecurity.class, "key bytes: " + toHex(keyBytes));

	            SecretKey result = new SecretKeySpec(keyBytes, "AES");
	            //long elapsed = System.currentTimeMillis() - start;
	            //SALog.d(HighLevelOfSecurity.class, String.format("PBKDF2 key derivation took %d [ms].",  elapsed));
	            return result;
	        } catch (GeneralSecurityException e) {
	            throw new RuntimeException(e);
	        }
	    }

	    public static byte[] generateIv(int length) {
	    	byte[] b = new byte[length];
	        random.nextBytes(b);
	        return b;
	    }

	    public static byte[] generateSalt() {
	        byte[] b = new byte[PKCS5_SALT_LENGTH];
	        random.nextBytes(b);

	        return b;
	    }



    	  public static String toHex(byte[] bytes) {
    	        StringBuffer buff = new StringBuffer();
    	        for (byte b : bytes) {
    	            buff.append(String.format("%02X", b));
    	        }
    	        return buff.toString();
    	    }

    	    public static String toBase64(byte[] bytes) {
    	    	return new sun.misc.BASE64Encoder().encode(bytes);
    	    //	return Base64.encodeBase64String(bytes);
    	       // return com.ning.http.util.Base64.encode(bytes);
    	    }

    	    public static byte[] fromBase64(String string) {
    	      	try {
					return new sun.misc.BASE64Decoder().decodeBuffer(string);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
    	    	  
    	   // 		return Base64.decodeBase64(string);
			 //   return  com.ning.http.util.Base64.decode(string);
    	    }

    	   
	    

	    /**
	     * @param ciphertext
	     * @param key
	     * @return
	     * @throws RuntimeException
	     */
	    public static String decryptPbkdf2(String ciphertext, SecretKey key) throws RuntimeException{
	    	
	        String[] fields = ciphertext.split(DELIMITER);
	        if (fields.length != 3) {
	            throw new IllegalArgumentException("Invalid encypted text format: "+fields.length);
	        }

	 //       byte[] salt = fromBase64(fields[0]);
	        byte[] iv = fromBase64(fields[1]);
	        byte[] cipherBytes = fromBase64(fields[2]);
	        try{
	        	String s = decrypt(cipherBytes, key, iv);
	        	return s;
		    } catch (RuntimeException e) {
	            throw new RuntimeException(e);
		    }
	      
	    }

	    private static String decrypt(byte[] cipherBytes, SecretKey key, byte[] iv) throws RuntimeException{
	        try {
	            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
	            IvParameterSpec ivParams = new IvParameterSpec(iv);
	            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
	        //    SALog.d(HighLevelOfSecurity.class, "Cipher IV: " + toHex(cipher.getIV()));
	            byte[] plaintext = cipher.doFinal(cipherBytes);
	            String plainrStr = new String(plaintext, "UTF-8");

	            return plainrStr;
	        } catch (GeneralSecurityException e) {
	            throw new RuntimeException(e);
	        } catch (UnsupportedEncodingException e) {
	            throw new RuntimeException(e);
	        }
	    }
	    
  	    
	    public static String encrypt(String plaintext, SecretKey key, byte[] salt) {
	  	        try {
	  	            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

	  	            byte[] iv = generateIv(cipher.getBlockSize());
	  	         //   SALog.d(HighLevelOfSecurity.class, "IV: " + toHex(iv));
	  	            IvParameterSpec ivParams = new IvParameterSpec(iv);
	  	            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
	  	         //   SALog.d(HighLevelOfSecurity.class, "Cipher IV: "
	  	       
	  	            //          + (cipher.getIV() == null ? null : toHex(cipher.getIV())));
	  	            byte[] cipherText = cipher.doFinal(plaintext.getBytes("UTF-8"));

	  	            if (salt != null) {
	  	                return String.format("%s%s%s%s%s", toBase64(salt), DELIMITER,
	  	                        toBase64(iv), DELIMITER, toBase64(cipherText));
	  	            }

	  	            return String.format("%s%s%s", toBase64(iv), DELIMITER,
	  	                    toBase64(cipherText));
	  	        } catch (GeneralSecurityException e) {
	  	            throw new RuntimeException(e);
	  	        } catch (UnsupportedEncodingException e) {
	  	            throw new RuntimeException(e);
	  	        }
	  	    }


	    	  
	    	  
	    	  
	    	  /**
	    	   * give string from char[]
	    	 * @param pwd
	    	 * @return
	    	 */
	    	public static String getStringFromCharArray(char[] pwd){
	    		  if(pwd==null)
	    			  return null;
	    		  StringBuilder build = new StringBuilder();
	    		  for (int i =0 ;i<pwd.length;i++){
	    			  build.append(pwd[i]);
	    		  }
	    		  return build.toString();
	    	  }
	  	    
			
}
