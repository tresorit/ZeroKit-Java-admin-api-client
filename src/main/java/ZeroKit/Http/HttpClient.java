package ZeroKit.Http;

import ZeroKit.Utils.IOUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Basic HTTP client for JAVA 7+
 * @author 		hami89 (Gergely Hamos, hami89@gmail.com)
 * @copyright	Copyright © Tresorit AG. 2017
 */
public class HttpClient {
    // Base URL of the client
    private URL baseUrl;

    // Indicate whether to use caches
    private boolean useCache;

    /**
     * Initializes a new ZeroKit.HttpKit.HttpClient
     */
    public HttpClient() {
        this.baseUrl = null;
        this.useCache = false;
    }

    /**
     * Initializes a new ZeroKit.HttpClienttpClient with the given base url
     * @param baseUrl Base url to use for on-the-fly calls
     * @throws MalformedURLException Thrown when the given base url is invalid
     */
    public HttpClient(String baseUrl) throws MalformedURLException {
        this(new URL(baseUrl));
    }

    /**
     * Initializes a new ZeroKit.HttpClienttpClient with the given base url
     * @param baseUrl Base url to use for on-the-fly calls
     */
    public HttpClient(URL baseUrl) {
        this();
        this.baseUrl = baseUrl;
    }

    /**
     * Gets whether the client is using caches
     * @return Returns true if the client uses caches
     */
    public boolean isUseCache() {
        return useCache;
    }

    /**
     * Sets the cache usage policy of the client
     * @param useCache If true the cache usage is turned on, otherwise off
     */
    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    /**
     * Creates a new, empty GET request object with the base URL of this client
     * @return Returns the created request
     */
    public HttpRequest createRequest() {
        return HttpRequest.createRequest(this.baseUrl);
    }

    /**
     * Creates a new, empty GET request object with the base URL of this client appended by the given path
     * @param path Path string to append to the base URL
     * @return Returns the created request
     * @throws MalformedURLException Thrown when the addition of the given path is impossible to the existing base URL
     */
    public HttpRequest createRequest(String path) throws MalformedURLException {
        return createRequest().addPath(path);
    }

    /**
     * Creates a new, empty HEAD request object with the base URL of this client appended by the given path
     * @param path Path string to append to the base URL
     * @return Returns the created request
     * @throws MalformedURLException Thrown when the addition of the given path is impossible to the existing base URL
     */
    public HttpRequest createHeadRequest(String path) throws MalformedURLException {
        return createRequest(path).setMethod(HttpMethod.GET);
    }

    /**
     * Creates a new, empty POST request object with the base URL of this client appended by the given path
     * @param path Path string to append to the base URL
     * @return Returns the created request
     * @throws MalformedURLException Thrown when the addition of the given path is impossible to the existing base URL
     */
    public HttpRequest createPostRequest(String path) throws MalformedURLException {
        return createRequest(path).setMethod(HttpMethod.POST);
    }

    /**
     * Creates a new, empty PUT request object with the base URL of this client appended by the given path
     * @param path Path string to append to the base URL
     * @return Returns the created request
     * @throws MalformedURLException Thrown when the addition of the given path is impossible to the existing base URL
     */
    public HttpRequest createPutRequest(String path) throws MalformedURLException {
        return createRequest(path).setMethod(HttpMethod.PUT);
    }

    /**
     * Creates a new, empty DELETE request object with the base URL of this client appended by the given path
     * @param path Path string to append to the base URL
     * @return Returns the created request
     * @throws MalformedURLException Thrown when the addition of the given path is impossible to the existing base URL
     */
    public HttpRequest createDeleteRequest(String path) throws MalformedURLException {
        return createRequest(path).setMethod(HttpMethod.DELETE);
    }

    /**
     * Creates a new, empty OPTIONS request object with the base URL of this client appended by the given path
     * @param path Path string to append to the base URL
     * @return Returns the created request
     * @throws MalformedURLException Thrown when the addition of the given path is impossible to the existing base URL
     */
    public HttpRequest createOptionsRequest(String path) throws MalformedURLException {
        return createRequest(path).setMethod(HttpMethod.OPTIONS);
    }

    /**
     * Executes the goven HTTP request and returns the result
     * @param request ZeroKit.Http request object to execute. Will be modified (signed) by the client!
     * @return Returns the result of the HTTP call
     * @throws IOException Thrown when the call fails due to network / accessibility issues.
     */
    public  HttpResponse doHttpCall(HttpRequest request) throws IOException {
        HttpURLConnection connection = null;

        try {
            //Create connection
            connection = (HttpURLConnection) request.getUrl().openConnection();
            connection.setRequestMethod(request.getMethod().name());

            // Add headers
            for (String key: request.getHeaders().keySet()){
                for (String value: request.getHeaders().get(key)){
                    if (key == "Content-Length")
                        connection.setFixedLengthStreamingMode(Integer.parseInt(value));
                    else
                        connection.addRequestProperty(key, value);
                }
            }

            // Set default headers
            Map<String, List<String>> properties = connection.getRequestProperties();
            if (request.getContents() != null && !properties.containsKey("Content-Length"))
                connection.setFixedLengthStreamingMode(request.getContents().length);
            else if (!properties.containsKey("Content-Length"))
                connection.setFixedLengthStreamingMode(0);

            // Disable caches
            connection.setUseCaches(this.useCache);

            // Set output mode to true
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

            if (request.getContents() != null)
                wr.write(request.getContents());
            else
                wr.write(new byte[0]);

            wr.close();

            // Get Response status code
            int statusCode = connection.getResponseCode();

            if (statusCode >= 200 && statusCode < 300) {
                InputStream is = connection.getInputStream();
                byte[] response = IOUtils.readAll(is);
                is.close();

                return new HttpResponse(statusCode, connection.getHeaderFields(), response);
            }
            else {
                InputStream is = connection.getErrorStream();
                byte[] response = IOUtils.readAll(is);
                is.close();

                return new HttpResponse(statusCode, connection.getHeaderFields(), response);
            }
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
