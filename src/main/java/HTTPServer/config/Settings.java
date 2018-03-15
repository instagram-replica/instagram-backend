package HTTPServer.config;


import org.json.JSONObject;

public class Settings {
    private String name;
    private long port;
    private long numberOfThreads;
    private String version;

    public Settings(String name, long port, long numberOfThreads, String version) {
        this.name = name;
        this.port = port;
        this.numberOfThreads = numberOfThreads;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public long getPort() {
        return port;
    }

    public long getNumberOfThreads() {
        return numberOfThreads;
    }

    public String getVersion() {
        return version;
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .put("name", this.name)
                .put("port", this.port)
                .put("numberOfThreads", this.numberOfThreads)
                .put("version", this.version);
    }

    public static class Builder {
        private String name = "netty";
        private long port = 8080;
        private long numberOfThreads = 10;
        private String version = "0.1.0";

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder port(long port) {
            this.port = port;
            return this;
        }

        public Builder numberOfThreads(long numberOfThreads) {
            this.numberOfThreads = numberOfThreads;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Settings build() {
            return new Settings(
                    this.name,
                    this.port,
                    this.numberOfThreads,
                    this.version
            );
        }
    }
}
