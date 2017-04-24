package ZeroKit;

import java.util.Map;

/**
 * Helper object to manage environment variables
 * The class also implements a basic caching
 * @author 		hami89 (Gergely Hamos, hami89@gmail.com)
 * @copyright	Copyright © Tresorit AG. 2017
 */
public final class Env {
    // Mapping o fthe loade4d environment variables
    private static Map<String, String> env;

    /**
     * Gets the mapping of the environment variables
     * @return Returns the mapping of the environment variables
     */
    public static Map<String, String> getEnv(){
        if (env == null)
            env = System.getenv();

        return env;
    }

    /**
     * Returns the value of the requested environment variable, or null is the variable is not set
     * @param name Name of the variable to get
     * @return Returns the value of the variable or null is the variable was not set
     */
    public static String get(String name){
        Map<String, String> env = getEnv();

        return env.get(name);
    }
}
