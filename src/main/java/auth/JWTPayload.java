package auth;

public class JWTPayload {
    public final String userId;
    public final String role;

    private JWTPayload(String userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public static class Builder {
        private String userId;
        private String role;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public JWTPayload build() {
            return new JWTPayload(
                    this.userId,
                    this.role
            );
        }
    }
}
