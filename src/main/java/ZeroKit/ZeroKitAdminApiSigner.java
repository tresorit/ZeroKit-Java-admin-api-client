package ZeroKit;

import ZeroKit.Http.HttpRequest;
import ZeroKit.Utils.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * ZeroKit admin API signer for creating signed HTTP requests
 * @author 		hami89 (Gergely Hamos, hami89@gmail.com)
 * @copyright	Copyright © Tresorit AG. 2017
 */
public final class ZeroKitAdminApiSigner {
    // Admin key in hexadecimal string format (64 char / 32 bytes long)
    public String key;

    /**
     * Initializes a new zeroKit admin API signer with the given API key
     * @param key Admin api key in hex string format (64 char / 32 bytes long)
     */
    public ZeroKitAdminApiSigner(String key) {
        this.key = key;
    }

    /**
     * Computes the HMAC-SHA256 signature of the given string data signed by the admin key of the instance
     * @param data String data to sign
     * @return Returns the signature in ZeroKit.Utilsit.Base64 encoded format
     */
    public String hmacSha256(String data) {
        return hmacSha256(this.key, data);
    }

    /**
     * Canonicalizes the given HTTP request
     * @param request Request to canonicalize
     * @return Returns the canonicalized string of the requests which is ready for signing
     */
    public static String canonicalizeRequest(HttpRequest request){
        StringBuilder sb = new StringBuilder();

        // Add method
        sb.append(request.getMethod().name()).append('\n');

        // Add path
        sb.append(request.getUrl().getPath().replaceAll("^\\/", ""));

        // Add query
        if (request.getUrl().getQuery()!=null)
            sb.append('?').append(request.getUrl().getQuery());

        // Add headers
        for (String key: request.getHeaders().keySet()){
            for (String value: request.getHeaders().get(key)){
                sb.append('\n').append(key).append(':').append(value);
            }
        }

        return sb.toString();
    }

    /**
     * Signs the given HTTP request with the admin key of this instance
     * @param request Request to sign
     * @return Returns the signature in ZeroKit.Base64s.Base64 format
     */
    public String signRequest(HttpRequest request) {
        String canonicalRequest = canonicalizeRequest(request);

        String signature = hmacSha256(canonicalRequest);

        return signature;
    }

    /**
     * Computes the HMAC-SHA256 signature of the given string data signed by the admin key of the instance
     * @param key Key to use for singing in hexadecimal string format (64 chars / 32 bytes long)
     * @param data String data to sign
     * @return Returns the signature in ZeroKit.Base64s.Base64 encoded format
     */
    private static String hmacSha256(String key, String data) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(hex2Bin(key), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] encoded = sha256_HMAC.doFinal(data.getBytes("UTF-8"));

            return Base64.encodeToString(encoded, Base64.NO_WRAP);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts the given hex string to binary data
     * @param hex Hex string to convert
     * @return Returns the binary data as a byte array
     */
    private static byte[] hex2Bin(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
