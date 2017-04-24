package ZeroKit;

/**
 * Exception thrown when a ZeroKit API error occurs
 * @author 		hami89 (Gergely Hamos, hami89@gmail.com)
 * @copyright	Copyright © Tresorit AG. 2017
 */
public class ZeroKitAdminApiException extends RuntimeException {
    // API error code
    private String errorCode;

    /**
     * Initializes a new zeroKit API exception
     * @param errorCode API error code
     * @param errorMessage API error message (propagated as exception message)
     */
    public ZeroKitAdminApiException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    /**
     * Gets the API error code
     * @return Returns the API error code string
     */
    public String getErrorCode() {
        return errorCode;
    }
}
