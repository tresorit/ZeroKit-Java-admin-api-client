package ZeroKit.Http;

import ZeroKit.Utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP request object
 * @author 		hami89 (Gergely Hamos, hami89@gmail.com)
 * @copyright	Copyright © Tresorit AG. 2017
 */
public class HttpRequest {
    // URL of the request, including the path and the query
    private URL url;

    // HTTP method to use
    private HttpMethod method;

    // HTTP header to send along with the request
    private Map<String, List<String>> headers;

    // Body contents to send
    private byte[] contents;

    /**
     * Initializes a new HTTP request
     * @param url URL of the request
     */
    public HttpRequest(URL url) {
        this.url = url;
        this.method = HttpMethod.GET;
        this.headers = new HashMap<String, List<String>>();
        this.contents = null;
    }

    /**
     * Creates a request initialized with the given URL
     * @param url URL to use for the request
     * @return Return the created request
     */
    public static HttpRequest createRequest(URL url) {
        return new HttpRequest(url);
    }

    /**
     * Creates a request initialized with the given URL
     * @param url URL to use for the request
     * @return Return the created request
     * @throws MalformedURLException Throw when the given URL is invalid
     */
    public static HttpRequest createRequest(String url) throws MalformedURLException {
        return new HttpRequest(new URL(url));
    }

    /**
     * Sets the HTTP method of the request
     * @param method Method to use for the request
     * @return Returns the modified request
     */
    public HttpRequest setMethod(HttpMethod method){
        this.method = method;
        return this;
    }

    /**
     * Sets the entire URL path of the request
     * @param path Path to use for the request
     * @return Returns the modified request
     * @throws MalformedURLException Throw when its impossible to concatenate the given path with the existing URL
     */
    public HttpRequest setPath(String path) throws MalformedURLException {
        String oldQuery = this.url.getQuery();

        String newFile = path;
        if (oldQuery != null && oldQuery.length() > 0)
            newFile += "?" + oldQuery;

        this.url = new URL(this.url.getProtocol(), this.url.getHost(), this.url.getPort(), newFile);

        return this;
    }

    /**
     * Appends the given path segment to the URL path of the request
     * @param path Path to append
     * @return Returns the modified request
     * @throws MalformedURLException Throw when its impossible to concatenate the given path with the existing URL
     */
    public HttpRequest addPath(String path) throws MalformedURLException {
        String oldPath = this.url.getPath();

        String newPath = oldPath.replaceAll("\\/$", "") + "/" + path.replaceAll("^\\/", "");

        return this.setPath(newPath);
    }

    /**
     * Creates a HEAD request initialized with the given URL
     * @param url URL to use for the request
     * @return Return the created request
     * @throws MalformedURLException Throw when the given URL is invalid
     */
    public HttpRequest createHeadRequest(String url) throws MalformedURLException {
        return createRequest(url).setMethod(HttpMethod.HEAD);
    }

    /**
     * Creates a POST request initialized with the given URL
     * @param url URL to use for the request
     * @return Return the created request
     * @throws MalformedURLException Throw when the given URL is invalid
     */
    public HttpRequest createPostRequest(String url) throws MalformedURLException {
        return createRequest(url).setMethod(HttpMethod.POST);
    }

    /**
     * Creates a PUT request initialized with the given URL
     * @param url URL to use for the request
     * @return Return the created request
     * @throws MalformedURLException Throw when the given URL is invalid
     */
    public HttpRequest createPutRequest(String url) throws MalformedURLException {
        return createRequest(url).setMethod(HttpMethod.PUT);
    }

    /**
     * Creates a DELETE request initialized with the given URL
     * @param url URL to use for the request
     * @return Return the created request
     * @throws MalformedURLException Throw when the given URL is invalid
     */
    public HttpRequest createDeleteRequest(String url) throws MalformedURLException {
        return createRequest(url).setMethod(HttpMethod.DELETE);
    }

    /**
     * Creates a OPTIONS request initialized with the given URL
     * @param url URL to use for the request
     * @return Return the created request
     * @throws MalformedURLException Throw when the given URL is invalid
     */
    public HttpRequest createOptionsRequest(String url) throws MalformedURLException {
        return createRequest(url).setMethod(HttpMethod.OPTIONS);
    }

    /**
     * Adds an HTTP header value to the request
     * If a previous header value exists, that will be unmodified, the new value will only be appended
     * @param name Header name to use
     * @param value header value to use
     * @return Returns the modified request
     */
    public HttpRequest addHeader(String name, Object value){
        if (!this.headers.containsKey(name))
            this.headers.put(name, new ArrayList<String>());

        this.headers.get(name).add(value.toString());

        return this;
    }

    /**
     * Sets an HTTP header value of the request
     * If a previous header value exists, that will be overwritten
     * @param name Header name to use
     * @param value header value to use
     * @return Returns the modified request
     */
    public HttpRequest setHeader(String name, Object value){
        this.headers.put(name, new ArrayList<String>());

        this.headers.get(name).add(value.toString());

        return this;
    }

    /**
     * Sets the contents of the message to the contents of the given input stream
     * The stream will be read to the end during the call!
     * @param contents Stream contents to add
     * @return Returns the modified request
     * @throws IOException Thrown if an error occurs during the reading of the underlying stream
     */
    public HttpRequest setContents(InputStream contents) throws IOException {
        return this.setContents(IOUtils.readAll(contents));
    }

    /**
     * Sets the contents of the message to the given binary contents
     * The given content array will be stored as a reference!
     * @param contents Binary contents to add
     * @return Returns the modified request
     */
    public HttpRequest setContents(byte[] contents){
        this.contents = contents;

        return this;
    }

    /**
     * Sets the contents of the message to the given string contents
     * @param contents String contents to add
     * @return Returns the modified request
     */
    public HttpRequest setContents(String contents){
        return this.setContents(contents.getBytes());
    }

    /**
     * Sets the contents of the message to the given string contents with the given charset
     * @param contents String contents to add
     * @param charset Charset to use for encoding the string
     * @return Returns the modified request
     */
    public HttpRequest setContents(String contents, Charset charset){
        return this.setContents(contents.getBytes(charset));
    }

    /**
     * Gets the URL of the request
     * @return Returns the request URL
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Gets the method of the request
     * @return Returns the request method
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * Gets the headers of the request
     * @return Returns the request headers
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * Gets the headers of the request as a list
     * @return Returns the request header list
     */
    public List<HttpHeader> getAllHeaders() {
        ArrayList<HttpHeader> result = new ArrayList<HttpHeader>();

        for (String key: getHeaders().keySet()){
            for (String value: getHeaders().get(key)){
                result.add(new HttpHeader(key, value));
            }
        }

        return result;
    }

    /**
     * Returns the contents of the request as a binary array
     * @return Returns the request contents
     */
    public byte[] getContents() {
        return contents;
    }
}
