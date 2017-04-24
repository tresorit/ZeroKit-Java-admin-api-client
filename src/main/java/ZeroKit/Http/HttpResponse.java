package ZeroKit.Http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP request object
 * @author 		hami89 (Gergely Hamos, hami89@gmail.com)
 * @copyright	Copyright © Tresorit AG. 2017
 */
public class HttpResponse {
    // Status code of the response
    private int statusCode;

    // Headers of the response
    private Map<String, List<String>> headers;

    // Response contents
    private byte[] contents;

    /**
     * Initializes a new HTTP response
     * @param statusCode Status code of the response
     * @param headers Headers of the response
     * @param contents Contents of the response
     */
    public HttpResponse(int statusCode, Map<String, List<String>> headers, byte[] contents) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.contents = contents;
    }

    /**
     * Gets the status code of the response
     * @return Returns the status code of the response
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Gets the headers of the response
     * @return Returns the response headers
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * Gets the headers of the response as a list
     * @return Returns the response header list
     */
    public List<HttpHeader> getAllheaders() {
        ArrayList<HttpHeader> result = new ArrayList<HttpHeader>();

        for (String key: getHeaders().keySet()){
            for (String value: getHeaders().get(key)){
                result.add(new HttpHeader(key, value));
            }
        }

        return result;
    }

    /**
     * Returns the contents of the response as a binary array
     * The response array can be null, it there were no received data
     * @return Returns the request contents
     */
    public byte[] getContents() {
        return contents;
    }

    /**
     * Returns the contents of the response as a string
     * The response string can be null, it there were no received data
     * @return Returns the request contents
     */
    public String getStringContents(){
        if (this.getContents() == null)
            return null;

        return new String(this.getContents());
    }
}
