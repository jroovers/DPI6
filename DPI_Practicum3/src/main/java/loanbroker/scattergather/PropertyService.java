package loanbroker.scattergather;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Jeroen Roovers <jroovers>
 */
public class PropertyService {

    public static Properties getBankProperties() {
        return getProperties("banks.properties");
    }

    public static String readValue(Properties p, String key) {
        if (key == null) {
            throw new IllegalArgumentException("Tried to find value for key 'null'");
        }
        String value = p.getProperty(key);
        if (value == null) {
            return null;
        }
        return value;
    }

    /**
     * Reads properties. Null if file is not found.
     *
     * @param resourceName filename
     * @return properties object or null if file not found
     */
    private static Properties getProperties(String resourceName) {

        Properties prop = new Properties();
        InputStream input = null;
        try {

            String filename = resourceName;
            input = PropertyService.class.getClassLoader().getResourceAsStream(filename);

            if (input == null) {
                System.out.println("Sorry, unable to find " + filename);
                return null;
            }

            //load a properties file from class path, inside static method
            prop.load(input);
            input.close();
            return prop;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
