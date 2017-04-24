package ZeroKit;

import ZeroKit.Http.HttpResponse;
import org.junit.Assert;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic tests for ZeroKit admin api client
 * @author 		hami89 (Gergely Hamos, hami89@gmail.com)
 * @copyright	Copyright © Tresorit AG. 2017
 */
public class ZeroKitAdminApiClientTest {
    // Service URL extracted from environment variables
    private static String ServiceUrl = Env.get("ZKIT_SERVICE_URL");

    // Admin key extracted from environment variables
    private static String AdminKey = Env.get("ZKIT_ADMIN_KEY");

    /**
     * Initializes a new instance of the test class
     */
    public ZeroKitAdminApiClientTest() {
        // Check the existance of the parameters extracted from the environment
        if (this.ServiceUrl == null || this.AdminKey == null)
            throw new RuntimeException("Test init failed. ZKIT_SERVICE_URL and/or ZKIT_ADMIN_KEY environment variables ar not set!");
    }

    @org.junit.Test(expected = MalformedURLException.class)
    public void canNotBeCreatedWithNullUrl() throws MalformedURLException {
        ZeroKitAdminApiClient client = new ZeroKitAdminApiClient(null, AdminKey);
    }

    @org.junit.Test(expected = MalformedURLException.class)
    public void canNotBeCreatedWithBadUrl() throws MalformedURLException {
        ZeroKitAdminApiClient client = new ZeroKitAdminApiClient("badurl://bad.bad", AdminKey);
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void canNotBeCreatedWithNullAdminKey() throws MalformedURLException {
        ZeroKitAdminApiClient client = new ZeroKitAdminApiClient(ServiceUrl, null);
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void canNotBeCreatedWithShortAdminKey() throws MalformedURLException {
        ZeroKitAdminApiClient client = new ZeroKitAdminApiClient(ServiceUrl, AdminKey.substring(2));
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void canNotBeCreatedWithNonHexAdminKey() throws MalformedURLException {
        ZeroKitAdminApiClient client = new ZeroKitAdminApiClient(ServiceUrl, "no" + AdminKey.substring(2));
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void canNotBeCreatedWithBadTenantId() throws MalformedURLException {
        ZeroKitAdminApiClient client = new ZeroKitAdminApiClient(ServiceUrl, AdminKey, "00testtest");
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void canNotBeCreatedWithShortTenantId() throws MalformedURLException {
        ZeroKitAdminApiClient client = new ZeroKitAdminApiClient(ServiceUrl, AdminKey, "nope");
    }

    @org.junit.Test
    public void canBeCreated() throws MalformedURLException {
        ZeroKitAdminApiClient client = new ZeroKitAdminApiClient(ServiceUrl, AdminKey);
    }

    @org.junit.Test
    public void canCallJsonApiWithoutPayload() throws IOException {
        ZeroKitAdminApiClient client = new ZeroKitAdminApiClient(ServiceUrl, AdminKey);

        HttpResponse response = client.doHttpCall(client.createPostRequest("/api/v4/admin/user/init-user-registration"));

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertNotNull(response.getStringContents());
        Assert.assertTrue(response.getStringContents().contains("\"UserId\""));
        Assert.assertTrue(response.getStringContents().contains("\"RegSessionId\""));
        Assert.assertTrue(response.getStringContents().contains("\"RegSessionVerifier\""));
    }

    @org.junit.Test
    public void canCallJsonApiWithPayload() throws IOException {
        ZeroKitAdminApiClient client = new ZeroKitAdminApiClient(ServiceUrl, AdminKey);

        HttpResponse response = client.doHttpCall(client.
                createPutRequest("/api/v4/admin/tenant/upload-custom-content?fileName=css/login.css").
                setHeader("Content-Type", "text/css").
                setContents("body { background-color: red; }"));

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertNotNull(response.getStringContents());
        Assert.assertTrue(Pattern.compile("\\A\\s*\\{.*}\\s*\\z").matcher(response.getStringContents()).matches());
        Assert.assertTrue(response.getStringContents().contains("\"Name\""));
        Assert.assertTrue(response.getStringContents().contains("\"Path\""));
        Assert.assertTrue(response.getStringContents().contains("\"Url\""));
        Assert.assertTrue(response.getStringContents().contains("\"Size\""));
        Assert.assertTrue(response.getStringContents().contains("\"ContentType\""));
        Assert.assertTrue(response.getStringContents().contains("\"Etag\""));
    }

    @org.junit.Test
    public void canCallJsonApiWithPayloadApiError() throws IOException {
        try {
            ZeroKitAdminApiClient client = new ZeroKitAdminApiClient(ServiceUrl, AdminKey);

            HttpResponse response = client.doHttpCall(client.createPostRequest("/api/v4/admin/user/init-user-registration"));

            Matcher matcher = Pattern.compile(
                    "\\A\\s*[^\"]*\"UserId\"\\s*\\:\\s*\"(?<userid>((?=\\\\)\\\\(\\\"|\\/|\\\\|b|f|n|r|t|u[0-9a-f]{4})|[^\\\\\"]*)*)\".*}\\s*\\z",
                    Pattern.DOTALL).
                    matcher(response.getStringContents());

            Assert.assertTrue(matcher.matches());

            String userId = matcher.group("userid");

            response = client.doHttpCall(client.createPostRequest("/api/v4/admin/user/set-user-state").
                    setContents("{\"UserId\":\"" + userId + "\", \"Enabled\":\"False\"}"));

            String contents = response.getStringContents();
            Assert.fail();
        }
        catch (ZeroKitAdminApiException e){
            Assert.assertEquals("UserNotExists", e.getErrorCode());
            Assert.assertNotNull(e.getMessage());
        }
    }
}