package HTTPServer;

import org.json.JSONObject;

import java.util.HashMap;

public class HTTPRequest {
    public final String method;
    public final HashMap<String, String> headers;
    public final JSONObject content;
    public final String userId;

    private HTTPRequest(
            String method,
            HashMap<String, String> headers,
            JSONObject content,
            String userId
    ) {
        this.method = method;
        this.headers = headers;
        this.content = content;
        this.userId = userId;
    }

    public boolean isAuthenticated() {
        return this.userId != null;
    }

    public static class Builder {
        private String method;
        private HashMap<String, String> headers;
        private JSONObject content;
        private String userId;

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder headers(HashMap<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder content(JSONObject content) {
            this.content = content;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public HTTPRequest build() {
            return new HTTPRequest(
                    this.method,
                    this.headers,
                    this.content,
                    this.userId
            );
        }
    }
}
