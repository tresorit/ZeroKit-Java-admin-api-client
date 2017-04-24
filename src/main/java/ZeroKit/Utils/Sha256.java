package ZeroKit.Utils;

import java.security.MessageDigest;

/**
 * ZeroKit.Utilsit.Sha256 hash utilites
 * @author 		hami89 (Gergely Hamos, hami89@gmail.com)
 * @copyright	Copyright © Tresorit AG. 2017
 */
public final class Sha256 {
    /**
     * Computes the SHA256 signature of the given string data
     * @param data Data to hash
     * @return Return the hash as a hex string
     */
    public static String hash(String data) {
        try{
            return hash(data.getBytes("UTF-8"));
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * Computes the SHA256 signature of the given binary data
     * @param data Data to hash
     * @return Return the hash as a hex string
     */
    public static String hash(byte[] data) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
