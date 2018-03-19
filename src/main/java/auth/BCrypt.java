package auth;

public class BCrypt {
    private static final int WORKLOAD = 12;

    public static String hashPassword(String plainPassword) {
        String salt = org.mindrot.jbcrypt.BCrypt.gensalt(WORKLOAD);
        return org.mindrot.jbcrypt.BCrypt.hashpw(plainPassword, salt);
    }

    public static boolean comparePassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
        }

        return org.mindrot.jbcrypt.BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
