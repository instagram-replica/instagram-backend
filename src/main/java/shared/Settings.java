package shared;


import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Settings {
    private static Settings settings;
    private String name;
    private String application;
    private int port;
    private int numberOfThreads;
    private String version;

    private Settings(String name, String application, int port, int numberOfThreads, String version) {
        this.name = name;
        this.application = application;
        this.port = port;
        this.numberOfThreads = numberOfThreads;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getApplication() {
        return application;
    }

    public int getPort() {
        return port;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public String getVersion() {
        return version;
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .put("name", name)
                .put("application", application)
                .put("port", port)
                .put("numberOfThreads", numberOfThreads)
                .put("version", version);
    }

    public static Settings getInstance() {
        if (settings == null) init(null);
        return settings;
    }

    public static void init(String fileUri) {
        JSONParser parser = new JSONParser();
        Settings.Builder builder = new Settings.Builder();

        if (fileUri == null) {
            settings = builder.build();
            return;
        }

        try {
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(new FileReader(fileUri));
            settings = builder
                    .name((String) jsonObject.get("name"))
                    .application((String) jsonObject.get("application"))
                    .port(((Long) jsonObject.get("port")).intValue())
                    .numberOfThreads(((Long) jsonObject.get("numberOfThreads")).intValue())
                    .version((String) jsonObject.get("version"))
                    .build();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        settings = builder.build();
    }


    public static class Builder {
        private String name = "netty";
        private String application = "netty";
        private int port = 8080;
        private int numberOfThreads = 10;
        private String version = "0.1.0";

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder application(String application) {
            this.application = application;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        Builder numberOfThreads(int numberOfThreads) {
            this.numberOfThreads = numberOfThreads;
            return this;
        }

        Builder version(String version) {
            this.version = version;
            return this;
        }

        public Settings build() {
            return new Settings(
                    this.name,
                    this.application,
                    this.port,
                    this.numberOfThreads,
                    this.version
            );
        }
    }
}
