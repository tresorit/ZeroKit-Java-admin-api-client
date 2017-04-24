package ZeroKit.Http;

/**
 * Represents an HTTP header name-value pair
 * @author 		hami89 (Gergely Hamos, hami89@gmail.com)
 * @copyright	Copyright © Tresorit AG. 2017
 */
public class HttpHeader {
    // Header name
    private String name;

    // Header value
    private String value;

    /**
     * Initializes a new HTTP header
     * @param name Header name
     * @param value Header value
     */
    public HttpHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * gets the header name
     * @return Returns the header name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the header value
     * @return Returns the header value
     */
    public String getValue() {
        return value;
    }
}
