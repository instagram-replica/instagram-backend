package auth;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.IOException;
import java.util.Properties;

import static utilities.Main.readPropertiesFile;

public class JWT {
    private final static String PROPERTIES_FILE_PATH = "src/main/resources/auth.properties";
    private final static String JWT_SECRET_PROPERTY_NAME = "JWT_SECRET";
    private final static String JWT_PAYLOAD_USER_ID_PROPERTY = "userId";
    private final static String JWT_PAYLOAD_ROLE_PROPERTY = "role";

    public static String signJWT(JWTPayload payload) throws IOException {
        String secret = readJWTSecret();
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return com.auth0.jwt.JWT.create()
                .withClaim(JWT_PAYLOAD_USER_ID_PROPERTY, payload.userId)
                .withClaim(JWT_PAYLOAD_ROLE_PROPERTY, payload.role)
                .sign(algorithm);
    }

    public static JWTPayload verifyJWT(String token) throws IOException {
        String secret = readJWTSecret();
        Algorithm algorithm = Algorithm.HMAC256(secret);

        JWTVerifier verifier = com.auth0.jwt.JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);

        return new JWTPayload.Builder()
                .userId(decodedJWT.getClaim(JWT_PAYLOAD_USER_ID_PROPERTY).asString())
                .role(decodedJWT.getClaim(JWT_PAYLOAD_ROLE_PROPERTY).asString())
                .build();
    }

    private static String readJWTSecret() throws IOException {
        Properties properties = readPropertiesFile(PROPERTIES_FILE_PATH);
        return properties.getProperty(JWT_SECRET_PROPERTY_NAME);
    }
}
