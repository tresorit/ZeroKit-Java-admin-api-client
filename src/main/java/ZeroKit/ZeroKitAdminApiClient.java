package ZeroKit;

import ZeroKit.Http.HttpClient;
import ZeroKit.Http.HttpRequest;
import ZeroKit.Http.HttpResponse;
import ZeroKit.Utils.Sha256;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ZeroKit admin API client for JAVA 7+
 * @author 		hami89 (Gergely Hamos, hami89@gmail.com)
 * @copyright	Copyright © Tresorit AG. 2017
 */
public class ZeroKitAdminApiClient extends HttpClient {
    // Regex patterns
    private static Pattern ErrorCodeRegex = Pattern.compile("\\A\\s*\\{.*\"ErrorCode\"\\s*:\\s*\"(?<errorcode>[a-zA-Z0-9_]+)\".*}\\s*\\z", Pattern.DOTALL);
    private static Pattern ErrorMessageRegex = Pattern.compile("\\A\\s*[^\"]*\"ErrorMessage\"\\s*:\\s*\"(?<errormessage>((?=\\\\)\\\\(\"|/|\\\\|b|f|n|r|t|u[0-9a-f]{4})|[^\\\\\"]*)*)\".*}\\s*\\z", Pattern.DOTALL);
    private static Pattern HostedTenantUrlPattern = Pattern.compile("\\Ahttps?://[^/,^?,^#]*/tenant-(?<tenantid>[a-z][a-z0-9]{7,9})/?\\z");
    private static Pattern ProductionTenantUrlPattern = Pattern.compile("\\Ahttps?://(?<tenantid>[a-z][a-z0-9]{7,9})\\.[^/,^?,^#]*/?\\z");
    private static Pattern TenantIdRegex = Pattern.compile("\\A[a-z][a-z0-9]{7,9}\\z");
    private static Pattern AdminKeyRegex = Pattern.compile("\\A[a-fA-F0-9]{64}\\z");

    // date format for HTTP api
    private SimpleDateFormat apiDateFormat;

    // API signer
    private ZeroKitAdminApiSigner signer;

    // Tenant ID
    private String tenantId;

    // Admin user ID
    private String adminUserId;

    // Indicates whether to translate API error to ZeroKit APi exceptions automatically
    private boolean translateExceptions;

    /**
     * Initializes a new zeroKit admin API client with the given parameters
     * @param baseUrl Service URL of the tenant (from management portal)
     * @param adminKey Admin key of the tenant in hex string format (64 chars / 32 bytes long, from management portal)
     * @param tenantId Tenant IS from management portal
     * @throws MalformedURLException Thrown when the given service url is invalid
     * @throws IllegalArgumentException Thrown when any of the given parameters is invalid
     */
    public ZeroKitAdminApiClient(String baseUrl, String adminKey, String tenantId) throws MalformedURLException {
        super(baseUrl);

        if (adminKey == null || !AdminKeyRegex.matcher(adminKey).matches())
            throw new IllegalArgumentException("The given admin key is invalid.");

        this.signer = new ZeroKitAdminApiSigner(adminKey);
        this.apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        this.apiDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.translateExceptions = true;
        this.tenantId = tenantId;

        // Try match tenant ID
        if (this.tenantId == null){
            Matcher matcher = ProductionTenantUrlPattern.matcher(baseUrl);
            if (matcher.matches()){
                this.tenantId = matcher.group("tenantid");
            }
            else{
                matcher = HostedTenantUrlPattern.matcher(baseUrl);
                if (matcher.matches()){
                    this.tenantId = matcher.group("tenantid");
                }
            }
        }

        if (this.tenantId == null || !TenantIdRegex.matcher(this.tenantId).matches())
            throw new IllegalArgumentException("The given or parsed tenant ID is invalid.");

        this.adminUserId = "admin@" + this.tenantId + ".tresorit.io";
    }

    /**
     * Initializes a new zeroKit admin API client with the given parameters
     * @param baseUrl Service URL of the tenant (from management portal)
     * @param adminKey Admin key of the tenant in hex string format (64 chars / 32 bytes long, from management portal)
     * @throws MalformedURLException Thrown when the given service url is invalid
     * @throws IllegalArgumentException Thrown when any of the given parameters is invalid
     */
    public ZeroKitAdminApiClient(String baseUrl, String adminKey) throws MalformedURLException {
        this(baseUrl, adminKey, null);
    }

    /**
     * Executes the given HTTP request and returns the result
     * This method automatically signs or re-signs the given request for ZeroKit admin API
     * @param request ZeroKit.Http request object to execute. Will be modified (signed) by the client!
     * @return Returns the result of the HTTP call
     * @throws IOException Thrown when the call fails due to network / accessibility issues.
     */
    @Override
    public HttpResponse doHttpCall(HttpRequest request) throws IOException {
        String contentHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        int contentLength = 0;
        if (request.getContents() != null){
            contentHash = Sha256.hash(request.getContents());
            contentLength = request.getContents().length;
        }

        request.setHeader("UserId", this.adminUserId);
        request.setHeader("TresoritDate", this.apiDateFormat.format(new Date()));
        request.setHeader("Content-SHA256", contentHash);
        request.setHeader("Content-Length", contentLength);

        if (!request.getHeaders().containsKey("\"Content-Type\""))
            request.setHeader("Content-Type", "application/json");

        request.setHeader("HMACHeaders", ""); // Pre-seed this header

        String[] headerNames = request.getHeaders().keySet().toArray(new String[0]);

        StringBuilder sb = new StringBuilder();
        sb.append(headerNames[0]);
        for(int i=1;i<headerNames.length;i++)
            sb.append(',').append(headerNames[i]);

        request.setHeader("HMACHeaders", sb.toString());

        request.setHeader("Authorization", "AdminKey " + this.signer.signRequest(request));

        HttpResponse response = super.doHttpCall(request);

        if (translateExceptions)
            translateException(response);

        return response;
    }

    /**
     * Automatically detects and translates JSON API error to API exceptions
     * @param response HTTP response to check for errors
     */
    private static void translateException(HttpResponse response) {
        try {
            if ((response.getStatusCode() < 200 || response.getStatusCode() >= 300) && response.getContents() != null) {
                String responseText = response.getStringContents();

                Matcher errorCodeMatcher = ErrorCodeRegex.matcher(responseText);
                Matcher errorMessageMatcher = ErrorMessageRegex.matcher(responseText);

                if (errorCodeMatcher.matches() && errorMessageMatcher.matches()){
                    String errorCode = errorCodeMatcher.group("errorcode");
                    String errorMessage = errorMessageMatcher.group("errormessage");

                    throw new ZeroKitAdminApiException(errorCode, errorMessage);
                }
            }
        }
        catch (ZeroKitAdminApiException e){
            throw e;
        }
        catch (Exception e)
        {
            // Suppress
        }
    }
}
