package HTTPServer.config;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Config {
    private Config() {

    }

    public static Settings getSettings(String fileUri) {
        JSONParser parser = new JSONParser();
        Settings.Builder builder = new Settings.Builder();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(fileUri));
            return builder
                    .name((String) jsonObject.get("name"))
                    .port((Long) jsonObject.get("port"))
                    .numberOfThreads((Long) jsonObject.get("numberOfThreads"))
                    .version((String) jsonObject.get("version"))
                    .build();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return builder.build();
    }
}
