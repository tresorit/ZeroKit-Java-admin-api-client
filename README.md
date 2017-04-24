# ZeroKit-Java-admin-api-client
[![Build Status](https://travis-ci.org/tresorit/ZeroKit-Java-admin-api-client.svg?branch=master)](https://travis-ci.org/tresorit/ZeroKit-Java-admin-api-client)

Small client lib to call ZeroKit's signed administrative API from Java.
This lib provides a special HTTP client which automatically signs the administrative requests for your ZeroKit tenant's admin API.

More information about ZeroKit encryption platform: [https://tresorit.com/zerokit](https://tresorit.com/zerokit)

ZeroKit management portal: [https://manage.tresorit.io](https://manage.tresorit.io)

## Example
```java
// Include lib
import ZeroKit.ZeroKitAdminApiClient;
import ZeroKit.ZeroKitAdminApiException;
import ZeroKit.Http.HttpResponse;

public class Main {
    public static void Main(String[] args){
        try {
            // Provider your zeroKit tenant's settings
            ZeroKitAdminApiClient client = new ZeroKitAdminApiClient("ServiceUrl", "AdminKey");
            
            // Assemble call and do the request
            HttpResponse response = client.doHttpCall(client.createPostRequest("/api/v4/admin/user/init-user-registration"));
            
            // Use returned data
            String jsonText = response.getStringContents();
        }
        catch (ZeroKitAdminApiException e){
            // Handle API errors
            System.out.println("An error occured. Api error code: " +
               e.getErrorCode() + 
               ", error message: " + e.getMessage());
        }
    }
}

```

## Notes
This lib is designed to be free from any external resources, therefore there is no JSON parsing lib included for the client.
It's highly recommended to use a JSON library for transforming JSON to and from Java objects.
You can derive from  ZeroKitAdminApiClient and easily add methods which can convert Java objects automatically to and from JSON text.