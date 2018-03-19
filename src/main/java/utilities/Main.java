package utilities;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

public class Main {
    public static boolean isUUID(String text) {
        return text.matches(
                "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
        );
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static Properties readPropertiesFile(String filePath) throws IOException {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(filePath);

        if (fileInputStream == null) {
            throw new FileNotFoundException("Properties file not found");
        }

        properties.load(fileInputStream);
        fileInputStream.close();

        return properties;
    }

    public static JSONObject cloneJSONObject(JSONObject originalJSONObject) {
        return new JSONObject(originalJSONObject, JSONObject.getNames(originalJSONObject));
    }
}
